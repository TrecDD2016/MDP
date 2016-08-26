package db;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class DBUtility {
	
    static String driver = "com.mysql.jdbc.Driver";
  
    static String url = "jdbc:mysql://127.0.0.1:3306/mdp?characterEncoding=utf-8";

    static String user = "root";

    static String password = "";
    
    static Connection conn;
    
    static{
    	try { 
            // 加载驱动程序
            Class.forName(driver);
            conn = DriverManager.getConnection(url, user, password);
            if(!conn.isClosed()) 
             System.out.println("Sucsceeded connecting to the Database!");
    	} catch(ClassNotFoundException e) {
            System.out.println("Sorry,can`t find the Driver!"); 
            e.printStackTrace();
           } catch(SQLException e) {
            e.printStackTrace();
           } catch(Exception e) {
            e.printStackTrace();
           } 
    }

    public static void executeInsert(String sql) {
    	try {
    		Statement stmt = conn.createStatement();
    		stmt.executeUpdate(sql);
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }

    public static void executeConsoleCommand(String command){
		try {
			Runtime.getRuntime().exec(new String[]{"/bin/sh","-c",command});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
    
    public static void executeSetting() {
    	try {
    		Statement stmt = conn.createStatement();
    		stmt.executeUpdate("set collation_connection='utf8_unicode_ci'");
    		stmt.executeUpdate("set collation_database='utf8_unicode_ci'");
    		stmt.executeUpdate("set collation_server='utf8_unicode_ci'");
    		
    		stmt.executeUpdate("set character_set_client='utf8'");
    		stmt.executeUpdate("set character_set_connection='utf8'");
    		stmt.executeUpdate("set character_set_database='utf8'");
    		stmt.executeUpdate("set character_set_results='utf8'");
    		stmt.executeUpdate("set character_set_server='utf8'");
			stmt.close();
    	}catch(SQLException e ) {
    		e.printStackTrace();
    	}
    }

	public static void executeTruncate2() {
		try {
			Statement stmt = conn.createStatement();
			stmt.executeUpdate("truncate table dlength");
			stmt.executeUpdate("truncate table tc");
			stmt.executeUpdate("truncate table td");
			stmt.executeUpdate("truncate table path");
			stmt.executeUpdate("truncate table idf");
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void executeTruncateDocs() {
		try {
			Statement stmt = conn.createStatement();
			stmt.executeUpdate("truncate table docs");
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

    
    public static void executeTruncate() {
    	try {
    		Statement stmt = conn.createStatement();
    		stmt.executeUpdate("truncate table pstd");
    		stmt.executeUpdate("truncate table pustd");
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }
    
    public static PreparedStatement getPreparedStatement(String sql) {
    	try {
			return conn.prepareStatement(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
    	return null;
    }
}
