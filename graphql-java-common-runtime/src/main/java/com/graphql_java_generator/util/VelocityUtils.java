/**
 * 
 */
package com.graphql_java_generator.util;

/**
 * Various methods used by the velocity templates while generating the code.
 * 
 * @author etienne-sf
 */
public class VelocityUtils {

	public static VelocityUtils velocityUtils = new VelocityUtils();

	public String repeat(String value, int nbTimes) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < nbTimes; i += 1)
			sb.append(value);
		return sb.toString();
	}

}
