/**
 * Query_Service.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.oliot.epcis.axis.wsdl;

public interface Query_Service extends javax.xml.rpc.Service {
    public java.lang.String getQueryAddress();

    public org.oliot.epcis.axis.wsdl.Query_PortType getQuery() throws javax.xml.rpc.ServiceException;

    public org.oliot.epcis.axis.wsdl.Query_PortType getQuery(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
