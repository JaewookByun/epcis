package org.lilliput.chronograph.cache.recipe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.bson.BsonArray;
import org.lilliput.chronograph.cache.CachedChronoEdge;
import org.lilliput.chronograph.cache.CachedChronoGraph;
import org.lilliput.chronograph.cache.CachedChronoVertex;
import org.lilliput.chronograph.cache.CachedEdgeEvent;
import org.lilliput.chronograph.cache.CachedVertexEvent;

import org.lilliput.chronograph.common.TemporalType;
import org.lilliput.chronograph.common.Tokens.AC;
import org.lilliput.chronograph.common.Tokens.Position;

import com.tinkerpop.blueprints.Direction;

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
public class InMemoryPaperBreadthFirstSearch {

	private HashMap<CachedChronoVertex, Long> gamma = new HashMap<CachedChronoVertex, Long>();
	private HashMap<CachedChronoVertex, Long> dist = new HashMap<CachedChronoVertex, Long>();
	private HashMap<CachedChronoVertex, CachedChronoVertex> parent = new HashMap<CachedChronoVertex, CachedChronoVertex>();
	private HashSet<CachedChronoVertex> visited = new HashSet<CachedChronoVertex>();
	private ArrayList<CachedChronoVertex> queue = new ArrayList<CachedChronoVertex>();

	public Set<CachedChronoVertex> compute(CachedChronoGraph g, CachedVertexEvent source, BsonArray labels,
			TemporalType typeOfEvent, AC tt, AC s, AC e, AC ss, AC se, AC es, AC ee, Position pos) {

		// gamma 는 미등록 = infinite
		// dist 는 미등록 = infinite
		// parent 는 미등록 = 부모없음

		// Step 2
		// gamma(s) = ts
		gamma.put(source.getVertex(), source.getTimestamp());
		// dist(s) = 0
		dist.put(source.getVertex(), 0l);
		// parent(s) = null
		parent.put(source.getVertex(), null);
		// push (s, dist(s), gamma(s), parent(s)
		queue.add(source.getVertex());
		// visit source
		visited.add(source.getVertex());

		// Step 3
		while (!queue.isEmpty()) {
			// Step 3.a pop
			CachedChronoVertex cVertex = queue.remove(0);
			// For each out-neighbor v of u
			Iterator<CachedChronoEdge> eIter = cVertex.getChronoEdges(Direction.OUT, null).iterator();
			while (eIter.hasNext()) {
				CachedChronoEdge tEdge = eIter.next();
				CachedChronoVertex tOutV = tEdge.getOutVertex();
				CachedChronoVertex tInV = tEdge.getInVertex();
				TreeSet<Long> timestampSet = tEdge.getTimestamps(gamma.get(cVertex), AC.$gt);
				if (!timestampSet.isEmpty()) {
					// Step 3.b.i, e = (u, v, t) , gamma(u) < t , min (t)
					CachedEdgeEvent tEvent = tEdge.setTimestamp(timestampSet.first());
					
					if (gamma.get(tInV) == null || tEvent.getTimestamp() < gamma.get(tInV)) {
						// visit v
						// push
						dist.put(tInV, dist.get(tOutV) + 1);
						gamma.put(tInV, tEvent.getTimestamp());
						parent.put(tInV, tOutV);
						if(!queue.contains(tInV)) {
							visited.add(tInV);
							queue.add(tInV);
						}

					}
				
				}
			}
			// System.out.println(queue.size() + "\t" + visited.size());
		}

		return visited;
	}
}
