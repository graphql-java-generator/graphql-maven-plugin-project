/**
 *
 */
package com.graphql_java_generator.client.request;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.UUID;

import org.apache.commons.text.StringEscapeUtils;

import com.graphql_java_generator.annotation.GraphQLInputParameters;
import com.graphql_java_generator.annotation.GraphQLInputType;
import com.graphql_java_generator.client.GraphqlClientUtils;
import com.graphql_java_generator.client.QueryExecutorSpringReactiveImpl;
import com.graphql_java_generator.client.directive.Directive;
import com.graphql_java_generator.client.directive.DirectiveRegistry;
import com.graphql_java_generator.client.directive.DirectiveRegistryImpl;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;
import com.graphql_java_generator.util.GraphqlUtils;

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

	/** The registry for all known GraphQL directives. */
	DirectiveRegistry directiveRegistry = DirectiveRegistryImpl.directiveRegistry;

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
		/** A GraphQL value provided in the request being parsed */
		GRAPHQL_VALUE,
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

	/** The parameter name, as defined in the GraphQL schema */
	final String name;

	/**
	 * The bind parameter, as defined in the GraphQL query. <BR/>
	 * For instance <I>sinceParam</I> in <I>posts(since: &sinceParam) {date}</I>
	 */
	final String bindParameterName;

	/** The value to send, for this input parameter */
	final Object value;

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
	 * @see QueryExecutorSpringReactiveImpl#execute(String, ObjectResponse, List, Class)
	 */
	public static InputParameter newBindParameter(String name, String bindParameterName, InputParameterType type,
			String graphQLTypeName, boolean mandatory, int listDepth, boolean itemMandatory) {
		if (bindParameterName == null) {
			throw new NullPointerException("[Internal error] The bind parameter name is mandatory");
		}
		return new InputParameter(name, bindParameterName, null, type, graphQLTypeName, mandatory, listDepth,
				itemMandatory);
	}

	/**
	 * Creates and returns a new instance of {@link InputParameter}, which value is given, and can not be changed
	 * afterwards.
	 *
	 * @param name
	 *            The parameter name, as defined in the GraphQL schema
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
	public static InputParameter newGraphQLVariableParameter(String name, String graphQLTypeName, boolean mandatory,
			int listDepth, boolean itemMandatory) {
		return new InputParameter(name, name, null, InputParameterType.GRAPHQL_VARIABLE, graphQLTypeName, mandatory,
				listDepth, itemMandatory);
	}

	/**
	 * Creates and returns a new instance of {@link InputParameter}, which value is given, and can not be changed
	 * afterwards.
	 *
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
	public static InputParameter newHardCodedParameter(String name, Object value, String graphQLTypeName,
			boolean mandatory, int listDepth, boolean itemMandatory) {
		return new InputParameter(name, null, value, InputParameterType.HARD_CODED, graphQLTypeName, mandatory,
				listDepth, itemMandatory);
	}

	/**
	 * The constructor is private. Instances must be created with one of these helper methods:
	 * {@link #newBindParameter(String, String)} or {@link #newHardCodedParameter(String, Object)}
	 *
	 * @param name
	 *            The parameter name, as defined in the GraphQL schema
	 * @param bindParameterName
	 *            The name of the bind parameter, as defined in the GraphQL response definition. If null, it's a hard
	 *            coded value. The value will be sent to the server, when the request is executed. Please read the
	 *            <A HREF="https://graphql-maven-plugin-project.graphql-java-generator.com/client.html">client doc</A>
	 *            for more information on input parameters and bind variables.
	 * @param value
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
	private InputParameter(String name, String bindParameterName, Object value, InputParameterType type,
			String graphQLTypeName, boolean mandatory, int listDepth, boolean itemMandatory) {
		if (name == null) {
			throw new NullPointerException("The input parameter's name is mandatory");
		}

		this.name = name;
		this.bindParameterName = bindParameterName;
		this.value = value;
		this.type = type;
		this.graphQLTypeName = graphQLTypeName;
		this.graphQLScalarType = (graphQLTypeName == null) ? null
				: graphqlClientUtils.getGraphQLScalarTypeFromName(graphQLTypeName);
		this.mandatory = mandatory;
		this.listDepth = listDepth;
		this.itemMandatory = itemMandatory;
	}

	/**
	 * The constructor is private. Instances must be created with one of these helper methods:
	 * {@link #newBindParameter(String, String)} or {@link #newHardCodedParameter(String, Object)}
	 *
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
	private InputParameter(String name, String parameterName, Object value, InputParameterType type,
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
			Directive dirDef = directiveRegistry.getDirective(directive.getName());
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

			if (inputParams == null)
				throw new GraphQLRequestPreparationException("The field <" + fieldName + "> of the class '"
						+ owningClass.getName() + "' has no input parameters. Error while looking for its '"
						+ parameterName + "' input parameter");

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

		this.name = name;
		this.bindParameterName = parameterName;
		this.value = value;
		this.type = type;
		this.graphQLTypeName = localGraphQLCustomScalarType;
		this.graphQLScalarType = graphqlClientUtils.getGraphQLScalarTypeFromName(graphQLTypeName);
		this.mandatory = localMandatory;
		this.listDepth = localList;
		this.itemMandatory = localItemMandatory;
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
			Class<?> owningClass, String fieldName) throws GraphQLRequestPreparationException {
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
						ret.add(new InputParameter(parameterName, token.substring(1), null, InputParameterType.OPTIONAL,
								directive, owningClass, fieldName));
					} else if (token.startsWith("&")) {
						ret.add(new InputParameter(parameterName, token.substring(1), null,
								InputParameterType.MANDATORY, directive, owningClass, fieldName));
					} else if (token.startsWith("$")) {
						ret.add(new InputParameter(parameterName, token.substring(1), null,
								InputParameterType.GRAPHQL_VARIABLE, directive, owningClass, fieldName));
					} else if (token.equals("[") || token.equals("{")) {
						// We've found the start of a JSON list or JSON object. Let's read this object.
						// We'll store it as a String, and write it back in the request toward the GraphQL server
						// request
						StringBuffer sb = new StringBuffer(token);
						String previousToken;
						boolean list = token.startsWith("[");
						int listDepth = 0;
						boolean withinAString = false;
						int recursiveLevel = 1;// Counts the depth of [ or { we're in. When it's back to 0, we're done
												// reading this list or object
						while (true) {
							if (!qt.hasMoreTokens(true)) {
								throw new GraphQLRequestPreparationException(
										"Found the end of the GraphQL request before the end of the "
												+ (list ? "list" : "object") + ": '" + sb.toString() + "'");
							}
							previousToken = token;
							token = qt.nextToken(true);
							if (token.contentEquals("\"")) {
								// We've found a double quote. So we probably are starting or leaving a string.

								// But perhaps we're within a String, and we've just read an escaped double quote. So,
								// then we're still in the string (it's not the end of the string).
								// As the query parameter is a string, the string delimiters are thesemlves escaped. So
								// an escaped double-quote within a string of a paremeter needs two \ to be escaped
								// within a string parameter in the query string of the json.
								boolean doubleQuoteEscapedWithinAString = withinAString && previousToken.endsWith("\\");
								if (doubleQuoteEscapedWithinAString) {
									// We must escape the previous '\', then escape the '"'
									sb.append("\\\\");
								} else {
									// We've found the start or the end of the string value. This important, as []{}
									// characters should be ignored, when in a string
									withinAString = !withinAString;
									// We must escape this '"', as in the JSON query, this string is itself in a String
									sb.append("\\");
								}
							}
							sb.append(token);
							if (!withinAString) {
								if ((list && token.equals("[") || (!list && token.equals("{")))) {
									// We're going deeper in the list or object
									recursiveLevel += 1;
									listDepth += 1;
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
						ret.add(new InputParameter(parameterName, null, new RawGraphQLString(sb.toString()),
								InputParameterType.GRAPHQL_VALUE, null, false, listDepth, false));
					} else if (token.equals("\"")) {
						// We've found a String value: let's read the string content
						StringBuffer sb = new StringBuffer();
						while (true) {
							if (!qt.hasMoreTokens(true)) {
								throw new GraphQLRequestPreparationException(
										"Found the end of the GraphQL request before the end of the string parameter '"
												+ sb.toString() + "'");
							}
							token = qt.nextToken(true);
							if (token.contentEquals("\"")) {
								// We've found the end of the string value.
								break;
							}
							sb.append(token);
							if (token.equals("\\")) {
								// It's the escape character. We add the next token, as is. Especially if it's a double
								// quote (as a double quote here doens't mean we found the end of the string)
								sb.append(qt.nextToken(true));
							}

						} // while (true)

						// It's a regular String.
						// The inputParameters mandatory, list and itemMandatory are forced (as theses attributes are
						// not used in this case)
						ret.add(new InputParameter(parameterName, null, sb.toString(), InputParameterType.HARD_CODED,
								"String", true, 0, false));
					} else if (token.startsWith("\"") || token.endsWith("\"")) {
						// Too bad, there is a " only at the end or only at the beginning
						throw new GraphQLRequestPreparationException(
								"Bad parameter value: parameter values should start and finish by \", or not having any \" at the beginning and end."
										+ " But it's not the case for the value <" + token + "> of parameter <"
										+ parameterName
										+ ">. Maybe you wanted to add a bind parameter instead (bind parameter must start with a ? or a &");
					} else if (directive != null) {
						Object parameterValue = parseDirectiveArgumentValue(directive, parameterName, token);
						// The inputParameters mandatory, list and itemMandatory are forced (as theses attributes are
						// not used in this case)
						InputParameter arg = new InputParameter(parameterName, null, parameterValue,
								InputParameterType.HARD_CODED, null, true, 0, false);
						ret.add(arg);
						directive.getArguments().add(arg);
					} else {
						Object parameterValue = parseInputParameterValue(owningClass, fieldName, parameterName, token);
						// The inputParameters mandatory, list and itemMandatory are forced (as theses attributes are
						// not used in this case)
						ret.add(new InputParameter(parameterName, null, parameterValue, InputParameterType.HARD_CODED,
								null, true, 0, false));
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
	 * Parse a value read for an input parameter, within the query
	 *
	 * @param owningClass
	 * @param fieldName
	 * @param parameterName
	 * @param parameterValue
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	private static Object parseInputParameterValue(Class<?> owningClass, String fieldName, String parameterName,
			String parameterValue) throws GraphQLRequestPreparationException {
		Field field = graphqlUtils.getDeclaredField(owningClass, graphqlUtils.getJavaName(fieldName), true);

		GraphQLInputParameters graphQLInputParameters = field.getDeclaredAnnotation(GraphQLInputParameters.class);
		if (graphQLInputParameters == null) {
			throw new GraphQLRequestPreparationException(
					"[Internal error] The field '" + fieldName + "' is lacking the GraphQLInputParameters annotation");
		}

		for (int i = 0; i < graphQLInputParameters.names().length; i += 1) {
			if (graphQLInputParameters.names()[i].equals(parameterName)) {
				// We've found the parameterType. Let's get its value.
				try {
					return parseValueForInputParameter(parameterValue, graphQLInputParameters.types()[i],
							owningClass.getPackage().getName());
				} catch (Exception e) {
					throw new GraphQLRequestPreparationException(
							"Could not read the value for the parameter '" + parameterName + "' of the field '"
									+ fieldName + "' of the type '" + owningClass.getName() + "'");
				}
			}
		}

		// Too bad...
		throw new GraphQLRequestPreparationException("[Internal error] Can't find the type for the parameter '"
				+ parameterName + "' of the field '" + fieldName + "'");
	}

	private static Object parseDirectiveArgumentValue(Directive directive, String parameterName, String parameterValue)
			throws GraphQLRequestPreparationException {
		// Let's find the directive definition for this read directive
		Directive directiveDefinition = directive.getDirectiveDefinition();

		// Let's find the parameter type, so that we can call parseValueForInputParameter method
		for (InputParameter param : directiveDefinition.getArguments()) {
			if (param.getName().equals(parameterName)) {
				// We've found the parameterType. Let's get its value.
				try {
					return parseValueForInputParameter(parameterValue, param.getGraphQLTypeName(),
							directive.getPackageName());
				} catch (Exception e) {
					throw new GraphQLRequestPreparationException("Could not read the value for the parameter '"
							+ parameterName + "' of the directive '" + directive.getName() + "'", e);
				}
			}
		}

		// Too bad...
		throw new GraphQLRequestPreparationException("[Internal error] Can't find the argument '" + parameterName
				+ "' of the directive '" + directive.getName() + "'");
	}

	/**
	 * Parse a value, depending on the parameter type.
	 *
	 * @param parameterValue
	 * @param parameterType
	 * @param packageName
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	private static Object parseValueForInputParameter(String parameterValue, String parameterType, String packageName)
			throws GraphQLRequestPreparationException {
		try {
			return graphqlUtils.parseValueForInputParameter(parameterValue, parameterType,
					graphqlUtils.getClass(packageName, parameterType));
		} catch (RuntimeException e) {
			throw new GraphQLRequestPreparationException(e.getMessage(), e);
		}
	}

	public String getName() {
		return name;
	}

	public Object getValue() {
		return value;
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
	public String getValueForGraphqlQuery(boolean writingGraphQLVariables, Map<String, Object> bindVariables)
			throws GraphQLRequestExecutionException {
		if (this.bindParameterName == null) {
			// It's a hard coded value
			return this.getValueForGraphqlQuery(writingGraphQLVariables, this.value, graphQLTypeName, graphQLScalarType,
					false);
		}
		// It's a Bind Variable.

		// If the InputParameter is mandatory, which must have its value in the map of BindVariables.
		if ((type.equals(InputParameterType.MANDATORY) || type.equals(InputParameterType.GRAPHQL_VARIABLE))
				&& (bindVariables == null || !bindVariables.keySet().contains(this.bindParameterName))) {
			throw new GraphQLRequestExecutionException("The Bind Parameter for '" + this.bindParameterName
					+ "' must be provided in the BindVariables map");
		}

		if (bindVariables == null || !bindVariables.keySet().contains(this.bindParameterName))
			return null;
		else
			return this.getValueForGraphqlQuery(writingGraphQLVariables, bindVariables.get(this.bindParameterName),
					graphQLTypeName, graphQLScalarType, type.equals(InputParameterType.GRAPHQL_VARIABLE));
	}

	/**
	 * This method is used both by {@link #getValueForGraphqlQuery()} and {@link #getListValue(List)} to extract a value
	 * as a string.
	 *
	 * @param writingGraphQLVariables
	 *            true if this call is done, while writing the value for the "variables" field of the json request.
	 * @param val
	 *            This value of the parameter. It can be the {@link #value} if it is not null, or the binding from the
	 *            bind parameters. It's up to the caller to map the bind parameter into this method argument.
	 * @param graphQLTypeName
	 * @param graphQLScalarType
	 *            The {@link GraphQLScalarType} for this value. It may be the same as the parameter one (for scalar), or
	 *            the one of the current field (for input types).
	 * @param graphQLVariable
	 *            true if the current input type should be deserialize as a GraphQL variable. In this case, it's
	 *            deserialized as a map. So the field names must be within double quotes
	 * @return
	 * @throws GraphQLRequestExecutionException
	 */
	String getValueForGraphqlQuery(boolean writingGraphQLVariables, Object val, String graphQLTypeName,
			GraphQLScalarType graphQLScalarType, boolean graphQLVariable) throws GraphQLRequestExecutionException {
		if (val == null) {
			return null;
		} else if (graphQLVariable && !writingGraphQLVariables) {
			// When writing a GraphQL variable in the query itself, then we write the variable name. The value is
			// written only in the GraphQL variable field
			return "$" + bindParameterName;
		} else if (writingGraphQLVariables && val.getClass().isEnum()) {
			// When writing an enum value in the variavles section, values should be between double quotes
			return "\"" + val.toString() + "\"";
		} else if (val instanceof java.util.List) {
			return getListValue(writingGraphQLVariables, (List<?>) val, graphQLTypeName, graphQLScalarType,
					graphQLVariable);
		} else if (graphQLScalarType != null) {
			Object ret = graphQLScalarType.getCoercing().serialize(val);
			if (ret instanceof String)
				return getStringValue((String) ret, graphQLVariable);
			else
				return ret.toString();
		} else if (val instanceof RawGraphQLString) {
			// The value is a part of the GraphQL request. Let's write it as is.
			return ((RawGraphQLString) val).toString();
		} else if (val instanceof String) {
			// The value is a String. Let's limit it by double quotes
			return getStringValue((String) val, graphQLVariable);
		} else if (val instanceof UUID) {
			return getStringValue(((UUID) val).toString(), graphQLVariable);
		} else if (val.getClass().getAnnotation(GraphQLInputType.class) != null) {
			return getInputTypeStringValue(writingGraphQLVariables, val, graphQLVariable);
		} else {
			return val.toString();
		}
	}

	/**
	 * Escape a string in accordance with the rules defined for JSON strings so that it can be included in a GraphQL
	 * payload. Because a GraphQL request consists of stringified JSON objects wrapped in another JSON object, the
	 * escaping is applied twice.
	 *
	 * @param str
	 *            The String value that should be formated for the GraphQL request
	 * @param graphQLVariable
	 *            true if the current input type should be deserialize as a GraphQL variable. In this case, the string
	 *            should not be escaped. Otherwise, the string value is in the query parameter, which is a string. So
	 *            the double quotes must be escaped
	 * @see <a href="https://www.json.org/">json.org section on strings</a>
	 * @return escaped string
	 */
	private String getStringValue(String str, boolean graphQLVariable) {
		return ""//
				+ (graphQLVariable ? "" : "\\")//
				+ "\"" //
				+ StringEscapeUtils.escapeJson(StringEscapeUtils.escapeJson(str)) //
				+ (graphQLVariable ? "" : "\\")//
				+ "\"";
	}

	/**
	 * This method returns the JSON string that represents the given list, according to GraphQL standard. This method is
	 * used to write a part of the GraphQL client query that will be sent to the server.
	 * 
	 * @param writingGraphQLVariables
	 *            true if this call is done, while writing the value for the "variables" field of the json request.
	 * @param list
	 *            a non null List
	 * @param graphQLVariable
	 *            true if the current input type should be deserialize as a GraphQL variable. In this case, it's
	 *            deserialized as a map. So the field names must be within double quotes
	 * @param graphQLScalarType
	 *            The {@link GraphQLScalarType} for this value. It may be the same as the parameter one (for scalar), or
	 *            the one of the current field (fot input types).
	 * @return
	 * @throws GraphQLRequestExecutionException
	 * @throws NullPointerException
	 *             If list is null
	 */
	private String getListValue(boolean writingGraphQLVariables, List<?> list, String graphQLTypeName,
			GraphQLScalarType graphQLScalarType, boolean graphQLVariable) throws GraphQLRequestExecutionException {
		StringBuilder result = new StringBuilder("[");
		for (int index = 0; index < list.size(); index++) {
			Object obj = list.get(index);
			result.append(this.getValueForGraphqlQuery(writingGraphQLVariables, obj, graphQLTypeName, graphQLScalarType,
					graphQLVariable));
			if (index < list.size() - 1) {
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
	 * @param graphQLVariable
	 *            true if the current input type should be deserialize as a GraphQL variable. In this case, it's
	 *            deserialized as a map. So the field names must be within double quotes
	 * @return The String that represents this object, according to GraphQL standard representation, as expected in the
	 *         query to be sent to the server
	 */
	private String getInputTypeStringValue(boolean writingGraphQLVariables, Object object, boolean graphQLVariable)
			throws GraphQLRequestExecutionException {
		StringBuilder result = new StringBuilder("{");
		String separator = "";

		for (Field field : object.getClass().getDeclaredFields()) {
			// Synthetic fields must be ignored
			if (!field.isSynthetic()) {
				Object val = graphqlUtils.invokeGetter(object, field.getName());

				if (val != null) {
					result//
							.append(separator)//
							.append(graphQLVariable ? "\"" : "")//
							.append(field.getName()).append(graphQLVariable ? "\"" : "")//
							.append(":")//
							.append(getValueForGraphqlQuery(writingGraphQLVariables, val, graphQLTypeName,
									graphqlClientUtils.getGraphQLCustomScalarType(field), graphQLVariable));

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
				String stringValue = param.getValueForGraphqlQuery(writingGraphQLVariables, parameters);
				if (stringValue != null) {
					params.add(param.getName() + ":" + stringValue);
				}
			}
			// ... in order to generate the list of parameters to send to the server
			if (params.size() > 0) {
				sb.append("(");
				boolean writeComma = false;
				for (String param : params) {
					if (writeComma)
						sb.append(",");
					writeComma = true;
					sb.append(param);
				} // for
				sb.append(")");
			}
		}
	}

}
