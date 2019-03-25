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
@SuppressWarnings({ "unchecked", "rawtypes", "unused" })
public class TraversalEngine {

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

	private Graph g;

	/**
	 * Initialize TraversalEngine
	 * 
	 * Path management is not supported. If needed, use PathEnabledTraversalEngine
	 * 
	 * @param g
	 * @param starts       of single Element (i.e., Graph, Vertex, Edge,
	 *                     VertexEvent, EdgeEvent) or Collection<Element>
	 * @param elementClass either of Graph.class, Vertex.class, Edge.class,
	 *                     VertexEvent.class, EdgeEvent.class
	 */
	public TraversalEngine(Graph g, Object starts, Class elementClass) {
		// Initialize Stream and Path
		if (starts instanceof Stream) {
			this.elementClass = elementClass;
			stream = (Stream) starts;
		} else if (starts instanceof Element) {
			stream = Stream.of(starts).parallel();
			this.elementClass = starts.getClass();
		}
		stepList = new ArrayList<Step>();
		stepIndex = new HashMap<String, Integer>();
		this.loopCount = 0;
		listElementClass = null;
		this.g = g;
	}

	private TraversalEngine(Graph g, Object starts, int loopCount, Class elementClass, Class listElementClass) {
		// Initialize Stream and Path
		if (starts instanceof Stream) {
			this.elementClass = elementClass;
			stream = (Stream) starts;
		} else if (starts instanceof Collection) {
			this.elementClass = listElementClass;
			stream = ((Collection) starts).parallelStream();
		} else if (starts instanceof Element) {
			HashSet set = new HashSet();
			set.add(starts);
			stream = set.parallelStream();
			this.elementClass = starts.getClass();
		}
		stepList = new ArrayList<Step>();
		stepIndex = new HashMap<String, Integer>();
		this.loopCount = loopCount;
		this.listElementClass = null;
		this.g = g;
	}

	///////////////////////////
	/// TRANSFORM PIPES ///
	//////////////////////////
	/**
	 * Traversers move from Graph to Stream<Vertex> using G.getVertexSet()
	 * 
	 * <Lazy> , Greedy
	 * 
	 * @return TraversalEngine
	 */
	public TraversalEngine V() {
		// Check Input element class
		checkInputElementClass(Graph.class);

		// Pipeline Update
		stream = stream.flatMap(g -> {
			return ((Graph) g).getVertexSet().parallelStream();
		});

		// Step Update
		Class[] args = {};
		Step step = new Step(this.getClass().getName(), "V", args);
		stepList.add(step);

		// Set Class
		elementClass = Vertex.class;
		return this;
	}

	/**
	 * Traversers move from Graph to Stream<Edge> using G.getEdgeSet()
	 * 
	 * <Lazy> , Greedy
	 * 
	 * @return TraversalEngine
	 */
	public TraversalEngine E() {
		// Check Input element class
		checkInputElementClass(Graph.class);

		// Pipeline Update
		stream = stream.flatMap(g -> {
			return ((Graph) g).getEdgeSet().parallelStream();
		});

		// Step Update
		Class[] args = {};
		Step step = new Step(this.getClass().getName(), "E", args);
		stepList.add(step);

		// Set Class
		elementClass = Edge.class;
		return this;
	}

	/**
	 * Traversers move from Graph to Stream<EdgeEvent> using G.getEdgeEventSet()
	 * 
	 * <Lazy> , Greedy
	 * 
	 * @return TraversalEngine
	 */
	public TraversalEngine e() {
		// Check Input element class
		checkInputElementClass(Graph.class);

		// Pipeline Update
		stream = stream.flatMap(g -> {
			return ((Graph) g).getEdgeEventSet().parallelStream();
		});

		// Step Update
		Class[] args = {};
		Step step = new Step(this.getClass().getName(), "e", args);
		stepList.add(step);

		// Set Class
		elementClass = EdgeEvent.class;
		return this;
	}

	/**
	 * Traversers move from Graph to Stream<Vertex> using G.getVertexSet(String key,
	 * BsonValue value)
	 * 
	 * <Lazy> , Greedy
	 * 
	 * @return TraversalEngine
	 */
	public TraversalEngine V(String key, BsonValue value) {
		// Check Input element class
		checkInputElementClass(Graph.class);

		// Pipeline Update
		stream = stream.flatMap(g -> {
			return ((Graph) g).getVertexSet(key, value).parallelStream();
		});

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
	 * Traversers move from Graph to Stream<Edge> using G.getEdgeSet(String key,
	 * BsonValue value)
	 * 
	 * <Lazy> , Greedy
	 * 
	 * @return TraversalEngine
	 */
	public TraversalEngine E(String key, BsonValue value) {
		// Check Input element class
		checkInputElementClass(Graph.class);

		// Pipeline Update
		stream = stream.flatMap(g -> {
			return ((Graph) g).getEdgeSet(key, value).parallelStream();
		});

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
	 * Traversers move from Stream<Vertex> to Stream<Edge> using
	 * Vertex.getEdgeSet(Direction.OUT, Bson String label)
	 * 
	 * <Lazy> , Greedy
	 * 
	 * @return TraversalEngine
	 */
	public TraversalEngine outE(BsonString label) {
		// Check Input element class
		checkInputElementClass(Vertex.class);

		// Pipeline Update
		stream = stream.flatMap(v -> {
			return ((Vertex) v).getEdgeSet(Direction.OUT, label).parallelStream();
		});

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
	 * Traversers move from Stream<Vertex> to Stream<Edge> using
	 * Vertex.getEdgeSet(Direction.IN, BsonString label)
	 * 
	 * <Lazy> , Greedy
	 * 
	 * @return TraversalEngine
	 */
	public TraversalEngine inE(BsonString label) {
		// Check Input element class
		checkInputElementClass(Vertex.class);

		// Pipeline Update
		stream = stream.flatMap(v -> {
			return ((Vertex) v).getEdgeSet(Direction.IN, label).parallelStream();
		});

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
	 * Traversers move from Stream<Edge> to Stream<Vertex> using
	 * Edge.getVertex(Direction.OUT)
	 * 
	 * <Lazy> , Greedy
	 * 
	 * @return TraversalEngine
	 */
	public TraversalEngine outV() {
		// Check Input element class
		checkInputElementClass(Edge.class);

		// Pipeline Update
		stream = stream.map(e -> {
			return ((Edge) e).getVertex(Direction.OUT);
		});

		// Step Update
		Class[] args = {};
		Step step = new Step(this.getClass().getName(), "outV", args);
		stepList.add(step);

		// Set Class
		elementClass = Vertex.class;
		return this;
	}

	/**
	 * Traversers move from Stream<Edge> to Stream<Vertex> using
	 * Edge.getVertex(Direction.IN)
	 * 
	 * <Lazy> , Greedy
	 * 
	 * @return TraversalEngine
	 */
	public TraversalEngine inV() {
		// Check Input element class
		checkInputElementClass(Edge.class);

		// Pipeline Update
		stream = stream.map(e -> {
			return ((Edge) e).getVertex(Direction.IN);
		});

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
	 * Traversers move from Stream<Vertex> to Stream<Vertex> using
	 * Vertex.getVertexSet(Direction.OUT, BsonString label)
	 * 
	 * <Lazy> , Greedy
	 * 
	 * @return TraversalEngine
	 */
	public TraversalEngine out(BsonString label) {
		// Check Input element class
		checkInputElementClass(Vertex.class);

		// Pipeline Update
		stream = stream.flatMap(v -> {
			return ((Vertex) v).getVertexSet(Direction.OUT, label).parallelStream();
		});

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
	 * Traversers move from Stream<Vertex> to Stream<Vertex> using
	 * Vertex.getVertexSet(Direction.IN, Bson String label)
	 * 
	 * <Lazy> , Greedy
	 * 
	 * @return TraversalEngine
	 */
	public TraversalEngine in(BsonString label) {
		// Check Input element class
		checkInputElementClass(Vertex.class);

		// Pipeline Update
		stream = stream.flatMap(v -> {
			return ((Vertex) v).getVertexSet(Direction.IN, label).parallelStream();
		});

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
	/**
	 * Traversers move from Stream<VertexEvent> to Stream<EdgeEvent> using
	 * VertexEvent.getEdgeEvents(Direction.OUT, BsonString label, AC tt, FC minMAx)
	 * 
	 * <Lazy> , Greedy
	 * 
	 * @return TraversalEngine
	 */
	public TraversalEngine outEe(BsonString label, AC tt, FC minMax) {
		// Check Input element class
		checkInputElementClass(VertexEvent.class);

		// Pipeline Update
		stream = stream.flatMap(ve -> {
			return ((VertexEvent) ve).getEdgeEvents(Direction.OUT, label, tt, minMax).parallelStream();
		});

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

	/**
	 * Traversers move from Stream<VertexEvent> to Stream<EdgeEvent> using
	 * VertexEvent.getEdgeEvents(Direction.IN, BsonString label, AC tt, FC minMAx)
	 * 
	 * <Lazy> , Greedy
	 * 
	 * @return TraversalEngine
	 */
	public TraversalEngine inEe(BsonString label, AC tt, FC minMax) {
		// Check Input element class
		checkInputElementClass(VertexEvent.class);

		// Pipeline Update
		stream = stream.flatMap(ve -> {
			return ((VertexEvent) ve).getEdgeEvents(Direction.IN, label, tt, minMax).parallelStream();
		});

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

	/**
	 * Traversers move from Stream<VertexEvent> to Stream<VertexEvent> using
	 * VertexEvent.getVertexEvents(Direction.OUT, BsonString label, AC tt, FC
	 * minMAx)
	 * 
	 * <Lazy> , Greedy
	 * 
	 * @return TraversalEngine
	 */
	public TraversalEngine oute(BsonString label, AC tt, FC minMax) {
		// Check Input element class
		checkInputElementClass(VertexEvent.class);

		// Pipeline Update
		stream = stream.flatMap(ve -> {
			return ((VertexEvent) ve).getVertexEvents(Direction.OUT, label, tt, minMax).parallelStream();
		});

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

	/**
	 * Traversers move from Stream<VertexEvent> to Stream<VertexEvent> using
	 * VertexEvent.getVertexEvents(Direction.IN, BsonString label, AC tt, FC minMAx)
	 * 
	 * <Lazy> , Greedy
	 * 
	 * @return TraversalEngine
	 */
	public TraversalEngine ine(BsonString label, AC tt, FC minMax) {
		// Check Input element class
		checkInputElementClass(VertexEvent.class);

		// Pipeline Update
		stream = stream.flatMap(ve -> {
			return ((VertexEvent) ve).getVertexEvents(Direction.IN, label, tt, minMax).parallelStream();
		});

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

	/**
	 * Traversers move from Stream<EdgeEvent> to Stream<VertexEvent> using
	 * EdgeEvent.getVertexEvents(Direction.IN)
	 * 
	 * <Lazy> , Greedy
	 * 
	 * @return TraversalEngine
	 */
	public TraversalEngine inVe() {
		// Check Input element class
		checkInputElementClass(EdgeEvent.class);

		// Pipeline Update
		stream = stream.map(ee -> {
			return ((EdgeEvent) ee).getVertexEvent(Direction.IN);
		});

		// Step Update
		Class[] args = {};
		Step step = new Step(this.getClass().getName(), "inVe", args);
		stepList.add(step);

		// Set Class
		elementClass = VertexEvent.class;
		return this;
	}

	/**
	 * Traversers move from Stream<EdgeEvent> to Stream<VertexEvent> using
	 * EdgeEvent.getVertexEvent(Direction.OUT)
	 * 
	 * <Lazy> , Greedy
	 * 
	 * @return TraversalEngine
	 */
	public TraversalEngine outVe() {
		// Check Input element class
		checkInputElementClass(EdgeEvent.class);

		// Pipeline Update
		stream = stream.map(ee -> {
			return ((EdgeEvent) ee).getVertexEvent(Direction.OUT);
		});

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
	 * Shuffle traversers
	 * 
	 * Lazy, <Greedy>
	 * 
	 * @return TraversalEngine
	 */
	public TraversalEngine shuffle() {
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
	 * Traversers move from Stream<Element> to Stream<List<Element>>
	 * 
	 * Lazy, <Greedy>
	 * 
	 * @return TraversalEngine
	 */
	public TraversalEngine gather() {
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
	 * Traversers move from Stream<List<Element>> to Stream<Element>
	 * 
	 * <Lazy>, Greedy
	 * 
	 * @return TraversalEngine
	 */
	public TraversalEngine scatter() {
		// Check Input element class		
		if(elementClass == List.class) {
			stream = stream.flatMap(e -> ((List) e).parallelStream());
			// Set Class
			elementClass = listElementClass;
			listElementClass = null;
		}

		// Step Update
		Class[] args = {};
		Step step = new Step(this.getClass().getName(), "scatter", args);
		stepList.add(step);

		return this;
	}

	/////////////// transform ////////////////////////////

	/**
	 * Traversers move according to Function
	 * 
	 * java.util.Function.function implements the mapping from the inputs to outputs
	 * 
	 * outputElementClass should be Element
	 * 
	 * users can remain it as collection by setting isResultCollection as true
	 * 
	 * <Lazy>, Greedy
	 * 
	 * @return TraversalEngine
	 */
	public TraversalEngine transform(Function function, Class outputElementClass, boolean isResultCollection) {
		// Pipeline Update

		if (isResultCollection) {
			stream = stream.flatMap(e -> {
				return ((Collection) function.apply(e)).parallelStream();
			});
			// Set Class
			elementClass = Collection.class;
			listElementClass = outputElementClass;
		} else {
			stream = stream.map(e -> {
				return function.apply(e);
			});
			elementClass = outputElementClass;
		}

		// Step Update
		Class[] args = new Class[3];
		args[0] = Function.class;
		args[1] = Class.class;
		args[2] = boolean.class;
		Step step = new Step(this.getClass().getName(), "transform", args, function, outputElementClass,
				isResultCollection);
		stepList.add(step);
		return this;
	}

	/////////////// order ////////////////////////////

	/**
	 * Sort traversers according to the comparator
	 * 
	 * Lazy, <Greedy>
	 * 
	 * @return TraversalEngine
	 */
	public TraversalEngine order(Comparator comparator) {
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
	 * Deduplicate traversers
	 * 
	 * <Lazy>, Greedy
	 * 
	 * @return TraversalEngine
	 */
	public TraversalEngine dedup() {
		// Pipeline Update
		stream = stream.distinct();

		// Step Update
		Class[] args = {};
		Step step = new Step(this.getClass().getName(), "dedup", args);
		stepList.add(step);
		return this;
	}

	/**
	 * Remove Traversers if its element is in collection
	 * 
	 * <Lazy>, Greedy
	 * 
	 * @return TraversalEngine
	 */
	public TraversalEngine except(Collection collection) {
		// Pipeline Update
		stream = stream.filter(e -> !collection.contains(e));

		// Step Update
		Class[] args = new Class[1];
		args[0] = Collection.class;
		Step step = new Step(this.getClass().getName(), "except", args, collection);
		stepList.add(step);
		return this;
	}

	/**
	 * Retain traversers if its element is in collection
	 * 
	 * <Lazy>, Greedy
	 * 
	 * @return TraversalEngine
	 */
	public TraversalEngine retain(Collection collection) {
		// Pipeline Update
		stream = stream.filter(e -> collection.contains(e));

		// Step Update
		Class[] args = new Class[1];
		args[0] = Collection.class;
		Step step = new Step(this.getClass().getName(), "retain", args, collection);
		stepList.add(step);
		return this;
	}

	/**
	 * Filter traversers according to Function
	 * 
	 * java.util.Function.function decides whether each element of traversers would
	 * be filtered (true) or not (false)
	 * 
	 * <Lazy>, Greedy
	 * 
	 * @return TraversalEngine
	 */
	public TraversalEngine filter(FilterFunction filterFunction) {
		// Pipeline Update
		stream = stream.filter(e -> filterFunction.apply(e));

		// Step Update
		Class[] args = new Class[1];
		args[0] = FilterFunction.class;
		Step step = new Step(this.getClass().getName(), "filter", args, filterFunction);
		stepList.add(step);
		return this;
	}

	public TraversalEngine elementDedup(FC fc) {
		// Stream Update
		// TODO:
		stream = stream.map(e -> {
			if (e instanceof List) {
				// System.out.println(((List) e).size());
				ConcurrentHashMap<BsonString, VertexEvent> minMap = new ConcurrentHashMap<BsonString, VertexEvent>();

				((List) e).parallelStream().forEach(elem -> {
					VertexEvent ve = (VertexEvent) elem;
					if (minMap.containsKey(ve.getVertexID())) {
						if (minMap.get(ve.getVertexID()).getTimestamp().getValue() > ve.getTimestamp().getValue())
							minMap.put(ve.getVertexID(), ve);
					} else
						minMap.put(ve.getVertexID(), ve);
				});

				return new ArrayList(minMap.values());
			} else {
				return e;
			}
		});

		// Step Update
		Class[] args = new Class[1];
		args[0] = FC.class;
		Step step = new Step(this.getClass().getName(), "elementDedup", args, fc);
		stepList.add(step);
		return this;
	}

	/**
	 * Filter traversers randomly
	 * 
	 * Users give double bias value from 0~1
	 * 
	 * if bias > new Random().nextDouble(), filter
	 * 
	 * <Lazy>, Greedy
	 * 
	 * @return TraversalEngine
	 */
	public TraversalEngine random(Double bias) {
		// Pipeline Update
		stream = stream.filter(e -> bias > new Random().nextDouble());

		// Step Update
		Class[] args = new Class[1];
		args[0] = Double.class;
		Step step = new Step(this.getClass().getName(), "random", args, bias);
		stepList.add(step);
		return this;
	}

	/**
	 * Limit the size of traversers
	 * 
	 * <Lazy>, Greedy
	 * 
	 * @return TraversalEngine
	 */
	public TraversalEngine range(int maxSize) {
		// Pipeline Update
		stream = stream.limit(maxSize);

		// Step Update
		Class[] args = new Class[1];
		args[0] = Integer.TYPE;
		Step step = new Step(this.getClass().getName(), "range", args, maxSize);
		stepList.add(step);
		return this;
	}

	/////////////////////////
	/// SIDE-EFFECT PIPES ///
	/////////////////////////

	/**
	 * Conduct additional tasks with current traversers by using sideEffectFunction
	 * 
	 * <Lazy>, Greedy
	 * 
	 * @return TraversalEngine
	 */
	public TraversalEngine sideEffect(SideEffectFunction sideEffectFunction) {
		stream = stream.map(e -> {
			sideEffectFunction.apply(e);
			return e;
		});

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
	 * Conduct thenFunction if ifFunction returns true
	 * 
	 * Conduct elseFunction if ifFunction returns false
	 * 
	 * <Lazy>, Greedy
	 * 
	 * @param ifFunction
	 * @param thenFunction
	 * @param elseFunction
	 * @return
	 */
	public TraversalEngine ifThenElse(FilterFunction ifFunction, Function thenFunction, Function elseFunction) {
		// Pipeline Update
		stream = stream.flatMap(element -> {
			if ((boolean) ifFunction.apply(element)) {
				return makeStream(thenFunction.apply(element));
			} else {
				return makeStream(elseFunction.apply(element));
			}
		});

		// Step Update
		Class[] args = new Class[3];
		args[0] = FilterFunction.class;
		args[1] = Function.class;
		args[2] = Function.class;
		Step step = new Step(this.getClass().getName(), "ifThenElse", args, ifFunction, thenFunction, elseFunction);
		stepList.add(step);
		return this;
	}

	private TraversalEngine innerLoop(List<Step> stepList, LoopFunction whileFunction) {
		// Inner Pipeline Update
		stream = stream.flatMap(e -> {
			Object intermediate = e;
			if ((boolean) whileFunction.apply(intermediate, null, this.loopCount)) {

				TraversalEngine innerPipeline = new TraversalEngine(g, e, this.loopCount + 1, e.getClass(),
						listElementClass);
				for (Object stepObject : stepList) {
					Step step = (Step) stepObject;
					step.setInstance(innerPipeline);
				}
				innerPipeline = innerPipeline.invoke(stepList);
				innerPipeline = innerPipeline.innerLoop(stepList, whileFunction);
				return innerPipeline.toList().parallelStream();

			}
			return makeStream(e);
		});

		return this;
	}

	/**
	 * Repeat steps from namedStep to currentStep until whileFunction returns false
	 * 
	 * @param namedStep
	 * @param whileFunction
	 * @return
	 */
	public TraversalEngine loop(String namedStep, LoopFunction whileFunction) {
		// Pipeline Update
		int lastStepIdx = stepList.size();
		stream = stream.flatMap(e -> {
			Object intermediate = e;
			if ((boolean) whileFunction.apply(intermediate, null, this.loopCount)) {
				Integer backStepIdx = stepIndex.get(namedStep);
				if (backStepIdx == null || (lastStepIdx - (backStepIdx) + 1) < 0)
					return makeStream(e);

				TraversalEngine innerPipeline = new TraversalEngine(g, e, this.loopCount + 1, e.getClass(),
						listElementClass);
				List loopSteps = stepList.subList(backStepIdx + 1, lastStepIdx);
				for (Object stepObject : loopSteps) {
					Step step = (Step) stepObject;
					step.setInstance(innerPipeline);
				}
				innerPipeline = innerPipeline.invoke(loopSteps);
				innerPipeline = innerPipeline.innerLoop(loopSteps, whileFunction);
				return innerPipeline.toList().parallelStream();

			} else
				return makeStream(e);
		});

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
	 * Label current step
	 * 
	 * @param name
	 * @return
	 */
	public TraversalEngine as(String name) {
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
	 * Compute the stream and return the results as List
	 * 
	 * @return
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

	private TraversalEngine invoke(List<Step> stepList) {
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
