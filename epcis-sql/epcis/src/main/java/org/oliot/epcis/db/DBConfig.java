package org.oliot.epcis.db;

public class DBConfig {
	
	public static String database="mysql";
	public static String url="localhost";
	public static String databaseName="epcis";
	public static String username="root";
	public static String password ="root";
	
	public static String getMysqlConfigXml(){
		String xml=	"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
		"<!-- dispatcher-servlet.xml -->\n"+
		"<beans xmlns=\"http://www.springframework.org/schema/beans\"\n"+
			"xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"+
			"xmlns:context=\"http://www.springframework.org/schema/context\"\n"+
			"xsi:schemaLocation=\"http://www.springframework.org/schema/beans\n"+
		        "http://www.springframework.org/schema/beans/spring-beans.xsd\n"+
		        "http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context.xsd\">\n"+     
		"<context:annotation-config/>\n"+
		" <context:component-scan base-package=\"org.oliot.epcis.serde.sql\"/>\n"+
		 " <bean id=\"queryOprationBackend\" class=\"org.oliot.epcis.service.query.sql.QueryOprationBackend\"/>\n"+
		 
		  "<bean id=\"dataSource\" class=\"org.springframework.jdbc.datasource.DriverManagerDataSource\">\n"+
		   " <property name=\"driverClassName\" value=\"com.mysql.jdbc.Driver\"/>\n"+
		    "<property name=\"url\" value=\"jdbc:mysql://"+url+"/"+databaseName+"\"/>\n"+
		    "<property name=\"username\" value=\""+username+"\"/>\n"+
		    "<property name=\"password\" value=\""+password+"\"/>\n"+    
		 "</bean>\n"+
		 
		 "<bean id=\"sessionFactory\" class=\"org.springframework.orm.hibernate3.annotation.AnnotationSessionFactoryBean\">\n"+
		    "<property name=\"dataSource\" ref=\"dataSource\" />\n"+
		    "<property name=\"packagesToScan\" value=\"org.oliot.model.oliot\"/>\n"+
		   " <property name=\"hibernateProperties\">\n"+
		   " <props>"+
		     " <prop key=\"dialect\">org.hibernate.dialect.MySQLDialect</prop>\n"+
		      " <prop key=\"hibernate.hbm2ddl.auto\">update</prop>\n"+
		       "<!--create-drop   update-->\n"+
		        "<prop key=\"hibernate.show_sql\">true</prop>\n"+
		        "<prop key=\"hibernate.format_sql\">true</prop>\n"+
		        "<prop key=\"use_sql_comments\">true</prop>\n"+
		    "</props>\n"+
		   " </property>\n"+
		       " <!--   <property name=\"hibernate.hbm2ddl.auto\" value=\"update\"/>\n"+
		       " <property name=\"connection.autocommit\">true</property>"+
		       " <property name=\"hibernate.show_sql\" value=\"true\"/>\n"+
		        "<property name=\"hibernate.format_sql\" value=\"false\"/>-->\n"+
		 "</bean>\n"+
		"</beans>";
		
		return xml;
	}
	
	public static String getPostgresqlXml(){
		String xml="<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
				"<!-- dispatcher-servlet.xml -->\n"+
				"<beans xmlns=\"http://www.springframework.org/schema/beans\"\n"+
					"xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"+
					"xmlns:context=\"http://www.springframework.org/schema/context\"\n"+
					"xsi:schemaLocation=\"http://www.springframework.org/schema/beans\n"+
				        "http://www.springframework.org/schema/beans/spring-beans.xsd\n"+
				        "http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context.xsd\">\n"+     
				"<context:annotation-config/>\n"+
				" <context:component-scan base-package=\"org.oliot.epcis.serde.sql\"/>\n"+
				 " <bean id=\"queryOprationBackend\" class=\"org.oliot.epcis.service.query.sql.QueryOprationBackend\"/>\n"+
 

 
 "<bean id=\"dataSource\" class=\"org.springframework.jdbc.datasource.DriverManagerDataSource\">\n"+
        "<property name=\"driverClassName\" value=\"org.postgresql.Driver\" />\n"+
        "<property name=\"url\" value=\"jdbc:postgresql://"+url+":5432/"+databaseName+"\" />\n"+
        "<property name=\"username\" value=\""+username+"\" />\n"+
        "<property name=\"password\" value=\""+password+"\" />\n"+

        "<property name=\"connectionProperties\">\n"+
            "<props>\n"+
                "<prop key=\"socketTimeout\">10</prop>\n"+
            "</props>\n"+
        "</property>\n"+
   "</bean>\n"+
 
"<bean id=\"sessionFactory\" class=\"org.springframework.orm.hibernate3.annotation.AnnotationSessionFactoryBean\">\n"+
    "<property name=\"dataSource\" ref=\"dataSource\" />\n"+
    "<property name=\"packagesToScan\" value=\"org.oliot.model.oliot\"/>\n"+
    "<property name=\"hibernateProperties\">\n"+
    "<props>\n"+
      "<prop key=\"dialect\">org.hibernate.dialect.PostgreSQLDialect</prop>\n"+
				"<prop key=\"hibernate.hbm2ddl.auto\">update</prop>\n"+ 
      	"<prop key=\"hibernate.show_sql\">true</prop>\n"+ 
        "<prop key=\"hibernate.format_sql\">true</prop>\n"+ 
        "<prop key=\"use_sql_comments\">true</prop>\n"+ 
    "</props>\n"+
    "</property>\n"+

 "</bean>\n"+

"</beans>";
		
		return xml;
	}
	

	
	public static String getOracleDBConfig(){
		String oracleDBConfig="";
		oracleDBConfig="<?xml version=\"1.0\" encoding=\"utf-8\"?>\n"+
"<!DOCTYPE hibernate-configuration PUBLIC\n"+
"\"-//Hibernate/Hibernate Configuration DTD 3.0//EN\"\n"+
"\"http://hibernate.sourceforge.net/hibernate-configuration-3.0.dtd\">\n"+

"<hibernate-configuration>\n"+
  
    "<session-factory>\n"+
  
        "<property name=\"hibernate.connection.driver_class\">oracle.jdbc.driver.OracleDriver</property>\n"+
        "<property name=\"hibernate.connection.url\">jdbc:oracle:thin:@"+url+":1521:"+databaseName+"</property>\n"+
        "<property name=\"hibernate.connection.username\">"+username+"</property>\n"+
        "<property name=\"hibernate.connection.password\">"+password+"</property>\n"+
       "<property name=\"hibernate.dialect\">org.hibernate.dialect.Oracle10gDialect</property>\n"+
      
       " <property name=\"connection.autocommit\">true</property>"+
  		"<property name=\"show_sql\">true</property>\n"+
  		"<property name=\"format_sql\">true</property>\n"+
  		
       "<!-- <mapping class=\"org.oliot.model.oliot.DBUser\"></mapping> -->\n"+
        "<mapping class=\"org.oliot.model.oliot.Action\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.AggregationEvent\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.AggregationEventEPCs\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.AggregationEventExtension\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.AggregationEventExtension2\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.ArrayOfString\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.Attribute\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.BusinessLocation\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.BusinessLocationExtension\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.BusinessScope\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.BusinessService\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.BusinessTransaction\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.BusinessTransactionList\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.ChildID\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.ContactInformation\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.CorrectiveEventID\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.CorrectiveEventIDs\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.CorrelationInformation\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.DBUser\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.DestinationList\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.Document\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.DocumentIdentification\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.DuplicateNameException\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.DuplicateSubscriptionException\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.EmptyParms\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.EPCISBodyExtension\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.EPCISBodyType\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.EPCISDocumentExtensionType\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.EPCISDocumentType\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.EPCISEvent\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.EPCISEventExtension_R\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.EPCISEventExtension\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.EPCISEventExtension2\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.EPCISEventListExtension2Type\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.EPCISEventListExtensionType\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.EPCISException\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.EPCISHeaderExtensionType\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.EPCISHeaderType\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.EPCISMasterDataBodyExtensionType\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.EPCISMasterDataBodyType\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.EPCISMasterDataDocumentExtensionType\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.EPCISMasterDataDocumentType\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.EPCISMasterDataExtensionType\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.EPCISMasterDataHeaderExtensionType\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.EPCISMasterDataType\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.EPCISQueryBodyType\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.EPCISQueryDocumentExtensionType\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.EPCISQueryDocumentType\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.EPCList\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.EPCN\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.ErrorDeclaration\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.ErrorDeclarationExtension\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.EventListType\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.ExtensionMap\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.ExtensionMaps\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.GetSubscriptionIDs\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.IDList\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.ILMD\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.ILMDExtension\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.ImplementationException\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.ImplementationExceptionSeverity\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.InvalidURIException\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.Manifest\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.ManifestItem\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.NoSuchNameException\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.NoSuchSubscriptionException\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.ObjectEvent\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.ObjectEventEPCs\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.ObjectEventExtension\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.ObjectEventExtension2\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.ObjectFactory\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.package-info\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.Partner\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.PartnerIdentification\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.Poll\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.PollParameters\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.QuantityElement\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.QuantityEvent\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.QuantityEventExtension\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.QuantityList\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.QueryParam\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.QueryParameterException\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.QueryParams\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.QueryResults\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.QueryResultsBody\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.QueryResultsExtensionType\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.QuerySchedule\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.QueryScheduleExtensionType\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.QueryTooComplexException\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.QueryTooLargeException\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.ReadPoint\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.ReadPointExtension\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.RESTSubscriptionType\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.Scope\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.SecurityException\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.SensingElement\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.SensingList\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.Sensor\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.SensorEvent\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.SensorEventExtension\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.ServiceTransaction\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.SourceDest\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.SourceList\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.StandardBusinessDocument\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.StandardBusinessDocumentHeader\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.Subscribe\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.SubscribeNotPermittedException\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.Subscription\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.SubscriptionControls\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.SubscriptionControlsException\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.SubscriptionControlsExtensionType\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.TransactionEvent\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.TransactionEventEPCs\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.TransactionEventExtension\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.TransactionEventExtension2\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.TransformationEvent\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.TransformationEventExtension\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.TypeOfServiceTransaction\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.Unsubscribe\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.ValidationException\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.Vocabulary\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.VocabularyElement\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.VocabularyElementExtension\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.VocabularyElementList\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.VocabularyExtension\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.VocabularyListType\" />\n"+
			"<mapping class=\"org.oliot.model.oliot.VoidHolder\" />\n"+
       "<!--  <property name=\"hibernate.hbm2ddl.auto\">create</property>\n"+
       "<mapping resource=\"com/mkyong/user/DBUser.hbm.xml\"></mapping>  -->\n"+
    "</session-factory>\n"+
"</hibernate-configuration>";
		return oracleDBConfig;
	}
	

}
