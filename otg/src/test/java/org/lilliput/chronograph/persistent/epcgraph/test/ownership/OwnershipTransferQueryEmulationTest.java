package org.lilliput.chronograph.persistent.epcgraph.test.ownership;

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
import java.util.TreeMap;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.bson.BsonDocument;
import org.bson.BsonInt32;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.oliot.epcis.service.capture.EventCapture;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

public class OwnershipTransferQueryEmulationTest {
	public static String fileBaseLoc = "/home/jack/test/";
	// db.edges.createIndex({"_outV" : 1, "_t" : 1, "_inV" : 1})
	// db.EventData.createIndex({"inputEPCList.epc":1})

	public int transferCount = 1000;
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

			Thread.sleep(1000);

			cTime = sdf.format(new Date());

			body += "<ObjectEvent>\n" + "	<eventTime>" + cTime + "</eventTime>\n"
					+ "	<eventTimeZoneOffset>+00:00</eventTimeZoneOffset>\n" + "	<epcList>\n"
					+ "		<epc>urn:epc:id:sgtin:0000001.000001.0</epc>\n" + "	</epcList>\n"
					+ "	<action>OBSERVE</action>\n" + "	<bizStep>urn:epcglobal:cbv:bizstep:receiving</bizStep>\n"
					+ "	<bizLocation>\n" + "		<id>urn:epc:id:sgln:0000001.00002.1</id>\n" + "	</bizLocation>\n"
					+ "	<extension>\n" + "		<sourceList>\n"
					+ "			<source type=\"urn:epcglobal:cbv:sdt:possessing_party\">urn:epc:id:sgln:0000001.00001."
					+ i + "</source>\n" + "		</sourceList>\n" + "		<destinationList>\n"
					+ "			<destination type=\"urn:epcglobal:cbv:sdt:possessing_party\">urn:epc:id:sgln:0000001.00001."
					+ (i + 1) + "</destination>\n" + "		</destinationList>\n" + "	</extension>\n"
					+ "</ObjectEvent>";

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

		String source = "urn:epc:id:sgtin:0000001.000001.0";

		for (int i = 0; i < iterationCount; i++) {

			String url = "http://localhost:8080/epcgis/Service/Poll/SimpleEventQuery?";
			url += "MATCH_epc=" + source;

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
		HashMap<String, TreeMap<Long, String>> tNeighbors = new HashMap<String, TreeMap<Long, String>>();

		NodeList objectEvents = doc.getElementsByTagName("ObjectEvent");
		for (int i = 0; i < objectEvents.getLength(); i++) {
			Node objectEvent = objectEvents.item(i);
			NodeList objectElements = objectEvent.getChildNodes();
			long eventTimeMil = 0;
			HashSet<String> sourceSet = new HashSet<String>();
			HashSet<String> destSet = new HashSet<String>();
			for (int j = 0; j < objectElements.getLength(); j++) {
				Node element = objectElements.item(j);
				String nodeName = element.getNodeName();
				if (nodeName.equals("eventTime")) {
					String eventTime = element.getTextContent();
					eventTimeMil = sdf.parse(eventTime).getTime();
				}
				if (nodeName.equals("extension")) {
					NodeList extensionElements = element.getChildNodes();
					for (int k = 0; k < extensionElements.getLength(); k++) {
						Node extensionElement = extensionElements.item(k);
						String extensionElementName = extensionElement.getNodeName();
						if (extensionElementName.equals("sourceList")) {
							NodeList sourceList = extensionElement.getChildNodes();
							for (int l = 0; l < sourceList.getLength(); l++) {
								Node source = sourceList.item(l);
								if (source instanceof Element) {
									if (source.getAttributes().getNamedItem("type").getTextContent()
											.equals("urn:epcglobal:cbv:sdt:possessing_party")) {
										String sourceStr = source.getTextContent();
										sourceSet.add(sourceStr);
									}
								}
							}
						}
						if (extensionElementName.equals("destinationList")) {
							NodeList destList = extensionElement.getChildNodes();
							for (int l = 0; l < destList.getLength(); l++) {
								Node dest = destList.item(l);
								if (dest instanceof Element) {
									if (dest.getAttributes().getNamedItem("type").getTextContent()
											.equals("urn:epcglobal:cbv:sdt:possessing_party")) {
										String destStr = dest.getTextContent();
										destSet.add(destStr);
									}
								}
							}
						}
					}
				}
			}
			Iterator<String> sourceIter = sourceSet.iterator();
			while (sourceIter.hasNext()) {
				String source = sourceIter.next();
				if (tNeighbors.containsKey(source)) {
					TreeMap<Long, String> sourceValue = tNeighbors.get(source);
					sourceValue.put(eventTimeMil, "DELETE");
					tNeighbors.put(source, sourceValue);
				} else {
					TreeMap<Long, String> sourceValue = new TreeMap<Long, String>();
					sourceValue.put(eventTimeMil, "DELETE");
					tNeighbors.put(source, sourceValue);
				}
			}
			Iterator<String> destIter = destSet.iterator();
			while (destIter.hasNext()) {
				String dest = destIter.next();
				if (tNeighbors.containsKey(dest)) {
					TreeMap<Long, String> destValue = tNeighbors.get(dest);
					destValue.put(eventTimeMil, "ADD");
					tNeighbors.put(dest, destValue);
				} else {
					TreeMap<Long, String> destValue = new TreeMap<Long, String>();
					destValue.put(eventTimeMil, "ADD");
					tNeighbors.put(dest, destValue);
				}
			}
		}

		// outV : [ "s-e", "s-e" ];
		JSONObject retObj = new JSONObject();
		Iterator<Entry<String, TreeMap<Long, String>>> iterator = tNeighbors.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, TreeMap<Long, String>> elem = iterator.next();
			String neighbor = elem.getKey();
			TreeMap<Long, String> valueMap = elem.getValue();
			Iterator<Entry<Long, String>> valueIter = valueMap.entrySet().iterator();

			Long start = null;
			JSONArray ranges = new JSONArray();
			while (valueIter.hasNext()) {
				Entry<Long, String> valueElem = valueIter.next();
				Long time = valueElem.getKey();
				String action = valueElem.getValue();
				if (action.equals("ADD")) {
					if (start == null)
						start = time;
				} else if (action.equals("DELETE")) {
					if (start != null) {
						ranges.put(start + "-" + time);
						start = null;
					}
				}
			}
			retObj.put(neighbor, ranges);
		}

		System.out.println(retObj.toString(2));
	}
}
