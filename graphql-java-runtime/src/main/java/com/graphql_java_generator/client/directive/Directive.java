/**
 * 
 */
package com.graphql_java_generator.client.directive;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.graphql_java_generator.client.request.InputParameter;

/**
 * This object can represent both:
 * <UL>
 * <LI>A <B>directive definition</B>, as defined in the GraphQL schema, or as a standard GraphQL directive (skip,
 * include, deprecated). In this case the argument's value is null.</LI>
 * <LI>An <B>applied directive</B> within a query/mutation/subscription. In this case, the argument's value is the value
 * read in the query/mutation/subscription. Thus it can be a hard coded value, or a bind parameter)/</LI>
 * </UL>
 * 
 * @author etienne-sf
 */
public class Directive {

	/** The name of the directive */
	private String name;

	/** A directive may have arguments. In the runtime, an argument is an {@link InputParameter}. */
	private List<InputParameter> arguments = new ArrayList<>();

	/** Returns the list of location that this directive may have */
	private List<DirectiveLocation> directiveLocations = new ArrayList<>();

	public Directive() {

	}

	public Directive(String name) {
		this.name = name;
	}

	public Directive(String name, List<InputParameter> arguments) {
		this.name = name;
		this.arguments = (arguments == null) ? new ArrayList<>() : arguments;
	}

	public Directive(String name, InputParameter... arguments) {
		this.name = name;
		this.arguments = Arrays.asList(arguments);
	}

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
