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
 * TransactionEventExtensionType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.oliot.epcis.model;

public class TransactionEventExtensionType {

	private QuantityElementType[] quantityList;
	private SourceDestType[] sourceList;
	private SourceDestType[] destinationList;
	private TransactionEventExtension2Type extension;

	public TransactionEventExtensionType() {
	}

	public TransactionEventExtensionType(QuantityElementType[] quantityList,
			SourceDestType[] sourceList, SourceDestType[] destinationList,
			TransactionEventExtension2Type extension) {
		this.quantityList = quantityList;
		this.sourceList = sourceList;
		this.destinationList = destinationList;
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

	public TransactionEventExtension2Type getExtension() {
		return extension;
	}

	public void setExtension(TransactionEventExtension2Type extension) {
		this.extension = extension;
	}

}
