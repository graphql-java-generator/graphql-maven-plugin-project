/**
 *
 */
package com.graphql_java_generator.client.request;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.UUID;

import org.apache.commons.text.StringEscapeUtils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.graphql_java_generator.annotation.GraphQLIgnore;
import com.graphql_java_generator.annotation.GraphQLInputParameters;
import com.graphql_java_generator.annotation.GraphQLInputType;
import com.graphql_java_generator.annotation.GraphQLNonScalar;
import com.graphql_java_generator.annotation.GraphQLScalar;
import com.graphql_java_generator.client.CustomScalarRegistryImpl;
import com.graphql_java_generator.client.GraphQLTypeMappingRegistry;
import com.graphql_java_generator.client.GraphqlClientUtils;
import com.graphql_java_generator.client.RequestExecutionSpringReactiveImpl;
import com.graphql_java_generator.client.directive.Directive;
import com.graphql_java_generator.client.directive.DirectiveRegistryImpl;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;
import com.graphql_java_generator.util.GraphqlUtils;

import graphql.GraphQLContext;
import graphql.schema.GraphQLScalarType;

/**
 * Contains an input parameter, to be sent to a query (mutation...). It can be either:
 * <LI>
 * <UL>
 * A hard coded value
 * </UL>
 * <UL>
 * A bind variable, which value must be provided when executing the query
 * <LI>
 *
 * @author etienne-sf
 */
public class InputParameter {

	/** A utility class, that's used here */
	private static GraphqlUtils graphqlUtils = new GraphqlUtils();
	/** A utility class, that's used here */
	private static GraphqlClientUtils graphqlClientUtils = new GraphqlClientUtils();

	/** Indicates the kind of parameter */
	public enum InputParameterType {
		/** For {@link InputParameter} which value is given in the request being parsed */
		HARD_CODED,
		/** A mandatory {@link InputParameter}, that is one which declaration starts with a '&' */
		MANDATORY,
		/** An optional {@link InputParameter}, that is one which declaration starts with a '?' */
		OPTIONAL,
		/**
		 * A GraphQL variable, marked by a '$', that will be transmitted to the server in the variables json attribute
		 * of the request
		 */
		GRAPHQL_VARIABLE
	};

	/** Indicates what is being read by the {@link #readTokenizerForInputParameters(StringTokenizer) method */
	private enum InputParameterStep {
		NAME, VALUE
	};

	/**
	 * value of the <i>springBeanSuffix</i> plugin parameter for the searched schema. When there is only one schema,
	 * this plugin parameter is usually not set. In this case, its default value ("") is used.
	 */
	final String schema;

	/** The parameter name, as defined in the GraphQL schema */
	final String name;

	/**
	 * The bind parameter, as defined in the GraphQL query. <BR/>
	 * For instance <I>sinceParam</I> in <I>posts(since: &sinceParam) {date}</I>
	 */
	final String bindParameterName;

	/** The default value, for this input parameter */
	final Object defaultValue;

	/** Indicates whether this parameter is mandatory or not */
	final InputParameterType type;

	/** The GraphQL type name of this parameter. For instance: "Human", for the type "[[Human]]" */
	final String graphQLTypeName;
	/** The Scalar GraphQL type name of this parameter. Null if this parameter is not a Scalar */
	final GraphQLScalarType graphQLScalarType;

	/**
	 * Used only if this parameter is a list. In this case: true if the item of the list are mandatory, false otherwise
	 */
	final private boolean itemMandatory;
	/**
	 * The depth of the list for this input parameter: 0 if this parameter is not a list. And 2, for instance, if the
	 * parameter's type is "[[Int]]"
	 */
	final private int listDepth;
	/** True if this parameter is mandatory */
	final private boolean mandatory;

	/**
	 * Creates and returns a new instance of {@link InputParameter}, which is bound to a bind variable. The value for
	 * this bind variable must be provided, when calling the request execution.
	 *
	 * @param schema
	 *            value of the <i>springBeanSuffix</i> plugin parameter for the searched schema. When there is only one
	 *            schema, this plugin parameter is usually not set. In this case, its default value ("") is used.
	 * @param name
	 * @param bindParameterName
	 *            The name of the bind parameter, as defined in the GraphQL response definition. It is mandator for bind
	 *            parameters, so it may not be null here. Please read the
	 *            <A HREF="https://graphql-maven-plugin-project.graphql-java-generator.com/client.html">client doc</A>
	 *            for more information on input parameters and bind variables.
	 * @param type
	 *            The kind of {@link InputParameter} to create
	 * @param graphQLTypeName
	 *            The GraphQL type name of this parameter. For instance: "Human", for the type "[[Human]]"
	 * @param mandatory
	 *            True if this parameter is mandatory
	 * @param listDepth
	 *            The depth of the list for this input parameter: 0 if this parameter is not a list. And 2, for
	 *            instance, if the parameter's type is "[[Int]]"
	 * @param itemMandatory
	 *            Used only if this parameter is a list. In this case: true if the item of the list are mandatory, false
	 *            otherwise
	 * @return The newly created {@link InputParameter}, according to these parameters
	 * @see RequestExecutionSpringReactiveImpl#execute(String, ObjectResponse, List, Class)
	 */
	public static InputParameter newBindParameter(String schema, String name, String bindParameterName,
			InputParameterType type, String graphQLTypeName, boolean mandatory, int listDepth, boolean itemMandatory) {
		if (bindParameterName == null) {
			throw new NullPointerException("[Internal error] The bind parameter name is mandatory");
		}
		return new InputParameter(schema, name, bindParameterName, null, type, graphQLTypeName, mandatory, listDepth,
				itemMandatory);
	}

	/**
	 * Creates and returns a new instance of {@link InputParameter}, which value is given, and can not be changed
	 * afterwards.
	 *
	 * @param value
	 *            of the <i>springBeanSuffix</i> plugin parameter for the searched schema. When there is only one
	 *            schema, this plugin parameter is usually not set. In this case, its default value ("") is used.
	 * @param name
	 *            The parameter name, as defined in the GraphQL schema
	 * @param defaultValue
	 *            Optional default value for parameter
	 * @param graphQLTypeName
	 *            The GraphQL type name of this parameter. For instance: "Human", for the type "[[Human]]"
	 * @param mandatory
	 *            True if this parameter is mandatory
	 * @param listDepth
	 *            The depth of the list for this input parameter: 0 if this parameter is not a list. And 2, for
	 *            instance, if the parameter's type is "[[Int]]"
	 * @param itemMandatory
	 *            Used only if this parameter is a list. In this case: true if the item of the list are mandatory, false
	 *            otherwise
	 * @return The newly created {@link InputParameter}, according to these parameters
	 */
	public static InputParameter newGraphQLVariableParameter(String schema, String name, Object defaultValue,
			String graphQLTypeName, boolean mandatory, int listDepth, boolean itemMandatory) {
		return new InputParameter(schema, name, name, defaultValue, InputParameterType.GRAPHQL_VARIABLE,
				graphQLTypeName, mandatory, listDepth, itemMandatory);
	}

	/**
	 * Creates and returns a new instance of {@link InputParameter}, which value is given, and can not be changed
	 * afterwards.
	 *
	 * @param value
	 *            of the <i>springBeanSuffix</i> plugin parameter for the searched schema. When there is only one
	 *            schema, this plugin parameter is usually not set. In this case, its default value ("") is used.
	 * @param name
	 *            The parameter name, as defined in the GraphQL schema
	 * @param value
	 *            The value to send, for this input parameter. If null, it's a bind parameter. The bindParameterName is
	 *            then mandatory.
	 * @param graphQLTypeName
	 *            The GraphQL type name of this parameter. For instance: "Human", for the type "[[Human]]"
	 * @param mandatory
	 *            True if this parameter is mandatory
	 * @param listDepth
	 *            The depth of the list for this input parameter: 0 if this parameter is not a list. And 2, for
	 *            instance, if the parameter's type is "[[Int]]"
	 * @param itemMandatory
	 *            Used only if this parameter is a list. In this case: true if the item of the list are mandatory, false
	 *            otherwise
	 * @return The newly created {@link InputParameter}, according to these parameters
	 */
	public static InputParameter newHardCodedParameter(String schema, String name, Object value, String graphQLTypeName,
			boolean mandatory, int listDepth, boolean itemMandatory) {
		return new InputParameter(schema, name, null, value, InputParameterType.HARD_CODED, graphQLTypeName, mandatory,
				listDepth, itemMandatory);
	}

	/**
	 * The constructor is private. Instances must be created with one of these helper methods:
	 * {@link #newBindParameter(String, String)} or {@link #newHardCodedParameter(String, Object)}
	 *
	 * @param schema
	 *            The <i>springBeanSuffix</i> plugin parameter for the searched schema. When there is only one schema,
	 *            this plugin parameter is usually not set. In this case, its default value ("") is used.
	 * @param name
	 *            The parameter name, as defined in the GraphQL schema
	 * @param bindParameterName
	 *            The name of the bind parameter, as defined in the GraphQL response definition. If null, it's a hard
	 *            coded value. The value will be sent to the server, when the request is executed. Please read the
	 *            <A HREF="https://graphql-maven-plugin-project.graphql-java-generator.com/client.html">client doc</A>
	 *            for more information on input parameters and bind variables.
	 * @param defaultValue
	 *            The value to send, for this input parameter. If null, it's a bind parameter. The bindParameterName is
	 *            then mandatory.
	 * @param type
	 *            The kind of {@link InputParameter} to create
	 * @param graphQLTypeName
	 *            The GraphQL type name of this parameter. For instance: "Human", for the type "[[Human]]"
	 * @param mandatory
	 *            True if this parameter is mandatory
	 * @param listDepth
	 *            The depth of the list for this input parameter: 0 if this parameter is not a list. And 2, for
	 *            instance, if the parameter's type is "[[Int]]"
	 * @param itemMandatory
	 *            Used only if this parameter is a list. In this case: true if the item of the list are mandatory, false
	 *            otherwise
	 */
	protected InputParameter(String schema, String name, String bindParameterName, Object defaultValue,
			InputParameterType type, String graphQLTypeName, boolean mandatory, int listDepth, boolean itemMandatory) {
		if (name == null) {
			throw new NullPointerException("The input parameter's name is mandatory");
		}

		this.schema = schema;
		this.name = name;
		this.bindParameterName = bindParameterName;
		this.defaultValue = defaultValue;
		this.type = type;
		this.graphQLTypeName = graphQLTypeName;
		graphQLScalarType = (graphQLTypeName == null) ? null
				: graphqlClientUtils.getGraphQLScalarTypeFromName(graphQLTypeName, schema);
		this.mandatory = mandatory;
		this.listDepth = listDepth;
		this.itemMandatory = itemMandatory;
	}

	/**
	 * The constructor is private. Instances must be created with one of these helper methods:
	 * {@link #newBindParameter(String, String)} or {@link #newHardCodedParameter(String, Object)}
	 *
	 *
	 * @param value
	 *            of the <i>springBeanSuffix</i> plugin parameter for the searched schema. When there is only one
	 *            schema, this plugin parameter is usually not set. In this case, its default value ("") is used.
	 * @param name
	 *            The parameter name, as defined in the GraphQL schema
	 * @param parameterName
	 *            The name of the bind parameter, as defined in the GraphQL response definition. If null, it's a hard
	 *            coded value. The value will be sent to the server, when the request is executed. Please read the
	 *            <A HREF="https://graphql-maven-plugin-project.graphql-java-generator.com/client.html">client doc</A>
	 *            for more information on input parameters and bind variables.
	 * @param value
	 *            The value to send, for this input parameter. If null, it's a bind parameter. The bindParameterName is
	 *            then mandatory.
	 * @param type
	 *            The kind of {@link InputParameter} to create
	 * @param directive
	 *            If not null, then we're looking for an argument of a GraphQL directive. Oherwise, it's a field
	 *            argument, and the owningClass and fieldName parameters will be used.
	 * @param owningClass
	 *            The class that contains this field
	 * @param fieldName
	 *            The field name
	 * @throws GraphQLRequestPreparationException
	 */
	protected InputParameter(String schema, String name, String parameterName, Object value, InputParameterType type,
			Directive directive, Class<?> owningClass, String fieldName) throws GraphQLRequestPreparationException {
		String localGraphQLCustomScalarType = null;
		boolean localMandatory = false;
		int localList = 0;
		boolean localItemMandatory = false;

		if (name == null) {
			throw new NullPointerException("The input parameter's name is mandatory");
		}

		if (directive != null) {
			// Let's find the definition for this directive
			Directive dirDef = DirectiveRegistryImpl.getDirective(schema, directive.getName());
			if (dirDef == null) {
				throw new GraphQLRequestPreparationException(
						"Could not find directive definition for the directive '" + directive.getName() + "'");
			}

			// Let's find the GraphQL type of this argument
			boolean found = false;
			for (InputParameter param : dirDef.getArguments()) {
				if (param.getName().equals(name)) {
					found = true;
					localGraphQLCustomScalarType = param.getGraphQLTypeName();
					localMandatory = param.isMandatory();
					localList = param.getListDepth();
					localItemMandatory = param.isItemMandatory();
				}
			} // for

			if (!found) {
				throw new GraphQLRequestPreparationException("The parameter of name '" + name
						+ "' has not been found for the directive '" + directive.getName() + "'");
			}
		} else {
			GraphQLInputParameters inputParams;

			if (owningClass.isInterface()) {
				Method method;
				try {
					method = owningClass
							.getMethod("get" + graphqlUtils.getPascalCase(graphqlUtils.getJavaName(fieldName)));
					inputParams = method.getAnnotation(GraphQLInputParameters.class);
				} catch (NoSuchMethodException | SecurityException e) {
					throw new GraphQLRequestPreparationException("Error while looking for the the getter for <"
							+ fieldName + "> in the interface '" + owningClass.getName() + "'", e);
				}
			} else {
				try {
					Field field = owningClass.getDeclaredField(graphqlUtils.getJavaName(fieldName));
					inputParams = field.getAnnotation(GraphQLInputParameters.class);
				} catch (NoSuchFieldException | SecurityException e) {
					// We may be in the XxxResponse class. ITs has no field, and all the relevant fields are in its
					// superclass. Let's recurse once.
					try {
						Field field = owningClass.getSuperclass().getDeclaredField(graphqlUtils.getJavaName(fieldName));
						inputParams = field.getAnnotation(GraphQLInputParameters.class);
					} catch (NoSuchFieldException | SecurityException e2) {
						// We may be in the XxxResponse class. ITs has no field, and all the relevant fields are in its
						// superclass. Let's recurse once.
						throw new GraphQLRequestPreparationException("Error while looking for the the field <"
								+ fieldName + "> in the class '" + owningClass.getName() + "', not in its superclass: "
								+ owningClass.getSuperclass().getName(), e);
					}
				}
			}

			if (inputParams == null) {
				throw new GraphQLRequestPreparationException("The field <" + fieldName + "> of the class '"
						+ owningClass.getName() + "' has no input parameters. Error while looking for its '"
						+ parameterName + "' input parameter");
			}

			boolean found = false;
			for (int i = 0; i < inputParams.names().length; i += 1) {
				if (inputParams.names()[i].equals(name)) {
					// We've found the expected parameter
					found = true;
					localGraphQLCustomScalarType = inputParams.types()[i];
					localMandatory = inputParams.mandatories()[i];
					localList = inputParams.listDepths()[i];
					localItemMandatory = inputParams.itemsMandatory()[i];
				}
			}

			if (!found) {
				throw new GraphQLRequestPreparationException(
						"The parameter of name <" + parameterName + "> has not been found for the field <" + fieldName
								+ "> of the class '" + owningClass.getName() + "'");
			}
		}

		this.schema = schema;
		this.name = name;
		bindParameterName = parameterName;
		defaultValue = value;
		this.type = type;
		graphQLTypeName = localGraphQLCustomScalarType;
		graphQLScalarType = graphqlClientUtils.getGraphQLScalarTypeFromName(graphQLTypeName, schema);
		mandatory = localMandatory;
		listDepth = localList;
		itemMandatory = localItemMandatory;
	}

	/**
	 * Reads a list of input parameters, from a {@link QueryTokenizer}. It can be the list of parameters for a Field or
	 * for a Directive. It can be either a Field of a Query, Mutation or Subscription, or a Field of a standard GraphQL
	 * Type, or any directive...
	 *
	 * @param qt
	 *            The StringTokenizer, where the opening parenthesis has been read. It will be read until and including
	 *            the next closing parenthesis.
	 * @param directive
	 *            if not null, then this method is reading the input parameters (arguments) for this {@link Directive}
	 * @param owningClass
	 *            if not null, then this method is reading the input parameters for the field <I>fieldName</I> of this
	 *            class.
	 * @param fieldName
	 *            if <I>owningClass</I>, this is the name of the field, whose input parameters are being read.
	 * @throws GraphQLRequestPreparationException
	 *             If the request string is invalid
	 */
	public static List<InputParameter> readTokenizerForInputParameters(QueryTokenizer qt, Directive directive,
			Class<?> owningClass, String fieldName, String schema) throws GraphQLRequestPreparationException {
		List<InputParameter> ret = new ArrayList<>(); // The list that will be returned by this method
		InputParameterStep step = InputParameterStep.NAME;

		String parameterName = null;

		while (qt.hasMoreTokens()) {
			String token = qt.nextToken();
			switch (token) {
			case ":":
				// We're about to read an input parameter value.
				break;
			case ",":
				if (step != InputParameterStep.NAME) {
					throw new GraphQLRequestPreparationException("Misplaced comma for the field '" + fieldName
							+ "' is not finished (no closing parenthesis)");
				}
				break;
			case ")":
				// We should be waiting for a name, and have already read at least one name
				if (parameterName == null) {
					throw new GraphQLRequestPreparationException("Misplaced closing parenthesis for the field '"
							+ fieldName + "' (no parameter has been read)");
				} else if (step != InputParameterStep.NAME) {
					throw new GraphQLRequestPreparationException("Misplaced closing parenthesis for the field '"
							+ fieldName + "' is not finished (no closing parenthesis)");
				}
				// We're finished, here.
				return ret;
			default:
				switch (step) {
				case NAME:
					parameterName = token;
					step = InputParameterStep.VALUE;
					break;
				case VALUE:
					// We've read the parameter value. Let's add this parameter.
					if (token.startsWith("?")) {
						ret.add(new InputParameter(schema, parameterName, token.substring(1), null,
								InputParameterType.OPTIONAL, directive, owningClass, fieldName));
					} else if (token.startsWith("&")) {
						ret.add(new InputParameter(schema, parameterName, token.substring(1), null,
								InputParameterType.MANDATORY, directive, owningClass, fieldName));
					} else if (token.startsWith("$")) {
						ret.add(new InputParameter(schema, parameterName, token.substring(1), null,
								InputParameterType.GRAPHQL_VARIABLE, directive, owningClass, fieldName));
					} else {
						// The inputParameters mandatory, list and itemMandatory are forced (as theses attributes are
						// not used in this case)
						String parameterTypeName = null;
						if (owningClass != null) {
							parameterTypeName = getInputParameterTypeStr(owningClass, fieldName, parameterName);
						} else {
							// Let's find the type from the directive definition
							for (InputParameter ip : directive.getArguments()) {
								if (ip.getName().equals(parameterName)) {
									parameterTypeName = ip.getGraphQLTypeName();
									break;
								}
							}
							if (parameterTypeName == null) {
								throw new GraphQLRequestPreparationException(
										"Could not find the type of the parameter '" + parameterName
												+ "' for the directive '" + directive.getName() + "'");
							}
						}

						// Ok, we've found the directive type. Let's read the value
						ret.add(new InputParameter(schema, parameterName, null,
								readTokenizerForInputParameterValue(qt, schema, parameterName, parameterTypeName),
								InputParameterType.HARD_CODED, null, true, 0, false));
					}
					step = InputParameterStep.NAME;
					break;
				}
			}// switch (token)
		} // while (st.hasMoreTokens())

		throw new GraphQLRequestPreparationException(
				"The list of parameters for the field '" + fieldName + "' is not finished (no closing parenthesis)");

	}

	/**
	 * Reads a value on the given {@link QueryTokenizer}.
	 * 
	 * @param qt
	 *            The {@link QueryTokenizer}. Its current token is the first token of the value to read
	 * @param schema
	 * @param parameterName
	 * @param parameterTypeName
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	static Object readTokenizerForInputParameterValue(QueryTokenizer qt, String schema, String parameterName,
			String parameterTypeName) throws GraphQLRequestPreparationException {

		String token = qt.currentToken();

		if (token.equals("[") || token.equals("{")) {
			// We've found the start of a JSON list or JSON object. Let's read this object.
			// We'll store it as a String, and write it back in the request toward the GraphQL server
			// request
			StringBuilder sb = new StringBuilder(token);
			String previousToken;
			boolean list = token.startsWith("[");
			boolean withinAString = false;
			int recursiveLevel = 1;// Counts the depth of [ or { we're in. When it's back to 0, we're done
									// reading this list or object
			while (true) {
				if (!qt.hasMoreTokens(true)) {
					throw new GraphQLRequestPreparationException(
							"Found the end of the GraphQL request before the end of the " + (list ? "list" : "object")
									+ ": '" + sb.toString() + "'");
				}
				previousToken = token;
				token = qt.nextToken(true);
				sb.append(token);
				if (token.contentEquals("\"") && !(withinAString && previousToken.endsWith("\\"))) {
					// We've found the start or the end of the string value. This important, as []{}
					// characters should be ignored, when in a string
					withinAString = !withinAString;
				}
				if (!withinAString) {
					if ((list && token.equals("[") || (!list && token.equals("{")))) {
						// We're going deeper in the list or object
						recursiveLevel += 1;
					} else if ((list && token.equals("]") || (!list && token.equals("}")))) {
						// We're going up once.
						recursiveLevel -= 1;
						if (recursiveLevel == 0) {
							// Ok, we're done for this list or object
							break;
						}
					}
				}
			} // while (true)

			// It's a GraphQL list or object. We store the read characters as is.
			// The inputParameters mandatory, list and itemMandatory are forced (as theses attributes are
			// not used in this case)
			return new RawGraphQLString(sb.toString());
		} else if (token.equals("\"")) {
			// We've found a String value: let's read the string content
			StringBuilder sb = new StringBuilder();
			sb.append("\"");
			boolean nextCharIsEscaped = false;
			while (true) {
				if (!qt.hasMoreTokens(true)) {
					throw new GraphQLRequestPreparationException(
							"Found the end of the GraphQL request before the end of the string parameter '"
									+ sb.toString() + "'");
				}

				token = qt.nextToken(true);

				// We read the string until its end, then we reuse a proper unescape method to correctly
				// apply json rules.
				// So we just look for the " that ends this string, and write all other characters as they
				// are
				if (!nextCharIsEscaped && token.equals("\"")) {
					// This the end of the string
					break;
				} else if (!nextCharIsEscaped) {
					nextCharIsEscaped = token.equals("\\");
				} else {
					nextCharIsEscaped = false;
				}

				sb.append(token);
			} // while (true)

			sb.append("\"");

			// The inputParameters mandatory, list and itemMandatory are forced (as theses attributes are
			// not used in this case)
			return new RawGraphQLString(sb.toString());
		} else if (token.startsWith("\"") || token.endsWith("\"")) {
			// Too bad, there is a " only at the end or only at the beginning
			throw new GraphQLRequestPreparationException(
					"Bad parameter value: parameter values should start and finish by \", or not having any \" at the beginning and end."
							+ " But it's not the case for the value <" + token + "> of parameter <" + parameterName
							+ ">. Maybe you wanted to add a bind parameter instead (bind parameter must start with a ? or a &");
		} else {
			return parseValueForInputParameter(token, schema, parameterTypeName);
		}
	}

	/**
	 * Parse a value read for an input parameter, within the query
	 *
	 * @param owningClass
	 * @param fieldName
	 * @param parameterName
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	private static String getInputParameterTypeStr(Class<?> owningClass, String fieldName, String parameterName)
			throws GraphQLRequestPreparationException {
		GraphQLInputParameters graphQLInputParameters;
		if (owningClass.isInterface()) {
			Method method = graphqlUtils.getSetter(owningClass, fieldName);
			graphQLInputParameters = method.getDeclaredAnnotation(GraphQLInputParameters.class);
		} else {
			Field field = graphqlClientUtils.getDeclaredField(owningClass, graphqlUtils.getJavaName(fieldName), true);
			graphQLInputParameters = field.getDeclaredAnnotation(GraphQLInputParameters.class);
		}

		if (graphQLInputParameters == null) {
			throw new GraphQLRequestPreparationException(
					"[Internal error] The field '" + fieldName + "' is lacking the GraphQLInputParameters annotation");
		}

		for (int i = 0; i < graphQLInputParameters.names().length; i += 1) {
			if (graphQLInputParameters.names()[i].equals(parameterName)) {
				// We've found the parameterType. Let's get its value.
				try {
					return graphQLInputParameters.types()[i];
				} catch (Exception e) {
					throw new GraphQLRequestPreparationException(
							"Could not find the type of the parameter '" + parameterName + "' of the field '"
									+ fieldName + "' of the type '" + owningClass.getName() + "'",
							e);
				}
			}
		}

		// Too bad...
		throw new GraphQLRequestPreparationException("[Internal error] Can't find the type for the parameter '"
				+ parameterName + "' of the field '" + fieldName + "'");
	}

	/**
	 * Parse a value, depending on the parameter type.
	 *
	 * @param parameterValue
	 * @param schema
	 * @param parameterTypeName
	 * @param packageName
	 *            value of the <i>springBeanSuffix</i> plugin parameter for the searched schema. When there is only one
	 *            schema, this plugin parameter is usually not set. In this case, its default value ("") is used.
	 * @return
	 * @throws GraphQLRequestPreparationException
	 * @throws RuntimeException
	 *             When the value could be parsed
	 */
	public static Object parseValueForInputParameter(Object parameterValue, String schema, String parameterTypeName)
			throws GraphQLRequestPreparationException {

		// Let's check if this type is a Custom Scalar
		GraphQLScalarType graphQLScalarType = CustomScalarRegistryImpl.getCustomScalarRegistry(schema)
				.getGraphQLCustomScalarType(parameterTypeName);

		if (graphQLScalarType != null) {
			// This type is a Custom Scalar. Let's ask the CustomScalar implementation to translate this value.
			// Note: the GraphqQL ID is managed by specific CustomScalars, which is specific to the client or the server
			// mode (ID are String for the client, and UUID for the server)
			return graphQLScalarType.getCoercing().parseValue(parameterValue, GraphQLContext.getDefault(),
					Locale.getDefault());
		} else if (parameterTypeName.equals("Boolean")) {
			if (parameterValue instanceof Boolean) {
				// This should not occur
				return parameterValue;
			} else if (parameterValue instanceof String) {
				if (parameterValue.equals("true")) {
					return Boolean.TRUE;
				} else if (parameterValue.equals("false")) {
					return Boolean.FALSE;
				}
			}
			throw new RuntimeException(
					"Bad boolean value '" + parameterValue + "' for the parameter type '" + parameterTypeName + "'");
		} else if (parameterTypeName.equals("Float")) {
			// GraphQL Float are double precision numbers
			return Double.parseDouble((String) parameterValue);
		} else if (parameterTypeName.equals("Int")) {
			return Integer.parseInt((String) parameterValue);
		} else if (parameterTypeName.equals("Long")) {
			return Long.parseLong((String) parameterValue);
		} else if (parameterTypeName.equals("String")) {
			return parameterValue;
		} else {
			// We need to get the class for this parameter type
			Class<?> parameterClass = GraphQLTypeMappingRegistry.getJavaClass(schema, parameterTypeName);

			// This type is not a Custom Scalar, so it must be a standard Scalar. Let's manage it
			if (parameterClass.isEnum()) {
				// This parameter is an enum. The parameterValue is one of its elements
				Method valueOf = graphqlUtils.getMethod("valueOf", parameterClass, String.class);
				return graphqlUtils.invokeMethod(valueOf, null, parameterValue);
			} else if (parameterClass.isAssignableFrom(Boolean.class)) {
				// This parameter is a boolean. Only true and false are valid boolean.
				if (!"true".equals(parameterValue) && !"false".equals(parameterValue)) {
					throw new RuntimeException("Only true and false are allowed values for booleans, but the value is '"
							+ parameterValue + "'");
				}
				return "true".equals(parameterValue);
			} else if (parameterClass.isAssignableFrom(Integer.class)) {
				return Integer.parseInt((String) parameterValue);
			} else if (parameterClass.isAssignableFrom(Float.class)) {
				return Float.parseFloat((String) parameterValue);
			}
		} // else (scalarType != null)

		// Too bad...
		throw new RuntimeException(
				"Couldn't parse the value'" + parameterValue + "' for the parameter type '" + parameterTypeName
						+ "': non managed GraphQL type (maybe a custom scalar is not properly registered?)");
	}

	public String getName() {
		return name;
	}

	/**
	 * This method is for internal use only. It returns a brute value of the parameter, without any processing. The
	 * {@link #getValueForGraphqlQuery(Map)} method should be used, instead
	 * 
	 * @return
	 */
	Object getDefaultValue() {
		return defaultValue;
	}

	/**
	 * Returns the default parameter value, properly formated,for inclusion as default value in a GraphQL query.
	 * 
	 * @return
	 */
	public Object getDefaultValueForGraphqlQuery() {
		if (defaultValue instanceof RawGraphQLString) {
			return defaultValue;
		} else {
			return getValueForGraphqlQuery(defaultValue, listDepth, graphQLScalarType);
		}
	}

	/**
	 * Returns the parameter value, as-is, without any escape character
	 * 
	 * @param bindVariables
	 *            The map for the bind variables. It may be null, if this input parameter is a hard coded one. If this
	 *            parameter is a Bind Variable, then bindVariables is mandatory, and it must contain a value for th bind
	 *            parameter which name is stored in {@link #bindParameterName}.
	 * @return
	 * @throws GraphQLRequestExecutionException
	 */
	public Object getValueForGraphqlQuery(Map<String, Object> bindVariables) {
		return getValueForGraphqlQuery(bindVariables.get(bindParameterName), listDepth, graphQLScalarType);
	}

	private Object getValueForGraphqlQuery(Object val, int listDepth, GraphQLScalarType graphQLScalarTypeParam) {
		if (val == null) {
			return null;
		} else if (listDepth > 0) {
			if (val.getClass().isArray()) {
				// Doing just "Arrays.asList(val)" results in a StackOverflowError!
				// We have to indicate that val is an array!
				return getValueForAListValue(Arrays.asList((Object[]) val), listDepth, graphQLScalarTypeParam);
			} else if (val instanceof List) {
				return getValueForAListValue((List<?>) val, listDepth, graphQLScalarTypeParam);
			} else {
				throw new IllegalArgumentException("The given value for the parameter '" + bindParameterName
						+ "' should be either an array or a list, but is a " + val.getClass().getName());
			}
		} else if (graphQLScalarTypeParam != null) {
			return graphQLScalarTypeParam.getCoercing().serialize(val, GraphQLContext.getDefault(),
					Locale.getDefault());
		} else {
			return val;
		}
	}

	private Object getValueForAListValue(List<?> list, int listDepth, GraphQLScalarType graphQLScalarTypeParam) {
		List<Object> ret = new ArrayList<>(list.size());
		for (Object v : list) {
			ret.add(getValueForGraphqlQuery(v, listDepth - 1, graphQLScalarTypeParam));
		}
		return ret;
	}

	/**
	 * Returns the parameter, as it should be written in the GraphQL query. For instance:
	 * <UL>
	 * <LI>String: a "string" -> "a \"string\""</LI>
	 * <LI>Enum: EPISODE -> EPISODE (no escape or double quote here)</LI>
	 * </UL>
	 *
	 * @param writingGraphQLVariables
	 *            true if this call is done, while writing the value for the "variables" field of the json request.
	 * @param bindVariables
	 *            The map for the bind variables. It may be null, if this input parameter is a hard coded one. If this
	 *            parameter is a Bind Variable, then bindVariables is mandatory, and it must contain a value for th bind
	 *            parameter which name is stored in {@link #bindParameterName}.
	 * @return
	 * @throws GraphQLRequestExecutionException
	 */
	public String getStringContentForGraphqlQuery(boolean writingGraphQLVariables, Map<String, Object> bindVariables)
			throws GraphQLRequestExecutionException {
		if (bindParameterName == null) {
			// It's a hard coded value
			return this.getStringContentForGraphqlQuery(writingGraphQLVariables, defaultValue, listDepth,
					graphQLScalarType, false);
		}
		// It's a Bind Variable.

		if (type.equals(InputParameterType.MANDATORY)
				&& (bindVariables == null || !bindVariables.keySet().contains(bindParameterName))
				&& defaultValue == null) {
			// The InputParameter is mandatory, it doesn't exist in the map of BindVariables, and it has no default
			// value: this is a non valid request
			throw new GraphQLRequestExecutionException(
					"The Bind Parameter for '" + bindParameterName + "' must be provided in the BindVariables map");
		}
		// else if (type.equals(InputParameterType.GRAPHQL_VARIABLE)
		// && (bindVariables == null || !bindVariables.keySet().contains(bindParameterName))
		// && defaultValue == null) {
		// // The InputParameter is a GraphQL variable defined in the query. It doesn't exist in the map of
		// // BindVariables, and it has no default value for this field.
		// // This GraphQL variable should have a default value. But it's too complex to check it here. We skip the
		// follow
		// return this.getStringContentForGraphqlQuery(writingGraphQLVariables, bindVariables.get(bindParameterName),
		// listDepth, graphQLScalarType, type.equals(InputParameterType.GRAPHQL_VARIABLE));
		// }

		Object bindVariableValue = (bindVariables == null || !bindVariables.keySet().contains(bindParameterName)) ? null
				: bindVariables.get(bindParameterName);

		return this.getStringContentForGraphqlQuery(writingGraphQLVariables, bindVariableValue, listDepth,
				graphQLScalarType, type.equals(InputParameterType.GRAPHQL_VARIABLE));

	}

	/**
	 * This method is used both by {@link #getValueForGraphqlQuery()} and {@link #getListValue(List)} to extract a value
	 * as a string.
	 *
	 * @param writingGraphQLVariables
	 *            true if this call is done, while writing the value for the "variables" field of the json request.
	 * @param val
	 *            This value of the parameter. It can be the {@link #defaultValue} if it is not null, or the binding
	 *            from the bind parameters. It's up to the caller to map the bind parameter into this method argument.
	 * @param listDepth
	 *            The expected list depth for the value. listDepth is 0 if val should not be a list, 1 if val should be
	 *            a list, 2 if val should be a list of list...
	 * @param graphQLTypeNameParam
	 * @param graphQLScalarTypeParam
	 *            The {@link GraphQLScalarType} for this value. It may be the same as the parameter one (for scalar), or
	 *            the one of the current field (for input types).
	 * @param graphQLVariable
	 *            true if the current input type should be deserialize as a GraphQL variable. In this case, it's
	 *            deserialized as a map. So the field names must be within double quotes
	 * @return
	 * @throws GraphQLRequestExecutionException
	 */
	protected String getStringContentForGraphqlQuery(boolean writingGraphQLVariables, Object val, int listDepth,
			GraphQLScalarType graphQLScalarTypeParam, boolean graphQLVariable) throws GraphQLRequestExecutionException {
		if (graphQLVariable && !writingGraphQLVariables) {
			// When writing a GraphQL variable in the query itself, then we write the variable name. The value is
			// written only in the GraphQL variable field
			return "$" + bindParameterName;
		} else if (val == null) {
			return null;
		} else if (listDepth > 0) {
			// We expect val to be a list
			return getStringContentForAListValue(writingGraphQLVariables, val, listDepth, graphQLScalarTypeParam,
					graphQLVariable);
		} else if (graphQLScalarTypeParam != null) {
			// This parameter is a scalar. We must apply its coercing method.
			Object ret = graphQLScalarTypeParam.getCoercing().serialize(val, GraphQLContext.getDefault(),
					Locale.getDefault());
			if (ret instanceof String) {
				return getQuotedString((String) ret);
			} else if (ret instanceof ObjectNode) {
				StringBuilder sb = new StringBuilder();
				appendStringContentForGraphqlQueryFromObjectNode(sb, ((ObjectNode) ret).traverse(), (ObjectNode) ret);
				return sb.toString();
			} else if (ret instanceof Map) {
				StringBuilder sb = new StringBuilder();
				appendStringContentForGraphqlQueryFromMap(sb, (Map<?, ?>) ret);
				return sb.toString();
			} else if (ret instanceof List) {
				StringBuilder sb = new StringBuilder();
				appendStringContentForGraphqlQueryFromMapForAListItem(sb, (List<?>) ret);
				return sb.toString();
			} else {
				return ret.toString();
			}
		} else if (writingGraphQLVariables && val.getClass().isEnum()) {
			// When writing an enum value in the variables section, values should be between double quotes
			return "\"" + val.toString() + "\"";
		} else if (val instanceof RawGraphQLString) {
			// The value is a part of the GraphQL request. Let's write it as is.
			return ((RawGraphQLString) val).toString();
		} else if (val instanceof String) {
			// The value is a String. Let's limit it by double quotes
			return getQuotedString((String) val);
		} else if (val instanceof UUID) {
			return getQuotedString(((UUID) val).toString());
		} else if (val.getClass().getAnnotation(GraphQLInputType.class) != null) {
			return getStringContentForAnInputTypeValue(writingGraphQLVariables, val, listDepth, graphQLVariable);
		} else if (val.getClass().isEnum()) {
			return (String) graphqlUtils.invokeMethod("graphQlValue", val);
		} else {
			return val.toString();
		}
	}

	/**
	 * Write a json {@link ObjectNode} as a valid GraphQL string for an input parameter, into a GraphQL query. This is
	 * mandatory for the Object scalar.
	 * 
	 * @param node
	 *            The node to be written into the GraphQL request
	 * @return
	 * @throws GraphQLRequestExecutionException
	 */
	private void appendStringContentForGraphqlQueryFromObjectNode(StringBuilder sb, JsonParser jsonParser,
			ObjectNode node) throws GraphQLRequestExecutionException {
		try {

			JsonToken token = jsonParser.nextToken();
			boolean objectJustStarted = false;
			boolean inArray = false;
			boolean arrayJustStarted = false;

			while (jsonParser.hasCurrentToken()) {
				switch (token) {
				case START_OBJECT:
					sb.append("{");
					objectJustStarted = true;
					break;
				case END_OBJECT:
					sb.append("}");
					break;
				case START_ARRAY:
					sb.append("[");
					inArray = true;
					arrayJustStarted = true;
					break;
				case END_ARRAY:
					sb.append("]");
					break;
				case FIELD_NAME:
					if (objectJustStarted) {
						objectJustStarted = false;
					} else {
						sb.append(",");
					}
					sb.append(jsonParser.getText());
					sb.append(":");
					// Let's recurse once, to read the content of this field (which may be a value, an object or an
					// array)
					appendStringContentForGraphqlQueryFromObjectNode(sb, jsonParser, node);
					break;
				case VALUE_EMBEDDED_OBJECT:
					// Placeholder token returned when the input source has a concept of embedded Object that are not
					// accessible as usual structure (of starting with {@link #START_OBJECT}, having values, ending with
					// {@link #END_OBJECT}), but as "raw" objects.
					// Note: this token is never returned by regular JSON readers, but only by readers that expose other
					// kinds of source (like <code>JsonNode</code>-based JSON trees, Maps, Lists and such).
					// below is an attempt to render this 'strange' object
					sb.append(jsonParser.getText());
					break;
				case VALUE_STRING:
					if (arrayJustStarted) {
						arrayJustStarted = false;
					} else if (inArray) {
						sb.append(",");
					}
					sb.append('\"');
					sb.append(jsonParser.getText());
					sb.append('\"');
					break;
				case VALUE_NUMBER_INT:
					if (arrayJustStarted) {
						arrayJustStarted = false;
					} else if (inArray) {
						sb.append(",");
					}
					sb.append(jsonParser.getIntValue());
					break;
				case VALUE_NUMBER_FLOAT:
					if (arrayJustStarted) {
						arrayJustStarted = false;
					} else if (inArray) {
						sb.append(",");
					}
					sb.append(jsonParser.getFloatValue());
					break;
				case VALUE_TRUE:
					if (arrayJustStarted) {
						arrayJustStarted = false;
					} else if (inArray) {
						sb.append(",");
					}
					sb.append("true");
					break;
				case VALUE_FALSE:
					if (arrayJustStarted) {
						arrayJustStarted = false;
					} else if (inArray) {
						sb.append(",");
					}
					sb.append("false");
					break;
				case VALUE_NULL:
					if (arrayJustStarted) {
						arrayJustStarted = false;
					} else if (inArray) {
						sb.append(",");
					}
					// VALUE_NULL is returned when encountering literal "null" in value context
					sb.append("null");
					break;
				default:
					throw new GraphQLRequestExecutionException(
							"Unexpected token type while writing an ObjectNode into the GraphQL request: "
									+ token.name() + ". The json being read is: " + node.toString());
				}

				token = jsonParser.nextToken();
			} // while
		} catch (IOException e) {
			throw new GraphQLRequestExecutionException(
					e.getMessage() + " (while writing an ObjectNode into the GraphQL request)", e);
		}
	}

	/**
	 * Write a map as a valid GraphQL string for an input parameter, into a GraphQL query. This is mandatory for the
	 * Object scalar.
	 * 
	 * @param map
	 *            The map to be written into the GraphQL request
	 * @return
	 * @throws GraphQLRequestExecutionException
	 */
	protected void appendStringContentForGraphqlQueryFromMap(StringBuilder sb, Map<?, ?> map) {
		boolean appendComma = false;// false for the first item, then true for others
		sb.append('{');
		for (Object key : map.keySet()) {

			// Let's append a comma, starting from the secondary item
			if (appendComma) {
				sb.append(',');
			} else {
				appendComma = true;
			}

			// Let's append the key (without the ", as this is the difference between GraphQL and json, which makes this
			// method mandatory
			sb.append(key);
			sb.append(':');

			// Let's append the value
			appendStringContentForGraphqlQueryFromValueItem(sb, map.get(key));
		}
		sb.append('}');
	}

	/**
	 * Write a list as a valid GraphQL string for an input parameter, into a GraphQL query. This method should only be
	 * called from {@link #appendStringContentForGraphqlQueryFromMap(StringBuilder, Map)}, in order to render a value of
	 * the map which is a list.
	 * 
	 * @param sb
	 * @param list
	 */
	protected void appendStringContentForGraphqlQueryFromMapForAListItem(StringBuilder sb, List<?> list) {
		boolean appendComma = false;// false for the first item, then true for others
		sb.append('[');
		for (Object val : list) {

			// Let's append a comma, starting from the secondary item
			if (appendComma) {
				sb.append(',');
			} else {
				appendComma = true;
			}

			// Let's append the value
			appendStringContentForGraphqlQueryFromValueItem(sb, val);
		}
		sb.append(']');
	}

	/**
	 * Write a list as a valid GraphQL string for an input parameter, into a GraphQL query. This method should only be
	 * called from {@link #appendStringContentForGraphqlQueryFromMap(StringBuilder, Map)} or
	 * {@link #appendStringContentForGraphqlQueryFromMapForAListItem(StringBuilder, List)}, in order to render a value
	 * of the map or a list.
	 * 
	 * @param sb
	 * @param value
	 * @throws GraphQLRequestExecutionException
	 */
	protected void appendStringContentForGraphqlQueryFromValueItem(StringBuilder sb, Object value) {
		if (value instanceof Map) {
			appendStringContentForGraphqlQueryFromMap(sb, (Map<?, ?>) value);
		} else if (value instanceof List) {
			appendStringContentForGraphqlQueryFromMapForAListItem(sb, (List<?>) value);
		} else if (value instanceof String) {
			sb.append('\"');
			sb.append(value);
			sb.append('\"');
		} else {
			sb.append(value.toString());
		}
	}

	/**
	 * Escape a string in accordance with the rules defined for JSON strings so that it can be included in a GraphQL
	 * payload. Because a GraphQL request consists of stringified JSON objects wrapped in another JSON object, the
	 * escaping is applied twice.
	 *
	 * @param str
	 *            The String value that should be formated for the GraphQL request
	 * @see <a href="https://www.json.org/">json.org section on strings</a>
	 * @return escaped string
	 */
	protected String getQuotedString(String str) {
		String r = "\"" + StringEscapeUtils.escapeJson(str) + "\"";
		return r;
	}

	/**
	 * This method returns the JSON string that represents the given list, according to GraphQL standard. This method is
	 * used to write a part of the GraphQL client query that will be sent to the server.
	 * 
	 * @param writingGraphQLVariables
	 *            true if this call is done, while writing the value for the "variables" field of the json request.
	 * @param list
	 *            a non null List or array
	 * @param listDepth
	 *            The expected list depth for the value. listDepth is 0 if val should not be a list, 1 if val should be
	 *            a list, 2 if val should be a list of list...
	 * @param graphQLScalarTypeParam
	 *            The {@link GraphQLScalarType} for this value. It may be the same as the parameter one (for scalar), or
	 *            the one of the current field (fot input types).
	 * @return
	 * @throws GraphQLRequestExecutionException
	 * @throws NullPointerException
	 *             If list is null
	 */
	protected String getStringContentForAListValue(boolean writingGraphQLVariables, Object list, int listDepth,
			GraphQLScalarType graphQLScalarTypeParam, boolean graphQLVariable) throws GraphQLRequestExecutionException {
		StringBuilder result = new StringBuilder("[");
		List<?> localList;

		if (list.getClass().isArray()) {
			localList = Arrays.asList((Object[]) list);
		} else if (list instanceof List) {
			localList = (List<?>) list;
		} else {
			throw new GraphQLRequestExecutionException("Unexpected type for the parameter  '" + name
					+ "': it should be either a java.lang.List or an Array, but is " + list.getClass().getName());
		}

		for (int index = 0; index < localList.size(); index++) {
			Object obj = localList.get(index);
			result.append(this.getStringContentForGraphqlQuery(writingGraphQLVariables, obj, listDepth - 1,
					graphQLScalarTypeParam, graphQLVariable));
			if (index < localList.size() - 1) {
				result.append(",");
			}
		}
		return result.append("]").toString();
	}

	/**
	 * This method returns the JSON string that represents the given object, according to GraphQL standard. This method
	 * is used to write a part of the GraphQL client query that will be sent to the server.
	 * 
	 * @param writingGraphQLVariables
	 *            true if this call is done, while writing the value for the "variables" field of the json request.
	 * @param object
	 *            An object which class is an InputType as defined in the GraphQL schema
	 * @param listDepth
	 *            The expected list depth for the value. listDepth is 0 if val should not be a list, 1 if val should be
	 *            a list, 2 if val should be a list of list...
	 * @param graphQLVariable
	 *            true if the current input type should be deserialize as a GraphQL variable. In this case, it's
	 *            deserialized as a map. So the field names must be within double quotes
	 * @return The String that represents this object, according to GraphQL standard representation, as expected in the
	 *         query to be sent to the server
	 */
	protected String getStringContentForAnInputTypeValue(boolean writingGraphQLVariables, Object object, int listDepth,
			boolean graphQLVariable) throws GraphQLRequestExecutionException {
		StringBuilder result = new StringBuilder("{");
		String separator = "";

		for (Field field : object.getClass().getDeclaredFields()) {
			// Synthetic and ignored fields must be ignored
			if (!field.isSynthetic() && field.getAnnotation(GraphQLIgnore.class) == null) {
				Object val = graphqlUtils.invokeGetter(object, field.getName());

				if (val != null) {
					int fieldListDepth = 0;
					String fieldName;
					String fieldGraphQLTypeName = null;
					GraphQLScalarType fieldGraphQLType = null;

					GraphQLScalar graphQLScalar = field.getAnnotation(GraphQLScalar.class);
					GraphQLNonScalar graphQLNonScalar = field.getAnnotation(GraphQLNonScalar.class);

					if (graphQLScalar != null) {
						fieldName = graphQLScalar.fieldName();
						fieldListDepth = graphQLScalar.listDepth();
						fieldGraphQLTypeName = graphQLScalar.graphQLTypeSimpleName();
						if (fieldGraphQLTypeName != null) {
							fieldGraphQLType = CustomScalarRegistryImpl.getCustomScalarRegistry(schema)
									.getGraphQLCustomScalarType(fieldGraphQLTypeName);
						}
					} else if (graphQLNonScalar != null) {
						fieldName = graphQLNonScalar.fieldName();
						fieldListDepth = graphQLNonScalar.listDepth();
					} else {
						throw new RuntimeException("The field " + object.getClass().getName() + "." + field.getName()
								+ " should be annotated by either 'GraphQLScalar' or 'GraphQLNonScalar', but it there is no such annotation");
					}

					result//
							.append(separator)//
							.append(graphQLVariable ? "\"" : "")//
							.append(fieldName)//
							.append(graphQLVariable ? "\"" : "")//
							.append(":")//
							.append(getStringContentForGraphqlQuery(writingGraphQLVariables, val, fieldListDepth,
									fieldGraphQLType, graphQLVariable));

					separator = ",";
				}
			}
		} // for

		return result.append("}").toString();
	}

	public String getBindParameterName() {
		return bindParameterName;
	}

	public InputParameterType getType() {
		return type;
	}

	public String getGraphQLTypeName() {
		return graphQLTypeName;
	}

	public GraphQLScalarType getGraphQLScalarType() {
		return graphQLScalarType;
	}

	public boolean isItemMandatory() {
		return itemMandatory;
	}

	public int getListDepth() {
		return listDepth;
	}

	public boolean isMandatory() {
		return mandatory;
	}

	/**
	 * 
	 * @param writingGraphQLVariables
	 *            true if this call is done, while writing the value for the "variables" field of the json request.
	 * @param sb
	 * @param inputParameters
	 * @param parameters
	 * @throws GraphQLRequestExecutionException
	 */
	public static void appendInputParametersToGraphQLRequests(boolean writingGraphQLVariables, StringBuilder sb,
			List<InputParameter> inputParameters, Map<String, Object> parameters)
			throws GraphQLRequestExecutionException {

		if (inputParameters != null && inputParameters.size() > 0) {
			// Let's list the non null parameters ...
			List<String> params = new ArrayList<String>();
			for (InputParameter param : inputParameters) {
				String stringValue = param.getStringContentForGraphqlQuery(writingGraphQLVariables, parameters);
				if (stringValue != null) {
					params.add(param.getName() + ":" + stringValue);
				}
			}
			// ... in order to generate the list of parameters to send to the server
			if (params.size() > 0) {
				sb.append("(");
				boolean writeComma = false;
				for (String param : params) {
					if (writeComma) {
						sb.append(",");
					}
					writeComma = true;
					sb.append(param);
				} // for
				sb.append(")");
			}
		}
	}

}
