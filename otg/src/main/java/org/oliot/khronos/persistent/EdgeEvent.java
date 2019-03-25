package org.oliot.khronos.persistent;

import java.util.Set;

import org.bson.BsonValue;
import org.oliot.khronos.common.ExceptionFactory;
import org.oliot.khronos.common.TemporalType;
import org.oliot.khronos.common.Tokens.AC;
import org.oliot.khronos.common.Tokens.Position;

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
public class EdgeEvent implements Element {

	private ChronoEdge edge;
	private Long timestamp;

	private TemporalType temporalType;

	protected ChronoGraph graph;

	/**
	 * Create Timestamp Edge Event
	 * 
	 * @param chronoGraph
	 * @param chronoEdge
	 * @param timestamp
	 */
	public EdgeEvent(ChronoGraph graph, ChronoEdge edge, Long timestamp) {
		this.graph = graph;
		this.edge = edge;
		this.timestamp = setTimestamp(timestamp);
		this.temporalType = TemporalType.TIMESTAMP;
	}

	/**
	 * @return ChronoEdge which has this
	 */
	public ChronoEdge getEdge() {
		return edge;
	}

	/**
	 * Set ChronoEdge to have this
	 * 
	 * @param chronoEdge
	 */
	public void setEdge(ChronoEdge edge) {
		this.edge = edge;
	}

	/**
	 * @return Temporal Type
	 */
	public TemporalType getTemporalType() {
		return this.temporalType;
	}

	/**
	 * @return readable string representing EdgeEvent
	 */
	public String toString() {
		return edge.toString() + "-" + timestamp;
	}

	/**
	 * @return current timestamp head or null if the type is INTERVAL
	 */
	public Long getTimestamp() {
		return timestamp;
	}

	/**
	 * Set current timestamp head to existing one Becomes TemporalType.TIMESTAMP if
	 * exist
	 * 
	 * @param timestamp
	 *            if exist, null if not exist
	 */
	public Long setTimestamp(Long timestamp) {
		if (edge.getTimestampProperties(timestamp) != null) {
			this.temporalType = TemporalType.TIMESTAMP;
			this.timestamp = timestamp;
			return this.timestamp;
		} else {
			return null;
		}
	}

	/**
	 * Get existing next timestamp greater than equal to current timestamp
	 * 
	 * @return existing next timestamp or null if not exist or the current temporal
	 *         type is INTERVAL
	 */
	public Long getCeilingTimestamp() {
		return edge.getCeilingTimestamp(timestamp);
	}

	/**
	 * Get existing next timestamp greater than equal to current timestamp
	 * 
	 * @param pos
	 *            used if the current temporal type is INTERVAL
	 * @return
	 */
	public Long getCeilingTimestamp(Position pos) {
		Long t = getTimestamp(pos);
		if (t != null)
			return edge.getCeilingTimestamp(t);
		return null;
	}

	/**
	 * Get existing higher timestamp based on current timestamp
	 * 
	 * @return existing higher timestamp or null if not exist or the current
	 *         temporal type is INTERVAL
	 */
	public Long getHigherTimestamp() {
		return edge.getHigherTimestamp(timestamp);
	}

	/**
	 * Get existing higher timestamp based on current timestamp
	 * 
	 * @param pos
	 *            used if the current temporal type is INTERVAL
	 * @return
	 */
	public Long getHigherTimestamp(Position pos) {
		Long t = getTimestamp(pos);
		if (t != null)
			return edge.getHigherTimestamp(t);
		return null;
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
	 * Set existing ceiling timestamp to current timestamp
	 * 
	 * @param pos
	 *            used if the current temporal type is INTERVAL
	 * @return ceiling timestamp. If null, there is no such timestamp, and timestamp
	 *         is not set up. If there is ceiling timestamp, the temporal type can
	 *         change
	 */
	public Long setCeilingTimestamp(Position pos) {
		Long ceiling = getCeilingTimestamp(pos);
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
		return edge.getFloorTimestamp(timestamp);
	}

	/**
	 * Get existing previous timestamp greater than equal to current timestamp
	 * 
	 * @param pos
	 *            used if the current temporal type is INTERVAL
	 * @return
	 */
	public Long getFloorTimestamp(Position pos) {
		Long t = getTimestamp(pos);
		if (t != null)
			return edge.getFloorTimestamp(t);
		return null;
	}

	/**
	 * Get existing lower timestamp based on current timestamp
	 * 
	 * @return existing lower timestamp or null if not exist or the current temporal
	 *         type is INTERVAL
	 */
	public Long getLowerTimestamp() {
		return edge.getLowerTimestamp(timestamp);
	}

	/**
	 * Get existing lower timestamp based on current timestamp
	 * 
	 * @param pos
	 *            used if the current temporal type is INTERVAL
	 * @return
	 */
	public Long getLowerTimestamp(Position pos) {
		Long t = getTimestamp(pos);
		if (t != null)
			return edge.getLowerTimestamp(t);
		return null;
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
	 * Set existing floor timestamp to current timestamp
	 * 
	 * @param pos
	 *            used if the current temporal type is INTERVAL
	 * @return floor timestamp. If null, there is no such timestamp, and timestamp
	 *         is not set up. If there is ceiling timestamp, the temporal type can
	 *         change
	 */
	public Long setFloorTimestamp(Position pos) {
		Long floor = getCeilingTimestamp(pos);
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
	 * Set existing lowerTimestamp to current timestamp
	 * 
	 * @param pos
	 *            used if the current temporal type is INTERVAL
	 * @return lower timestamp. If null, there is no timestamp key greater than
	 *         current timestamp, and timestamp is not setted up. If there is next
	 *         timestamp, the temporal type changes
	 */
	public Long setLowerTimestamp(Position pos) {
		Long lower = getLowerTimestamp(pos);
		if (lower != null) {
			timestamp = lower;
			temporalType = TemporalType.TIMESTAMP;
			return timestamp;
		} else
			return null;
	}

	/**
	 * Get existing timestamp based on current timestamp and comparator
	 * 
	 * @return existing timestamp or null if not exist or the current temporal type
	 *         is INTERVAL
	 */
	public Long getTimestamp(AC comparator) {
		return edge.getTimestamp(timestamp, comparator);
	}

	/**
	 * Get existing timestamp based on current timestamp and comparator
	 * 
	 * @param pos
	 *            used if the current temporal type is INTERVAL
	 * @return
	 */
	public Long getTimestamp(Position pos, AC comparator) {
		Long t = getTimestamp(pos);
		if (t != null)
			return edge.getTimestamp(t, comparator);
		return null;
	}

	/**
	 * Get vertex event, the timestamp doesn't need to be existing one.
	 * 
	 * The temporal type is inherited.
	 * 
	 * @param direction
	 * @return
	 * @throws IllegalArgumentException
	 */
	public VertexEvent getVertexEvent(final Direction direction) throws IllegalArgumentException {
		if (direction.equals(Direction.IN)) {
			ChronoVertex v = edge.getChronoVertex(Direction.IN);
			if (v == null)
				return null;
			else
				return new VertexEvent(graph, v, timestamp);
		} else if (direction.equals(Direction.OUT)) {
			ChronoVertex v = edge.getChronoVertex(Direction.OUT);
			if (v == null)
				return null;
			else
				return new VertexEvent(graph, v, timestamp);
		} else {
			throw ExceptionFactory.bothIsNotSupported();
		}
	}

	/**
	 * Interal Method
	 * 
	 * @param pos
	 * @return timestamp regardless of temporalType with pos
	 */
	private Long getTimestamp(Position pos) {
		return timestamp;
	}

	/**
	 * @param key
	 * @return timestamp properties or interval properties
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> T getProperty(String key) {
		return (T) edge.getTimestampPropertyValue(timestamp, key);
	}

	/**
	 * @deprecated use getTimestampPropertyKeys or getIntervalPropertyKeys
	 */
	@Deprecated
	@Override
	public Set<String> getPropertyKeys() {
		return edge.getPropertyKeys();
	}

	/**
	 * @return timestamps
	 */
	public Set<Long> getTimestampPropertyKeys() {
		return edge.getTimestamps();
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
			edge.setTimestampProperty(timestamp, key, (BsonValue) value);
	}

	@Override
	public <T> T removeProperty(String key) {
		return removeProperty(key);
	}

	@Deprecated
	@Override
	public void remove() {
		edge.remove();
	}

	@Override
	public Object getId() {
		return edge.id;
	}
}
