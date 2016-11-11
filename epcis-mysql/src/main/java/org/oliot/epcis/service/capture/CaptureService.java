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
//import org.oliot.epcis.service.capture.mongodb.MongoCaptureUtil;
import org.oliot.epcis.service.capture.mysql.MysqlCaptureUtil;
import org.oliot.gcp.core.SimplePureIdentityFilter;
import org.oliot.model.epcis.ActionType;
import org.oliot.model.epcis.AggregationEventType;
import org.oliot.model.epcis.EPCISDocumentType;
import org.oliot.model.epcis.EPCISEventListExtensionType;
import org.oliot.model.epcis.EPCISMasterDataDocumentType;
import org.oliot.model.epcis.EventListType;
import org.oliot.model.epcis.ObjectEventType;
import org.oliot.model.epcis.QuantityEventType;
import org.oliot.model.epcis.TransactionEventType;
import org.oliot.model.epcis.TransformationEventType;
import org.oliot.model.epcis.VocabularyElementType;
import org.oliot.model.epcis.VocabularyListType;
import org.oliot.model.epcis.VocabularyType;

/**
 * Copyright (C) 2014-2016 Jaewook Byun
 *
 * This project is part of Oliot open source (http://oliot.org). Oliot EPCIS
 * v1.2.x is Java Web Service complying with Electronic Product Code Information
 * Service (EPCIS) v1.2.
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

	public String capture(AggregationEventType event, String userID, String accessModifier, Integer gcpLength) {

		// General Exception Handling
		// M7
		String timeZone = event.getEventTimeZoneOffset();
		if (!CaptureUtil.isCorrectTimeZone(timeZone)) {
			Configuration.logger.error("Req. M7 Error");
			return "[Error] Req. M7 Error";
		}

		// Mandatory Field: Action
		if (event.getAction() == null) {
			Configuration.logger.error("Aggregation Event should have 'Action' field ");
			return "[Error] Aggregation Event should have 'Action' field ";
		}
		// M13
		if (event.getAction() == ActionType.ADD || event.getAction() == ActionType.DELETE) {
			if (event.getParentID() == null) {
				Configuration.logger.error("Req. M13 Error");
				return "[Error] Req. M13 Error";
			}
		}
		// M10
		String parentID = event.getParentID();
		if (parentID != null) {

			if (SimplePureIdentityFilter.isPureIdentity(parentID) == false) {
				Configuration.logger.error("Req. M10 Error");
				return "[Error] Req. M10 Error";
			}
		}

		//MongoCaptureUtil m = new MongoCaptureUtil();
		//return m.capture(event, userID, accessModifier, gcpLength);
		
		MysqlCaptureUtil m = new MysqlCaptureUtil();
		m.capture(event);
		return null;
	}

	public String capture(ObjectEventType event, String userID, String accessModifier, Integer gcpLength) {

		// General Exception Handling
		// M7
		String timeZone = event.getEventTimeZoneOffset();
		if (!CaptureUtil.isCorrectTimeZone(timeZone)) {
			Configuration.logger.error("Req. M7 Error");
			return "[Error] Req. M7 Error";
		}
		Configuration.logger.info("Objec event capturing started");
		//MongoCaptureUtil m = new MongoCaptureUtil();
		//return m.capture(event, userID, accessModifier, gcpLength);
		MysqlCaptureUtil m = new MysqlCaptureUtil();
		m.capture(event);
		return null;
	}

	public String capture(QuantityEventType event, String userID, String accessModifier, Integer gcpLength) {

		// General Exception Handling
		// M7
		String timeZone = event.getEventTimeZoneOffset();
		if (!CaptureUtil.isCorrectTimeZone(timeZone)) {
			Configuration.logger.error("Req. M7 Error");
			return "[Error] Req. M7 Error";
		}

		//MongoCaptureUtil m = new MongoCaptureUtil();
		//return m.capture(event, userID, accessModifier, gcpLength);
		Configuration.logger.info("Quantity event capturing started");
		MysqlCaptureUtil m = new MysqlCaptureUtil();
		m.capture(event);
		return null;
	}

	public String capture(TransactionEventType event, String userID, String accessModifier, Integer gcpLength) {

		// General Exception Handling
		// M7
		String timeZone = event.getEventTimeZoneOffset();
		if (!CaptureUtil.isCorrectTimeZone(timeZone)) {
			Configuration.logger.error("Req. M7 Error");
			return "[Error]Req. M7 Error";
		}

		// M14
		String parentID = event.getParentID();
		if (parentID != null) {

			if (SimplePureIdentityFilter.isPureIdentity(parentID) == false) {
				Configuration.logger.error("Req. M14 Error");
				return "Req. M14 Error";
			}
		}

		//MongoCaptureUtil m = new MongoCaptureUtil();
		//return m.capture(event, userID, accessModifier, gcpLength);
		
		Configuration.logger.info("Transaction event capturing started");
		MysqlCaptureUtil m = new MysqlCaptureUtil();
		m.capture(event);
		return null;
	}

	public String capture(TransformationEventType event, String userID, String accessModifier, Integer gcpLength) {
		// General Exception Handling
		// M7
		String timeZone = event.getEventTimeZoneOffset();
		if (!CaptureUtil.isCorrectTimeZone(timeZone)) {
			Configuration.logger.error("Req. M7 Error");
			return "[Error]Req. M7 Error";
		}
		//MongoCaptureUtil m = new MongoCaptureUtil();
		//return m.capture(event, userID, accessModifier, gcpLength);
		
		Configuration.logger.info("Transformation event capturing started");
		MysqlCaptureUtil m = new MysqlCaptureUtil();
		m.capture(event);
		return null;
	}

	public String capture(VocabularyType vocabulary, Integer gcpLength) {
		// mysql updata here ------   
		//MongoCaptureUtil m = new MongoCaptureUtil();
		//return m.capture(vocabulary, null, null, gcpLength);
		
		Configuration.logger.info("Vocabulary capturing started");
		MysqlCaptureUtil m = new MysqlCaptureUtil();
		m.capture(vocabulary);
		return null;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void capture(EPCISDocumentType epcisDocument) {
		Configuration.logger.info("Implimenting: capture(EPCISDocumentType epcisDocument --- mysql) ");
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
	// Return null -> Succeed, not null --> error message
	public String capture(EPCISDocumentType epcisDocument, String userID, String accessModifier, Integer gcpLength) {
		Configuration.logger.info("Implimenting: capture(EPCISDocumentType epcisDocument, String userID, String accessModifier, Integer gcpLength ---mysql");
		String errorMessage = null;
		if (epcisDocument.getEPCISBody() == null) {
			Configuration.logger.info(" There is no DocumentBody ");
			return "[ERROR] There is no DocumentBody ";
		}
		if (epcisDocument.getEPCISBody().getEventList() == null) {
			Configuration.logger.info(" There is no EventList ");
			return null;
		}

		// Master Data in the document
		if (epcisDocument.getEPCISHeader() != null && epcisDocument.getEPCISHeader().getExtension() != null
				&& epcisDocument.getEPCISHeader().getExtension().getEPCISMasterData() != null
				&& epcisDocument.getEPCISHeader().getExtension().getEPCISMasterData().getVocabularyList() != null
				&& epcisDocument.getEPCISHeader().getExtension().getEPCISMasterData().getVocabularyList()
						.getVocabulary() != null) {
			List<VocabularyType> vocabularyTypeList = epcisDocument.getEPCISHeader().getExtension().getEPCISMasterData()
					.getVocabularyList().getVocabulary();

			for (int i = 0; i < vocabularyTypeList.size(); i++) {
				VocabularyType vocabulary = vocabularyTypeList.get(i);
				if (vocabulary.getVocabularyElementList() != null) {
					if (vocabulary.getVocabularyElementList().getVocabularyElement() != null) {
						List<VocabularyElementType> vetList = vocabulary.getVocabularyElementList()
								.getVocabularyElement();
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
							String message = capture(vocabulary, gcpLength);
							if (message != null) {
								if (errorMessage == null)
									errorMessage = message + "\n";
								else
									errorMessage = errorMessage + message + "\n";
							}
						}
					}
				}
			}
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
				String message = capture((ObjectEventType) event, userID, accessModifier, gcpLength);
				if (message != null) {
					if (errorMessage == null)
						errorMessage = message + "\n";
					else
						errorMessage = errorMessage + message + "\n";
				}
			} else if (event instanceof AggregationEventType) {
				String message = capture((AggregationEventType) event, userID, accessModifier, gcpLength);
				if (message != null) {
					if (errorMessage == null)
						errorMessage = message + "\n";
					else
						errorMessage = errorMessage + message + "\n";
				}
			} else if (event instanceof TransactionEventType) {
				String message = capture((TransactionEventType) event, userID, accessModifier, gcpLength);
				if (message != null) {
					if (errorMessage == null)
						errorMessage = message + "\n";
					else
						errorMessage = errorMessage + message + "\n";
				}
			} /*
				 * else if (event instanceof TransformationEventType) {
				 * capture((TransformationEventType) event, userID,
				 * accessModifier, gcpLength); }
				 */
			else if (event instanceof QuantityEventType) {
				String message = capture((QuantityEventType) event, userID, accessModifier, gcpLength);
				if (message != null) {
					if (errorMessage == null)
						errorMessage = message + "\n";
					else
						errorMessage = errorMessage + message + "\n";
				}
			} else if (event instanceof EPCISEventListExtensionType) {
				// TransformationEvent is now included as
				// EPCISEventListExtensionType
				String message = capture(((EPCISEventListExtensionType) event).getTransformationEvent(), userID,
						accessModifier, gcpLength);
				if (message != null) {
					if (errorMessage == null)
						errorMessage = message + "\n";
					else
						errorMessage = errorMessage + message + "\n";
				}
			}
		}
		return errorMessage;
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

	public String capture(EPCISMasterDataDocumentType epcisMasterDataDocument, Integer gcpLength) {
		Configuration.logger.info("Implimenting: capture(EPCISMasterDataDocumentType epcisMasterDataDocument, Integer gcpLength)");
		String errorMessage = null;
		if (epcisMasterDataDocument.getEPCISBody() == null) {
			Configuration.logger.info(" There is no DocumentBody ");
			return "[ERROR] There is no DocumentBody ";
		}

		if (epcisMasterDataDocument.getEPCISBody().getVocabularyList() == null) {
			Configuration.logger.info(" There is no Vocabulary List ");
			return "[ERROR] There is no Vocabulary List ";
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
						String message = capture(vocabulary, gcpLength);
						if (message != null) {
							if (errorMessage == null)
								errorMessage = message + "\n";
							else
								errorMessage = errorMessage + message + "\n";
						}
					}
				}
			}
		}
		return errorMessage;
	}
}
