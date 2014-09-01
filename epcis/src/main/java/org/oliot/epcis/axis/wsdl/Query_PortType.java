/**
 * Query_PortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.oliot.epcis.axis.wsdl;

public interface Query_PortType extends java.rmi.Remote {
    public java.lang.String[] getQueryNames(org.oliot.epcis.axis.EmptyParms parms) throws java.rmi.RemoteException, org.oliot.epcis.axis.ImplementationException, org.oliot.epcis.axis.SecurityException, org.oliot.epcis.axis.ValidationException;
    public org.oliot.epcis.axis.VoidHolder subscribe(org.oliot.epcis.axis.Subscribe parms) throws java.rmi.RemoteException, org.oliot.epcis.axis.QueryTooComplexException, org.oliot.epcis.axis.SubscribeNotPermittedException, org.oliot.epcis.axis.QueryParameterException, org.oliot.epcis.axis.NoSuchNameException, org.oliot.epcis.axis.DuplicateSubscriptionException, org.oliot.epcis.axis.InvalidURIException, org.oliot.epcis.axis.ImplementationException, org.oliot.epcis.axis.SecurityException, org.oliot.epcis.axis.SubscriptionControlsException, org.oliot.epcis.axis.ValidationException;
    public org.oliot.epcis.axis.VoidHolder unsubscribe(org.oliot.epcis.axis.Unsubscribe parms) throws java.rmi.RemoteException, org.oliot.epcis.axis.NoSuchSubscriptionException, org.oliot.epcis.axis.ImplementationException, org.oliot.epcis.axis.SecurityException, org.oliot.epcis.axis.ValidationException;
    public java.lang.String[] getSubscriptionIDs(org.oliot.epcis.axis.GetSubscriptionIDs parms) throws java.rmi.RemoteException, org.oliot.epcis.axis.NoSuchNameException, org.oliot.epcis.axis.ImplementationException, org.oliot.epcis.axis.SecurityException, org.oliot.epcis.axis.ValidationException;
    public org.oliot.epcis.axis.QueryResults poll(org.oliot.epcis.axis.Poll parms) throws java.rmi.RemoteException, org.oliot.epcis.axis.QueryTooComplexException, org.oliot.epcis.axis.QueryTooLargeException, org.oliot.epcis.axis.NoSuchNameException, org.oliot.epcis.axis.QueryParameterException, org.oliot.epcis.axis.ImplementationException, org.oliot.epcis.axis.SecurityException, org.oliot.epcis.axis.ValidationException;
    public java.lang.String getStandardVersion(org.oliot.epcis.axis.EmptyParms parms) throws java.rmi.RemoteException, org.oliot.epcis.axis.ImplementationException, org.oliot.epcis.axis.SecurityException, org.oliot.epcis.axis.ValidationException;
    public java.lang.String getVendorVersion(org.oliot.epcis.axis.EmptyParms parms) throws java.rmi.RemoteException, org.oliot.epcis.axis.ImplementationException, org.oliot.epcis.axis.SecurityException, org.oliot.epcis.axis.ValidationException;
}
