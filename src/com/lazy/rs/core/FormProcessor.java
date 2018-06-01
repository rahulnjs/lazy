package com.lazy.rs.core;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

import com.lazy.rs.util.Util;

/**
 * Helps in converting an HTML form to Java Bean
 * 
 * @author rahulnjs
 *
 */

public class FormProcessor {


	/**
	 * Creates a bean for an HTML form which is being submitted.
	 * 
	 * @param map
	 *            represents
	 *            javax.servlet.http.HttpServletRequest.getParameterMap()
	 * @param beanClass
	 *            The type of object that is to be returned.
	 * @return Object of type beanClass.
	 */
	public Object toBean(Map<String, String[]> map, Class<?> beanClass) {
		try {
			Object targetObj = beanClass.newInstance();
			Field[] fields = beanClass.getDeclaredFields();
			for (Field field : fields) {
				Method method = beanClass.getMethod(
						Util.setterName(field.getName()), field.getType());
				String[] fieldvalue = map.get(field.getName());
				if (fieldvalue != null) {
					method.invoke(targetObj, Util.getParsedValue(
							normalizeArray(fieldvalue), field.getType()
									.getName()));
				}
			}
			return targetObj;
		} catch (Exception exp) {
			System.out.println(exp);
		}
		return null;
	}

	private String normalizeArray(String[] arr) {
		String noramlized = "";
		for (int i = 0; i < arr.length; i++) {
			if (i != 0) {
				noramlized += ",";
			}
			noramlized += arr[i];

		}
		return noramlized;
	}

}
