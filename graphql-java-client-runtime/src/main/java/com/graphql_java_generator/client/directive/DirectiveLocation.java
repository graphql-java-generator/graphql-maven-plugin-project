/**
 * 
 */
package com.graphql_java_generator.client.directive;

/**
 * All possible directive locations
 * 
 * @author etienne-sf
 *
 */
public enum DirectiveLocation {

	// ExecutableDirectiveLocation
	QUERY, MUTATION, SUBSCRIPTION, FIELD, FRAGMENT_DEFINITION, FRAGMENT_SPREAD, INLINE_FRAGMENT, VARIABLE_DEFINITION,

	// TypeSystemDirectiveLocation
	SCHEMA, SCALAR, OBJECT, FIELD_DEFINITION, ARGUMENT_DEFINITION, INTERFACE, UNION, ENUM, ENUM_VALUE, INPUT_OBJECT, INPUT_FIELD_DEFINITION

}
