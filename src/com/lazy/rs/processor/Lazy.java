package com.lazy.rs.processor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.lazy.rs.exception.UnresolvedDBVendorException;
import com.lazy.rs.util.DBVendor;


public class Lazy {
	
	private QueryGenerator qg;
	private Connection con;
	private DataSource ds;
	
	private boolean hasDS = false;
	
	
	public Lazy(Connection con) throws SQLException {
		this.con = con;
		try {
			qg = QueryGenerator.getQG(whichVendor(con));
		} catch (UnresolvedDBVendorException e) {
			System.out.println(e);
		} 
	}

	public Lazy(DataSource ds) throws SQLException {
		this(ds.getConnection());
		this.ds = ds;
		this.hasDS = true;
		
	}
	
	private DBVendor whichVendor(Connection con) 
			throws SQLException, UnresolvedDBVendorException {
		String db = con.getMetaData().getDatabaseProductName().toUpperCase();
		if(db.equals(DBVendor.ORACLE.toString())) {
			return DBVendor.ORACLE;
		} else if(db.equals(DBVendor.MYSQL.toString())) {
			return DBVendor.MYSQL;
		} else {
			throw new UnresolvedDBVendorException();
		}
	}
	
	
	private Connection _con() throws SQLException {
		return hasDS ? ds.getConnection() : con;
	}
	
	public int save(Object o) throws SQLException {
		PreparedStatement ps = _con().prepareStatement(qg.getInsertQuery(o.getClass()));
		qg.doPopulate(ps, o, QueryType.INSERT);
		return ps.executeUpdate();
	}
	
	public int update(Object o) throws SQLException {
		PreparedStatement ps = _con().prepareStatement(qg.getUpdateQuery(o.getClass()));
		qg.doPopulate(ps, o, QueryType.UPDATE);
		return ps.executeUpdate();
	}
	
	
}


