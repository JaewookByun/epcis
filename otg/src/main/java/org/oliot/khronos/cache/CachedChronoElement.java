package org.oliot.khronos.cache;

import com.tinkerpop.blueprints.Element;

import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.bson.BsonDocument;
import org.bson.BsonValue;
import org.oliot.khronos.common.Tokens.AC;

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
@SuppressWarnings("unchecked")
abstract class CachedChronoElement implements Element {

	protected Object idx;

	protected BsonDocument staticProperties = new BsonDocument();
	protected TreeMap<Long, BsonDocument> timestampProperties = new TreeMap<Long, BsonDocument>();

	protected final CachedChronoGraph graph;

	public Object getIdx() {
		return idx;
	}

	/**
	 * Create Graph Element
	 * 
	 * @param id
	 * @param graph
	 */
	protected CachedChronoElement(final Object idx, final CachedChronoGraph graph) {
		this.graph = graph;
		this.idx = idx;
	}

	public BsonDocument getStaticProperties() {
		return staticProperties;
	}

	public void setStaticProperties(BsonDocument staticProperties) {
		this.staticProperties = staticProperties;
	}

	public TreeMap<Long, BsonDocument> getTimestampProperties() {
		return timestampProperties;
	}

	public void setTimestampProperties(TreeMap<Long, BsonDocument> timestampProperties) {
		this.timestampProperties = timestampProperties;
	}

	/**
	 * Return the object value associated with the provided string key. If no value
	 * exists for that key, return null.
	 *
	 * @param key
	 *            the key of the key/value property, Tokens.ID, Tokens.LABEL,
	 *            Tokens.OUT_VERTEX, Tokens.IN_VERTEX included
	 * @return the object value related to the string key
	 */
	@Override
	public <T> T getProperty(final String key) {
		return (T) this.staticProperties.get(key);
	}

	/**
	 * Return all the keys associated with the element.
	 * 
	 * @return the set of all string keys associated with the element
	 */
	@Override
	public Set<String> getPropertyKeys() {
		return this.staticProperties.keySet();
	}

	/**
	 * Return all the keys associated with the element.
	 *
	 * @return the set of all string keys associated with the element
	 */
	@Override
	public void setProperty(final String key, final Object value) {
		this.staticProperties.put(key, (BsonValue) value);
	}

	public void setProperties(final BsonDocument properties) {
		if (properties == null)
			return;
		this.staticProperties = properties;
	}

	public BsonDocument getProperties() {
		return this.staticProperties;
	}

	/**
	 * Un-assigns a key/value property from the element. The object value of the
	 * removed property is returned.
	 *
	 * @param key
	 *            the key of the property to remove from the element
	 * @return the object value associated with that key prior to removal. Should be
	 *         instance of BsonValue
	 */
	@Override
	public <T> T removeProperty(final String key) {
		return (T) this.staticProperties.remove(key);
	}

	/**
	 * Remove the element from the graph.
	 */
	@Override
	public void remove() {
		if (this instanceof CachedChronoVertex) {
			graph.removeVertex((Long) idx);
		} else {
			graph.removeEdge((CachedEdgeID) idx);
		}
	}

	/**
	 * Clear Static Properties
	 */
	public void clearStaticProperties() {
		this.staticProperties.clear();
	}

	/**
	 * Edge: outV|label|inV Vertex: id
	 * 
	 */
	/**
	 * Return identifier provided by users
	 */
	@Override
	public String toString() {
		if (idx instanceof Long) {
			return graph.getVertexIndex().inverse().get(idx);
		} else if (idx instanceof CachedEdgeID) {
			CachedEdgeID eid = (CachedEdgeID) idx;
			return eid.toString(graph);
		} else
			return null;
	}

	/**
	 * Return Graph
	 * 
	 * @return
	 */
	public CachedChronoGraph getGraph() {
		return this.graph;
	}

	/**
	 * set timestamp properties (replace) for the given timestamp
	 * 
	 * @param timestamp
	 * @param timestampProperties
	 */
	public void setTimestampProperties(final Long timestamp, BsonDocument timestampProperties) {
		this.timestampProperties.put(timestamp, timestampProperties);
		if (this instanceof CachedChronoEdge) {
			CachedChronoEdge edge = (CachedChronoEdge) this;
			// HashMap<Long, TreeMap<Long, CachedEdgeEvent>>
			CachedEdgeID idx = ((CachedEdgeID) edge.getId());

			TreeMap<Long, CachedChronoEdge> outEvents = edge.getOutVertex().getOutEvents().get(idx);
			if (outEvents == null)
				outEvents = new TreeMap<Long, CachedChronoEdge>();
			outEvents.put(timestamp, edge);
			edge.getOutVertex().getOutEvents().put(idx, outEvents);

			TreeMap<Long, CachedChronoEdge> inEvents = edge.getInVertex().getInEvents().get(idx);
			if (inEvents == null)
				inEvents = new TreeMap<Long, CachedChronoEdge>();
			inEvents.put(timestamp, edge);
			edge.getInVertex().getInEvents().put(idx, inEvents);
		}
	}

	/**
	 * Set TimestampProperty for timestamp and key, existing value will be replaced
	 * 
	 * @param timestamp
	 * @param key
	 * @param value
	 */
	public void setTimestampProperty(final Long timestamp, String key, BsonValue value) {
		BsonDocument timestampProperties = getTimestampProperties(timestamp);
		if (timestampProperties == null)
			timestampProperties = new BsonDocument();
		timestampProperties.put(key, value);
		setTimestampProperties(timestamp, timestampProperties);
	}

	/**
	 * Expensive Operation
	 * 
	 * @return TreeSet of temporalProperty key set
	 */
	public Set<Long> getTimestamps() {
		return this.timestampProperties.keySet();
	}

	/**
	 * @param timestamp
	 * @return TimestampProperties BsonDocument
	 */
	public BsonDocument getTimestampProperties(final Long timestamp) {
		return this.timestampProperties.get(timestamp);
	}

	/**
	 * @param timestamp
	 * @param key
	 * @return property value for the given key at the given timestamp
	 */
	public BsonValue getTimestampPropertyValue(final Long timestamp, final String key) {
		BsonDocument timestampProperties = getTimestampProperties(timestamp);
		if (timestampProperties != null)
			return (BsonValue) timestampProperties.get(key);
		return null;
	}

	/**
	 * Clear all timestamp properties
	 */
	public void clearTimestampProperties() {
		this.timestampProperties = new TreeMap<Long, BsonDocument>();
		;
	}

	/**
	 * Remain only timestamp property related to the given key
	 * 
	 * @param timestamp
	 * @return null or remaining timestamp property
	 */
	public BsonDocument retainTimestampProperties(final Long timestamp) {
		BsonDocument timestampProperties = getTimestampProperties(timestamp);
		clearTimestampProperties();
		setTimestampProperties(timestamp, timestampProperties);
		return timestampProperties;
	}

	/**
	 * MongoDB: When a $sort immediately precedes a $limit in the pipeline, the
	 * $sort operation only maintains the top n results as it progresses.
	 * 
	 * @return first existing timestamp
	 */
	public Long getFirstTimestamp() {
		return this.timestampProperties.firstKey();
	}

	/**
	 * MongoDB: When a $sort immediately precedes a $limit in the pipeline, the
	 * $sort operation only maintains the top n results as it progresses.
	 * 
	 * @return last existing timestamp
	 */
	public Long getLastTimestamp() {
		return this.timestampProperties.lastKey();
	}

	/**
	 * @param timestamp
	 * @return most existing ceiling timestamp
	 */
	public Long getCeilingTimestamp(long timestamp) {
		return this.timestampProperties.ceilingKey(timestamp);
	}

	/**
	 * @param timestamp
	 * @return most existing higher timestamp
	 */
	public Long getHigherTimestamp(long timestamp) {
		return this.timestampProperties.higherKey(timestamp);
	}

	/**
	 * @param timestamp
	 * @return most existing floor timestamp
	 */
	public Long getFloorTimestamp(long timestamp) {
		return this.timestampProperties.floorKey(timestamp);
	}

	/**
	 * @param timestamp
	 * @return most existing lower timestamp
	 */
	public Long getLowerTimestamp(long timestamp) {
		return this.timestampProperties.lowerKey(timestamp);
	}

	/**
	 * @param left
	 * @param comparator
	 * @return closest timestamp to the given timestamp based on comparator
	 */
	public Long getTimestamp(long left, AC tt) {
		if (tt.equals(AC.$gte)) {
			return this.timestampProperties.ceilingKey(left);
		} else if (tt.equals(AC.$gt)) {
			return this.timestampProperties.higherKey(left);
		} else if (tt.equals(AC.$eq) && this.timestampProperties.containsKey(left)) {
			return left;
		} else if (tt.equals(AC.$lte)) {
			return this.timestampProperties.floorKey(left);
		} else if (tt.equals(AC.$lt)) {
			return this.timestampProperties.lowerKey(left);
		}
		return null;
	}

	/**
	 * @param left
	 * @param comparator
	 * @return closest timestamp to the given timestamp based on comparator
	 */
	public TreeSet<Long> getTimestamps(long left, AC comparator) {

		TreeSet<Long> ret = new TreeSet<Long>();

		ret.addAll(this.timestampProperties.keySet().parallelStream()
				.filter(t -> isPassingComparator(left, comparator, t)).collect(Collectors.toList()));
		return ret;

	}

	/**
	 * Remove All Temporal Properties
	 */
	public void clearTemporalProperties() {
		clearStaticProperties();
	}

	/**
	 * return vertex index
	 */
	@Override
	public Object getId() {
		if (this instanceof CachedChronoVertex)
			return this.idx;
		else {
			CachedChronoEdge e = (CachedChronoEdge) this;
			return new CachedEdgeID((Long) e.getOutVertex().idx, e.getLabelIdx(), (Long) e.getInVertex().idx);
		}
	}

	public boolean isPassingComparator(long left, AC comparator, long right) {
		if (comparator == null)
			return true;
		if (comparator.equals(AC.$gte) && left <= right) {
			return true;
		} else if (comparator.equals(AC.$gt) && left < right) {
			return true;
		} else if (comparator.equals(AC.$eq) && left == right) {
			return true;
		} else if (comparator.equals(AC.$lte) && left <= right) {
			return true;
		} else if (comparator.equals(AC.$lt) && left < right) {
			return true;
		} else
			return false;
	}

}
