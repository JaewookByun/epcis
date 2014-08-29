/**
 * ServiceTransaction.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.oliot.epcis.model;

public class ServiceTransaction  {
    private TypeOfServiceTransaction typeOfServiceTransaction;  // attribute

    private String isNonRepudiationRequired;  // attribute

    private String isAuthenticationRequired;  // attribute

    private String isNonRepudiationOfReceiptRequired;  // attribute

    private String isIntegrityCheckRequired;  // attribute

    private String isApplicationErrorResponseRequested;  // attribute

    private String timeToAcknowledgeReceipt;  // attribute

    private String timeToAcknowledgeAcceptance;  // attribute

    private String timeToPerform;  // attribute

    private String recurrence;  // attribute

    public ServiceTransaction() {
    }

    public ServiceTransaction(
           TypeOfServiceTransaction typeOfServiceTransaction,
           String isNonRepudiationRequired,
           String isAuthenticationRequired,
           String isNonRepudiationOfReceiptRequired,
           String isIntegrityCheckRequired,
           String isApplicationErrorResponseRequested,
           String timeToAcknowledgeReceipt,
           String timeToAcknowledgeAcceptance,
           String timeToPerform,
           String recurrence) {
           this.typeOfServiceTransaction = typeOfServiceTransaction;
           this.isNonRepudiationRequired = isNonRepudiationRequired;
           this.isAuthenticationRequired = isAuthenticationRequired;
           this.isNonRepudiationOfReceiptRequired = isNonRepudiationOfReceiptRequired;
           this.isIntegrityCheckRequired = isIntegrityCheckRequired;
           this.isApplicationErrorResponseRequested = isApplicationErrorResponseRequested;
           this.timeToAcknowledgeReceipt = timeToAcknowledgeReceipt;
           this.timeToAcknowledgeAcceptance = timeToAcknowledgeAcceptance;
           this.timeToPerform = timeToPerform;
           this.recurrence = recurrence;
    }

	public TypeOfServiceTransaction getTypeOfServiceTransaction() {
		return typeOfServiceTransaction;
	}

	public void setTypeOfServiceTransaction(
			TypeOfServiceTransaction typeOfServiceTransaction) {
		this.typeOfServiceTransaction = typeOfServiceTransaction;
	}

	public String getIsNonRepudiationRequired() {
		return isNonRepudiationRequired;
	}

	public void setIsNonRepudiationRequired(String isNonRepudiationRequired) {
		this.isNonRepudiationRequired = isNonRepudiationRequired;
	}

	public String getIsAuthenticationRequired() {
		return isAuthenticationRequired;
	}

	public void setIsAuthenticationRequired(String isAuthenticationRequired) {
		this.isAuthenticationRequired = isAuthenticationRequired;
	}

	public String getIsNonRepudiationOfReceiptRequired() {
		return isNonRepudiationOfReceiptRequired;
	}

	public void setIsNonRepudiationOfReceiptRequired(
			String isNonRepudiationOfReceiptRequired) {
		this.isNonRepudiationOfReceiptRequired = isNonRepudiationOfReceiptRequired;
	}

	public String getIsIntegrityCheckRequired() {
		return isIntegrityCheckRequired;
	}

	public void setIsIntegrityCheckRequired(String isIntegrityCheckRequired) {
		this.isIntegrityCheckRequired = isIntegrityCheckRequired;
	}

	public String getIsApplicationErrorResponseRequested() {
		return isApplicationErrorResponseRequested;
	}

	public void setIsApplicationErrorResponseRequested(
			String isApplicationErrorResponseRequested) {
		this.isApplicationErrorResponseRequested = isApplicationErrorResponseRequested;
	}

	public String getTimeToAcknowledgeReceipt() {
		return timeToAcknowledgeReceipt;
	}

	public void setTimeToAcknowledgeReceipt(String timeToAcknowledgeReceipt) {
		this.timeToAcknowledgeReceipt = timeToAcknowledgeReceipt;
	}

	public String getTimeToAcknowledgeAcceptance() {
		return timeToAcknowledgeAcceptance;
	}

	public void setTimeToAcknowledgeAcceptance(String timeToAcknowledgeAcceptance) {
		this.timeToAcknowledgeAcceptance = timeToAcknowledgeAcceptance;
	}

	public String getTimeToPerform() {
		return timeToPerform;
	}

	public void setTimeToPerform(String timeToPerform) {
		this.timeToPerform = timeToPerform;
	}

	public String getRecurrence() {
		return recurrence;
	}

	public void setRecurrence(String recurrence) {
		this.recurrence = recurrence;
	}



}
