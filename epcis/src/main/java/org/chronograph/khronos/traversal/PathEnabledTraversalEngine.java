package org.chronograph.khronos.traversal;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import org.bson.BsonArray;
import org.bson.BsonString;
import org.bson.BsonValue;
import org.chronograph.khronos.common.Direction;
import org.chronograph.khronos.common.FilterFunction;
import org.chronograph.khronos.common.HistoryEnabledFunction;
import org.chronograph.khronos.common.LoopFunction;
import org.chronograph.khronos.common.SideEffectFunction;
import org.chronograph.khronos.common.Step;
import org.chronograph.khronos.common.Tokens.AC;
import org.chronograph.khronos.common.Tokens.FC;
import org.chronograph.khronos.element.Edge;
import org.chronograph.khronos.element.EdgeEvent;
import org.chronograph.khronos.element.Element;
import org.chronograph.khronos.element.Graph;
import org.chronograph.khronos.element.Vertex;
import org.chronograph.khronos.element.VertexEvent;

/**
 * Copyright (C) 2016-2018 Jaewook Byun
 * 
 * ChronoGraph: A Temporal Graph Management and Traversal Platform
 * 
 * The loop management scheme unlike Gremlin makes this class
 * 
 * @author Jaewook Byun, Assistant Professor, Halla University
 * 
 *         Data Frameworks and Platforms Laboratory (DFPL)
 * 
 *         jaewook.byun@halla.ac.kr, bjw0829@kaist.ac.kr, bjw0829@gmail.com
 * 
 */
@SuppressWarnings({ "unchecked", "rawtypes", "serial", "unused" })
public class PathEnabledTraversalEngine {

	// Stream for graph traversal
	private Stream stream;

	// Manage traversal steps for Loop using Java reflection
	private ArrayList<Step> stepList;
	private HashMap<String, Integer> stepIndex;

	// Loop count starts with 0
	private int loopCount;

	// Class of stream element
	private Class elementClass;
	private Class listElementClass;

	// Path Map: Last Object -> Set of Path List
	// HashMap<Object, HashSet<List>>
	// private Map<Object, Object> previousPath;
	private Map<Object, Object> currentPath;

	private Graph g;

	/**
	 * 
	 * Initialize CachedChronoGraphPipeline
	 * 
	 * @param starts        is basically instance of Stream but
	 *                      CachedChronoGraph/Vertex/Edge is permitted ( would be
	 *                      Stream<> )
	 * @param setParallel   ignored if starts is already instanceof Stream
	 * @param isPathEnabled would spend resources to manage Path
	 */

	public PathEnabledTraversalEngine(Graph g, Object starts, Class elementClass) {

		// previousPath = new HashMap<Object, Object>();
		currentPath = new HashMap<Object, Object>();

		// Initialize Stream and Path
		if (starts instanceof Stream) {
			this.elementClass = elementClass;

			stream = ((Set) ((Stream) starts).map(element -> {
				HashSet initPathSet = new HashSet() {
					{
						add(Arrays.asList(element));
					}
				};
				currentPath.put(element, initPathSet);
				return element;
			}).collect(Collectors.toSet())).parallelStream();

		} else if (starts instanceof Element) {

			stream = Stream.of(starts).parallel();
			this.elementClass = starts.getClass();

			HashSet initPathSet = new HashSet() {
				{
					add(Arrays.asList(starts));
				}
			};
			currentPath.put(starts, initPathSet);

		}
		stepList = new ArrayList<Step>();
		stepIndex = new HashMap<String, Integer>();
		this.loopCount = 0;
		listElementClass = null;
		this.g = g;
	}

	private PathEnabledTraversalEngine(Graph g, Object starts, int loopCount, Class elementClass,
			Class listElementClass, Map currentPath) {

		// previousPath = new HashMap<Object, Object>();
		// if (currentPath != null)
		// this.currentPath = new HashMap(currentPath);
		// else
		this.currentPath = currentPath;

		// Initialize Stream and Path
		if (starts instanceof Stream) {
			this.elementClass = elementClass;
			// if (setPathEnabled) {
			// if (setParallel) {
			// stream = ((Set) ((Stream) starts).map(element -> {
			// HashSet initPathSet = new HashSet();
			// List list = new ArrayList();
			// list.add(element);
			// initPathSet.add(list);
			// this.currentPath.put(element, initPathSet);
			// return element;
			// }).collect(Collectors.toSet())).parallelStream();
			// } else {
			// stream = ((Set) ((Stream) starts).map(element -> {
			// HashSet initPathSet = new HashSet();
			// List list = new ArrayList();
			// list.add(element);
			// initPathSet.add(list);
			// this.currentPath.put(element, initPathSet);
			// return element;
			// }).collect(Collectors.toSet())).stream();
			// }
			// } else {
			stream = (Stream) starts;
			// }
		} else if (starts instanceof Collection) {
			this.elementClass = listElementClass;
			// if (setPathEnabled) {
			// if (setParallel) {
			// stream = ((Set) ((Collection) starts).parallelStream().map(element -> {
			// HashSet initPathSet = new HashSet();
			// List list = new ArrayList();
			// list.add(element);
			// initPathSet.add(list);
			// this.currentPath.put(element, initPathSet);
			// return element;
			// }).collect(Collectors.toSet())).parallelStream();
			// } else {
			// stream = ((Set) ((Collection) starts).parallelStream().map(element -> {
			// HashSet initPathSet = new HashSet();
			// List list = new ArrayList();
			// list.add(element);
			// initPathSet.add(list);
			// this.currentPath.put(element, initPathSet);
			// return element;
			// }).collect(Collectors.toSet())).stream();
			// }
			// } else {
			stream = ((Collection) starts).parallelStream();
			// }

		} else if (starts instanceof Element) {

			HashSet set = new HashSet();
			set.add(starts);
			stream = set.parallelStream();
			this.elementClass = starts.getClass();

			// if (setPathEnabled) {
			// HashSet initPathSet = new HashSet();
			// List list = new ArrayList();
			// list.add(starts);
			// initPathSet.add(list);
			// this.currentPath.put(starts, initPathSet);
			// }
		}
		stepList = new ArrayList<Step>();
		stepIndex = new HashMap<String, Integer>();
		this.loopCount = loopCount;
		this.listElementClass = null;
		this.g = g;
	}

	///////////////////////
	/// TRANSFORM PIPES ///
	///////////////////////

	/**
	 * Add a GraphQueryPipe to the end of the Pipeline. If optimizations are
	 * enabled, then the the next steps can fold into a GraphQueryPipe compilation.
	 *
	 * Path Enabled -> Greedy,
	 * 
	 * Path Disabled -> Lazy
	 *
	 * Pipeline: Stream<CachedChronoGraph> -> Stream<CachedChronoVertex>
	 * 
	 * Path: Map<CachedChronoGraph, Set<CachedChronoVertex>>
	 *
	 * @return the extended Pipeline
	 */

	public PathEnabledTraversalEngine V() {
		// Check Input element class
		checkInputElementClass(Graph.class);

		// Pipeline Update
		// Get Sub-Path
		Map intermediate = (Map) stream.map(g -> {
			Graph cg = (Graph) g;
			return new AbstractMap.SimpleImmutableEntry(cg, cg.getVertexSet());
		}).collect(Collectors.toMap(e -> ((Entry) e).getKey(), e -> ((Entry) e).getValue()));

		// Update Path
		updateTransformationPath(intermediate);

		// Make stream again
		stream = getStream(intermediate);

		// Step Update
		Class[] args = {};
		Step step = new Step(this.getClass().getName(), "V", args);
		stepList.add(step);

		// Set Class
		elementClass = Vertex.class;
		return this;
	}

	/**
	 * Add a GraphQueryPipe to the end of the Pipeline. If optimizations are
	 * enabled, then the the next steps can fold into a GraphQueryPipe compilation.
	 *
	 * Path Enabled -> Greedy,
	 * 
	 * Path Disabled -> Lazy
	 * 
	 * Pipeline: Stream<CachedChronoGraph> -> Stream<CachedChronoEdge>
	 *
	 * Path: Map<CachedChronoGraph, Set<CachedChronoEdge>>
	 *
	 * @return the extended Pipeline
	 */
	public PathEnabledTraversalEngine E() {
		// Check Input element class
		checkInputElementClass(Graph.class);

		// Pipeline Update
		// Get Sub-Path
		Map intermediate = (Map) stream.map(g -> {
			Graph cg = (Graph) g;
			return new AbstractMap.SimpleImmutableEntry(cg, cg.getEdgeSet());
		}).collect(Collectors.toMap(e -> ((Entry) e).getKey(), e -> ((Entry) e).getValue()));

		// Update Path
		updateTransformationPath(intermediate);

		// Make stream again
		stream = getStream(intermediate);

		// Step Update
		Class[] args = {};
		Step step = new Step(this.getClass().getName(), "E", args);
		stepList.add(step);

		// Set Class
		elementClass = Edge.class;
		return this;
	}

	public PathEnabledTraversalEngine e() {
		// Check Input element class
		checkInputElementClass(Graph.class);

		// Pipeline Update
		// Get Sub-Path
		Map intermediate = (Map) stream.map(g -> {
			Graph cg = (Graph) g;
			return new AbstractMap.SimpleImmutableEntry(cg, cg.getEdgeEventSet());
		}).collect(Collectors.toMap(e -> ((Entry) e).getKey(), e -> ((Entry) e).getValue()));

		// Update Path
		updateTransformationPath(intermediate);

		// Make stream again
		stream = getStream(intermediate);

		// Step Update
		Class[] args = {};
		Step step = new Step(this.getClass().getName(), "e", args);
		stepList.add(step);

		// Set Class
		elementClass = EdgeEvent.class;
		return this;
	}

	/**
	 * Add a GraphQueryPipe to the end of the Pipeline. If optimizations are
	 * enabled, then the the next steps can fold into a GraphQueryPipe compilation.
	 *
	 * Path Enabled -> Greedy,
	 * 
	 * Path Disabled -> Lazy
	 *
	 * Pipeline: Stream<CachedChronoGraph> -> Stream<CachedChronoVertex>
	 *
	 * Path: Map<CachedChronoGraph, Set<CachedChronoVertex>>
	 *
	 * @param key   they key that all the emitted vertices should be checked on
	 * @param value the value that all the emitted vertices should have for the key
	 * @return the extended Pipeline
	 */
	public PathEnabledTraversalEngine V(String key, BsonValue value) {
		// Check Input element class
		checkInputElementClass(Graph.class);

		// Pipeline Update
		// Get Sub-Path
		Map intermediate = (Map) stream.map(g -> {
			Graph cg = (Graph) g;
			return new AbstractMap.SimpleImmutableEntry(cg, cg.getVertexSet(key, value));
		}).collect(Collectors.toMap(e -> ((Entry) e).getKey(), e -> ((Entry) e).getValue()));

		// Update Path
		updateTransformationPath(intermediate);

		// Make stream again
		stream = getStream(intermediate);

		// Step Update
		Class[] args = new Class[2];
		args[0] = String.class;
		args[1] = BsonValue.class;
		Step step = new Step(this.getClass().getName(), "V", args, key, value);
		stepList.add(step);

		// Set Class
		elementClass = Vertex.class;
		return this;
	}

	/**
	 * Add a GraphQueryPipe to the end of the Pipeline. If optimizations are
	 * enabled, then the the next steps can fold into a GraphQueryPipe compilation.
	 *
	 * Path Enabled -> Greedy,
	 * 
	 * Path Disabled -> Lazy
	 * 
	 * Pipeline: Stream<CachedChronoGraph> -> Stream<CachedChronoEdge>
	 *
	 * Path: Map<CachedChronoGraph, Set<CachedChronoEdge>>
	 *
	 * @param key   they key that all the emitted edges should be checked on
	 * @param value the value that all the emitted edges should have for the key
	 * @return the extended Pipeline
	 */
	public PathEnabledTraversalEngine E(String key, BsonValue value) {
		// Check Input element class
		checkInputElementClass(Graph.class);

		// Pipeline Update
		// Get Sub-Path
		Map intermediate = (Map) stream.map(g -> {
			Graph cg = (Graph) g;
			return new AbstractMap.SimpleImmutableEntry(cg, cg.getEdgeSet(key, value));
		}).collect(Collectors.toMap(e -> ((Entry) e).getKey(), e -> ((Entry) e).getValue()));

		// Update Path
		updateTransformationPath(intermediate);

		// Make stream again
		stream = getStream(intermediate);

		// Step Update
		Class[] args = new Class[2];
		args[0] = String.class;
		args[1] = BsonValue.class;
		Step step = new Step(this.getClass().getName(), "E", args, key, value);
		stepList.add(step);

		// Set Class
		elementClass = Edge.class;
		return this;
	}

	/////////////// outE, inE////////////////////////////

	/**
	 * Add an OutEdgesPipe to the end of the Pipeline. Emit the outgoing edges for
	 * the incoming vertex.
	 *
	 * Path Enabled -> Greedy,
	 * 
	 * Path Disabled -> Lazy
	 *
	 * Pipeline: Stream<CachedChronoVertex> -> Stream<CachedChronoEdge>
	 *
	 * Path: Map<CachedChronoVertex, Set<CachedChronoEdge>>
	 *
	 * @param branchFactor the number of max incident edges for each incoming vertex
	 * @param labels       the edge labels to traverse
	 * @return the extended Pipeline
	 */
	public PathEnabledTraversalEngine outE(BsonString label) {
		// Check Input element class
		checkInputElementClass(Vertex.class);

		// Pipeline Update
		// Get Sub-Path
		Map intermediate = (Map) stream.map(v -> {
			Vertex cv = (Vertex) v;
			return new AbstractMap.SimpleImmutableEntry(cv, cv.getEdgeSet(Direction.OUT, label));
		}).collect(Collectors.toMap(e -> ((Entry) e).getKey(), e -> ((Entry) e).getValue()));

		// Update Path
		updateTransformationPath(intermediate);

		// Make stream again
		stream = getStream(intermediate);

		// Step Update
		Class[] args = new Class[1];
		args[0] = BsonString.class;
		Step step = new Step(this.getClass().getName(), "outE", args, label);
		stepList.add(step);

		// Set Class
		elementClass = Edge.class;
		return this;
	}

	/**
	 * Add a InPipe to the end of the Pipeline. Emit the adjacent incoming vertices
	 * for the incoming vertex.
	 *
	 * Path Enabled -> Greedy,
	 * 
	 * Path Disabled -> Lazy
	 *
	 * Pipeline: Stream<CachedChronoVertex> -> Stream<CachedChronoEdge>
	 *
	 * Path: Map<CachedChronoVertex, Set<CachedChronoEdge>>
	 *
	 * @param branchFactor the number of max incident edges for each incoming vertex
	 * @param labels       the edge labels to traverse
	 * @return the extended Pipeline
	 */
	public PathEnabledTraversalEngine inE(BsonString label) {
		// Check Input element class
		checkInputElementClass(Vertex.class);

		// Pipeline Update
		// Get Sub-Path
		Map intermediate = (Map) stream.map(v -> {
			Vertex cv = (Vertex) v;
			return new AbstractMap.SimpleImmutableEntry(cv, cv.getEdgeSet(Direction.IN, label));
		}).collect(Collectors.toMap(e -> ((Entry) e).getKey(), e -> ((Entry) e).getValue()));

		// Update Path
		updateTransformationPath(intermediate);

		// Make stream again
		stream = getStream(intermediate);

		// Step Update
		Class[] args = new Class[1];
		args[0] = BsonString.class;
		Step step = new Step(this.getClass().getName(), "inE", args, label);
		stepList.add(step);

		// Set Class
		elementClass = Edge.class;
		return this;
	}

	/////////////// outV, inV ////////////////////////////

	/**
	 * Add an OutVertexPipe to the end of the Pipeline. Emit the tail vertex of the
	 * incoming edge.
	 *
	 * Path Enabled -> Greedy,
	 * 
	 * Path Disabled -> Lazy
	 *
	 * Pipeline: Stream<CachedChronoEdge> -> Stream<CachedChronoVertex>
	 *
	 * Path: Map<CachedChronoEdge, CachedChronoVertex>
	 *
	 * @return the extended Pipeline
	 */
	public PathEnabledTraversalEngine outV() {
		// Check Input element class
		checkInputElementClass(Edge.class);

		// Pipeline Update
		// Get Sub-Path
		Map intermediate = (Map) stream.map(e -> {
			Edge ce = (Edge) e;
			return new AbstractMap.SimpleImmutableEntry(ce, ce.getVertex(Direction.OUT));
		}).collect(Collectors.toMap(e -> ((Entry) e).getKey(), e -> ((Entry) e).getValue()));

		// Update Path
		updateTransformationPath(intermediate);

		// Make stream again
		stream = getStream(intermediate);

		// Step Update
		Class[] args = {};
		Step step = new Step(this.getClass().getName(), "outV", args);
		stepList.add(step);

		// Set Class
		elementClass = Vertex.class;
		return this;
	}

	/**
	 * Add an InVertexPipe to the end of the Pipeline. Emit the head vertex of the
	 * incoming edge.
	 * 
	 * Path Enabled -> Greedy,
	 * 
	 * Path Disabled -> Lazy
	 * 
	 * Pipeline: Stream<CachedChronoEdge> -> Stream<CachedChronoVertex>
	 * 
	 * Path: Map<CachedChronoEdge, CachedChronoVertex>
	 * 
	 * @return the extended Pipeline
	 */
	public PathEnabledTraversalEngine inV() {
		// Check Input element class
		checkInputElementClass(Edge.class);

		// Pipeline Update
		// Get Sub-Path
		Map intermediate = (Map) stream.map(e -> {
			Edge ce = (Edge) e;
			return new AbstractMap.SimpleImmutableEntry(ce, ce.getVertex(Direction.IN));
		}).collect(Collectors.toMap(e -> ((Entry) e).getKey(), e -> ((Entry) e).getValue()));

		// Update Path
		updateTransformationPath(intermediate);

		// Make stream again
		stream = getStream(intermediate);

		// Step Update
		Class[] args = {};
		Step step = new Step(this.getClass().getName(), "inV", args);
		stepList.add(step);

		// Set Class
		elementClass = Vertex.class;
		return this;
	}

	/////////////// out, in ////////////////////////////

	/**
	 * Add an OutPipe to the end of the Pipeline. Emit the adjacent outgoing
	 * vertices of the incoming vertex.
	 *
	 * Path Enabled -> Greedy,
	 * 
	 * Path Disabled -> Lazy
	 *
	 * Pipeline: Stream<CachedChronoVertex> -> Stream<CachedChronoVertex>
	 *
	 * Path: Map<CachedChronoVertex, Set<CachedChronoVertex>>
	 *
	 * @param branchFactor the number of max adjacent vertices for each incoming
	 *                     vertex
	 * @param labels       the edge labels to traverse
	 * @return the extended Pipeline
	 */
	public PathEnabledTraversalEngine out(BsonString label) {
		// Check Input element class
		checkInputElementClass(Vertex.class);

		// Pipeline Update
		// Get Sub-Path
		Map intermediate = (Map) stream.map(v -> {
			Vertex cv = (Vertex) v;
			return new AbstractMap.SimpleImmutableEntry(cv, cv.getVertexSet(Direction.OUT, label));
		}).collect(Collectors.toMap(e -> ((Entry) e).getKey(), e -> ((Entry) e).getValue()));

		// Update Path
		updateTransformationPath(intermediate);

		// Make stream again
		stream = getStream(intermediate);

		// Step Update
		Class[] args = new Class[1];
		args[0] = BsonString.class;
		Step step = new Step(this.getClass().getName(), "out", args, label);
		stepList.add(step);

		// Set Class
		elementClass = Vertex.class;
		return this;
	}

	/**
	 * Add a InPipe to the end of the Pipeline. Emit the adjacent incoming vertices
	 * for the incoming vertex.
	 *
	 * Path Enabled -> Greedy,
	 * 
	 * Path Disabled -> Lazy
	 *
	 * Pipeline: Stream<CachedChronoVertex> -> Stream<CachedChronoVertex>
	 *
	 * Path: Map<CachedChronoVertex, Set<CachedChronoVertex>>
	 *
	 * @param branchFactor the number of max adjacent vertices for each incoming
	 *                     vertex
	 * @param labels       the edge labels to traverse
	 * @return the extended Pipeline
	 */
	public PathEnabledTraversalEngine in(BsonString label) {
		// Check Input element class
		checkInputElementClass(Vertex.class);

		// Pipeline Update
		// Get Sub-Path
		Map intermediate = (Map) stream.map(v -> {
			Vertex cv = (Vertex) v;
			return new AbstractMap.SimpleImmutableEntry(cv, cv.getVertexSet(Direction.IN, label));
		}).collect(Collectors.toMap(e -> ((Entry) e).getKey(), e -> ((Entry) e).getValue()));

		// Update Path
		updateTransformationPath(intermediate);

		// Make stream again
		stream = getStream(intermediate);

		// Step Update
		Class[] args = new Class[1];
		args[0] = BsonString.class;
		Step step = new Step(this.getClass().getName(), "in", args, label);
		stepList.add(step);

		// Set Class
		elementClass = Vertex.class;
		return this;
	}

	////////////////////////////////////////
	///// Temporal Traversal Language /////
	//////////////////////////////////////

	// Vertex Events to Edge Events //

	public PathEnabledTraversalEngine outEe(BsonString label, AC tt, FC minMax) {
		// Check Input element class
		checkInputElementClass(VertexEvent.class);

		// Pipeline Update
		// Get Sub-Path
		Map intermediate = (Map) stream.map(ve -> {
			VertexEvent cve = (VertexEvent) ve;
			return new AbstractMap.SimpleImmutableEntry(cve, cve.getEdgeEvents(Direction.OUT, label, tt, minMax));
		}).collect(Collectors.toMap(entry -> ((Entry) entry).getKey(), entry -> ((Entry) entry).getValue()));

		// Update Path
		updateTransformationPath(intermediate);

		// Make stream again
		stream = getStream(intermediate);

		// Step Update
		Class[] args = new Class[3];
		args[0] = BsonString.class;
		args[1] = AC.class;
		args[2] = FC.class;
		Step step = new Step(this.getClass().getName(), "outEe", args, label, tt, minMax);
		stepList.add(step);

		// Set Class
		elementClass = EdgeEvent.class;
		return this;
	}

	public PathEnabledTraversalEngine inEe(BsonString label, AC tt, FC minMax) {
		// Check Input element class
		checkInputElementClass(VertexEvent.class);

		// Pipeline Update
		// Get Sub-Path
		Map intermediate = (Map) stream.map(ve -> {
			VertexEvent cve = (VertexEvent) ve;
			return new AbstractMap.SimpleImmutableEntry(cve, cve.getEdgeEvents(Direction.IN, label, tt, minMax));
		}).collect(Collectors.toMap(entry -> ((Entry) entry).getKey(), entry -> ((Entry) entry).getValue()));

		// Update Path
		updateTransformationPath(intermediate);

		// Make stream again
		stream = getStream(intermediate);

		// Step Update
		Class[] args = new Class[3];
		args[0] = BsonString.class;
		args[1] = AC.class;
		args[2] = FC.class;
		Step step = new Step(this.getClass().getName(), "inEe", args, label, tt, minMax);
		stepList.add(step);

		// Set Class
		elementClass = EdgeEvent.class;
		return this;
	}

	// Vertex Events to Vertex Events //

	public PathEnabledTraversalEngine oute(BsonString label, AC tt, FC minMax) {
		// Check Input element class
		checkInputElementClass(VertexEvent.class);

		// Pipeline Update
		// Get Sub-Path
		Map intermediate = (Map) stream.map(ve -> {
			VertexEvent cve = (VertexEvent) ve;
			return new AbstractMap.SimpleImmutableEntry(cve, cve.getVertexEvents(Direction.OUT, label, tt, minMax));
		}).collect(Collectors.toMap(entry -> ((Entry) entry).getKey(), entry -> ((Entry) entry).getValue()));
		// Update Path
		updateTransformationPath(intermediate);

		// Make stream again
		stream = getStream(intermediate);

		// Step Update
		Class[] args = new Class[3];
		args[0] = BsonString.class;
		args[1] = AC.class;
		args[2] = FC.class;
		Step step = new Step(this.getClass().getName(), "oute", args, label, tt, minMax);
		stepList.add(step);

		// Set Class
		elementClass = VertexEvent.class;
		return this;
	}

	public PathEnabledTraversalEngine ine(BsonString label, AC tt, FC minMax) {
		// Check Input element class
		checkInputElementClass(VertexEvent.class);

		// Pipeline Update
		// Get Sub-Path
		Map intermediate = (Map) stream.map(ve -> {
			VertexEvent cve = (VertexEvent) ve;
			return new AbstractMap.SimpleImmutableEntry(cve, cve.getVertexEvents(Direction.IN, label, tt, minMax));
		}).collect(Collectors.toMap(entry -> ((Entry) entry).getKey(), entry -> ((Entry) entry).getValue()));

		// Update Path
		updateTransformationPath(intermediate);

		// Make stream again
		stream = getStream(intermediate);

		// Step Update
		Class[] args = new Class[3];
		args[0] = BsonString.class;
		args[1] = AC.class;
		args[2] = FC.class;
		Step step = new Step(this.getClass().getName(), "ine", args, label, tt);
		stepList.add(step);

		// Set Class
		elementClass = VertexEvent.class;
		return this;
	}

	// Edge Events to Vertex Events //

	public PathEnabledTraversalEngine inVe() {
		// Check Input element class
		checkInputElementClass(EdgeEvent.class);

		// Pipeline Update
		// Get Sub-Path
		Map intermediate = (Map) stream.map(ee -> {
			EdgeEvent cee = (EdgeEvent) ee;
			return new AbstractMap.SimpleImmutableEntry(cee, cee.getVertexEvent(Direction.IN));
		}).collect(Collectors.toMap(e -> ((Entry) e).getKey(), e -> ((Entry) e).getValue()));

		// Update Path
		updateTransformationPath(intermediate);

		// Make stream again
		stream = getStream(intermediate);

		// Step Update
		Class[] args = {};
		Step step = new Step(this.getClass().getName(), "inVe", args);
		stepList.add(step);

		// Set Class
		elementClass = VertexEvent.class;
		return this;
	}

	public PathEnabledTraversalEngine outVe() {
		// Check Input element class
		checkInputElementClass(EdgeEvent.class);

		// Pipeline Update
		// Get Sub-Path
		Map intermediate = (Map) stream.map(ee -> {
			EdgeEvent cee = (EdgeEvent) ee;
			return new AbstractMap.SimpleImmutableEntry(cee, cee.getVertexEvent(Direction.OUT));
		}).collect(Collectors.toMap(e -> ((Entry) e).getKey(), e -> ((Entry) e).getValue()));

		// Update Path
		updateTransformationPath(intermediate);

		// Make stream again
		stream = getStream(intermediate);

		// Step Update
		Class[] args = {};
		Step step = new Step(this.getClass().getName(), "outVe", args);
		stepList.add(step);

		// Set Class
		elementClass = VertexEvent.class;
		return this;
	}

	/////////////// shuffle ////////////////////////////

	/**
	 * Add a ShufflePipe to the end of the Pipeline. All the objects previous to
	 * this step are aggregated in a greedy fashion, their order randomized and
	 * emitted as a List.
	 *
	 * Greedy
	 * 
	 * No Effect on Path
	 *
	 * Stream -> Shuffled Stream
	 *
	 * Utilize Collections.shuffle()
	 *
	 * @return the extended Pipeline
	 */
	public PathEnabledTraversalEngine shuffle() {
		// Check Invalid Input element class
		// checkInvalidInputElementClass(List.class);

		// Pipeline Update
		List list = (List) stream.collect(Collectors.toList());
		Collections.shuffle(list);
		stream = list.parallelStream();

		// Step Update
		Class[] args = {};
		Step step = new Step(this.getClass().getName(), "shuffle", args);
		stepList.add(step);
		return this;
	}

	/////////////// scatter gather ////////////////////////////

	/**
	 * Add a GatherPipe to the end of the Pipeline. All the objects previous to this
	 * step are aggregated in a greedy fashion and emitted as a List.
	 *
	 * Do not use gather twice before scattered.
	 *
	 * Greedy
	 * 
	 * Pipeline: Stream -> Stream<Stream>
	 *
	 * @return the extended Pipeline
	 */
	public PathEnabledTraversalEngine gather() {
		// Check Invalid Input element class
		checkInvalidInputElementClass(List.class);

		// Pipeline Update

		List intermediate = (List) stream.collect(Collectors.toList());

		ArrayList list = new ArrayList();
		list.add(intermediate);

		stream = list.parallelStream();

		// Step Update
		Class[] args = {};
		Step step = new Step(this.getClass().getName(), "gather", args);
		stepList.add(step);

		// Set Class
		listElementClass = elementClass;
		elementClass = List.class;
		return this;
	}

	/**
	 * Add a ScatterPipe to the end of the Pipeline. Any input iterator or iterable
	 * is unrolled and the iterator/iterable's objects are emitted one at a time.
	 *
	 * Path Enabled: Greedy
	 * 
	 * Path Disabled: Lazy
	 * 
	 * Pipeline: Stream<Collection> -> Stream<Object>, Stream<Stream> ->
	 * Stream<Object>, Stream<Object> -> Stream<Object>
	 *
	 * @return the extended Pipeline
	 */
	public PathEnabledTraversalEngine scatter() {
		// Check Input element class
		checkInputElementClass(List.class);

		// Pipeline Update
		// Get Sub-Path
		List intermediate = (List) stream.flatMap(e -> {
			return ((List) e).parallelStream();
		}).collect(Collectors.toList());

		// Update Path ( Filter if any last elements of each path are not
		// included in intermediate )
		currentPath.keySet().retainAll(intermediate);

		// Make stream again
		stream = intermediate.parallelStream();

		// Step Update
		Class[] args = {};
		Step step = new Step(this.getClass().getName(), "scatter", args);
		stepList.add(step);

		// Set Class
		elementClass = listElementClass;
		listElementClass = null;
		return this;
	}

	/////////////// transform ////////////////////////////

	/**
	 * Add a TransformFunctionPipe to the end of the Pipeline. Given an input, the
	 * provided function is computed on the input and the output of that function is
	 * emitted.
	 *
	 * Path Enabled: Greedy
	 * 
	 * Path Disabled: Lazy
	 * 
	 * Pipeline: Stream -> Transformed Stream
	 * 
	 * Path: Map<Object, TransformedObject>
	 *
	 * @param function the transformation function of the pipe
	 * @return the extended Pipeline
	 */
	public PathEnabledTraversalEngine transform(Function function, Class outputClass) {
		// Pipeline Update
		// Get Sub-Path
		Map intermediate = (Map) stream.map(e -> {
			Object val = function.apply(e);
			if (val == null)
				return null;
			return new AbstractMap.SimpleImmutableEntry(e, val);
		}).filter(e -> e != null).collect(Collectors.toMap(e -> ((Entry) e).getKey(), e -> ((Entry) e).getValue()));

		if (!Collection.class.isAssignableFrom(outputClass))
			updateTransformationPath(intermediate);

		// Make stream again
		stream = getStream(intermediate);

		// Step Update
		Class[] args = new Class[2];
		args[0] = Function.class;
		args[1] = Class.class;
		Step step = new Step(this.getClass().getName(), "transform", args, function, outputClass);
		stepList.add(step);
		// Set Class
		elementClass = outputClass;
		return this;
	}

	public PathEnabledTraversalEngine pathEnabledTransform(HistoryEnabledFunction function, Class outputClass) {
		// Stream Update
		// Get Sub-Path
		Map intermediate = (Map) stream.map(e -> {
			return new AbstractMap.SimpleImmutableEntry(e, function.apply(e, currentPath));
		}).collect(Collectors.toMap(e -> ((Entry) e).getKey(), e -> ((Entry) e).getValue()));

		// Update Path
		if (elementClass != List.class)
			updateTransformationPath(intermediate);

		// Make stream again
		stream = getStream(intermediate);

		// Step Update
		Class[] args = new Class[2];
		args[0] = HistoryEnabledFunction.class;
		args[1] = Class.class;
		Step step = new Step(this.getClass().getName(), "pathEnabledTransform", args, function, outputClass);
		stepList.add(step);

		// Set Class
		elementClass = outputClass;
		return this;
	}

	/////////////// order ////////////////////////////

	/**
	 * Add an OrderPipe to the end of the Pipeline. This step will sort the objects
	 * in the stream in a default Comparable order.
	 *
	 * if parallel, stream is first aggregated, ordered and becomes sequential
	 * ordered stream
	 * 
	 * No Effect on Path
	 *
	 * @return the extended Pipeline
	 */

	public PathEnabledTraversalEngine order(Comparator comparator) {
		// Pipeline Update
		List list = (List) stream.collect(Collectors.toList());
		stream = list.stream().sorted(comparator);

		// Step Update
		Class[] args = new Class[1];
		args[0] = Comparator.class;
		Step step = new Step(this.getClass().getName(), "order", args, comparator);
		stepList.add(step);
		return this;
	}

	////////////////////
	/// FILTER PIPES ///
	////////////////////

	/**
	 * Add a DuplicateFilterPipe to the end of the Pipeline. Will only emit the
	 * distinct objects
	 *
	 * Path Enabled: Greedy
	 * 
	 * Path Disabled: Lazy
	 * 
	 * Pipeline: Stream -> Deduplicated Stream
	 * 
	 * Path: Map<DeduplicationHolder, Deduplicated Set<Object>>
	 * 
	 * @return the extended Pipeline
	 */
	public PathEnabledTraversalEngine dedup() {
		// Pipeline Update
		List intermediate = (List) stream.distinct().collect(Collectors.toList());

		// Update Path ( Only one path retain per last key )
		currentPath = currentPath.entrySet().parallelStream().map(e -> {
			Entry entry = (Entry) e;
			Set set = new HashSet((Set) ((Set) e.getValue()).parallelStream().limit(1).collect(Collectors.toSet()));
			return new AbstractMap.SimpleImmutableEntry(entry.getKey(), set);
		}).collect(Collectors.toMap(entry -> ((Entry) entry).getKey(), entry -> ((Entry) entry).getValue()));

		// Make stream again
		stream = intermediate.parallelStream();

		// Step Update
		Class[] args = {};
		Step step = new Step(this.getClass().getName(), "dedup", args);
		stepList.add(step);
		return this;
	}

	/**
	 * Add an ExceptFilterPipe to the end of the Pipeline. Will only emit the object
	 * if it is not in the provided collection.
	 *
	 * Path Enabled: Greedy
	 * 
	 * Path Disabled: Lazy
	 * 
	 * Pipeline: Stream -> Filtered Stream
	 * 
	 * Path: Map<DeduplicationHolder, Filtered Set<Object>>
	 * 
	 * @param collection the collection except from the stream
	 * @return the extended Pipeline
	 */
	public PathEnabledTraversalEngine except(Collection collection) {
		// Pipeline Update
		List intermediate = (List) stream.filter(e -> !collection.contains(e)).collect(Collectors.toList());

		// Update Path ( Filter if any last elements of each path are not
		// included in intermediate )
		currentPath.keySet().retainAll(intermediate);

		// Make stream again
		stream = intermediate.parallelStream();

		// Step Update
		Class[] args = new Class[1];
		args[0] = Collection.class;
		Step step = new Step(this.getClass().getName(), "except", args, collection);
		stepList.add(step);
		return this;
	}

	/**
	 * Add a RetainFilterPipe to the end of the Pipeline. Will emit the object only
	 * if it is in the provided collection.
	 *
	 * Path Enabled: Greedy
	 * 
	 * Path Disabled: Lazy
	 * 
	 * Pipeline: Stream -> Filtered Stream
	 * 
	 * Path: Map<DeduplicationHolder, Filtered Set<Object>>
	 * 
	 * @param collection the collection to retain
	 * @return the extended Pipeline
	 */

	public PathEnabledTraversalEngine retain(Collection collection) {
		// Pipeline Update
		List intermediate = (List) stream.filter(e -> collection.contains(e)).collect(Collectors.toList());

		// Update Path ( Filter if any last elements of each path are not
		// included in intermediate )
		currentPath.keySet().retainAll(intermediate);

		// Make stream again
		stream = intermediate.parallelStream();

		// Step Update
		Class[] args = new Class[1];
		args[0] = Collection.class;
		Step step = new Step(this.getClass().getName(), "retain", args, collection);
		stepList.add(step);
		return this;
	}

	/**
	 * Add an FilterFunctionPipe to the end of the Pipeline. The serves are an
	 * arbitrary filter where the filter criteria is provided by the filterFunction.
	 *
	 * Identical semantic with Java Parallelism filter
	 *
	 * Path Enabled: Greedy
	 * 
	 * Path Disabled: Lazy
	 * 
	 * Pipeline: Stream -> Filtered Stream
	 * 
	 * Path: Map<DeduplicationHolder, Filtered Set<Object>>
	 * 
	 * @param filterFunction the filter function of the pipe
	 * @return the extended Pipeline
	 */
	public PathEnabledTraversalEngine filter(FilterFunction filterFunction) {
		// Pipeline Update
		List intermediate = (List) stream.filter(e -> filterFunction.apply(e)).collect(Collectors.toList());

		// Update Path ( Filter if any last elements of each path are not
		// included in intermediate )
		// currentPath.keySet().retainAll(intermediate);

		// Make stream again
		stream = intermediate.parallelStream();

		// Step Update
		Class[] args = new Class[1];
		args[0] = FilterFunction.class;
		Step step = new Step(this.getClass().getName(), "filter", args, filterFunction);
		stepList.add(step);
		return this;
	}

	public PathEnabledTraversalEngine elementDedup(FC fc) {
		// Stream Update
		// Get Sub-Path
		Map intermediate = (Map) stream.map(e -> {

			System.out.println(((List) e).size());
			Map<String, Set<VertexEvent>> group = (Map<String, Set<VertexEvent>>) ((List) e).parallelStream()
					.collect(Collectors.groupingBy(VertexEvent::getVertexID,
							Collectors.mapping(VertexEvent::getThis, Collectors.toSet())));

			List<VertexEvent> dedup = group.entrySet().parallelStream().map(e1 -> {
				VertexEvent min = e1.getValue().parallelStream()
						.min(Comparator.comparing(ve -> ve.getTimestamp().getValue())).get();
				return min;

			}).collect(Collectors.toList());

			System.out.println(dedup.size());

			return new AbstractMap.SimpleImmutableEntry(e, dedup);

		}).collect(Collectors.toMap(e -> ((Entry) e).getKey(), e -> ((Entry) e).getValue()));

		// System.out.println("MAP : " + intermediate.size());

		// Update Path
		if (elementClass != List.class)
			updateTransformationPath(intermediate);

		// Make stream again
		stream = getStream(intermediate);

		// Step Update
		Class[] args = new Class[1];
		args[0] = FC.class;
		Step step = new Step(this.getClass().getName(), "elementDedup", args, fc);
		stepList.add(step);
		return this;
	}

	/**
	 * Add a RandomFilterPipe to the end of the Pipeline. A biased coin toss
	 * determines if the object is emitted or not.
	 *
	 * random number: 0~1 ( higher -> less filtered )
	 *
	 * Path Enabled: Greedy
	 * 
	 * Path Disabled: Lazy
	 * 
	 * Pipeline: Stream -> Filtered Stream
	 * 
	 * Path: Map<DeduplicationHolder, Filtered Set<Object>>
	 * 
	 * @param bias: pass if bias > random the bias of the random coin
	 * @return the extended Pipeline
	 */
	public PathEnabledTraversalEngine random(Double bias) {
		// Pipeline Update
		List intermediate = (List) stream.filter(e -> bias > new Random().nextDouble()).collect(Collectors.toList());

		// Update Path ( Filter if any last elements of each path are not
		// included in intermediate )
		currentPath.keySet().retainAll(intermediate);

		// Make stream again
		stream = intermediate.parallelStream();

		// Step Update
		Class[] args = new Class[1];
		args[0] = Double.class;
		Step step = new Step(this.getClass().getName(), "random", args, bias);
		stepList.add(step);
		return this;
	}

	/**
	 * Add a RageFilterPipe to the end of the Pipeline. Analogous to a high/low
	 * index lookup.
	 *
	 * Path Enabled: Greedy
	 * 
	 * Path Disabled: Lazy
	 * 
	 * Pipeline: Stream -> Filtered Stream
	 * 
	 * Path: Map<DeduplicationHolder, Filtered Set<Object>>
	 * 
	 * @param maxSize the high end of the range
	 * @return the extended Pipeline
	 */
	public PathEnabledTraversalEngine range(int maxSize) {
		// Pipeline Update
		List intermediate = (List) stream.limit(maxSize).collect(Collectors.toList());

		// Update Path ( Filter if any last elements of each path are not
		// included in intermediate )
		currentPath.keySet().retainAll(intermediate);

		// Make stream again
		stream = intermediate.parallelStream();

		// Step Update
		Class[] args = new Class[1];
		args[0] = Integer.TYPE;
		Step step = new Step(this.getClass().getName(), "range", args, maxSize);
		stepList.add(step);
		return this;
	}

	/**
	 * Add a CyclicPathFilterPipe to the end of the Pipeline. If the object's path
	 * is repeating (looping), then the object is filtered. Thus, what is emitted
	 * are those objects whose history is composed of unique objects.
	 *
	 * Path Enabled: Greedy
	 * 
	 * Path Disabled: Throw UnsupportedOperationException
	 *
	 * @return the extended Pipeline
	 */
	public PathEnabledTraversalEngine simplePath() {
		// Pipeline Update
		// Update Path ( Only one path retain per last key )
		Set prevLastPathElementSet = currentPath.keySet();

		currentPath = currentPath.entrySet().parallelStream().map(e -> {
			Entry entry = (Entry) e;
			Set pathSet = (Set) e.getValue();
			Set simplePathSet = (Set) pathSet.parallelStream().filter(p -> {
				List path = (List) p;
				Set redTestSet = new HashSet(path);
				if (path.size() != redTestSet.size())
					return false;
				else
					return true;
			}).collect(Collectors.toSet());
			if (simplePathSet.size() == 0)
				return null;
			else
				return new AbstractMap.SimpleImmutableEntry(entry.getKey(), simplePathSet);
		}).filter(p -> p != null)
				.collect(Collectors.toMap(entry -> ((Entry) entry).getKey(), entry -> ((Entry) entry).getValue()));

		// Non simple paths
		prevLastPathElementSet.removeAll(currentPath.keySet());

		// Filter non simple paths
		List intermediate = (List) stream.filter(e -> !prevLastPathElementSet.contains(e)).collect(Collectors.toList());
		stream = intermediate.parallelStream();

		// Step Update
		Class[] args = {};
		Step step = new Step(this.getClass().getName(), "simplePath", args);
		stepList.add(step);
		return this;
	}

	/////////////////////////
	/// SIDE-EFFECT PIPES ///
	/////////////////////////

	/**
	 * Add a SideEffectFunctionPipe to the end of the Pipeline. The provided
	 * function is evaluated and the incoming object is the outgoing object.
	 *
	 * @param sideEffectFunction the function of the pipe
	 * @return the extended Pipeline
	 */

	public PathEnabledTraversalEngine sideEffect(SideEffectFunction sideEffectFunction) {
		List intermediate = (List) stream.map(e -> {
			sideEffectFunction.apply(e);
			return e;
		}).collect(Collectors.toList());
		stream = intermediate.parallelStream();

		// Step Update
		Class[] args = new Class[1];
		args[0] = SideEffectFunction.class;
		Step step = new Step(this.getClass().getName(), "sideEffect", args, sideEffectFunction);
		stepList.add(step);
		return this;
	}

	////////////////////
	/// BRANCH PIPES ///
	////////////////////

	/**
	 * Add an IfThenElsePipe to the end of the Pipeline. If the ifFunction is true,
	 * then the results of the thenFunction are emitted. If the ifFunction is false,
	 * then the results of the elseFunction are emitted.
	 *
	 * Path Enabled: Greedy
	 * 
	 * Path Disabled: Lazy
	 * 
	 * Pipeline: Stream -> Then Stream or Else Stream
	 * 
	 * Path: Map<Object,
	 *
	 * @param ifFunction   the function denoting the "if" part of the pipe
	 * @param thenFunction the function denoting the "then" part of the pipe
	 * @param elseFunction the function denoting the "else" part of the pipe
	 * @return the extended Pipeline
	 */

	public PathEnabledTraversalEngine ifThenElse(Function ifFunction, Function thenFunction, Function elseFunction) {
		// Pipeline Update
		// Get Sub-Path
		Map intermediate = (Map) stream.map(element -> {
			if ((boolean) ifFunction.apply(element)) {
				return new AbstractMap.SimpleImmutableEntry(element, thenFunction.apply(element));
			} else {
				return new AbstractMap.SimpleImmutableEntry(element, elseFunction.apply(element));
			}
		}).collect(Collectors.toMap(e -> ((Entry) e).getKey(), e -> ((Entry) e).getValue()));

		// Update Path
		updateTransformationPath(intermediate);

		// Make stream again
		stream = getStream(intermediate);

		// Step Update
		Class[] args = new Class[3];
		args[0] = Function.class;
		args[1] = Function.class;
		args[2] = Function.class;
		Step step = new Step(this.getClass().getName(), "ifThenElse", args, ifFunction, thenFunction, elseFunction);
		stepList.add(step);
		return this;
	}

	private PathEnabledTraversalEngine innerLoop(List<Step> stepList, LoopFunction whileFunction) {
		// Inner Pipeline Update
		// previousPath = new HashMap<Object, Object>(currentPath);
		// currentPath.clear();

		List intermediateList = (List) stream.flatMap(e -> {
			Object intermediate = e;

			if ((boolean) whileFunction.apply(intermediate, currentPath, this.loopCount)) {

				PathEnabledTraversalEngine innerPipeline = new PathEnabledTraversalEngine(g, e, this.loopCount + 1,
						e.getClass(), listElementClass, currentPath);
				for (Object stepObject : stepList) {
					Step step = (Step) stepObject;
					step.setInstance(innerPipeline);
				}
				innerPipeline = innerPipeline.invoke(stepList);
				innerPipeline = innerPipeline.innerLoop(stepList, whileFunction);
				List innerIntermediate = innerPipeline.toList();

				// Update Path
				Map innerPathMap = innerPipeline.path();
				currentPath = innerPathMap;
				// Iterator<Entry> innerPathEntryIter = innerPathMap.entrySet().iterator();
				// while (innerPathEntryIter.hasNext()) {
				// Entry entry = innerPathEntryIter.next();
				// if (currentPath.containsKey(entry.getKey())) {
				// Set tempSet = (Set) currentPath.get(entry.getKey());
				// tempSet.addAll((Set) entry.getValue());
				// } else {
				// currentPath.put(entry.getKey(), entry.getValue());
				// }
				// }
				return innerIntermediate.parallelStream();
			} else
				return makeStream(e);

		}).collect(Collectors.toList());

		// Check No Path Update
		// if (currentPath.isEmpty())
		// currentPath = new HashMap<Object, Object>(previousPath);

		stream = intermediateList.parallelStream();

		return this;
	}

	/**
	 * Add a LoopPipe to the end of the Pipeline. Looping is useful for repeating a
	 * section of a pipeline. The provided whileFunction determines when to drop out
	 * of the loop. The whileFunction is provided a LoopBundle object which contains
	 * the object in loop along with other useful metadata.
	 *
	 * @param namedStep     the name of the step to loop back to
	 * @param whileFunction whether or not to continue looping on the current object
	 * @return the extended Pipeline
	 */
	public PathEnabledTraversalEngine loop(String namedStep, LoopFunction whileFunction) {
		// Pipeline Update
		int lastStepIdx = stepList.size();
		// previousPath = new HashMap<Object, Object>(currentPath);
		// currentPath.clear();

		List intermediateList = (List) stream.flatMap(e -> {
			Object intermediate = e;

			if ((boolean) whileFunction.apply(intermediate, currentPath, this.loopCount)) {

				// Create inner pipeline
				PathEnabledTraversalEngine innerPipeline = new PathEnabledTraversalEngine(g, e, this.loopCount + 1,
						e.getClass(), listElementClass, currentPath);

				// Prepare reflection method
				Integer backStepIdx = stepIndex.get(namedStep);
				if (backStepIdx == null || (lastStepIdx - (backStepIdx) + 1) < 0)
					return makeStream(e);
				List loopSteps = stepList.subList(backStepIdx + 1, lastStepIdx);
				for (Object stepObject : loopSteps) {
					Step step = (Step) stepObject;
					step.setInstance(innerPipeline);
				}

				innerPipeline = innerPipeline.invoke(loopSteps);
				innerPipeline = innerPipeline.innerLoop(loopSteps, whileFunction);
				List innerIntermediate = innerPipeline.toList();

				// Update Path
				Map innerPathMap = innerPipeline.path();
				currentPath = innerPathMap;

				return innerIntermediate.parallelStream();

			} else
				return makeStream(e);
		}).collect(Collectors.toList());

		stream = intermediateList.parallelStream();

		// Step Update
		Class[] args = new Class[2];
		args[0] = String.class;
		args[1] = LoopFunction.class;
		Step step = new Step(this.getClass().getName(), "loop", args);
		stepList.add(step);
		return this;
	}

	//////////////////////
	/// UTILITY PIPES ///
	//////////////////////

	/**
	 * Wrap the previous step in an AsPipe. Useful for naming steps and is used in
	 * conjunction with various other steps including: loop, select, back, table,
	 * etc.
	 *
	 * @param name the name of the AsPipe
	 * @return the extended Pipeline
	 */

	public PathEnabledTraversalEngine as(String name) {
		// Step Update
		Class[] args = new Class[1];
		args[0] = String.class;
		Step step = new Step(this.getClass().getName(), "as", args, name);
		stepList.add(step);

		this.stepIndex.put(name, stepList.indexOf(step));
		return this;
	}

	///////////////////////
	/// UTILITY METHODS ///
	///////////////////////

	/**
	 * Return a list of all the objects in the pipeline.
	 *
	 * @return a list of all the objects
	 */
	public List toList() {
		try {
			// ForkJoinPool forkJoinPool = new ForkJoinPool(16);
			// return (List) forkJoinPool.submit(() ->
			// stream.collect(Collectors.toList())).get();
			return (List) stream.collect(Collectors.toList());
		} catch (ClassCastException e) {
			throw e;
		}
	}

	/**
	 * Use after activating stream (e.g., toList())
	 * 
	 * @return
	 */
	public Map path() {
		return currentPath;
	}

	private PathEnabledTraversalEngine invoke(List<Step> stepList) {
		for (Step step : stepList) {
			step.invoke();
		}
		return this;
	}

	private Stream makeStream(Object e) {
		if (e instanceof Stream)
			return (Stream) e;
		else if (e instanceof Collection)
			return ((Collection) e).parallelStream();
		else {
			return Arrays.asList(e).parallelStream();
		}
	}

	private Stream getStream(Map intermediate) {
		if (elementClass == List.class) {
			ArrayList next = new ArrayList();
			next.addAll(intermediate.values());
			return next.parallelStream();
		} else {
			Set next = (Set) intermediate.values().parallelStream().flatMap(e -> {
				if (e instanceof Collection) {
					return ((Collection) e).parallelStream();
				} else {
					return Stream.of(e);
				}
			}).collect(Collectors.toSet());
			return next.parallelStream();
		}
	}

	private void updateTransformationPath(Map intermediate) {

		HashMap<Object, Object> nextPath = new HashMap<Object, Object>();
		System.out.println("PREV: " + currentPath);
		System.out.println("INTM: " + intermediate);
		// intermediate maps source to dest set
		Iterator<Entry> intermediateEntrySet = intermediate.entrySet().iterator();
		while (intermediateEntrySet.hasNext()) {
			Entry entry = intermediateEntrySet.next();
			Object source = entry.getKey();
			Object objectValue = entry.getValue();
			if (objectValue instanceof Collection) {
				Collection destSet = (Collection) objectValue;
				if (destSet.isEmpty()) {
					HashSet<List> currentPaths = (HashSet) currentPath.get(source);
					Iterator<List> currentPathIterator = currentPaths.iterator();
					while (currentPathIterator.hasNext()) {
						List current = currentPathIterator.next();
						List clone = new ArrayList(current);
						clone.add(null);
						if (nextPath.containsKey(null)) {
							HashSet<List> nextExisting = (HashSet) nextPath.get(null);

							nextExisting.add(clone);
							nextPath.put(null, nextExisting);
						} else {
							HashSet<List> nextEmpty = new HashSet<List>();
							nextEmpty.add(clone);
							nextPath.put(null, nextEmpty);
						}
					}
					continue;
				}

				Iterator valueIterator = destSet.iterator();
				while (valueIterator.hasNext()) {
					Object dest = valueIterator.next();
					HashSet<List> currentPaths = (HashSet) currentPath.get(source);
					Iterator<List> currentPathIterator = currentPaths.iterator();
					while (currentPathIterator.hasNext()) {
						List current = currentPathIterator.next();
						List clone = new ArrayList(current);
						clone.add(dest);
						if (nextPath.containsKey(dest)) {
							HashSet<List> nextExisting = (HashSet) nextPath.get(dest);
							nextExisting.add(clone);
							nextPath.put(dest, nextExisting);
						} else {
							HashSet<List> nextEmpty = new HashSet<List>();
							nextEmpty.add(clone);
							nextPath.put(dest, nextEmpty);
						}
					}
				}
			} else {
				Object dest = entry.getValue();

				if (dest == null) {

					HashSet<List> currentPaths = (HashSet) currentPath.get(source);
					Iterator<List> currentPathIterator = currentPaths.iterator();
					while (currentPathIterator.hasNext()) {
						List current = currentPathIterator.next();
						List clone = new ArrayList(current);
						clone.add(null);
						if (nextPath.containsKey(null)) {
							HashSet<List> nextExisting = (HashSet) nextPath.get(null);

							nextExisting.add(clone);
							nextPath.put(null, nextExisting);
						} else {
							HashSet<List> nextEmpty = new HashSet<List>();
							nextEmpty.add(clone);
							nextPath.put(null, nextEmpty);
						}
					}
					continue;
				}

				HashSet<List> currentPaths = (HashSet) currentPath.get(source);

				Iterator<List> currentPathIterator = currentPaths.iterator();
				while (currentPathIterator.hasNext()) {
					List current = currentPathIterator.next();
					List clone = new ArrayList(current);
					clone.add(dest);
					if (nextPath.containsKey(dest)) {
						HashSet<List> nextExisting = (HashSet) nextPath.get(dest);
						nextExisting.add(clone);
						nextPath.put(dest, nextExisting);
					} else {
						HashSet<List> nextEmpty = new HashSet<List>();
						nextEmpty.add(clone);
						nextPath.put(dest, nextEmpty);
					}
				}
			}
		}

		Iterator<Entry<Object, Object>> iter = currentPath.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<Object, Object> entry = iter.next();
			if (entry.getKey() == null && nextPath.containsKey(null)) {
				HashSet<List> next = (HashSet) nextPath.get(null);
				next.addAll((HashSet<List>) entry.getValue());
			} else {
				if (!intermediate.containsKey(entry.getKey())) {
					nextPath.put(entry.getKey(), entry.getValue());
				}
			}
		}
		currentPath.clear();
		currentPath = new HashMap<Object, Object>(nextPath);
		System.out.println("NEXT: " + currentPath);
	}

	private void checkInputElementClass(Class correctClass) {
		if (elementClass != correctClass)
			throw new UnsupportedOperationException(
					"Current stream element class " + elementClass + " should be " + correctClass);
	}

	private void checkInvalidInputElementClass(Class wrongClass) {
		if (elementClass == wrongClass)
			throw new UnsupportedOperationException(
					"Current stream element class " + elementClass + " is not available");
	}

	private void checkInputElementClass(Class... correctClasses) {
		boolean isMatched = false;
		for (Class correct : correctClasses) {
			if (elementClass == correct) {
				isMatched = true;
				break;
			}
		}
		if (isMatched == false) {
			throw new UnsupportedOperationException(
					"Current stream element class " + elementClass + " should be one of " + correctClasses);
		}
	}
}
