package org.oliot.epcis.db;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JButton;
import javax.swing.JOptionPane;

import org.oliot.epcis.configuration.Configuration;

public class LoginListener implements ActionListener{

	@Override
	public void actionPerformed(ActionEvent e) {
		JButton source = (JButton) e.getSource();
		
		
		
		if(source.getText().equals("Connect")){
			
			DBConfig.database=Configuration.dbNameComboBox.getSelectedItem().toString();
			DBConfig.databaseName=Configuration.databaseTextConfig.getText();
			DBConfig.url=Configuration.urlTextConfig.getText();
			DBConfig.username=Configuration.userTextConfig.getText();
			DBConfig.password=String.valueOf(Configuration.passwordTextConfig.getPassword());//Configuration.passwordTextConfig.getText();
			
			System.out.println(DBConfig.database);
			System.out.println(DBConfig.url);
			System.out.println(DBConfig.databaseName);
			System.out.println(DBConfig.username);
			System.out.println(DBConfig.password);
			
			System.out.println("File Write started ");
			String path;
			FileWriter fileWrite=null;
			BufferedWriter writer=null;
			try {
				
				if(DBConfig.database.equals("MySQL")){
					path=Configuration.webInfoPath; ///WEB-INF/classes/
					File file = new File(path + "/classes/MysqlConfig.xml");
					
					fileWrite=new FileWriter(file);
					writer=new BufferedWriter(fileWrite);
					writer.write(DBConfig.getMysqlConfigXml());
				}else if(DBConfig.database.equals("MariaDB")){
					path=Configuration.webInfoPath; ///WEB-INF/classes/
					File file = new File(path + "/classes/MysqlConfig.xml");
					
					fileWrite=new FileWriter(file);
					writer=new BufferedWriter(fileWrite);
					writer.write(DBConfig.getMysqlConfigXml());
				}else if(DBConfig.database.equals("PostgreSQL")){
					path=Configuration.webInfoPath; ///WEB-INF/classes/
					File file = new File(path + "/classes/PostgreSQLConfig.xml");
					
					fileWrite=new FileWriter(file);
					writer=new BufferedWriter(fileWrite); 
					writer.write(DBConfig.getPostgresqlXml());
				}else if(DBConfig.database.equals("Oracle")){
					//writer.write(DBConfig.getPostgresqlXml());
				}
				
				System.out.println("File write ended");	

			} catch (IOException ex) {
				Configuration.logger.error(ex.getMessage());
				ex.printStackTrace();
			}finally {

				try {

					if (writer != null)
						writer.close();

					if (fileWrite != null)
						fileWrite.close();

				}  catch (IOException ex) {
				Configuration.logger.error(ex.getMessage());
				ex.printStackTrace();
			}
			}
			createDatabase(DBConfig.database);
			System.out.println("Back to configuration");
			
		}
		
		
		
		
	}
	
	private void createDatabase(String backendDB){
		if(backendDB.equals("MySQL")){
			String JDBC_DRIVER = "com.mysql.jdbc.Driver"; 
			String DB_URL = "jdbc:mysql://"+DBConfig.url+"/";
			
			 
			 Connection conn = null;
			 Statement stmt = null;
			 
			   try{
				      
				      Class.forName(JDBC_DRIVER);

				      
				      Configuration.logger.info("Connecting to database...");
				      conn = DriverManager.getConnection(DB_URL, DBConfig.username, DBConfig.password);

				      Configuration.logger.info("Creating database...");
				      stmt = conn.createStatement();
				      
				      String sql = "CREATE DATABASE IF NOT EXISTS "+DBConfig.databaseName;
				      stmt.executeUpdate(sql);
				      Configuration.logger.info("Database created successfully...");
				      Configuration.connect=true;
				      Configuration.frame.setVisible(false);
				   }catch(SQLException se){
				     se.printStackTrace();
				     String message=se.getMessage();
				     JOptionPane.showMessageDialog(null, message);
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
			   
		}else if(backendDB.equals("PostgreSQL")){
			String JDBC_DRIVER ="org.postgresql.Driver";
			String DB_URL ="jdbc:postgresql://"+DBConfig.url+":5432/epcis2";
			
			 
			 Connection conn = null;
			 Statement stmt = null;
			 
			   try{
				      
				      Class.forName(JDBC_DRIVER);

				      
				      Configuration.logger.info("Connecting to database...");
				      conn = DriverManager.getConnection(DB_URL, DBConfig.username, DBConfig.password);

				      Configuration.logger.info("Creating database...");
				      stmt = conn.createStatement();
				      
				      String sql = "CREATE DATABASE "+DBConfig.databaseName;
				      stmt.executeUpdate(sql);
				      Configuration.logger.info("Database created successfully...");
				      Configuration.connect=true;
				      Configuration.frame.setVisible(false);
				   }catch(SQLException se){
				     //se.printStackTrace();
					   String message=se.getMessage();
					     if(message.contains("database \""+DBConfig.databaseName+"\" already exists")){
					    	 System.out.println(" My message: "+message);
					    	 Configuration.logger.info("Database connected successfully...");
						     Configuration.connect=true;
						     Configuration.frame.setVisible(false);
						     JOptionPane.showMessageDialog(null, "Database connected successfully...");
					     }else{
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
			
		}else if(backendDB.equals("MariaDB")){
			String JDBC_DRIVER = "com.mysql.jdbc.Driver"; 
			String DB_URL = "jdbc:mysql://"+DBConfig.url+"/";
			
			 
			 Connection conn = null;
			 Statement stmt = null;
			 
			   try{
				      
				      Class.forName(JDBC_DRIVER);

				      
				      Configuration.logger.info("Connecting to database...");
				      conn = DriverManager.getConnection(DB_URL, DBConfig.username, DBConfig.password);

				      Configuration.logger.info("Creating database...");
				      stmt = conn.createStatement();
				      
				      String sql = "CREATE DATABASE IF NOT EXISTS "+DBConfig.databaseName;
				      stmt.executeUpdate(sql);
				      Configuration.logger.info("Database created successfully...");
				      Configuration.connect=true;
				      Configuration.frame.setVisible(false);
				   }catch(SQLException se){
				     se.printStackTrace();
				     String message=se.getMessage();
				     JOptionPane.showMessageDialog(null, message);
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
			
		}else if(backendDB.equals("Oracle")){
			
		}
	}

}
