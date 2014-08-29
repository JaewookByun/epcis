/**
 * ContactInformation.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.oliot.epcis.model;

public class ContactInformation {
	private String contact;

	private String emailAddress;

	private String faxNumber;

	private String telephoneNumber;

	private String contactTypeIdentifier;

	public ContactInformation() {
	}

	public ContactInformation(String contact, String emailAddress,
			String faxNumber, String telephoneNumber,
			String contactTypeIdentifier) {
		this.contact = contact;
		this.emailAddress = emailAddress;
		this.faxNumber = faxNumber;
		this.telephoneNumber = telephoneNumber;
		this.contactTypeIdentifier = contactTypeIdentifier;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getFaxNumber() {
		return faxNumber;
	}

	public void setFaxNumber(String faxNumber) {
		this.faxNumber = faxNumber;
	}

	public String getTelephoneNumber() {
		return telephoneNumber;
	}

	public void setTelephoneNumber(String telephoneNumber) {
		this.telephoneNumber = telephoneNumber;
	}

	public String getContactTypeIdentifier() {
		return contactTypeIdentifier;
	}

	public void setContactTypeIdentifier(String contactTypeIdentifier) {
		this.contactTypeIdentifier = contactTypeIdentifier;
	}

}
