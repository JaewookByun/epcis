/**
 * EPCglobalEPCISServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.oliot.epcis.wsdl;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.Remote;
import java.util.HashSet;
import java.util.Iterator;

import javax.xml.namespace.QName;
import javax.xml.rpc.ServiceException;

import org.apache.axis.AxisFault;
import org.apache.axis.EngineConfiguration;
import org.apache.axis.client.Service;
import org.apache.axis.client.Stub;

@SuppressWarnings("serial")
public class EPCglobalEPCISServiceLocator extends Service implements EPCglobalEPCISService {

	public EPCglobalEPCISServiceLocator() {}


	public EPCglobalEPCISServiceLocator( EngineConfiguration config) {
		super(config);
	}

	public EPCglobalEPCISServiceLocator( String wsdlLoc, QName sName) throws ServiceException {
		super(wsdlLoc, sName);
	}

	// Use to get a proxy class for EPCglobalEPCISServicePort
	private String EPCglobalEPCISServicePort_address = "http://localhost:6060/axis/services/EPCglobalEPCISService";

	public String getEPCglobalEPCISServicePortAddress() {
		return EPCglobalEPCISServicePort_address;
	}

	// The WSDD service name defaults to the port name.
	private String EPCglobalEPCISServicePortWSDDServiceName = "EPCglobal Services";

	public String getEPCglobalEPCISServicePortWSDDServiceName() {
		return EPCglobalEPCISServicePortWSDDServiceName;
	}

	public void setEPCglobalEPCISServicePortWSDDServiceName(String name) {
		EPCglobalEPCISServicePortWSDDServiceName = name;
	}

	public EPCISServicePortType getEPCglobalEPCISServicePort() throws ServiceException {
		URL endpoint;
		try {
			endpoint = new URL(EPCglobalEPCISServicePort_address);
		}
		catch (MalformedURLException e) {
			throw new ServiceException(e);
		}
		return getEPCglobalEPCISServicePort(endpoint);
	}

	public EPCISServicePortType getEPCglobalEPCISServicePort(URL portAddress) throws ServiceException {
		try {
			EPCISServiceBindingStub _stub = new EPCISServiceBindingStub(portAddress, this);
			_stub.setPortName(getEPCglobalEPCISServicePortWSDDServiceName());
			return _stub;
		}
		catch (AxisFault e) {
			return null;
		}
	}

	public void setEPCglobalEPCISServicePortEndpointAddress(String address) {
		EPCglobalEPCISServicePort_address = address;
	}

	/**
	 * For the given interface, get the stub implementation.
	 * If this service has no port for the given interface,
	 * then ServiceException is thrown.
	 */
	@SuppressWarnings("rawtypes")
	public Remote getPort(Class serviceEndpointInterface) throws ServiceException {
		try {
			if (EPCISServicePortType.class.isAssignableFrom(serviceEndpointInterface)) {
				EPCISServiceBindingStub _stub = new EPCISServiceBindingStub(new URL(EPCglobalEPCISServicePort_address), this);
				_stub.setPortName(getEPCglobalEPCISServicePortWSDDServiceName());
				return _stub;
			}
		}
		catch (Throwable t) {
			throw new ServiceException(t);
		}
		throw new ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
	}

	/**
	 * For the given interface, get the stub implementation.
	 * If this service has no port for the given interface,
	 * then ServiceException is thrown.
	 */
	@SuppressWarnings("rawtypes")
	public Remote getPort(QName portName, Class serviceEndpointInterface) throws ServiceException {
		if (portName == null) {
			return getPort(serviceEndpointInterface);
		}
		String inputPortName = portName.getLocalPart();
		if ("EPCglobalEPCISServicePort".equals(inputPortName)) {
			return getEPCglobalEPCISServicePort();
		}
		else  {
			Remote _stub = getPort(serviceEndpointInterface);
			((Stub) _stub).setPortName(portName);
			return _stub;
		}
	}

	public QName getServiceName() {
		return new QName("wsdl.epcis.oliot.org", "EPCglobalEPCISService");
	}

	@SuppressWarnings("rawtypes")
	private HashSet ports = null;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Iterator getPorts() {
		if (ports == null) {
			ports = new HashSet();
			ports.add(new QName("wsdl.epcis.oliot.org", "EPCglobalEPCISServicePort"));
		}
		return ports.iterator();
	}

	/**
	 * Set the endpoint address for the specified port name.
	 */
	public void setEndpointAddress(String portName, String address) throws ServiceException {

		if ("EPCglobalEPCISServicePort".equals(portName)) {
			setEPCglobalEPCISServicePortEndpointAddress(address);
		}
		else 
		{ // Unknown Port Name
			throw new ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
		}
	}

	/**
	 * Set the endpoint address for the specified port name.
	 */
	public void setEndpointAddress(QName portName, String address) throws ServiceException {
		setEndpointAddress(portName.getLocalPart(), address);
	}

}
