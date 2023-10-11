package org.oliot.epcis.query;

import static org.oliot.epcis.validation.HeaderValidator.*;

import java.util.UUID;

import org.oliot.epcis.capture.json.JSONMessageFactory;
import org.oliot.epcis.model.EPCISException;
import org.oliot.epcis.model.QueryParameterException;
import org.oliot.epcis.server.EPCISServer;
import org.oliot.epcis.util.HTTPUtil;

import io.vertx.ext.web.Router;

/**
 * Copyright (C) 2020-2023. (Jaewook Byun) all rights reserved.
 * <p>
 * This project is an open source implementation of Electronic Product Code
 * Information Service (EPCIS) v2.0,
 * <p>
 * SOAPQueryServiceHandler holds routers for Query Interface
 * <p>
 *
 * @author Jaewook Byun, Ph.D., Assistant Professor, Sejong University,
 *         jwbyun@sejong.ac.kr, Associate Director, Auto-ID Labs, Korea,
 *         bjw0829@gmail.com
 */
public class RESTQueryServiceHandler {

	/*
	 * 
	 * eventType GE_eventTime LT_eventTime GE_recordTime LT_recordTime EQ_action
	 * EQ_bizStep EQ_disposition EQ_persistentDisposition_set
	 * EQ_persistentDisposition_unset EQ_readPoint WD_readPoint EQ_bizLocation
	 * WD_bizLocation EQ_transformationID MATCH_epc MATCH_parentID MATCH_inputEPC
	 * MATCH_outputEPC MATCH_anyEPC MATCH_epcClass MATCH_inputEPCClass
	 * MATCH_outputEPCClass MATCH_anyEPCClass EQ_quantity GT_quantity GE_quantity
	 * LT_quantity LE_quantity EQ_eventID EXISTS_errorDeclaration
	 * GE_errorDeclarationTime LT_errorDeclarationTime EQ_errorReason
	 * EQ_correctiveEventID orderBy orderDirection eventCountLimit maxEventCount
	 * GE_startTime LT_startTime GE_endTime LT_endTime EQ_type EQ_deviceID
	 * EQ_dataProcessingMethod EQ_microorganism EQ_chemicalSubstance EQ_bizRules
	 * EQ_stringValue EQ_hexBinaryValue EQ_uriValue EQ_booleanValue
	 */

	public static void registerGetEventsHandler(Router router, RESTQueryService restQueryService) {
		/*
		 * NextPageToken PerPage GS1-CBV-Min GS1-CBV-Max GS1-EPCIS-Min GS1-EPCIS-Max
		 * GS1-EPC-Format GS1-CBV-XML-Format
		 */
		router.get("/epcis/events").consumes("application/json").handler(routingContext -> {
			if (!checkEPCISMinMaxVersion(routingContext))
				return;
			if (!isEqualHeaderREST(routingContext, "GS1-EPC-Format"))
				return;
			if (!isEqualHeaderREST(routingContext, "GS1-CBV-XML-Format"))
				return;
			routingContext.response().setChunked(true);

			// get UUID
			String nextPageToken = routingContext.request().getParam("NextPageToken");
			if (nextPageToken == null) {
				restQueryService.query(routingContext);
			} else {
				UUID uuid = null;
				try {
					uuid = UUID.fromString(routingContext.request().getParam("NextPageToken"));
				} catch (Exception e) {
					QueryParameterException e1 = new QueryParameterException("invalid nextPageToken - " + uuid);
					HTTPUtil.sendQueryResults(routingContext.response(),
							JSONMessageFactory.get406NotAcceptableException(
									"[406NotAcceptable] The server cannot return the response as requested: "
											+ e1.getMessage()),
							406);
					return;
				}
				restQueryService.getNextEventPage(routingContext, uuid);
			}
		});
		EPCISServer.logger.info("[GET /epcis/evetns (application/json)] - router added");
	}

}
