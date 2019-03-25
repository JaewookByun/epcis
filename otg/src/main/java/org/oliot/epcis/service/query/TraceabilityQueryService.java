package org.oliot.epcis.service.query;

import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.script.ScriptEngine;
import javax.script.ScriptException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.bson.BsonArray;
import org.bson.BsonDocument;
import org.bson.BsonDouble;
import org.bson.BsonString;
import org.bson.BsonValue;
import org.json.JSONArray;
import org.json.JSONObject;
import org.oliot.epcis.configuration.Configuration;
import org.oliot.epcis.service.query.mongodb.MongoQueryService;
import org.oliot.khronos.cache.CachedChronoVertex;
import org.oliot.khronos.common.LoopPipeFunction;
import org.oliot.khronos.common.TemporalType;
import org.oliot.khronos.common.Tokens.AC;
import org.oliot.khronos.common.Tokens.Position;
import org.oliot.khronos.persistent.ChronoEdge;
import org.oliot.khronos.persistent.ChronoGraph;
import org.oliot.khronos.persistent.ChronoVertex;
import org.oliot.khronos.persistent.VertexEvent;
import org.oliot.khronos.persistent.engine.TraversalEngine;
import org.oliot.khronos.persistent.recipe.PersistentBreadthFirstSearch;
import org.oliot.khronos.persistent.recipe.PersistentBreadthFirstSearchEmulation;
import org.oliot.model.epcis.PollParameters;
import org.oliot.model.epcis.QueryParameterException;
import org.oliot.model.epcis.QueryTooLargeException;
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
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.VertexQuery;
import com.tinkerpop.pipes.PipeFunction;
import com.tinkerpop.pipes.util.structures.Tree;

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

@Controller
public class TraceabilityQueryService implements ServletContextAware {

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

	public Document createBaseQueryResults(String traceEPC, String traceTarget, String startTime, String endTime,
			String orderDirection) {
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.newDocument();
			Element queryResults = doc.createElement("QueryResults");
			doc.appendChild(queryResults);
			Element queryName = doc.createElement("queryName");
			queryName.setTextContent("TraceabilityQuery");
			Element querySpec = doc.createElement("querySpec");
			Element traceEPCXML = doc.createElement("traceEPC");
			traceEPCXML.setTextContent(traceEPC);
			Element traceTargetXML = doc.createElement("traceTarget");
			traceTargetXML.setTextContent(traceTarget);
			Element startTimeXML = doc.createElement("startEventTime");
			startTimeXML.setTextContent(startTime);
			Element endTimeXML = doc.createElement("endEventTime");
			endTimeXML.setTextContent(endTime);
			Element orderDirectionXML = doc.createElement("orderDirection");
			orderDirectionXML.setTextContent(orderDirection);
			querySpec.appendChild(traceEPCXML);
			querySpec.appendChild(traceTargetXML);
			querySpec.appendChild(startTimeXML);
			querySpec.appendChild(endTimeXML);
			querySpec.appendChild(orderDirectionXML);
			queryResults.appendChild(querySpec);

			return doc;

		} catch (ParserConfigurationException e) {
			return null;
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public String getTransformation(String traceEPC, String traceTarget, String startTime, String endTime,
			Long fromTimeMil, Long toTimeMil, String orderDirection) {
		BsonArray transforms = new BsonArray();
		transforms.add(new BsonString("transformsTo"));

		ChronoGraph g = Configuration.persistentGraph;

		PersistentBreadthFirstSearch tBFS = new PersistentBreadthFirstSearch();
		Map pathMap = new HashMap();
		VertexEvent source;
		if (orderDirection.equals("ASC"))
			source = g.getChronoVertex(traceEPC).setTimestamp(fromTimeMil);
		else
			source = g.getChronoVertex(traceEPC).setTimestamp(toTimeMil);
		pathMap = tBFS.compute(g, source, "transformsTo", orderDirection);

		Document doc = createBaseQueryResults(traceEPC, traceTarget, startTime, endTime, orderDirection);

		Element transformationTrace = doc.createElement("transformationTrace");
		Iterator<Set> pathSetIter = pathMap.values().iterator();
		while (pathSetIter.hasNext()) {
			Set pathSet = pathSetIter.next();
			Iterator<List> pathIter = pathSet.iterator();

			while (pathIter.hasNext()) {
				List path = pathIter.next();
				Iterator<VertexEvent> vi = path.iterator();
				Element transformationPath = doc.createElement("transformationPath");
				while (vi.hasNext()) {
					Object ve = vi.next();
					if (ve != null) {
						Element pathElement = doc.createElement("pathElement");
						VertexEvent veObj = (VertexEvent) ve;
						Element eventTime = doc.createElement("eventTime");
						Date date = new Date(veObj.getTimestamp());
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
						String dateString = sdf.format(date);
						eventTime.setTextContent(dateString);
						Element epc = doc.createElement("epc");
						epc.setTextContent(veObj.getVertexID());
						pathElement.appendChild(epc);
						pathElement.appendChild(eventTime);
						transformationPath.appendChild(pathElement);
					}
				}
				transformationTrace.appendChild(transformationPath);
			}
		}

		Element resultsBody = doc.createElement("resultsBody");
		resultsBody.appendChild(transformationTrace);
		doc.getFirstChild().appendChild(resultsBody);

		return toString(doc);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public String getAggregation(String traceEPC, String traceTarget, String startTime, String endTime,
			Long fromTimeMil, Long toTimeMil, String orderDirection) {
		ChronoGraph g = Configuration.persistentGraph;
		VertexEvent ve = g.getChronoVertex(traceEPC).setTimestamp(fromTimeMil);

		BsonArray contains = new BsonArray();
		contains.add(new BsonString("contains"));

		LoopPipeFunction loop = new LoopPipeFunction() {

			@Override
			public boolean compute(Object argument, Map<Object, Object> currentPath, int loopCount) {
				if (argument == null)
					return false;
				else
					return true;
			}
		};

		PipeFunction<VertexEvent, VertexEvent> transform = new PipeFunction<VertexEvent, VertexEvent>() {

			@Override
			public VertexEvent compute(VertexEvent argument) {

				Set<VertexEvent> veSet = argument.getBothVertexEventSet("contains", AC.$gt);
				if (veSet == null || veSet.isEmpty() || veSet.size() == 0)
					return null;

				if (veSet.size() == 1)
					return veSet.iterator().next();

				Iterator<VertexEvent> iter = veSet.iterator();
				VertexEvent v1 = null;
				VertexEvent v2 = null;
				if (iter.hasNext())
					v1 = iter.next();
				if (iter.hasNext())
					v2 = iter.next();

				if (v1 != null && v2 != null) {
					if (v1.getTimestamp() < v2.getTimestamp())
						return v1;
					else
						return v2;
				}
				return null;

			}
		};

		TraversalEngine engine = new TraversalEngine(g, ve, false, true, VertexEvent.class);
		engine.as("s");
		engine.transform(transform, VertexEvent.class);
		engine.loop("s", loop).toList();

		ArrayList<VertexEvent> refinedPath = new ArrayList<VertexEvent>();
		HashMap<HashSet<ChronoVertex>, ArrayList<Long>> ppp = new HashMap<HashSet<ChronoVertex>, ArrayList<Long>>();

		Map path = engine.path();

		Iterator iter = path.values().iterator();
		while (iter.hasNext()) {
			HashSet next = (HashSet) iter.next();
			Iterator<ArrayList> pathIter = next.iterator();
			while (pathIter.hasNext()) {
				ArrayList eachPath = pathIter.next();
				Iterator elemIter = eachPath.iterator();
				while (elemIter.hasNext()) {
					Object elem = elemIter.next();
					if (elem instanceof VertexEvent) {
						VertexEvent veElem = (VertexEvent) elem;
						refinedPath.add(veElem);
					}
				}
			}
		}

		for (int i = 0; i < refinedPath.size() - 1; i++) {
			VertexEvent source = refinedPath.get(i);
			VertexEvent dest = refinedPath.get(i + 1);

			HashSet<ChronoVertex> sd = new HashSet<ChronoVertex>();
			sd.add(source.getVertex());
			sd.add(dest.getVertex());

			if (ppp.containsKey(sd)) {
				ArrayList<Long> series = ppp.get(sd);
				series.add(dest.getTimestamp());
				ppp.put(sd, series);
			} else {
				ArrayList<Long> series = new ArrayList<Long>();
				series.add(dest.getTimestamp());
				ppp.put(sd, series);
			}
		}

		Document doc = createBaseQueryResults(traceEPC, traceTarget, startTime, endTime, orderDirection);

		Element aggregationTrace = doc.createElement("aggregationTrace");

		// JSONObject: source-dest : [intervals]
		JSONObject ret = new JSONObject();

		Iterator<Entry<HashSet<ChronoVertex>, ArrayList<Long>>> iter2 = ppp.entrySet().iterator();
		while (iter2.hasNext()) {
			Entry<HashSet<ChronoVertex>, ArrayList<Long>> entry = iter2.next();

			Iterator<ChronoVertex> vi = entry.getKey().iterator();

			Element parentID = doc.createElement("parentID");
			parentID.setTextContent(vi.next().toString());
			Element childEPC = doc.createElement("childEPC");
			childEPC.setTextContent(vi.next().toString());

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

					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

					Element startEventTime = doc.createElement("startEventTime");
					Date startDate = new Date(start);
					String startDateString = sdf.format(startDate);
					startEventTime.setTextContent(startDateString);
					Element endEventTime = doc.createElement("endEventTime");
					Date endDate = new Date(temp);
					String endDateString = sdf.format(endDate);
					endEventTime.setTextContent(endDateString);

					Element aggregationElement = doc.createElement("aggregationElement");
					aggregationElement.appendChild(parentID);
					aggregationElement.appendChild(childEPC);
					aggregationElement.appendChild(startEventTime);
					aggregationElement.appendChild(endEventTime);
					aggregationTrace.appendChild(aggregationElement);
					intvJsonArr.put(start + "-" + temp);
					start = null;
					continue;
				}
			}
			ret.put(key, intvJsonArr);
		}

		Element resultsBody = doc.createElement("resultsBody");
		resultsBody.appendChild(aggregationTrace);
		doc.getFirstChild().appendChild(resultsBody);

		// {"[urn:epc:id:sscc:0614141.1234567890,
		// urn:epc:id:sgtin:0614141.107346.2017]": ["1370703536591-1433775536591"]}
		return toString(doc);
	}

	public String getPossession(String traceEPC, String traceTarget, String startTime, String endTime, Long fromTimeMil,
			Long toTimeMil, String orderDirection) {

		Document doc = createBaseQueryResults(traceEPC, traceTarget, startTime, endTime, orderDirection);

		// Time processing
		long startTimeMil = 0;
		startTimeMil = TimeUtil.getTimeMil(startTime);

		ChronoGraph g = Configuration.persistentGraph;

		ChronoVertex v = g.getChronoVertex(traceEPC);
		HashMap<String, TreeMap<Long, String>> tNeighbors = v.getOwnershipTransfer(Direction.OUT, "isPossessed",
				startTimeMil, AC.$gte);

		// outV : [ "s-e", "s-e" ];
		JSONObject retObj = new JSONObject();
		Iterator<Entry<String, TreeMap<Long, String>>> iterator = tNeighbors.entrySet().iterator();
		Element possessionTrace = doc.createElement("possessionTrace");
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
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
						Element possessor = doc.createElement("possessor");
						possessor.setTextContent(neighbor);
						Element startEventTime = doc.createElement("startEventTime");
						Date startDate = new Date(start);
						String startDateString = sdf.format(startDate);
						startEventTime.setTextContent(startDateString);
						Element endEventTime = doc.createElement("endEventTime");
						Date endDate = new Date(time);
						String endDateString = sdf.format(endDate);
						endEventTime.setTextContent(endDateString);
						Element possessionElement = doc.createElement("possessionElement");
						possessionElement.appendChild(possessor);
						possessionElement.appendChild(startEventTime);
						possessionElement.appendChild(endEventTime);
						possessionTrace.appendChild(possessionElement);
						ranges.put(start + "-" + time);
						start = null;
					}
				}
			}
			retObj.put(neighbor, ranges);
		}

		Element resultsBody = doc.createElement("resultsBody");
		resultsBody.appendChild(possessionTrace);
		doc.getFirstChild().appendChild(resultsBody);

		return toString(doc);
	}

	public String getOwnership(String traceEPC, String traceTarget, String startTime, String endTime, Long fromTimeMil,
			Long toTimeMil, String orderDirection) {

		Document doc = createBaseQueryResults(traceEPC, traceTarget, startTime, endTime, orderDirection);

		// Time processing
		long startTimeMil = 0;
		startTimeMil = TimeUtil.getTimeMil(startTime);

		ChronoGraph g = Configuration.persistentGraph;

		ChronoVertex v = g.getChronoVertex(traceEPC);
		HashMap<String, TreeMap<Long, String>> tNeighbors = v.getOwnershipTransfer(Direction.OUT, "isOwned",
				startTimeMil, AC.$gte);

		// outV : [ "s-e", "s-e" ];
		JSONObject retObj = new JSONObject();
		Iterator<Entry<String, TreeMap<Long, String>>> iterator = tNeighbors.entrySet().iterator();
		Element possessionTrace = doc.createElement("ownershipTrace");
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
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
						Element possessor = doc.createElement("owner");
						possessor.setTextContent(neighbor);
						Element startEventTime = doc.createElement("startEventTime");
						Date startDate = new Date(start);
						String startDateString = sdf.format(startDate);
						startEventTime.setTextContent(startDateString);
						Element endEventTime = doc.createElement("endEventTime");
						Date endDate = new Date(time);
						String endDateString = sdf.format(endDate);
						endEventTime.setTextContent(endDateString);
						Element possessionElement = doc.createElement("ownershipElement");
						possessionElement.appendChild(possessor);
						possessionElement.appendChild(startEventTime);
						possessionElement.appendChild(endEventTime);
						possessionTrace.appendChild(possessionElement);
						ranges.put(start + "-" + time);
						start = null;
					}
				}
			}
			retObj.put(neighbor, ranges);
		}

		Element resultsBody = doc.createElement("resultsBody");
		resultsBody.appendChild(possessionTrace);
		doc.getFirstChild().appendChild(resultsBody);

		return toString(doc);
	}

	public String getReadPoint(String traceEPC, String traceTarget, String startTime, String endTime, Long fromTimeMil,
			Long toTimeMil, String orderDirection) {
		Document doc = createBaseQueryResults(traceEPC, traceTarget, startTime, endTime, orderDirection);

		// Time processing
		long startTimeMil = 0;
		startTimeMil = TimeUtil.getTimeMil(startTime);

		ChronoGraph g = Configuration.persistentGraph;

		ChronoVertex v = g.getChronoVertex(traceEPC);
		TreeMap<Long, ChronoVertex> timestampNeighbors = v.getTimestampNeighborVertices(Direction.OUT, "isLocatedIn",
				startTimeMil, AC.$gte);

		// outV : [ "t1", "t2", "t3", "t4" ];
		JSONObject retObj = new JSONObject();
		Iterator<Entry<Long, ChronoVertex>> iterator = timestampNeighbors.entrySet().iterator();
		Element readPointTrace = doc.createElement("readPointTrace");
		while (iterator.hasNext()) {
			Entry<Long, ChronoVertex> elem = iterator.next();
			Long time = elem.getKey();
			ChronoVertex neighbor = elem.getValue();

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			Element eventTime = doc.createElement("eventTime");
			Date date = new Date(time);
			String dateString = sdf.format(date);
			eventTime.setTextContent(dateString);
			Element readPoint = doc.createElement("readPoint");
			readPoint.setTextContent(neighbor.toString());
			Element readPointElement = doc.createElement("readPointElement");
			readPointElement.appendChild(eventTime);
			readPointElement.appendChild(readPoint);
			readPointTrace.appendChild(readPointElement);
			retObj.put(time.toString(), neighbor.toString());
		}

		Element resultsBody = doc.createElement("resultsBody");
		resultsBody.appendChild(readPointTrace);
		doc.getFirstChild().appendChild(resultsBody);

		return toString(doc);
	}

	public String getBizLocation(String traceEPC, String traceTarget, String startTime, String endTime,
			Long fromTimeMil, Long toTimeMil, String orderDirection) {
		Document doc = createBaseQueryResults(traceEPC, traceTarget, startTime, endTime, orderDirection);

		// Time processing
		long startTimeMil = 0;
		startTimeMil = TimeUtil.getTimeMil(startTime);

		ChronoGraph g = Configuration.persistentGraph;

		ChronoVertex v = g.getChronoVertex(traceEPC);
		TreeMap<Long, ChronoVertex> timestampNeighbors = v.getTimestampNeighborVertices(Direction.OUT, "isLocatedIn",
				startTimeMil, AC.$gte);

		// outV : [ "t1", "t2", "t3", "t4" ];
		JSONObject retObj = new JSONObject();
		Iterator<Entry<Long, ChronoVertex>> iterator = timestampNeighbors.entrySet().iterator();
		Element readPointTrace = doc.createElement("bizLocationTrace");
		while (iterator.hasNext()) {
			Entry<Long, ChronoVertex> elem = iterator.next();
			Long time = elem.getKey();
			ChronoVertex neighbor = elem.getValue();

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
			Element eventTime = doc.createElement("eventTime");
			Date date = new Date(time);
			String dateString = sdf.format(date);
			eventTime.setTextContent(dateString);
			Element readPoint = doc.createElement("bizLocation");
			readPoint.setTextContent(neighbor.toString());
			Element readPointElement = doc.createElement("bizLocationElement");
			readPointElement.appendChild(eventTime);
			readPointElement.appendChild(readPoint);
			readPointTrace.appendChild(readPointElement);
			retObj.put(time.toString(), neighbor.toString());
		}

		Element resultsBody = doc.createElement("resultsBody");
		resultsBody.appendChild(readPointTrace);
		doc.getFirstChild().appendChild(resultsBody);

		return toString(doc);
	}

	@SuppressWarnings("unused")
	public String getQuantity(String traceEPC, String traceTarget, String startTime, String endTime, Long fromTimeMil,
			Long toTimeMil, String orderDirection) {

		Document doc = createBaseQueryResults(traceEPC, traceTarget, startTime, endTime, orderDirection);

		// Time processing
		long startTimeMil = 0;
		startTimeMil = TimeUtil.getTimeMil(startTime);

		ChronoGraph g = Configuration.persistentGraph;

		ChronoVertex v = g.getChronoVertex(traceEPC);
		TreeSet<Long> timestamps = v.getTimestamps();

		Iterator<Long> ti = timestamps.iterator();
		Element quantityTrace = doc.createElement("quantityTrace");
		while (ti.hasNext()) {
			Long t = ti.next();
			VertexEvent ve = v.setTimestamp(t);
			Object qObj = ve.getProperty("quantity");
			Object uObj = ve.getProperty("uom");
			Double quantityDouble = null;
			if (qObj != null)
				quantityDouble = ((BsonDouble) qObj).doubleValue();

			String uomString = null;
			if (uObj != null)
				uomString = ((BsonString) uObj).getValue();

			if (quantityDouble != null || uomString != null) {
				Element quantity = doc.createElement("quantity");
				quantity.setTextContent(quantityDouble.toString());
				Element uom = doc.createElement("uom");
				uom.setTextContent(uomString);
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
				Element eventTime = doc.createElement("eventTime");
				Date date = new Date(t);
				String dateString = sdf.format(date);
				eventTime.setTextContent(dateString);
				Element quantityElement = doc.createElement("quantityElement");
				quantityElement.appendChild(eventTime);
				quantityElement.appendChild(quantity);
				quantityElement.appendChild(uom);
				quantityTrace.appendChild(quantityElement);
			}
		}

		Element resultsBody = doc.createElement("resultsBody");
		resultsBody.appendChild(quantityTrace);
		doc.getFirstChild().appendChild(resultsBody);

		return toString(doc);

	}

	public String getEventType(String traceEPC, String traceTarget, String startTime, String endTime, Long fromTimeMil,
			Long toTimeMil, String orderDirection) {
		
		Document document = createBaseQueryResults(traceEPC, traceTarget, startTime, endTime, orderDirection);
		Element eventTypeTrace = document.createElement("eventTypeTrace");

		MongoQueryService mqs = new MongoQueryService();
		try {
			ArrayList<CachedChronoVertex> vertexList = null;
			if (traceEPC.contains("lgtin") || traceEPC.indexOf("*") != -1) {
				PollParameters params = new PollParameters("SimpleEventQuery", null, null, null, null, null, null,
						null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
						traceEPC, null, null, null, null, null, null, null, null, null, null, null, "eventTime",
						orderDirection, null, null, null, null, null, null, null, null, null, null, null, null);

				vertexList = mqs.pollEventVertices(params, null, null, null);

			} else {
				PollParameters params = new PollParameters("SimpleEventQuery", null, null, null, null, null, null,
						null, null, null, null, null, null, null, null, null, null, null, traceEPC, null, null,
						null, null, null, null, null, null, null, null, null, null, null, null, null, "eventTime",
						orderDirection, null, null, null, null, null, null, null, null, null, null, null, null);

				vertexList = mqs.pollEventVertices(params, null, null, null);
			}

			Iterator<CachedChronoVertex> vi = vertexList.iterator();
			while (vi.hasNext()) {
				CachedChronoVertex v = vi.next();
				BsonDocument doc = v.getProperties();
				String eventTypeString = doc.getString("eventType").getValue();
				String actionString = null;
				if (doc.containsKey("action"))
					actionString = doc.getString("action").getValue();
				Element eventType = document.createElement("eventType");
				eventType.setTextContent(eventTypeString);
				Element action = document.createElement("action");
				action.setTextContent(actionString);

				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
				Element eventTime = document.createElement("eventTime");
				Date date = new Date(doc.getDateTime("eventTime").getValue());
				String dateString = sdf.format(date);
				eventTime.setTextContent(dateString);
				Element eventTypeElement = document.createElement("eventTypeElement");
				eventTypeElement.appendChild(eventTime);
				eventTypeElement.appendChild(eventType);
				eventTypeElement.appendChild(action);
				eventTypeTrace.appendChild(eventTypeElement);
			}
			Element resultsBody = document.createElement("resultsBody");
			resultsBody.appendChild(eventTypeTrace);
			document.getFirstChild().appendChild(resultsBody);
		} catch (QueryParameterException | QueryTooLargeException e) {
			e.printStackTrace();
		}
		
		Element resultsBody = document.createElement("resultsBody");
		resultsBody.appendChild(eventTypeTrace);
		document.getFirstChild().appendChild(resultsBody);

		return toString(document);
	}
	
	public String getBizStep(String traceEPC, String traceTarget, String startTime, String endTime, Long fromTimeMil,
			Long toTimeMil, String orderDirection) {
		
		Document document = createBaseQueryResults(traceEPC, traceTarget, startTime, endTime, orderDirection);
		Element eventTypeTrace = document.createElement("bizStepTrace");

		MongoQueryService mqs = new MongoQueryService();
		try {
			ArrayList<CachedChronoVertex> vertexList = null;
			if (traceEPC.contains("lgtin") || traceEPC.indexOf("*") != -1) {
				PollParameters params = new PollParameters("SimpleEventQuery", null, null, null, null, null, null,
						null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
						traceEPC, null, null, null, null, null, null, null, null, null, null, null, "eventTime",
						orderDirection, null, null, null, null, null, null, null, null, null, null, null, null);

				vertexList = mqs.pollEventVertices(params, null, null, null);

			} else {
				PollParameters params = new PollParameters("SimpleEventQuery", null, null, null, null, null, null,
						null, null, null, null, null, null, null, null, null, null, null, traceEPC, null, null,
						null, null, null, null, null, null, null, null, null, null, null, null, null, "eventTime",
						orderDirection, null, null, null, null, null, null, null, null, null, null, null, null);

				vertexList = mqs.pollEventVertices(params, null, null, null);
			}

			Iterator<CachedChronoVertex> vi = vertexList.iterator();
			while (vi.hasNext()) {
				CachedChronoVertex v = vi.next();
				BsonDocument doc = v.getProperties();
				if (!doc.containsKey("bizStep"))
					continue;
				String bizStepString = doc.getString("bizStep").getValue();
				Element bizStep = document.createElement("bizstep");
				bizStep.setTextContent(bizStepString);
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
				Element eventTime = document.createElement("eventTime");
				Date date = new Date(doc.getDateTime("eventTime").getValue());
				String dateString = sdf.format(date);
				eventTime.setTextContent(dateString);
				Element bizStepElement = document.createElement("bizStepElement");
				bizStepElement.appendChild(eventTime);
				bizStepElement.appendChild(bizStep);
				eventTypeTrace.appendChild(bizStepElement);
			}
			Element resultsBody = document.createElement("resultsBody");
			resultsBody.appendChild(eventTypeTrace);
			document.getFirstChild().appendChild(resultsBody);
		} catch (QueryParameterException | QueryTooLargeException e) {
			e.printStackTrace();
		}
		
		Element resultsBody = document.createElement("resultsBody");
		resultsBody.appendChild(eventTypeTrace);
		document.getFirstChild().appendChild(resultsBody);

		return toString(document);
	}
	
	public String getDisposition(String traceEPC, String traceTarget, String startTime, String endTime, Long fromTimeMil,
			Long toTimeMil, String orderDirection) {
		
		Document document = createBaseQueryResults(traceEPC, traceTarget, startTime, endTime, orderDirection);
		Element eventTypeTrace = document.createElement("dispositionTrace");

		MongoQueryService mqs = new MongoQueryService();
		try {
			ArrayList<CachedChronoVertex> vertexList = null;
			if (traceEPC.contains("lgtin") || traceEPC.indexOf("*") != -1) {
				PollParameters params = new PollParameters("SimpleEventQuery", null, null, null, null, null, null,
						null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
						traceEPC, null, null, null, null, null, null, null, null, null, null, null, "eventTime",
						orderDirection, null, null, null, null, null, null, null, null, null, null, null, null);

				vertexList = mqs.pollEventVertices(params, null, null, null);

			} else {
				PollParameters params = new PollParameters("SimpleEventQuery", null, null, null, null, null, null,
						null, null, null, null, null, null, null, null, null, null, null, traceEPC, null, null,
						null, null, null, null, null, null, null, null, null, null, null, null, null, "eventTime",
						orderDirection, null, null, null, null, null, null, null, null, null, null, null, null);

				vertexList = mqs.pollEventVertices(params, null, null, null);
			}

			Iterator<CachedChronoVertex> vi = vertexList.iterator();
			while (vi.hasNext()) {
				CachedChronoVertex v = vi.next();
				BsonDocument doc = v.getProperties();
				if (!doc.containsKey("disposition"))
					continue;
				String bizStepString = doc.getString("disposition").getValue();
				Element bizStep = document.createElement("disposition");
				bizStep.setTextContent(bizStepString);
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
				Element eventTime = document.createElement("eventTime");
				Date date = new Date(doc.getDateTime("eventTime").getValue());
				String dateString = sdf.format(date);
				eventTime.setTextContent(dateString);
				Element bizStepElement = document.createElement("dispositionElement");
				bizStepElement.appendChild(eventTime);
				bizStepElement.appendChild(bizStep);
				eventTypeTrace.appendChild(bizStepElement);
			}
			Element resultsBody = document.createElement("resultsBody");
			resultsBody.appendChild(eventTypeTrace);
			document.getFirstChild().appendChild(resultsBody);
		} catch (QueryParameterException | QueryTooLargeException e) {
			e.printStackTrace();
		}
		
		Element resultsBody = document.createElement("resultsBody");
		resultsBody.appendChild(eventTypeTrace);
		document.getFirstChild().appendChild(resultsBody);

		return toString(document);
	}
	
	public String getBizTransactionList(String traceEPC, String traceTarget, String startTime, String endTime, Long fromTimeMil,
			Long toTimeMil, String orderDirection) {
		
		Document document = createBaseQueryResults(traceEPC, traceTarget, startTime, endTime, orderDirection);
		Element bizTransactionListTrace = document.createElement("bizTransactionListTrace");

		MongoQueryService mqs = new MongoQueryService();
		try {
			ArrayList<CachedChronoVertex> vertexList = null;
			if (traceEPC.contains("lgtin") || traceEPC.indexOf("*") != -1) {
				PollParameters params = new PollParameters("SimpleEventQuery", null, null, null, null, null, null,
						null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
						traceEPC, null, null, null, null, null, null, null, null, null, null, null, "eventTime",
						orderDirection, null, null, null, null, null, null, null, null, null, null, null, null);

				vertexList = mqs.pollEventVertices(params, null, null, null);

			} else {
				PollParameters params = new PollParameters("SimpleEventQuery", null, null, null, null, null, null,
						null, null, null, null, null, null, null, null, null, null, null, traceEPC, null, null,
						null, null, null, null, null, null, null, null, null, null, null, null, null, "eventTime",
						orderDirection, null, null, null, null, null, null, null, null, null, null, null, null);

				vertexList = mqs.pollEventVertices(params, null, null, null);
			}

			Iterator<CachedChronoVertex> vi = vertexList.iterator();
			while (vi.hasNext()) {
				CachedChronoVertex v = vi.next();
				BsonDocument doc = v.getProperties();
				if (!doc.containsKey("bizTransactionList"))
					continue;
				
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
				Element eventTime = document.createElement("eventTime");
				Date date = new Date(doc.getDateTime("eventTime").getValue());
				String dateString = sdf.format(date);
				eventTime.setTextContent(dateString);
				
				BsonArray bizTransactionArray = doc.getArray("bizTransactionList");
				Iterator<BsonValue> bizTransactionIter = bizTransactionArray.iterator();
				Element bizTransactionList = document.createElement("bizTransactionList");
				while(bizTransactionIter.hasNext()) {
					BsonDocument bizTransactionDoc = bizTransactionIter.next().asDocument();
					Entry<String, BsonValue> entry = bizTransactionDoc.entrySet().iterator().next();
					String key = entry.getKey();
					String value = entry.getValue().asString().getValue();
					Element bizTransaction = document.createElement("bizTransaction");
					bizTransaction.setAttribute("type", key);
					bizTransaction.setTextContent(value);
					bizTransactionList.appendChild(bizTransaction);
				}
				
				Element bizTransactionElement = document.createElement("bizTransactionElement");
				bizTransactionElement.appendChild(eventTime);
				bizTransactionElement.appendChild(bizTransactionList);
				bizTransactionListTrace.appendChild(bizTransactionElement);
			}
			Element resultsBody = document.createElement("resultsBody");
			resultsBody.appendChild(bizTransactionListTrace);
			document.getFirstChild().appendChild(resultsBody);
		} catch (QueryParameterException | QueryTooLargeException e) {
			e.printStackTrace();
		}
		
		Element resultsBody = document.createElement("resultsBody");
		resultsBody.appendChild(bizTransactionListTrace);
		document.getFirstChild().appendChild(resultsBody);

		return toString(document);
	}

	public String toString(Document doc) {
		try {
			Transformer tf = TransformerFactory.newInstance().newTransformer();
			tf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			tf.setOutputProperty(OutputKeys.INDENT, "yes");
			Writer out = new StringWriter();
			tf.transform(new DOMSource(doc), new StreamResult(out));
			return out.toString();
		} catch (TransformerConfigurationException e) {
			return null;
		} catch (TransformerException e) {

			return null;
		}
	}

	@RequestMapping(value = "/TraceabilityQuery", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<?> traceabilityQuery(@RequestParam String traceEPC, @RequestParam String traceTarget,
			@RequestParam(required = false) String startTime, @RequestParam(required = false) String endTime,
			@RequestParam(required = false) String orderDirection) {

		// Time processing
		long fromTimeMil = 0;
		long toTimeMil = 0;
		fromTimeMil = TimeUtil.getTimeMil(startTime);
		toTimeMil = TimeUtil.getTimeMil(endTime);

		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "application/xml; charset=utf-8");

		if (traceTarget.equals("transformation")) {
			String result = getTransformation(traceEPC, traceTarget, startTime, endTime, fromTimeMil, toTimeMil,
					orderDirection);
			if (result != null)
				return new ResponseEntity<>(result, responseHeaders, HttpStatus.OK);
			else
				return new ResponseEntity<>(null, responseHeaders, HttpStatus.BAD_REQUEST);
		} else if (traceTarget.equals("aggregation")) {
			String result = getAggregation(traceEPC, traceTarget, startTime, endTime, fromTimeMil, toTimeMil,
					orderDirection);
			if (result != null)
				return new ResponseEntity<>(result, responseHeaders, HttpStatus.OK);
			else
				return new ResponseEntity<>(null, responseHeaders, HttpStatus.BAD_REQUEST);
		} else if (traceTarget.equals("possession")) {
			String result = getPossession(traceEPC, traceTarget, startTime, endTime, fromTimeMil, toTimeMil,
					orderDirection);
			if (result != null)
				return new ResponseEntity<>(result, responseHeaders, HttpStatus.OK);
			else
				return new ResponseEntity<>(null, responseHeaders, HttpStatus.BAD_REQUEST);
		} else if (traceTarget.equals("ownership")) {
			String result = getOwnership(traceEPC, traceTarget, startTime, endTime, fromTimeMil, toTimeMil,
					orderDirection);
			if (result != null)
				return new ResponseEntity<>(result, responseHeaders, HttpStatus.OK);
			else
				return new ResponseEntity<>(null, responseHeaders, HttpStatus.BAD_REQUEST);
		} else if (traceTarget.equals("readPoint")) {
			String result = getReadPoint(traceEPC, traceTarget, startTime, endTime, fromTimeMil, toTimeMil,
					orderDirection);
			if (result != null)
				return new ResponseEntity<>(result, responseHeaders, HttpStatus.OK);
			else
				return new ResponseEntity<>(null, responseHeaders, HttpStatus.BAD_REQUEST);
		} else if (traceTarget.equals("bizLocation")) {
			String result = getBizLocation(traceEPC, traceTarget, startTime, endTime, fromTimeMil, toTimeMil,
					orderDirection);
			if (result != null)
				return new ResponseEntity<>(result, responseHeaders, HttpStatus.OK);
			else
				return new ResponseEntity<>(null, responseHeaders, HttpStatus.BAD_REQUEST);
		} else if (traceTarget.equals("quantity")) {
			String result = getQuantity(traceEPC, traceTarget, startTime, endTime, fromTimeMil, toTimeMil,
					orderDirection);
			if (result != null)
				return new ResponseEntity<>(result, responseHeaders, HttpStatus.OK);
			else
				return new ResponseEntity<>(null, responseHeaders, HttpStatus.BAD_REQUEST);
		} else if (traceTarget.equals("eventType")) {
			String result = getEventType(traceEPC, traceTarget, startTime, endTime, fromTimeMil, toTimeMil,
					orderDirection);
			if (result != null)
				return new ResponseEntity<>(result, responseHeaders, HttpStatus.OK);
			else
				return new ResponseEntity<>(null, responseHeaders, HttpStatus.BAD_REQUEST);
		} else if (traceTarget.equals("bizStep")) {
			String result = getBizStep(traceEPC, traceTarget, startTime, endTime, fromTimeMil, toTimeMil,
					orderDirection);
			if (result != null)
				return new ResponseEntity<>(result, responseHeaders, HttpStatus.OK);
			else
				return new ResponseEntity<>(null, responseHeaders, HttpStatus.BAD_REQUEST);
		} else if (traceTarget.equals("disposition")) {
			String result = getDisposition(traceEPC, traceTarget, startTime, endTime, fromTimeMil, toTimeMil,
					orderDirection);
			if (result != null)
				return new ResponseEntity<>(result, responseHeaders, HttpStatus.OK);
			else
				return new ResponseEntity<>(null, responseHeaders, HttpStatus.BAD_REQUEST);
		} else if (traceTarget.equals("bizTransactionList")) {
			String result = getBizTransactionList(traceEPC, traceTarget, startTime, endTime, fromTimeMil, toTimeMil,
					orderDirection);
			if (result != null)
				return new ResponseEntity<>(result, responseHeaders, HttpStatus.OK);
			else
				return new ResponseEntity<>(null, responseHeaders, HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(null, responseHeaders, HttpStatus.OK);
	}

	/**
	 * JSONArray
	 * 
	 * @return a list of nodes in graph store
	 */
	@RequestMapping(value = "/Resources", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<?> getAllVertices() {

		// ScriptEngineManager man = new ScriptEngineManager();
		// ScriptEngine engine = man.getEngineByName("groovy");
		// engine.put("graph", servletContext.getAttribute("cachedGraph"));
		//
		// engine.put("engine", new CachedTraversalEngine());
		// engine.put("CachedChronoVertex_class", CachedChronoVertex.class);
		// try {
		// // CachedChronoGraph g = Configuration.cachedGraph;
		// // g.addEdge("1", "2", "c");
		// // g.getChronoVertex("1");
		// // new CachedTraversalEngine().out(null, Integer.MAX_VALUE);
		// engine.eval("graph.addEdge(\"1\",\"2\",\"c\");"
		// + "engine =
		// engine.setStartsLazy(graph.getChronoVertex(\"1\"),true,false,CachedChronoVertex_class);"
		// + "engine.out(null, Integer.MAX_VALUE);" +
		// "System.out.println(engine.toList());");
		// } catch (ScriptException e) {
		// e.printStackTrace();
		// }

		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "application/json; charset=utf-8");

		JSONArray jarr = new JSONArray();
		Iterator<ChronoVertex> vi = Configuration.persistentGraph.getChronoVertices().iterator();
		while (vi.hasNext()) {
			jarr.put(vi.next().toString());
		}
		return new ResponseEntity<>(new String(jarr.toString(1)), responseHeaders, HttpStatus.OK);
	}

	/**
	 * Text
	 * 
	 * @return remove All vertices for developer
	 */
	@RequestMapping(value = "/Resources", method = RequestMethod.DELETE)
	@ResponseBody
	public ResponseEntity<?> deleteAllExistingNodes() {
		Configuration.persistentGraph.getVertexCollection().drop();
		Configuration.persistentGraph.getEdgeCollection().drop();
		return new ResponseEntity<>("[Lilliput] : All Vertices Removed\n", HttpStatus.OK);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/Transform", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<?> getTransformTree(@RequestParam String epc,
			@RequestParam(required = false) String startTime, @RequestParam(required = false) String order) {

		// Time processing
		long startTimeMil = 0;
		startTimeMil = TimeUtil.getTimeMil(startTime);

		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "application/json; charset=utf-8");

		BsonArray transforms = new BsonArray();
		transforms.add(new BsonString("transformsTo"));

		ChronoGraph g = Configuration.persistentGraph;

		PersistentBreadthFirstSearch tBFS = new PersistentBreadthFirstSearch();
		Map pathMap = new HashMap();
		if (order.equals("forward"))
			pathMap = tBFS.compute(g, g.getChronoVertex(epc).setTimestamp(startTimeMil), "transformsTo",
					TemporalType.TIMESTAMP, AC.$gte, null, null, null, null, null, null, Position.first, order);
		else
			pathMap = tBFS.compute(g, g.getChronoVertex(epc).setTimestamp(startTimeMil), "transformsTo",
					TemporalType.TIMESTAMP, AC.$lte, null, null, null, null, null, null, Position.last, order);

		// JSONarray contains each path
		// contains time - vertex mapping

		JSONArray pathArray = new JSONArray();

		Iterator<Set> pathSetIter = pathMap.values().iterator();
		while (pathSetIter.hasNext()) {
			Set pathSet = pathSetIter.next();
			Iterator<List> pathIter = pathSet.iterator();
			while (pathIter.hasNext()) {
				List path = pathIter.next();
				Iterator<VertexEvent> vi = path.iterator();
				JSONArray p = new JSONArray();
				while (vi.hasNext()) {
					Object ve = vi.next();
					if (ve != null)
						p.put(ve.toString());
				}
				pathArray.put(p);
			}
		}

		return new ResponseEntity<>(pathArray.toString(2), responseHeaders, HttpStatus.OK);

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/TransformE", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<?> getTransformTreeEmulation(@RequestParam String epc,
			@RequestParam(required = false) String startTime, @RequestParam(required = false) String order) {

		// 여기에 에뮬레이션을 함

		// Time processing
		long startTimeMil = 0;
		startTimeMil = TimeUtil.getTimeMil(startTime);

		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "application/json; charset=utf-8");

		BsonArray transforms = new BsonArray();
		transforms.add(new BsonString("transformsTo"));

		PersistentBreadthFirstSearchEmulation tBFS = new PersistentBreadthFirstSearchEmulation();
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
					if (ve != null) {
						p.put(ve.toString());
					}
				}
				pathArray.put(p);
			}
		}
		// {urn:epc:id:sgtin:0000001.000001.6-1509461936591=[[urn:epc:id:sgtin:0000001.000001.1-946652400000,
		// urn:epc:id:sgtin:0000001.000001.2-1383231536591,
		// urn:epc:id:sgtin:0000001.000001.3-1414767536591,
		// urn:epc:id:sgtin:0000001.000001.4-1446303536591,
		// urn:epc:id:sgtin:0000001.000001.5-1477925936591,
		// urn:epc:id:sgtin:0000001.000001.6-1509461936591]]}

		return new ResponseEntity<>(pathArray.toString(2), responseHeaders, HttpStatus.OK);

	}

	@RequestMapping(value = "/OwnershipTransfer", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<?> getOwnershipTransferQuery(@RequestParam String epc,
			@RequestParam(required = false) String startTime, @RequestParam(required = false) String order) {

		// Time processing
		long startTimeMil = 0;
		startTimeMil = TimeUtil.getTimeMil(startTime);

		ChronoGraph g = Configuration.persistentGraph;

		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "application/json; charset=utf-8");

		ChronoVertex v = g.getChronoVertex(epc);
		HashMap<String, TreeMap<Long, String>> tNeighbors = v.getOwnershipTransfer(Direction.OUT, "isPossessed",
				startTimeMil, AC.$gte);

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

		// {
		// "urn:epc:id:sgln:0000001.00001.0": [
		// "1513081997716-1513081998717"
		// ],
		// "urn:epc:id:sgln:0000001.00001.1": [
		// "1513081998717-1513081999717"
		// ],
		// "urn:epc:id:sgln:0000001.00001.2": [
		// "1513081999717-1513082000717"
		// ],
		// "urn:epc:id:sgln:0000001.00001.3": [
		// "1513082000717-1513082001718"
		// ]
		// }

		return new ResponseEntity<>(retObj.toString(2), responseHeaders, HttpStatus.OK);

	}

	@RequestMapping(value = "/Location", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<?> getLocationQuery(@RequestParam String epc,
			@RequestParam(required = false) String startTime, @RequestParam(required = false) String order) {

		// Time processing
		long startTimeMil = 0;
		startTimeMil = TimeUtil.getTimeMil(startTime);

		ChronoGraph g = Configuration.persistentGraph;

		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "application/json; charset=utf-8");

		ChronoVertex v = g.getChronoVertex(epc);
		TreeMap<Long, ChronoVertex> timestampNeighbors = v.getTimestampNeighborVertices(Direction.OUT, "isLocatedIn",
				startTimeMil, AC.$gte);

		// outV : [ "t1", "t2", "t3", "t4" ];
		JSONObject retObj = new JSONObject();
		Iterator<Entry<Long, ChronoVertex>> iterator = timestampNeighbors.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<Long, ChronoVertex> elem = iterator.next();
			Long time = elem.getKey();
			ChronoVertex neighbor = elem.getValue();
			retObj.put(time.toString(), neighbor.toString());
		}

		return new ResponseEntity<>(retObj.toString(2), responseHeaders, HttpStatus.OK);

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/Aggregation", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<?> getAggregationQuery(@RequestParam String epc,
			@RequestParam(required = false) String startTime, @RequestParam(required = false) String order) {

		// Time processing
		long startTimeMil = 0;
		startTimeMil = TimeUtil.getTimeMil(startTime);

		ChronoGraph g = Configuration.persistentGraph;

		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "application/json; charset=utf-8");

		VertexEvent ve = g.getChronoVertex(epc).setTimestamp(startTimeMil);

		BsonArray contains = new BsonArray();
		contains.add(new BsonString("contains"));

		LoopPipeFunction loop = new LoopPipeFunction() {

			@Override
			public boolean compute(Object argument, Map<Object, Object> currentPath, int loopCount) {
				if (argument == null)
					return false;
				else
					return true;
			}
		};

		PipeFunction<VertexEvent, VertexEvent> transform = new PipeFunction<VertexEvent, VertexEvent>() {

			@Override
			public VertexEvent compute(VertexEvent argument) {

				Set<VertexEvent> veSet = argument.getBothVertexEventSet("contains", AC.$gt);
				if (veSet == null || veSet.isEmpty() || veSet.size() == 0)
					return null;

				if (veSet.size() == 1)
					return veSet.iterator().next();

				Iterator<VertexEvent> iter = veSet.iterator();
				VertexEvent v1 = null;
				VertexEvent v2 = null;
				if (iter.hasNext())
					v1 = iter.next();
				if (iter.hasNext())
					v2 = iter.next();

				if (v1 != null && v2 != null) {
					if (v1.getTimestamp() < v2.getTimestamp())
						return v1;
					else
						return v2;
				}
				return null;

			}
		};

		TraversalEngine engine = new TraversalEngine(g, ve, false, true, VertexEvent.class);
		engine.as("s");
		engine.transform(transform, VertexEvent.class);
		engine.loop("s", loop).toList();

		ArrayList<VertexEvent> refinedPath = new ArrayList<VertexEvent>();
		HashMap<HashSet<ChronoVertex>, ArrayList<Long>> ppp = new HashMap<HashSet<ChronoVertex>, ArrayList<Long>>();

		Map path = engine.path();

		Iterator iter = path.values().iterator();
		while (iter.hasNext()) {
			HashSet next = (HashSet) iter.next();
			Iterator<ArrayList> pathIter = next.iterator();
			while (pathIter.hasNext()) {
				ArrayList eachPath = pathIter.next();
				Iterator elemIter = eachPath.iterator();
				while (elemIter.hasNext()) {
					Object elem = elemIter.next();
					if (elem instanceof VertexEvent) {
						VertexEvent veElem = (VertexEvent) elem;
						refinedPath.add(veElem);
					}
				}
			}
		}

		for (int i = 0; i < refinedPath.size() - 1; i++) {
			VertexEvent source = refinedPath.get(i);
			VertexEvent dest = refinedPath.get(i + 1);

			HashSet<ChronoVertex> sd = new HashSet<ChronoVertex>();
			sd.add(source.getVertex());
			sd.add(dest.getVertex());

			if (ppp.containsKey(sd)) {
				ArrayList<Long> series = ppp.get(sd);
				series.add(dest.getTimestamp());
				ppp.put(sd, series);
			} else {
				ArrayList<Long> series = new ArrayList<Long>();
				series.add(dest.getTimestamp());
				ppp.put(sd, series);
			}
		}

		// JSONObject: source-dest : [intervals]
		JSONObject ret = new JSONObject();

		Iterator<Entry<HashSet<ChronoVertex>, ArrayList<Long>>> iter2 = ppp.entrySet().iterator();
		while (iter2.hasNext()) {
			Entry<HashSet<ChronoVertex>, ArrayList<Long>> entry = iter2.next();
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

		return new ResponseEntity<>(ret.toString(2), responseHeaders, HttpStatus.OK);

	}

	class SourceDest {
		public ChronoVertex source;
		public ChronoVertex dest;

		public SourceDest(ChronoVertex source, ChronoVertex dest) {
			this.source = source;
			this.dest = dest;
		}

		public String toString() {
			return source.toString() + "-" + dest.toString();
		}
	}

	/**
	 * 
	 * @param epc
	 *            Name of Resource
	 * @param scope
	 * @param fromTime
	 *            Refer to Graphite URL API model
	 * 
	 *            RELATIVE_TIME or ABSOLUTE_TIME
	 * 
	 *            RELATIVE_TIME
	 * 
	 *            s: Seconds
	 * 
	 *            min: Minutes
	 * 
	 *            h: Hours
	 * 
	 *            d: Days
	 * 
	 *            w: Weeks mon: 30 Days(month)
	 * 
	 *            y: 365 Days (year)
	 * 
	 *            ABSOLUTE_TIME FORMAT SimpleDateFormat sdf = new SimpleDateFormat(
	 *            "yyyy-MM-dd'T'HH:mm:ss"); GregorianCalendar eventTimeCalendar =
	 *            new GregorianCalendar(); Date date = sdf.parse("time");
	 * @param toTime
	 *            examples: &from=-8d&until=-7d
	 *            &from=2007-12-02T21:32:52&until=2007-12-02T21:35:55
	 * @param orderByTime
	 *            {asc|desc}
	 * @param limit
	 *            number of results
	 * @return a list of neighbor nodes which {epc} have {csv of relationship} with.
	 *         Time range can be specified with ‘fromTime’, ‘toTime’ or ‘recentOne’.
	 *         Attribute(s) in {csv of relationship} can be filtered. If recursive
	 *         is on, this API returns transitive closure of nodes as sub-document
	 *         form. Also, if recursiveOrder is desc, a sub-document cannot contain
	 *         older relationship of parent one.
	 */
	@RequestMapping(value = "/Resource", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<?> getResource(@RequestParam String epc, @RequestParam(required = false) String scope,
			@RequestParam(required = false) String fromTime, @RequestParam(required = false) String toTime,
			@RequestParam(required = false) String orderByTime, @RequestParam(required = false) String limit,
			@RequestParam(required = false) String relationship, HttpServletRequest request) {

		if (scope == null)
			scope = "resource";

		// Time processing
		long fromTimeMil = 0;
		long toTimeMil = 0;
		fromTimeMil = TimeUtil.getTimeMil(fromTime);
		toTimeMil = TimeUtil.getTimeMil(toTime);

		ChronoGraph g = Configuration.persistentGraph;

		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "application/json; charset=utf-8");

		if (scope.trim().equals("resource")) {
			String ret = getVertexAttributesAsJsonLD(g, request, epc, fromTimeMil, toTimeMil, orderByTime, limit);
			return new ResponseEntity<>(ret, responseHeaders, HttpStatus.OK);
		} else if (scope.trim().equals("relationship")) {
			String ret = getRelationship(g, epc);
			return new ResponseEntity<>(ret, responseHeaders, HttpStatus.OK);
		} else if (scope.trim().equals("ego")) {
			String ret = getEgoNetwork(g, request, epc, relationship, fromTimeMil, toTimeMil);
			return new ResponseEntity<>(ret, responseHeaders, HttpStatus.OK);

		} else if (scope.trim().equals("sibling")) {
			if (relationship.split(",").length != 2) {
				return new ResponseEntity<>(
						new String(
								"Format of 'relationship' in sibling network = {edgeLabelToParent,edgeLabelToSibling}"),
						HttpStatus.BAD_REQUEST);
			}
			String ret = getSiblingNetwork(g, request, epc, relationship, fromTimeMil, toTimeMil);
			return new ResponseEntity<>(ret, responseHeaders, HttpStatus.OK);
		} else if (scope.trim().equals("trace")) {
			if (relationship.split(",").length != 1) {
				return new ResponseEntity<>(new String("Format of 'relationship' = edgeLabel"), HttpStatus.BAD_REQUEST);
			}
			String ret = getTracePath(g, request, epc, relationship, fromTimeMil, toTimeMil);
			return new ResponseEntity<>(ret, responseHeaders, HttpStatus.OK);
		}

		return new ResponseEntity<>(new String("Format of 'scope' = {resource|relationship|ego|sibling|trace}"),
				HttpStatus.BAD_REQUEST);
	}

	public JSONObject getNoN(ChronoGraph g, HttpServletRequest request, String parent, String parentRel, String epc,
			String relationship, long fromTimeMil, long toTimeMil) {
		try {

			// Vertex should be unique

			ChronoVertex v = g.getChronoVertices("vlabel", epc).iterator().next();

			JSONObject retObj = new JSONObject();

			// Keep list of relationships
			Set<String> relSet = new HashSet<String>();
			if (relationship != null) {
				String[] relArr = relationship.split(",");
				for (int i = 0; i < relArr.length; i++) {
					String relStr = relArr[i].trim();
					if (relStr != null)
						relSet.add(relStr);
				}
			}

			if (relSet.size() == 0) {
				// If relation is empty, obtain all the relationships
				Iterator<ChronoEdge> edgeIter = v.getChronoEdges(Direction.OUT, null).iterator();
				while (edgeIter.hasNext()) {
					relSet.add(edgeIter.next().getLabel());
				}
			}

			Iterator<ChronoEdge> edgeIter = v.getChronoEdges(Direction.OUT, null).iterator();

			while (edgeIter.hasNext()) {
				// Variables
				ChronoEdge edge = edgeIter.next();
				String edgeLabel = edge.getLabel();
				long timestamp = 0;

				// Contraint Check
				if (!relSet.contains(edgeLabel))
					continue;
				if (edge.getPropertyKeys().size() == 0)
					continue;

				JSONObject edgeObj;
				if (retObj.isNull(edgeLabel)) {
					edgeObj = new JSONObject();
				} else {
					edgeObj = retObj.getJSONObject(edgeLabel);
				}

				// NeigborVertex
				ChronoVertex neighbor = edge.getChronoVertex(Direction.IN);
				String neighborLabel = neighbor.getProperty("vlabel");

				if (neighborLabel.equals(parent))
					continue;

				JSONObject neighborObj;

				if (edgeObj.isNull(neighborLabel)) {
					neighborObj = new JSONObject();
				} else {
					neighborObj = edgeObj.getJSONObject(neighborLabel);
				}

				Iterator<String> key = edge.getPropertyKeys().iterator();
				boolean isIncluded = false;
				while (key.hasNext()) {
					try {
						String keyString = key.next();
						timestamp = Long.parseLong(keyString);
						if (timestamp == 0)
							continue;

						if ((fromTimeMil != 0 && timestamp < fromTimeMil) || (toTimeMil != 0 && timestamp > toTimeMil))
							continue;

						isIncluded = true;
					} catch (NumberFormatException e) {
						// Do Nothing
					}
				}
				if (isIncluded == true) {
					edgeObj.put(neighborLabel, neighborObj);
					retObj.put(edgeLabel, edgeObj);
				}
			}

			// String id = convertJsonLDID(epc, request);
			retObj.put("@id", epc);
			return retObj;

		} catch (NoSuchElementException e) {
			return null;
		}
	}

	@SuppressWarnings({ "rawtypes", "null" })
	public String getTracePath(ChronoGraph g, HttpServletRequest request, String epc, String relationship,
			long fromTimeMil, long toTimeMil) {
		try {

			relationship = relationship.trim();

			JSONObject retObj = new JSONObject();

			// Vertex should be unique
			ChronoVertex v = g.getChronoVertices("vlabel", epc).iterator().next();

			List path = new ArrayList();
			ScriptEngine engine = null;
			// ScriptEngine engine = new GremlinGroovyScriptEngine();
			engine.put("g", g);
			engine.put("list", path);

			String prefix = "g.V('vlabel','" + v.getProperty("vlabel") + "')";
			String body = "";
			String surfix = ".tree{it.vlabel}.cap.fill(list)";

			int i = 0;
			for (; true; i++) {
				body += ".out('" + relationship + "')";
				engine.eval(prefix + body + surfix);
				if (((Tree) path.get(path.size() - 1)).size() == 0) {
					break;
				}
			}
			if (i < 1) {
				return null;
			}

			retObj = new JSONObject((HashMap) path.get(i - 1));

			Tree resultTree = (Tree) path.get(path.size() - 2);

			Iterator keyIter = resultTree.keySet().iterator();
			if (keyIter.hasNext()) {
				String key = (String) keyIter.next();
				HashMap map = (HashMap) resultTree.get(key);
				retObj = new JSONObject(map);
			}

			// putJsonLDID(retObj, request);
			retObj.put("@id", epc);
			// putJsonLDRelContext(retObj);
			return retObj.toString(3);

		} catch (NoSuchElementException e) {
			return "No such vertex labelled with {epc} : " + e;
		} catch (ScriptException e) {
			return e.toString();
		}
	}

	public String getSiblingNetwork(ChronoGraph g, HttpServletRequest request, String epc, String relationship,
			long fromTimeMil, long toTimeMil) {
		try {

			// String expression = "g.V('label','" +
			// v.getProperty("label") + "').as('x').outE('" + outEdge1 +
			// "').inV.outE('"+ outEdge2+"').inV.except('x').path.fill(list)";

			// Vertex should be unique
			ChronoVertex v = g.getChronoVertices("vlabel", epc).iterator().next();

			VertexQuery vq = v.query().direction(Direction.OUT);

			// Keep list of relationships
			if (relationship != null) {
				String[] relArr = relationship.split(",");
				for (int i = 0; i < relArr.length; i++) {
					relArr[i] = relArr[i].trim();
				}
				vq = vq.labels(relArr);
			}

			Iterator<Edge> edgeIter = vq.edges().iterator();

			JSONObject retObj = new JSONObject();
			while (edgeIter.hasNext()) {
				// Variables
				Edge edge = edgeIter.next();
				String edgeLabel = edge.getLabel();
				long timestamp = 0;

				if (edge.getPropertyKeys().size() == 0)
					continue;

				JSONObject edgeObj;
				if (retObj.isNull(edgeLabel)) {
					edgeObj = new JSONObject();
				} else {
					edgeObj = retObj.getJSONObject(edgeLabel);
				}
				// NeigborVertex
				Vertex neighbor = edge.getVertex(Direction.IN);
				String neighborLabel = neighbor.getProperty("vlabel");

				JSONObject neighborObj;

				if (edgeObj.isNull(neighborLabel)) {
					neighborObj = new JSONObject();
				} else {
					neighborObj = edgeObj.getJSONObject(neighborLabel);
				}

				JSONArray tArr;
				if (neighborObj.isNull("eventTime")) {
					tArr = new JSONArray();
				} else {
					tArr = neighborObj.getJSONArray("eventTime");
				}
				Iterator<String> key = edge.getPropertyKeys().iterator();
				while (key.hasNext()) {
					try {
						String keyString = key.next();
						timestamp = Long.parseLong(keyString);

						if (timestamp == 0)
							continue;
						if ((fromTimeMil != 0 && timestamp <= fromTimeMil)
								|| (toTimeMil != 0 && timestamp >= toTimeMil))
							continue;

						tArr.put(timestamp);

					} catch (NumberFormatException e) {
						// Do Nothing
					}
				}
				if (tArr.length() > 0) {

					/*
					 * May not require this kind of functionality long minMil = 3112582018116L; long
					 * maxMil = 0L; for (int j = 0; j < tArr.length(); j++) { long mil =
					 * tArr.getLong(j); if (minMil > mil) { minMil = mil; } if (maxMil < mil) {
					 * maxMil = mil; } }
					 */
					neighborObj = getNoN(g, request, epc, edgeLabel, neighborLabel, relationship, fromTimeMil,
							toTimeMil);
					neighborObj.put("eventTime", tArr);
					edgeObj.put(neighborLabel, neighborObj);
					retObj.put(edgeLabel, edgeObj);
				}
			}

			// putJsonLDID(retObj, request);
			retObj.put("@id", epc);
			retObj = JSONUtil.putJsonLDRelContext(retObj);
			return retObj.toString(2);

		} catch (NoSuchElementException e) {
			return "No such vertex labelled with {epc} : " + e;
		}
	}

	@SuppressWarnings("null")
	public String getEgoNetwork(ChronoGraph g, HttpServletRequest request, String epc, String relationship,
			long fromTimeMil, long toTimeMil) {
		try {

			// Vertex should be unique
			ChronoVertex v = g.getChronoVertices("vlabel", epc).iterator().next();

			// JSON Object to be returned
			JSONObject retObj = new JSONObject();

			// Keep list of relationships
			Set<String> relList = new HashSet<String>();
			if (relationship != null) {
				String[] relArr = relationship.split(",");
				if (relationship != null) {
					for (int i = 0; i < relArr.length; i++) {
						if (relArr[i] != null)
							relList.add(relArr[i].trim());
					}
				}
			}

			List<ChronoEdge> outE = new ArrayList<ChronoEdge>();
			ScriptEngine engine = null;
			// ScriptEngine engine = new GremlinGroovyScriptEngine();
			engine.put("g", g);
			engine.put("list", outE);
			try {
				engine.eval("g.V('vlabel','" + v.getProperty("vlabel") + "').outE.fill(list)");
				for (int i = 0; i < outE.size(); i++) {
					if (outE.get(i) == null)
						continue;
					ChronoEdge edge = outE.get(i);
					String edgeLabel = edge.getLabel();

					// Relationship filter
					if (relList.size() != 0 && !relList.contains(edgeLabel))
						continue;

					// Time filter
					long timestamp = 0;
					if (edge.getPropertyKeys().size() == 0) {
						continue;
					}
					JSONObject edgeObj;
					if (retObj.isNull(edgeLabel)) {
						edgeObj = new JSONObject();
					} else {
						edgeObj = retObj.getJSONObject(edgeLabel);
					}

					ChronoVertex neighbor = edge.getChronoVertex(Direction.IN);
					String neighborLabel = neighbor.getProperty("vlabel");

					JSONObject neighborObj;

					if (edgeObj.isNull(neighborLabel)) {
						neighborObj = new JSONObject();
					} else {
						neighborObj = edgeObj.getJSONObject(neighborLabel);
					}

					JSONArray tArr;
					if (neighborObj.isNull("eventTime")) {
						tArr = new JSONArray();
					} else {
						tArr = neighborObj.getJSONArray("eventTime");
					}
					Iterator<String> key = edge.getPropertyKeys().iterator();
					while (key.hasNext()) {
						try {
							String keyString = key.next();
							timestamp = Long.parseLong(keyString);

							if (timestamp == 0)
								continue;
							if ((fromTimeMil != 0 && timestamp <= fromTimeMil)
									|| (toTimeMil != 0 && timestamp >= toTimeMil))
								continue;

							tArr.put(timestamp);

						} catch (NumberFormatException e) {
							// Do Nothing
						}
					}
					if (tArr.length() > 0) {
						neighborObj.put("@id", neighborLabel);
						neighborObj.put("eventTime", tArr);
						edgeObj.put(neighborLabel, neighborObj);
						retObj.put(edgeLabel, edgeObj);
					}
				}
			} catch (ScriptException e1) {
				e1.printStackTrace();
			}

			// putJsonLDID(retObj, request);
			retObj.put("@id", epc);
			retObj = JSONUtil.putJsonLDRelContext(retObj);
			return retObj.toString(2);

		} catch (NoSuchElementException e) {
			return "No such vertex labelled with {epc} : " + e;
		}
	}

	public String getRelationship(ChronoGraph g, String epc) {
		try {
			Set<String> edgeLabelSet = new HashSet<String>();
			JSONArray labelArr = new JSONArray();
			// Vertex should be unique
			ChronoVertex v = g.getChronoVertices("vlabel", epc).iterator().next();
			Iterator<ChronoEdge> edgeIter = v.getChronoEdges(Direction.BOTH, null).iterator();
			while (edgeIter.hasNext()) {
				ChronoEdge edge = edgeIter.next();
				edgeLabelSet.add(edge.getLabel());
			}
			Iterator<String> edgeLabelIter = edgeLabelSet.iterator();
			while (edgeLabelIter.hasNext()) {
				labelArr.put(edgeLabelIter.next().toString());
			}
			return labelArr.toString(2);
		} catch (NoSuchElementException e) {
			return "No such vertex labelled with {epc} : " + e;
		}
	}

	public String getVertexAttributesAsJsonLD(ChronoGraph g, HttpServletRequest request, String epc, long fromTimeMil,
			long toTimeMil, String orderByTime, String limit) {

		try {

			JSONObject retObj = new JSONObject();

			// Vertex should be unique
			ChronoVertex v = g.getChronoVertex(epc);
			BsonDocument staticProp = v.getProperties();

			// Master Data
			if (staticProp != null) {
				staticProp.remove("_type");
				staticProp.remove("_id");
				JSONObject masterObj = new JSONObject(staticProp.toJson());
				retObj.put("attributeList", masterObj);
			}

			HashMap<Long, AC> range = new HashMap<Long, AC>();
			range.put(fromTimeMil, AC.$gte);
			range.put(toTimeMil, AC.$lte);

			TreeSet<Long> timestamps = v.getTimestamps(range);

			Iterator<Long> timestampIter = null;
			// Order
			if (orderByTime == null || orderByTime.equals("desc")) {
				timestampIter = timestamps.descendingIterator();
			} else {
				timestampIter = timestamps.iterator();
			}

			// Limit
			int limitInt = Integer.MAX_VALUE;
			if (limit != null) {
				limitInt = Integer.parseInt(limit);
			}

			int cnt = 0;
			while (timestampIter.hasNext()) {
				if (cnt >= limitInt)
					break;
				Long t = timestampIter.next();

				BsonDocument tProp = v.getTimestampProperties(t);
				tProp.remove("_vertex");
				tProp.remove("_t");
				tProp.remove("_type");
				tProp.remove("_id");
				tProp.remove("recordTime");

				retObj.put(t.toString(), new JSONObject(tProp.toJson()));
				cnt++;
			}

			// PUT JSON-LD @id field
			// putJsonLDID(retObj, request);
			retObj.put("@id", epc);

			// PUT JSON-LD @context field
			// retObj = JSONUtil.putJsonLDContext(retObj, context);

			return retObj.toString(2);
		} catch (NoSuchElementException e) {
			return "No such vertex labelled with {epc} : " + e;
		} catch (NumberFormatException e) {
			return "Parse Error(Integer) : " + e;
		}
	}

	@RequestMapping(value = "/Resource/{epc}/{relationships}", method = RequestMethod.GET)
	@ResponseBody
	public String getNeighborNodes(@PathVariable String epc, @PathVariable String relationships) {
		return epc + " : " + relationships;
	}
}
