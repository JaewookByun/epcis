/**
 * Query_ServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.oliot.epcis.axis.wsdl;

public class Query_ServiceLocator extends org.apache.axis.client.Service implements org.oliot.epcis.axis.wsdl.Query_Service {

    public Query_ServiceLocator() {
    }


    public Query_ServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public Query_ServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for Query
    private java.lang.String Query_address = "http://localhost:8080/epcis/services/EPCglobalEPCISServicePort";

    public java.lang.String getQueryAddress() {
        return Query_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String QueryWSDDServiceName = "Query";

    public java.lang.String getQueryWSDDServiceName() {
        return QueryWSDDServiceName;
    }

    public void setQueryWSDDServiceName(java.lang.String name) {
        QueryWSDDServiceName = name;
    }

    public org.oliot.epcis.axis.wsdl.Query_PortType getQuery() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(Query_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getQuery(endpoint);
    }

    public org.oliot.epcis.axis.wsdl.Query_PortType getQuery(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            org.oliot.epcis.axis.wsdl.EPCISServiceBindingStub _stub = new org.oliot.epcis.axis.wsdl.EPCISServiceBindingStub(portAddress, this);
            _stub.setPortName(getQueryWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setQueryEndpointAddress(java.lang.String address) {
        Query_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (org.oliot.epcis.axis.wsdl.Query_PortType.class.isAssignableFrom(serviceEndpointInterface)) {
                org.oliot.epcis.axis.wsdl.EPCISServiceBindingStub _stub = new org.oliot.epcis.axis.wsdl.EPCISServiceBindingStub(new java.net.URL(Query_address), this);
                _stub.setPortName(getQueryWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("Query".equals(inputPortName)) {
            return getQuery();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("wsdl.axis.epcis.oliot.org", "Query");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("wsdl.axis.epcis.oliot.org", "Query"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("Query".equals(portName)) {
            setQueryEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
