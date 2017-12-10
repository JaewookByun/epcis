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

import org.bson.BsonDocument;
import org.bson.BsonInt32;
import org.json.JSONArray;
import org.junit.Test;
import org.oliot.epcis.service.capture.EventCapture;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

public class TransformationQueryTest {
	public static String fileBaseLoc = "/home/jack/test/";
	// db.edges.createIndex({"_outV" : 1, "_t" : 1, "_inV" : 1})
	// db.EventData.createIndex({"inputEPCList.epc":1})

	@Test
	public void test() throws IOException {

		File file = new File(fileBaseLoc + this.getClass().getSimpleName() + "-cache-bfs");
		file.createNewFile();
		FileWriter fw = new FileWriter(file);
		BufferedWriter bw = new BufferedWriter(fw);

		MongoClient client = new MongoClient();
		MongoDatabase db = client.getDatabase("epcis");
		db.getCollection("EventData").drop();
		db.getCollection("edges").drop();
		db.getCollection("vertices").drop();
		db.getCollection("edges").createIndex(new BsonDocument("_outV", new BsonInt32(1)).append("_t", new BsonInt32(1))
				.append("_inV", new BsonInt32(1)));

		client.close();

		for (int i = 0; i < 10000; i++) {

			// Insert Event
			String top = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" + "<!DOCTYPE project>\n"
					+ "<epcis:EPCISDocument schemaVersion=\"1.2\"\n"
					+ "	creationDate=\"2013-06-04T14:59:02.099+02:00\" xmlns:epcis=\"urn:epcglobal:epcis:xsd:1\"\n"
					+ "	xmlns:example=\"http://ns.example.com/epcis\">\n" + "	<EPCISBody>\n" + "		<EventList>";

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
					+ "					</outputEPCList>\n" + "				</TransformationEvent>\n"
					+ "			</extension>";
			EventCapture cap = new EventCapture();
			cap.capture(top + body + bottom);

			double avg = doTransformationQuery();

			System.out.println(i + "\t" + avg);
			bw.write(i+"\t"+avg+"\n");
			bw.flush();
		}
		bw.close();
	}

	public double doTransformationQuery() throws IOException {

		ArrayList<Long> timeList = new ArrayList<Long>();

		String source = "urn:epc:id:sgtin:0000001.000001.0";
		String startTime = "2000-01-01T00:00:00";

		int loopCount = 100;

		for (int i = 0; i < loopCount; i++) {
			String url = "http://localhost:8080/epcis/Service/Transform?startTime=" + startTime + "&epc=" + source
					+ "&order=forward";
			URL captureURL = new URL(url);
			long pre = System.currentTimeMillis();
			sendPost(captureURL, null);
			long aft = System.currentTimeMillis();
			long elapsedTime = aft - pre;
			// System.out.println("Elapsed Time: " + elapsedTime);
			timeList.add(elapsedTime);
		}

		double total = timeList.parallelStream().mapToDouble(t -> {
			return t.longValue();
		}).sum();
		// System.out.println(total / loopCount);

		// 10: 37.61
		// 100: 56.11
		// 1000: 270.62
		// 10000:
		return total / loopCount;
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
	private void sendPost(URL captureURL, byte[] bytes) throws IOException {
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
		JSONArray arr = new JSONArray(response.toString());
		// System.out.println(arr.toString(2));
	}
}
