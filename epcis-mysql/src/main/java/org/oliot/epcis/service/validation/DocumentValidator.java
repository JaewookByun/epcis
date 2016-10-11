package org.oliot.epcis.service.validation;

import java.io.InputStream;
import java.util.List;

import javax.servlet.ServletContext;
import javax.xml.bind.JAXB;

import org.oliot.epcis.service.capture.CaptureUtil;
import org.json.JSONObject;
import org.oliot.epcis.configuration.Configuration;
import org.oliot.model.epcis.EPCISDocumentType;
import org.oliot.model.epcis.EPCISMasterDataDocumentType;
import org.oliot.model.epcis.VocabularyType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.ServletContextAware;

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

@Controller
public class DocumentValidator implements ServletContextAware {

	@Autowired
	ServletContext servletContext;

	@Override
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	@RequestMapping(value = "/EPCISDocumentValidation", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> validateEpcisDocument(@RequestBody String inputString) {
		JSONObject retJSON = new JSONObject();
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "application/json; charset=utf-8");
		InputStream validateStream = CaptureUtil.getXMLDocumentInputStream(inputString);
		String exception = CaptureUtil.getValidationException(validateStream,
				Configuration.wsdlPath + "/EPCglobal-epcis-1_2.xsd");
		if (exception == null) {
			retJSON.put("isValidated", true);
			retJSON.put("schemaVersion", 1.2);
			InputStream epcisStream = CaptureUtil.getXMLDocumentInputStream(inputString);
			EPCISDocumentType epcisDocument = JAXB.unmarshal(epcisStream, EPCISDocumentType.class);
			if (epcisDocument.getEpcisBody() != null && epcisDocument.getEpcisBody().getEventList() != null
					&& epcisDocument.getEpcisBody().getEventList()
							.getObjectEventOrAggregationEventOrQuantityEvent() != null) {
				retJSON.put("eventDataCount", epcisDocument.getEpcisBody().getEventList()
						.getObjectEventOrAggregationEventOrQuantityEvent().size());
			} else {
				retJSON.put("eventDataCount", 0);
			}
			if (epcisDocument.getEpcisHeader() != null && epcisDocument.getEpcisHeader().getExtension() != null
					&& epcisDocument.getEpcisHeader().getExtension().getEpcisMasterData() != null
					&& epcisDocument.getEpcisHeader().getExtension().getEpcisMasterData().getVocabularyList() != null
					&& epcisDocument.getEpcisHeader().getExtension().getEpcisMasterData().getVocabularyList()
							.getVocabulary() != null) {
				List<VocabularyType> vList = epcisDocument.getEpcisHeader().getExtension().getEpcisMasterData()
						.getVocabularyList().getVocabulary();
				retJSON.put("vocabularyCount", vList.size());
				retJSON.put("vocabularyElementCount", getNumberOfVocabularyElements(vList));
			} else {
				retJSON.put("vocabularyCount", 0);
				retJSON.put("vocabularyElementCount", 0);
			}
			return new ResponseEntity<>(retJSON.toString(1), responseHeaders, HttpStatus.OK);
		} else {
			retJSON.put("isValidated", false);
			retJSON.put("errorMessage", exception);
			return new ResponseEntity<>(retJSON.toString(1), responseHeaders, HttpStatus.BAD_REQUEST);
		}
	}

	@RequestMapping(value = "/EPCISMasterDataDocumentValidation", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> validateEpcisMasterDataDocument(@RequestBody String inputString) {
		JSONObject retJSON = new JSONObject();
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "application/json; charset=utf-8");
		InputStream validateStream = CaptureUtil.getXMLDocumentInputStream(inputString);
		String exception = CaptureUtil.getValidationException(validateStream,
				Configuration.wsdlPath + "/EPCglobal-epcis-masterdata-1_2.xsd");
		if (exception == null) {
			retJSON.put("isValidated", true);
			retJSON.put("schemaVersion", 1.2);
			InputStream epcisStream = CaptureUtil.getXMLDocumentInputStream(inputString);
			EPCISMasterDataDocumentType epcisMasterDataDocument = JAXB.unmarshal(epcisStream,
					EPCISMasterDataDocumentType.class);

			if (epcisMasterDataDocument.getEpcisBody() != null
					&& epcisMasterDataDocument.getEpcisBody().getVocabularyList() != null
					&& epcisMasterDataDocument.getEpcisBody().getVocabularyList().getVocabulary() != null) {
				List<VocabularyType> vList = epcisMasterDataDocument.getEpcisBody().getVocabularyList().getVocabulary();
				retJSON.put("vocabularyCount", vList.size());
				retJSON.put("vocabularyElementCount", getNumberOfVocabularyElements(vList));
			} else {
				retJSON.put("vocabularyCount", 0);
				retJSON.put("vocabularyElementCount", 0);
			}
			return new ResponseEntity<>(retJSON.toString(1), responseHeaders, HttpStatus.OK);
		} else {
			retJSON.put("isValidated", false);
			retJSON.put("errorMessage", exception);
			return new ResponseEntity<>(retJSON.toString(1), responseHeaders, HttpStatus.BAD_REQUEST);
		}
	}
	
	public int getNumberOfVocabularyElements(List<VocabularyType> vList){
		int cnt = 0;
		for(VocabularyType vocabulary: vList){
			if( vocabulary.getVocabularyElementList() != null && vocabulary.getVocabularyElementList().getVocabularyElement() != null){
				cnt += vocabulary.getVocabularyElementList().getVocabularyElement().size();				
			}
		}
		return cnt;
	}
}
