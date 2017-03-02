package org.oliot.epcis.service.capture;

import java.io.InputStream;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.xml.bind.JAXB;

import org.bson.BsonDocument;

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
import org.oliot.epcis.converter.mongodb.ECReportWriteConverter;

import com.mongodb.client.MongoCollection;

import org.oliot.epcis.service.subscription.TriggerEngine;
import org.oliot.model.ale.ECReport;
import org.oliot.model.ale.ECReportMemberField;
import org.oliot.model.ale.ECReports;
import org.oliot.model.ale.ECReportGroupListMemberExtension.FieldList;

/**
 * Copyright (C) 2014-2017 Jaewook Byun
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
				return new ResponseEntity<>(new String("[Error] Input EC Report does not comply the standard schema"),
						HttpStatus.BAD_REQUEST);
			}
			Configuration.logger.info(" ECReport : Validated ");
		}

		InputStream stream = CaptureUtil.getXMLDocumentInputStream(inputString);
		ecReports = JAXB.unmarshal(stream, ECReports.class);

		String retMsg = "Temporary Message ";
		boolean result = capture(ecReports, eventTimeZoneOffset, action, bizStep, disposition, readPoint, bizLocation);

		Configuration.logger.info(result);

		return new ResponseEntity<>(retMsg.toString(), HttpStatus.OK);
	}

	private Map<String, Object> getExtensionMap(List<ECReportMemberField> fields) {
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
		return extMap;
	}

	private boolean capture(ECReports ecReports, String eventTimeZoneOffset, String action, String bizStep,
			String disposition, String readPoint, String bizLocation) {

		// Event Time in timemillis , type long
		long eventTime = ecReports.getCreationDate().toGregorianCalendar().getTimeInMillis();
		// Record Time
		long recordTimeMillis = new GregorianCalendar().getTimeInMillis();

		List<ECReport> ecReportList = ecReports.getReports().getReport();

		ecReportList.parallelStream().filter(ecReport -> ecReport.getGroup() != null).forEach(ecReport -> {
			ecReport.getGroup().parallelStream().filter(ecReportGroup -> ecReportGroup.getGroupList() != null)
					.forEach(ecReportGroup -> {
						ecReportGroup.getGroupList().getMember().parallelStream()
								.filter(member -> (member.getExtension() != null)
										&& (member.getExtension().getFieldList() != null))
								.forEach(member -> {
									String epcString = member.getEpc().getValue();
									FieldList fieldList = member.getExtension().getFieldList();
									List<ECReportMemberField> fields = fieldList.getField();
									Map<String, Object> extMap = getExtensionMap(fields);

									BsonDocument dbo = ECReportWriteConverter.convert(epcString, eventTime,
											eventTimeZoneOffset, recordTimeMillis, action, bizStep, disposition,
											readPoint, bizLocation, extMap);

									MongoCollection<BsonDocument> collection = Configuration.mongoDatabase
											.getCollection("EventData", BsonDocument.class);
									if (Configuration.isTriggerSupported == true) {
										TriggerEngine.examineAndFire("ObjectEvent", dbo);
									}
									collection.insertOne(dbo);
								});
					});
		});

		return true;
	}

	/*
	 * static BsonValue converseType(String value) { String[] valArr =
	 * value.split("\\^"); if (valArr.length != 2) { return new
	 * BsonString(value); } try { String type = valArr[1]; if
	 * (type.equals("int")) { return new BsonInt32(Integer.parseInt(valArr[0]));
	 * } else if (type.equals("long")) { return new
	 * BsonInt64(Long.parseLong(valArr[0])); } else if (type.equals("double")) {
	 * return new BsonDouble(Double.parseDouble(valArr[0])); } else if
	 * (type.equals("boolean")) { return new
	 * BsonBoolean(Boolean.parseBoolean(valArr[0])); } else if
	 * (type.equals("float")) { return new
	 * BsonDouble(Double.parseDouble(valArr[0])); } else if
	 * (type.equals("dateTime")) { BsonDateTime time =
	 * getBsonDateTime(valArr[0]); if (time != null) return time; return new
	 * BsonString(value); } else { return new BsonString(value); } } catch
	 * (NumberFormatException e) { return new BsonString(value); } }
	 */

	/*
	 * static public String encodeMongoObjectKey(String key) { key =
	 * key.replace(".", "\uff0e"); return key; }
	 */

}
