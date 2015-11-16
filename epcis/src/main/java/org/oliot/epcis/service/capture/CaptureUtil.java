package org.oliot.epcis.service.capture;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Level;
import org.json.JSONObject;
import org.oliot.epcis.configuration.Configuration;
import org.oliot.model.ale.ECReports;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;

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
	public static boolean validate(JSONObject Json, JSONObject schema_obj) {
		try {

			ObjectMapper mapper = new ObjectMapper();
			JsonNode input_node = mapper.readTree(Json.toString());
			JsonNode schema_node = mapper.readTree(schema_obj.toString());

			final JsonSchemaFactory factory = JsonSchemaFactory.byDefault();
			final JsonSchema schema = factory.getJsonSchema(schema_node);
			ProcessingReport report;
			report = schema.validate(input_node);
			Configuration.logger.info("validation process report : " + report);
			return report.isSuccess();

		} catch (IOException e) {
			Configuration.logger.log(Level.ERROR, e.toString());
			return false;
		} catch (ProcessingException e) {
			Configuration.logger.log(Level.ERROR, e.toString());
			return false;
		}
	}
	
	public static boolean isReportNull(ECReports ecReports) {

		if (ecReports.getReports() == null) {
			return true;
		}
		if (ecReports.getReports().getReport() == null) {
			return true;
		}
		return false;
	}
	
	public static XMLGregorianCalendar getEventTime(ECReports ecReports) {
		XMLGregorianCalendar eventTime = ecReports.getCreationDate();
		// Example: 2014-08-11T19:57:59.717+09:00
		// SimpleDateFormat sdf = new SimpleDateFormat(
		// "yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
		// eventTime.setTime(sdf.parse(timeString));
		return eventTime;
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

		boolean isMatch = timeZone
				.matches("^(?:Z|[+-](?:2[0-3]|[01][0-9]):[0-5][0-9])$");

		return isMatch;
	}
}
