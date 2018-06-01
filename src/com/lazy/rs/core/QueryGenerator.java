package com.lazy.rs.core;

import java.lang.reflect.Field;
import java.util.Hashtable;
import java.util.Map;

import com.lazy.rs.annotation.Id;
import com.lazy.rs.annotation.Ignore;
import com.lazy.rs.annotation.Table;
import com.lazy.rs.util.DBVendor;
import com.lazy.rs.util.Metadata;

/**
 * Helps in creating queries and populating prepared statements.
 * 
 * @author rahulnjs
 *
 */

public class QueryGenerator {

	public static Map<String, Metadata> cache = new Hashtable<String, Metadata>();
	private DBVendor vendor;
	private static QueryGenerator qg;

	private QueryGenerator(DBVendor vendor) {
		this.vendor = vendor;
	}

	public static QueryGenerator getQG(DBVendor vendor) {
		if (qg == null) {
			qg = new QueryGenerator(vendor);
		}
		return qg;
	}

	/**
	 * Gets the Insert query for PreparedStatement for the class represented by
	 * targetClass.
	 * 
	 * @param targetClass
	 *            class for which the insert query to be created.
	 * @return Insert query for targetClass.
	 */
	public String getInsertQuery(Class<?> targetClass) {
		return query(targetClass, QueryType.INSERT);
	}

	/**
	 * Gets the Update query for PreparedStatement for the class represented by
	 * targetClass.
	 * 
	 * @param targetClass
	 *            class for which the update query to be created.
	 * @return Update query for targetClass.
	 */
	public String getUpdateQuery(Class<?> targetClass) {
		return query(targetClass, QueryType.UPDATE);
	}

	private String query(Class<?> targetClass, QueryType type) {
		Metadata meta = cache.get(targetClass.getName());
		if (meta == null) {
			meta = generateMetadata(targetClass);
			cache.put(targetClass.getName(), meta);
		}
		return type == QueryType.INSERT ? meta.getInsertQuery() : meta
				.getUpdateQuery();

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
		for (Field f : fields) {
			if (f.getAnnotation(Id.class) != null) {
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
		for (int i = 0; i < fields.length; i++) {
			if (!hasIgnoreAnnotation(fields[i])) {
				if (!fields[i].getName().equals(meta.getPrimaryKey())) {
					qPart1 += fields[i].getName().toUpperCase();
					qPart2 += "?";
					if (i != fields.length - 1) {
						qPart1 += ", ";
						qPart2 += ", ";
					}
				} else {
					idAt = i;
				}
				if (i == fields.length - 1) {
					if (idAt != -1 && vendor != DBVendor.MYSQL) {
						qPart1 += ", " + fields[idAt].getName().toUpperCase();
						qPart2 += ", ?";
					}
					qPart1 += ")";
					qPart2 += ")";
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
			for (Field f : flds) {
				if (!hasIgnoreAnnotation(f)) {
					if (!f.equals(fl)) {
						if (i != 0) {
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

	private String getTableName(Class<?> c) {
		return c.getAnnotation(Table.class).value();
	}

}
