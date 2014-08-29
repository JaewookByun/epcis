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
 * EPCISEventListExtensionType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.oliot.epcis.model;

public class EPCISEventListExtensionType {

	private TransformationEventType transformationEvent;
	private EPCISEventListExtension2Type extension;

	public EPCISEventListExtensionType() {
	}

	public EPCISEventListExtensionType(
			TransformationEventType transformationEvent,
			EPCISEventListExtension2Type extension) {
		this.transformationEvent = transformationEvent;
		this.extension = extension;
	}

	public TransformationEventType getTransformationEvent() {
		return transformationEvent;
	}

	public void setTransformationEvent(
			TransformationEventType transformationEvent) {
		this.transformationEvent = transformationEvent;
	}

	public EPCISEventListExtension2Type getExtension() {
		return extension;
	}

	public void setExtension(EPCISEventListExtension2Type extension) {
		this.extension = extension;
	}

}
