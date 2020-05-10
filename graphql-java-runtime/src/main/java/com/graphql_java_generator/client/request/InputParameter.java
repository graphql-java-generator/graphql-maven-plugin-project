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

import com.graphql_java_generator.GraphqlUtils;
import com.graphql_java_generator.annotation.GraphQLInputParameters;
import com.graphql_java_generator.annotation.GraphQLInputType;
import com.graphql_java_generator.client.GraphqlClientUtils;
import com.graphql_java_generator.client.QueryExecutorImpl;
import com.graphql_java_generator.client.directive.Directive;
import com.graphql_java_generator.customscalars.CustomScalarRegistryImpl;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;

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

	/** Indicates what is being read by the {@link #readTokenizerForInputParameters(StringTokenizer) method */
	private enum InputParameterStep {
		NAME, VALUE
	};

	/** The parameter name, as defined in the GraphQL schema */
	final String name;

	/**
	 * The bind parameter, as defined in the GraphQL query. <BR/>
	 * For instance <I>sinceParam</I> in <I>posts(since: :sinceParam) {date}</I>
	 */
	final String bindParameterName;

	/** The value to send, for this input parameter */
	final Object value;

	/** Indicates whether this parameter is mandatory or not */
	final boolean mandatory;

	/**
	 * If this input parameter's type is a GraphQL Custom Scalar, it is initialized in the constructor. Otherwise, it is
	 * null. <BR/>
	 * graphQLCustomScalarType contains the {@link GraphQLScalarType} that allows to convert the value to a String that
	 * can be written in the GraphQL request, or convert from a String that is found in the GraphQL response. If this
	 * type is not a GraphQL Custom Scalar, it must be null.
	 * 
	 */
	final GraphQLScalarType graphQLCustomScalarType;

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
	 * @param mandatory
	 *            true if the parameter's value must be defined during request/mutation/subscription execution. <BR/>
	 *            If mandatory is true and the parameter's value is not provided, a
	 *            {@link GraphQLRequestExecutionException} exception is thrown at execution time<BR/>
	 *            If mandatory is false and the parameter's value is not provided, this input parameter is not sent to
	 *            the server
	 * @return
	 * @see QueryExecutorImpl#execute(String, ObjectResponse, List, Class)
	 */
	public static InputParameter newBindParameter(String name, String bindParameterName, boolean mandatory) {
		if (bindParameterName == null) {
			throw new NullPointerException("[Internal error] The bindParameterName is mandatory");
		}
		return InputParameter.newBindParameter(name, bindParameterName, mandatory, null);
	}

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
	 * @param mandatory
	 *            true if the parameter's value must be defined during request/mutation/subscription execution. <BR/>
	 *            If mandatory is true and the parameter's value is not provided, a
	 *            {@link GraphQLRequestExecutionException} exception is thrown at execution time<BR/>
	 *            If mandatory is false and the parameter's value is not provided, this input parameter is not sent to
	 *            the server
	 * @param graphQLCustomScalarType
	 *            If this input parameter's type is a GraphQL Custom Scalar, it must be provided. Otherwise, it must be
	 *            null. <BR/>
	 *            graphQLCustomScalarType contains the {@link GraphQLScalarType} that allows to convert the value to a
	 *            String that can be written in the GraphQL request, or convert from a String that is found in the
	 *            GraphQL response. If this type is not a GraphQL Custom Scalar, it must be null.
	 * @return
	 * @see QueryExecutorImpl#execute(String, ObjectResponse, List, Class)
	 */
	public static InputParameter newBindParameter(String name, String bindParameterName, boolean mandatory,
			GraphQLScalarType graphQLScalarType) {
		if (bindParameterName == null) {
			throw new NullPointerException("[Internal error] The bindParameterName is mandatory");
		}
		return new InputParameter(name, bindParameterName, null, mandatory, graphQLScalarType);
	}

	/**
	 * Creates and returns a new instance of {@link InputParameter}, which value is given, and can not be changed
	 * afterwards
	 * 
	 * @param name
	 * @param value
	 * @return
	 */
	public static InputParameter newHardCodedParameter(String name, Object value) {
		return new InputParameter(name, null, value, true, null);
	}

	/**
	 * Creates and returns a new instance of {@link InputParameter}, which value is given, and can not be changed
	 * afterwards.
	 * 
	 * @param name
	 * @param value
	 * @param mandatory
	 * @param type
	 * @return
	 */
	public static InputParameter newHardCodedParameter(String name, Object value, boolean mandatory,
			GraphQLScalarType type) {
		return new InputParameter(name, null, value, mandatory, type);
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
	 * @param mandatory
	 *            true if the parameter's value must be defined during request/mutation/subscription execution. <BR/>
	 *            If mandatory is true and the parameter's value is not provided, a
	 *            {@link GraphQLRequestExecutionException} exception is thrown at execution time<BR/>
	 *            If mandatory is false and the parameter's value is not provided, this input parameter is not sent to
	 *            the server
	 * @param graphQLCustomScalarType
	 *            If this input parameter's type is a GraphQL Custom Scalar, it must be provided. Otherwise, it must be
	 *            null. <BR/>
	 *            graphQLCustomScalarType contains the {@link GraphQLScalarType} that allows to convert the value to a
	 *            String that can be written in the GraphQL request, or convert from a String that is found in the
	 *            GraphQL response. If this type is not a GraphQL Custom Scalar, it must be null.
	 */
	private InputParameter(String name, String bindParameterName, Object value, boolean mandatory,
			GraphQLScalarType graphQLCustomScalarType) {
		if (name == null) {
			throw new NullPointerException("The input parameter's name is mandatory");
		}

		this.name = name;
		this.bindParameterName = bindParameterName;
		this.value = value;
		this.mandatory = mandatory;
		this.graphQLCustomScalarType = graphQLCustomScalarType;
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
	 * @param packageName
	 *            The package name is necessary to load the generated classes, to read the metadata that has been
	 *            generated
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
			case "{":
				throw new GraphQLRequestPreparationException(
						"Encountered a '{' while reading parameters for the field '" + fieldName
								+ "' : if this is an input type as a parameter value, please use bind variable instead. "
								+ "For instance: \"" + parameterName + ":?" + parameterName
								+ "Param\", and provide a value for the " + parameterName + "Param bind parameter");
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
						ret.add(new InputParameter(parameterName, token.substring(1), null, false,
								graphqlClientUtils.getGraphQLType(directive, owningClass, fieldName, parameterName)));
					} else if (token.startsWith("&")) {
						ret.add(new InputParameter(parameterName, token.substring(1), null, true,
								graphqlClientUtils.getGraphQLType(directive, owningClass, fieldName, parameterName)));
					} else if (token.equals("\"")) {
						// We've found a String value: let's read the string content
						StringBuffer sb = new StringBuffer();
						while (true) {
							if (!qt.hasMoreTokens(true)) {
								throw new GraphQLRequestPreparationException(
										"Found the end of string before the end of the string parameter '"
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
						ret.add(new InputParameter(parameterName, null, sb.toString(), true, null));
					} else if (token.startsWith("\"") || token.endsWith("\"")) {
						// Too bad, there is a " only at the end or only at the beginning
						throw new GraphQLRequestPreparationException(
								"Bad parameter value: parameter values should start and finish by \", or not having any \" at the beginning and end."
										+ " But it's not the case for the value <" + token + "> of parameter <"
										+ parameterName
										+ ">. Maybe you wanted to add a bind parameter instead (bind parameter must start with a ? or a &");
					} else if (directive != null) {
						Object parameterValue = parseDirectiveArgumentValue(directive, parameterName, token);
						InputParameter arg = new InputParameter(parameterName, null, parameterValue, true, null);
						ret.add(arg);
						directive.getArguments().add(arg);
					} else {
						Object parameterValue = parseInputParameterValue(owningClass, fieldName, parameterName, token);
						ret.add(new InputParameter(parameterName, null, parameterValue, true, null));
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
					return parseValueForInputParameter(parameterValue, param.getGraphQLScalarType().getName(),
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

		// Let's check if this type is a Custom Scalar
		GraphQLScalarType scalarType = CustomScalarRegistryImpl.customScalarRegistry
				.getGraphQLScalarType(parameterType);

		if (scalarType != null) {
			// This type is a Custom Scalar. Let's ask the CustomScalar implementation to translate this value.
			return scalarType.getCoercing().parseValue(parameterValue);
		} else if (parameterType.equals("Boolean")) {
			if (parameterValue.equals("true"))
				return Boolean.TRUE;
			else if (parameterValue.equals("false"))
				return Boolean.FALSE;
			else
				throw new GraphQLRequestPreparationException(
						"Bad boolean value '" + parameterValue + "' for the parameter type '" + parameterType + "'");
		} else if (parameterType.equals("ID")) {
			return parameterValue;
		} else if (parameterType.equals("Float")) {
			// GraphQL Float are double precision numbers
			return Double.parseDouble(parameterValue);
		} else if (parameterType.equals("Int")) {
			return Integer.parseInt(parameterValue);
		} else if (parameterType.equals("Long")) {
			return Long.parseLong(parameterValue);
		} else if (parameterType.equals("String")) {
			return parameterValue;
		} else {
			// This type is not a Custom Scalar, so it must be a standard Scalar. Let's manage it
			String parameterClassname = packageName + "." + graphqlUtils.getJavaName(parameterType);
			Class<?> parameterClass;
			try {
				parameterClass = Class.forName(parameterClassname);
			} catch (ClassNotFoundException e) {
				throw new GraphQLRequestPreparationException(
						"Couldn't find the class (" + parameterClassname + ") of the type '" + parameterType + "'", e);
			}

			if (parameterClass.isEnum()) {
				// This parameter is an enum. The parameterValue is one of its elements
				Method valueOf = graphqlUtils.getMethod("valueOf", parameterClass, String.class);
				return graphqlUtils.invokeMethod(valueOf, null, parameterValue);
			} else if (parameterClass.isAssignableFrom(Boolean.class)) {
				// This parameter is a boolean. Only true and false are valid boolean.
				if (!"true".equals(parameterValue) && !"false".equals(parameterValue)) {
					throw new GraphQLRequestPreparationException(
							"Only true and false are allowed values for booleans, but the value is '" + parameterValue
									+ "'");
				}
				return "true".equals(parameterValue);
			} else if (parameterClass.isAssignableFrom(Integer.class)) {
				return Integer.parseInt(parameterValue);
			} else if (parameterClass.isAssignableFrom(Float.class)) {
				return Float.parseFloat(parameterValue);
			}
		} // else (scalarType != null)

		// Too bad...
		throw new GraphQLRequestPreparationException(
				"Couldn't parse the value'" + parameterValue + "' for the parameter type '" + parameterType + "'");
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
	 * @param bindVariables
	 *            The map for the bind variables. It may be null, if this input parameter is a hard coded one. If this
	 *            parameter is a Bind Variable, then bindVariables is mandatory, and it must contain a value for th bind
	 *            parameter which name is stored in {@link #bindParameterName}.
	 * @return
	 * @throws GraphQLRequestExecutionException
	 */
	public String getValueForGraphqlQuery(Map<String, Object> bindVariables) throws GraphQLRequestExecutionException {
		if (this.bindParameterName == null) {
			// It's a hard coded value
			return this.getValueForGraphqlQuery(this.value, graphQLCustomScalarType);
		} else
		// It's a Bind Variable.

		// If the InputParameter is mandatory, which must have its value in the map of BindVariables.
		if (mandatory && (bindVariables == null || !bindVariables.keySet().contains(this.bindParameterName))) {
			throw new GraphQLRequestExecutionException("The Bind Parameter for '" + this.bindParameterName
					+ "' must be provided in the BindVariables map");
		}

		if (bindVariables == null || !bindVariables.keySet().contains(this.bindParameterName))
			return null;
		else
			return this.getValueForGraphqlQuery(bindVariables.get(this.bindParameterName), graphQLCustomScalarType);
	}

	/**
	 * This method is used both by {@link #getValueForGraphqlQuery()} and {@link #getListValue(List)} to extract a value
	 * as a string.
	 * 
	 * @param val
	 *            This value of the parameter. It can be the {@link #value} if it is not null, or the binding from the
	 *            bind parameters. It's up to the caller to map the bind parameter into this method argument.
	 * @return
	 * @throws GraphQLRequestExecutionException
	 */
	String getValueForGraphqlQuery(Object val, GraphQLScalarType graphQLScalarType)
			throws GraphQLRequestExecutionException {
		if (val == null) {
			return null;
		} else if (val instanceof java.util.List) {
			return getListValue((List<?>) val, graphQLScalarType);
		} else if (graphQLScalarType != null) {
			Object ret = graphQLScalarType.getCoercing().serialize(val);
			if (ret instanceof String)
				return getStringValue((String) ret);
			else
				return ret.toString();
		} else if (val instanceof String) {
			return getStringValue((String) val);
		} else if (val instanceof UUID) {
			return getStringValue(((UUID) val).toString());
		} else if (val.getClass().getAnnotation(GraphQLInputType.class) != null) {
			return getInputTypeStringValue(val);
		} else {
			return val.toString();
		}
	}

	/**
	 * @return
	 */
	private String getStringValue(String str) {
		return "\\\"" + str.replace("\"", "\\\"") + "\\\"";
	}

	/**
	 * This method returns the JSON string that represents the given list, according to GraphQL standard. This method is
	 * used to write a part of the GraphQL client query that will be sent to the server.
	 * 
	 * @param list
	 *            a non null List
	 * @return
	 * @throws GraphQLRequestExecutionException
	 * @throws NullPointerException
	 *             If lst is null
	 */
	private String getListValue(List<?> list, GraphQLScalarType graphQLScalarType)
			throws GraphQLRequestExecutionException {
		StringBuilder result = new StringBuilder("[");
		for (int index = 0; index < list.size(); index++) {
			Object obj = list.get(index);
			result.append(this.getValueForGraphqlQuery(obj, graphQLScalarType));
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
	 * @param object
	 *            An object which class is an InputType as defined in the GraphQL schema
	 * @return The String that represents this object, according to GraphQL standard representation, as expected in the
	 *         query to be sent to the server
	 */
	private String getInputTypeStringValue(Object object) throws GraphQLRequestExecutionException {
		StringBuilder result = new StringBuilder("{");
		String separator = "";

		for (Field field : object.getClass().getDeclaredFields()) {
			Object val = graphqlUtils.invokeGetter(object, field.getName());

			if (val != null) {
				result.append(separator);

				result.append(field.getName());
				result.append(":");
				result.append(getValueForGraphqlQuery(val, graphqlClientUtils.getGraphQLCustomScalarType(field)));

				separator = ",";
			}
		} // for

		return result.append("}").toString();
	}

	public String getBindParameterName() {
		return bindParameterName;
	}

	public boolean isMandatory() {
		return mandatory;
	}

	public GraphQLScalarType getGraphQLScalarType() {
		return graphQLCustomScalarType;
	}

	public static void appendInputParametersToGraphQLRequests(StringBuilder sb, List<InputParameter> inputParameters,
			Map<String, Object> parameters) throws GraphQLRequestExecutionException {

		if (inputParameters != null && inputParameters.size() > 0) {
			// Let's list the non null parameters ...
			List<String> params = new ArrayList<String>();
			for (InputParameter param : inputParameters) {
				String stringValue = param.getValueForGraphqlQuery(parameters);
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
