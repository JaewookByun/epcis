package org.lilliput.chronograph.persistent.epcgraph.test.aggregation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.bson.BsonDocument;
import org.bson.BsonInt32;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.lilliput.chronograph.common.LongInterval;
import org.oliot.epcis.service.capture.EventCapture;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

public class AggregationQueryEmulationTest {
	public static String fileBaseLoc = "/home/jack/test/";
	// db.edges.createIndex({"_outV" : 1, "_t" : 1, "_inV" : 1})
	// db.EventData.createIndex({"inputEPCList.epc":1})

	public static HashMap<HashSet<String>, ArrayList<Long>> ppp = null;

	public int transferCount = 200;
	public int iterationCount = 100;

	@Test
	public void test()
			throws IOException, InterruptedException, ParserConfigurationException, SAXException, ParseException {

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

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		String cTime = sdf.format(new Date());

		long lastTimeMil = System.currentTimeMillis() + 100000000;

		for (int i = 0; i < transferCount; i++) {

			Thread.sleep(1000);

			cTime = sdf.format(new Date());
			String epcParent = String.format("%010d", i + 1);
			String epcChild = String.format("%010d", i);

			// urn:epc:id:sscc:0000002.0000000001

			String top = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" + "<!DOCTYPE project>\n"
					+ "<epcis:EPCISDocument schemaVersion=\"1.2\"\n"
					+ "	creationDate=\"2013-06-04T14:59:02.099+02:00\" xmlns:epcis=\"urn:epcglobal:epcis:xsd:1\"\n"
					+ "	xmlns:example=\"http://ns.example.com/epcis\">\n" + "	<EPCISBody>\n" + "		<EventList>";

			String bottom = "</EventList>\n" + "	</EPCISBody>\n" + "</epcis:EPCISDocument>";

			String body = "<AggregationEvent>\n" + "				<eventTime>" + cTime + "</eventTime>\n"
					+ "				<eventTimeZoneOffset>+00:00</eventTimeZoneOffset>\n"
					+ "				<parentID>urn:epc:id:sscc:0000001." + epcParent + "</parentID>\n"
					+ "				<childEPCs>\n" + "					<epc>urn:epc:id:sscc:0000001." + epcChild
					+ "</epc>\n" + "				</childEPCs>\n" + "				<action>ADD</action>\n"
					+ "				<bizStep>urn:epcglobal:cbv:bizstep:loading</bizStep>\n"
					+ "				<!-- TNT Liverpool depot -->\n" + "				<bizLocation>\n"
					+ "					<id>urn:epc:id:sgln:0000001.00002.1</id>\n" + "				</bizLocation>\n"
					+ "			</AggregationEvent>";

			String lastTime = sdf.format(new Date(lastTimeMil - i));

			body += "<AggregationEvent>\n" + "				<eventTime>" + lastTime + "</eventTime>\n"
					+ "				<eventTimeZoneOffset>+00:00</eventTimeZoneOffset>\n"
					+ "				<parentID>urn:epc:id:sscc:0000001." + epcParent + "</parentID>\n"
					+ "				<childEPCs>\n" + "					<epc>urn:epc:id:sscc:0000001." + epcChild
					+ "</epc>\n" + "				</childEPCs>\n" + "				<action>DELETE</action>\n"
					+ "				<bizStep>urn:epcglobal:cbv:bizstep:loading</bizStep>\n"
					+ "				<!-- TNT Liverpool depot -->\n" + "				<bizLocation>\n"
					+ "					<id>urn:epc:id:sgln:0000001.00002.1</id>\n" + "				</bizLocation>\n"
					+ "			</AggregationEvent>";

			EventCapture cap = new EventCapture();
			cap.capture(top + body + bottom);

			Thread.sleep(1000);

			double avg = doTransformationQuery();

			System.out.println(i + "\t" + avg);
			bw.write(i + "\t" + avg + "\n");
			bw.flush();
		}
		bw.close();
	}

	public double doTransformationQuery()
			throws IOException, ParserConfigurationException, SAXException, ParseException {

		ArrayList<Long> timeList = new ArrayList<Long>();

		String source = "urn:epc:id:sscc:0000001.0000000000";

		for (int i = 0; i < iterationCount; i++) {

			String url = "http://localhost:8080/epcgraph/Service/Poll/SimpleEventQuery?eventType=AggregationEvent&orderBy=eventTime&orderDirection=ASC&";
			url += "MATCH_epc=" + source;

			URL captureURL = new URL(url);
			ppp = new HashMap<HashSet<String>, ArrayList<Long>>();

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

		return total / iterationCount;
	}

	/**
	 * 
	 * @param address
	 * @param port
	 * @param remainingURL
	 *            start with /
	 * @param message
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws ParseException
	 */
	@SuppressWarnings("unused")
	private void sendPost(URL captureURL, byte[] bytes)
			throws IOException, ParserConfigurationException, SAXException, ParseException {

		// 각 container에 대해
		// action = ADD 인 처음 시간
		// action = DELETE 인 마지막 시간
		// 을 구간 단위로 만듬

		// container - [range list]

		// parentID
		// eventTime
		// action

		HttpURLConnection con = (HttpURLConnection) captureURL.openConnection();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

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

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(new ByteArrayInputStream(response.toString().getBytes()));

		// HashMap<String, TreeMap<Long, EdgeEvent>> tNeighbors =
		// v.getTNeighbors(Direction.OUT, "isPossessed",
		// startTimeMil, AC.$gte);

		// dest , 시간 , ADD or DELETE

		JSONObject timeNeighbors = new JSONObject();

		NodeList objectEvents = doc.getElementsByTagName("AggregationEvent");
		// parent, long, action
		HashMap<String, TreeMap<Long, String>> managing = new HashMap<String, TreeMap<Long, String>>();
		for (int i = 0; i < objectEvents.getLength(); i++) {
			// for each event
			Node objectEvent = objectEvents.item(i);
			NodeList objectElements = objectEvent.getChildNodes();
			long eventTimeMil = 0;
			String parentID = null;
			String action = null;
			for (int j = 0; j < objectElements.getLength(); j++) {
				Node element = objectElements.item(j);
				String nodeName = element.getNodeName();
				if (nodeName.equals("eventTime")) {
					String eventTime = element.getTextContent();
					eventTimeMil = sdf.parse(eventTime).getTime();
				}
				if (nodeName.equals("parentID")) {
					parentID = element.getTextContent();
				}
				if (nodeName.equals("action")) {
					action = element.getTextContent();
				}
			}

			if (managing.containsKey(parentID)) {
				TreeMap<Long, String> timestampAction = managing.get(parentID);
				timestampAction.put(eventTimeMil, action);
				managing.put(parentID, timestampAction);
			} else {
				TreeMap<Long, String> timestampAction = new TreeMap<Long, String>();
				timestampAction.put(eventTimeMil, action);
				managing.put(parentID, timestampAction);
			}
		}

		// {urn:epc:id:sscc:0000001.0000000001={1513138621448=ADD,
		// 1513238620448=DELETE}}

		Iterator<Entry<String, TreeMap<Long, String>>> manIter = managing.entrySet().iterator();
		while (manIter.hasNext()) {
			Entry<String, TreeMap<Long, String>> man = manIter.next();
			String parentID = man.getKey();
			TreeMap<Long, String> timestampAction = man.getValue();

			HashSet<LongInterval> rangeSet = new HashSet<LongInterval>();

			Iterator<Entry<Long, String>> taIter = timestampAction.entrySet().iterator();
			Long temp = null;
			while (taIter.hasNext()) {
				Entry<Long, String> ta = taIter.next();
				Long t = ta.getKey();
				String action = ta.getValue();

				if (temp == null && action.equals("ADD")) {
					temp = t;
					continue;
				}

				if (temp != null && action.equals("DELETE")) {
					rangeSet.add(new LongInterval(temp, t));
					temp = null;
					continue;
				}
			}

			// 각각의 시간 range에 대해 parentID를 child로 갖는 또 다른 호출을 함
			Iterator<LongInterval> rangeIter = rangeSet.iterator();
			while (rangeIter.hasNext()) {
				LongInterval range = rangeIter.next();

				HashSet<String> p = new HashSet<String>();
				p.add("urn:epc:id:sscc:0000001.0000000000");
				p.add(parentID);

				if (ppp.containsKey(p)) {
					ArrayList<Long> pp = ppp.get(p);
					pp.add(range.getStart());
					pp.add(range.getEnd());
					ppp.put(p, pp);
				} else {
					ArrayList<Long> pp = new ArrayList<Long>();
					pp.add(range.getStart());
					pp.add(range.getEnd());
					ppp.put(p, pp);
				}

				recursiveAggregation(parentID, range);
			}
		}

		// JSONObject: source-dest : [intervals]
		JSONObject ret = new JSONObject();

		Iterator<Entry<HashSet<String>, ArrayList<Long>>> iter2 = ppp.entrySet().iterator();
		while (iter2.hasNext()) {
			Entry<HashSet<String>, ArrayList<Long>> entry = iter2.next();
			String key = entry.getKey().toString();
			ArrayList<Long> intvArr = entry.getValue();
			Long start = null;
			Iterator<Long> iter3 = intvArr.iterator();
			JSONArray intvJsonArr = new JSONArray();
			while (iter3.hasNext()) {
				Long temp = iter3.next();
				if (start == null) {
					start = temp;
					continue;
				} else {
					intvJsonArr.put(start + "-" + temp);
					start = null;
					continue;
				}
			}
			ret.put(key, intvJsonArr);
		}

		// System.out.println(managing);
		// System.out.println(ret.toString(2));
		// System.out.println(timeNeighbors.toString(2));
	}

	@SuppressWarnings("unused")
	private void recursiveAggregation(String child, LongInterval range)
			throws IOException, ParserConfigurationException, SAXException, ParseException {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		String startTime = sdf.format(range.getStart());
		String endTime = sdf.format(range.getEnd());

		String url = "http://localhost:8080/epcgraph/Service/Poll/SimpleEventQuery?eventType=AggregationEvent&orderBy=eventTime&orderDirection=ASC&";
		url += "MATCH_epc=" + child + "&GE_eventTime=" + startTime + "&LT_eventTime" + endTime;

		URL captureURL = new URL(url);

		// optional default is GET
		HttpURLConnection con = (HttpURLConnection) captureURL.openConnection();
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

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(new ByteArrayInputStream(response.toString().getBytes()));

		// HashMap<String, TreeMap<Long, EdgeEvent>> tNeighbors =
		// v.getTNeighbors(Direction.OUT, "isPossessed",
		// startTimeMil, AC.$gte);

		// dest , 시간 , ADD or DELETE

		JSONObject timeNeighbors = new JSONObject();

		NodeList objectEvents = doc.getElementsByTagName("AggregationEvent");
		// parent, long, action
		HashMap<String, TreeMap<Long, String>> managing = new HashMap<String, TreeMap<Long, String>>();
		for (int i = 0; i < objectEvents.getLength(); i++) {
			// for each event
			Node objectEvent = objectEvents.item(i);
			NodeList objectElements = objectEvent.getChildNodes();
			long eventTimeMil = 0;
			String parentID = null;
			String action = null;
			for (int j = 0; j < objectElements.getLength(); j++) {
				Node element = objectElements.item(j);
				String nodeName = element.getNodeName();
				if (nodeName.equals("eventTime")) {
					String eventTime = element.getTextContent();
					eventTimeMil = sdf.parse(eventTime).getTime();
				}
				if (nodeName.equals("parentID")) {
					parentID = element.getTextContent();
				}
				if (nodeName.equals("action")) {
					action = element.getTextContent();
				}
			}

			if (managing.containsKey(parentID)) {
				TreeMap<Long, String> timestampAction = managing.get(parentID);
				timestampAction.put(eventTimeMil, action);
				managing.put(parentID, timestampAction);
			} else {
				TreeMap<Long, String> timestampAction = new TreeMap<Long, String>();
				timestampAction.put(eventTimeMil, action);
				managing.put(parentID, timestampAction);
			}
		}

		Iterator<Entry<String, TreeMap<Long, String>>> manIter = managing.entrySet().iterator();
		while (manIter.hasNext()) {
			Entry<String, TreeMap<Long, String>> man = manIter.next();
			String parentID = man.getKey();
			TreeMap<Long, String> timestampAction = man.getValue();

			HashSet<LongInterval> rangeSet = new HashSet<LongInterval>();

			Iterator<Entry<Long, String>> taIter = timestampAction.entrySet().iterator();
			Long temp = null;
			while (taIter.hasNext()) {
				Entry<Long, String> ta = taIter.next();
				Long t = ta.getKey();
				String action = ta.getValue();

				if (temp == null && action.equals("ADD")) {
					temp = t;
					continue;
				}

				if (temp != null && action.equals("DELETE")) {
					rangeSet.add(new LongInterval(temp, t));
					temp = null;
					continue;
				}
			}
			// 각각의 시간 range에 대해 parentID를 child로 갖는 또 다른 호출을 함
			Iterator<LongInterval> rangeIter = rangeSet.iterator();
			while (rangeIter.hasNext()) {
				LongInterval range2 = rangeIter.next();

				HashSet<String> p = new HashSet<String>();
				p.add(child);
				p.add(parentID);

				if (ppp.containsKey(p)) {
					ArrayList<Long> pp = ppp.get(p);
					pp.add(range2.getStart());
					pp.add(range2.getEnd());
					ppp.put(p, pp);
				} else {
					ArrayList<Long> pp = new ArrayList<Long>();
					pp.add(range2.getStart());
					pp.add(range2.getEnd());
					ppp.put(p, pp);
				}

				recursiveAggregation(parentID, range2);
			}
		}
		// System.out.println(managing);
	}
}
