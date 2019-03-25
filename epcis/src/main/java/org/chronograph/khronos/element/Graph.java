package org.chronograph.khronos.element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.bson.BsonArray;
import org.bson.BsonBoolean;
import org.bson.BsonDateTime;
import org.bson.BsonDocument;
import org.bson.BsonDouble;
import org.bson.BsonInt32;
import org.bson.BsonNull;
import org.bson.BsonString;
import org.bson.BsonValue;
import org.bson.Document;
import org.chronograph.khronos.common.Converter;
import org.chronograph.khronos.common.Tokens;

import com.mongodb.Function;
import com.mongodb.MongoBulkWriteException;
import com.mongodb.MongoClient;
import com.mongodb.ReadConcern;
import com.mongodb.ServerAddress;
import com.mongodb.WriteConcern;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.model.UpdateOptions;

/**
 * Copyright (C) 2016-2018 Jaewook Byun
 * 
 * ChronoGraph: A Temporal Graph Management and Traversal Platform
 * 
 * The loop management scheme unlike Gremlin makes this class
 * 
 * @author Jaewook Byun, Assistant Professor, Halla University
 * 
 *         Data Frameworks and Platforms Laboratory (DFPL)
 * 
 *         jaewook.byun@halla.ac.kr, bjw0829@kaist.ac.kr, bjw0829@gmail.com
 * 
 */
public class Graph extends Element {
	private MongoClient mongoClient = null;
	private MongoDatabase mongoDatabase = null;
	private MongoCollection<BsonDocument> edges = null;
	private MongoCollection<BsonDocument> vertices = null;
	private MongoCollection<BsonDocument> edgeEvents = null;
	private MongoCollection<BsonDocument> vertexEvents = null;
	private String id;

	public MongoClient getMongoClient() {
		return mongoClient;
	}

	public void cloneGraph(Graph clonedGraph) {
		Iterator<Vertex> vi = getVertexSet().iterator();
		while (vi.hasNext()) {
			Vertex v = vi.next();
			if (v.getProperties() != null)
				clonedGraph.getVertex(v.getID()).setProperties(v.getProperties().asDocument());
		}
		Iterator<Edge> ei = getEdgeSet().iterator();
		while (ei.hasNext()) {
			Edge e = ei.next();
			Edge ne = clonedGraph.addEdge(e.getOutVertexID(), e.getLabel(), e.getInVertexID());
			if (e.getProperties() != null)
				ne.setProperties(e.getProperties().asDocument());
		}
		Iterator<VertexEvent> vei = getVertexEventSet().iterator();
		while (vei.hasNext()) {
			VertexEvent ve = vei.next();
			if (ve.getProperties() != null)
				clonedGraph.addVertexEventProperties(ve.getVertexID(), ve.getTimestamp(), ve.getProperties());
			else
				clonedGraph.addVertexEventProperties(ve.getVertexID(), ve.getTimestamp(), null);
		}
		Iterator<EdgeEvent> eei = getEdgeEventSet().iterator();
		while (eei.hasNext()) {
			EdgeEvent ee = eei.next();
			clonedGraph.addEdgeEvent(ee.getOutVertex(), ee.getLabel(), ee.getInVertex(), ee.getTimestamp());
		}
	}

	//////////////////////////////////// Graph/////////////////////////////////////////////

	/**
	 * Create a Khronos graph with the default setting
	 * 
	 * Connect to MongoDB at localhost:27017 and use 'chronograph' database
	 */
	public Graph() {
		this(new ServerAddress("localhost", 27017), "chronograph");
	}

	public Graph(String graphID) {
		this(new ServerAddress("localhost", 27017), graphID);
	}

	/**
	 * Create a Khronos graph
	 * 
	 * Connect to MongoDB at serverAddress and use graphID database
	 * 
	 * @param serverAddress com.mongodb.ServerAddress (e.g., new
	 *                      ServerAddress("localhost", 27017))
	 * @param graphID       database will be used
	 */
	public Graph(ServerAddress serverAddress, String graphID) {
		mongoClient = new MongoClient(serverAddress);
		mongoDatabase = mongoClient.getDatabase(graphID);
		edges = mongoDatabase.getCollection(Tokens.EDGE_COLLECTION, BsonDocument.class)
				.withWriteConcern(WriteConcern.UNACKNOWLEDGED).withReadConcern(ReadConcern.MAJORITY);
		vertices = mongoDatabase.getCollection(Tokens.VERTEX_COLLECTION, BsonDocument.class)
				.withWriteConcern(WriteConcern.UNACKNOWLEDGED).withReadConcern(ReadConcern.MAJORITY);
		edgeEvents = mongoDatabase.getCollection(Tokens.EDGE_EVENT_COLLECTION, BsonDocument.class)
				.withWriteConcern(WriteConcern.UNACKNOWLEDGED).withReadConcern(ReadConcern.MAJORITY);
		vertexEvents = mongoDatabase.getCollection(Tokens.VERTEX_EVENT_COLLECTION, BsonDocument.class)
				.withWriteConcern(WriteConcern.UNACKNOWLEDGED).withReadConcern(ReadConcern.MAJORITY);
		createBasicIndex();
		id = graphID;
	}

	/**
	 * @return graphID Mongo database (Internal or Advanced Usage)
	 */
	public MongoDatabase getMongoDatabase() {
		return mongoDatabase;
	}

	/**
	 * @return an identifier of graph
	 */
	public String getID() {
		return id;
	}

	/**
	 * A shutdown function is required to properly close the graph. This is
	 * important for implementations that utilize disk-based serializations.
	 * 
	 * Close MongoDB Client
	 */
	public void shutdown() {
		this.mongoClient.close();
	}

	/**
	 * @return readable string
	 */
	public String toString() {
		return "graph(" + id + ")";
	}

	/**
	 * Drop database
	 */
	public void drop() {
		mongoDatabase.drop();
	}

	/**
	 * Drop four collections
	 */
	public void clear() {
		edges.drop();
		vertices.drop();
		edgeEvents.drop();
		vertexEvents.drop();
	}

	//////////////////////////////////// Vertex/////////////////////////////////////////////

	/**
	 * Return 'vertices' collection (Internal or Advanced Usage)
	 * 
	 * @return 'vertices' collection
	 */
	public MongoCollection<BsonDocument> getVertexCollection() {
		return vertices;
	}

	/**
	 * Return the vertex referenced by the provided object identifier. If no vertex
	 * is referenced by that identifier, then return null (Not happen in
	 * ChronoGraph).
	 *
	 * @param id the identifier of the vertex to retrieved from the graph v(id)
	 * @return the vertex referenced by the provided identifier or null when no such
	 *         vertex exists
	 */
	public Vertex getVertex(BsonString vertexID) {
		return new Vertex(this, vertexID);
	}

	/**
	 * Return the vertex referenced by the provided object identifier only if the
	 * vertex has at least one property.
	 * 
	 * (Interaction with DB)
	 * 
	 * @param vertexID
	 * @return
	 */
	public Vertex pickVertex(BsonString vertexID) {
		BsonDocument doc = vertices.find(Converter.makeVertexDocument(null, vertexID)).first();
		if (doc != null)
			return new Vertex(this, vertexID);
		else
			return null;
	}

	/**
	 * Remove the provided vertex from the graph. Upon removing the vertex, all the
	 * edges by which the vertex is connected must be removed as well.
	 * 
	 * @param vertex the vertex to remove from the graph
	 */
	public void removeVertex(Vertex vertex) {
		BsonDocument filter = new BsonDocument();
		BsonArray or = new BsonArray();
		BsonDocument outV = new BsonDocument(Tokens.OUT_VERTEX, vertex.getID());
		BsonDocument inV = new BsonDocument(Tokens.IN_VERTEX, vertex.getID());
		or.add(outV);
		or.add(inV);
		filter.put(Tokens.Conjunction.$or.name(), or);
		edges.deleteMany(filter);

		vertices.deleteOne(new BsonDocument(Tokens.ID, vertex.getID()));
	}

	/**
	 * Return an iterable to all the vertices in the graph. If this is not possible
	 * for the implementation, then an UnsupportedOperationException can be thrown.
	 *
	 * @return an iterable reference to all vertices in the graph
	 */
	public Set<Vertex> getVertexSet() {
		HashSet<BsonString> idSet = new HashSet<BsonString>();
		Function<BsonDocument, BsonString> mapper = new Function<BsonDocument, BsonString>() {
			@Override
			public BsonString apply(BsonDocument val) {
				return val.getString(Tokens.ID);
			}

		};
		HashSet<BsonString> outV = new HashSet<BsonString>();
		ArrayList<BsonDocument> outVQuery = new ArrayList<BsonDocument>();
		outVQuery.add(new BsonDocument("$group", new BsonDocument(Tokens.ID, new BsonString("$" + Tokens.OUT_VERTEX))));
		edges.aggregate(outVQuery).map(mapper).into(outV);
		idSet.addAll(outV);

		HashSet<BsonString> inV = new HashSet<BsonString>();
		ArrayList<BsonDocument> inVQuery = new ArrayList<BsonDocument>();
		inVQuery.add(new BsonDocument("$group", new BsonDocument(Tokens.ID, new BsonString("$" + Tokens.IN_VERTEX))));
		edges.aggregate(inVQuery).map(mapper).into(inV);
		idSet.addAll(inV);

		MongoCursor<BsonDocument> vi = vertices.find().projection(Tokens.PRJ_ONLY_ID).iterator();
		while (vi.hasNext()) {
			BsonDocument d = vi.next();
			idSet.add(d.getString(Tokens.ID));
		}

		outV = new HashSet<BsonString>();
		outVQuery = new ArrayList<BsonDocument>();
		outVQuery.add(new BsonDocument("$group", new BsonDocument(Tokens.ID, new BsonString("$" + Tokens.OUT_VERTEX))));
		edgeEvents.aggregate(outVQuery).allowDiskUse(true).map(mapper).into(outV);
		idSet.addAll(outV);

		inV = new HashSet<BsonString>();
		inVQuery = new ArrayList<BsonDocument>();
		inVQuery.add(new BsonDocument("$group", new BsonDocument(Tokens.ID, new BsonString("$" + Tokens.IN_VERTEX))));
		edgeEvents.aggregate(inVQuery).map(mapper).into(inV);
		idSet.addAll(inV);

		vi = vertexEvents.find().projection(Tokens.PRJ_ONLY_VERTEX).iterator();
		while (vi.hasNext()) {
			BsonDocument d = vi.next();
			idSet.add(d.getString(Tokens.VERTEX));
		}

		return idSet.parallelStream().map(s -> new Vertex(this, s)).collect(Collectors.toSet());
	}

	/**
	 * Return an iterable to all the vertices in the graph that have a particular
	 * key/value property. If this is not possible for the implementation, then an
	 * UnsupportedOperationException can be thrown. The graph implementation should
	 * use indexing structures to make this efficient else a full vertex-filter scan
	 * is required.
	 * 
	 * @param key   the key of vertex
	 * @param value the value of the vertex
	 * @return a set of vertices with provided key and value
	 */
	public Set<Vertex> getVertexSet(String key, Object value) {
		HashSet<Vertex> ret = new HashSet<Vertex>();

		MongoCursor<BsonDocument> cursor = vertices.find(new BsonDocument(key, (BsonValue) value))
				.projection(Tokens.PRJ_ONLY_ID).iterator();

		while (cursor.hasNext()) {
			BsonDocument v = cursor.next();
			ret.add(new Vertex(this, v.getString(Tokens.ID)));
		}
		return ret;
	}

	public Set<VertexEvent> getVertexEventSet() {
		HashSet<VertexEvent> ret = new HashSet<VertexEvent>();

		MongoCursor<BsonDocument> cursor = vertexEvents.find().iterator();

		while (cursor.hasNext()) {
			BsonDocument doc = cursor.next();
			VertexEvent ve = new VertexEvent(doc.getDateTime(Tokens.TIMESTAMP), doc.getString(Tokens.VERTEX), this);
			ret.add(ve);
		}
		return ret;
	}

	/**
	 * Geospatial query
	 * 
	 * @param key    should be indexed by 2dsphere
	 *               db.vertices.createIndex({"urn:oliot:ubv:mda:gps" : "2dsphere"})
	 * @param lon
	 * @param lat
	 * @param radius in metres db.vertices.find({ "urn:oliot:ubv:mda:gps" : { $near
	 *               : { $geometry: { type: "Point", coordinates: [ -1.1673,52.93]},
	 *               $maxDistance: 50000}}})
	 * 
	 * @return
	 */
	public HashSet<Vertex> getVertexSet(String key, double lon, double lat, double radius) {
		HashSet<Vertex> ret = new HashSet<Vertex>();

		BsonArray coordinates = new BsonArray();
		coordinates.add(new BsonDouble(lon));
		coordinates.add(new BsonDouble(lat));
		BsonDocument geometry = new BsonDocument();
		geometry.put("type", new BsonString("Point"));
		geometry.put("coordinates", coordinates);
		BsonDocument near = new BsonDocument();
		near.put("$geometry", geometry);
		near.put("$maxDistance", new BsonDouble(radius));
		BsonDocument geoquery = new BsonDocument();
		geoquery.put("$near", near);
		BsonDocument queryDoc = new BsonDocument();
		queryDoc.put(key, geoquery);

		MongoCursor<BsonDocument> cursor = vertices.find(queryDoc).projection(Tokens.PRJ_ONLY_ID).iterator();

		while (cursor.hasNext()) {
			BsonDocument v = cursor.next();
			ret.add(new Vertex(this, v.getString(Tokens.ID)));
		}
		return ret;
	}

	//////////////////////////////////// Edge/////////////////////////////////////////////

	/**
	 * Return 'edges' collection (Internal or Advanced Usage)
	 * 
	 * @return 'edges' collection
	 */
	public MongoCollection<BsonDocument> getEdgeCollection() {
		return edges;
	}

	/**
	 * Add an edge to the graph. The added edges requires a recommended identifier,
	 * a tail vertex, an head vertex, and a label. Like adding a vertex, the
	 * provided object identifier may be ignored by the implementation.
	 * 
	 * @param outVertexID: the vertex on the tail of the edge
	 * @param inVertexID: the vertex on the head of the edge
	 * @param label: the label associated with the edge
	 * @return the newly created edge
	 */
	public Edge addEdge(BsonString outVertexID, BsonString label, BsonString inVertexID) {

		BsonDocument doc = edges
				.find(new BsonDocument(Tokens.OUT_VERTEX, outVertexID).append(Tokens.LABEL, label)
						.append(Tokens.IN_VERTEX, inVertexID))
				.projection(new BsonDocument(Tokens.ID, new BsonInt32(0))).first();
		if (doc == null) {
			BsonDocument e = new BsonDocument();
			e.put(Tokens.OUT_VERTEX, outVertexID);
			e.put(Tokens.IN_VERTEX, inVertexID);
			e.put(Tokens.LABEL, label);
			edges.insertOne(e);
		}
		return new Edge(this, outVertexID, label, inVertexID);
	}

	/**
	 * Insert bulk edges
	 * 
	 * @see: outV|label|inV is unique in ChronoGraph. this operation does not permit
	 *       duplicated edges
	 * @param edges use with Converter.getBsonDocumentEdge
	 */
	public void addEdges(List<BsonDocument> edgeArray) {
		while (true) {
			try {
				edges.insertMany(edgeArray);
				return;
			} catch (MongoBulkWriteException e) {
				if (e.getCode() == -3) {
					int cnt = e.getWriteResult().getInsertedCount();
					edgeArray = edgeArray.subList(cnt + 1, edgeArray.size());
				} else
					throw e;
			}
		}
	}

	/**
	 * Add an edge to the graph. The added edges requires a recommended identifier,
	 * a tail vertex, an head vertex, and a label. Like adding a vertex, the
	 * provided object identifier may be ignored by the implementation.
	 * 
	 * @param id: outVertexID|label|inVertexID
	 */
	public Edge getEdge(BsonString outVertexID, BsonString edgeLabel, BsonString inVertexID) {

		BsonDocument edgeDoc = edges
				.find(new BsonDocument(Tokens.OUT_VERTEX, outVertexID).append(Tokens.LABEL, edgeLabel)
						.append(Tokens.IN_VERTEX, inVertexID))
				.projection(new BsonDocument(Tokens.ID, new BsonInt32(0))).first();
		if (edgeDoc == null)
			return null;
		else
			return new Edge(this, edgeDoc.getString(Tokens.OUT_VERTEX), edgeDoc.getString(Tokens.LABEL),
					edgeDoc.getString(Tokens.IN_VERTEX));
	}

	/**
	 * Remove the provided edge from the graph.
	 *
	 * @param edge the edge to remove from the graph
	 */
	public void removeEdge(BsonString outVertex, BsonString label, BsonString inVertex) {
		BsonDocument filter = new BsonDocument(Tokens.OUT_VERTEX, outVertex).append(Tokens.LABEL, label)
				.append(Tokens.IN_VERTEX, inVertex);
		edges.deleteOne(filter);
	}

	/**
	 * Remove the provided edge from the graph.
	 *
	 * @param edge the edge to remove from the graph
	 */
	public void removeEdge(Edge edge) {
		BsonDocument filter = new BsonDocument(Tokens.OUT_VERTEX, edge.getOutVertexID())
				.append(Tokens.LABEL, edge.getLabel()).append(Tokens.IN_VERTEX, edge.getInVertexID());
		edges.deleteOne(filter);
	}

	/**
	 * Return an iterable to all the edges in the graph. If this is not possible for
	 * the implementation, then an UnsupportedOperationException can be thrown.
	 *
	 * @return an iterable reference to all edges in the graph
	 */
	public Set<Edge> getEdgeSet() {
		HashSet<Edge> ret = new HashSet<Edge>();
		MongoCursor<BsonDocument> cursor = edges.find().projection(Tokens.PRJ_ONLY_OUTV_LABEL_INV).iterator();
		while (cursor.hasNext()) {
			BsonDocument e = cursor.next();
			ret.add(new Edge(this, e.getString(Tokens.OUT_VERTEX), e.getString(Tokens.LABEL),
					e.getString(Tokens.IN_VERTEX)));
		}
		return ret;
	}

	/**
	 * Return an iterable to all the edges in the graph that have a particular
	 * key/value property. If this is not possible for the implementation, then an
	 * UnsupportedOperationException can be thrown. The graph implementation should
	 * use indexing structures to make this efficient else a full edge-filter scan
	 * is required.
	 * 
	 * @param key   the key of the edge
	 * @param value the value of the edge
	 * @return an iterable of edges with provided key and value
	 */
	public Set<Edge> getEdgeSet(String key, Object value) {
		HashSet<Edge> ret = new HashSet<Edge>();
		MongoCursor<BsonDocument> cursor = edges.find(new BsonDocument(key, (BsonValue) value))
				.projection(Tokens.PRJ_ONLY_OUTV_LABEL_INV).iterator();

		while (cursor.hasNext()) {
			BsonDocument e = cursor.next();
			ret.add(new Edge(this, e.getString(Tokens.OUT_VERTEX), e.getString(Tokens.LABEL),
					e.getString(Tokens.IN_VERTEX)));
		}
		return ret;
	}

	/**
	 * Return an iterable to all the edges in the graph. If this is not possible for
	 * the implementation, then an UnsupportedOperationException can be thrown.
	 * 
	 * @param labels
	 * @return iterable Edge Identifiers having the given label sets
	 */
	public HashSet<Edge> getEdges(BsonString label) {
		HashSet<Edge> edgeSet = new HashSet<Edge>();
		BsonDocument filter = new BsonDocument();
		filter.put(Tokens.LABEL, label);
		Iterator<BsonDocument> cursor = edges.find(filter).projection(Tokens.PRJ_ONLY_OUTV_LABEL_INV).iterator();
		while (cursor.hasNext()) {
			BsonDocument e = cursor.next();
			edgeSet.add(new Edge(this, e.getString(Tokens.OUT_VERTEX), e.getString(Tokens.LABEL),
					e.getString(Tokens.IN_VERTEX)));
		}
		return edgeSet;
	}

	//////////////////////////////////// Vertex
	//////////////////////////////////// Event/////////////////////////////////////////////

	public MongoCollection<BsonDocument> getEdgeEventCollection() {
		return edgeEvents;
	}

	public MongoCollection<BsonDocument> getVertexEventCollection() {
		return vertexEvents;
	}

	public VertexEvent getVertexEvent(BsonString vertexID, BsonDateTime timestamp) {
		return new VertexEvent(timestamp, vertexID, this);
	}

	public Set<EdgeEvent> getEdgeEventSet() {

		MongoCursor<BsonDocument> cursor = edgeEvents.find().iterator();

		HashSet<EdgeEvent> eeSet = new HashSet<EdgeEvent>();
		while (cursor.hasNext()) {
			BsonDocument doc = cursor.next();
			eeSet.add(new EdgeEvent(doc.getDateTime(Tokens.TIMESTAMP), doc.getString(Tokens.OUT_VERTEX),
					doc.getString(Tokens.LABEL), doc.getString(Tokens.IN_VERTEX), this));
		}

		return eeSet;
	}

	public Set<EdgeEvent> getEdgeEventSet(BsonString outVertexID, BsonString label, BsonString inVertexID,
			BsonDateTime t, Tokens.AC tt) {

		BsonDocument base = Converter.makeEdgeDocument(null, outVertexID, label, inVertexID);
		base = Converter.addTimeComparison(base, t, tt);
		TreeSet<EdgeEvent> resultSet = new TreeSet<EdgeEvent>();

		MongoCursor<BsonDocument> cursor = edgeEvents.find(base).projection(Tokens.PRJ_ONLY_TIMESTAMP).iterator();
		while (cursor.hasNext()) {
			BsonDocument doc = cursor.next();
			EdgeEvent ee = new EdgeEvent(doc.getDateTime(Tokens.TIMESTAMP), outVertexID, label, inVertexID, this);
			resultSet.add(ee);
		}

		return resultSet;
	}

	public EdgeEvent getEdgeEvent(BsonDateTime timestamp, BsonString outVertex, BsonString label, BsonString inVertex,
			Graph graph) {
		return new EdgeEvent(timestamp, outVertex, label, inVertex, graph);
	}

	public void removeEdgeEvent(BsonString outV, BsonString label, BsonString inV, BsonDateTime timestamp) {
		BsonDocument filter = new BsonDocument(Tokens.OUT_VERTEX, outV).append(Tokens.LABEL, label)
				.append(Tokens.IN_VERTEX, inV).append(Tokens.TIMESTAMP, timestamp);
		edgeEvents.deleteOne(filter);
		// edgeEvents.updateOne(filter, new BsonDocument("$set", new BsonDocument("_tbd", new BsonBoolean(true))));
	}
	
	public void pseudoRemoveEdgeEvent(BsonString outV, BsonString label, BsonString inV, BsonDateTime timestamp) {
		BsonDocument filter = new BsonDocument(Tokens.OUT_VERTEX, outV).append(Tokens.LABEL, label)
				.append(Tokens.IN_VERTEX, inV).append(Tokens.TIMESTAMP, timestamp);
		// edgeEvents.deleteOne(filter);
		edgeEvents.updateOne(filter, new BsonDocument("$set", new BsonDocument("_tbd", new BsonBoolean(true))));
	}


	/**
	 * Extension: only support ChronoVertex add temporal property into the vertex
	 * for same timestamp & key to the existing property, replace the value
	 * 
	 * @param id: unique vertex id
	 * @param timestamp: unix epoch
	 * @param key: property key for the given timestamp
	 * @param value: property value for the given timestamp
	 * @return updated Vertex
	 */
	public VertexEvent addVertexEventProperty(BsonString vertexID, BsonDateTime timestamp, String key,
			BsonValue value) {
		vertexEvents.updateOne(new BsonDocument(Tokens.VERTEX, vertexID).append(Tokens.TIMESTAMP, timestamp),
				new BsonDocument("$set", new BsonDocument(key, value)), new UpdateOptions().upsert(true));
		return new VertexEvent(timestamp, vertexID, this);
	}

	public VertexEvent addVertexEventProperties(BsonString vertexID, BsonDateTime timestamp, BsonDocument properties) {
		BsonDocument filter = Converter.makeVertexEventDocument(null, timestamp, vertexID);
		BsonDocument replacement = Converter.makeVertexEventDocument(null, timestamp, vertexID);
		replacement.putAll(properties);
		vertexEvents.replaceOne(filter, replacement, new ReplaceOptions().upsert(true));
		return new VertexEvent(timestamp, vertexID, this);
	}

	/**
	 * Extension: only support ChronoEdge, add temporal property into the edge for
	 * same timestamp & key to the existing property, replace the value
	 * 
	 * @param outVertexID: unique out vertex id
	 * @param inVertexID: unique in vertex id
	 * @param label: edge label
	 * @param timestamp: unix epoch
	 * @param key: property key for the given timestamp
	 * @param value: property value for the given timestamp
	 * @return updated edge
	 */
	public EdgeEvent addEdgeEvent(BsonString outVertexID, BsonString label, BsonString inVertexID,
			BsonDateTime timestamp) {

		BsonDocument filter = Converter.makeEdgeEventDocument(null, timestamp, outVertexID, label, inVertexID);
		BsonDocument edgeEvent = edgeEvents.find(filter).first();
		if (edgeEvent == null)
			edgeEvents.insertOne(filter);
		return new EdgeEvent(timestamp, outVertexID, label, inVertexID, this);
	}

	//////////////////////////////////// Index/////////////////////////////////////////////

	/**
	 * Remove an automatic indexing structure associated with indexing provided key
	 * for element class.
	 *
	 * @param key          the key to drop the index for
	 * @param elementClass the element class that the index is for
	 * @param              <T> the element class specification
	 */
	public <T> void dropKeyIndex(String key, Class<T> elementClass) {
		if (elementClass.equals(Vertex.class)) {
			vertices.dropIndex(key);
		} else if (elementClass.equals(Edge.class)) {
			edges.dropIndex(key);
		}
	}

	/**
	 * Create an automatic indexing structure for indexing provided key for element
	 * class.
	 *
	 * @param key             the key to create the index for
	 * 
	 *                        (auto-generated) _[key]_ or (_[key]_(-1|1))*
	 * 
	 * @param elementClass    the element class that the index is for
	 * @param indexParameters a collection of parameters for the underlying index
	 *                        implementation
	 * @param                 <T> the element class specification
	 */
	public <T> void createKeyIndex(String key, Class<T> elementClass, HashMap<String, Boolean> indexParams) {
		if (elementClass.equals(Vertex.class)) {
			BsonDocument index = Converter.makeIndexParamterDocument(indexParams);
			vertices.createIndex(index);
		} else if (elementClass.equals(Edge.class)) {
			BsonDocument index = Converter.makeIndexParamterDocument(indexParams);
			edges.createIndex(index);
		}
	}

	/**
	 * Return all the index keys associated with a particular element class.
	 *
	 * @param elementClass: class that the index is for
	 *        (ChronoVertex|ChronoEdge|VertexEvent|EdgeEvent) the element
	 * 
	 * @param <T> the element class specification
	 * @return the indexed keys as a Set in [database].[collection]
	 * 
	 *         _[key]_ or (_[key]_(-1|1))*
	 */
	public <T> Set<String> getIndexedKeys(Class<T> elementClass) {
		Set<String> indexKeys = new HashSet<String>();
		if (elementClass.equals(Vertex.class)) {
			Iterator<BsonDocument> bi = vertices.listIndexes(BsonDocument.class).iterator();
			while (bi.hasNext()) {
				BsonDocument b = bi.next();
				indexKeys.add(b.getString("name").getValue());
			}
		} else if (elementClass.equals(Edge.class)) {
			Iterator<BsonDocument> bi = edges.listIndexes(BsonDocument.class).iterator();
			while (bi.hasNext()) {
				BsonDocument b = bi.next();
				indexKeys.add(b.getString("name").getValue());
			}
		}
		return indexKeys;
	}

	/**
	 * Return non-redundant timestamps of all graph element events
	 * 
	 * @return HashSet<Long> timestamps
	 */
	public TreeSet<Long> getTimestamps() {
		TreeSet<Long> timestampSet = new TreeSet<Long>();

		Function<BsonDateTime, Long> mapper = new Function<BsonDateTime, Long>() {
			@Override
			public Long apply(BsonDateTime val) {
				return val.getValue();
			}

		};
		edgeEvents.distinct(Tokens.TIMESTAMP, BsonDateTime.class)
				.filter(new BsonDocument(Tokens.TIMESTAMP, new BsonDocument(Tokens.FC.$ne.toString(), new BsonNull())))
				.map(mapper).into(timestampSet);

		return timestampSet;
	}

	public TreeSet<Long> getTimestamps(Long startTime, Long endTime) {
		TreeSet<Long> timestampSet = new TreeSet<Long>();

		Function<BsonDateTime, Long> mapper = new Function<BsonDateTime, Long>() {
			@Override
			public Long apply(BsonDateTime val) {
				return val.getValue();
			}

		};
		edgeEvents.distinct(Tokens.TIMESTAMP, BsonDateTime.class)
				.filter(new BsonDocument(Tokens.TIMESTAMP,
						new BsonDocument(Tokens.FC.$gt.toString(), new BsonDateTime(startTime))
								.append(Tokens.FC.$lt.toString(), new BsonDateTime(endTime))))
				.map(mapper).into(timestampSet);
		Set<Long> vtSet = new TreeSet<Long>();

		vertexEvents.distinct(Tokens.TIMESTAMP, BsonDateTime.class)
				.filter(new BsonDocument(Tokens.TIMESTAMP,
						new BsonDocument(Tokens.FC.$gt.toString(), new BsonDateTime(startTime))
								.append(Tokens.FC.$lt.toString(), new BsonDateTime(endTime))))
				.map(mapper).into(timestampSet);
		timestampSet.addAll(vtSet);

		return timestampSet;
	}

	public HashSet<Long> getTimestampsHashSet() {
		HashSet<Long> timestampSet = new HashSet<Long>();

		Function<BsonDateTime, Long> mapper = new Function<BsonDateTime, Long>() {
			@Override
			public Long apply(BsonDateTime val) {
				return val.getValue();
			}

		};
		edges.distinct(Tokens.TIMESTAMP, BsonDateTime.class)
				.filter(new BsonDocument(Tokens.TIMESTAMP, new BsonDocument(Tokens.FC.$ne.toString(), new BsonNull())))
				.map(mapper).into(timestampSet);

		return timestampSet;
	}

	public void createBasicIndex() {
		Document isNull = edges.listIndexes().first();
		if (isNull == null) {
			edges.createIndex(new BsonDocument(Tokens.OUT_VERTEX, new BsonInt32(1))
					.append(Tokens.LABEL, new BsonInt32(1)).append(Tokens.IN_VERTEX, new BsonInt32(1)));
			edges.createIndex(new BsonDocument(Tokens.IN_VERTEX, new BsonInt32(1))
					.append(Tokens.LABEL, new BsonInt32(1)).append(Tokens.OUT_VERTEX, new BsonInt32(1)));
		}

		isNull = edgeEvents.listIndexes().first();
		if (isNull == null) {
			edgeEvents.createIndex(
					new BsonDocument(Tokens.OUT_VERTEX, new BsonInt32(1)).append(Tokens.LABEL, new BsonInt32(1))
							.append(Tokens.TIMESTAMP, new BsonInt32(1)).append(Tokens.IN_VERTEX, new BsonInt32(1)));
			edgeEvents.createIndex(
					new BsonDocument(Tokens.IN_VERTEX, new BsonInt32(1)).append(Tokens.LABEL, new BsonInt32(1))
							.append(Tokens.TIMESTAMP, new BsonInt32(1)).append(Tokens.OUT_VERTEX, new BsonInt32(1)));
		}

		isNull = vertexEvents.listIndexes().first();
		if (isNull == null) {
			vertexEvents.createIndex(
					new BsonDocument(Tokens.VERTEX, new BsonInt32(1)).append(Tokens.TIMESTAMP, new BsonInt32(1)));
		}
	}

	@Override
	public boolean equals(Object obj) {
		return this.toString().equals(obj.toString());
	}

	@Override
	public int hashCode() {
		return this.toString().hashCode();
	}
}
