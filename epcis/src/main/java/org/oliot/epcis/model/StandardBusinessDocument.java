/**
 * StandardBusinessDocument.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.oliot.epcis.model;

import org.apache.axis.message.MessageElement;

public class StandardBusinessDocument {
	private StandardBusinessDocumentHeader standardBusinessDocumentHeader;

	private MessageElement[] _any;

	public StandardBusinessDocument() {
	}

	public StandardBusinessDocument(
			StandardBusinessDocumentHeader standardBusinessDocumentHeader,
			MessageElement[] _any) {
		this.standardBusinessDocumentHeader = standardBusinessDocumentHeader;
		this._any = _any;
	}

	public StandardBusinessDocumentHeader getStandardBusinessDocumentHeader() {
		return standardBusinessDocumentHeader;
	}

	public void setStandardBusinessDocumentHeader(
			StandardBusinessDocumentHeader standardBusinessDocumentHeader) {
		this.standardBusinessDocumentHeader = standardBusinessDocumentHeader;
	}

	public MessageElement[] get_any() {
		return _any;
	}

	public void set_any(MessageElement[] _any) {
		this._any = _any;
	}

}
