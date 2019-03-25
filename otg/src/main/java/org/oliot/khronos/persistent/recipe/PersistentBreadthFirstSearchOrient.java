package org.oliot.khronos.persistent.recipe;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.oliot.epcis.service.query.EPCTime;
import org.oliot.khronos.common.LoopPipeFunction;
import org.oliot.khronos.common.TemporalType;
import org.oliot.khronos.common.Tokens.AC;
import org.oliot.khronos.common.Tokens.FC;
import org.oliot.khronos.common.Tokens.Position;
import org.oliot.khronos.persistent.engine.ExternalTraversalEngine;

import com.tinkerpop.pipes.PipeFunction;

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

public class PersistentBreadthFirstSearchOrient {

	private ConcurrentHashMap<String, Long> gamma = new ConcurrentHashMap<String, Long>();

	@SuppressWarnings("rawtypes")
	public Map compute(String epc, Long startTime, AC tt) {

		// order = forward / backward
		EPCTime source = new EPCTime(epc, startTime);
		gamma.put(epc, startTime);

		PipeFunction<EPCTime, Boolean> exceedBound2 = new PipeFunction<EPCTime, Boolean>() {
			@Override
			public Boolean compute(EPCTime ve) {
				if (gamma.containsKey(ve.epc) && (ve.time >= gamma.get(ve.epc))) {
					return false;
				}
				return true;
			}
		};

		PipeFunction<List<EPCTime>, Object> storeGamma = new PipeFunction<List<EPCTime>, Object>() {
			@Override
			public Object compute(List<EPCTime> vertexEvents) {
				vertexEvents.parallelStream().forEach(ve -> {
					gamma.put(ve.epc, ve.time);
				});
				return null;
			}
		};

		LoopPipeFunction exitIfEmptyIterator = new LoopPipeFunction() {
			@Override
			public boolean compute(Object argument, Map<Object, Object> currentPath, int loopCount) {

				List list = (List) argument;
				// System.out.println(list.size());
				if (list == null || list.size() == 0)
					return false;

				return true;
			}
		};
		// Event Collection
		// ChronoGraph g = new ChronoGraph("epcis");

		// input이 epc 이고 시간이 그 뒤인 output 들과 그 시간을 가져온다

		// collection.find()

		ExternalTraversalEngine pipeLine = new ExternalTraversalEngine(null, source, true, true, String.class);
		pipeLine = pipeLine.as("s");
		pipeLine = pipeLine.scatter();
		pipeLine = pipeLine.oute(null, TemporalType.TIMESTAMP, tt, null, null, null, null, null, null, Position.first);
		pipeLine = pipeLine.filter(exceedBound2);
		pipeLine = pipeLine.gather();
		// 방문한 버텍스 중 최소만을 꼽는다 해당 스텝에서 도달 한 것 중
		pipeLine = pipeLine.elementDedup(FC.$min);
		// lower bound 보다 크면 필터한다.
		// lower bound 가 없는 것은 무한대
		// pipeLine = pipeLine.transform(exceedBound, List.class);
		pipeLine = pipeLine.sideEffect(storeGamma);
		// pipeLine = pipeLine.pathEnabledTransform(historyPipe, List.class);
		// pipeLine.storeTimestamp(bound);
		// pipeLine.pathFilter(bound, );
		// pipeLine = pipeLine.sideEffect(storeCurrentVertexEvents);
		pipeLine = pipeLine.loop("s", exitIfEmptyIterator);
		return pipeLine.path();
	}
}
