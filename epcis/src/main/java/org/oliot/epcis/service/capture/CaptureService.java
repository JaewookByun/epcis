package org.oliot.epcis.service.capture;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBElement;

import org.bson.BsonDocument;
import org.json.JSONObject;
import org.oliot.epcis.configuration.Configuration;
import org.oliot.epcis.service.capture.mongodb.MongoCaptureUtil;

import org.oliot.model.epcis.EPCISDocumentType;

import org.oliot.model.epcis.EPCISMasterDataDocumentType;
import org.oliot.model.epcis.EventListType;

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
 * @author Jaewook Byun, Ph.D student
 * 
 *         Korea Advanced Institute of Science and Technology (KAIST)
 * 
 *         Real-time Embedded System Laboratory(RESL)
 * 
 *         bjw0829@kaist.ac.kr, bjw0829@gmail.com
 */

public class CaptureService implements CoreCaptureService {

	// Return null -> Succeed, not null --> error message
	public JSONObject capture(EPCISDocumentType epcisDocument, String userID, String accessModifier,
			Integer gcpLength) {

		// Minor EPCIS Document Checking
		HashMap<String, Object> retMsg = minorCheckDocument(epcisDocument);
		if (retMsg.isEmpty() == false)
			return new JSONObject(retMsg);

		retMsg = new HashMap<String, Object>();

		// Capture EPCIS Events
		retMsg.putAll(captureEvents(epcisDocument, userID, accessModifier, gcpLength));
		// Capture EPCIS Vocabularies
		retMsg.putAll(captureVocabularies(epcisDocument, userID, accessModifier, gcpLength));

		return new JSONObject(retMsg);
	}

	public JSONObject capture(EPCISMasterDataDocumentType epcisMasterDataDocument, Integer gcpLength) {

		// Minor EPCIS Document Checking
		HashMap<String, Object> retMsg = minorCheckDocument(epcisMasterDataDocument);
		if (retMsg.isEmpty() == false)
			return new JSONObject(retMsg);

		retMsg = new HashMap<String, Object>();

		VocabularyListType vocabularyListType = epcisMasterDataDocument.getEPCISBody().getVocabularyList();

		List<VocabularyType> vocabularyTypeList = vocabularyListType.getVocabulary();

		retMsg.putAll(captureVocabularies(vocabularyTypeList, gcpLength));

		return new JSONObject(retMsg);
	}

	@SuppressWarnings("rawtypes")
	private BsonDocument prepareEvent(Object jaxbEvent, String userID, String accessModifier, Integer gcpLength) {
		JAXBElement eventElement = (JAXBElement) jaxbEvent;
		Object event = eventElement.getValue();

		CaptureUtil.isCorrectEvent(event);
		MongoCaptureUtil m = new MongoCaptureUtil();
		BsonDocument doc = m.convert(event, userID, accessModifier, gcpLength);
		return doc;
	}

	private HashMap<String, Object> captureEvents(EPCISDocumentType epcisDocument, String userID, String accessModifier,
			Integer gcpLength) {
		EventListType eventListType = epcisDocument.getEPCISBody().getEventList();
		List<Object> eventList = eventListType.getObjectEventOrAggregationEventOrQuantityEvent();

		List<BsonDocument> bsonDocumentList = eventList.parallelStream().parallel()
				.map(jaxbEvent -> prepareEvent(jaxbEvent, userID, accessModifier, gcpLength)).filter(doc -> doc != null)
				.collect(Collectors.toList());

		MongoCaptureUtil util = new MongoCaptureUtil();
		if (bsonDocumentList != null && bsonDocumentList.size() != 0)
			return util.capture(bsonDocumentList);
		return new HashMap<String, Object>();
	}

	private HashMap<String, Object> captureVocabularies(EPCISDocumentType epcisDocument, String userID,
			String accessModifier, Integer gcpLength) {

		HashMap<String, Object> retMsg = new HashMap<String, Object>();
		// Master Data in the document
		if (epcisDocument.getEPCISHeader() != null && epcisDocument.getEPCISHeader().getExtension() != null
				&& epcisDocument.getEPCISHeader().getExtension().getEPCISMasterData() != null
				&& epcisDocument.getEPCISHeader().getExtension().getEPCISMasterData().getVocabularyList() != null
				&& epcisDocument.getEPCISHeader().getExtension().getEPCISMasterData().getVocabularyList()
						.getVocabulary() != null) {
			List<VocabularyType> vocabularyTypeList = epcisDocument.getEPCISHeader().getExtension().getEPCISMasterData()
					.getVocabularyList().getVocabulary();

			retMsg = captureVocabularies(vocabularyTypeList, gcpLength);
		}
		return retMsg;
	}

	private HashMap<String, Object> captureVocabularies(List<VocabularyType> vocabularyTypeList, Integer gcpLength) {

		HashMap<String, Object> retMsg = new HashMap<String, Object>();

		int cntVoc = 0;
		
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
							retMsg.put("error", message);
						}else{
							cntVoc++;
						}
					}
				}
			}
		}

		retMsg.put("vocabularyCaptured", cntVoc);
		
		return retMsg;
	}
	
	

	private String capture(VocabularyType vocabulary, Integer gcpLength) {
		MongoCaptureUtil m = new MongoCaptureUtil();
		return m.capture(vocabulary, null, null, gcpLength);
	}

	private HashMap<String, Object> minorCheckDocument(EPCISDocumentType epcisDocument) {
		HashMap<String, Object> retMsg = new HashMap<String, Object>();
		if (epcisDocument.getEPCISBody() == null) {
			Configuration.logger.info(" There is no DocumentBody ");
			retMsg.put("error", "There is no DocumentBody");
			return retMsg;
		}
		if (epcisDocument.getEPCISBody().getEventList() == null) {
			Configuration.logger.info(" There is no EventList ");
			retMsg.put("error", "There is no EventList");
			return retMsg;
		}
		return retMsg;
	}

	private HashMap<String, Object> minorCheckDocument(EPCISMasterDataDocumentType epcisMasterDataDocument) {
		HashMap<String, Object> retMsg = new HashMap<String, Object>();
		if (epcisMasterDataDocument.getEPCISBody() == null) {
			Configuration.logger.info(" There is no DocumentBody ");
			retMsg.put("error", "There is no DocumentBody ");
			return retMsg;
		}

		if (epcisMasterDataDocument.getEPCISBody().getVocabularyList() == null) {
			Configuration.logger.info(" There is no Vocabulary List ");
			retMsg.put("error", "There is no Vocabulary List ");
			return retMsg;
		}
		return retMsg;
	}

	@Override
	public void capture(EPCISDocumentType epcisDocument) {
		capture(epcisDocument, null, null, null);
	}

	@Override
	public void capture(EPCISMasterDataDocumentType epcisMasterDataDocument) {
		capture(epcisMasterDataDocument, null);
	}
}
