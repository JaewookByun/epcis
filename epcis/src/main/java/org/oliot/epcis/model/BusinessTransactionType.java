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
 * BusinessTransactionType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.oliot.epcis.model;

import org.apache.axis.types.URI;

public class BusinessTransactionType {

	private org.apache.axis.types.URI type; // attribute

	public BusinessTransactionType() {
	}

	// Simple Types must have a String constructor
	public BusinessTransactionType(URI _value) {
		this.type = _value;
	}

	public org.apache.axis.types.URI getType() {
		return type;
	}

	public void setType(org.apache.axis.types.URI type) {
		this.type = type;
	}

}
