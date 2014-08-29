/**
 * Copyright (C) 2014 KAIST RESL 
 *
 * This file is part of Oliot (oliot.org).

 * @author Jack Jaewook Byun, Ph.D student
 * Korea Advanced Institute of Science and Technology
 * Real-time Embedded System Laboratory(RESL)
 * bjw0829@kaist.ac.kr
 */

/**
 * ActionType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.oliot.epcis.model;

public class ActionType{
	private String value;
	
	// Constructor
	public ActionType(String value) {
		this.value = value;
	}

	public static final String _ADD = "ADD";
	public static final String _OBSERVE = "OBSERVE";
	public static final String _DELETE = "DELETE";
	public static final ActionType ADD = new ActionType(_ADD);
	public static final ActionType OBSERVE = new ActionType(_OBSERVE);
	public static final ActionType DELETE = new ActionType(_DELETE);

	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
}
