package org.lilliput.chronograph.persistent.epcgraph.test;

import java.util.HashSet;
import java.util.stream.Collectors;

import org.bson.BsonArray;
import org.bson.BsonString;
import org.junit.Test;
import org.lilliput.chronograph.common.TemporalType;
import org.lilliput.chronograph.common.Tokens.AC;
import org.lilliput.chronograph.common.Tokens.Position;
import org.lilliput.chronograph.persistent.ChronoGraph;
import org.lilliput.chronograph.persistent.ChronoVertex;
import org.lilliput.chronograph.persistent.EdgeEvent;
import org.lilliput.chronograph.persistent.engine.TraversalEngine;

import com.tinkerpop.pipes.PipeFunction;

public class CollabLogisticsTest {

	// 쿼리2 : loc2의 근방 ~ 에서 loc3의 근방 ~ 로 가는
	// 소유권이 이전되고 아직 대기중인 물건을 검색하여 Collaborative logistics를 이룸

	// 2dsphere indexes
	// db.vertices.createIndex({"urn:oliot:ubv:mda:gps" : "2dsphere"})

	// Query
	// db.vertices.find({ "urn:oliot:ubv:mda:gps" : { $near : { $geometry: { type:
	// "Point", coordinates: [ -1.1673,52.93]}, $maxDistance: 50000}}})

	@Test
	public void test() {
		ChronoGraph g = new ChronoGraph("epcis");

		// Depots within 5 km from source
		System.out.println("Depots near source");
		HashSet<ChronoVertex> sources = g.getChronoVertexSet("urn:oliot:ubv:mda:gps", -1.627516, 53.752503, 5000);
		// Depots within 5 km from destination
		System.out.println("Depots near destination");
		HashSet<String> destinations = (HashSet<String>) g
				.getChronoVertexSet("urn:oliot:ubv:mda:gps", -1.246341, 53.427623, 5000).parallelStream()
				.map(v -> v.toString()).collect(Collectors.toSet());

		HashSet<ChronoVertex> freightSet = new HashSet<ChronoVertex>();

		BsonArray isLocatedIn = new BsonArray();
		isLocatedIn.add(new BsonString("isLocatedIn"));

		BsonArray isPossessed = new BsonArray();
		isPossessed.add(new BsonString("isPossessed"));

		BsonArray contains = new BsonArray();
		contains.add(new BsonString("contains"));

		PipeFunction<EdgeEvent, Boolean> retainIfAdd = new PipeFunction<EdgeEvent, Boolean>() {
			@Override
			public Boolean compute(EdgeEvent ve) {
				Object act = ve.getProperty("action");
				if (act == null)
					return false;
				BsonString action = (BsonString) act;
				if (action.getValue().equals("ADD"))
					return true;
				else
					return false;
			}
		};

		PipeFunction<EdgeEvent, Boolean> filterIfDestinationNotClosed = new PipeFunction<EdgeEvent, Boolean>() {
			@Override
			public Boolean compute(EdgeEvent ee) {
				ChronoVertex v = ee.getEdge().getInVertex();
				if (destinations.contains(v.toString()))
					return true;
				else
					return false;
			}
		};

		PipeFunction<EdgeEvent, Object> storeCandidates = new PipeFunction<EdgeEvent, Object>() {
			@Override
			public Object compute(EdgeEvent ee) {
				freightSet.add(ee.getEdge().getOutVertex());
				return ee;
			}
		};

		PipeFunction<EdgeEvent, Object> removeIfAlreadyLoaded = new PipeFunction<EdgeEvent, Object>() {
			@Override
			public Object compute(EdgeEvent ee) {
				freightSet.remove(ee.getEdge().getInVertex());
				return ee;
			}
		};

		TraversalEngine engine = new TraversalEngine(g, sources.stream(), false, false, ChronoVertex.class);
		// get freights near source
		engine = engine.toEvent(1497016800000l).ine(isLocatedIn, TemporalType.TIMESTAMP, AC.$lte, null, null, null,
				null, null, null, Position.last);
		// get freights to go near destination
		engine = engine.outEe(isPossessed, TemporalType.TIMESTAMP, AC.$gte, null, null, null, null, null, null)
				.filter(retainIfAdd).filter(filterIfDestinationNotClosed).sideEffect(storeCandidates);
		// filter if candidates are alrady loaded
		engine.outVe().inEe(contains, TemporalType.TIMESTAMP, AC.$gte, null, null, null, null, null, null)
				.sideEffect(removeIfAlreadyLoaded).toList();

		System.out.println(freightSet);

		// Iterator<ChronoVertex> sIter = sources.iterator();
		// while (sIter.hasNext()) {
		// ChronoVertex source = sIter.next();
		// VertexEvent st = source.setTimestamp(1497016800000l);
		// st.getVertexEventSet(Direction.IN, labels2, TemporalType.TIMESTAMP, AC.$lte,
		// null, null, null, null, null,
		// null, Position.last);
		//
		// // ee)e
		// // TreeSet<Long> tt = source.getTimestamps(Direction.IN, labels, 0l,
		// AC.$gte);
		// // 1497013200000
		// // 1497016800000, 1497103200000
		// // System.out.println(tt);
		// // Iterator<ChronoEdge> candidateIter = source.getChronoEdgeSet(Direction.IN,
		// // labels, Integer.MAX_VALUE).iterator();
		// // while(candidateIter.hasNext()) {
		// // ChronoEdge e = candidateIter.next();
		// // }
		// }
		// loc contains objects possessed by one of destination depots
		// and not in_progress

		// g.getChronoVertexSet().parallelStream().forEach(v -> System.out.println(v));

		g.shutdown();
	}
}
