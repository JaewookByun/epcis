package org.oliot.khronos.cache.engine;

import java.util.AbstractMap;
import java.util.ArrayList;
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

import org.bson.BsonArray;
import org.oliot.khronos.cache.CachedChronoEdge;
import org.oliot.khronos.cache.CachedChronoGraph;
import org.oliot.khronos.cache.CachedChronoVertex;
import org.oliot.khronos.cache.CachedEdgeEvent;
import org.oliot.khronos.cache.CachedVertexEvent;
import org.oliot.khronos.common.HistoryPipeFunction;
import org.oliot.khronos.common.LoopPipeFunction;
import org.oliot.khronos.common.Step;
import org.oliot.khronos.common.TemporalType;
import org.oliot.khronos.common.Tokens.AC;
import org.oliot.khronos.common.Tokens.FC;
import org.oliot.khronos.common.Tokens.Position;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.pipes.PipeFunction;

/**
 * Copyright (C) 2016-2017 Jaewook Byun
 * 
 * Traversal Engine for In-memory Temporal Property Graph
 *
 * To leverage Java Parallelism, we no longer implement Gremlin interface.
 * (https://github.com/tinkerpop/gremlin).
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
@SuppressWarnings({ "rawtypes", "unchecked" })
public class CachedTraversalEngine {

	// Stream for graph traversal
	private Stream stream;

	// Manage traversal steps for Loop using Java reflection
	private ArrayList<Step> stepList;
	private HashMap<String, Integer> stepIndex;

	// Loop count starts with 0
	private final int loopCount;

	// Engine environment
	private final boolean isPathEnabled;
	private final boolean isParallel;

	// Class of stream element
	private Class elementClass;
	private Class listElementClass;

	// Path Map: Last Object -> Set of Path List
	// HashMap<Object, HashSet<List>>
	private Map<Object, Object> previousPath;
	private Map<Object, Object> currentPath;

	/**
	 * 
	 * Initialization of Traversal Engine
	 * 
	 * @param starts
	 *            is basically instance of Stream but CachedChronoGraph/Vertex/Edge
	 *            is permitted ( would be Stream<> )
	 * @param setParallel
	 *            ignored if starts is already instanceof Stream
	 * @param isPathEnabled
	 *            would spend resources to manage Path
	 */
	public CachedTraversalEngine(final Object starts, final boolean setParallel, final boolean setPathEnabled,
			final Class elementClass) {

		previousPath = new HashMap<Object, Object>();
		currentPath = new HashMap<Object, Object>();

		// Initialize Stream and Path
		if (starts instanceof Stream) {
			this.elementClass = elementClass;
			if (setPathEnabled) {
				if (setParallel) {
					stream = ((Set) ((Stream) starts).map(element -> {
						HashSet initPathSet = new HashSet();
						List list = new ArrayList();
						list.add(element);
						initPathSet.add(list);
						currentPath.put(element, initPathSet);
						return element;
					}).collect(Collectors.toSet())).parallelStream();
				} else {
					stream = ((Set) ((Stream) starts).map(element -> {
						HashSet initPathSet = new HashSet();
						List list = new ArrayList();
						list.add(element);
						initPathSet.add(list);
						currentPath.put(element, initPathSet);
						return element;
					}).collect(Collectors.toSet())).stream();
				}
			} else {
				stream = (Stream) starts;
			}
		} else if (starts instanceof CachedChronoGraph || starts instanceof CachedChronoVertex
				|| starts instanceof CachedChronoEdge || starts instanceof CachedVertexEvent
				|| starts instanceof CachedEdgeEvent) {

			HashSet set = new HashSet();
			set.add(starts);
			if (setParallel == true)
				stream = set.parallelStream();
			else
				stream = set.stream();
			this.elementClass = starts.getClass();

			if (setPathEnabled) {
				HashSet initPathSet = new HashSet();
				List list = new ArrayList();
				list.add(starts);
				initPathSet.add(list);
				currentPath.put(starts, initPathSet);
			}
		}
		stepList = new ArrayList<Step>();
		stepIndex = new HashMap<String, Integer>();
		this.isPathEnabled = setPathEnabled;
		this.isParallel = setParallel;
		this.loopCount = 0;
		listElementClass = null;
	}

	private CachedTraversalEngine(final Object starts, final boolean setParallel, final int loopCount,
			final boolean setPathEnabled, final Class elementClass, final Class listElementClass, Map currentPath) {

		previousPath = new HashMap<Object, Object>();
		if (currentPath != null)
			this.currentPath = new HashMap(currentPath);
		else
			this.currentPath = new HashMap<Object, Object>();

		// Initialize Stream and Path
		if (starts instanceof Stream) {
			this.elementClass = elementClass;
			if (setPathEnabled) {
				if (setParallel) {
					stream = ((Set) ((Stream) starts).map(element -> {
						HashSet initPathSet = new HashSet();
						List list = new ArrayList();
						list.add(element);
						initPathSet.add(list);
						currentPath.put(element, initPathSet);
						return element;
					}).collect(Collectors.toSet())).parallelStream();
				} else {
					stream = ((Set) ((Stream) starts).map(element -> {
						HashSet initPathSet = new HashSet();
						List list = new ArrayList();
						list.add(element);
						initPathSet.add(list);
						currentPath.put(element, initPathSet);
						return element;
					}).collect(Collectors.toSet())).stream();
				}
			} else {
				stream = (Stream) starts;
			}
		} else if (starts instanceof Collection) {
			if (setParallel == true)
				stream = ((Collection) starts).parallelStream();
			else
				stream = ((Collection) starts).stream();
			this.elementClass = listElementClass;
		} else if (starts instanceof CachedChronoGraph || starts instanceof CachedChronoVertex
				|| starts instanceof CachedChronoEdge || starts instanceof CachedVertexEvent
				|| starts instanceof CachedEdgeEvent) {

			HashSet set = new HashSet();
			set.add(starts);
			if (setParallel == true)
				stream = set.parallelStream();
			else
				stream = set.stream();
			this.elementClass = starts.getClass();

			if (setPathEnabled) {
				HashSet initPathSet = new HashSet();
				List list = new ArrayList();
				list.add(starts);
				initPathSet.add(list);
				currentPath.put(starts, initPathSet);
			}
		}
		stepList = new ArrayList<Step>();
		stepIndex = new HashMap<String, Integer>();
		this.loopCount = loopCount;
		this.isPathEnabled = setPathEnabled;
		this.isParallel = setParallel;
		this.listElementClass = null;
	}

	//////////////////////////
	/// TRANSFORM STEPS ///
	//////////////////////////

	/**
	 * Add V step to the traversal engine
	 *
	 * Path Enabled -> Greedy,
	 * 
	 * Path Disabled -> Lazy
	 *
	 * Stream: Stream<CachedChronoGraph> -> Stream<CachedChronoVertex> (flatMap)
	 * 
	 * Path: Map<CachedChronoGraph, Set<CachedChronoVertex>>
	 *
	 * @return the extended Stream
	 */
	public CachedTraversalEngine V() {
		// Check Input element class
		checkInputElementClass(CachedChronoGraph.class);

		// Stream Update
		if (isPathEnabled) {
			// Get Sub-Path
			Map intermediate = (Map) stream.map(g -> {
				CachedChronoGraph cg = (CachedChronoGraph) g;
				return new AbstractMap.SimpleImmutableEntry(cg, cg.getChronoVertexSet());
			}).collect(Collectors.toMap(e -> ((Entry) e).getKey(), e -> ((Entry) e).getValue()));

			// Update Path
			updateTransformationPath(intermediate);

			// Make stream again
			stream = getStream(intermediate, isParallel);
		} else {
			stream = stream.flatMap(g -> {
				return ((CachedChronoGraph) g).getChronoVertexStream(isParallel);
			});
		}
		// Step Update
		final Class[] args = {};
		final Step step = new Step(this.getClass().getName(), "V", args);
		stepList.add(step);

		// Set Class
		elementClass = CachedChronoVertex.class;
		return this;
	}

	/**
	 * Add E step to the traversal engine
	 *
	 * Path Enabled -> Greedy,
	 * 
	 * Path Disabled -> Lazy
	 * 
	 * Stream: Stream<CachedChronoGraph> -> Stream<CachedChronoEdge> (flatMap)
	 *
	 * Path: Map<CachedChronoGraph, Set<CachedChronoEdge>>
	 *
	 * @return the extended Stream
	 */
	public CachedTraversalEngine E() {
		// Check Input element class
		checkInputElementClass(CachedChronoGraph.class);

		// Stream Update
		if (isPathEnabled) {
			// Get Sub-Path
			Map intermediate = (Map) stream.map(g -> {
				CachedChronoGraph cg = (CachedChronoGraph) g;
				return new AbstractMap.SimpleImmutableEntry(cg, cg.getChronoEdgeSet());
			}).collect(Collectors.toMap(e -> ((Entry) e).getKey(), e -> ((Entry) e).getValue()));

			// Update Path
			updateTransformationPath(intermediate);

			// Make stream again
			stream = getStream(intermediate, isParallel);
		} else {
			stream = stream.flatMap(g -> {
				return ((CachedChronoGraph) g).getChronoEdgeStream(isParallel);
			});
		}
		// Step Update
		final Class[] args = {};
		final Step step = new Step(this.getClass().getName(), "E", args);
		stepList.add(step);

		// Set Class
		elementClass = CachedChronoEdge.class;
		return this;
	}

	/**
	 * Add V(key,value) step to the traversal engine
	 *
	 * Path Enabled -> Greedy,
	 * 
	 * Path Disabled -> Lazy
	 *
	 * Stream: Stream<CachedChronoGraph> -> Stream<CachedChronoVertex> (flatMap)
	 *
	 * Path: Map<CachedChronoGraph, Set<CachedChronoVertex>>
	 *
	 * @param key
	 *            they key that all the emitted vertices should be checked on
	 * @param value
	 *            the value that all the emitted vertices should have for the key
	 * @return the extended Stream
	 */
	public CachedTraversalEngine V(final String key, final Object value) {
		// Check Input element class
		checkInputElementClass(CachedChronoGraph.class);

		// Stream Update
		if (isPathEnabled) {
			// Get Sub-Path
			Map intermediate = (Map) stream.map(g -> {
				CachedChronoGraph cg = (CachedChronoGraph) g;
				return new AbstractMap.SimpleImmutableEntry(cg, cg.getChronoVertexSet(key, value));
			}).collect(Collectors.toMap(e -> ((Entry) e).getKey(), e -> ((Entry) e).getValue()));

			// Update Path
			updateTransformationPath(intermediate);

			// Make stream again
			stream = getStream(intermediate, isParallel);
		} else {
			stream = stream.flatMap(g -> {
				return ((CachedChronoGraph) g).getChronoVertexStream(key, value, isParallel);
			});
		}
		// Step Update
		final Class[] args = new Class[2];
		args[0] = String.class;
		args[1] = Object.class;
		final Step step = new Step(this.getClass().getName(), "V", args, key, value);
		stepList.add(step);

		// Set Class
		elementClass = CachedChronoVertex.class;
		return this;
	}

	/**
	 * Add E(key,value) step to the traversal engine
	 *
	 * Path Enabled -> Greedy,
	 * 
	 * Path Disabled -> Lazy
	 * 
	 * Stream: Stream<CachedChronoGraph> -> Stream<CachedChronoEdge> (flatMap)
	 *
	 * Path: Map<CachedChronoGraph, Set<CachedChronoEdge>>
	 *
	 * @param key
	 *            they key that all the emitted edges should be checked on
	 * @param value
	 *            the value that all the emitted edges should have for the key
	 * @return the extended Stream
	 */
	public CachedTraversalEngine E(final String key, final Object value) {
		// Check Input element class
		checkInputElementClass(CachedChronoGraph.class);

		// Stream Update
		if (isPathEnabled) {
			// Get Sub-Path
			Map intermediate = (Map) stream.map(g -> {
				CachedChronoGraph cg = (CachedChronoGraph) g;
				return new AbstractMap.SimpleImmutableEntry(cg, cg.getChronoEdgeSet(key, value));
			}).collect(Collectors.toMap(e -> ((Entry) e).getKey(), e -> ((Entry) e).getValue()));

			// Update Path
			updateTransformationPath(intermediate);

			// Make stream again
			stream = getStream(intermediate, isParallel);
		} else {
			stream = stream.flatMap(g -> {
				return ((CachedChronoGraph) g).getChronoEdgeStream(key, value, isParallel);
			});
		}
		// Step Update
		final Class[] args = new Class[2];
		args[0] = String.class;
		args[1] = Object.class;
		final Step step = new Step(this.getClass().getName(), "E", args, key, value);
		stepList.add(step);

		// Set Class
		elementClass = CachedChronoEdge.class;
		return this;
	}

	/////////////// bothE, outE, inE////////////////////////////

	/**
	 * Add bothE step to the traversal engine
	 *
	 * Path Enabled -> Greedy,
	 * 
	 * Path Disabled -> Lazy
	 * 
	 * Stream: Stream<CachedChronoVertex> -> Stream<CachedChronoEdge> (flatMap)
	 *
	 * Path: Map<CachedChronoVertex, Set<CachedChronoEdge>>
	 *
	 * @param branchFactor
	 *            the number of max incident edges for each incoming vertex
	 * @param labels
	 *            the edge labels to traverse
	 * @return the extended Stream
	 */
	public CachedTraversalEngine bothE(final BsonArray labels, final int branchFactor) {
		// Check Input element class
		checkInputElementClass(CachedChronoVertex.class);

		// Stream Update
		if (isPathEnabled) {
			// Get Sub-Path
			Map intermediate = (Map) stream.distinct().map(v -> {
				CachedChronoVertex cv = (CachedChronoVertex) v;
				return new AbstractMap.SimpleImmutableEntry(cv,
						cv.getChronoEdgeSet(Direction.BOTH, labels, branchFactor));
			}).collect(Collectors.toMap(e -> ((Entry) e).getKey(), e -> ((Entry) e).getValue()));

			// Update Path
			updateTransformationPath(intermediate);

			// Make stream again
			stream = getStream(intermediate, isParallel);
		} else {
			stream = stream.flatMap(v -> {
				return ((CachedChronoVertex) v).getChronoEdgeStream(Direction.BOTH, labels, branchFactor, isParallel);
			});
		}
		// Step Update
		final Class[] args = new Class[2];
		args[0] = BsonArray.class;
		args[1] = Integer.TYPE;

		final Step step = new Step(this.getClass().getName(), "bothE", args, labels, branchFactor);
		stepList.add(step);

		// Set Class
		elementClass = CachedChronoEdge.class;
		return this;
	}

	/**
	 * Add outE step to the traversal engine
	 *
	 * Path Enabled -> Greedy,
	 * 
	 * Path Disabled -> Lazy
	 *
	 * Pipeline: Stream<CachedChronoVertex> -> Stream<CachedChronoEdge> (flatMap)
	 *
	 * Path: Map<CachedChronoVertex, Set<CachedChronoEdge>>
	 *
	 * @param branchFactor
	 *            the number of max incident edges for each incoming vertex
	 * @param labels
	 *            the edge labels to traverse
	 * @return the extended Stream
	 */
	public CachedTraversalEngine outE(final BsonArray labels, final int branchFactor) {
		// Check Input element class
		checkInputElementClass(CachedChronoVertex.class);

		// Stream Update
		if (isPathEnabled) {
			// Get Sub-Path
			Map intermediate = (Map) stream.distinct().map(v -> {
				CachedChronoVertex cv = (CachedChronoVertex) v;
				return new AbstractMap.SimpleImmutableEntry(cv,
						cv.getChronoEdgeSet(Direction.OUT, labels, branchFactor));
			}).collect(Collectors.toMap(e -> ((Entry) e).getKey(), e -> ((Entry) e).getValue()));

			// Update Path
			updateTransformationPath(intermediate);

			// Make stream again
			stream = getStream(intermediate, isParallel);
		} else {
			stream = stream.flatMap(v -> {
				return ((CachedChronoVertex) v).getChronoEdgeStream(Direction.OUT, labels, branchFactor, isParallel);
			});
		}

		// Step Update
		final Class[] args = new Class[2];
		args[0] = BsonArray.class;
		args[1] = Integer.TYPE;
		final Step step = new Step(this.getClass().getName(), "outE", args, labels, branchFactor);
		stepList.add(step);

		// Set Class
		elementClass = CachedChronoEdge.class;
		return this;
	}

	/**
	 * Add inE step to the traversal engine
	 *
	 * Path Enabled -> Greedy,
	 * 
	 * Path Disabled -> Lazy
	 *
	 * Pipeline: Stream<CachedChronoVertex> -> Stream<CachedChronoEdge> (flatMap)
	 *
	 * Path: Map<CachedChronoVertex, Set<CachedChronoEdge>>
	 *
	 * @param branchFactor
	 *            the number of max incident edges for each incoming vertex
	 * @param labels
	 *            the edge labels to traverse
	 * @return the extended Stream
	 */
	public CachedTraversalEngine inE(final BsonArray labels, final int branchFactor) {
		// Check Input element class
		checkInputElementClass(CachedChronoVertex.class);

		// Stream Update
		if (isPathEnabled) {
			// Get Sub-Path
			Map intermediate = (Map) stream.distinct().map(v -> {
				CachedChronoVertex cv = (CachedChronoVertex) v;
				return new AbstractMap.SimpleImmutableEntry(cv,
						cv.getChronoEdgeSet(Direction.IN, labels, branchFactor));
			}).collect(Collectors.toMap(e -> ((Entry) e).getKey(), e -> ((Entry) e).getValue()));

			// Update Path
			updateTransformationPath(intermediate);

			// Make stream again
			stream = getStream(intermediate, isParallel);
		} else {
			stream = stream.flatMap(v -> {
				return ((CachedChronoVertex) v).getChronoEdgeStream(Direction.IN, labels, branchFactor, isParallel);
			});
		}

		// Step Update
		final Class[] args = new Class[2];
		args[0] = BsonArray.class;
		args[1] = Integer.TYPE;
		final Step step = new Step(this.getClass().getName(), "inE", args, labels, branchFactor);
		stepList.add(step);

		// Set Class
		elementClass = CachedChronoEdge.class;
		return this;
	}

	/////////////// bothV, outV, inV ////////////////////////////

	/**
	 * Add bothV step to the traversal engine
	 *
	 * Path Enabled -> Greedy,
	 * 
	 * Path Disabled -> Lazy
	 *
	 * Pipeline: Stream<CachedChronoEdge> -> Stream<CachedChronoVertex> (flatMap)
	 *
	 * Path: Map<CachedChronoEdge, List<CachedChronoVertex>>
	 *
	 * @return the extended Stream
	 */
	public CachedTraversalEngine bothV() {
		// Check Input element class
		checkInputElementClass(CachedChronoEdge.class);

		// Stream Update
		if (isPathEnabled) {
			// Get Sub-Path
			Map intermediate = (Map) stream.map(e -> {
				CachedChronoEdge ce = (CachedChronoEdge) e;
				return new AbstractMap.SimpleImmutableEntry(ce, ce.getBothChronoVertexList());
			}).collect(Collectors.toMap(e -> ((Entry) e).getKey(), e -> ((Entry) e).getValue()));

			// Update Path
			updateTransformationPath(intermediate);

			// Make stream again
			stream = getStream(intermediate, isParallel);
		} else {
			stream = stream.flatMap(e -> {
				return ((CachedChronoEdge) e).getBothChronoVertexStream(isParallel);
			});
		}
		// Step Update
		final Class[] args = {};
		final Step step = new Step(this.getClass().getName(), "bothV", args);
		stepList.add(step);

		// Set Class
		elementClass = CachedChronoVertex.class;
		return this;
	}

	/**
	 * Add outV step to the traversal engine
	 *
	 * Path Enabled -> Greedy,
	 * 
	 * Path Disabled -> Lazy
	 *
	 * Pipeline: Stream<CachedChronoEdge> -> Stream<CachedChronoVertex> (map)
	 *
	 * Path: Map<CachedChronoEdge, CachedChronoVertex>
	 *
	 * @return the extended Stream
	 */
	public CachedTraversalEngine outV() {
		// Check Input element class
		checkInputElementClass(CachedChronoEdge.class);

		// Stream Update
		if (isPathEnabled) {
			// Get Sub-Path
			Map intermediate = (Map) stream.map(e -> {
				CachedChronoEdge ce = (CachedChronoEdge) e;
				return new AbstractMap.SimpleImmutableEntry(ce, ce.getChronoVertex(Direction.OUT));
			}).collect(Collectors.toMap(e -> ((Entry) e).getKey(), e -> ((Entry) e).getValue()));

			// Update Path
			updateTransformationPath(intermediate);

			// Make stream again
			stream = getStream(intermediate, isParallel);
		} else {
			stream = stream.map(e -> {
				return ((CachedChronoEdge) e).getChronoVertex(Direction.OUT);
			});
		}
		// Step Update
		final Class[] args = {};
		final Step step = new Step(this.getClass().getName(), "outV", args);
		stepList.add(step);

		// Set Class
		elementClass = CachedChronoVertex.class;
		return this;
	}

	/**
	 * Add inV step to the traversal engine
	 * 
	 * Path Enabled -> Greedy,
	 * 
	 * Path Disabled -> Lazy
	 * 
	 * Pipeline: Stream<CachedChronoEdge> -> Stream<CachedChronoVertex> (map)
	 * 
	 * Path: Map<CachedChronoEdge, CachedChronoVertex>
	 * 
	 * @return the extended Pipeline
	 */
	public CachedTraversalEngine inV() {
		// Check Input element class
		checkInputElementClass(CachedChronoEdge.class);

		// Stream Update
		if (isPathEnabled) {
			// Get Sub-Path
			Map intermediate = (Map) stream.map(e -> {
				CachedChronoEdge ce = (CachedChronoEdge) e;
				return new AbstractMap.SimpleImmutableEntry(ce, ce.getChronoVertex(Direction.IN));
			}).collect(Collectors.toMap(e -> ((Entry) e).getKey(), e -> ((Entry) e).getValue()));

			// Update Path
			updateTransformationPath(intermediate);

			// Make stream again
			stream = getStream(intermediate, isParallel);
		} else {
			stream = stream.map(e -> {
				return ((CachedChronoEdge) e).getChronoVertex(Direction.IN);
			});
		}

		// Step Update
		final Class[] args = {};
		final Step step = new Step(this.getClass().getName(), "inV", args);
		stepList.add(step);

		// Set Class
		elementClass = CachedChronoVertex.class;
		return this;
	}

	/////////////// both, out, in ////////////////////////////

	/**
	 * Add both step to the traversal engine
	 *
	 * Path Enabled -> Greedy,
	 * 
	 * Path Disabled -> Lazy
	 * 
	 * Pipeline: Stream<CachedChronoVertex> -> Stream<CachedChronoVertex> (flatMap)
	 *
	 * Path: Map<CachedChronoVertex, Set<CachedChronoVertex>>
	 *
	 * @param branchFactor
	 *            the number of max adjacent vertices for each incoming vertex
	 * @param labels
	 *            the edge labels to traverse
	 * @return the extended Stream
	 */
	public CachedTraversalEngine both(final BsonArray labels, final int branchFactor) {
		// Check Input element class
		checkInputElementClass(CachedChronoVertex.class);

		// Stream Update
		if (isPathEnabled) {
			// Get Sub-Path
			Map intermediate = (Map) stream.distinct().map(v -> {
				CachedChronoVertex cv = (CachedChronoVertex) v;
				return new AbstractMap.SimpleImmutableEntry(cv,
						cv.getChronoVertexSet(Direction.BOTH, labels, branchFactor));
			}).collect(Collectors.toMap(e -> ((Entry) e).getKey(), e -> ((Entry) e).getValue()));

			// Update Path
			updateTransformationPath(intermediate);

			// Make stream again
			stream = getStream(intermediate, isParallel);
		} else {
			stream = stream.flatMap(v -> {
				return ((CachedChronoVertex) v).getChronoVertexStream(Direction.BOTH, labels, branchFactor, isParallel);
			});
		}
		// Step Update
		final Class[] args = new Class[2];
		args[0] = BsonArray.class;
		args[1] = Integer.TYPE;
		final Step step = new Step(this.getClass().getName(), "both", args, labels, branchFactor);
		stepList.add(step);

		// Set Class
		elementClass = CachedChronoVertex.class;
		return this;
	}

	/**
	 * Add out step to the traversal engine
	 *
	 * Path Enabled -> Greedy,
	 * 
	 * Path Disabled -> Lazy
	 *
	 * Pipeline: Stream<CachedChronoVertex> -> Stream<CachedChronoVertex> (flatMap)
	 *
	 * Path: Map<CachedChronoVertex, Set<CachedChronoVertex>>
	 *
	 * @param branchFactor
	 *            the number of max adjacent vertices for each incoming vertex
	 * @param labels
	 *            the edge labels to traverse
	 * @return the extended Stream
	 */
	public CachedTraversalEngine out(final BsonArray labels, final int branchFactor) {
		// Check Input element class
		checkInputElementClass(CachedChronoVertex.class);

		// Stream Update
		if (isPathEnabled) {
			// Get Sub-Path
			Map intermediate = (Map) stream.distinct().map(v -> {
				CachedChronoVertex cv = (CachedChronoVertex) v;
				return new AbstractMap.SimpleImmutableEntry(cv,
						cv.getChronoVertexSet(Direction.OUT, labels, branchFactor));
			}).collect(Collectors.toMap(e -> ((Entry) e).getKey(), e -> ((Entry) e).getValue()));

			// Update Path
			updateTransformationPath(intermediate);

			// Make stream again
			stream = getStream(intermediate, isParallel);
		} else {
			stream = stream.flatMap(v -> {
				return ((CachedChronoVertex) v).getChronoVertexStream(Direction.OUT, labels, branchFactor, isParallel);
			});
		}

		// Step Update
		final Class[] args = new Class[2];
		args[0] = BsonArray.class;
		args[1] = Integer.TYPE;
		final Step step = new Step(this.getClass().getName(), "out", args, labels, branchFactor);
		stepList.add(step);

		// Set Class
		elementClass = CachedChronoVertex.class;
		return this;
	}

	/**
	 * Add in step to the traversal engine
	 *
	 * Path Enabled -> Greedy,
	 * 
	 * Path Disabled -> Lazy
	 *
	 * Pipeline: Stream<CachedChronoVertex> -> Stream<CachedChronoVertex> (flatMap)
	 *
	 * Path: Map<CachedChronoVertex, Set<CachedChronoVertex>>
	 *
	 * @param branchFactor
	 *            the number of max adjacent vertices for each incoming vertex
	 * @param labels
	 *            the edge labels to traverse
	 * @return the extended Stream
	 */
	public CachedTraversalEngine in(final BsonArray labels, final int branchFactor) {
		// Check Input element class
		checkInputElementClass(CachedChronoVertex.class);

		// Stream Update
		if (isPathEnabled) {
			// Get Sub-Path
			Map intermediate = (Map) stream.distinct().map(v -> {
				CachedChronoVertex cv = (CachedChronoVertex) v;
				return new AbstractMap.SimpleImmutableEntry(cv,
						cv.getChronoVertexSet(Direction.IN, labels, branchFactor));
			}).collect(Collectors.toMap(e -> ((Entry) e).getKey(), e -> ((Entry) e).getValue()));

			// Update Path
			updateTransformationPath(intermediate);

			// Make stream again
			stream = getStream(intermediate, isParallel);
		} else {
			stream = stream.flatMap(v -> {
				return ((CachedChronoVertex) v).getChronoVertexStream(Direction.IN, labels, branchFactor, isParallel);
			});
		}
		// Step Update
		final Class[] args = new Class[2];
		args[0] = BsonArray.class;
		args[1] = Integer.TYPE;
		final Step step = new Step(this.getClass().getName(), "in", args, labels, branchFactor);
		stepList.add(step);

		// Set Class
		elementClass = CachedChronoVertex.class;
		return this;
	}

	////////////////////////////////////////
	/// Temporal Traversal Language ///
	////////////////////////////////////////

	/**
	 * Add toEvent step to the traversal engine
	 * 
	 * 
	 * Path Enabled -> Greedy,
	 * 
	 * Path Disabled -> Lazy
	 *
	 * Pipeline: Stream<CachedChronoVertex> -> Stream<CachedVertexEvent> (map)
	 * 
	 * or
	 * 
	 * Stream<CachedChronoEdge> -> Stream<CachedEdgeEvent> (map)
	 *
	 * Path: Map<CachedChronoVertex, Set<CachedVertexEvent>>
	 *
	 * or
	 *
	 * Path: Map<CachedChronoEdge, Set<CachedEdgeEvent>>
	 * 
	 * @param timestamp
	 *            to pick
	 * @return the extended Stream
	 */
	public CachedTraversalEngine toEvent(final Long timestamp) {
		// Check Input element class
		checkInputElementClass(CachedChronoVertex.class, CachedChronoEdge.class);

		// Pipeline Update
		if (isPathEnabled) {
			// Get Sub-Path
			Map intermediate = (Map) stream.map(element -> {
				if (element instanceof CachedChronoVertex) {
					CachedChronoVertex cv = (CachedChronoVertex) element;
					return new AbstractMap.SimpleImmutableEntry(cv, cv.setTimestamp(timestamp));
				} else if (element instanceof CachedChronoEdge) {
					CachedChronoEdge ce = (CachedChronoEdge) element;
					CachedEdgeEvent cee = ce.setTimestamp(timestamp);
					if (cee == null)
						return null;
					return new AbstractMap.SimpleImmutableEntry(ce, cee);
				} else {
					return null;
				}
			}).filter(e -> e != null).collect(Collectors.toMap(e -> ((Entry) e).getKey(), e -> ((Entry) e).getValue()));

			// Update Path
			updateTransformationPath(intermediate);

			// Make stream again
			stream = getStream(intermediate, isParallel);
		} else {
			stream = stream.map(element -> {
				if (element instanceof CachedChronoVertex) {
					return ((CachedChronoVertex) element).setTimestamp(timestamp);
				} else {
					// EdgeEvent can be null
					return ((CachedChronoEdge) element).setTimestamp(timestamp);
				}
			}).filter(e -> e != null);
		}

		// Step Update
		final Class[] args = new Class[1];
		args[0] = Long.class;
		final Step step = new Step(this.getClass().getName(), "toEvent", args, timestamp);
		stepList.add(step);

		// Set Class
		if (elementClass == CachedChronoVertex.class)
			elementClass = CachedVertexEvent.class;
		else
			elementClass = CachedEdgeEvent.class;
		return this;
	}

	/**
	 * Add toEvent step to the traversal engine
	 * 
	 * 
	 * Path Enabled -> Greedy,
	 * 
	 * Path Disabled -> Lazy
	 *
	 * Pipeline: Stream<CachedVertexEvent> -> Stream<CachedChronoVertex> (map)
	 * 
	 * or
	 * 
	 * Stream<CachedEdgeEvent> -> Stream<CachedChronoEdge> (map)
	 *
	 * Path: Map<CachedVertexEvent, Set<CachedChronoVertex>>
	 *
	 * or
	 *
	 * Path: Map<CachedEdgeEvent, Set<CachedChronoEdge>>
	 * 
	 * @return the extended Stream
	 */
	public CachedTraversalEngine toElement() {
		// Check Input element class
		checkInputElementClass(CachedVertexEvent.class, CachedEdgeEvent.class);

		// Stream Update
		if (isPathEnabled) {
			// Get Sub-Path
			Map intermediate = (Map) stream.map(element -> {
				if (element instanceof CachedVertexEvent) {
					CachedVertexEvent cve = (CachedVertexEvent) element;
					return new AbstractMap.SimpleImmutableEntry(cve, cve.getVertex());
				} else if (element instanceof CachedEdgeEvent) {
					CachedEdgeEvent cee = (CachedEdgeEvent) element;
					return new AbstractMap.SimpleImmutableEntry(cee, cee.getEdge());
				} else {
					return null;
				}
			}).filter(e -> e != null).collect(Collectors.toMap(e -> ((Entry) e).getKey(), e -> ((Entry) e).getValue()));

			// Update Path
			updateTransformationPath(intermediate);

			// Make stream again
			stream = getStream(intermediate, isParallel);
		} else {
			stream = stream.map(event -> {
				if (event instanceof CachedVertexEvent) {
					return ((CachedVertexEvent) event).getVertex();
				} else {
					return ((CachedEdgeEvent) event).getEdge();
				}
			});
		}
		// Step Update
		final Class[] args = {};
		final Step step = new Step(this.getClass().getName(), "toElement", args);
		stepList.add(step);

		// Set Class
		if (elementClass == CachedVertexEvent.class)
			elementClass = CachedChronoVertex.class;
		else
			elementClass = CachedChronoEdge.class;
		return this;
	}

	// Vertex Events to Edge Events //
	/**
	 * Add outEe step to the traversal engine
	 * 
	 * 
	 * Path Enabled -> Greedy,
	 * 
	 * Path Disabled -> Lazy
	 *
	 * Pipeline: Stream<CachedVertexEdge> -> Stream<CachedEdgeEvent> (flatMap)
	 * 
	 * Path: Map<CachedVertexEdge, Set<CachedEdgeEvent>>
	 * 
	 * @param labels
	 * @param typeOfEdgeEvent
	 * @param tt
	 *            from timestamp to timestamp
	 * @param s
	 *            from timestamp to interval or from interval to timestamp
	 * @param e
	 *            from timestamp to interval or from interval to timestamp
	 * @param ss
	 *            from interval to interval
	 * @param se
	 *            from interval to interval
	 * @param es
	 *            from interval to interval
	 * @param ee
	 *            from interval to interval
	 * @return the extended Stream
	 */
	public CachedTraversalEngine outEe(final BsonArray labels, final TemporalType typeOfEdgeEvent, final AC tt,
			final AC s, final AC e, final AC ss, final AC se, final AC es, final AC ee) {
		// Check Input element class
		checkInputElementClass(CachedVertexEvent.class);

		// Stream Update
		if (isPathEnabled) {
			// Get Sub-Path
			Map intermediate = (Map) stream.distinct().map(ve -> {
				CachedVertexEvent cve = (CachedVertexEvent) ve;
				return new AbstractMap.SimpleImmutableEntry(cve, cve.getEdgeEventSet(Direction.OUT, labels, tt));
			}).collect(Collectors.toMap(entry -> ((Entry) entry).getKey(), entry -> ((Entry) entry).getValue()));

			// Update Path
			updateTransformationPath(intermediate);

			// Make stream again
			stream = getStream(intermediate, isParallel);
		} else {
			stream = stream.flatMap(ve -> {
				return ((CachedVertexEvent) ve).getEdgeEventStream(Direction.OUT, labels, tt, isParallel);
			});
		}
		// Step Update
		final Class[] args = new Class[9];
		args[0] = BsonArray.class;
		args[1] = TemporalType.class;
		args[2] = AC.class;
		args[3] = AC.class;
		args[4] = AC.class;
		args[5] = AC.class;
		args[6] = AC.class;
		args[7] = AC.class;
		args[8] = AC.class;
		final Step step = new Step(this.getClass().getName(), "outEe", args, labels, typeOfEdgeEvent, tt, s, e, ss, se,
				es, ee);
		stepList.add(step);

		// Set Class
		elementClass = CachedEdgeEvent.class;
		return this;
	}

	/**
	 * Add inEe step to the traversal engine
	 * 
	 * 
	 * Path Enabled -> Greedy,
	 * 
	 * Path Disabled -> Lazy
	 *
	 * Pipeline: Stream<CachedVertexEdge> -> Stream<CachedEdgeEvent> (flatMap)
	 * 
	 * Path: Map<CachedVertexEdge, Set<CachedEdgeEvent>>
	 * 
	 * @param labels
	 * @param typeOfEdgeEvent
	 * @param tt
	 *            from timestamp to timestamp
	 * @param s
	 *            from timestamp to interval or from interval to timestamp
	 * @param e
	 *            from timestamp to interval or from interval to timestamp
	 * @param ss
	 *            from interval to interval
	 * @param se
	 *            from interval to interval
	 * @param es
	 *            from interval to interval
	 * @param ee
	 *            from interval to interval
	 * @return the extended Stream
	 */
	public CachedTraversalEngine inEe(final BsonArray labels, final TemporalType typeOfEdgeEvent, final AC tt,
			final AC s, final AC e, final AC ss, final AC se, final AC es, final AC ee) {
		// Check Input element class
		checkInputElementClass(CachedVertexEvent.class);

		// Pipeline Update

		if (isPathEnabled) {
			// Get Sub-Path
			Map intermediate = (Map) stream.distinct().map(ve -> {
				CachedVertexEvent cve = (CachedVertexEvent) ve;
				return new AbstractMap.SimpleImmutableEntry(cve, cve.getEdgeEventSet(Direction.IN, labels, tt));
			}).collect(Collectors.toMap(entry -> ((Entry) entry).getKey(), entry -> ((Entry) entry).getValue()));

			// Update Path
			updateTransformationPath(intermediate);

			// Make stream again
			stream = getStream(intermediate, isParallel);
		} else {
			stream = stream.flatMap(ve -> {
				return ((CachedVertexEvent) ve).getEdgeEventStream(Direction.IN, labels, tt, isParallel);
			});
		}
		// Step Update
		final Class[] args = new Class[9];
		args[0] = BsonArray.class;
		args[1] = TemporalType.class;
		args[2] = AC.class;
		args[3] = AC.class;
		args[4] = AC.class;
		args[5] = AC.class;
		args[6] = AC.class;
		args[7] = AC.class;
		args[8] = AC.class;
		final Step step = new Step(this.getClass().getName(), "inEe", args, labels, typeOfEdgeEvent, tt, s, e, ss, se,
				es, ee);
		stepList.add(step);

		// Set Class
		elementClass = CachedEdgeEvent.class;
		return this;
	}

	// Vertex Events to Vertex Events //
	/**
	 * Add oute step to the traversal engine
	 * 
	 * 
	 * Path Enabled -> Greedy,
	 * 
	 * Path Disabled -> Lazy
	 *
	 * Pipeline: Stream<CachedVertexEdge> -> Stream<CachedVertexEdge> (flatMap)
	 * 
	 * Path: Map<CachedVertexEdge, Set<CachedVertexEdge>>
	 * 
	 * @param labels
	 * @param typeOfEdgeEvent
	 * @param tt
	 *            from timestamp to timestamp
	 * @param s
	 *            from timestamp to interval or from interval to timestamp
	 * @param e
	 *            from timestamp to interval or from interval to timestamp
	 * @param ss
	 *            from interval to interval
	 * @param se
	 *            from interval to interval
	 * @param es
	 *            from interval to interval
	 * @param ee
	 *            from interval to interval
	 * 
	 * @return the extended Stream
	 */
	public CachedTraversalEngine oute(final BsonArray labels, final TemporalType typeOfVertexEvent, final AC tt,
			final AC s, final AC e, final AC ss, final AC se, final AC es, final AC ee, final Position pos) {
		// Check Input element class
		checkInputElementClass(CachedVertexEvent.class);

		// Stream Update
		if (isPathEnabled) {
			// Get Sub-Path
			Map intermediate = (Map) stream.distinct().map(ve -> {
				CachedVertexEvent cve = (CachedVertexEvent) ve;
				return new AbstractMap.SimpleImmutableEntry(cve, cve.getVertexEventSet(Direction.OUT, labels, tt));
			}).collect(Collectors.toMap(entry -> ((Entry) entry).getKey(), entry -> ((Entry) entry).getValue()));

			// Update Path
			updateTransformationPath(intermediate);

			// Make stream again
			stream = getStream(intermediate, isParallel);
		} else {
			stream = stream.flatMap(ve -> {
				return ((CachedVertexEvent) ve).getVertexEventStream(Direction.OUT, labels, tt, isParallel);
			});
		}
		// Step Update
		final Class[] args = new Class[10];
		args[0] = BsonArray.class;
		args[1] = TemporalType.class;
		args[2] = AC.class;
		args[3] = AC.class;
		args[4] = AC.class;
		args[5] = AC.class;
		args[6] = AC.class;
		args[7] = AC.class;
		args[8] = AC.class;
		args[9] = Position.class;
		final Step step = new Step(this.getClass().getName(), "oute", args, labels, typeOfVertexEvent, tt, s, e, ss, se,
				es, ee, pos);
		stepList.add(step);

		// Set Class
		elementClass = CachedVertexEvent.class;
		return this;
	}

	/**
	 * Add ine step to the traversal engine
	 * 
	 * 
	 * Path Enabled -> Greedy,
	 * 
	 * Path Disabled -> Lazy
	 *
	 * Pipeline: Stream<CachedVertexEdge> -> Stream<CachedVertexEdge> (flatMap)
	 * 
	 * Path: Map<CachedVertexEdge, Set<CachedVertexEdge>>
	 * 
	 * @param labels
	 * @param typeOfEdgeEvent
	 * @param tt
	 *            from timestamp to timestamp
	 * @param s
	 *            from timestamp to interval or from interval to timestamp
	 * @param e
	 *            from timestamp to interval or from interval to timestamp
	 * @param ss
	 *            from interval to interval
	 * @param se
	 *            from interval to interval
	 * @param es
	 *            from interval to interval
	 * @param ee
	 *            from interval to interval
	 * 
	 * @return the extended Stream
	 */
	public CachedTraversalEngine ine(final BsonArray labels, final TemporalType typeOfVertexEvent, final AC tt,
			final AC s, final AC e, final AC ss, final AC se, final AC es, final AC ee, final Position pos) {
		// Check Input element class
		checkInputElementClass(CachedVertexEvent.class);

		// Stream Update
		if (isPathEnabled) {
			// Get Sub-Path
			Map intermediate = (Map) stream.distinct().map(ve -> {
				CachedVertexEvent cve = (CachedVertexEvent) ve;
				return new AbstractMap.SimpleImmutableEntry(cve, cve.getVertexEventSet(Direction.IN, labels, tt));
			}).collect(Collectors.toMap(entry -> ((Entry) entry).getKey(), entry -> ((Entry) entry).getValue()));

			// Update Path
			updateTransformationPath(intermediate);

			// Make stream again
			stream = getStream(intermediate, isParallel);
		} else {
			stream = stream.flatMap(ve -> {
				return ((CachedVertexEvent) ve).getVertexEventStream(Direction.IN, labels, tt, isParallel);
			});
		}
		// Step Update
		final Class[] args = new Class[10];
		args[0] = BsonArray.class;
		args[1] = TemporalType.class;
		args[2] = AC.class;
		args[3] = AC.class;
		args[4] = AC.class;
		args[5] = AC.class;
		args[6] = AC.class;
		args[7] = AC.class;
		args[8] = AC.class;
		args[9] = Position.class;
		final Step step = new Step(this.getClass().getName(), "ine", args, labels, typeOfVertexEvent, tt, s, e, ss, se,
				es, ee, pos);
		stepList.add(step);

		// Set Class
		elementClass = CachedVertexEvent.class;
		return this;
	}

	// Edge Events to Vertex Events //
	/**
	 * Add inVe step to the traversal engine
	 * 
	 * 
	 * Path Enabled -> Greedy,
	 * 
	 * Path Disabled -> Lazy
	 *
	 * Pipeline: Stream<CachedEdgeEdge> -> Stream<CachedVertexEdge> (map)
	 * 
	 * Path: Map<CachedEdgeEdge, Set<CachedVertexEdge>>
	 * 
	 * @return the extended Stream
	 */
	public CachedTraversalEngine inVe() {
		// Check Input element class
		checkInputElementClass(CachedEdgeEvent.class);

		// Stream Update
		if (isPathEnabled) {
			// Get Sub-Path
			Map intermediate = (Map) stream.map(ee -> {
				CachedEdgeEvent cee = (CachedEdgeEvent) ee;
				return new AbstractMap.SimpleImmutableEntry(cee, cee.getVertexEvent(Direction.IN));
			}).collect(Collectors.toMap(e -> ((Entry) e).getKey(), e -> ((Entry) e).getValue()));

			// Update Path
			updateTransformationPath(intermediate);

			// Make stream again
			stream = getStream(intermediate, isParallel);
		} else {
			stream = stream.map(ee -> {
				return ((CachedEdgeEvent) ee).getVertexEvent(Direction.IN);
			});
		}
		// Step Update
		final Class[] args = {};
		final Step step = new Step(this.getClass().getName(), "inVe", args);
		stepList.add(step);

		// Set Class
		elementClass = CachedVertexEvent.class;
		return this;
	}

	/**
	 * Add inVe step to the traversal engine
	 * 
	 * 
	 * Path Enabled -> Greedy,
	 * 
	 * Path Disabled -> Lazy
	 *
	 * Pipeline: Stream<CachedEdgeEdge> -> Stream<CachedVertexEdge> (map)
	 * 
	 * Path: Map<CachedEdgeEdge, Set<CachedVertexEdge>>
	 * 
	 * @return the extended Stream
	 */
	public CachedTraversalEngine outVe() {
		// Check Input element class
		checkInputElementClass(CachedEdgeEvent.class);

		// Stream Update
		if (isPathEnabled) {
			// Get Sub-Path
			Map intermediate = (Map) stream.map(ee -> {
				CachedEdgeEvent cee = (CachedEdgeEvent) ee;
				return new AbstractMap.SimpleImmutableEntry(cee, cee.getVertexEvent(Direction.OUT));
			}).collect(Collectors.toMap(e -> ((Entry) e).getKey(), e -> ((Entry) e).getValue()));

			// Update Path
			updateTransformationPath(intermediate);

			// Make stream again
			stream = getStream(intermediate, isParallel);
		} else {
			stream = stream.map(ee -> {
				return ((CachedEdgeEvent) ee).getVertexEvent(Direction.OUT);
			});
		}

		// Step Update
		final Class[] args = {};
		final Step step = new Step(this.getClass().getName(), "outVe", args);
		stepList.add(step);

		// Set Class
		elementClass = CachedVertexEvent.class;
		return this;
	}

	/////////////// shuffle ////////////////////////////

	/**
	 * Add shuffle step to the traversal engine
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
	public CachedTraversalEngine shuffle() {
		// Check Invalid Input element class
		checkInvalidInputElementClass(List.class);

		// Stream Update
		List list = (List) stream.collect(Collectors.toList());
		Collections.shuffle(list);
		if (isParallel)
			stream = list.parallelStream();
		else
			stream = list.stream();

		// Step Update
		final Class[] args = {};
		final Step step = new Step(this.getClass().getName(), "shuffle", args);
		stepList.add(step);
		return this;
	}

	/////////////// scatter gather ////////////////////////////

	/**
	 * Add gather step to the traversal engine
	 *
	 * Do not use gather twice before scattered.
	 *
	 * Greedy
	 * 
	 * Pipeline: Stream -> Stream<Stream>
	 *
	 * @return the extended Stream
	 */
	public CachedTraversalEngine gather() {
		// Check Invalid Input element class
		checkInvalidInputElementClass(List.class);

		// Stream Update
		List intermediate = (List) stream.collect(Collectors.toList());

		ArrayList list = new ArrayList();
		list.add(intermediate);

		// System.out.println(list.size());

		if (isParallel)
			stream = list.parallelStream();
		else
			stream = list.stream();

		// Step Update
		final Class[] args = {};
		final Step step = new Step(this.getClass().getName(), "gather", args);
		stepList.add(step);

		// Set Class
		listElementClass = elementClass;
		elementClass = List.class;
		return this;
	}

	/**
	 * Add scatter step to the traversal engine
	 *
	 * Path Enabled: Greedy
	 * 
	 * Path Disabled: Lazy
	 * 
	 * Pipeline: Stream<Collection> -> Stream<Object>, Stream<Stream> ->
	 * Stream<Object>, Stream<Object> -> Stream<Object>
	 *
	 * @return the extended Stream
	 */
	public CachedTraversalEngine scatter() {
		if (elementClass != List.class)
			return this;

		// Stream Update
		if (isPathEnabled) {
			// Get Sub-Path
			List intermediate = (List) stream.flatMap(e -> {
				return ((List) e).parallelStream();
			}).collect(Collectors.toList());

			// Update Path ( Filter if any last elements of each path are not
			// included in intermediate )
			currentPath.keySet().retainAll(intermediate);

			// Make stream again
			if (isParallel)
				stream = intermediate.parallelStream();
			else
				stream = intermediate.stream();
		} else {
			stream = stream.flatMap(e -> {
				if (isParallel)
					return ((List) e).parallelStream();
				else
					return ((List) e).stream();
			});
		}

		// Step Update
		final Class[] args = {};
		final Step step = new Step(this.getClass().getName(), "scatter", args);
		stepList.add(step);

		// Set Class
		elementClass = listElementClass;
		listElementClass = null;
		return this;
	}

	/////////////// transform ////////////////////////////

	/**
	 * Add transform step to the traversal engine
	 *
	 * Path Enabled: Greedy
	 * 
	 * Path Disabled: Lazy
	 * 
	 * Pipeline: Stream -> Transformed Stream (map)
	 * 
	 * Path: Map<Object, TransformedObject>
	 *
	 * @param function
	 *            the transformation function of the pipe
	 * @return the extended Stream
	 */
	public CachedTraversalEngine transform(final PipeFunction function, final Class outputClass) {
		// Stream Update
		if (isPathEnabled) {
			// Get Sub-Path
			Map intermediate = (Map) stream.map(e -> {
				return new AbstractMap.SimpleImmutableEntry(e, function.compute(e));
			}).collect(Collectors.toMap(e -> ((Entry) e).getKey(), e -> ((Entry) e).getValue()));

			// Update Path
			if (elementClass != List.class)
				updateTransformationPath(intermediate);

			// Make stream again
			stream = getStream(intermediate, isParallel);
		} else {
			stream = stream.map(e -> {
				return function.compute(e);
			});
		}
		// Step Update
		final Class[] args = new Class[2];
		args[0] = PipeFunction.class;
		args[1] = Class.class;
		final Step step = new Step(this.getClass().getName(), "transform", args, function, outputClass);
		stepList.add(step);

		// Set Class
		elementClass = outputClass;
		return this;
	}

	public CachedTraversalEngine pathEnabledTransform(final HistoryPipeFunction function, final Class outputClass) {
		// Stream Update
		if (isPathEnabled) {
			// Get Sub-Path
			Map intermediate = (Map) stream.map(e -> {
				return new AbstractMap.SimpleImmutableEntry(e, function.compute(e, currentPath));
			}).collect(Collectors.toMap(e -> ((Entry) e).getKey(), e -> ((Entry) e).getValue()));

			// Update Path
			if (elementClass != List.class)
				updateTransformationPath(intermediate);

			// Make stream again
			stream = getStream(intermediate, isParallel);
		} else {
			// Not available
			throw new UnsupportedOperationException();
		}
		// Step Update
		final Class[] args = new Class[2];
		args[0] = HistoryPipeFunction.class;
		args[1] = Class.class;
		final Step step = new Step(this.getClass().getName(), "pathEnabledTransform", args, function, outputClass);
		stepList.add(step);

		// Set Class
		elementClass = outputClass;
		return this;
	}

	/////////////// order ////////////////////////////

	/**
	 * Add order step to the traversal engine
	 *
	 * if parallel, stream is first aggregated, ordered and becomes sequential
	 * ordered stream
	 * 
	 * No Effect on Path
	 *
	 * @return the extended Stream
	 */

	public CachedTraversalEngine order(final Comparator comparator) {
		// Stream Update
		if (isParallel) {
			List list = (List) stream.collect(Collectors.toList());
			stream = list.stream().sorted(comparator);
		} else {
			stream = stream.sorted(comparator);
		}

		// Step Update
		final Class[] args = new Class[1];
		args[0] = Comparator.class;
		final Step step = new Step(this.getClass().getName(), "order", args, comparator);
		stepList.add(step);
		return this;
	}

	////////////////////
	/// FILTER PIPES ///
	////////////////////

	/**
	 * Add dedup step to the traversal engine
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
	public CachedTraversalEngine dedup() {
		// Stream Update
		if (isPathEnabled) {
			List intermediate = (List) stream.distinct().collect(Collectors.toList());

			// Update Path ( Only one path retain per last key )
			currentPath = currentPath.entrySet().parallelStream().map(e -> {
				Entry entry = (Entry) e;
				Set set = new HashSet((Set) ((Set) e.getValue()).parallelStream().limit(1).collect(Collectors.toSet()));
				return new AbstractMap.SimpleImmutableEntry(entry.getKey(), set);
			}).collect(Collectors.toMap(entry -> ((Entry) entry).getKey(), entry -> ((Entry) entry).getValue()));

			// Make stream again
			if (isParallel)
				stream = intermediate.parallelStream();
			else
				stream = intermediate.stream();

		} else {
			stream = stream.distinct();
		}

		// Step Update
		final Class[] args = {};
		final Step step = new Step(this.getClass().getName(), "dedup", args);
		stepList.add(step);
		return this;
	}

	/**
	 * Add except step to the traversal engine
	 *
	 * Path Enabled: Greedy
	 * 
	 * Path Disabled: Lazy
	 * 
	 * Pipeline: Stream -> Filtered Stream
	 * 
	 * Path: Map<DeduplicationHolder, Filtered Set<Object>>
	 * 
	 * @param collection
	 *            the collection except from the stream
	 * @return the extended Stream
	 */
	public CachedTraversalEngine except(final Collection collection) {
		// Stream Update
		if (isPathEnabled) {
			List intermediate = (List) stream.filter(e -> !collection.contains(e)).collect(Collectors.toList());

			// Update Path ( Filter if any last elements of each path are not
			// included in intermediate )
			currentPath.keySet().retainAll(intermediate);

			// Make stream again
			if (isParallel)
				stream = intermediate.parallelStream();
			else
				stream = intermediate.stream();

		} else {
			stream = stream.filter(e -> !collection.contains(e));
		}

		// Step Update
		final Class[] args = new Class[1];
		args[0] = Collection.class;
		final Step step = new Step(this.getClass().getName(), "except", args, collection);
		stepList.add(step);
		return this;
	}

	/**
	 * Add retain step to the traversal engine
	 *
	 * Path Enabled: Greedy
	 * 
	 * Path Disabled: Lazy
	 * 
	 * Pipeline: Stream -> Filtered Stream
	 * 
	 * Path: Map<DeduplicationHolder, Filtered Set<Object>>
	 * 
	 * @param collection
	 *            the collection to retain
	 * @return the extended Stream
	 */

	public CachedTraversalEngine retain(final Collection collection) {
		// Stream Update
		if (isPathEnabled) {
			List intermediate = (List) stream.filter(e -> collection.contains(e)).collect(Collectors.toList());

			// Update Path ( Filter if any last elements of each path are not
			// included in intermediate )
			currentPath.keySet().retainAll(intermediate);

			// Make stream again
			if (isParallel)
				stream = intermediate.parallelStream();
			else
				stream = intermediate.stream();

		} else {
			stream = stream.filter(e -> collection.contains(e));
		}

		// Step Update
		final Class[] args = new Class[1];
		args[0] = Collection.class;
		final Step step = new Step(this.getClass().getName(), "retain", args, collection);
		stepList.add(step);
		return this;
	}

	/**
	 * Add filter step to the traversal engine
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
	 * @param filterFunction
	 *            the filter function of the step
	 * @return the extended Stream
	 */
	public CachedTraversalEngine filter(final PipeFunction filterFunction) {
		// Stream Update
		if (isPathEnabled) {
			List intermediate = (List) stream.filter(e -> (boolean) filterFunction.compute(e))
					.collect(Collectors.toList());

			// Update Path ( Filter if any last elements of each path are not
			// included in intermediate )
			currentPath.keySet().retainAll(intermediate);

			// Make stream again
			if (isParallel)
				stream = intermediate.parallelStream();
			else
				stream = intermediate.stream();

		} else {
			stream = stream.filter(e -> (boolean) filterFunction.compute(e));
		}

		// Step Update
		final Class[] args = new Class[1];
		args[0] = PipeFunction.class;
		final Step step = new Step(this.getClass().getName(), "filter", args, filterFunction);
		stepList.add(step);
		return this;
	}

	// Event의 Collection을 가정하고
	// 주어진 Graph Element Set를 지운다
	public CachedTraversalEngine elementExcept(Collection collection) {
		// Stream Update
		if (isPathEnabled) {

			// Get Sub-Path
			Map intermediate = (Map) stream.map(e -> {
				if (e instanceof List) {
					List eventList = (List) e;
					// System.out.println(eventList.size());
					// System.out.println(collection);
					eventList = (List) eventList.stream().filter(e1 -> {
						if (e1 instanceof CachedVertexEvent) {
							if (collection.contains(((CachedVertexEvent) e1).getVertex()))
								return false;
							return true;
						} else if (e1 instanceof CachedEdgeEvent) {
							if (collection.contains(((CachedEdgeEvent) e1).getEdge()))
								return false;
							return true;
						} else {
							return true;
						}
					}).collect(Collectors.toList());
					// System.out.println(eventList.size());
					return new AbstractMap.SimpleImmutableEntry(e, eventList);
				} else {
					return new AbstractMap.SimpleImmutableEntry(e, e);
				}
			}).collect(Collectors.toMap(e -> ((Entry) e).getKey(), e -> ((Entry) e).getValue()));

			// Update Path
			if (elementClass != List.class)
				updateTransformationPath(intermediate);

			// Make stream again
			stream = getStream(intermediate, isParallel);

		} else {
			stream = stream.map(e -> {
				if (e instanceof List) {
					List eventList = (List) e;
					// System.out.println(eventList.size());
					// System.out.println(collection);
					eventList = (List) eventList.parallelStream().filter(e1 -> {
						if (e1 instanceof CachedVertexEvent) {
							if (collection.contains(((CachedVertexEvent) e1).getVertex()))
								return false;
							return true;
						} else if (e1 instanceof CachedEdgeEvent) {
							if (collection.contains(((CachedEdgeEvent) e1).getEdge()))
								return false;
							return true;
						} else {
							return true;
						}
					}).collect(Collectors.toList());
					// System.out.println(eventList.size());
					return eventList;
				} else {
					return e;
				}
			});
		}

		// Step Update
		final Class[] args = new Class[1];
		args[0] = Collection.class;
		final Step step = new Step(this.getClass().getName(), "elementExcept", args, collection);
		stepList.add(step);
		return this;
	}

	// Event의 Collection을 가정하고
	// 주어진 Graph Element Set를 지운다
	public CachedTraversalEngine elementDedup(FC fc) {
		// Stream Update
		if (isPathEnabled) {

			// Get Sub-Path
			Map intermediate = (Map) stream.map(e -> {

				// System.out.println(((List)e).size());
				Map<String, Set<CachedVertexEvent>> group = (Map<String, Set<CachedVertexEvent>>) ((List) e)
						.parallelStream().collect(Collectors.groupingBy(CachedVertexEvent::getVertexID,
								Collectors.mapping(CachedVertexEvent::getThis, Collectors.toSet())));

				List<CachedVertexEvent> dedup = group.entrySet().parallelStream().map(e1 -> {

					CachedVertexEvent min = e1.getValue().parallelStream()
							.min(Comparator.comparing(ve -> ve.getTimestamp())).get();
					return min;

				}).collect(Collectors.toList());

				// System.out.println(dedup.size());

				return new AbstractMap.SimpleImmutableEntry(e, dedup);

			}).collect(Collectors.toMap(e -> ((Entry) e).getKey(), e -> ((Entry) e).getValue()));

			// System.out.println("MAP : " + intermediate.size());

			// Update Path
			if (elementClass != List.class)
				updateTransformationPath(intermediate);

			// Make stream again
			stream = getStream(intermediate, isParallel);

		} else {
			stream = stream.map(e -> {

				if (e instanceof List) {

					// System.out.println(((List) e).size());
					Map<String, Set<CachedVertexEvent>> group = (Map<String, Set<CachedVertexEvent>>) ((List) e)
							.parallelStream().collect(Collectors.groupingBy(CachedVertexEvent::getVertexID,
									Collectors.mapping(CachedVertexEvent::getThis, Collectors.toSet())));

					List<CachedVertexEvent> dedup = group.entrySet().parallelStream().map(e1 -> {

						CachedVertexEvent min = e1.getValue().parallelStream()
								.min(Comparator.comparing(ve -> ve.getTimestamp())).get();
						return min;

					}).collect(Collectors.toList());

					// System.out.println(dedup.size());
					return dedup;
				} else {
					return e;
				}
			});
		}

		// Step Update
		final Class[] args = new Class[1];
		args[0] = FC.class;
		final Step step = new Step(this.getClass().getName(), "elementDedup", args, fc);
		stepList.add(step);
		return this;
	}

	/**
	 * Add random step to the traversal engine
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
	 * @param bias:
	 *            pass if bias > random the bias of the random coin
	 * @return the extended Stream
	 */
	public CachedTraversalEngine random(final Double bias) {
		// Stream Update
		if (isPathEnabled) {
			List intermediate = (List) stream.filter(e -> bias > new Random().nextDouble())
					.collect(Collectors.toList());

			// Update Path ( Filter if any last elements of each path are not
			// included in intermediate )
			currentPath.keySet().retainAll(intermediate);

			// Make stream again
			if (isParallel)
				stream = intermediate.parallelStream();
			else
				stream = intermediate.stream();

		} else {
			stream = stream.filter(e -> bias > new Random().nextDouble());
		}

		// Step Update
		final Class[] args = new Class[1];
		args[0] = Double.class;
		final Step step = new Step(this.getClass().getName(), "random", args, bias);
		stepList.add(step);
		return this;
	}

	/**
	 * Add range step to the traversal engine
	 *
	 * Path Enabled: Greedy
	 * 
	 * Path Disabled: Lazy
	 * 
	 * Pipeline: Stream -> Filtered Stream
	 * 
	 * Path: Map<DeduplicationHolder, Filtered Set<Object>>
	 * 
	 * @param maxSize
	 *            the high end of the range
	 * @return the extended Stream
	 */
	public CachedTraversalEngine range(final int maxSize) {
		// Stream Update
		if (isPathEnabled) {
			List intermediate = (List) stream.limit(maxSize).collect(Collectors.toList());

			// Update Path ( Filter if any last elements of each path are not
			// included in intermediate )
			currentPath.keySet().retainAll(intermediate);

			// Make stream again
			if (isParallel)
				stream = intermediate.parallelStream();
			else
				stream = intermediate.stream();

		} else {
			stream = stream.limit(maxSize);
		}

		// Step Update
		final Class[] args = new Class[1];
		args[0] = Integer.TYPE;
		final Step step = new Step(this.getClass().getName(), "range", args, maxSize);
		stepList.add(step);
		return this;
	}

	/**
	 * Add simplePath step to the traversal engine
	 *
	 * Path Enabled: Greedy
	 * 
	 * Path Disabled: Throw UnsupportedOperationException
	 *
	 * @return the extended Stream
	 */
	public CachedTraversalEngine simplePath() {
		// Stream Update
		if (isPathEnabled) {

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
			List intermediate = (List) stream.filter(e -> !prevLastPathElementSet.contains(e))
					.collect(Collectors.toList());
			if (isParallel)
				stream = intermediate.parallelStream();
			else
				stream = intermediate.stream();
		} else
			throw new UnsupportedOperationException();

		// Step Update
		final Class[] args = {};
		final Step step = new Step(this.getClass().getName(), "simplePath", args);
		stepList.add(step);
		return this;
	}

	////////////////////////////
	/// SIDE-EFFECT PIPES ///
	////////////////////////////

	/**
	 * Add sideEffect step to the traversal engine
	 *
	 * @param sideEffectFunction
	 *            the function of the pipe
	 * @return the extended Stream
	 */
	public CachedTraversalEngine sideEffect(final PipeFunction sideEffectFunction) {
		if (isPathEnabled) {
			List intermediate = (List) stream.map(e -> {
				sideEffectFunction.compute(e);
				return e;
			}).collect(Collectors.toList());
			if (isParallel) {
				stream = intermediate.parallelStream();
			} else {
				stream = intermediate.stream();
			}
		} else {
			stream = stream.map(e -> {
				sideEffectFunction.compute(e);
				return e;
			});
		}
		// Step Update
		final Class[] args = new Class[1];
		args[0] = PipeFunction.class;
		final Step step = new Step(this.getClass().getName(), "sideEffect", args, sideEffectFunction);
		stepList.add(step);
		return this;
	}

	///////////////////////
	/// BRANCH PIPES ///
	///////////////////////

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
	 * @param ifFunction
	 *            the function denoting the "if" part of the pipe
	 * @param thenFunction
	 *            the function denoting the "then" part of the pipe
	 * @param elseFunction
	 *            the function denoting the "else" part of the pipe
	 * @return the extended Stream
	 */
	public CachedTraversalEngine ifThenElse(final PipeFunction ifFunction, final PipeFunction thenFunction,
			final PipeFunction elseFunction) {
		// Stream Update
		if (isPathEnabled) {
			// Get Sub-Path
			Map intermediate = (Map) stream.map(element -> {
				if ((boolean) ifFunction.compute(element)) {
					Object thenResult = thenFunction.compute(element);
					if (thenResult != null)
						return new AbstractMap.SimpleImmutableEntry(element, thenResult);
					else
						return null;
				} else {
					Object elseResult = elseFunction.compute(element);
					if (elseResult != null)
						return new AbstractMap.SimpleImmutableEntry(element, elseResult);
					else
						return null;
				}
			}).filter(e -> e != null).collect(Collectors.toMap(e -> ((Entry) e).getKey(), e -> ((Entry) e).getValue()));

			// Update Path
			updateTransformationPath(intermediate);

			// Make stream again
			stream = getStream(intermediate, isParallel);
		} else {
			stream = stream.flatMap(element -> {
				if ((boolean) ifFunction.compute(element)) {
					return makeStream(thenFunction.compute(element), isParallel);
				} else {
					return makeStream(elseFunction.compute(element), isParallel);
				}
			});
		}
		// Step Update
		final Class[] args = new Class[3];
		args[0] = PipeFunction.class;
		args[1] = PipeFunction.class;
		args[2] = PipeFunction.class;
		final Step step = new Step(this.getClass().getName(), "ifThenElse", args, ifFunction, thenFunction,
				elseFunction);
		stepList.add(step);
		return this;
	}

	private CachedTraversalEngine innerLoop(final List<Step> stepList, final LoopPipeFunction whileFunction) {
		// Inner Pipeline Update
		if (isPathEnabled) {
			previousPath = new HashMap<Object, Object>(currentPath);
			currentPath.clear();

			List intermediateList = (List) stream.flatMap(e -> {
				Object intermediate = e;

				if ((boolean) whileFunction.compute(intermediate, previousPath, this.loopCount)) {

					CachedTraversalEngine innerPipeline = new CachedTraversalEngine(e, isParallel, this.loopCount + 1,
							isPathEnabled, e.getClass(), listElementClass, previousPath);
					for (Object stepObject : stepList) {
						Step step = (Step) stepObject;
						step.setInstance(innerPipeline);
					}
					innerPipeline = innerPipeline.invoke(stepList);
					innerPipeline = innerPipeline.innerLoop(stepList, whileFunction);
					List innerIntermediate = innerPipeline.toList();

					// Update Path
					Map innerPathMap = innerPipeline.path();
					Iterator<Entry> innerPathEntryIter = innerPathMap.entrySet().iterator();
					while (innerPathEntryIter.hasNext()) {
						Entry entry = innerPathEntryIter.next();
						if (currentPath.containsKey(entry.getKey())) {
							Set tempSet = (Set) currentPath.get(entry.getKey());
							tempSet.addAll((Set) entry.getValue());
						} else {
							currentPath.put(entry.getKey(), entry.getValue());
						}
					}

					if (isParallel)
						return innerIntermediate.parallelStream();
					else
						return innerIntermediate.stream();
				} else
					return makeStream(e, isParallel);

			}).collect(Collectors.toList());

			// Check No Path Update
			if (currentPath.isEmpty())
				currentPath = new HashMap<Object, Object>(previousPath);

			if (isParallel)
				stream = intermediateList.parallelStream();
			else
				stream = intermediateList.stream();
		} else {
			stream = stream.flatMap(e -> {
				Object intermediate = e;
				if ((boolean) whileFunction.compute(intermediate, null, this.loopCount)) {

					CachedTraversalEngine innerPipeline = new CachedTraversalEngine(e, isParallel, this.loopCount + 1,
							isPathEnabled, e.getClass(), listElementClass, null);
					for (Object stepObject : stepList) {
						Step step = (Step) stepObject;
						step.setInstance(innerPipeline);
					}
					innerPipeline = innerPipeline.invoke(stepList);
					innerPipeline = innerPipeline.innerLoop(stepList, whileFunction);
					if (isParallel)
						return innerPipeline.toList().parallelStream();
					else
						return innerPipeline.toList().stream();

				}
				return makeStream(e, isParallel);
			});
		}
		return this;
	}

	/**
	 * Add loop step to the end of the Stream. Looping is useful for repeating a
	 * section of a pipeline. The provided whileFunction determines when to drop out
	 * of the loop. The whileFunction is provided a LoopBundle object which contains
	 * the object in loop along with other useful metadata.
	 *
	 * @param namedStep
	 *            the name of the step to loop back to
	 * @param whileFunction
	 *            whether or not to continue looping on the current object
	 * @return the extended Stream
	 */
	public CachedTraversalEngine loop(final String namedStep, final LoopPipeFunction whileFunction) {
		// Stream Update
		int lastStepIdx = stepList.size();

		if (isPathEnabled) {

			previousPath = new HashMap<Object, Object>(currentPath);
			currentPath.clear();

			List intermediateList = (List) stream.flatMap(e -> {
				Object intermediate = e;

				if ((boolean) whileFunction.compute(intermediate, previousPath, this.loopCount)) {

					// Create inner pipeline
					CachedTraversalEngine innerPipeline = new CachedTraversalEngine(e, isParallel, this.loopCount + 1,
							isPathEnabled, e.getClass(), listElementClass, previousPath);

					// Prepare reflection method
					Integer backStepIdx = stepIndex.get(namedStep);
					if (backStepIdx == null || (lastStepIdx - (backStepIdx) + 1) < 0)
						return makeStream(e, isParallel);
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
					Iterator<Entry> innerPathEntryIter = innerPathMap.entrySet().iterator();
					while (innerPathEntryIter.hasNext()) {
						Entry entry = innerPathEntryIter.next();
						if (currentPath.containsKey(entry.getKey())) {
							Set tempSet = (Set) currentPath.get(entry.getKey());
							tempSet.addAll((Set) entry.getValue());
						} else {
							currentPath.put(entry.getKey(), entry.getValue());
						}
					}

					if (isParallel)
						return innerIntermediate.parallelStream();
					else
						return innerIntermediate.stream();
				} else {
					return makeStream(e, isParallel);
				}

			}).collect(Collectors.toList());

			if (currentPath.isEmpty()) {
				currentPath = new HashMap<Object, Object>(previousPath);
				previousPath.clear();
			}

			if (isParallel)
				stream = intermediateList.parallelStream();
			else
				stream = intermediateList.stream();
		} else {
			stream = stream.flatMap(e -> {
				Object intermediate = e;
				if ((boolean) whileFunction.compute(intermediate, null, this.loopCount)) {
					Integer backStepIdx = stepIndex.get(namedStep);
					if (backStepIdx == null || (lastStepIdx - (backStepIdx) + 1) < 0)
						return makeStream(e, isParallel);

					CachedTraversalEngine innerPipeline = new CachedTraversalEngine(e, isParallel, this.loopCount + 1,
							isPathEnabled, e.getClass(), listElementClass, null);
					List loopSteps = stepList.subList(backStepIdx + 1, lastStepIdx);
					for (Object stepObject : loopSteps) {
						Step step = (Step) stepObject;
						step.setInstance(innerPipeline);
					}
					innerPipeline = innerPipeline.invoke(loopSteps);
					innerPipeline = innerPipeline.innerLoop(loopSteps, whileFunction);
					if (isParallel)
						return innerPipeline.toList().parallelStream();
					else
						return innerPipeline.toList().stream();

				} else
					return makeStream(e, isParallel);
			});
		}

		// Step Update
		final Class[] args = new Class[2];
		args[0] = String.class;
		args[1] = LoopPipeFunction.class;
		final Step step = new Step(this.getClass().getName(), "loop", args);
		stepList.add(step);
		return this;
	}

	////////////////////////
	/// UTILITY PIPES ///
	////////////////////////

	/**
	 * Wrap the previous step in an AsPipe. Useful for naming steps and is used in
	 * conjunction with various other steps including: loop
	 *
	 * @param name
	 *            the name of the AsPipe
	 * @return the extended Stream
	 */

	public CachedTraversalEngine as(final String name) {
		// Step Update
		final Class[] args = new Class[1];
		args[0] = String.class;
		final Step step = new Step(this.getClass().getName(), "as", args, name);
		stepList.add(step);

		this.stepIndex.put(name, stepList.indexOf(step));
		return this;
	}

	//////////////////////////
	/// UTILITY METHODS ///
	//////////////////////////

	/**
	 * Check the path support is enabled
	 * 
	 * @return true: enabled false: disabled
	 */

	public boolean isPathEnabled() {
		return isPathEnabled;
	}

	/**
	 * Return a list of all the objects in the pipeline.
	 *
	 * @return a list of all the objects
	 */
	public List toList() {
		try {

			// try {
			// ForkJoinPool forkJoinPool = new ForkJoinPool(16);
			// return (List) forkJoinPool.submit(() ->
			// stream.collect(Collectors.toList())).get();
			// } catch (InterruptedException | ExecutionException e) {
			// e.printStackTrace();
			// return null;
			// }
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

	private CachedTraversalEngine invoke(final List<Step> stepList) {
		for (Step step : stepList) {
			step.invoke();
		}
		return this;
	}

	private Stream makeStream(final Object e, final boolean isParallel) {
		if (e instanceof Stream)
			return (Stream) e;
		else if (e instanceof Collection && isParallel)
			return ((Collection) e).parallelStream();
		else if (e instanceof Collection && !isParallel)
			return ((Collection) e).stream();
		else {
			if (isParallel) {
				ArrayList list = new ArrayList();
				list.add(e);
				return list.parallelStream();
			} else
				return Stream.of(e);
		}
	}

	private Stream getStream(final Map intermediate, final boolean isParallel) {
		if (elementClass == List.class) {
			ArrayList next = new ArrayList();
			next.addAll(intermediate.values());
			if (isParallel)
				return next.parallelStream();
			else
				return next.stream();
		} else {
			Set next = (Set) intermediate.values().parallelStream().flatMap(e -> {
				if (e instanceof Collection) {
					return ((Collection) e).parallelStream();
				} else {
					return Stream.of(e);
				}
			}).collect(Collectors.toSet());
			if (isParallel)
				return next.parallelStream();
			else
				return next.stream();
		}
	}

	private void updateTransformationPath(final Map intermediate) {
		previousPath = new HashMap<Object, Object>(currentPath);
		currentPath.clear();

		Iterator<Entry> intermediateEntrySet = intermediate.entrySet().iterator();
		while (intermediateEntrySet.hasNext()) {
			Entry entry = intermediateEntrySet.next();
			// 1 or 2
			Object key = entry.getKey();
			Object objectValue = entry.getValue();
			if (objectValue instanceof Set) {
				Set value = (Set) objectValue;

				Iterator valueIterator = value.iterator();
				while (valueIterator.hasNext()) {
					Object val = valueIterator.next();
					HashSet<List> previousPaths = (HashSet) previousPath.get(key);

					Iterator<List> previousPathIterator = previousPaths.iterator();
					while (previousPathIterator.hasNext()) {
						List previousPath = previousPathIterator.next();
						if (currentPath.containsKey(val)) {
							HashSet<List> currentExisting = (HashSet) currentPath.get(val);
							List clone = new ArrayList(previousPath);
							clone.add(val);
							currentExisting.add(clone);
							currentPath.put(val, currentExisting);
						} else {
							HashSet<List> currentNew = new HashSet<List>();
							List clone = new ArrayList(previousPath);
							clone.add(val);
							currentNew.add(clone);
							currentPath.put(val, currentNew);
						}
					}
				}
			} else {
				Object val = entry.getValue();

				HashSet<List> previousPaths = (HashSet) previousPath.get(key);

				Iterator<List> previousPathIterator = previousPaths.iterator();
				while (previousPathIterator.hasNext()) {
					List previousPath = previousPathIterator.next();
					if (currentPath.containsKey(val)) {
						HashSet<List> currentExisting = (HashSet) currentPath.get(val);
						List clone = new ArrayList(previousPath);
						clone.add(val);
						currentExisting.add(clone);
						currentPath.put(val, currentExisting);
					} else {
						HashSet<List> currentNew = new HashSet<List>();
						List clone = new ArrayList(previousPath);
						clone.add(val);
						currentNew.add(clone);
						currentPath.put(val, currentNew);
					}
				}
			}
		}
		previousPath.clear();
	}

	private void checkInputElementClass(final Class correctClass) {
		if (elementClass != correctClass)
			throw new UnsupportedOperationException(
					"Current stream element class " + elementClass + " should be " + correctClass);
	}

	private void checkInvalidInputElementClass(final Class wrongClass) {
		if (elementClass == wrongClass)
			throw new UnsupportedOperationException(
					"Current stream element class " + elementClass + " is not available");
	}

	private void checkInputElementClass(final Class... correctClasses) {
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
