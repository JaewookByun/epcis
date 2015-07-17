package org.oliot.epcis.service.capture;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

import javax.servlet.ServletContext;
import javax.servlet.ServletInputStream;
import javax.xml.bind.JAXB;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Level;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.oliot.epcis.configuration.Configuration;
import org.oliot.epcis.service.capture.mongodb.MongoCaptureUtil;
import org.oliot.model.epcis.DocumentIdentification;
import org.oliot.model.epcis.EPCISDocumentType;
import org.oliot.model.epcis.StandardBusinessDocumentHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.ServletContextAware;
import org.xml.sax.SAXException;

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
@RequestMapping("/JsonEventCapture")
public class JsonEventCapture implements ServletContextAware {

	@Autowired
	ServletContext servletContext;

	@Override
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public String post(@RequestBody String inputString) {
		Configuration.logger.info(" EPCIS Json Document Capture Started.... ");

		Configuration.isCaptureVerfificationOn = false;
		if (Configuration.isCaptureVerfificationOn == true) {

			InputStream validateStream = getXMLDocumentInputStream(inputString);
			// Parsing and Validating data
			String xsdPath = servletContext.getRealPath("/wsdl");
			xsdPath += "/EPCglobal-epcis-1_2_jack.xsd";
			boolean isValidated = validate(validateStream, xsdPath);
			if (isValidated == false) {
				// M63
				return "Error M63";
			}

			InputStream epcisStream = getXMLDocumentInputStream(inputString);
			Configuration.logger.info(" EPCIS Document : Validated ");
			EPCISDocumentType epcisDocument = JAXB.unmarshal(epcisStream,
					EPCISDocumentType.class);

			// M50, M63
			if (epcisDocument.getEPCISHeader() != null) {
				if (epcisDocument.getEPCISHeader()
						.getStandardBusinessDocumentHeader() != null) {
					StandardBusinessDocumentHeader header = epcisDocument
							.getEPCISHeader()
							.getStandardBusinessDocumentHeader();
					if (header.getHeaderVersion() == null
							|| !header.getHeaderVersion().equals("1.1")) {
						Configuration.logger
								.error(" HeaderVersion should 1.1 if use SBDH ");
						return "Error: HeaderVersion should 1.1 if use SBDH";
					}
					if (header.getDocumentIdentification() == null) {
						Configuration.logger
								.error(" DocumentIdentification should exist if use SBDH ");
						return "Error: DocumentIdentification should exist if use SBDH";
					} else {
						DocumentIdentification docID = header
								.getDocumentIdentification();
						if (docID.getStandard() == null
								| !docID.getStandard().equals("EPCglobal")) {
							Configuration.logger
									.error(" DocumentIdentification/Standard should EPCglobal if use SBDH ");
							return "Error: DocumentIdentification/Standard should EPCglobal if use SBDH";
						}
						if (docID.getType() == null
								|| (!docID.getType().equals("Events") && !docID
										.getType().equals("MasterData"))) {
							Configuration.logger
									.error(" DocumentIdentification/Type should Events|MasterData in Capture Method if use SBDH ");
							return "Error: DocumentIdentification/Type should Events|MasterData in Capture Method if use SBDH";
						}
						if (docID.getTypeVersion() == null
								| !docID.getTypeVersion().equals("1.1")) {
							Configuration.logger
									.error(" DocumentIdentification/TypeVersion should 1.1 if use SBDH ");
							return "Error: DocumentIdentification/TypeVersion should 1.1 if use SBDH";
						}

					}
				}
			}

			CaptureService cs = new CaptureService();
			cs.capture(epcisDocument);
			Configuration.logger.info(" EPCIS Document : Captured ");
		} else {

			try { 
				JSONObject json = new JSONObject(inputString);
				JSONObject json2 = json.getJSONObject("epcis");
				JSONObject json3 = json2.getJSONObject("EPCISBody");
				JSONArray json4 = json3.getJSONArray("EventList");
				
				
				for(int i = 0 ; i < json4.length(); i++){
					
					if(json4.getJSONObject(i).has("ObjectEvent") == true){
						
						//validation logic about ObjectEvent
						
						if (Configuration.backend.equals("MongoDB")) {
							MongoCaptureUtil m = new MongoCaptureUtil();
							m.objectevent_capture(json4.getJSONObject(i).getJSONObject("ObjectEvent"));
						}
					}
					else if(json4.getJSONObject(i).has("AggregationEvent") == true){
						
						//validation logic about AggregationEvent
						
						if (Configuration.backend.equals("MongoDB")) {
							MongoCaptureUtil m = new MongoCaptureUtil();
							m.aggregationevent_capture(json4.getJSONObject(i).getJSONObject("AggregationEvent"));
						}
					}
					else if(json4.getJSONObject(i).has("TransformationEvent") == true){
						
						//validation logic about TransformationEvent
						
						if (Configuration.backend.equals("MongoDB")) {
							MongoCaptureUtil m = new MongoCaptureUtil();
							m.transformationevent_capture(json4.getJSONObject(i).getJSONObject("TransformationEvent"));
						}
					}
					else if(json4.getJSONObject(i).has("TransactionEvent") == true){
						
						//validation logic about TransactionEvent
						
						if (Configuration.backend.equals("MongoDB")) {
							MongoCaptureUtil m = new MongoCaptureUtil();
							m.transactionevent_capture(json4.getJSONObject(i).getJSONObject("TransactionEvent"));
						}
					}
					else{
						Configuration.logger.info(" Json Document is not valid1 ");
					}
					
				}
				if(json4.length() !=0)
					Configuration.logger.info(" EPCIS Document : Captured ");
				
	        } catch(JSONException e) {
	        	Configuration.logger.info(" Json Document is not valid2 ");
	        }
		}
		return "EPCIS Document : Captured ";
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
			Configuration.logger.log(Level.ERROR, e.toString());
			return false;
		} catch (IOException e) {
			Configuration.logger.log(Level.ERROR, e.toString());
			return false;
		}
	}

}
