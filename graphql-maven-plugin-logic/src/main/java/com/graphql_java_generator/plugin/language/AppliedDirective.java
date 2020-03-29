/**
 * 
 */
package com.graphql_java_generator.plugin.language;

import java.util.Map;

/**
 * Contains getter for the attributes of a GraphQL directive, as it has been applied on an item of the GraphQL schema.
 * This directive should be a standard GraphQL directive, or one defined in the schema.
 * 
 * @author etienne-sf
 */
public interface AppliedDirective {

	/** The applied directive */
	public Directive getDirective();

	/**
	 * An applied directive may have arguments. These arguments must be defined in the directive definition. In the
	 * applied directive, we only store the arguments names and values.<BR/>
	 * 
	 * @return The map with all arguments values, where the key is the argument's name, and the value is the argument's
	 *         value
	 */
	public Map<String, Object> getArgumentValues();

}
