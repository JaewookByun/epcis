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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBElement;

import org.oliot.epcis.configuration.Configuration;
import org.oliot.epcis.serde.mongodb.MasterDataWriteConverter;
import org.oliot.epcis.service.capture.mongodb.MongoCaptureUtil;
import org.oliot.epcis.service.capture.mysql.MysqlCaptureUtil;
import org.oliot.model.epcis.ActionType;
import org.oliot.model.epcis.AggregationEventType;
import org.oliot.model.epcis.EPCISDocumentType;
import org.oliot.model.epcis.EPCISMasterDataDocumentType;
import org.oliot.model.epcis.EventListType;
import org.oliot.model.epcis.ObjectEventType;
import org.oliot.model.epcis.QuantityEventType;
import org.oliot.model.epcis.TransactionEventType;
import org.oliot.model.epcis.TransformationEventType;
import org.oliot.model.epcis.VocabularyElementType;
import org.oliot.model.epcis.VocabularyListType;
import org.oliot.model.epcis.VocabularyType;
import org.oliot.tdt.SimplePureIdentityFilter;

/**
 * Copyright (C) 2014 Jaewook Byun
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

public class CaptureService implements CoreCaptureService {

	public void capture(AggregationEventType event, String userID, String accessModifier, Integer gcpLength) {

		// General Exception Handling
		// M7
		String timeZone = event.getEventTimeZoneOffset();
		if (!CaptureUtil.isCorrectTimeZone(timeZone)) {
			Configuration.logger.error("Req. M7 Error");
			return;
		}

		// Mandatory Field: Action
		if (event.getAction() == null) {
			Configuration.logger.error("Aggregation Event should have 'Action' field ");
			return;
		}
		// M13
		if (event.getAction() == ActionType.ADD || event.getAction() == ActionType.DELETE) {
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
			m.capture(event, userID, accessModifier, gcpLength);
		}else if(Configuration.backend.equals("MySQL")){
			MysqlCaptureUtil m = new MysqlCaptureUtil();
			m.capture(event);
		}
	}

	public void capture(ObjectEventType event, String userID, String accessModifier, Integer gcpLength) {

		// General Exception Handling
		// M7
		String timeZone = event.getEventTimeZoneOffset();
		if (!CaptureUtil.isCorrectTimeZone(timeZone)) {
			Configuration.logger.error("Req. M7 Error");
			return;
		}

		if (Configuration.backend.equals("MongoDB")) {
			MongoCaptureUtil m = new MongoCaptureUtil();
			m.capture(event, userID, accessModifier, gcpLength);
		}else if(Configuration.backend.equals("MySQL")){
			MysqlCaptureUtil m = new MysqlCaptureUtil();
			m.capture(event);
		}
	}

	public void capture(QuantityEventType event, String userID, String accessModifier, Integer gcpLength) {

		// General Exception Handling
		// M7
		String timeZone = event.getEventTimeZoneOffset();
		if (!CaptureUtil.isCorrectTimeZone(timeZone)) {
			Configuration.logger.error("Req. M7 Error");
			return;
		}

		if (Configuration.backend.equals("MongoDB")) {
			MongoCaptureUtil m = new MongoCaptureUtil();
			m.capture(event, userID, accessModifier, gcpLength);
		}else if(Configuration.backend.equals("MySQL")){
			MysqlCaptureUtil m = new MysqlCaptureUtil();
			m.capture(event);
		}
	}

	public void capture(TransactionEventType event, String userID, String accessModifier, Integer gcpLength) {

		// General Exception Handling
		// M7
		String timeZone = event.getEventTimeZoneOffset();
		if (!CaptureUtil.isCorrectTimeZone(timeZone)) {
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
			m.capture(event, userID, accessModifier, gcpLength);
		}else if(Configuration.backend.equals("MySQL")){
			MysqlCaptureUtil m = new MysqlCaptureUtil();
			m.capture(event);
		}
	}

	public void capture(TransformationEventType event, String userID, String accessModifier, Integer gcpLength) {
		// General Exception Handling
		// M7
		String timeZone = event.getEventTimeZoneOffset();
		if (!CaptureUtil.isCorrectTimeZone(timeZone)) {
			Configuration.logger.error("Req. M7 Error");
			return;
		}
		if (Configuration.backend.equals("MongoDB")) {
			MongoCaptureUtil m = new MongoCaptureUtil();
			m.capture(event, userID, accessModifier, gcpLength);
		}else if(Configuration.backend.equals("MySQL")){
			MysqlCaptureUtil m = new MysqlCaptureUtil();
			m.capture(event);
		}
	}

	public void capture(VocabularyType vocabulary, Integer gcpLength) {
		if (Configuration.backend.equals("MongoDB")) {
			// Previous Logic
			// MongoCaptureUtil m = new MongoCaptureUtil();
			// m.capture(vocabulary);

			MasterDataWriteConverter mdConverter = new MasterDataWriteConverter();
			mdConverter.capture(vocabulary, gcpLength);
		}else if(Configuration.backend.equals("MySQL")){
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
		EventListType eventListType = epcisDocument.getEPCISBody().getEventList();
		List<Object> eventList = eventListType.getObjectEventOrAggregationEventOrQuantityEvent();

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
				capture((ObjectEventType) event, null, null, null);
			} else if (event instanceof AggregationEventType) {
				capture((AggregationEventType) event, null, null, null);
			} else if (event instanceof TransactionEventType) {
				capture((TransactionEventType) event, null, null, null);
			} else if (event instanceof TransformationEventType) {
				capture((TransformationEventType) event, null, null, null);
			} else if (event instanceof QuantityEventType) {
				capture((QuantityEventType) event, null, null, null);
			}
		}
	}

	@SuppressWarnings("rawtypes")
	public void capture(EPCISDocumentType epcisDocument, String userID, String accessModifier, Integer gcpLength) {
		if (epcisDocument.getEPCISBody() == null) {
			Configuration.logger.info(" There is no DocumentBody ");
			return;
		}
		if (epcisDocument.getEPCISBody().getEventList() == null) {
			Configuration.logger.info(" There is no EventList ");
			return;
		}
		EventListType eventListType = epcisDocument.getEPCISBody().getEventList();
		List<Object> eventList = eventListType.getObjectEventOrAggregationEventOrQuantityEvent();

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
				capture((ObjectEventType) event, userID, accessModifier, gcpLength);
			} else if (event instanceof AggregationEventType) {
				capture((AggregationEventType) event, userID, accessModifier, gcpLength);
			} else if (event instanceof TransactionEventType) {
				capture((TransactionEventType) event, userID, accessModifier, gcpLength);
			} else if (event instanceof TransformationEventType) {
				capture((TransformationEventType) event, userID, accessModifier, gcpLength);
			} else if (event instanceof QuantityEventType) {
				capture((QuantityEventType) event, userID, accessModifier, gcpLength);
			}
		}
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

		VocabularyListType vocabularyListType = epcisMasterDataDocument.getEPCISBody().getVocabularyList();

		List<VocabularyType> vocabularyTypeList = vocabularyListType.getVocabulary();

		for (int i = 0; i < vocabularyTypeList.size(); i++) {
			VocabularyType vocabulary = vocabularyTypeList.get(i);
			if (vocabulary.getVocabularyElementList() != null) {
				if (vocabulary.getVocabularyElementList().getVocabularyElement() != null) {
					List<VocabularyElementType> vetList = vocabulary.getVocabularyElementList().getVocabularyElement();
					List<VocabularyElementType> vetTempList = new ArrayList<VocabularyElementType>();
					for (int j = 0; j < vetList.size(); j++) {
						VocabularyElementType vet = vetList.get(j);
						VocabularyElementType vetTemp = new VocabularyElementType();
						vetTemp = vet;
						vetTempList.add(vetTemp);
					}
					for (int j = 0; j < vetTempList.size(); j++) {
						vocabulary.getVocabularyElementList().getVocabularyElement().clear();
						vocabulary.getVocabularyElementList().getVocabularyElement().add(vetTempList.get(j));
						capture(vocabulary, null);
					}
				}
			}

		}
	}

	public void capture(EPCISMasterDataDocumentType epcisMasterDataDocument, Integer gcpLength) {

		if (epcisMasterDataDocument.getEPCISBody() == null) {
			Configuration.logger.info(" There is no DocumentBody ");
			return;
		}

		if (epcisMasterDataDocument.getEPCISBody().getVocabularyList() == null) {
			Configuration.logger.info(" There is no Vocabulary List ");
			return;
		}

		VocabularyListType vocabularyListType = epcisMasterDataDocument.getEPCISBody().getVocabularyList();

		List<VocabularyType> vocabularyTypeList = vocabularyListType.getVocabulary();

		for (int i = 0; i < vocabularyTypeList.size(); i++) {
			VocabularyType vocabulary = vocabularyTypeList.get(i);
			if (vocabulary.getVocabularyElementList() != null) {
				if (vocabulary.getVocabularyElementList().getVocabularyElement() != null) {
					List<VocabularyElementType> vetList = vocabulary.getVocabularyElementList().getVocabularyElement();
					List<VocabularyElementType> vetTempList = new ArrayList<VocabularyElementType>();
					for (int j = 0; j < vetList.size(); j++) {
						VocabularyElementType vet = vetList.get(j);
						VocabularyElementType vetTemp = new VocabularyElementType();
						vetTemp = vet;
						vetTempList.add(vetTemp);
					}
					for (int j = 0; j < vetTempList.size(); j++) {
						vocabulary.getVocabularyElementList().getVocabularyElement().clear();
						vocabulary.getVocabularyElementList().getVocabularyElement().add(vetTempList.get(j));
						capture(vocabulary, gcpLength);
					}
				}
			}

		}
	}

}
