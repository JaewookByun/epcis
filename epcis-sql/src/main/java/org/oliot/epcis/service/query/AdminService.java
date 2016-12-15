package org.oliot.epcis.service.query;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Level;
import org.oliot.epcis.configuration.Configuration;
import org.oliot.epcis.security.OAuthUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.ServletContextAware;

import com.restfb.Connection;
import com.restfb.FacebookClient;
import com.restfb.types.User;

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

@Controller
public class AdminService implements ServletContextAware {

	@Autowired
	ServletContext servletContext;

	@SuppressWarnings("unused")
	@Autowired
	private HttpServletRequest request;

	@Override
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;

	}

	/**
	 * Removes a previously registered subscription having the specified
	 * subscriptionID.
	 */
	@RequestMapping(value = "/Admin/ResetDB", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<?> resetDB(@RequestParam String userID, @RequestParam String accessToken) {
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "text/html; charset=utf-8");

		// Access Control is not mandatory
		// However, if fid and accessToken provided, more information provided
		FacebookClient fc = null;
		List<String> friendList = null;
		if (userID != null) {
			// Check accessToken
			fc = OAuthUtil.isValidatedFacebookClient(accessToken, userID);
			if (fc == null) {
				return new ResponseEntity<>(new String("Unauthorized Token"), responseHeaders, HttpStatus.UNAUTHORIZED);
			}
			friendList = new ArrayList<String>();

			Connection<User> friendConnection = fc.fetchConnection("me/friends", User.class);
			for (User friend : friendConnection.getData()) {
				friendList.add(friend.getId());
			}
		}

		// OAuth Fails
		if (!OAuthUtil.isAdministratable(userID, friendList)) {
			Configuration.logger.log(Level.INFO, " No right to administration ");
			return new ResponseEntity<>(new String("No right to administration"), responseHeaders,
					HttpStatus.BAD_REQUEST);

		}
//		if (Configuration.mongoDatabase.getCollection("EventData") != null) {
//			Configuration.mongoDatabase.getCollection("EventData").drop();
//		}
//		if (Configuration.mongoDatabase.getCollection("MasterData") != null) {
//			Configuration.mongoDatabase.getCollection("MasterData").drop();
//		}
		Configuration.logger.log(Level.INFO, " Repository Initialized ");
		return new ResponseEntity<>(new String("All Event/Master Data removed"), responseHeaders, HttpStatus.OK);
	}
}
