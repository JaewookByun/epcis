/**
 * Copyright (C) 2014 KAIST RESL 
 *
 * This file is part of Oliot (oliot.org).

 * @author Jack Jaewook Byun, Ph.D student
 * Korea Advanced Institute of Science and Technology
 * Real-time Embedded System Laboratory(RESL)
 * bjw0829@kaist.ac.kr
 */

package org.oliot.epcis.service.capture;

import java.util.List;

import javax.xml.bind.JAXBElement;

import org.oliot.epcis.configuration.Configuration;
import org.oliot.epcis.service.capture.mongodb.MongoCaptureUtil;
import org.oliot.epcis.service.capture.mysql.MysqlCaptureUtil;
import org.oliot.model.epcis.ActionType;
import org.oliot.model.epcis.AggregationEventType;
import org.oliot.model.epcis.EPCISDocumentType;
import org.oliot.model.epcis.EPCISMasterDataDocumentType;
import org.oliot.model.epcis.EventListType;
import org.oliot.model.epcis.ObjectEventType;
import org.oliot.model.epcis.QuantityEventType;
import org.oliot.model.epcis.SensorEventType;
import org.oliot.model.epcis.TransactionEventType;
import org.oliot.model.epcis.TransformationEventType;
import org.oliot.model.epcis.VocabularyListType;
import org.oliot.model.epcis.VocabularyType;
import org.oliot.tdt.SimplePureIdentityFilter;

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
public class CaptureService implements CoreCaptureService {

	public void capture(AggregationEventType event) {

		// General Exception Handling
		// M7
		String timeZone = event.getEventTimeZoneOffset();
		if (!isCorrectTimeZone(timeZone)) {
			Configuration.logger.error("Req. M7 Error");
			return;
		}
//event.getEventTimeZoneOffset();

		// Mandatory Field: Action
		if (event.getAction() == null) {
			Configuration.logger
					.error("Aggregation Event should have 'Action' field ");
			return;
		}
		// M13
		if (event.getAction() == ActionType.ADD
				|| event.getAction() == ActionType.DELETE) {
			if (event.getParentID() == null) {
				Configuration.logger.error("Req. M13 Error");
				return;
			}
		}
		// M10
		String parentID = event.getParentID();
		if (parentID != null) {

			if (SimplePureIdentityFilter.isPureIdentity(parentID) == false) {
				Configuration.logger.error("Req. M10 Error");
				return;
			}
		}

		if (Configuration.backend.equals("MongoDB")) {
			MongoCaptureUtil m = new MongoCaptureUtil();
			m.capture(event);
		}
		else if(Configuration.backend.equals("MySQL")){
			MysqlCaptureUtil m = new MysqlCaptureUtil();
			m.capture(event);
		}
	}

	public void capture(ObjectEventType event) {

		// General Exception Handling
		// M7
		String timeZone = event.getEventTimeZoneOffset();
		if (!isCorrectTimeZone(timeZone)) {
			Configuration.logger.error("Req. M7 Error");
			return;
		}

		if (Configuration.backend.equals("MongoDB")) {
			MongoCaptureUtil m = new MongoCaptureUtil();
			m.capture(event);
		}else if(Configuration.backend.equals("MySQL")){
			MysqlCaptureUtil m = new MysqlCaptureUtil();
			m.capture(event);
		}
	}

	public void capture(QuantityEventType event) {

		// General Exception Handling
		// M7
		String timeZone = event.getEventTimeZoneOffset();
		if (!isCorrectTimeZone(timeZone)) {
			Configuration.logger.error("Req. M7 Error");
			return;
		}

		if (Configuration.backend.equals("MongoDB")) {
			MongoCaptureUtil m = new MongoCaptureUtil();
			m.capture(event);
		}else if(Configuration.backend.equals("MySQL")){
			MysqlCaptureUtil m = new MysqlCaptureUtil();
			m.capture(event);
		}
	}

	public void capture(TransactionEventType event) {

		// General Exception Handling
		// M7
		String timeZone = event.getEventTimeZoneOffset();
		if (!isCorrectTimeZone(timeZone)) {
			Configuration.logger.error("Req. M7 Error");
			return;
		}

		// M14
		String parentID = event.getParentID();
		if (parentID != null) {

			if (SimplePureIdentityFilter.isPureIdentity(parentID) == false) {
				Configuration.logger.error("Req. M14 Error");
				return;
			}
		}

		if (Configuration.backend.equals("MongoDB")) {
			MongoCaptureUtil m = new MongoCaptureUtil();
			m.capture(event);
		}	
		else if(Configuration.backend.equals("MySQL")){
			MysqlCaptureUtil m = new MysqlCaptureUtil();
			m.capture(event);
		}
	}

	public void capture(TransformationEventType event) {

		// General Exception Handling
		// M7
		String timeZone = event.getEventTimeZoneOffset();
		if (!isCorrectTimeZone(timeZone)) {
			Configuration.logger.error("Req. M7 Error");
			return;
		}

		if (Configuration.backend.equals("MongoDB")) {
			MongoCaptureUtil m = new MongoCaptureUtil();
			m.capture(event);
		}
		else if(Configuration.backend.equals("MySQL")){
			MysqlCaptureUtil m = new MysqlCaptureUtil();
			m.capture(event);
		}
	}

	public void capture(SensorEventType event) {

		// General Exception Handling
		// M7
		String timeZone = event.getEventTimeZoneOffset();
		if (!isCorrectTimeZone(timeZone)) {
			Configuration.logger.error("Req. M7 Error");
			return;
		}

		if (Configuration.backend.equals("MongoDB")) {
			MongoCaptureUtil m = new MongoCaptureUtil();
			m.capture(event);
		}
		else if(Configuration.backend.equals("MySQL")){
			MysqlCaptureUtil m = new MysqlCaptureUtil();
			m.capture(event);
		}
	}

	public void capture(VocabularyType vocabulary) {

		if (Configuration.backend.equals("MongoDB")) {
			MongoCaptureUtil m = new MongoCaptureUtil();
			m.capture(vocabulary);
		}
		else if(Configuration.backend.equals("MySQL")){
			MysqlCaptureUtil m = new MysqlCaptureUtil();
			m.capture(vocabulary);
		}

	}

	@SuppressWarnings("rawtypes")
	@Override
	public void capture(EPCISDocumentType epcisDocument) {
		if (epcisDocument.getEPCISBody() == null) {
			Configuration.logger.info(" There is no DocumentBody ");
			return;
		}
		if (epcisDocument.getEPCISBody().getEventList() == null) {
			Configuration.logger.info(" There is no EventList ");
			return;
		}
		EventListType eventListType = epcisDocument.getEPCISBody()
				.getEventList();
		List<Object> eventList = eventListType
				.getObjectEventOrAggregationEventOrQuantityEvent();

		/*
		 * JAXBElement<EPCISEventListExtensionType>
		 * JAXBElement<TransactionEventType> Object
		 * JAXBElement<QuantityEventType> JAXBElement<ObjectEventType>
		 * JAXBElement<AggregationEventType> Element
		 */

		for (int i = 0; i < eventList.size(); i++) {
			JAXBElement eventElement = (JAXBElement) eventList.get(i);
			Object event = eventElement.getValue();
			if (event instanceof ObjectEventType) {
				capture((ObjectEventType) event);
			} else if (event instanceof AggregationEventType) {
				capture((AggregationEventType) event);
			} else if (event instanceof TransactionEventType) {
				capture((TransactionEventType) event);
			} else if (event instanceof TransformationEventType) {
				capture((TransformationEventType) event);
			} else if (event instanceof QuantityEventType) {
				capture((QuantityEventType) event);
			} else if (event instanceof SensorEventType) {
				capture((SensorEventType) event);
			}
		}
	}

	public boolean isCorrectTimeZone(String timeZone) {

		boolean isMatch = timeZone
				.matches("^(?:Z|[+-](?:2[0-3]|[01][0-9]):[0-5][0-9])$");

		return isMatch;
	}

	@Override
	public void capture(EPCISMasterDataDocumentType epcisMasterDataDocument) {

		if (epcisMasterDataDocument.getEPCISBody() == null) {
			Configuration.logger.info(" There is no DocumentBody ");
			return;
		}

		if (epcisMasterDataDocument.getEPCISBody().getVocabularyList() == null) {
			Configuration.logger.info(" There is no Vocabulary List ");
			return;
		}

		VocabularyListType vocabularyListType = epcisMasterDataDocument
				.getEPCISBody().getVocabularyList();

		List<VocabularyType> vocabularyTypeList = vocabularyListType
				.getVocabulary();

		for (int i = 0; i < vocabularyTypeList.size(); i++) {
			VocabularyType vocabulary = vocabularyTypeList.get(i);
			capture(vocabulary);
		}
	}

}
