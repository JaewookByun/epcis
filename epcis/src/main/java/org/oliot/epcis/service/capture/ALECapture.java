package org.oliot.epcis.service.capture;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXB;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Level;
import org.oliot.epcis.configuration.Configuration;
import org.oliot.model.ale.ECReport;
import org.oliot.model.ale.ECReportGroup;
import org.oliot.model.ale.ECReportGroupList;
import org.oliot.model.ale.ECReportGroupListMember;
import org.oliot.model.ale.ECReportMemberField;
import org.oliot.model.ale.ECReports;
import org.oliot.model.ale.ECReportGroupListMemberExtension.FieldList;
import org.oliot.model.epcis.ActionType;
import org.oliot.model.epcis.BusinessLocationType;
import org.oliot.model.epcis.EPC;
import org.oliot.model.epcis.EPCListType;
import org.oliot.model.epcis.ObjectEventExtension2Type;
import org.oliot.model.epcis.ObjectEventExtensionType;
import org.oliot.model.epcis.ObjectEventType;
import org.oliot.model.epcis.ReadPointType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.ServletContextAware;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

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

			// Event Type branch
			if (eventType.equals("AggregationEvent")) {

			} else if (eventType.equals("ObjectEvent")) {
				List<ObjectEventType> objectEventArray = makeObjectEvent(ecReports, request);
				for (int i = 0; i < objectEventArray.size(); i++) {
					ObjectEventType oet = objectEventArray.get(i);
					CaptureService capture = new CaptureService();
					capture.capture(oet, null, null);
				}
			} else if (eventType.equals("QuantityEvent")) {

			} else if (eventType.equals("TransactionEvent")) {

			} else if (eventType.equals("TransformationEvent")) {

			}
		} catch (IOException e) {
			Configuration.logger.log(Level.ERROR, e.toString());
		}
	}

	@Override
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	private List<ObjectEventType> makeObjectEvent(ECReports ecReports, HttpServletRequest request) {

		try {
			List<ObjectEventType> oetList = new ArrayList<ObjectEventType>();
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
						ObjectEventType oet = makeBaseObjectEvent(ecReports, request);
						EPCListType elt = new EPCListType();
						List<EPC> epcList = elt.getEpc();
						EPC epc = new EPC();
						epc.setValue(member.getEpc().getValue());
						epcList.add(epc);
						oet.setEpcList(elt);

						ObjectEventExtensionType oeet = new ObjectEventExtensionType();
						ObjectEventExtension2Type oee2t = new ObjectEventExtension2Type();

						if (member.getExtension() == null)
							continue;
						if (member.getExtension().getFieldList() == null)
							continue;
						FieldList fieldList = member.getExtension().getFieldList();
						List<ECReportMemberField> fields = fieldList.getField();
						List<Object> elementList = new ArrayList<Object>();
						for (int l = 0; l < fields.size(); l++) {
							ECReportMemberField field = fields.get(l);

							if (field.getName() != null && field.getValue() != null) {
								DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
								DocumentBuilder builder = dbf.newDocumentBuilder();
								Document doc = builder.newDocument();

								Node node = doc.createElement("value");
								node.setTextContent(field.getValue());
								Element element = doc.createElement(field.getName());
								element.appendChild(node);
								elementList.add(element);
							}
						}
						oee2t.setAny(elementList);
						oeet.setExtension(oee2t);
						oet.setExtension(oeet);
						oetList.add(oet);
					}

				}
			}
			return oetList;
		} catch (ParserConfigurationException e) {
			Configuration.logger.log(Level.ERROR, e.toString());
			return null;
		}
	}

	@SuppressWarnings("deprecation")
	private ObjectEventType makeBaseObjectEvent(ECReports ecReports, HttpServletRequest request) {

		try {
			ObjectEventType oet = new ObjectEventType();
			// get extra param : action
			String actionString = request.getParameter("action");
			// Mandatory Field : Default - OBSERVE
			ActionType actionType;
			if (actionString == null)
				actionType = ActionType.OBSERVE;
			else
				actionType = ActionType.valueOf(actionString);
			// Optional Field
			String bizStep = request.getParameter("bizStep");
			// Optional Field
			String disposition = request.getParameter("disposition");
			// Optional Field
			String readPoint = request.getParameter("readPoint");
			// Optional Field
			String bizLocation = request.getParameter("bizLocation");

			XMLGregorianCalendar eventTime = CaptureUtil.getEventTime(ecReports);

			String eventTimeZoneOffset = null;

			if (eventTime.getTimezone() == -2147483648) {
				GregorianCalendar cal = new GregorianCalendar();
				eventTimeZoneOffset = CaptureUtil.makeTimeZoneString(cal.getTime().getTimezoneOffset());
			} else {
				eventTimeZoneOffset = CaptureUtil.makeTimeZoneString(eventTime.getTimezone());
			}

			GregorianCalendar recordCalendar = new GregorianCalendar();
			DatatypeFactory df = DatatypeFactory.newInstance();
			XMLGregorianCalendar recordTime = df.newXMLGregorianCalendar(recordCalendar);

			// Null Reports Check
			boolean isNull = CaptureUtil.isReportNull(ecReports);
			if (isNull == true) {
				return null;
			}

			// Start to make EPCIS Object
			if (eventTime != null)
				oet.setEventTime(eventTime);
			if (eventTimeZoneOffset != null)
				oet.setEventTimeZoneOffset(eventTimeZoneOffset);
			if (recordTime != null)
				oet.setRecordTime(recordTime);
			if (actionType != null)
				oet.setAction(actionType);
			if (bizStep != null)
				oet.setBizStep(bizStep);
			if (bizLocation != null) {
				BusinessLocationType blt = new BusinessLocationType();
				blt.setId(bizLocation);
				oet.setBizLocation(blt);
			}
			if (disposition != null)
				oet.setDisposition(disposition);
			if (readPoint != null) {
				ReadPointType rpt = new ReadPointType();
				rpt.setId(readPoint);
				oet.setReadPoint(rpt);
			}
			return oet;
		} catch (DatatypeConfigurationException e) {
			Configuration.logger.log(Level.ERROR, e.toString());
			return null;
		}
	}
}
