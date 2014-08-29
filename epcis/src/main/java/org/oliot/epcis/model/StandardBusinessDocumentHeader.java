/**
 * StandardBusinessDocumentHeader.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.oliot.epcis.model;

public class StandardBusinessDocumentHeader {
	private String headerVersion;

	private Partner[] sender;

	private Partner[] receiver;

	private DocumentIdentification documentIdentification;

	private Manifest manifest;

	private Scope[] businessScope;

	public StandardBusinessDocumentHeader() {
	}

	public StandardBusinessDocumentHeader(String headerVersion,
			Partner[] sender, Partner[] receiver,
			DocumentIdentification documentIdentification, Manifest manifest,
			Scope[] businessScope) {
		this.headerVersion = headerVersion;
		this.sender = sender;
		this.receiver = receiver;
		this.documentIdentification = documentIdentification;
		this.manifest = manifest;
		this.businessScope = businessScope;
	}

	public String getHeaderVersion() {
		return headerVersion;
	}

	public void setHeaderVersion(String headerVersion) {
		this.headerVersion = headerVersion;
	}

	public Partner[] getSender() {
		return sender;
	}

	public void setSender(Partner[] sender) {
		this.sender = sender;
	}

	public Partner[] getReceiver() {
		return receiver;
	}

	public void setReceiver(Partner[] receiver) {
		this.receiver = receiver;
	}

	public DocumentIdentification getDocumentIdentification() {
		return documentIdentification;
	}

	public void setDocumentIdentification(
			DocumentIdentification documentIdentification) {
		this.documentIdentification = documentIdentification;
	}

	public Manifest getManifest() {
		return manifest;
	}

	public void setManifest(Manifest manifest) {
		this.manifest = manifest;
	}

	public Scope[] getBusinessScope() {
		return businessScope;
	}

	public void setBusinessScope(Scope[] businessScope) {
		this.businessScope = businessScope;
	}

}
