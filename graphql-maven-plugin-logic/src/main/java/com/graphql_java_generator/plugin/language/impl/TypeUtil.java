/**
 * 
 */
package com.graphql_java_generator.plugin.language.impl;

/**
 * A class to store some utilities
 * 
 * @author EtienneSF
 */
public class TypeUtil {

	/**
	 * Convert the given name, to a camel case name. Currenly very simple : it puts the first character in lower case.
	 * 
	 * @return
	 */
	public static String getCamelCase(String name) {
		return name.substring(0, 1).toLowerCase() + name.substring(1);
	}

	/**
	 * Convert the given name, which is supposed to be in camel case (for instance: thisIsCamelCase) to a pascal case
	 * string (for instance: ThisIsCamelCase).
	 * 
	 * @return
	 */
	public static String getPascalCase(String name) {
		return name.substring(0, 1).toUpperCase() + name.substring(1);
	}

}
