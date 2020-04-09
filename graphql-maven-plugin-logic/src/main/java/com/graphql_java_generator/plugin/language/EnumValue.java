package com.graphql_java_generator.plugin.language;

import java.util.List;

import com.graphql_java_generator.GraphqlUtils;

/**
 * Represents one of the possible values of a GraphQL Enum. An {@link EnumValue} is actually a name, and the list of
 * directives that have been defined for this enum value, in the GraphQL shema.
 * 
 * @author etienne-sf
 */
public interface EnumValue {
	/**
	 * The name of the field, as found in the GraphQL schema
	 * 
	 * @return The name of the field
	 */
	public String getName();

	/**
	 * The name of the field, as it can be used in the Java code. If the name is a java keyword (class, default,
	 * break...), the java name it prefixed by an underscore.
	 * 
	 * @return The name of the field, as it can be used in Java code
	 */
	default public String getJavaName() {
		return GraphqlUtils.graphqlUtils.getJavaName(getName());
	}

	/**
	 * Returns the package's name where the GraphQL objects from the GraphQL schema must be generated.
	 * 
	 * @return
	 */
	public String getPackageName();

	/** Returns the list of directives that have been defined for this field, in the GraphQL schema */
	public List<AppliedDirective> getAppliedDirectives();
}
