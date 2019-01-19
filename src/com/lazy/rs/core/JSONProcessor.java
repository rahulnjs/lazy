package com.lazy.rs.core;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.lazy.rs.annotation.JsonIgnore;
import com.lazy.rs.util.Util;

/**
 * Helps in converting a Java object to JSON String and vice versa.
 * 
 * @author rahulnjs
 *
 */

public class JSONProcessor {

	private enum OType {
		OBJECT, LIST, MAP, SET, ARRAY, S_A, B_A, I_A, J_A, F_A, D_A, C_A, Z_A  
	}

	/**
	 * 
	 * @param o
	 *            object which is being converted to JSON String.
	 * @return returns JSON String.
	 */

	public String toJSONString(Object o) {
		if (o == null) {
			return "{}";
		}
		Class<?> target = o.getClass();
		Field[] fields = target.getDeclaredFields();
		String json = "{";
		for (Field f : fields) {
			if (!f.isAnnotationPresent(JsonIgnore.class)) {
				if (json.length() != 1) {
					json += ",";
				}
				boolean primitive = isPrimitive(f.getType().getName());
				Object fieldVal = Util.getValueForField(target, o, f);
				if (!primitive) {
					OType cType = getOType(f.getType());
					fieldVal = getSubFieldValue(cType, fieldVal);
					json += "\"" + f.getName() + "\":" + fieldVal;
				} else {
					fieldVal = normalizeForJSON(fieldVal);
					json += "\"" + f.getName() + "\":\"" + fieldVal + "\"";
				}
			}
		}
		return json + "}";
	}

	/**
	 * Gets the object type depending on the type of Class
	 * **/
	private OType getOType(Class<?> type) {
		String name = type.getName();
		if (name.contains("List")) {
			return OType.LIST;
		} else if (name.contains("Map")) {
			return OType.MAP;
		} else if (name.contains("Set")) {
			return OType.SET;
		} else if(name.startsWith("[L")) {
			return OType.ARRAY;
		} else if(name.startsWith("[I")) {
			return OType.I_A;
		}  else if(name.startsWith("[J")) {
			return OType.J_A;
		}  else if(name.startsWith("[S")) {
			return OType.S_A;
		}  else if(name.startsWith("[B")) {
			return OType.B_A;
		}  else if(name.startsWith("[F")) {
			return OType.F_A;
		}  else if(name.startsWith("[D")) {
			return OType.D_A;
		}  else if(name.startsWith("[C")) {
			return OType.C_A;
		}  else if(name.startsWith("[Z")) {
			return OType.Z_A;
		}  else {
			return OType.OBJECT;
		}
	}

	@SuppressWarnings("unchecked")
	private Object getSubFieldValue(OType cType, Object fieldVal) {
		switch (cType) {
		case OBJECT:
			fieldVal = toJSONString(fieldVal);
			break;
		case LIST:
			fieldVal = toJSONArray((List<? extends Object>) fieldVal);
			break;
		case SET:
			fieldVal = toJSONArray((Set<? extends Object>) fieldVal);
		case MAP:
			fieldVal = toJSONObject((Map<? extends Object, ? extends Object>) fieldVal);
		case ARRAY:
			fieldVal = toJSONArray((Object[])fieldVal);
			break;
		case I_A:
			fieldVal = fieldVal == null ? null : toString((int[]) fieldVal);
			break;
		case J_A:
			fieldVal = fieldVal == null ? null : toString((long[]) fieldVal);
			break;
		case S_A:
			fieldVal = fieldVal == null ? null : toString((short[]) fieldVal);
			break;
		case B_A:
			fieldVal = fieldVal == null ? null : toString((byte[]) fieldVal);
			break;
		case C_A:
			fieldVal = fieldVal == null ? null : toString((char[]) fieldVal);
			break;
		case F_A:
			fieldVal = fieldVal == null ? null : toString((float[]) fieldVal);
			break;
		case D_A:
			fieldVal = fieldVal == null ? null : toString((double[]) fieldVal);
			break;
		case Z_A:
			fieldVal = fieldVal == null ? null : toString((boolean[]) fieldVal);
			break;
		default:
			break;
		}
		return fieldVal;
	}
	
	private String toString(int[] arr) {
		String json = "[";
		for(int i : arr) {
			if(json.length() > 1) {
				json += ",";
			}
			json += i;
		}
		return json + "]";
	}
	
	private String toString(long[] arr) {
		String json = "[";
		for(long i : arr) {
			if(json.length() > 1) {
				json += ",";
			}
			json += i;
		}
		return json + "]";
	}
	
	private String toString(byte[] arr) {
		String json = "[";
		for(byte i : arr) {
			if(json.length() > 1) {
				json += ",";
			}
			json += i;
		}
		return json + "]";
	}
	
	private String toString(short[] arr) {
		String json = "[";
		for(short i : arr) {
			if(json.length() > 1) {
				json += ",";
			}
			json += i;
		}
		return json + "]";
	}
	
	private String toString(float[] arr) {
		String json = "[";
		for(float i : arr) {
			if(json.length() > 1) {
				json += ",";
			}
			json += i;
		}
		return json + "]";
	}
	
	private String toString(double[] arr) {
		String json = "[";
		for(double i : arr) {
			if(json.length() > 1) {
				json += ",";
			}
			json += i;
		}
		return json + "]";
	}
	
	private String toString(boolean[] arr) {
		String json = "[";
		for(boolean i : arr) {
			if(json.length() > 1) {
				json += ",";
			}
			json += i;
		}
		return json + "]";
	}
	
	private String toString(char[] arr) {
		String json = "[";
		for(char i : arr) {
			if(json.length() > 1) {
				json += ",";
			}
			json += "\"" + i + "\"";
		}
		return json + "]";
	}
	
	/**
	 * @param rs
	 *            ResultSet to read from
	 * @return JSON representation of ResultSet e.g. if ResultSet contains a row
	 *         [first_name, last_name with values java, language] then returned
	 *         JSON would be: {"first_name": "java", "last_name": "language"}
	 *         
	 * @note call rs.next() before calling this method        
	 * 
	 * */
	public String resultSet2JSONString(ResultSet rs) {
		String json = "{";
		try {
			ResultSetMetaData meta = rs.getMetaData();
			for (int i = 1; i <= meta.getColumnCount(); i++) {
				if (json.length() != 1) {
					json += ",";
				}
				String value = rs.getString(meta.getColumnLabel(i));
				json += "\"" + meta.getColumnLabel(i).toLowerCase() + "\":\""
						+ normalizeForJSON(value) + "\"";
			}
		} catch (Exception e) {
			e.printStackTrace();
			//System.out.println(e);
			return null;
		}
		return json + "}";
	}

	/**
	 * @param rs
	 *            ResultSet to read from
	 * @return JSON representation of ResultSet e.g. if ResultSet contains two
	 *         rows [first_name, last_name with values (java, language) and
	 *         (json, object)] then returned JSON would be: [{"first_name":
	 *         "java", "last_name": "language"}, {"first_name": "json",
	 *         "last_name": "object"}]
	 * 
	 * */
	public String toJSONArray(ResultSet rs) {
		String jArray = "[";
		try {
			while (rs.next()) {
				if (jArray.length() > 1) {
					jArray += ", ";
				}
				jArray += resultSet2JSONString(rs);
			}
		} catch (Exception e) {
			System.out.println(e);

		}
		return jArray + "]";
	}

	/***
	 * @param: list, list of object to be converted to JSON
	 * @return: JSON array for the input
	 * */

	public String toJSONArray(List<? extends Object> list) {
		return list == null ? "[]" : toJSONArray(list.toArray());
	}
	
	/***
	 * @param: arr, array of object to be converted to JSON
	 * @return: JSON array for the input
	 * */
	public String toJSONArray(Object[] arr) {
		if (arr == null) {
			return "[]";
		}
		String jArray = "[";
		
		for (Object obj : arr) {
			if (jArray.length() > 1) {
				jArray += ", ";
			}
			Object val = obj;
			Class<?> cls = val.getClass();
			boolean primitive = isPrimitive(cls.getName());
			if (!primitive) {
				OType cType = getOType(cls);
				val = getSubFieldValue(cType, val);
				jArray += val;
			} else {
				val = normalizeForJSON(val);
				jArray += "\"" + val + "\"";
			}

		}
		return jArray + "]";
	}

	/***
	 * @param: Set, set of object to be converted to JSON
	 * @return: JSON array for the input
	 * */

	public String toJSONArray(Set<? extends Object> set) {
		return set == null ? "[]" : toJSONArray(set.toArray());
	}

	/***
	 * @param: Map, map to be converted to JSON
	 * @return: JSON String for the input
	 * */
	public String toJSONObject(Map<? extends Object, ? extends Object> map) {
		if (map == null) {
			return "{}";
		}
		String json = "{";
		Set<? extends Object> keySet = map.keySet();
		for (Object key : keySet) {
			if (json.length() > 1) {
				json += ", ";
			}
			Object val = map.get(key);
			Class<?> cls = val.getClass();
			boolean primitive = isPrimitive(cls.getName());
			if (!primitive) {
				OType cType = getOType(cls);
				val = getSubFieldValue(cType, val);
				json += "\"" + key + "\":" + val;
			} else {
				val = normalizeForJSON(val);
				json += "\"" + key + "\":\"" + val + "\"";
			}
		}
		return json + "}";
	}

	/**
	 * 
	 * @param jsonString
	 * @param targetClass
	 * @return object created by parsing json string
	 */
	public Object toObject(String jsonString, Class<?> targetClass) {
		String[] values = jsonString.replace("\"", "").split(",");
		Map<String, String[]> map = new HashMap<String, String[]>();
		// FormProcessor form = new FormProcessor();
		for (String keyVal : values) {
			String[] parts = keyVal.split(":");
			map.put(parts[0], new String[] { parts[1] });
		}

		return null;// form.toBean(map, targetClass);
	}

	private String normalizeForJSON(Object val) {
		String normalizedString = null;
		if (val != null) {
			normalizedString = val.toString().replaceAll("\\\\", "\\\\\\\\") // Escapes
																				// backward
																				// slash
					.replaceAll("\"", "\\\\\"") // Escapes double quote -> "
					.replaceAll("\n", "\\\\\\n") // Escapes new line
					.replaceAll("\r", "\\\\\\r") // Escapes carriage return
					.replaceAll("\t", "\\\\\\t") // Escapes tab
					.replaceAll("\f", "\\\\\\f") // Escapes form feed
					.replaceAll("\b", "\\\\\\b"); // Escapes backspace
		}
		return normalizedString;
	}

	private boolean isPrimitive(String type) {
		String primitive = "boolean_string_integer_float_double_long_short_byte_char";
		type = type.substring(type.lastIndexOf('.') + 1).toLowerCase();
		return primitive.contains(type);
	}

}
