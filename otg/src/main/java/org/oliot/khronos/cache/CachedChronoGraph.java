package org.oliot.khronos.cache;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Features;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.GraphQuery;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.util.DefaultGraphQuery;
import com.tinkerpop.blueprints.util.ExceptionFactory;
import com.tinkerpop.blueprints.util.PropertyFilteredIterable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bson.BsonDocument;
import org.bson.BsonValue;

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
public class CachedChronoGraph implements Graph {

	private AtomicLong vCnt = new AtomicLong(Long.MIN_VALUE);
	private BiMap<String, Long> vertexIndex = HashBiMap.create();
	private AtomicLong labelCnt = new AtomicLong(Long.MIN_VALUE);
	private BiMap<String, Long> labelIndex = HashBiMap.create();

	private HashMap<Long, CachedChronoVertex> vertices = new HashMap<Long, CachedChronoVertex>();
	private HashMap<CachedEdgeID, CachedChronoEdge> edges = new HashMap<CachedEdgeID, CachedChronoEdge>();

	public AtomicLong getvCnt() {
		return vCnt;
	}

	public void setvCnt(AtomicLong vCnt) {
		this.vCnt = vCnt;
	}

	public BiMap<String, Long> getVertexIndex() {
		return vertexIndex;
	}

	public void setVertexIndex(BiMap<String, Long> vertexIndex) {
		this.vertexIndex = vertexIndex;
	}

	public AtomicLong getLabelCnt() {
		return labelCnt;
	}

	public void setLabelCnt(AtomicLong labelCnt) {
		this.labelCnt = labelCnt;
	}

	public BiMap<String, Long> getLabelIndex() {
		return labelIndex;
	}

	public void setLabelIndex(BiMap<String, Long> labelIndex) {
		this.labelIndex = labelIndex;
	}

	public CachedChronoGraph() {
	}

	/**
	 * Return the vertex referenced by the provided object identifier. If no vertex
	 * is referenced by that identifier, then return null (Not happen in
	 * ChronoGraph).
	 *
	 * @param id
	 *            the identifier of the vertex to retrieved from the graph v(id)
	 * @return the vertex referenced by the provided identifier or null when no such
	 *         vertex exists
	 */
	public CachedChronoVertex getChronoVertex(String id) {
		try {
			if(	vertexIndex.containsKey(id))
				return this.vertices.get(vertexIndex.get(id));
			else {
				long vid = vCnt.incrementAndGet();
				vertexIndex.put(id, vid);
				vertices.put(vid, new CachedChronoVertex(vid, this));
				return this.vertices.get(vertexIndex.get(id));
			}
		} catch (NullPointerException e) {
			return null;
		}
	}

	public CachedChronoVertex getChronoVertex(Long idx) {
		try {
			return this.vertices.get(idx);
		} catch (NullPointerException e) {
			return null;
		}
	}

	/**
	 * 
	 * @param vertex
	 *            the vertex to remove from the graph
	 */
	public void removeVertex(CachedChronoVertex vertex) {
		try {
			this.vertices.remove(vertexIndex.get(vertex.getId()));
		} catch (NullPointerException e) {

		}

	}

	public void removeVertex(String vertexID) {
		try {
			this.vertices.remove(vertexIndex.get(vertexID));
		} catch (NullPointerException e) {

		}
	}

	public void removeVertex(Long vertexIdx) {
		try {
			this.vertices.remove(vertexIdx);
		} catch (NullPointerException e) {

		}
	}

	/**
	 * Return an iterable to all the vertices in the graph. If this is not possible
	 * for the implementation, then an UnsupportedOperationException can be thrown.
	 *
	 * @return an iterable reference to all vertices in the graph
	 */
	public Iterable<CachedChronoVertex> getChronoVertices() {
		return new ArrayList<CachedChronoVertex>(this.vertices.values());
	}

	public HashSet<CachedChronoVertex> getChronoVertexSet() {
		return new HashSet<CachedChronoVertex>(this.vertices.values());
	}

	public Stream<CachedChronoVertex> getChronoVertexStream() {
		return getChronoVertexStream(true);
	}

	public Stream<CachedChronoVertex> getChronoVertexStream(boolean setParallel) {
		if (setParallel == true)
			return this.vertices.values().parallelStream();
		else
			return this.vertices.values().stream();
	}

	/**
	 * Return an iterable to all the vertices in the graph that have a particular
	 * key/value property. If this is not possible for the implementation, then an
	 * UnsupportedOperationException can be thrown. The graph implementation should
	 * use indexing structures to make this efficient else a full vertex-filter scan
	 * is required.
	 * 
	 * @param key
	 *            the key of vertex
	 * @param value
	 *            the value of the vertex
	 * @return an iterable of vertices with provided key and value
	 */
	public Iterable<CachedChronoVertex> getChronoVertices(String key, Object value) {
		return new PropertyFilteredIterable<CachedChronoVertex>(key, value, this.getChronoVertices());
	}

	public Set<CachedChronoVertex> getChronoVertexSet(String key, Object value) {
		Set<CachedChronoVertex> ret = this.vertices.values().parallelStream().filter(v -> {
			Object obj = v.getProperty(key);
			if (obj == null)
				return false;
			else {
				if (obj.equals(value))
					return true;
				else
					return false;
			}
		}).collect(Collectors.toSet());
		return ret;
	}

	public Stream<CachedChronoVertex> getChronoVertexStream(String key, Object value) {
		return getChronoVertexStream(true);
	}

	public Stream<CachedChronoVertex> getChronoVertexStream(String key, Object value, boolean setParallel) {
		if (setParallel) {
			return this.vertices.values().parallelStream().filter(v -> {
				Object obj = v.getProperty(key);
				if (obj == null)
					return false;
				else {
					if (obj.equals(value))
						return true;
					else
						return false;
				}
			});
		} else {
			return this.vertices.values().stream().filter(v -> {
				Object obj = v.getProperty(key);
				if (obj == null)
					return false;
				else {
					if (obj.equals(value))
						return true;
					else
						return false;
				}
			});
		}
	}

	/**
	 * Add Vertex with unique ID. For redundant vertex, just return
	 * 
	 * @param id
	 *            : unique vertex id
	 * @return vertex
	 */
	public CachedChronoVertex addVertex(final String id) {
		Long vIdx = vertexIndex.get(id);
		if (vIdx != null) {
			CachedChronoVertex vertex = this.vertices.get(vIdx);
			if (vertex == null) {
				vertex = new CachedChronoVertex(vIdx, this);
				this.vertices.put(vIdx, vertex);
			}
			return vertex;
		} else {
			Long newIdx = vCnt.incrementAndGet();
			vertexIndex.put(id, newIdx);
			CachedChronoVertex vertex = new CachedChronoVertex(newIdx, this);
			this.vertices.put(newIdx, vertex);
			return vertex;
		}
	}

	/**
	 * Add an edge to the graph. The added edges requires a recommended identifier,
	 * a tail vertex, an head vertex, and a label. Like adding a vertex, the
	 * provided object identifier may be ignored by the implementation.
	 * 
	 * @param outVertexID:
	 *            the vertex on the tail of the edge
	 * @param inVertexID:
	 *            the vertex on the head of the edge
	 * @param label:
	 *            the label associated with the edge
	 * @return the newly created edge
	 */
	public CachedChronoEdge addEdge(final String outVertexID, final String inVertexID, final String label) {
		if (label == null)
			throw ExceptionFactory.edgeLabelCanNotBeNull();

		CachedEdgeID edgeID = new CachedEdgeID(this, outVertexID, label, inVertexID);
		CachedChronoEdge edge = this.edges.get(edgeID);
		if (edge == null) {
			edge = new CachedChronoEdge(outVertexID, inVertexID, label, this);
			this.edges.put(edgeID, edge);
			edge.getOutVertex().addOutEdge(edge);
			edge.getInVertex().addInEdge(edge);
		}
		return edge;
	}

	public CachedChronoEdge addEdge(final Long outVertexIdx, final Long inVertexIdx, final Long labelIdx) {
		if (labelIdx == null)
			throw ExceptionFactory.edgeLabelCanNotBeNull();

		CachedEdgeID edgeID = new CachedEdgeID(outVertexIdx, labelIdx, inVertexIdx);
		CachedChronoEdge edge = this.edges.get(edgeID);
		if (edge == null) {
			CachedChronoVertex out = getChronoVertex(outVertexIdx);
			CachedChronoVertex in = getChronoVertex(inVertexIdx);
			edge = new CachedChronoEdge(out, in, labelIdx, this);
			this.edges.put(edgeID, edge);
			out.addOutEdge(labelIdx, in);
			in.addInEdge(labelIdx, out);
		}
		return edge;
	}

	/**
	 * Add an edge to the graph. The added edges requires a recommended identifier,
	 * a tail vertex, an head vertex, and a label. Like adding a vertex, the
	 * provided object identifier may be ignored by the implementation.
	 * 
	 * @param id:
	 *            outVertexID|label|inVertexID
	 */
	public CachedChronoEdge getEdge(String id) {
		if (null == id)
			throw ExceptionFactory.edgeIdCanNotBeNull();
		CachedEdgeID edgeID = new CachedEdgeID(this, id);
		return this.edges.get(edgeID);
	}

	/**
	 * Remove edge
	 *
	 * @param edge
	 *            the edge to remove from the graph
	 */
	public void removeEdge(final CachedEdgeID edgeID) {
		this.edges.remove(edgeID);
	}

	/**
	 * Return an iterable to all the edges in the graph. If this is not possible for
	 * the implementation, then an UnsupportedOperationException can be thrown.
	 *
	 * @return an iterable reference to all edges in the graph
	 */
	public Iterable<CachedChronoEdge> getChronoEdges() {
		return new ArrayList<CachedChronoEdge>(this.edges.values());
	}

	public HashSet<CachedChronoEdge> getChronoEdgeSet() {
		return new HashSet<CachedChronoEdge>(this.edges.values());
	}

	public Stream<CachedChronoEdge> getChronoEdgeStream() {
		return getChronoEdgeStream(true);
	}

	/**
	 * Return an iterable to all the edges in the graph. If this is not possible for
	 * the implementation, then an UnsupportedOperationException can be thrown.
	 *
	 * @return a parallel stream of all edges in the graph
	 */
	public Stream<CachedChronoEdge> getChronoEdgeStream(boolean setParallel) {
		if (setParallel)
			return this.edges.values().parallelStream();
		else
			return this.edges.values().stream();
	}

	/**
	 * Return an iterable to all the edges in the graph that have a particular
	 * key/value property. If this is not possible for the implementation, then an
	 * UnsupportedOperationException can be thrown. The graph implementation should
	 * use indexing structures to make this efficient else a full edge-filter scan
	 * is required.
	 * 
	 * @param key
	 *            the key of the edge
	 * @param value
	 *            the value of the edge
	 * @return an iterable of edges with provided key and value
	 */
	public Iterable<CachedChronoEdge> getChronoEdges(String key, Object value) {
		return new PropertyFilteredIterable<CachedChronoEdge>(key, value, this.getChronoEdges());
	}

	public Set<CachedChronoEdge> getChronoEdgeSet(String key, Object value) {
		Set<CachedChronoEdge> ret = this.edges.values().parallelStream().filter(e -> {
			Object obj = e.getProperty(key);
			if (obj == null)
				return false;
			else {
				if (obj.equals(value))
					return true;
				else
					return false;
			}
		}).collect(Collectors.toSet());
		return ret;
	}

	public Stream<CachedChronoEdge> getChronoEdgeStream(String key, Object value) {
		return getChronoEdgeStream(key, value, true);
	}

	public Stream<CachedChronoEdge> getChronoEdgeStream(String key, Object value, boolean setParallel) {
		if (setParallel) {
			return this.edges.values().parallelStream().filter(e -> {
				Object obj = e.getProperty(key);
				if (obj == null)
					return false;
				else {
					if (obj.equals(value))
						return true;
					else
						return false;
				}
			});
		} else {
			return this.edges.values().stream().filter(e -> {
				Object obj = e.getProperty(key);
				if (obj == null)
					return false;
				else {
					if (obj.equals(value))
						return true;
					else
						return false;
				}
			});
		}
	}

	/**
	 * Return an iterable to all the edges in the graph. If this is not possible for
	 * the implementation, then an UnsupportedOperationException can be thrown.
	 * 
	 * @param labels
	 * @return iterable Edge Identifiers having the given label sets
	 */
	public Iterable<CachedChronoEdge> getEdges(final Set<String> labelSet) {
		return this.edges.values().parallelStream().filter(e -> !labelSet.contains(e.getLabel()))
				.collect(Collectors.toSet());
	}

	/**
	 * Return an iterable to all the edges in the graph. If this is not possible for
	 * the implementation, then an UnsupportedOperationException can be thrown.
	 * 
	 * @param label
	 * @return iterable Edge Identifiers having the given label sets
	 */
	public Set<CachedChronoEdge> getEdges(final String label) {
		return this.edges.values().parallelStream().filter(e -> {
			String edgeLabel = e.getLabel();
			if(label.equals(edgeLabel))
				return true;
			else
				return false;
		}).collect(Collectors.toSet());
	}


	/**
	 * Generate a query object that can be used to fine tune which edges/vertices
	 * are retrieved from the graph.
	 * 
	 * @return a graph query object with methods for constraining which data is
	 *         pulled from the underlying graph
	 */
	@Override
	public GraphQuery query() {
		return new DefaultGraphQuery(this);
	}

	/**
	 * A shutdown function is required to properly close the graph. This is
	 * important for implementations that utilize disk-based serializations.
	 * 
	 * Close MongoDB Client
	 */
	@Override
	public void shutdown() {
		// Do nothing
	}

	/**
	 * Clear Memory
	 */
	public void clear() {
		edges.clear();
		vertices.clear();
	}

	/**
	 * Extension: only support ChronoVertex add temporal property into the vertex
	 * for same timestamp & key to the existing property, replace the value
	 * 
	 * @param id:
	 *            unique vertex id
	 * @param timestamp:
	 *            unix epoch
	 * @param key:
	 *            property key for the given timestamp
	 * @param value:
	 *            property value for the given timestamp
	 * @return updated Vertex
	 */
	public CachedChronoVertex addTimestampVertexProperty(final String id, final Long timestamp, final String key,
			final BsonValue value) {
		CachedChronoVertex cv = addVertex(id);
		cv.setTimestampProperty(timestamp, key, value);
		return cv;
	}

	/**
	 * Extension: only support ChronoVertex add temporal property into the vertex
	 * for same timestamp & key to the existing property, replace the value
	 * 
	 * @param id:
	 *            unique vertex id
	 * @param timestamp:
	 *            unix epoch
	 * @param properties:
	 *            BsonDocument propertyies
	 * @return updated Vertex
	 */
	public CachedChronoVertex addTimestampVertexProperties(final String id, final Long timestamp,
			final BsonDocument properties) {
		CachedChronoVertex cv = addVertex(id);
		cv.setTimestampProperties(timestamp, properties);
		return cv;
	}

	/**
	 * Extension: only support ChronoEdge, add temporal property into the edge for
	 * same timestamp & key to the existing property, replace the value
	 * 
	 * @param outVertexID:
	 *            unique out vertex id
	 * @param inVertexID:
	 *            unique in vertex id
	 * @param label:
	 *            edge label
	 * @param timestamp:
	 *            unix epoch
	 * @param key:
	 *            property key for the given timestamp
	 * @param value:
	 *            property value for the given timestamp
	 * @return updated edge
	 */
	public CachedChronoEdge addTimestampEdgeProperty(final String outVertexID, final String inVertexID,
			final String label, final Long timestamp, final String key, final BsonValue value) {
		CachedChronoEdge e = addEdge(outVertexID, inVertexID, label);
		e.setTimestampProperty(timestamp, key, value);
		return e;
	}

	public CachedChronoEdge addTimestampEdgeProperties(final String outVertexID, final String inVertexID,
			final String label, final Long timestamp, final BsonDocument properties) {
		CachedChronoEdge e = addEdge(outVertexID, inVertexID, label);
		e.setTimestampProperties(timestamp, properties);
		return e;
	}

	/**
	 * Remove all the interval events
	 */
	public void removeIntervalEvents() {

	}

	/**
	 * Remove all the timestamp events
	 */
	public void removeTimestampEvents() {

	}

	/**
	 * Remove all the timestamp/interval events
	 */
	public void removeTemporalEvents() {

	}

	/**
	 * Get the particular features of the graph implementation. Not all graph
	 * implementations are identical nor perfectly implement the Blueprints API. The
	 * Features object returned contains meta-data about numerous potential
	 * divergences between implementations.
	 *
	 * @return the features of this particular Graph implementation
	 */
	@Override
	public Features getFeatures() {
		return null;
	}

	/**
	 * @Deprecated: use getChronoVertex(String id)
	 */
	@Deprecated
	@Override
	public Vertex addVertex(Object id) {
		return getChronoVertex(id.toString());
	}

	/**
	 * @deprecated: use getChronoVertex(String id)
	 */
	@Deprecated
	@Override
	public Vertex getVertex(Object id) {
		return getChronoVertex(id.toString());
	}

	/**
	 * @deprecated use getChronoVertices()
	 */
	@Deprecated
	@Override
	public Iterable<Vertex> getVertices() {
		return null;
	}

	/**
	 * @deprecated use getChronoVertices
	 */
	@Override
	public Iterable<Vertex> getVertices(String key, Object value) {
		return null;
	}

	/**
	 * @deprecated use addEdge(outVertexID, inVertexID, label)
	 */
	@Deprecated
	@Override
	public Edge addEdge(Object id, Vertex outVertex, Vertex inVertex, String label) {
		return null;
	}

	/**
	 * @deprecated use getEdge(String id)
	 */
	@Deprecated
	@Override
	public Edge getEdge(Object id) {
		return null;
	}

	/**
	 * @deprecated use removeEdge(ChronoEdge)
	 */
	@Deprecated
	@Override
	public void removeEdge(final Edge edge) {
		return;
	}

	/**
	 * @deprecated use getChronoEdges()
	 */
	@Deprecated
	@Override
	public Iterable<Edge> getEdges() {
		return null;
	}

	/**
	 * @deprecated use removeVertex(ChronoVertex)
	 */
	@Deprecated
	@Override
	public void removeVertex(Vertex vertex) {
		return;
	}

	/**
	 * @deprecated use getChronoEdges(key,value)
	 */
	@Deprecated
	@Override
	public Iterable<Edge> getEdges(String key, Object value) {
		return null;
	}

}
