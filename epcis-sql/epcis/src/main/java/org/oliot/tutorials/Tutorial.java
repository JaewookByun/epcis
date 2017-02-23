package org.oliot.tutorials;

import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.oliot.epcis.configuration.Configuration;
import org.oliot.epcis.db.DBConfig;
import org.oliot.epcis.db.LoginListener;

@SuppressWarnings("unused")
public class Tutorial {
	

	public static JLabel dbNameLabelConfig;
	public static JComboBox<String> dbNameComboBox;
	public static JLabel urlLabelConfig;
	public static JTextField urlTextConfig;
	public static JLabel userLabelConfig;
	public static JTextField userTextConfig;
	public static JLabel passwordLabelConfig;
	public static JPasswordField passwordTextConfig;
	public static JButton loginButtonConfig;

	public static void main(String[] args) {
		
		
//		BufferedReader br;
//		try {
//			br = new BufferedReader(new FileReader("src/main/resources/MysqlConfig.xml"));
//			String line = null;
//			while ((line = br.readLine()) != null) {
//				System.out.println(line);
//			}
//		 
//			br.close();
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		createWindow();
//		postgreSQLJDBC();
		
	System.out.println(DBConfig.getPostgresqlXml());
		
			

	}
	
	
	
	
	
	private static void createWindow(){
		JFrame frame = new JFrame("Database Configuration");
		frame.setSize(600, 250);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel panel = new JPanel();
		frame.add(panel);
		placeComponents(panel);

		frame.setVisible(true);
	}
	
	private static void placeComponents(JPanel panel) {

		panel.setLayout(null);

		
		
		dbNameLabelConfig= new JLabel("Database");
		dbNameLabelConfig.setBounds(310, 10, 80, 25);
		panel.add(dbNameLabelConfig);
		
		dbNameComboBox=new JComboBox<String>();
		dbNameComboBox.setBounds(400, 10, 160, 25);
		dbNameComboBox.addItem("mysql");
		dbNameComboBox.addItem("mariaDB");
		dbNameComboBox.addItem("postgresql");
		dbNameComboBox.addItem("oracle");
		dbNameComboBox.setSelectedItem("mysql");
		panel.add(dbNameComboBox);
		
		
		urlLabelConfig = new JLabel("URL");
		urlLabelConfig.setBounds(310, 40, 80, 25);
		panel.add(urlLabelConfig);

		urlTextConfig = new JTextField(20);
		urlTextConfig.setBounds(400, 40, 160, 25);
		urlTextConfig.setText("localhost/epcis");
		panel.add(urlTextConfig);
		
		userLabelConfig = new JLabel("User");
		userLabelConfig.setBounds(310, 80, 80, 25);
		panel.add(userLabelConfig);

		userTextConfig = new JTextField(20);
		userTextConfig.setBounds(400, 80, 160, 25);
		userTextConfig.setText("root");
		panel.add(userTextConfig);

		passwordLabelConfig = new JLabel("Password");
		passwordLabelConfig.setBounds(310, 120, 80, 25);
		panel.add(passwordLabelConfig);

		passwordTextConfig = new JPasswordField(20);
		passwordTextConfig.setBounds(400, 120, 160, 25);
		passwordTextConfig.setText("root");
		panel.add(passwordTextConfig);
		
		loginButtonConfig = new JButton("Connect");
		loginButtonConfig.setBounds(350, 160, 120, 25);
		panel.add(loginButtonConfig);
		
		
		ActionListener myButtonListener = new LoginListenerTutorial();
		loginButtonConfig.addActionListener(myButtonListener);
	}
	
	

}
