package org.lilliput.chronograph.common;

/**
 * Copyright (C) 2016-2017 Jaewook Byun
 * 
 * ChronoGraph: Temporal Property Graph and Traversal Language
 * 
 * Temporal Type
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
public enum TemporalType {
	TIMESTAMP, INTERVAL;

	public TemporalType opposite() {
		switch (this) {
		case TIMESTAMP:
			return INTERVAL;
		case INTERVAL:
			return TIMESTAMP;
		}
		return null;
	}
}
