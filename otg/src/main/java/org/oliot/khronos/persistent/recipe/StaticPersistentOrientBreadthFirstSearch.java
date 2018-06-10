package org.oliot.khronos.persistent.recipe;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.bson.BsonArray;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;
import com.tinkerpop.pipes.PipeFunction;

import org.oliot.khronos.common.LoopPipeFunction;
import org.oliot.khronos.common.TemporalType;
import org.oliot.khronos.common.Tokens.AC;
import org.oliot.khronos.common.Tokens.Position;
import org.oliot.khronos.persistent.engine.OrientTraversalEngine;

/**
 * Copyright (C) 2016-2017 Jaewook Byun
 * 
 * Temporal Property Graph Recipes
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
public class StaticPersistentOrientBreadthFirstSearch {

	private HashMap<Vertex, Long> leastTimestamps = new HashMap<Vertex, Long>();
	private HashMap<Vertex, Long> lowerBound = new HashMap<Vertex, Long>();

	@SuppressWarnings({ "rawtypes" })
	public Map compute(OrientGraphNoTx g, Vertex source, BsonArray labels, TemporalType typeOfEvent, AC tt, AC s, AC e,
			AC ss, AC se, AC es, AC ee, Position pos) {

		leastTimestamps.put(source, 0l);
		lowerBound.put(source, 0l);

		// outv 의 타임스탬프를 긁어 현재 timestamp 중 크거나 같은 요소 얻어 (inV, timestamp) 로 저장
		PipeFunction<Edge, Object> storeNextLeastTimestamps = new PipeFunction<Edge, Object>() {
			@Override
			public Object compute(Edge edge) {
				// 엣지의 모든 Timestamp를 얻어와 그것보다 큰 것만 추린다
				Predicate<Long> p = t -> {
					Vertex outV = edge.getVertex(Direction.OUT);
					if (leastTimestamps.get(outV) < t)
						return true;
					return false;
				};
				Set<Long> longSet = edge.getPropertyKeys().stream().map(t -> Long.parseLong(t)).filter(p)
						.collect(Collectors.toSet());
				// 그 중 가장 최소의 값을 얻어
				if (longSet.isEmpty()) {
					return null;
				}
				Long first = (Long) new TreeSet<Long>(longSet).first();
				if (leastTimestamps.containsKey(edge.getVertex(Direction.IN))) {
					Long prevT = leastTimestamps.get(edge.getVertex(Direction.IN));
					if (first < prevT)
						leastTimestamps.put(edge.getVertex(Direction.IN), first);
				} else {
					leastTimestamps.put(edge.getVertex(Direction.IN), first);
				}
				return null;
			}
		};

		PipeFunction<Edge, Boolean> filterIfNextEventNotExist = new PipeFunction<Edge, Boolean>() {
			public Boolean compute(Edge edge) {
				if (leastTimestamps.containsKey(edge.getVertex(Direction.IN))) {
					return true;
				} else {
					return false;
				}
			}
		};

		PipeFunction<List<Vertex>, List<Vertex>> exceedBound = new PipeFunction<List<Vertex>, List<Vertex>>() {
			@Override
			public List<Vertex> compute(List<Vertex> vertices) {
				List<Vertex> list = vertices.stream().filter(v -> {
					if (lowerBound.containsKey(v) && (leastTimestamps.get(v) >= lowerBound.get(v))) {
						return false;
					}
					return true;
				}).collect(Collectors.toList());
				return list;
			}
		};

		PipeFunction<List<Vertex>, Object> storeLowerBound = new PipeFunction<List<Vertex>, Object>() {
			@Override
			public Object compute(List<Vertex> vertices) {
				vertices.stream().forEach(v -> {
					lowerBound.put(v, leastTimestamps.get(v));
				});

				// System.out.println(lowerBound.keySet().size());

				return null;
			}
		};

		LoopPipeFunction exitIfEmptyIterator = new LoopPipeFunction() {
			@Override
			public boolean compute(Object argument, Map<Object, Object> currentPath, int loopCount) {
				if (argument == null || ((List) argument).isEmpty())
					return false;

				return true;
			}
		};

		// GremlinFluentPipeline line = new GremlinPipeline<>(source, true);
		// line.as("s");
		// line.scatter();
		// line.outE("transformTo");
		// line.sideEffect(storeNextLeastTimestamps);
		// line.filter(filterIfNextEventNotExist);
		// line.inV();
		// line.dedup();
		// line.gather();
		// line.transform(exceedBound);
		// line.sideEffect(storeLowerBound);
		// line.loop("s", exitIfEmptyIterator);
		// line.path();

		OrientTraversalEngine engine = new OrientTraversalEngine(g, source, false, true, Vertex.class);
		engine = engine.as("s");
		engine = engine.scatter();
		// Edge에 Timestamp 정보가 있기 때문에 방문 해야함
		engine = engine.outE(labels, Integer.MAX_VALUE);
		// System.out.println(engine.path());
		// outv 의 타임스탬프를 긁어 현재 timestamp 중 크거나 같은 요소 얻어 (inV, timestamp) 로 저장
		engine = engine.sideEffect(storeNextLeastTimestamps);
		// 해당하는 inV가 없으면 필터
		engine = engine.filter(filterIfNextEventNotExist);
		engine = engine.inV();
		// System.out.println(engine.path());
		engine = engine.dedup();
		// engine = engine.except(visited);
		engine = engine.gather();
		engine = engine.transform(exceedBound, List.class);
		engine = engine.sideEffect(storeLowerBound);
		engine = engine.loop("s", exitIfEmptyIterator);

		return engine.path();
		// return lowerBound.keySet();
	}
}