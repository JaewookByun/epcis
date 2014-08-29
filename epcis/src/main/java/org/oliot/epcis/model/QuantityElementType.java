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
 * QuantityElementType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.oliot.epcis.model;

import org.apache.axis.types.URI;

public class QuantityElementType {

	private URI epcClass;
	private float quantity;
	private URI uom;

	public QuantityElementType() {
	}

	public QuantityElementType(URI epcClass, float quantity, URI uom) {
		this.epcClass = epcClass;
		this.quantity = quantity;
		this.uom = uom;
	}

	public URI getEpcClass() {
		return epcClass;
	}

	public void setEpcClass(URI epcClass) {
		this.epcClass = epcClass;
	}

	public float getQuantity() {
		return quantity;
	}

	public void setQuantity(float quantity) {
		this.quantity = quantity;
	}

	public URI getUom() {
		return uom;
	}

	public void setUom(URI uom) {
		this.uom = uom;
	}

}
