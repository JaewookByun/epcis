package org.oliot.epcis.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DropTables {
	
	private static final String DB_DRIVER = "oracle.jdbc.driver.OracleDriver";
	private static final String DB_CONNECTION = "jdbc:oracle:thin:@localhost:1521:orcl";
	private static final String DB_USER = "c##yale";//c##yale
	private static final String DB_PASSWORD = "yale";//yale

	public static void main(String[] argv) {

		try {

			dropAll(getStatmentList());
			dropAll(getStatmentList());

		} catch (SQLException e) {

			System.out.println(e.getMessage());

		}

	}
	
	public static void dropAll(List<String> statmentList)throws SQLException{
		
		for(int i=statmentList.size()-1; i>=0; i--){
			Connection dbConnection = null;
			Statement statement = null;

			String createTableSQL = "Drop table "+statmentList.get(i);

			try {
				dbConnection = getDBConnection();
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
	
	private static Connection getDBConnection() {

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
	
	
	public static List<String> getStatmentList(){
		List<String> statmentList = new ArrayList<String>();
		statmentList.add("dbuser");
		statmentList.add("extensionmaps ");
		statmentList.add("epc");
		statmentList.add("epcisevent"); 
		statmentList.add("attribute"); 
		statmentList.add("aggregationeventepcs"); 
		statmentList.add("aggregationeventepcs_epc"); 
		statmentList.add("aggregationeventextension2"); 
		statmentList.add("aggregationeventextension"); 
		statmentList.add("businesslocationextension ");
		statmentList.add("businesslocation ");
		statmentList.add("businesstransaction ");
		statmentList.add("businesstransactionlist ");
		
		statmentList.add("correctiveeventids ");
		statmentList.add("correctiveeventid ");
		statmentList.add("correctiveids_correctiveid ");
		statmentList.add("sourcedest ");
		statmentList.add("sourcelist ");
		statmentList.add("sourcelist_sourcedest ");
		statmentList.add("destinationlist ");
		statmentList.add("destinationlist_sourcedest ");
		statmentList.add("epciseventextension2 ");
		statmentList.add("epciseventextension"); 
		statmentList.add("epclist ");
		statmentList.add("epclist_epc ");
		statmentList.add("errordeclarationextension ");
		statmentList.add("errordeclaration ");
		statmentList.add("extensionmap ");
		statmentList.add("quantitylist ");
		
		statmentList.add("extensionmaps_extensionmap ");
		statmentList.add("idlist ");
		statmentList.add("idlist_sid ");
		statmentList.add("ilmdextension ");
		statmentList.add("ilmd ");
		statmentList.add("objecteventepcs ");
		statmentList.add("objecteventepcs_epc ");
		statmentList.add("objecteventextension2"); 
		statmentList.add("objecteventextension ");
		statmentList.add("pollparameters ");
		statmentList.add("BusTranList_BusTran ");
		statmentList.add("quantityelement ");
		
		statmentList.add("qelementlist_qelement ");
		statmentList.add("quantityeventextension ");
		statmentList.add("readpointextension ");
		statmentList.add("readpoint ");
		statmentList.add("sensingelement ");
		statmentList.add("sensinglist ");
		statmentList.add("sensinglist_sensingelement ");
		statmentList.add("sensoreventextension ");
		statmentList.add("subscription ");
		statmentList.add("transactioneventepcs ");
		statmentList.add("transactioneventepcs_epc ");
		statmentList.add("transactioneventextension2 ");
		statmentList.add("transactioneventextension ");
		statmentList.add("transformationeventextension ");
		statmentList.add("vocabularyelementextension ");
		statmentList.add("vocabularyelement ");
		statmentList.add("vocabularyelement_attribute ");
		statmentList.add("vocabularyelementlist ");
		statmentList.add("vocabularyextension ");
		statmentList.add("vocelementlist_vocelement ");
		statmentList.add("vocabulary ");
		statmentList.add("aggregationevent ");
		statmentList.add("transformationevent ");
		statmentList.add("objectevent ");
		statmentList.add("sensorevent ");
		statmentList.add("quantityevent ");
		statmentList.add("transactionevent ");
		statmentList.add("idlist_childid");
		statmentList.add("childid");
		return statmentList;
	}

}
