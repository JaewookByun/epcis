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
 * AggregationEventExtensionType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.oliot.epcis.model;

public class AggregationEventExtensionType {
	
	private QuantityElementType[] childQuantityList;
	private SourceDestType[] sourceList;
	private SourceDestType[] destinationList;
	private AggregationEventExtension2Type extension;

	public AggregationEventExtensionType() {}

	public AggregationEventExtensionType(
			QuantityElementType[] childQuantityList,
			SourceDestType[] sourceList,
			SourceDestType[] destinationList,
			AggregationEventExtension2Type extension) {
		this.childQuantityList = childQuantityList;
		this.sourceList = sourceList;
		this.destinationList = destinationList;
		this.extension = extension;
	}

	public QuantityElementType[] getChildQuantityList() {
		return childQuantityList;
	}

	public void setChildQuantityList(QuantityElementType[] childQuantityList) {
		this.childQuantityList = childQuantityList;
	}

	public SourceDestType[] getSourceList() {
		return sourceList;
	}

	public void setSourceList(SourceDestType[] sourceList) {
		this.sourceList = sourceList;
	}

	public SourceDestType[] getDestinationList() {
		return destinationList;
	}

	public void setDestinationList(SourceDestType[] destinationList) {
		this.destinationList = destinationList;
	}

	public AggregationEventExtension2Type getExtension() {
		return extension;
	}

	public void setExtension(AggregationEventExtension2Type extension) {
		this.extension = extension;
	}

}
