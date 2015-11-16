package org.oliot.epcis.service.query;

import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.oliot.epcis.configuration.Configuration;
import org.oliot.epcis.security.OAuthUtil;
import org.oliot.epcis.service.query.mongodb.MongoQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.ServletContextAware;

import com.restfb.FacebookClient;

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
public class SecuredQueryService implements ServletContextAware {

	@Autowired
	ServletContext servletContext;

	@SuppressWarnings("unused")
	@Autowired
	private HttpServletRequest request;

	@SuppressWarnings("unused")
	@Autowired
	private HttpServletResponse response;

	@Override
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	@RequestMapping(value = "/SecuredPoll/{queryName}", method = RequestMethod.GET)
	@ResponseBody
	public String poll(@PathVariable String queryName, @RequestParam(required = false) String eventType,
			@RequestParam(required = false) String GE_eventTime, @RequestParam(required = false) String LT_eventTime,
			@RequestParam(required = false) String GE_recordTime, @RequestParam(required = false) String LT_recordTime,
			@RequestParam(required = false) String EQ_action, @RequestParam(required = false) String EQ_bizStep,
			@RequestParam(required = false) String EQ_disposition, @RequestParam(required = false) String EQ_readPoint,
			@RequestParam(required = false) String WD_readPoint, @RequestParam(required = false) String EQ_bizLocation,
			@RequestParam(required = false) String WD_bizLocation,
			@RequestParam(required = false) String EQ_transformationID,
			@RequestParam(required = false) String MATCH_epc, @RequestParam(required = false) String MATCH_parentID,
			@RequestParam(required = false) String MATCH_inputEPC,
			@RequestParam(required = false) String MATCH_outputEPC, @RequestParam(required = false) String MATCH_anyEPC,
			@RequestParam(required = false) String MATCH_epcClass,
			@RequestParam(required = false) String MATCH_inputEPCClass,
			@RequestParam(required = false) String MATCH_outputEPCClass,
			@RequestParam(required = false) String MATCH_anyEPCClass,
			@RequestParam(required = false) String EQ_quantity, @RequestParam(required = false) String GT_quantity,
			@RequestParam(required = false) String GE_quantity, @RequestParam(required = false) String LT_quantity,
			@RequestParam(required = false) String LE_quantity, @RequestParam(required = false) String orderBy,
			@RequestParam(required = false) String orderDirection,
			@RequestParam(required = false) String eventCountLimit,
			@RequestParam(required = false) String maxEventCount,

	@RequestParam(required = false) String vocabularyName, @RequestParam(required = false) boolean includeAttributes,
			@RequestParam(required = false) boolean includeChildren,
			@RequestParam(required = false) String attributeNames, @RequestParam(required = false) String EQ_name,
			@RequestParam(required = false) String WD_name, @RequestParam(required = false) String HASATTR,
			@RequestParam(required = false) String maxElementCount,

	@RequestParam(required = false) String format, @RequestParam(required = false) String fid, @RequestParam(required = false) String accessToken,
			@RequestParam Map<String, String> params) {

		FacebookClient fc = null;
		
		if (fid != null){
			fc = OAuthUtil.isValidatedFacebookClient(accessToken, fid);
		}
		
		if (Configuration.backend.equals("MongoDB")) {
			MongoQueryService mongoQueryService = new MongoQueryService();
			return mongoQueryService.securedPoll(queryName, eventType, GE_eventTime, LT_eventTime, GE_recordTime,
					LT_recordTime, EQ_action, EQ_bizStep, EQ_disposition, EQ_readPoint, WD_readPoint, EQ_bizLocation,
					WD_bizLocation, EQ_transformationID, MATCH_epc, MATCH_parentID, MATCH_inputEPC, MATCH_outputEPC,
					MATCH_anyEPC, MATCH_epcClass, MATCH_inputEPCClass, MATCH_outputEPCClass, MATCH_anyEPCClass,
					EQ_quantity, GT_quantity, GE_quantity, LT_quantity, LE_quantity, orderBy, orderDirection,
					eventCountLimit, maxEventCount, vocabularyName, includeAttributes, includeChildren, attributeNames,
					EQ_name, WD_name, HASATTR, maxElementCount, format, fc, fid, params);
		} else if (Configuration.backend.equals("Cassandra")) {
			return null;
		} else if (Configuration.backend.equals("MySQL")) {
			return null;
		}

		return null;
	}
}
