package org.chronograph.khronos.element;

import java.util.HashSet;
import java.util.Set;

import org.bson.BsonDateTime;
import org.bson.BsonDocument;
import org.bson.BsonInt32;
import org.bson.BsonString;
import org.bson.BsonValue;
import org.chronograph.khronos.common.Converter;
import org.chronograph.khronos.common.Direction;
import org.chronograph.khronos.common.Tokens;

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
public class Edge extends Element {

	private final Graph graph;
	private final BsonString outVertex;
	private final BsonString inVertex;
	private final BsonString label;

	/**
	 * Create an edge with an outgoing vertex, an edge label, an ingoing vertex, a
	 * graph
	 * 
	 * @param graph
	 * @param outVertex
	 * @param label
	 * @param inVertex
	 */
	public Edge(Graph graph, BsonString outVertex, BsonString label, BsonString inVertex) {
		this.graph = graph;
		this.outVertex = outVertex;
		this.label = label;
		this.inVertex = inVertex;
	}

	/**
	 * @return an identifier of outgoing vertex
	 */
	public BsonString getOutVertexID() {
		return this.outVertex;
	}

	public Vertex getVertex(Direction direction) {
		if (direction == Direction.OUT)
			return new Vertex(graph, outVertex);
		else
			return new Vertex(graph, inVertex);
	}

	/**
	 * @return an identifier of ingoing vertex
	 */
	public BsonString getInVertexID() {
		return this.inVertex;
	}

	/**
	 * Return the label associated with the edge.
	 *
	 * @return the edge label
	 */
	public BsonString getLabel() {
		return this.label;
	}

	/**
	 * Return a property value of a given key
	 * 
	 * @param key
	 * @return a property value
	 */
	public BsonValue getProperty(String key) {
		BsonDocument filter = Converter.makeEdgeDocument(null, outVertex, label, inVertex);
		BsonDocument doc = graph.getEdgeCollection().find(filter).first();
		if (doc == null)
			return null;
		return doc.get(key);
	}

	/**
	 * Return key-value properties of an edge
	 * 
	 * @return properties except Tokens.OUT_VERTEX, Tokens.LABEL, Tokens.IN_VERTEX
	 */
	public BsonValue getProperties() {
		BsonDocument filter = Converter.makeEdgeDocument(null, outVertex, label, inVertex);
		BsonDocument doc = graph.getEdgeCollection().find(filter).first();
		if (doc == null)
			return null;
		return Converter.removeEdgeKeys(doc);
	}

	/**
	 * Set a key-value property into an edge
	 * 
	 * @param key
	 * @param value
	 */
	public void setProperty(String key, BsonValue value) {
		BsonDocument filter = Converter.makeEdgeDocument(null, outVertex, label, inVertex);
		BsonDocument update = new BsonDocument("$set", new BsonDocument(key, value));
		graph.getEdgeCollection().updateOne(filter, update, new UpdateOptions().upsert(true));
	}

	/**
	 * Set key-value properties into an edge
	 * 
	 * @param properties
	 */
	public void setProperties(BsonDocument properties) {
		if (properties == null)
			properties = new BsonDocument();
		BsonDocument filter = Converter.makeEdgeDocument(null, outVertex, label, inVertex);
		graph.getEdgeCollection().replaceOne(filter, Converter.makeEdgeDocument(properties, outVertex, label, inVertex),
				new ReplaceOptions().upsert(true));
	}

	/**
	 * Return timestampEdgeEvent with given timestamp
	 * 
	 * @param timestamp can be null
	 * @return timestampEdgeEvent
	 */
	public EdgeEvent addEvent(BsonDateTime timestamp) {
		BsonDocument filter = Converter.makeEdgeEventDocument(null, timestamp, outVertex, label, inVertex);
		BsonDocument doc = graph.getEdgeEventCollection().find(filter)
				.projection(new BsonDocument(Tokens.ID, new BsonInt32(0))).first();
		if (doc == null) {
			graph.getEdgeEventCollection().insertOne(filter);
		}
		return new EdgeEvent(timestamp, outVertex, label, inVertex, this.graph);
	}

	/**
	 * Return timestampEdgeEvent with existing given timestamp
	 * 
	 * @param timestamp existing timestamp property key
	 * @return timestampEdgeEvent or null
	 */
	public EdgeEvent getEvent(BsonDateTime timestamp) {
		BsonDocument filter = Converter.makeEdgeEventDocument(null, timestamp, outVertex, label, inVertex);
		BsonDocument result = graph.getEdgeEventCollection().find(filter).first();
		if (result == null)
			return null;
		return new EdgeEvent(timestamp, outVertex, label, inVertex, this.graph);
	}

	public Set<EdgeEvent> pickEventSet(BsonDateTime t, Tokens.AC tt) {
		BsonDocument base = Converter.makeEdgeDocument(null, outVertex, label, inVertex);
		base = Converter.addTimeComparison(base, t, tt);

		HashSet<EdgeEvent> resultSet = new HashSet<EdgeEvent>();

		graph.getVertexEventCollection().find(base).projection(Tokens.PRJ_ONLY_TIMESTAMP)
				.map(doc -> new EdgeEvent(doc.getDateTime(Tokens.TIMESTAMP), outVertex, label, inVertex, graph))
				.into(resultSet);

		return resultSet;
	}

	@Override
	public String toString() {
		return outVertex.getValue() + "|" + label.getValue() + "|" + inVertex.getValue();
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
