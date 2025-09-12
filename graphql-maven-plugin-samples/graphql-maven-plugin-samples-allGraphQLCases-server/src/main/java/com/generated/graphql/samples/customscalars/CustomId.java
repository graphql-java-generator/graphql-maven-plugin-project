/**
 * 
 */
package com.generated.graphql.samples.customscalars;

/**
 * A class that override a String, to check custom scalars, where the Java type is not known of the plugin while
 * generating the code. As explained in the issues
 * <a href="https://github.com/graphql-java-generator/graphql-maven-plugin-project/issues/184">184</a> and
 * <a href="https://github.com/graphql-java-generator/graphql-maven-plugin-project/issues/198">198</a>, it would
 * generate an error.
 * 
 * @author etienne-sf
 *
 */
public class CustomId {

	final private String id;

	public CustomId(String id) {
		this.id = id;
	}

	public String getId() {
		return this.id;
	}

	@Override
	public String toString() {
		return this.id;
	}

}
