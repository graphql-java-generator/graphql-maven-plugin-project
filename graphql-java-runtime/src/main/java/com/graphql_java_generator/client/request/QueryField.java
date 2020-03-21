package com.graphql_java_generator.client.request;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.graphql_java_generator.GraphqlUtils;
import com.graphql_java_generator.annotation.GraphQLInputParameters;
import com.graphql_java_generator.client.GraphqlClientUtils;
import com.graphql_java_generator.client.directive.Directive;
import com.graphql_java_generator.client.directive.DirectiveRegistry;
import com.graphql_java_generator.client.directive.DirectiveRegistryImpl;
import com.graphql_java_generator.customscalars.CustomScalarRegistryImpl;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;

import graphql.schema.GraphQLScalarType;

/**
 * This class gives parsing capabilities for the QueryString for one object.<BR/>
 * For instance, for the GraphQL query <I>queryType.boards("{id name publiclyAvailable topics(since:
 * \"2018-12-20\"){id}}")</I>, it is created for the field named <I>boards</I>, then the
 * {@link #readTokenizerForResponseDefinition(StringTokenizer)} is called for the whole String. <BR/>
 * Then another {@link QueryField} is created, for the field named <I>topics</I>, and the <I>(since: \"2018-12-20\")</I>
 * is parsed by the {@link #readTokenizerForInputParameters(StringTokenizer)}, then the <I>{id}</I> String is parsed by
 * {@link #readTokenizerForResponseDefinition(StringTokenizer)} .
 * 
 * @author EtienneSF
 */
public class QueryField {

	/** Logger for this class */
	private static Logger logger = LoggerFactory.getLogger(QueryField.class);

	/** Indicates what is being read by the {@link #readTokenizerForInputParameters(StringTokenizer) method */
	private enum InputParameterStep {
		NAME, VALUE
	};

	/** A utility class, for various ... utility methods :) */
	GraphqlUtils graphqlUtils = GraphqlUtils.graphqlUtils;
	/** Another utility class, for various ... utility methods :) */
	GraphqlClientUtils graphqlClientUtils = GraphqlClientUtils.graphqlClientUtils;

	/** The registry for all known GraphQL directives */
	DirectiveRegistry directiveRegistry = DirectiveRegistryImpl.directiveRegistry;

	/** The class that contains this field */
	Class<?> owningClazz;
	/**
	 * The GraphQL class of the type, that is: the type of the field if it's not a List. And the type of the items of
	 * the list, if the field's type is a list
	 */
	Class<?> clazz;
	/** The name of this field */
	String name;
	/** The alias of this field */
	String alias = null;

	/** true if the {@link QueryField} is a query, a mutation or a subscription. False otherwise. */
	Boolean scalar = null;
	/** true if the {@link QueryField} is a query, a mutation or a subscription. False otherwise. */
	Boolean queryLevel = null;

	/** The list of input parameters for this QueryField */
	List<InputParameter> inputParameters = new ArrayList<>();

	/** The list of directives for this QueryField */
	List<Directive> directives = new ArrayList<>();

	/**
	 * All subfields contained in this field. It should remain empty if the field is a GraphQL Scalar. At least one if
	 * the field is a not a Scalar
	 */
	List<QueryField> fields = new ArrayList<>();

	/**
	 * The constructor, when created by the {@link Builder}: it must provide the owningClass
	 * 
	 * @param owningClass
	 *            The {@link Class} that owns the field
	 * @param fieldClass
	 *            The {@link Class} of the field
	 * @param fieldName
	 *            The name of the field
	 * @throws GraphQLRequestPreparationException
	 */
	public QueryField(Class<?> owningClass, Class<?> fieldClass, String fieldName)
			throws GraphQLRequestPreparationException {
		this.owningClazz = owningClass;
		this.clazz = fieldClass;
		this.name = fieldName;
	}

	/**
	 * Reads the definition of the expected response definition from the server. It is recursive.<BR/>
	 * For instance, for the GraphQL query <I>queryType.boards("{id name publiclyAvailable topics(since:
	 * \"2018-12-20\"){id}}")</I>, it will be called twice: <BR/>
	 * Once for the String <I>id name publiclyAvailable topics(since: \"2018-12-20\"){id}}</I> (without the leading
	 * '{'), where QueryField is <I>boards</I>,<BR/>
	 * Then for the String <I>id}</I>, where the QueryField is <I>topics</I>
	 * 
	 * @param st
	 *            The {@link StringTokenizer}, where the next token is the first token <B><I>after</I></B> the '{' have
	 *            already been read. <BR/>
	 *            The {@link StringTokenizer} is read until the '}' associated with this already read '{'.<BR/>
	 *            For instance, when this method is called with the {@link StringTokenizer} where these characters are
	 *            still to read: <I>id date author{name email alias} title content}}</I>, the {@link StringTokenizer} is
	 *            read until and including the first '}' that follows content. Thus, there is still a '}' to read.
	 * @throws GraphQLRequestPreparationException
	 */
	public void readTokenizerForResponseDefinition(StringTokenizer st) throws GraphQLRequestPreparationException {
		// The field we're reading
		QueryField currentField = null;
		// The directive we're reading. It is associated to the current field during its creation
		// (see the case "@" below for details)
		Directive directive = null;

		while (st.hasMoreTokens()) {

			String token = st.nextToken();

			switch (token) {
			case " ":
			case "\n":
			case "\r":
			case "\t":
				// Nothing to do.
				break;
			case ":":
				// The previously read field name is actually an alias
				if (currentField == null) {
					throw new GraphQLRequestPreparationException(
							"The given query has a ':' character, not preceded by a proper alias name (before <"
									+ st.nextToken() + ">)");
				}
				currentField.alias = currentField.name;
				// The real field name is the next real token (we'll check latter that the field names are valid)
				currentField.name = " ";
				while (currentField.name.equals(" ")) {
					currentField.name = st.nextToken();
				}

				// We try to get the class of this field
				currentField.owningClazz = clazz;
				currentField.clazz = graphqlUtils.getFieldType(clazz, currentField.name, true);

				break;
			case "@":
				// We're starting a GraphQL directive. The next token is its name.
				directive = new Directive();
				directive.setName(st.nextToken());// The directive name follows directly the @
				currentField.directives.add(directive);
				break;
			case "(":
				if (directive != null) {
					// We're starting to read the arguments for the last directive we've read
					directive.setArguments(readTokenizerForInputParameters(st, directive));
				} else if (currentField != null) {
					// We're starting the reading of field parameters
					currentField.inputParameters = currentField.readTokenizerForInputParameters(st, null);
				} else {
					throw new GraphQLRequestPreparationException(
							"The given query has a parentesis '(' not preceded by a field name (error while reading field <"
									+ name + ">");
				}
				break;
			case "{":
				directive = null;
				// The last field we've read is actually an object (a non Scalar GraphQL type), as it itself has
				// fields
				if (currentField == null) {
					throw new GraphQLRequestPreparationException(
							"The given query has two '{', one after another (error while reading field <" + name
									+ ">)");
				} else if (currentField.clazz == null) {
					throw new GraphQLRequestPreparationException(
							"Starting reading definition of field '" + currentField.name + "' of class '"
									+ owningClazz.getName() + "', but the owningClass is not set");
				} else if (currentField.fields.size() > 0) {
					throw new GraphQLRequestPreparationException(
							"The given query contains a '{' not preceded by a fieldname, after field <"
									+ currentField.name + "> while reading <" + this.name + ">");
				} else {
					// Ok, let's read the field for the subobject, for which we just read the name (and potentiel
					// alias :
					currentField.readTokenizerForResponseDefinition(st);
					// Let's clear the lastReadField, as we already have read its content.
					currentField = null;
				}
				break;
			case "}":
				// We're finished our current object : let's get out of this method.
				return;
			default:
				directive = null;
				// It's a field. Scalar or not ? That is the question. We don't care yet. If the next token is a
				// '{', we'll read its content and fill its fields list.
				currentField = new QueryField(clazz, graphqlUtils.getFieldType(clazz, token, false), token);

				fields.add(currentField);
			}// switch
		} // while

		// Oups, we should not arrive here:
		throw new GraphQLRequestPreparationException("The field <" + name
				+ "> has a non finished list of fields (it lacks the finishing '}') while reading <" + this.name + ">");
	}

	/**
	 * Reads the input parameters for a Field. It can be either a Field of a Query, Mutation or Subscription, or a Field
	 * of a standard GraphQL Type.
	 * 
	 * @param st
	 *            The StringTokenizer, where the opening parenthesis has been read. It will be read until and including
	 *            the next closing parenthesis.
	 * @param directive
	 *            is not null, then this method is reading the input parameters (arguments) for this {@link Directive}
	 * @throws GraphQLRequestPreparationException
	 *             If the request string is invalid
	 */
	List<InputParameter> readTokenizerForInputParameters(StringTokenizer st, Directive directive)
			throws GraphQLRequestPreparationException {
		List<InputParameter> ret = new ArrayList<>(); // The list that will be returned by this method
		InputParameterStep step = InputParameterStep.NAME;

		String parameterName = null;

		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			switch (token) {
			case "{":
				throw new GraphQLRequestPreparationException(
						"Encountered a '{' while reading parameters for the field '" + name
								+ "' : if you're using DirectQueries with field's parameter that are Input Types, please consider using Prepared Queries. "
								+ "Otherwise, please correct the query syntax");
			case ":":
			case " ":
			case "\n":
			case "\r":
			case "\t":
				break;
			case ",":
				if (step != InputParameterStep.NAME) {
					throw new GraphQLRequestPreparationException(
							"Misplaced comma for the field '" + name + "' is not finished (no closing parenthesis)");
				}
				break;
			case ")":
				// We should be waiting for a name, and have already read at least one name
				if (parameterName == null) {
					throw new GraphQLRequestPreparationException(
							"Misplaced closing parenthesis for the field '" + name + "' (no parameter has been read)");
				} else if (step != InputParameterStep.NAME) {
					throw new GraphQLRequestPreparationException("Misplaced closing parenthesis for the field '" + name
							+ "' is not finished (no closing parenthesis)");
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
								graphqlUtils.getCustomScalarGraphQLType(directive, owningClazz, name, parameterName)));
					} else if (token.startsWith("&")) {
						ret.add(new InputParameter(parameterName, token.substring(1), null, true,
								graphqlUtils.getCustomScalarGraphQLType(directive, owningClazz, name, parameterName)));
					} else if (token.startsWith("\"")) {
						// The inputParameter starts with "
						// We need to read all tokens until we find one that finishes by a "
						// This ending token may be the current one.
						StringBuffer sb = new StringBuffer();
						if (token.length() > 1 && token.endsWith("\"")) {
							// Ok, this token starts and finishes by " (and is not an only ")
							// We have read the whole value
							sb.append(token.substring(1, token.length() - 1));
						} else {
							// This token doesn't end with a "
							// So we must read until we find one that finishes by " (meaning we're finished with
							// reading this value)
							sb.append(token.substring(1));
							while (st.hasMoreTokens()) {
								String subtoken = st.nextToken();
								if (subtoken.endsWith("\"")) {
									// We've found the end of the value
									sb.append(subtoken.substring(0, subtoken.length() - 1));
									break;
								} else {
									// It's just a value within the string
									sb.append(subtoken);
								}
							}
						}
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
						Object parameterValue = parseDirectiveArgumentValue(directive, parameterName, token,
								owningClazz.getPackage().getName());
						InputParameter arg = new InputParameter(parameterName, null, parameterValue, true, null);
						ret.add(arg);
						directive.getArguments().add(arg);
					} else {
						Object parameterValue = parseInputParameterValue(owningClazz, name, parameterName, token);
						ret.add(new InputParameter(parameterName, null, parameterValue, true, null));
					}
					step = InputParameterStep.NAME;
					break;
				}
			}
		}

		throw new GraphQLRequestPreparationException(
				"The list of parameters for the field '" + name + "' is not finished (no closing parenthesis)");
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
	private Object parseInputParameterValue(Class<?> owningClass, String fieldName, String parameterName,
			String parameterValue) throws GraphQLRequestPreparationException {
		Field field;
		try {
			field = owningClass.getDeclaredField(graphqlUtils.getJavaName(fieldName));
		} catch (NoSuchFieldException | SecurityException e) {
			throw new GraphQLRequestPreparationException("Couldn't find the value for the parameter '" + parameterName
					+ "' of the field '" + fieldName + "'", e);
		}

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

	private Object parseDirectiveArgumentValue(Directive directive, String parameterName, String parameterValue,
			String packageName) throws GraphQLRequestPreparationException {
		// Let's find the directive definition for this read directive
		Directive directiveDefinition = directiveRegistry.getDirective(directive.getName());
		if (directiveDefinition == null) {
			throw new GraphQLRequestPreparationException(
					"Could not find the definition for the directive '" + directive.getName() + "'");
		}

		// Let's find the parameter type, so that we can call parseValueForInputParameter method
		for (InputParameter param : directiveDefinition.getArguments()) {
			if (param.getName().equals(parameterName)) {
				// We've found the parameterType. Let's get its value.
				try {
					return parseValueForInputParameter(parameterValue, param.getGraphQLScalarType().getName(),
							packageName);
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
	 *            Needed to find the class that implements this type
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	private Object parseValueForInputParameter(String parameterValue, String parameterType, String packageName)
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
			return Float.parseFloat(parameterValue);
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

	/**
	 * Append this query field in the {@link StringBuilder} in which the query is being written. Any parameter will be
	 * replaced by its value. It's a recursive method, that calls itself when this field is not a scalar: it calls
	 * itself for each subfield.
	 * 
	 * @param sb
	 * @param parameters
	 * @throws GraphQLRequestExecutionException
	 */
	public void appendToGraphQLRequests(StringBuilder sb, Map<String, Object> parameters)
			throws GraphQLRequestExecutionException {

		//////////////////////////////////////////////////////////
		// We start with the field name
		if (alias == null) {
			sb.append(name);
		} else {
			sb.append(alias).append(":").append(name);
		}

		//////////////////////////////////////////////////////////
		// Then the input parameters
		appendInputParameters(sb, inputParameters, parameters);

		//////////////////////////////////////////////////////////
		// Then the directives
		appendDirectives(sb, directives, parameters);

		//////////////////////////////////////////////////////////
		// Then field list (if any)
		boolean appendSpaceLocal = false;
		if (fields.size() > 0) {
			logger.debug("Appending ReponseDef content for field " + name + " of type " + clazz.getSimpleName());

			sb.append("{");
			for (QueryField f : fields) {
				if (appendSpaceLocal) {
					sb.append(" ");
				}

				f.appendToGraphQLRequests(sb, parameters);

				appendSpaceLocal = true;
			}
			sb.append("}");
		}
	}

	private void appendInputParameters(StringBuilder sb, List<InputParameter> inputParameters,
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

	private void appendDirectives(StringBuilder sb, List<Directive> directives, Map<String, Object> parameters)
			throws GraphQLRequestExecutionException {
		if (directives != null && directives.size() > 0) {
			for (Directive dir : directives) {
				sb.append(" ").append("@").append(dir.getName());
				appendInputParameters(sb, dir.getArguments(), parameters);
			}
		}
	}

	/**
	 * If this field is not a scalar, this method adds the _typename into the requested fields list (if it doesn't
	 * already exist) for this {@link QueryField}, and the same recursively for all its non scalar fields. <BR/>
	 * If this field is a scalar, no action.
	 * 
	 * @param objectResponse
	 * @throws GraphQLRequestPreparationException
	 */
	void addTypenameFields() throws GraphQLRequestPreparationException {

		// No action for scalar fields
		if (!isScalar()) {

			QueryField __typename = null;

			// Let's go through sub fields to:
			// 1) look for an existing __typename field
			// 2) Recurse into this method for non scalar fields
			for (QueryField f : fields) {
				if (f.name.equals("__typename")) {
					__typename = f;
					break;
				}
				f.addTypenameFields();
			}

			// We add the __typename for all levels, but not for the query/mutation/subscription one
			if (!isQueryLevel() && __typename == null) {
				__typename = new QueryField(this.clazz, String.class, "__typename");
				fields.add(__typename);
			}
		}
	}

	/**
	 * Indicates whether this field is a scalar or not.
	 * 
	 * @return true if this field is a scalar (custom or not), and false otherwise.
	 * @throws GraphQLRequestPreparationException
	 */
	public boolean isScalar() throws GraphQLRequestPreparationException {
		if (scalar == null) {
			try {
				return graphqlClientUtils.isScalar(owningClazz.getDeclaredField(graphqlUtils.getJavaName(name)));
			} catch (NoSuchFieldException | SecurityException e) {
				throw new GraphQLRequestPreparationException("Could not determine if the '" + name + "' field of the '"
						+ owningClazz.getName() + "' is a scalar", e);
			}
		}
		return scalar;
	}

	/**
	 * Indicates whether this field is a query/mutation/subscription or not
	 * 
	 * @return true if the {@link QueryField} is a query, a mutation or a subscription. False otherwise.
	 */
	public boolean isQueryLevel() {
		if (queryLevel == null) {
			queryLevel = name.equals("query") || name.equals("mutation") || name.equals("subscription");
		}
		return queryLevel;
	}

}
