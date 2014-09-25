package org.oliot.epcis.service.capture;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import javax.servlet.ServletInputStream;
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
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.core.io.ClassPathResource;

import org.xml.sax.SAXException;

public class CaptureMQListener implements MessageListener{

	@Override
	public void onMessage(Message message) {

		try {
			byte[] bytes = message.getBody();
			String xmlString = new String(bytes, "UTF-8");
			
			ConfigurationServlet.logger
			.info(" EPCIS Document Capture Started.... ");
			if (ConfigurationServlet.isCaptureVerfificationOn == true) {
				
				String isString = xmlString;

				InputStream validateStream = getXMLDocumentInputStream(isString);
				// Parsing and Validating data
				String xsdPath = "wsdl/EPCglobal-epcis-1_2_jack.xsd";
				boolean isValidated = validate(validateStream, xsdPath);
				if (isValidated == false) {
					// M63
					ConfigurationServlet.logger
					.error(" Non Validated XML Input ");
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
							return;
						}
						if( header.getDocumentIdentification() == null )
						{
							ConfigurationServlet.logger
							.error(" DocumentIdentification should exist if use SBDH ");
							return;
						}else
						{
							DocumentIdentification docID = header.getDocumentIdentification();
							if( docID.getStandard() == null | !docID.getStandard().equals("EPCglobal") )
							{
								ConfigurationServlet.logger
								.error(" DocumentIdentification/Standard should EPCglobal if use SBDH ");
								return;
							}
							if( docID.getType() == null || (!docID.getType().equals("Events") && !docID.getType().equals("MasterData")) )
							{
								ConfigurationServlet.logger
								.error(" DocumentIdentification/Type should Events|MasterData in Capture Method if use SBDH ");
								return;
							}
							if( docID.getTypeVersion() == null | !docID.getTypeVersion().equals("1.1"))
							{
								ConfigurationServlet.logger
								.error(" DocumentIdentification/TypeVersion should 1.1 if use SBDH ");
								return;
							}
							
						}
					}
				}
				
				CaptureService cs = new CaptureService();
				cs.capture(epcisDocument);
				ConfigurationServlet.logger.info(" EPCIS Document : Captured ");
			} else {
				EPCISDocumentType epcisDocument = JAXB.unmarshal(xmlString,
						EPCISDocumentType.class);
				CaptureService cs = new CaptureService();
				cs.capture(epcisDocument);
				ConfigurationServlet.logger.info(" EPCIS Document : Captured ");
			}
			
			
		} catch (UnsupportedEncodingException e) {
			ConfigurationServlet.logger.error(e.toString());
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
			
			ClassPathResource cpr = new ClassPathResource(xsdPath);
			File xsdFile = cpr.getFile();
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
