package org.chronograph.khronos.common;

import java.util.Map;

/**
 * Copyright (C) 2016-2018 Jaewook Byun
 * 
 * ChronoGraph: A Temporal Graph Management and Traversal Platform
 * 
 * The loop management scheme unlike Gremlin makes this class
 * 
 * @author Jaewook Byun, Assistant Professor, Halla University
 * 
 *         Data Frameworks and Platforms Laboratory (DFPL)
 * 
 *         jaewook.byun@halla.ac.kr, bjw0829@kaist.ac.kr, bjw0829@gmail.com
 * 
 */
public interface HistoryEnabledFunction<T, R> {
	/**
	 * A function determines the termination condition of loop based on current
	 * element, current path, the number of loops
	 * 
	 * @param argument
	 * @param currentPath
	 * @param loopCount
	 * @return
	 */
	public R apply(T argument, Map<Object, Object> currentPath);
}
