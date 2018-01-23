package org.oliot.epcis.converter.mongodb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.bson.BsonArray;
import org.bson.BsonBoolean;
import org.bson.BsonDateTime;
import org.bson.BsonDocument;
import org.bson.BsonString;
import org.lilliput.chronograph.cache.CachedChronoEdge;
import org.lilliput.chronograph.cache.CachedChronoGraph;
import org.lilliput.chronograph.cache.CachedChronoVertex;
import org.lilliput.chronograph.persistent.ChronoGraph;
import org.oliot.epcis.configuration.Configuration;
import org.oliot.model.epcis.AggregationEventType;

import com.tinkerpop.blueprints.Direction;

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

public class GraphReadConverter {

	public ArrayList<Object> convert(CachedChronoGraph g, Long eventTime) {

		ArrayList<Object> convertedEvents = new ArrayList<Object>();

		// Aggregation을 처리
		Set<CachedChronoEdge> aggrEdgeSet = g.getEdges("contains");

		HashMap<CachedChronoVertex, BsonDocument> parentSet = new HashMap<CachedChronoVertex, BsonDocument>();
		Iterator<CachedChronoEdge> eIter = aggrEdgeSet.iterator();
		while (eIter.hasNext()) {
			CachedChronoEdge e = eIter.next();
			parentSet.put(e.getOutVertex(), e.getProperties());
		}

		Iterator<Entry<CachedChronoVertex, BsonDocument>> parentIter = parentSet.entrySet().iterator();
		while (parentIter.hasNext()) {
			Entry<CachedChronoVertex, BsonDocument> entry = parentIter.next();

			CachedChronoVertex parent = entry.getKey();
			BsonDocument base = entry.getValue();
			base.put("eventTime", new BsonDateTime(eventTime));
			base.put("parentID", new BsonString(parent.toString()));

			ArrayList<BsonDocument> childList = new ArrayList<BsonDocument>();
			ArrayList<BsonDocument> childQuantityList = new ArrayList<BsonDocument>();

			Iterator<CachedChronoVertex> childIter = parent
					.getChronoVertexSet(Direction.OUT, "contains", Integer.MAX_VALUE).iterator();
			while (childIter.hasNext()) {
				CachedChronoVertex child = childIter.next();

				ChronoGraph persistentG = Configuration.persistentGraph;
				BsonDocument prop = persistentG.getChronoVertex(child.toString()).getTimestampProperties(eventTime);

				if (prop == null || !prop.containsKey("quantity"))
					childList.add(new BsonDocument("epc", new BsonString(child.toString())));
				else {
					BsonDocument childQuantity = new BsonDocument();
					childQuantity.put("epcClass", new BsonString(child.toString()));
					childQuantity.put("quantity", prop.getDouble("quantity"));
					if (prop.containsKey("uom")) {
						childQuantity.put("uom", prop.getString("uom"));
					}
				}
			}
			base.put("childEPCs", new BsonArray(childList));

			Iterator<CachedChronoEdge> li = parent.getChronoEdgeSet(Direction.OUT, "isLocatedIn", Integer.MAX_VALUE)
					.iterator();
			while (li.hasNext()) {
				CachedChronoEdge locEdge = li.next();
				CachedChronoVertex loc = locEdge.getInVertex();

				if (((BsonBoolean) locEdge.getProperty("isReadPoint")).getValue()) {
					base.put("readPoint", new BsonDocument("id", new BsonString(loc.toString())));
				} else
					base.put("bizLocation", new BsonDocument("id", new BsonString(loc.toString())));
			}

			BsonDocument extension = new BsonDocument();
			if (base.containsKey("extension")) {
				extension = base.getDocument("extension");
			}
			if (!childQuantityList.isEmpty()) {
				extension.put("childQuantityList", new BsonArray(childQuantityList));
			}

			BsonArray source = new BsonArray();
			BsonArray destination = new BsonArray();

			Iterator<CachedChronoEdge> ownerIter = parent.getChronoEdgeSet(Direction.OUT, "isOwned", Integer.MAX_VALUE)
					.iterator();
			while (ownerIter.hasNext()) {
				CachedChronoEdge owner = ownerIter.next();
				String action = ((BsonString) owner.getProperty("action")).getValue();
				if (action.equals("DELETE")) {
					source.add(
							new BsonDocument("urn:epcglobal:cbv:sdt:owning_party", new BsonString(owner.toString())));
				} else
					destination.add(
							new BsonDocument("urn:epcglobal:cbv:sdt:owning_party", new BsonString(owner.toString())));
			}

			Iterator<CachedChronoEdge> possessorIter = parent
					.getChronoEdgeSet(Direction.OUT, "isPossessed", Integer.MAX_VALUE).iterator();
			while (possessorIter.hasNext()) {
				CachedChronoEdge possessor = possessorIter.next();
				String action = ((BsonString) possessor.getProperty("action")).getValue();
				if (action.equals("DELETE")) {
					source.add(new BsonDocument("urn:epcglobal:cbv:sdt:possessing_party",
							new BsonString(possessor.toString())));
				} else
					destination.add(new BsonDocument("urn:epcglobal:cbv:sdt:possessing_party",
							new BsonString(possessor.toString())));
			}
			if (!source.isEmpty())
				extension.put("sourceList", source);
			if (!destination.isEmpty())
				extension.put("destinationList", destination);

			base.put("extension", extension);

			AggregationEventReadConverter ae = new AggregationEventReadConverter();
			AggregationEventType aggregationEvent = ae.convert(base);
			convertedEvents.add(aggregationEvent);

			System.out.println("Aggregation added");
		}

		// Transformation을 처리
		Set<CachedChronoEdge> transEdgeSet = g.getEdges("transformsTo");
		Map<BsonDocument, List<CachedChronoEdge>> groupByDoc = transEdgeSet.parallelStream().map(e -> {
			BsonDocument doc = e.getProperties();
			doc.remove("_id");
			doc.remove("_o");
			doc.remove("_i");
			doc.remove("_l");
			doc.remove("_t");
			return e;
		}).collect(Collectors.groupingBy(CachedChronoEdge::getProperties));

		System.out.println(groupByDoc);
		
		// Object/Transaction 처리

		return convertedEvents;
	}
}
