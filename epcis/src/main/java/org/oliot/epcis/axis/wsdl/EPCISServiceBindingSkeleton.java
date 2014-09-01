/**
 * EPCISServiceBindingSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.oliot.epcis.axis.wsdl;

public class EPCISServiceBindingSkeleton implements org.oliot.epcis.axis.wsdl.Query_PortType, org.apache.axis.wsdl.Skeleton {
    private org.oliot.epcis.axis.wsdl.Query_PortType impl;
    private static java.util.Map _myOperations = new java.util.Hashtable();
    private static java.util.Collection _myOperationsList = new java.util.ArrayList();

    /**
    * Returns List of OperationDesc objects with this name
    */
    public static java.util.List getOperationDescByName(java.lang.String methodName) {
        return (java.util.List)_myOperations.get(methodName);
    }

    /**
    * Returns Collection of OperationDescs
    */
    public static java.util.Collection getOperationDescs() {
        return _myOperationsList;
    }

    static {
        org.apache.axis.description.OperationDesc _oper;
        org.apache.axis.description.FaultDesc _fault;
        org.apache.axis.description.ParameterDesc [] _params;
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("query.axis.epcis.oliot.org", "GetQueryNames"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("query.axis.epcis.oliot.org", "EmptyParms"), org.oliot.epcis.axis.EmptyParms.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getQueryNames", _params, new javax.xml.namespace.QName("query.axis.epcis.oliot.org", "GetQueryNamesResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("query.axis.epcis.oliot.org", "ArrayOfString"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "getQueryNames"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getQueryNames") == null) {
            _myOperations.put("getQueryNames", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getQueryNames")).add(_oper);
        _fault = new org.apache.axis.description.FaultDesc();
        _fault.setName("ImplementationExceptionFault");
        _fault.setQName(new javax.xml.namespace.QName("query.axis.epcis.oliot.org", "ImplementationException"));
        _fault.setClassName("org.oliot.epcis.axis.ImplementationException");
        _fault.setXmlType(new javax.xml.namespace.QName("query.axis.epcis.oliot.org", "ImplementationException"));
        _oper.addFault(_fault);
        _fault = new org.apache.axis.description.FaultDesc();
        _fault.setName("SecurityExceptionFault");
        _fault.setQName(new javax.xml.namespace.QName("query.axis.epcis.oliot.org", "SecurityException"));
        _fault.setClassName("org.oliot.epcis.axis.SecurityException");
        _fault.setXmlType(new javax.xml.namespace.QName("query.axis.epcis.oliot.org", "SecurityException"));
        _oper.addFault(_fault);
        _fault = new org.apache.axis.description.FaultDesc();
        _fault.setName("ValidationExceptionFault");
        _fault.setQName(new javax.xml.namespace.QName("query.axis.epcis.oliot.org", "ValidationException"));
        _fault.setClassName("org.oliot.epcis.axis.ValidationException");
        _fault.setXmlType(new javax.xml.namespace.QName("query.axis.epcis.oliot.org", "ValidationException"));
        _oper.addFault(_fault);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("query.axis.epcis.oliot.org", "Subscribe"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("query.axis.epcis.oliot.org", "Subscribe"), org.oliot.epcis.axis.Subscribe.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("subscribe", _params, new javax.xml.namespace.QName("query.axis.epcis.oliot.org", "SubscribeResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("query.axis.epcis.oliot.org", "VoidHolder"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "subscribe"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("subscribe") == null) {
            _myOperations.put("subscribe", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("subscribe")).add(_oper);
        _fault = new org.apache.axis.description.FaultDesc();
        _fault.setName("QueryTooComplexExceptionFault");
        _fault.setQName(new javax.xml.namespace.QName("query.axis.epcis.oliot.org", "QueryTooComplexException"));
        _fault.setClassName("org.oliot.epcis.axis.QueryTooComplexException");
        _fault.setXmlType(new javax.xml.namespace.QName("query.axis.epcis.oliot.org", "QueryTooComplexException"));
        _oper.addFault(_fault);
        _fault = new org.apache.axis.description.FaultDesc();
        _fault.setName("SubscribeNotPermittedExceptionFault");
        _fault.setQName(new javax.xml.namespace.QName("query.axis.epcis.oliot.org", "SubscribeNotPermittedException"));
        _fault.setClassName("org.oliot.epcis.axis.SubscribeNotPermittedException");
        _fault.setXmlType(new javax.xml.namespace.QName("query.axis.epcis.oliot.org", "SubscribeNotPermittedException"));
        _oper.addFault(_fault);
        _fault = new org.apache.axis.description.FaultDesc();
        _fault.setName("QueryParameterExceptionFault");
        _fault.setQName(new javax.xml.namespace.QName("query.axis.epcis.oliot.org", "QueryParameterException"));
        _fault.setClassName("org.oliot.epcis.axis.QueryParameterException");
        _fault.setXmlType(new javax.xml.namespace.QName("query.axis.epcis.oliot.org", "QueryParameterException"));
        _oper.addFault(_fault);
        _fault = new org.apache.axis.description.FaultDesc();
        _fault.setName("NoSuchNameExceptionFault");
        _fault.setQName(new javax.xml.namespace.QName("query.axis.epcis.oliot.org", "NoSuchNameException"));
        _fault.setClassName("org.oliot.epcis.axis.NoSuchNameException");
        _fault.setXmlType(new javax.xml.namespace.QName("query.axis.epcis.oliot.org", "NoSuchNameException"));
        _oper.addFault(_fault);
        _fault = new org.apache.axis.description.FaultDesc();
        _fault.setName("DuplicateSubscriptionExceptionFault");
        _fault.setQName(new javax.xml.namespace.QName("query.axis.epcis.oliot.org", "DuplicateSubscriptionException"));
        _fault.setClassName("org.oliot.epcis.axis.DuplicateSubscriptionException");
        _fault.setXmlType(new javax.xml.namespace.QName("query.axis.epcis.oliot.org", "DuplicateSubscriptionException"));
        _oper.addFault(_fault);
        _fault = new org.apache.axis.description.FaultDesc();
        _fault.setName("InvalidURIExceptionFault");
        _fault.setQName(new javax.xml.namespace.QName("query.axis.epcis.oliot.org", "InvalidURIException"));
        _fault.setClassName("org.oliot.epcis.axis.InvalidURIException");
        _fault.setXmlType(new javax.xml.namespace.QName("query.axis.epcis.oliot.org", "InvalidURIException"));
        _oper.addFault(_fault);
        _fault = new org.apache.axis.description.FaultDesc();
        _fault.setName("ImplementationExceptionFault");
        _fault.setQName(new javax.xml.namespace.QName("query.axis.epcis.oliot.org", "ImplementationException"));
        _fault.setClassName("org.oliot.epcis.axis.ImplementationException");
        _fault.setXmlType(new javax.xml.namespace.QName("query.axis.epcis.oliot.org", "ImplementationException"));
        _oper.addFault(_fault);
        _fault = new org.apache.axis.description.FaultDesc();
        _fault.setName("SecurityExceptionFault");
        _fault.setQName(new javax.xml.namespace.QName("query.axis.epcis.oliot.org", "SecurityException"));
        _fault.setClassName("org.oliot.epcis.axis.SecurityException");
        _fault.setXmlType(new javax.xml.namespace.QName("query.axis.epcis.oliot.org", "SecurityException"));
        _oper.addFault(_fault);
        _fault = new org.apache.axis.description.FaultDesc();
        _fault.setName("SubscriptionControlsExceptionFault");
        _fault.setQName(new javax.xml.namespace.QName("query.axis.epcis.oliot.org", "SubscriptionControlsException"));
        _fault.setClassName("org.oliot.epcis.axis.SubscriptionControlsException");
        _fault.setXmlType(new javax.xml.namespace.QName("query.axis.epcis.oliot.org", "SubscriptionControlsException"));
        _oper.addFault(_fault);
        _fault = new org.apache.axis.description.FaultDesc();
        _fault.setName("ValidationExceptionFault");
        _fault.setQName(new javax.xml.namespace.QName("query.axis.epcis.oliot.org", "ValidationException"));
        _fault.setClassName("org.oliot.epcis.axis.ValidationException");
        _fault.setXmlType(new javax.xml.namespace.QName("query.axis.epcis.oliot.org", "ValidationException"));
        _oper.addFault(_fault);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("query.axis.epcis.oliot.org", "Unsubscribe"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("query.axis.epcis.oliot.org", "Unsubscribe"), org.oliot.epcis.axis.Unsubscribe.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("unsubscribe", _params, new javax.xml.namespace.QName("query.axis.epcis.oliot.org", "UnsubscribeResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("query.axis.epcis.oliot.org", "VoidHolder"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "unsubscribe"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("unsubscribe") == null) {
            _myOperations.put("unsubscribe", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("unsubscribe")).add(_oper);
        _fault = new org.apache.axis.description.FaultDesc();
        _fault.setName("NoSuchSubscriptionExceptionFault");
        _fault.setQName(new javax.xml.namespace.QName("query.axis.epcis.oliot.org", "NoSuchSubscriptionException"));
        _fault.setClassName("org.oliot.epcis.axis.NoSuchSubscriptionException");
        _fault.setXmlType(new javax.xml.namespace.QName("query.axis.epcis.oliot.org", "NoSuchSubscriptionException"));
        _oper.addFault(_fault);
        _fault = new org.apache.axis.description.FaultDesc();
        _fault.setName("ImplementationExceptionFault");
        _fault.setQName(new javax.xml.namespace.QName("query.axis.epcis.oliot.org", "ImplementationException"));
        _fault.setClassName("org.oliot.epcis.axis.ImplementationException");
        _fault.setXmlType(new javax.xml.namespace.QName("query.axis.epcis.oliot.org", "ImplementationException"));
        _oper.addFault(_fault);
        _fault = new org.apache.axis.description.FaultDesc();
        _fault.setName("SecurityExceptionFault");
        _fault.setQName(new javax.xml.namespace.QName("query.axis.epcis.oliot.org", "SecurityException"));
        _fault.setClassName("org.oliot.epcis.axis.SecurityException");
        _fault.setXmlType(new javax.xml.namespace.QName("query.axis.epcis.oliot.org", "SecurityException"));
        _oper.addFault(_fault);
        _fault = new org.apache.axis.description.FaultDesc();
        _fault.setName("ValidationExceptionFault");
        _fault.setQName(new javax.xml.namespace.QName("query.axis.epcis.oliot.org", "ValidationException"));
        _fault.setClassName("org.oliot.epcis.axis.ValidationException");
        _fault.setXmlType(new javax.xml.namespace.QName("query.axis.epcis.oliot.org", "ValidationException"));
        _oper.addFault(_fault);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("query.axis.epcis.oliot.org", "GetSubscriptionIDs"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("query.axis.epcis.oliot.org", "GetSubscriptionIDs"), org.oliot.epcis.axis.GetSubscriptionIDs.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getSubscriptionIDs", _params, new javax.xml.namespace.QName("query.axis.epcis.oliot.org", "GetSubscriptionIDsResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("query.axis.epcis.oliot.org", "ArrayOfString"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "getSubscriptionIDs"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getSubscriptionIDs") == null) {
            _myOperations.put("getSubscriptionIDs", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getSubscriptionIDs")).add(_oper);
        _fault = new org.apache.axis.description.FaultDesc();
        _fault.setName("NoSuchNameExceptionFault");
        _fault.setQName(new javax.xml.namespace.QName("query.axis.epcis.oliot.org", "NoSuchNameException"));
        _fault.setClassName("org.oliot.epcis.axis.NoSuchNameException");
        _fault.setXmlType(new javax.xml.namespace.QName("query.axis.epcis.oliot.org", "NoSuchNameException"));
        _oper.addFault(_fault);
        _fault = new org.apache.axis.description.FaultDesc();
        _fault.setName("ImplementationExceptionFault");
        _fault.setQName(new javax.xml.namespace.QName("query.axis.epcis.oliot.org", "ImplementationException"));
        _fault.setClassName("org.oliot.epcis.axis.ImplementationException");
        _fault.setXmlType(new javax.xml.namespace.QName("query.axis.epcis.oliot.org", "ImplementationException"));
        _oper.addFault(_fault);
        _fault = new org.apache.axis.description.FaultDesc();
        _fault.setName("SecurityExceptionFault");
        _fault.setQName(new javax.xml.namespace.QName("query.axis.epcis.oliot.org", "SecurityException"));
        _fault.setClassName("org.oliot.epcis.axis.SecurityException");
        _fault.setXmlType(new javax.xml.namespace.QName("query.axis.epcis.oliot.org", "SecurityException"));
        _oper.addFault(_fault);
        _fault = new org.apache.axis.description.FaultDesc();
        _fault.setName("ValidationExceptionFault");
        _fault.setQName(new javax.xml.namespace.QName("query.axis.epcis.oliot.org", "ValidationException"));
        _fault.setClassName("org.oliot.epcis.axis.ValidationException");
        _fault.setXmlType(new javax.xml.namespace.QName("query.axis.epcis.oliot.org", "ValidationException"));
        _oper.addFault(_fault);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("query.axis.epcis.oliot.org", "Poll"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("query.axis.epcis.oliot.org", "Poll"), org.oliot.epcis.axis.Poll.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("poll", _params, new javax.xml.namespace.QName("query.axis.epcis.oliot.org", "QueryResults"));
        _oper.setReturnType(new javax.xml.namespace.QName("query.axis.epcis.oliot.org", "QueryResults"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "poll"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("poll") == null) {
            _myOperations.put("poll", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("poll")).add(_oper);
        _fault = new org.apache.axis.description.FaultDesc();
        _fault.setName("QueryTooLargeExceptionFault");
        _fault.setQName(new javax.xml.namespace.QName("query.axis.epcis.oliot.org", "QueryTooLargeException"));
        _fault.setClassName("org.oliot.epcis.axis.QueryTooLargeException");
        _fault.setXmlType(new javax.xml.namespace.QName("query.axis.epcis.oliot.org", "QueryTooLargeException"));
        _oper.addFault(_fault);
        _fault = new org.apache.axis.description.FaultDesc();
        _fault.setName("QueryTooComplexExceptionFault");
        _fault.setQName(new javax.xml.namespace.QName("query.axis.epcis.oliot.org", "QueryTooComplexException"));
        _fault.setClassName("org.oliot.epcis.axis.QueryTooComplexException");
        _fault.setXmlType(new javax.xml.namespace.QName("query.axis.epcis.oliot.org", "QueryTooComplexException"));
        _oper.addFault(_fault);
        _fault = new org.apache.axis.description.FaultDesc();
        _fault.setName("NoSuchNameExceptionFault");
        _fault.setQName(new javax.xml.namespace.QName("query.axis.epcis.oliot.org", "NoSuchNameException"));
        _fault.setClassName("org.oliot.epcis.axis.NoSuchNameException");
        _fault.setXmlType(new javax.xml.namespace.QName("query.axis.epcis.oliot.org", "NoSuchNameException"));
        _oper.addFault(_fault);
        _fault = new org.apache.axis.description.FaultDesc();
        _fault.setName("QueryParameterExceptionFault");
        _fault.setQName(new javax.xml.namespace.QName("query.axis.epcis.oliot.org", "QueryParameterException"));
        _fault.setClassName("org.oliot.epcis.axis.QueryParameterException");
        _fault.setXmlType(new javax.xml.namespace.QName("query.axis.epcis.oliot.org", "QueryParameterException"));
        _oper.addFault(_fault);
        _fault = new org.apache.axis.description.FaultDesc();
        _fault.setName("ImplementationExceptionFault");
        _fault.setQName(new javax.xml.namespace.QName("query.axis.epcis.oliot.org", "ImplementationException"));
        _fault.setClassName("org.oliot.epcis.axis.ImplementationException");
        _fault.setXmlType(new javax.xml.namespace.QName("query.axis.epcis.oliot.org", "ImplementationException"));
        _oper.addFault(_fault);
        _fault = new org.apache.axis.description.FaultDesc();
        _fault.setName("SecurityExceptionFault");
        _fault.setQName(new javax.xml.namespace.QName("query.axis.epcis.oliot.org", "SecurityException"));
        _fault.setClassName("org.oliot.epcis.axis.SecurityException");
        _fault.setXmlType(new javax.xml.namespace.QName("query.axis.epcis.oliot.org", "SecurityException"));
        _oper.addFault(_fault);
        _fault = new org.apache.axis.description.FaultDesc();
        _fault.setName("ValidationExceptionFault");
        _fault.setQName(new javax.xml.namespace.QName("query.axis.epcis.oliot.org", "ValidationException"));
        _fault.setClassName("org.oliot.epcis.axis.ValidationException");
        _fault.setXmlType(new javax.xml.namespace.QName("query.axis.epcis.oliot.org", "ValidationException"));
        _oper.addFault(_fault);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("query.axis.epcis.oliot.org", "GetStandardVersion"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("query.axis.epcis.oliot.org", "EmptyParms"), org.oliot.epcis.axis.EmptyParms.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getStandardVersion", _params, new javax.xml.namespace.QName("query.axis.epcis.oliot.org", "GetStandardVersionResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "getStandardVersion"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getStandardVersion") == null) {
            _myOperations.put("getStandardVersion", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getStandardVersion")).add(_oper);
        _fault = new org.apache.axis.description.FaultDesc();
        _fault.setName("ImplementationExceptionFault");
        _fault.setQName(new javax.xml.namespace.QName("query.axis.epcis.oliot.org", "ImplementationException"));
        _fault.setClassName("org.oliot.epcis.axis.ImplementationException");
        _fault.setXmlType(new javax.xml.namespace.QName("query.axis.epcis.oliot.org", "ImplementationException"));
        _oper.addFault(_fault);
        _fault = new org.apache.axis.description.FaultDesc();
        _fault.setName("SecurityExceptionFault");
        _fault.setQName(new javax.xml.namespace.QName("query.axis.epcis.oliot.org", "SecurityException"));
        _fault.setClassName("org.oliot.epcis.axis.SecurityException");
        _fault.setXmlType(new javax.xml.namespace.QName("query.axis.epcis.oliot.org", "SecurityException"));
        _oper.addFault(_fault);
        _fault = new org.apache.axis.description.FaultDesc();
        _fault.setName("ValidationExceptionFault");
        _fault.setQName(new javax.xml.namespace.QName("query.axis.epcis.oliot.org", "ValidationException"));
        _fault.setClassName("org.oliot.epcis.axis.ValidationException");
        _fault.setXmlType(new javax.xml.namespace.QName("query.axis.epcis.oliot.org", "ValidationException"));
        _oper.addFault(_fault);
        _params = new org.apache.axis.description.ParameterDesc [] {
            new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("query.axis.epcis.oliot.org", "GetVendorVersion"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("query.axis.epcis.oliot.org", "EmptyParms"), org.oliot.epcis.axis.EmptyParms.class, false, false), 
        };
        _oper = new org.apache.axis.description.OperationDesc("getVendorVersion", _params, new javax.xml.namespace.QName("query.axis.epcis.oliot.org", "GetVendorVersionResult"));
        _oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        _oper.setElementQName(new javax.xml.namespace.QName("", "getVendorVersion"));
        _oper.setSoapAction("");
        _myOperationsList.add(_oper);
        if (_myOperations.get("getVendorVersion") == null) {
            _myOperations.put("getVendorVersion", new java.util.ArrayList());
        }
        ((java.util.List)_myOperations.get("getVendorVersion")).add(_oper);
        _fault = new org.apache.axis.description.FaultDesc();
        _fault.setName("ImplementationExceptionFault");
        _fault.setQName(new javax.xml.namespace.QName("query.axis.epcis.oliot.org", "ImplementationException"));
        _fault.setClassName("org.oliot.epcis.axis.ImplementationException");
        _fault.setXmlType(new javax.xml.namespace.QName("query.axis.epcis.oliot.org", "ImplementationException"));
        _oper.addFault(_fault);
        _fault = new org.apache.axis.description.FaultDesc();
        _fault.setName("SecurityExceptionFault");
        _fault.setQName(new javax.xml.namespace.QName("query.axis.epcis.oliot.org", "SecurityException"));
        _fault.setClassName("org.oliot.epcis.axis.SecurityException");
        _fault.setXmlType(new javax.xml.namespace.QName("query.axis.epcis.oliot.org", "SecurityException"));
        _oper.addFault(_fault);
        _fault = new org.apache.axis.description.FaultDesc();
        _fault.setName("ValidationExceptionFault");
        _fault.setQName(new javax.xml.namespace.QName("query.axis.epcis.oliot.org", "ValidationException"));
        _fault.setClassName("org.oliot.epcis.axis.ValidationException");
        _fault.setXmlType(new javax.xml.namespace.QName("query.axis.epcis.oliot.org", "ValidationException"));
        _oper.addFault(_fault);
    }

    public EPCISServiceBindingSkeleton() {
        this.impl = new org.oliot.epcis.axis.wsdl.EPCISServiceBindingImpl();
    }

    public EPCISServiceBindingSkeleton(org.oliot.epcis.axis.wsdl.Query_PortType impl) {
        this.impl = impl;
    }
    public java.lang.String[] getQueryNames(org.oliot.epcis.axis.EmptyParms parms) throws java.rmi.RemoteException, org.oliot.epcis.axis.ImplementationException, org.oliot.epcis.axis.SecurityException, org.oliot.epcis.axis.ValidationException
    {
        java.lang.String[] ret = impl.getQueryNames(parms);
        return ret;
    }

    public org.oliot.epcis.axis.VoidHolder subscribe(org.oliot.epcis.axis.Subscribe parms) throws java.rmi.RemoteException, org.oliot.epcis.axis.QueryTooComplexException, org.oliot.epcis.axis.SubscribeNotPermittedException, org.oliot.epcis.axis.QueryParameterException, org.oliot.epcis.axis.NoSuchNameException, org.oliot.epcis.axis.DuplicateSubscriptionException, org.oliot.epcis.axis.InvalidURIException, org.oliot.epcis.axis.ImplementationException, org.oliot.epcis.axis.SecurityException, org.oliot.epcis.axis.SubscriptionControlsException, org.oliot.epcis.axis.ValidationException
    {
        org.oliot.epcis.axis.VoidHolder ret = impl.subscribe(parms);
        return ret;
    }

    public org.oliot.epcis.axis.VoidHolder unsubscribe(org.oliot.epcis.axis.Unsubscribe parms) throws java.rmi.RemoteException, org.oliot.epcis.axis.NoSuchSubscriptionException, org.oliot.epcis.axis.ImplementationException, org.oliot.epcis.axis.SecurityException, org.oliot.epcis.axis.ValidationException
    {
        org.oliot.epcis.axis.VoidHolder ret = impl.unsubscribe(parms);
        return ret;
    }

    public java.lang.String[] getSubscriptionIDs(org.oliot.epcis.axis.GetSubscriptionIDs parms) throws java.rmi.RemoteException, org.oliot.epcis.axis.NoSuchNameException, org.oliot.epcis.axis.ImplementationException, org.oliot.epcis.axis.SecurityException, org.oliot.epcis.axis.ValidationException
    {
        java.lang.String[] ret = impl.getSubscriptionIDs(parms);
        return ret;
    }

    public org.oliot.epcis.axis.QueryResults poll(org.oliot.epcis.axis.Poll parms) throws java.rmi.RemoteException, org.oliot.epcis.axis.QueryTooComplexException, org.oliot.epcis.axis.QueryTooLargeException, org.oliot.epcis.axis.NoSuchNameException, org.oliot.epcis.axis.QueryParameterException, org.oliot.epcis.axis.ImplementationException, org.oliot.epcis.axis.SecurityException, org.oliot.epcis.axis.ValidationException
    {
        org.oliot.epcis.axis.QueryResults ret = impl.poll(parms);
        return ret;
    }

    public java.lang.String getStandardVersion(org.oliot.epcis.axis.EmptyParms parms) throws java.rmi.RemoteException, org.oliot.epcis.axis.ImplementationException, org.oliot.epcis.axis.SecurityException, org.oliot.epcis.axis.ValidationException
    {
        java.lang.String ret = impl.getStandardVersion(parms);
        return ret;
    }

    public java.lang.String getVendorVersion(org.oliot.epcis.axis.EmptyParms parms) throws java.rmi.RemoteException, org.oliot.epcis.axis.ImplementationException, org.oliot.epcis.axis.SecurityException, org.oliot.epcis.axis.ValidationException
    {
        java.lang.String ret = impl.getVendorVersion(parms);
        return ret;
    }

}
