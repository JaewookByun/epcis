package org.oliot.khronos.common;

import org.bson.BsonBoolean;
import org.bson.BsonDocument;
import org.bson.BsonInt32;
import org.bson.BsonString;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 * 
 * @author Jaewook Byun, Ph.D candidate extends Marko's work to implement
 *         Temporal Property Graph
 * 
 *         Korea Advanced Institute of Science and Technology (KAIST)
 * 
 *         Real-time Embedded System Laboratory (RESL)
 * 
 *         bjw0829@kaist.ac.kr, bjw0829@gmail.com
 *
 */
public class Tokens {

	public static final String VERSION = "0.0.1";
	/**
	 * _label
	 */
	public static final String LABEL = "_l";
	public static final String ID = "_id";
	public static final String EDGE_COLLECTION = "edges";
	public static final String TIMESTAMP_EDGE_EVENT_COLLECTION = "tEdgeEvents";
	public static final String TIMESTAMP_VERTEX_EVENT_COLLECTION = "tVertexEvents";
	public static final String VERTEX_COLLECTION = "vertices";
	public static final String TYPE = "_type";
	public static final BsonString TYPE_STATIC = new BsonString("s");
	public static final BsonString TYPE_TIMESTAMP = new BsonString("t");
	public static final BsonString TYPE_INTERVAL = new BsonString("i");
	public static final String OUT_VERTEX = "_o";
	public static final String IN_VERTEX = "_i";
	public static final String VERTEX = "_v";
	/**
	 * _edge
	 */
	public static final String EDGE = "_edge";
	public static final String START = "_s";
	public static final String END = "_e";
	/**
	 * _t
	 */
	public static final String TIMESTAMP = "_t";
	public static final String INTERVAL = "_i";
	public static final BsonDocument SORT_ID_ASC = new BsonDocument(Tokens.ID, new BsonInt32(1));
	public static final BsonDocument SORT_ID_DESC = new BsonDocument(Tokens.ID, new BsonInt32(-1));
	public static final BsonDocument SORT_START_ASC = new BsonDocument(START, new BsonInt32(1));
	public static final BsonDocument SORT_START_DESC = new BsonDocument(START, new BsonInt32(-1));
	public static final BsonDocument SORT_END_ASC = new BsonDocument(END, new BsonInt32(1));
	public static final BsonDocument SORT_END_DESC = new BsonDocument(END, new BsonInt32(-1));
	public static final BsonDocument SORT_TIMESTAMP_ASC = new BsonDocument(TIMESTAMP, new BsonInt32(1));
	public static final BsonDocument SORT_TIMESTAMP_DESC = new BsonDocument(TIMESTAMP, new BsonInt32(-1));
	public static final BsonDocument PRJ_ONLY_ID = new BsonDocument(Tokens.ID, new BsonBoolean(true));
	public static final BsonDocument PRJ_NOT_ID = new BsonDocument(Tokens.ID, new BsonBoolean(false));
	public static final BsonDocument PRJ_ONLY_OUTV_LABEL_INV = new BsonDocument(Tokens.OUT_VERTEX,
			new BsonBoolean(true)).append(Tokens.LABEL, new BsonBoolean(true))
					.append(Tokens.IN_VERTEX, new BsonBoolean(true)).append(Tokens.ID, new BsonBoolean(false));
	public static final BsonDocument PRJ_ONLY_START = new BsonDocument(START, new BsonBoolean(true));
	public static final BsonDocument PRJ_ONLY_TIMESTAMP = new BsonDocument(TIMESTAMP, new BsonBoolean(true))
			.append(Tokens.ID, new BsonBoolean(false));
	public static final BsonDocument PRJ_ONLY_TIMESTAMP_EXCEPT_ID = new BsonDocument(TIMESTAMP, new BsonBoolean(true));
	public static final BsonDocument PRJ_ONLY_START_AND_END = new BsonDocument(START, new BsonBoolean(true))
			.append(END, new BsonBoolean(true)).append(Tokens.ID, new BsonBoolean(false));
	public static final BsonDocument PRJ_ONLY_START_AND_END_EXCEPT_ID = new BsonDocument(START, new BsonBoolean(true))
			.append(END, new BsonBoolean(true));
	public static final BsonDocument PRJ_RMV_ID_AND_TYPE = new BsonDocument(Tokens.ID, new BsonBoolean(false))
			.append(Tokens.TYPE, new BsonBoolean(false));
	public static final BsonDocument PRJ_ONLY_INTERVAL_PROPERTIES = new BsonDocument(Tokens.ID, new BsonBoolean(false))
			.append(Tokens.TYPE, new BsonBoolean(false)).append(Tokens.START, new BsonBoolean(false))
			.append(Tokens.END, new BsonBoolean(false));
	public static final BsonDocument PRJ_ONLY_OUTV_INV_NOT_ID = new BsonDocument(Tokens.OUT_VERTEX,
			new BsonBoolean(true)).append(Tokens.IN_VERTEX, new BsonBoolean(true)).append(Tokens.ID,
					new BsonBoolean(false));

	public static final BsonDocument FLT_VERTEX_FIELD_NOT_INCLUDED = new BsonDocument(Tokens.VERTEX,
			new BsonDocument(Tokens.FC.$exists.toString(), new BsonBoolean(false)));

	public static final BsonDocument FLT_EDGE_FIELD_NOT_INCLUDED = new BsonDocument(Tokens.EDGE,
			new BsonDocument(Tokens.FC.$exists.toString(), new BsonBoolean(false)));

	public static enum C {
		$and, $or
	}

	public static enum S {
		min, max, cnt, tot
	}

	public static enum Position {
		/**
		 * first timestamp
		 */
		first,
		/**
		 * last timestamp
		 */
		last
	}

	/**
	 * Arithmetic Comparators
	 * 
	 * @author jack
	 *
	 */
	public static enum AC {
		/**
		 * Greater than
		 */
		$gt,
		/**
		 * Less than
		 */
		$lt,
		/**
		 * Equal to
		 */
		$eq,
		/**
		 * Greater than or equal to
		 */
		$gte,
		/**
		 * Less than or equal to
		 */
		$lte,
		/**
		 * Not equal to
		 */
		$ne
	}

	/**
	 * Full Comparator
	 * 
	 * @author jack
	 *
	 */
	public static enum FC {
		/**
		 * Greater than
		 */
		$gt,
		/**
		 * Less than
		 */
		$lt,
		/**
		 * Equal to
		 */
		$eq,
		/**
		 * Greater than or equal to
		 */
		$gte,
		/**
		 * Less than or equal to
		 */
		$lte,
		/**
		 * Not equal to
		 */
		$ne,
		/**
		 * In collection
		 */
		$in,
		/**
		 * Not in collection
		 */
		$nin, $exists, $max, $min
	}

	public static enum Order {
		DECR, INCR
	}
}
