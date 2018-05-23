package com.lazy.rs.processor;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Map;

import com.lazy.rs.annotation.Id;
import com.lazy.rs.annotation.Ignore;
import com.lazy.rs.annotation.KeyType;
import com.lazy.rs.annotation.Table;
import com.lazy.rs.util.Metadata;
import com.lazy.rs.util.Util;

/**
 * Helps in creating queries and populating prepared statements.
 * @author rahulnjs
 *
 */


public class QueryGenerator {

	private static Map<String, Metadata> cache = new Hashtable<String, Metadata>();

	/**
	 * Gets the Insert query for PreparedStatement for the class represented by targetClass.
	 * @param targetClass class for which the insert query to be created.
	 * @return Insert query for targetClass.
	 */
	public String getInsertQuery(Class<?> targetClass) {
		return query(targetClass, QueryType.INSERT);
	}

	/**
	 * Gets the Update query for PreparedStatement for the class represented by targetClass.
	 * @param targetClass class for which the update query to be created.
	 * @return Update query for targetClass.
	 */
	public String getUpdateQuery(Class<?> targetClass) {
		return query(targetClass, QueryType.UPDATE);
	}

	/**
	 * Returns generated metadata for class.
	 * @param className the full qualified name of the class
	 * @return metadata, for the given class.
	 * 		   null, if metadata is not generated yet.
	 *
	 */
	public Metadata getMetadata(String className) {
		return cache.get(className);
	}

	private String query(Class<?> targetClass, QueryType type) {
		Metadata meta = cache.get(targetClass.getName());
		if(meta == null) {
			meta = generateMetadata(targetClass);
			cache.put(targetClass.getName(), meta);
		}
		return type == QueryType.INSERT ? meta.getInsertQuery() : meta.getUpdateQuery();

	}

	private Metadata generateMetadata(Class<?> targetClass) {
		Metadata meta = new Metadata();
		meta.setTableName(getTableName(targetClass));
		try {
			String pk = getIdForThisClass(targetClass);
			Field fl = targetClass.getDeclaredField(pk);
			Id id = fl.getAnnotation(Id.class);
			meta.setPrefix(id.prefix());
			meta.setKeyType(id.keyType());
			meta.setSuffix(id.suffix());
			meta.setSequnce(id.sequence());
			meta.setPrimaryKey(pk);
		} catch (Exception e) {

		}
		generateInsertQuery(targetClass, meta);
		generateUpdateQuery(targetClass, meta);
		System.out.println(meta);
		return meta;
	}


	private String getIdForThisClass(Class<?> targetClass) {
		Field[] fields = targetClass.getDeclaredFields();
		for(Field f : fields) {
			if(f.getAnnotation(Id.class) != null) {
				return f.getName();
			}
		}
		return null;

	}

	private void generateInsertQuery(Class<?> targetClass, Metadata meta) {
		Field[] fields = targetClass.getDeclaredFields();
		String qPart1 = "INSERT INTO " + meta.getTableName() + "(";
		String qPart2 = " VALUES(";
		int idAt = -1;
		for(int i = 0; i < fields.length; i++) {
			if(!hasIgnoreAnnotation(fields[i])) {
				if(!fields[i].getName().equals(meta.getPrimaryKey())) {
					qPart1 += fields[i].getName().toUpperCase();
					if(i != fields.length - 1) {
						qPart1 += ", ";
					}
				} else {
					idAt = i;
				}
				qPart2 += "?";
				if(i != fields.length - 1) {
					qPart2 += ", ";
				} else {
					if(idAt != -1) {
						qPart1 += ", " + fields[idAt].getName().toUpperCase();
					}
					qPart1 += ")"; qPart2 += ")";
				}
			}

		}
		meta.setInsertQuery(qPart1 + qPart2);
	}

	private void generateUpdateQuery(Class<?> targetClass, Metadata meta) {
		String q = null;
		try {
			Field[] flds = targetClass.getDeclaredFields();
			Field fl = targetClass.getDeclaredField(meta.getPrimaryKey());
			q = "UPDATE " + meta.getTableName() + " SET ";
			int i = 0;
			for(Field f : flds) {
				if(!hasIgnoreAnnotation(f)) {
					if(!f.equals(fl)) {
						if(i != 0) {
							q += ", ";
						}
						q += f.getName().toUpperCase() + "=?";
						i++;
					}
				}
			}
			q += " WHERE " + fl.getName().toUpperCase() + "=?";
		} catch (Exception e) {
			System.out.println(e);
		}
		meta.setUpdateQuery(q);
	}

	private boolean hasIgnoreAnnotation(Field f) {
		return f.getAnnotation(Ignore.class) != null;
	}

	private  String getTableName(Class<?> c) {
		return c.getAnnotation(Table.class).value();
	}


	/**
	 * Populates the PreparedStatement depending on the obj.
	 * @param ps PreparedStatement to populate.
	 * @param obj Object for which the PreparedStatement is created.
	 * @param type INSERT or UPDATE, used to determine if a new id to be generated
	 * 		  during insertion of an object.
	 * @throws SQLException
	 */
	public void doPopulate(PreparedStatement ps, Object obj, QueryType type) throws SQLException {
		Class<?> targetClass = obj.getClass();
		Field[] fields = targetClass.getDeclaredFields();
		Metadata m = cache.get(obj.getClass().getName());
		int colIndex = 1;
		Object idVal = null;
		Field pkF = null;
		for(Field f : fields) {
			if(!hasIgnoreAnnotation(f)) {
				Object fieldValue = Util.getValueForField(targetClass, obj, f);
				if(!f.getName().equals(m.getPrimaryKey())) {
					ps.setObject(colIndex, fieldValue);
					colIndex++;
				} else {
					idVal = fieldValue;
					pkF = f;
				}
			}
		}
		if(type == QueryType.INSERT && m.getKeyType() == KeyType.SEQUENCE) {
			idVal = m.getPrefix() + getNextValueForSequence(ps.getConnection(), m.getSequnce()) + m.getSuffix();
			if(pkF != null) { //Set the generated id to object.
				try {
					Method meth = targetClass.getMethod(Util.setterName(pkF.getName()), pkF.getType());
					System.out.println("Method for primary key: " + meth);
					if(pkF.getType().getName().equals("int") || pkF.getType().getName().equals("Integer")) {
						idVal = Integer.parseInt((String)idVal);
					}
					try {
						meth.invoke(obj, idVal);
					} catch (IllegalAccessException e) {

					} catch (IllegalArgumentException e) {
						
					} catch (InvocationTargetException e) {

					}

				} catch (NoSuchMethodException e) {
					System.out.println(e);
				}
				catch (SecurityException e) {
					System.out.println(e);
				}
			}
		}
		ps.setObject(colIndex, idVal);
	}

	private String getNextValueForSequence(Connection con, String seqName) {
		String nxtVal = null;
		try {
			ResultSet rs = con.createStatement().executeQuery("select "  + seqName + ".nextval from dual");
			rs.next();
			nxtVal = rs.getString(1);
			rs.close();
		} catch (Exception e) {
			System.out.println(e);
		}
		return nxtVal;
	}





}
