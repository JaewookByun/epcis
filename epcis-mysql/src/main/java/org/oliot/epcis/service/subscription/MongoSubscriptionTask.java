package org.oliot.epcis.service.subscription;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.bind.JAXB;

import org.apache.log4j.Level;
import org.bson.BsonDocument;
import org.bson.BsonString;
import org.oliot.epcis.configuration.Configuration;
import org.oliot.epcis.service.query.mongodb.MongoQueryService;
import org.oliot.model.epcis.EPCISQueryDocumentType;
import org.oliot.model.epcis.ImplementationException;
import org.oliot.model.epcis.QueryParameterException;
import org.oliot.model.epcis.QueryResults;
import org.oliot.model.epcis.QueryTooLargeException;
import org.oliot.model.epcis.SubscriptionType;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;

import com.mongodb.client.MongoCollection;

/**
 * Copyright (C) 2014-2016 Jaewook Byun
 *
 * This project is part of Oliot (oliot.org), pursuing the implementation of
 * Electronic Product Code Information Service(EPCIS) v1.1 specification in
 * EPCglobal.
 * [http://www.gs1.org/gsmp/kc/epcglobal/epcis/epcis_1_1-standard-20140520.pdf]
 * 
 *
 * @author Jaewook Byun, Ph.D student
 * 
 *         Korea Advanced Institute of Science and Technology (KAIST)
 * 
 *         Real-time Embedded System Laboratory(RESL)
 * 
 *         bjw0829@kaist.ac.kr, bjw0829@gmail.com
 */

public class MongoSubscriptionTask implements Job {

	/**
	 * Whenever execute method invoked according to the cron expression Query
	 * the database and send the result to the destination.
	 */
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		JobDataMap map = context.getJobDetail().getJobDataMap();
		BsonDocument jobData = (BsonDocument) map.get("jobData");
		SubscriptionType s = new SubscriptionType(jobData);

		// InitialRecordTime limits recordTime
		if (s.getInitialRecordTime() != null) {
			try {
				s.getPollParameters().setGE_recordTime(s.getInitialRecordTime());
				GregorianCalendar cal = new GregorianCalendar();
				Date curTime = cal.getTime();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
				s.setInitialRecordTime(sdf.format(curTime));
				updateInitialRecordTime(s.getSubscriptionID(), s.getInitialRecordTime());
				JobDetail detail = context.getJobDetail();
				detail.getJobDataMap().put("jobData", SubscriptionType.asBsonDocument(s));
				MongoSubscription.sched.addJob(detail, true, true);
			} catch (SchedulerException e) {
				e.printStackTrace();
			}
		}
		MongoQueryService queryService = new MongoQueryService();

		try {
			String pollResult;
			pollResult = queryService.poll(s.getPollParameters(), null, null, s.getSubscriptionID());
			String resultString = "";

			if (s.getPollParameters().getFormat() == null || s.getPollParameters().getFormat().equals("XML")) {
				EPCISQueryDocumentType resultXML = JAXB.unmarshal(new StringReader(pollResult),
						EPCISQueryDocumentType.class);

				if (resultXML != null && resultXML.getEPCISBody() != null
						&& resultXML.getEPCISBody().getQueryTooLargeException() != null) {
					QueryTooLargeException e = resultXML.getEPCISBody().getQueryTooLargeException();
					StringWriter sw = new StringWriter();
					JAXB.marshal(e, sw);
					resultString = sw.toString();
				} else if (resultXML != null && resultXML.getEPCISBody() != null
						&& resultXML.getEPCISBody().getImplementationException() != null) {
					ImplementationException e = resultXML.getEPCISBody().getImplementationException();
					StringWriter sw = new StringWriter();
					JAXB.marshal(e, sw);
					resultString = sw.toString();
				} else if (resultXML != null && resultXML.getEPCISBody() != null
						&& resultXML.getEPCISBody().getQueryResults() != null
						&& resultXML.getEPCISBody().getQueryResults().getResultsBody() != null) {

					List<Object> checkList = resultXML.getEPCISBody().getQueryResults().getResultsBody().getEventList()
							.getObjectEventOrAggregationEventOrQuantityEvent();

					if (s.getReportIfEmpty() == false) {
						if (checkList == null || checkList.size() == 0) {
							// Do not report if reportIfEmpty is true
							return;
						}
					}

					QueryResults queryResults = new QueryResults();
					queryResults.setQueryName(s.getPollParameters().getQueryName());
					queryResults.setResultsBody(resultXML.getEPCISBody().getQueryResults().getResultsBody());
					queryResults.setSubscriptionID(s.getSubscriptionID());
					StringWriter sw = new StringWriter();
					JAXB.marshal(queryResults, sw);
					resultString = sw.toString();
				}
			} else {
				resultString = pollResult;
			}
			URL url = new URL(s.getDest());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setRequestProperty("Content-Length", "" + Integer.toString(resultString.getBytes().length));
			conn.setRequestProperty("Content-Type", "application/xml; charset=utf-8");
			DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
			wr.writeBytes(resultString);
			wr.flush();
			wr.close();
			int x = conn.getResponseCode();
			System.out.println(x);
			conn.disconnect();
		} catch (QueryParameterException e1) {
			Configuration.logger.log(Level.ERROR, e1.toString());
		}catch (MalformedURLException e) {
			Configuration.logger.log(Level.ERROR, e.toString());
		} catch (IOException e) {
			Configuration.logger.log(Level.ERROR, e.toString());
		} catch (QueryTooLargeException e1) {
			Configuration.logger.log(Level.ERROR, e1.toString());
		}
	}

	private void updateInitialRecordTime(String subscriptionID, String initialRecordTime) {

		MongoCollection<BsonDocument> collection = Configuration.mongoDatabase.getCollection("Subscription",
				BsonDocument.class);

		BsonDocument subscription = collection.find(new BsonDocument("subscriptionID", new BsonString(subscriptionID)))
				.first();
		subscription.put("initialRecordTime", new BsonString(initialRecordTime));

		if (subscription != null) {
			collection.findOneAndReplace(new BsonDocument("subscriptionID", new BsonString(subscriptionID)),
					subscription);
		}
		Configuration.logger.log(Level.INFO,
				"InitialRecordTime of Subscription ID: " + subscriptionID + " is updated to DB. ");
	}
}
