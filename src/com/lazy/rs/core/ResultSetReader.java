package com.lazy.rs.core;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.ResultSet;

import com.lazy.rs.annotation.Ignore;
import com.lazy.rs.util.Util;

/**
 * Helps in converting a result set to a Java object.
 * 
 * @author rahulnjs
 *
 */

public class ResultSetReader {

	/**
	 * 
	 * @param rs
	 *            ResultSet to read from
	 * @param beanClass
	 *            type of Object to return
	 * @return object created using beanClass and populated from rs.
	 */
	public Object toBean(ResultSet rs, Class<?> beanClass) {
		try {
			Object targetObj = beanClass.newInstance();
			Field[] fields = beanClass.getDeclaredFields();
			for (Field field : fields) {
				if (field.getAnnotation(Ignore.class) != null) {
					continue;
				}
				Method method = beanClass.getMethod(
						Util.setterName(field.getName()), field.getType());
				String fieldvalue = rs.getString(field.getName().toUpperCase());
				if (fieldvalue != null) {
					method.invoke(
							targetObj,
							Util.getParsedValue(fieldvalue, field.getType()
									.getName().toLowerCase()));
				}
			}
			return targetObj;
		} catch (Exception exp) {
			System.out.println(exp);
		}
		return null;
	}

}
