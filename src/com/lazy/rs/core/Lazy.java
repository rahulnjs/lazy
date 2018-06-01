package com.lazy.rs.core;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import com.lazy.rs.annotation.Ignore;
import com.lazy.rs.annotation.KeyType;
import com.lazy.rs.exception.UnresolvedDBVendorException;
import com.lazy.rs.util.DBVendor;
import com.lazy.rs.util.Metadata;
import com.lazy.rs.util.Util;

public class Lazy {

	private QueryGenerator qg;
	private Connection con;
	private DataSource ds;
	private ResultSetReader rsr = new ResultSetReader();
	private DBVendor vendor;

	private boolean hasDS = false;

	public Lazy(Connection con) throws SQLException {
		this.con = con;
		try {
			vendor = getVendor(con);
			qg = QueryGenerator.getQG(vendor);
		} catch (UnresolvedDBVendorException e) {
			System.out.println("");
		}
	}

	public Lazy(DataSource ds) throws SQLException {
		this(ds.getConnection());
		this.ds = ds;
		this.hasDS = true;

	}

	private DBVendor getVendor(Connection con) throws SQLException,
			UnresolvedDBVendorException {
		String db = con.getMetaData().getDatabaseProductName().toUpperCase();
		if (db.equals(DBVendor.ORACLE.toString())) {
			return DBVendor.ORACLE;
		} else if (db.equals(DBVendor.MYSQL.toString())) {
			return DBVendor.MYSQL;
		} else {
			throw new UnresolvedDBVendorException(db
					+ " vendor is not supported by lazy.");
		}
	}

	private Connection _con() throws SQLException {
		return hasDS ? ds.getConnection() : con;
	}

	public int insert(Object o) throws SQLException {
		Connection thisCon = _con();
		int status = executeUpdate(
				thisCon.prepareStatement(qg.getInsertQuery(o.getClass())), o,
				QueryType.INSERT);
		if (vendor == DBVendor.MYSQL) {
			assignMySqlId(o, thisCon);
		}
		return status;
	}

	private void assignMySqlId(Object o, Connection thisCon) {
		Metadata m = QueryGenerator.cache.get(o.getClass().getName());
		try {
			ResultSet rs = thisCon.createStatement().executeQuery(
					"select last_insert_id() as id");
			rs.next();
			Field f = o.getClass().getDeclaredField(m.getPrimaryKey());
			Method mthd = o.getClass().getMethod(
					Util.setterName(m.getPrimaryKey()), f.getType());
			mthd.invoke(o, Util.getParsedValue(rs.getString("id"), f.getType().getName()));
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public int update(Object o) throws SQLException {
		return executeUpdate(
				_con().prepareStatement(qg.getUpdateQuery(o.getClass())), o,
				QueryType.UPDATE);
	}

	public Object selectOne(String query, Class<? extends Object> cls)
			throws SQLException {
		List<Object> list = selectMany(query, cls);
		return list.size() > 0 ? list.get(0) : null;
	}

	public List<Object> selectMany(String query, Class<? extends Object> cls)
			throws SQLException {
		List<Object> list = new ArrayList<Object>();
		ResultSet rs = _con().createStatement().executeQuery(query);
		while (rs.next()) {
			list.add(rsr.toBean(rs, cls));
		}
		return list;
	}

	/**
	 * Populates the PreparedStatement depending on the obj.
	 * 
	 * @param ps
	 *            PreparedStatement to populate.
	 * @param obj
	 *            Object for which the PreparedStatement is created.
	 * @param type
	 *            INSERT or UPDATE, used to determine if a new id to be
	 *            generated during insertion of an object.
	 * @throws SQLException
	 */
	private int executeUpdate(PreparedStatement ps, Object obj, QueryType type)
			throws SQLException {
		Class<?> targetClass = obj.getClass();
		Field[] fields = targetClass.getDeclaredFields();
		Metadata m = QueryGenerator.cache.get(obj.getClass().getName());
		int colIndex = 1;
		Object idVal = null;
		Field pkF = null;
		for (Field f : fields) {
			if (!hasIgnoreAnnotation(f)) {
				Object fieldValue = Util.getValueForField(targetClass, obj, f);
				if (!f.getName().equals(m.getPrimaryKey())) {
					ps.setObject(colIndex, fieldValue);
					colIndex++;
				} else {
					idVal = fieldValue;
					pkF = f;
				}
			}
		}
		if (type == QueryType.INSERT && m.getKeyType() == KeyType.SEQUENCE) {
			ps.setObject(colIndex,
					getIdFromSequence(m, idVal, pkF, targetClass, obj));
		} else if (type == QueryType.UPDATE && colIndex > 0) {
			ps.setObject(colIndex, idVal);
		}
		return ps.executeUpdate();
	}

	private Object getIdFromSequence(Metadata m, Object idVal, Field pkF,
			Class<?> targetClass, Object obj) {
		idVal = m.getPrefix() + getNextVal(m.getSequnce()) + m.getSuffix();
		if (pkF != null) {
			String pkFType = pkF.getType().getName().toLowerCase();
			try {
				Method meth = targetClass.getMethod(
						Util.setterName(pkF.getName()), pkF.getType());
				meth.invoke(obj, Util.getParsedValue((String)idVal, pkFType));
			} catch (Exception e) {
				System.out.println(e);
			}
		}
		return idVal;
	}

	private String getNextVal(String seqName) {
		String nxtVal = null;
		try {
			ResultSet rs = _con().createStatement().executeQuery(
					"select " + seqName + ".nextval from dual");
			rs.next();
			nxtVal = rs.getString(1);
			rs.close();
		} catch (Exception e) {
			System.out.println(e);
		}
		return nxtVal;
	}

	private boolean hasIgnoreAnnotation(Field f) {
		return f.getAnnotation(Ignore.class) != null;
	}

}
