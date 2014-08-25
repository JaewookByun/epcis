/**
 * EPCglobalEPCISServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.oliot.epcis.wsdl;

@SuppressWarnings("serial")
public class EPCglobalEPCISServiceLocator extends org.apache.axis.client.Service implements org.oliot.epcis.wsdl.EPCglobalEPCISService {

    public EPCglobalEPCISServiceLocator() {
    }


    public EPCglobalEPCISServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public EPCglobalEPCISServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for EPCglobalEPCISServicePort
    private java.lang.String EPCglobalEPCISServicePort_address = "http://localhost:6060/axis/services/EPCglobalEPCISService";

    public java.lang.String getEPCglobalEPCISServicePortAddress() {
        return EPCglobalEPCISServicePort_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String EPCglobalEPCISServicePortWSDDServiceName = "EPCglobalEPCISServicePort";

    public java.lang.String getEPCglobalEPCISServicePortWSDDServiceName() {
        return EPCglobalEPCISServicePortWSDDServiceName;
    }

    public void setEPCglobalEPCISServicePortWSDDServiceName(java.lang.String name) {
        EPCglobalEPCISServicePortWSDDServiceName = name;
    }

    public org.oliot.epcis.wsdl.EPCISServicePortType getEPCglobalEPCISServicePort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(EPCglobalEPCISServicePort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getEPCglobalEPCISServicePort(endpoint);
    }

    public org.oliot.epcis.wsdl.EPCISServicePortType getEPCglobalEPCISServicePort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            org.oliot.epcis.wsdl.EPCISServiceBindingStub _stub = new org.oliot.epcis.wsdl.EPCISServiceBindingStub(portAddress, this);
            _stub.setPortName(getEPCglobalEPCISServicePortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setEPCglobalEPCISServicePortEndpointAddress(java.lang.String address) {
        EPCglobalEPCISServicePort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    @SuppressWarnings("rawtypes")
	public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (org.oliot.epcis.wsdl.EPCISServicePortType.class.isAssignableFrom(serviceEndpointInterface)) {
                org.oliot.epcis.wsdl.EPCISServiceBindingStub _stub = new org.oliot.epcis.wsdl.EPCISServiceBindingStub(new java.net.URL(EPCglobalEPCISServicePort_address), this);
                _stub.setPortName(getEPCglobalEPCISServicePortWSDDServiceName());
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
    @SuppressWarnings("rawtypes")
	public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("EPCglobalEPCISServicePort".equals(inputPortName)) {
            return getEPCglobalEPCISServicePort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("wsdl.epcis.oliot.org", "EPCglobalEPCISService");
    }

    @SuppressWarnings("rawtypes")
	private java.util.HashSet ports = null;

    @SuppressWarnings({ "rawtypes", "unchecked" })
	public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("wsdl.epcis.oliot.org", "EPCglobalEPCISServicePort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("EPCglobalEPCISServicePort".equals(portName)) {
            setEPCglobalEPCISServicePortEndpointAddress(address);
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
