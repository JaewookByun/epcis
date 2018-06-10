package org.oliot.khronos.persistent;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;

import java.util.ArrayList;
import java.util.List;

import org.oliot.khronos.common.ExceptionFactory;

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
	 * Ceiling: greater than or equal to the given timestamp
	 * 
	 * @param timestamp
	 * @return timestampEdgeEvent with the ceiling timestamp of the given timestamp
	 *         or null
	 */
	public EdgeEvent pickCeilingTimestamp(Long timestamp) {
		Long ceilingTimestamp = this.getCeilingTimestamp(timestamp);
		if (ceilingTimestamp != null)
			return new EdgeEvent(this.graph, this, ceilingTimestamp);
		return null;
	}

	/**
	 * @param timestamp
	 * @return timestampEdgeEvent with the higher timestamp of the given timestamp
	 *         or null
	 */
	public EdgeEvent pickHigherTimestamp(Long timestamp) {
		Long higherTimestamp = this.getHigherTimestamp(timestamp);
		if (higherTimestamp != null)
			return new EdgeEvent(this.graph, this, higherTimestamp);
		return null;
	}

	/**
	 * Floor: less than or equal to the given timestamp
	 * 
	 * @param timestamp
	 * @return timestampEdgeEvent with the floor timestamp of the given timestamp or
	 *         null
	 */
	public EdgeEvent pickFloorTimestamp(Long timestamp) {
		Long floorTimestamp = this.getFloorTimestamp(timestamp);
		if (floorTimestamp != null)
			return new EdgeEvent(this.graph, this, floorTimestamp);
		return null;
	}

	/**
	 * Lower: less than or equal to the given timestamp
	 * 
	 * @param timestamp
	 * @return timestampEdgeEvent with the lower timestamp of the given timestamp or
	 *         null
	 */
	public EdgeEvent pickLowerTimestamp(Long timestamp) {
		Long lowerTimestamp = this.getLowerTimestamp(timestamp);
		if (lowerTimestamp != null)
			return new EdgeEvent(this.graph, this, lowerTimestamp);
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
