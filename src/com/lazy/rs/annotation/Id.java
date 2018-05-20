package com.lazy.rs.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Indicates the primary field equivalent of the Java Bean. 
 * @author rahulnjs
 *
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Id {
	/**
	 * @return name of the sequence for the field which is being declared as @Id 
	 */
	String sequence();
	
	/**
	 * 
	 * @return a string which is being added after the generated key.
	 */
	String suffix();
	
	/**
	 * 
	 * @return a string which is being added before the generated key.
	 */
	String prefix();
	
	/**
	 * 
	 * @return the key type. Either ASSIGNED or SEQUENCE
	 * @see com.lazy.rs.annotation.KeyType
	 */
	KeyType keyType();
}
