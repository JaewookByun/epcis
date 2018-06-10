package org.oliot.khronos.cache.recipe;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import java.util.stream.Collectors;

import org.bson.BsonArray;

import com.tinkerpop.pipes.PipeFunction;

import org.oliot.khronos.cache.CachedChronoGraph;
import org.oliot.khronos.cache.CachedChronoVertex;
import org.oliot.khronos.cache.CachedVertexEvent;
import org.oliot.khronos.cache.engine.CachedTraversalEngine;
import org.oliot.khronos.common.LoopPipeFunction;

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
public class InMemoryStaticBreadthFirstSearch {

	private HashSet<CachedChronoVertex> visited = new HashSet<CachedChronoVertex>();
	private ArrayList<List<CachedChronoVertex>> visitedVertices = new ArrayList<List<CachedChronoVertex>>();

	public ArrayList<List<CachedChronoVertex>> compute(CachedChronoGraph g, CachedChronoVertex source,
			BsonArray labels) {

		visited.add(source);
		ArrayList<CachedChronoVertex> d1 = new ArrayList<CachedChronoVertex>();
		d1.add(source);
		visitedVertices.add(d1);

		PipeFunction<List<CachedChronoVertex>, List<CachedChronoVertex>> distinctAndNonVisited = new PipeFunction<List<CachedChronoVertex>, List<CachedChronoVertex>>() {

			@Override
			public List<CachedChronoVertex> compute(List<CachedChronoVertex> vertices) {
				return vertices.parallelStream().distinct().filter(v -> !visited.contains(v))
						.collect(Collectors.toList());
			}
		};

		PipeFunction<List<CachedChronoVertex>, Object> storeCurrentVertexEvents = new PipeFunction<List<CachedChronoVertex>, Object>() {
			@Override
			public Object compute(List<CachedChronoVertex> vertices) {
				//System.out.println(vertices.size());
				visited.addAll(vertices);
				visitedVertices.add(vertices);
				return vertices;
			}
		};

		LoopPipeFunction exitIfEmptyIterator = new LoopPipeFunction() {
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
		pipeLine = pipeLine.out(labels, Integer.MAX_VALUE);
		pipeLine = pipeLine.gather();
		pipeLine = pipeLine.transform(distinctAndNonVisited, List.class);
		pipeLine = pipeLine.sideEffect(storeCurrentVertexEvents);
		pipeLine = pipeLine.loop("s", exitIfEmptyIterator);
		pipeLine.toList();
		return visitedVertices;
	}
}
