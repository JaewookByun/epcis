package org.oliot.epcis.service.capture;

import java.io.InputStream;

import javax.servlet.ServletContext;
import javax.xml.bind.JAXB;

import org.oliot.epcis.configuration.Configuration;
import org.oliot.model.epcis.EPCISMasterDataDocumentType;
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
@RequestMapping("/VocabularyCapture")
public class VocabularyCapture implements ServletContextAware {
	@Autowired
	ServletContext servletContext;

	@Override
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	public ResponseEntity<?> asyncPost(String inputString) {
		ResponseEntity<?> result = post(inputString, null);
		return result;
	}

	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> post(@RequestBody String inputString, @RequestParam(required = false) Integer gcpLength) {
		Configuration.logger.info(" EPCIS Masterdata Document Capture Started.... ");

		if (Configuration.isCaptureVerfificationOn == true) {
			InputStream validateStream = CaptureUtil.getXMLDocumentInputStream(inputString);
			// Parsing and Validating data
			boolean isValidated = CaptureUtil.validate(validateStream,
					Configuration.wsdlPath + "/EPCglobal-epcis-masterdata-1_1_jack.xsd");
			if (isValidated == false) {
				return new ResponseEntity<>(new String("Error: EPCIS Masterdata Document is not validated"),
						HttpStatus.BAD_REQUEST);
			}

			InputStream epcisStream = CaptureUtil.getXMLDocumentInputStream(inputString);
			Configuration.logger.info(" EPCIS Masterdata Document : Validated ");
			EPCISMasterDataDocumentType epcisMasterDataDocument = JAXB.unmarshal(epcisStream,
					EPCISMasterDataDocumentType.class);

			CaptureService cs = new CaptureService();
			cs.capture(epcisMasterDataDocument, gcpLength);
			Configuration.logger.info(" EPCIS Masterdata Document : Captured ");
		} else {
			InputStream epcisStream = CaptureUtil.getXMLDocumentInputStream(inputString);
			EPCISMasterDataDocumentType epcisMasterDataDocument = JAXB.unmarshal(epcisStream,
					EPCISMasterDataDocumentType.class);
			CaptureService cs = new CaptureService();
			cs.capture(epcisMasterDataDocument, gcpLength);
			Configuration.logger.info(" EPCIS Masterdata Document : Captured ");
		}
		return new ResponseEntity<>(new String("EPCIS Masterdata Document : Captured"), HttpStatus.OK);
	}
}
