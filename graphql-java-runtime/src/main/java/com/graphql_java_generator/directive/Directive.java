/**
 * 
 */
package com.graphql_java_generator.directive;

import java.util.List;

import com.graphql_java_generator.client.request.InputParameter;

/**
 * Contains getter for the attributes of a GraphQL directive definition.
 * 
 * @author etienne-sf
 */
public class Directive {

	/** The name of the directive */
	private String name;

	/** A directive may have arguments. In the runtime, an argument is an {@link InputParameter}. */
	private List<InputParameter> arguments;

	/** Returns the list of location that this directive may have */
	private List<DirectiveLocation> directiveLocations;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<InputParameter> getArguments() {
		return arguments;
	}

	public void setArguments(List<InputParameter> arguments) {
		this.arguments = arguments;
	}

	public List<DirectiveLocation> getDirectiveLocations() {
		return directiveLocations;
	}

	public void setDirectiveLocations(List<DirectiveLocation> directiveLocations) {
		this.directiveLocations = directiveLocations;
	}

}
