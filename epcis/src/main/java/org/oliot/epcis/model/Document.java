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
 * EPCglobal document properties for all messages.
 */

/**
 * Document.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.oliot.epcis.model;

public abstract class Document {
	private java.math.BigDecimal schemaVersion; // attribute

	private java.util.Calendar creationDate; // attribute

	public Document() {
	}

	public Document(java.math.BigDecimal schemaVersion,
			java.util.Calendar creationDate) {
		this.schemaVersion = schemaVersion;
		this.creationDate = creationDate;
	}

	public java.math.BigDecimal getSchemaVersion() {
		return schemaVersion;
	}

	public void setSchemaVersion(java.math.BigDecimal schemaVersion) {
		this.schemaVersion = schemaVersion;
	}

	public java.util.Calendar getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(java.util.Calendar creationDate) {
		this.creationDate = creationDate;
	}
}
