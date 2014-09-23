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
import org.oliot.model.epcis.DocumentIdentification;
import org.oliot.model.epcis.EPCISDocumentType;
import org.oliot.model.epcis.StandardBusinessDocumentHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.ServletContextAware;
import org.xml.sax.SAXException;

/**
 * Copyright (C) 2014 KAIST RESL
 *
 * This project is part of Oliot (oliot.org), pursuing the implementation of
 * Electronic Product Code Information Service(EPCIS) v1.1 specification in
 * EPCglobal.
 * [http://www.gs1.org/gsmp/kc/epcglobal/epcis/epcis_1_1-standard-20140520.pdf]
 * 
 *
 * @author Jack Jaewook Byun, Ph.D student
 * 
 *         Korea Advanced Institute of Science and Technology (KAIST)
 * 
 *         Real-time Embedded System Laboratory(RESL)
 * 
 *         bjw0829@kaist.ac.kr
 */
@Controller
@RequestMapping("/eventCapture")
public class EventCapture implements ServletContextAware {

	@Autowired
	ServletContext servletContext;

	@Override
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	@RequestMapping
	public void post(HttpServletRequest request, HttpServletResponse response) {

		try {

			ConfigurationServlet.logger
					.info(" EPCIS Document Capture Started.... ");

			// Get Input Stream
			InputStream is = request.getInputStream();
			if (ConfigurationServlet.isCaptureVerfificationOn == true) {
				String isString = getInputStream(is);

				InputStream validateStream = getXMLDocumentInputStream(isString);
				// Parsing and Validating data
				String xsdPath = servletContext.getRealPath("/wsdl");
				xsdPath += "/EPCglobal-epcis-1_2_jack.xsd";
				boolean isValidated = validate(validateStream, xsdPath);
				if (isValidated == false) {
					// M63
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					return;
				}

				InputStream epcisStream = getXMLDocumentInputStream(isString);
				ConfigurationServlet.logger
						.info(" EPCIS Document : Validated ");
				EPCISDocumentType epcisDocument = JAXB.unmarshal(epcisStream,
						EPCISDocumentType.class);
				
				// M50, M63
				if( epcisDocument.getEPCISHeader() != null )
				{
					if( epcisDocument.getEPCISHeader().getStandardBusinessDocumentHeader() != null )
					{
						StandardBusinessDocumentHeader header = epcisDocument.getEPCISHeader().getStandardBusinessDocumentHeader();
						if( header.getHeaderVersion() == null || !header.getHeaderVersion().equals("1.1"))
						{
							ConfigurationServlet.logger
							.error(" HeaderVersion should 1.1 if use SBDH ");
							response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
							return;
						}
						if( header.getDocumentIdentification() == null )
						{
							ConfigurationServlet.logger
							.error(" DocumentIdentification should exist if use SBDH ");
							response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
							return;
						}else
						{
							DocumentIdentification docID = header.getDocumentIdentification();
							if( docID.getStandard() == null | !docID.getStandard().equals("EPCglobal") )
							{
								ConfigurationServlet.logger
								.error(" DocumentIdentification/Standard should EPCglobal if use SBDH ");
								response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
								return;
							}
							if( docID.getType() == null || (!docID.getType().equals("Events") && !docID.getType().equals("MasterData")) )
							{
								ConfigurationServlet.logger
								.error(" DocumentIdentification/Type should Events|MasterData in Capture Method if use SBDH ");
								response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
								return;
							}
							if( docID.getTypeVersion() == null | !docID.getTypeVersion().equals("1.1"))
							{
								ConfigurationServlet.logger
								.error(" DocumentIdentification/TypeVersion should 1.1 if use SBDH ");
								response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
								return;
							}
							
						}
					}
				}
				
				CaptureService cs = new CaptureService();
				cs.capture(epcisDocument);
				ConfigurationServlet.logger.info(" EPCIS Document : Captured ");
				response.setStatus(HttpServletResponse.SC_ACCEPTED);
			} else {
				EPCISDocumentType epcisDocument = JAXB.unmarshal(is,
						EPCISDocumentType.class);
				CaptureService cs = new CaptureService();
				cs.capture(epcisDocument);
				ConfigurationServlet.logger.info(" EPCIS Document : Captured ");
				response.setStatus(HttpServletResponse.SC_ACCEPTED);
			}

		} catch (IOException e) {
			ConfigurationServlet.logger.log(Level.ERROR, e.toString());
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
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
