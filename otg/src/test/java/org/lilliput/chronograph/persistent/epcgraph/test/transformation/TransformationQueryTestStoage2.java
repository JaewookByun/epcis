package org.lilliput.chronograph.persistent.epcgraph.test.transformation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

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

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.bson.Document;
import org.oliot.epcis.service.capture.EventCapture;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

public class TransformationQueryTestStoage2 {
	public static String fileBaseLoc = "/home/jack/test/";
	// db.edges.createIndex({"_outV" : 1, "_t" : 1, "_inV" : 1})
	// db.EventData.createIndex({"inputEPCList.epc":1})

	public static String baseURL = "http://localhost:8080/otg";
	public int transferCount = 108;
	public int iterationCount = 1;

	public void test() throws IOException, InterruptedException {

		File file = new File(fileBaseLoc + this.getClass().getSimpleName() + "-cache-bfs");
		file.createNewFile();
		FileWriter fw = new FileWriter(file);
		BufferedWriter bw = new BufferedWriter(fw);

		MongoClient client = new MongoClient();
		MongoDatabase db = client.getDatabase("epcis");
		db.getCollection("EventData").drop();
		db.getCollection("edges").drop();
		db.getCollection("vertices").drop();
		db.getCollection("tEdgeEvents").drop();
		db.getCollection("tVertexEvents").drop();
		client.close();

		for (int i = 0; i < transferCount; i++) {

			// Insert Event
			String top = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" + "<!DOCTYPE project>\n"
					+ "<epcis:EPCISDocument schemaVersion=\"1.2\"\n"
					+ "	creationDate=\"2013-06-04T14:59:02.099+02:00\" xmlns:epcis=\"urn:epcglobal:epcis:xsd:1\"\n"
					+ "xmlns:example0=\"http://ns.example.com/epcis0\" xmlns:example1=\"http://ns.example.com/epcis1\"\n"
					+ "	xmlns:example2=\"http://ns.example.com/epcis2\" xmlns:example3=\"http://ns.example.com/epcis3\"\n"
					+ "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:example=\"http://ns.example.com/epcis\">\n"
					+ "	<EPCISBody>\n" + "		<EventList>";

			String bottom = "</EventList>\n" + "	</EPCISBody>\n" + "</epcis:EPCISDocument>";

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			String cTime = sdf.format(new Date());

			String body = "";

			cTime = sdf.format(new Date());
			body += "<extension>\n" + "				<TransformationEvent>\n" + "					<eventTime>" + cTime
					+ "</eventTime>\n" + "					<eventTimeZoneOffset>+00:00</eventTimeZoneOffset>\n"
					+ "					<inputEPCList>\n"
					+ "						<epc>urn:epc:id:sgtin:0000001.000001." + i + "</epc>\n"
					+ "					</inputEPCList>\n" + "					<outputEPCList>\n"
					+ "						<epc>urn:epc:id:sgtin:0000001.000001." + (2 * i + 1) + "</epc>\n"
					+ "						<epc>urn:epc:id:sgtin:0000001.000001." + (2 * i + 2) + "</epc>\n"
					+ "					</outputEPCList>\n" + "<transformationID>TransformationID</transformationID>\n"
					+ "					<bizStep>urn:epcglobal:cbv:bizstep:transforming</bizStep>\n"
					+ "					<disposition>urn:epcglobal:cbv:disp:in_progress</disposition>"
					+ "<example0:a xsi:type=\"xsd:int\">15</example0:a>\n" + "					<example0:b>\n"
					+ "						<example1:c xsi:type=\"xsd:double\">20.5</example1:c>\n"
					+ "					</example0:b>\n" + "					<example0:h>\n"
					+ "						<example1:d xsi:type=\"xsd:boolean\">true</example1:d>\n"
					+ "						<example1:e>\n"
					+ "							<example2:f xsi:type=\"xsd:dateTime\">2013-06-08T14:58:56.591Z</example2:f>\n"
					+ "						</example1:e>\n"
					+ "						<example1:g xsi:type=\"xsd:long\">50</example1:g>\n"
					+ "					</example0:h>" + "				</TransformationEvent>\n"
					+ "			</extension>";
			EventCapture cap = new EventCapture();
			cap.capture(top + body + bottom);

			client = new MongoClient();
			db = client.getDatabase("epcis");
			
			Document ed = db.runCommand(new Document("collStats", "EventData"));
			Document gd = db.runCommand(new Document("collStats", "tEdgeEvents"));
			
			int event = ed.getInteger("size");
			int graph = gd.getInteger("size");
			client.close();

			System.out.println(i + "\t" + event + "\t" + graph);
			bw.write(i + "\t" + event + "\t" + graph + "\n");
			bw.flush();
		}
		bw.close();
	}

	@SuppressWarnings("unused")
	public int doTransformationQuery() throws IOException {

		ArrayList<Long> timeList = new ArrayList<Long>();

		String source = "urn:epc:id:sgtin:0000001.000001.0";
		String startTime = "2000-01-01T00:00:00";

		// for (int i = 0; i < iterationCount; i++) {
		// String url = baseURL + "/Service/Transform?startTime=" + startTime + "&epc="
		// + source + "&order=forward";
		String url = baseURL + "/Service/TraceabilityQuery?startTime=" + startTime + "&traceEPC=" + source
				+ "&traceTarget=transformation&orderDirection=ASC";
		URL captureURL = new URL(url);
		long pre = System.currentTimeMillis();
		int length1 = sendPost(captureURL, null);
		long aft = System.currentTimeMillis();
		long elapsedTime = aft - pre;
		// System.out.println("Elapsed Time: " + elapsedTime);
		timeList.add(elapsedTime);
		// }

		double total = timeList.parallelStream().mapToDouble(t -> {
			return t.longValue();
		}).sum();

		// return total / iterationCount;
		return length1;
	}

	/**
	 * 
	 * @param address
	 * @param port
	 * @param remainingURL
	 *            start with /
	 * @param message
	 * @throws IOException
	 */
	@SuppressWarnings("unused")
	private int sendPost(URL captureURL, byte[] bytes) throws IOException {
		HttpURLConnection con = (HttpURLConnection) captureURL.openConnection();

		// optional default is GET
		con.setRequestMethod("GET");

		int responseCode = con.getResponseCode();
		// System.out.println("\nSending 'GET' request to URL : " +
		// captureURL.toString());
		// System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		// print result
		// JSONArray arr = new JSONArray(response.toString());
		// System.out.println(arr.toString(2));
		int length = response.toString().getBytes().length;
		return length;
	}
}
