package org.chronograph.khronos.recipe;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.bson.BsonString;
import org.chronograph.khronos.common.FilterFunction;
import org.chronograph.khronos.common.LoopFunction;
import org.chronograph.khronos.common.SideEffectFunction;
import org.chronograph.khronos.common.Tokens.AC;
import org.chronograph.khronos.common.Tokens.FC;
import org.chronograph.khronos.element.Graph;
import org.chronograph.khronos.element.VertexEvent;
import org.chronograph.khronos.traversal.TraversalEngine;

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
public class BreadthFirstSearch {

	@SuppressWarnings("rawtypes")
	public ConcurrentHashMap<String, Long> compute(Graph g, VertexEvent source, AC tt, FC minMax) {

		ConcurrentHashMap<String, Long> lowerBound = new ConcurrentHashMap<String, Long>();

		lowerBound.put(source.getVertexID().getValue(), source.getTimestamp().getValue());

		FilterFunction<VertexEvent> exceedBound = new FilterFunction<VertexEvent>() {
			@Override
			public boolean apply(VertexEvent ve) {
				if (lowerBound.get(ve.getVertexID().getValue()) != null
						&& ve.getTimestamp().getValue() >= lowerBound.get(ve.getVertexID().getValue())) {
					return false;
				}
				return true;
			}
		};

		SideEffectFunction<List<VertexEvent>> storeLowerBound = new SideEffectFunction<List<VertexEvent>>() {

			@Override
			public void apply(List<VertexEvent> list) {
				list.parallelStream().forEach(ve -> {
					lowerBound.put(ve.getVertexID().getValue(), ve.getTimestamp().getValue());
				});
			}
		};

		LoopFunction exitIfEmptyIterator = new LoopFunction() {

			@Override
			public boolean apply(Object argument, Map currentPath, int loopCount) {
				List list = (List) argument;
				// System.out.println(list.size());
				if (list == null || list.size() == 0)
					return false;

				return true;
			}
		};

		TraversalEngine engine = new TraversalEngine(g, source, VertexEvent.class);

		engine = engine.as("s");
		engine = engine.scatter();
		engine = engine.oute(new BsonString("s"), tt, minMax);
		engine = engine.filter(exceedBound);
		engine = engine.gather();
		engine = engine.elementDedup(FC.$min);
		engine = engine.sideEffect(storeLowerBound);
		engine = engine.loop("s", exitIfEmptyIterator);
		engine.toList();
		return lowerBound;
	}
}
