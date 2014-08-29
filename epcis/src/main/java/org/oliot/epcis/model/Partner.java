/**
 * Partner.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.oliot.epcis.model;

public class Partner  {
    private PartnerIdentification identifier;

    private ContactInformation[] contactInformation;

    public Partner() {
    }

    public Partner(
           PartnerIdentification identifier,
           ContactInformation[] contactInformation) {
           this.identifier = identifier;
           this.contactInformation = contactInformation;
    }

	public PartnerIdentification getIdentifier() {
		return identifier;
	}

	public void setIdentifier(PartnerIdentification identifier) {
		this.identifier = identifier;
	}

	public ContactInformation[] getContactInformation() {
		return contactInformation;
	}

	public void setContactInformation(ContactInformation[] contactInformation) {
		this.contactInformation = contactInformation;
	}

}
