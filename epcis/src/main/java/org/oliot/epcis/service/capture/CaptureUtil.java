package org.oliot.epcis.service.capture;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Level;
import org.oliot.epcis.configuration.Configuration;
import org.oliot.epcis.security.OAuthUtil;
import org.oliot.gcp.core.SimplePureIdentityFilter;
import org.oliot.model.epcis.ActionType;
import org.oliot.model.epcis.AggregationEventType;
import org.oliot.model.epcis.DocumentIdentification;
import org.oliot.model.epcis.EPCISDocumentType;
import org.oliot.model.epcis.EPCISEventListExtensionType;
import org.oliot.model.epcis.ObjectEventType;
import org.oliot.model.epcis.QuantityEventType;
import org.oliot.model.epcis.StandardBusinessDocumentHeader;
import org.oliot.model.epcis.TransactionEventType;
import org.oliot.model.epcis.TransformationEventType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.xml.sax.SAXException;

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

public class CaptureUtil {
	public static boolean validate(InputStream is, String xsdPath) {
		try {
			SchemaFactory schemaFactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
			File xsdFile = new File(xsdPath);
			Schema schema = schemaFactory.newSchema(xsdFile);
			Validator validator = schema.newValidator();
			StreamSource xmlSource = new StreamSource(is);
			validator.validate(xmlSource);
			return true;
		} catch (SAXException e) {
			Configuration.logger.log(Level.ERROR, e.toString());
			return false;
		} catch (IOException e) {
			Configuration.logger.log(Level.ERROR, e.toString());
			return false;
		}
	}

	public static String getValidationException(InputStream is, String docPath) {
		try {
			SchemaFactory schemaFactory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
			File xsdFile = new File(docPath);
			Schema schema = schemaFactory.newSchema(xsdFile);
			Validator validator = schema.newValidator();
			StreamSource xmlSource = new StreamSource(is);
			validator.validate(xmlSource);
			return null;
		} catch (SAXException e) {
			return e.toString();
		} catch (IOException e) {
			return e.toString();
		}
	}

	public static InputStream getXMLDocumentInputStream(String xmlString) {
		InputStream stream = new ByteArrayInputStream(xmlString.getBytes(StandardCharsets.UTF_8));
		return stream;
	}

	public static String getXMLDocumentString(InputStream is) {
		try {
			StringWriter writer = new StringWriter();
			IOUtils.copy(is, writer, "UTF-8");
			String xmlString = writer.toString();
			return xmlString;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String makeTimeZoneString(int timeZone) {
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

	public static boolean isCorrectTimeZone(String timeZone) {

		boolean isMatch = timeZone.matches("^(?:Z|[+-](?:2[0-3]|[01][0-9]):[0-5][0-9])$");

		return isMatch;
	}

	public static ResponseEntity<?> minorCheckDocumentHeader(EPCISDocumentType epcisDocument) {
		// M50, M63
		if (epcisDocument.getEPCISHeader() != null) {
			if (epcisDocument.getEPCISHeader().getStandardBusinessDocumentHeader() != null) {
				StandardBusinessDocumentHeader header = epcisDocument.getEPCISHeader()
						.getStandardBusinessDocumentHeader();
				if (header.getHeaderVersion() == null || !header.getHeaderVersion().equals("1.2")) {
					Configuration.logger.error(" HeaderVersion should 1.2 if use SBDH ");
					return new ResponseEntity<>(new String("Error: HeaderVersion should 1.2 if use SBDH"),
							HttpStatus.BAD_REQUEST);
				}
				if (header.getDocumentIdentification() == null) {
					Configuration.logger.error(" DocumentIdentification should exist if use SBDH ");
					return new ResponseEntity<>(new String("Error: DocumentIdentification should exist if use SBDH"),
							HttpStatus.BAD_REQUEST);
				} else {
					DocumentIdentification docID = header.getDocumentIdentification();
					if (docID.getStandard() == null | !docID.getStandard().equals("EPCglobal")) {
						Configuration.logger.error(" DocumentIdentification/Standard should EPCglobal if use SBDH ");
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
					if (docID.getTypeVersion() == null | !docID.getTypeVersion().equals("1.2")) {
						Configuration.logger.error(" DocumentIdentification/TypeVersion should 1.2 if use SBDH ");
						return new ResponseEntity<>(
								new String("Error: DocumentIdentification/TypeVersion should 1.2 if use SBDH"),
								HttpStatus.BAD_REQUEST);
					}

				}
			}
		}
		return null;
	}

	public static ResponseEntity<?> checkAccessToken(String userID, @RequestParam(required = false) String accessToken,
			@RequestParam(required = false) String accessModifier) {
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
		return null;
	}

	public static boolean isCorrectEvent(Object event) {
		if (event instanceof ObjectEventType) {
			return isCorrectObjectEvent((ObjectEventType) event);
		} else if (event instanceof AggregationEventType) {
			return isCorrectAggregationEvent((AggregationEventType) event);
		} else if (event instanceof TransactionEventType) {
			return isCorrectTransactionEvent((TransactionEventType) event);
		} else if (event instanceof QuantityEventType) {
			return isCorrectQuantityEvent((QuantityEventType) event);
		} else if (event instanceof EPCISEventListExtensionType) {
			return isCorrectTransformationEvent(((EPCISEventListExtensionType) event).getTransformationEvent());
		}
		return true;
	}

	public static boolean isCorrectAggregationEvent(AggregationEventType event) {
		// General Exception Handling
		// M7
		String timeZone = event.getEventTimeZoneOffset();
		if (!CaptureUtil.isCorrectTimeZone(timeZone)) {
			Configuration.logger.error("Req. M7 Error");
			return false;
		}

		// Mandatory Field: Action
		if (event.getAction() == null) {
			Configuration.logger.error("Aggregation Event should have 'Action' field ");
			return false;
		}
		// M13
		if (event.getAction() == ActionType.ADD || event.getAction() == ActionType.DELETE) {
			if (event.getParentID() == null) {
				Configuration.logger.error("Req. M13 Error");
				return false;
			}
		}
		// M10
		String parentID = event.getParentID();
		if (parentID != null) {

			if (SimplePureIdentityFilter.isPureIdentity(parentID) == false) {
				Configuration.logger.error("Req. M10 Error");
				return false;
			}
		}
		return true;
	}

	public static boolean isCorrectObjectEvent(ObjectEventType event) {
		// General Exception Handling
		// M7
		String timeZone = event.getEventTimeZoneOffset();
		if (!CaptureUtil.isCorrectTimeZone(timeZone)) {
			Configuration.logger.error("Req. M7 Error");
			return false;
		}
		return true;
	}

	public static boolean isCorrectTransactionEvent(TransactionEventType event) {
		// General Exception Handling
		// M7
		String timeZone = event.getEventTimeZoneOffset();
		if (!CaptureUtil.isCorrectTimeZone(timeZone)) {
			Configuration.logger.error("Req. M7 Error");
			return false;
		}

		// M14
		String parentID = event.getParentID();
		if (parentID != null) {

			if (SimplePureIdentityFilter.isPureIdentity(parentID) == false) {
				Configuration.logger.error("Req. M14 Error");
				return false;
			}
		}
		return true;
	}

	public static boolean isCorrectQuantityEvent(QuantityEventType event) {
		// General Exception Handling
		// M7
		String timeZone = event.getEventTimeZoneOffset();
		if (!CaptureUtil.isCorrectTimeZone(timeZone)) {
			Configuration.logger.error("Req. M7 Error");
			return false;
		}
		return true;
	}

	public static boolean isCorrectTransformationEvent(TransformationEventType event) {
		// General Exception Handling
		// M7
		String timeZone = event.getEventTimeZoneOffset();
		if (!CaptureUtil.isCorrectTimeZone(timeZone)) {
			Configuration.logger.error("Req. M7 Error");
			return false;
		}
		return true;
	}
}
