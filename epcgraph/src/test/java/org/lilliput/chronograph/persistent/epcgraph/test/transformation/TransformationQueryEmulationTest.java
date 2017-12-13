package org.lilliput.chronograph.persistent.epcgraph.test.transformation;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bson.BsonArray;
import org.bson.BsonDocument;
import org.bson.BsonInt32;
import org.bson.BsonString;
import org.json.JSONArray;
import org.junit.Test;
import org.lilliput.chronograph.common.Tokens.AC;
import org.lilliput.chronograph.persistent.recipe.PersistentBreadthFirstSearchExternal;
import org.oliot.epcis.service.capture.EventCapture;
import org.oliot.epcis.service.query.EPCTime;
import org.oliot.epcis.service.query.TimeUtil;
import org.springframework.http.HttpHeaders;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

public class TransformationQueryEmulationTest {
	public static String fileBaseLoc = "/home/jack/test/";
	// db.edges.createIndex({"_outV" : 1, "_t" : 1, "_inV" : 1})
	// db.EventData.createIndex({"inputEPCList.epc":1})

	public static String baseURL = "http://localhost:8080/epcgraph";
	public int transferCount = 10000;
	public int iterationCount = 100;

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
		db.getCollection("EventData").createIndex(
				new BsonDocument("eventTime", new BsonInt32(1)).append("inputEPCList.epc", new BsonInt32(1)));

		client.close();

		for (int i = 0; i < transferCount; i++) {

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
			bw.write(i + "\t" + avg + "\n");
			bw.flush();
		}
		bw.close();
	}

	@SuppressWarnings("unused")
	public double doTransformationQuery() throws IOException {

		ArrayList<Long> timeList = new ArrayList<Long>();

		String source = "urn:epc:id:sgtin:0000001.000001.0";
		String startTime = "2000-12-09T16:17:05.765Z";

		for (int i = 0; i < iterationCount; i++) {
			long pre = System.currentTimeMillis();
			JSONArray arr = getTransformationTreeEmulation(source, startTime);
			long aft = System.currentTimeMillis();
			long elapsedTime = aft - pre;
			// System.out.println(arr.toString(2));
			// System.out.println("Elapsed Time: " + elapsedTime);
			timeList.add(elapsedTime);
		}

		double total = timeList.parallelStream().mapToDouble(t -> {
			return t.longValue();
		}).sum();
		return total / iterationCount;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private JSONArray getTransformationTreeEmulation(String epc, String startTime) {
		// 여기에 에뮬레이션을 함

		// Time processing
		long startTimeMil = 0;
		startTimeMil = TimeUtil.getTimeMil(startTime);

		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "application/json; charset=utf-8");

		BsonArray transforms = new BsonArray();
		transforms.add(new BsonString("transformsTo"));

		PersistentBreadthFirstSearchExternal tBFS = new PersistentBreadthFirstSearchExternal();
		Map pathMap = new HashMap();
		pathMap = tBFS.compute(epc, startTimeMil, AC.$gte);

		// JSONarray contains each path
		// contains time - vertex mapping

		JSONArray pathArray = new JSONArray();

		Iterator<Set> pathSetIter = pathMap.values().iterator();
		while (pathSetIter.hasNext()) {
			Set pathSet = pathSetIter.next();
			Iterator<List> pathIter = pathSet.iterator();
			while (pathIter.hasNext()) {
				List path = pathIter.next();
				Iterator<EPCTime> vi = path.iterator();
				JSONArray p = new JSONArray();
				while (vi.hasNext()) {
					Object ve = vi.next();
					if (ve != null)
						p.put(ve.toString());
				}
				pathArray.put(p);
			}
		}
		return pathArray;
	}
}
