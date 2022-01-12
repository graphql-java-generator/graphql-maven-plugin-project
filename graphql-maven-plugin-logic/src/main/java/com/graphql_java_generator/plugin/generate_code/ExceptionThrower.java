/**
 * 
 */
package com.graphql_java_generator.plugin.generate_code;

/**
 * This class allows to throw a user defined Exception from a Velocity template
 * 
 * @see https://www.google.com/search?client=firefox-b-e&q=velocity+throw+exception
 */
public class ExceptionThrower {

	public void throwRuntimeException(String message) {
		throw new RuntimeException(message);
	}

}
