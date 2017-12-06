package org.oliot.epcis.service.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import javax.script.ScriptEngine;
import javax.script.ScriptException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;
import org.lilliput.chronograph.persistent.ChronoEdge;
import org.lilliput.chronograph.persistent.ChronoGraph;
import org.lilliput.chronograph.persistent.ChronoVertex;
import org.oliot.epcis.configuration.Configuration;
import org.oliot.epcis.security.OAuthUtil;
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

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.restfb.Connection;
import com.restfb.FacebookClient;
import com.restfb.types.User;
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
	 * FOR DEBUGGING
	 * 
	 * @return a list of nodes in graph store
	 */
	@RequestMapping(value = "/Resources", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<?> getAllVertices() {

		ChronoGraph g = new ChronoGraph(Configuration.backend_ip, Configuration.backend_port,
				Configuration.databaseName);
		JSONArray jarr = new JSONArray();

		try {

			Iterator<ChronoVertex> vi = g.getChronoVertices().iterator();
			while (vi.hasNext()) {
				jarr.put(vi.next().toString());
			}

		} finally {
			g.shutdown();
		}
		return new ResponseEntity<>(new String("[Lilliput] : Debugging method\n" + jarr.toString(1)), HttpStatus.OK);
	}

	/**
	 * JSONArray
	 * 
	 * @return a list of nodes in graph store
	 */
	@RequestMapping(value = "/Resource", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<?> getExistingNodes() {

		ChronoGraph g = new ChronoGraph(Configuration.backend_ip, Configuration.backend_port,
				Configuration.databaseName);
		String ret = null;
		try {
			// ret = GraphBuilderUtil.getAllVerticesAsJSONArrayString(g);
		} finally {
			g.shutdown();
		}
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "application/json; charset=utf-8");
		return new ResponseEntity<>(ret, responseHeaders, HttpStatus.OK);
	}

	/**
	 * Text
	 * 
	 * @return remove All vertices for developer
	 */
	@RequestMapping(value = "/Resource", method = RequestMethod.DELETE)
	@ResponseBody
	public ResponseEntity<?> deleteAllExistingNodes() {

		ChronoGraph g = new ChronoGraph(Configuration.backend_ip, Configuration.backend_port,
				Configuration.databaseName);
		try {
			// GraphBuilderUtil.removeAllVertices(g);
		} finally {
			g.shutdown();
		}
		return new ResponseEntity<>("[Lilliput] : All Vertices Removed\n", HttpStatus.OK);
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
	@RequestMapping(value = "/Resource/{epc}", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<?> getResource(@PathVariable String epc, @RequestParam(required = false) String scope,
			@RequestParam(required = false) String fromTime, @RequestParam(required = false) String toTime,
			@RequestParam(required = false) String orderByTime, @RequestParam(required = false) String limit,
			@RequestParam(required = false) String relationship, @RequestParam(required = false) String fid,
			@RequestParam(required = false) String accessToken, HttpServletRequest request) {

		// Access Control is not mandatory
		// However, if fid and accessToken provided, more information provided
		FacebookClient fc = null;
		List<String> friendList = null;
		if (fid != null) {
			// Check accessToken
			fc = OAuthUtil.isValidatedFacebookClient(accessToken, fid);
			if (fc == null) {
				return new ResponseEntity<>(new String("Invalid AccessToken"), HttpStatus.BAD_REQUEST);
			}
			friendList = new ArrayList<String>();

			Connection<User> friendConnection = fc.fetchConnection("me/friends", User.class);
			for (List<User> friends : friendConnection) {
				for (User friend : friends) {
					friendList.add(friend.getId());
				}
			}
		}

		if (scope == null)
			scope = "resource";

		// Time processing
		long fromTimeMil = 0;
		long toTimeMil = 0;
		fromTimeMil = TimeUtil.getTimeMil(fromTime);
		toTimeMil = TimeUtil.getTimeMil(toTime);

		ChronoGraph g = new ChronoGraph(Configuration.backend_ip, Configuration.backend_port,
				Configuration.databaseName);
		try {

			HttpHeaders responseHeaders = new HttpHeaders();
			responseHeaders.add("Content-Type", "application/json; charset=utf-8");

			if (scope.trim().equals("resource")) {
				String ret = getVertexAttributesAsJsonLD(g, request, epc, fromTimeMil, toTimeMil, orderByTime, limit,
						fid, friendList);
				g.shutdown();
				return new ResponseEntity<>(ret, responseHeaders, HttpStatus.OK);
			} else if (scope.trim().equals("relationship")) {
				String ret = getRelationship(g, epc);
				g.shutdown();
				return new ResponseEntity<>(ret, responseHeaders, HttpStatus.OK);
			} else if (scope.trim().equals("ego")) {

				String ret = getEgoNetwork(g, request, epc, relationship, fromTimeMil, toTimeMil, fc, fid);
				g.shutdown();
				return new ResponseEntity<>(ret, responseHeaders, HttpStatus.OK);

			} else if (scope.trim().equals("sibling")) {
				if (relationship.split(",").length != 2) {
					return new ResponseEntity<>(new String(
							"Format of 'relationship' in sibling network = {edgeLabelToParent,edgeLabelToSibling}"),
							HttpStatus.BAD_REQUEST);
				}
				String ret = getSiblingNetwork(g, request, epc, relationship, fromTimeMil, toTimeMil, fc, fid);
				g.shutdown();
				return new ResponseEntity<>(ret, responseHeaders, HttpStatus.OK);
			} else if (scope.trim().equals("trace")) {
				if (relationship.split(",").length != 1) {
					return new ResponseEntity<>(new String("Format of 'relationship' = edgeLabel"),
							HttpStatus.BAD_REQUEST);
				}
				String ret = getTracePath(g, request, epc, relationship, fromTimeMil, toTimeMil);
				g.shutdown();
				return new ResponseEntity<>(ret, responseHeaders, HttpStatus.OK);
			}
		} finally {
			g.shutdown();
		}

		return new ResponseEntity<>(new String("Format of 'scope' = {resource|relationship|ego|sibling|trace}"),
				HttpStatus.BAD_REQUEST);
	}

	public JSONObject getNoN(ChronoGraph g, HttpServletRequest request, String parent, String parentRel, String epc,
			String relationship, long fromTimeMil, long toTimeMil, FacebookClient fc, String fid) {
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
			long fromTimeMil, long toTimeMil, FacebookClient fc, String fid) {
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
							toTimeMil, fc, fid);
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
			long fromTimeMil, long toTimeMil, FacebookClient fc, String fid) {
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
			long toTimeMil, String orderByTime, String limit, String fid, List<String> friendList) {

		try {

			MongoClient mongo = null;
			mongo = new MongoClient("localhost", 27017);
			MongoDatabase db = mongo.getDatabase("sepcis");

			JSONObject retObj = new JSONObject();

			// Vertex should be unique
			ChronoVertex v = g.getChronoVertices("vlabel", epc).iterator().next();
			String masterdataString = v.getProperty("attributeList");

			// Master Data
			if (masterdataString != null) {
				JSONObject masterObj = new JSONObject(masterdataString);
				retObj.put("attributeList", masterObj);
			}

			String serviceProfileString = v.getProperty("serviceProfile");

			// Service Profile
			if (serviceProfileString != null) {
				JSONArray serviceArr = new JSONArray(serviceProfileString);
				retObj.put("serviceProfile", serviceArr);
			}

			// DBCollection collection = mongoOperation.getCollection(epc);
			MongoCollection<Document> collection = db.getCollection(epc);
			FindIterable<Document> findIterable = collection.find();
			// Order
			if (orderByTime == null || orderByTime.equals("desc")) {
				findIterable.sort(new Document("eventTime", -1));
			} else {
				findIterable.sort(new Document("eventTime", 1));
			}

			// Limit
			int limitInt = 0;
			if (limit != null) {
				limitInt = Integer.parseInt(limit);
				findIterable.limit(limitInt);
			}
			MongoCursor<Document> cursor = findIterable.iterator();
			JSONObject context = new JSONObject();
			while (cursor.hasNext()) {
				Document event = (Document) cursor.next();
				if (event == null)
					continue;

				Object eventTime = event.get("eventTime");
				long eventTimeMil = 0;
				try {
					eventTimeMil = Long.parseLong(eventTime.toString());
				} catch (NumberFormatException e) {
					continue;
				} catch (NullPointerException e) {
					continue;
				}
				if (eventTimeMil == 0)
					continue;
				if ((fromTimeMil != 0) && (fromTimeMil > eventTimeMil))
					continue;
				if ((toTimeMil != 0) && (eventTimeMil > toTimeMil))
					continue;

				Iterator<String> keyIter = event.keySet().iterator();

				while (keyIter.hasNext()) {
					String objKey = keyIter.next().toString();

					if (objKey.equals("extensionList")) {
						Document extObj = (Document) event.get(objKey);
						Iterator<String> extKeyIter = extObj.keySet().iterator();
						JSONObject tempObj = new JSONObject();
						while (extKeyIter.hasNext()) {
							String extKey = extKeyIter.next().toString();
							if (extKey.startsWith("@")) {
								context.put(extKey.substring(1, extKey.length()), extObj.get(extKey).toString());

							} else {
								tempObj.put(extKey, extObj.get(extKey).toString());
							}
						}
						event.put("extensionList", tempObj);
					} else if (objKey.equals("ilmd")) {
						Document ilmdObj = (Document) event.get(objKey);
						Iterator<String> ilmdKeyIter = ilmdObj.keySet().iterator();
						JSONObject tempObj = new JSONObject();
						while (ilmdKeyIter.hasNext()) {
							String ilmdKey = ilmdKeyIter.next().toString();
							if (ilmdKey.startsWith("@")) {
								context.put(ilmdKey.substring(1, ilmdKey.length()), ilmdObj.get(ilmdKey).toString());

							} else {
								tempObj.put(ilmdKey, ilmdObj.get(ilmdKey).toString());
							}
						}
						event.put("ilmd", tempObj);
					}
				}
				event.remove("_id");
				retObj.put(String.valueOf(eventTimeMil), JSONUtil.toJson(event));
			}

			// PUT JSON-LD @id field
			// putJsonLDID(retObj, request);
			retObj.put("@id", epc);

			// PUT JSON-LD @context field
			retObj = JSONUtil.putJsonLDContext(retObj, context);

			mongo.close();
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
