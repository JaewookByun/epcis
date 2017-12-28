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
import java.util.Set;

import org.bson.BsonDocument;
import org.bson.BsonInt32;
import org.junit.Test;
import org.lilliput.chronograph.common.TemporalType;
import org.lilliput.chronograph.common.Tokens.AC;
import org.lilliput.chronograph.common.Tokens.Position;
import org.lilliput.chronograph.persistent.ChronoGraph;
import org.lilliput.chronograph.persistent.VertexEvent;
import org.lilliput.chronograph.persistent.recipe.PersistentBreadthFirstSearch;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

public class TransformationQueryTestInternal {
	public static String fileBaseLoc = "/home/jack/test/";
	// db.edges.createIndex({"_outV" : 1, "_t" : 1, "_inV" : 1})
	// db.EventData.createIndex({"inputEPCList.epc":1})

	public static String baseURL = "http://localhost:8080/epcgraph";
	public int transferCount = 308;
	public int iterationCount = 100;

	@Test
	public void test() throws IOException, InterruptedException {

		File file = new File(fileBaseLoc + this.getClass().getSimpleName() + "-cache-bfs");
		file.createNewFile();
		FileWriter fw = new FileWriter(file);
		BufferedWriter bw = new BufferedWriter(fw);

		String baseEPC = "urn:epc:id:sgtin:0000001.000001.";

		MongoClient client = new MongoClient();
		MongoDatabase db = client.getDatabase("test1");
		db.getCollection("edges").drop();
		db.getCollection("vertices").drop();
		db.getCollection("edges").createIndex(new BsonDocument("_outV", new BsonInt32(1))
				.append("_label", new BsonInt32(1)).append("_t", new BsonInt32(1)).append("_inV", new BsonInt32(1)));
		client.close();

		ChronoGraph g = new ChronoGraph("test1");

		for (int i = 0; i < transferCount; i++) {

			long cTime = System.currentTimeMillis();
			g.addTimestampEdgeProperties(baseEPC + i, baseEPC + (2 * i + 1), "transformTo", cTime, new BsonDocument());
			g.addTimestampEdgeProperties(baseEPC + i, baseEPC + (2 * i + 2), "transformTo", cTime, new BsonDocument());

			Thread.sleep(2000);

			double avg = doTransformationQuery(g);

			System.out.println(i + "\t" + avg);
			bw.write(i + "\t" + avg + "\n");
			bw.flush();
		}
		bw.close();
	}

	public double doTransformationQuery(ChronoGraph g) throws IOException {

		ArrayList<Long> timeList = new ArrayList<Long>();

		String source = "urn:epc:id:sgtin:0000001.000001.0";

		VertexEvent ve = g.getChronoVertex(source).setTimestamp(0l);

		for (int i = 0; i < iterationCount; i++) {

			PersistentBreadthFirstSearch bfs = new PersistentBreadthFirstSearch();

			long pre = System.currentTimeMillis();
			bfs.compute(g, ve, "transformTo", TemporalType.TIMESTAMP, AC.$gt, null, null, null, null, null, null, Position.first,
					"ASC");
			long aft = System.currentTimeMillis();
			long elapsedTime = aft - pre;
			// System.out.println("Elapsed Time: " + elapsedTime);
			timeList.add(elapsedTime);
		}

		double total = timeList.parallelStream().mapToDouble(t -> {
			return t.longValue();
		}).sum();

		return total / iterationCount;
	}
}
