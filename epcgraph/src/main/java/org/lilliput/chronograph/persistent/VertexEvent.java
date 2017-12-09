package org.lilliput.chronograph.persistent;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bson.BsonArray;
import org.bson.BsonBoolean;
import org.bson.BsonDateTime;
import org.bson.BsonDocument;
import org.bson.BsonString;
import org.bson.BsonValue;
import org.lilliput.chronograph.common.ExceptionFactory;
import org.lilliput.chronograph.common.LongInterval;
import org.lilliput.chronograph.common.TemporalType;
import org.lilliput.chronograph.common.Tokens;
import org.lilliput.chronograph.common.Tokens.AC;
import org.lilliput.chronograph.common.Tokens.Position;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Element;

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
public class VertexEvent implements Element {

	private ChronoVertex vertex;
	private Long timestamp;
	private LongInterval interval;
	private TemporalType temporalType;

	protected final ChronoGraph graph;

	/**
	 * Create timestampVertexEvent
	 * 
	 * @param chronoGraph
	 * @param chronoVertex
	 * @param timestamp
	 */
	public VertexEvent(ChronoGraph graph, ChronoVertex vertex, Long timestamp) {
		this.graph = graph;
		this.vertex = vertex;
		this.timestamp = timestamp;
		this.interval = null;
		this.temporalType = TemporalType.TIMESTAMP;
	}

	public VertexEvent(ChronoGraph graph, String vertexEventID) {
		this.graph = graph;
		String[] arr = vertexEventID.split("-");
		this.vertex = new ChronoVertex(arr[0], graph);
		this.timestamp = Long.parseLong(arr[1]);
		this.interval = null;
		this.temporalType = TemporalType.TIMESTAMP;
	}

	/**
	 * Create intervalVertexEvent
	 * 
	 * @param chronoGraph
	 * @param chronoVertex
	 * @param interval
	 */
	public VertexEvent(ChronoGraph graph, ChronoVertex vertex, LongInterval interval) {
		this.graph = graph;
		this.vertex = vertex;
		this.timestamp = null;
		this.interval = interval;
		this.temporalType = TemporalType.INTERVAL;
	}

	public VertexEvent getThis() {
		return this;
	}

	/**
	 * @return ChronoVertex which has this
	 */
	public ChronoVertex getVertex() {
		return vertex;
	}

	public String getVertexID() {
		return vertex.toString();
	}

	/**
	 * Set ChronoVertex to have this
	 * 
	 * @param chronoVertex
	 */
	public void setVertex(ChronoVertex vertex) {
		this.vertex = vertex;
	}

	/**
	 * @return readable string representing VertexEvent
	 */
	public String toString() {
		if (this.temporalType.equals(TemporalType.TIMESTAMP))
			return vertex.toString() + "-" + timestamp;
		else
			return vertex.toString() + "-(" + interval.getStart() + "," + interval.getEnd() + ")";
	}

	/**
	 * @return temporal type
	 */
	public TemporalType getTemporalType() {
		return this.temporalType;
	}

	/**
	 * @return current timestamp head. return null if the type is
	 *         TemporalType.INTERVAL
	 */
	public Long getTimestamp() {
		return timestamp;
	}

	/**
	 * Set timestamp. Set temporalType as TIMESTAMP. Set interval to null
	 * 
	 * @param timestamp
	 * @return timestamp
	 */
	public void setTimestamp(Long timestamp) {
		this.temporalType = TemporalType.TIMESTAMP;
		this.timestamp = timestamp;
		this.interval = null;
	}

	/**
	 * Change TemporalType to TIMESTAMP if the type is timestamp, just return
	 * timestamp
	 * 
	 * @param pos
	 *            Indicater for first or last timestamp of interval
	 * @return timestamp
	 */
	public Long changeToTimestamp(Position pos) {
		if (this.temporalType.equals(TemporalType.TIMESTAMP))
			return timestamp;
		else {
			if (pos.equals(Position.first)) {
				this.temporalType = TemporalType.TIMESTAMP;
				this.timestamp = this.interval.getStart();
				this.interval = null;
				return this.timestamp;
			} else {
				this.temporalType = TemporalType.TIMESTAMP;
				this.timestamp = this.interval.getEnd();
				this.interval = null;
				return this.timestamp;
			}
		}
	}

	/**
	 * @return current interval head. return null if the type is
	 *         TemporalType.TIMESTAMP
	 */
	public LongInterval getInterval() {
		return interval;
	}

	/**
	 * Set interval. Set temporalType as INTERVAL. Set timestamp to null
	 * 
	 * @param interval
	 */
	public void setInterval(LongInterval interval) {
		this.temporalType = TemporalType.INTERVAL;
		this.timestamp = null;
		this.interval = interval;
	}

	/**
	 * Change TemporalType to INTERVAL if the type is interval, just return
	 * interval. Set interval with current timestamp and the difference
	 * 
	 * @param difference
	 *            unsigned long value
	 * @return interval LongRange
	 */
	public LongInterval changeToInterval(Long difference) {
		if (this.temporalType.equals(TemporalType.INTERVAL))
			return this.interval;
		else {
			this.interval = new LongInterval(timestamp.longValue(), timestamp.longValue() + difference.longValue());
			this.timestamp = null;
			this.temporalType = TemporalType.INTERVAL;
			return interval;
		}
	}

	/**
	 * Get existing next timestamp greater than equal to current timestamp
	 * 
	 * @return existing next timestamp or null if not exist or the current temporal
	 *         type is INTERVAL
	 */
	public Long getCeilingTimestamp() {
		return vertex.getCeilingTimestamp(timestamp);
	}

	/**
	 * Get existing next timestamp greater than equal to current timestamp
	 * 
	 * @param pos
	 *            used if the current temporal type is INTERVAL
	 * @return
	 */
	public Long getCeilingTimestamp(Position pos) {
		Long t = getTimestamp(pos);
		if (t != null)
			return vertex.getCeilingTimestamp(t);
		return null;
	}

	/**
	 * Get existing higher timestamp based on current timestamp
	 * 
	 * @return existing higher timestamp or null if not exist or the current
	 *         temporal type is INTERVAL
	 */
	public Long getHigherTimestamp() {
		return vertex.getHigherTimestamp(timestamp);
	}

	/**
	 * Get existing higher timestamp based on current timestamp
	 * 
	 * @param pos
	 *            used if the current temporal type is INTERVAL
	 * @return
	 */
	public Long getHigherTimestamp(Position pos) {
		Long t = getTimestamp(pos);
		if (t != null)
			return vertex.getHigherTimestamp(t);
		return null;
	}

	/**
	 * Set existing ceiling timestamp to current timestamp
	 * 
	 * @return 1) ceiling timestamp if exists. 2) null if current type is INTERVAL
	 */
	public Long setCeilingTimestamp() {
		Long ceiling = getCeilingTimestamp();
		if (ceiling != null) {
			timestamp = ceiling;
			return timestamp;
		} else
			return null;
	}

	/**
	 * Set existing ceiling timestamp to current timestamp
	 * 
	 * @param pos
	 *            used if the current temporal type is INTERVAL
	 * @return ceiling timestamp. If null, there is no such timestamp, and timestamp
	 *         is not set up. If there is ceiling timestamp, the temporal type can
	 *         change
	 */
	public Long setCeilingTimestamp(Position pos) {
		Long ceiling = getCeilingTimestamp(pos);
		if (ceiling != null) {
			timestamp = ceiling;
			return timestamp;
		} else
			return null;
	}

	/**
	 * Set existing higher timestamp to current timestamp
	 * 
	 * @return 1) next timestamp if exists. 2) current timestamp if there is no next
	 *         timestamp 3) null if current type is INTERVAL
	 */
	public Long setHigherTimestamp() {
		if (temporalType.equals(TemporalType.INTERVAL))
			return null;
		Long higher = getHigherTimestamp();
		if (higher != null) {
			timestamp = higher;
			return timestamp;
		} else
			return null;
	}

	/**
	 * Set existing NextTimestamp to current timestamp
	 * 
	 * @param pos
	 *            used if the current temporal type is INTERVAL
	 * @return next timestamp. If null, there is no timestamp key greater than
	 *         current timestamp, and timestamp is not setted up. If there is next
	 *         timestamp, the temporal type changes
	 */
	public Long setHigherTimestamp(Position pos) {
		Long higher = getHigherTimestamp(pos);
		if (higher != null) {
			timestamp = higher;
			interval = null;
			temporalType = TemporalType.TIMESTAMP;
			return timestamp;
		} else
			return null;
	}

	/**
	 * Get existing previous timestamp less than equal to current timestamp
	 * 
	 * @return existing previous timestamp or null if not exist or the current
	 *         temporal type is INTERVAL
	 */
	public Long getFloorTimestamp() {
		return vertex.getFloorTimestamp(timestamp);
	}

	/**
	 * Get existing previous timestamp greater than equal to current timestamp
	 * 
	 * @param pos
	 *            used if the current temporal type is INTERVAL
	 * @return
	 */
	public Long getFloorTimestamp(Position pos) {
		Long t = getTimestamp(pos);
		if (t != null)
			return vertex.getFloorTimestamp(t);
		return null;
	}

	/**
	 * Get existing lower timestamp based on current timestamp
	 * 
	 * @return existing lower timestamp or null if not exist or the current temporal
	 *         type is INTERVAL
	 */
	public Long getLowerTimestamp() {
		return vertex.getLowerTimestamp(timestamp);
	}

	/**
	 * Get existing lower timestamp based on current timestamp
	 * 
	 * @param pos
	 *            used if the current temporal type is INTERVAL
	 * @return
	 */
	public Long getLowerTimestamp(Position pos) {
		Long t = getTimestamp(pos);
		if (t != null)
			return vertex.getLowerTimestamp(t);
		return null;
	}

	/**
	 * Set existing floor timestamp to current timestamp
	 * 
	 * @return 1) floor timestamp if exists. 2) null if current type is INTERVAL
	 */
	public Long setFloorTimestamp() {
		Long floor = getFloorTimestamp();
		if (floor != null) {
			timestamp = floor;
			return timestamp;
		} else
			return null;
	}

	/**
	 * Set existing floor timestamp to current timestamp
	 * 
	 * @param pos
	 *            used if the current temporal type is INTERVAL
	 * @return floor timestamp. If null, there is no such timestamp, and timestamp
	 *         is not set up. If there is ceiling timestamp, the temporal type can
	 *         change
	 */
	public Long setFloorTimestamp(Position pos) {
		Long floor = getCeilingTimestamp(pos);
		if (floor != null) {
			timestamp = floor;
			return timestamp;
		} else
			return null;
	}

	/**
	 * Set existing lower timestamp to current timestamp
	 * 
	 * @return 1) lower timestamp if exists. 2) current timestamp if there is no
	 *         next timestamp 3) null if current type is INTERVAL
	 */
	public Long setLowerTimestamp() {
		if (temporalType.equals(TemporalType.INTERVAL))
			return null;
		Long lower = getLowerTimestamp();
		if (lower != null) {
			timestamp = lower;
			return timestamp;
		} else
			return null;
	}

	/**
	 * Set existing lowerTimestamp to current timestamp
	 * 
	 * @param pos
	 *            used if the current temporal type is INTERVAL
	 * @return lower timestamp. If null, there is no timestamp key greater than
	 *         current timestamp, and timestamp is not setted up. If there is next
	 *         timestamp, the temporal type changes
	 */
	public Long setLowerTimestamp(Position pos) {
		Long lower = getLowerTimestamp(pos);
		if (lower != null) {
			timestamp = lower;
			interval = null;
			temporalType = TemporalType.TIMESTAMP;
			return timestamp;
		} else
			return null;
	}

	/**
	 * @return iterable Long interval. null if current type is INTERVAL emptry
	 *         treeset if there is no element
	 */
	public Iterable<LongInterval> getIntervals(AC ss, AC se) {
		if (temporalType.equals(TemporalType.INTERVAL))
			return null;
		return vertex.getIntervals(timestamp, ss, se);
	}

	/**
	 * @return iterable Long interval. null if current type is INTERVAL emptry
	 *         treeset if there is no element
	 */
	public Iterable<LongInterval> getIntervals(AC ss, AC se, AC es, AC ee) {
		if (temporalType.equals(TemporalType.TIMESTAMP))
			return null;
		return vertex.getIntervals(interval, ss, se, es, ee);
	}

	/**
	 * Set and Return ceiling interval compared to current timestamp
	 * 
	 * @return ceiling interval. null if current type is INTERVAL or there is no
	 *         such element.
	 */
	public LongInterval setCeilingInterval() {
		if (temporalType.equals(TemporalType.INTERVAL))
			return null;
		return vertex.getInterval(timestamp, AC.$gte, AC.$gte);
	}

	/**
	 * Set and Return next interval compared to current timestamp
	 * 
	 * @return ceiling interval. null if current type is INTERVAL or there is no
	 *         such element.
	 */
	public LongInterval setCeilingInterval(Position pos) {
		long t = getTimestamp(pos);
		return vertex.getInterval(t, AC.$gte, AC.$gte);
	}

	/**
	 * Set and Return ceiling interval compared to current timestamp
	 * 
	 * @return higher interval. null if current type is INTERVAL or there is no such
	 *         element.
	 */
	public LongInterval setHigherInterval() {
		if (temporalType.equals(TemporalType.INTERVAL))
			return null;
		return vertex.getInterval(timestamp, AC.$gt, AC.$gt);
	}

	/**
	 * Set and Return next interval compared to current timestamp
	 * 
	 * @return higher interval. null if current type is INTERVAL or there is no such
	 *         element.
	 */
	public LongInterval setHigherInterval(Position pos) {
		long t = getTimestamp(pos);
		return vertex.getInterval(t, AC.$gt, AC.$gt);
	}

	/**
	 * Set and Return ceiling interval compared to current timestamp
	 * 
	 * @return floor interval. null if current type is INTERVAL or there is no such
	 *         element.
	 */
	public LongInterval setFloorInterval() {
		if (temporalType.equals(TemporalType.INTERVAL))
			return null;
		return vertex.getInterval(timestamp, AC.$lte, AC.$lte);
	}

	/**
	 * Set and Return next interval compared to current timestamp
	 * 
	 * @return floor interval. null if current type is INTERVAL or there is no such
	 *         element.
	 */
	public LongInterval setFloorInterval(Position pos) {
		long t = getTimestamp(pos);
		return vertex.getInterval(t, AC.$lte, AC.$lte);
	}

	/**
	 * Set and Return ceiling interval compared to current timestamp
	 * 
	 * @return lower interval. null if current type is INTERVAL or there is no such
	 *         element.
	 */
	public LongInterval setLowerInterval() {
		if (temporalType.equals(TemporalType.INTERVAL))
			return null;
		return vertex.getInterval(timestamp, AC.$lt, AC.$lt);
	}

	/**
	 * Set and Return next interval compared to current timestamp
	 * 
	 * @return lower interval. null if current type is INTERVAL or there is no such
	 *         element.
	 */
	public LongInterval setLowerInterval(Position pos) {
		long t = getTimestamp(pos);
		return vertex.getInterval(t, AC.$lte, AC.$lte);
	}

	/**
	 * 
	 * @param direction
	 * @param labels
	 * @param edgeType
	 * @param timestampPosition
	 * @param ss
	 * @param se
	 * @param es
	 * @param ee
	 * @return
	 */
	public Iterable<EdgeEvent> getEdgeEvents(final Direction direction, final BsonArray labels,
			TemporalType typeOfEdgeEvent, final AC tt, final AC s, final AC e, final AC ss, final AC se, final AC es,
			final AC ee) {
		if (typeOfEdgeEvent == null)
			typeOfEdgeEvent = this.temporalType;

		Set<ChronoEdge> edgeSet = vertex.getChronoEdgeSet(direction, labels, Integer.MAX_VALUE);

		if (temporalType.equals(TemporalType.TIMESTAMP) && typeOfEdgeEvent.equals(TemporalType.TIMESTAMP)) {
			// T -> T
			List<EdgeEvent> ret = edgeSet.parallelStream().map(edge -> {
				Long t = edge.getTimestamp(timestamp, tt);
				return edge.pickTimestamp(t);
			}).filter(edgeEvent -> exFilter(edgeEvent)).collect(Collectors.toList());
			return ret;
		} else if (temporalType.equals(TemporalType.TIMESTAMP) && typeOfEdgeEvent.equals(TemporalType.INTERVAL)) {
			// T -> I
			return edgeSet.parallelStream().map(edge -> {
				TreeSet<LongInterval> intvSet = edge.getIntervals(timestamp, s, e);
				if (intvSet.isEmpty())
					return null;
				return edge.pickInterval(intvSet.first());
			}).filter(edge -> edge != null).collect(Collectors.toSet());
		} else if (temporalType.equals(TemporalType.INTERVAL) && typeOfEdgeEvent.equals(TemporalType.TIMESTAMP)) {
			// I -> T
			return edgeSet.parallelStream().map(edge -> {
				TreeSet<Long> tSet = edge.getTimestamps(interval, s, e);
				if (tSet.isEmpty())
					return null;
				return edge.pickTimestamp(tSet.first());
			}).filter(edge -> edge != null).collect(Collectors.toSet());
		} else {
			// I -> I
			return edgeSet.parallelStream().map(edge -> {
				TreeSet<LongInterval> intvSet = edge.getIntervals(interval, ss, se, es, ee);
				if (intvSet.isEmpty())
					return null;
				return edge.pickInterval(intvSet.first());
			}).filter(edge -> edge != null).collect(Collectors.toSet());
		}
	}

	private boolean exFilter(EdgeEvent ee) {
		if (ee == null)
			return false;
		return true;
	}

	public Stream<EdgeEvent> getEdgeEventStream(final Direction direction, final BsonArray labels,
			TemporalType typeOfEdgeEvent, final AC tt, final AC s, final AC e, final AC ss, final AC se, final AC es,
			final AC ee, final boolean setParallel) {
		if (typeOfEdgeEvent == null)
			typeOfEdgeEvent = this.temporalType;

		Stream<ChronoEdge> edgeStream = vertex.getChronoEdgeStream(direction, labels, Integer.MAX_VALUE, setParallel);

		if (temporalType.equals(TemporalType.TIMESTAMP) && typeOfEdgeEvent.equals(TemporalType.TIMESTAMP)) {
			// T -> T
			return edgeStream.map(edge -> {
				Long t = edge.getTimestamp(timestamp, tt);
				return edge.pickTimestamp(t);
			}).filter(edgeEvent -> exFilter(edgeEvent));
		} else if (temporalType.equals(TemporalType.TIMESTAMP) && typeOfEdgeEvent.equals(TemporalType.INTERVAL)) {
			// T -> I
			return edgeStream.map(edge -> {
				TreeSet<LongInterval> intvSet = edge.getIntervals(timestamp, s, e);
				if (intvSet.isEmpty())
					return null;
				return edge.pickInterval(intvSet.first());
			}).filter(edge -> edge != null);
		} else if (temporalType.equals(TemporalType.INTERVAL) && typeOfEdgeEvent.equals(TemporalType.TIMESTAMP)) {
			// I -> T
			return edgeStream.map(edge -> {
				TreeSet<Long> tSet = edge.getTimestamps(interval, s, e);
				if (tSet.isEmpty())
					return null;
				return edge.pickTimestamp(tSet.first());
			}).filter(edge -> edge != null);
		} else {
			// I -> I
			return edgeStream.map(edge -> {
				TreeSet<LongInterval> intvSet = edge.getIntervals(interval, ss, se, es, ee);
				if (intvSet.isEmpty())
					return null;
				return edge.pickInterval(intvSet.first());
			}).filter(edge -> edge != null);
		}
	}

	public Set<EdgeEvent> getEdgeEventSet(final Direction direction, final BsonArray labels,
			TemporalType typeOfEdgeEvent, final AC tt, final AC s, final AC e, final AC ss, final AC se, final AC es,
			final AC ee) {
		if (typeOfEdgeEvent == null)
			typeOfEdgeEvent = this.temporalType;

		Set<ChronoEdge> edgeSet = vertex.getChronoEdgeSet(direction, labels, Integer.MAX_VALUE);

		if (temporalType.equals(TemporalType.TIMESTAMP) && typeOfEdgeEvent.equals(TemporalType.TIMESTAMP)) {
			// T -> T
			Set<EdgeEvent> ret = edgeSet.parallelStream().map(edge -> {
				Long t = edge.getTimestamp(timestamp, tt);
				return edge.pickTimestamp(t);
			}).filter(edgeEvent -> exFilter(edgeEvent)).collect(Collectors.toSet());
			return ret;
		} else if (temporalType.equals(TemporalType.TIMESTAMP) && typeOfEdgeEvent.equals(TemporalType.INTERVAL)) {
			// T -> I
			return edgeSet.parallelStream().map(edge -> {
				TreeSet<LongInterval> intvSet = edge.getIntervals(timestamp, s, e);
				if (intvSet.isEmpty())
					return null;
				return edge.pickInterval(intvSet.first());
			}).filter(edge -> edge != null).collect(Collectors.toSet());
		} else if (temporalType.equals(TemporalType.INTERVAL) && typeOfEdgeEvent.equals(TemporalType.TIMESTAMP)) {
			// I -> T
			return edgeSet.parallelStream().map(edge -> {
				TreeSet<Long> tSet = edge.getTimestamps(interval, s, e);
				if (tSet.isEmpty())
					return null;
				return edge.pickTimestamp(tSet.first());
			}).filter(edge -> edge != null).collect(Collectors.toSet());
		} else {
			// I -> I
			return edgeSet.parallelStream().map(edge -> {
				TreeSet<LongInterval> intvSet = edge.getIntervals(interval, ss, se, es, ee);
				if (intvSet.isEmpty())
					return null;
				return edge.pickInterval(intvSet.first());
			}).filter(edge -> edge != null).collect(Collectors.toSet());
		}
	}

	public Stream<VertexEvent> getVertexEventStream(final Direction direction, final BsonArray labels,
			TemporalType typeOfVertexEvent, final AC tt, final AC s, final AC e, final AC ss, final AC se, final AC es,
			final AC ee, final Position pos, final boolean setParallel) {

		if (typeOfVertexEvent == null)
			typeOfVertexEvent = this.temporalType;

		final Stream<ChronoEdge> edgeStream = vertex.getChronoEdgeStream(direction, labels, Integer.MAX_VALUE,
				setParallel);

		if (temporalType.equals(TemporalType.TIMESTAMP) && typeOfVertexEvent.equals(TemporalType.TIMESTAMP)) {
			// T -> T
			return edgeStream.map(edge -> {
				Long t = edge.getTimestamp(timestamp, tt);
				return edge.pickTimestamp(t);
			}).filter(edge -> edge != null).map(edgeEvent -> edgeEvent.getVertexEvent(direction.opposite()));
		} else if (temporalType.equals(TemporalType.TIMESTAMP) && typeOfVertexEvent.equals(TemporalType.TIMESTAMP)) {
			// T -> I
			return edgeStream.map(edge -> {
				TreeSet<LongInterval> intvSet = edge.getIntervals(timestamp, s, e);
				if (intvSet.isEmpty())
					return null;
				if (pos.equals(Position.first))
					return edge.pickInterval(intvSet.first());
				else
					return edge.pickInterval(intvSet.last());
			}).filter(edge -> edge != null).map(edgeEvent -> edgeEvent.getVertexEvent(direction.opposite()));
		} else if (temporalType.equals(TemporalType.TIMESTAMP) && typeOfVertexEvent.equals(TemporalType.TIMESTAMP)) {
			// I -> T
			return edgeStream.map(edge -> {
				TreeSet<Long> tSet = edge.getTimestamps(interval, s, e);
				if (tSet.isEmpty())
					return null;
				return edge.pickTimestamp(tSet.first());
			}).filter(edge -> edge != null).map(edgeEvent -> edgeEvent.getVertexEvent(direction.opposite()));
		} else {
			// I -> I
			return edgeStream.map(edge -> {
				TreeSet<LongInterval> intvSet = edge.getIntervals(interval, ss, se, es, ee);
				if (intvSet.isEmpty())
					return null;
				if (pos.equals(Position.first))
					return edge.pickInterval(intvSet.first());
				else
					return edge.pickInterval(intvSet.last());
			}).filter(edge -> edge != null).map(edgeEvent -> edgeEvent.getVertexEvent(direction.opposite()));
		}
	}

	public Set<VertexEvent> getVertexEventSet(final Direction direction, final BsonArray labels,
			TemporalType typeOfVertexEvent, final AC tt, final AC s, final AC e, final AC ss, final AC se, final AC es,
			final AC ee, Position pos) {

		// TODO: PoC
		// db.edges.createIndex({"_outV" : 1, "_t" : 1, "_inV" : 1})
		// db.EventData.createIndex({"inputEPCList.epc":1})
		BsonDocument query = new BsonDocument(Tokens.OUT_VERTEX, new BsonString(vertex.toString()));
		query.append("_t", new BsonDocument("$gte", new BsonDateTime(timestamp)));
		BsonDocument proj = new BsonDocument("_t", new BsonBoolean(true))
				.append(Tokens.IN_VERTEX, new BsonBoolean(true)).append(Tokens.ID, new BsonBoolean(false));

		// outV
		// label
		// t > gte
		HashSet<VertexEvent> ret = new HashSet<VertexEvent>();
		Iterator<BsonDocument> x = vertex.graph.getEdgeCollection().find(query).projection(proj).iterator();
		while (x.hasNext()) {
			BsonDocument d = x.next();
			String inV = d.getString("_inV").getValue();
			Long t = d.getDateTime("_t").getValue();
			VertexEvent ve = new VertexEvent(graph, inV + "-" + t);
			ret.add(ve);
		}
		return ret;

		// previous logic backup
		// if (typeOfVertexEvent == null)
		// typeOfVertexEvent = this.temporalType;
		//
		// Set<ChronoEdge> edgeSet = vertex.getChronoEdgeSet(direction, labels,
		// Integer.MAX_VALUE);
		//
		// if (temporalType.equals(TemporalType.TIMESTAMP) &&
		// typeOfVertexEvent.equals(TemporalType.TIMESTAMP)) {
		// // T -> T
		// return edgeSet.parallelStream().map(edge -> {
		// Long t = edge.getTimestamp(timestamp, tt);
		// return edge.pickTimestamp(t);
		// }).filter(edge -> edge != null).map(edgeEvent ->
		// edgeEvent.getVertexEvent(direction.opposite()))
		// .collect(Collectors.toSet());
		// } else if (temporalType.equals(TemporalType.TIMESTAMP) &&
		// typeOfVertexEvent.equals(TemporalType.TIMESTAMP)) {
		// // T -> I
		// return edgeSet.parallelStream().map(edge -> {
		// TreeSet<LongInterval> intvSet = edge.getIntervals(timestamp, s, e);
		// if (intvSet.isEmpty())
		// return null;
		// if (pos.equals(Position.first))
		// return edge.pickInterval(intvSet.first());
		// else
		// return edge.pickInterval(intvSet.last());
		// }).filter(edge -> edge != null).map(edgeEvent ->
		// edgeEvent.getVertexEvent(direction.opposite()))
		// .collect(Collectors.toSet());
		// } else if (temporalType.equals(TemporalType.TIMESTAMP) &&
		// typeOfVertexEvent.equals(TemporalType.TIMESTAMP)) {
		// // I -> T
		// return edgeSet.parallelStream().map(edge -> {
		// TreeSet<Long> tSet = edge.getTimestamps(interval, s, e);
		// if (tSet.isEmpty())
		// return null;
		// return edge.pickTimestamp(tSet.first());
		// }).filter(edge -> edge != null).map(edgeEvent ->
		// edgeEvent.getVertexEvent(direction.opposite()))
		// .collect(Collectors.toSet());
		// } else {
		// // I -> I
		// return edgeSet.parallelStream().map(edge -> {
		// TreeSet<LongInterval> intvSet = edge.getIntervals(interval, ss, se, es, ee);
		// if (intvSet.isEmpty())
		// return null;
		// if (pos.equals(Position.first))
		// return edge.pickInterval(intvSet.first());
		// else
		// return edge.pickInterval(intvSet.last());
		// }).filter(edge -> edge != null).map(edgeEvent ->
		// edgeEvent.getVertexEvent(direction.opposite()))
		// .collect(Collectors.toSet());
		// }
	}

	public Iterable<VertexEvent> getVertexEvents(final Direction direction, final BsonArray labels,
			TemporalType typeOfVertexEvent, final AC tt, final AC s, final AC e, final AC ss, final AC se, final AC es,
			final AC ee, Position pos) {

		if (typeOfVertexEvent == null)
			typeOfVertexEvent = this.temporalType;

		Set<ChronoEdge> edgeSet = vertex.getChronoEdgeSet(direction, labels, Integer.MAX_VALUE);

		if (temporalType.equals(TemporalType.TIMESTAMP) && typeOfVertexEvent.equals(TemporalType.TIMESTAMP)) {
			// T -> T
			return edgeSet.parallelStream().map(edge -> {
				Long t = edge.getTimestamp(timestamp, tt);
				return edge.pickTimestamp(t);
			}).filter(edge -> edge != null).map(edgeEvent -> edgeEvent.getVertexEvent(direction.opposite()))
					.collect(Collectors.toSet());
		} else if (temporalType.equals(TemporalType.TIMESTAMP) && typeOfVertexEvent.equals(TemporalType.TIMESTAMP)) {
			// T -> I
			return edgeSet.parallelStream().map(edge -> {
				TreeSet<LongInterval> intvSet = edge.getIntervals(timestamp, s, e);
				if (intvSet.isEmpty())
					return null;
				if (pos.equals(Position.first))
					return edge.pickInterval(intvSet.first());
				else
					return edge.pickInterval(intvSet.last());
			}).filter(edge -> edge != null).map(edgeEvent -> edgeEvent.getVertexEvent(direction.opposite()))
					.collect(Collectors.toSet());
		} else if (temporalType.equals(TemporalType.TIMESTAMP) && typeOfVertexEvent.equals(TemporalType.TIMESTAMP)) {
			// I -> T
			return edgeSet.parallelStream().map(edge -> {
				TreeSet<Long> tSet = edge.getTimestamps(interval, s, e);
				if (tSet.isEmpty())
					return null;
				return edge.pickTimestamp(tSet.first());
			}).filter(edge -> edge != null).map(edgeEvent -> edgeEvent.getVertexEvent(direction.opposite()))
					.collect(Collectors.toSet());
		} else {
			// I -> I
			return edgeSet.parallelStream().map(edge -> {
				TreeSet<LongInterval> intvSet = edge.getIntervals(interval, ss, se, es, ee);
				if (intvSet.isEmpty())
					return null;
				if (pos.equals(Position.first))
					return edge.pickInterval(intvSet.first());
				else
					return edge.pickInterval(intvSet.last());
			}).filter(edge -> edge != null).map(edgeEvent -> edgeEvent.getVertexEvent(direction.opposite()))
					.collect(Collectors.toSet());
		}
	}

	/**
	 * Interal Method
	 * 
	 * @param pos
	 * @return timestamp regardless of temporalType with pos
	 */
	private Long getTimestamp(Position pos) {
		Long t = null;
		if (temporalType.equals(TemporalType.TIMESTAMP))
			t = timestamp;
		else {
			if (pos.equals(Position.first))
				t = this.interval.getStart();
			else
				t = this.interval.getEnd();
		}
		return t;
	}

	/**
	 * @param key
	 * @return timestamp properties or interval properties
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> T getProperty(String key) {
		if (temporalType.equals(TemporalType.TIMESTAMP))
			return (T) vertex.getTimestampPropertyValue(timestamp, key);
		else
			return (T) vertex.getIntervalPropertyValue(interval, key);
	}

	/**
	 * @deprecated use getTimestampPropertyKeys or getIntervalPropertyKeys
	 */
	@Deprecated
	@Override
	public Set<String> getPropertyKeys() {
		return vertex.getPropertyKeys();
	}

	/**
	 * @return timestamps
	 */
	public Set<Long> getTimestampPropertyKeys() {
		return vertex.getTimestamps();
	}

	/**
	 * @return timestamps
	 */
	public Set<LongInterval> getIntervalPropertyKeys() {
		return vertex.getIntervals();
	}

	/**
	 * set key value only active at the timestamp or interval
	 * 
	 * @param key
	 * @param value
	 */
	@Override
	public void setProperty(String key, Object value) {
		if (!(value instanceof BsonValue))
			throw ExceptionFactory.propertyValueShouldBeInstanceOfBsonValue();
		if (temporalType.equals(TemporalType.TIMESTAMP))
			vertex.setTimestampProperty(timestamp, key, (BsonValue) value);
		else
			vertex.setIntervalProperty(interval, key, (BsonValue) value);
	}

	@Override
	public <T> T removeProperty(String key) {
		// TODO:
		return vertex.removeProperty(key);
	}

	@Deprecated
	@Override
	public void remove() {
		vertex.remove();
	}

	@Override
	public Object getId() {
		return this.vertex.id;
	}

}
