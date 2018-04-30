package org.lilliput.chronograph.cache;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.lilliput.chronograph.common.ExceptionFactory;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;

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
public class CachedChronoEdge extends CachedChronoElement implements Edge {

	private final Long labelIdx;
	private final CachedChronoVertex outVertex;
	private final CachedChronoVertex inVertex;
	
	/**
	 * Create Edge
	 * 
	 * @param id
	 * @param outVertex
	 * @param inVertex
	 * @param label
	 * @param graph
	 */
	protected CachedChronoEdge(CachedChronoVertex outVertex, CachedChronoVertex inVertex, final Long labelIdx,
			final CachedChronoGraph graph) {
		super(null, graph);
		this.labelIdx = labelIdx;
		this.outVertex = outVertex;
		this.inVertex = inVertex;
	}

	public CachedChronoEdge getThis() {
		return this;
	}

	public CachedChronoVertex getOutVertex() {
		return this.outVertex;
	}

	public CachedChronoVertex getInVertex() {
		return this.inVertex;
	}

	public Long getLabelIdx() {
		return labelIdx;
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
	protected CachedChronoEdge(final String outVertexID, final String inVertexID, final String label,
			final CachedChronoGraph graph) {
		super(null, graph);

		if (graph.getLabelIndex().containsKey(label))
			this.labelIdx = graph.getLabelIndex().get(label);
		else {
			Long newIdx = graph.getLabelCnt().incrementAndGet();
			graph.getLabelIndex().put(label, newIdx);
			this.labelIdx = newIdx;
		}

		this.outVertex = graph.addVertex(outVertexID);
		this.inVertex = graph.addVertex(inVertexID);
		this.idx = new CachedEdgeID((Long)this.outVertex.idx, labelIdx, (Long)this.inVertex.idx);
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
	protected CachedChronoEdge(final String id, final CachedChronoGraph graph) {
		super(null, graph);

		final String[] e = id.split("\\|");
		if (e.length == 3) {
			if (graph.getLabelIndex().containsKey(e[1]))
				this.labelIdx = graph.getLabelIndex().get(e[1]);
			else {
				Long newIdx = graph.getLabelCnt().incrementAndGet();
				graph.getLabelIndex().put(e[1], newIdx);
				this.labelIdx = newIdx;
			}
			this.outVertex = graph.addVertex(e[0]);
			this.inVertex = graph.addVertex(e[2]);
		} else {
			this.outVertex = null;
			this.inVertex = null;
			this.labelIdx = null;
		}
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
	public CachedChronoVertex getChronoVertex(final Direction direction) throws IllegalArgumentException {
		if (direction.equals(Direction.IN))
			return this.inVertex;
		else if (direction.equals(Direction.OUT))
			return this.outVertex;
		else
			throw ExceptionFactory.bothIsNotSupported();
	}

	/**
	 * 
	 * @return outVertex and inVertex as ArrayList<CachedChronoVertex>
	 */
	public List<CachedChronoVertex> getBothChronoVertexList() {
		List<CachedChronoVertex> list = new ArrayList<CachedChronoVertex>();
		list.add(this.outVertex);
		list.add(this.inVertex);
		return list;
	}

	public Stream<CachedChronoVertex> getBothChronoVertexStream(boolean setParallel) {
		if (setParallel)
			return getBothChronoVertices().parallelStream();
		else
			return getBothChronoVertices().stream();
	}

	public List<CachedChronoVertex> getBothChronoVertices() {
		List<CachedChronoVertex> list = new ArrayList<CachedChronoVertex>();
		list.add(this.outVertex);
		list.add(this.inVertex);
		return list;
	}

	/**
	 * Return the label associated with the edge.
	 *
	 * @return the edge label
	 */
	@Override
	public String getLabel() {
		return graph.getLabelIndex().inverse().get(labelIdx);
	}

	/**
	 * Return timestampEdgeEvent with given timestamp
	 * 
	 * @param timestamp
	 *            can be null
	 * @return timestampEdgeEvent
	 */
	public CachedEdgeEvent setTimestamp(final Long timestamp) {
		return new CachedEdgeEvent(this, timestamp);
	}

	/**
	 * Return timestampEdgeEvent with existing given timestamp
	 * 
	 * @param timestamp
	 *            existing timestamp property key
	 * @return timestampEdgeEvent or null
	 */
	public CachedEdgeEvent pickTimestamp(final Long timestamp) {
		if (timestamp == null)
			return null;
		if (this.timestampProperties.containsKey(timestamp) != false) {
			return new CachedEdgeEvent(this, timestamp);
		}
		return null;
	}

	/**
	 * Return timestampEdgeEvent having existing first timestamp or null
	 * 
	 * @return timestampEdgeEvent or null
	 */
	public CachedEdgeEvent pickFirstTimestamp() {
		Long t = this.getFirstTimestamp();
		if (t == null)
			return null;
		else
			return new CachedEdgeEvent(this, t);
	}

	/**
	 * Return timestampEdgeEvent having existing last timestamp or null
	 * 
	 * @return timestampEdgeEvent or null
	 */
	public CachedEdgeEvent pickLastTimestamp() {
		Long t = this.getLastTimestamp();
		if (t == null)
			return null;
		else
			return new CachedEdgeEvent(this, t);
	}

	/**
	 * Ceiling: greater than or equal to the given timestamp
	 * 
	 * @param timestamp
	 * @return timestampEdgeEvent with the ceiling timestamp of the given timestamp
	 *         or null
	 */
	public CachedEdgeEvent pickCeilingTimestamp(final Long timestamp) {
		Long ceilingTimestamp = this.getCeilingTimestamp(timestamp);
		if (ceilingTimestamp != null)
			return new CachedEdgeEvent(this, ceilingTimestamp);
		return null;
	}

	/**
	 * @param timestamp
	 * @return timestampEdgeEvent with the higher timestamp of the given timestamp
	 *         or null
	 */
	public CachedEdgeEvent pickHigherTimestamp(final Long timestamp) {
		Long higherTimestamp = this.getHigherTimestamp(timestamp);
		if (higherTimestamp != null)
			return new CachedEdgeEvent(this, higherTimestamp);
		return null;
	}

	/**
	 * Floor: less than or equal to the given timestamp
	 * 
	 * @param timestamp
	 * @return timestampEdgeEvent with the floor timestamp of the given timestamp or
	 *         null
	 */
	public CachedEdgeEvent pickFloorTimestamp(final Long timestamp) {
		Long floorTimestamp = this.getFloorTimestamp(timestamp);
		if (floorTimestamp != null)
			return new CachedEdgeEvent(this, floorTimestamp);
		return null;
	}

	/**
	 * Lower: less than or equal to the given timestamp
	 * 
	 * @param timestamp
	 * @return timestampEdgeEvent with the lower timestamp of the given timestamp or
	 *         null
	 */
	public CachedEdgeEvent pickLowerTimestamp(final Long timestamp) {
		Long lowerTimestamp = this.getLowerTimestamp(timestamp);
		if (lowerTimestamp != null)
			return new CachedEdgeEvent(this, lowerTimestamp);
		return null;
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
