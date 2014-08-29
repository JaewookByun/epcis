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
 * EPCISHeaderType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.oliot.epcis.model;

import org.apache.axis.message.MessageElement;

/**
 * specific header(s) including the Standard Business Document Header.
 */

public class EPCISHeaderType  {

	private StandardBusinessDocumentHeader standardBusinessDocumentHeader;
	private EPCISHeaderExtensionType extension;
	private MessageElement [] _any;

	public EPCISHeaderType() {}

	public EPCISHeaderType(StandardBusinessDocumentHeader standardBusinessDocumentHeader,EPCISHeaderExtensionType extension,MessageElement [] _any) {
		this.standardBusinessDocumentHeader = standardBusinessDocumentHeader;
		this.extension = extension;
		this._any = _any;
	}

	public StandardBusinessDocumentHeader getStandardBusinessDocumentHeader() {
		return standardBusinessDocumentHeader;
	}

	public void setStandardBusinessDocumentHeader(
			StandardBusinessDocumentHeader standardBusinessDocumentHeader) {
		this.standardBusinessDocumentHeader = standardBusinessDocumentHeader;
	}

	public EPCISHeaderExtensionType getExtension() {
		return extension;
	}

	public void setExtension(EPCISHeaderExtensionType extension) {
		this.extension = extension;
	}

	public MessageElement[] get_any() {
		return _any;
	}

	public void set_any(MessageElement[] _any) {
		this._any = _any;
	}

}
