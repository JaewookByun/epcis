package org.oliot.epcis.service.capture.secured;

import java.io.InputStream;
import javax.servlet.ServletContext;
import javax.xml.bind.JAXB;

import org.json.JSONObject;
import org.oliot.epcis.configuration.Configuration;
import org.oliot.epcis.service.capture.CaptureService;
import org.oliot.epcis.service.capture.CaptureUtil;
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

import redis.clients.jedis.Jedis;

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

@Controller
@RequestMapping("/SecureEventCapture")
public class SecureEventCapture implements ServletContextAware {

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
	public ResponseEntity<?> post(@RequestBody String inputString, @RequestParam(required = true) String userID,
			@RequestParam(required = true) String accessToken, @RequestParam(required = false) Integer gcpLength,
			@RequestParam(required = false) String accessModifier) {
		JSONObject retMsg = new JSONObject();

		// =============================================================================================
		/* jaeheeHa1 AC_capture service (check repository) */

		// Access Token Validation
		if (accessToken == null) {
			return new ResponseEntity<>(new String("put accessToken for CaptureService"), HttpStatus.BAD_REQUEST);
		}

		// Checking subscribe authorization

		// If there is no subscription right
		// pop up this . return new ResponseEntity<>("No accessRight",
		// HttpStatus.BAD_REQUEST);

		/* this is query example for querying ac_api */

		// check userID and accessToken is in caching

		boolean pass = false;

		// (Yalew Cache) 11. if pass the cache.

		Jedis RedisCL = Configuration.jedisClient;

		String result = RedisCL.get(userID + "-furnish");

		if (result == null || !(result.equals(accessToken))) {
			// add to cache..
			// url of ac_api server
			String quri = "http://" + Configuration.ac_api_address + "/user/" + userID + "/epcis/"
					+ Configuration.epcis_id + "/furnish";

			// query to ac_api server
			String qurlParameters = "";
			String query_result = Configuration.query_access_relation(quri, accessToken, qurlParameters);

			// for debug, erase after implementing.
			Configuration.logger.info(query_result);
			query_result = query_result.replaceAll("[\"{} ]", "").split(":")[1];

			pass = (query_result.equals("yes")) ? true : false;
			/* end of example for querying ac_api */
			if (pass) {
				RedisCL.set(userID + "-furnish", accessToken);
			}

			// =============================================================================================
		} else {
			pass = true;
		}

		if (pass) {

			Configuration.logger.info(" EPCIS Document Capture Started.... ");

			System.out.println(inputString);
			// XSD based Validation
			if (Configuration.isCaptureVerfificationOn == true) {
				InputStream validateStream = CaptureUtil.getXMLDocumentInputStream(inputString);
				boolean isValidated = CaptureUtil.validate(validateStream,
						Configuration.wsdlPath + "/EPCglobal-epcis-1_2.xsd");
				if (isValidated == false) {
					// M63
					return new ResponseEntity<>(new String("Error M63"), HttpStatus.BAD_REQUEST);
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

			String accessModifierString;
			if (accessModifier != null) {
				accessModifierString = accessModifier;
			} else {
				accessModifierString = "friend";
			}

			CaptureService cs = new CaptureService();
			retMsg = cs.capture(epcisDocument, userID, accessModifierString, gcpLength);
			Configuration.logger.info(" EPCIS Document : Captured ");
		} else {
			retMsg.put("Authorized", "no");
		}

		if (retMsg.isNull("error") == true && pass)
			return new ResponseEntity<>(retMsg.toString(), HttpStatus.OK);
		else
			return new ResponseEntity<>(retMsg.toString(), HttpStatus.BAD_REQUEST);
	}
}
