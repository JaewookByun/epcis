package org.oliot.tutorials;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JOptionPane;

import org.oliot.epcis.db.DBConfig;

public class PostgreSQLJDBC {

	public static void main(String[] args) {
		//createDatabase();
		deleteDatabase();
	}
	
	@SuppressWarnings("unused")
	private static void createDatabase(){
		DBConfig.url="localhost";
		DBConfig.databaseName="epcis4";
		DBConfig.username="post";
		DBConfig.password="post";
		String JDBC_DRIVER = "org.postgresql.Driver"; 
		//String DB_URL = "jdbc:mysql://"+DBConfig.url+"/";
		String DB_URL ="jdbc:postgresql://"+DBConfig.url+":5432/epcis2";
		
		 
		 Connection conn = null;
		 Statement stmt = null;
		 
		   try{
			      
			      Class.forName(JDBC_DRIVER);

			      
			      System.out.println("Connecting to database...");
			      conn = DriverManager.getConnection(DB_URL, DBConfig.username, DBConfig.password);

			      System.out.println("Creating database...");
			      stmt = conn.createStatement();
			     
			      String sql = "CREATE DATABASE "+DBConfig.databaseName;
			      stmt.executeUpdate(sql);
			      System.out.println("Database created successfully...");
			      //Configuration.connect=true;
			      //Configuration.frame.setVisible(false);
			   }catch(SQLException se){
			     se.printStackTrace();
			     System.out.println("i1");
			     
			     String message=se.getMessage();
			     if(message.contains("database \""+DBConfig.databaseName+"\" already exists")){
			    	 System.out.println(" My message: "+message);
			     }else{
			    	 System.out.println(" My message: "+message);
				     JOptionPane.showMessageDialog(null, message);
			     }
			     
			   }catch(Exception e){
				   System.out.println("i2");
			      e.printStackTrace();
			      String message=e.getMessage();
				  JOptionPane.showMessageDialog(null, message);
			   }finally{
			      try{
			         if(stmt!=null)
			        	 System.out.println("i3");
			            stmt.close();
			      }catch(SQLException se2){
			      }
			      try{
			         if(conn!=null)
			        	 System.out.println("i6");
			            conn.close();
			      }catch(SQLException se){
			    	  System.out.println("i5");
			         se.printStackTrace();
			         String message=se.getMessage();
					 JOptionPane.showMessageDialog(null, message);
			      }
			   }
	}
	
	private static void deleteDatabase(){
		DBConfig.url="localhost";
		DBConfig.databaseName="epcis7";
		DBConfig.username="post";
		DBConfig.password="post";
		String JDBC_DRIVER = "org.postgresql.Driver"; 
		//String DB_URL = "jdbc:mysql://"+DBConfig.url+"/";
		String DB_URL ="jdbc:postgresql://"+DBConfig.url+":5432/epcis2";
		
		 
		 Connection conn = null;
		 Statement stmt = null;
		 
		   try{
			      
			      Class.forName(JDBC_DRIVER);

			      
			      System.out.println("Connecting to PostgresSQL...");
			      conn = DriverManager.getConnection(DB_URL, DBConfig.username, DBConfig.password);

			      System.out.println("Deleting database...");
			      stmt = conn.createStatement();
			     
			      String sql = "DROP DATABASE "+DBConfig.databaseName;
			      stmt.executeUpdate(sql);
			      System.out.println("Database deleted successfully...");
			      //Configuration.connect=true;
			      //Configuration.frame.setVisible(false);
			   }catch(SQLException se){
			     se.printStackTrace();
			     System.out.println("i1");
			     
			     String message=se.getMessage();
			     if(message.contains("database \""+DBConfig.databaseName+"\" already exists")){
			    	 System.out.println(" My message: "+message);
			     }else{
			    	 System.out.println(" My message: "+message);
				     JOptionPane.showMessageDialog(null, message);
			     }
			     
			   }catch(Exception e){
			      e.printStackTrace();
			      String message=e.getMessage();
				  JOptionPane.showMessageDialog(null, message);
			   }finally{
			      try{
			         if(stmt!=null)
			            stmt.close();
			      }catch(SQLException se2){
			      }
			      try{
			         if(conn!=null)
			            conn.close();
			      }catch(SQLException se){
			         se.printStackTrace();
			         String message=se.getMessage();
					 JOptionPane.showMessageDialog(null, message);
			      }
			   }
	}

}
