package org.lilliput.chronograph.persistent;

import com.mongodb.MongoCursorNotFoundException;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.VertexQuery;
import com.tinkerpop.blueprints.util.DefaultVertexQuery;
import com.tinkerpop.blueprints.util.MultiIterable;
import com.tinkerpop.blueprints.util.VerticesFromEdgesIterable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Stream;

import org.bson.BsonArray;
import org.bson.BsonBoolean;
import org.bson.BsonDocument;
import org.bson.BsonString;
import org.lilliput.chronograph.common.ExceptionFactory;
import org.lilliput.chronograph.common.Tokens;
import org.lilliput.chronograph.persistent.util.Converter;

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
public class ChronoVertex extends ChronoElement implements Vertex {

	public ChronoVertex(final String id, final ChronoGraph graph) {
		super(id, graph);
	}

	public ChronoGraph getGraph() {
		return this.graph;
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
	public Iterable<ChronoEdge> getChronoEdges(final Direction direction, final BsonArray labels) {
		return getChronoEdges(direction, labels, Integer.MAX_VALUE);
	}

	public Set<ChronoEdge> getChronoEdgeSet(final Direction direction, final BsonArray labels) {
		return getChronoEdgeSet(direction, labels, Integer.MAX_VALUE);
	}

	public Stream<ChronoEdge> getChronoEdgeStream(final Direction direction, final BsonArray labels,
			final boolean setParallel) {
		return getChronoEdgeStream(direction, labels, Integer.MAX_VALUE, setParallel);
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
	public Iterable<ChronoEdge> getChronoEdges(final Direction direction, final BsonArray labels,
			final int branchFactor) {
		if (direction.equals(Direction.OUT)) {
			return this.getOutChronoEdges(labels, branchFactor);
		} else if (direction.equals(Direction.IN))
			return this.getInChronoEdges(labels, branchFactor);
		else {
			return new MultiIterable<ChronoEdge>(Arrays.asList(this.getInChronoEdges(labels, branchFactor),
					this.getOutChronoEdges(labels, branchFactor)));
		}
	}

	public Set<ChronoEdge> getChronoEdgeSet(final Direction direction, final BsonArray labels, final int branchFactor) {
		if (direction.equals(Direction.OUT)) {
			return this.getOutChronoEdgeSet(labels, branchFactor);
		} else if (direction.equals(Direction.IN))
			return this.getInChronoEdgeSet(labels, branchFactor);
		else {
			Set<ChronoEdge> edgeSet = this.getOutChronoEdgeSet(labels, branchFactor);
			edgeSet.addAll(this.getInChronoEdgeSet(labels, branchFactor));
			return edgeSet;
		}
	}

	public Stream<ChronoEdge> getChronoEdgeStream(final Direction direction, final BsonArray labels,
			final int branchFactor, final boolean setParallel) {
		if (direction.equals(Direction.OUT)) {
			return this.getOutChronoEdgeStream(labels, branchFactor, setParallel);
		} else if (direction.equals(Direction.IN))
			return this.getInChronoEdgeStream(labels, branchFactor, setParallel);
		else {
			Set<ChronoEdge> ret = this.getOutChronoEdgeSet(labels, branchFactor);
			ret.addAll(this.getInChronoEdgeSet(labels, branchFactor));
			if (setParallel)
				return getChronoEdgeSet(direction, labels, branchFactor).parallelStream();
			else
				return getChronoEdgeSet(direction, labels, branchFactor).stream();
		}
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
	public Iterable<ChronoVertex> getChronoVertices(final Direction direction, final BsonArray labels) {
		if (direction.equals(Direction.OUT)) {
			return this.getOutChronoVertices(labels, Integer.MAX_VALUE);
		} else if (direction.equals(Direction.IN))
			return this.getInChronoVertices(labels, Integer.MAX_VALUE);
		else {
			return new MultiIterable<ChronoVertex>(Arrays.asList(this.getOutChronoVertices(labels, Integer.MAX_VALUE),
					this.getInChronoVertices(labels, Integer.MAX_VALUE)));
		}
	}

	public Iterable<ChronoVertex> getChronoVertices(final Direction direction, final BsonArray labels,
			final int branchFactor) {
		if (direction.equals(Direction.OUT)) {
			return this.getOutChronoVertices(labels, branchFactor);
		} else if (direction.equals(Direction.IN))
			return this.getInChronoVertices(labels, branchFactor);
		else {
			return new MultiIterable<ChronoVertex>(Arrays.asList(this.getOutChronoVertices(labels, branchFactor),
					this.getInChronoVertices(labels, branchFactor)));
		}
	}

	public Set<ChronoVertex> getChronoVertexSet(final Direction direction, final BsonArray labels,
			final int branchFactor) {
		if (direction.equals(Direction.OUT)) {
			return this.getOutChronoVertexSet(labels, branchFactor);
		} else if (direction.equals(Direction.IN))
			return this.getInChronoVertexSet(labels, branchFactor);
		else {
			Set<ChronoVertex> bothVSet = this.getOutChronoVertexSet(labels, branchFactor);
			bothVSet.addAll(this.getInChronoVertexSet(labels, branchFactor));
			return bothVSet;
		}
	}

	public Stream<ChronoVertex> getChronoVertexStream(final Direction direction, final BsonArray labels,
			final int branchFactor, final boolean setParallel) {
		if (direction.equals(Direction.OUT)) {
			return this.getOutChronoVertexStream(labels, branchFactor, setParallel);
		} else if (direction.equals(Direction.IN))
			return this.getInChronoVertexStream(labels, branchFactor, setParallel);
		else {
			Set<ChronoVertex> bothVSet = this.getOutChronoVertexSet(labels, branchFactor);
			bothVSet.addAll(this.getInChronoVertexSet(labels, branchFactor));
			if (setParallel)
				return bothVSet.parallelStream();
			else
				return bothVSet.stream();
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
	public ChronoEdge addOutEdge(final String label, final ChronoVertex out) {
		return this.graph.addEdge(id.toString(), out.toString(), label);
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
	public ChronoEdge addOutEdge(final String label, final String outID) {
		return this.graph.addEdge(id.toString(), outID, label);
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
	public ChronoEdge addInEdge(final String label, final ChronoVertex in) {
		return this.graph.addEdge(in.toString(), id.toString(), label);
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
	public ChronoEdge addInEdge(final String label, final String inID) {
		return this.graph.addEdge(inID, id.toString(), label);
	}

	/**
	 * Return timestampVertexEvent with given timestamp (No interaction with DB)
	 * 
	 * @param timestamp
	 * @return timestampVertexEvent
	 */
	public VertexEvent setTimestamp(Long timestamp) {
		if (timestamp == null)
			throw ExceptionFactory.timestampCanNotBeNull();

		return new VertexEvent(this.graph, this, timestamp);
	}

	/**
	 * Return timestampVertexEvent with the existing timestamp
	 * 
	 * @param timestamp
	 *            existing timestamp property key
	 * @return timestampVertexEvent or null
	 */
	public VertexEvent pickTimestamp(Long timestamp) {
		if (this.getTimestampProperties(timestamp) != null) {
			return new VertexEvent(this.graph, this, timestamp);
		}
		return null;
	}

	/**
	 * @return timestampVertexEvent with the first timestamp or null
	 */
	public VertexEvent pickFirstTimestamp() {
		Long t = this.getFirstTimestamp();
		if (t == null)
			return null;
		else
			return new VertexEvent(this.graph, this, t);
	}

	/**
	 * @return timestampVertexEvent with the last timestamp or null
	 */
	public VertexEvent pickLastTimestamp() {
		Long t = this.getLastTimestamp();
		if (t == null)
			return null;
		else
			return new VertexEvent(this.graph, this, t);
	}

	/**
	 * Ceiling: greater than or equal to the given timestamp
	 * 
	 * @param timestamp
	 * @return timestampVertexEvent with the ceiling timestamp of the given
	 *         timestamp or null
	 */
	public VertexEvent pickCeilingTimestamp(Long timestamp) {
		Long ceilingTimestamp = this.getCeilingTimestamp(timestamp);
		if (ceilingTimestamp != null)
			return new VertexEvent(this.graph, this, ceilingTimestamp);
		return null;
	}

	/**
	 * @param timestamp
	 * @return timestampVertexEvent with the higher timestamp of the given timestamp
	 *         or null
	 */
	public VertexEvent pickHigherTimestamp(Long timestamp) {
		Long higherTimestamp = this.getHigherTimestamp(timestamp);
		if (higherTimestamp != null)
			return new VertexEvent(this.graph, this, higherTimestamp);
		return null;
	}

	/**
	 * Floor: less than or equal to the given timestamp
	 * 
	 * @param timestamp
	 * @return timestampVertexEvent with the floor timestamp of the given timestamp
	 *         or null
	 */
	public VertexEvent pickFloorTimestamp(Long timestamp) {
		Long floorTimestamp = this.getFloorTimestamp(timestamp);
		if (floorTimestamp != null)
			return new VertexEvent(this.graph, this, floorTimestamp);
		return null;
	}

	/**
	 * Lower: less than or equal to the given timestamp
	 * 
	 * @param timestamp
	 * @return timestampVertexEvent with the lower timestamp of the given timestamp
	 *         or null
	 */
	public VertexEvent pickLowerTimestamp(Long timestamp) {
		Long lowerTimestamp = this.getLowerTimestamp(timestamp);
		if (lowerTimestamp != null)
			return new VertexEvent(this.graph, this, lowerTimestamp);
		return null;
	}

	/**
	 * Return in-going edges (Internal Method)
	 * 
	 * @param labels
	 * @return in-going edges
	 */
	private Iterable<ChronoEdge> getInChronoEdges(final BsonArray labels, final int branchFactor) {
		HashSet<ChronoEdge> edgeSet = new HashSet<ChronoEdge>();
		BsonDocument filter = new BsonDocument();
		BsonDocument inner = new BsonDocument();
		filter.put(Tokens.IN_VERTEX, new BsonString(this.toString()));
		if (labels != null && labels.size() != 0) {
			inner.put(Tokens.FC.$in.toString(), labels);
			filter.put(Tokens.LABEL, inner);
		}

		Iterator<BsonDocument> it = null;
		if (branchFactor == Integer.MAX_VALUE)
			it = graph.getEdgeCollection().find(filter).projection(Tokens.PRJ_ONLY_ID).iterator();
		else
			it = graph.getEdgeCollection().find(filter).projection(Tokens.PRJ_ONLY_ID).limit(branchFactor).iterator();

		while (it.hasNext()) {
			BsonDocument d = it.next();
			edgeSet.add(new ChronoEdge(d.getString(Tokens.ID).getValue(), this.graph));
		}
		return edgeSet;
	}

	private Set<ChronoEdge> getInChronoEdgeSet(final BsonArray labels, final int branchFactor) {
		HashSet<ChronoEdge> edgeSet = new HashSet<ChronoEdge>();
		BsonDocument filter = new BsonDocument();
		BsonDocument inner = new BsonDocument();
		filter.put(Tokens.IN_VERTEX, new BsonString(this.toString()));
		if (labels != null && labels.size() != 0) {
			inner.put(Tokens.FC.$in.toString(), labels);
			filter.put(Tokens.LABEL, inner);
		}

		Iterator<BsonDocument> it = null;
		if (branchFactor == Integer.MAX_VALUE)
			it = graph.getEdgeCollection().find(filter).projection(Tokens.PRJ_ONLY_ID).iterator();
		else
			it = graph.getEdgeCollection().find(filter).projection(Tokens.PRJ_ONLY_ID).limit(branchFactor).iterator();

		while (it.hasNext()) {
			BsonDocument d = it.next();
			edgeSet.add(new ChronoEdge(d.getString(Tokens.ID).getValue(), this.graph));
		}
		return edgeSet;
	}

	private Stream<ChronoEdge> getInChronoEdgeStream(final BsonArray labels, final int branchFactor,
			final boolean setParallel) {
		HashSet<ChronoEdge> edgeSet = new HashSet<ChronoEdge>();
		BsonDocument filter = new BsonDocument();
		BsonDocument inner = new BsonDocument();
		filter.put(Tokens.IN_VERTEX, new BsonString(this.toString()));
		if (labels != null && labels.size() != 0) {
			inner.put(Tokens.FC.$in.toString(), labels);
			filter.put(Tokens.LABEL, inner);
		}

		Iterator<BsonDocument> it = null;
		if (branchFactor == Integer.MAX_VALUE)
			it = graph.getEdgeCollection().find(filter).projection(Tokens.PRJ_ONLY_ID).iterator();
		else
			it = graph.getEdgeCollection().find(filter).projection(Tokens.PRJ_ONLY_ID).limit(branchFactor).iterator();

		while (it.hasNext()) {
			BsonDocument d = it.next();
			edgeSet.add(new ChronoEdge(d.getString(Tokens.ID).getValue(), this.graph));
		}
		if (setParallel)
			return edgeSet.parallelStream();
		else
			return edgeSet.stream();
	}

	/**
	 * Return out-going edges (Internal Method)
	 * 
	 * @param labels
	 * @return
	 */
	private Iterable<ChronoEdge> getOutChronoEdges(final BsonArray labels, final int branchFactor) {
		HashSet<ChronoEdge> edgeSet = new HashSet<ChronoEdge>();
		BsonDocument filter = new BsonDocument();
		BsonDocument inner = new BsonDocument();
		filter.put(Tokens.OUT_VERTEX, new BsonString(this.toString()));
		if (labels != null && labels.size() != 0) {
			inner.put(Tokens.FC.$in.toString(), labels);
			filter.put(Tokens.LABEL, inner);
		}

		Iterator<BsonDocument> it = null;
		if (branchFactor == Integer.MAX_VALUE)
			it = graph.getEdgeCollection().find(filter).projection(Tokens.PRJ_ONLY_ID).iterator();
		else
			it = graph.getEdgeCollection().find(filter).projection(Tokens.PRJ_ONLY_ID).limit(branchFactor).iterator();

		while (it.hasNext()) {
			BsonDocument d = it.next();
			edgeSet.add(new ChronoEdge(d.getString(Tokens.ID).getValue(), this.graph));
		}
		return edgeSet;
	}

	private Set<ChronoEdge> getOutChronoEdgeSet(final BsonArray labels, final int branchFactor) {
		HashSet<ChronoEdge> edgeSet = new HashSet<ChronoEdge>();
		BsonDocument filter = new BsonDocument();
		BsonDocument inner = new BsonDocument();
		filter.put(Tokens.OUT_VERTEX, new BsonString(this.toString()));
		if (labels != null && labels.size() != 0) {
			inner.put(Tokens.FC.$in.toString(), labels);
			filter.put(Tokens.LABEL, inner);
		}

		Iterator<BsonDocument> it = null;
		if (branchFactor == Integer.MAX_VALUE)
			it = graph.getEdgeCollection().find(filter).projection(Tokens.PRJ_ONLY_ID).iterator();
		else
			it = graph.getEdgeCollection().find(filter).projection(Tokens.PRJ_ONLY_ID).limit(branchFactor).iterator();

		while (it.hasNext()) {
			BsonDocument d = it.next();
			edgeSet.add(new ChronoEdge(d.getString(Tokens.ID).getValue(), this.graph));
		}
		return edgeSet;
	}

	private Stream<ChronoEdge> getOutChronoEdgeStream(final BsonArray labels, final int branchFactor,
			final boolean setParallel) {

		while (true) {
			try {
				HashSet<ChronoEdge> edgeSet = new HashSet<ChronoEdge>();
				BsonDocument filter = new BsonDocument();
				filter.append(Tokens.OUT_VERTEX, new BsonString(this.toString()));

				Iterator<BsonDocument> it = graph.getEdgeCollection()
						.find(new BsonDocument(Tokens.OUT_VERTEX, new BsonString(this.toString())))
						.projection(new BsonDocument(Tokens.LABEL, new BsonBoolean(true))
								.append(Tokens.IN_VERTEX, new BsonBoolean(true))
								.append(Tokens.ID, new BsonBoolean(false)))
						.iterator();
				while (it.hasNext()) {
					BsonDocument doc = it.next();
					String inV = doc.getString(Tokens.IN_VERTEX).getValue();
					String label = doc.getString(Tokens.LABEL).getValue();
					edgeSet.add(new ChronoEdge(this.toString() + "|" + label + "|" + inV, this.toString(), inV, label,
							graph));
				}

				if (setParallel)
					return edgeSet.parallelStream();
				else
					return edgeSet.stream();
			} catch (MongoCursorNotFoundException e1) {
				System.out.println(e1.getErrorMessage());
			}
		}

		// HashSet<ChronoEdge> edgeSet = new HashSet<ChronoEdge>();
		// BsonDocument filter = new BsonDocument();
		// BsonDocument inner = new BsonDocument();
		// filter.put(Tokens.OUT_VERTEX, new BsonString(this.toString()));
		// if (labels != null && labels.size() != 0) {
		// inner.put(Tokens.FC.$in.toString(), labels);
		// filter.put(Tokens.LABEL, inner);
		// }
		//
		// Iterator<BsonDocument> it = null;
		// if (branchFactor == Integer.MAX_VALUE)
		// it =
		// graph.getEdgeCollection().find(filter).projection(Tokens.PRJ_ONLY_ID).iterator();
		// else
		// it =
		// graph.getEdgeCollection().find(filter).projection(Tokens.PRJ_ONLY_ID).limit(branchFactor).iterator();
		//
		// while (it.hasNext()) {
		// BsonDocument d = it.next();
		// edgeSet.add(new ChronoEdge(d.getString(Tokens.ID).getValue(), this.graph));
		// }
		// if (setParallel)
		// return edgeSet.parallelStream();
		// else
		// return edgeSet.stream();
	}

	private Iterable<ChronoVertex> getOutChronoVertices(BsonArray labels, final int branchFactor) {
		HashSet<ChronoVertex> vertexSet = new HashSet<ChronoVertex>();
		BsonDocument filter = new BsonDocument();
		BsonDocument inner = new BsonDocument();
		filter.put(Tokens.OUT_VERTEX, new BsonString(this.toString()));
		if (labels != null && labels.size() != 0) {
			inner.put(Tokens.FC.$in.toString(), labels);
			filter.put(Tokens.LABEL, inner);
		}

		Iterator<BsonDocument> it = null;
		if (branchFactor == Integer.MAX_VALUE)
			it = graph.getEdgeCollection().find(filter).projection(Tokens.PRJ_ONLY_ID).iterator();
		else
			it = graph.getEdgeCollection().find(filter).projection(Tokens.PRJ_ONLY_ID).limit(branchFactor).iterator();
		while (it.hasNext()) {
			BsonDocument d = it.next();
			vertexSet.add(new ChronoVertex(d.getString(Tokens.ID).getValue().split("\\|")[2], this.graph));
		}
		return vertexSet;
	}

	private Set<ChronoVertex> getOutChronoVertexSet(BsonArray labels, final int branchFactor) {
		HashSet<ChronoVertex> vertexSet = new HashSet<ChronoVertex>();
		BsonDocument filter = new BsonDocument();
		BsonDocument inner = new BsonDocument();
		filter.put(Tokens.OUT_VERTEX, new BsonString(this.toString()));
		if (labels != null && labels.size() != 0) {
			inner.put(Tokens.FC.$in.toString(), labels);
			filter.put(Tokens.LABEL, inner);
		}

		Iterator<BsonDocument> it = null;
		if (branchFactor == Integer.MAX_VALUE)
			it = graph.getEdgeCollection().find(filter).projection(Tokens.PRJ_ONLY_ID).iterator();
		else
			it = graph.getEdgeCollection().find(filter).projection(Tokens.PRJ_ONLY_ID).limit(branchFactor).iterator();
		while (it.hasNext()) {
			BsonDocument d = it.next();
			vertexSet.add(new ChronoVertex(d.getString(Tokens.ID).getValue().split("\\|")[2], this.graph));
		}
		return vertexSet;
	}

	private Stream<ChronoVertex> getOutChronoVertexStream(BsonArray labels, final int branchFactor,
			final boolean setParallel) {
		HashSet<ChronoVertex> vertexSet = new HashSet<ChronoVertex>();
		BsonDocument filter = new BsonDocument();
		BsonDocument inner = new BsonDocument();
		filter.put(Tokens.OUT_VERTEX, new BsonString(this.toString()));
		if (labels != null && labels.size() != 0) {
			inner.put(Tokens.FC.$in.toString(), labels);
			filter.put(Tokens.LABEL, inner);
		}

		Iterator<BsonDocument> it = null;
		if (branchFactor == Integer.MAX_VALUE)
			it = graph.getEdgeCollection().find(filter).projection(Tokens.PRJ_ONLY_ID).iterator();
		else
			it = graph.getEdgeCollection().find(filter).projection(Tokens.PRJ_ONLY_ID).limit(branchFactor).iterator();
		while (it.hasNext()) {
			BsonDocument d = it.next();
			vertexSet.add(new ChronoVertex(d.getString(Tokens.ID).getValue().split("\\|")[2], this.graph));
		}
		if (setParallel)
			return vertexSet.parallelStream();
		else
			return vertexSet.stream();
	}

	private Iterable<ChronoVertex> getInChronoVertices(BsonArray labels, final int branchFactor) {
		HashSet<ChronoVertex> vertexSet = new HashSet<ChronoVertex>();
		BsonDocument filter = new BsonDocument();
		BsonDocument inner = new BsonDocument();
		filter.put(Tokens.IN_VERTEX, new BsonString(this.toString()));
		if (labels != null && labels.size() != 0) {
			inner.put(Tokens.FC.$in.toString(), labels);
			filter.put(Tokens.LABEL, inner);
		}

		Iterator<BsonDocument> it = null;
		if (branchFactor == Integer.MAX_VALUE)
			it = graph.getEdgeCollection().find(filter).projection(Tokens.PRJ_ONLY_ID).iterator();
		else
			it = graph.getEdgeCollection().find(filter).projection(Tokens.PRJ_ONLY_ID).limit(branchFactor).iterator();
		while (it.hasNext()) {
			BsonDocument d = it.next();
			vertexSet.add(new ChronoVertex(d.getString(Tokens.ID).getValue().split("\\|")[0], this.graph));
		}
		return vertexSet;
	}

	private Set<ChronoVertex> getInChronoVertexSet(BsonArray labels, final int branchFactor) {
		HashSet<ChronoVertex> vertexSet = new HashSet<ChronoVertex>();
		BsonDocument filter = new BsonDocument();
		BsonDocument inner = new BsonDocument();
		filter.put(Tokens.IN_VERTEX, new BsonString(this.toString()));
		if (labels != null && labels.size() != 0) {
			inner.put(Tokens.FC.$in.toString(), labels);
			filter.put(Tokens.LABEL, inner);
		}

		Iterator<BsonDocument> it = null;
		if (branchFactor == Integer.MAX_VALUE)
			it = graph.getEdgeCollection().find(filter).projection(Tokens.PRJ_ONLY_ID).iterator();
		else
			it = graph.getEdgeCollection().find(filter).projection(Tokens.PRJ_ONLY_ID).limit(branchFactor).iterator();
		while (it.hasNext()) {
			BsonDocument d = it.next();
			vertexSet.add(new ChronoVertex(d.getString(Tokens.ID).getValue().split("\\|")[0], this.graph));
		}
		return vertexSet;
	}

	private Stream<ChronoVertex> getInChronoVertexStream(final BsonArray labels, final int branchFactor,
			final boolean setParallel) {
		HashSet<ChronoVertex> vertexSet = new HashSet<ChronoVertex>();
		BsonDocument filter = new BsonDocument();
		BsonDocument inner = new BsonDocument();
		filter.put(Tokens.IN_VERTEX, new BsonString(this.toString()));
		if (labels != null && labels.size() != 0) {
			inner.put(Tokens.FC.$in.toString(), labels);
			filter.put(Tokens.LABEL, inner);
		}

		Iterator<BsonDocument> it = null;
		if (branchFactor == Integer.MAX_VALUE)
			it = graph.getEdgeCollection().find(filter).projection(Tokens.PRJ_ONLY_ID).iterator();
		else
			it = graph.getEdgeCollection().find(filter).projection(Tokens.PRJ_ONLY_ID).limit(branchFactor).iterator();
		while (it.hasNext()) {
			BsonDocument d = it.next();
			vertexSet.add(new ChronoVertex(d.getString(Tokens.ID).getValue().split("\\|")[0], this.graph));
		}
		if (setParallel)
			return vertexSet.parallelStream();
		else
			return vertexSet.stream();
	}

	/**
	 * @deprecated use getChronoEdges(Direction, BsonArray)
	 */
	@Deprecated
	public Iterable<Edge> getEdges(final Direction direction, final String... labels) {
		if (direction.equals(Direction.OUT)) {
			return this.getOutEdges(labels);
		} else if (direction.equals(Direction.IN))
			return this.getInEdges(labels);
		else {
			return new MultiIterable<Edge>(Arrays.asList(this.getInEdges(labels), this.getOutEdges(labels)));
		}
	}

	/**
	 * @deprecated internal method of getEdges
	 */
	@Deprecated
	private Iterable<Edge> getInEdges(final String... labels) {
		HashSet<Edge> edgeSet = new HashSet<Edge>();
		BsonDocument filter = new BsonDocument();
		BsonDocument inner = new BsonDocument();
		filter.put("_inV", new BsonString(this.toString()));
		inner.put("$in", Converter.getBsonArrayOfBsonString(labels));
		filter.put("_label", inner);

		Iterator<BsonDocument> it = graph.getEdgeCollection().find(filter).iterator();
		while (it.hasNext()) {
			BsonDocument d = it.next();
			edgeSet.add(new ChronoEdge(d.getString("_id").getValue(), this.graph));
		}
		return edgeSet;
	}

	/**
	 * @deprecated internal method of getEdges
	 */
	@Deprecated
	private Iterable<Edge> getOutEdges(final String... labels) {
		HashSet<Edge> edgeSet = new HashSet<Edge>();
		BsonDocument filter = new BsonDocument();
		BsonDocument inner = new BsonDocument();
		filter.put("_outV", new BsonString(this.toString()));
		inner.put("$in", Converter.getBsonArrayOfBsonString(labels));
		filter.put("_label", inner);

		Iterator<BsonDocument> it = graph.getEdgeCollection().find(filter).iterator();
		while (it.hasNext()) {
			BsonDocument d = it.next();
			edgeSet.add(new ChronoEdge(d.getString("_id").getValue(), this.graph));
		}
		return edgeSet;
	}

	/**
	 * @deprecated use getChronoVertices(Direction, labels)
	 */
	@Deprecated
	@Override
	public Iterable<Vertex> getVertices(final Direction direction, final String... labels) {
		return new VerticesFromEdgesIterable(this, direction, labels);
	}

	/**
	 * @deprecated use addOutEdge(label, out) or addOutEdge(label, outID)
	 */
	@Deprecated
	@Override
	public Edge addEdge(final String label, final Vertex vertex) {
		return addOutEdge(label, vertex.toString());
	}
}
