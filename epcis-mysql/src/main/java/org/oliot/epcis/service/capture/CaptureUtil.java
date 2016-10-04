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
import org.xml.sax.SAXException;

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
}
