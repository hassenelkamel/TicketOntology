package edu.fiu.cs.kdrg.tkrec.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnection {

	
	private static String dbConfigureFile = "conf/db.conf";
	
	private static Properties prop = null;
	
	
	static{
		try {
			prop = loadDBConfigureProperty();
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	
	/**
	 * load property file into memory
	 * @throws FileNotFoundException 
	 */
	private static Properties loadDBConfigureProperty() throws IOException{
		
		Properties prop = new Properties();
		FileInputStream in = new FileInputStream(dbConfigureFile);
		prop.load(in);
		in.close();
		return prop;
	}
	
	public static Connection connect(){
		String defaultSchema = "ticket";
		return DBConnection.connect(defaultSchema);
	}
	
	
	
	/**
	 * @param schema: the schema connect to
	 * @return connection to this schema
	 */
	public static Connection connect(String schema){
		
		try {
			System.out.print(prop.getProperty("mysqlDriverName"));
			Class.forName(prop.getProperty("mysqlDriverName"));
		} catch (Exception ex) {
			System.out.println(prop.getProperty("mysqlDriverName") + " WTF");
			return null;
		}

		try {
			String urlTemplate = prop.getProperty("DBUrlTemplate");
			System.out.println(String.format(urlTemplate, schema));
			return DriverManager.getConnection(String.format(urlTemplate, schema), prop.getProperty("userName"),
					prop.getProperty("password"));
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		
	}

}
