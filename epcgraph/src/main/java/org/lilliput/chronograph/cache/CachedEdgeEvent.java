package org.lilliput.chronograph.cache;

import java.util.Set;

import org.bson.BsonValue;
import org.lilliput.chronograph.common.ExceptionFactory;
import org.lilliput.chronograph.common.TemporalType;
import org.lilliput.chronograph.common.Tokens.AC;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Element;

/**
 * Copyright (C) 2016-2017 Jaewook Byun
 * 
 * In-memory Temporal Property Graph
 *
 * The part of static graph implements Tinkerpop Blueprints
 * (https://github.com/tinkerpop/blueprints).
 * 
 * We refer TinkerGraph to implement in-memory version.
 * 
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
public class CachedEdgeEvent implements Element {

	private final CachedChronoEdge edge;
	private Long timestamp;

	/**
	 * Create Timestamp Edge Event
	 * 
	 * @param chronoGraph
	 * @param chronoEdge
	 * @param timestamp
	 */
	public CachedEdgeEvent(final CachedChronoEdge edge, final Long timestamp) {
		this.edge = edge;
		this.timestamp = timestamp;
	}

	/**
	 * @return ChronoEdge which has this
	 */
	public CachedChronoEdge getEdge() {
		return edge;
	}

	/**
	 * @return Temporal Type
	 */
	public TemporalType getTemporalType() {
		if (timestamp != null)
			return TemporalType.TIMESTAMP;
		else
			return TemporalType.INTERVAL;
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
	 * Get existing higher timestamp based on current timestamp
	 * 
	 * @return existing higher timestamp or null if not exist or the current
	 *         temporal type is INTERVAL
	 */
	public Long getHigherTimestamp() {
		return edge.getHigherTimestamp(timestamp);
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
	 * Get existing lower timestamp based on current timestamp
	 * 
	 * @return existing lower timestamp or null if not exist or the current temporal
	 *         type is INTERVAL
	 */
	public Long getLowerTimestamp() {
		return edge.getLowerTimestamp(timestamp);
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
		Long lower = getLowerTimestamp();
		if (lower != null) {
			timestamp = lower;
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
	 * Get vertex event, the timestamp doesn't need to be existing one.
	 * 
	 * The temporal type is inherited.
	 * 
	 * @param direction
	 * @return
	 * @throws IllegalArgumentException
	 */
	public CachedVertexEvent getVertexEvent(final Direction direction) throws IllegalArgumentException {
		if (direction.equals(Direction.IN)) {
			CachedChronoVertex v = edge.getChronoVertex(Direction.IN);

			return new CachedVertexEvent(v, timestamp);

		} else if (direction.equals(Direction.OUT)) {
			CachedChronoVertex v = edge.getChronoVertex(Direction.OUT);

			return new CachedVertexEvent(v, timestamp);

		} else {
			throw ExceptionFactory.bothIsNotSupported();
		}
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
		return edge.idx;
	}
}
