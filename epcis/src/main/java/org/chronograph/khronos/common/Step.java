package org.chronograph.khronos.common;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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
public class Step {
	private Object classInstance;
	private Method method;
	private Object[] params;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Step(final String className, final String methodName, final Class[] args, final Object... params) {
		try {
			Class cls = Class.forName(className);
			this.method = cls.getDeclaredMethod(methodName, args);
			this.params = params;
		} catch (ClassNotFoundException | NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
			System.out.println(e.getCause());
		}
	}

	public void setInstance(Object classInstance) {
		this.classInstance = classInstance;
	}

	public void invoke() {
		try {
			method.invoke(classInstance, params);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
			System.out.println(e.getCause());
		}
	}

}
