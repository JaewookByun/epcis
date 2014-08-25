/**
 * EPCglobalEPCISService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.oliot.epcis.wsdl;

import java.net.URL;

import javax.xml.rpc.ServiceException;

public interface EPCglobalEPCISService extends javax.xml.rpc.Service {
    public String getEPCglobalEPCISServicePortAddress();

    public EPCISServicePortType getEPCglobalEPCISServicePort() throws ServiceException;

    public EPCISServicePortType getEPCglobalEPCISServicePort(URL portAddress) throws ServiceException;
}
