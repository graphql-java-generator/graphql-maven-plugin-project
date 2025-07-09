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

	/**
	 * Returns the argument that matches the given argument name
	 * 
	 * @param argumentName
	 * @return
	 * @throws NoSuchFieldException
	 *             If the directive has no such argument
	 * @throws NullPointerException
	 *             If argumentName is null
	 */
	default Field getArgument(String argumentName) throws NoSuchFieldException {
		for (Field f : getArguments()) {
			if (argumentName.equals(f.getName())) {
				return f;
			}
		}
		// Not found...
		throw new NoSuchFieldException("The " + getName() + " directive has no '" + argumentName + "' argument");
	}

	/** Returns the comments that have been found before this object, in the provided GraphQL schema */
	public List<String> getComments();

	/** Returns the description for this object, in the provided GraphQL schema */
	public Description getDescription();

	/** Returns the list of location that this directive may have */
	public List<DirectiveLocation> getDirectiveLocations();

	/** Returns true if this directive is a repeatable directive */
	public boolean isRepeatable();

	/**
	 * Returns true if this directive is a standard GraphQL directive, or if it has been defined in the GraphQL schema
	 */
	public boolean isStandard();
}
