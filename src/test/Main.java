package test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.lazy.rs.processor.JSONProcessor;

public class Main {

	public static void main(String[] args) {
		Connection con = getDBConnection();
		
		
	}
	
	private static Connection getDBConnection() {
		Connection con = null;
		try {
			Class.forName("oracle.jdbc.OracleDriver");
			con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:xe",
					"system", "root");
			con.setAutoCommit(false);
		} catch(Exception e) {
			System.out.println(e);
			e.printStackTrace();
		}
		return con;
	}

}





/*MyBean mb = new MyBean();
mb.setIn(5);
JSONProcessor jp = new JSONProcessor();
System.out.println(jp.toJSONString(mb));

List<MyBean2> list = new ArrayList<MyBean2>();
List<MyBean3> sl = new ArrayList<MyBean3>();
MyBean3 m3 = new MyBean3();
m3.setBn("abc");
MyBean3 m4 = new MyBean3();
m4.setBn("abcd");
sl.add(m3);
sl.add(m4);
MyBean2 b1 = new MyBean2();
b1.setName("Ai");
b1.setAge(22);
b1.setList(sl);
MyBean2 b2 = new MyBean2();
b2.setAge(23);
b2.setName("Sam");
b2.setList(sl);
list.add(b1);
list.add(b2);
MyBean b = new MyBean();
b.setB2(b1);
b.setC('r');
b.setB2List(null);
JSONProcessor jp = new JSONProcessor();
System.out.println(jp.toJSONString(b));
Map<String, MyBean> map = new HashMap<String, MyBean>();
map.put("1", b);
map.put("2", b);
map.put("3", b);
//System.out.println(jp.toJSONObject(map));
Map<String, String> smap = new HashMap<String, String>();
smap.put("1", "One");
smap.put("2", "One");
smap.put("3", "One");
smap.put("4", "One");
//System.out.println(jp.toJSONObject(smap));
Map<Integer, List<MyBean2>> lmap = new HashMap<Integer, List<MyBean2>>();
lmap.put(1, list);
//System.out.println(jp.toJSONObject(lmap));
//System.out.println(jp.toJSONArray(list));*/

