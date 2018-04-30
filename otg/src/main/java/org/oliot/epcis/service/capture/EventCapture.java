package org.oliot.epcis.service.capture;

import java.io.InputStream;

import javax.servlet.ServletContext;
import javax.xml.bind.JAXB;

import org.json.JSONObject;
import org.lilliput.chronograph.persistent.ChronoGraph;
import org.oliot.epcis.configuration.Configuration;
import org.oliot.model.epcis.EPCISDocumentType;
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

import com.mongodb.MongoClient;

/**
 * Copyright (C) 2014-2017 Jaewook Byun
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
			@RequestParam(required = false) String accessToken, @RequestParam(required = false) String accessModifier,
			@RequestParam(required = false) Integer gcpLength) {
		JSONObject retMsg = new JSONObject();

		// Facebook Auth.: Access Token Validation
		if (userID != null) {
			ResponseEntity<?> isError = CaptureUtil.checkAccessToken(userID, accessToken, accessModifier);
			if (isError != null)
				return isError;
		}

		Configuration.logger.info(" EPCIS Document Capture Started.... ");

		// XSD based Validation
		if (Configuration.isCaptureVerfificationOn == true) {
			InputStream validateStream = CaptureUtil.getXMLDocumentInputStream(inputString);
			boolean isValidated = CaptureUtil.validate(validateStream,
					Configuration.wsdlPath + "/EPCglobal-epcis-1_2.xsd");
			if (isValidated == false) {
				return new ResponseEntity<>(
						new String("[Error] Input EPCIS Document does not comply the standard schema"),
						HttpStatus.BAD_REQUEST);
			}
			Configuration.logger.info(" EPCIS Document : Validated ");

		}

		InputStream epcisStream = CaptureUtil.getXMLDocumentInputStream(inputString);
		EPCISDocumentType epcisDocument = JAXB.unmarshal(epcisStream, EPCISDocumentType.class);

		if (Configuration.isCaptureVerfificationOn == true) {
			ResponseEntity<?> error = CaptureUtil.checkDocumentHeader(epcisDocument);
			if (error != null)
				return error;
		}

		CaptureService cs = new CaptureService();
		retMsg = cs.capture(epcisDocument, userID, accessModifier, gcpLength);
		Configuration.logger.info(" EPCIS Document : Captured ");

		if (retMsg.isNull("error") == true)
			return new ResponseEntity<>(retMsg.toString(), HttpStatus.OK);
		else
			return new ResponseEntity<>(retMsg.toString(), HttpStatus.BAD_REQUEST);
	}

	public void capture(String inputString) {

		InputStream epcisStream = CaptureUtil.getXMLDocumentInputStream(inputString);
		EPCISDocumentType epcisDocument = JAXB.unmarshal(epcisStream, EPCISDocumentType.class);

		Configuration.backend_ip = "localhost";
		Configuration.backend_port = 27017;
		Configuration.databaseName = "epcis";
		Configuration.mongoClient = new MongoClient(Configuration.backend_ip, Configuration.backend_port);
		Configuration.mongoDatabase = Configuration.mongoClient.getDatabase(Configuration.databaseName);
		Configuration.persistentGraph = new ChronoGraph(Configuration.backend_ip, Configuration.backend_port,
				Configuration.databaseName);
		Configuration.persistentGraphData = new ChronoGraph(Configuration.backend_ip, Configuration.backend_port,
				Configuration.databaseName+"-data");

		CaptureService cs = new CaptureService();
		cs.capture(epcisDocument, null, null, null);

		Configuration.mongoClient.close();
	}

}
