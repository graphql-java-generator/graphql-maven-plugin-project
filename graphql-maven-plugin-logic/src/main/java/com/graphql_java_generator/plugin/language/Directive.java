/**
 * 
 */
package com.graphql_java_generator.plugin.language;

import java.util.List;

/**
 * Contains getter for the attributes of a GraphQL directive definition.
 * 
 * @author etienne-sf
 */
public interface Directive {

	/** The name of the directive */
	public String getName();

	/** A directive may have arguments. An argument is actually a field. */
	public List<Field> getArguments();

	/** Returns the list of location that this directive may have */
	public List<DirectiveLocation> getDirectiveLocations();
}
