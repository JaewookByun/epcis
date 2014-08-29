/**
 * DocumentIdentification.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.oliot.epcis.model;

import java.util.Calendar;

public class DocumentIdentification {
	private String standard;

	private String typeVersion;

	private String instanceIdentifier;

	private String type;

	private Boolean multipleType;

	private Calendar creationDateAndTime;

	public DocumentIdentification() {
	}

	public DocumentIdentification(String standard, String typeVersion,
			String instanceIdentifier, String type, Boolean multipleType,
			Calendar creationDateAndTime) {
		this.standard = standard;
		this.typeVersion = typeVersion;
		this.instanceIdentifier = instanceIdentifier;
		this.type = type;
		this.multipleType = multipleType;
		this.creationDateAndTime = creationDateAndTime;
	}

	public String getStandard() {
		return standard;
	}

	public void setStandard(String standard) {
		this.standard = standard;
	}

	public String getTypeVersion() {
		return typeVersion;
	}

	public void setTypeVersion(String typeVersion) {
		this.typeVersion = typeVersion;
	}

	public String getInstanceIdentifier() {
		return instanceIdentifier;
	}

	public void setInstanceIdentifier(String instanceIdentifier) {
		this.instanceIdentifier = instanceIdentifier;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Boolean getMultipleType() {
		return multipleType;
	}

	public void setMultipleType(Boolean multipleType) {
		this.multipleType = multipleType;
	}

	public Calendar getCreationDateAndTime() {
		return creationDateAndTime;
	}

	public void setCreationDateAndTime(Calendar creationDateAndTime) {
		this.creationDateAndTime = creationDateAndTime;
	}

}
