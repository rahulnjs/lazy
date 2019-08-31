# lazyapi

A light weight Java API for ORM, JSON and much more...

*   [About](#about)
*   [Getting Started](#getting-started)
*   [Examples](#examples)
    *   [Initialization](#exmp-init)
    *   [Bean Persistence](#exmp-sbp)
    *   [Select Queries](#exmp-select)
    *   [ResultSetReader](#exmp-rsr)
    *   [JSONProcessor](#exmp-jp)
    *   [FormProcessor](#exmp-fp)

## About

Lazy is a light weight Java API to enable developers to do ORM, Object to JSON, HTML Form to Object and so on with very less code. The motivation behind Lazy was to minimize configuration and not to force developers to study and learn a new framework. Lazy has just few rules that one needs to follow while creating relational databases and JavaBeans which are actually JavaBeans standards. Lazy is not that matured to be used in enterprise application as a framework but it can be used alongside. But Lazy can be very useful for new developers and students.

## Getting Started

To use Lazy download the latest jar and add it to your classpath.

### Examples > Initialization

Lazy can be initialized in two ways,

*   With java.sql.DataSource  
    
        DataSource ds = getSomeDataSource();
        Lazy lazy = new Lazy(ds);
        
    
*   With java.sql.Connection  
    
        Connection con = getConnection();
        Lazy lazy = new Lazy(con);
        
    

Lazy curently supports Oracle and MySql databases. It automatically determines Database Vendor from connection object. So you don't need to make any particular changes for Lazy if you switch your db. Lazy can be found in package **com.lazy.rs.core**

### Examples > Simple Bean Persistence

Lazy only supports Annotation configuration. To make a JavaBean persistable annotate your class with @Table.

    @Table("orm_tab_a")
    public class OrmTabMS {
    
      /**
      * @Id annotation defines the id field
      **/
      @Id(keyType=KeyType.NATIVE, prefix="", sequence="", suffix="")
      private int tabId;
      private String userName;
      private String pass;
    
      public String getUserName() {
    	  return userName;
      }
    
      // ....
      // ....
      // ....
      // other setters and getters
    
    }
    

Saving this bean to the Database is fairly simple.

    OrmTabMS test = new OrmTabMS();
    test.setUserName("Obj2");
    test.setPass("obj-pass");
    Lazy lazy = new Lazy(con);
    System.out.println("Status: " + lazy.insert(test));
    System.out.println("Id: "+ test.getTabId());
    
    
    //Status: 1
    //Id: 1
    

Similarly lazy.update(test) will update the value in the database.

Memeber variables decalred with @Ignore annotation will not be persisted to database.

    @Id(keyType=KeyType.NATIVE, prefix="", sequence="", suffix="")
    private int tabId;
    private String userName;
    private String pass;
    @Ignore("will be ignored during persistence")
    private String foo;
    

### Examples > Select Queries

Only simple objects can be retrieved from database.

    //Returns list of objects.
    lazy.selectMany("select * from orm_tab_a", OrmTabMS.class)
    
    
    //Returns first object
    lazy.selectOne("select * from orm_tab_a", OrmTabMS.class)
    

### Examples > ResultSetReader

With Simple JDBC we do something like,

    Connection con = getConnection();
    Statement stmt = con.createStatement();
    ResultSet rs = stmt.executeQuery("select *from orm_tab_a");
    List<OrmTabMS> list = new ArrayList<OrmTabMS>();
    while(rs.next()) {
      OrmTabMS orm = new OrmTabMS();
    	orm.setPass(rs.getString("pass"));
    	orm.setTabId(rs.getInt(1));
    	orm.setUserName(rs.getString("userName"));
    	list.add(orm);
    }

With Lazy we can get rid of all the boiler plate code,

    ResultSetReader rsr = new ResultSetReader();
    while(rs.next()) {
    	list.add((OrmTabMS)rsr.toBean(rs, OrmTabMS.class));
    }

### Examples > JSONProcessor

JSONProcessor class helps in converting Java objects to JSON strings.

    OrmTabMS orm = new OrmTabMS();
    orm.setPass("xyzpqr");
    orm.setTabId(98);
    orm.setUserName("lazyapi");
    JSONProcessor jp = new JSONProcessor();
    System.out.println(jp.toJSONString(orm));
    
    
    // {"tabId":"98","userName":"lazyapi","pass":"xyzpqr"}
    

Variables annoted with @JSONIgnore are ignored during conversion.

You can also convert java.sql.ResultSet to JSON directly.

    JSONProcessor jp = new JSONProcessor();
    Connection con = getConnection();
    Statement stmt = con.createStatement();
    ResultSet rs = stmt.executeQuery("select *from orm_tab_a");
    System.out.println(jp.toJSONArray(rs));
    
    
    // [{"tabid":"98","username":"lazyapi","pass":"xyzpqr"}, {...}, {...}]
      

In this case the json property names are determined from java.sql.ResultSetMetaData and are lowercased.

### Examples > FormProcessor

Developers still using Java Servlets, tend to write a lot of code that converts HTML form data to Java Bean. With Lazy that can also be minimized.

    protected void doGetOrPost(HttpServletRequest req, HttpServletResponse) {
      FormProcessor fp = new FormProcessor();
      OrmTabMS orm = fp.toBean(req.getParameterMap(), OrmTabMS.class);
    }

Developers are required to send req.getParameterMap() instead of req itself because that would make Lazy dependent on servlet-api.
