package org.oliot.epcis.service.capture;

import java.io.InputStream;

import javax.servlet.ServletContext;
import javax.xml.bind.JAXB;

import org.oliot.epcis.security.OAuthUtil;
import org.oliot.epcis.configuration.Configuration;
import org.oliot.model.epcis.DocumentIdentification;
import org.oliot.model.epcis.EPCISDocumentType;
import org.oliot.model.epcis.StandardBusinessDocumentHeader;
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

@Controller
@RequestMapping("/EventCapture")
public class EventCapture implements ServletContextAware {

	@Autowired
	ServletContext servletContext;

	@Override
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	public ResponseEntity<?> asyncPost(String inputString) {
		ResponseEntity<?> result = post(inputString, null, null, null, null);
		return result;
	}

	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> post(@RequestBody String inputString, @RequestParam(required = false) String userID,
			@RequestParam(required = false) String accessToken, @RequestParam(required = false) String accessModifier, @RequestParam(required = false) Integer gcpLength) {

		// Request a protection on events
		if (userID != null) {
			// Check accessToken
			if (!OAuthUtil.isValidated(accessToken, userID)) {
				return new ResponseEntity<>(new String("Invalid AccessToken"), HttpStatus.BAD_REQUEST);
			}
			if (accessModifier == null) {
				return new ResponseEntity<>(new String("Need AccessModifier (Private,Friend)"), HttpStatus.BAD_REQUEST);
			}
			accessModifier = accessModifier.trim();
			if (!accessModifier.equals("Private") && !accessModifier.equals("Friend")) {
				return new ResponseEntity<>(new String("Need AccessModifier (Private,Friend)"), HttpStatus.BAD_REQUEST);
			}
		}

		Configuration.logger.info(" EPCIS Document Capture Started.... ");

		if (Configuration.isCaptureVerfificationOn == true) {
			InputStream validateStream = CaptureUtil.getXMLDocumentInputStream(inputString);
			boolean isValidated = CaptureUtil.validate(validateStream,
					Configuration.wsdlPath + "/EPCglobal-epcis-1_2_jack.xsd");
			if (isValidated == false) {
				// M63
				return new ResponseEntity<>(new String("Error M63"), HttpStatus.BAD_REQUEST);
			}
			InputStream epcisStream = CaptureUtil.getXMLDocumentInputStream(inputString);
			Configuration.logger.info(" EPCIS Document : Validated ");
			EPCISDocumentType epcisDocument = JAXB.unmarshal(epcisStream, EPCISDocumentType.class);

			// M50, M63
			if (epcisDocument.getEPCISHeader() != null) {
				if (epcisDocument.getEPCISHeader().getStandardBusinessDocumentHeader() != null) {
					StandardBusinessDocumentHeader header = epcisDocument.getEPCISHeader()
							.getStandardBusinessDocumentHeader();
					if (header.getHeaderVersion() == null || !header.getHeaderVersion().equals("1.1")) {
						Configuration.logger.error(" HeaderVersion should 1.1 if use SBDH ");
						return new ResponseEntity<>(new String("Error: HeaderVersion should 1.1 if use SBDH"),
								HttpStatus.BAD_REQUEST);
					}
					if (header.getDocumentIdentification() == null) {
						Configuration.logger.error(" DocumentIdentification should exist if use SBDH ");
						return new ResponseEntity<>(
								new String("Error: DocumentIdentification should exist if use SBDH"),
								HttpStatus.BAD_REQUEST);
					} else {
						DocumentIdentification docID = header.getDocumentIdentification();
						if (docID.getStandard() == null | !docID.getStandard().equals("EPCglobal")) {
							Configuration.logger
									.error(" DocumentIdentification/Standard should EPCglobal if use SBDH ");
							return new ResponseEntity<>(
									new String("Error: DocumentIdentification/Standard should EPCglobal if use SBDH"),
									HttpStatus.BAD_REQUEST);
						}
						if (docID.getType() == null
								|| (!docID.getType().equals("Events") && !docID.getType().equals("MasterData"))) {
							Configuration.logger.error(
									" DocumentIdentification/Type should Events|MasterData in Capture Method if use SBDH ");
							return new ResponseEntity<>(
									new String(
											"Error: DocumentIdentification/Type should Events|MasterData in Capture Method if use SBDH"),
									HttpStatus.BAD_REQUEST);
						}
						if (docID.getTypeVersion() == null | !docID.getTypeVersion().equals("1.1")) {
							Configuration.logger.error(" DocumentIdentification/TypeVersion should 1.1 if use SBDH ");
							return new ResponseEntity<>(
									new String("Error: DocumentIdentification/TypeVersion should 1.1 if use SBDH"),
									HttpStatus.BAD_REQUEST);
						}

					}
				}
			}

			CaptureService cs = new CaptureService();
			cs.capture(epcisDocument, userID, accessModifier, gcpLength);
			Configuration.logger.info(" EPCIS Document : Captured ");
		} else {
			InputStream epcisStream = CaptureUtil.getXMLDocumentInputStream(inputString);
			EPCISDocumentType epcisDocument = JAXB.unmarshal(epcisStream, EPCISDocumentType.class);
			CaptureService cs = new CaptureService();
			cs.capture(epcisDocument, userID, accessModifier, gcpLength);
			Configuration.logger.info(" EPCIS Document : Captured ");
		}
		return new ResponseEntity<>(new String("EPCIS Document : Captured "), HttpStatus.OK);
	}
}
