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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JOptionPane;

import org.hibernate.Query;
import org.hibernate.Session;
import org.oliot.epcis.configuration.Configuration;
import org.oliot.epcis.serde.sql.HibernateUtil;
import org.oliot.model.epcis.SubscriptionType;
import org.oliot.model.oliot.DBUser;
import org.oliot.model.oliot.Subscription;

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
					path=Configuration.webInfoPath; ///WEB-INF/classes/
					File file = new File(path + "/classes/hibernate.cfg.xml");
					
					fileWrite=new FileWriter(file);
					writer=new BufferedWriter(fileWrite); 
					writer.write(DBConfig.getOracleDBConfig());
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
			try {
				createDatabase(DBConfig.database);
			} catch (SQLException e1) {
				System.out.println("createDatabase failed");
				e1.printStackTrace();
			}
			System.out.println("Back to configuration");
			
		}
		
		
		
		
	}
	
	private void createDatabase(String backendDB) throws SQLException{
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
			try{				
				System.out.println("Maven + Hibernate + Oracle");
				Session session = HibernateUtil.getSessionFactory().openSession();

				session.beginTransaction();
				DBUser user = new DBUser();

				user.setUserId(111);
				user.setUsername("superman");
				user.setCreatedBy("system");
				user.setCreatedDate(new Date());

				session.save(user);
				session.getTransaction().commit();
				
				//Session session = getSessionFactory().openSession();
				String hql=" from Subscription ";
				Query query=session.createQuery(hql);
				List<Subscription> subscriptionList=query.list();
				session.close();
				
				List<SubscriptionType> retList = new ArrayList<SubscriptionType>();
				for (int i = 0; i < subscriptionList.size(); i++) {
					//retList.add(convertToSubscriptionType(subscriptionList.get(i)));
				}
				Configuration.connect=true;
			    Configuration.frame.setVisible(false);
			}catch(Exception e){
				createDbUserTable();
				
				
			}
		}
	}
	
	private static void createDbUserTable() throws SQLException {
		
		String DB_DRIVER = "oracle.jdbc.driver.OracleDriver";
		String DB_CONNECTION = "jdbc:oracle:thin:@"+DBConfig.url+":1521:"+DBConfig.databaseName;
		//private static final String DB_CONNECTION = "jdbc:oracle:thin:@143.248.57.21:1521:orcl";
		String DB_USER = DBConfig.username;
		String DB_PASSWORD = DBConfig.password;
		
		Connection dbConnection = null;
		Statement statement = null;
		System.out.println("Table creation started!");
//		String createTableSQL = "CREATE TABLE DBUSER ("+
//							"USER_ID NUMBER GENERATED BY DEFAULT ON NULL AS IDENTITY,"+
//							"USERNAME      VARCHAR2 (20)  NOT NULL,"+
//							"CREATED_BY    VARCHAR2 (20)  NOT NULL,"+
//							"CREATED_DATE  DATE          NOT NULL)";
		BufferedReader br = null;
		FileReader fr = null;
		try {
			/*
			dbConnection = getDBConnection( DB_DRIVER, DB_CONNECTION,  DB_USER,  DB_PASSWORD);
			statement = dbConnection.createStatement();

			System.out.println(createTableSQL);
                        // execute the SQL stetement
			statement.execute(createTableSQL);

			System.out.println("Table \"dbuser\" is created!");
			*/

		    
			String path="";//"C:/Project/epcis/workspace/accessingApp/src/resource/createTables.txt";
			path=Configuration.webInfoPath; ///WEB-INF/classes/
			path=path+ "/classes/createTables.txt";
			File file = new File(path);
			System.out.println(path);
			fr = new FileReader(file);
			br = new BufferedReader(fr);

			String sCurrentLine;

			br = new BufferedReader(new FileReader(path));
			String createTableSQL="";
			while ((sCurrentLine = br.readLine()) != null) {
				if(sCurrentLine.contains(";")){
					sCurrentLine=sCurrentLine.replace(';', ' ');
					createTableSQL+=sCurrentLine+"\n";
					//System.out.println("Statment:");
					//System.out.println(createTableSQL);
					createTable(createTableSQL);
					createTableSQL="";
				}else{
					createTableSQL+=sCurrentLine+"\n";
				}
				
				
			}
			Configuration.connect=true;
		    Configuration.frame.setVisible(false);

		} catch (SQLException e) {
			String message=e.getMessage();
			JOptionPane.showMessageDialog(null, message);
			System.out.println(e.getMessage());

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {

			if (statement != null) {
				statement.close();
			}

			if (dbConnection != null) {
				dbConnection.close();
			}

		}

	}
	
	private static Connection getDBConnection(String DB_DRIVER,String DB_CONNECTION, String DB_USER, String DB_PASSWORD ) {

		Connection dbConnection = null;

		try {

			Class.forName(DB_DRIVER);

		} catch (ClassNotFoundException e) {

			System.out.println(e.getMessage());

		}

		try {

			dbConnection = DriverManager.getConnection(
					DB_CONNECTION, DB_USER,DB_PASSWORD);
			return dbConnection;

		} catch (SQLException e) {

			System.out.println(e.getMessage());

		}

		return dbConnection;

	}
	
	
	private static void createTable(String createTableSQL) throws SQLException {
		
		String DB_DRIVER = "oracle.jdbc.driver.OracleDriver";
		String DB_CONNECTION = "jdbc:oracle:thin:@"+DBConfig.url+":1521:"+DBConfig.databaseName;
		//private static final String DB_CONNECTION = "jdbc:oracle:thin:@143.248.57.21:1521:orcl";
		String DB_USER = DBConfig.username;
		String DB_PASSWORD = DBConfig.password;
		
		Connection dbConnection = null;
		Statement statement = null;


		try {
			//dbConnection = getDBConnection();
			dbConnection = getDBConnection( DB_DRIVER, DB_CONNECTION,  DB_USER,  DB_PASSWORD);
			statement = dbConnection.createStatement();

			System.out.println(createTableSQL);
                        // execute the SQL stetement
			statement.execute(createTableSQL);

			System.out.println("Table \"dbuser\" is created!");

		} catch (SQLException e) {

			System.out.println(e.getMessage());

		} finally {

			if (statement != null) {
				statement.close();
			}

			if (dbConnection != null) {
				dbConnection.close();
			}

		}

	}
	
	

}
