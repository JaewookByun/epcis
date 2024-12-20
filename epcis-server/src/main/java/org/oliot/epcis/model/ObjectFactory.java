//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2024.12.19 at 01:15:43 PM KST 
//


package org.oliot.epcis.model;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlElementDecl;
import jakarta.xml.bind.annotation.XmlRegistry;

import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.oliot.epcis.model package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _EPCISCaptureJobList_QNAME = new QName("urn:epcglobal:epcis:xsd:2", "EPCISCaptureJobList");
    private final static QName _EPCISDocument_QNAME = new QName("urn:epcglobal:epcis:xsd:2", "EPCISDocument");
    private final static QName _EPCISCaptureJob_QNAME = new QName("urn:epcglobal:epcis:xsd:2", "EPCISCaptureJob");
    private final static QName _EPCISException_QNAME = new QName("urn:epcglobal:epcis:xsd:2", "EPCISException");
    private final static QName _CorrelationInformation_QNAME = new QName("http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", "CorrelationInformation");
    private final static QName _BusinessService_QNAME = new QName("http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", "BusinessService");
    private final static QName _ScopeInformation_QNAME = new QName("http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", "ScopeInformation");
    private final static QName _StandardBusinessDocumentHeader_QNAME = new QName("http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", "StandardBusinessDocumentHeader");
    private final static QName _StandardBusinessDocument_QNAME = new QName("http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", "StandardBusinessDocument");
    private final static QName _ErrorDeclarationTypeReason_QNAME = new QName("", "reason");
    private final static QName _ErrorDeclarationTypeExtension_QNAME = new QName("", "extension");
    private final static QName _ErrorDeclarationTypeDeclarationTime_QNAME = new QName("", "declarationTime");
    private final static QName _ErrorDeclarationTypeCorrectiveEventIDs_QNAME = new QName("", "correctiveEventIDs");
    private final static QName _QuantityElementTypeQuantity_QNAME = new QName("", "quantity");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.oliot.epcis.model
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link EPCISCaptureJobListType }
     * 
     */
    public EPCISCaptureJobListType createEPCISCaptureJobListType() {
        return new EPCISCaptureJobListType();
    }

    /**
     * Create an instance of {@link EPCISDocumentType }
     * 
     */
    public EPCISDocumentType createEPCISDocumentType() {
        return new EPCISDocumentType();
    }

    /**
     * Create an instance of {@link EPCISCaptureJobType }
     * 
     */
    public EPCISCaptureJobType createEPCISCaptureJobType() {
        return new EPCISCaptureJobType();
    }

    /**
     * Create an instance of {@link RFC7807ProblemResponseBodyType }
     * 
     */
    public RFC7807ProblemResponseBodyType createRFC7807ProblemResponseBodyType() {
        return new RFC7807ProblemResponseBodyType();
    }

    /**
     * Create an instance of {@link EPCListType }
     * 
     */
    public EPCListType createEPCListType() {
        return new EPCListType();
    }

    /**
     * Create an instance of {@link QuantityListType }
     * 
     */
    public QuantityListType createQuantityListType() {
        return new QuantityListType();
    }

    /**
     * Create an instance of {@link EPCISMasterDataExtensionType }
     * 
     */
    public EPCISMasterDataExtensionType createEPCISMasterDataExtensionType() {
        return new EPCISMasterDataExtensionType();
    }

    /**
     * Create an instance of {@link SensorElementListExtensionType }
     * 
     */
    public SensorElementListExtensionType createSensorElementListExtensionType() {
        return new SensorElementListExtensionType();
    }

    /**
     * Create an instance of {@link EPCISHeaderExtension2Type }
     * 
     */
    public EPCISHeaderExtension2Type createEPCISHeaderExtension2Type() {
        return new EPCISHeaderExtension2Type();
    }

    /**
     * Create an instance of {@link EPCISCaptureDocumentErrorListType }
     * 
     */
    public EPCISCaptureDocumentErrorListType createEPCISCaptureDocumentErrorListType() {
        return new EPCISCaptureDocumentErrorListType();
    }

    /**
     * Create an instance of {@link AssociationEventExtensionType }
     * 
     */
    public AssociationEventExtensionType createAssociationEventExtensionType() {
        return new AssociationEventExtensionType();
    }

    /**
     * Create an instance of {@link ReadPointExtensionType }
     * 
     */
    public ReadPointExtensionType createReadPointExtensionType() {
        return new ReadPointExtensionType();
    }

    /**
     * Create an instance of {@link ILMDType }
     * 
     */
    public ILMDType createILMDType() {
        return new ILMDType();
    }

    /**
     * Create an instance of {@link PersistentDispositionType }
     * 
     */
    public PersistentDispositionType createPersistentDispositionType() {
        return new PersistentDispositionType();
    }

    /**
     * Create an instance of {@link SensorMetadataType }
     * 
     */
    public SensorMetadataType createSensorMetadataType() {
        return new SensorMetadataType();
    }

    /**
     * Create an instance of {@link AssociationEventType }
     * 
     */
    public AssociationEventType createAssociationEventType() {
        return new AssociationEventType();
    }

    /**
     * Create an instance of {@link EPCISHeaderType }
     * 
     */
    public EPCISHeaderType createEPCISHeaderType() {
        return new EPCISHeaderType();
    }

    /**
     * Create an instance of {@link SensorElementType }
     * 
     */
    public SensorElementType createSensorElementType() {
        return new SensorElementType();
    }

    /**
     * Create an instance of {@link AttributeType }
     * 
     */
    public AttributeType createAttributeType() {
        return new AttributeType();
    }

    /**
     * Create an instance of {@link TransformationEventType }
     * 
     */
    public TransformationEventType createTransformationEventType() {
        return new TransformationEventType();
    }

    /**
     * Create an instance of {@link VocabularyElementType }
     * 
     */
    public VocabularyElementType createVocabularyElementType() {
        return new VocabularyElementType();
    }

    /**
     * Create an instance of {@link EPCISBodyType }
     * 
     */
    public EPCISBodyType createEPCISBodyType() {
        return new EPCISBodyType();
    }

    /**
     * Create an instance of {@link ErrorDeclarationType }
     * 
     */
    public ErrorDeclarationType createErrorDeclarationType() {
        return new ErrorDeclarationType();
    }

    /**
     * Create an instance of {@link EventListType }
     * 
     */
    public EventListType createEventListType() {
        return new EventListType();
    }

    /**
     * Create an instance of {@link IDListType }
     * 
     */
    public IDListType createIDListType() {
        return new IDListType();
    }

    /**
     * Create an instance of {@link BusinessTransactionListType }
     * 
     */
    public BusinessTransactionListType createBusinessTransactionListType() {
        return new BusinessTransactionListType();
    }

    /**
     * Create an instance of {@link TransactionEventType }
     * 
     */
    public TransactionEventType createTransactionEventType() {
        return new TransactionEventType();
    }

    /**
     * Create an instance of {@link EPCISMasterDataType }
     * 
     */
    public EPCISMasterDataType createEPCISMasterDataType() {
        return new EPCISMasterDataType();
    }

    /**
     * Create an instance of {@link EPCISEventListExtensionType }
     * 
     */
    public EPCISEventListExtensionType createEPCISEventListExtensionType() {
        return new EPCISEventListExtensionType();
    }

    /**
     * Create an instance of {@link CertificationList }
     * 
     */
    public CertificationList createCertificationList() {
        return new CertificationList();
    }

    /**
     * Create an instance of {@link TransactionEventExtensionType }
     * 
     */
    public TransactionEventExtensionType createTransactionEventExtensionType() {
        return new TransactionEventExtensionType();
    }

    /**
     * Create an instance of {@link VocabularyListType }
     * 
     */
    public VocabularyListType createVocabularyListType() {
        return new VocabularyListType();
    }

    /**
     * Create an instance of {@link ObjectEventType }
     * 
     */
    public ObjectEventType createObjectEventType() {
        return new ObjectEventType();
    }

    /**
     * Create an instance of {@link TransformationEventExtensionType }
     * 
     */
    public TransformationEventExtensionType createTransformationEventExtensionType() {
        return new TransformationEventExtensionType();
    }

    /**
     * Create an instance of {@link EPCISEventExtensionType }
     * 
     */
    public EPCISEventExtensionType createEPCISEventExtensionType() {
        return new EPCISEventExtensionType();
    }

    /**
     * Create an instance of {@link AggregationEventExtensionType }
     * 
     */
    public AggregationEventExtensionType createAggregationEventExtensionType() {
        return new AggregationEventExtensionType();
    }

    /**
     * Create an instance of {@link SourceDestType }
     * 
     */
    public SourceDestType createSourceDestType() {
        return new SourceDestType();
    }

    /**
     * Create an instance of {@link VocabularyType }
     * 
     */
    public VocabularyType createVocabularyType() {
        return new VocabularyType();
    }

    /**
     * Create an instance of {@link QuantityElementType }
     * 
     */
    public QuantityElementType createQuantityElementType() {
        return new QuantityElementType();
    }

    /**
     * Create an instance of {@link DestinationListType }
     * 
     */
    public DestinationListType createDestinationListType() {
        return new DestinationListType();
    }

    /**
     * Create an instance of {@link VocabularyElementListType }
     * 
     */
    public VocabularyElementListType createVocabularyElementListType() {
        return new VocabularyElementListType();
    }

    /**
     * Create an instance of {@link BusinessTransactionType }
     * 
     */
    public BusinessTransactionType createBusinessTransactionType() {
        return new BusinessTransactionType();
    }

    /**
     * Create an instance of {@link ILMDExtensionType }
     * 
     */
    public ILMDExtensionType createILMDExtensionType() {
        return new ILMDExtensionType();
    }

    /**
     * Create an instance of {@link AggregationEventType }
     * 
     */
    public AggregationEventType createAggregationEventType() {
        return new AggregationEventType();
    }

    /**
     * Create an instance of {@link ReadPointType }
     * 
     */
    public ReadPointType createReadPointType() {
        return new ReadPointType();
    }

    /**
     * Create an instance of {@link CorrectiveEventIDsType }
     * 
     */
    public CorrectiveEventIDsType createCorrectiveEventIDsType() {
        return new CorrectiveEventIDsType();
    }

    /**
     * Create an instance of {@link BusinessLocationType }
     * 
     */
    public BusinessLocationType createBusinessLocationType() {
        return new BusinessLocationType();
    }

    /**
     * Create an instance of {@link BusinessLocationExtensionType }
     * 
     */
    public BusinessLocationExtensionType createBusinessLocationExtensionType() {
        return new BusinessLocationExtensionType();
    }

    /**
     * Create an instance of {@link EPCISDocumentExtensionType }
     * 
     */
    public EPCISDocumentExtensionType createEPCISDocumentExtensionType() {
        return new EPCISDocumentExtensionType();
    }

    /**
     * Create an instance of {@link SensorReportType }
     * 
     */
    public SensorReportType createSensorReportType() {
        return new SensorReportType();
    }

    /**
     * Create an instance of {@link EPCISBodyExtensionType }
     * 
     */
    public EPCISBodyExtensionType createEPCISBodyExtensionType() {
        return new EPCISBodyExtensionType();
    }

    /**
     * Create an instance of {@link SensorElementExtensionType }
     * 
     */
    public SensorElementExtensionType createSensorElementExtensionType() {
        return new SensorElementExtensionType();
    }

    /**
     * Create an instance of {@link VocabularyElementExtensionType }
     * 
     */
    public VocabularyElementExtensionType createVocabularyElementExtensionType() {
        return new VocabularyElementExtensionType();
    }

    /**
     * Create an instance of {@link ObjectEventExtensionType }
     * 
     */
    public ObjectEventExtensionType createObjectEventExtensionType() {
        return new ObjectEventExtensionType();
    }

    /**
     * Create an instance of {@link SourceListType }
     * 
     */
    public SourceListType createSourceListType() {
        return new SourceListType();
    }

    /**
     * Create an instance of {@link SensorElementListType }
     * 
     */
    public SensorElementListType createSensorElementListType() {
        return new SensorElementListType();
    }

    /**
     * Create an instance of {@link ErrorDeclarationExtensionType }
     * 
     */
    public ErrorDeclarationExtensionType createErrorDeclarationExtensionType() {
        return new ErrorDeclarationExtensionType();
    }

    /**
     * Create an instance of {@link VocabularyExtensionType }
     * 
     */
    public VocabularyExtensionType createVocabularyExtensionType() {
        return new VocabularyExtensionType();
    }

    /**
     * Create an instance of {@link EPCISHeaderExtensionType }
     * 
     */
    public EPCISHeaderExtensionType createEPCISHeaderExtensionType() {
        return new EPCISHeaderExtensionType();
    }

    /**
     * Create an instance of {@link EPC }
     * 
     */
    public EPC createEPC() {
        return new EPC();
    }

    /**
     * Create an instance of {@link BusinessService }
     * 
     */
    public BusinessService createBusinessService() {
        return new BusinessService();
    }

    /**
     * Create an instance of {@link CorrelationInformation }
     * 
     */
    public CorrelationInformation createCorrelationInformation() {
        return new CorrelationInformation();
    }

    /**
     * Create an instance of {@link StandardBusinessDocument }
     * 
     */
    public StandardBusinessDocument createStandardBusinessDocument() {
        return new StandardBusinessDocument();
    }

    /**
     * Create an instance of {@link StandardBusinessDocumentHeader }
     * 
     */
    public StandardBusinessDocumentHeader createStandardBusinessDocumentHeader() {
        return new StandardBusinessDocumentHeader();
    }

    /**
     * Create an instance of {@link ManifestItem }
     * 
     */
    public ManifestItem createManifestItem() {
        return new ManifestItem();
    }

    /**
     * Create an instance of {@link DocumentIdentification }
     * 
     */
    public DocumentIdentification createDocumentIdentification() {
        return new DocumentIdentification();
    }

    /**
     * Create an instance of {@link ServiceTransaction }
     * 
     */
    public ServiceTransaction createServiceTransaction() {
        return new ServiceTransaction();
    }

    /**
     * Create an instance of {@link BusinessScope }
     * 
     */
    public BusinessScope createBusinessScope() {
        return new BusinessScope();
    }

    /**
     * Create an instance of {@link PartnerIdentification }
     * 
     */
    public PartnerIdentification createPartnerIdentification() {
        return new PartnerIdentification();
    }

    /**
     * Create an instance of {@link Manifest }
     * 
     */
    public Manifest createManifest() {
        return new Manifest();
    }

    /**
     * Create an instance of {@link ContactInformation }
     * 
     */
    public ContactInformation createContactInformation() {
        return new ContactInformation();
    }

    /**
     * Create an instance of {@link Scope }
     * 
     */
    public Scope createScope() {
        return new Scope();
    }

    /**
     * Create an instance of {@link Partner }
     * 
     */
    public Partner createPartner() {
        return new Partner();
    }

    /**
     * Create an instance of {@link Problem }
     * 
     */
    public Problem createProblem() {
        return new Problem();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EPCISCaptureJobListType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:epcglobal:epcis:xsd:2", name = "EPCISCaptureJobList")
    public JAXBElement<EPCISCaptureJobListType> createEPCISCaptureJobList(EPCISCaptureJobListType value) {
        return new JAXBElement<EPCISCaptureJobListType>(_EPCISCaptureJobList_QNAME, EPCISCaptureJobListType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EPCISDocumentType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:epcglobal:epcis:xsd:2", name = "EPCISDocument")
    public JAXBElement<EPCISDocumentType> createEPCISDocument(EPCISDocumentType value) {
        return new JAXBElement<EPCISDocumentType>(_EPCISDocument_QNAME, EPCISDocumentType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link EPCISCaptureJobType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:epcglobal:epcis:xsd:2", name = "EPCISCaptureJob")
    public JAXBElement<EPCISCaptureJobType> createEPCISCaptureJob(EPCISCaptureJobType value) {
        return new JAXBElement<EPCISCaptureJobType>(_EPCISCaptureJob_QNAME, EPCISCaptureJobType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RFC7807ProblemResponseBodyType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:epcglobal:epcis:xsd:2", name = "EPCISException")
    public JAXBElement<RFC7807ProblemResponseBodyType> createEPCISException(RFC7807ProblemResponseBodyType value) {
        return new JAXBElement<RFC7807ProblemResponseBodyType>(_EPCISException_QNAME, RFC7807ProblemResponseBodyType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CorrelationInformation }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", name = "CorrelationInformation", substitutionHeadNamespace = "http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", substitutionHeadName = "ScopeInformation")
    public JAXBElement<CorrelationInformation> createCorrelationInformation(CorrelationInformation value) {
        return new JAXBElement<CorrelationInformation>(_CorrelationInformation_QNAME, CorrelationInformation.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BusinessService }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", name = "BusinessService", substitutionHeadNamespace = "http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", substitutionHeadName = "ScopeInformation")
    public JAXBElement<BusinessService> createBusinessService(BusinessService value) {
        return new JAXBElement<BusinessService>(_BusinessService_QNAME, BusinessService.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Object }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", name = "ScopeInformation")
    public JAXBElement<Object> createScopeInformation(Object value) {
        return new JAXBElement<Object>(_ScopeInformation_QNAME, Object.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link StandardBusinessDocumentHeader }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", name = "StandardBusinessDocumentHeader")
    public JAXBElement<StandardBusinessDocumentHeader> createStandardBusinessDocumentHeader(StandardBusinessDocumentHeader value) {
        return new JAXBElement<StandardBusinessDocumentHeader>(_StandardBusinessDocumentHeader_QNAME, StandardBusinessDocumentHeader.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link StandardBusinessDocument }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader", name = "StandardBusinessDocument")
    public JAXBElement<StandardBusinessDocument> createStandardBusinessDocument(StandardBusinessDocument value) {
        return new JAXBElement<StandardBusinessDocument>(_StandardBusinessDocument_QNAME, StandardBusinessDocument.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "reason", scope = ErrorDeclarationType.class)
    public JAXBElement<String> createErrorDeclarationTypeReason(String value) {
        return new JAXBElement<String>(_ErrorDeclarationTypeReason_QNAME, String.class, ErrorDeclarationType.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ErrorDeclarationExtensionType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "extension", scope = ErrorDeclarationType.class)
    public JAXBElement<ErrorDeclarationExtensionType> createErrorDeclarationTypeExtension(ErrorDeclarationExtensionType value) {
        return new JAXBElement<ErrorDeclarationExtensionType>(_ErrorDeclarationTypeExtension_QNAME, ErrorDeclarationExtensionType.class, ErrorDeclarationType.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "declarationTime", scope = ErrorDeclarationType.class)
    public JAXBElement<XMLGregorianCalendar> createErrorDeclarationTypeDeclarationTime(XMLGregorianCalendar value) {
        return new JAXBElement<XMLGregorianCalendar>(_ErrorDeclarationTypeDeclarationTime_QNAME, XMLGregorianCalendar.class, ErrorDeclarationType.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link CorrectiveEventIDsType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "correctiveEventIDs", scope = ErrorDeclarationType.class)
    public JAXBElement<CorrectiveEventIDsType> createErrorDeclarationTypeCorrectiveEventIDs(CorrectiveEventIDsType value) {
        return new JAXBElement<CorrectiveEventIDsType>(_ErrorDeclarationTypeCorrectiveEventIDs_QNAME, CorrectiveEventIDsType.class, ErrorDeclarationType.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Double }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "quantity", scope = QuantityElementType.class)
    public JAXBElement<Double> createQuantityElementTypeQuantity(Double value) {
        return new JAXBElement<Double>(_QuantityElementTypeQuantity_QNAME, Double.class, QuantityElementType.class, value);
    }

}
