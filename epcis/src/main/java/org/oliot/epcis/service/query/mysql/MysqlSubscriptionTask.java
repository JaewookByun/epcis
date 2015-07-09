package org.oliot.epcis.service.query.mysql;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import javax.xml.bind.JAXB;

import org.apache.log4j.Level;
import org.oliot.epcis.configuration.Configuration;
import org.oliot.model.epcis.EPCISQueryDocumentType;
import org.oliot.model.epcis.ImplementationException;
import org.oliot.model.epcis.QueryResults;
import org.oliot.model.epcis.QueryTooLargeException;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * Copyright (C) 2014 KAIST RESL
 *
 * This project is part of Oliot (oliot.org), pursuing the implementation of
 * Electronic Product Code Information Service(EPCIS) v1.1 specification in
 * EPCglobal.
 * [http://www.gs1.org/gsmp/kc/epcglobal/epcis/epcis_1_1-standard-20140520.pdf]
 * 
 *
 * @author Jack Jaewook Byun, Ph.D student
 * 
 *         Korea Advanced Institute of Science and Technology (KAIST)
 * 
 *         Real-time Embedded System Laboratory(RESL)
 * 
 *         bjw0829@kaist.ac.kr
 */
public class MysqlSubscriptionTask implements Job {
//
	/**
	 * Whenever execute method invoked according to the cron expression Query
	 * the database and send the result to the destination.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		JobDataMap map = context.getJobDetail().getJobDataMap();
		String queryName = map.getString("queryName");
		// String subscriptionID = map.getString("subscriptionID");
		String dest = map.getString("dest");
		// String cronExpression = map.getString("cronExpression");
		String eventType = map.getString("eventType");
		String GE_eventTime = map.getString("GE_eventTime");
		String LT_eventTime = map.getString("LT_eventTime");
		String GE_recordTime = map.getString("GE_recordTime");
		String LT_recordTime = map.getString("LT_recordTime");
		String EQ_action = map.getString("EQ_action");
		String EQ_bizStep = map.getString("EQ_bizStep");
		String EQ_disposition = map.getString("EQ_disposition");
		String EQ_readPoint = map.getString("EQ_readPoint");
		String WD_readPoint = map.getString("WD_readPoint");
		String EQ_bizLocation = map.getString("EQ_bizLocation");
		String WD_bizLocation = map.getString("WD_bizLocation");
		String EQ_transformationID = map.getString("EQ_transformationID");
		String MATCH_epc = map.getString("MATCH_epc");
		String MATCH_parentID = map.getString("MATCH_parentID");
		String MATCH_inputEPC = map.getString("MATCH_inputEPC");
		String MATCH_outputEPC = map.getString("MATCH_outputEPC");
		String MATCH_anyEPC = map.getString("MATCH_anyEPC");
		String MATCH_epcClass = map.getString("MATCH_epcClass");
		String MATCH_inputEPCClass = map.getString("MATCH_inputEPCClass");
		String MATCH_outputEPCClass = map.getString("MATCH_outputEPCClass");
		String MATCH_anyEPCClass = map.getString("MATCH_anyEPCClass");
		String EQ_quantity = map.getString("EQ_quantity");
		String GT_quantity = map.getString("GT_quantity");
		String GE_quantity = map.getString("GE_quantity");
		String LT_quantity = map.getString("LT_quantity");
		String LE_quantity = map.getString("LE_quantity");
		String orderBy = map.getString("orderBy");
		String orderDirection = map.getString("orderDirection");
		String eventCountLimit = map.getString(" eventCountLimit");
		String maxEventCount = map.getString("maxEventCount");
		Map<String, String> paramMap = (Map<String, String>) map
				.get("paramMap");

		MysqlQueryService queryService = new MysqlQueryService();
		String pollResult = queryService.poll(queryName, eventType,
				GE_eventTime, LT_eventTime, GE_recordTime, LT_recordTime,
				EQ_action, EQ_bizStep, EQ_disposition, EQ_readPoint,
				WD_readPoint, EQ_bizLocation, WD_bizLocation,
				EQ_transformationID, MATCH_epc, MATCH_parentID, MATCH_inputEPC,
				MATCH_outputEPC, MATCH_anyEPC, MATCH_epcClass,
				MATCH_inputEPCClass, MATCH_outputEPCClass, MATCH_anyEPCClass,
				EQ_quantity, GT_quantity, GE_quantity, LT_quantity,
				LE_quantity, orderBy, orderDirection, eventCountLimit,
				maxEventCount, null, false, false, null, null, null, null,
				null, paramMap);

		EPCISQueryDocumentType resultXML = JAXB.unmarshal(new StringReader(
				pollResult), EPCISQueryDocumentType.class);

		String resultString = "";

		if (resultXML != null && resultXML.getEPCISBody() != null
				&& resultXML.getEPCISBody().getQueryTooLargeException() != null) {
			QueryTooLargeException e = resultXML.getEPCISBody()
					.getQueryTooLargeException();
			StringWriter sw = new StringWriter();
			JAXB.marshal(e, sw);
			resultString = sw.toString();
		} else if (resultXML != null
				&& resultXML.getEPCISBody() != null
				&& resultXML.getEPCISBody().getImplementationException() != null) {
			ImplementationException e = resultXML.getEPCISBody()
					.getImplementationException();
			StringWriter sw = new StringWriter();
			JAXB.marshal(e, sw);
			resultString = sw.toString();
		} else if (resultXML != null
				&& resultXML.getEPCISBody() != null
				&& resultXML.getEPCISBody().getQueryResults() != null
				&& resultXML.getEPCISBody().getQueryResults().getResultsBody() != null) {
			QueryResults queryResults = new QueryResults();
			queryResults.setQueryName(queryName);
			queryResults.setResultsBody(resultXML.getEPCISBody()
					.getQueryResults().getResultsBody());
			StringWriter sw = new StringWriter();
			JAXB.marshal(queryResults, sw);
			resultString = sw.toString();
		}

		try {
			URL url = new URL(dest);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setRequestProperty("Content-Length",
					"" + Integer.toString(resultString.getBytes().length));
			DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
			wr.writeBytes(resultString);
			wr.flush();
			wr.close();
			int x = conn.getResponseCode();
			System.out.println(x);
			conn.disconnect();
		} catch (MalformedURLException e) {
			Configuration.logger.log(Level.ERROR, e.toString());
		} catch (IOException e) {
			Configuration.logger.log(Level.ERROR, e.toString());
		}

	}
}
