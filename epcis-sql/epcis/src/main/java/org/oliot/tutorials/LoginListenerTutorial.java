package org.oliot.tutorials;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JButton;
import javax.swing.JOptionPane;

import org.oliot.epcis.configuration.Configuration;
import org.oliot.epcis.db.DBConfig;

public class LoginListenerTutorial implements ActionListener{

	@Override
	public void actionPerformed(ActionEvent e) {
		JButton source = (JButton) e.getSource();
		
		
		
		if(source.getText().equals("Connect")){
			JOptionPane.showMessageDialog(source, source.getText() + " button has been pressed");
				
			
		}else if(source.getText().equals("Configure")){
			JOptionPane.showMessageDialog(source, Tutorial.dbNameComboBox.getSelectedItem()+" is selected");
		}
		
		
		
	}
	
	@SuppressWarnings("unused")
	private void createDatabase(String backendDB){
		if(backendDB.equals("MySQL")){
			String JDBC_DRIVER = "com.mysql.jdbc.Driver"; 
			String DB_URL = "jdbc:mysql://localhost/";
			
			//static final
			// String USER = "root";
			// String PASS = "root";
			 
			 Connection conn = null;
			 Statement stmt = null;
			 
			   try{
				      //STEP 2: Register JDBC driver
				      Class.forName("com.mysql.jdbc.Driver");

				      //STEP 3: Open a connection
				      System.out.println("Connecting to database...");
				      conn = DriverManager.getConnection(DB_URL, DBConfig.username, DBConfig.password);

				      //STEP 4: Execute a query
				      System.out.println("Creating database...");
				      stmt = conn.createStatement();
				      
				      String sql = "CREATE DATABASE IF NOT EXISTS epcis";
				      stmt.executeUpdate(sql);
				      System.out.println("Database created successfully...");
				      Configuration.connect=true;
				   }catch(SQLException se){
				      //Handle errors for JDBC
				     se.printStackTrace();
				     String message=se.getMessage();
				     JOptionPane.showMessageDialog(null, message);
				   }catch(Exception e){
				      //Handle errors for Class.forName
				      e.printStackTrace();
				      String message=e.getMessage();
					  JOptionPane.showMessageDialog(null, message);
				   }finally{
				      //finally block used to close resources
				      try{
				         if(stmt!=null)
				            stmt.close();
				      }catch(SQLException se2){
				      }// nothing we can do
				      try{
				         if(conn!=null)
				            conn.close();
				      }catch(SQLException se){
				         se.printStackTrace();
				         String message=se.getMessage();
						 JOptionPane.showMessageDialog(null, message);
				      }//end finally try
				   }//end try
			   
		}else if(backendDB.equals("PostgreSQL")){
			
		}else if(backendDB.equals("MariaDB")){
			
		}
	}

}
