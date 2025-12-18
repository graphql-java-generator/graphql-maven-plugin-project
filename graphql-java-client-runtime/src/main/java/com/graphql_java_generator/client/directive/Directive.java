/**
 * 
 */
package com.graphql_java_generator.client.directive;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.graphql_java_generator.client.request.InputParameter;
import com.graphql_java_generator.client.request.QueryTokenizer;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;

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

	/**
	 * The schema that contains this : value of the <i>springBeanSuffix</i> plugin parameter for the searched schema.
	 * When there is only one schema, this plugin parameter is usually not set. In this case, its default value ("") is
	 * used.
	 */
	private final String schema;

	/** The name of the directive */
	private String name;

	/**
	 * The package name where the code for this directive has been generated. It's used to properly parse the directive
	 * arguments
	 */
	private String packageName;

	/** A directive may have arguments. In the runtime, an argument is an {@link InputParameter}. */
	private List<InputParameter> arguments = new ArrayList<>();

	/** Returns the list of location that this directive may have */
	private List<DirectiveLocation> directiveLocations = new ArrayList<>();

	/**
	 * Create a Directive from a {@link QueryTokenizer}. This {@link QueryTokenizer} should have read the starting @,
	 * and the next token should be the directive's name.
	 * 
	 * @param qt
	 * @param schema
	 *            value of the <i>springBeanSuffix</i> plugin parameter for the searched schema. When there is only one
	 *            schema, this plugin parameter is usually not set. In this case, its default value ("") is used.
	 * @throws GraphQLRequestPreparationException
	 */
	public Directive(QueryTokenizer qt, String schema) throws GraphQLRequestPreparationException {
		this.schema = schema;
		name = qt.nextToken();

		// We need to get some data from the registration of this directive.
		Directive d = DirectiveRegistryImpl.getDirective(schema, name);
		setPackageName(d.getPackageName());
		setDirectiveLocations(d.getDirectiveLocations());

		if (qt.checkNextToken("(")) {
			// We must consume the parenthesis, then read the list of argument.
			qt.nextToken();
			// This directive has one or more parameters
			arguments = InputParameter.readTokenizerForInputParameters(qt, d, null, null, schema);
		}
	}

	/**
	 * Appends this current directive into the given {@link StringBuilder}, to build the GraphQL request
	 * 
	 * @param sb
	 * @param parameters
	 *            The list of bind values for the possible bind parameters
	 * @throws GraphQLRequestExecutionException
	 */
	public void appendToGraphQLRequests(StringBuilder sb, Map<String, Object> parameters)
			throws GraphQLRequestExecutionException {
		sb.append(" ").append("@").append(name);
		InputParameter.appendInputParametersToGraphQLRequests(false, sb, arguments, parameters);
	}

	/**
	 * Default constructor, used by the generated DirectiveRegistryInitialize
	 * 
	 * @param qt
	 */
	public Directive(String schema) {
		this.schema = schema;
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

	/**
	 * Returns the definition for this GraphQL directive
	 * 
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public Directive getDirectiveDefinition() throws GraphQLRequestPreparationException {
		Directive directiveDefinition = DirectiveRegistryImpl.getDirective(schema, getName());
		if (directiveDefinition == null) {
			throw new GraphQLRequestPreparationException(
					"Could not find the definition for the directive '" + getName() + "'");
		}
		return directiveDefinition;
	}

	/**
	 * Returns the package name where the code for this directive has been generated. It's used to properly parse the
	 * directive arguments
	 * 
	 * @return
	 */
	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

}
