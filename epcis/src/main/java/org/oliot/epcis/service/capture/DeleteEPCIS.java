package org.oliot.epcis.service.capture;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.Random;

import javax.servlet.ServletContext;
import javax.xml.bind.JAXB;

import org.json.JSONObject;
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
@RequestMapping("/DeleteEPCIS")
public class DeleteEPCIS implements ServletContextAware {

	@Autowired
	ServletContext servletContext;

	@Override
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	public ResponseEntity<?> asyncPost(String inputString) {
		ResponseEntity<?> result = post(inputString, null, null, null);
		return result;
	}

	@RequestMapping(method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<?> post(@RequestBody String inputString, 
			@RequestParam(required = true) String userID,
			@RequestParam(required = true) String accessToken,
			@RequestParam(required = false) Integer gcpLength) {
		JSONObject retMsg = new JSONObject();

//=============================================================================================
		/* jaeheeHa3 AC_delete repository */
		
		Configuration.logger.info(" EPCIS Repository : Deleted ");

		if (retMsg.isNull("error") == true)
			return new ResponseEntity<>(retMsg.toString(), HttpStatus.OK);
		else
			return new ResponseEntity<>(retMsg.toString(), HttpStatus.BAD_REQUEST);
	}
	
	/**
	 * del
	 * @creator Jaehee Ha 
	 * lovesm135@kaist.ac.kr
	 * created
	 * 2016/11/05
	 * @param inputString
	 * @param gcpLength
	 * @return ResponseEntity<?>
	 */
	@RequestMapping(method = RequestMethod.DELETE)
	@ResponseBody
	public ResponseEntity<?> del(@RequestBody String inputString, @RequestParam(required = false) Integer gcpLength) {
		String errorMessage = null;

		//Implement RBAC Access control for Capture Service Here.
		//String EPCISName = "";
		Configuration.logger.info(" EPCIS Drop Started.... ");

		Configuration.dropMongoDB();
		
		Configuration.logger.info(" EPCIS : Dropped ");

		if( errorMessage == null )
		{
			return new ResponseEntity<>(new String("{\"EPCIS Document\" : \"Dropped\"} "), HttpStatus.OK);
		}
		else
			return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
	}
	
	
	public String query_access_relation(String quri, String qtoken, String qurlParameters){
		Configuration.logger.info(" Client Token retrieve");
		StringBuffer response = null;
		String result = null;
		
		try {
		String url = quri; //"http://143.248.55.139:3001/oauth/token";
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		//add reuqest header
		con.setRequestMethod("POST");
		con.setRequestProperty("Authorization", "Bearer "+qtoken);
		con.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
		

		String urlParameters = qurlParameters;

		// Send post request
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(urlParameters);
		wr.flush();
		wr.close();

		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'POST' request to URL : " + url);
		System.out.println("Post parameters : " + urlParameters);
		System.out.println("Response Code : " + responseCode);

		BufferedReader in;
			in = new BufferedReader(
			        new InputStreamReader(con.getInputStream()));
		
		String inputLine;
		response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//print result
		
		if(response!=null){
			result=response.toString();
		}
		
		return result;
	}

}
