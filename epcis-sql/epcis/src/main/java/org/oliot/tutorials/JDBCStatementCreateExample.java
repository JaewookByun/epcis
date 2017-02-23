package org.oliot.tutorials;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class JDBCStatementCreateExample {

	private static final String DB_DRIVER = "oracle.jdbc.driver.OracleDriver";
	private static final String DB_CONNECTION = "jdbc:oracle:thin:@143.248.57.21:1521:orcl";
	private static final String DB_USER = "c##yale";
	private static final String DB_PASSWORD = "yale";

	public static void main(String[] argv) {

		try {
			
			System.out.println("Start!");

			//createDbUserTable();
			selectfromDbUserTable();

		} catch (SQLException e) {

			System.out.println(e.getMessage());

		}

	}
	
	private static  void selectfromDbUserTable()throws SQLException{
		Connection dbConnection = null;
		Statement statement = null;
		String createTableSQL = "select * from  DBUSER ";
		
		
		try {
			dbConnection = getDBConnection();
			statement = dbConnection.createStatement();


			System.out.println(createTableSQL);
                        // execute the SQL stetement
			ResultSet rs=statement.executeQuery(createTableSQL);
			
			while(rs.next()){
				String userid=rs.getString("USER_ID");
				System.out.println("USER_ID :"+userid);
			}


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

	@SuppressWarnings("unused")
	private static void createDbUserTable() throws SQLException {

		Connection dbConnection = null;
		Statement statement = null;

		String createTableSQL = "CREATE TABLE DBUSER("
				+ "USER_ID NUMBER(5) NOT NULL, "
				+ "USERNAME VARCHAR(20) NOT NULL, "
				+ "CREATED_BY VARCHAR(20) NOT NULL, "
				+ "CREATED_DATE DATE NOT NULL, " + "PRIMARY KEY (USER_ID) "
				+ ")";

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

}