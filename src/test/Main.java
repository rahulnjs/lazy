package test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;

import com.lazy.rs.core.JSONProcessor;
import com.lazy.rs.core.Lazy;

public class Main {
	public static void main(String[] args) throws Throwable {
		Class.forName("com.mysql.jdbc.Driver");
		Connection c = DriverManager.getConnection("jdbc:mysql://139.59.91.106:3306/rahuldb?useSSL=false", "rahul", "root");
		//System.out.println(c.createStatement().executeUpdate("insert into _tab_ values('rahul')"));
		ResultSet rs = c.createStatement().executeQuery("select n as nan from _tab_");
		JSONProcessor jp = new JSONProcessor();
		System.out.println(jp.toJSONArray(rs));
		/*while(rs.next()) {
			System.out.println(rs.getString(1));
		}*/
		c.close();
		
	}
}
