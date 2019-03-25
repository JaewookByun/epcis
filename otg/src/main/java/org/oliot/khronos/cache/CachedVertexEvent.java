package org.oliot.khronos.cache;

import java.util.Set;
import java.util.stream.Stream;

import org.bson.BsonArray;
import org.bson.BsonValue;
import org.oliot.khronos.common.ExceptionFactory;
import org.oliot.khronos.common.Tokens.AC;

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
public class CachedVertexEvent implements Element {

	private final CachedChronoVertex vertex;
	private Long timestamp;

	/**
	 * Create timestampVertexEvent
	 * 
	 * @param chronoGraph
	 * @param chronoVertex
	 * @param timestamp
	 */
	public CachedVertexEvent(final CachedChronoVertex vertex, final Long timestamp) {
		this.vertex = vertex;
		this.timestamp = timestamp;
	}

	public CachedVertexEvent getThis() {
		return this;
	}

	public String getVertexID() {
		return vertex.toString();
	}

	/**
	 * @return ChronoVertex which has this
	 */
	public CachedChronoVertex getVertex() {
		return vertex;
	}

	/**
	 * @return readable string representing VertexEvent
	 */
	public String toString() {
		return vertex.toString() + "-" + timestamp;
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
		Long lower = getLowerTimestamp();
		if (lower != null) {
			timestamp = lower;
			return timestamp;
		} else
			return null;
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
		vertex.setTimestampProperty(timestamp, key, (BsonValue) value);
		setProperty(key, value);
	}

	public Stream<CachedEdgeEvent> getEdgeEventStream(final Direction direction, final BsonArray labels, final AC tt,
			final boolean isParallel) {
		if (isParallel)
			return vertex.getEdgeEventSet(direction, labels, this.timestamp, tt, Integer.MAX_VALUE).parallelStream();
		else
			return vertex.getEdgeEventSet(direction, labels, this.timestamp, tt, Integer.MAX_VALUE).stream();
	}

	public Set<CachedEdgeEvent> getEdgeEventSet(final Direction direction, final BsonArray labels, final AC tt) {
		return vertex.getEdgeEventSet(direction, labels, this.timestamp, tt, Integer.MAX_VALUE);
	}

	public Stream<CachedVertexEvent> getVertexEventStream(final Direction direction, final BsonArray labels,
			final AC tt, final boolean setParallel) {
		if (setParallel)
			return vertex.getVertexEventSet(direction, labels, this.timestamp, tt, Integer.MAX_VALUE).parallelStream();
		else
			return vertex.getVertexEventSet(direction, labels, this.timestamp, tt, Integer.MAX_VALUE).stream();
	}

	public Set<CachedVertexEvent> getVertexEventSet(final Direction direction, final BsonArray labels, final AC tt) {
		return vertex.getVertexEventSet(direction, labels, this.timestamp, tt, Integer.MAX_VALUE);
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
		return this.vertex.idx;
	}

}
