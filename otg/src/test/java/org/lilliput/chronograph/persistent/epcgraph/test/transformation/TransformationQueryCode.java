package org.lilliput.chronograph.persistent.epcgraph.test.transformation;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.lilliput.chronograph.common.LoopPipeFunction;
import org.lilliput.chronograph.common.Tokens.AC;
import org.lilliput.chronograph.common.Tokens.FC;
import org.lilliput.chronograph.persistent.ChronoGraph;
import org.lilliput.chronograph.persistent.ChronoVertex;
import org.lilliput.chronograph.persistent.VertexEvent;
import org.lilliput.chronograph.persistent.engine.TraversalEngine;

import com.tinkerpop.pipes.PipeFunction;

@SuppressWarnings("rawtypes")
public class TransformationQueryCode {

	public VertexEvent source = null;
	public String label = null;

	public Object compute() {
		ConcurrentHashMap<ChronoVertex, Long> gamma = new ConcurrentHashMap<ChronoVertex, Long>();
		ChronoGraph g = new ChronoGraph();
		gamma.put(source.getVertex(), source.getTimestamp());
		PipeFunction<VertexEvent, Boolean> exceedBound2 = new PipeFunction<VertexEvent, Boolean>() {
			@Override
			public Boolean compute(VertexEvent ve) {
				if (gamma.containsKey(ve.getVertex()) && (ve.getTimestamp() >= gamma.get(ve.getVertex()))) {
					return false;
				}
				return true;
			}
		};
		PipeFunction<List<VertexEvent>, Object> storeGamma = new PipeFunction<List<VertexEvent>, Object>() {
			@Override
			public Object compute(List<VertexEvent> vertexEvents) {
				vertexEvents.parallelStream().forEach(ve -> {
					gamma.put(ve.getVertex(), ve.getTimestamp());
				});
				return null;
			}
		};
		LoopPipeFunction exitIfEmptyIterator = new LoopPipeFunction() {
			@Override
			public boolean compute(Object argument, Map<Object, Object> currentPath, int loopCount) {
				List list = (List) argument;
				if (list == null || list.size() == 0)
					return false;

				return true;
			}
		};
		return new TraversalEngine(g, source, false, true, VertexEvent.class).as("s").scatter().oute(label, AC.$gt)
				.filter(exceedBound2).gather().elementDedup(FC.$min).sideEffect(storeGamma)
				.loop("s", exitIfEmptyIterator).path();
	}
}
