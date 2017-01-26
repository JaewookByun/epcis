package org.oliot.epcis.service.capture;

import java.io.IOException;
import java.io.InputStream;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXB;

import org.bson.BsonArray;
import org.bson.BsonBoolean;
import org.bson.BsonDateTime;
import org.bson.BsonDocument;
import org.bson.BsonDouble;
import org.bson.BsonInt32;
import org.bson.BsonInt64;
import org.bson.BsonString;
import org.bson.BsonValue;
import org.oliot.epcis.configuration.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.ServletContextAware;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.oliot.epcis.converter.mongodb.MongoWriterUtil;

import com.mongodb.client.MongoCollection;

import org.oliot.epcis.service.subscription.TriggerEngine;
import org.oliot.model.ale.ECReport;
import org.oliot.model.ale.ECReportGroup;
import org.oliot.model.ale.ECReportGroupList;
import org.oliot.model.ale.ECReportGroupListMember;
import org.oliot.model.ale.ECReportMemberField;
import org.oliot.model.ale.ECReports;
import org.oliot.model.ale.ECReportGroupListMemberExtension.FieldList;

/**
 * Copyright (C) 2014-2016 Jaewook Byun
 *
 * This project is part of Oliot open source (http://oliot.org). Oliot EPCIS
 * v1.2.x is Java Web Service complying with Electronic Product Code Information
 * Service (EPCIS) v1.2.
 *
 * @author Jaewook Byun, Ph.D student
 * 
 *         Korea Advanced Institute of Science and Technology (KAIST)
 * 
 *         Real-time Embedded System Laboratory(RESL)
 * 
 *         bjw0829@kaist.ac.kr, bjw0829@gmail.com
 */

@Controller
@RequestMapping("/ECReportCapture")
public class ECReportCapture implements ServletContextAware {

	@Autowired
	ServletContext servletContext;

	@Override
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> post(@RequestBody String inputString, @RequestParam(required = false) String eventType,
			@RequestParam(required = false) String eventTimeZoneOffset, @RequestParam(required = false) String action,
			@RequestParam(required = false) String bizStep, @RequestParam(required = false) String disposition,
			@RequestParam(required = false) String readPoint, @RequestParam(required = false) String bizLocation) {

		Configuration.logger.info(" ECReport Capture Started.... ");

		ECReports ecReports = null;

		// Default Event Type
		if (eventType == null)
			eventType = "ObjectEvent";

		if (Configuration.isCaptureVerfificationOn == true) {
			InputStream validateStream = CaptureUtil.getXMLDocumentInputStream(inputString);
			boolean isValidated = CaptureUtil.validate(validateStream,
					Configuration.wsdlPath + "/EPCglobal-ale-1_1-ale.xsd");
			if (isValidated == false) {
				Configuration.logger.info(" ECReport : Verification Failed ");
				return new ResponseEntity<>(new String("Error M63"), HttpStatus.BAD_REQUEST);
			}
			InputStream ecReportStream = CaptureUtil.getXMLDocumentInputStream(inputString);
			Configuration.logger.info(" ECReport : Validated ");
			ecReports = JAXB.unmarshal(ecReportStream, ECReports.class);
		} else {
			InputStream stream = CaptureUtil.getXMLDocumentInputStream(inputString);
			ecReports = JAXB.unmarshal(stream, ECReports.class);
		}

		String retMsg = "Temporary Message ";
		boolean result = capture(ecReports, eventTimeZoneOffset, action, bizStep, disposition, readPoint, bizLocation);

		Configuration.logger.info(result);
		
		return new ResponseEntity<>(retMsg.toString(), HttpStatus.OK);
	}

	private boolean capture(ECReports ecReports, String eventTimeZoneOffset, String action, String bizStep,
			String disposition, String readPoint, String bizLocation) {

		// Event Time in timemillis , type long

		long eventTime = ecReports.getCreationDate().toGregorianCalendar().getTimeInMillis();
		// Event Time Zone
		GregorianCalendar recordTime = new GregorianCalendar();
		long recordTimeMillis = recordTime.getTimeInMillis();
		// Action

		List<ECReport> ecReportList = ecReports.getReports().getReport();
		for (int i = 0; i < ecReportList.size(); i++) {
			ECReport ecReport = ecReportList.get(i);
			if (ecReport.getGroup() == null)
				continue;
			List<ECReportGroup> ecReportGroups = ecReport.getGroup();
			for (int j = 0; j < ecReportGroups.size(); j++) {
				ECReportGroup ecReportGroup = ecReportGroups.get(j);
				if (ecReportGroup.getGroupList() == null)
					continue;
				ECReportGroupList ecReportGroupList = ecReportGroup.getGroupList();
				List<ECReportGroupListMember> members = ecReportGroupList.getMember();
				for (int k = 0; k < members.size(); k++) {
					ECReportGroupListMember member = members.get(k);
					String epcString = member.getEpc().getValue();
					if (member.getExtension() == null)
						continue;
					if (member.getExtension().getFieldList() == null)
						continue;
					FieldList fieldList = member.getExtension().getFieldList();
					List<ECReportMemberField> fields = fieldList.getField();
					Map<String, Object> extMap = new HashMap<String, Object>();
					for (int l = 0; l < fields.size(); l++) {
						ECReportMemberField field = fields.get(l);
						String key = field.getName();
						String value = field.getValue();
						String[] valArr = value.split("\\^");
						if (valArr.length != 2) {
							extMap.put(key, value);
							continue;
						}
						try {
							String type = valArr[1];
							if (type.equals("int")) {
								extMap.put(key, Integer.parseInt(valArr[0]));
							} else if (type.equals("long")) {
								extMap.put(key, Long.parseLong(valArr[0]));
							} else if (type.equals("float")) {
								extMap.put(key, Float.parseFloat(valArr[0]));
							} else if (type.equals("double")) {
								extMap.put(key, Double.parseDouble(valArr[0]));
							} else if (type.equals("boolean")) {
								extMap.put(key, Boolean.parseBoolean(valArr[0]));
							} else {
								extMap.put(key, valArr[0]);
							}
						} catch (NumberFormatException e) {
							extMap.put(key, valArr[0]);
						}
					}
					BsonDocument dbo = new BsonDocument();
					// EPC
					BsonArray epcList = new BsonArray();
					BsonDocument epc = new BsonDocument();
					epc.put("epc", new BsonString(epcString));
					epcList.add(epc);
					dbo.put("epcList", epcList);
					dbo.put("eventType", new BsonString("ObjectEvent"));
					dbo.put("eventTime", new BsonDateTime(eventTime));
					if (eventTimeZoneOffset == null) {
						dbo.put("eventTimeZoneOffset", new BsonString("+09:00"));
					} else {
						dbo.put("eventTimeZoneOffset", new BsonString(eventTimeZoneOffset));
					}
					dbo.put("recordTime", new BsonDateTime(recordTimeMillis));
					if (action == null) {
						dbo.put("action", new BsonString("OBSERVE"));
					} else {
						dbo.put("action", new BsonString(action));
					}
					if (bizStep != null) {
						dbo.put("bizStep", new BsonString(bizStep));
					}
					if (disposition != null) {
						dbo.put("dispsition", new BsonString(disposition));
					}
					if (readPoint != null) {
						dbo.put("readPoint", new BsonDocument("id", new BsonString(readPoint)));
					}
					if (bizLocation != null) {
						dbo.put("bizLocation", new BsonDocument("id", new BsonString(bizLocation)));
					}
					// Extension Field
					if (extMap.isEmpty() == false) {
						Iterator<String> keyIterator = extMap.keySet().iterator();
						BsonDocument any = new BsonDocument();
						String namespaceURI = MongoWriterUtil.encodeMongoObjectKey("http://www.gs1.org/docs/epc/ale_1_1-schemas-20071202/EPCglobal-ale-1_1-ale.xsd");
						any.put("@" + namespaceURI, new BsonString("ale"));
						
						while (keyIterator.hasNext()) {
							String key = keyIterator.next();
							Object value = extMap.get(key);
							String qnameKey = MongoWriterUtil.encodeMongoObjectKey(namespaceURI + "#" + key);
							
							any.put(qnameKey, MongoWriterUtil.converseType(value.toString()));
						}
						dbo.put("any", any);
					}

					MongoCollection<BsonDocument> collection = Configuration.mongoDatabase.getCollection("EventData",
							BsonDocument.class);
					if (Configuration.isTriggerSupported == true) {
						TriggerEngine.examineAndFire("ObjectEvent", dbo);
					}
					collection.insertOne(dbo);
				}
			}
		}

		return true;
	}
	
	/*
	static BsonValue converseType(String value) {
		String[] valArr = value.split("\\^");
		if (valArr.length != 2) {
			return new BsonString(value);
		}
		try {
			String type = valArr[1];
			if (type.equals("int")) {
				return new BsonInt32(Integer.parseInt(valArr[0]));
			} else if (type.equals("long")) {
				return new BsonInt64(Long.parseLong(valArr[0]));
			} else if (type.equals("double")) {
				return new BsonDouble(Double.parseDouble(valArr[0]));
			} else if (type.equals("boolean")) {
				return new BsonBoolean(Boolean.parseBoolean(valArr[0]));
			} else if (type.equals("float")) {
				return new BsonDouble(Double.parseDouble(valArr[0]));
			} else if (type.equals("dateTime")) {
				BsonDateTime time = getBsonDateTime(valArr[0]);
				if (time != null)
					return time;
				return new BsonString(value);
			} else {
				return new BsonString(value);
			}
		} catch (NumberFormatException e) {
			return new BsonString(value);
		}
	}
	*/
	
	/*static public String encodeMongoObjectKey(String key) {
		key = key.replace(".", "\uff0e");
		return key;
	}*/
	

}
