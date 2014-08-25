/**
 * EPCISServicePortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.oliot.epcis.wsdl;

public interface EPCISServicePortType extends java.rmi.Remote {
    public java.lang.String[] getQueryNames(org.oliot.epcis.query.EmptyParms parms) throws java.rmi.RemoteException, org.oliot.epcis.query.ImplementationException, org.oliot.epcis.query.SecurityException, org.oliot.epcis.query.ValidationException;
    public org.oliot.epcis.query.VoidHolder subscribe(org.oliot.epcis.query.Subscribe parms) throws java.rmi.RemoteException, org.oliot.epcis.query.QueryTooComplexException, org.oliot.epcis.query.SubscribeNotPermittedException, org.oliot.epcis.query.QueryParameterException, org.oliot.epcis.query.NoSuchNameException, org.oliot.epcis.query.DuplicateSubscriptionException, org.oliot.epcis.query.InvalidURIException, org.oliot.epcis.query.ImplementationException, org.oliot.epcis.query.SecurityException, org.oliot.epcis.query.SubscriptionControlsException, org.oliot.epcis.query.ValidationException;
    public org.oliot.epcis.query.VoidHolder unsubscribe(org.oliot.epcis.query.Unsubscribe parms) throws java.rmi.RemoteException, org.oliot.epcis.query.NoSuchSubscriptionException, org.oliot.epcis.query.ImplementationException, org.oliot.epcis.query.SecurityException, org.oliot.epcis.query.ValidationException;
    public java.lang.String[] getSubscriptionIDs(org.oliot.epcis.query.GetSubscriptionIDs parms) throws java.rmi.RemoteException, org.oliot.epcis.query.NoSuchNameException, org.oliot.epcis.query.ImplementationException, org.oliot.epcis.query.SecurityException, org.oliot.epcis.query.ValidationException;
    public org.oliot.epcis.query.QueryResults poll(org.oliot.epcis.query.Poll parms) throws java.rmi.RemoteException, org.oliot.epcis.query.QueryTooComplexException, org.oliot.epcis.query.QueryTooLargeException, org.oliot.epcis.query.NoSuchNameException, org.oliot.epcis.query.QueryParameterException, org.oliot.epcis.query.ImplementationException, org.oliot.epcis.query.SecurityException, org.oliot.epcis.query.ValidationException;
    public java.lang.String getStandardVersion(org.oliot.epcis.query.EmptyParms parms) throws java.rmi.RemoteException, org.oliot.epcis.query.ImplementationException, org.oliot.epcis.query.SecurityException, org.oliot.epcis.query.ValidationException;
    public java.lang.String getVendorVersion(org.oliot.epcis.query.EmptyParms parms) throws java.rmi.RemoteException, org.oliot.epcis.query.ImplementationException, org.oliot.epcis.query.SecurityException, org.oliot.epcis.query.ValidationException;
}
