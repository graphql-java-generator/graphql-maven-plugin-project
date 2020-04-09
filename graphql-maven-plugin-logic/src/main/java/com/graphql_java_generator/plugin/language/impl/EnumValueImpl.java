package com.graphql_java_generator.plugin.language.impl;

import java.util.List;

import com.graphql_java_generator.plugin.language.AppliedDirective;
import com.graphql_java_generator.plugin.language.EnumValue;

import lombok.Builder;
import lombok.Data;

/**
 * Implementation of the {@link EnumValue} interface.
 * 
 * @author etienne-sf
 */
@Data
@Builder(toBuilder = true)
public class EnumValueImpl implements EnumValue {

	/**
	 * The name of the field, as found in the GraphQL schema
	 * 
	 * @return The name of the field
	 */
	private String name;

	/** The name of the package for this class */
	private String packageName;

	/** Returns the list of directives that have been defined for this field, in the GraphQL schema */
	private List<AppliedDirective> appliedDirectives;
}
