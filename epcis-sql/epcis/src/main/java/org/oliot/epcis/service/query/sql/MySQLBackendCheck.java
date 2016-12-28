package org.oliot.epcis.service.query.sql;
//package org.oliot.epcis.service.query.mysql;
//
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.SQLException;
//import java.sql.Statement;
//
//
//
//public class MySQLBackendCheck {
//	
//	
//	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver"; 
//    static final String DB_URL = "jdbc:mysql://localhost/epcis";
//    static final String DB_URL2 = "jdbc:mysql://localhost/";
//
//    //  Database credentials
//    static final String USER = "root";
//    static final String PASS = "root";
//
//    
//    public void createDatabaseIfNotExist(){
//        
//        Connection conn = null;
//        Statement stmt = null;
//     try{
//           Class.forName("com.mysql.jdbc.Driver");
//           conn = DriverManager.getConnection(DB_URL2,USER,PASS);
//           stmt = conn.createStatement();
//           String sql;
//           sql = "CREATE DATABASE IF NOT EXISTS epcis";
//           stmt.executeUpdate(sql);
//           stmt.close();
//           conn.close();
//        }catch(SQLException se){
//           se.printStackTrace();
//        }catch(Exception e){
//           e.printStackTrace();
//        }finally{
//           try{
//              if(stmt!=null)
//                 stmt.close();
//           }catch(SQLException se2){
//           }
//           try{
//              if(conn!=null)
//                 conn.close();
//           }catch(SQLException se){
//              se.printStackTrace();
//           }
//        }
//       
//    }
//    
//    
//}
