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
import com.graphql_java_generator.annotation.GraphQLInputType;
import com.graphql_java_generator.annotation.GraphQLInterfaceType;
import com.graphql_java_generator.annotation.GraphQLObjectType;
import com.graphql_java_generator.annotation.GraphQLQuery;
import com.graphql_java_generator.annotation.GraphQLUnionType;
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
	final Class<?> clazz;
	/** The name of this field */
	final String name;
	/** The alias of this field */
	final String alias;
	/**
	 * The package name, where the generated classes are. It's used to load the class definition, and get the GraphQL
	 * metadata coming from the GraphQL schema
	 */
	final String packageName;

	/** true if the {@link QueryField} is a query, a mutation or a subscription. False otherwise. */
	Boolean scalar = null;
	/** true if the {@link QueryField} is a query, a mutation or a subscription. False otherwise. */
	Boolean queryLevel = null;

	/** The list of input parameters for this QueryField */
	List<InputParameter> inputParameters = new ArrayList<>();

	/** The list of directives for this QueryField */
	List<Directive> directives = new ArrayList<>();

	/**
	 * The lists of fragment that are in this field's definition, like <I>fragment1</I> and <I>fragment2</I> in:
	 * <I>thisField {field1 ...fragment1 field2 ...fragment2}</I>
	 */
	List<String> fragments = new ArrayList<>();
	/** The list of inline fragments that are defined for this field */
	List<Fragment> inlineFragments = new ArrayList<>();

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
	 * @param fieldName
	 *            The name of the field
	 * @param fieldAlias
	 *            The alias for this field
	 * @throws GraphQLRequestPreparationException
	 */
	public QueryField(Class<?> owningClass, String fieldName, String fieldAlias)
			throws GraphQLRequestPreparationException {
		graphqlClientUtils.checkName(fieldName);
		if (fieldAlias != null) {
			graphqlClientUtils.checkName(fieldAlias);
		}
		this.owningClazz = owningClass;
		this.clazz = graphqlClientUtils.checkFieldOfGraphQLType(fieldName, null, owningClass);
		this.name = fieldName;
		this.alias = fieldAlias;
		this.packageName = owningClass.getPackage().getName();
	}

	/**
	 * The constructor, when created by the {@link Builder}: it must provide the owningClass
	 * 
	 * @param owningClass
	 *            The {@link Class} that owns the field
	 * @param fieldName
	 *            The name of the field
	 * @throws GraphQLRequestPreparationException
	 */
	public QueryField(Class<?> owningClass, String fieldName) throws GraphQLRequestPreparationException {
		this(owningClass, fieldName, null);
	}

	/**
	 * The constructor, when created for a {@link Fragment}. We only know the class of the Fragment. This class is the
	 * owning class of all the fields defined in the fragment.<BR/>
	 * The access for this constructor is limited to the package, as only the {@link Fragment} class should call it.
	 * 
	 * @param clazz
	 *            The {@link Class} of the {@link Fragment} we're about to read.
	 * @throws GraphQLRequestPreparationException
	 */
	QueryField(Class<?> clazz) throws GraphQLRequestPreparationException {
		this.owningClazz = null;
		this.clazz = clazz;
		this.name = null;
		this.alias = null;
		this.packageName = clazz.getPackage().getName();
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
	public void readTokenizerForResponseDefinition(QueryTokenizer st) throws GraphQLRequestPreparationException {
		// The field we're reading
		QueryField currentField = null;
		// The directive we're reading. It is associated to the current field during its creation
		// (see the case "@" below for details)
		Directive directive = null;

		while (st.hasMoreTokens()) {

			String token = st.nextToken();

			switch (token) {
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
			case "...":
				// We're reading an inline fragment
				inlineFragments.add(new Fragment(st, packageName, true));
				break;
			case "}":
				// We're finished our current object : let's get out of this method
				// (end of this recursion level)
				return;
			default:
				directive = null;

				if (token.startsWith("...")) {
					// This token starts by "...", we've read a global fragment
					String fragmentName = token.substring(3);
					logger.trace("Found fragment {} for field {}", fragmentName, name);
					fragments.add(fragmentName);
				} else {
					// We've read a regular field
					if (st.checkNextToken(":")) {
						// The next token is ":", so we've found an alias (not a name field)
						String alias = token;
						token = st.nextToken(); // It's the ":". We ignore it
						token = st.nextToken();
						currentField = new QueryField(clazz, token, alias);
					} else {
						currentField = new QueryField(clazz, token);
					}

					// Does a field of this name already exist ?
					// (if this name is an alias, we'll read the real name later, and we'll repeat the check later)
					if (getField(currentField.name) != null) {
						throw new GraphQLRequestPreparationException("The field <" + currentField.name
								+ "> exists twice in the field list for the " + owningClazz.getSimpleName() + " type");
					}

					fields.add(currentField);
				}
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
	List<InputParameter> readTokenizerForInputParameters(QueryTokenizer st, Directive directive)
			throws GraphQLRequestPreparationException {
		List<InputParameter> ret = new ArrayList<>(); // The list that will be returned by this method
		InputParameterStep step = InputParameterStep.NAME;

		String parameterName = null;

		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			switch (token) {
			case ":":
				// We're about to read an input parameter value.
				break;
			case "{":
				throw new GraphQLRequestPreparationException(
						"Encountered a '{' while reading parameters for the field '" + name
								+ "' : if you're using DirectQueries with field's parameter that are Input Types, please consider using Prepared Queries. "
								+ "Otherwise, please correct the query syntax");
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
								graphqlClientUtils.getGraphQLType(directive, owningClazz, name, parameterName)));
					} else if (token.startsWith("&")) {
						ret.add(new InputParameter(parameterName, token.substring(1), null, true,
								graphqlClientUtils.getGraphQLType(directive, owningClazz, name, parameterName)));
					} else if (token.equals("\"")) {
						// We've found a String value: let's read the string content
						StringBuffer sb = new StringBuffer();
						while (true) {
							if (!st.hasMoreTokens(true)) {
								throw new GraphQLRequestPreparationException(
										"Found the end of string before the end of the string parameter '"
												+ sb.toString() + "'");
							}
							token = st.nextToken(true);
							if (token.contentEquals("\"")) {
								// We've found the end of the string value.
								break;
							}
							sb.append(token);
							if (token.equals("\\")) {
								// It's the escape character. We add the next token, as is. Especially if it's a double
								// quote (as a double quote here doens't mean we found the end of the string)
								sb.append(st.nextToken(true));
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
						Object parameterValue = parseInputParameterValue(owningClazz, name, parameterName, token);
						ret.add(new InputParameter(parameterName, null, parameterValue, true, null));
					}
					step = InputParameterStep.NAME;
					break;
				}
			}// switch (token)
		} // while (st.hasMoreTokens())

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
					return parseValueForInputParameter(parameterValue, graphQLInputParameters.types()[i]);
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

	private Object parseDirectiveArgumentValue(Directive directive, String parameterName, String parameterValue)
			throws GraphQLRequestPreparationException {
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
					return parseValueForInputParameter(parameterValue, param.getGraphQLScalarType().getName());
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
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	private Object parseValueForInputParameter(String parameterValue, String parameterType)
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
	 * @param appendName
	 *            true if the name of the field must be written in the query (for regular fields for instance). False
	 *            otherwise (for fragments, for instance)
	 * @throws GraphQLRequestExecutionException
	 */
	public void appendToGraphQLRequests(StringBuilder sb, Map<String, Object> parameters, boolean appendName)
			throws GraphQLRequestExecutionException {

		//////////////////////////////////////////////////////////
		// We start with the field name
		if (appendName) {
			if (alias == null) {
				sb.append(name);
			} else {
				sb.append(alias).append(":").append(name);
			}
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

		if (fields.size() > 0 || fragments.size() > 0) {
			logger.debug("Appending ReponseDef content for field " + name + " of type " + clazz.getSimpleName());
			sb.append("{");

			// Let's append the fields...
			for (QueryField f : fields) {
				if (appendSpaceLocal) {
					sb.append(" ");
				}
				f.appendToGraphQLRequests(sb, parameters, true);
				appendSpaceLocal = true;
			}

			// ...the fragment names
			for (String f : fragments) {
				if (appendSpaceLocal) {
					sb.append(" ");
				}
				sb.append("...").append(f);
				appendSpaceLocal = true;
			} // for

			// ...the inline fragments
			for (Fragment f : inlineFragments) {
				if (appendSpaceLocal) {
					sb.append(" ");
				}
				sb.append("...");
				f.appendToGraphQLRequests(sb, parameters);
				appendSpaceLocal = true;
			} // for

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

		if (isScalar()) {
			// No action for scalar fields
		} else if (inlineFragments.size() > 0) {
			// We add the __typename field into all fragments, but not on the type itself (useless)
			for (Fragment f : inlineFragments) {
				f.addTypenameFields();
			}
		} else if (fragments.size() == 0) {
			// It's a non scalar field, without any fragment. We must add the __typename
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
				__typename = new QueryField(this.clazz, "__typename");
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
			// The scalar value has not yet been calculated.

			// All the generated classes have a GraphQL annotation.
			// If no such annotation, then this type is a scalar.
			GraphQLInputType graphQLInputType = clazz.getAnnotation(GraphQLInputType.class);
			GraphQLInterfaceType graphQLInterfaceType = clazz.getAnnotation(GraphQLInterfaceType.class);
			GraphQLObjectType graphQLObjectType = clazz.getAnnotation(GraphQLObjectType.class);
			GraphQLQuery graphQLQuery = clazz.getAnnotation(GraphQLQuery.class);
			GraphQLUnionType graphQLUnionType = clazz.getAnnotation(GraphQLUnionType.class);

			// If one of these annotations is not null, then it's not a scalar. Otherwise, this type is a scalar.
			scalar = !(graphQLInputType != null || graphQLInterfaceType != null || graphQLObjectType != null
					|| graphQLQuery != null || graphQLUnionType != null);
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
			queryLevel = name != null
					&& (name.equals("query") || name.equals("mutation") || name.equals("subscription"));
		}
		return queryLevel;
	}

	/**
	 * Returns the subfield for this {@link QueryField} of the given name
	 * 
	 * @param name
	 *            The field's name to search
	 * @return The subfield of the given name, or null of this {@link QueryField} contains no field of this name
	 */
	QueryField getField(String name) {
		for (QueryField f : fields) {
			if (f.name.equals(name)) {
				// We found it
				return f;
			}
		}
		// No field of this name has been found
		return null;
	}

	public Class<?> getOwningClazz() {
		return owningClazz;
	}

	public Class<?> getClazz() {
		return clazz;
	}

	public String getName() {
		return name;
	}

	public String getAlias() {
		return alias;
	}

}
