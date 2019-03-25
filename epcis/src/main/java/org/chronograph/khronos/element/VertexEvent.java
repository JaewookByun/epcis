package org.chronograph.khronos.element;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

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
public class VertexEvent extends Element implements Comparable<VertexEvent> {
	private final BsonDateTime timestamp;
	private final BsonString vertex;

	private final Graph graph;

	public VertexEvent getThis() {
		return this;
	}

	public BsonString getVertexID() {
		return vertex;
	}

	public BsonDateTime getTimestamp() {
		return timestamp;
	}

	public VertexEvent(BsonDateTime timestamp, BsonString vertex, Graph graph) {
		this.timestamp = timestamp;
		this.vertex = vertex;
		this.graph = graph;
	}

	public TreeSet<VertexEvent> getVertexEventSet(Tokens.AC tt) {

		BsonDocument base = new BsonDocument(Tokens.VERTEX, vertex);
		base = Converter.addTimeComparison(base, timestamp, tt);

		TreeSet<VertexEvent> resultSet = new TreeSet<VertexEvent>();

		graph.getVertexEventCollection().find(base).projection(Tokens.PRJ_ONLY_VERTEX_TIMESTAMP)
				.map(doc -> new VertexEvent(doc.getDateTime(Tokens.TIMESTAMP), vertex, graph)).into(resultSet);

		return resultSet;
	}

	/**
	 * 
	 * @return vertex event properties as BsonDocument
	 */
	public BsonDocument getProperties() {
		BsonDocument filter = Converter.makeVertexEventDocument(null, timestamp, vertex);
		BsonDocument result = graph.getVertexEventCollection().find(filter).first();
		result.remove(Tokens.ID);
		result.remove(Tokens.TIMESTAMP);
		result.remove(Tokens.VERTEX);
		return result;
	}

	/**
	 * Update property
	 * 
	 * @param key
	 * @param value
	 */
	public void setProperty(String key, BsonValue value) {
		BsonDocument filter = Converter.makeVertexEventDocument(null, timestamp, vertex);
		graph.getVertexEventCollection().updateOne(filter, new BsonDocument("$set", new BsonDocument(key, value)),
				new UpdateOptions().upsert(true));
	}

	public void removeProperty(String key) {
		BsonDocument filter = Converter.makeVertexEventDocument(null, timestamp, vertex);
		graph.getVertexEventCollection().updateOne(filter,
				new BsonDocument("$unset", new BsonDocument(key, new BsonBoolean(true))),
				new UpdateOptions().upsert(true));
	}

	public Set<EdgeEvent> getEdgeEvents(Direction direction, BsonString label, Tokens.AC tt, Tokens.FC minMax) {
		// db.edges.createIndex({"_outV" : 1, "_label" : 1, "_t" : 1, "_inV" : 1})

		if (direction == Direction.OUT) {
			BsonDocument filter = new BsonDocument(Tokens.OUT_VERTEX, vertex);
			filter = Converter.addTimeComparison(filter, timestamp, tt);

			Iterator<BsonDocument> iterator = graph.getEdgeEventCollection().find(filter)
					.projection(Tokens.PRJ_ONLY_INV_TIMESTAMP).iterator();
			HashMap<BsonString, Long> map = new HashMap<BsonString, Long>();
			while (iterator.hasNext()) {
				BsonDocument doc = iterator.next();
				BsonString inV = doc.getString(Tokens.IN_VERTEX);
				Long t = doc.getDateTime(Tokens.TIMESTAMP).getValue();
				if (minMax == Tokens.FC.$min) {
					if (map.containsKey(inV) && map.get(inV) > t) {
						map.put(inV, t);
					} else {
						map.put(inV, t);
					}
				} else {
					if (map.containsKey(inV) && map.get(inV) < t) {
						map.put(inV, t);
					} else {
						map.put(inV, t);
					}
				}
			}
			return map.entrySet().parallelStream().map(
					entry -> new EdgeEvent(new BsonDateTime(entry.getValue()), vertex, label, entry.getKey(), graph))
					.collect(Collectors.toSet());
		} else {
			BsonDocument filter = new BsonDocument(Tokens.IN_VERTEX, vertex);
			filter = Converter.addTimeComparison(filter, timestamp, tt);

			Iterator<BsonDocument> iterator = graph.getEdgeEventCollection().find(filter)
					.projection(Tokens.PRJ_ONLY_OUTV_TIMESTAMP).iterator();
			HashMap<BsonString, Long> map = new HashMap<BsonString, Long>();
			while (iterator.hasNext()) {
				BsonDocument doc = iterator.next();
				BsonString outV = doc.getString(Tokens.OUT_VERTEX);
				Long t = doc.getDateTime(Tokens.TIMESTAMP).getValue();
				if (minMax == Tokens.FC.$min) {
					if (map.containsKey(outV) && map.get(outV) > t) {
						map.put(outV, t);
					} else {
						map.put(outV, t);
					}
				} else {
					if (map.containsKey(outV) && map.get(outV) < t) {
						map.put(outV, t);
					} else {
						map.put(outV, t);
					}
				}
			}
			return map.entrySet().parallelStream().map(
					entry -> new EdgeEvent(new BsonDateTime(entry.getValue()), entry.getKey(), label, vertex, graph))
					.collect(Collectors.toSet());
		}

	}

	public Set<VertexEvent> getVertexEvents(Direction direction, BsonString label, Tokens.AC tt, Tokens.FC minMax) {
		if (direction == Direction.OUT) {
			BsonDocument filter = new BsonDocument(Tokens.OUT_VERTEX, vertex);
			filter = Converter.addTimeComparison(filter, timestamp, tt);

			Iterator<BsonDocument> iterator = graph.getEdgeEventCollection().find(filter)
					.projection(Tokens.PRJ_ONLY_INV_TIMESTAMP).iterator();
			HashMap<BsonString, Long> map = new HashMap<BsonString, Long>();
			while (iterator.hasNext()) {
				BsonDocument doc = iterator.next();
				BsonString inV = doc.getString(Tokens.IN_VERTEX);
				Long t = doc.getDateTime(Tokens.TIMESTAMP).getValue();
				if (minMax == Tokens.FC.$min) {
					if (map.containsKey(inV) && map.get(inV) > t) {
						map.put(inV, t);
					} else {
						map.put(inV, t);
					}
				} else {
					if (map.containsKey(inV) && map.get(inV) < t) {
						map.put(inV, t);
					} else {
						map.put(inV, t);
					}
				}
			}
			return map.entrySet().parallelStream()
					.map(entry -> new VertexEvent(new BsonDateTime(entry.getValue()), entry.getKey(), graph))
					.collect(Collectors.toSet());
		} else {
			BsonDocument filter = new BsonDocument(Tokens.IN_VERTEX, vertex);
			filter = Converter.addTimeComparison(filter, timestamp, tt);

			Iterator<BsonDocument> iterator = graph.getEdgeEventCollection().find(filter)
					.projection(Tokens.PRJ_ONLY_OUTV_TIMESTAMP).iterator();
			HashMap<BsonString, Long> map = new HashMap<BsonString, Long>();
			while (iterator.hasNext()) {
				BsonDocument doc = iterator.next();
				BsonString outV = doc.getString(Tokens.OUT_VERTEX);
				Long t = doc.getDateTime(Tokens.TIMESTAMP).getValue();
				if (minMax == Tokens.FC.$min) {
					if (map.containsKey(outV) && map.get(outV) > t) {
						map.put(outV, t);
					} else {
						map.put(outV, t);
					}
				} else {
					if (map.containsKey(outV) && map.get(outV) < t) {
						map.put(outV, t);
					} else {
						map.put(outV, t);
					}
				}
			}
			return map.entrySet().parallelStream()
					.map(entry -> new VertexEvent(new BsonDateTime(entry.getValue()), entry.getKey(), graph))
					.collect(Collectors.toSet());
		}
	}

	@Override
	public String toString() {
		return vertex.getValue() + "-" + timestamp.getValue();
	}

	@Override
	public boolean equals(Object obj) {
		return this.toString().equals(obj.toString());
	}

	@Override
	public int hashCode() {
		return this.toString().hashCode();
	}

	/**
	 * Compares this object with the specified object for order. Returns anegative
	 * integer, zero, or a positive integer as this object is lessthan, equal to, or
	 * greater than the specified object.
	 */
	@Override
	public int compareTo(VertexEvent o) {
		if (this.timestamp.getValue() < o.getTimestamp().getValue())
			return -1;
		else if (this.timestamp.getValue() == o.getTimestamp().getValue())
			return 0;
		return +1;
	}
}
