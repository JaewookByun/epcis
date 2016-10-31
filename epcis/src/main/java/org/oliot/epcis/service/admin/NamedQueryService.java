package org.oliot.epcis.service.admin;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Level;
import org.oliot.epcis.configuration.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
public class NamedQueryService implements ServletContextAware {

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
	 * Provide existing Named Event Queries
	 */
	@RequestMapping(value = "/Admin/NamedEventQuery", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<?> getNamedEventQuery() {
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "text/html; charset=utf-8");
		
		Configuration.logger.log(Level.INFO, " Success ");
		return new ResponseEntity<>(new String("GET NamedEventQuery"), responseHeaders, HttpStatus.OK);
	}
	
	/**
	 * Provide existing Named Event Queries
	 */
	@RequestMapping(value = "/Admin/NamedEventQuery", method = RequestMethod.PUT)
	@ResponseBody
	public ResponseEntity<?> putNamedEventQuery() {
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "text/html; charset=utf-8");
		
		Configuration.logger.log(Level.INFO, " Success ");
		return new ResponseEntity<>(new String("PUT NamedEventQuery"), responseHeaders, HttpStatus.OK);
	}
	
	/**
	 * Provide existing Named Event Queries
	 */
	@RequestMapping(value = "/Admin/NamedEventQuery", method = RequestMethod.DELETE)
	@ResponseBody
	public ResponseEntity<?> deleteNamedEventQuery() {
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "text/html; charset=utf-8");
		
		Configuration.logger.log(Level.INFO, " Success ");
		return new ResponseEntity<>(new String("DELETE NamedEventQuery"), responseHeaders, HttpStatus.OK);
	}
}
