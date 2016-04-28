package org.oliot.epcis.service.codeparser;

import javax.servlet.ServletContext;

import org.json.JSONObject;
import org.oliot.gcp.core.AICodeParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.ServletContextAware;

/**
 * Copyright (C) 2015 Jaewook Byun
 *
 * @author Jaewook Byun, Ph.D student Korea Advanced Institute of Science and
 *         Technology (KAIST) Real-time Embedded System Laboratory(RESL)
 *         bjw0829@kaist.ac.kr, bjw0829@gmail.com
 */

@Controller
public class CodeParserService implements ServletContextAware {

	@Autowired
	ServletContext servletContext;

	@Override
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	@RequestMapping(value = "/CodeParser/{code}", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<?> getStandardVersion(@PathVariable String code,
			@RequestParam int gcpLength) {
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "application/json; charset=utf-8");
		
		AICodeParser codeParser = new AICodeParser();
		JSONObject result = new JSONObject(codeParser.parse(code, gcpLength));
		
		return new ResponseEntity<>(result.toString(1), responseHeaders, HttpStatus.OK);
	}
}
