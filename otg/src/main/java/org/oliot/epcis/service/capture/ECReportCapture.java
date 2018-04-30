package org.oliot.epcis.service.capture;

import java.io.InputStream;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.xml.bind.JAXB;

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
import org.oliot.epcis.converter.mongodb.ECReportWriteConverter;

import com.mongodb.MongoException;
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

	/**
	 * ECReportCapture API is a convenient method to convert ECReport into
	 * ObjectEvent with the following additional parameters
	 * 
	 * @param inputString:
	 *            ECReport
	 * @param eventTimeZoneOffset
	 * @param action
	 * @param bizStep
	 * @param disposition
	 * @param readPoint
	 * @param bizLocation
	 * @return
	 */
	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> post(@RequestBody String inputString,
			@RequestParam(required = false) String eventTimeZoneOffset, @RequestParam(required = false) String action,
			@RequestParam(required = false) String bizStep, @RequestParam(required = false) String disposition,
			@RequestParam(required = false) String readPoint, @RequestParam(required = false) String bizLocation) {

		Configuration.logger.info(" ECReport Capture Started.... ");

		ECReports ecReports = null;

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

		String msg = capture(ecReports, eventTimeZoneOffset, action, bizStep, disposition, readPoint, bizLocation);

		if (msg == null) {
			Configuration.logger.info(" ECReport : Captured ");
			return new ResponseEntity<>(new String(), HttpStatus.OK);
		} else {
			Configuration.logger.info(" ECReport : Some errors occurred ");
			return new ResponseEntity<>(msg, HttpStatus.BAD_REQUEST);
		}

	}

	private Map<String, BsonValue> getExtensionMap(List<ECReportMemberField> fields) {
		Map<String, BsonValue> extMap = new HashMap<String, BsonValue>();
		for (int l = 0; l < fields.size(); l++) {
			ECReportMemberField field = fields.get(l);
			String key = field.getName();
			String value = field.getValue();
			String[] valArr = value.split("\\^");
			if (valArr.length != 2) {
				extMap.put(key, new BsonString(value));
				continue;
			}
			try {
				String type = valArr[1];
				if (type.equals("int")) {
					extMap.put(key, new BsonInt32(Integer.parseInt(valArr[0])));
				} else if (type.equals("long")) {
					extMap.put(key, new BsonInt64(Long.parseLong(valArr[0])));
				} else if (type.equals("double")) {
					extMap.put(key, new BsonDouble(Double.parseDouble(valArr[0])));
				} else if (type.equals("boolean")) {
					extMap.put(key, new BsonBoolean(Boolean.parseBoolean(valArr[0])));
				} else if (type.equals("dateTime")) {
					extMap.put(key, new BsonDateTime(Long.parseLong(valArr[0])));
				} else {
					extMap.put(key, new BsonString(valArr[0]));
				}
			} catch (NumberFormatException e) {
				extMap.put(key, new BsonString(valArr[0]));
			}
		}
		return extMap;
	}

	private String capture(ECReports ecReports, String eventTimeZoneOffset, String action, String bizStep,
			String disposition, String readPoint, String bizLocation) {

		// Event Time in timemillis , type long
		long eventTime = ecReports.getCreationDate().toGregorianCalendar().getTimeInMillis();
		// Record Time
		long recordTimeMillis = new GregorianCalendar().getTimeInMillis();

		List<ECReport> ecReportList = ecReports.getReports().getReport();

		try {
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
										Map<String, BsonValue> extMap = getExtensionMap(fields);

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
		} catch (MongoException ex) {
			return ex.toString();
		}
		return null;
	}
}
