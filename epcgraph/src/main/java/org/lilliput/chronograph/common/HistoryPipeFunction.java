package org.lilliput.chronograph.common;

import java.util.Map;

/**
 * Copyright (C) 2016-2017 Jaewook Byun
 * 
 * ChronoGraph: Temporal Property Graph and Traversal Language
 * 
 * Our loop management scheme unlike Gremlin makes this class
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
public interface HistoryPipeFunction {

	/**
	 * A function determines the termination condition of loop based on current
	 * element, current path, the number of loops
	 * 
	 * @param argument
	 * @param currentPath
	 * @param loopCount
	 * @return
	 */
	public Object compute(Object argument, Map<Object, Object> currentPath);

}
