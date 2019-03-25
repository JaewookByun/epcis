package org.chronograph.khronos.common;

import java.util.HashMap;
import java.util.Iterator;

import org.bson.BsonDateTime;
import org.bson.BsonDocument;
import org.bson.BsonInt32;
import org.bson.BsonObjectId;
import org.bson.BsonString;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.FindOneAndReplaceOptions;

/**
 * Copyright (C) 2016-2017 Jaewook Byun
 * 
 * ChronoGraph: Temporal Property Graph and Traversal Language
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
public class Converter {

	public static BsonObjectId getID(MongoCollection<BsonDocument> ids, BsonString id) {
		BsonDocument doc = ids.findOneAndReplace(new BsonDocument(Tokens.UID, id), new BsonDocument(Tokens.UID, id),
				new FindOneAndReplaceOptions().upsert(true));
		if(doc == null)
			return ids.find(new BsonDocument(Tokens.UID, id)).first().getObjectId(Tokens.ID);
		return doc.getObjectId(Tokens.ID);
	}

	public static BsonString getRealID(MongoCollection<BsonDocument> ids, BsonObjectId id) {
		BsonDocument doc = ids.find(new BsonDocument(Tokens.ID, id)).first();
		return doc.getString(Tokens.UID);
	}

	public static BsonDocument makeVertexDocument(BsonDocument base, BsonString id) {
		if (base == null)
			base = new BsonDocument();
		base.put(Tokens.ID, id);
		return base;
	}

	public static BsonDocument makeVertexDocument(BsonDocument base, BsonObjectId id) {
		if (base == null)
			base = new BsonDocument();
		base.put(Tokens.ID, id);
		return base;
	}

	public static BsonDocument makeEdgeDocument(BsonDocument base, BsonString outVID, BsonString label,
			BsonString inVID) {
		if (base == null)
			base = new BsonDocument();
		base.put(Tokens.OUT_VERTEX, outVID);
		base.put(Tokens.LABEL, label);
		base.put(Tokens.IN_VERTEX, inVID);
		return base;
	}

	public static BsonDocument makeEdgeDocument(BsonDocument base, BsonObjectId outVID, BsonObjectId label,
			BsonObjectId inVID) {
		if (base == null)
			base = new BsonDocument();
		base.put(Tokens.OUT_VERTEX, outVID);
		base.put(Tokens.LABEL, label);
		base.put(Tokens.IN_VERTEX, inVID);
		return base;
	}

	public static BsonDocument removeEdgeKeys(BsonDocument base) {
		base.remove(Tokens.OUT_VERTEX);
		base.remove(Tokens.LABEL);
		base.remove(Tokens.IN_VERTEX);
		base.remove(Tokens.ID);
		return base;
	}

	public static BsonDocument makeIndexParamterDocument(HashMap<String, Boolean> indexParams) {
		BsonDocument index = new BsonDocument();

		Iterator<String> iterator = indexParams.keySet().iterator();
		while (iterator.hasNext()) {
			String key = iterator.next();
			Boolean value = indexParams.get(key);
			index.append(key, (value ? new BsonInt32(1) : new BsonInt32(-1)));
		}

		return index;
	}

	public static BsonDocument makeVertexEventDocument(BsonDocument base, BsonDateTime t, BsonString vertexID) {
		if (base == null)
			base = new BsonDocument();
		base.put(Tokens.VERTEX, vertexID);
		base.put(Tokens.TIMESTAMP, t);
		return base;
	}

	public static BsonDocument makeVertexEventDocument(BsonDocument base, BsonDateTime t, BsonObjectId vertexID) {
		if (base == null)
			base = new BsonDocument();
		base.put(Tokens.VERTEX, vertexID);
		base.put(Tokens.TIMESTAMP, t);
		return base;
	}

	public static BsonDocument makeEdgeEventDocument(BsonDocument base, BsonDateTime t, BsonString outVertex,
			BsonString label, BsonString inVertex) {
		if (base == null)
			base = new BsonDocument();

		base.put(Tokens.TIMESTAMP, t);
		base.put(Tokens.OUT_VERTEX, outVertex);
		base.put(Tokens.LABEL, label);
		base.put(Tokens.IN_VERTEX, inVertex);

		return base;
	}

	public static BsonDocument makeEdgeEventDocument(BsonDocument base, BsonDateTime t, BsonObjectId outVertex,
			BsonObjectId label, BsonObjectId inVertex) {
		if (base == null)
			base = new BsonDocument();

		base.put(Tokens.TIMESTAMP, t);
		base.put(Tokens.OUT_VERTEX, outVertex);
		base.put(Tokens.LABEL, label);
		base.put(Tokens.IN_VERTEX, inVertex);

		return base;
	}

	public static BsonDocument addTimeComparison(BsonDocument base, BsonDateTime t, Tokens.AC tt) {
		base.put(Tokens.TIMESTAMP, new BsonDocument(tt.name(), t));
		return base;
	}

//	public static BsonDocument getTraversalDocument(Graph cg) {
//		BsonArray arr = new BsonArray();
//		arr.add(new BsonString(cg.getID()));
//		return new BsonDocument(Tokens.ID, arr);
//	}
//
//	public static BsonDocument getTraversalDocument(Vertex cv) {
//		BsonArray arr = new BsonArray();
//		arr.add(new BsonString(cv.toString()));
//		return new BsonDocument(Tokens.ID, arr);
//	}
//
//	public static BsonDocument getTraversalDocument(Edge ce) {
////		BsonArray idArr = new BsonArray();
////		idArr.add(new BsonString(ce.toString()));
////		BsonArray outVArr = new BsonArray();
////		outVArr.add(new BsonString(ce.getOutVertex().toString()));
////		BsonArray inVArr = new BsonArray();
////		inVArr.add(new BsonString(ce.getInVertex().toString()));
////		BsonArray labelArr = new BsonArray();
////		labelArr.add(new BsonString(ce.getLabel()));
////
////		return new BsonDocument(Tokens.ID, idArr).append(Tokens.OUT_VERTEX, outVArr).append(Tokens.IN_VERTEX, inVArr)
////				.append(Tokens.LABEL, labelArr);
//		return null;
//	}
//
//	@SuppressWarnings({ "rawtypes" })
//	public static BsonDocument getTraversalDocument(Collection collection, Class elementClass) {
//
////		BsonArray idArr = new BsonArray();
////		BsonArray outVArr = new BsonArray();
////		BsonArray inVArr = new BsonArray();
////		BsonArray labelArr = new BsonArray();
////		BsonArray edgeArr = new BsonArray();
////
////		Iterator iterator = collection.iterator();
////		while (iterator.hasNext()) {
////			if (elementClass == Vertex.class) {
////				Vertex cv = (Vertex) iterator.next();
////				idArr.add(new BsonString(cv.toString()));
////			} else if (elementClass == Edge.class) {
////				Edge ce = (Edge) iterator.next();
////				idArr.add(new BsonString(ce.toString()));
////				outVArr.add(new BsonString(ce.getOutVertex().toString()));
////				inVArr.add(new BsonString(ce.getInVertex().toString()));
////				labelArr.add(new BsonString(ce.getLabel().toString()));
////			} else if (elementClass == VertexEvent.class) {
////				VertexEvent cve = (VertexEvent) iterator.next();
////				idArr.add(new BsonString(cve.toString()));
////			} else if (elementClass == EdgeEvent.class) {
////				EdgeEvent cee = (EdgeEvent) iterator.next();
////				idArr.add(new BsonString(cee.toString()));
////				edgeArr.add(new BsonString(cee.getEdge().toString()));
////
////			}
////		}
////
////		if (elementClass == KhronosVertex.class) {
////			return new BsonDocument(Tokens.ID, idArr);
////		} else if (elementClass == KhronosEdge.class) {
////			return new BsonDocument(Tokens.ID, idArr).append(Tokens.OUT_VERTEX, outVArr)
////					.append(Tokens.IN_VERTEX, inVArr).append(Tokens.LABEL, labelArr);
////		}
//
//		return null;
//	}
//
//	public static BsonDocument getBaseVertexDocument(String id) {
//		BsonDocument base = new BsonDocument();
//		base.put(Tokens.ID, new BsonString(id));
//		base.put(Tokens.TYPE, Tokens.TYPE_STATIC);
//		return base;
//	}
//
//	public static BsonDocument getBaseEdgeDocument(String id, String outVID, String inVID, String label) {
//		BsonDocument base = new BsonDocument();
//		base.put(Tokens.ID, new BsonString(id));
//		base.put(Tokens.OUT_VERTEX, new BsonString(outVID));
//		base.put(Tokens.IN_VERTEX, new BsonString(inVID));
//		base.put(Tokens.LABEL, new BsonString(label));
//		base.put(Tokens.TYPE, Tokens.TYPE_STATIC);
//		return base;
//	}
//
//	/**
//	 * Retrieve only temporal properties from document
//	 * 
//	 * @param bson
//	 * @return
//	 */
//	public static BsonDocument retrieveTemporalProperties(BsonDocument bson) {
//		bson.remove(Tokens.ID);
//		bson.remove(Tokens.EDGE);
//		bson.remove(Tokens.VERTEX);
//		bson.remove(Tokens.TIMESTAMP);
//		bson.remove(Tokens.INTERVAL);
//		bson.remove(Tokens.TYPE);
//		return bson;
//	}
//

//
//	public static BsonDocument getBaseTimestampEdgeEventDocument(String eID, long timestamp) {
//		BsonDocument base = new BsonDocument();
//		base.put(Tokens.ID, new BsonString(eID + "-" + timestamp));
//		base.put(Tokens.EDGE, new BsonString(eID));
//		base.put(Tokens.TIMESTAMP, new BsonDateTime(timestamp));
//		base.put(Tokens.TYPE, Tokens.TYPE_TIMESTAMP);
//		return base;
//	}
//

//
//	public static BsonDocument makeTimestampVertexEventDocumentWithoutID(BsonDocument base, String vID,
//			long timestamp) {
//		if (base == null)
//			base = new BsonDocument();
//		base.put(Tokens.VERTEX, new BsonString(vID));
//		base.put(Tokens.TIMESTAMP, new BsonDateTime(timestamp));
//		return base;
//	}
//
//	public static BsonDocument makeTimestampEdgeEventDocument(BsonDocument base, String eID, long timestamp) {
//		if (base == null)
//			base = new BsonDocument();
//
//		String[] arr = eID.split("\\|");
//		String outV = arr[0];
//		String inV = arr[2];
//		String label = arr[1];
//		base.put(Tokens.ID, new BsonString(eID + "-" + timestamp));
//		base.put(Tokens.OUT_VERTEX, new BsonString(outV));
//		base.put(Tokens.LABEL, new BsonString(label));
//		base.put(Tokens.IN_VERTEX, new BsonString(inV));
//		base.put(Tokens.EDGE, new BsonString(eID));
//		base.put(Tokens.TIMESTAMP, new BsonDateTime(timestamp));
//		base.put(Tokens.TYPE, Tokens.TYPE_TIMESTAMP);
//		return base;
//	}
//
//	public static BsonDocument makeTimestampEdgeEventDocumentWithoutID(BsonDocument base, String eID, long timestamp) {
//		if (base == null)
//			base = new BsonDocument();
//		String[] arr = eID.split("\\|");
//		String outV = arr[0];
//		String inV = arr[2];
//		String label = arr[1];
//		base.put(Tokens.OUT_VERTEX, new BsonString(outV));
//		base.put(Tokens.LABEL, new BsonString(label));
//		base.put(Tokens.IN_VERTEX, new BsonString(inV));
//		base.put(Tokens.TIMESTAMP, new BsonDateTime(timestamp));
//		return base;
//	}
//
//	public static BsonArray getBsonArrayOfBsonString(String... strings) {
//		BsonArray ret = new BsonArray();
//		for (String string : strings) {
//			ret.add(new BsonString(string));
//		}
//		return ret;
//	}
//
//	public static BsonDocument getBsonDocumentEdge(String outVertexID, String inVertexID, String label) {
//		BsonDocument docEdge = new BsonDocument();
//		docEdge.put("_id", new BsonString(getEdgeID(outVertexID, label, inVertexID)));
//		docEdge.put("_outV", new BsonString(outVertexID));
//		docEdge.put("_inV", new BsonString(inVertexID));
//		docEdge.put("_label", new BsonString(label));
//		return docEdge;
//	}
//
//	/**
//	 * Return an identifier of edge
//	 * 
//	 * @param outVertexID: an identifier of out-vertex
//	 * @param edgeLabel: edge label
//	 * @param inVertexID: an identifier of in-vertex
//	 * @return an identifier of edge
//	 */
//	public static String getEdgeID(String outVertexID, String edgeLabel, String inVertexID) {
//		return outVertexID + "|" + edgeLabel + "|" + inVertexID;
//	}
//
//	public static String getReversedEdgeID(BsonDocument edgeDocument) {
//		return edgeDocument.getString("_inV").getValue() + "|" + edgeDocument.getString("_label").getValue() + "|"
//				+ edgeDocument.getString("_outV").getValue();
//	}
//
//	public static BsonDocument getEdgeDocument(String outVertexID, String edgeLabel, String inVertexID) {
//		if (edgeLabel == null)
//			throw ExceptionFactory.edgeLabelCanNotBeNull();
//		String edgeID = Converter.getEdgeID(outVertexID, edgeLabel, inVertexID);
//		BsonDocument e = new BsonDocument();
//		e.put("_id", new BsonString(edgeID));
//		e.put("_outV", new BsonString(outVertexID));
//		e.put("_inV", new BsonString(inVertexID));
//		e.put("_label", new BsonString(edgeLabel));
//		return e;
//	}
//
//	/**
//	 * Return an identifier of edge
//	 * 
//	 * @param outVertexID: an identifier of out-vertex
//	 * @param edgeLabel: edge label
//	 * @param inVertexID: an identifier of in-vertex
//	 * @return an identifier of edge
//	 */
//	public static String getEdgeID(Vertex outVertex, String edgeLabel, Vertex inVertex) {
//		return outVertex.getID() + "|" + edgeLabel + "|" + inVertex.getID();
//	}
//
//	public static BsonArray stringArrayToBsonArray(String[] stringArray) {
//		BsonArray bson = new BsonArray();
//		for (String string : stringArray) {
//			bson.add(new BsonString(string));
//		}
//		return bson;
//	}
//

}
