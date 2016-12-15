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
	
	public static String getMysqlConfigXml2(){
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
		   " <property name=\"driverClassName\" value=\"org.postgresql.Driver\"/>\n"+
		    "<property name=\"url\" value=\"jdbc:postgresql://localhost:5432/epcis2\" />\n"+
		    "<property name=\"username\" value=\"post\" />\n"+
		    "<property name=\"password\" value=\"post\" />\n"+    
		 "</bean>\n"+
		 
		 "<bean id=\"sessionFactory\" class=\"org.springframework.orm.hibernate3.annotation.AnnotationSessionFactoryBean\">\n"+
		    "<property name=\"dataSource\" ref=\"dataSource\" />\n"+
		    "<property name=\"packagesToScan\" value=\"org.oliot.model.oliot\"/>\n"+
		   " <property name=\"hibernateProperties\">\n"+
		   " <props>"+
		     " <prop key=\"dialect\">org.hibernate.dialect.PostgreSQLDialect</prop>\n"+
		      " <prop key=\"hibernate.hbm2ddl.auto\">update</prop>\n"+
		       "<!--create-drop   update-->\n"+
		        "<prop key=\"hibernate.show_sql\">true</prop>\n"+
		        "<prop key=\"hibernate.format_sql\">true</prop>\n"+
		        "<prop key=\"use_sql_comments\">true</prop>\n"+
		    "</props>\n"+
		   " </property>\n"+
		       " <!--   <property name=\"hibernate.hbm2ddl.auto\" value=\"update\"/>\n"+
		       " <property name=\"hibernate.show_sql\" value=\"true\"/>\n"+
		        "<property name=\"hibernate.format_sql\" value=\"false\"/>-->\n"+
		 "</bean>\n"+
		"</beans>";
		
		return xml;
	}
	

}
