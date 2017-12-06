package org.lilliput.chronograph.persistent;

import com.mongodb.Function;
import com.mongodb.MongoBulkWriteException;
import com.mongodb.MongoClient;
import com.mongodb.MongoWriteException;
import com.mongodb.WriteConcern;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Element;
import com.tinkerpop.blueprints.Features;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.GraphQuery;
import com.tinkerpop.blueprints.KeyIndexableGraph;
import com.tinkerpop.blueprints.Parameter;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.util.DefaultGraphQuery;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bson.BsonArray;
import org.bson.BsonDateTime;
import org.bson.BsonDocument;
import org.bson.BsonNull;
import org.bson.BsonString;
import org.bson.BsonValue;
import org.lilliput.chronograph.common.ExceptionFactory;
import org.lilliput.chronograph.common.LongInterval;
import org.lilliput.chronograph.common.Tokens;
import org.lilliput.chronograph.persistent.util.Converter;

/**
 * Copyright (C) 2016-2017 Jaewook Byun
 * 
 * Persistent Temporal Property Graph with MongoDB
 *
 * The part of static graph implements Tinkerpop Blueprints
 * (https://github.com/tinkerpop/blueprints).
 * 
 * @author Jaewook Byun, Ph.D candidate
 * 
 *         Korea Advanced Institute of Science and Technology (KAIST)
 * 
 *         Real-time Embedded System Laboratory (RESL)
 * 
 *         bjw0829@kaist.ac.kr, bjw0829@gmail.com
 * 
 */
public class ChronoGraph implements Graph, KeyIndexableGraph {

	private MongoClient mongoClient = null;
	private MongoDatabase mongoDatabase = null;
	private MongoCollection<BsonDocument> edges = null;
	private MongoCollection<BsonDocument> vertices = null;
	private String id;

	private static Features FEATURES = new Features();
	private static Features PERSISTENT_FEATURES;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void createBasicIndex() {
		// outE, out
		createKeyIndex(null, ChronoEdge.class, new Parameter(Tokens.OUT_VERTEX, 1), new Parameter(Tokens.ID, 1));
		// outE(label), out(label)
		createKeyIndex(null, ChronoEdge.class, new Parameter(Tokens.OUT_VERTEX, 1), new Parameter(Tokens.LABEL, 1),
				new Parameter(Tokens.ID, 1));
		// inE, in
		createKeyIndex(null, ChronoEdge.class, new Parameter(Tokens.IN_VERTEX, 1), new Parameter(Tokens.ID, 1));
		// inE(label), in(label)
		createKeyIndex(null, ChronoEdge.class, new Parameter(Tokens.IN_VERTEX, 1), new Parameter(Tokens.LABEL, 1),
				new Parameter(Tokens.ID, 1));
		// g.getEdges()
		createKeyIndex(null, ChronoVertex.class, new Parameter(Tokens.TYPE, 1), new Parameter(Tokens.ID, 1));
		// edges.getTimestamps, edges.getTimestamps(timestamps),
		// getFirstTimestamp, getLastTimestamp, getCeilingTimestamp,
		// getHigherTimestamp, getFloorTimestamp, getLowerTimestamp
		createKeyIndex(null, ChronoEdge.class, new Parameter(Tokens.EDGE, 1), new Parameter(Tokens.TYPE, 1),
				new Parameter(Tokens.TIMESTAMP, 1));
		createKeyIndex(null, ChronoEdge.class, new Parameter(Tokens.EDGE, 1), new Parameter(Tokens.TYPE, 1),
				new Parameter(Tokens.START, 1), new Parameter(Tokens.END, 1));

		// vertices.getTimestamps, vertices.getTimestamps(timestamps),
		// getFirstTimestamp, getLastTimestamp, getCeilingTimestamp,
		// getHigherTimestamp, getFloorTimestamp, getLowerTimestamp
		createKeyIndex(null, ChronoVertex.class, new Parameter(Tokens.VERTEX, 1), new Parameter(Tokens.TYPE, 1),
				new Parameter(Tokens.TIMESTAMP, 1));
		createKeyIndex(null, ChronoVertex.class, new Parameter(Tokens.VERTEX, 1), new Parameter(Tokens.TYPE, 1),
				new Parameter(Tokens.START, 1), new Parameter(Tokens.END, 1));
		// g.getChronoVertices()
		createKeyIndex(null, ChronoVertex.class, new Parameter(Tokens.TYPE, 1), new Parameter(Tokens.ID, 1));
	}

	public ChronoGraph() {
		setFeatures();
		mongoClient = new MongoClient("localhost", 27017);
		mongoDatabase = mongoClient.getDatabase("chronograph");
		edges = mongoDatabase.getCollection(Tokens.EDGE_COLLECTION, BsonDocument.class)
				.withWriteConcern(WriteConcern.UNACKNOWLEDGED);
		vertices = mongoDatabase.getCollection(Tokens.VERTEX_COLLECTION, BsonDocument.class)
				.withWriteConcern(WriteConcern.UNACKNOWLEDGED);
		createBasicIndex();
		id = "chronograph";
	}

	public ChronoGraph(String databaseName) {
		setFeatures();
		mongoClient = new MongoClient("localhost", 27017);
		mongoDatabase = mongoClient.getDatabase(databaseName);
		edges = mongoDatabase.getCollection(Tokens.EDGE_COLLECTION, BsonDocument.class)
				.withWriteConcern(WriteConcern.UNACKNOWLEDGED);
		vertices = mongoDatabase.getCollection(Tokens.VERTEX_COLLECTION, BsonDocument.class)
				.withWriteConcern(WriteConcern.UNACKNOWLEDGED);
		createBasicIndex();
		id = databaseName;
	}

	public ChronoGraph(String host, int port, String databaseName) {
		setFeatures();
		mongoClient = new MongoClient(host, port);
		mongoDatabase = mongoClient.getDatabase(databaseName);
		edges = mongoDatabase.getCollection(Tokens.EDGE_COLLECTION, BsonDocument.class)
				.withWriteConcern(WriteConcern.UNACKNOWLEDGED);
		vertices = mongoDatabase.getCollection(Tokens.VERTEX_COLLECTION, BsonDocument.class)
				.withWriteConcern(WriteConcern.UNACKNOWLEDGED);
		createBasicIndex();
		id = databaseName;
	}

	public MongoCollection<BsonDocument> getEdgeCollection() {
		return edges;
	}

	public MongoCollection<BsonDocument> getVertexCollection() {
		return vertices;
	}

	public MongoDatabase getMongoDatabase() {
		return mongoDatabase;
	}

	public String getID() {
		return id;
	}

	/**
	 * Return the vertex referenced by the provided object identifier. If no vertex
	 * is referenced by that identifier, then return null (Not happen in
	 * ChronoGraph).
	 *
	 * @param id
	 *            the identifier of the vertex to retrieved from the graph v(id)
	 * @return the vertex referenced by the provided identifier or null when no such
	 *         vertex exists
	 */
	public ChronoVertex getChronoVertex(String id) {
		return new ChronoVertex(id.toString(), this);
	}

	public VertexEvent getVertexEvent(String id) {
		return new VertexEvent(this, id);
	}

	public EdgeEvent getEdgeEvent(String id) {
		String[] arr = id.split("-");
		if (arr[1].contains("(")) {
			String[] intvString = arr[1].substring(1, arr[1].length() - 1).split(",");
			return new EdgeEvent(this, new ChronoEdge(arr[0], this),
					new LongInterval(Long.parseLong(intvString[0]), Long.parseLong(intvString[1])));
		} else {
			return new EdgeEvent(this, new ChronoEdge(arr[0], this), Long.parseLong(arr[1]));
		}
	}

	/**
	 * Remove the provided vertex from the graph. Upon removing the vertex, all the
	 * edges by which the vertex is connected must be removed as well.
	 * 
	 * @param vertex
	 *            the vertex to remove from the graph
	 */
	public void removeVertex(ChronoVertex vertex) {
		BsonDocument filter = new BsonDocument();
		BsonArray or = new BsonArray();
		BsonDocument outV = new BsonDocument(Tokens.OUT_VERTEX, new BsonString(vertex.toString()));
		BsonDocument inV = new BsonDocument(Tokens.IN_VERTEX, new BsonString(vertex.toString()));
		or.add(outV);
		or.add(inV);
		filter.put("$or", or);
		edges.deleteMany(filter);

		filter = new BsonDocument();
		or = new BsonArray();
		BsonDocument temporalV = new BsonDocument(Tokens.VERTEX, new BsonString(vertex.id));
		BsonDocument v = new BsonDocument(Tokens.ID, new BsonString(vertex.id));
		or.add(temporalV);
		or.add(v);
		filter.put("$or", or);
		vertices.deleteMany(filter);
	}

	/**
	 * Return an iterable to all the vertices in the graph. If this is not possible
	 * for the implementation, then an UnsupportedOperationException can be thrown.
	 *
	 * @return an iterable reference to all vertices in the graph
	 */
	public Iterable<ChronoVertex> getChronoVertices() {
		HashSet<String> idSet = new HashSet<String>();
		Function<BsonString, String> mapper = new Function<BsonString, String>() {
			@Override
			public String apply(BsonString val) {
				return val.getValue();
			}

		};
		HashSet<String> outV = new HashSet<String>();
		edges.distinct(Tokens.OUT_VERTEX, BsonString.class)
				.filter(new BsonDocument(Tokens.OUT_VERTEX, new BsonDocument(Tokens.FC.$ne.toString(), new BsonNull())))
				.map(mapper).into(outV);
		idSet.addAll(outV);
		HashSet<String> inV = new HashSet<String>();
		edges.distinct(Tokens.IN_VERTEX, BsonString.class)
				.filter(new BsonDocument(Tokens.IN_VERTEX, new BsonDocument(Tokens.FC.$ne.toString(), new BsonNull())))
				.map(mapper).into(inV);
		idSet.addAll(inV);

		MongoCursor<BsonDocument> vi = vertices.find(Tokens.FLT_VERTEX_FIELD_NOT_INCLUDED)
				.projection(Tokens.PRJ_ONLY_ID).iterator();
		while (vi.hasNext()) {
			BsonDocument d = vi.next();
			idSet.add(d.getString(Tokens.ID).getValue());
		}

		HashSet<String> vertex = new HashSet<String>();
		vertices.distinct(Tokens.VERTEX, BsonString.class)
				.filter(new BsonDocument(Tokens.VERTEX, new BsonDocument(Tokens.FC.$ne.toString(), new BsonNull())))
				.map(mapper).into(vertex);
		idSet.addAll(vertex);

		return idSet.parallelStream().map(s -> new ChronoVertex(s, this)).collect(Collectors.toSet());
	}

	/**
	 * Return an iterable to all the vertices in the graph. If this is not possible
	 * for the implementation, then an UnsupportedOperationException can be thrown.
	 *
	 * @return an iterable reference to all vertices in the graph
	 */
	public Set<ChronoVertex> getChronoVertexSet() {
		HashSet<String> idSet = new HashSet<String>();
		Function<BsonString, String> mapper = new Function<BsonString, String>() {
			@Override
			public String apply(BsonString val) {
				return val.getValue();
			}

		};
		HashSet<String> outV = new HashSet<String>();
		edges.distinct(Tokens.OUT_VERTEX, BsonString.class)
				.filter(new BsonDocument(Tokens.OUT_VERTEX, new BsonDocument(Tokens.FC.$ne.toString(), new BsonNull())))
				.map(mapper).into(outV);
		idSet.addAll(outV);
		HashSet<String> inV = new HashSet<String>();
		edges.distinct(Tokens.IN_VERTEX, BsonString.class)
				.filter(new BsonDocument(Tokens.IN_VERTEX, new BsonDocument(Tokens.FC.$ne.toString(), new BsonNull())))
				.map(mapper).into(inV);
		idSet.addAll(inV);

		MongoCursor<BsonDocument> vi = vertices.find(Tokens.FLT_VERTEX_FIELD_NOT_INCLUDED)
				.projection(Tokens.PRJ_ONLY_ID).iterator();
		while (vi.hasNext()) {
			BsonDocument d = vi.next();
			idSet.add(d.getString(Tokens.ID).getValue());
		}

		HashSet<String> vertex = new HashSet<String>();
		vertices.distinct(Tokens.VERTEX, BsonString.class)
				.filter(new BsonDocument(Tokens.VERTEX, new BsonDocument(Tokens.FC.$ne.toString(), new BsonNull())))
				.map(mapper).into(vertex);
		idSet.addAll(vertex);

		return idSet.parallelStream().map(s -> new ChronoVertex(s, this)).collect(Collectors.toSet());
	}

	/**
	 * Return an iterable to all the vertices in the graph. If this is not possible
	 * for the implementation, then an UnsupportedOperationException can be thrown.
	 *
	 * @return an iterable reference to all vertices in the graph
	 */
	public Stream<ChronoVertex> getChronoVertexStream(boolean isParallel) {
		HashSet<String> idSet = new HashSet<String>();
		Function<BsonString, String> mapper = new Function<BsonString, String>() {
			@Override
			public String apply(BsonString val) {
				return val.getValue();
			}

		};
		HashSet<String> outV = new HashSet<String>();
		edges.distinct(Tokens.OUT_VERTEX, BsonString.class)
				.filter(new BsonDocument(Tokens.OUT_VERTEX, new BsonDocument(Tokens.FC.$ne.toString(), new BsonNull())))
				.map(mapper).into(outV);
		idSet.addAll(outV);
		HashSet<String> inV = new HashSet<String>();
		edges.distinct(Tokens.IN_VERTEX, BsonString.class)
				.filter(new BsonDocument(Tokens.IN_VERTEX, new BsonDocument(Tokens.FC.$ne.toString(), new BsonNull())))
				.map(mapper).into(inV);
		idSet.addAll(inV);

		MongoCursor<BsonDocument> vi = vertices.find(Tokens.FLT_VERTEX_FIELD_NOT_INCLUDED)
				.projection(Tokens.PRJ_ONLY_ID).iterator();
		while (vi.hasNext()) {
			BsonDocument d = vi.next();
			idSet.add(d.getString(Tokens.ID).getValue());
		}

		HashSet<String> vertex = new HashSet<String>();
		vertices.distinct(Tokens.VERTEX, BsonString.class)
				.filter(new BsonDocument(Tokens.VERTEX, new BsonDocument(Tokens.FC.$ne.toString(), new BsonNull())))
				.map(mapper).into(vertex);
		idSet.addAll(vertex);
		if (isParallel)
			return idSet.parallelStream().map(s -> new ChronoVertex(s, this)).collect(Collectors.toSet())
					.parallelStream();
		else
			return idSet.parallelStream().map(s -> new ChronoVertex(s, this)).collect(Collectors.toSet()).stream();
	}

	/**
	 * Return an iterable to all the vertices in the graph that have a particular
	 * key/value property. If this is not possible for the implementation, then an
	 * UnsupportedOperationException can be thrown. The graph implementation should
	 * use indexing structures to make this efficient else a full vertex-filter scan
	 * is required.
	 * 
	 * @param key
	 *            the key of vertex
	 * @param value
	 *            the value of the vertex
	 * @return an iterable of vertices with provided key and value
	 */
	public Iterable<ChronoVertex> getChronoVertices(String key, Object value) {
		ElementHelper.validateProperty(null, key, value);
		HashSet<ChronoVertex> ret = new HashSet<ChronoVertex>();

		MongoCursor<BsonDocument> cursor = vertices
				.find(Tokens.FLT_VERTEX_FIELD_NOT_INCLUDED.append(key, (BsonValue) value))
				.projection(Tokens.PRJ_ONLY_ID).iterator();

		while (cursor.hasNext()) {
			BsonDocument v = cursor.next();
			ret.add(new ChronoVertex(v.getString(Tokens.ID).getValue(), this));
		}
		return ret;
	}

	/**
	 * Return an iterable to all the vertices in the graph that have a particular
	 * key/value property. If this is not possible for the implementation, then an
	 * UnsupportedOperationException can be thrown. The graph implementation should
	 * use indexing structures to make this efficient else a full vertex-filter scan
	 * is required.
	 * 
	 * @param key
	 *            the key of vertex
	 * @param value
	 *            the value of the vertex
	 * @return an iterable of vertices with provided key and value
	 */
	public Set<ChronoVertex> getChronoVertexSet(String key, Object value) {
		ElementHelper.validateProperty(null, key, value);
		HashSet<ChronoVertex> ret = new HashSet<ChronoVertex>();

		MongoCursor<BsonDocument> cursor = vertices
				.find(Tokens.FLT_VERTEX_FIELD_NOT_INCLUDED.append(key, (BsonValue) value))
				.projection(Tokens.PRJ_ONLY_ID).iterator();

		while (cursor.hasNext()) {
			BsonDocument v = cursor.next();
			ret.add(new ChronoVertex(v.getString(Tokens.ID).getValue(), this));
		}
		return ret;
	}

	public Stream<ChronoVertex> getChronoVertexStream(String key, Object value) {
		ElementHelper.validateProperty(null, key, value);
		HashSet<ChronoVertex> ret = new HashSet<ChronoVertex>();

		MongoCursor<BsonDocument> cursor = vertices
				.find(Tokens.FLT_VERTEX_FIELD_NOT_INCLUDED.append(key, (BsonValue) value))
				.projection(Tokens.PRJ_ONLY_ID).iterator();

		while (cursor.hasNext()) {
			BsonDocument v = cursor.next();
			ret.add(new ChronoVertex(v.getString(Tokens.ID).getValue(), this));
		}
		return ret.parallelStream();
	}

	public Stream<ChronoVertex> getChronoVertexStream(String key, Object value, boolean isParallel) {
		ElementHelper.validateProperty(null, key, value);
		HashSet<ChronoVertex> ret = new HashSet<ChronoVertex>();

		MongoCursor<BsonDocument> cursor = vertices
				.find(Tokens.FLT_VERTEX_FIELD_NOT_INCLUDED.append(key, (BsonValue) value))
				.projection(Tokens.PRJ_ONLY_ID).iterator();

		while (cursor.hasNext()) {
			BsonDocument v = cursor.next();
			ret.add(new ChronoVertex(v.getString(Tokens.ID).getValue(), this));
		}
		if (isParallel)
			return ret.parallelStream();
		else
			return ret.stream();
	}

	/**
	 * Add an edge to the graph. The added edges requires a recommended identifier,
	 * a tail vertex, an head vertex, and a label. Like adding a vertex, the
	 * provided object identifier may be ignored by the implementation.
	 * 
	 * @param outVertexID:
	 *            the vertex on the tail of the edge
	 * @param inVertexID:
	 *            the vertex on the head of the edge
	 * @param label:
	 *            the label associated with the edge
	 * @return the newly created edge
	 */
	public ChronoEdge addEdge(final String outVertexID, final String inVertexID, final String label) {
		if (label == null)
			throw ExceptionFactory.edgeLabelCanNotBeNull();
		String edgeID = Converter.getEdgeID(outVertexID, label, inVertexID);
		try {
			BsonDocument e = new BsonDocument();
			e.put(Tokens.ID, new BsonString(edgeID));
			e.put(Tokens.OUT_VERTEX, new BsonString(outVertexID));
			e.put(Tokens.IN_VERTEX, new BsonString(inVertexID));
			e.put(Tokens.LABEL, new BsonString(label));
			edges.insertOne(e);
			return new ChronoEdge(edgeID, outVertexID, inVertexID, label, this);
		} catch (MongoWriteException e) {
			// CODE: 11000, Duplicated _ID = (edge)
			return new ChronoEdge(edgeID, outVertexID, inVertexID, label, this);
		}
	}

	/**
	 * Insert bulk edges
	 * 
	 * @see: outV|label|inV is unique in ChronoGraph. this operation does not permit
	 *       duplicated edges
	 * @param edges
	 *            use with Converter.getBsonDocumentEdge
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
	 * @param id:
	 *            outVertexID|label|inVertexID
	 */
	public ChronoEdge getEdge(String id) {
		if (id == null)
			throw ExceptionFactory.edgeIdCanNotBeNull();
		BsonString bsonID = new BsonString(id.toString());
		if (edges.find(new BsonDocument(Tokens.ID, bsonID)).first() != null)
			return new ChronoEdge(id.toString(), this);
		return null;
	}

	/**
	 * Remove the provided edge from the graph.
	 *
	 * @param edge
	 *            the edge to remove from the graph
	 */
	public void removeEdge(final String edgeID) {
		BsonDocument filter = new BsonDocument();
		BsonArray or = new BsonArray();
		BsonDocument temporalE = new BsonDocument(Tokens.EDGE, new BsonString(edgeID));
		BsonDocument e = new BsonDocument(Tokens.ID, new BsonString(edgeID));
		or.add(temporalE);
		or.add(e);
		filter.put(Tokens.C.$or.toString(), or);
		edges.deleteMany(filter);
	}

	/**
	 * Remove the provided edge from the graph.
	 *
	 * @param edge
	 *            the edge to remove from the graph
	 */
	public void removeEdge(final ChronoEdge edge) {
		BsonDocument filter = new BsonDocument();
		BsonArray or = new BsonArray();
		BsonDocument temporalE = new BsonDocument(Tokens.EDGE, new BsonString(edge.toString()));
		BsonDocument e = new BsonDocument(Tokens.ID, new BsonString(edge.toString()));
		or.add(temporalE);
		or.add(e);
		filter.put(Tokens.C.$or.toString(), or);
		edges.deleteMany(filter);
	}

	/**
	 * Remove Edge
	 * 
	 * @param edge
	 */
	public void removeEdge(final String outVertexID, final String inVertexID, final String label) {
		String edgeID = Converter.getEdgeID(outVertexID, label, inVertexID);
		removeEdge(edgeID);
	}

	/**
	 * Return an iterable to all the edges in the graph. If this is not possible for
	 * the implementation, then an UnsupportedOperationException can be thrown.
	 *
	 * @return an iterable reference to all edges in the graph
	 */
	public Iterable<ChronoEdge> getChronoEdges() {
		HashSet<ChronoEdge> ret = new HashSet<ChronoEdge>();
		MongoCursor<BsonDocument> cursor = edges.find(Tokens.FLT_EDGE_FIELD_NOT_INCLUDED).projection(Tokens.PRJ_ONLY_ID)
				.iterator();

		while (cursor.hasNext()) {
			BsonDocument v = cursor.next();
			ret.add(new ChronoEdge(v.getString(Tokens.ID).getValue(), this));
		}
		return ret;
	}

	/**
	 * Return an iterable to all the edges in the graph. If this is not possible for
	 * the implementation, then an UnsupportedOperationException can be thrown.
	 *
	 * @return an iterable reference to all edges in the graph
	 */
	public Set<ChronoEdge> getChronoEdgeSet() {
		HashSet<ChronoEdge> ret = new HashSet<ChronoEdge>();
		MongoCursor<BsonDocument> cursor = edges.find(Tokens.FLT_EDGE_FIELD_NOT_INCLUDED).projection(Tokens.PRJ_ONLY_ID)
				.iterator();

		while (cursor.hasNext()) {
			BsonDocument v = cursor.next();
			ret.add(new ChronoEdge(v.getString(Tokens.ID).getValue(), this));
		}
		return ret;
	}

	/**
	 * Return an iterable to all the edges in the graph. If this is not possible for
	 * the implementation, then an UnsupportedOperationException can be thrown.
	 * 
	 * @return
	 *
	 * @return an iterable reference to all edges in the graph
	 */
	public Stream<ChronoEdge> getChronoEdgeStream(boolean isParallel) {
		HashSet<ChronoEdge> ret = new HashSet<ChronoEdge>();
		MongoCursor<BsonDocument> cursor = edges.find(Tokens.FLT_EDGE_FIELD_NOT_INCLUDED).projection(Tokens.PRJ_ONLY_ID)
				.iterator();

		while (cursor.hasNext()) {
			BsonDocument v = cursor.next();
			ret.add(new ChronoEdge(v.getString(Tokens.ID).getValue(), this));
		}
		if (isParallel)
			return ret.parallelStream();
		else
			return ret.stream();
	}

	/**
	 * Return an iterable to all the edges in the graph that have a particular
	 * key/value property. If this is not possible for the implementation, then an
	 * UnsupportedOperationException can be thrown. The graph implementation should
	 * use indexing structures to make this efficient else a full edge-filter scan
	 * is required.
	 * 
	 * @param key
	 *            the key of the edge
	 * @param value
	 *            the value of the edge
	 * @return an iterable of edges with provided key and value
	 */
	public Iterable<ChronoEdge> getChronoEdges(String key, Object value) {
		ElementHelper.validateProperty(null, key, value);

		HashSet<ChronoEdge> ret = new HashSet<ChronoEdge>();
		MongoCursor<BsonDocument> cursor = edges.find(Tokens.FLT_EDGE_FIELD_NOT_INCLUDED.append(key, (BsonValue) value))
				.projection(Tokens.PRJ_ONLY_ID).iterator();

		while (cursor.hasNext()) {
			BsonDocument v = cursor.next();
			ret.add(new ChronoEdge(v.getString(Tokens.ID).getValue(), this));
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
	 * @param key
	 *            the key of the edge
	 * @param value
	 *            the value of the edge
	 * @return an iterable of edges with provided key and value
	 */
	public Set<ChronoEdge> getChronoEdgeSet(String key, Object value) {
		ElementHelper.validateProperty(null, key, value);

		HashSet<ChronoEdge> ret = new HashSet<ChronoEdge>();
		MongoCursor<BsonDocument> cursor = edges.find(Tokens.FLT_EDGE_FIELD_NOT_INCLUDED.append(key, (BsonValue) value))
				.projection(Tokens.PRJ_ONLY_ID).iterator();

		while (cursor.hasNext()) {
			BsonDocument v = cursor.next();
			ret.add(new ChronoEdge(v.getString(Tokens.ID).getValue(), this));
		}
		return ret;
	}

	public Stream<ChronoEdge> getChronoEdgeStream(String key, Object value) {
		ElementHelper.validateProperty(null, key, value);

		HashSet<ChronoEdge> ret = new HashSet<ChronoEdge>();
		MongoCursor<BsonDocument> cursor = edges.find(Tokens.FLT_EDGE_FIELD_NOT_INCLUDED.append(key, (BsonValue) value))
				.projection(Tokens.PRJ_ONLY_ID).iterator();

		while (cursor.hasNext()) {
			BsonDocument v = cursor.next();
			ret.add(new ChronoEdge(v.getString(Tokens.ID).getValue(), this));
		}
		return ret.parallelStream();
	}

	public Stream<ChronoEdge> getChronoEdgeStream(String key, Object value, boolean isParallel) {
		ElementHelper.validateProperty(null, key, value);

		HashSet<ChronoEdge> ret = new HashSet<ChronoEdge>();
		MongoCursor<BsonDocument> cursor = edges.find(Tokens.FLT_EDGE_FIELD_NOT_INCLUDED.append(key, (BsonValue) value))
				.projection(Tokens.PRJ_ONLY_ID).iterator();

		while (cursor.hasNext()) {
			BsonDocument v = cursor.next();
			ret.add(new ChronoEdge(v.getString(Tokens.ID).getValue(), this));
		}
		if (isParallel)
			return ret.parallelStream();
		else
			return ret.stream();
	}

	/**
	 * Return an iterable to all the edges in the graph. If this is not possible for
	 * the implementation, then an UnsupportedOperationException can be thrown.
	 * 
	 * @param labels
	 * @return iterable Edge Identifiers having the given label sets
	 */
	public Iterable<ChronoEdge> getEdges(final BsonArray labels) {

		HashSet<ChronoEdge> edgeSet = new HashSet<ChronoEdge>();
		BsonDocument filter = new BsonDocument();
		BsonDocument inner = new BsonDocument();
		inner.put(Tokens.FC.$in.toString(), labels);
		filter.put(Tokens.LABEL, inner);
		Iterator<BsonDocument> it = edges.find(filter).iterator();
		while (it.hasNext()) {
			BsonDocument d = it.next();
			edgeSet.add(new ChronoEdge(d.getString(Tokens.ID).getValue(), this));
		}
		return edgeSet;
	}

	/**
	 * Return an iterable to all the edges in the graph. If this is not possible for
	 * the implementation, then an UnsupportedOperationException can be thrown.
	 * 
	 * @param labels
	 * @return iterable Edge Identifiers having the given label sets
	 */
	public HashSet<ChronoEdge> getEdges(final String label) {

		HashSet<ChronoEdge> edgeSet = new HashSet<ChronoEdge>();
		BsonDocument filter = new BsonDocument();
		filter.put(Tokens.LABEL, new BsonString(label));
		Iterator<BsonDocument> it = edges.find(filter).iterator();
		while (it.hasNext()) {
			BsonDocument d = it.next();
			edgeSet.add(new ChronoEdge(d.getString(Tokens.ID).getValue(), this));
		}
		return edgeSet;
	}

	/**
	 * Generate a query object that can be used to fine tune which edges/vertices
	 * are retrieved from the graph.
	 * 
	 * @return a graph query object with methods for constraining which data is
	 *         pulled from the underlying graph
	 */
	@Override
	public GraphQuery query() {
		return new DefaultGraphQuery(this);
	}

	/**
	 * A shutdown function is required to properly close the graph. This is
	 * important for implementations that utilize disk-based serializations.
	 * 
	 * Close MongoDB Client
	 */
	@Override
	public void shutdown() {
		this.mongoClient.close();
	}

	/**
	 * @return readable string
	 */
	public String toString() {
		return this.mongoDatabase.getName() + " database connected to " + this.mongoClient.getAddress();
	}

	/**
	 * Clear Database
	 */
	public void clear() {
		edges.drop();
		vertices.drop();
	}

	/**
	 * Extension: only support ChronoVertex add temporal property into the vertex
	 * for same timestamp & key to the existing property, replace the value
	 * 
	 * @param id:
	 *            unique vertex id
	 * @param timestamp:
	 *            unix epoch
	 * @param key:
	 *            property key for the given timestamp
	 * @param value:
	 *            property value for the given timestamp
	 * @return updated Vertex
	 */
	public ChronoVertex addTimestampVertexProperty(final String id, final Long timestamp, final String key,
			final BsonValue value) {
		ChronoVertex v = getChronoVertex(id);
		v.setTimestampProperty(timestamp, key, value);
		return v;
	}

	/**
	 * Extension: only support ChronoEdge add interval property into the vertex for
	 * same interval & key to the existing property, replace the value
	 * 
	 * @param id:
	 *            unique vertex id
	 * @param interval:
	 *            interval
	 * @param key:
	 *            property key for the given interval
	 * @param value:
	 *            property value for the given interval
	 * @return updated Vertex
	 */
	public ChronoVertex addIntervalVertexProperty(final String id, final LongInterval interval, final String key,
			final BsonValue value) {
		ChronoVertex v = getChronoVertex(id);
		v.setIntervalProperty(interval, key, value);
		return v;
	}

	/**
	 * Extension: only support ChronoEdge, add temporal property into the edge for
	 * same timestamp & key to the existing property, replace the value
	 * 
	 * @param outVertexID:
	 *            unique out vertex id
	 * @param inVertexID:
	 *            unique in vertex id
	 * @param label:
	 *            edge label
	 * @param timestamp:
	 *            unix epoch
	 * @param key:
	 *            property key for the given timestamp
	 * @param value:
	 *            property value for the given timestamp
	 * @return updated edge
	 */
	public ChronoEdge addTimestampEdgeProperty(final String outVertexID, final String inVertexID, final String label,
			final Long timestamp, final String key, final BsonValue value) {
		ChronoEdge e = addEdge(outVertexID, inVertexID, label);
		e.setTimestampProperty(timestamp, key, value);
		return e;
	}

	public ChronoEdge addTimestampEdgeProperties(final String outVertexID, final String inVertexID, final String label,
			final Long timestamp, final BsonDocument properties) {
		ChronoEdge e = addEdge(outVertexID, inVertexID, label);
		e.setTimestampProperties(timestamp, properties);
		return e;
	}

	/**
	 * Extension: only support ChronoEdge, add temporal property into the edge for
	 * same timestamp & key to the existing property, replace the value
	 * 
	 * @param outVertexID:
	 *            unique out vertex id
	 * @param inVertexID:
	 *            unique in vertex id
	 * @param label:
	 *            edge label
	 * @param interval:
	 *            interval
	 * @param key:
	 *            property key for the given interval
	 * @param value:
	 *            property value for the given interval
	 * @return updated edge
	 */
	public ChronoEdge addIntervalEdgeProperty(final String outVertexID, final String inVertexID, final String label,
			final LongInterval interval, final String key, final BsonValue value) {
		ChronoEdge e = addEdge(outVertexID, inVertexID, label);
		e.setIntervalProperty(interval, key, value);
		return e;
	}

	public ChronoEdge addIntervalEdgeProperties(final String outVertexID, final String inVertexID, final String label,
			final LongInterval interval, final BsonDocument properties) {
		ChronoEdge e = addEdge(outVertexID, inVertexID, label);
		e.setIntervalProperties(interval, properties);
		return e;
	}

	/**
	 * Remove all the interval events
	 */
	public void removeIntervalEvents() {
		this.getChronoEdgeStream(true).forEach(e -> e.clearIntervalProperties());
		this.getChronoVertexStream(true).forEach(v -> v.clearIntervalProperties());
	}

	/**
	 * Remove all the timestamp events
	 */
	public void removeTimestampEvents() {
		this.getChronoEdgeStream(true).forEach(e -> e.clearTimestampProperties());
		this.getChronoVertexStream(true).forEach(v -> v.clearTimestampProperties());
	}

	/**
	 * Remove all the timestamp/interval events
	 */
	public void removeTemporalEvents() {
		this.getChronoEdgeStream(true).forEach(e -> {
			e.clearIntervalProperties();
			e.clearTimestampProperties();
		});
		this.getChronoVertexStream(true).forEach(v -> {
			v.clearIntervalProperties();
			v.clearTimestampProperties();
		});
	}

	/**
	 * Remove an automatic indexing structure associated with indexing provided key
	 * for element class.
	 *
	 * @param key
	 *            the key to drop the index for
	 * @param elementClass
	 *            the element class that the index is for
	 * @param <T>
	 *            the element class specification
	 */
	@Override
	public <T extends Element> void dropKeyIndex(String key, Class<T> elementClass) {
		if (elementClass.equals(ChronoVertex.class)) {
			vertices.dropIndex(key);
		} else if (elementClass.equals(ChronoEdge.class)) {
			edges.dropIndex(key);
		} else {
			throw ExceptionFactory.indexDoesNotSupportClass(key, elementClass);
		}
	}

	/**
	 * Create an automatic indexing structure for indexing provided key for element
	 * class.
	 *
	 * @param key
	 *            the key to create the index for
	 * 
	 *            (auto-generated) _[key]_ or (_[key]_(-1|1))*
	 * 
	 * @param elementClass
	 *            the element class that the index is for
	 * @param indexParameters
	 *            a collection of parameters for the underlying index implementation
	 * @param <T>
	 *            the element class specification
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public <T extends Element> void createKeyIndex(String key, Class<T> elementClass, Parameter... indexParameters) {
		if (elementClass.equals(ChronoVertex.class)) {
			BsonDocument index = Converter.makeIndexParamterDocument(indexParameters);
			vertices.createIndex(index);
		} else if (elementClass.equals(ChronoEdge.class)) {
			BsonDocument index = Converter.makeIndexParamterDocument(indexParameters);
			edges.createIndex(index);
		} else {
			throw ExceptionFactory.indexDoesNotSupportClass(key, elementClass);
		}
	}

	/**
	 * Return all the index keys associated with a particular element class.
	 *
	 * @param elementClass:
	 *            class that the index is for
	 *            (ChronoVertex|ChronoEdge|VertexEvent|EdgeEvent) the element
	 * 
	 * @param <T>
	 *            the element class specification
	 * @return the indexed keys as a Set in [database].[collection]
	 * 
	 *         _[key]_ or (_[key]_(-1|1))*
	 */
	@Override
	public <T extends Element> Set<String> getIndexedKeys(Class<T> elementClass) {
		Set<String> indexKeys = new HashSet<String>();
		if (elementClass.equals(ChronoVertex.class)) {
			Iterator<BsonDocument> bi = vertices.listIndexes(BsonDocument.class).iterator();
			while (bi.hasNext()) {
				BsonDocument b = bi.next();
				indexKeys.add(b.getString("name").getValue());
			}
		} else if (elementClass.equals(ChronoEdge.class)) {
			Iterator<BsonDocument> bi = edges.listIndexes(BsonDocument.class).iterator();
			while (bi.hasNext()) {
				BsonDocument b = bi.next();
				indexKeys.add(b.getString("name").getValue());
			}
		} else {
			throw ExceptionFactory.indexDoesNotSupportClass("", elementClass);
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
		edges.distinct(Tokens.TIMESTAMP, BsonDateTime.class)
				.filter(new BsonDocument(Tokens.TIMESTAMP, new BsonDocument(Tokens.FC.$ne.toString(), new BsonNull())))
				.map(mapper).into(timestampSet);

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

	/**
	 * Get the particular features of the graph implementation. Not all graph
	 * implementations are identical nor perfectly implement the Blueprints API. The
	 * Features object returned contains meta-data about numerous potential
	 * divergences between implementations.
	 *
	 * @return the features of this particular Graph implementation
	 */
	@Override
	public Features getFeatures() {
		return FEATURES;
	}

	private static void setFeatures() {
		FEATURES.supportsDuplicateEdges = false;
		FEATURES.supportsSelfLoops = true;
		FEATURES.supportsSerializableObjectProperty = true;
		FEATURES.supportsBooleanProperty = true;
		FEATURES.supportsDoubleProperty = true;
		FEATURES.supportsFloatProperty = true;
		FEATURES.supportsIntegerProperty = true;
		FEATURES.supportsPrimitiveArrayProperty = true;
		FEATURES.supportsUniformListProperty = true;
		FEATURES.supportsMixedListProperty = true;
		FEATURES.supportsLongProperty = true;
		FEATURES.supportsMapProperty = true;
		FEATURES.supportsStringProperty = true;

		FEATURES.ignoresSuppliedIds = false;
		FEATURES.isPersistent = false;
		FEATURES.isWrapper = false;

		// Index
		FEATURES.supportsIndices = false;
		FEATURES.supportsKeyIndices = false;
		FEATURES.supportsVertexKeyIndex = false;
		FEATURES.supportsEdgeKeyIndex = false;
		FEATURES.supportsVertexIndex = false;
		FEATURES.supportsEdgeIndex = false;

		FEATURES.supportsTransactions = false;
		FEATURES.supportsVertexIteration = true;
		FEATURES.supportsEdgeIteration = true;
		FEATURES.supportsEdgeRetrieval = true;
		FEATURES.supportsVertexProperties = true;
		FEATURES.supportsEdgeProperties = true;
		FEATURES.supportsThreadedTransactions = false;
		FEATURES.supportsThreadIsolatedTransactions = false;

		PERSISTENT_FEATURES = FEATURES.copyFeatures();
		PERSISTENT_FEATURES.isPersistent = true;
	}

	/**
	 * @Deprecated: use getChronoVertex(String id)
	 */
	@Deprecated
	@Override
	public Vertex addVertex(Object id) {
		return getChronoVertex(id.toString());
	}

	/**
	 * @deprecated: use getChronoVertex(String id)
	 */
	@Deprecated
	@Override
	public Vertex getVertex(Object id) {
		return getChronoVertex(id.toString());
	}

	/**
	 * @deprecated use getChronoVertices()
	 */
	@Deprecated
	@Override
	public Iterable<Vertex> getVertices() {
		HashSet<Vertex> vertexSet = new HashSet<Vertex>();
		Iterator<BsonDocument> it = edges.find().projection(Tokens.PRJ_ONLY_OUTV_INV_NOT_ID).iterator();
		while (it.hasNext()) {
			BsonDocument d = it.next();
			vertexSet.add(new ChronoVertex(d.getString(Tokens.OUT_VERTEX).getValue(), this));
			vertexSet.add(new ChronoVertex(d.getString(Tokens.IN_VERTEX).getValue(), this));
		}
		return vertexSet;
	}

	/**
	 * @deprecated use getChronoVertices
	 */
	@Override
	public Iterable<Vertex> getVertices(String key, Object value) {
		Iterator<ChronoVertex> it = getChronoVertices(key, value).iterator();
		HashSet<Vertex> ret = new HashSet<Vertex>();
		while (it.hasNext()) {
			ret.add(getChronoVertex(it.next().toString()));
		}
		return ret;
	}

	/**
	 * @deprecated use addEdge(outVertexID, inVertexID, label)
	 */
	@Deprecated
	@Override
	public Edge addEdge(Object id, Vertex outVertex, Vertex inVertex, String label) {
		return addEdge(outVertex.toString(), inVertex.toString(), label);
	}

	/**
	 * @deprecated use getEdge(String id)
	 */
	@Deprecated
	@Override
	public Edge getEdge(Object id) {
		return getEdge(id.toString());
	}

	/**
	 * @deprecated use removeEdge(ChronoEdge)
	 */
	@Deprecated
	@Override
	public void removeEdge(final Edge edge) {
		edges.deleteOne(new BsonDocument(Tokens.ID, new BsonString(edge.toString())));
	}

	/**
	 * @deprecated use getChronoEdges()
	 */
	@Deprecated
	@Override
	public Iterable<Edge> getEdges() {
		HashSet<Edge> edgeSet = new HashSet<Edge>();
		Iterator<BsonDocument> it = edges.find().projection(Tokens.PRJ_ONLY_ID).iterator();
		while (it.hasNext()) {
			BsonDocument d = it.next();
			edgeSet.add(new ChronoEdge(d.getString(Tokens.ID).getValue(), this));
		}
		return edgeSet;
	}

	/**
	 * @deprecated use removeVertex(ChronoVertex)
	 */
	@Deprecated
	@Override
	public void removeVertex(Vertex vertex) {
		removeVertex(new ChronoVertex(vertex.toString(), this));
	}

	/**
	 * @deprecated use getChronoEdges(key,value)
	 */
	@Deprecated
	@Override
	public Iterable<Edge> getEdges(String key, Object value) {
		HashSet<Edge> ret = new HashSet<Edge>();
		MongoCursor<BsonDocument> cursor = vertices.find(Tokens.FLT_EDGE_FIELD_NOT_INCLUDED)
				.projection(Tokens.PRJ_ONLY_ID).iterator();

		while (cursor.hasNext()) {
			BsonDocument v = cursor.next();
			ret.add(new ChronoEdge(v.getString(Tokens.ID).getValue(), this));
		}
		return ret;
	}

}
