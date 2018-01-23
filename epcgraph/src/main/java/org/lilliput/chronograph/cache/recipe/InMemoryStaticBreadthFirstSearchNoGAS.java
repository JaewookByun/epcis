package org.lilliput.chronograph.cache.recipe;

import java.util.HashSet;
import java.util.Map;

import org.bson.BsonArray;
import org.lilliput.chronograph.cache.CachedChronoGraph;
import org.lilliput.chronograph.cache.CachedChronoVertex;
import org.lilliput.chronograph.cache.CachedVertexEvent;
import org.lilliput.chronograph.cache.engine.CachedTraversalEngine;

import com.tinkerpop.pipes.PipeFunction;

import org.lilliput.chronograph.common.LoopPipeFunction;

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
public class InMemoryStaticBreadthFirstSearchNoGAS {

	private HashSet<CachedChronoVertex> visited = new HashSet<CachedChronoVertex>();

	public HashSet<CachedChronoVertex> compute(CachedChronoGraph g, CachedChronoVertex source, BsonArray labels) {

		visited.add(source);

		PipeFunction<CachedChronoVertex, Boolean> filterFunction = new PipeFunction<CachedChronoVertex, Boolean>() {

			@Override
			public Boolean compute(CachedChronoVertex vertex) {
				if (visited.contains(vertex))
					return false;
				else
					return true;
			}
		};

		PipeFunction<CachedChronoVertex, Object> storeCurrentVertexEvents = new PipeFunction<CachedChronoVertex, Object>() {
			@Override
			public Object compute(CachedChronoVertex vertex) {
				visited.add(vertex);
				return null;
			}
		};

		LoopPipeFunction exitIfEmptyIterator = new LoopPipeFunction() {
			@Override
			public boolean compute(Object argument, Map<Object, Object> currentPath, int loopCount) {
				if (argument == null)
					return false;

				return true;
			}
		};

		CachedTraversalEngine pipeLine = new CachedTraversalEngine(source, true, false, CachedVertexEvent.class);
		pipeLine = pipeLine.as("s");
		pipeLine = pipeLine.out(labels, Integer.MAX_VALUE);
		pipeLine = pipeLine.dedup();
		pipeLine = pipeLine.filter(filterFunction);
		pipeLine = pipeLine.sideEffect(storeCurrentVertexEvents);
		pipeLine = pipeLine.loop("s", exitIfEmptyIterator);
		pipeLine.toList();
		return visited;
	}
}
