package org.lilliput.chronograph.persistent.epcgraph.test.transformation;

import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.oliot.khronos.common.LoopPipeFunction;
import org.oliot.khronos.persistent.ChronoGraph;
import org.oliot.khronos.persistent.ChronoVertex;
import org.oliot.khronos.persistent.engine.TraversalEngine;

public class SimplePathTest {

	@Test
	public void test() {
		ChronoGraph g = new ChronoGraph("test1");
		g.addEdge("0", "1", "c");
		g.addEdge("0", "2", "c");
		g.addEdge("1", "3", "c");
		g.addEdge("1", "4", "c");

		LoopPipeFunction loop = new LoopPipeFunction() {
			@SuppressWarnings("rawtypes")
			@Override
			public boolean compute(Object argument, Map<Object, Object> currentPath, int loopCount) {

				List list = (List) argument;
				// System.out.println(list.size());
				if (list == null || list.size() == 0)
					return false;

				return true;
			}
		};

		TraversalEngine engine = new TraversalEngine(g, g.getChronoVertex("0"), true, true, ChronoVertex.class);
		engine.as("s");
		engine.scatter();
		engine.out(null, Integer.MAX_VALUE);
		engine.gather();
		engine.loop("s", loop).toList();

		System.out.println(engine.path());
		
		g.clear();
		g.shutdown();
	}
}
