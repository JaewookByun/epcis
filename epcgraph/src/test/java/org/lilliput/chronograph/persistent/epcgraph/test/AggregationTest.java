package org.lilliput.chronograph.persistent.epcgraph.test;

import java.sql.Date;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import org.bson.BsonArray;
import org.bson.BsonDouble;
import org.bson.BsonString;
import org.junit.Test;
import org.lilliput.chronograph.common.Tokens.AC;
import org.lilliput.chronograph.persistent.ChronoGraph;
import org.lilliput.chronograph.persistent.EdgeEvent;

import com.google.common.util.concurrent.AtomicDouble;
import com.tinkerpop.blueprints.Direction;

public class AggregationTest {

	// 쿼리1 : 각 중요시간 마다 트럭 안에 있는 물건들의 리스트와 무게를 가져옴 
	// freight, location, vehicle_aggregation 데이터 셋 
	// 운전사가 현재 트럭안의 상태를 알수 있다. 
	@Test
	public void test() {
		ChronoGraph g = new ChronoGraph("epcis");
		// g.getChronoVertexSet().parallelStream().forEach(v -> System.out.println(v));

		BsonArray labels = new BsonArray();
		labels.add(new BsonString("contains"));

		// 각 중요시간 마다 트럭 안에 있는 물건들의 리스트와 무게를 가져옴

		Iterator<Long> timeSet = g.getChronoVertex("urn:epc:id:sscc:0000001.0000000001")
				.getTimestamps(Direction.OUT, labels, 0l, AC.$gt).iterator();

		while (timeSet.hasNext()) {
			Long t = timeSet.next();
			System.out.println("at " + new Date(t));
			AtomicDouble totalK = new AtomicDouble();
			ConcurrentHashMap<String, String> map = new ConcurrentHashMap<String, String>();

			g.getChronoVertex("urn:epc:id:sscc:0000001.0000000001").getChronoEdgeSet(Direction.OUT, labels)
					.parallelStream().forEach(e -> {
						// System.out.println(e);

						EdgeEvent ee = e.pickFloorTimestamp(t);
						if (ee != null && ee.getProperty("action").equals(new BsonString("ADD"))) {
							BsonDouble kg = (BsonDouble) e.getInVertex().getProperty("urn:epc:cbv:mda:netWeight");
							totalK.addAndGet(kg.doubleValue());
							map.put(e.getInVertex().toString(), "1");
						}
					});

			System.out.println(map.keySet());
			System.out.println(totalK);
		}

		g.shutdown();
	}
}
