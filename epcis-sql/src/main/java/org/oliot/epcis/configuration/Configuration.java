package org.oliot.epcis.configuration;

import java.awt.Desktop;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import org.json.JSONObject;
import org.oliot.epcis.db.DBConfig;
import org.oliot.epcis.db.LoginListener;
import org.oliot.epcis.serde.sql.CaptureOperationsBackend;
import org.oliot.epcis.service.query.sql.QueryOprationBackend;
import org.oliot.epcis.service.subscription.MysqlSubscription;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;




/**
 * Copyright (C) 2014-2016 Jaewook Byun
 *
 * This project is part of Oliot open source (http://oliot.org). Oliot EPCIS
 * v1.2.x is Java Web Service complying with Electronic Product Code Information
 * Service (EPCIS) v1.2.
 *
 * @author Jaewook Jack Byun, Ph.D student
 * 
 *         Korea Advanced Institute of Science and Technology (KAIST)
 * 
 *         Real-time Embedded System Laboratory(RESL)
 * 
 *         bjw0829@kaist.ac.kr, bjw0829@gmail.com
 *         
 * @author Yalew kidane, Ph.D student
 * 
 *         Korea Advanced Institute of Science and Technology (KAIST)
 * 
 *         Real-time Embedded System Laboratory(RESL)
 * 
 *         yalewkidane@gmail.com/@kaist.ac.kr
 */

public class Configuration implements ServletContextListener {

	public static Logger logger;
	public static String webInfoPath;
	public static String wsdlPath;
	public static String contextPath;
	public static boolean isCaptureVerfificationOn;
	public static String facebookAppID;
	public static String adminID;
	public static String adminScope;
	public static boolean isQueryAccessControlOn;
	public static boolean isTriggerSupported;
	
	public static String DBxml;
	
	
	
	public static boolean connect=false;
	
	public static CaptureOperationsBackend mysqlOperationdao;
	
	private static ApplicationContext ctx;
	
	public static QueryOprationBackend mysqlOperationdaoQr;
	
	private static ApplicationContext ctxQr;
	
	

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {

	}

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {

		// Set Logger
		setLogger();

		// Set Basic Configuration with Configuration.json
		setBasicConfiguration(servletContextEvent.getServletContext());

		// load existing subscription
		loadExistingSubscription();
		
	}

	private void setLogger() {
		// Log4j Setting
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.INFO);
		Configuration.logger = Logger.getRootLogger();
	}

	private void setBasicConfiguration(ServletContext context) {
		String path = context.getRealPath("/WEB-INF");
		Configuration.logger.info(path);
		try {
			// Get Configuration.json
			File file = new File(path + "/Configuration.json");
			FileReader fileReader = new FileReader(file);
			BufferedReader reader = new BufferedReader(fileReader);

			String data = "";
			String line = null;
			while ((line = reader.readLine()) != null) {
				data += line;
			}
			reader.close();
			JSONObject json = new JSONObject(data);

			Configuration.webInfoPath = path;
			Configuration.wsdlPath = context.getRealPath("/wsdl");			
			Configuration.contextPath = context.getRealPath("/");

			// Set up capture_verification
			String captureVerification = json.getString("capture_verification");
			if (captureVerification == null) {
				Configuration.logger.error(
						"capture_verification is null, please make sure Configuration.json is correct, and restart.");
			}
			captureVerification = captureVerification.trim();
			if (captureVerification.equals("on")) {
				Configuration.isCaptureVerfificationOn = true;
				Configuration.logger.info("Capture_Verification - ON ");
			} else if (captureVerification.equals("off")) {
				Configuration.isCaptureVerfificationOn = false;
				Configuration.logger.info("Capture_Verification - OFF ");
			} else {
				Configuration.logger.error(
						"capture_verification should be (on|off), please make sure Configuration.json is correct, and restart.");
			}

			// Query Access Control
			// Set up capture_verification
			String queryAC = json.getString("query_access_control");
			if (queryAC == null) {
				Configuration.logger
						.error("query_access_control, please make sure Configuration.json is correct, and restart.");
			}
			queryAC = queryAC.trim();
			if (queryAC.equals("on")) {
				Configuration.isQueryAccessControlOn = true;
				Configuration.logger.info("Query_AccessControl - ON ");
			} else if (queryAC.equals("off")) {
				Configuration.isQueryAccessControlOn = false;
				Configuration.logger.info("Query_AccessControl - OFF ");
			} else {
				Configuration.logger.error(
						"query_access_control should be (on|off), please make sure Configuration.json is correct, and restart.");
			}

			// Facebook Application ID
			String fai = json.getString("facebook_app_id");
			if (fai == null) {
				Configuration.logger
						.error("facebook_app_id, please make sure Configuration.json is correct, and restart.");
			}
			facebookAppID = fai.trim();

			// Admin Facebook ID
			String aID = json.getString("admin_facebook_id");
			if (aID == null) {
				Configuration.logger
						.error("admin_facebook_id, please make sure Configuration.json is correct, and restart.");
			}
			adminID = aID.trim();

			// Admin Scope
			String aScope = json.getString("admin_scope");
			if (aScope == null) {
				Configuration.logger.error("admin_scope, please make sure Configuration.json is correct, and restart.");
			}
			adminScope = aScope.trim();

			 
			// Trigger Support
			String triggerSupport = json.getString("trigger_support");
			if (triggerSupport == null || triggerSupport.trim().equals("on")) {
				isTriggerSupported = true;
			} else {
				isTriggerSupported = false;
			}
			
			//window will pop up for connection 
			dbLogin();
			while(!connect){
				System.out.println("...");
				Thread.sleep(2000);
			}
			
			//open database
			//ApplicationContext 
			
			//set database
			//backendDB = json.getString("backend_DB");
			//backendDB=Configuration.dbNameComboBox.
			setDB(DBConfig.database);
			
			Configuration.logger.info("Waiting for database configuration");
			
			ctx=new ClassPathXmlApplicationContext(Configuration.DBxml);
			
			mysqlOperationdao=ctx.getBean
					("captureOperationsBackend", CaptureOperationsBackend.class);
			
			ctxQr=new ClassPathXmlApplicationContext(Configuration.DBxml);
			//ctxQr.getBean("dataSource").s
			mysqlOperationdaoQr=ctxQr.getBean
					("queryOprationBackend", QueryOprationBackend.class);
			
			
			frame.setVisible(false);
			
		    if (Desktop.isDesktopSupported()) {
		        Desktop desktop = Desktop.getDesktop();
		        if (desktop.isSupported(Desktop.Action.BROWSE)) {
		            try {
		                desktop.browse(new URI("http://localhost:8080/epcis/"));
		            }
		            catch(IOException ioe) {
		                ioe.printStackTrace();
		            }
		            catch(URISyntaxException use) {
		                use.printStackTrace();
		            }
		        }
		    }
			

		} catch (Exception ex) {
			Configuration.logger.error(ex.toString());
		}
	}


	private void setDB(String backendDB){
		if(backendDB.equals("MySQL")){
			Configuration.logger.info("Backend is MySQL");
			DBxml="MysqlConfig.xml";
			
		}else if(backendDB.equals("PostgreSQL")){
			Configuration.logger.info("Backend is PostgreSQL");
			DBxml="PostgreSQLConfig.xml";
		}else if(backendDB.equals("MariaDB")){
			Configuration.logger.info("Backend is MariaDB");
			DBxml="MariaDBConfig.xml";
		}
	
	}
	

	private void loadExistingSubscription() {
				
		MysqlSubscription ms = new MysqlSubscription();
		ms.init();
	}
	
	
	
	private void dbLogin(){
		frame = new JFrame("Database Configuration");
		frame.setSize(600, 300);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel panel = new JPanel();
		frame.add(panel);
		placeComponents(panel);

		frame.setVisible(true);
	}
	public static JFrame frame;
	public static JLabel dbNameLabelConfig;
	public static JComboBox<String> dbNameComboBox;
	public static JLabel urlLabelConfig;
	public static JTextField urlTextConfig;
	public static JLabel databaseLabelConfig;
	public static JTextField databaseTextConfig;
	public static JLabel userLabelConfig;
	public static JTextField userTextConfig;
	public static JLabel passwordLabelConfig;
	public static JPasswordField passwordTextConfig;
	public static JButton loginButtonConfig;
	
	private static void placeComponents(JPanel panel) {
		
		panel.setLayout(null);
		
		JLabel imageLabel=new JLabel();
		imageLabel.setBounds(10, 10, 380, 210);
		

		ImageIcon icon=new ImageIcon(Configuration.contextPath+"image/Cintro.png");
		imageLabel.setIcon(icon);
		panel.add(imageLabel);
		
		int x1=310;
		int x2=400;
		
		dbNameLabelConfig= new JLabel("Database");
		dbNameLabelConfig.setBounds(x1, 10, 80, 25);
		panel.add(dbNameLabelConfig);
		
		dbNameComboBox=new JComboBox<String>(); 
		dbNameComboBox.setBounds(x2, 10, 160, 25);
		dbNameComboBox.addItem("MySQL");
		dbNameComboBox.addItem("MariaDB");
		dbNameComboBox.addItem("PostgreSQL");
		dbNameComboBox.addItem("Oracle");
		dbNameComboBox.setSelectedItem("MySQL");
		panel.add(dbNameComboBox);
		
		
		urlLabelConfig = new JLabel("URL");
		urlLabelConfig.setBounds(x1, 40, 80, 25);
		panel.add(urlLabelConfig);

		urlTextConfig = new JTextField(20);
		urlTextConfig.setBounds(x2, 40, 160, 25);
		urlTextConfig.setText("localhost");
		panel.add(urlTextConfig);
		
		databaseLabelConfig = new JLabel("User");
		databaseLabelConfig.setBounds(x1, 80, 80, 25);
		panel.add(databaseLabelConfig);

		databaseTextConfig = new JTextField(20);
		databaseTextConfig.setBounds(x2, 80, 160, 25);
		databaseTextConfig.setText("epcis");
		panel.add(databaseTextConfig);
		
		userLabelConfig = new JLabel("User");
		userLabelConfig.setBounds(x1, 120, 80, 25);
		panel.add(userLabelConfig);

		userTextConfig = new JTextField(20);
		userTextConfig.setBounds(x2, 120, 160, 25);
		userTextConfig.setText("root");
		panel.add(userTextConfig);

		passwordLabelConfig = new JLabel("Password");
		passwordLabelConfig.setBounds(x1, 160, 80, 25);
		panel.add(passwordLabelConfig);

		passwordTextConfig = new JPasswordField(20);
		passwordTextConfig.setBounds(x2, 160, 160, 25);
		passwordTextConfig.setText("root");
		panel.add(passwordTextConfig);
		
		loginButtonConfig = new JButton("Connect");
		loginButtonConfig.setBounds(x1+40, 200, 120, 25);
		panel.add(loginButtonConfig);
		
		
		ActionListener myButtonListener = new LoginListener();
		loginButtonConfig.addActionListener(myButtonListener);
	}
}
