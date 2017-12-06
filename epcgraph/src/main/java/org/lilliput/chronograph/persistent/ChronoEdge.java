package org.lilliput.chronograph.persistent;

import com.mongodb.client.MongoCursor;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.bson.BsonDocument;
import org.bson.BsonString;
import org.lilliput.chronograph.common.ExceptionFactory;
import org.lilliput.chronograph.common.LongInterval;
import org.lilliput.chronograph.common.Tokens;
import org.lilliput.chronograph.common.Tokens.AC;
import org.lilliput.chronograph.common.Tokens.Position;

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
public class ChronoEdge extends ChronoElement implements Edge {

	private final String label;
	private final ChronoVertex outVertex;
	private final ChronoVertex inVertex;

	/**
	 * Create Edge
	 * 
	 * @param id
	 * @param outVertex
	 * @param inVertex
	 * @param label
	 * @param graph
	 */
	protected ChronoEdge(final String id, ChronoVertex outVertex, ChronoVertex inVertex, final String label,
			final ChronoGraph graph) {
		super(id, graph);
		this.label = label;
		this.outVertex = outVertex;
		this.inVertex = inVertex;
	}

	public ChronoVertex getOutVertex() {
		return this.outVertex;
	}

	public ChronoVertex getInVertex() {
		return this.inVertex;
	}

	public List<ChronoVertex> getBothVertexList() {
		List<ChronoVertex> bothV = new ArrayList<ChronoVertex>();
		bothV.add(outVertex);
		bothV.add(inVertex);
		return bothV;
	}

	public List<ChronoVertex> getBothVertexStream(final boolean setParallel) {
		List<ChronoVertex> bothV = new ArrayList<ChronoVertex>();
		bothV.add(outVertex);
		bothV.add(inVertex);
		return bothV;
	}

	/**
	 * Create Edge
	 * 
	 * @param id
	 * @param outVertexID
	 * @param inVertexID
	 * @param label
	 * @param graph
	 */
	protected ChronoEdge(final String id, final String outVertexID, final String inVertexID, final String label,
			final ChronoGraph graph) {
		super(id, graph);
		this.label = label;
		this.outVertex = new ChronoVertex(outVertexID, graph);
		this.inVertex = new ChronoVertex(inVertexID, graph);
	}

	/**
	 * Create ChronoEdge
	 * 
	 * @param id:
	 *            outVertexID|label|inVertexID
	 * @param outVertexID
	 * @param inVertexID
	 * @param label
	 * @param graph
	 */
	protected ChronoEdge(final String id, final ChronoGraph graph) {
		super(id, graph);
		String[] e = id.split("\\|");
		this.outVertex = new ChronoVertex(e[0], graph);
		this.label = e[1];
		this.inVertex = new ChronoVertex(e[2], graph);
	}

	/**
	 * Return the tail/out or head/in vertex.
	 *
	 * @param direction
	 *            whether to return the tail/out or head/in vertex
	 * @return the tail/out or head/in vertex
	 * @throws IllegalArgumentException
	 *             is thrown if a direction of both is provided
	 */
	public ChronoVertex getChronoVertex(final Direction direction) throws IllegalArgumentException {
		if (direction.equals(Direction.IN))
			return this.inVertex;
		else if (direction.equals(Direction.OUT))
			return this.outVertex;
		else
			throw ExceptionFactory.bothIsNotSupported();
	}

	/**
	 * Return the label associated with the edge.
	 *
	 * @return the edge label
	 */
	@Override
	public String getLabel() {
		return this.label;
	}

	/**
	 * Return timestampEdgeEvent with given timestamp
	 * 
	 * @param timestamp
	 *            can be null
	 * @return timestampEdgeEvent
	 */
	public EdgeEvent setTimestamp(Long timestamp) {
		return new EdgeEvent(this.graph, this, timestamp);
	}

	/**
	 * Return timestampEdgeEvent with given timestamp
	 * 
	 * @param interval
	 * @param pos
	 * @return
	 */
	public EdgeEvent setTimestamp(LongInterval interval, Position pos) {
		return new EdgeEvent(this.graph, this, interval.getTimestamp(pos));
	}

	/**
	 * Return intervalEdgeEvent with given interval
	 * 
	 * @param interval
	 *            can be null
	 * @return intervalEdgeEvent
	 */
	public EdgeEvent setInterval(LongInterval interval) {
		return new EdgeEvent(this.graph, this, interval);
	}

	/**
	 * Return timestampEdgeEvent with existing given timestamp
	 * 
	 * @param timestamp
	 *            existing timestamp property key
	 * @return timestampEdgeEvent or null
	 */
	public EdgeEvent pickTimestamp(Long timestamp) {
		if (this.getTimestampProperties(timestamp) != null) {
			return new EdgeEvent(this.graph, this, timestamp);
		}
		return null;
	}

	/**
	 * Return timestampEdgeEvent with existing given timestamp
	 * 
	 * @param interval
	 * @param pos
	 * @return
	 */
	public EdgeEvent pickTimestamp(LongInterval interval, Position pos) {
		long timestamp = interval.getTimestamp(pos);
		if (this.getTimestampProperties(timestamp) != null) {
			return new EdgeEvent(this.graph, this, timestamp);
		}
		return null;
	}

	/**
	 * Return timestampEdgeEvent having existing first timestamp or null
	 * 
	 * @return timestampEdgeEvent or null
	 */
	public EdgeEvent pickFirstTimestamp() {
		Long t = this.getFirstTimestamp();
		if (t == null)
			return null;
		else
			return new EdgeEvent(this.graph, this, t);
	}

	/**
	 * Return timestampEdgeEvent having existing last timestamp or null
	 * 
	 * @return timestampEdgeEvent or null
	 */
	public EdgeEvent pickLastTimestamp() {
		Long t = this.getLastTimestamp();
		if (t == null)
			return null;
		else
			return new EdgeEvent(this.graph, this, t);
	}

	/**
	 * Return intervalEdgeEvent with existing given interval
	 * 
	 * @param interval
	 * @return intervalEdgeEvent or null
	 */
	public EdgeEvent pickInterval(LongInterval interval) {
		if (this.getIntervalProperties(interval) != null) {
			return new EdgeEvent(this.graph, this, interval);
		}
		return null;
	}

	/**
	 * Return timestampEdgeEvent having existing first timestamp or null
	 * 
	 * @return timestampEdgeEvent or null
	 */
	public EdgeEvent pickFirstInterval() {
		LongInterval i = this.getFirstInterval();
		if (i == null)
			return null;
		else
			return new EdgeEvent(this.graph, this, i);
	}

	/**
	 * Return timestampEdgeEvent having existing last timestamp or null
	 * 
	 * @return timestampEdgeEvent or null
	 */
	public EdgeEvent pickLastInterval() {
		LongInterval i = this.getLastInterval();
		if (i == null)
			return null;
		else
			return new EdgeEvent(this.graph, this, i);
	}

	/**
	 * Pick the existing intervalEdgeEvents where each of their interval has
	 * temporal relationship with the given timestamp
	 * 
	 * @param left
	 * @param ss
	 * @param se
	 * @return HashSet<EdgeEvent>
	 */
	public Iterable<EdgeEvent> pickInterval(long left, AC ss, AC se) {
		HashSet<EdgeEvent> eventSet = new HashSet<EdgeEvent>();

		BsonDocument filter = new BsonDocument(Tokens.EDGE, new BsonString(this.id)).append(Tokens.TYPE,
				Tokens.TYPE_INTERVAL);
		filter = LongInterval.addTemporalRelationFilterQuery(filter, left, ss, se);
		MongoCursor<BsonDocument> cursor = graph.getVertexCollection().find(filter)
				.projection(Tokens.PRJ_ONLY_START_AND_END).iterator();
		while (cursor.hasNext()) {
			BsonDocument matched = cursor.next();
			eventSet.add(new EdgeEvent(this.getGraph(), this, new LongInterval(
					matched.getDateTime(Tokens.START).getValue(), matched.getDateTime(Tokens.END).getValue())));
		}
		return eventSet;
	}

	/**
	 * Pick the existing intervalVertexEvents where each of their interval has
	 * temporal relationship between the given interval
	 * 
	 * @param left
	 * @param ss
	 * @param se
	 * @param es
	 * @param ee
	 * @return HashSet<VertexEvent>
	 */
	public Iterable<EdgeEvent> pickInterval(LongInterval left, AC ss, AC se, AC es, AC ee) {
		HashSet<EdgeEvent> eventSet = new HashSet<EdgeEvent>();

		BsonDocument filter = new BsonDocument(Tokens.EDGE, new BsonString(this.id)).append(Tokens.TYPE,
				Tokens.TYPE_INTERVAL);
		filter = LongInterval.addTemporalRelationFilterQuery(filter, left, ss, se, es, ee);

		MongoCursor<BsonDocument> cursor = graph.getVertexCollection().find(filter)
				.projection(Tokens.PRJ_ONLY_START_AND_END).iterator();

		while (cursor.hasNext()) {
			BsonDocument matched = cursor.next();
			eventSet.add(new EdgeEvent(this.getGraph(), this, new LongInterval(
					matched.getDateTime(Tokens.START).getValue(), matched.getDateTime(Tokens.END).getValue())));
		}
		return eventSet;
	}

	/**
	 * Ceiling: greater than or equal to the given timestamp
	 * 
	 * @param timestamp
	 * @return timestampEdgeEvent with the ceiling timestamp of the given
	 *         timestamp or null
	 */
	public EdgeEvent pickCeilingTimestamp(Long timestamp) {
		Long ceilingTimestamp = this.getCeilingTimestamp(timestamp);
		if (ceilingTimestamp != null)
			return new EdgeEvent(this.graph, this, ceilingTimestamp);
		return null;
	}

	/**
	 * @param timestamp
	 * @return timestampEdgeEvent with the ceiling timestamp of the given
	 *         timestamp or null
	 */
	public EdgeEvent pickCeilingTimestamp(LongInterval interval, Position pos) {
		long timestamp = interval.getTimestamp(pos);
		return pickCeilingTimestamp(timestamp);
	}

	/**
	 * @param timestamp
	 * @return timestampEdgeEvent with the higher timestamp of the given
	 *         timestamp or null
	 */
	public EdgeEvent pickHigherTimestamp(Long timestamp) {
		Long higherTimestamp = this.getHigherTimestamp(timestamp);
		if (higherTimestamp != null)
			return new EdgeEvent(this.graph, this, higherTimestamp);
		return null;
	}

	/**
	 * @param timestamp
	 * @return timestampEdgeEvent with the higher timestamp of the given
	 *         timestamp or null
	 */
	public EdgeEvent pickHigherTimestamp(LongInterval interval, Position pos) {
		long timestamp = interval.getTimestamp(pos);
		return pickHigherTimestamp(timestamp);
	}

	/**
	 * Floor: less than or equal to the given timestamp
	 * 
	 * @param timestamp
	 * @return timestampEdgeEvent with the floor timestamp of the given
	 *         timestamp or null
	 */
	public EdgeEvent pickFloorTimestamp(Long timestamp) {
		Long floorTimestamp = this.getFloorTimestamp(timestamp);
		if (floorTimestamp != null)
			return new EdgeEvent(this.graph, this, floorTimestamp);
		return null;
	}

	/**
	 * @param timestamp
	 * @return timestampEdgeEvent with the floor timestamp of the given
	 *         timestamp or null
	 */
	public EdgeEvent pickFloorTimestamp(LongInterval interval, Position pos) {
		long timestamp = interval.getTimestamp(pos);
		return pickFloorTimestamp(timestamp);
	}

	/**
	 * Lower: less than or equal to the given timestamp
	 * 
	 * @param timestamp
	 * @return timestampEdgeEvent with the lower timestamp of the given
	 *         timestamp or null
	 */
	public EdgeEvent pickLowerTimestamp(Long timestamp) {
		Long lowerTimestamp = this.getLowerTimestamp(timestamp);
		if (lowerTimestamp != null)
			return new EdgeEvent(this.graph, this, lowerTimestamp);
		return null;
	}

	/**
	 * @param timestamp
	 * @return timestampVertexEvent with the lower timestamp of the given
	 *         timestamp or null
	 */
	public EdgeEvent pickLowerTimestamp(LongInterval interval, Position pos) {
		long timestamp = interval.getTimestamp(pos);
		return pickLowerTimestamp(timestamp);
	}

	/**
	 * @deprecated use getChronoVertex
	 */
	@Deprecated
	@Override
	public Vertex getVertex(final Direction direction) throws IllegalArgumentException {
		return getChronoVertex(direction);
	}
}
