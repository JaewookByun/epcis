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
 * EPCISDocumentType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.oliot.epcis.model;

import java.math.BigDecimal;
import java.util.Calendar;
import org.apache.axis.message.MessageElement;

/**
 * document that contains a Header and a Body.
 */
public class EPCISDocumentType extends Document {

	private EPCISHeaderType EPCISHeader;
	private EPCISBodyType EPCISBody;
	private EPCISDocumentExtensionType extension;
	private MessageElement[] _any;

	public EPCISDocumentType() {
	}

	public EPCISDocumentType(BigDecimal schemaVersion, Calendar creationDate,
			EPCISHeaderType EPCISHeader, EPCISBodyType EPCISBody,
			EPCISDocumentExtensionType extension, MessageElement[] _any) {
		super(schemaVersion, creationDate);
		this.EPCISHeader = EPCISHeader;
		this.EPCISBody = EPCISBody;
		this.extension = extension;
		this._any = _any;
	}

	public EPCISHeaderType getEPCISHeader() {
		return EPCISHeader;
	}

	public void setEPCISHeader(EPCISHeaderType ePCISHeader) {
		EPCISHeader = ePCISHeader;
	}

	public EPCISBodyType getEPCISBody() {
		return EPCISBody;
	}

	public void setEPCISBody(EPCISBodyType ePCISBody) {
		EPCISBody = ePCISBody;
	}

	public EPCISDocumentExtensionType getExtension() {
		return extension;
	}

	public void setExtension(EPCISDocumentExtensionType extension) {
		this.extension = extension;
	}

	public MessageElement[] get_any() {
		return _any;
	}

	public void set_any(MessageElement[] _any) {
		this._any = _any;
	}

}
