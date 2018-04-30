package org.lilliput.chronograph.common;

import org.bson.BsonArray;
import org.bson.BsonDateTime;
import org.bson.BsonDocument;
import org.lilliput.chronograph.common.Tokens.AC;
import org.lilliput.chronograph.common.Tokens.C;
import org.lilliput.chronograph.common.Tokens.Position;

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
public class LongInterval implements Comparable<LongInterval> {

	private long start;
	private long end;

	public void setStart(long start) {
		this.start = start;
	}

	public void setEnd(long end) {
		this.end = end;
	}

	public LongInterval(long start, long end) {
		this.start = start;
		this.end = end;
	}

	@Override
	public int compareTo(LongInterval anotherLongInterval) {
		return compare(this.start, anotherLongInterval.start);
	}

	public static int compare(long x, long y) {
		return (x < y) ? -1 : ((x == y) ? 0 : 1);
	}

	public long getTimestamp(final Position pos) {
		if (pos.equals(Position.first))
			return start;
		else
			return end;
	}

	public long getStart() {
		return start;
	}

	public long getEnd() {
		return end;
	}

	public long getDuration() {
		return end - start;
	}

	public boolean isValid() {
		if (getDuration() == 0)
			return false;
		return true;
	}

	public void addTimestamp(Long timestamp) {
		if (timestamp < start)
			start = timestamp;
		if (timestamp > end)
			end = timestamp;
	}

	/**
	 * start-end
	 */
	@Override
	public String toString() {
		return start + "-" + end;
	}

	public boolean containsLong(long value) {
		return value >= start && value <= end;
	}

	public boolean hasTemporalRelation(AC ss, AC se, AC es, AC ee, LongInterval intv) {
		if (isTokenPassed(ss, start, intv.start) && isTokenPassed(se, start, intv.end)
				&& isTokenPassed(es, end, intv.start) && isTokenPassed(ee, end, intv.end))
			return true;
		return false;
	}

	public static BsonDocument getTemporalRelationFilterQuery(long left, AC ss, AC se) {
		BsonDocument filter = new BsonDocument();
		if (ss != null)
			filter.append(Tokens.START, new BsonDocument(ss.toString(), new BsonDateTime(left)));
		if (se != null)
			filter.append(Tokens.END, new BsonDocument(se.toString(), new BsonDateTime(left)));
		return filter;
	}

	public static BsonDocument addTemporalRelationFilterQuery(BsonDocument filter, long left, AC ss, AC se) {
		if (filter == null)
			filter = new BsonDocument();
		if (ss != null)
			filter.append(Tokens.START, new BsonDocument(ss.toString(), new BsonDateTime(left)));
		if (se != null)
			filter.append(Tokens.END, new BsonDocument(se.toString(), new BsonDateTime(left)));
		return filter;
	}

	public static BsonDocument getTemporalRelationFilterQuery(LongInterval left, AC ss, AC se, AC es, AC ee) {
		BsonArray and = new BsonArray();
		if (ss != null)
			and.add(new BsonDocument(Tokens.START, new BsonDocument(ss.toString(), new BsonDateTime(left.getStart()))));
		if (se != null)
			and.add(new BsonDocument(Tokens.START, new BsonDocument(es.toString(), new BsonDateTime(left.getEnd()))));
		if (es != null)
			and.add(new BsonDocument(Tokens.END, new BsonDocument(se.toString(), new BsonDateTime(left.getStart()))));
		if (ee != null)
			and.add(new BsonDocument(Tokens.END, new BsonDocument(ee.toString(), new BsonDateTime(left.getEnd()))));
		return new BsonDocument(C.$and.toString(), and);
	}

	public static BsonDocument addTemporalRelationFilterQuery(BsonDocument filter, LongInterval left, AC ss, AC se,
			AC es, AC ee) {
		if (filter == null)
			filter = new BsonDocument();

		BsonArray and = new BsonArray();
		if (ss != null)
			and.add(new BsonDocument(Tokens.START, new BsonDocument(ss.toString(), new BsonDateTime(left.getStart()))));
		if (se != null)
			and.add(new BsonDocument(Tokens.START, new BsonDocument(es.toString(), new BsonDateTime(left.getEnd()))));
		if (es != null)
			and.add(new BsonDocument(Tokens.END, new BsonDocument(se.toString(), new BsonDateTime(left.getStart()))));
		if (ee != null)
			and.add(new BsonDocument(Tokens.END, new BsonDocument(ee.toString(), new BsonDateTime(left.getEnd()))));
		return filter.append(C.$and.toString(), and);
	}

	private boolean isTokenPassed(AC token, long t1, long t2) {
		if (token == AC.$gt) {
			return (t1 > t2 ? true : false);
		} else if (token == AC.$gte) {
			return (t1 >= t2 ? true : false);
		} else if (token == AC.$eq) {
			return (t1 == t2 ? true : false);
		} else if (token == AC.$lt) {
			return (t1 < t2 ? true : false);
		} else if (token == AC.$lte) {
			return (t1 <= t2 ? true : false);
		} else if (token == AC.$ne) {
			return (t1 != t2 ? true : false);
		} else
			return false;
	}
}
