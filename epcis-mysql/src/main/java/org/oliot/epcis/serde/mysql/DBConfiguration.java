package org.oliot.epcis.serde.mysql;

public class DBConfiguration {

	public  static String getDB(){
		String config="<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
	"<!-- dispatcher-servlet.xml -->\n"+
		"<beans xmlns=\"http://www.springframework.org/schema/beans\"\n"+
			"xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n"+ 
			"xmlns:context=\"http://www.springframework.org/schema/context\"\n"+
			"xsi:schemaLocation=\"http://www.springframework.org/schema/beans\n"+
		        "http://www.springframework.org/schema/beans/spring-beans.xsd\n"+
		        "http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context.xsd\n"+
		        "http://www.springframework.org/schema/data/mongo\n"+
		        "http://www.springframework.org/schema/data/mongo/spring-mongo-1.5.xsd\">\n"+       
		"<context:annotation-config/>\n"+
		 "<context:component-scan base-package=\"org.oliot.epcis.serde.mysql\"/>\n"+   
		  "<bean id=\"queryOprationBackend\" class=\"org.oliot.epcis.service.query.mysql.QueryOprationBackend\"/>\n"+  
		  "<!--<bean id=\"dataSource\" class=\"org.apache.commons.dbcp2.BasicDataSource\">-->\n"+ 
		  "<bean id=\"dataSource\" class=\"org.springframework.jdbc.datasource.DriverManagerDataSource\">\n"+ 
		    "<property name=\"driverClassName\" value=\"com.mysql.jdbc.Driver\"/>\n"+ 
		    "<property name=\"url\" value=\"jdbc:mysql://localhost/epcis\"/>\n"+ 
		    "<property name=\"username\" value=\"root\"/>\n"+ 
		    "<property name=\"password\" value=\"root\"/>\n"+ 
		    
		    "<!--<property name=\"initialSize\" value=\"2\"/>\n"+ 
		    "<property name=\"maxTotal\" value=\"100\"/>-->\n"+ 
		 "</bean>"+  
		"<!--org.springframework.orm.hibernate4.LocalSessionFactoryBean  -->\n"+ 
		 "<bean id=\"sessionFactory\" class=\"org.springframework.orm.hibernate3.annotation.AnnotationSessionFactoryBean\">\n"+ 
		    "<property name=\"dataSource\" ref=\"dataSource\" />\n"+ 
		    "<property name=\"packagesToScan\" value=\"org.oliot.model.oliot\"/>\n"+ 
		    "<property name=\"hibernateProperties\">\n"+ 
		    "<props>\n"+ 
		      "<prop key=\"dialect\">org.hibernate.dialect.MySQLDialect</prop>\n"+ 
		       "<prop key=\"hibernate.hbm2ddl.auto\">update</prop>\n"+  
		       "<!--create-drop   update-->\n"+ 
		        "<prop key=\"hibernate.show_sql\">true</prop>\n"+  
		        "<prop key=\"hibernate.format_sql\">true</prop>\n"+  
		        "<prop key=\"use_sql_comments\">true</prop>\n"+ 
		   " </props>\n"+ 
		    "</property>\n"+ 
		        "<!--   <property name=\"hibernate.hbm2ddl.auto\" value=\"update\"/>\n"+ 
		        "<property name=\"hibernate.show_sql\" value=\"true\"/>\n"+ 
		        "<property name=\"hibernate.format_sql\" value=\"false\"/>-->\n"+ 
		 "</bean>\n"+  
		"</beans>\n";
		
		return config;
	}
	
	public static String getDB(String userName, String password){
		String config="<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
	"<!-- dispatcher-servlet.xml -->"+
		"<beans xmlns=\"http://www.springframework.org/schema/beans\""+
			"xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""+ 
			"xmlns:context=\"http://www.springframework.org/schema/context\""+
			"xsi:schemaLocation=\"http://www.springframework.org/schema/beans"+
		        "http://www.springframework.org/schema/beans/spring-beans.xsd"+
		        "http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context.xsd"+
		        "http://www.springframework.org/schema/data/mongo"+
		        "http://www.springframework.org/schema/data/mongo/spring-mongo-1.5.xsd\">"+       
		"<context:annotation-config/>"+
		 "<context:component-scan base-package=\"org.oliot.epcis.serde.mysql\"/>"+   
		  "<bean id=\"queryOprationBackend\" class=\"org.oliot.epcis.service.query.mysql.QueryOprationBackend\"/>"+  
		  "<!--<bean id=\"dataSource\" class=\"org.apache.commons.dbcp2.BasicDataSource\">-->"+ 
		  "<bean id=\"dataSource\" class=\"org.springframework.jdbc.datasource.DriverManagerDataSource\">"+ 
		    "<property name=\"driverClassName\" value=\"com.mysql.jdbc.Driver\"/>"+ 
		    "<property name=\"url\" value=\"jdbc:mysql://localhost/epcis\"/>"+ 
		    "<property name=\"username\" value=\""+userName+"\"/>"+ 
		    "<property name=\"password\" value=\""+password+"\"/>"+ 
		    
		    "<!--<property name=\"initialSize\" value=\"2\"/>"+ 
		    "<property name=\"maxTotal\" value=\"100\"/>-->"+ 
		 "</bean>"+  
		"<!--org.springframework.orm.hibernate4.LocalSessionFactoryBean  -->"+ 
		 "<bean id=\"sessionFactory\" class=\"org.springframework.orm.hibernate3.annotation.AnnotationSessionFactoryBean\">"+ 
		    "<property name=\"dataSource\" ref=\"dataSource\" />"+ 
		    "<property name=\"packagesToScan\" value=\"org.oliot.model.oliot\"/>"+ 
		    "<property name=\"hibernateProperties\">"+ 
		    "<props>"+ 
		      "<prop key=\"dialect\">org.hibernate.dialect.MySQLDialect</prop>"+ 
		       "<prop key=\"hibernate.hbm2ddl.auto\">update</prop>"+  
		       "<!--create-drop   update-->"+ 
		        "<prop key=\"hibernate.show_sql\">true</prop>"+  
		        "<prop key=\"hibernate.format_sql\">true</prop>"+  
		        "<prop key=\"use_sql_comments\">true</prop>"+ 
		   " </props>"+ 
		    "</property>"+ 
		        "<!--   <property name=\"hibernate.hbm2ddl.auto\" value=\"update\"/>"+ 
		        "<property name=\"hibernate.show_sql\" value=\"true\"/>"+ 
		        "<property name=\"hibernate.format_sql\" value=\"false\"/>-->"+ 
		 "</bean>"+  
		"</beans>";
		
		return config;
	}
}
