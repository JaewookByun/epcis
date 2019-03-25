package org.oliot.khronos.persistent.engine;

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
import org.oliot.khronos.common.HistoryPipeFunction;
import org.oliot.khronos.common.LoopPipeFunction;
import org.oliot.khronos.common.Step;
import org.oliot.khronos.common.TemporalType;
import org.oliot.khronos.common.Tokens.AC;
import org.oliot.khronos.common.Tokens.FC;
import org.oliot.khronos.common.Tokens.Position;
import org.oliot.khronos.persistent.ChronoEdge;
import org.oliot.khronos.persistent.ChronoGraph;
import org.oliot.khronos.persistent.ChronoVertex;
import org.oliot.khronos.persistent.EdgeEvent;
import org.oliot.khronos.persistent.VertexEvent;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.pipes.PipeFunction;

/**
 * Copyright (C) 2016-2017 Jaewook Byun
 * 
 * Traversal Language for In-memory Temporal Property Graph
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
public class TraversalEngine {

	// Stream for graph traversal
	private Stream stream;

	// Manage traversal steps for Loop using Java reflection
	private ArrayList<Step> stepList;
	private HashMap<String, Integer> stepIndex;

	// Loop count starts with 0
	private final int loopCount;

	// Pipeline environment
	private final boolean isPathEnabled;
	private final boolean isParallel;

	// Class of stream element
	private Class elementClass;
	private Class listElementClass;

	// Path Map: Last Object -> Set of Path List
	// HashMap<Object, HashSet<List>>
	// private Map<Object, Object> previousPath;
	private Map<Object, Object> currentPath;

	private final ChronoGraph g;

	/**
	 * 
	 * Initialize CachedChronoGraphPipeline
	 * 
	 * @param starts
	 *            is basically instance of Stream but CachedChronoGraph/Vertex/Edge
	 *            is permitted ( would be Stream<> )
	 * @param setParallel
	 *            ignored if starts is already instanceof Stream
	 * @param isPathEnabled
	 *            would spend resources to manage Path
	 */
	public TraversalEngine(final ChronoGraph g, final Object starts, final boolean setParallel,
			final boolean setPathEnabled, final Class elementClass) {

		// previousPath = new HashMap<Object, Object>();
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
		} else if (starts instanceof ChronoGraph || starts instanceof ChronoVertex || starts instanceof ChronoEdge
				|| starts instanceof VertexEvent || starts instanceof EdgeEvent) {

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
		this.g = g;
	}

	private TraversalEngine(final ChronoGraph g, final Object starts, final boolean setParallel, final int loopCount,
			final boolean setPathEnabled, final Class elementClass, final Class listElementClass, Map currentPath) {

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
			if (setParallel) {
				stream = ((Collection) starts).parallelStream();
			} else {
				stream = ((Collection) starts).stream();
			}
			// }

		} else if (starts instanceof ChronoGraph || starts instanceof ChronoVertex || starts instanceof ChronoEdge
				|| starts instanceof VertexEvent || starts instanceof EdgeEvent) {

			HashSet set = new HashSet();
			set.add(starts);
			if (setParallel == true)
				stream = set.parallelStream();
			else
				stream = set.stream();
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
		this.isPathEnabled = setPathEnabled;
		this.isParallel = setParallel;
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
	public TraversalEngine V() {
		// Check Input element class
		checkInputElementClass(ChronoGraph.class);

		// Pipeline Update
		if (isPathEnabled) {
			// Get Sub-Path
			Map intermediate = (Map) stream.map(g -> {
				ChronoGraph cg = (ChronoGraph) g;
				return new AbstractMap.SimpleImmutableEntry(cg, cg.getChronoVertexSet());
			}).collect(Collectors.toMap(e -> ((Entry) e).getKey(), e -> ((Entry) e).getValue()));

			// Update Path
			updateTransformationPath(intermediate);

			// Make stream again
			stream = getStream(intermediate, isParallel);
		} else {
			stream = stream.flatMap(g -> {
				return ((ChronoGraph) g).getChronoVertexStream(isParallel);
			});
		}
		// Step Update
		final Class[] args = {};
		final Step step = new Step(this.getClass().getName(), "V", args);
		stepList.add(step);

		// Set Class
		elementClass = ChronoVertex.class;
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
	public TraversalEngine E() {
		// Check Input element class
		checkInputElementClass(ChronoGraph.class);

		// Pipeline Update
		if (isPathEnabled) {
			// Get Sub-Path
			Map intermediate = (Map) stream.map(g -> {
				ChronoGraph cg = (ChronoGraph) g;
				return new AbstractMap.SimpleImmutableEntry(cg, cg.getChronoEdgeSet());
			}).collect(Collectors.toMap(e -> ((Entry) e).getKey(), e -> ((Entry) e).getValue()));

			// Update Path
			updateTransformationPath(intermediate);

			// Make stream again
			stream = getStream(intermediate, isParallel);
		} else {
			stream = stream.flatMap(g -> {
				return ((ChronoGraph) g).getChronoEdgeStream(isParallel);
			});
		}
		// Step Update
		final Class[] args = {};
		final Step step = new Step(this.getClass().getName(), "E", args);
		stepList.add(step);

		// Set Class
		elementClass = ChronoEdge.class;
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
	 * @param key
	 *            they key that all the emitted vertices should be checked on
	 * @param value
	 *            the value that all the emitted vertices should have for the key
	 * @return the extended Pipeline
	 */
	public TraversalEngine V(final String key, final Object value) {
		// Check Input element class
		checkInputElementClass(ChronoGraph.class);

		// Pipeline Update
		if (isPathEnabled) {
			// Get Sub-Path
			Map intermediate = (Map) stream.map(g -> {
				ChronoGraph cg = (ChronoGraph) g;
				return new AbstractMap.SimpleImmutableEntry(cg, cg.getChronoVertexSet(key, value));
			}).collect(Collectors.toMap(e -> ((Entry) e).getKey(), e -> ((Entry) e).getValue()));

			// Update Path
			updateTransformationPath(intermediate);

			// Make stream again
			stream = getStream(intermediate, isParallel);
		} else {
			stream = stream.flatMap(g -> {
				return ((ChronoGraph) g).getChronoVertexStream(key, value, isParallel);
			});
		}
		// Step Update
		final Class[] args = new Class[2];
		args[0] = String.class;
		args[1] = Object.class;
		final Step step = new Step(this.getClass().getName(), "V", args, key, value);
		stepList.add(step);

		// Set Class
		elementClass = ChronoVertex.class;
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
	 * @param key
	 *            they key that all the emitted edges should be checked on
	 * @param value
	 *            the value that all the emitted edges should have for the key
	 * @return the extended Pipeline
	 */
	public TraversalEngine E(final String key, final Object value) {
		// Check Input element class
		checkInputElementClass(ChronoGraph.class);

		// Pipeline Update
		if (isPathEnabled) {
			// Get Sub-Path
			Map intermediate = (Map) stream.map(g -> {
				ChronoGraph cg = (ChronoGraph) g;
				return new AbstractMap.SimpleImmutableEntry(cg, cg.getChronoEdgeSet(key, value));
			}).collect(Collectors.toMap(e -> ((Entry) e).getKey(), e -> ((Entry) e).getValue()));

			// Update Path
			updateTransformationPath(intermediate);

			// Make stream again
			stream = getStream(intermediate, isParallel);
		} else {
			stream = stream.flatMap(g -> {
				return ((ChronoGraph) g).getChronoEdgeStream(key, value, isParallel);
			});
		}
		// Step Update
		final Class[] args = new Class[2];
		args[0] = String.class;
		args[1] = Object.class;
		final Step step = new Step(this.getClass().getName(), "E", args, key, value);
		stepList.add(step);

		// Set Class
		elementClass = ChronoEdge.class;
		return this;
	}

	/////////////// bothE, outE, inE////////////////////////////

	/**
	 * Add a BothEdgesPipe to the end of the Pipeline. Emit the adjacent incoming
	 * edges for the incoming vertex.
	 *
	 * Path Enabled -> Greedy,
	 * 
	 * Path Disabled -> Lazy
	 * 
	 * Pipeline: Stream<CachedChronoVertex> -> Stream<CachedChronoEdge>
	 *
	 * Path: Map<CachedChronoVertex, Set<CachedChronoEdge>>
	 *
	 * @param branchFactor
	 *            the number of max incident edges for each incoming vertex
	 * @param labels
	 *            the edge labels to traverse
	 * @return the extended Pipeline
	 */
	public TraversalEngine bothE(final BsonArray labels, final int branchFactor) {
		// Check Input element class
		checkInputElementClass(ChronoVertex.class);

		// Pipeline Update
		if (isPathEnabled) {
			// Get Sub-Path
			Map intermediate = (Map) stream.map(v -> {
				ChronoVertex cv = (ChronoVertex) v;
				return new AbstractMap.SimpleImmutableEntry(cv,
						cv.getChronoEdgeSet(Direction.BOTH, labels, branchFactor));
			}).collect(Collectors.toMap(e -> ((Entry) e).getKey(), e -> ((Entry) e).getValue()));

			// Update Path
			updateTransformationPath(intermediate);

			// Make stream again
			stream = getStream(intermediate, isParallel);
		} else {
			stream = stream.flatMap(v -> {
				return ((ChronoVertex) v).getChronoEdgeStream(Direction.BOTH, labels, branchFactor, isParallel);
			});
		}
		// Step Update
		final Class[] args = new Class[2];
		args[0] = BsonArray.class;
		args[1] = Integer.TYPE;

		final Step step = new Step(this.getClass().getName(), "bothE", args, labels, branchFactor);
		stepList.add(step);

		// Set Class
		elementClass = ChronoEdge.class;
		return this;
	}

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
	 * @param branchFactor
	 *            the number of max incident edges for each incoming vertex
	 * @param labels
	 *            the edge labels to traverse
	 * @return the extended Pipeline
	 */
	public TraversalEngine outE(final BsonArray labels, final int branchFactor) {
		// Check Input element class
		checkInputElementClass(ChronoVertex.class);

		// Pipeline Update
		if (isPathEnabled) {
			// Get Sub-Path
			Map intermediate = (Map) stream.map(v -> {
				ChronoVertex cv = (ChronoVertex) v;
				return new AbstractMap.SimpleImmutableEntry(cv,
						cv.getChronoEdgeSet(Direction.OUT, labels, branchFactor));
			}).collect(Collectors.toMap(e -> ((Entry) e).getKey(), e -> ((Entry) e).getValue()));

			// Update Path
			updateTransformationPath(intermediate);

			// Make stream again
			stream = getStream(intermediate, isParallel);
		} else {
			stream = stream.flatMap(v -> {
				return ((ChronoVertex) v).getChronoEdgeStream(Direction.OUT, labels, branchFactor, isParallel);
			});
		}

		// Step Update
		final Class[] args = new Class[2];
		args[0] = BsonArray.class;
		args[1] = Integer.TYPE;
		final Step step = new Step(this.getClass().getName(), "outE", args, labels, branchFactor);
		stepList.add(step);

		// Set Class
		elementClass = ChronoEdge.class;
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
	 * @param branchFactor
	 *            the number of max incident edges for each incoming vertex
	 * @param labels
	 *            the edge labels to traverse
	 * @return the extended Pipeline
	 */
	public TraversalEngine inE(final BsonArray labels, final int branchFactor) {
		// Check Input element class
		checkInputElementClass(ChronoVertex.class);

		// Pipeline Update

		if (isPathEnabled) {
			// Get Sub-Path
			Map intermediate = (Map) stream.map(v -> {
				ChronoVertex cv = (ChronoVertex) v;
				return new AbstractMap.SimpleImmutableEntry(cv,
						cv.getChronoEdgeSet(Direction.IN, labels, branchFactor));
			}).collect(Collectors.toMap(e -> ((Entry) e).getKey(), e -> ((Entry) e).getValue()));

			// Update Path
			updateTransformationPath(intermediate);

			// Make stream again
			stream = getStream(intermediate, isParallel);
		} else {
			stream = stream.flatMap(v -> {
				return ((ChronoVertex) v).getChronoEdgeStream(Direction.IN, labels, branchFactor, isParallel);
			});
		}

		// Step Update
		final Class[] args = new Class[2];
		args[0] = BsonArray.class;
		args[1] = Integer.TYPE;
		final Step step = new Step(this.getClass().getName(), "inE", args, labels, branchFactor);
		stepList.add(step);

		// Set Class
		elementClass = ChronoEdge.class;
		return this;
	}

	/////////////// bothV, outV, inV ////////////////////////////

	/**
	 * Add a BothVerticesPipe to the end of the Pipeline. Emit both the tail and
	 * head vertices of the incoming edge.
	 *
	 * Path Enabled -> Greedy,
	 * 
	 * Path Disabled -> Lazy
	 *
	 * Pipeline: Stream<CachedChronoEdge> -> Stream<CachedChronoVertex>
	 *
	 * Path: Map<CachedChronoEdge, List<CachedChronoVertex>>
	 *
	 * @return the extended Pipeline
	 */
	public TraversalEngine bothV() {
		// Check Input element class
		checkInputElementClass(ChronoEdge.class);

		// Pipeline Update

		if (isPathEnabled) {
			// Get Sub-Path
			Map intermediate = (Map) stream.map(e -> {
				ChronoEdge ce = (ChronoEdge) e;
				return new AbstractMap.SimpleImmutableEntry(ce, ce.getBothVertexList());
			}).collect(Collectors.toMap(e -> ((Entry) e).getKey(), e -> ((Entry) e).getValue()));

			// Update Path
			updateTransformationPath(intermediate);

			// Make stream again
			stream = getStream(intermediate, isParallel);
		} else {
			stream = stream.flatMap(e -> {
				return ((ChronoEdge) e).getBothVertexStream(isParallel);
			});
		}
		// Step Update
		final Class[] args = {};
		final Step step = new Step(this.getClass().getName(), "bothV", args);
		stepList.add(step);

		// Set Class
		elementClass = ChronoVertex.class;
		return this;
	}

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
	public TraversalEngine outV() {
		// Check Input element class
		checkInputElementClass(ChronoEdge.class);

		// Pipeline Update
		if (isPathEnabled) {
			// Get Sub-Path
			Map intermediate = (Map) stream.map(e -> {
				ChronoEdge ce = (ChronoEdge) e;
				return new AbstractMap.SimpleImmutableEntry(ce, ce.getChronoVertex(Direction.OUT));
			}).collect(Collectors.toMap(e -> ((Entry) e).getKey(), e -> ((Entry) e).getValue()));

			// Update Path
			updateTransformationPath(intermediate);

			// Make stream again
			stream = getStream(intermediate, isParallel);
		} else {
			stream = stream.map(e -> {
				return ((ChronoEdge) e).getChronoVertex(Direction.OUT);
			});
		}
		// Step Update
		final Class[] args = {};
		final Step step = new Step(this.getClass().getName(), "outV", args);
		stepList.add(step);

		// Set Class
		elementClass = ChronoVertex.class;
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
	public TraversalEngine inV() {
		// Check Input element class
		checkInputElementClass(ChronoEdge.class);

		// Pipeline Update
		if (isPathEnabled) {
			// Get Sub-Path
			Map intermediate = (Map) stream.map(e -> {
				ChronoEdge ce = (ChronoEdge) e;
				return new AbstractMap.SimpleImmutableEntry(ce, ce.getChronoVertex(Direction.IN));
			}).collect(Collectors.toMap(e -> ((Entry) e).getKey(), e -> ((Entry) e).getValue()));

			// Update Path
			updateTransformationPath(intermediate);

			// Make stream again
			stream = getStream(intermediate, isParallel);
		} else {
			stream = stream.map(e -> {
				return ((ChronoEdge) e).getChronoVertex(Direction.IN);
			});
		}

		// Step Update
		final Class[] args = {};
		final Step step = new Step(this.getClass().getName(), "inV", args);
		stepList.add(step);

		// Set Class
		elementClass = ChronoVertex.class;
		return this;
	}

	/////////////// both, out, in ////////////////////////////

	/**
	 * Add a BothPipe to the end of the Pipeline. Emit the adjacent incoming
	 * vertices for the incoming vertex.
	 *
	 * Path Enabled -> Greedy,
	 * 
	 * Path Disabled -> Lazy
	 * 
	 * Pipeline: Stream<CachedChronoVertex> -> Stream<CachedChronoVertex>
	 *
	 * Path: Map<CachedChronoVertex, Set<CachedChronoVertex>>
	 *
	 * @param branchFactor
	 *            the number of max adjacent vertices for each incoming vertex
	 * @param labels
	 *            the edge labels to traverse
	 * @return the extended Pipeline
	 */
	public TraversalEngine both(final BsonArray labels, final int branchFactor) {
		// Check Input element class
		checkInputElementClass(ChronoVertex.class);

		// Pipeline Update
		if (isPathEnabled) {
			// Get Sub-Path
			Map intermediate = (Map) stream.map(v -> {
				ChronoVertex cv = (ChronoVertex) v;
				return new AbstractMap.SimpleImmutableEntry(cv,
						cv.getChronoVertexSet(Direction.BOTH, labels, branchFactor));
			}).collect(Collectors.toMap(e -> ((Entry) e).getKey(), e -> ((Entry) e).getValue()));

			// Update Path
			updateTransformationPath(intermediate);

			// Make stream again
			stream = getStream(intermediate, isParallel);
		} else {
			stream = stream.flatMap(v -> {
				return ((ChronoVertex) v).getChronoVertexStream(Direction.BOTH, labels, branchFactor, isParallel);
			});
		}
		// Step Update
		final Class[] args = new Class[2];
		args[0] = BsonArray.class;
		args[1] = Integer.TYPE;
		final Step step = new Step(this.getClass().getName(), "both", args, labels, branchFactor);
		stepList.add(step);

		// Set Class
		elementClass = ChronoVertex.class;
		return this;
	}

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
	 * @param branchFactor
	 *            the number of max adjacent vertices for each incoming vertex
	 * @param labels
	 *            the edge labels to traverse
	 * @return the extended Pipeline
	 */
	public TraversalEngine out(final BsonArray labels, final int branchFactor) {
		// Check Input element class
		checkInputElementClass(ChronoVertex.class);

		// Pipeline Update
		if (isPathEnabled) {
			// Get Sub-Path
			Map intermediate = (Map) stream.map(v -> {
				ChronoVertex cv = (ChronoVertex) v;
				return new AbstractMap.SimpleImmutableEntry(cv,
						cv.getChronoVertexSet(Direction.OUT, labels, branchFactor));
			}).collect(Collectors.toMap(e -> ((Entry) e).getKey(), e -> ((Entry) e).getValue()));

			// Update Path
			updateTransformationPath(intermediate);

			// Make stream again
			stream = getStream(intermediate, isParallel);
		} else {
			stream = stream.flatMap(v -> {
				return ((ChronoVertex) v).getChronoVertexStream(Direction.OUT, labels, branchFactor, isParallel);
			});
		}

		// Step Update
		final Class[] args = new Class[2];
		args[0] = BsonArray.class;
		args[1] = Integer.TYPE;
		final Step step = new Step(this.getClass().getName(), "out", args, labels, branchFactor);
		stepList.add(step);

		// Set Class
		elementClass = ChronoVertex.class;
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
	 * @param branchFactor
	 *            the number of max adjacent vertices for each incoming vertex
	 * @param labels
	 *            the edge labels to traverse
	 * @return the extended Pipeline
	 */
	public TraversalEngine in(final BsonArray labels, final int branchFactor) {
		// Check Input element class
		checkInputElementClass(ChronoVertex.class);

		// Pipeline Update

		if (isPathEnabled) {
			// Get Sub-Path
			Map intermediate = (Map) stream.map(v -> {
				ChronoVertex cv = (ChronoVertex) v;
				return new AbstractMap.SimpleImmutableEntry(cv,
						cv.getChronoVertexSet(Direction.IN, labels, branchFactor));
			}).collect(Collectors.toMap(e -> ((Entry) e).getKey(), e -> ((Entry) e).getValue()));

			// Update Path
			updateTransformationPath(intermediate);

			// Make stream again
			stream = getStream(intermediate, isParallel);
		} else {
			stream = stream.flatMap(v -> {
				return ((ChronoVertex) v).getChronoVertexStream(Direction.IN, labels, branchFactor, isParallel);
			});
		}
		// Step Update
		final Class[] args = new Class[2];
		args[0] = BsonArray.class;
		args[1] = Integer.TYPE;
		final Step step = new Step(this.getClass().getName(), "in", args, labels, branchFactor);
		stepList.add(step);

		// Set Class
		elementClass = ChronoVertex.class;
		return this;
	}

	////////////////////////////////////////
	/// Temporal Traversal Language ///
	////////////////////////////////////////

	public TraversalEngine toEvent(final Long timestamp) {
		// Check Input element class
		// checkInputElementClass(ChronoVertex.class, ChronoEdge.class);

		// Pipeline Update
		if (isPathEnabled) {
			// Get Sub-Path
			Map intermediate = (Map) stream.map(element -> {
				if (element instanceof ChronoVertex) {
					ChronoVertex cv = (ChronoVertex) element;
					return new AbstractMap.SimpleImmutableEntry(cv, cv.setTimestamp(timestamp));
				} else if (element instanceof ChronoEdge) {
					ChronoEdge ce = (ChronoEdge) element;
					EdgeEvent cee = ce.setTimestamp(timestamp);
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
				if (element instanceof ChronoVertex) {
					return ((ChronoVertex) element).setTimestamp(timestamp);
				} else {
					// EdgeEvent can be null
					return ((ChronoEdge) element).setTimestamp(timestamp);
				}
			}).filter(e -> e != null);
		}

		// Step Update
		final Class[] args = new Class[1];
		args[0] = Long.class;
		final Step step = new Step(this.getClass().getName(), "toEvent", args, timestamp);
		stepList.add(step);

		// Set Class
		if (elementClass == ChronoVertex.class)
			elementClass = VertexEvent.class;
		else
			elementClass = EdgeEvent.class;
		return this;
	}

	public TraversalEngine toElement() {
		// Check Input element class
		checkInputElementClass(VertexEvent.class, EdgeEvent.class);

		// Pipeline Update

		if (isPathEnabled) {
			// Get Sub-Path
			Map intermediate = (Map) stream.map(element -> {
				if (element instanceof VertexEvent) {
					VertexEvent cve = (VertexEvent) element;
					return new AbstractMap.SimpleImmutableEntry(cve, cve.getVertex());
				} else if (element instanceof EdgeEvent) {
					EdgeEvent cee = (EdgeEvent) element;
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
				if (event instanceof VertexEvent) {
					return ((VertexEvent) event).getVertex();
				} else {
					return ((EdgeEvent) event).getEdge();
				}
			});
		}
		// Step Update
		final Class[] args = {};
		final Step step = new Step(this.getClass().getName(), "toElement", args);
		stepList.add(step);

		// Set Class
		if (elementClass == VertexEvent.class)
			elementClass = ChronoVertex.class;
		else
			elementClass = ChronoEdge.class;
		return this;
	}

	// Vertex Events to Edge Events //

	public TraversalEngine outEe(final BsonArray labels, final TemporalType typeOfEdgeEvent, final AC tt, final AC s,
			final AC e, final AC ss, final AC se, final AC es, final AC ee) {
		// Check Input element class
		checkInputElementClass(VertexEvent.class);

		// Pipeline Update

		if (isPathEnabled) {
			// Get Sub-Path
			Map intermediate = (Map) stream.map(ve -> {
				VertexEvent cve = (VertexEvent) ve;
				return new AbstractMap.SimpleImmutableEntry(cve,
						cve.getEdgeEventSet(Direction.OUT, labels, typeOfEdgeEvent, tt, s, e, ss, se, es, ee));
			}).collect(Collectors.toMap(entry -> ((Entry) entry).getKey(), entry -> ((Entry) entry).getValue()));

			// Update Path
			updateTransformationPath(intermediate);

			// Make stream again
			stream = getStream(intermediate, isParallel);
		} else {
			stream = stream.flatMap(ve -> {
				return ((VertexEvent) ve).getEdgeEventStream(Direction.OUT, labels, typeOfEdgeEvent, tt, s, e, ss, se,
						es, ee, isParallel);
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
		elementClass = EdgeEvent.class;
		return this;
	}

	public TraversalEngine inEe(final BsonArray labels, final TemporalType typeOfEdgeEvent, final AC tt, final AC s,
			final AC e, final AC ss, final AC se, final AC es, final AC ee) {
		// Check Input element class
		checkInputElementClass(VertexEvent.class);

		// Pipeline Update

		if (isPathEnabled) {
			// Get Sub-Path
			Map intermediate = (Map) stream.map(ve -> {
				VertexEvent cve = (VertexEvent) ve;
				return new AbstractMap.SimpleImmutableEntry(cve,
						cve.getEdgeEventSet(Direction.IN, labels, typeOfEdgeEvent, tt, s, e, ss, se, es, ee));
			}).collect(Collectors.toMap(entry -> ((Entry) entry).getKey(), entry -> ((Entry) entry).getValue()));

			// Update Path
			updateTransformationPath(intermediate);

			// Make stream again
			stream = getStream(intermediate, isParallel);
		} else {
			stream = stream.flatMap(ve -> {
				return ((VertexEvent) ve).getEdgeEventStream(Direction.IN, labels, typeOfEdgeEvent, tt, s, e, ss, se,
						es, ee, isParallel);
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
		elementClass = EdgeEvent.class;
		return this;
	}

	// Vertex Events to Vertex Events //

	public TraversalEngine oute(final AC tt) {
		// Check Input element class
		checkInputElementClass(VertexEvent.class);

		// Pipeline Update

		if (isPathEnabled) {
			// Get Sub-Path
			Map intermediate = (Map) stream.map(ve -> {
				VertexEvent cve = (VertexEvent) ve;

				return new AbstractMap.SimpleImmutableEntry(cve, cve.getOutVertexEventSet(tt));
			}).collect(Collectors.toMap(entry -> ((Entry) entry).getKey(), entry -> ((Entry) entry).getValue()));

			// Update Path
			updateTransformationPath(intermediate);

			// Make stream again
			stream = getStream(intermediate, isParallel);
		} else {
			stream = stream.flatMap(ve -> {
				return ((VertexEvent) ve).getOutVertexEventSet(tt).stream();
			});
		}
		// Step Update
		final Class[] args = new Class[1];
		args[0] = AC.class;
		final Step step = new Step(this.getClass().getName(), "oute", args, tt);
		stepList.add(step);

		// Set Class
		elementClass = VertexEvent.class;
		return this;
	}

	public TraversalEngine oute(final BsonArray labels, final TemporalType typeOfVertexEvent, final AC tt, final AC s,
			final AC e, final AC ss, final AC se, final AC es, final AC ee, final Position pos) {
		// Check Input element class
		checkInputElementClass(VertexEvent.class);

		// Pipeline Update

		if (isPathEnabled) {
			// Get Sub-Path
			Map intermediate = (Map) stream.map(ve -> {
				VertexEvent cve = (VertexEvent) ve;

				return new AbstractMap.SimpleImmutableEntry(cve,
						cve.getVertexEventSet(Direction.OUT, labels, typeOfVertexEvent, tt, s, e, ss, se, es, ee, pos));
			}).collect(Collectors.toMap(entry -> ((Entry) entry).getKey(), entry -> ((Entry) entry).getValue()));

			// Update Path
			updateTransformationPath(intermediate);

			// Make stream again
			stream = getStream(intermediate, isParallel);
		} else {
			stream = stream.flatMap(ve -> {
				return ((VertexEvent) ve)
						.getVertexEventSet(Direction.OUT, labels, typeOfVertexEvent, tt, s, e, ss, se, es, ee, pos)
						.parallelStream();
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
		elementClass = VertexEvent.class;
		return this;
	}

	public TraversalEngine bothe(final String label, final AC tt) {
		// Check Input element class
		checkInputElementClass(VertexEvent.class);

		// Pipeline Update

		if (isPathEnabled) {
			// Get Sub-Path
			Map intermediate = (Map) stream.map(ve -> {
				VertexEvent cve = (VertexEvent) ve;
				return new AbstractMap.SimpleImmutableEntry(cve, cve.getBothVertexEventSet(label, tt));
			}).collect(Collectors.toMap(entry -> ((Entry) entry).getKey(), entry -> ((Entry) entry).getValue()));

			// Update Path
			updateTransformationPath(intermediate);

			// Make stream again
			stream = getStream(intermediate, isParallel);
		} else {
			stream = stream.flatMap(ve -> {
				return ((VertexEvent) ve).getBothVertexEventSet(label, tt);
			});
		}
		// Step Update
		final Class[] args = new Class[2];
		args[0] = String.class;
		args[1] = AC.class;
		final Step step = new Step(this.getClass().getName(), "bothe", args, label, tt);
		stepList.add(step);

		// Set Class
		elementClass = VertexEvent.class;
		return this;
	}

	public TraversalEngine oute(final String label, final AC tt) {
		// Check Input element class
		checkInputElementClass(VertexEvent.class);

		// Pipeline Update

		if (isPathEnabled) {
			// Get Sub-Path
			Map intermediate = (Map) stream.map(ve -> {
				VertexEvent cve = (VertexEvent) ve;
				return new AbstractMap.SimpleImmutableEntry(cve, cve.getOutVertexEventSet(label, tt));
			}).collect(Collectors.toMap(entry -> ((Entry) entry).getKey(), entry -> ((Entry) entry).getValue()));

			// Update Path
			updateTransformationPath(intermediate);

			// Make stream again
			stream = getStream(intermediate, isParallel);
		} else {
			stream = stream.flatMap(ve -> {
				return ((VertexEvent) ve).getOutVertexEventSet(label, tt);
			});
		}
		// Step Update
		final Class[] args = new Class[2];
		args[0] = String.class;
		args[1] = AC.class;
		final Step step = new Step(this.getClass().getName(), "oute", args, label, tt);
		stepList.add(step);

		// Set Class
		elementClass = VertexEvent.class;
		return this;
	}

	public TraversalEngine ine(final String label, final AC tt) {
		// Check Input element class
		checkInputElementClass(VertexEvent.class);

		// Pipeline Update

		if (isPathEnabled) {
			// Get Sub-Path
			Map intermediate = (Map) stream.map(ve -> {
				VertexEvent cve = (VertexEvent) ve;
				return new AbstractMap.SimpleImmutableEntry(cve, cve.getInVertexEventSet(label, tt));
			}).collect(Collectors.toMap(entry -> ((Entry) entry).getKey(), entry -> ((Entry) entry).getValue()));

			// Update Path
			updateTransformationPath(intermediate);

			// Make stream again
			stream = getStream(intermediate, isParallel);
		} else {
			stream = stream.flatMap(ve -> {
				return ((VertexEvent) ve).getInVertexEventSet(label, tt);
			});
		}
		// Step Update
		final Class[] args = new Class[2];
		args[0] = String.class;
		args[1] = AC.class;
		final Step step = new Step(this.getClass().getName(), "ine", args, label, tt);
		stepList.add(step);

		// Set Class
		elementClass = VertexEvent.class;
		return this;
	}

	public TraversalEngine ine(final BsonArray labels, final TemporalType typeOfVertexEvent, final AC tt, final AC s,
			final AC e, final AC ss, final AC se, final AC es, final AC ee, final Position pos) {
		// Check Input element class
		checkInputElementClass(VertexEvent.class);

		// Pipeline Update

		if (isPathEnabled) {
			// Get Sub-Path
			Map intermediate = (Map) stream.map(ve -> {
				VertexEvent cve = (VertexEvent) ve;
				return new AbstractMap.SimpleImmutableEntry(cve,
						cve.getVertexEventSet(Direction.IN, labels, typeOfVertexEvent, tt, s, e, ss, se, es, ee, pos));
			}).collect(Collectors.toMap(entry -> ((Entry) entry).getKey(), entry -> ((Entry) entry).getValue()));

			// Update Path
			updateTransformationPath(intermediate);

			// Make stream again
			stream = getStream(intermediate, isParallel);
		} else {
			stream = stream.flatMap(ve -> {
				return ((VertexEvent) ve).getVertexEventStream(Direction.IN, labels, typeOfVertexEvent, tt, s, e, ss,
						se, es, ee, pos, isParallel);
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
		elementClass = VertexEvent.class;
		return this;
	}

	// Edge Events to Vertex Events //

	public TraversalEngine inVe() {
		// Check Input element class
		checkInputElementClass(EdgeEvent.class);

		// Pipeline Update

		if (isPathEnabled) {
			// Get Sub-Path
			Map intermediate = (Map) stream.map(ee -> {
				EdgeEvent cee = (EdgeEvent) ee;
				return new AbstractMap.SimpleImmutableEntry(cee, cee.getVertexEvent(Direction.IN));
			}).collect(Collectors.toMap(e -> ((Entry) e).getKey(), e -> ((Entry) e).getValue()));

			// Update Path
			updateTransformationPath(intermediate);

			// Make stream again
			stream = getStream(intermediate, isParallel);
		} else {
			stream = stream.map(ee -> {
				return ((EdgeEvent) ee).getVertexEvent(Direction.IN);
			});
		}
		// Step Update
		final Class[] args = {};
		final Step step = new Step(this.getClass().getName(), "inVe", args);
		stepList.add(step);

		// Set Class
		elementClass = VertexEvent.class;
		return this;
	}

	public TraversalEngine outVe() {
		// Check Input element class
		checkInputElementClass(EdgeEvent.class);

		// Pipeline Update

		if (isPathEnabled) {
			// Get Sub-Path
			Map intermediate = (Map) stream.map(ee -> {
				EdgeEvent cee = (EdgeEvent) ee;
				return new AbstractMap.SimpleImmutableEntry(cee, cee.getVertexEvent(Direction.OUT));
			}).collect(Collectors.toMap(e -> ((Entry) e).getKey(), e -> ((Entry) e).getValue()));

			// Update Path
			updateTransformationPath(intermediate);

			// Make stream again
			stream = getStream(intermediate, isParallel);
		} else {
			stream = stream.map(ee -> {
				return ((EdgeEvent) ee).getVertexEvent(Direction.OUT);
			});
		}

		// Step Update
		final Class[] args = {};
		final Step step = new Step(this.getClass().getName(), "outVe", args);
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
	public TraversalEngine shuffle() {
		// Check Invalid Input element class
		checkInvalidInputElementClass(List.class);

		// Pipeline Update

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
	public TraversalEngine gather() {
		// Check Invalid Input element class
		checkInvalidInputElementClass(List.class);

		// Pipeline Update

		List intermediate = (List) stream.collect(Collectors.toList());

		ArrayList list = new ArrayList();
		list.add(intermediate);

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
	public TraversalEngine scatter() {
		if (elementClass != List.class)
			return this;
		// Check Input element class
		// checkInputElementClass(List.class);

		// Pipeline Update
		if (isPathEnabled) {
			// Get Sub-Path
			List intermediate = (List) stream.flatMap(e -> {
				return ((List) e).parallelStream();
			}).collect(Collectors.toList());

			// Update Path ( Filter if any last elements of each path are not
			// included in intermediate )
			// currentPath.keySet().retainAll(intermediate);

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
	 * @param function
	 *            the transformation function of the pipe
	 * @return the extended Pipeline
	 */
	public TraversalEngine transform(final PipeFunction function, final Class outputClass) {
		// Pipeline Update

		if (isPathEnabled) {
			// Get Sub-Path
			Map intermediate = (Map) stream.map(e -> {

				Object val = function.compute(e);
				if (val == null)
					return null;

				return new AbstractMap.SimpleImmutableEntry(e, val);
			}).filter(e -> e != null).collect(Collectors.toMap(e -> ((Entry) e).getKey(), e -> ((Entry) e).getValue()));

			// Update Path
			if (elementClass != List.class)
				updateTransformationPath(intermediate);

			// Make stream again
			stream = getStream(intermediate, isParallel);
		} else {
			stream = stream.map(e -> {
				return function.compute(e);
			});
			// stream = stream.flatMap(e -> {
			// return function.compute(e);
			// });
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

	public TraversalEngine pathEnabledTransform(final HistoryPipeFunction function, final Class outputClass) {
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

	public TraversalEngine order(final Comparator comparator) {
		// Pipeline Update

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
	public TraversalEngine dedup() {
		// Pipeline Update

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
	 * @param collection
	 *            the collection except from the stream
	 * @return the extended Pipeline
	 */
	public TraversalEngine except(final Collection collection) {
		// Pipeline Update

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
	 * @param collection
	 *            the collection to retain
	 * @return the extended Pipeline
	 */

	public TraversalEngine retain(final Collection collection) {
		// Pipeline Update

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
	 * @param filterFunction
	 *            the filter function of the pipe
	 * @return the extended Pipeline
	 */
	public TraversalEngine filter(final PipeFunction filterFunction) {
		// Pipeline Update

		if (isPathEnabled) {
			List intermediate = (List) stream.filter(e -> (boolean) filterFunction.compute(e))
					.collect(Collectors.toList());

			// Update Path ( Filter if any last elements of each path are not
			// included in intermediate )
			// currentPath.keySet().retainAll(intermediate);

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
	public TraversalEngine elementDedup(FC fc) {
		// Stream Update
		if (isPathEnabled) {

			// Get Sub-Path
			Map intermediate = (Map) stream.map(e -> {

				// System.out.println(((List)e).size());
				Map<String, Set<VertexEvent>> group = (Map<String, Set<VertexEvent>>) ((List) e).parallelStream()
						.collect(Collectors.groupingBy(VertexEvent::getVertexID,
								Collectors.mapping(VertexEvent::getThis, Collectors.toSet())));

				List<VertexEvent> dedup = group.entrySet().parallelStream().map(e1 -> {
					VertexEvent min = e1.getValue().parallelStream().min(Comparator.comparing(ve -> ve.getTimestamp()))
							.get();
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

					System.out.println(((List) e).size());
					Map<String, Set<VertexEvent>> group = (Map<String, Set<VertexEvent>>) ((List) e).parallelStream()
							.collect(Collectors.groupingBy(VertexEvent::getVertexID,
									Collectors.mapping(VertexEvent::getThis, Collectors.toSet())));

					List<VertexEvent> dedup = group.entrySet().parallelStream().map(e1 -> {
						VertexEvent min = e1.getValue().parallelStream()
								.min(Comparator.comparing(ve -> ve.getTimestamp())).get();
						return min;

					}).collect(Collectors.toList());

					System.out.println(dedup.size());
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
	 * @param bias:
	 *            pass if bias > random the bias of the random coin
	 * @return the extended Pipeline
	 */
	public TraversalEngine random(final Double bias) {
		// Pipeline Update
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
	 * @param maxSize
	 *            the high end of the range
	 * @return the extended Pipeline
	 */
	public TraversalEngine range(final int maxSize) {
		// Pipeline Update
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
	public TraversalEngine simplePath() {
		// Pipeline Update
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

	/////////////////////////
	/// SIDE-EFFECT PIPES ///
	/////////////////////////

	/**
	 * Add a SideEffectFunctionPipe to the end of the Pipeline. The provided
	 * function is evaluated and the incoming object is the outgoing object.
	 *
	 * @param sideEffectFunction
	 *            the function of the pipe
	 * @return the extended Pipeline
	 */

	public TraversalEngine sideEffect(final PipeFunction sideEffectFunction) {

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
			if(stream == null) {
				System.out.println();
			}
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
	 * @param ifFunction
	 *            the function denoting the "if" part of the pipe
	 * @param thenFunction
	 *            the function denoting the "then" part of the pipe
	 * @param elseFunction
	 *            the function denoting the "else" part of the pipe
	 * @return the extended Pipeline
	 */

	public TraversalEngine ifThenElse(final PipeFunction ifFunction, final PipeFunction thenFunction,
			final PipeFunction elseFunction) {
		// Pipeline Update

		if (isPathEnabled) {
			// Get Sub-Path
			Map intermediate = (Map) stream.map(element -> {
				if ((boolean) ifFunction.compute(element)) {
					return new AbstractMap.SimpleImmutableEntry(element, thenFunction.compute(element));
				} else {
					return new AbstractMap.SimpleImmutableEntry(element, elseFunction.compute(element));
				}
			}).collect(Collectors.toMap(e -> ((Entry) e).getKey(), e -> ((Entry) e).getValue()));

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

	private TraversalEngine innerLoop(final List<Step> stepList, final LoopPipeFunction whileFunction) {
		// Inner Pipeline Update
		if (isPathEnabled) {
			// previousPath = new HashMap<Object, Object>(currentPath);
			// currentPath.clear();

			List intermediateList = (List) stream.flatMap(e -> {
				Object intermediate = e;

				if ((boolean) whileFunction.compute(intermediate, currentPath, this.loopCount)) {

					TraversalEngine innerPipeline = new TraversalEngine(g, e, isParallel, this.loopCount + 1,
							isPathEnabled, e.getClass(), listElementClass, currentPath);
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

					if (isParallel)
						return innerIntermediate.parallelStream();
					else
						return innerIntermediate.stream();
				} else
					return makeStream(e, isParallel);

			}).collect(Collectors.toList());

			// Check No Path Update
			// if (currentPath.isEmpty())
			// currentPath = new HashMap<Object, Object>(previousPath);

			if (isParallel)
				stream = intermediateList.parallelStream();
			else
				stream = intermediateList.stream();
		} else {
			stream = stream.flatMap(e -> {
				Object intermediate = e;
				if ((boolean) whileFunction.compute(intermediate, null, this.loopCount)) {

					TraversalEngine innerPipeline = new TraversalEngine(g, e, isParallel, this.loopCount + 1,
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
	 * Add a LoopPipe to the end of the Pipeline. Looping is useful for repeating a
	 * section of a pipeline. The provided whileFunction determines when to drop out
	 * of the loop. The whileFunction is provided a LoopBundle object which contains
	 * the object in loop along with other useful metadata.
	 *
	 * @param namedStep
	 *            the name of the step to loop back to
	 * @param whileFunction
	 *            whether or not to continue looping on the current object
	 * @return the extended Pipeline
	 */
	public TraversalEngine loop(final String namedStep, final LoopPipeFunction whileFunction) {
		// Pipeline Update
		int lastStepIdx = stepList.size();

		if (isPathEnabled) {

			// previousPath = new HashMap<Object, Object>(currentPath);
			// currentPath.clear();

			List intermediateList = (List) stream.flatMap(e -> {
				Object intermediate = e;

				if ((boolean) whileFunction.compute(intermediate, currentPath, this.loopCount)) {

					// Create inner pipeline
					TraversalEngine innerPipeline = new TraversalEngine(g, e, isParallel, this.loopCount + 1,
							isPathEnabled, e.getClass(), listElementClass, currentPath);

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
					currentPath = innerPathMap;
					// {urn:epc:id:sgtin:0000001.000001.1-1513000563783=
					// [[urn:epc:id:sgtin:0000001.000001.0-946652400000,
					// urn:epc:id:sgtin:0000001.000001.1-1513000563783]],
					// urn:epc:id:sgtin:0000001.000001.2-1513000563783=
					// [[urn:epc:id:sgtin:0000001.000001.0-946652400000,
					// urn:epc:id:sgtin:0000001.000001.2-1513000563783]]}

					// {urn:epc:id:sgtin:0000001.000001.4-1513000564583=
					// [[urn:epc:id:sgtin:0000001.000001.1-1513000563783,
					// urn:epc:id:sgtin:0000001.000001.4-1513000564583]],
					// urn:epc:id:sgtin:0000001.000001.3-1513000564583=
					// [[urn:epc:id:sgtin:0000001.000001.1-1513000563783,
					// urn:epc:id:sgtin:0000001.000001.3-1513000564583]]}
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

					if (isParallel)
						return innerIntermediate.parallelStream();
					else
						return innerIntermediate.stream();
				} else
					return makeStream(e, isParallel);
			}).collect(Collectors.toList());

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

					TraversalEngine innerPipeline = new TraversalEngine(g, e, isParallel, this.loopCount + 1,
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

	//////////////////////
	/// UTILITY PIPES ///
	//////////////////////

	/**
	 * Wrap the previous step in an AsPipe. Useful for naming steps and is used in
	 * conjunction with various other steps including: loop, select, back, table,
	 * etc.
	 *
	 * @param name
	 *            the name of the AsPipe
	 * @return the extended Pipeline
	 */

	public TraversalEngine as(final String name) {
		// Step Update
		final Class[] args = new Class[1];
		args[0] = String.class;
		final Step step = new Step(this.getClass().getName(), "as", args, name);
		stepList.add(step);

		this.stepIndex.put(name, stepList.indexOf(step));
		return this;
	}

	///////////////////////
	/// UTILITY METHODS ///
	///////////////////////

	/**
	 * Enable path calculations in the pipeline. This is typically handled
	 * automatically and on rare occasions requires an explicit call.
	 * 
	 * @return the path-enabled Pipeline
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

	private TraversalEngine invoke(final List<Step> stepList) {
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

		HashMap<Object, Object> nextPath = new HashMap<Object, Object>();
		// System.out.println("PREV: " + currentPath);

		// intermediate maps source to dest set
		Iterator<Entry> intermediateEntrySet = intermediate.entrySet().iterator();
		while (intermediateEntrySet.hasNext()) {
			Entry entry = intermediateEntrySet.next();
			// 1 or 2
			Object source = entry.getKey();
			Object objectValue = entry.getValue();
			if (objectValue instanceof Set) {
				Set destSet = (Set) objectValue;

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
			if (entry.getKey() == null) {
				if (nextPath.containsKey(null)) {
					HashSet<List> next = (HashSet) nextPath.get(null);
					next.addAll((HashSet<List>) entry.getValue());
				}
			} else {

				if (!intermediate.containsKey(entry.getKey())) {
					nextPath.put(entry.getKey(), entry.getValue());
				}
			}

		}

		currentPath.clear();
		currentPath = new HashMap<Object, Object>(nextPath);
		// System.out.println("NEXT: " + currentPath);
	}

	// Backup
	// private void updateTransformationPath(final Map intermediate) {
	// previousPath = new HashMap<Object, Object>(currentPath);
	// currentPath.clear();
	//
	// Iterator<Entry> intermediateEntrySet = intermediate.entrySet().iterator();
	// while (intermediateEntrySet.hasNext()) {
	// Entry entry = intermediateEntrySet.next();
	// // 1 or 2
	// Object key = entry.getKey();
	// Object objectValue = entry.getValue();
	// if (objectValue instanceof Set) {
	// Set value = (Set) objectValue;
	//
	// Iterator valueIterator = value.iterator();
	// while (valueIterator.hasNext()) {
	// Object val = valueIterator.next();
	// HashSet<List> previousPaths = (HashSet) previousPath.get(key);
	//
	// Iterator<List> previousPathIterator = previousPaths.iterator();
	// while (previousPathIterator.hasNext()) {
	// List previousPath = previousPathIterator.next();
	// if (currentPath.containsKey(val)) {
	// HashSet<List> currentExisting = (HashSet) currentPath.get(val);
	// List clone = new ArrayList(previousPath);
	// clone.add(val);
	// currentExisting.add(clone);
	// currentPath.put(val, currentExisting);
	// } else {
	// HashSet<List> currentNew = new HashSet<List>();
	// List clone = new ArrayList(previousPath);
	// clone.add(val);
	// currentNew.add(clone);
	// currentPath.put(val, currentNew);
	// }
	// }
	// }
	// } else {
	// Object val = entry.getValue();
	//
	// HashSet<List> previousPaths = (HashSet) previousPath.get(key);
	//
	// Iterator<List> previousPathIterator = previousPaths.iterator();
	// while (previousPathIterator.hasNext()) {
	// List previousPath = previousPathIterator.next();
	// if (currentPath.containsKey(val)) {
	// HashSet<List> currentExisting = (HashSet) currentPath.get(val);
	// List clone = new ArrayList(previousPath);
	// clone.add(val);
	// currentExisting.add(clone);
	// currentPath.put(val, currentExisting);
	// } else {
	// HashSet<List> currentNew = new HashSet<List>();
	// List clone = new ArrayList(previousPath);
	// clone.add(val);
	// currentNew.add(clone);
	// currentPath.put(val, currentNew);
	// }
	// }
	// }
	// }
	// previousPath.clear();
	// }

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
