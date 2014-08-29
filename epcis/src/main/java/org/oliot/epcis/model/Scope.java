/**
 * Scope.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.oliot.epcis.model;

public class Scope {
	private String type;

	private String instanceIdentifier;

	private String identifier;

	private Object[] scopeInformation;

	public Scope() {
	}

	public Scope(String type, String instanceIdentifier, String identifier,
			Object[] scopeInformation) {
		this.type = type;
		this.instanceIdentifier = instanceIdentifier;
		this.identifier = identifier;
		this.scopeInformation = scopeInformation;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getInstanceIdentifier() {
		return instanceIdentifier;
	}

	public void setInstanceIdentifier(String instanceIdentifier) {
		this.instanceIdentifier = instanceIdentifier;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public Object[] getScopeInformation() {
		return scopeInformation;
	}

	public void setScopeInformation(Object[] scopeInformation) {
		this.scopeInformation = scopeInformation;
	}

}
