package org.lilliput.chronograph.cache.recipe;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

import java.util.stream.Collectors;

import org.bson.BsonArray;
import org.lilliput.chronograph.cache.CachedChronoGraph;
import org.lilliput.chronograph.cache.CachedChronoVertex;
import org.lilliput.chronograph.cache.CachedVertexEvent;
import org.lilliput.chronograph.cache.engine.CachedTraversalEngine;

import com.tinkerpop.pipes.PipeFunction;

import org.lilliput.chronograph.common.LoopPipeFunction;
import org.lilliput.chronograph.common.TemporalType;
import org.lilliput.chronograph.common.Tokens.AC;
import org.lilliput.chronograph.common.Tokens.FC;
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
public class InMemoryNaiveBreadthFirstSearch {

	private HashSet<CachedChronoVertex> visited = new HashSet<CachedChronoVertex>();

	public HashSet<CachedChronoVertex> compute(CachedChronoGraph g, CachedVertexEvent source, BsonArray labels,
			TemporalType typeOfEvent, AC tt, AC s, AC e, AC ss, AC se, AC es, AC ee, Position pos) {

		visited.add(source.getVertex());

		PipeFunction<List<CachedVertexEvent>, Object> storeVisitedVertices = new PipeFunction<List<CachedVertexEvent>, Object>() {
			@Override
			public Object compute(List<CachedVertexEvent> vertexEvents) {
				List<CachedChronoVertex> list = vertexEvents.parallelStream().map(e -> {
					return e.getVertex();
				}).collect(Collectors.toList());

				visited.addAll(list);
				return vertexEvents;
			}
		};

		LoopPipeFunction exitIfTraverserEmpty = new LoopPipeFunction() {
			@SuppressWarnings("rawtypes")
			@Override
			public boolean compute(Object argument, Map<Object, Object> currentPath, int loopCount) {
				if (argument == null || ((List) argument).isEmpty())
					return false;

				return true;
			}
		};

		CachedTraversalEngine pipeLine = new CachedTraversalEngine(source, true, false, CachedVertexEvent.class);
		pipeLine = pipeLine.as("s");
		pipeLine = pipeLine.scatter();
		pipeLine = pipeLine.oute(labels, typeOfEvent, tt, s, e, ss, se, es, ee, pos);
		pipeLine = pipeLine.gather();
		pipeLine = pipeLine.elementExcept(visited);
		pipeLine = pipeLine.elementDedup(FC.$min);
		pipeLine = pipeLine.sideEffect(storeVisitedVertices);
		pipeLine = pipeLine.loop("s", exitIfTraverserEmpty);
		pipeLine.toList();
		return visited;
	}
}
