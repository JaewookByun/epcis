package org.oliot.epcis.service.rest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.oliot.epcis.configuration.ConfigurationServlet;
import org.oliot.epcis.service.capture.CaptureService;
import org.oliot.epcis.service.rest.mongodb.MongoRESTQueryService;
import org.oliot.model.epcis.EPC;
import org.oliot.model.epcis.SensingElementType;
import org.oliot.model.epcis.SensingListType;
import org.oliot.model.epcis.SensorEventType;
import org.oliot.tdt.SimplePureIdentityFilter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Copyright (C) 2014 KAIST RESL
 *
 * This project is part of Oliot (oliot.org), pursuing the implementation of
 * Electronic Product Code Information Service(EPCIS) v1.1 specification in
 * EPCglobal.
 * [http://www.gs1.org/gsmp/kc/epcglobal/epcis/epcis_1_1-standard-20140520.pdf]
 * 
 *
 * @author Jack Jaewook Byun, Ph.D student
 * 
 *         Korea Advanced Institute of Science and Technology (KAIST)
 * 
 *         Real-time Embedded System Laboratory(RESL)
 * 
 *         bjw0829@kaist.ac.kr
 */
@Controller
@RequestMapping("/resource")
public class Resource {

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public String getEPCResource(@RequestParam(required = true) String target,
			@RequestParam(required = true) String targetType,
			@RequestParam(required = false) String from,
			@RequestParam(required = false) String until) {

		String result = "";
		if (ConfigurationServlet.backend.equals("MongoDB")) {
			MongoRESTQueryService mrqs = new MongoRESTQueryService();
			result = mrqs.getEPCResource(target, targetType, from, until);
		} else if (ConfigurationServlet.backend.equals("Cassandra")) {

		} else if (ConfigurationServlet.backend.equals("MySQL")) {

		}
		return result;
	}

	@RequestMapping(method = RequestMethod.PUT)
	@ResponseBody
	public String putEPCResource(@RequestParam(required = true) String target,
			@RequestParam(required = true) String targetType,
			@RequestParam(required = false) String eventTime,
			@RequestParam(required = false) String finishTime,
			@RequestParam(required = false) String sensorEPC,
			@RequestParam(required = false) String sensorType,
			@RequestParam(required = false) String sensorValue) {

		if (!SimplePureIdentityFilter.isPureIdentity(target)) {
			return "targetObject or targetArea should follow Pure Identity Form";
		}

		if (!targetType.equals("Object") && !targetType.equals("Area")) {
			return "targetType should be Object or Area";
		}

		CaptureService cs = new CaptureService();
		SensorEventType event = new SensorEventType();

		if (targetType.equals("Object")) {
			event.setTargetObject(target);
		} else if (targetType.equals("Area")) {
			event.setTargetArea(target);
		}

		try {
			SimpleDateFormat sdf = new SimpleDateFormat(
					"yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
			if (eventTime != null) {
				GregorianCalendar eventCalendar = new GregorianCalendar();
				eventCalendar.setTime(sdf.parse(eventTime));
				XMLGregorianCalendar xmlEventTime = DatatypeFactory
						.newInstance().newXMLGregorianCalendar(eventCalendar);
				event.setEventTime(xmlEventTime);
				event.setEventTimeZoneOffset(eventTime.substring(eventTime
						.length() - 6));
			} else {
				GregorianCalendar eventCalendar = new GregorianCalendar();
				XMLGregorianCalendar xmlEventTime = DatatypeFactory
						.newInstance().newXMLGregorianCalendar(eventCalendar);
				event.setEventTime(xmlEventTime);
				event.setEventTimeZoneOffset(makeTimeZoneString(xmlEventTime
						.getTimezone()));
			}
			if (finishTime != null) {
				GregorianCalendar finishCalendar = new GregorianCalendar();
				finishCalendar.setTime(sdf.parse(finishTime));
				XMLGregorianCalendar xmlFinishTime = DatatypeFactory
						.newInstance().newXMLGregorianCalendar(finishCalendar);
				event.setFinishTime(xmlFinishTime);
			}
		} catch (ParseException e) {
			return e.toString();
		} catch (DatatypeConfigurationException e) {
			return e.toString();
		}
		SensingElementType sensingElement = new SensingElementType();
		sensingElement.setEpc(new EPC(sensorEPC));
		sensingElement.setType(sensorType);
		sensingElement.setValue(sensorValue);
		List<SensingElementType> setList = new ArrayList<SensingElementType>();
		setList.add(sensingElement);
		SensingListType slt = new SensingListType();
		slt.setSensingElement(setList);
		event.setSensingList(slt);
		cs.capture(event);
		return "Event Captured";
	}

	public String getEPCResourceOne(
			@RequestParam(required = false) String target,
			@RequestParam(required = false) String targetType) {

		String result = "";
		if (ConfigurationServlet.backend.equals("MongoDB")) {
			MongoRESTQueryService mrqs = new MongoRESTQueryService();
			result = mrqs.getEPCResourceOne(target, targetType);
		} else if (ConfigurationServlet.backend.equals("Cassandra")) {

		} else if (ConfigurationServlet.backend.equals("MySQL")) {

		}
		return result;
	}

	public String makeTimeZoneString(int timeZone) {
		String retString = "";
		timeZone = timeZone / 60;

		if (timeZone >= 0) {
			retString = String.format("+%02d:00", timeZone);
		} else {
			timeZone = Math.abs(timeZone);
			retString = String.format("-%02d:00", timeZone);
		}
		return retString;
	}
}
