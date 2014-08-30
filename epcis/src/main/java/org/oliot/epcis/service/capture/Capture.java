package org.oliot.epcis.service.capture;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

import javax.servlet.ServletContext;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXB;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Level;
import org.oliot.epcis.configuration.ConfigurationServlet;
import org.oliot.model.epcis.EPCISDocumentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.ServletContextAware;
import org.xml.sax.SAXException;

@Controller
@RequestMapping("/capture")
public class Capture implements ServletContextAware {

	@Autowired
	ServletContext servletContext;

	@Override
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}
	
	@RequestMapping
	public void post(HttpServletRequest request, HttpServletResponse response) {

		try {
			
			ConfigurationServlet.logger.info(" EPCIS Document Capture Started.... ");
			
			// Get ECReport
			InputStream is = request.getInputStream();
			String isString = getInputStream(is);
			
			InputStream validateStream = getXMLDocumentInputStream(isString);
			// Parsing and Validating data
			String xsdPath = servletContext.getRealPath("/wsdl");
			xsdPath += "/EPCglobal-epcis-1_1.xsd";
			boolean isValidated = validate(validateStream, xsdPath);
			if (isValidated == false) {
				return;
			}
			
			InputStream epcisStream = getXMLDocumentInputStream(isString);
			ConfigurationServlet.logger.info(" EPCIS Document : Validated ");
			EPCISDocumentType epcisDocument = JAXB.unmarshal(epcisStream, EPCISDocumentType.class);
			
			CaptureService cs = new CaptureService();
			cs.capture(epcisDocument);
			
		} catch (IOException e) {
			ConfigurationServlet.logger.log(Level.ERROR, e.toString());
		} 
	}

	private static String getInputStream(InputStream is) {
		try {
			StringWriter writer = new StringWriter();
			IOUtils.copy(is, writer, "UTF-8");
			String str = writer.toString();
			return str;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private static InputStream getXMLDocumentInputStream(String xmlString) {
		InputStream stream = new ByteArrayInputStream(
				xmlString.getBytes(StandardCharsets.UTF_8));
		return stream;
	}

	public static String getDataFromInputStream(ServletInputStream is)
			throws IOException {
		StringWriter writer = new StringWriter();
		IOUtils.copy(is, writer, "UTF-8");
		String data = writer.toString();
		return data;
	}
	
	private static boolean validate(InputStream is, String xsdPath) {
		try {
			SchemaFactory schemaFactory = SchemaFactory
					.newInstance("http://www.w3.org/2001/XMLSchema");
			File xsdFile = new File(xsdPath);
			Schema schema = schemaFactory.newSchema(xsdFile);
			Validator validator = schema.newValidator();
			StreamSource xmlSource = new StreamSource(is);
			validator.validate(xmlSource);
			return true;
		} catch (SAXException e) {
			ConfigurationServlet.logger.log(Level.ERROR, e.toString());
			return false;
		} catch (IOException e) {
			ConfigurationServlet.logger.log(Level.ERROR, e.toString());
			return false;
		}
	}
}
