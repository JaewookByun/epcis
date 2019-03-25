package org.oliot.khronos.persistent;

import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.FindOneAndReplaceOptions;
import com.mongodb.client.model.UpdateOptions;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Element;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.bson.BsonArray;
import org.bson.BsonBoolean;
import org.bson.BsonDateTime;
import org.bson.BsonDocument;
import org.bson.BsonNull;
import org.bson.BsonString;
import org.bson.BsonValue;
import org.oliot.khronos.common.Tokens;
import org.oliot.khronos.common.Tokens.AC;
import org.oliot.khronos.persistent.util.Converter;

/**
 * Copyright (C) 2016-2017 Jaewook Byun
 * 
 * Persistent Temporal Property Graph with MongoDB
 *
 * The part of static graph implements Tinkerpop Blueprints
 * (https://github.com/tinkerpop/blueprints).
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
@SuppressWarnings("unchecked")
abstract class ChronoElement implements Element {

	protected final String id;
	protected final ChronoGraph graph;

	/**
	 * Create Graph Element
	 * 
	 * @param id
	 * @param graph
	 */
	protected ChronoElement(final String id, final ChronoGraph graph) {
		this.id = id;
		this.graph = graph;
	}

	/**
	 * 
	 * @return all properties
	 */
	public BsonDocument getProperties() {
		BsonDocument doc = null;
		if (this instanceof ChronoVertex) {
			doc = graph.getVertexCollection().find(new BsonDocument(Tokens.ID, new BsonString(this.id))).first();
		} else {
			ChronoEdge e = (ChronoEdge) this;
			BsonDocument query = new BsonDocument();
			query.put(Tokens.OUT_VERTEX, new BsonString(e.getOutVertex().toString()));
			query.put(Tokens.LABEL, new BsonString(e.getLabel()));
			query.put(Tokens.IN_VERTEX, new BsonString(e.getInVertex().toString()));
			doc = graph.getEdgeCollection().find(query).first();
		}
		return doc;
	}

	/**
	 * Return the object value associated with the provided string key. If no value
	 * exists for that key, return null.
	 *
	 * @param key the key of the key/value property, Tokens.ID, Tokens.LABEL,
	 *            Tokens.OUT_VERTEX, Tokens.IN_VERTEX included
	 * @return the object value related to the string key
	 */
	@Override
	public <T> T getProperty(final String key) {
		BsonDocument doc = null;
		if (this instanceof ChronoVertex) {
			doc = graph.getVertexCollection().find(new BsonDocument(Tokens.ID, new BsonString(this.id)))
					.projection(new BsonDocument(key, new BsonBoolean(true))).first();
		} else {
			doc = graph.getEdgeCollection().find(new BsonDocument(Tokens.ID, new BsonString(this.id)))
					.projection(new BsonDocument(key, new BsonBoolean(true))).first();
		}
		if (doc != null)
			return (T) doc.get(key);
		return null;
	}

	/**
	 * Return all the keys associated with the element.
	 * 
	 * @return the set of all string keys associated with the element
	 */
	@Override
	public Set<String> getPropertyKeys() {
		BsonDocument doc = getProperties();
		if (doc != null) {
			return doc.keySet();
		}
		return null;
	}

	/**
	 * Return all the keys associated with the element.
	 *
	 * @return the set of all string keys associated with the element
	 */
	@Override
	public void setProperty(final String key, final Object value) {
		ElementHelper.validateProperty(this, key, value);

		if (this instanceof ChronoVertex) {
			BsonDocument filter = new BsonDocument();
			filter.put(Tokens.ID, new BsonString(this.id));
			BsonDocument update = new BsonDocument();
			update.put("$set", new BsonDocument(key, (BsonValue) value));
			graph.getVertexCollection().updateOne(filter, update, new UpdateOptions().upsert(true));
		} else {
			BsonDocument filter = new BsonDocument();
			ChronoEdge e = (ChronoEdge) this;
			filter.put(Tokens.OUT_VERTEX, new BsonString(e.getOutVertex().toString()));
			filter.put(Tokens.LABEL, new BsonString(e.getLabel()));
			filter.put(Tokens.IN_VERTEX, new BsonString(e.getInVertex().toString()));
			BsonDocument update = new BsonDocument();
			update.put("$set", new BsonDocument(key, (BsonValue) value));
			graph.getEdgeCollection().updateOne(filter, update, new UpdateOptions().upsert(true));
		}
	}

	@SuppressWarnings("deprecation")
	public void setProperties(final BsonDocument properties) {
		if (properties == null)
			return;
		BsonDocument filter = new BsonDocument();
		filter.put(Tokens.ID, new BsonString(this.id));
		if (this instanceof ChronoVertex) {
			graph.getVertexCollection().replaceOne(filter, Converter.makeVertexDocument(properties, this.id),
					new UpdateOptions().upsert(true));
		} else {
			ChronoEdge ce = (ChronoEdge) this;
			graph.getEdgeCollection().replaceOne(filter, Converter.makeEdgeDocument(properties, this.id,
					ce.getOutVertex().id, ce.getInVertex().id, ce.getLabel()), new UpdateOptions().upsert(true));
		}
	}

	/**
	 * Un-assigns a key/value property from the element. The object value of the
	 * removed property is returned.
	 *
	 * @param key the key of the property to remove from the element
	 * @return the object value associated with that key prior to removal. Should be
	 *         instance of BsonValue
	 */
	@Override
	public <T> T removeProperty(final String key) {
		try {
			BsonValue value = getProperty(key);
			BsonDocument filter = new BsonDocument();
			filter.put(Tokens.ID, new BsonString(this.id));
			BsonDocument update = new BsonDocument();
			update.put("$unset", new BsonDocument(key, new BsonNull()));
			if (this instanceof ChronoVertex) {
				graph.getVertexCollection().updateOne(filter, update, new UpdateOptions().upsert(true));
				return (T) value;
			} else {
				graph.getEdgeCollection().updateOne(filter, update, new UpdateOptions().upsert(true));
				return (T) value;
			}
		} catch (MongoWriteException e) {
			throw e;
		}
	}

	/**
	 * Remove the element from the graph.
	 */
	@Override
	public void remove() {
		if (this instanceof ChronoVertex) {
			graph.removeVertex((ChronoVertex) this);
		} else {
			graph.removeEdge(id);
		}
	}

	/**
	 * Clear Static Properties
	 */
	@SuppressWarnings("deprecation")
	public void clearStaticProperties() {
		BsonDocument filter = new BsonDocument();
		filter.put(Tokens.ID, new BsonString(this.id));
		if (this instanceof ChronoVertex) {
			graph.getVertexCollection().replaceOne(filter, Converter.getBaseVertexDocument(this.id),
					new UpdateOptions().upsert(true));
		} else {
			ChronoEdge ce = (ChronoEdge) this;
			graph.getEdgeCollection().replaceOne(filter,
					Converter.getBaseEdgeDocument(this.id, ce.getOutVertex().id, ce.getInVertex().id, ce.getLabel()),
					new UpdateOptions().upsert(true));
		}
	}

	public boolean equals(final Object object) {
		return ElementHelper.areEqual(this, object);
	}

	public int hashCode() {
		return this.id.hashCode();
	}

	/**
	 * Edge: outV|label|inV Vertex: id
	 */
	@Override
	public String toString() {
		return id.toString();
	}

	/**
	 * Return Graph
	 * 
	 * @return
	 */
	public ChronoGraph getGraph() {
		return this.graph;
	}

	/**
	 * set timestamp properties (replace) for the given timestamp
	 * 
	 * @param timestamp
	 * @param timestampProperties
	 */
	public void setTimestampProperties(final Long timestamp, BsonDocument timestampProperties) {
		if (timestampProperties == null)
			timestampProperties = new BsonDocument();
		if (this instanceof ChronoVertex) {
			graph.getVertexEvents().findOneAndReplace(
					new BsonDocument(Tokens.VERTEX, new BsonString(this.id)).append(Tokens.TIMESTAMP,
							new BsonDateTime(timestamp)),
					Converter.makeTimestampVertexEventDocumentWithoutID(timestampProperties, this.id, timestamp),
					new FindOneAndReplaceOptions().upsert(true));
		} else {
			ChronoEdge e = (ChronoEdge) this;
			graph.getEdgeEvents().findOneAndReplace(
					new BsonDocument(Tokens.OUT_VERTEX, new BsonString(e.getOutVertex().toString()))
							.append(Tokens.LABEL, new BsonString(e.getLabel()))
							.append(Tokens.TIMESTAMP, new BsonDateTime(timestamp))
							.append(Tokens.IN_VERTEX, new BsonString(e.getInVertex().toString())),
					Converter.makeTimestampEdgeEventDocumentWithoutID(timestampProperties, this.id, timestamp),
					new FindOneAndReplaceOptions().upsert(true));
		}
	}

	/**
	 * Set TimestampProperty for timestamp and key, existing value will be replaced
	 * 
	 * @param timestamp
	 * @param key
	 * @param value
	 */
	public void setTimestampProperty(final Long timestamp, String key, BsonValue value) {
		ElementHelper.validateTimestampProperty(this, timestamp, key, value);

		if (this instanceof ChronoVertex)
			graph.getVertexEvents().updateOne(
					new BsonDocument(Tokens.VERTEX, new BsonString(this.id)).append(Tokens.TIMESTAMP,
							new BsonDateTime(timestamp)),
					new BsonDocument("$set", new BsonDocument(key, value)), new UpdateOptions().upsert(true));
		else {
			ChronoEdge e = (ChronoEdge) this;
			graph.getEdgeEvents().updateOne(
					new BsonDocument(Tokens.OUT_VERTEX, new BsonString(e.getOutVertex().toString()))
							.append(Tokens.LABEL, new BsonString(e.getLabel()))
							.append(Tokens.TIMESTAMP, new BsonDateTime(timestamp))
							.append(Tokens.IN_VERTEX, new BsonString(e.getInVertex().toString())),
					new BsonDocument("$set", new BsonDocument(key, value)), new UpdateOptions().upsert(true));
		}
		//
		// BsonDocument exist = getTimestampProperties(timestamp);
		// if (exist == null || exist.size() == 0) {
		// BsonDocument timestampProperties = new BsonDocument(key, value);
		// if (this instanceof ChronoVertex)
		// graph.getVertexEvents().findOneAndReplace(
		// new BsonDocument(Tokens.VERTEX, new
		// BsonString(this.id)).append(Tokens.TIMESTAMP,
		// new BsonDateTime(timestamp)),
		// Converter.makeTimestampVertexEventDocument(timestampProperties, this.id,
		// timestamp),
		// new FindOneAndReplaceOptions().upsert(true));
		// else {
		// ChronoEdge e = (ChronoEdge) this;
		// graph.getEdgeEvents().findOneAndReplace(
		// new BsonDocument(Tokens.OUT_VERTEX, new
		// BsonString(e.getOutVertex().toString()))
		// .append(Tokens.LABEL, new BsonString(e.getLabel()))
		// .append(Tokens.TIMESTAMP, new BsonDateTime(timestamp))
		// .append(Tokens.IN_VERTEX, new BsonString(e.getInVertex().toString())),
		// Converter.makeTimestampEdgeEventDocument(timestampProperties, this.id,
		// timestamp),
		// new FindOneAndReplaceOptions().upsert(true));
		// }
		// } else {
		// if (this instanceof ChronoVertex)
		// graph.getVertexCollection()
		// .updateOne(
		// new BsonDocument(Tokens.ID, new BsonString(this.id + "-" + timestamp))
		// .append(Tokens.VERTEX, new BsonString(this.id)),
		// new BsonDocument("$set", new BsonDocument(key, value)));
		// else
		// graph.getEdgeCollection()
		// .updateOne(
		// new BsonDocument(Tokens.ID, new BsonString(this.id + "-" + timestamp))
		// .append(Tokens.VERTEX, new BsonString(this.id)),
		// new BsonDocument("$set", new BsonDocument(key, value)));
		// }
	}

	/**
	 * Expensive Operation
	 * 
	 * @return TreeSet of temporalProperty key set
	 */
	public TreeSet<Long> getTimestamps() {
		TreeSet<Long> timestampPropertyKeys = new TreeSet<Long>();
		if (this instanceof ChronoVertex) {
			MongoCursor<BsonDocument> cursor = graph.getVertexEvents().find(
					new BsonDocument(Tokens.VERTEX, new BsonString(this.id)).append(Tokens.TYPE, Tokens.TYPE_TIMESTAMP))
					.projection(Tokens.PRJ_ONLY_TIMESTAMP).iterator();
			while (cursor.hasNext()) {
				BsonDocument doc = cursor.next();
				timestampPropertyKeys.add(doc.getDateTime(Tokens.TIMESTAMP).getValue());
			}
		} else {
			MongoCursor<BsonDocument> cursor = graph.getEdgeEvents().find(
					new BsonDocument(Tokens.EDGE, new BsonString(this.id)).append(Tokens.TYPE, Tokens.TYPE_TIMESTAMP))
					.projection(Tokens.PRJ_ONLY_TIMESTAMP).iterator();
			while (cursor.hasNext()) {
				BsonDocument doc = cursor.next();
				timestampPropertyKeys.add(doc.getDateTime(Tokens.TIMESTAMP).getValue());
			}
		}
		return timestampPropertyKeys;
	}

	/**
	 * 
	 * @param timestamps
	 * @return
	 */
	public TreeSet<Long> getTimestamps(BsonArray timestamps) {
		TreeSet<Long> timestampPropertyKeys = new TreeSet<Long>();

		if (this instanceof ChronoVertex) {
			MongoCursor<BsonDocument> cursor = graph.getVertexCollection()
					.find(new BsonDocument(Tokens.VERTEX, new BsonString(this.id))
							.append(Tokens.TYPE, Tokens.TYPE_TIMESTAMP)
							.append(Tokens.TIMESTAMP, new BsonDocument(Tokens.FC.$in.toString(), timestamps)))
					.projection(Tokens.PRJ_ONLY_TIMESTAMP).iterator();
			while (cursor.hasNext()) {
				BsonDocument doc = cursor.next();
				timestampPropertyKeys.add(doc.getDateTime(Tokens.TIMESTAMP).getValue());
			}
		} else {
			MongoCursor<BsonDocument> cursor = graph.getEdgeCollection()
					.find(new BsonDocument(Tokens.EDGE, new BsonString(this.id))
							.append(Tokens.TYPE, Tokens.TYPE_TIMESTAMP)
							.append(Tokens.TIMESTAMP, new BsonDocument(Tokens.FC.$in.toString(), timestamps)))
					.projection(Tokens.PRJ_ONLY_TIMESTAMP).iterator();
			while (cursor.hasNext()) {
				BsonDocument doc = cursor.next();
				timestampPropertyKeys.add(doc.getDateTime(Tokens.TIMESTAMP).getValue());
			}
		}
		return timestampPropertyKeys;
	}

	/**
	 * @param timestamp
	 * @return TimestampProperties BsonDocument
	 */
	public BsonDocument getTimestampProperties(final Long timestamp) {
		if (this instanceof ChronoVertex)
			return graph.getVertexEvents().find(new BsonDocument(Tokens.VERTEX, new BsonString(this.id))
					.append(Tokens.TIMESTAMP, new BsonDateTime(timestamp))).first();
		else {
			String[] idArr = this.id.split("\\|");
			String outV = idArr[0];
			String label = idArr[1];
			String inV = idArr[2];
			return graph.getEdgeEvents().find(new BsonDocument(Tokens.OUT_VERTEX, new BsonString(outV))
					.append(Tokens.LABEL, new BsonString(label)).append(Tokens.TIMESTAMP, new BsonDateTime(timestamp))
					.append(Tokens.IN_VERTEX, new BsonString(inV))).projection(Tokens.PRJ_NOT_ID).first();
		}
	}

	/**
	 * @param timestamp
	 * @param projection
	 * @return TimestampProperties BsonDocument only containing projection keys
	 */
	public BsonDocument getTimestampProperties(final Long timestamp, final String[] projection) {
		BsonDocument bsonProjection = new BsonDocument();
		if (projection != null) {
			for (String string : projection) {
				bsonProjection.append(string, new BsonBoolean(true));
			}
		}

		if (this instanceof ChronoVertex)
			return graph.getVertexEvents().find(new BsonDocument(Tokens.VERTEX, new BsonString(this.id))
					.append(Tokens.TIMESTAMP, new BsonDateTime(timestamp))).projection(bsonProjection).first();
		else
			return graph.getEdgeCollection()
					.find(new BsonDocument(Tokens.ID, new BsonString(this.id + "-" + timestamp)))
					.projection(bsonProjection).first();
	}

	/**
	 * @param timestamp
	 * @param key
	 * @return property value for the given key at the given timestamp
	 */
	public BsonValue getTimestampPropertyValue(final Long timestamp, final String key) {
		BsonDocument timestampProperties = getTimestampProperties(timestamp, new String[] { key });
		if (timestampProperties.containsKey(key))
			return timestampProperties.get(key);
		else
			return null;
	}

	/**
	 * Clear all timestamp properties
	 */
	public void clearTimestampProperties() {
		if (this instanceof ChronoVertex) {
			graph.getVertexCollection().deleteMany(new BsonDocument(Tokens.VERTEX, new BsonString(this.id))
					.append(Tokens.TYPE, Tokens.TYPE_TIMESTAMP));
		} else {
			graph.getEdgeCollection().deleteMany(
					new BsonDocument(Tokens.EDGE, new BsonString(this.id)).append(Tokens.TYPE, Tokens.TYPE_TIMESTAMP));
		}
	}

	/**
	 * Remain only timestamp property related to the given key
	 * 
	 * @param timestamp
	 * @return null or remaining timestamp property
	 */
	public BsonDocument retainTimestampProperties(final Long timestamp) {
		BsonDocument timestampProperties = getTimestampProperties(timestamp);
		clearTimestampProperties();
		setTimestampProperties(timestamp, timestampProperties);
		return timestampProperties;
	}

	/**
	 * MongoDB: When a $sort immediately precedes a $limit in the pipeline, the
	 * $sort operation only maintains the top n results as it progresses.
	 * 
	 * @return first existing timestamp
	 */
	public Long getFirstTimestamp() {
		if (this instanceof ChronoVertex) {
			BsonDocument first = graph.getVertexCollection()
					.find(new BsonDocument(Tokens.VERTEX, new BsonString(this.id)).append(Tokens.TYPE,
							Tokens.TYPE_TIMESTAMP))
					.projection(Tokens.PRJ_ONLY_TIMESTAMP).sort(Tokens.SORT_TIMESTAMP_ASC).limit(1).first();
			if (first == null)
				return null;
			else
				return first.getDateTime(Tokens.TIMESTAMP).getValue();
		} else {
			BsonDocument first = graph.getEdgeCollection()
					.find(new BsonDocument(Tokens.EDGE, new BsonString(this.id)).append(Tokens.TYPE,
							Tokens.TYPE_TIMESTAMP))
					.projection(Tokens.PRJ_ONLY_TIMESTAMP).sort(Tokens.SORT_TIMESTAMP_ASC).limit(1).first();
			if (first == null)
				return null;
			else
				return first.getDateTime(Tokens.TIMESTAMP).getValue();
		}
	}

	/**
	 * MongoDB: When a $sort immediately precedes a $limit in the pipeline, the
	 * $sort operation only maintains the top n results as it progresses.
	 * 
	 * @return last existing timestamp
	 */
	public Long getLastTimestamp() {
		if (this instanceof ChronoVertex) {
			BsonDocument last = graph.getVertexCollection()
					.find(new BsonDocument(Tokens.VERTEX, new BsonString(this.id)).append(Tokens.TYPE,
							Tokens.TYPE_TIMESTAMP))
					.projection(Tokens.PRJ_ONLY_TIMESTAMP).sort(Tokens.SORT_TIMESTAMP_DESC).limit(1).first();
			if (last == null)
				return null;
			else
				return last.getDateTime(Tokens.TIMESTAMP).getValue();
		} else {
			BsonDocument last = graph.getEdgeCollection()
					.find(new BsonDocument(Tokens.EDGE, new BsonString(this.id)).append(Tokens.TYPE,
							Tokens.TYPE_TIMESTAMP))
					.projection(Tokens.PRJ_ONLY_TIMESTAMP).sort(Tokens.SORT_TIMESTAMP_DESC).limit(1).first();
			if (last == null)
				return null;
			else
				return last.getDateTime(Tokens.TIMESTAMP).getValue();
		}
	}

	/**
	 * @param timestamp
	 * @return most existing ceiling timestamp
	 */
	public Long getCeilingTimestamp(long timestamp) {
		if (this instanceof ChronoVertex) {
			BsonDocument ceiling = graph.getVertexCollection()
					.find(new BsonDocument(Tokens.VERTEX, new BsonString(this.id))
							.append(Tokens.TYPE, Tokens.TYPE_TIMESTAMP).append(Tokens.TIMESTAMP,
									new BsonDocument(AC.$gte.toString(), new BsonDateTime(timestamp))))
					.projection(Tokens.PRJ_ONLY_TIMESTAMP).sort(Tokens.SORT_TIMESTAMP_ASC).limit(1).first();
			if (ceiling == null)
				return null;
			else
				return ceiling.getDateTime(Tokens.TIMESTAMP).getValue();
		} else {
			BsonDocument ceiling = graph.getEdgeCollection()
					.find(new BsonDocument(Tokens.EDGE, new BsonString(this.id))
							.append(Tokens.TYPE, Tokens.TYPE_TIMESTAMP).append(Tokens.TIMESTAMP,
									new BsonDocument(AC.$gte.toString(), new BsonDateTime(timestamp))))
					.projection(Tokens.PRJ_ONLY_TIMESTAMP).sort(Tokens.SORT_TIMESTAMP_ASC).limit(1).first();
			if (ceiling == null)
				return null;
			else
				return ceiling.getDateTime(Tokens.TIMESTAMP).getValue();
		}
	}

	/**
	 * @param timestamp
	 * @return most existing higher timestamp
	 */
	public Long getHigherTimestamp(long timestamp) {
		if (this instanceof ChronoVertex) {
			BsonDocument higher = graph.getVertexCollection()
					.find(new BsonDocument(Tokens.VERTEX, new BsonString(this.id))
							.append(Tokens.TYPE, Tokens.TYPE_TIMESTAMP)
							.append(Tokens.TIMESTAMP, new BsonDocument(AC.$gt.toString(), new BsonDateTime(timestamp))))
					.projection(Tokens.PRJ_ONLY_TIMESTAMP).sort(Tokens.SORT_TIMESTAMP_ASC).limit(1).first();
			if (higher == null)
				return null;
			else
				return higher.getDateTime(Tokens.TIMESTAMP).getValue();
		} else {
			BsonDocument higher = graph.getEdgeCollection()
					.find(new BsonDocument(Tokens.EDGE, new BsonString(this.id))
							.append(Tokens.TYPE, Tokens.TYPE_TIMESTAMP)
							.append(Tokens.TIMESTAMP, new BsonDocument(AC.$gt.toString(), new BsonDateTime(timestamp))))
					.projection(Tokens.PRJ_ONLY_TIMESTAMP).sort(Tokens.SORT_TIMESTAMP_ASC).limit(1).first();
			if (higher == null)
				return null;
			else
				return higher.getDateTime(Tokens.TIMESTAMP).getValue();
		}
	}

	/**
	 * @param timestamp
	 * @return most existing floor timestamp
	 */
	public Long getFloorTimestamp(long timestamp) {
		if (this instanceof ChronoVertex) {
			BsonDocument floor = graph.getVertexCollection()
					.find(new BsonDocument(Tokens.VERTEX, new BsonString(this.id))
							.append(Tokens.TYPE, Tokens.TYPE_TIMESTAMP).append(Tokens.TIMESTAMP,
									new BsonDocument(AC.$lte.toString(), new BsonDateTime(timestamp))))
					.projection(Tokens.PRJ_ONLY_TIMESTAMP).sort(Tokens.SORT_TIMESTAMP_DESC).limit(1).first();
			if (floor == null)
				return null;
			else
				return floor.getDateTime(Tokens.TIMESTAMP).getValue();
		} else {
			BsonDocument floor = graph.getEdgeCollection()
					.find(new BsonDocument(Tokens.EDGE, new BsonString(this.id))
							.append(Tokens.TYPE, Tokens.TYPE_TIMESTAMP).append(Tokens.TIMESTAMP,
									new BsonDocument(AC.$lte.toString(), new BsonDateTime(timestamp))))
					.projection(Tokens.PRJ_ONLY_TIMESTAMP).sort(Tokens.SORT_TIMESTAMP_DESC).limit(1).first();
			if (floor == null)
				return null;
			else
				return floor.getDateTime(Tokens.TIMESTAMP).getValue();
		}
	}

	/**
	 * @param timestamp
	 * @return most existing lower timestamp
	 */
	public Long getLowerTimestamp(long timestamp) {
		if (this instanceof ChronoVertex) {
			BsonDocument lower = graph.getVertexCollection()
					.find(new BsonDocument(Tokens.VERTEX, new BsonString(this.id))
							.append(Tokens.TYPE, Tokens.TYPE_TIMESTAMP)
							.append(Tokens.TIMESTAMP, new BsonDocument(AC.$lt.toString(), new BsonDateTime(timestamp))))
					.projection(Tokens.PRJ_ONLY_TIMESTAMP).sort(Tokens.SORT_TIMESTAMP_DESC).limit(1).first();
			if (lower == null)
				return null;
			else
				return lower.getDateTime(Tokens.TIMESTAMP).getValue();
		} else {
			BsonDocument lower = graph.getEdgeCollection()
					.find(new BsonDocument(Tokens.EDGE, new BsonString(this.id))
							.append(Tokens.TYPE, Tokens.TYPE_TIMESTAMP)
							.append(Tokens.TIMESTAMP, new BsonDocument(AC.$lt.toString(), new BsonDateTime(timestamp))))
					.projection(Tokens.PRJ_ONLY_TIMESTAMP).sort(Tokens.SORT_TIMESTAMP_DESC).limit(1).first();
			if (lower == null)
				return null;
			else
				return lower.getDateTime(Tokens.TIMESTAMP).getValue();
		}
	}

	/**
	 * @param left
	 * @param comparator
	 * @return closest timestamp to the given timestamp based on comparator
	 */
	public Long getTimestamp(long left, AC comparator) {
		if (comparator.equals(AC.$gte) || comparator.equals(AC.$gt) || comparator.equals(AC.$eq)) {
			if (this instanceof ChronoVertex) {
				BsonDocument doc = graph.getVertexCollection()
						.find(new BsonDocument(Tokens.VERTEX, new BsonString(this.id))
								.append(Tokens.TYPE, Tokens.TYPE_TIMESTAMP).append(Tokens.TIMESTAMP,
										new BsonDocument(comparator.toString(), new BsonDateTime(left))))
						.projection(Tokens.PRJ_ONLY_TIMESTAMP).sort(Tokens.SORT_TIMESTAMP_ASC).limit(1).first();
				if (doc == null)
					return null;
				else
					return doc.getDateTime(Tokens.TIMESTAMP).getValue();
			} else {
				BsonDocument doc = graph.getEdgeCollection()
						.find(new BsonDocument(Tokens.EDGE, new BsonString(this.id))
								.append(Tokens.TYPE, Tokens.TYPE_TIMESTAMP).append(Tokens.TIMESTAMP,
										new BsonDocument(comparator.toString(), new BsonDateTime(left))))
						.projection(Tokens.PRJ_ONLY_TIMESTAMP).sort(Tokens.SORT_TIMESTAMP_ASC).limit(1).first();
				if (doc == null)
					return null;
				else
					return doc.getDateTime(Tokens.TIMESTAMP).getValue();
			}
		} else {
			if (this instanceof ChronoVertex) {
				BsonDocument doc = graph.getVertexCollection()
						.find(new BsonDocument(Tokens.VERTEX, new BsonString(this.id))
								.append(Tokens.TYPE, Tokens.TYPE_TIMESTAMP).append(Tokens.TIMESTAMP,
										new BsonDocument(comparator.toString(), new BsonDateTime(left))))
						.projection(Tokens.PRJ_ONLY_TIMESTAMP).sort(Tokens.SORT_TIMESTAMP_DESC).limit(1).first();
				if (doc == null)
					return null;
				else
					return doc.getDateTime(Tokens.TIMESTAMP).getValue();
			} else {
				BsonDocument doc = graph.getEdgeCollection()
						.find(new BsonDocument(Tokens.EDGE, new BsonString(this.id))
								.append(Tokens.TYPE, Tokens.TYPE_TIMESTAMP).append(Tokens.TIMESTAMP,
										new BsonDocument(comparator.toString(), new BsonDateTime(left))))
						.projection(Tokens.PRJ_ONLY_TIMESTAMP).sort(Tokens.SORT_TIMESTAMP_DESC).limit(1).first();
				if (doc == null)
					return null;
				else
					return doc.getDateTime(Tokens.TIMESTAMP).getValue();
			}
		}
	}

	/**
	 * Only work for ChronoVertex
	 * 
	 * @param direction
	 * @return
	 */
	public TreeSet<Long> getTimestamps(Direction direction, BsonArray labels, long left, AC comparator) {
		TreeSet<Long> timestamps = new TreeSet<Long>();

		if (this instanceof ChronoVertex) {
			ChronoVertex v = (ChronoVertex) this;
			HashSet<Long> tSet = (HashSet<Long>) v.getChronoEdgeSet(direction, labels).parallelStream()
					.flatMap(e -> e.getTimestamps(left, comparator).parallelStream()).collect(Collectors.toSet());
			timestamps.addAll(tSet);
		}

		return timestamps;
	}

	/**
	 * edge events per vertex
	 * 
	 * @param direction
	 * @param label
	 * @param left
	 * @param comparator
	 * @return
	 */
	public HashMap<String, TreeMap<Long, String>> getOwnershipTransfer(Direction direction, String label, long left,
			AC comparator) {

		HashMap<String, TreeMap<Long, String>> ret = new HashMap<String, TreeMap<Long, String>>();

		if (this instanceof ChronoVertex) {
			ChronoVertex v = (ChronoVertex) this;

			if (direction.equals(Direction.OUT)) {
				BsonDocument query = new BsonDocument(Tokens.OUT_VERTEX, new BsonString(v.toString()))
						.append(Tokens.LABEL, new BsonString(label))
						.append(Tokens.TIMESTAMP, new BsonDocument(comparator.toString(), new BsonDateTime(left)));
				BsonDocument projection = new BsonDocument(Tokens.TIMESTAMP, new BsonBoolean(true))
						.append(Tokens.IN_VERTEX, new BsonBoolean(true)).append("action", new BsonBoolean(true))
						.append(Tokens.ID, new BsonBoolean(false));
				MongoCursor<BsonDocument> iterator = graph.getEdgeEvents().find(query).projection(projection)
						.iterator();
				while (iterator.hasNext()) {
					BsonDocument doc = iterator.next();
					String inV = doc.getString(Tokens.IN_VERTEX).getValue();
					long time = doc.getDateTime(Tokens.TIMESTAMP).getValue();
					String action = doc.getString("action").getValue();
					if (ret.containsKey(inV)) {
						TreeMap<Long, String> retVal = ret.get(inV);
						retVal.put(time, action);
						ret.put(inV, retVal);
					} else {
						TreeMap<Long, String> retVal = new TreeMap<Long, String>();
						retVal.put(time, action);
						ret.put(inV, retVal);
					}
				}
				return ret;
			} else if (direction.equals(Direction.IN)) {

				BsonDocument query = new BsonDocument();
				query.append(Tokens.IN_VERTEX, new BsonString(v.toString()));
				query.append(Tokens.LABEL, new BsonString(label));
				query.append(Tokens.TIMESTAMP, new BsonDocument(comparator.toString(), new BsonDateTime(left)));

				BsonDocument projection = new BsonDocument();
				projection.append(Tokens.TIMESTAMP, new BsonBoolean(true));
				projection.append(Tokens.OUT_VERTEX, new BsonBoolean(true));
				projection.append("action", new BsonBoolean(true));
				projection.append(Tokens.ID, new BsonBoolean(false));
				MongoCursor<BsonDocument> iterator = graph.getEdgeEvents().find(query).projection(projection)
						.iterator();
				while (iterator.hasNext()) {
					BsonDocument doc = iterator.next();
					String outV = doc.getString(Tokens.OUT_VERTEX).getValue();
					long time = doc.getDateTime(Tokens.TIMESTAMP).getValue();
					String action = doc.getString("action").getValue();
					// { "_inV" : "urn:epc:id:sgln:0000001.00001.0", "_t" : { "$date" :
					// 1513079328721 } }
					if (ret.containsKey(outV)) {
						TreeMap<Long, String> retVal = ret.get(outV);
						retVal.put(time, action);
						ret.put(outV, retVal);
					} else {
						TreeMap<Long, String> retVal = new TreeMap<Long, String>();
						retVal.put(time, action);
						ret.put(outV, retVal);
					}
				}
				return ret;

			}

		}

		return null;
	}

	/**
	 * edge events per vertex
	 * 
	 * @param direction
	 * @param label
	 * @param left
	 * @param comparator
	 * @return
	 */
	public TreeMap<Long, ChronoVertex> getTimestampNeighborVertices(Direction direction, String label, long left,
			AC comparator) {

		TreeMap<Long, ChronoVertex> ret = new TreeMap<Long, ChronoVertex>();

		if (this instanceof ChronoVertex) {
			ChronoVertex v = (ChronoVertex) this;

			if (direction.equals(Direction.OUT)) {

				// outv label t inv
				// inv label t outv
				BsonDocument query = new BsonDocument();
				query.append(Tokens.OUT_VERTEX, new BsonString(v.toString()));
				query.append(Tokens.LABEL, new BsonString(label));
				query.append(Tokens.TIMESTAMP, new BsonDocument(comparator.toString(), new BsonDateTime(left)));

				BsonDocument projection = new BsonDocument();
				projection.append(Tokens.TIMESTAMP, new BsonBoolean(true));
				projection.append(Tokens.IN_VERTEX, new BsonBoolean(true));
				projection.append(Tokens.ID, new BsonBoolean(false));
				MongoCursor<BsonDocument> iterator = graph.getEdgeEvents().find(query).projection(projection)
						.iterator();
				while (iterator.hasNext()) {
					BsonDocument doc = iterator.next();
					String inV = doc.getString(Tokens.IN_VERTEX).getValue();
					long time = doc.getDateTime(Tokens.TIMESTAMP).getValue();
					ret.put(time, graph.getChronoVertex(inV));
				}
				return ret;
			} else if (direction.equals(Direction.IN)) {

				BsonDocument query = new BsonDocument();
				query.append(Tokens.IN_VERTEX, new BsonString(v.toString()));
				query.append(Tokens.LABEL, new BsonString(label));
				query.append(Tokens.TIMESTAMP, new BsonDocument(comparator.toString(), new BsonDateTime(left)));

				BsonDocument projection = new BsonDocument();
				projection.append(Tokens.TIMESTAMP, new BsonBoolean(true));
				projection.append(Tokens.OUT_VERTEX, new BsonBoolean(true));
				projection.append(Tokens.ID, new BsonBoolean(false));
				MongoCursor<BsonDocument> iterator = graph.getEdgeEvents().find(query).projection(projection)
						.iterator();
				while (iterator.hasNext()) {
					BsonDocument doc = iterator.next();
					String outV = doc.getString(Tokens.OUT_VERTEX).getValue();
					long time = doc.getDateTime(Tokens.TIMESTAMP).getValue();
					ret.put(time, graph.getChronoVertex(outV));
				}
				return ret;

			}

		}

		return null;
	}

	/**
	 * edge events per vertex
	 * 
	 * @param direction
	 * @param label
	 * @param left
	 * @param comparator
	 * @return
	 */
	public HashMap<String, TreeMap<Long, EdgeEvent>> getTNeighbors(Direction direction, String label, long left,
			AC comparator) {

		HashMap<String, TreeMap<Long, EdgeEvent>> ret = new HashMap<String, TreeMap<Long, EdgeEvent>>();

		if (this instanceof ChronoVertex) {
			ChronoVertex v = (ChronoVertex) this;

			if (direction.equals(Direction.OUT)) {

				// outv label t inv
				// inv label t outv
				BsonDocument query = new BsonDocument();
				query.append(Tokens.OUT_VERTEX, new BsonString(v.toString()));
				query.append(Tokens.LABEL, new BsonString(label));
				query.append(Tokens.TIMESTAMP, new BsonDocument(comparator.toString(), new BsonDateTime(left)));

				BsonDocument projection = new BsonDocument();
				projection.append(Tokens.TIMESTAMP, new BsonBoolean(true));
				projection.append(Tokens.IN_VERTEX, new BsonBoolean(true));
				projection.append(Tokens.ID, new BsonBoolean(false));
				MongoCursor<BsonDocument> iterator = graph.getEdgeCollection().find(query).projection(projection)
						.iterator();
				while (iterator.hasNext()) {
					BsonDocument doc = iterator.next();
					String inV = doc.getString(Tokens.IN_VERTEX).getValue();
					long time = doc.getDateTime(Tokens.TIMESTAMP).getValue();
					EdgeEvent ee = new EdgeEvent(graph, new ChronoEdge(v.toString() + "|" + label + "|" + inV, graph),
							time);
					// { "_inV" : "urn:epc:id:sgln:0000001.00001.0", "_t" : { "$date" :
					// 1513079328721 } }
					if (ret.containsKey(inV)) {
						TreeMap<Long, EdgeEvent> retVal = ret.get(inV);
						retVal.put(time, ee);
						ret.put(inV, retVal);
					} else {
						TreeMap<Long, EdgeEvent> retVal = new TreeMap<Long, EdgeEvent>();
						retVal.put(time, ee);
						ret.put(inV, retVal);
					}
				}
				return ret;
			} else if (direction.equals(Direction.IN)) {

				BsonDocument query = new BsonDocument();
				query.append(Tokens.IN_VERTEX, new BsonString(v.toString()));
				query.append(Tokens.LABEL, new BsonString(label));
				query.append(Tokens.TIMESTAMP, new BsonDocument(comparator.toString(), new BsonDateTime(left)));

				BsonDocument projection = new BsonDocument();
				projection.append(Tokens.TIMESTAMP, new BsonBoolean(true));
				projection.append(Tokens.OUT_VERTEX, new BsonBoolean(true));
				projection.append(Tokens.ID, new BsonBoolean(false));
				MongoCursor<BsonDocument> iterator = graph.getEdgeCollection().find(query).projection(projection)
						.iterator();
				while (iterator.hasNext()) {
					BsonDocument doc = iterator.next();
					String outV = doc.getString(Tokens.OUT_VERTEX).getValue();
					long time = doc.getDateTime(Tokens.TIMESTAMP).getValue();
					EdgeEvent ee = new EdgeEvent(graph, new ChronoEdge(outV + "|" + label + v.toString(), graph), time);
					// { "_inV" : "urn:epc:id:sgln:0000001.00001.0", "_t" : { "$date" :
					// 1513079328721 } }
					if (ret.containsKey(outV)) {
						TreeMap<Long, EdgeEvent> retVal = ret.get(outV);
						retVal.put(time, ee);
						ret.put(outV, retVal);
					} else {
						TreeMap<Long, EdgeEvent> retVal = new TreeMap<Long, EdgeEvent>();
						retVal.put(time, ee);
						ret.put(outV, retVal);
					}
				}
				return ret;

			}

		}

		return null;
	}

	/**
	 * @param left
	 * @param comparator
	 * @return closest timestamp to the given timestamp based on comparator
	 */
	public TreeSet<Long> getTimestamps(HashMap<Long, AC> compSet) {
		TreeSet<Long> timestamps = new TreeSet<Long>();

		if (this instanceof ChronoVertex) {
			Iterator<Entry<Long, AC>> iter = compSet.entrySet().iterator();
			BsonDocument query = new BsonDocument(Tokens.VERTEX, new BsonString(this.id));

			while (iter.hasNext()) {
				Entry<Long, AC> comp = iter.next();
				query.append(Tokens.TYPE, Tokens.TYPE_TIMESTAMP).append(Tokens.TIMESTAMP,
						new BsonDocument(comp.getValue().toString(), new BsonDateTime(comp.getKey())));
			}

			MongoCursor<BsonDocument> docIter = graph.getVertexCollection().find(query).iterator();

			while (docIter.hasNext()) {
				BsonDocument doc = docIter.next();
				timestamps.add(doc.getDateTime(Tokens.TIMESTAMP).getValue());
			}
			return timestamps;
		} else if (this instanceof ChronoEdge) {

			Iterator<Entry<Long, AC>> iter = compSet.entrySet().iterator();
			BsonDocument query = new BsonDocument(Tokens.EDGE, new BsonString(this.id));

			while (iter.hasNext()) {
				Entry<Long, AC> comp = iter.next();
				query.append(Tokens.TYPE, Tokens.TYPE_TIMESTAMP).append(Tokens.TIMESTAMP,
						new BsonDocument(comp.getValue().toString(), new BsonDateTime(comp.getKey())));
			}

			MongoCursor<BsonDocument> docIter = graph.getEdgeCollection().find(query).iterator();

			while (docIter.hasNext()) {
				BsonDocument doc = docIter.next();
				timestamps.add(doc.getDateTime(Tokens.TIMESTAMP).getValue());
			}
			return timestamps;

		}
		return timestamps;
	}

	/**
	 * @param left
	 * @param comparator
	 * @return closest timestamp to the given timestamp based on comparator
	 */
	public TreeSet<Long> getTimestamps(long left, AC comparator) {
		TreeSet<Long> timestamps = new TreeSet<Long>();
		if (comparator.equals(AC.$gte) || comparator.equals(AC.$gt) || comparator.equals(AC.$eq)) {
			if (this instanceof ChronoVertex) {
				MongoCursor<BsonDocument> docIter = graph.getVertexCollection()
						.find(new BsonDocument(Tokens.VERTEX, new BsonString(this.id))
								.append(Tokens.TYPE, Tokens.TYPE_TIMESTAMP).append(Tokens.TIMESTAMP,
										new BsonDocument(comparator.toString(), new BsonDateTime(left))))
						.projection(Tokens.PRJ_ONLY_TIMESTAMP).sort(Tokens.SORT_TIMESTAMP_ASC).iterator();
				while (docIter.hasNext()) {
					BsonDocument doc = docIter.next();
					timestamps.add(doc.getDateTime(Tokens.TIMESTAMP).getValue());
				}
				return timestamps;
			} else {
				MongoCursor<BsonDocument> docIter = graph.getEdgeCollection()
						.find(new BsonDocument(Tokens.EDGE, new BsonString(this.id))
								.append(Tokens.TYPE, Tokens.TYPE_TIMESTAMP).append(Tokens.TIMESTAMP,
										new BsonDocument(comparator.toString(), new BsonDateTime(left))))
						.projection(Tokens.PRJ_ONLY_TIMESTAMP).sort(Tokens.SORT_TIMESTAMP_ASC).iterator();
				while (docIter.hasNext()) {
					BsonDocument doc = docIter.next();
					timestamps.add(doc.getDateTime(Tokens.TIMESTAMP).getValue());
				}
				return timestamps;
			}
		} else {
			if (this instanceof ChronoVertex) {
				MongoCursor<BsonDocument> docIter = graph.getVertexCollection()
						.find(new BsonDocument(Tokens.VERTEX, new BsonString(this.id))
								.append(Tokens.TYPE, Tokens.TYPE_TIMESTAMP).append(Tokens.TIMESTAMP,
										new BsonDocument(comparator.toString(), new BsonDateTime(left))))
						.projection(Tokens.PRJ_ONLY_TIMESTAMP).sort(Tokens.SORT_TIMESTAMP_DESC).iterator();
				while (docIter.hasNext()) {
					BsonDocument doc = docIter.next();
					timestamps.add(doc.getDateTime(Tokens.TIMESTAMP).getValue());
				}
				return timestamps;
			} else {
				MongoCursor<BsonDocument> docIter = graph.getEdgeCollection()
						.find(new BsonDocument(Tokens.EDGE, new BsonString(this.id))
								.append(Tokens.TYPE, Tokens.TYPE_TIMESTAMP).append(Tokens.TIMESTAMP,
										new BsonDocument(comparator.toString(), new BsonDateTime(left))))
						.projection(Tokens.PRJ_ONLY_TIMESTAMP).sort(Tokens.SORT_TIMESTAMP_DESC).iterator();
				while (docIter.hasNext()) {
					BsonDocument doc = docIter.next();
					timestamps.add(doc.getDateTime(Tokens.TIMESTAMP).getValue());
				}
				return timestamps;
			}
		}
	}

	/**
	 * Remove All Interval Properties
	 */
	public void clearIntervalProperties() {
		if (this instanceof ChronoVertex) {
			graph.getVertexCollection().deleteMany(
					new BsonDocument(Tokens.VERTEX, new BsonString(this.id)).append(Tokens.TYPE, Tokens.TYPE_INTERVAL));
		} else {
			graph.getEdgeCollection().deleteMany(
					new BsonDocument(Tokens.EDGE, new BsonString(this.id)).append(Tokens.TYPE, Tokens.TYPE_INTERVAL));
		}
	}

	/**
	 * Remove All Temporal Properties
	 */
	public void clearTemporalProperties() {
		clearIntervalProperties();
		clearStaticProperties();
	}

	/**
	 * @deprecated use toString
	 */
	@Override
	public Object getId() {
		return this.id;
	}
}
