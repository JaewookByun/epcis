package org.lilliput.chronograph.persistent.epcgraph.test.transformation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

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
import java.util.Map;

import org.oliot.khronos.common.TemporalType;
import org.oliot.khronos.common.Tokens.AC;
import org.oliot.khronos.common.Tokens.Position;
import org.oliot.khronos.persistent.recipe.StaticPersistentOrientBreadthFirstSearch;

import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.orient.OrientGraphNoTx;

public class TransformationQueryOrientDBTest {
	public static String fileBaseLoc = "/home/jack/test/";
	// db.edges.createIndex({"_outV" : 1, "_t" : 1, "_inV" : 1})
	// db.EventData.createIndex({"inputEPCList.epc":1})

	public static String baseURL = "http://localhost:8080/epcgraph";
	public int transferCount = 308;
	public int iterationCount = 100;

	public HashMap<String, Vertex> idVMap = new HashMap<String, Vertex>();

	public void test() throws IOException, InterruptedException {

		File file = new File(fileBaseLoc + this.getClass().getSimpleName() + "-cache-bfs");
		file.createNewFile();
		FileWriter fw = new FileWriter(file);
		BufferedWriter bw = new BufferedWriter(fw);

		OrientGraphNoTx graph = new OrientGraphNoTx("plocal:/home/jack/orientrepo/");
		Iterator<Vertex> vIter = graph.getVertices().iterator();
		while (vIter.hasNext()) {
			graph.removeVertex(vIter.next());
		}

		String baseEPC = "urn:epc:id:sgtin:0000001.000001.";

		Vertex source = null;

		for (int i = 0; i < transferCount; i++) {

			long cTime = System.currentTimeMillis();

			Vertex s = null;
			if (idVMap.containsKey(baseEPC + i)) {
				s = idVMap.get(baseEPC + i);
			} else {
				s = graph.addVertex(baseEPC + i);
				s.setProperty("name", baseEPC + i);
				idVMap.put(baseEPC + i, s);
			}

			if (source == null)
				source = s;

			Vertex d1 = null;
			if (idVMap.containsKey(baseEPC + ((2 * i) + 1))) {
				d1 = idVMap.get(baseEPC + ((2 * i) + 1));
			} else {
				d1 = graph.addVertex(baseEPC + ((2 * i) + 1));
				d1.setProperty("name", baseEPC + ((2 * i) + 1));
				idVMap.put(baseEPC + ((2 * i) + 1), d1);
			}

			Vertex d2 = null;
			if (idVMap.containsKey(baseEPC + ((2 * i) + 2))) {
				d2 = idVMap.get(baseEPC + ((2 * i) + 2));
			} else {
				d2 = graph.addVertex(baseEPC + ((2 * i) + 2));
				d2.setProperty("name", baseEPC + ((2 * i) + 2));
				idVMap.put(baseEPC + ((2 * i) + 2), d2);
			}

			Edge e1 = s.addEdge("transformTo", d1);
			e1.setProperty(String.valueOf(cTime), 1);
			Edge e2 = s.addEdge("transformTo", d2);
			e2.setProperty(String.valueOf(cTime), 1);

			// Iterator<Edge> iter = graph.getEdges().iterator();
			// while (iter.hasNext()) {
			// System.out.println(iter.next());
			// }

			Thread.sleep(1000);

			double avg = doTransformationQuery(graph, source);

			System.out.println(i + "\t" + avg);
			bw.write(i + "\t" + avg + "\n");
			bw.flush();
		}
		bw.close();
	}

	@SuppressWarnings({ "unused", "rawtypes" })
	public double doTransformationQuery(OrientGraphNoTx graph, Vertex source) throws IOException {

		ArrayList<Long> timeList = new ArrayList<Long>();

		for (int i = 0; i < iterationCount; i++) {

			long pre = System.currentTimeMillis();
			StaticPersistentOrientBreadthFirstSearch tBFS = new StaticPersistentOrientBreadthFirstSearch();
			Map vSet = tBFS.compute(graph, source, null, TemporalType.TIMESTAMP, AC.$gt, null, null, null, null,
					null, null, Position.first);
			long aft = System.currentTimeMillis();
			long elapsedTime = aft - pre;
			// System.out.println(vSet);
			// System.out.println("Elapsed Time: " + elapsedTime);
			timeList.add(elapsedTime);
		}

		double total = timeList.parallelStream().mapToDouble(t -> {
			return t.longValue();
		}).sum();

		return total / iterationCount;
	}

}
