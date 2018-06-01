package com.lazy.rs.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Fields declared with @Ignore annotations are ignored during json parsing.
 * 
 * @author rahulnjs
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface JsonIgnore {
	/**
	 * 
	 * @return A string representing the justification for marking a field as
	 *         @JsonIgnore, can be empty.
	 */
	String value();
}
