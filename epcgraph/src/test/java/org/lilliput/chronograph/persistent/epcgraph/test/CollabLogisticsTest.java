package org.lilliput.chronograph.persistent.epcgraph.test;

import org.junit.Test;
import org.lilliput.chronograph.persistent.ChronoGraph;

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

		g.getChronoVertexStream("urn:oliot:ubv:mda:gps", -1.1673, 52.93, 500000).forEach(v -> System.out.println(v));

		// g.getChronoVertexSet().parallelStream().forEach(v -> System.out.println(v));

		g.shutdown();
	}
}
