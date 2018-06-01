package com.lazy.rs.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.ParseException;

/**
 * @author rahulnjs
 *
 */

public class Util {

	public static String setterName(String name) {
		return "set" + restPart(name);
	}

	public static String getterName(String name, String type) {
		return (type.indexOf("oo") != -1 ? "is" : "get") + restPart(name);
	}

	public static String restPart(String name) {
		return name.substring(0, 1).toUpperCase() + name.substring(1);
	}

	public static Object getParsedValue(String value, String type)
			throws ParseException, NumberFormatException {
		if (type.endsWith("boolean")) {
			return Boolean.parseBoolean(value);
		} else if (type.endsWith("double")) {
			return Double.parseDouble(value);
		} else if (type.endsWith("int") || type.endsWith("integer")) {
			return Integer.parseInt(value);
		} else if (type.endsWith("char")) {
			return new Character(value.charAt(0));
		} else if (type.endsWith("long")) {
			return Long.parseLong(value);
		} else {
			return value;
		}
	}

	public static Object getValueForField(Class<?> targetClass, Object obj,
			Field f) {
		try {
			Method meth = targetClass.getMethod(
					Util.getterName(f.getName(), f.getType().getName()),
					new Class[] {});
			return meth.invoke(obj, new Object[] {});
		} catch (Exception e) {
			System.out.println(e);
			return null;
		}

	}

}
