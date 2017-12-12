package org.oliot.epcis.service.query;

import java.util.ArrayList;
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

import org.bson.BsonArray;
import org.bson.BsonDocument;
import org.bson.BsonString;
import org.json.JSONArray;
import org.json.JSONObject;
import org.lilliput.chronograph.common.TemporalType;
import org.lilliput.chronograph.common.Tokens.AC;
import org.lilliput.chronograph.common.Tokens.Position;
import org.lilliput.chronograph.persistent.ChronoEdge;
import org.lilliput.chronograph.persistent.ChronoGraph;
import org.lilliput.chronograph.persistent.ChronoVertex;
import org.lilliput.chronograph.persistent.VertexEvent;
import org.lilliput.chronograph.persistent.recipe.PersistentBreadthFirstSearch;
import org.lilliput.chronograph.persistent.recipe.PersistentBreadthFirstSearchEmulation;
import org.oliot.epcis.configuration.Configuration;
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

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.VertexQuery;
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

	/**
	 * JSONArray
	 * 
	 * @return a list of nodes in graph store
	 */
	@RequestMapping(value = "/Resources", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<?> getAllVertices() {

		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "application/json; charset=utf-8");

		JSONArray jarr = new JSONArray();
		Iterator<ChronoVertex> vi = Configuration.g.getChronoVertices().iterator();
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
		Configuration.g.getVertexCollection().drop();
		Configuration.g.getEdgeCollection().drop();
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

		ChronoGraph g = Configuration.g;

		PersistentBreadthFirstSearch tBFS = new PersistentBreadthFirstSearch();
		Map pathMap = new HashMap();
		if (order.equals("forward"))
			pathMap = tBFS.compute(g, g.getChronoVertex(epc).setTimestamp(startTimeMil), transforms,
					TemporalType.TIMESTAMP, AC.$gte, null, null, null, null, null, null, Position.first, order);
		else
			pathMap = tBFS.compute(g, g.getChronoVertex(epc).setTimestamp(startTimeMil), transforms,
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

		ChronoGraph g = Configuration.g;

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

		ChronoGraph g = Configuration.g;

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
