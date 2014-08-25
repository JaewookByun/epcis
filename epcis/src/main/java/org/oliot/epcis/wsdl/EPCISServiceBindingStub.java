/**
 * EPCISServiceBindingStub.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.oliot.epcis.wsdl;

public class EPCISServiceBindingStub extends org.apache.axis.client.Stub implements org.oliot.epcis.wsdl.EPCISServicePortType {
    @SuppressWarnings("rawtypes")
	private java.util.Vector cachedSerClasses = new java.util.Vector();
    @SuppressWarnings("rawtypes")
	private java.util.Vector cachedSerQNames = new java.util.Vector();
    @SuppressWarnings("rawtypes")
	private java.util.Vector cachedSerFactories = new java.util.Vector();
    @SuppressWarnings("rawtypes")
	private java.util.Vector cachedDeserFactories = new java.util.Vector();

    static org.apache.axis.description.OperationDesc [] _operations;

    static {
        _operations = new org.apache.axis.description.OperationDesc[7];
        _initOperationDesc1();
    }

    private static void _initOperationDesc1(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getQueryNames");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("query.epcis.oliot.org", "GetQueryNames"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("query.epcis.oliot.org", "EmptyParms"), org.oliot.epcis.query.EmptyParms.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("query.epcis.oliot.org", "ArrayOfString"));
        oper.setReturnClass(java.lang.String[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("query.epcis.oliot.org", "GetQueryNamesResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("", "string"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("query.epcis.oliot.org", "ImplementationException"),
                      "org.oliot.epcis.query.ImplementationException",
                      new javax.xml.namespace.QName("query.epcis.oliot.org", "ImplementationException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("query.epcis.oliot.org", "SecurityException"),
                      "org.oliot.epcis.query.SecurityException",
                      new javax.xml.namespace.QName("query.epcis.oliot.org", "SecurityException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("query.epcis.oliot.org", "ValidationException"),
                      "org.oliot.epcis.query.ValidationException",
                      new javax.xml.namespace.QName("query.epcis.oliot.org", "ValidationException"), 
                      true
                     ));
        _operations[0] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("subscribe");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("query.epcis.oliot.org", "Subscribe"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("query.epcis.oliot.org", "Subscribe"), org.oliot.epcis.query.Subscribe.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("query.epcis.oliot.org", "VoidHolder"));
        oper.setReturnClass(org.oliot.epcis.query.VoidHolder.class);
        oper.setReturnQName(new javax.xml.namespace.QName("query.epcis.oliot.org", "SubscribeResult"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("query.epcis.oliot.org", "QueryTooComplexException"),
                      "org.oliot.epcis.query.QueryTooComplexException",
                      new javax.xml.namespace.QName("query.epcis.oliot.org", "QueryTooComplexException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("query.epcis.oliot.org", "SubscribeNotPermittedException"),
                      "org.oliot.epcis.query.SubscribeNotPermittedException",
                      new javax.xml.namespace.QName("query.epcis.oliot.org", "SubscribeNotPermittedException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("query.epcis.oliot.org", "QueryParameterException"),
                      "org.oliot.epcis.query.QueryParameterException",
                      new javax.xml.namespace.QName("query.epcis.oliot.org", "QueryParameterException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("query.epcis.oliot.org", "NoSuchNameException"),
                      "org.oliot.epcis.query.NoSuchNameException",
                      new javax.xml.namespace.QName("query.epcis.oliot.org", "NoSuchNameException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("query.epcis.oliot.org", "DuplicateSubscriptionException"),
                      "org.oliot.epcis.query.DuplicateSubscriptionException",
                      new javax.xml.namespace.QName("query.epcis.oliot.org", "DuplicateSubscriptionException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("query.epcis.oliot.org", "InvalidURIException"),
                      "org.oliot.epcis.query.InvalidURIException",
                      new javax.xml.namespace.QName("query.epcis.oliot.org", "InvalidURIException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("query.epcis.oliot.org", "ImplementationException"),
                      "org.oliot.epcis.query.ImplementationException",
                      new javax.xml.namespace.QName("query.epcis.oliot.org", "ImplementationException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("query.epcis.oliot.org", "SecurityException"),
                      "org.oliot.epcis.query.SecurityException",
                      new javax.xml.namespace.QName("query.epcis.oliot.org", "SecurityException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("query.epcis.oliot.org", "SubscriptionControlsException"),
                      "org.oliot.epcis.query.SubscriptionControlsException",
                      new javax.xml.namespace.QName("query.epcis.oliot.org", "SubscriptionControlsException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("query.epcis.oliot.org", "ValidationException"),
                      "org.oliot.epcis.query.ValidationException",
                      new javax.xml.namespace.QName("query.epcis.oliot.org", "ValidationException"), 
                      true
                     ));
        _operations[1] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("unsubscribe");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("query.epcis.oliot.org", "Unsubscribe"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("query.epcis.oliot.org", "Unsubscribe"), org.oliot.epcis.query.Unsubscribe.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("query.epcis.oliot.org", "VoidHolder"));
        oper.setReturnClass(org.oliot.epcis.query.VoidHolder.class);
        oper.setReturnQName(new javax.xml.namespace.QName("query.epcis.oliot.org", "UnsubscribeResult"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("query.epcis.oliot.org", "NoSuchSubscriptionException"),
                      "org.oliot.epcis.query.NoSuchSubscriptionException",
                      new javax.xml.namespace.QName("query.epcis.oliot.org", "NoSuchSubscriptionException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("query.epcis.oliot.org", "ImplementationException"),
                      "org.oliot.epcis.query.ImplementationException",
                      new javax.xml.namespace.QName("query.epcis.oliot.org", "ImplementationException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("query.epcis.oliot.org", "SecurityException"),
                      "org.oliot.epcis.query.SecurityException",
                      new javax.xml.namespace.QName("query.epcis.oliot.org", "SecurityException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("query.epcis.oliot.org", "ValidationException"),
                      "org.oliot.epcis.query.ValidationException",
                      new javax.xml.namespace.QName("query.epcis.oliot.org", "ValidationException"), 
                      true
                     ));
        _operations[2] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getSubscriptionIDs");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("query.epcis.oliot.org", "GetSubscriptionIDs"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("query.epcis.oliot.org", "GetSubscriptionIDs"), org.oliot.epcis.query.GetSubscriptionIDs.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("query.epcis.oliot.org", "ArrayOfString"));
        oper.setReturnClass(java.lang.String[].class);
        oper.setReturnQName(new javax.xml.namespace.QName("query.epcis.oliot.org", "GetSubscriptionIDsResult"));
        param = oper.getReturnParamDesc();
        param.setItemQName(new javax.xml.namespace.QName("", "string"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("query.epcis.oliot.org", "NoSuchNameException"),
                      "org.oliot.epcis.query.NoSuchNameException",
                      new javax.xml.namespace.QName("query.epcis.oliot.org", "NoSuchNameException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("query.epcis.oliot.org", "ImplementationException"),
                      "org.oliot.epcis.query.ImplementationException",
                      new javax.xml.namespace.QName("query.epcis.oliot.org", "ImplementationException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("query.epcis.oliot.org", "SecurityException"),
                      "org.oliot.epcis.query.SecurityException",
                      new javax.xml.namespace.QName("query.epcis.oliot.org", "SecurityException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("query.epcis.oliot.org", "ValidationException"),
                      "org.oliot.epcis.query.ValidationException",
                      new javax.xml.namespace.QName("query.epcis.oliot.org", "ValidationException"), 
                      true
                     ));
        _operations[3] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("poll");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("query.epcis.oliot.org", "Poll"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("query.epcis.oliot.org", "Poll"), org.oliot.epcis.query.Poll.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("query.epcis.oliot.org", "QueryResults"));
        oper.setReturnClass(org.oliot.epcis.query.QueryResults.class);
        oper.setReturnQName(new javax.xml.namespace.QName("query.epcis.oliot.org", "QueryResults"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("query.epcis.oliot.org", "QueryTooLargeException"),
                      "org.oliot.epcis.query.QueryTooLargeException",
                      new javax.xml.namespace.QName("query.epcis.oliot.org", "QueryTooLargeException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("query.epcis.oliot.org", "QueryTooComplexException"),
                      "org.oliot.epcis.query.QueryTooComplexException",
                      new javax.xml.namespace.QName("query.epcis.oliot.org", "QueryTooComplexException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("query.epcis.oliot.org", "NoSuchNameException"),
                      "org.oliot.epcis.query.NoSuchNameException",
                      new javax.xml.namespace.QName("query.epcis.oliot.org", "NoSuchNameException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("query.epcis.oliot.org", "QueryParameterException"),
                      "org.oliot.epcis.query.QueryParameterException",
                      new javax.xml.namespace.QName("query.epcis.oliot.org", "QueryParameterException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("query.epcis.oliot.org", "ImplementationException"),
                      "org.oliot.epcis.query.ImplementationException",
                      new javax.xml.namespace.QName("query.epcis.oliot.org", "ImplementationException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("query.epcis.oliot.org", "SecurityException"),
                      "org.oliot.epcis.query.SecurityException",
                      new javax.xml.namespace.QName("query.epcis.oliot.org", "SecurityException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("query.epcis.oliot.org", "ValidationException"),
                      "org.oliot.epcis.query.ValidationException",
                      new javax.xml.namespace.QName("query.epcis.oliot.org", "ValidationException"), 
                      true
                     ));
        _operations[4] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getStandardVersion");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("query.epcis.oliot.org", "GetStandardVersion"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("query.epcis.oliot.org", "EmptyParms"), org.oliot.epcis.query.EmptyParms.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        oper.setReturnClass(java.lang.String.class);
        oper.setReturnQName(new javax.xml.namespace.QName("query.epcis.oliot.org", "GetStandardVersionResult"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("query.epcis.oliot.org", "ImplementationException"),
                      "org.oliot.epcis.query.ImplementationException",
                      new javax.xml.namespace.QName("query.epcis.oliot.org", "ImplementationException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("query.epcis.oliot.org", "SecurityException"),
                      "org.oliot.epcis.query.SecurityException",
                      new javax.xml.namespace.QName("query.epcis.oliot.org", "SecurityException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("query.epcis.oliot.org", "ValidationException"),
                      "org.oliot.epcis.query.ValidationException",
                      new javax.xml.namespace.QName("query.epcis.oliot.org", "ValidationException"), 
                      true
                     ));
        _operations[5] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("getVendorVersion");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("query.epcis.oliot.org", "GetVendorVersion"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("query.epcis.oliot.org", "EmptyParms"), org.oliot.epcis.query.EmptyParms.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        oper.setReturnClass(java.lang.String.class);
        oper.setReturnQName(new javax.xml.namespace.QName("query.epcis.oliot.org", "GetVendorVersionResult"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("query.epcis.oliot.org", "ImplementationException"),
                      "org.oliot.epcis.query.ImplementationException",
                      new javax.xml.namespace.QName("query.epcis.oliot.org", "ImplementationException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("query.epcis.oliot.org", "SecurityException"),
                      "org.oliot.epcis.query.SecurityException",
                      new javax.xml.namespace.QName("query.epcis.oliot.org", "SecurityException"), 
                      true
                     ));
        oper.addFault(new org.apache.axis.description.FaultDesc(
                      new javax.xml.namespace.QName("query.epcis.oliot.org", "ValidationException"),
                      "org.oliot.epcis.query.ValidationException",
                      new javax.xml.namespace.QName("query.epcis.oliot.org", "ValidationException"), 
                      true
                     ));
        _operations[6] = oper;

    }

    public EPCISServiceBindingStub() throws org.apache.axis.AxisFault {
         this(null);
    }

    public EPCISServiceBindingStub(java.net.URL endpointURL, javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
         this(service);
         super.cachedEndpoint = endpointURL;
    }

    @SuppressWarnings({ "rawtypes", "unused" })
	public EPCISServiceBindingStub(javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
        if (service == null) {
            super.service = new org.apache.axis.client.Service();
        } else {
            super.service = service;
        }
        ((org.apache.axis.client.Service)super.service).setTypeMappingVersion("1.2");
            java.lang.Class cls;
            javax.xml.namespace.QName qName;
            javax.xml.namespace.QName qName2;
            java.lang.Class beansf = org.apache.axis.encoding.ser.BeanSerializerFactory.class;
            java.lang.Class beandf = org.apache.axis.encoding.ser.BeanDeserializerFactory.class;
            java.lang.Class enumsf = org.apache.axis.encoding.ser.EnumSerializerFactory.class;
            java.lang.Class enumdf = org.apache.axis.encoding.ser.EnumDeserializerFactory.class;
            java.lang.Class arraysf = org.apache.axis.encoding.ser.ArraySerializerFactory.class;
            java.lang.Class arraydf = org.apache.axis.encoding.ser.ArrayDeserializerFactory.class;
            java.lang.Class simplesf = org.apache.axis.encoding.ser.SimpleSerializerFactory.class;
            java.lang.Class simpledf = org.apache.axis.encoding.ser.SimpleDeserializerFactory.class;
            java.lang.Class simplelistsf = org.apache.axis.encoding.ser.SimpleListSerializerFactory.class;
            java.lang.Class simplelistdf = org.apache.axis.encoding.ser.SimpleListDeserializerFactory.class;
        addBindings0();
        addBindings1();
    }

    @SuppressWarnings({ "rawtypes", "unused", "unchecked" })
	private void addBindings0() {
            java.lang.Class cls;
            javax.xml.namespace.QName qName;
            javax.xml.namespace.QName qName2;
            java.lang.Class beansf = org.apache.axis.encoding.ser.BeanSerializerFactory.class;
            java.lang.Class beandf = org.apache.axis.encoding.ser.BeanDeserializerFactory.class;
            java.lang.Class enumsf = org.apache.axis.encoding.ser.EnumSerializerFactory.class;
            java.lang.Class enumdf = org.apache.axis.encoding.ser.EnumDeserializerFactory.class;
            java.lang.Class arraysf = org.apache.axis.encoding.ser.ArraySerializerFactory.class;
            java.lang.Class arraydf = org.apache.axis.encoding.ser.ArrayDeserializerFactory.class;
            java.lang.Class simplesf = org.apache.axis.encoding.ser.SimpleSerializerFactory.class;
            java.lang.Class simpledf = org.apache.axis.encoding.ser.SimpleDeserializerFactory.class;
            java.lang.Class simplelistsf = org.apache.axis.encoding.ser.SimpleListSerializerFactory.class;
            java.lang.Class simplelistdf = org.apache.axis.encoding.ser.SimpleListDeserializerFactory.class;
            qName = new javax.xml.namespace.QName("epcglobal.oliot.org", "Document");
            cachedSerQNames.add(qName);
            cls = org.oliot.epcglobal.Document.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("epcglobal.oliot.org", "EPC");
            cachedSerQNames.add(qName);
            cls = org.oliot.epcglobal.EPC.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("epcis.oliot.org", "ActionType");
            cachedSerQNames.add(qName);
            cls = org.oliot.epcis.ActionType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("epcis.oliot.org", "AggregationEventExtension2Type");
            cachedSerQNames.add(qName);
            cls = org.oliot.epcis.AggregationEventExtension2Type.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("epcis.oliot.org", "AggregationEventExtensionType");
            cachedSerQNames.add(qName);
            cls = org.oliot.epcis.AggregationEventExtensionType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("epcis.oliot.org", "AggregationEventType");
            cachedSerQNames.add(qName);
            cls = org.oliot.epcis.AggregationEventType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("epcis.oliot.org", "BusinessLocationExtensionType");
            cachedSerQNames.add(qName);
            cls = org.oliot.epcis.BusinessLocationExtensionType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("epcis.oliot.org", "BusinessLocationIDType");
            cachedSerQNames.add(qName);
            cls = org.apache.axis.types.URI.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("epcis.oliot.org", "BusinessLocationType");
            cachedSerQNames.add(qName);
            cls = org.oliot.epcis.BusinessLocationType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("epcis.oliot.org", "BusinessStepIDType");
            cachedSerQNames.add(qName);
            cls = org.apache.axis.types.URI.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("epcis.oliot.org", "BusinessTransactionIDType");
            cachedSerQNames.add(qName);
            cls = org.apache.axis.types.URI.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("epcis.oliot.org", "BusinessTransactionListType");
            cachedSerQNames.add(qName);
            cls = org.oliot.epcis.BusinessTransactionType[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("epcis.oliot.org", "BusinessTransactionType");
            qName2 = new javax.xml.namespace.QName("", "bizTransaction");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("epcis.oliot.org", "BusinessTransactionType");
            cachedSerQNames.add(qName);
            cls = org.oliot.epcis.BusinessTransactionType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("epcis.oliot.org", "BusinessTransactionTypeIDType");
            cachedSerQNames.add(qName);
            cls = org.apache.axis.types.URI.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("epcis.oliot.org", "DestinationListType");
            cachedSerQNames.add(qName);
            cls = org.oliot.epcis.SourceDestType[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("epcis.oliot.org", "SourceDestType");
            qName2 = new javax.xml.namespace.QName("", "destination");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("epcis.oliot.org", "DispositionIDType");
            cachedSerQNames.add(qName);
            cls = org.apache.axis.types.URI.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("epcis.oliot.org", "EPCClassType");
            cachedSerQNames.add(qName);
            cls = org.apache.axis.types.URI.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("epcis.oliot.org", "EPCISBodyExtensionType");
            cachedSerQNames.add(qName);
            cls = org.oliot.epcis.EPCISBodyExtensionType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("epcis.oliot.org", "EPCISBodyType");
            cachedSerQNames.add(qName);
            cls = org.oliot.epcis.EPCISBodyType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("epcis.oliot.org", "EPCISDocumentExtensionType");
            cachedSerQNames.add(qName);
            cls = org.oliot.epcis.EPCISDocumentExtensionType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("epcis.oliot.org", "EPCISDocumentType");
            cachedSerQNames.add(qName);
            cls = org.oliot.epcis.EPCISDocumentType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("epcis.oliot.org", "EPCISEventExtensionType");
            cachedSerQNames.add(qName);
            cls = org.oliot.epcis.EPCISEventExtensionType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("epcis.oliot.org", "EPCISEventListExtension2Type");
            cachedSerQNames.add(qName);
            cls = org.oliot.epcis.EPCISEventListExtension2Type.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("epcis.oliot.org", "EPCISEventListExtensionType");
            cachedSerQNames.add(qName);
            cls = org.oliot.epcis.EPCISEventListExtensionType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("epcis.oliot.org", "EPCISEventType");
            cachedSerQNames.add(qName);
            cls = org.oliot.epcis.EPCISEventType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("epcis.oliot.org", "EPCISHeaderExtensionType");
            cachedSerQNames.add(qName);
            cls = org.oliot.epcis.EPCISHeaderExtensionType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("epcis.oliot.org", "EPCISHeaderType");
            cachedSerQNames.add(qName);
            cls = org.oliot.epcis.EPCISHeaderType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("epcis.oliot.org", "EPCListType");
            cachedSerQNames.add(qName);
            cls = org.oliot.epcglobal.EPC[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("epcglobal.oliot.org", "EPC");
            qName2 = new javax.xml.namespace.QName("", "epc");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("epcis.oliot.org", "EventListType");
            cachedSerQNames.add(qName);
            cls = org.oliot.epcis.EventListType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("epcis.oliot.org", "ILMDExtensionType");
            cachedSerQNames.add(qName);
            cls = org.oliot.epcis.ILMDExtensionType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("epcis.oliot.org", "ILMDType");
            cachedSerQNames.add(qName);
            cls = org.oliot.epcis.ILMDType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("epcis.oliot.org", "ObjectEventExtension2Type");
            cachedSerQNames.add(qName);
            cls = org.oliot.epcis.ObjectEventExtension2Type.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("epcis.oliot.org", "ObjectEventExtensionType");
            cachedSerQNames.add(qName);
            cls = org.oliot.epcis.ObjectEventExtensionType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("epcis.oliot.org", "ObjectEventType");
            cachedSerQNames.add(qName);
            cls = org.oliot.epcis.ObjectEventType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("epcis.oliot.org", "ParentIDType");
            cachedSerQNames.add(qName);
            cls = org.apache.axis.types.URI.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("epcis.oliot.org", "QuantityElementType");
            cachedSerQNames.add(qName);
            cls = org.oliot.epcis.QuantityElementType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("epcis.oliot.org", "QuantityEventExtensionType");
            cachedSerQNames.add(qName);
            cls = org.oliot.epcis.QuantityEventExtensionType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("epcis.oliot.org", "QuantityEventType");
            cachedSerQNames.add(qName);
            cls = org.oliot.epcis.QuantityEventType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("epcis.oliot.org", "QuantityListType");
            cachedSerQNames.add(qName);
            cls = org.oliot.epcis.QuantityElementType[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("epcis.oliot.org", "QuantityElementType");
            qName2 = new javax.xml.namespace.QName("", "quantityElement");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("epcis.oliot.org", "ReadPointExtensionType");
            cachedSerQNames.add(qName);
            cls = org.oliot.epcis.ReadPointExtensionType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("epcis.oliot.org", "ReadPointIDType");
            cachedSerQNames.add(qName);
            cls = org.apache.axis.types.URI.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("epcis.oliot.org", "ReadPointType");
            cachedSerQNames.add(qName);
            cls = org.oliot.epcis.ReadPointType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("epcis.oliot.org", "SourceDestIDType");
            cachedSerQNames.add(qName);
            cls = org.apache.axis.types.URI.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("epcis.oliot.org", "SourceDestType");
            cachedSerQNames.add(qName);
            cls = org.oliot.epcis.SourceDestType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("epcis.oliot.org", "SourceDestTypeIDType");
            cachedSerQNames.add(qName);
            cls = org.apache.axis.types.URI.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("epcis.oliot.org", "SourceListType");
            cachedSerQNames.add(qName);
            cls = org.oliot.epcis.SourceDestType[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("epcis.oliot.org", "SourceDestType");
            qName2 = new javax.xml.namespace.QName("", "source");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("epcis.oliot.org", "TransactionEventExtension2Type");
            cachedSerQNames.add(qName);
            cls = org.oliot.epcis.TransactionEventExtension2Type.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("epcis.oliot.org", "TransactionEventExtensionType");
            cachedSerQNames.add(qName);
            cls = org.oliot.epcis.TransactionEventExtensionType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("epcis.oliot.org", "TransactionEventType");
            cachedSerQNames.add(qName);
            cls = org.oliot.epcis.TransactionEventType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("epcis.oliot.org", "TransformationEventExtensionType");
            cachedSerQNames.add(qName);
            cls = org.oliot.epcis.TransformationEventExtensionType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("epcis.oliot.org", "TransformationEventType");
            cachedSerQNames.add(qName);
            cls = org.oliot.epcis.TransformationEventType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("epcis.oliot.org", "TransformationIDType");
            cachedSerQNames.add(qName);
            cls = org.apache.axis.types.URI.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("epcis.oliot.org", "UOMType");
            cachedSerQNames.add(qName);
            cls = org.apache.axis.types.URI.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", "BusinessScope");
            cachedSerQNames.add(qName);
            cls = org.unece.www.cefact.namespaces.StandardBusinessDocumentHeader.Scope[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", "Scope");
            qName2 = new javax.xml.namespace.QName("http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", "Scope");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", "BusinessService");
            cachedSerQNames.add(qName);
            cls = org.unece.www.cefact.namespaces.StandardBusinessDocumentHeader.BusinessService.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", "ContactInformation");
            cachedSerQNames.add(qName);
            cls = org.unece.www.cefact.namespaces.StandardBusinessDocumentHeader.ContactInformation.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", "CorrelationInformation");
            cachedSerQNames.add(qName);
            cls = org.unece.www.cefact.namespaces.StandardBusinessDocumentHeader.CorrelationInformation.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", "DocumentIdentification");
            cachedSerQNames.add(qName);
            cls = org.unece.www.cefact.namespaces.StandardBusinessDocumentHeader.DocumentIdentification.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", "Language");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", "Manifest");
            cachedSerQNames.add(qName);
            cls = org.unece.www.cefact.namespaces.StandardBusinessDocumentHeader.Manifest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", "ManifestItem");
            cachedSerQNames.add(qName);
            cls = org.unece.www.cefact.namespaces.StandardBusinessDocumentHeader.ManifestItem.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", "MimeTypeQualifier");
            cachedSerQNames.add(qName);
            cls = java.lang.String.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", "Partner");
            cachedSerQNames.add(qName);
            cls = org.unece.www.cefact.namespaces.StandardBusinessDocumentHeader.Partner.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", "PartnerIdentification");
            cachedSerQNames.add(qName);
            cls = org.unece.www.cefact.namespaces.StandardBusinessDocumentHeader.PartnerIdentification.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(org.apache.axis.encoding.ser.BaseSerializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleSerializerFactory.class, cls, qName));
            cachedDeserFactories.add(org.apache.axis.encoding.ser.BaseDeserializerFactory.createFactory(org.apache.axis.encoding.ser.SimpleDeserializerFactory.class, cls, qName));

            qName = new javax.xml.namespace.QName("http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", "Scope");
            cachedSerQNames.add(qName);
            cls = org.unece.www.cefact.namespaces.StandardBusinessDocumentHeader.Scope.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", "ServiceTransaction");
            cachedSerQNames.add(qName);
            cls = org.unece.www.cefact.namespaces.StandardBusinessDocumentHeader.ServiceTransaction.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", "StandardBusinessDocument");
            cachedSerQNames.add(qName);
            cls = org.unece.www.cefact.namespaces.StandardBusinessDocumentHeader.StandardBusinessDocument.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", "StandardBusinessDocumentHeader");
            cachedSerQNames.add(qName);
            cls = org.unece.www.cefact.namespaces.StandardBusinessDocumentHeader.StandardBusinessDocumentHeader.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", "TypeOfServiceTransaction");
            cachedSerQNames.add(qName);
            cls = org.unece.www.cefact.namespaces.StandardBusinessDocumentHeader.TypeOfServiceTransaction.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("masterdata.epcis.oliot.org", "AttributeType");
            cachedSerQNames.add(qName);
            cls = org.oliot.epcis.masterdata.AttributeType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("masterdata.epcis.oliot.org", "EPCISMasterDataBodyExtensionType");
            cachedSerQNames.add(qName);
            cls = org.oliot.epcis.masterdata.EPCISMasterDataBodyExtensionType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("masterdata.epcis.oliot.org", "EPCISMasterDataBodyType");
            cachedSerQNames.add(qName);
            cls = org.oliot.epcis.masterdata.EPCISMasterDataBodyType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("masterdata.epcis.oliot.org", "EPCISMasterDataDocumentExtensionType");
            cachedSerQNames.add(qName);
            cls = org.oliot.epcis.masterdata.EPCISMasterDataDocumentExtensionType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("masterdata.epcis.oliot.org", "EPCISMasterDataDocumentType");
            cachedSerQNames.add(qName);
            cls = org.oliot.epcis.masterdata.EPCISMasterDataDocumentType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("masterdata.epcis.oliot.org", "EPCISMasterDataHeaderExtensionType");
            cachedSerQNames.add(qName);
            cls = org.oliot.epcis.masterdata.EPCISMasterDataHeaderExtensionType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("masterdata.epcis.oliot.org", "IDListType");
            cachedSerQNames.add(qName);
            cls = org.apache.axis.types.URI[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "anyURI");
            qName2 = new javax.xml.namespace.QName("", "id");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("masterdata.epcis.oliot.org", "VocabularyElementExtensionType");
            cachedSerQNames.add(qName);
            cls = org.oliot.epcis.masterdata.VocabularyElementExtensionType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("masterdata.epcis.oliot.org", "VocabularyElementListType");
            cachedSerQNames.add(qName);
            cls = org.oliot.epcis.masterdata.VocabularyElementType[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("masterdata.epcis.oliot.org", "VocabularyElementType");
            qName2 = new javax.xml.namespace.QName("", "VocabularyElement");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("masterdata.epcis.oliot.org", "VocabularyElementType");
            cachedSerQNames.add(qName);
            cls = org.oliot.epcis.masterdata.VocabularyElementType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("masterdata.epcis.oliot.org", "VocabularyExtensionType");
            cachedSerQNames.add(qName);
            cls = org.oliot.epcis.masterdata.VocabularyExtensionType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("masterdata.epcis.oliot.org", "VocabularyListType");
            cachedSerQNames.add(qName);
            cls = org.oliot.epcis.masterdata.VocabularyType[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("masterdata.epcis.oliot.org", "VocabularyType");
            qName2 = new javax.xml.namespace.QName("", "Vocabulary");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("masterdata.epcis.oliot.org", "VocabularyType");
            cachedSerQNames.add(qName);
            cls = org.oliot.epcis.masterdata.VocabularyType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("query.epcis.oliot.org", "ArrayOfString");
            cachedSerQNames.add(qName);
            cls = java.lang.String[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string");
            qName2 = new javax.xml.namespace.QName("", "string");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("query.epcis.oliot.org", "DuplicateNameException");
            cachedSerQNames.add(qName);
            cls = org.oliot.epcis.query.DuplicateNameException.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("query.epcis.oliot.org", "DuplicateSubscriptionException");
            cachedSerQNames.add(qName);
            cls = org.oliot.epcis.query.DuplicateSubscriptionException.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("query.epcis.oliot.org", "EmptyParms");
            cachedSerQNames.add(qName);
            cls = org.oliot.epcis.query.EmptyParms.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("query.epcis.oliot.org", "EPCISException");
            cachedSerQNames.add(qName);
            cls = org.oliot.epcis.query.EPCISException.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("query.epcis.oliot.org", "EPCISQueryBodyType");
            cachedSerQNames.add(qName);
            cls = org.oliot.epcis.query.EPCISQueryBodyType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("query.epcis.oliot.org", "EPCISQueryDocumentExtensionType");
            cachedSerQNames.add(qName);
            cls = org.oliot.epcis.query.EPCISQueryDocumentExtensionType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("query.epcis.oliot.org", "EPCISQueryDocumentType");
            cachedSerQNames.add(qName);
            cls = org.oliot.epcis.query.EPCISQueryDocumentType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("query.epcis.oliot.org", "GetSubscriptionIDs");
            cachedSerQNames.add(qName);
            cls = org.oliot.epcis.query.GetSubscriptionIDs.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("query.epcis.oliot.org", "ImplementationException");
            cachedSerQNames.add(qName);
            cls = org.oliot.epcis.query.ImplementationException.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("query.epcis.oliot.org", "ImplementationExceptionSeverity");
            cachedSerQNames.add(qName);
            cls = org.oliot.epcis.query.ImplementationExceptionSeverity.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(enumsf);
            cachedDeserFactories.add(enumdf);

            qName = new javax.xml.namespace.QName("query.epcis.oliot.org", "InvalidURIException");
            cachedSerQNames.add(qName);
            cls = org.oliot.epcis.query.InvalidURIException.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("query.epcis.oliot.org", "NoSuchNameException");
            cachedSerQNames.add(qName);
            cls = org.oliot.epcis.query.NoSuchNameException.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("query.epcis.oliot.org", "NoSuchSubscriptionException");
            cachedSerQNames.add(qName);
            cls = org.oliot.epcis.query.NoSuchSubscriptionException.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("query.epcis.oliot.org", "Poll");
            cachedSerQNames.add(qName);
            cls = org.oliot.epcis.query.Poll.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("query.epcis.oliot.org", "QueryParam");
            cachedSerQNames.add(qName);
            cls = org.oliot.epcis.query.QueryParam.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("query.epcis.oliot.org", "QueryParameterException");
            cachedSerQNames.add(qName);
            cls = org.oliot.epcis.query.QueryParameterException.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("query.epcis.oliot.org", "QueryParams");
            cachedSerQNames.add(qName);
            cls = org.oliot.epcis.query.QueryParam[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("query.epcis.oliot.org", "QueryParam");
            qName2 = new javax.xml.namespace.QName("", "param");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

    }
    @SuppressWarnings({ "rawtypes", "unused", "unchecked" })
	private void addBindings1() {
            java.lang.Class cls;
            javax.xml.namespace.QName qName;
            javax.xml.namespace.QName qName2;
            java.lang.Class beansf = org.apache.axis.encoding.ser.BeanSerializerFactory.class;
            java.lang.Class beandf = org.apache.axis.encoding.ser.BeanDeserializerFactory.class;
            java.lang.Class enumsf = org.apache.axis.encoding.ser.EnumSerializerFactory.class;
            java.lang.Class enumdf = org.apache.axis.encoding.ser.EnumDeserializerFactory.class;
            java.lang.Class arraysf = org.apache.axis.encoding.ser.ArraySerializerFactory.class;
            java.lang.Class arraydf = org.apache.axis.encoding.ser.ArrayDeserializerFactory.class;
            java.lang.Class simplesf = org.apache.axis.encoding.ser.SimpleSerializerFactory.class;
            java.lang.Class simpledf = org.apache.axis.encoding.ser.SimpleDeserializerFactory.class;
            java.lang.Class simplelistsf = org.apache.axis.encoding.ser.SimpleListSerializerFactory.class;
            java.lang.Class simplelistdf = org.apache.axis.encoding.ser.SimpleListDeserializerFactory.class;
            qName = new javax.xml.namespace.QName("query.epcis.oliot.org", "QueryResults");
            cachedSerQNames.add(qName);
            cls = org.oliot.epcis.query.QueryResults.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("query.epcis.oliot.org", "QueryResultsBody");
            cachedSerQNames.add(qName);
            cls = org.oliot.epcis.query.QueryResultsBody.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("query.epcis.oliot.org", "QueryResultsExtensionType");
            cachedSerQNames.add(qName);
            cls = org.oliot.epcis.query.QueryResultsExtensionType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("query.epcis.oliot.org", "QuerySchedule");
            cachedSerQNames.add(qName);
            cls = org.oliot.epcis.query.QuerySchedule.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("query.epcis.oliot.org", "QueryScheduleExtensionType");
            cachedSerQNames.add(qName);
            cls = org.oliot.epcis.query.QueryScheduleExtensionType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("query.epcis.oliot.org", "QueryTooComplexException");
            cachedSerQNames.add(qName);
            cls = org.oliot.epcis.query.QueryTooComplexException.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("query.epcis.oliot.org", "QueryTooLargeException");
            cachedSerQNames.add(qName);
            cls = org.oliot.epcis.query.QueryTooLargeException.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("query.epcis.oliot.org", "SecurityException");
            cachedSerQNames.add(qName);
            cls = org.oliot.epcis.query.SecurityException.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("query.epcis.oliot.org", "Subscribe");
            cachedSerQNames.add(qName);
            cls = org.oliot.epcis.query.Subscribe.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("query.epcis.oliot.org", "SubscribeNotPermittedException");
            cachedSerQNames.add(qName);
            cls = org.oliot.epcis.query.SubscribeNotPermittedException.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("query.epcis.oliot.org", "SubscriptionControls");
            cachedSerQNames.add(qName);
            cls = org.oliot.epcis.query.SubscriptionControls.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("query.epcis.oliot.org", "SubscriptionControlsException");
            cachedSerQNames.add(qName);
            cls = org.oliot.epcis.query.SubscriptionControlsException.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("query.epcis.oliot.org", "SubscriptionControlsExtensionType");
            cachedSerQNames.add(qName);
            cls = org.oliot.epcis.query.SubscriptionControlsExtensionType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("query.epcis.oliot.org", "Unsubscribe");
            cachedSerQNames.add(qName);
            cls = org.oliot.epcis.query.Unsubscribe.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("query.epcis.oliot.org", "ValidationException");
            cachedSerQNames.add(qName);
            cls = org.oliot.epcis.query.ValidationException.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("query.epcis.oliot.org", "VoidHolder");
            cachedSerQNames.add(qName);
            cls = org.oliot.epcis.query.VoidHolder.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

    }

    @SuppressWarnings("rawtypes")
	protected org.apache.axis.client.Call createCall() throws java.rmi.RemoteException {
        try {
            org.apache.axis.client.Call _call = super._createCall();
            if (super.maintainSessionSet) {
                _call.setMaintainSession(super.maintainSession);
            }
            if (super.cachedUsername != null) {
                _call.setUsername(super.cachedUsername);
            }
            if (super.cachedPassword != null) {
                _call.setPassword(super.cachedPassword);
            }
            if (super.cachedEndpoint != null) {
                _call.setTargetEndpointAddress(super.cachedEndpoint);
            }
            if (super.cachedTimeout != null) {
                _call.setTimeout(super.cachedTimeout);
            }
            if (super.cachedPortName != null) {
                _call.setPortName(super.cachedPortName);
            }
            java.util.Enumeration keys = super.cachedProperties.keys();
            while (keys.hasMoreElements()) {
                java.lang.String key = (java.lang.String) keys.nextElement();
                _call.setProperty(key, super.cachedProperties.get(key));
            }
            // All the type mapping information is registered
            // when the first call is made.
            // The type mapping information is actually registered in
            // the TypeMappingRegistry of the service, which
            // is the reason why registration is only needed for the first call.
            synchronized (this) {
                if (firstCall()) {
                    // must set encoding style before registering serializers
                    _call.setEncodingStyle(null);
                    for (int i = 0; i < cachedSerFactories.size(); ++i) {
                        java.lang.Class cls = (java.lang.Class) cachedSerClasses.get(i);
                        javax.xml.namespace.QName qName =
                                (javax.xml.namespace.QName) cachedSerQNames.get(i);
                        java.lang.Object x = cachedSerFactories.get(i);
                        if (x instanceof Class) {
                            java.lang.Class sf = (java.lang.Class)
                                 cachedSerFactories.get(i);
                            java.lang.Class df = (java.lang.Class)
                                 cachedDeserFactories.get(i);
                            _call.registerTypeMapping(cls, qName, sf, df, false);
                        }
                        else if (x instanceof javax.xml.rpc.encoding.SerializerFactory) {
                            org.apache.axis.encoding.SerializerFactory sf = (org.apache.axis.encoding.SerializerFactory)
                                 cachedSerFactories.get(i);
                            org.apache.axis.encoding.DeserializerFactory df = (org.apache.axis.encoding.DeserializerFactory)
                                 cachedDeserFactories.get(i);
                            _call.registerTypeMapping(cls, qName, sf, df, false);
                        }
                    }
                }
            }
            return _call;
        }
        catch (java.lang.Throwable _t) {
            throw new org.apache.axis.AxisFault("Failure trying to get the Call object", _t);
        }
    }

    public java.lang.String[] getQueryNames(org.oliot.epcis.query.EmptyParms parms) throws java.rmi.RemoteException, org.oliot.epcis.query.ImplementationException, org.oliot.epcis.query.SecurityException, org.oliot.epcis.query.ValidationException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[0]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "getQueryNames"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {parms});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (java.lang.String[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (java.lang.String[]) org.apache.axis.utils.JavaUtils.convert(_resp, java.lang.String[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
    if (axisFaultException.detail != null) {
        if (axisFaultException.detail instanceof java.rmi.RemoteException) {
              throw (java.rmi.RemoteException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.oliot.epcis.query.ImplementationException) {
              throw (org.oliot.epcis.query.ImplementationException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.oliot.epcis.query.SecurityException) {
              throw (org.oliot.epcis.query.SecurityException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.oliot.epcis.query.ValidationException) {
              throw (org.oliot.epcis.query.ValidationException) axisFaultException.detail;
         }
   }
  throw axisFaultException;
}
    }

    public org.oliot.epcis.query.VoidHolder subscribe(org.oliot.epcis.query.Subscribe parms) throws java.rmi.RemoteException, org.oliot.epcis.query.QueryTooComplexException, org.oliot.epcis.query.SubscribeNotPermittedException, org.oliot.epcis.query.QueryParameterException, org.oliot.epcis.query.NoSuchNameException, org.oliot.epcis.query.DuplicateSubscriptionException, org.oliot.epcis.query.InvalidURIException, org.oliot.epcis.query.ImplementationException, org.oliot.epcis.query.SecurityException, org.oliot.epcis.query.SubscriptionControlsException, org.oliot.epcis.query.ValidationException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[1]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "subscribe"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {parms});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.oliot.epcis.query.VoidHolder) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.oliot.epcis.query.VoidHolder) org.apache.axis.utils.JavaUtils.convert(_resp, org.oliot.epcis.query.VoidHolder.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
    if (axisFaultException.detail != null) {
        if (axisFaultException.detail instanceof java.rmi.RemoteException) {
              throw (java.rmi.RemoteException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.oliot.epcis.query.QueryTooComplexException) {
              throw (org.oliot.epcis.query.QueryTooComplexException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.oliot.epcis.query.SubscribeNotPermittedException) {
              throw (org.oliot.epcis.query.SubscribeNotPermittedException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.oliot.epcis.query.QueryParameterException) {
              throw (org.oliot.epcis.query.QueryParameterException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.oliot.epcis.query.NoSuchNameException) {
              throw (org.oliot.epcis.query.NoSuchNameException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.oliot.epcis.query.DuplicateSubscriptionException) {
              throw (org.oliot.epcis.query.DuplicateSubscriptionException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.oliot.epcis.query.InvalidURIException) {
              throw (org.oliot.epcis.query.InvalidURIException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.oliot.epcis.query.ImplementationException) {
              throw (org.oliot.epcis.query.ImplementationException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.oliot.epcis.query.SecurityException) {
              throw (org.oliot.epcis.query.SecurityException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.oliot.epcis.query.SubscriptionControlsException) {
              throw (org.oliot.epcis.query.SubscriptionControlsException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.oliot.epcis.query.ValidationException) {
              throw (org.oliot.epcis.query.ValidationException) axisFaultException.detail;
         }
   }
  throw axisFaultException;
}
    }

    public org.oliot.epcis.query.VoidHolder unsubscribe(org.oliot.epcis.query.Unsubscribe parms) throws java.rmi.RemoteException, org.oliot.epcis.query.NoSuchSubscriptionException, org.oliot.epcis.query.ImplementationException, org.oliot.epcis.query.SecurityException, org.oliot.epcis.query.ValidationException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[2]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "unsubscribe"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {parms});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.oliot.epcis.query.VoidHolder) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.oliot.epcis.query.VoidHolder) org.apache.axis.utils.JavaUtils.convert(_resp, org.oliot.epcis.query.VoidHolder.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
    if (axisFaultException.detail != null) {
        if (axisFaultException.detail instanceof java.rmi.RemoteException) {
              throw (java.rmi.RemoteException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.oliot.epcis.query.NoSuchSubscriptionException) {
              throw (org.oliot.epcis.query.NoSuchSubscriptionException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.oliot.epcis.query.ImplementationException) {
              throw (org.oliot.epcis.query.ImplementationException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.oliot.epcis.query.SecurityException) {
              throw (org.oliot.epcis.query.SecurityException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.oliot.epcis.query.ValidationException) {
              throw (org.oliot.epcis.query.ValidationException) axisFaultException.detail;
         }
   }
  throw axisFaultException;
}
    }

    public java.lang.String[] getSubscriptionIDs(org.oliot.epcis.query.GetSubscriptionIDs parms) throws java.rmi.RemoteException, org.oliot.epcis.query.NoSuchNameException, org.oliot.epcis.query.ImplementationException, org.oliot.epcis.query.SecurityException, org.oliot.epcis.query.ValidationException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[3]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "getSubscriptionIDs"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {parms});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (java.lang.String[]) _resp;
            } catch (java.lang.Exception _exception) {
                return (java.lang.String[]) org.apache.axis.utils.JavaUtils.convert(_resp, java.lang.String[].class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
    if (axisFaultException.detail != null) {
        if (axisFaultException.detail instanceof java.rmi.RemoteException) {
              throw (java.rmi.RemoteException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.oliot.epcis.query.NoSuchNameException) {
              throw (org.oliot.epcis.query.NoSuchNameException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.oliot.epcis.query.ImplementationException) {
              throw (org.oliot.epcis.query.ImplementationException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.oliot.epcis.query.SecurityException) {
              throw (org.oliot.epcis.query.SecurityException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.oliot.epcis.query.ValidationException) {
              throw (org.oliot.epcis.query.ValidationException) axisFaultException.detail;
         }
   }
  throw axisFaultException;
}
    }

    public org.oliot.epcis.query.QueryResults poll(org.oliot.epcis.query.Poll parms) throws java.rmi.RemoteException, org.oliot.epcis.query.QueryTooComplexException, org.oliot.epcis.query.QueryTooLargeException, org.oliot.epcis.query.NoSuchNameException, org.oliot.epcis.query.QueryParameterException, org.oliot.epcis.query.ImplementationException, org.oliot.epcis.query.SecurityException, org.oliot.epcis.query.ValidationException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[4]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "poll"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {parms});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (org.oliot.epcis.query.QueryResults) _resp;
            } catch (java.lang.Exception _exception) {
                return (org.oliot.epcis.query.QueryResults) org.apache.axis.utils.JavaUtils.convert(_resp, org.oliot.epcis.query.QueryResults.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
    if (axisFaultException.detail != null) {
        if (axisFaultException.detail instanceof java.rmi.RemoteException) {
              throw (java.rmi.RemoteException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.oliot.epcis.query.QueryTooComplexException) {
              throw (org.oliot.epcis.query.QueryTooComplexException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.oliot.epcis.query.QueryTooLargeException) {
              throw (org.oliot.epcis.query.QueryTooLargeException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.oliot.epcis.query.NoSuchNameException) {
              throw (org.oliot.epcis.query.NoSuchNameException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.oliot.epcis.query.QueryParameterException) {
              throw (org.oliot.epcis.query.QueryParameterException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.oliot.epcis.query.ImplementationException) {
              throw (org.oliot.epcis.query.ImplementationException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.oliot.epcis.query.SecurityException) {
              throw (org.oliot.epcis.query.SecurityException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.oliot.epcis.query.ValidationException) {
              throw (org.oliot.epcis.query.ValidationException) axisFaultException.detail;
         }
   }
  throw axisFaultException;
}
    }

    public java.lang.String getStandardVersion(org.oliot.epcis.query.EmptyParms parms) throws java.rmi.RemoteException, org.oliot.epcis.query.ImplementationException, org.oliot.epcis.query.SecurityException, org.oliot.epcis.query.ValidationException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[5]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "getStandardVersion"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {parms});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (java.lang.String) _resp;
            } catch (java.lang.Exception _exception) {
                return (java.lang.String) org.apache.axis.utils.JavaUtils.convert(_resp, java.lang.String.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
    if (axisFaultException.detail != null) {
        if (axisFaultException.detail instanceof java.rmi.RemoteException) {
              throw (java.rmi.RemoteException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.oliot.epcis.query.ImplementationException) {
              throw (org.oliot.epcis.query.ImplementationException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.oliot.epcis.query.SecurityException) {
              throw (org.oliot.epcis.query.SecurityException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.oliot.epcis.query.ValidationException) {
              throw (org.oliot.epcis.query.ValidationException) axisFaultException.detail;
         }
   }
  throw axisFaultException;
}
    }

    public java.lang.String getVendorVersion(org.oliot.epcis.query.EmptyParms parms) throws java.rmi.RemoteException, org.oliot.epcis.query.ImplementationException, org.oliot.epcis.query.SecurityException, org.oliot.epcis.query.ValidationException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[6]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "getVendorVersion"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {parms});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (java.lang.String) _resp;
            } catch (java.lang.Exception _exception) {
                return (java.lang.String) org.apache.axis.utils.JavaUtils.convert(_resp, java.lang.String.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
    if (axisFaultException.detail != null) {
        if (axisFaultException.detail instanceof java.rmi.RemoteException) {
              throw (java.rmi.RemoteException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.oliot.epcis.query.ImplementationException) {
              throw (org.oliot.epcis.query.ImplementationException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.oliot.epcis.query.SecurityException) {
              throw (org.oliot.epcis.query.SecurityException) axisFaultException.detail;
         }
        if (axisFaultException.detail instanceof org.oliot.epcis.query.ValidationException) {
              throw (org.oliot.epcis.query.ValidationException) axisFaultException.detail;
         }
   }
  throw axisFaultException;
}
    }

}
