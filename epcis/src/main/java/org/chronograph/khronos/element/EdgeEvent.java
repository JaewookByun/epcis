package org.chronograph.khronos.element;

import org.bson.BsonBoolean;
import org.bson.BsonDateTime;
import org.bson.BsonDocument;
import org.bson.BsonString;
import org.bson.BsonValue;
import org.chronograph.khronos.common.Converter;
import org.chronograph.khronos.common.Direction;
import org.chronograph.khronos.common.Tokens;

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
public class EdgeEvent extends Element implements Comparable<EdgeEvent> {
	private final BsonDateTime timestamp;
	private final BsonString outVertex;
	private final BsonString label;
	private final BsonString inVertex;
	private final Graph graph;

	public BsonDateTime getTimestamp() {
		return timestamp;
	}

	public BsonString getOutVertex() {
		return outVertex;
	}

	public BsonString getLabel() {
		return label;
	}

	public BsonString getInVertex() {
		return inVertex;
	}

	public Graph getGraph() {
		return graph;
	}

	public EdgeEvent(BsonDateTime timestamp, BsonString outVertex, BsonString label, BsonString inVertex, Graph graph) {
		this.timestamp = timestamp;
		this.outVertex = outVertex;
		this.label = label;
		this.inVertex = inVertex;
		this.graph = graph;
	}

	/**
	 * 
	 * @return vertex event properties as BsonDocument
	 */
	public BsonDocument getProperties() {
		BsonDocument filter = Converter.makeEdgeEventDocument(null, timestamp, outVertex, label, inVertex);
		BsonDocument result = graph.getEdgeEventCollection().find(filter).first();
		result.remove(Tokens.ID);
		result.remove(Tokens.TIMESTAMP);
		result.remove(Tokens.OUT_VERTEX);
		result.remove(Tokens.LABEL);
		result.remove(Tokens.IN_VERTEX);
		return result;
	}

	/**
	 * Update property
	 * 
	 * @param key
	 * @param value
	 */
	public void setProperty(String key, BsonValue value) {
		BsonDocument filter = Converter.makeEdgeEventDocument(null, timestamp, outVertex, label, inVertex);
		graph.getEdgeEventCollection().updateOne(filter, new BsonDocument("$set", new BsonDocument(key, value)),
				new UpdateOptions().upsert(true));
	}
	
	public void removeProperty(String key) {
		BsonDocument filter = Converter.makeEdgeEventDocument(null, timestamp, outVertex, label, inVertex);
		graph.getEdgeEventCollection().updateOne(filter,
				new BsonDocument("$unset", new BsonDocument(key, new BsonBoolean(true))),
				new UpdateOptions().upsert(true));
	}

	public VertexEvent getVertexEvent(Direction direction) {
		if (direction == Direction.OUT)
			return new VertexEvent(timestamp, outVertex, graph);
		else
			return new VertexEvent(timestamp, inVertex, graph);
	}

	@Override
	public String toString() {
		return outVertex.getValue() + "|" + label.getValue() + "|" + inVertex.getValue() + "-" + timestamp.getValue();
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
	public int compareTo(EdgeEvent o) {
		if (this.timestamp.getValue() < o.getTimestamp().getValue())
			return -1;
		else if (this.timestamp.getValue() == o.getTimestamp().getValue())
			return 0;
		return +1;
	}
}
