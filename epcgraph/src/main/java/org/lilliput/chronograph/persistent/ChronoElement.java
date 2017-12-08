package org.lilliput.chronograph.persistent;

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
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.bson.BsonArray;
import org.bson.BsonBoolean;
import org.bson.BsonDateTime;
import org.bson.BsonDocument;
import org.bson.BsonNull;
import org.bson.BsonString;
import org.bson.BsonValue;
import org.lilliput.chronograph.common.LongInterval;
import org.lilliput.chronograph.common.Tokens;
import org.lilliput.chronograph.common.Tokens.AC;
import org.lilliput.chronograph.persistent.util.Converter;

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
			doc = graph.getEdgeCollection().find(new BsonDocument(Tokens.ID, new BsonString(this.id))).first();
		}
		return doc;
	}

	/**
	 * Return the object value associated with the provided string key. If no value
	 * exists for that key, return null.
	 *
	 * @param key
	 *            the key of the key/value property, Tokens.ID, Tokens.LABEL,
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
		BsonDocument filter = new BsonDocument();
		filter.put(Tokens.ID, new BsonString(this.id));
		BsonDocument update = new BsonDocument();
		update.put("$set", new BsonDocument(Tokens.TYPE, Tokens.TYPE_STATIC).append(key, (BsonValue) value));
		if (this instanceof ChronoVertex) {
			graph.getVertexCollection().updateOne(filter, update, new UpdateOptions().upsert(true));
		} else {
			graph.getEdgeCollection().updateOne(filter, update, new UpdateOptions().upsert(true));
		}
	}

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
	 * @param key
	 *            the key of the property to remove from the element
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
			graph.getVertexCollection().findOneAndReplace(
					new BsonDocument(Tokens.ID, new BsonString(this.id + "-" + timestamp)),
					Converter.makeTimestampVertexEventDocument(timestampProperties, this.id, timestamp),
					new FindOneAndReplaceOptions().upsert(true));
		} else {
			graph.getEdgeCollection().findOneAndReplace(
					new BsonDocument(Tokens.ID, new BsonString(this.id + "-" + timestamp)),
					Converter.makeTimestampEdgeEventDocument(timestampProperties, this.id, timestamp),
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

		BsonDocument exist = getTimestampProperties(timestamp);
		if (exist == null || exist.size() == 0) {
			BsonDocument timestampProperties = new BsonDocument(key, value);
			if (this instanceof ChronoVertex)
				graph.getVertexCollection().findOneAndReplace(
						new BsonDocument(Tokens.ID, new BsonString(this.id + "-" + timestamp)).append(Tokens.VERTEX,
								new BsonString(this.id)),
						Converter.makeTimestampVertexEventDocument(timestampProperties, this.id, timestamp),
						new FindOneAndReplaceOptions().upsert(true));
			else
				graph.getEdgeCollection().findOneAndReplace(
						new BsonDocument(Tokens.ID, new BsonString(this.id + "-" + timestamp)).append(Tokens.VERTEX,
								new BsonString(this.id)),
						Converter.makeTimestampEdgeEventDocument(timestampProperties, this.id, timestamp),
						new FindOneAndReplaceOptions().upsert(true));
		} else {
			if (this instanceof ChronoVertex)
				graph.getVertexCollection()
						.updateOne(
								new BsonDocument(Tokens.ID, new BsonString(this.id + "-" + timestamp))
										.append(Tokens.VERTEX, new BsonString(this.id)),
								new BsonDocument("$set", new BsonDocument(key, value)));
			else
				graph.getEdgeCollection()
						.updateOne(
								new BsonDocument(Tokens.ID, new BsonString(this.id + "-" + timestamp))
										.append(Tokens.VERTEX, new BsonString(this.id)),
								new BsonDocument("$set", new BsonDocument(key, value)));
		}
	}

	/**
	 * Expensive Operation
	 * 
	 * @return TreeSet of temporalProperty key set
	 */
	public TreeSet<Long> getTimestamps() {
		TreeSet<Long> timestampPropertyKeys = new TreeSet<Long>();
		if (this instanceof ChronoVertex) {
			MongoCursor<BsonDocument> cursor = graph
					.getVertexCollection().find(new BsonDocument(Tokens.VERTEX, new BsonString(this.id))
							.append(Tokens.TYPE, Tokens.TYPE_TIMESTAMP))
					.projection(Tokens.PRJ_ONLY_TIMESTAMP).iterator();
			while (cursor.hasNext()) {
				BsonDocument doc = cursor.next();
				timestampPropertyKeys.add(doc.getDateTime(Tokens.TIMESTAMP).getValue());
			}
		} else {
			MongoCursor<BsonDocument> cursor = graph.getEdgeCollection().find(
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
	 *
	 * @param interval
	 * @return
	 */
	public TreeSet<Long> getTimestamps(LongInterval interval) {
		TreeSet<Long> timestampPropertyKeys = new TreeSet<Long>();

		BsonArray bsonInterval = new BsonArray();
		bsonInterval.add(new BsonDocument(Tokens.TIMESTAMP,
				new BsonDocument(Tokens.FC.$gte.toString(), new BsonDateTime(interval.getStart()))));
		bsonInterval.add(new BsonDocument(Tokens.TIMESTAMP,
				new BsonDocument(Tokens.FC.$lte.toString(), new BsonDateTime(interval.getEnd()))));

		if (this instanceof ChronoVertex) {
			MongoCursor<BsonDocument> cursor = graph.getVertexCollection()
					.find(new BsonDocument(Tokens.VERTEX, new BsonString(this.id))
							.append(Tokens.TYPE, Tokens.TYPE_TIMESTAMP).append(Tokens.C.$and.toString(), bsonInterval))
					.projection(Tokens.PRJ_ONLY_TIMESTAMP).iterator();
			while (cursor.hasNext()) {
				BsonDocument doc = cursor.next();
				timestampPropertyKeys.add(doc.getDateTime(Tokens.TIMESTAMP).getValue());
			}
		} else {
			MongoCursor<BsonDocument> cursor = graph.getEdgeCollection()
					.find(new BsonDocument(Tokens.EDGE, new BsonString(this.id))
							.append(Tokens.TYPE, Tokens.TYPE_TIMESTAMP).append(Tokens.C.$and.toString(), bsonInterval))
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
	 * TODO: Check again
	 *
	 * @param interval
	 * @return
	 */
	public TreeSet<Long> getTimestamps(LongInterval interval, AC s, AC e) {
		TreeSet<Long> timestampPropertyKeys = new TreeSet<Long>();

		BsonArray bsonInterval = new BsonArray();
		bsonInterval.add(new BsonDocument(Tokens.TIMESTAMP,
				new BsonDocument(s.toString(), new BsonDateTime(interval.getStart()))));
		bsonInterval.add(new BsonDocument(Tokens.TIMESTAMP,
				new BsonDocument(e.toString(), new BsonDateTime(interval.getEnd()))));

		if (this instanceof ChronoVertex) {
			MongoCursor<BsonDocument> cursor = graph.getVertexCollection()
					.find(new BsonDocument(Tokens.VERTEX, new BsonString(this.id))
							.append(Tokens.TYPE, Tokens.TYPE_TIMESTAMP).append(Tokens.C.$and.toString(), bsonInterval))
					.projection(Tokens.PRJ_ONLY_TIMESTAMP).iterator();
			while (cursor.hasNext()) {
				BsonDocument doc = cursor.next();
				timestampPropertyKeys.add(doc.getDateTime(Tokens.TIMESTAMP).getValue());
			}
		} else {
			MongoCursor<BsonDocument> cursor = graph.getEdgeCollection()
					.find(new BsonDocument(Tokens.EDGE, new BsonString(this.id))
							.append(Tokens.TYPE, Tokens.TYPE_TIMESTAMP).append(Tokens.C.$and.toString(), bsonInterval))
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
			return graph.getVertexCollection()
					.find(new BsonDocument(Tokens.ID, new BsonString(this.id + "-" + timestamp))).first();
		else
			return graph.getEdgeCollection()
					.find(new BsonDocument(Tokens.ID, new BsonString(this.id + "-" + timestamp))).first();
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
			return graph.getVertexCollection()
					.find(new BsonDocument(Tokens.ID, new BsonString(this.id + "-" + timestamp)))
					.projection(bsonProjection).first();
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
		return timestampProperties.get(key);
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
	 * @param interval
	 * @param properties
	 */
	public void setIntervalProperties(final LongInterval interval, BsonDocument intervalProperties) {
		if (intervalProperties == null)
			intervalProperties = new BsonDocument();

		if (this instanceof ChronoVertex) {
			graph.getVertexCollection().findOneAndReplace(
					new BsonDocument(Tokens.ID, new BsonString(this.id + "-" + interval.toString())),
					Converter.makeIntervalVertexEventDocument(intervalProperties, this.id, interval),
					new FindOneAndReplaceOptions().upsert(true));
		} else {
			graph.getEdgeCollection().findOneAndReplace(
					new BsonDocument(Tokens.ID, new BsonString(this.id + "-" + interval.toString())),
					Converter.makeIntervalEdgeEventDocument(intervalProperties, this.id, interval),
					new FindOneAndReplaceOptions().upsert(true));
		}
	}

	/**
	 * @param timestamp
	 * @param key
	 * @param value
	 */
	public void setIntervalProperty(final LongInterval interval, final String key, final BsonValue value) {
		ElementHelper.validateIntervalProperty(this, interval, key, value);

		BsonDocument exist = getIntervalProperties(interval);
		if (exist == null || exist.size() == 0) {
			BsonDocument intervalProperties = new BsonDocument(key, value);
			if (this instanceof ChronoVertex)
				graph.getVertexCollection().findOneAndReplace(
						new BsonDocument(Tokens.ID, new BsonString(this.id + "-" + interval.toString())),
						Converter.makeIntervalVertexEventDocument(intervalProperties, this.id, interval),
						new FindOneAndReplaceOptions().upsert(true));
			else
				graph.getEdgeCollection().findOneAndReplace(
						new BsonDocument(Tokens.ID, new BsonString(this.id + "-" + interval.toString())),
						Converter.makeIntervalEdgeEventDocument(intervalProperties, this.id, interval),
						new FindOneAndReplaceOptions().upsert(true));
		} else {
			if (this instanceof ChronoVertex)
				graph.getVertexCollection().updateOne(
						new BsonDocument(Tokens.ID, new BsonString(this.id + "-" + interval.toString())),
						new BsonDocument("$set", new BsonDocument(key, value)));
			else
				graph.getEdgeCollection().updateOne(
						new BsonDocument(Tokens.ID, new BsonString(this.id + "-" + interval.toString())),
						new BsonDocument("$set", new BsonDocument(key, value)));
		}
	}

	/**
	 * @param interval
	 * @return interval properties for the given interval or null
	 */
	public BsonDocument getIntervalProperties(LongInterval interval) {
		if (this instanceof ChronoVertex)
			return graph.getVertexCollection()
					.find(new BsonDocument(Tokens.ID, new BsonString(this.id + "-" + interval.toString()))).first();
		else
			return graph.getEdgeCollection()
					.find(new BsonDocument(Tokens.ID, new BsonString(this.id + "-" + interval.toString()))).first();
	}

	/**
	 * @param interval
	 * @return interval properties for the given interval or null
	 */
	public BsonDocument getIntervalProperties(LongInterval interval, String[] projection) {
		BsonDocument bsonProjection = new BsonDocument();
		if (projection != null) {
			for (String string : projection) {
				bsonProjection.append(string, new BsonBoolean(true));
			}
		}

		if (this instanceof ChronoVertex)
			return graph.getVertexCollection()
					.find(new BsonDocument(Tokens.ID, new BsonString(this.id + "-" + interval.toString())))
					.projection(bsonProjection).first();
		else
			return graph.getEdgeCollection()
					.find(new BsonDocument(Tokens.ID, new BsonString(this.id + "-" + interval.toString())))
					.projection(bsonProjection).first();
	}

	/**
	 * @param interval
	 * @param key
	 * @return interval property value for the given interval and key
	 */
	public BsonValue getIntervalPropertyValue(LongInterval interval, String key) {
		BsonDocument intervalProperties = getIntervalProperties(interval, new String[] { key });
		return intervalProperties.get(key);
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
	 * Remain only timestamp property related to the given key
	 * 
	 * @param timestamp
	 * @return null or remaining timestamp property
	 */
	public BsonDocument retainIntervalProperties(final LongInterval interval) {
		BsonDocument intervalProperties = getIntervalProperties(interval);
		clearIntervalProperties();
		setIntervalProperties(interval, intervalProperties);
		return intervalProperties;
	}

	/**
	 * Remove All Temporal Properties
	 */
	public void clearTemporalProperties() {
		clearIntervalProperties();
		clearStaticProperties();
	}

	/**
	 * FIRST: interval event which has earliest start time
	 * 
	 * @return first existing interval
	 */
	public LongInterval getFirstInterval() {
		if (this instanceof ChronoVertex) {
			BsonDocument first = graph.getVertexCollection()
					.find(new BsonDocument(Tokens.VERTEX, new BsonString(this.id)).append(Tokens.TYPE,
							Tokens.TYPE_INTERVAL))
					.projection(Tokens.PRJ_ONLY_START_AND_END).sort(Tokens.SORT_START_ASC).limit(1).first();
			if (first == null)
				return null;
			else
				return new LongInterval(first.getDateTime(Tokens.START).getValue(),
						first.getDateTime(Tokens.END).getValue());
		} else {
			BsonDocument first = graph.getEdgeCollection()
					.find(new BsonDocument(Tokens.EDGE, new BsonString(this.id)).append(Tokens.TYPE,
							Tokens.TYPE_INTERVAL))
					.projection(Tokens.PRJ_ONLY_START_AND_END).sort(Tokens.SORT_START_ASC).limit(1).first();
			if (first == null)
				return null;
			else
				return new LongInterval(first.getDateTime(Tokens.START).getValue(),
						first.getDateTime(Tokens.END).getValue());
		}
	}

	/**
	 * LAST: interval event which has latest start time
	 * 
	 * @return last existing interval
	 */
	public LongInterval getLastInterval() {
		if (this instanceof ChronoVertex) {
			BsonDocument last = graph.getVertexCollection()
					.find(new BsonDocument(Tokens.VERTEX, new BsonString(this.id)).append(Tokens.TYPE,
							Tokens.TYPE_INTERVAL))
					.projection(Tokens.PRJ_ONLY_START_AND_END).sort(Tokens.SORT_START_DESC).limit(1).first();
			if (last == null)
				return null;
			else
				return new LongInterval(last.getDateTime(Tokens.START).getValue(),
						last.getDateTime(Tokens.END).getValue());
		} else {
			BsonDocument last = graph.getEdgeCollection()
					.find(new BsonDocument(Tokens.EDGE, new BsonString(this.id)).append(Tokens.TYPE,
							Tokens.TYPE_INTERVAL))
					.projection(Tokens.PRJ_ONLY_START_AND_END).sort(Tokens.SORT_START_DESC).limit(1).first();
			if (last == null)
				return null;
			else
				return new LongInterval(last.getDateTime(Tokens.START).getValue(),
						last.getDateTime(Tokens.END).getValue());
		}
	}

	/**
	 * 
	 * @return treeSet of temporalProperty key set
	 */
	public TreeSet<LongInterval> getIntervals() {
		TreeSet<LongInterval> intervalPropertyKeys = new TreeSet<LongInterval>();
		if (this instanceof ChronoVertex) {
			MongoCursor<BsonDocument> cursor = graph
					.getVertexCollection().find(new BsonDocument(Tokens.VERTEX, new BsonString(this.id))
							.append(Tokens.TYPE, Tokens.TYPE_INTERVAL))
					.projection(Tokens.PRJ_ONLY_START_AND_END).iterator();
			while (cursor.hasNext()) {
				BsonDocument doc = cursor.next();
				intervalPropertyKeys.add(new LongInterval(doc.getDateTime(Tokens.START).getValue(),
						doc.getDateTime(Tokens.END).getValue()));
			}
		} else {
			MongoCursor<BsonDocument> cursor = graph.getEdgeCollection().find(
					new BsonDocument(Tokens.EDGE, new BsonString(this.id)).append(Tokens.TYPE, Tokens.TYPE_INTERVAL))
					.projection(Tokens.PRJ_ONLY_START_AND_END).iterator();
			while (cursor.hasNext()) {
				BsonDocument doc = cursor.next();
				intervalPropertyKeys.add(new LongInterval(doc.getDateTime(Tokens.START).getValue(),
						doc.getDateTime(Tokens.END).getValue()));
			}
		}
		return intervalPropertyKeys;
	}

	/**
	 * 
	 * @return treeSet of temporalProperty key set
	 */
	public TreeSet<LongInterval> getIntervals(Long[] timestamps, AC s, AC e) {
		TreeSet<LongInterval> intervalPropertyKeys = new TreeSet<LongInterval>();

		if (this instanceof ChronoVertex) {
			BsonDocument filter = new BsonDocument(Tokens.VERTEX, new BsonString(this.id)).append(Tokens.TYPE,
					Tokens.TYPE_INTERVAL);
			BsonArray bsonInterval = new BsonArray();
			for (Long timestamp : timestamps) {
				bsonInterval
						.add(new BsonDocument(Tokens.START, new BsonDocument(s.toString(), new BsonDateTime(timestamp)))
								.append(Tokens.END, new BsonDocument(e.toString(), new BsonDateTime(timestamp))));
			}
			filter.append(Tokens.C.$or.toString(), bsonInterval);

			MongoCursor<BsonDocument> cursor = graph.getVertexCollection().find(filter)
					.projection(Tokens.PRJ_ONLY_START_AND_END).iterator();
			while (cursor.hasNext()) {
				BsonDocument doc = cursor.next();
				intervalPropertyKeys.add(new LongInterval(doc.getDateTime(Tokens.START).getValue(),
						doc.getDateTime(Tokens.END).getValue()));
			}
		} else {
			BsonDocument filter = new BsonDocument(Tokens.EDGE, new BsonString(this.id)).append(Tokens.TYPE,
					Tokens.TYPE_INTERVAL);
			BsonArray bsonInterval = new BsonArray();
			for (Long timestamp : timestamps) {
				bsonInterval
						.add(new BsonDocument(Tokens.START, new BsonDocument(s.toString(), new BsonDateTime(timestamp)))
								.append(Tokens.END, new BsonDocument(e.toString(), new BsonDateTime(timestamp))));
			}
			filter.append(Tokens.C.$or.toString(), bsonInterval);

			MongoCursor<BsonDocument> cursor = graph.getEdgeCollection().find(filter)
					.projection(Tokens.PRJ_ONLY_START_AND_END).iterator();
			while (cursor.hasNext()) {
				BsonDocument doc = cursor.next();
				intervalPropertyKeys.add(new LongInterval(doc.getDateTime(Tokens.START).getValue(),
						doc.getDateTime(Tokens.END).getValue()));
			}
		}
		return intervalPropertyKeys;
	}

	/**
	 *
	 * @return treeSet of temporalProperty key set
	 */
	public TreeSet<LongInterval> getIntervals(LongInterval[] intervals, AC ss, AC se, AC es, AC ee) {
		TreeSet<LongInterval> intervalPropertyKeys = new TreeSet<LongInterval>();

		if (this instanceof ChronoVertex) {
			BsonDocument filter = new BsonDocument(Tokens.VERTEX, new BsonString(this.id)).append(Tokens.TYPE,
					Tokens.TYPE_INTERVAL);
			BsonArray bsonInterval = new BsonArray();
			for (LongInterval interval : intervals) {
				bsonInterval.add(LongInterval.getTemporalRelationFilterQuery(interval, ss, se, es, ee));
			}
			filter.append(Tokens.C.$or.toString(), bsonInterval);

			MongoCursor<BsonDocument> cursor = graph.getVertexCollection().find(filter)
					.projection(Tokens.PRJ_ONLY_START_AND_END).iterator();
			while (cursor.hasNext()) {
				BsonDocument doc = cursor.next();
				intervalPropertyKeys.add(new LongInterval(doc.getDateTime(Tokens.START).getValue(),
						doc.getDateTime(Tokens.END).getValue()));
			}
		} else {
			BsonDocument filter = new BsonDocument(Tokens.EDGE, new BsonString(this.id)).append(Tokens.TYPE,
					Tokens.TYPE_INTERVAL);
			BsonArray bsonInterval = new BsonArray();
			for (LongInterval interval : intervals) {
				bsonInterval.add(LongInterval.getTemporalRelationFilterQuery(interval, ss, se, es, ee));
			}
			filter.append(Tokens.C.$or.toString(), bsonInterval);

			MongoCursor<BsonDocument> cursor = graph.getEdgeCollection().find(filter)
					.projection(Tokens.PRJ_ONLY_START_AND_END).iterator();
			while (cursor.hasNext()) {
				BsonDocument doc = cursor.next();
				intervalPropertyKeys.add(new LongInterval(doc.getDateTime(Tokens.START).getValue(),
						doc.getDateTime(Tokens.END).getValue()));
			}
		}
		return intervalPropertyKeys;
	}

	/**
	 * 
	 * @param left
	 * @param ss:
	 *            left <ss> intervalInDB.start
	 * @param se:
	 *            left <se> intervalInDB.end
	 *
	 * @return TreeSet<LongInterval>
	 */
	public TreeSet<LongInterval> getIntervals(long left, AC ss, AC se) {
		TreeSet<LongInterval> intervalPropertyKeys = new TreeSet<LongInterval>();

		if (this instanceof ChronoVertex) {
			BsonDocument filter = new BsonDocument(Tokens.VERTEX, new BsonString(this.id)).append(Tokens.TYPE,
					Tokens.TYPE_INTERVAL);
			filter = LongInterval.addTemporalRelationFilterQuery(filter, left, ss, se);
			MongoCursor<BsonDocument> cursor = graph.getVertexCollection().find(filter)
					.projection(Tokens.PRJ_ONLY_START_AND_END).iterator();
			while (cursor.hasNext()) {
				BsonDocument doc = cursor.next();
				intervalPropertyKeys.add(new LongInterval(doc.getDateTime(Tokens.START).getValue(),
						doc.getDateTime(Tokens.END).getValue()));
			}
		} else {
			BsonDocument filter = new BsonDocument(Tokens.EDGE, new BsonString(this.id)).append(Tokens.TYPE,
					Tokens.TYPE_INTERVAL);
			filter = LongInterval.addTemporalRelationFilterQuery(filter, left, ss, se);
			MongoCursor<BsonDocument> cursor = graph.getEdgeCollection().find(filter)
					.projection(Tokens.PRJ_ONLY_START_AND_END).iterator();
			while (cursor.hasNext()) {
				BsonDocument doc = cursor.next();
				intervalPropertyKeys.add(new LongInterval(doc.getDateTime(Tokens.START).getValue(),
						doc.getDateTime(Tokens.END).getValue()));
			}
		}
		return intervalPropertyKeys;
	}

	/**
	 * 
	 * @param left
	 * @param ss:
	 *            left.start <ss> intervalInDB.start
	 * @param se:
	 *            left.start <se> intervalInDB.end
	 * @param es:
	 *            left.end <es> intervalInDB.start
	 * @param ee:
	 *            left.end <ee> intervalInDB.end
	 * @return TreeSet<LongInterval>
	 */
	public TreeSet<LongInterval> getIntervals(LongInterval left, AC ss, AC se, AC es, AC ee) {
		TreeSet<LongInterval> intervalPropertyKeys = new TreeSet<LongInterval>();

		if (this instanceof ChronoVertex) {
			BsonDocument filter = new BsonDocument(Tokens.VERTEX, new BsonString(this.id)).append(Tokens.TYPE,
					Tokens.TYPE_INTERVAL);
			filter = LongInterval.addTemporalRelationFilterQuery(filter, left, ss, se, es, ee);
			MongoCursor<BsonDocument> cursor = graph.getVertexCollection().find(filter)
					.projection(Tokens.PRJ_ONLY_START_AND_END).iterator();
			while (cursor.hasNext()) {
				BsonDocument doc = cursor.next();
				intervalPropertyKeys.add(new LongInterval(doc.getDateTime(Tokens.START).getValue(),
						doc.getDateTime(Tokens.END).getValue()));
			}
		} else {
			BsonDocument filter = new BsonDocument(Tokens.EDGE, new BsonString(this.id)).append(Tokens.TYPE,
					Tokens.TYPE_INTERVAL);
			filter = LongInterval.addTemporalRelationFilterQuery(filter, left, ss, se, es, ee);
			MongoCursor<BsonDocument> cursor = graph.getEdgeCollection().find(filter)
					.projection(Tokens.PRJ_ONLY_START_AND_END).iterator();
			while (cursor.hasNext()) {
				BsonDocument doc = cursor.next();
				intervalPropertyKeys.add(new LongInterval(doc.getDateTime(Tokens.START).getValue(),
						doc.getDateTime(Tokens.END).getValue()));
			}
		}
		return intervalPropertyKeys;
	}

	/**
	 * 
	 * @param left
	 * @param ss:
	 *            left <ss> intervalInDB.start
	 * @param se:
	 *            left <se> intervalInDB.end
	 *
	 * @return TreeSet<LongInterval>
	 */
	public LongInterval getInterval(long left, AC ss, AC se) {
		BsonDocument matched = null;
		if (ss != null && (ss.equals(AC.$gte) || ss.equals(AC.$gt))) {
			if (this instanceof ChronoVertex) {
				BsonDocument filter = new BsonDocument(Tokens.VERTEX, new BsonString(this.id)).append(Tokens.TYPE,
						Tokens.TYPE_INTERVAL);
				filter = LongInterval.addTemporalRelationFilterQuery(filter, left, ss, se);
				matched = graph.getVertexCollection().find(filter).projection(Tokens.PRJ_ONLY_START_AND_END)
						.sort(Tokens.SORT_START_ASC).limit(1).first();
			} else {
				BsonDocument filter = new BsonDocument(Tokens.EDGE, new BsonString(this.id)).append(Tokens.TYPE,
						Tokens.TYPE_INTERVAL);
				filter = LongInterval.addTemporalRelationFilterQuery(filter, left, ss, se);
				matched = graph.getEdgeCollection().find(filter).projection(Tokens.PRJ_ONLY_START_AND_END)
						.sort(Tokens.SORT_START_ASC).limit(1).first();
			}
		} else if (se != null && (se.equals(AC.$lte) || se.equals(AC.$lt))) {
			if (this instanceof ChronoVertex) {
				BsonDocument filter = new BsonDocument(Tokens.VERTEX, new BsonString(this.id)).append(Tokens.TYPE,
						Tokens.TYPE_INTERVAL);
				filter = LongInterval.addTemporalRelationFilterQuery(filter, left, ss, se);
				matched = graph.getVertexCollection().find(filter).projection(Tokens.PRJ_ONLY_START_AND_END)
						.sort(Tokens.SORT_START_DESC).limit(1).first();
			} else {
				BsonDocument filter = new BsonDocument(Tokens.EDGE, new BsonString(this.id)).append(Tokens.TYPE,
						Tokens.TYPE_INTERVAL);
				filter = LongInterval.addTemporalRelationFilterQuery(filter, left, ss, se);
				matched = graph.getEdgeCollection().find(filter).projection(Tokens.PRJ_ONLY_START_AND_END)
						.sort(Tokens.SORT_START_DESC).limit(1).first();
			}
		} else {
			if (this instanceof ChronoVertex) {
				BsonDocument filter = new BsonDocument(Tokens.VERTEX, new BsonString(this.id)).append(Tokens.TYPE,
						Tokens.TYPE_INTERVAL);
				filter = LongInterval.addTemporalRelationFilterQuery(filter, left, ss, se);
				matched = graph.getVertexCollection().find(filter).projection(Tokens.PRJ_ONLY_START_AND_END).limit(1)
						.first();
			} else {
				BsonDocument filter = new BsonDocument(Tokens.EDGE, new BsonString(this.id)).append(Tokens.TYPE,
						Tokens.TYPE_INTERVAL);
				filter = LongInterval.addTemporalRelationFilterQuery(filter, left, ss, se);
				matched = graph.getEdgeCollection().find(filter).projection(Tokens.PRJ_ONLY_START_AND_END).limit(1)
						.first();
			}
		}
		if (matched != null) {
			return new LongInterval(matched.getDateTime(Tokens.START).getValue(),
					matched.getDateTime(Tokens.END).getValue());
		} else
			return null;
	}

	/**
	 * 
	 * @param left
	 * @param ss:
	 *            left.start <ss> intervalInDB.start
	 * @param se:
	 *            left.start <se> intervalInDB.end
	 * @param es:
	 *            left.end <es> intervalInDB.start
	 * @param ee:
	 *            left.end <ee> intervalInDB.end
	 * @return LongInterval
	 */
	public LongInterval getInterval(LongInterval left, AC ss, AC se, AC es, AC ee) {
		BsonDocument matched = null;
		if (ss != null && (ss.equals(AC.$gte) || ss.equals(AC.$gt))) {
			if (this instanceof ChronoVertex) {
				BsonDocument filter = new BsonDocument(Tokens.VERTEX, new BsonString(this.id)).append(Tokens.TYPE,
						Tokens.TYPE_INTERVAL);
				filter = LongInterval.addTemporalRelationFilterQuery(filter, left, ss, se, es, ee);
				matched = graph.getVertexCollection().find(filter).projection(Tokens.PRJ_ONLY_START_AND_END)
						.sort(Tokens.SORT_START_ASC).limit(1).first();
			} else {
				BsonDocument filter = new BsonDocument(Tokens.EDGE, new BsonString(this.id)).append(Tokens.TYPE,
						Tokens.TYPE_INTERVAL);
				filter = LongInterval.addTemporalRelationFilterQuery(filter, left, ss, se, es, ee);
				matched = graph.getEdgeCollection().find(filter).projection(Tokens.PRJ_ONLY_START_AND_END)
						.sort(Tokens.SORT_START_ASC).limit(1).first();
			}
		} else if (se != null && (se.equals(AC.$lte) || se.equals(AC.$lt))) {
			if (this instanceof ChronoVertex) {
				BsonDocument filter = new BsonDocument(Tokens.VERTEX, new BsonString(this.id)).append(Tokens.TYPE,
						Tokens.TYPE_INTERVAL);
				filter = LongInterval.addTemporalRelationFilterQuery(filter, left, ss, se, es, ee);
				matched = graph.getVertexCollection().find(filter).projection(Tokens.PRJ_ONLY_START_AND_END)
						.sort(Tokens.SORT_START_DESC).limit(1).first();
			} else {
				BsonDocument filter = new BsonDocument(Tokens.EDGE, new BsonString(this.id)).append(Tokens.TYPE,
						Tokens.TYPE_INTERVAL);
				filter = LongInterval.addTemporalRelationFilterQuery(filter, left, ss, se, es, ee);
				matched = graph.getEdgeCollection().find(filter).projection(Tokens.PRJ_ONLY_START_AND_END)
						.sort(Tokens.SORT_START_DESC).limit(1).first();
			}
		} else {
			if (this instanceof ChronoVertex) {
				BsonDocument filter = new BsonDocument(Tokens.VERTEX, new BsonString(this.id)).append(Tokens.TYPE,
						Tokens.TYPE_INTERVAL);
				filter = LongInterval.addTemporalRelationFilterQuery(filter, left, ss, se, es, ee);
				matched = graph.getVertexCollection().find(filter).projection(Tokens.PRJ_ONLY_START_AND_END).limit(1)
						.first();
			} else {
				BsonDocument filter = new BsonDocument(Tokens.EDGE, new BsonString(this.id)).append(Tokens.TYPE,
						Tokens.TYPE_INTERVAL);
				filter = LongInterval.addTemporalRelationFilterQuery(filter, left, ss, se, es, ee);
				matched = graph.getEdgeCollection().find(filter).projection(Tokens.PRJ_ONLY_START_AND_END).limit(1)
						.first();
			}
		}
		if (matched != null) {
			return new LongInterval(matched.getDateTime(Tokens.START).getValue(),
					matched.getDateTime(Tokens.END).getValue());
		} else
			return null;
	}

	/**
	 * @deprecated use toString
	 */
	@Override
	public Object getId() {
		return this.id;
	}
}
