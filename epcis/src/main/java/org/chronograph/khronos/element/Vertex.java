package org.chronograph.khronos.element;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.bson.BsonBoolean;
import org.bson.BsonDateTime;
import org.bson.BsonDocument;
import org.bson.BsonString;
import org.bson.BsonValue;
import org.chronograph.khronos.common.Converter;
import org.chronograph.khronos.common.Direction;
import org.chronograph.khronos.common.Tokens;

import com.mongodb.MongoCursorNotFoundException;
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
public class Vertex extends Element {

	private final BsonString id;
	private final Graph graph;

	/**
	 * Create Vertex with vertex id and graph
	 * 
	 * @param id
	 * @param graph
	 */
	public Vertex(Graph graph, BsonString id) {
		this.graph = graph;
		this.id = id;
	}

	/**
	 * Get an identifier of vertex
	 * 
	 * @return an identifier of vertex
	 */
	public BsonString getID() {
		return id;
	}

	/**
	 * Get a graph including this
	 * 
	 * @return a graph including this
	 */
	public Graph getGraph() {
		return graph;
	}

	/**
	 * Return the edges incident to the vertex according to the provided direction
	 * and edge labels.
	 * 
	 * @param direction the direction of the edges to retrieve
	 * @param label     the label of the edges to retrieve
	 * @return a set of incident edges
	 */
	public Set<Edge> getEdgeSet(Direction direction, BsonString label) {
		if (direction == Direction.OUT) {
			while (true) {
				try {
					HashSet<Edge> edgeSet = new HashSet<Edge>();
					BsonDocument filter = new BsonDocument();
					filter.append(Tokens.OUT_VERTEX, this.id);

					Iterator<BsonDocument> it = graph.getEdgeCollection()
							.find(new BsonDocument(Tokens.OUT_VERTEX, this.id).append(Tokens.LABEL, label))
							.projection(new BsonDocument(Tokens.IN_VERTEX, new BsonBoolean(true)).append(Tokens.ID,
									new BsonBoolean(false)))
							.iterator();
					while (it.hasNext()) {
						BsonDocument doc = it.next();
						BsonString inV = doc.getString(Tokens.IN_VERTEX);
						edgeSet.add(new Edge(graph, this.id, label, inV));
					}
					return edgeSet;
				} catch (MongoCursorNotFoundException e1) {
					System.out.println(e1.getErrorMessage());
				}
			}
		} else if (direction == Direction.IN) {
			while (true) {
				try {
					HashSet<Edge> edgeSet = new HashSet<Edge>();
					BsonDocument filter = new BsonDocument();
					filter.append(Tokens.IN_VERTEX, this.id);

					Iterator<BsonDocument> it = graph.getEdgeCollection()
							.find(new BsonDocument(Tokens.IN_VERTEX, this.id).append(Tokens.LABEL, label))
							.projection(new BsonDocument(Tokens.OUT_VERTEX, new BsonBoolean(true)).append(Tokens.ID,
									new BsonBoolean(false)))
							.iterator();
					while (it.hasNext()) {
						BsonDocument doc = it.next();
						BsonString inV = doc.getString(Tokens.OUT_VERTEX);
						edgeSet.add(new Edge(graph, inV, label, this.id));
					}
					return edgeSet;
				} catch (MongoCursorNotFoundException e1) {
					System.out.println(e1.getErrorMessage());
				}
			}
		}
		return null;
	}

	/**
	 * Return the vertices adjacent to the vertex according to the provided
	 * direction and edge labels. This method does not remove duplicate vertices
	 * (i.e. those vertices that are connected by more than one edge).
	 * 
	 * @param direction the direction of the edges of the adjacent vertices
	 * @param label     the labels of the edges of the adjacent vertices
	 * @return a set of adjacent vertices
	 */
	public Set<Vertex> getVertexSet(Direction direction, BsonString label) {
		if (direction == Direction.OUT) {
			while (true) {
				try {
					HashSet<Vertex> vertexSet = new HashSet<Vertex>();
					BsonDocument filter = new BsonDocument();
					filter.append(Tokens.OUT_VERTEX, this.id);

					Iterator<BsonDocument> it = graph.getEdgeCollection()
							.find(new BsonDocument(Tokens.OUT_VERTEX, this.id).append(Tokens.LABEL, label))
							.projection(new BsonDocument(Tokens.IN_VERTEX, new BsonBoolean(true)).append(Tokens.ID,
									new BsonBoolean(false)))
							.iterator();
					while (it.hasNext()) {
						BsonDocument doc = it.next();
						BsonString inV = doc.getString(Tokens.IN_VERTEX);
						vertexSet.add(new Vertex(graph, inV));
					}
					return vertexSet;
				} catch (MongoCursorNotFoundException e1) {
					System.out.println(e1.getErrorMessage());
				}
			}
		} else if (direction == Direction.IN) {
			while (true) {
				try {
					HashSet<Vertex> vertexSet = new HashSet<Vertex>();
					BsonDocument filter = new BsonDocument();
					filter.append(Tokens.IN_VERTEX, this.id);

					Iterator<BsonDocument> it = graph.getEdgeCollection()
							.find(new BsonDocument(Tokens.IN_VERTEX, this.id).append(Tokens.LABEL, label))
							.projection(new BsonDocument(Tokens.OUT_VERTEX, new BsonBoolean(true)).append(Tokens.ID,
									new BsonBoolean(false)))
							.iterator();
					while (it.hasNext()) {
						BsonDocument doc = it.next();
						BsonString inV = doc.getString(Tokens.OUT_VERTEX);
						vertexSet.add(new Vertex(graph, inV));
					}
					return vertexSet;
				} catch (MongoCursorNotFoundException e1) {
					System.out.println(e1.getErrorMessage());
				}
			}
		}
		return null;
	}

	/**
	 * Return a property value of a given key
	 * 
	 * @param key
	 * @return a property value
	 */
	public BsonValue getProperty(String key) {

		BsonDocument doc = graph.getVertexCollection().find(new BsonDocument(Tokens.ID, id)).first();
		if (doc == null)
			return null;
		return doc.get(key);
	}

	/**
	 * Return key-value properties of a vertex
	 * 
	 * @return properties except Tokens.ID
	 */
	public BsonValue getProperties() {
		BsonDocument doc = graph.getVertexCollection().find(new BsonDocument(Tokens.ID, id)).first();
		if (doc == null)
			return null;
		doc.remove(Tokens.ID);
		return doc;
	}

	/**
	 * Set a key-value property into a vertex
	 * 
	 * @param key
	 * @param value
	 */
	public void setProperty(String key, BsonValue value) {
		BsonDocument filter = new BsonDocument();
		filter.put(Tokens.ID, id);
		BsonDocument update = new BsonDocument();
		update.put("$set", new BsonDocument(key, value));
		graph.getVertexCollection().updateOne(filter, update, new UpdateOptions().upsert(true));

	}

	/**
	 * Set key-value properties into a vertex
	 * 
	 * @param properties
	 */
	public void setProperties(BsonDocument properties) {
		if (properties == null)
			properties = new BsonDocument();
		BsonDocument filter = new BsonDocument();
		filter.put(Tokens.ID, id);
		graph.getVertexCollection().replaceOne(filter, Converter.makeVertexDocument(properties, id),
				new ReplaceOptions().upsert(true));
	}

	public TreeSet<Long> getEventTimestampSet() {
		TreeSet<Long> set = new TreeSet<Long>();

		Iterator<BsonDocument> iter = graph.getVertexEventCollection().find(new BsonDocument(Tokens.VERTEX, id))
				.projection(new BsonDocument(Tokens.TIMESTAMP, new BsonBoolean(true))).iterator();
		while (iter.hasNext()) {
			BsonDocument t = iter.next();
			set.add(t.getDateTime(Tokens.TIMESTAMP).getValue());
		}

		return set;
	}

	/**
	 * Return timestampVertexEvent with given timestamp (No interaction with DB)
	 * 
	 * @param timestamp
	 * @return timestampVertexEvent
	 */
	public VertexEvent getEvent(BsonDateTime timestamp) {
		return new VertexEvent(timestamp, id, this.graph);
	}

	public Set<VertexEvent> pickEventSet(BsonDateTime t, Tokens.AC tt, Boolean awareOutVertex, Boolean awareInVertex) {

		BsonDocument base = new BsonDocument(Tokens.VERTEX, id);
		base = Converter.addTimeComparison(base, t, tt);

		TreeSet<VertexEvent> resultSet = new TreeSet<VertexEvent>();

		graph.getVertexEventCollection().find(base).projection(Tokens.PRJ_ONLY_VERTEX_TIMESTAMP)
				.map(doc -> new VertexEvent(doc.getDateTime(Tokens.TIMESTAMP), id, graph)).into(resultSet);

		if (awareOutVertex) {
			graph.getEdgeEventCollection().find(new BsonDocument(Tokens.OUT_VERTEX, id))
					.projection(Tokens.PRJ_ONLY_TIMESTAMP)
					.map(doc -> new VertexEvent(doc.getDateTime(Tokens.TIMESTAMP), id, graph)).into(resultSet);
		}

		if (awareInVertex) {
			graph.getEdgeEventCollection().find(new BsonDocument(Tokens.IN_VERTEX, id))
					.projection(Tokens.PRJ_ONLY_TIMESTAMP)
					.map(doc -> new VertexEvent(doc.getDateTime(Tokens.TIMESTAMP), id, graph)).into(resultSet);
		}

		return resultSet;
	}

	@Override
	public boolean equals(Object obj) {
		return this.toString().equals(obj.toString());
	}

	@Override
	public int hashCode() {
		return this.toString().hashCode();
	}

	@Override
	public String toString() {
		return id.getValue();
	}
	// getEvent(Time a)
	// pickEvent(Time a)
	// pickEvents(Time a, TR)
	// Iterable getEdgeEvents(Direction, Time, TR, Label)
}
