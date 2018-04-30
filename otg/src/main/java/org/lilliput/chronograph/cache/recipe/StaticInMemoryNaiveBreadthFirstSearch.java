package org.lilliput.chronograph.cache.recipe;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.bson.BsonArray;
import org.lilliput.chronograph.cache.CachedChronoEdge;
import org.lilliput.chronograph.cache.CachedChronoGraph;
import org.lilliput.chronograph.cache.CachedChronoVertex;
import org.lilliput.chronograph.cache.engine.CachedTraversalEngine;

import com.tinkerpop.pipes.PipeFunction;

import org.lilliput.chronograph.common.LoopPipeFunction;
import org.lilliput.chronograph.common.TemporalType;
import org.lilliput.chronograph.common.Tokens.AC;
import org.lilliput.chronograph.common.Tokens.Position;

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
public class StaticInMemoryNaiveBreadthFirstSearch {

	private HashSet<CachedChronoVertex> visited = new HashSet<CachedChronoVertex>();
	private HashMap<CachedChronoVertex, Long> leastTimestamps = new HashMap<CachedChronoVertex, Long>();

	@SuppressWarnings({ "rawtypes" })
	public Set<CachedChronoVertex> compute(CachedChronoGraph g, CachedChronoVertex source, BsonArray labels,
			TemporalType typeOfEvent, AC tt, AC s, AC e, AC ss, AC se, AC es, AC ee, Position pos) {

		visited.add(source);
		leastTimestamps.put(source, 0l);

		// outv 의 타임스탬프를 긁어 현재 timestamp 중 크거나 같은 요소 얻어 (inV, timestamp) 로 저장
		PipeFunction<CachedChronoEdge, Object> storeNextLeastTimestamps = new PipeFunction<CachedChronoEdge, Object>() {
			@Override
			public Object compute(CachedChronoEdge edge) {
				// 엣지의 모든 Timestamp를 얻어와 그것보다 큰 것만 추린다
				Predicate<Long> p = t -> {
					CachedChronoVertex outV = edge.getOutVertex();
					if (leastTimestamps.get(outV) < t)
						return true;
					return false;
				};
				Set<Long> longSet = edge.getProperties().keySet().stream().map(t -> Long.parseLong(t)).filter(p)
						.collect(Collectors.toSet());
				// 그 중 가장 최소의 값을 얻어
				if (longSet.isEmpty()) {
					return null;
				}
				Long first = (Long) new TreeSet<Long>(longSet).first();
				if (leastTimestamps.containsKey(edge.getInVertex())) {
					Long prevT = leastTimestamps.get(edge.getInVertex());
					if (first < prevT)
						leastTimestamps.put(edge.getInVertex(), first);
				} else {
					leastTimestamps.put(edge.getInVertex(), first);
				}
				return null;
			}
		};

		PipeFunction<CachedChronoEdge, Boolean> filterIfNextEventNotExist = new PipeFunction<CachedChronoEdge, Boolean>() {
			public Boolean compute(CachedChronoEdge edge) {
				if (leastTimestamps.containsKey(edge.getInVertex())) {
					return true;
				} else {
					return false;
				}
			}
		};

		PipeFunction<List<CachedChronoVertex>, Object> storeCurrentVertexEvents = new PipeFunction<List<CachedChronoVertex>, Object>() {
			@Override
			public Object compute(List<CachedChronoVertex> vertices) {
				visited.addAll(vertices);
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

		CachedTraversalEngine engine = new CachedTraversalEngine(source, false, false, CachedChronoVertex.class);
		engine = engine.as("s");
		engine = engine.scatter();
		// Edge에 Timestamp 정보가 있기 때문에 방문 해야함
		engine = engine.outE(labels, Integer.MAX_VALUE);
		// outv 의 타임스탬프를 긁어 현재 timestamp 중 크거나 같은 요소 얻어 (inV, timestamp) 로 저장
		engine = engine.sideEffect(storeNextLeastTimestamps);
		// 해당하는 inV가 없으면 필터
		engine = engine.filter(filterIfNextEventNotExist);
		engine = engine.inV();
		engine = engine.dedup();
		engine = engine.except(visited);
		engine = engine.gather();
		engine = engine.sideEffect(storeCurrentVertexEvents);
		engine = engine.loop("s", exitIfEmptyIterator);
		engine.toList();
		return visited;
	}
}