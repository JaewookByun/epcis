package org.oliot.khronos.cache.recipe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bson.BsonArray;
import org.oliot.khronos.cache.CachedChronoGraph;
import org.oliot.khronos.cache.CachedChronoVertex;
import org.oliot.khronos.cache.CachedVertexEvent;
import org.oliot.khronos.cache.engine.CachedTraversalEngine;
import org.oliot.khronos.common.HistoryPipeFunction;
import org.oliot.khronos.common.LoopPipeFunction;
import org.oliot.khronos.common.TemporalType;
import org.oliot.khronos.common.Tokens.AC;
import org.oliot.khronos.common.Tokens.FC;
import org.oliot.khronos.common.Tokens.Position;

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
public class InMemoryConcreteBreadthFirstSearchPath {

	private HashMap<CachedChronoVertex, Long> lowerBound = new HashMap<CachedChronoVertex, Long>();

	public Set<CachedChronoVertex> compute(CachedChronoGraph g, CachedVertexEvent source, BsonArray labels,
			TemporalType typeOfEvent, AC tt, AC s, AC e, AC ss, AC se, AC es, AC ee, Position pos) {

		HistoryPipeFunction historyPipe = new HistoryPipeFunction() {

			@SuppressWarnings("rawtypes")
			@Override
			public Object compute(Object argument, Map<Object, Object> currentPath) {

				ArrayList<CachedVertexEvent> filteredEvent = new ArrayList<CachedVertexEvent>();

				List current = (List) argument;
				for (Object elem : current) {
					CachedVertexEvent ve = (CachedVertexEvent) elem;

					HashSet set = (HashSet) currentPath.get(ve);
					// 안에는 ArrayList가 있음
					boolean isPass = true;

					Iterator iter = set.iterator();
					while (iter.hasNext()) {
						Object e1 = iter.next();
						List x = (List) e1;
						for (Object e2 : x) {
							CachedVertexEvent tve = (CachedVertexEvent) e2;
							if (lowerBound.containsKey(tve.getVertex())) {
								Long prevT = lowerBound.get(tve.getVertex());
								if (tve.getTimestamp() > prevT)
									isPass = false;
							}
						}
					}

					if (isPass == true) {
						filteredEvent.add(ve);
						lowerBound.put(ve.getVertex(), ve.getTimestamp());
					}

				}
				// System.out.println(lowerBound.size());
				return filteredEvent;
			}
		};

		LoopPipeFunction exitIfEmptyIterator = new LoopPipeFunction() {
			@SuppressWarnings("rawtypes")
			@Override
			public boolean compute(Object argument, Map<Object, Object> currentPath, int loopCount) {

				List list = (List) argument;
				if (list == null || list.size() == 0)
					return false;

				return true;
			}
		};

		CachedTraversalEngine pipeLine = new CachedTraversalEngine(source, true, true, CachedVertexEvent.class);
		pipeLine = pipeLine.as("s");
		pipeLine = pipeLine.scatter();
		pipeLine = pipeLine.oute(labels, typeOfEvent, tt, s, e, ss, se, es, ee, pos);
		pipeLine = pipeLine.gather();
		// 방문한 버텍스 중 최소만을 꼽는다 해당 스텝에서 도달 한 것 중
		pipeLine = pipeLine.elementDedup(FC.$min);
		pipeLine = pipeLine.pathEnabledTransform(historyPipe, List.class);
		// pipeLine.storeTimestamp(bound);
		// pipeLine.pathFilter(bound, );
		// pipeLine = pipeLine.sideEffect(storeCurrentVertexEvents);
		pipeLine = pipeLine.loop("s", exitIfEmptyIterator);
		pipeLine.toList();
		return lowerBound.keySet();
	}
}
