package org.lilliput.chronograph.persistent.epcgraph.test.transformation;

/**
 * Copyright (C) 2014-2016 Jaewook Byun
 *
 * This project is part of Oliot open source (http://oliot.org). Oliot EPCIS
 * v1.2.x is Java Web Service complying with Electronic Product Code Information
 * Service (EPCIS) v1.2.
 *
 * @author Jaewook Byun, Ph.D student
 * 
 *         Korea Advanced Institute of Science and Technology (KAIST)
 * 
 *         Real-time Embedded System Laboratory(RESL)
 * 
 *         bjw0829@kaist.ac.kr, bjw0829@gmail.com
 */

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bson.BsonArray;
import org.bson.BsonString;
import org.json.JSONArray;
import org.oliot.epcis.service.query.EPCTime;
import org.oliot.epcis.service.query.TimeUtil;
import org.oliot.khronos.common.Tokens.AC;
import org.oliot.khronos.persistent.recipe.PersistentBreadthFirstSearchExternal;
import org.springframework.http.HttpHeaders;

public class TransformationAAExisting {

	// db.edges.createIndex({"_outV" : 1, "_t" : 1, "_inV" : 1})
	// db.EventData.createIndex({"inputEPCList.epc":1})

	@SuppressWarnings("unused")
	public void doTransformationQuery() throws IOException {

		ArrayList<Long> timeList = new ArrayList<Long>();

		String source = "urn:epc:id:sgtin:0000001.000001.0";
		String startTime = "2000-12-09T16:17:05.765Z";

		int loopCount = 100;

		for (int i = 0; i < loopCount; i++) {
			long pre = System.currentTimeMillis();
			JSONArray arr = getTransformationTreeEmulation(source, startTime);
			long aft = System.currentTimeMillis();
			long elapsedTime = aft - pre;
			// System.out.println(arr.toString(2));
			System.out.println("Elapsed Time: " + elapsedTime);
			timeList.add(elapsedTime);
		}

		double total = timeList.parallelStream().mapToDouble(t -> {
			return t.longValue();
		}).sum();
		System.out.println(total / loopCount);

		// 10: 144.3
		// 100: 888.55
		// 1000: 7441.24

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private JSONArray getTransformationTreeEmulation(String epc, String startTime) {
		// 여기에 에뮬레이션을 함

		// Time processing
		long startTimeMil = 0;
		startTimeMil = TimeUtil.getTimeMil(startTime);

		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.add("Content-Type", "application/json; charset=utf-8");

		BsonArray transforms = new BsonArray();
		transforms.add(new BsonString("transformsTo"));

		PersistentBreadthFirstSearchExternal tBFS = new PersistentBreadthFirstSearchExternal();
		Map pathMap = new HashMap();
		pathMap = tBFS.compute(epc, startTimeMil, AC.$gte);

		// JSONarray contains each path
		// contains time - vertex mapping

		JSONArray pathArray = new JSONArray();

		Iterator<Set> pathSetIter = pathMap.values().iterator();
		while (pathSetIter.hasNext()) {
			Set pathSet = pathSetIter.next();
			Iterator<List> pathIter = pathSet.iterator();
			while (pathIter.hasNext()) {
				List path = pathIter.next();
				Iterator<EPCTime> vi = path.iterator();
				JSONArray p = new JSONArray();
				while (vi.hasNext()) {
					EPCTime ve = vi.next();
					p.put(ve.epc + "-" + ve.time);
				}
				pathArray.put(p);
			}
		}
		return pathArray;
	}
}
