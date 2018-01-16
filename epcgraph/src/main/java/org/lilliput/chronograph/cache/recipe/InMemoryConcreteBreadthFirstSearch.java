package org.lilliput.chronograph.cache.recipe;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.bson.BsonArray;
import org.lilliput.chronograph.cache.CachedChronoGraph;
import org.lilliput.chronograph.cache.CachedChronoVertex;
import org.lilliput.chronograph.cache.CachedVertexEvent;
import org.lilliput.chronograph.cache.engine.CachedTraversalEngine;

import org.lilliput.chronograph.common.LoopPipeFunction;
import org.lilliput.chronograph.common.TemporalType;
import org.lilliput.chronograph.common.Tokens.AC;
import org.lilliput.chronograph.common.Tokens.FC;
import org.lilliput.chronograph.common.Tokens.Position;

import com.tinkerpop.pipes.PipeFunction;

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
public class InMemoryConcreteBreadthFirstSearch {

	private ConcurrentHashMap<CachedChronoVertex, Long> lowerBound = new ConcurrentHashMap<CachedChronoVertex, Long>();

	@SuppressWarnings("rawtypes")
	public Map compute(CachedChronoGraph g, CachedVertexEvent source, BsonArray labels, TemporalType typeOfEvent, AC tt,
			AC s, AC e, AC ss, AC se, AC es, AC ee, Position pos) {

		lowerBound.put(source.getVertex(), source.getTimestamp());

		PipeFunction<CachedVertexEvent, Boolean> exceedBound2 = new PipeFunction<CachedVertexEvent, Boolean>() {
			@Override
			public Boolean compute(CachedVertexEvent ve) {
				if (lowerBound.containsKey(ve.getVertex()) && (ve.getTimestamp() >= lowerBound.get(ve.getVertex()))) {
					return false;
				}
				return true;
			}
		};

		// PipeFunction<List<CachedVertexEvent>, List<CachedVertexEvent>> exceedBound =
		// new PipeFunction<List<CachedVertexEvent>, List<CachedVertexEvent>>() {
		// @Override
		// public List<CachedVertexEvent> compute(List<CachedVertexEvent> vertexEvents)
		// {
		// List<CachedVertexEvent> list = vertexEvents.parallelStream().filter(ve -> {
		// if (lowerBound.containsKey(ve.getVertex())
		// && (ve.getTimestamp() >= lowerBound.get(ve.getVertex()))) {
		// return false;
		// }
		// return true;
		// }).collect(Collectors.toList());
		// return list;
		// }
		// };

		PipeFunction<List<CachedVertexEvent>, Object> storeLowerBound = new PipeFunction<List<CachedVertexEvent>, Object>() {
			@Override
			public Object compute(List<CachedVertexEvent> vertexEvents) {
				vertexEvents.parallelStream().forEach(ve -> {
					lowerBound.put(ve.getVertex(), ve.getTimestamp());
				});
				return null;
			}
		};

		LoopPipeFunction exitIfEmptyIterator = new LoopPipeFunction() {
			@Override
			public boolean compute(Object argument, Map<Object, Object> currentPath, int loopCount) {

				List list = (List) argument;
				// System.out.println(list.size());
				if (list == null || list.size() == 0)
					return false;

				return true;
			}
		};

		CachedTraversalEngine pipeLine = new CachedTraversalEngine(source, false, false, CachedVertexEvent.class);
		pipeLine = pipeLine.as("s");
		pipeLine = pipeLine.scatter();
		pipeLine = pipeLine.oute(labels, typeOfEvent, tt, s, e, ss, se, es, ee, pos);
		pipeLine = pipeLine.filter(exceedBound2);
		pipeLine = pipeLine.gather();
		// 방문한 버텍스 중 최소만을 꼽는다 해당 스텝에서 도달 한 것 중
		pipeLine = pipeLine.elementDedup(FC.$min);
		// lower bound 보다 크면 필터한다.
		// lower bound 가 없는 것은 무한대
		// pipeLine = pipeLine.transform(exceedBound, List.class);
		pipeLine = pipeLine.sideEffect(storeLowerBound);
		// pipeLine = pipeLine.pathEnabledTransform(historyPipe, List.class);
		// pipeLine.storeTimestamp(bound);
		// pipeLine.pathFilter(bound, );
		// pipeLine = pipeLine.sideEffect(storeCurrentVertexEvents);
		pipeLine = pipeLine.loop("s", exitIfEmptyIterator);
		return pipeLine.path();
		// pipeLine.toList();
		// return lowerBound.keySet();
	}
}
