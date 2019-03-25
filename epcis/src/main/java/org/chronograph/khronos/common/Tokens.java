package org.chronograph.khronos.common;

import org.bson.BsonBoolean;
import org.bson.BsonDocument;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 * 
 * @author Jaewook Byun, Assistant Professor, Halla University
 * 
 *         Data Frameworks and Platforms Laboratory (DFPL)
 * 
 *         jaewook.byun@halla.ac.kr, bjw0829@kaist.ac.kr, bjw0829@gmail.com
 *
 */
public class Tokens {

	public static final String VERSION = "1.0";
	public static final String LABEL = "_l";
	public static final String ID = "_id";
	public static final String UID = "k";
	public static final String EDGE_COLLECTION = "edges";
	public static final String VERTEX_COLLECTION = "vertices";
	public static final String EDGE_EVENT_COLLECTION = "edgeEvents";
	public static final String VERTEX_EVENT_COLLECTION = "vertexEvents";
	public static final String ID_COLLECTION = "ids";
	public static final String OUT_VERTEX = "_o";
	public static final String IN_VERTEX = "_i";
	public static final String TIMESTAMP = "_t";
	public static final String VERTEX = "_v";

	public static final BsonDocument PRJ_ONLY_ID = new BsonDocument(Tokens.ID, new BsonBoolean(true));
	public static final BsonDocument PRJ_ONLY_OUTV_LABEL_INV = new BsonDocument(Tokens.OUT_VERTEX,
			new BsonBoolean(true)).append(Tokens.LABEL, new BsonBoolean(true))
					.append(Tokens.IN_VERTEX, new BsonBoolean(true)).append(Tokens.ID, new BsonBoolean(false));
	public static final BsonDocument PRJ_ONLY_VERTEX = new BsonDocument(Tokens.VERTEX, new BsonBoolean(true))
			.append(Tokens.ID, new BsonBoolean(false));
	public static final BsonDocument PRJ_ONLY_VERTEX_TIMESTAMP = new BsonDocument(Tokens.VERTEX, new BsonBoolean(true))
			.append(Tokens.TIMESTAMP, new BsonBoolean(true)).append(Tokens.ID, new BsonBoolean(false));

	public static final BsonDocument PRJ_ONLY_TIMESTAMP = new BsonDocument(Tokens.TIMESTAMP, new BsonBoolean(true))
			.append(Tokens.ID, new BsonBoolean(false));
	public static final BsonDocument PRJ_ONLY_INV_TIMESTAMP = new BsonDocument(Tokens.TIMESTAMP, new BsonBoolean(true))
			.append(Tokens.IN_VERTEX, new BsonBoolean(true)).append(Tokens.ID, new BsonBoolean(false));
	public static final BsonDocument PRJ_ONLY_OUTV_TIMESTAMP = new BsonDocument(Tokens.TIMESTAMP, new BsonBoolean(true))
			.append(Tokens.OUT_VERTEX, new BsonBoolean(true)).append(Tokens.ID, new BsonBoolean(false));

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

	public static enum TemporalRelation {
		isBefore, isAfter, meets, isMetBy, overlapsWith, isOverlappedBy, starts, isStartedBy, during, contains,
		finishes, isFinishedBy, cotemporal
	}

	public static enum Conjunction {
		$and, $or
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
