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
 * ObjectEventExtensionType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.oliot.epcis.model;

public class ObjectEventExtensionType {

	private QuantityElementType[] quantityList;
	private SourceDestType[] sourceList;
	private SourceDestType[] destinationList;
	private ILMDType ilmd;
	private ObjectEventExtension2Type extension;

	public ObjectEventExtensionType() {
	}

	public ObjectEventExtensionType(QuantityElementType[] quantityList,
			SourceDestType[] sourceList, SourceDestType[] destinationList,
			ILMDType ilmd, ObjectEventExtension2Type extension) {
		this.quantityList = quantityList;
		this.sourceList = sourceList;
		this.destinationList = destinationList;
		this.ilmd = ilmd;
		this.extension = extension;
	}

	public QuantityElementType[] getQuantityList() {
		return quantityList;
	}

	public void setQuantityList(QuantityElementType[] quantityList) {
		this.quantityList = quantityList;
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

	public ILMDType getIlmd() {
		return ilmd;
	}

	public void setIlmd(ILMDType ilmd) {
		this.ilmd = ilmd;
	}

	public ObjectEventExtension2Type getExtension() {
		return extension;
	}

	public void setExtension(ObjectEventExtension2Type extension) {
		this.extension = extension;
	}

}
