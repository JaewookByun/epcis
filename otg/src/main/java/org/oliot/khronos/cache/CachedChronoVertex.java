package org.oliot.khronos.cache;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.VertexQuery;
import com.tinkerpop.blueprints.util.DefaultVertexQuery;
import com.tinkerpop.blueprints.util.MultiIterable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bson.BsonArray;
import org.oliot.khronos.common.ExceptionFactory;
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
public class CachedChronoVertex extends CachedChronoElement implements Vertex {

	private HashMap<Long, Set<CachedChronoEdge>> outEdges = new HashMap<Long, Set<CachedChronoEdge>>();
	private HashMap<Long, Set<CachedChronoEdge>> inEdges = new HashMap<Long, Set<CachedChronoEdge>>();

	// Edge Event 등록시에 시간으로 인덱싱 해놓는다
	private HashMap<CachedEdgeID, TreeMap<Long, CachedChronoEdge>> outEvents = new HashMap<CachedEdgeID, TreeMap<Long, CachedChronoEdge>>();
	private HashMap<CachedEdgeID, TreeMap<Long, CachedChronoEdge>> inEvents = new HashMap<CachedEdgeID, TreeMap<Long, CachedChronoEdge>>();

	public HashMap<Long, Set<CachedChronoEdge>> getOutEdges() {
		return outEdges;
	}

	public void setOutEdges(HashMap<Long, Set<CachedChronoEdge>> outEdges) {
		this.outEdges = outEdges;
	}

	public HashMap<Long, Set<CachedChronoEdge>> getInEdges() {
		return inEdges;
	}

	public void setInEdges(HashMap<Long, Set<CachedChronoEdge>> inEdges) {
		this.inEdges = inEdges;
	}

	public HashMap<CachedEdgeID, TreeMap<Long, CachedChronoEdge>> getOutEvents() {
		return outEvents;
	}

	public void setOutEvents(HashMap<CachedEdgeID, TreeMap<Long, CachedChronoEdge>> outEvents) {
		this.outEvents = outEvents;
	}

	public HashMap<CachedEdgeID, TreeMap<Long, CachedChronoEdge>> getInEvents() {
		return inEvents;
	}

	public void setInEvents(HashMap<CachedEdgeID, TreeMap<Long, CachedChronoEdge>> inEvents) {
		this.inEvents = inEvents;
	}

	public CachedChronoVertex(final Object idx, final CachedChronoGraph graph) {
		super(idx, graph);
	}

	public CachedChronoGraph getGraph() {
		return this.graph;
	}

	public String getVertexID() {
		return this.toString();
	}

	public CachedChronoVertex getThis() {
		return this;
	}

	public HashSet<Long> convertToLabelIdxSet(BsonArray labels) {
		return (HashSet<Long>) labels.parallelStream().map(label -> {
			return (Long) graph.getLabelIndex().get(label.asString().getValue());
		}).filter(label -> label != null).collect(Collectors.toSet());
	}

	public Long convertToLabelIdx(String label) {
		return graph.getLabelIndex().get(label);
	}

	/**
	 * Return the edges incident to the vertex according to the provided direction
	 * and edge labels.
	 *
	 * @param direction
	 *            the direction of the edges to retrieve
	 * @param labels
	 *            the labels of the edges to retrieve
	 * @return an iterable of incident edges
	 */
	public Iterable<CachedChronoEdge> getChronoEdges(final Direction direction, final BsonArray labels) {
		return getChronoEdges(direction, labels, Integer.MAX_VALUE);
	}

	/**
	 * Return the edges incident to the vertex according to the provided direction
	 * and edge labels.
	 *
	 * @param direction
	 *            the direction of the edges to retrieve
	 * @param labels
	 *            the labels of the edges to retrieve
	 * @return an iterable of incident edges
	 */
	public Iterable<CachedChronoEdge> getChronoEdges(final Direction direction, final BsonArray labels,
			final int branchFactor) {

		HashSet<Long> labelIdxSet = null;
		if (labels != null) {
			labelIdxSet = convertToLabelIdxSet(labels);
		}

		if (direction.equals(Direction.OUT)) {
			return this.getOutChronoEdges(labelIdxSet, branchFactor);
		} else if (direction.equals(Direction.IN))
			return this.getInChronoEdges(labelIdxSet, branchFactor);
		else {
			return new MultiIterable<CachedChronoEdge>(Arrays.asList(this.getInChronoEdges(labelIdxSet, branchFactor),
					this.getOutChronoEdges(labelIdxSet, branchFactor)));
		}
	}

	public Stream<CachedChronoEdge> getChronoEdgeStream(final Direction direction, final BsonArray labels,
			final int branchFactor, final boolean setParallel) {

		HashSet<Long> labelIdxSet = null;
		if (labels != null) {
			labelIdxSet = convertToLabelIdxSet(labels);
		}

		if (direction.equals(Direction.OUT)) {
			return this.getOutChronoEdgeStream(labelIdxSet, branchFactor, setParallel);
		} else if (direction.equals(Direction.IN))
			return this.getInChronoEdgeStream(labelIdxSet, branchFactor, setParallel);
		else {
			Set<CachedChronoEdge> ret = this.getOutChronoEdgeSet(labelIdxSet, branchFactor);
			ret.addAll(this.getInChronoEdgeSet(labelIdxSet, branchFactor));
			if (setParallel)
				return ret.parallelStream();
			else
				return ret.stream();
		}
	}

	public Set<CachedChronoEdge> getChronoEdgeSet(final Direction direction, final String label,
			final int branchFactor) {

		Long labelIdx = convertToLabelIdx(label);

		if (direction.equals(Direction.OUT)) {
			return this.getOutChronoEdgeSet(labelIdx, branchFactor);
		} else if (direction.equals(Direction.IN))
			return this.getInChronoEdgeSet(labelIdx, branchFactor);
		else {
			Set<CachedChronoEdge> ret = this.getOutChronoEdgeSet(labelIdx, branchFactor);
			ret.addAll(this.getInChronoEdgeSet(labelIdx, branchFactor));
			return ret;
		}
	}

	public Set<CachedChronoEdge> getChronoEdgeSet(final Direction direction, final BsonArray labels,
			final int branchFactor) {

		HashSet<Long> labelIdxSet = null;
		if (labels != null) {
			labelIdxSet = convertToLabelIdxSet(labels);
		}

		if (direction.equals(Direction.OUT)) {
			return this.getOutChronoEdgeSet(labelIdxSet, branchFactor);
		} else if (direction.equals(Direction.IN))
			return this.getInChronoEdgeSet(labelIdxSet, branchFactor);
		else {
			Set<CachedChronoEdge> ret = this.getOutChronoEdgeSet(labelIdxSet, branchFactor);
			ret.addAll(this.getInChronoEdgeSet(labelIdxSet, branchFactor));
			return ret;
		}
	}

	public Set<CachedEdgeEvent> getEdgeEventSet(final Direction direction, final BsonArray labels, final Long left,
			final AC tt, final int branchFactor) {

		HashSet<Long> labelIdxSet = null;
		if (labels != null) {
			labelIdxSet = convertToLabelIdxSet(labels);
		}

		if (direction.equals(Direction.OUT)) {
			return this.getOutEdgeEventSet(labelIdxSet, left, tt, branchFactor);
		} else if (direction.equals(Direction.IN))
			return this.getInEdgeEventSet(labelIdxSet, left, tt, branchFactor);
		else {
			Set<CachedEdgeEvent> ret = this.getOutEdgeEventSet(labelIdxSet, left, tt, branchFactor);
			ret.addAll(this.getInEdgeEventSet(labelIdxSet, left, tt, branchFactor));
			return ret;
		}
	}

	private Set<CachedEdgeEvent> getOutEdgeEventSet(final HashSet<Long> labelIdxSet, final Long left, final AC tt,
			final int branchFactor) {
		return this.getOutEvents().entrySet().parallelStream().filter(elem -> {
			if (labelIdxSet == null || labelIdxSet.isEmpty() == true)
				return true;
			return labelIdxSet.contains(elem.getKey().getLabelIdx());
		}).map(elem -> {
			// 최대 한개의 edge event를 내보낸다.
			if (tt == AC.$gte)
				return elem.getValue().ceilingEntry(left);
			else if (tt == AC.$gt)
				return elem.getValue().higherEntry(left);
			else if (tt == AC.$eq) {
				if (elem.getValue().containsKey(left))
					return elem.getValue().ceilingEntry(left);
				return null;
			} else if (tt == AC.$lt)
				return elem.getValue().lowerEntry(left);
			else
				return elem.getValue().floorEntry(left);
		}).filter(elem -> elem != null).map(elem -> {
			Entry<Long, CachedChronoEdge> entry = (Entry<Long, CachedChronoEdge>) elem;
			return entry.getValue().setTimestamp(entry.getKey());
		}).collect(Collectors.toSet());
	}

	private Set<CachedEdgeEvent> getInEdgeEventSet(final HashSet<Long> labelIdxSet, final Long left, final AC tt,
			final int branchFactor) {
		return this.getInEvents().entrySet().parallelStream().filter(elem -> {
			if (labelIdxSet == null || labelIdxSet.isEmpty() == true)
				return true;
			return labelIdxSet.contains(elem.getKey().getLabelIdx());
		}).map(elem -> {
			// 최대 한개의 edge event를 내보낸다.
			if (tt == AC.$gte)
				return elem.getValue().ceilingEntry(left);
			else if (tt == AC.$gt)
				return elem.getValue().higherEntry(left);
			else if (tt == AC.$eq) {
				if (elem.getValue().containsKey(left))
					return elem.getValue().ceilingEntry(left);
				return null;
			} else if (tt == AC.$lt)
				return elem.getValue().lowerEntry(left);
			else
				return elem.getValue().floorEntry(left);
		}).filter(elem -> elem != null).map(elem -> {
			Entry<Long, CachedChronoEdge> entry = (Entry<Long, CachedChronoEdge>) elem;
			return entry.getValue().setTimestamp(entry.getKey());
		}).collect(Collectors.toSet());
	}

	public Set<CachedVertexEvent> getVertexEventSet(final Direction direction, final BsonArray labels, final Long left,
			final AC tt, final int branchFactor) {

		HashSet<Long> labelIdxSet = null;
		if (labels != null) {
			labelIdxSet = convertToLabelIdxSet(labels);
		}

		if (direction.equals(Direction.OUT)) {
			return this.getOutVertexEventSet(labelIdxSet, left, tt, branchFactor);
		} else if (direction.equals(Direction.IN))
			return this.getInVertexEventSet(labelIdxSet, left, tt, branchFactor);
		else {
			Set<CachedVertexEvent> ret = this.getOutVertexEventSet(labelIdxSet, left, tt, branchFactor);
			ret.addAll(this.getInVertexEventSet(labelIdxSet, left, tt, branchFactor));
			return ret;
		}
	}

	private Set<CachedVertexEvent> getOutVertexEventSet(final HashSet<Long> labelIdxSet, final Long left, final AC tt,
			final int branchFactor) {

		if (labelIdxSet == null || labelIdxSet.isEmpty()) {
			return this.getOutEvents().entrySet().parallelStream().map(elem -> {
				// 최대 한개의 edge event를 내보낸다.
				if (tt == AC.$gte) {
					Entry<Long, CachedChronoEdge> entry = elem.getValue().ceilingEntry(left);
					if (entry != null) {
						return entry.getValue().getInVertex().setTimestamp(entry.getKey());
					}
					return null;
				} else if (tt == AC.$gt) {
					Entry<Long, CachedChronoEdge> entry = elem.getValue().higherEntry(left);
					if (entry != null) {
						return entry.getValue().getInVertex().setTimestamp(entry.getKey());
					}
					return null;
				} else if (tt == AC.$eq) {
					if (elem.getValue().containsKey(left)) {
						return elem.getValue().get(left).getInVertex().setTimestamp(left);
					}
					return null;
				} else if (tt == AC.$lt) {
					Entry<Long, CachedChronoEdge> entry = elem.getValue().lowerEntry(left);
					if (entry != null) {
						return entry.getValue().getInVertex().setTimestamp(entry.getKey());
					}
					return null;
				} else {
					Entry<Long, CachedChronoEdge> entry = elem.getValue().floorEntry(left);
					if (entry != null) {
						return entry.getValue().getInVertex().setTimestamp(entry.getKey());
					}
					return null;
				}
			}).filter(elem -> elem != null).collect(Collectors.toSet());
		} else {
			return this.getOutEvents().entrySet().parallelStream().filter(elem -> {
				if (labelIdxSet == null || labelIdxSet.isEmpty() == true)
					return true;
				return labelIdxSet.contains(elem.getKey().getLabelIdx());
			}).map(elem -> {
				// 최대 한개의 edge event를 내보낸다.
				if (tt == AC.$gte) {
					Entry<Long, CachedChronoEdge> entry = elem.getValue().ceilingEntry(left);
					if (entry != null) {
						return entry.getValue().getInVertex().setTimestamp(entry.getKey());
					}
					return null;
				} else if (tt == AC.$gt) {
					Entry<Long, CachedChronoEdge> entry = elem.getValue().higherEntry(left);
					if (entry != null) {
						return entry.getValue().getInVertex().setTimestamp(entry.getKey());
					}
					return null;
				} else if (tt == AC.$eq) {
					if (elem.getValue().containsKey(left)) {
						return elem.getValue().get(left).getInVertex().setTimestamp(left);
					}
					return null;
				} else if (tt == AC.$lt) {
					Entry<Long, CachedChronoEdge> entry = elem.getValue().lowerEntry(left);
					if (entry != null) {
						return entry.getValue().getInVertex().setTimestamp(entry.getKey());
					}
					return null;
				} else {
					Entry<Long, CachedChronoEdge> entry = elem.getValue().floorEntry(left);
					if (entry != null) {
						return entry.getValue().getInVertex().setTimestamp(entry.getKey());
					}
					return null;
				}
			}).filter(elem -> elem != null).collect(Collectors.toSet());
		}
	}

	private Set<CachedVertexEvent> getInVertexEventSet(final HashSet<Long> labelIdxSet, final Long left, final AC tt,
			final int branchFactor) {
		return this.getInEvents().entrySet().parallelStream().filter(elem -> {
			if (labelIdxSet == null || labelIdxSet.isEmpty() == true)
				return true;
			return labelIdxSet.contains(elem.getKey().getLabelIdx());
		}).map(elem -> {
			// 최대 한개의 edge event를 내보낸다.
			if (tt == AC.$gte)
				return elem.getValue().ceilingEntry(left);
			else if (tt == AC.$gt)
				return elem.getValue().higherEntry(left);
			else if (tt == AC.$eq) {
				if (elem.getValue().containsKey(left))
					return elem.getValue().ceilingEntry(left);
				return null;
			} else if (tt == AC.$lt)
				return elem.getValue().lowerEntry(left);
			else
				return elem.getValue().floorEntry(left);
		}).filter(elem -> elem != null).map(elem -> {
			Entry<Long, CachedChronoEdge> entry = (Entry<Long, CachedChronoEdge>) elem;
			return entry.getValue().setTimestamp(entry.getKey());
		}).map(ee -> ee.getVertexEvent(Direction.OUT)).collect(Collectors.toSet());
	}

	/**
	 * Return the vertices adjacent to the vertex according to the provided
	 * direction and edge labels. This method does not remove duplicate vertices
	 * (i.e. those vertices that are connected by more than one edge).
	 *
	 * @param direction
	 *            the direction of the edges of the adjacent vertices
	 * @param labels
	 *            the labels of the edges of the adjacent vertices
	 * @return an iterable of adjacent vertices
	 */
	public Iterable<CachedChronoVertex> getChronoVertices(final Direction direction, final BsonArray labels) {

		HashSet<Long> labelIdxSet = null;
		if (labels != null) {
			labelIdxSet = convertToLabelIdxSet(labels);
		}

		if (direction.equals(Direction.OUT)) {
			return this.getOutChronoVertices(labelIdxSet, Integer.MAX_VALUE);
		} else if (direction.equals(Direction.IN))
			return this.getInChronoVertices(labelIdxSet, Integer.MAX_VALUE);
		else {
			return new MultiIterable<CachedChronoVertex>(
					Arrays.asList(this.getOutChronoVertices(labelIdxSet, Integer.MAX_VALUE),
							this.getInChronoVertices(labelIdxSet, Integer.MAX_VALUE)));
		}
	}

	public Iterable<CachedChronoVertex> getChronoVertices(final Direction direction, final BsonArray labels,
			final int branchFactor) {

		HashSet<Long> labelIdxSet = null;
		if (labels != null) {
			labelIdxSet = convertToLabelIdxSet(labels);
		}

		if (direction.equals(Direction.OUT)) {
			return this.getOutChronoVertices(labelIdxSet, branchFactor);
		} else if (direction.equals(Direction.IN))
			return this.getInChronoVertices(labelIdxSet, branchFactor);
		else {
			return new MultiIterable<CachedChronoVertex>(
					Arrays.asList(this.getOutChronoVertices(labelIdxSet, branchFactor),
							this.getInChronoVertices(labelIdxSet, branchFactor)));
		}
	}

	public Set<CachedChronoVertex> getChronoVertexSet(final Direction direction, final String label,
			final int branchFactor) {

		Long labelIdx = graph.getLabelIndex().get(label);

		if (direction.equals(Direction.OUT)) {
			return this.getOutChronoVertexSet(labelIdx, branchFactor);
		} else if (direction.equals(Direction.IN))
			return this.getInChronoVertexSet(labelIdx, branchFactor);
		else {
			Set<CachedChronoVertex> ret = this.getOutChronoVertexSet(labelIdx, branchFactor);
			ret.addAll(this.getInChronoVertexSet(labelIdx, branchFactor));
			return ret;
		}
	}

	public Stream<CachedChronoVertex> getChronoVertexStream(final Direction direction, final BsonArray labels,
			final int branchFactor, final boolean setParallel) {

		HashSet<Long> labelIdxSet = null;
		if (labels != null) {
			labelIdxSet = convertToLabelIdxSet(labels);
		}

		if (direction.equals(Direction.OUT)) {
			return this.getOutChronoVertexStream(labelIdxSet, branchFactor, setParallel);
		} else if (direction.equals(Direction.IN))
			return this.getInChronoVertexStream(labelIdxSet, branchFactor, setParallel);
		else {
			Set<CachedChronoVertex> ret = this.getOutChronoVertexSet(labelIdxSet, branchFactor);
			ret.addAll(this.getInChronoVertexSet(labelIdxSet, branchFactor));
			if (setParallel)
				return ret.parallelStream();
			else
				return ret.stream();
		}
	}

	public Set<CachedChronoVertex> getChronoVertexSet(final Direction direction, final BsonArray labels,
			final int branchFactor) {

		HashSet<Long> labelIdxSet = null;
		if (labels != null) {
			labelIdxSet = convertToLabelIdxSet(labels);
		}

		if (direction.equals(Direction.OUT)) {
			return this.getOutChronoVertexSet(labelIdxSet, branchFactor);
		} else if (direction.equals(Direction.IN))
			return this.getInChronoVertexSet(labelIdxSet, branchFactor);
		else {
			Set<CachedChronoVertex> ret = this.getOutChronoVertexSet(labelIdxSet, branchFactor);
			ret.addAll(this.getInChronoVertexSet(labelIdxSet, branchFactor));
			return ret;
		}
	}

	/**
	 * Generate a query object that can be used to fine tune which edges/vertices
	 * are retrieved that are incident/adjacent to this vertex.
	 *
	 * @return a vertex query object with methods for constraining which data is
	 *         pulled from the underlying graph
	 */
	@Override
	public VertexQuery query() {
		return new DefaultVertexQuery(this);
	}

	/**
	 * Add a new outgoing edge v.outE from this vertex to the parameter vertex with
	 * provided edge label.
	 *
	 * @param label
	 *            the label of the edge
	 * @param out:
	 *            v.out: the vertex to connect to with an incoming edge to be
	 *            created
	 * @return the newly created edge
	 */
	public CachedChronoEdge addOutEdge(final Long labelIdx, final CachedChronoVertex out) {
		// Create if null
		CachedChronoEdge edge = graph.addEdge((Long) this.getId(), labelIdx, (Long) out.getId());
		Set<CachedChronoEdge> edgesForLabel = this.outEdges.get(labelIdx);
		if (edgesForLabel == null)
			edgesForLabel = new HashSet<CachedChronoEdge>();
		edgesForLabel.add(edge);
		this.outEdges.put(labelIdx, edgesForLabel);
		return edge;
	}

	public CachedChronoEdge addOutEdge(CachedChronoEdge edge) {
		// Create if null
		Long labelIdx = ((CachedEdgeID) edge.getId()).getLabelIdx();
		Set<CachedChronoEdge> edgesForLabel = this.outEdges.get(labelIdx);
		if (edgesForLabel == null)
			edgesForLabel = new HashSet<CachedChronoEdge>();
		edgesForLabel.add(edge);
		this.outEdges.put(labelIdx, edgesForLabel);
		return edge;
	}

	/**
	 * Add a new outgoing edge v.outE from this vertex to the parameter vertex with
	 * provided edge label.
	 *
	 * @param label
	 *            the label of the edge
	 * @param outID:
	 *            v.out identifier
	 * @return the newly created edge
	 */
	public CachedChronoEdge addOutEdge(final String label, final String outID) {
		// Create if null
		CachedChronoEdge edge = graph.addEdge(this.toString(), outID, label);
		Set<CachedChronoEdge> edgesForLabel = this.outEdges.get(((CachedEdgeID) edge.getId()).getLabelIdx());
		if (edgesForLabel == null)
			edgesForLabel = new HashSet<CachedChronoEdge>();
		edgesForLabel.add(edge);
		this.outEdges.put(graph.getLabelIndex().get(label), edgesForLabel);
		return edge;
	}

	/**
	 * Add a new ingoing edge v.inE from this vertex to the parameter vertex with
	 * provided edge label.
	 *
	 * @param label
	 *            the label of the edge
	 * @param in:
	 *            v.in: the vertex to connect to with an incoming edge to be created
	 * @return the newly created edge
	 */
	public CachedChronoEdge addInEdge(final Long labelIdx, final CachedChronoVertex in) {
		// Create if null
		CachedChronoEdge edge = graph.addEdge((Long) in.getId(), (Long) this.getId(), labelIdx);
		Set<CachedChronoEdge> edgesForLabel = this.inEdges.get(labelIdx);
		if (edgesForLabel == null)
			edgesForLabel = new HashSet<CachedChronoEdge>();
		edgesForLabel.add(edge);
		this.inEdges.put(labelIdx, edgesForLabel);
		return edge;
	}

	public CachedChronoEdge addInEdge(CachedChronoEdge edge) {
		// Create if null
		Long labelIdx = ((CachedEdgeID) edge.getId()).getLabelIdx();
		Set<CachedChronoEdge> edgesForLabel = this.inEdges.get(labelIdx);
		if (edgesForLabel == null)
			edgesForLabel = new HashSet<CachedChronoEdge>();
		edgesForLabel.add(edge);
		this.inEdges.put(labelIdx, edgesForLabel);
		return edge;
	}

	/**
	 * Add a new ingoing edge v.inE from this vertex to the parameter vertex with
	 * provided edge label.
	 *
	 * @param label
	 *            the label of the edge
	 * @param inID:
	 *            v.in identifier
	 * @return the newly created edge
	 */
	public CachedChronoEdge addInEdge(final String label, final String inID) {
		// Create if null
		CachedChronoEdge edge = graph.addEdge(inID, this.toString(), label);
		Set<CachedChronoEdge> edgesForLabel = this.inEdges.get(((CachedEdgeID) edge.getId()).getLabelIdx());
		if (edgesForLabel == null)
			edgesForLabel = new HashSet<CachedChronoEdge>();
		edgesForLabel.add(edge);
		this.inEdges.put(graph.getLabelIndex().get(label), edgesForLabel);
		return edge;
	}

	/**
	 * Return timestampVertexEvent with given timestamp (No interaction with DB)
	 * 
	 * @param timestamp
	 * @return timestampVertexEvent
	 */
	public CachedVertexEvent setTimestamp(final Long timestamp) {
		if (timestamp == null)
			throw ExceptionFactory.timestampCanNotBeNull();

		return new CachedVertexEvent(this, timestamp);
	}

	/**
	 * Return timestampVertexEvent with the existing timestamp
	 * 
	 * @param timestamp
	 *            existing timestamp property key
	 * @return timestampVertexEvent or null
	 */
	public CachedVertexEvent pickTimestamp(Long timestamp) {
		if (this.getTimestampProperties(timestamp) != null) {
			return new CachedVertexEvent(this, timestamp);
		}
		return null;
	}

	/**
	 * @return timestampVertexEvent with the first timestamp or null
	 */
	public CachedVertexEvent pickFirstTimestamp() {
		Long t = this.getFirstTimestamp();
		if (t == null)
			return null;
		else
			return new CachedVertexEvent(this, t);
	}

	/**
	 * @return timestampVertexEvent with the last timestamp or null
	 */
	public CachedVertexEvent pickLastTimestamp() {
		Long t = this.getLastTimestamp();
		if (t == null)
			return null;
		else
			return new CachedVertexEvent(this, t);
	}

	/**
	 * Ceiling: greater than or equal to the given timestamp
	 * 
	 * @param timestamp
	 * @return timestampVertexEvent with the ceiling timestamp of the given
	 *         timestamp or null
	 */
	public CachedVertexEvent pickCeilingTimestamp(Long timestamp) {
		Long ceilingTimestamp = this.getCeilingTimestamp(timestamp);
		if (ceilingTimestamp != null)
			return new CachedVertexEvent(this, ceilingTimestamp);
		return null;
	}

	/**
	 * @param timestamp
	 * @return timestampVertexEvent with the higher timestamp of the given timestamp
	 *         or null
	 */
	public CachedVertexEvent pickHigherTimestamp(Long timestamp) {
		Long higherTimestamp = this.getHigherTimestamp(timestamp);
		if (higherTimestamp != null)
			return new CachedVertexEvent(this, higherTimestamp);
		return null;
	}

	/**
	 * Floor: less than or equal to the given timestamp
	 * 
	 * @param timestamp
	 * @return timestampVertexEvent with the floor timestamp of the given timestamp
	 *         or null
	 */
	public CachedVertexEvent pickFloorTimestamp(Long timestamp) {
		Long floorTimestamp = this.getFloorTimestamp(timestamp);
		if (floorTimestamp != null)
			return new CachedVertexEvent(this, floorTimestamp);
		return null;
	}

	/**
	 * Lower: less than or equal to the given timestamp
	 * 
	 * @param timestamp
	 * @return timestampVertexEvent with the lower timestamp of the given timestamp
	 *         or null
	 */
	public CachedVertexEvent pickLowerTimestamp(Long timestamp) {
		Long lowerTimestamp = this.getLowerTimestamp(timestamp);
		if (lowerTimestamp != null)
			return new CachedVertexEvent(this, lowerTimestamp);
		return null;
	}

	/**
	 * Return in-going edges (Internal Method)
	 * 
	 * @param labels
	 * @return in-going edges
	 */
	private Iterable<CachedChronoEdge> getInChronoEdges(final HashSet<Long> labelIdxSet, final int branchFactor) {
		if (labelIdxSet == null && branchFactor == Integer.MAX_VALUE) {
			return this.inEdges.values().parallelStream().flatMap(e -> e.parallelStream()).collect(Collectors.toSet());
		} else if (labelIdxSet == null && branchFactor != Integer.MAX_VALUE) {
			return this.inEdges.values().parallelStream().flatMap(e -> e.parallelStream()).limit(branchFactor)
					.collect(Collectors.toSet());
		} else if (labelIdxSet != null && branchFactor == Integer.MAX_VALUE) {
			return this.inEdges.entrySet().parallelStream().filter(e -> {
				if (labelIdxSet.contains(e.getKey()))
					return true;
				else
					return false;
			}).map(e -> e.getValue()).flatMap(e -> e.parallelStream()).collect(Collectors.toSet());

		} else {
			return this.inEdges.entrySet().parallelStream().filter(e -> {
				if (labelIdxSet.contains(e.getKey()))
					return true;
				else
					return false;
			}).map(e -> e.getValue()).flatMap(e -> e.parallelStream()).limit(branchFactor).collect(Collectors.toSet());
		}
	}

	private Stream<CachedChronoEdge> getInChronoEdgeStream(final HashSet<Long> labelIdxSet, final int branchFactor,
			final boolean setParallel) {
		if (labelIdxSet == null && branchFactor == Integer.MAX_VALUE) {
			if (setParallel)
				return this.inEdges.values().parallelStream().flatMap(e -> e.parallelStream());
			else
				return this.inEdges.values().stream().flatMap(e -> e.parallelStream());
		} else if (labelIdxSet.isEmpty() && branchFactor != Integer.MAX_VALUE) {
			if (setParallel)
				return this.inEdges.values().parallelStream().flatMap(e -> e.parallelStream()).limit(branchFactor);
			else
				return this.inEdges.values().stream().flatMap(e -> e.parallelStream()).limit(branchFactor);
		} else if (!labelIdxSet.isEmpty() && branchFactor == Integer.MAX_VALUE) {
			if (setParallel)
				return this.inEdges.entrySet().parallelStream().filter(e -> {
					if (labelIdxSet.contains(e.getKey()))
						return true;
					else
						return false;
				}).map(e -> e.getValue()).flatMap(e -> e.parallelStream());
			else
				return this.inEdges.entrySet().stream().filter(e -> {
					if (labelIdxSet.contains(e.getKey()))
						return true;
					else
						return false;
				}).map(e -> e.getValue()).flatMap(e -> e.parallelStream());
		} else {
			if (setParallel)
				return this.inEdges.entrySet().parallelStream().filter(e -> {
					if (labelIdxSet.contains(e.getKey()))
						return true;
					else
						return false;
				}).map(e -> e.getValue()).flatMap(e -> e.parallelStream()).limit(branchFactor);
			else
				return this.inEdges.entrySet().stream().filter(e -> {
					if (labelIdxSet.contains(e.getKey()))
						return true;
					else
						return false;
				}).map(e -> e.getValue()).flatMap(e -> e.parallelStream()).limit(branchFactor);
		}
	}

	private Set<CachedChronoEdge> getInChronoEdgeSet(final Long labelIdx, final int branchFactor) {
		if (labelIdx == null && branchFactor == Integer.MAX_VALUE) {
			return this.inEdges.values().parallelStream().flatMap(e -> e.parallelStream()).collect(Collectors.toSet());
		} else if (labelIdx == null && branchFactor != Integer.MAX_VALUE) {
			return this.inEdges.values().parallelStream().flatMap(e -> e.parallelStream()).limit(branchFactor)
					.collect(Collectors.toSet());
		} else if (!(labelIdx == null) && branchFactor == Integer.MAX_VALUE) {
			return this.inEdges.entrySet().parallelStream().filter(e -> {
				if (labelIdx == e.getKey())
					return true;
				else
					return false;
			}).map(e -> e.getValue()).flatMap(e -> e.parallelStream()).collect(Collectors.toSet());
		} else {
			return this.inEdges.entrySet().parallelStream().filter(e -> {
				if (labelIdx == e.getKey())
					return true;
				else
					return false;
			}).map(e -> e.getValue()).flatMap(e -> e.parallelStream()).limit(branchFactor).collect(Collectors.toSet());
		}
	}

	private Set<CachedChronoEdge> getInChronoEdgeSet(final HashSet<Long> labelIdxSet, final int branchFactor) {
		if (labelIdxSet.isEmpty() && branchFactor == Integer.MAX_VALUE) {
			return this.inEdges.values().parallelStream().flatMap(e -> e.parallelStream()).collect(Collectors.toSet());
		} else if (labelIdxSet.isEmpty() && branchFactor != Integer.MAX_VALUE) {
			return this.inEdges.values().parallelStream().flatMap(e -> e.parallelStream()).limit(branchFactor)
					.collect(Collectors.toSet());
		} else if (!labelIdxSet.isEmpty() && branchFactor == Integer.MAX_VALUE) {
			return this.inEdges.entrySet().parallelStream().filter(e -> {
				if (labelIdxSet.contains(e.getKey()))
					return true;
				else
					return false;
			}).map(e -> e.getValue()).flatMap(e -> e.parallelStream()).collect(Collectors.toSet());
		} else {
			return this.inEdges.entrySet().parallelStream().filter(e -> {
				if (labelIdxSet.contains(e.getKey()))
					return true;
				else
					return false;
			}).map(e -> e.getValue()).flatMap(e -> e.parallelStream()).limit(branchFactor).collect(Collectors.toSet());
		}
	}

	/**
	 * Return out-going edges (Internal Method)
	 * 
	 * @param labels
	 * @return
	 */
	private Iterable<CachedChronoEdge> getOutChronoEdges(final HashSet<Long> labelIdxSet, final int branchFactor) {
		if (labelIdxSet == null && branchFactor == Integer.MAX_VALUE) {
			return this.outEdges.values().parallelStream().flatMap(e -> e.parallelStream()).collect(Collectors.toSet());
		} else if (labelIdxSet == null && branchFactor != Integer.MAX_VALUE) {
			return this.outEdges.values().parallelStream().flatMap(e -> e.parallelStream()).limit(branchFactor)
					.collect(Collectors.toSet());
		} else if (labelIdxSet != null && branchFactor == Integer.MAX_VALUE) {
			return this.outEdges.entrySet().parallelStream().filter(e -> {
				if (labelIdxSet.contains(e.getKey()))
					return true;
				else
					return false;
			}).map(e -> e.getValue()).flatMap(e -> e.parallelStream()).collect(Collectors.toSet());

		} else {
			return this.outEdges.entrySet().parallelStream().filter(e -> {
				if (labelIdxSet.contains(e.getKey()))
					return true;
				else
					return false;
			}).map(e -> e.getValue()).flatMap(e -> e.parallelStream()).limit(branchFactor).collect(Collectors.toSet());
		}
	}

	private Stream<CachedChronoEdge> getOutChronoEdgeStream(final HashSet<Long> labelIdxSet, final int branchFactor,
			final boolean setParallel) {
		if (labelIdxSet == null && branchFactor == Integer.MAX_VALUE) {
			if (setParallel)
				return this.outEdges.values().parallelStream().flatMap(e -> e.parallelStream());
			else
				return this.outEdges.values().stream().flatMap(e -> e.parallelStream());
		} else if (labelIdxSet == null && branchFactor != Integer.MAX_VALUE) {
			if (setParallel)
				return this.outEdges.values().parallelStream().flatMap(e -> e.parallelStream()).limit(branchFactor);
			else
				return this.outEdges.values().stream().flatMap(e -> e.parallelStream()).limit(branchFactor);
		} else if (labelIdxSet != null && branchFactor == Integer.MAX_VALUE) {
			if (setParallel) {
				return this.outEdges.entrySet().parallelStream().filter(e -> {
					if (labelIdxSet.contains(e.getKey()))
						return true;
					else
						return false;
				}).map(e -> e.getValue()).flatMap(e -> e.parallelStream());
			} else {
				return this.outEdges.entrySet().stream().filter(e -> {
					if (labelIdxSet.contains(e.getKey()))
						return true;
					else
						return false;
				}).map(e -> e.getValue()).flatMap(e -> e.parallelStream());
			}
		} else {
			if (setParallel) {
				return this.outEdges.entrySet().parallelStream().filter(e -> {
					if (labelIdxSet.contains(e.getKey()))
						return true;
					else
						return false;
				}).map(e -> e.getValue()).flatMap(e -> e.parallelStream()).limit(branchFactor);
			} else {
				return this.outEdges.entrySet().stream().filter(e -> {
					if (labelIdxSet.contains(e.getKey()))
						return true;
					else
						return false;
				}).map(e -> e.getValue()).flatMap(e -> e.parallelStream()).limit(branchFactor);
			}
		}
	}

	private Set<CachedChronoEdge> getOutChronoEdgeSet(final Long labelIdx, final int branchFactor) {
		if (labelIdx == null && branchFactor == Integer.MAX_VALUE) {
			return this.outEdges.values().parallelStream().flatMap(e -> e.parallelStream()).collect(Collectors.toSet());
		} else if (labelIdx == null && branchFactor != Integer.MAX_VALUE) {
			return this.outEdges.values().parallelStream().flatMap(e -> e.parallelStream()).limit(branchFactor)
					.collect(Collectors.toSet());
		} else if (labelIdx != null && branchFactor == Integer.MAX_VALUE) {
			return this.outEdges.entrySet().parallelStream().filter(e -> {
				if (labelIdx == e.getKey())
					return true;
				else
					return false;
			}).map(e -> e.getValue()).flatMap(e -> e.parallelStream()).collect(Collectors.toSet());
		} else {
			return this.outEdges.entrySet().parallelStream().filter(e -> {
				if (labelIdx == e.getKey())
					return true;
				else
					return false;
			}).map(e -> e.getValue()).flatMap(e -> e.parallelStream()).limit(branchFactor).collect(Collectors.toSet());
		}
	}

	private Set<CachedChronoEdge> getOutChronoEdgeSet(final HashSet<Long> labelIdxSet, final int branchFactor) {
		if (labelIdxSet == null && branchFactor == Integer.MAX_VALUE) {
			return this.outEdges.values().parallelStream().flatMap(e -> e.parallelStream()).collect(Collectors.toSet());
		} else if (labelIdxSet == null && branchFactor != Integer.MAX_VALUE) {
			return this.outEdges.values().parallelStream().flatMap(e -> e.parallelStream()).limit(branchFactor)
					.collect(Collectors.toSet());
		} else if (labelIdxSet != null && branchFactor == Integer.MAX_VALUE) {
			return this.outEdges.entrySet().parallelStream().filter(e -> {
				if (labelIdxSet.contains(e.getKey()))
					return true;
				else
					return false;
			}).map(e -> e.getValue()).flatMap(e -> e.parallelStream()).collect(Collectors.toSet());
		} else {
			return this.outEdges.entrySet().parallelStream().filter(e -> {
				if (labelIdxSet.contains(e.getKey()))
					return true;
				else
					return false;
			}).map(e -> e.getValue()).flatMap(e -> e.parallelStream()).limit(branchFactor).collect(Collectors.toSet());
		}
	}

	private Iterable<CachedChronoVertex> getOutChronoVertices(HashSet<Long> labelIdxSet, final int branchFactor) {
		return this.outEdges.entrySet().parallelStream().filter(e -> !labelIdxSet.contains(e.getKey()))
				.map(e -> e.getValue()).flatMap(e -> e.parallelStream()).map(e -> e.getInVertex())
				.collect(Collectors.toSet());
	}

	private Set<CachedChronoVertex> getOutChronoVertexSet(Long labelIdx, final int branchFactor) {
		if (branchFactor == Integer.MAX_VALUE) {
			if (this.outEdges.containsKey(labelIdx)) {
				return this.outEdges.get(labelIdx).parallelStream().map(e -> e.getInVertex())
						.collect(Collectors.toSet());
			} else {
				return null;
			}
		} else {
			if (this.outEdges.containsKey(labelIdx)) {
				return this.outEdges.get(labelIdx).parallelStream().map(e -> e.getInVertex()).limit(branchFactor)
						.collect(Collectors.toSet());
			} else {
				return null;
			}
		}
	}

	private Stream<CachedChronoVertex> getOutChronoVertexStream(final HashSet<Long> labelIdxSet, final int branchFactor,
			final boolean setParallel) {
		if (labelIdxSet == null && branchFactor == Integer.MAX_VALUE) {
			if (setParallel)
				return this.outEdges.values().parallelStream().flatMap(e -> e.parallelStream())
						.map(e -> e.getInVertex());
			else
				return this.outEdges.values().stream().flatMap(e -> e.parallelStream()).map(e -> e.getInVertex());
		} else if (labelIdxSet == null && branchFactor != Integer.MAX_VALUE) {
			if (setParallel)
				return this.outEdges.values().parallelStream().flatMap(e -> e.parallelStream())
						.map(e -> e.getInVertex()).limit(branchFactor);
			else
				return this.outEdges.values().stream().flatMap(e -> e.parallelStream()).map(e -> e.getInVertex())
						.limit(branchFactor);
		} else if (labelIdxSet != null && branchFactor == Integer.MAX_VALUE) {
			if (setParallel)
				return this.outEdges.entrySet().parallelStream().filter(e -> {
					if (labelIdxSet.contains(e.getKey()))
						return true;
					else
						return false;
				}).map(e -> e.getValue()).flatMap(e -> e.parallelStream()).map(e -> e.getInVertex());
			else
				return this.outEdges.entrySet().stream().filter(e -> {
					if (labelIdxSet.contains(e.getKey()))
						return true;
					else
						return false;
				}).map(e -> e.getValue()).flatMap(e -> e.parallelStream()).map(e -> e.getInVertex());
		} else {
			if (setParallel)
				return this.outEdges.entrySet().parallelStream().filter(e -> {
					if (labelIdxSet.contains(e.getKey()))
						return true;
					else
						return false;
				}).map(e -> e.getValue()).flatMap(e -> e.parallelStream()).map(e -> e.getInVertex())
						.limit(branchFactor);
			else
				return this.outEdges.entrySet().stream().filter(e -> {
					if (labelIdxSet.contains(e.getKey()))
						return true;
					else
						return false;
				}).map(e -> e.getValue()).flatMap(e -> e.parallelStream()).map(e -> e.getInVertex())
						.limit(branchFactor);
		}
	}

	private Set<CachedChronoVertex> getOutChronoVertexSet(HashSet<Long> labelIdxSet, final int branchFactor) {
		if (labelIdxSet == null && branchFactor == Integer.MAX_VALUE) {
			return this.outEdges.values().parallelStream().flatMap(e -> e.parallelStream()).map(e -> e.getInVertex())
					.collect(Collectors.toSet());
		} else if (labelIdxSet == null && branchFactor != Integer.MAX_VALUE) {
			return this.outEdges.values().parallelStream().flatMap(e -> e.parallelStream()).map(e -> e.getInVertex())
					.limit(branchFactor).collect(Collectors.toSet());
		} else if (labelIdxSet != null && branchFactor == Integer.MAX_VALUE) {
			return this.outEdges.entrySet().parallelStream().filter(e -> {
				if (labelIdxSet.contains(e.getKey()))
					return true;
				else
					return false;
			}).map(e -> e.getValue()).flatMap(e -> e.parallelStream()).map(e -> e.getInVertex())
					.collect(Collectors.toSet());
		} else {
			return this.outEdges.entrySet().parallelStream().filter(e -> {
				if (labelIdxSet.contains(e.getKey()))
					return true;
				else
					return false;
			}).map(e -> e.getValue()).flatMap(e -> e.parallelStream()).map(e -> e.getInVertex()).limit(branchFactor)
					.collect(Collectors.toSet());
		}
	}

	private Iterable<CachedChronoVertex> getInChronoVertices(HashSet<Long> labelIdxSet, final int branchFactor) {

		return this.inEdges.entrySet().parallelStream().filter(e -> !labelIdxSet.contains(e.getKey()))
				.map(e -> e.getValue()).flatMap(e -> e.parallelStream()).map(e -> e.getOutVertex())
				.collect(Collectors.toSet());
	}

	private Set<CachedChronoVertex> getInChronoVertexSet(Long labelIdx, final int branchFactor) {
		if (branchFactor == Integer.MAX_VALUE) {
			return this.inEdges.get(labelIdx).parallelStream().map(e -> e.getOutVertex()).collect(Collectors.toSet());
		} else {
			return this.inEdges.get(labelIdx).parallelStream().map(e -> e.getOutVertex()).limit(branchFactor)
					.collect(Collectors.toSet());
		}
	}

	private Stream<CachedChronoVertex> getInChronoVertexStream(final HashSet<Long> labelIdxSet, final int branchFactor,
			final boolean setParallel) {
		if (labelIdxSet == null && branchFactor == Integer.MAX_VALUE) {
			if (setParallel)
				return this.inEdges.values().parallelStream().flatMap(e -> e.parallelStream())
						.map(e -> e.getOutVertex());
			else
				return this.inEdges.values().stream().flatMap(e -> e.parallelStream()).map(e -> e.getOutVertex());
		} else if (labelIdxSet == null && branchFactor != Integer.MAX_VALUE) {
			if (setParallel)
				return this.inEdges.values().parallelStream().flatMap(e -> e.parallelStream())
						.map(e -> e.getOutVertex()).limit(branchFactor);
			else
				return this.inEdges.values().stream().flatMap(e -> e.parallelStream()).map(e -> e.getOutVertex())
						.limit(branchFactor);
		} else if (labelIdxSet != null && branchFactor == Integer.MAX_VALUE) {
			if (setParallel)
				return this.inEdges.entrySet().parallelStream().filter(e -> {
					if (labelIdxSet.contains(e.getKey()))
						return true;
					else
						return false;
				}).map(e -> e.getValue()).flatMap(e -> e.parallelStream()).map(e -> e.getOutVertex());
			else
				return this.inEdges.entrySet().stream().filter(e -> {
					if (labelIdxSet.contains(e.getKey()))
						return true;
					else
						return false;
				}).map(e -> e.getValue()).flatMap(e -> e.parallelStream()).map(e -> e.getOutVertex());
		} else {
			if (setParallel)
				return this.inEdges.entrySet().parallelStream().filter(e -> {
					if (labelIdxSet.contains(e.getKey()))
						return true;
					else
						return false;
				}).map(e -> e.getValue()).flatMap(e -> e.parallelStream()).map(e -> e.getOutVertex())
						.limit(branchFactor);
			else
				return this.inEdges.entrySet().stream().filter(e -> {
					if (labelIdxSet.contains(e.getKey()))
						return true;
					else
						return false;
				}).map(e -> e.getValue()).flatMap(e -> e.parallelStream()).map(e -> e.getOutVertex())
						.limit(branchFactor);
		}
	}

	private Set<CachedChronoVertex> getInChronoVertexSet(HashSet<Long> labelIdxSet, final int branchFactor) {
		if (labelIdxSet.isEmpty() && branchFactor == Integer.MAX_VALUE) {
			return this.inEdges.values().parallelStream().flatMap(e -> e.parallelStream()).map(e -> e.getOutVertex())
					.collect(Collectors.toSet());
		} else if (labelIdxSet.isEmpty() && branchFactor != Integer.MAX_VALUE) {
			return this.inEdges.values().parallelStream().flatMap(e -> e.parallelStream()).map(e -> e.getOutVertex())
					.limit(branchFactor).collect(Collectors.toSet());
		} else if (!labelIdxSet.isEmpty() && branchFactor == Integer.MAX_VALUE) {
			return this.inEdges.entrySet().parallelStream().filter(e -> {
				if (labelIdxSet.contains(e.getKey()))
					return true;
				else
					return false;
			}).map(e -> e.getValue()).flatMap(e -> e.parallelStream()).map(e -> e.getOutVertex())
					.collect(Collectors.toSet());
		} else {
			return this.inEdges.entrySet().parallelStream().filter(e -> {
				if (labelIdxSet.contains(e.getKey()))
					return true;
				else
					return false;
			}).map(e -> e.getValue()).flatMap(e -> e.parallelStream()).map(e -> e.getOutVertex()).limit(branchFactor)
					.collect(Collectors.toSet());
		}
	}

	/**
	 * @deprecated use getChronoEdges(Direction, BsonArray)
	 */
	@Deprecated
	public Iterable<Edge> getEdges(final Direction direction, final String... labels) {
		return null;
	}

	/**
	 * @deprecated use getChronoVertices(Direction, labels)
	 */
	@Deprecated
	@Override
	public Iterable<Vertex> getVertices(final Direction direction, final String... labels) {
		return null;
	}

	/**
	 * @deprecated use addOutEdge(label, out) or addOutEdge(label, outID)
	 */
	@Deprecated
	@Override
	public Edge addEdge(final String label, final Vertex vertex) {
		return null;
	}
}
