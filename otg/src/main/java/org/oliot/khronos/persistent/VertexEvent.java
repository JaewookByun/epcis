package org.oliot.khronos.persistent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bson.BsonArray;
import org.bson.BsonBoolean;
import org.bson.BsonDateTime;
import org.bson.BsonDocument;
import org.bson.BsonString;
import org.bson.BsonValue;
import org.oliot.khronos.common.ExceptionFactory;
import org.oliot.khronos.common.TemporalType;
import org.oliot.khronos.common.Tokens;
import org.oliot.khronos.common.Tokens.AC;
import org.oliot.khronos.common.Tokens.Position;

import com.mongodb.Function;
import com.mongodb.MongoCursorNotFoundException;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Element;

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
public class VertexEvent implements Element {

	private ChronoVertex vertex;
	private Long timestamp;
	private TemporalType temporalType;

	protected final ChronoGraph graph;

	/**
	 * Create timestampVertexEvent
	 * 
	 * @param chronoGraph
	 * @param chronoVertex
	 * @param timestamp
	 */
	public VertexEvent(ChronoGraph graph, ChronoVertex vertex, Long timestamp) {
		this.graph = graph;
		this.vertex = vertex;
		this.timestamp = timestamp;
		this.temporalType = TemporalType.TIMESTAMP;
	}

	public VertexEvent(ChronoGraph graph, String vertexEventID) {
		this.graph = graph;
		String[] arr = vertexEventID.split("-");
		this.vertex = new ChronoVertex(arr[0], graph);
		this.timestamp = Long.parseLong(arr[1]);
		this.temporalType = TemporalType.TIMESTAMP;
	}

	public VertexEvent getThis() {
		return this;
	}

	/**
	 * @return ChronoVertex which has this
	 */
	public ChronoVertex getVertex() {
		return vertex;
	}

	public String getVertexID() {
		return vertex.toString();
	}

	/**
	 * Set ChronoVertex to have this
	 * 
	 * @param chronoVertex
	 */
	public void setVertex(ChronoVertex vertex) {
		this.vertex = vertex;
	}

	/**
	 * @return readable string representing VertexEvent
	 */
	public String toString() {
		return vertex.toString() + "-" + timestamp;
	}

	/**
	 * @return temporal type
	 */
	public TemporalType getTemporalType() {
		return this.temporalType;
	}

	/**
	 * @return current timestamp head. return null if the type is
	 *         TemporalType.INTERVAL
	 */
	public Long getTimestamp() {
		return timestamp;
	}

	/**
	 * Set timestamp. Set temporalType as TIMESTAMP. Set interval to null
	 * 
	 * @param timestamp
	 * @return timestamp
	 */
	public void setTimestamp(Long timestamp) {
		this.temporalType = TemporalType.TIMESTAMP;
		this.timestamp = timestamp;
	}

	/**
	 * Get existing next timestamp greater than equal to current timestamp
	 * 
	 * @return existing next timestamp or null if not exist or the current temporal
	 *         type is INTERVAL
	 */
	public Long getCeilingTimestamp() {
		return vertex.getCeilingTimestamp(timestamp);
	}

	/**
	 * Get existing higher timestamp based on current timestamp
	 * 
	 * @return existing higher timestamp or null if not exist or the current
	 *         temporal type is INTERVAL
	 */
	public Long getHigherTimestamp() {
		return vertex.getHigherTimestamp(timestamp);
	}

	/**
	 * Set existing ceiling timestamp to current timestamp
	 * 
	 * @return 1) ceiling timestamp if exists. 2) null if current type is INTERVAL
	 */
	public Long setCeilingTimestamp() {
		Long ceiling = getCeilingTimestamp();
		if (ceiling != null) {
			timestamp = ceiling;
			return timestamp;
		} else
			return null;
	}

	/**
	 * Set existing higher timestamp to current timestamp
	 * 
	 * @return 1) next timestamp if exists. 2) current timestamp if there is no next
	 *         timestamp 3) null if current type is INTERVAL
	 */
	public Long setHigherTimestamp() {
		if (temporalType.equals(TemporalType.INTERVAL))
			return null;
		Long higher = getHigherTimestamp();
		if (higher != null) {
			timestamp = higher;
			return timestamp;
		} else
			return null;
	}

	/**
	 * Get existing previous timestamp less than equal to current timestamp
	 * 
	 * @return existing previous timestamp or null if not exist or the current
	 *         temporal type is INTERVAL
	 */
	public Long getFloorTimestamp() {
		return vertex.getFloorTimestamp(timestamp);
	}

	/**
	 * Get existing lower timestamp based on current timestamp
	 * 
	 * @return existing lower timestamp or null if not exist or the current temporal
	 *         type is INTERVAL
	 */
	public Long getLowerTimestamp() {
		return vertex.getLowerTimestamp(timestamp);
	}

	/**
	 * Set existing floor timestamp to current timestamp
	 * 
	 * @return 1) floor timestamp if exists. 2) null if current type is INTERVAL
	 */
	public Long setFloorTimestamp() {
		Long floor = getFloorTimestamp();
		if (floor != null) {
			timestamp = floor;
			return timestamp;
		} else
			return null;
	}

	/**
	 * Set existing lower timestamp to current timestamp
	 * 
	 * @return 1) lower timestamp if exists. 2) current timestamp if there is no
	 *         next timestamp 3) null if current type is INTERVAL
	 */
	public Long setLowerTimestamp() {
		if (temporalType.equals(TemporalType.INTERVAL))
			return null;
		Long lower = getLowerTimestamp();
		if (lower != null) {
			timestamp = lower;
			return timestamp;
		} else
			return null;
	}

	/**
	 * 
	 * @param direction
	 * @param labels
	 * @param edgeType
	 * @param timestampPosition
	 * @param ss
	 * @param se
	 * @param es
	 * @param ee
	 * @return
	 */
	public Iterable<EdgeEvent> getEdgeEvents(final Direction direction, final BsonArray labels,
			TemporalType typeOfEdgeEvent, final AC tt, final AC s, final AC e, final AC ss, final AC se, final AC es,
			final AC ee) {
		if (typeOfEdgeEvent == null)
			typeOfEdgeEvent = this.temporalType;

		Set<ChronoEdge> edgeSet = vertex.getChronoEdgeSet(direction, labels, Integer.MAX_VALUE);

		List<EdgeEvent> ret = edgeSet.parallelStream().map(edge -> {
			Long t = edge.getTimestamp(timestamp, tt);
			return edge.pickTimestamp(t);
		}).filter(edgeEvent -> exFilter(edgeEvent)).collect(Collectors.toList());
		return ret;
	}

	private boolean exFilter(EdgeEvent ee) {
		if (ee == null)
			return false;
		return true;
	}

	public Stream<EdgeEvent> getEdgeEventStream(final Direction direction, final BsonArray labels,
			TemporalType typeOfEdgeEvent, final AC tt, final AC s, final AC e, final AC ss, final AC se, final AC es,
			final AC ee, final boolean setParallel) {
		if (typeOfEdgeEvent == null)
			typeOfEdgeEvent = this.temporalType;

		Stream<ChronoEdge> edgeStream = vertex.getChronoEdgeStream(direction, labels, Integer.MAX_VALUE, setParallel);

		// T -> T
		return edgeStream.map(edge -> {
			Long t = edge.getTimestamp(timestamp, tt);
			return edge.pickTimestamp(t);
		}).filter(edgeEvent -> exFilter(edgeEvent));
	}

	public Set<EdgeEvent> getEdgeEventSet(final Direction direction, final BsonArray labels,
			TemporalType typeOfEdgeEvent, final AC tt, final AC s, final AC e, final AC ss, final AC se, final AC es,
			final AC ee) {
		if (typeOfEdgeEvent == null)
			typeOfEdgeEvent = this.temporalType;

		Set<ChronoEdge> edgeSet = vertex.getChronoEdgeSet(direction, labels, Integer.MAX_VALUE);

		// T -> T
		Set<EdgeEvent> ret = edgeSet.parallelStream().map(edge -> {
			Long t = edge.getTimestamp(timestamp, tt);
			return edge.pickTimestamp(t);
		}).filter(edgeEvent -> exFilter(edgeEvent)).collect(Collectors.toSet());
		return ret;
	}

	public Stream<VertexEvent> getVertexEventStream(final Direction direction, final BsonArray labels,
			TemporalType typeOfVertexEvent, final AC tt, final AC s, final AC e, final AC ss, final AC se, final AC es,
			final AC ee, final Position pos, final boolean setParallel) {

		if (typeOfVertexEvent == null)
			typeOfVertexEvent = this.temporalType;

		final Stream<ChronoEdge> edgeStream = vertex.getChronoEdgeStream(direction, labels, Integer.MAX_VALUE,
				setParallel);

		// T -> T
		return edgeStream.map(edge -> {
			Long t = edge.getTimestamp(timestamp, tt);
			return edge.pickTimestamp(t);
		}).filter(edge -> edge != null).map(edgeEvent -> edgeEvent.getVertexEvent(direction.opposite()));
	}

	public Set<VertexEvent> getBothVertexEventSet(final String label, final AC tt) {
		HashSet<VertexEvent> ret = new HashSet<VertexEvent>();

		HashMap<String, Long> map = new HashMap<String, Long>();

		// db.edges.createIndex({"_outV" : 1, "_label" : 1, "_t" : 1, "_inV" : 1})
		BsonDocument query1 = new BsonDocument(Tokens.IN_VERTEX, new BsonString(vertex.toString()));
		query1.append(Tokens.LABEL, new BsonString(label));
		query1.append(Tokens.TIMESTAMP, new BsonDocument(tt.toString(), new BsonDateTime(timestamp)));
		BsonDocument proj1 = new BsonDocument(Tokens.TIMESTAMP, new BsonBoolean(true))
				.append(Tokens.OUT_VERTEX, new BsonBoolean(true)).append(Tokens.ID, new BsonBoolean(false));

		Iterator<BsonDocument> x1 = vertex.graph.getEdgeEvents().find(query1).projection(proj1).iterator();

		while (x1.hasNext()) {
			BsonDocument d = x1.next();
			String outV = d.getString(Tokens.OUT_VERTEX).getValue();
			Long t = d.getDateTime(Tokens.TIMESTAMP).getValue();
			if (map.containsKey(outV)) {
				if (map.get(outV) > t)
					map.put(outV, t);
			} else
				map.put(outV, t);
		}

		// db.edges.createIndex({"_outV" : 1, "_label" : 1, "_t" : 1, "_inV" : 1})
		BsonDocument query2 = new BsonDocument(Tokens.OUT_VERTEX, new BsonString(vertex.toString()));
		query2.append(Tokens.LABEL, new BsonString(label));
		query2.append(Tokens.TIMESTAMP, new BsonDocument(tt.toString(), new BsonDateTime(timestamp)));
		BsonDocument proj2 = new BsonDocument(Tokens.TIMESTAMP, new BsonBoolean(true))
				.append(Tokens.IN_VERTEX, new BsonBoolean(true)).append(Tokens.ID, new BsonBoolean(false));

		Iterator<BsonDocument> x2 = vertex.graph.getEdgeEvents().find(query2).projection(proj2).iterator();

		while (x2.hasNext()) {
			BsonDocument d = x2.next();
			String inV = d.getString(Tokens.IN_VERTEX).getValue();
			Long t = d.getDateTime(Tokens.TIMESTAMP).getValue();
			if (map.containsKey(inV)) {
				if (map.get(inV) > t)
					map.put(inV, t);
			} else
				map.put(inV, t);
		}

		map.entrySet().parallelStream().forEach(entry -> {
			VertexEvent ve = new VertexEvent(graph, entry.getKey() + "-" + entry.getValue());
			ret.add(ve);
		});
		return ret;
	}

	public Set<VertexEvent> getOutVertexEventSet(final String label, final AC tt) {

		// db.edges.createIndex({"_outV" : 1, "_label" : 1, "_t" : 1, "_inV" : 1})
		BsonDocument query = new BsonDocument(Tokens.OUT_VERTEX, new BsonString(vertex.toString()));
		query.append(Tokens.LABEL, new BsonString(label));
		query.append(Tokens.TIMESTAMP, new BsonDocument(tt.toString(), new BsonDateTime(timestamp)));
		BsonDocument proj = new BsonDocument(Tokens.TIMESTAMP, new BsonBoolean(true))
				.append(Tokens.IN_VERTEX, new BsonBoolean(true)).append(Tokens.ID, new BsonBoolean(false));

		HashSet<VertexEvent> ret = new HashSet<VertexEvent>();
		Iterator<BsonDocument> x = vertex.graph.getEdgeEvents().find(query).projection(proj).iterator();
		HashMap<String, Long> map = new HashMap<String, Long>();
		while (x.hasNext()) {
			BsonDocument d = x.next();
			String inV = d.getString(Tokens.IN_VERTEX).getValue();
			Long t = d.getDateTime(Tokens.TIMESTAMP).getValue();
			if (map.containsKey(inV)) {
				if (map.get(inV) > t)
					map.put(inV, t);
			} else
				map.put(inV, t);
		}

		map.entrySet().parallelStream().forEach(entry -> {
			VertexEvent ve = new VertexEvent(graph, entry.getKey() + "-" + entry.getValue());
			ret.add(ve);
		});
		return ret;
	}

	public Set<VertexEvent> getInVertexEventSet(final String label, final AC tt) {

		// db.edges.createIndex({"_outV" : 1, "_label" : 1, "_t" : 1, "_inV" : 1})
		BsonDocument query = new BsonDocument(Tokens.IN_VERTEX, new BsonString(vertex.toString()));
		query.append(Tokens.LABEL, new BsonString(label));
		query.append(Tokens.TIMESTAMP, new BsonDocument(tt.toString(), new BsonDateTime(timestamp)));
		BsonDocument proj = new BsonDocument(Tokens.TIMESTAMP, new BsonBoolean(true))
				.append(Tokens.OUT_VERTEX, new BsonBoolean(true)).append(Tokens.ID, new BsonBoolean(false));

		HashSet<VertexEvent> ret = new HashSet<VertexEvent>();
		Iterator<BsonDocument> x = vertex.graph.getEdgeCollection().find(query).projection(proj).iterator();
		HashMap<String, Long> map = new HashMap<String, Long>();
		while (x.hasNext()) {
			BsonDocument d = x.next();
			String outV = d.getString(Tokens.OUT_VERTEX).getValue();
			Long t = d.getDateTime(Tokens.TIMESTAMP).getValue();
			if (map.containsKey(outV)) {
				if (map.get(outV) > t)
					map.put(outV, t);
			} else
				map.put(outV, t);
		}

		map.entrySet().parallelStream().forEach(entry -> {
			VertexEvent ve = new VertexEvent(graph, entry.getKey() + "-" + entry.getValue());
			ret.add(ve);
		});
		return ret;
	}

	public Set<VertexEvent> getOutVertexEventSet(final AC tt) {
		while (true) {
			try {
				// db.tEdgeEvents.aggregate([{$match:{"_o":"1","_t":{ $lt : ISODate(0)
				// }}},{$project:{"_i":1,"_t":1,"_id":0}},{$group:{"_id":"$_i", "_mt": {$min:
				// "$_t"}}}])
				BsonDocument match = new BsonDocument("$match",
						new BsonDocument(Tokens.OUT_VERTEX, new BsonString(vertex.toString())).append(Tokens.TIMESTAMP,
								new BsonDocument("$gt", new BsonDateTime(timestamp))));
				BsonDocument project = new BsonDocument("$project",
						new BsonDocument(Tokens.IN_VERTEX, new BsonBoolean(true))
								.append(Tokens.TIMESTAMP, new BsonBoolean(true))
								.append(Tokens.ID, new BsonBoolean(false)));
				BsonDocument group = new BsonDocument("$group",
						new BsonDocument(Tokens.ID, new BsonString("$" + Tokens.IN_VERTEX)).append(Tokens.TIMESTAMP,
								new BsonDocument("$min", new BsonString("$" + Tokens.TIMESTAMP))));

				ArrayList<BsonDocument> aggregateQuery = new ArrayList<BsonDocument>();
				aggregateQuery.add(match);
				aggregateQuery.add(project);
				aggregateQuery.add(group);

				HashSet<VertexEvent> ret = new HashSet<VertexEvent>();
				Function<BsonDocument, VertexEvent> mapper = new Function<BsonDocument, VertexEvent>() {
					@Override
					public VertexEvent apply(BsonDocument d) {
						String inV = d.getString(Tokens.ID).getValue();
						Long t = d.getDateTime(Tokens.TIMESTAMP).getValue();
						return new VertexEvent(graph, new ChronoVertex(inV, graph), t);
					}
				};
				vertex.graph.getEdgeEvents().aggregate(aggregateQuery).map(mapper).into(ret);

				return ret;

			} catch (MongoCursorNotFoundException e1) {
				System.out.println(e1.getErrorMessage());
			}
		}
	}

	public Set<VertexEvent> getVertexEventSet(final Direction direction, final BsonArray labels,
			TemporalType typeOfVertexEvent, final AC tt, final AC s, final AC e, final AC ss, final AC se, final AC es,
			final AC ee, Position pos) {

		while (true) {
			try {

				// db.tEdgeEvents.aggregate([{$match:{"_o":"1","_t":{ $lt : ISODate(0)
				// }}},{$project:{"_i":1,"_t":1,"_id":0}},{$group:{"_id":"$_i", "_mt": {$min:
				// "$_t"}}}])

				BsonDocument match = new BsonDocument("$match",
						new BsonDocument(Tokens.OUT_VERTEX, new BsonString(vertex.toString())).append(Tokens.TIMESTAMP,
								new BsonDocument("$gt", new BsonDateTime(timestamp))));
				BsonDocument project = new BsonDocument("$project",
						new BsonDocument(Tokens.IN_VERTEX, new BsonBoolean(true))
								.append(Tokens.TIMESTAMP, new BsonBoolean(true))
								.append(Tokens.ID, new BsonBoolean(false)));
				BsonDocument group = new BsonDocument("$group",
						new BsonDocument(Tokens.ID, new BsonString("$" + Tokens.IN_VERTEX)).append(Tokens.TIMESTAMP,
								new BsonDocument("$min", new BsonString("$" + Tokens.TIMESTAMP))));

				ArrayList<BsonDocument> aggregateQuery = new ArrayList<BsonDocument>();
				aggregateQuery.add(match);
				aggregateQuery.add(project);
				aggregateQuery.add(group);

				HashSet<VertexEvent> ret = new HashSet<VertexEvent>();
				Function<BsonDocument, VertexEvent> mapper = new Function<BsonDocument, VertexEvent>() {
					@Override
					public VertexEvent apply(BsonDocument d) {
						String inV = d.getString(Tokens.ID).getValue();
						Long t = d.getDateTime(Tokens.TIMESTAMP).getValue();
						return new VertexEvent(graph, new ChronoVertex(inV, graph), t);
					}

				};
				vertex.graph.getEdgeEvents().aggregate(aggregateQuery).map(mapper).into(ret);
				return ret;

				// db.edges.createIndex({"_outV" : 1, "_t" : 1, "_inV" : 1})
				// db.EventData.createIndex({"inputEPCList.epc":1})
				// BsonDocument query = new BsonDocument(Tokens.OUT_VERTEX, new
				// BsonString(vertex.toString()));
				// query.append("_t", new BsonDocument("$gt", new BsonDateTime(timestamp)));
				// BsonDocument proj = new BsonDocument("_t", new BsonBoolean(true))
				// .append(Tokens.IN_VERTEX, new BsonBoolean(true)).append(Tokens.ID, new
				// BsonBoolean(false));
				//
				// // outV
				// // label
				// // t > gte
				// HashSet<VertexEvent> ret = new HashSet<VertexEvent>();
				//
				// Iterator<BsonDocument> x =
				// vertex.graph.getEdgeCollection().find(query).noCursorTimeout(true)
				// .projection(proj).iterator();
				// while (x.hasNext()) {
				// BsonDocument d = x.next();
				// String inV = d.getString("_inV").getValue();
				// Long t = d.getDateTime("_t").getValue();
				// VertexEvent ve = new VertexEvent(graph, inV + "-" + t);
				// ret.add(ve);
				// }
				// return ret;
			} catch (MongoCursorNotFoundException e1) {
				System.out.println(e1.getErrorMessage());
			}
		}

		// previous logic backup
		// if (typeOfVertexEvent == null)
		// typeOfVertexEvent = this.temporalType;
		//
		// Set<ChronoEdge> edgeSet = vertex.getChronoEdgeSet(direction, labels,
		// Integer.MAX_VALUE);
		//
		// if (temporalType.equals(TemporalType.TIMESTAMP) &&
		// typeOfVertexEvent.equals(TemporalType.TIMESTAMP)) {
		// // T -> T
		// return edgeSet.parallelStream().map(edge -> {
		// Long t = edge.getTimestamp(timestamp, tt);
		// return edge.pickTimestamp(t);
		// }).filter(edge -> edge != null).map(edgeEvent ->
		// edgeEvent.getVertexEvent(direction.opposite()))
		// .collect(Collectors.toSet());
		// } else if (temporalType.equals(TemporalType.TIMESTAMP) &&
		// typeOfVertexEvent.equals(TemporalType.TIMESTAMP)) {
		// // T -> I
		// return edgeSet.parallelStream().map(edge -> {
		// TreeSet<LongInterval> intvSet = edge.getIntervals(timestamp, s, e);
		// if (intvSet.isEmpty())
		// return null;
		// if (pos.equals(Position.first))
		// return edge.pickInterval(intvSet.first());
		// else
		// return edge.pickInterval(intvSet.last());
		// }).filter(edge -> edge != null).map(edgeEvent ->
		// edgeEvent.getVertexEvent(direction.opposite()))
		// .collect(Collectors.toSet());
		// } else if (temporalType.equals(TemporalType.TIMESTAMP) &&
		// typeOfVertexEvent.equals(TemporalType.TIMESTAMP)) {
		// // I -> T
		// return edgeSet.parallelStream().map(edge -> {
		// TreeSet<Long> tSet = edge.getTimestamps(interval, s, e);
		// if (tSet.isEmpty())
		// return null;
		// return edge.pickTimestamp(tSet.first());
		// }).filter(edge -> edge != null).map(edgeEvent ->
		// edgeEvent.getVertexEvent(direction.opposite()))
		// .collect(Collectors.toSet());
		// } else {
		// // I -> I
		// return edgeSet.parallelStream().map(edge -> {
		// TreeSet<LongInterval> intvSet = edge.getIntervals(interval, ss, se, es, ee);
		// if (intvSet.isEmpty())
		// return null;
		// if (pos.equals(Position.first))
		// return edge.pickInterval(intvSet.first());
		// else
		// return edge.pickInterval(intvSet.last());
		// }).filter(edge -> edge != null).map(edgeEvent ->
		// edgeEvent.getVertexEvent(direction.opposite()))
		// .collect(Collectors.toSet());
		// }
	}

	public Iterable<VertexEvent> getVertexEvents(final Direction direction, final BsonArray labels,
			TemporalType typeOfVertexEvent, final AC tt, final AC s, final AC e, final AC ss, final AC se, final AC es,
			final AC ee, Position pos) {

		if (typeOfVertexEvent == null)
			typeOfVertexEvent = this.temporalType;

		Set<ChronoEdge> edgeSet = vertex.getChronoEdgeSet(direction, labels, Integer.MAX_VALUE);

		// T -> T
		return edgeSet.parallelStream().map(edge -> {
			Long t = edge.getTimestamp(timestamp, tt);
			return edge.pickTimestamp(t);
		}).filter(edge -> edge != null).map(edgeEvent -> edgeEvent.getVertexEvent(direction.opposite()))
				.collect(Collectors.toSet());
	}

	/**
	 * @param key
	 * @return timestamp properties or interval properties
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> T getProperty(String key) {
		return (T) vertex.getTimestampPropertyValue(timestamp, key);
	}

	/**
	 * @deprecated use getTimestampPropertyKeys or getIntervalPropertyKeys
	 */
	@Deprecated
	@Override
	public Set<String> getPropertyKeys() {
		return vertex.getPropertyKeys();
	}

	/**
	 * @return timestamps
	 */
	public Set<Long> getTimestampPropertyKeys() {
		return vertex.getTimestamps();
	}

	/**
	 * set key value only active at the timestamp or interval
	 * 
	 * @param key
	 * @param value
	 */
	@Override
	public void setProperty(String key, Object value) {
		if (!(value instanceof BsonValue))
			throw ExceptionFactory.propertyValueShouldBeInstanceOfBsonValue();
		else
			vertex.setTimestampProperty(timestamp, key, (BsonValue) value);
	}

	@Override
	public <T> T removeProperty(String key) {
		return vertex.removeProperty(key);
	}

	@Deprecated
	@Override
	public void remove() {
		vertex.remove();
	}

	@Override
	public Object getId() {
		return this.vertex.id;
	}

}
