package org.lilliput.chronograph.common;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Copyright (C) 2016-2017 Jaewook Byun
 * 
 * ChronoGraph: Temporal Property Graph and Traversal Language
 * 
 * For inner loop invocation using Java Reflection
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
