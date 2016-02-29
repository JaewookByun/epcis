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

import org.apache.log4j.Level;
import org.oliot.epcis.configuration.Configuration;
import org.oliot.model.ale.ECReport;
import org.oliot.model.ale.ECReportGroup;
import org.oliot.model.ale.ECReportGroupList;
import org.oliot.model.ale.ECReportGroupListMember;
import org.oliot.model.ale.ECReportMemberField;
import org.oliot.model.ale.ECReports;
import org.oliot.model.ale.ECReportGroupListMemberExtension.FieldList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.ServletContextAware;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

/**
 * Copyright (C) 2014 Jaewook Jack Byun
 *
 * This project is part of Oliot (oliot.org), pursuing the implementation of
 * Electronic Product Code Information Service(EPCIS) v1.1 specification in
 * EPCglobal.
 * [http://www.gs1.org/gsmp/kc/epcglobal/epcis/epcis_1_1-standard-20140520.pdf]
 * 
 *
 * @author Jaewook Jack Byun, Ph.D student
 * 
 *         Korea Advanced Institute of Science and Technology (KAIST)
 * 
 *         Real-time Embedded System Laboratory(RESL)
 * 
 *         bjw0829@kaist.ac.kr, bjw0829@gmail.com
 */

@Controller
@RequestMapping("/ALECapture")
public class ALECapture implements ServletContextAware {

	@Autowired
	ServletContext servletContext;

	@RequestMapping
	public void post(HttpServletRequest request, HttpServletResponse response) {

		try {
			Configuration.logger.info(" ECReport Capture Started.... ");
			// Identifying what the event type is
			String eventType = request.getParameter("eventType");
			// Default Event Type
			if (eventType == null)
				eventType = "ObjectEvent";

			// Get ECReport
			InputStream is = request.getInputStream();
			ECReports ecReports;

			if (Configuration.isCaptureVerfificationOn == true) {
				String xmlDocumentString = CaptureUtil.getXMLDocumentString(is);
				InputStream validateStream = CaptureUtil.getXMLDocumentInputStream(xmlDocumentString);
				boolean isValidated = CaptureUtil.validate(validateStream,
						Configuration.wsdlPath + "/EPCglobal-ale-1_1-ale.xsd");
				if (isValidated == false) {
					Configuration.logger.info(" ECReport : Verification Failed ");
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					return;
				}
				InputStream ecReportStream = CaptureUtil.getXMLDocumentInputStream(xmlDocumentString);
				Configuration.logger.info(" ECReport : Validated ");
				ecReports = JAXB.unmarshal(ecReportStream, ECReports.class);
			} else {
				ecReports = JAXB.unmarshal(is, ECReports.class);
			}

			capture(ecReports, request);
			
		} catch (IOException e) {
			Configuration.logger.log(Level.ERROR, e.toString());
		}
	}

	@Override
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	private boolean capture(ECReports ecReports, HttpServletRequest request) {

		// Event Time in timemillis , type long
		long eventTime = CaptureUtil.getEventTime(ecReports).toGregorianCalendar().getTimeInMillis();
		// Event Time Zone
		String eventTimeZoneOffset = request.getParameter("eventTimeZoneOffset");
		// Record Time : according to M5
		GregorianCalendar recordTime = new GregorianCalendar();
		long recordTimeMillis = recordTime.getTimeInMillis();
		// Action
		String action = request.getParameter("action");
		// Biz Step
		String bizStep = request.getParameter("bizStep");
		// Disposition
		String disposition = request.getParameter("disposition");
		// Read Point
		String readPoint = request.getParameter("readPoint");
		// BizLocation
		String bizLocation = request.getParameter("bizLocation");

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
					DBObject dbo = new BasicDBObject();
					// EPC
					BasicDBList epcList = new BasicDBList();
					DBObject epc = new BasicDBObject();
					epc.put("epc", epcString);
					epcList.add(epc);
					dbo.put("epcList", epcList);
					dbo.put("eventTime", eventTime);
					if(eventTimeZoneOffset == null ){
						dbo.put("eventTimeZoneOffset", "+09:00");
					}else{
						dbo.put("eventTimeZoneOffset", eventTimeZoneOffset);						
					}
					dbo.put("recordTime", recordTimeMillis);
					if(action == null){
						dbo.put("action", "OBSERVE");
					}else{
						dbo.put("action", action);
					}
					if(bizStep != null){
						dbo.put("bizStep", bizStep);
					}
					if(disposition != null){
						dbo.put("dispsition", disposition);
					}
					if(readPoint != null){
						dbo.put("readPoint", new BasicDBObject("id", readPoint));
					}
					if(bizLocation != null){
						dbo.put("bizLocation", new BasicDBObject("id", bizLocation));
					}					
					// Extension Field
					if( extMap.isEmpty() == false ){
						Iterator<String> keyIterator = extMap.keySet().iterator();
						DBObject any = new BasicDBObject();
						any.put("@ale", "http://"+request.getLocalAddr()+":"+request.getLocalPort()+request.getContextPath()+"/schema/aleCapture.xsd");
						while(keyIterator.hasNext()){
							String key = keyIterator.next();
							Object value = extMap.get(key);
							
							any.put("ale:"+key, value);
						}
						dbo.put("any", any);
					}
					ApplicationContext ctx = new GenericXmlApplicationContext("classpath:MongoConfig.xml");
					MongoOperations mongoOperation = (MongoOperations) ctx.getBean("mongoTemplate");
					DBCollection collection = mongoOperation.getCollection("ObjectEvent");
					collection.save(dbo);
					((AbstractApplicationContext) ctx).close();
				}
			}
		}
		return true;
	}
}
