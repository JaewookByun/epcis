package org.lilliput.chronograph.persistent.util;

import java.util.Collection;
import java.util.Iterator;

import org.bson.BsonArray;
import org.bson.BsonDateTime;
import org.bson.BsonDocument;
import org.bson.BsonInt32;
import org.bson.BsonString;
import org.lilliput.chronograph.common.ExceptionFactory;
import org.lilliput.chronograph.common.LongInterval;
import org.lilliput.chronograph.common.Tokens;
import org.lilliput.chronograph.persistent.ChronoEdge;
import org.lilliput.chronograph.persistent.ChronoGraph;
import org.lilliput.chronograph.persistent.ChronoVertex;
import org.lilliput.chronograph.persistent.EdgeEvent;
import org.lilliput.chronograph.persistent.VertexEvent;

import com.tinkerpop.blueprints.Parameter;
import com.tinkerpop.blueprints.Vertex;

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

	public static BsonDocument getTraversalDocument(ChronoGraph cg) {
		BsonArray arr = new BsonArray();
		arr.add(new BsonString(cg.getID()));
		return new BsonDocument(Tokens.ID, arr);
	}

	public static BsonDocument getTraversalDocument(ChronoVertex cv) {
		BsonArray arr = new BsonArray();
		arr.add(new BsonString(cv.toString()));
		return new BsonDocument(Tokens.ID, arr);
	}

	public static BsonDocument getTraversalDocument(ChronoEdge ce) {
		BsonArray idArr = new BsonArray();
		idArr.add(new BsonString(ce.toString()));
		BsonArray outVArr = new BsonArray();
		outVArr.add(new BsonString(ce.getOutVertex().toString()));
		BsonArray inVArr = new BsonArray();
		inVArr.add(new BsonString(ce.getInVertex().toString()));
		BsonArray labelArr = new BsonArray();
		labelArr.add(new BsonString(ce.getLabel()));

		return new BsonDocument(Tokens.ID, idArr).append(Tokens.OUT_VERTEX, outVArr).append(Tokens.IN_VERTEX, inVArr)
				.append(Tokens.LABEL, labelArr);
	}

	@SuppressWarnings({ "rawtypes" })
	public static BsonDocument getTraversalDocument(Collection collection, Class elementClass) {

		BsonArray idArr = new BsonArray();
		BsonArray outVArr = new BsonArray();
		BsonArray inVArr = new BsonArray();
		BsonArray labelArr = new BsonArray();
		BsonArray edgeArr = new BsonArray();

		Iterator iterator = collection.iterator();
		while (iterator.hasNext()) {
			if (elementClass == ChronoVertex.class) {
				ChronoVertex cv = (ChronoVertex) iterator.next();
				idArr.add(new BsonString(cv.toString()));
			} else if (elementClass == ChronoEdge.class) {
				ChronoEdge ce = (ChronoEdge) iterator.next();
				idArr.add(new BsonString(ce.toString()));
				outVArr.add(new BsonString(ce.getOutVertex().toString()));
				inVArr.add(new BsonString(ce.getInVertex().toString()));
				labelArr.add(new BsonString(ce.getLabel().toString()));
			} else if (elementClass == VertexEvent.class) {
				VertexEvent cve = (VertexEvent) iterator.next();
				idArr.add(new BsonString(cve.toString()));
			} else if (elementClass == EdgeEvent.class) {
				EdgeEvent cee = (EdgeEvent) iterator.next();
				idArr.add(new BsonString(cee.toString()));
				edgeArr.add(new BsonString(cee.getEdge().toString()));

			}
		}

		if (elementClass == ChronoVertex.class) {
			return new BsonDocument(Tokens.ID, idArr);
		} else if (elementClass == ChronoEdge.class) {
			return new BsonDocument(Tokens.ID, idArr).append(Tokens.OUT_VERTEX, outVArr)
					.append(Tokens.IN_VERTEX, inVArr).append(Tokens.LABEL, labelArr);
		}

		return null;
	}

	public static BsonDocument getBaseVertexDocument(String id) {
		BsonDocument base = new BsonDocument();
		base.put(Tokens.ID, new BsonString(id));
		base.put(Tokens.TYPE, Tokens.TYPE_STATIC);
		return base;
	}

	public static BsonDocument getBaseEdgeDocument(String id, String outVID, String inVID, String label) {
		BsonDocument base = new BsonDocument();
		base.put(Tokens.ID, new BsonString(id));
		base.put(Tokens.OUT_VERTEX, new BsonString(outVID));
		base.put(Tokens.IN_VERTEX, new BsonString(inVID));
		base.put(Tokens.LABEL, new BsonString(label));
		base.put(Tokens.TYPE, Tokens.TYPE_STATIC);
		return base;
	}

	/**
	 * Retrieve only temporal properties from document
	 * 
	 * @param bson
	 * @return
	 */
	public static BsonDocument retrieveTemporalProperties(BsonDocument bson) {
		bson.remove(Tokens.ID);
		bson.remove(Tokens.EDGE);
		bson.remove(Tokens.VERTEX);
		bson.remove(Tokens.TIMESTAMP);
		bson.remove(Tokens.INTERVAL);
		bson.remove(Tokens.TYPE);
		return bson;
	}

	public static BsonDocument makeVertexDocument(BsonDocument base, String id) {
		if (base == null)
			base = new BsonDocument();
		base.put(Tokens.ID, new BsonString(id));
		base.put(Tokens.TYPE, Tokens.TYPE_STATIC);
		return base;
	}

	public static BsonDocument makeEdgeDocument(BsonDocument base, String id, String outVID, String inVID,
			String label) {
		if (base == null)
			base = new BsonDocument();
		base.put(Tokens.ID, new BsonString(id));
		base.put(Tokens.OUT_VERTEX, new BsonString(outVID));
		base.put(Tokens.IN_VERTEX, new BsonString(inVID));
		base.put(Tokens.LABEL, new BsonString(label));
		base.put(Tokens.TYPE, Tokens.TYPE_STATIC);
		return base;
	}

	public static BsonDocument getBaseTimestampVertexEventDocument(String vID, long timestamp) {
		BsonDocument base = new BsonDocument();
		base.put(Tokens.ID, new BsonString(vID + "-" + timestamp));
		base.put(Tokens.VERTEX, new BsonString(vID));
		base.put(Tokens.TIMESTAMP, new BsonDateTime(timestamp));
		base.put(Tokens.TYPE, Tokens.TYPE_TIMESTAMP);
		return base;
	}

	public static BsonDocument getBaseTimestampEdgeEventDocument(String eID, long timestamp) {
		BsonDocument base = new BsonDocument();
		base.put(Tokens.ID, new BsonString(eID + "-" + timestamp));
		base.put(Tokens.EDGE, new BsonString(eID));
		base.put(Tokens.TIMESTAMP, new BsonDateTime(timestamp));
		base.put(Tokens.TYPE, Tokens.TYPE_TIMESTAMP);
		return base;
	}

	public static BsonDocument makeTimestampVertexEventDocument(BsonDocument base, String vID, long timestamp) {
		if (base == null)
			base = new BsonDocument();
		base.put(Tokens.ID, new BsonString(vID + "-" + timestamp));
		base.put(Tokens.VERTEX, new BsonString(vID));
		base.put(Tokens.TIMESTAMP, new BsonDateTime(timestamp));
		base.put(Tokens.TYPE, Tokens.TYPE_TIMESTAMP);
		return base;
	}

	public static BsonDocument makeTimestampEdgeEventDocument(BsonDocument base, String eID, long timestamp) {
		if (base == null)
			base = new BsonDocument();

		String[] arr = eID.split("\\|");
		String outV = arr[0];
		String inV = arr[2];
		String label = arr[1];
		base.put(Tokens.ID, new BsonString(eID + "-" + timestamp));
		base.put(Tokens.OUT_VERTEX, new BsonString(outV));
		base.put(Tokens.LABEL, new BsonString(label));
		base.put(Tokens.IN_VERTEX, new BsonString(inV));
		base.put(Tokens.EDGE, new BsonString(eID));
		base.put(Tokens.TIMESTAMP, new BsonDateTime(timestamp));
		base.put(Tokens.TYPE, Tokens.TYPE_TIMESTAMP);
		return base;
	}

	public static BsonDocument getBaseIntervalVertexEventDocument(String vID, LongInterval interval) {
		BsonDocument base = new BsonDocument();
		base.put(Tokens.ID, new BsonString(vID + "-" + interval.toString()));
		base.put(Tokens.VERTEX, new BsonString(vID));
		base.put(Tokens.START, new BsonDateTime(interval.getStart()));
		base.put(Tokens.END, new BsonDateTime(interval.getEnd()));
		base.put(Tokens.TYPE, Tokens.TYPE_INTERVAL);
		return base;
	}

	public static BsonDocument getBaseIntervalEdgeEventDocument(String eID, LongInterval interval) {
		BsonDocument base = new BsonDocument();
		base.put(Tokens.ID, new BsonString(eID + "-" + interval.toString()));
		base.put(Tokens.EDGE, new BsonString(eID));
		base.put(Tokens.START, new BsonDateTime(interval.getStart()));
		base.put(Tokens.END, new BsonDateTime(interval.getEnd()));
		base.put(Tokens.TYPE, Tokens.TYPE_INTERVAL);
		return base;
	}

	public static BsonDocument makeIntervalVertexEventDocument(BsonDocument base, String vID, LongInterval interval) {
		if (base == null)
			base = new BsonDocument();
		base.put(Tokens.ID, new BsonString(vID + "-" + interval.toString()));
		base.put(Tokens.VERTEX, new BsonString(vID));
		base.put(Tokens.START, new BsonDateTime(interval.getStart()));
		base.put(Tokens.END, new BsonDateTime(interval.getEnd()));
		base.put(Tokens.TYPE, Tokens.TYPE_INTERVAL);
		return base;
	}

	public static BsonDocument makeIntervalEdgeEventDocument(BsonDocument base, String eID, LongInterval interval) {
		if (base == null)
			base = new BsonDocument();
		base.put(Tokens.ID, new BsonString(eID + "-" + interval.toString()));
		base.put(Tokens.EDGE, new BsonString(eID));
		base.put(Tokens.START, new BsonDateTime(interval.getStart()));
		base.put(Tokens.END, new BsonDateTime(interval.getEnd()));
		base.put(Tokens.TYPE, Tokens.TYPE_INTERVAL);
		return base;
	}

	public static BsonArray getBsonArrayOfBsonString(String... strings) {
		BsonArray ret = new BsonArray();
		for (String string : strings) {
			ret.add(new BsonString(string));
		}
		return ret;
	}

	public static BsonDocument getBsonDocumentEdge(String outVertexID, String inVertexID, String label) {
		BsonDocument docEdge = new BsonDocument();
		docEdge.put("_id", new BsonString(getEdgeID(outVertexID, label, inVertexID)));
		docEdge.put("_outV", new BsonString(outVertexID));
		docEdge.put("_inV", new BsonString(inVertexID));
		docEdge.put("_label", new BsonString(label));
		return docEdge;
	}

	/**
	 * Return an identifier of edge
	 * 
	 * @param outVertexID:
	 *            an identifier of out-vertex
	 * @param edgeLabel:
	 *            edge label
	 * @param inVertexID:
	 *            an identifier of in-vertex
	 * @return an identifier of edge
	 */
	public static String getEdgeID(String outVertexID, String edgeLabel, String inVertexID) {
		return outVertexID + "|" + edgeLabel + "|" + inVertexID;
	}

	public static String getReversedEdgeID(BsonDocument edgeDocument) {
		return edgeDocument.getString("_inV").getValue() + "|" + edgeDocument.getString("_label").getValue() + "|"
				+ edgeDocument.getString("_outV").getValue();
	}

	public static BsonDocument getEdgeDocument(String outVertexID, String edgeLabel, String inVertexID) {
		if (edgeLabel == null)
			throw ExceptionFactory.edgeLabelCanNotBeNull();
		String edgeID = Converter.getEdgeID(outVertexID, edgeLabel, inVertexID);
		BsonDocument e = new BsonDocument();
		e.put("_id", new BsonString(edgeID));
		e.put("_outV", new BsonString(outVertexID));
		e.put("_inV", new BsonString(inVertexID));
		e.put("_label", new BsonString(edgeLabel));
		return e;
	}

	/**
	 * Return an identifier of edge
	 * 
	 * @param outVertexID:
	 *            an identifier of out-vertex
	 * @param edgeLabel:
	 *            edge label
	 * @param inVertexID:
	 *            an identifier of in-vertex
	 * @return an identifier of edge
	 */
	public static String getEdgeID(Vertex outVertex, String edgeLabel, Vertex inVertex) {
		return outVertex.getId() + "|" + edgeLabel + "|" + inVertex.getId();
	}

	public static BsonArray stringArrayToBsonArray(String[] stringArray) {
		BsonArray bson = new BsonArray();
		for (String string : stringArray) {
			bson.add(new BsonString(string));
		}
		return bson;
	}

	@SuppressWarnings({ "rawtypes" })
	public static BsonDocument makeIndexParamterDocument(Parameter... indexParameters) {
		BsonDocument index = new BsonDocument();
		for (Parameter param : indexParameters) {
			validateIndexParameter(param);
			index.append(param.getKey().toString(), new BsonInt32((Integer) param.getValue()));
		}
		return index;
	}

	@SuppressWarnings("rawtypes")
	public static void validateIndexParameter(Parameter param) {
		Object objKey = param.getKey();
		Object objValue = param.getValue();
		if (!(objKey instanceof String))
			throw ExceptionFactory.indexKeyShouldBeString();
		if (!(objValue instanceof Integer))
			throw ExceptionFactory.indexValueShouldBeInteger();
		Integer value = (Integer) objValue;
		if (value != 1 && value != -1)
			throw ExceptionFactory.indexValueShouldBeIntegerOneOrMinusOne();
	}
}
