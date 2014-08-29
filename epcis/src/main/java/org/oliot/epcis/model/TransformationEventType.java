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
 * TransformationEventType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.oliot.epcis.model;

import java.util.Calendar;

import org.apache.axis.message.MessageElement;
import org.apache.axis.types.URI;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Transformation Event captures an event in which inputs are consumed and
 * outputs are produced
 */
@Document(collection="TransformationEvent")
public class TransformationEventType extends EPCISEventType {

	private EPC[] inputEPCList;
	private QuantityElementType[] inputQuantityList;
	private EPC[] outputEPCList;
	private QuantityElementType[] outputQuantityList;
	private URI transformationID;
	private URI bizStep;
	private URI disposition;
	private ReadPointType readPoint;
	private BusinessLocationType bizLocation;
	private BusinessTransactionType[] bizTransactionList;
	private SourceDestType[] sourceList;
	private SourceDestType[] destinationList;
	private ILMDType ilmd;
	private TransformationEventExtensionType extension;
	private MessageElement[] _any;

	public TransformationEventType() {
	}

	public TransformationEventType(Calendar eventTime, Calendar recordTime,
			String eventTimeZoneOffset, EPCISEventExtensionType baseExtension,
			EPC[] inputEPCList, QuantityElementType[] inputQuantityList,
			EPC[] outputEPCList, QuantityElementType[] outputQuantityList,
			URI transformationID, URI bizStep, URI disposition,
			ReadPointType readPoint, BusinessLocationType bizLocation,
			BusinessTransactionType[] bizTransactionList,
			SourceDestType[] sourceList, SourceDestType[] destinationList,
			ILMDType ilmd, TransformationEventExtensionType extension,
			MessageElement[] _any) {
		super(eventTime, recordTime, eventTimeZoneOffset, baseExtension);
		this.inputEPCList = inputEPCList;
		this.inputQuantityList = inputQuantityList;
		this.outputEPCList = outputEPCList;
		this.outputQuantityList = outputQuantityList;
		this.transformationID = transformationID;
		this.bizStep = bizStep;
		this.disposition = disposition;
		this.readPoint = readPoint;
		this.bizLocation = bizLocation;
		this.bizTransactionList = bizTransactionList;
		this.sourceList = sourceList;
		this.destinationList = destinationList;
		this.ilmd = ilmd;
		this.extension = extension;
		this._any = _any;
	}

	public EPC[] getInputEPCList() {
		return inputEPCList;
	}

	public void setInputEPCList(EPC[] inputEPCList) {
		this.inputEPCList = inputEPCList;
	}

	public QuantityElementType[] getInputQuantityList() {
		return inputQuantityList;
	}

	public void setInputQuantityList(QuantityElementType[] inputQuantityList) {
		this.inputQuantityList = inputQuantityList;
	}

	public EPC[] getOutputEPCList() {
		return outputEPCList;
	}

	public void setOutputEPCList(EPC[] outputEPCList) {
		this.outputEPCList = outputEPCList;
	}

	public QuantityElementType[] getOutputQuantityList() {
		return outputQuantityList;
	}

	public void setOutputQuantityList(QuantityElementType[] outputQuantityList) {
		this.outputQuantityList = outputQuantityList;
	}

	public URI getTransformationID() {
		return transformationID;
	}

	public void setTransformationID(URI transformationID) {
		this.transformationID = transformationID;
	}

	public URI getBizStep() {
		return bizStep;
	}

	public void setBizStep(URI bizStep) {
		this.bizStep = bizStep;
	}

	public URI getDisposition() {
		return disposition;
	}

	public void setDisposition(URI disposition) {
		this.disposition = disposition;
	}

	public ReadPointType getReadPoint() {
		return readPoint;
	}

	public void setReadPoint(ReadPointType readPoint) {
		this.readPoint = readPoint;
	}

	public BusinessLocationType getBizLocation() {
		return bizLocation;
	}

	public void setBizLocation(BusinessLocationType bizLocation) {
		this.bizLocation = bizLocation;
	}

	public BusinessTransactionType[] getBizTransactionList() {
		return bizTransactionList;
	}

	public void setBizTransactionList(
			BusinessTransactionType[] bizTransactionList) {
		this.bizTransactionList = bizTransactionList;
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

	public TransformationEventExtensionType getExtension() {
		return extension;
	}

	public void setExtension(TransformationEventExtensionType extension) {
		this.extension = extension;
	}

	public MessageElement[] get_any() {
		return _any;
	}

	public void set_any(MessageElement[] _any) {
		this._any = _any;
	}

}
