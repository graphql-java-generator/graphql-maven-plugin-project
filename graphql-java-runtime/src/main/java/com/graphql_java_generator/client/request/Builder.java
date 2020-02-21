package com.graphql_java_generator.client.request;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import com.graphql_java_generator.CustomScalarRegistryImpl;
import com.graphql_java_generator.GraphqlUtils;
import com.graphql_java_generator.annotation.GraphQLCustomScalar;
import com.graphql_java_generator.annotation.GraphQLInputParameters;
import com.graphql_java_generator.annotation.GraphQLNonScalar;
import com.graphql_java_generator.annotation.GraphQLScalar;
import com.graphql_java_generator.client.GraphqlClientUtils;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;

import graphql.schema.GraphQLScalarType;

/**
 * This class is a Builder that'll help to build a {@link ObjectResponse}, which defines what should appear in the
 * response from the GraphQL server.
 * 
 * @author EtienneSF
 */
public class Builder {

	/**
	 * The list of character that can separate tokens, in the GraphQL query string. These token are read by the
	 * {@link StringTokenizer}.
	 */
	private static final String STRING_TOKENIZER_DELIMITER = " {},:()";

	GraphqlUtils graphqlUtils = new GraphqlUtils();
	GraphqlClientUtils graphqlClientUtils = new GraphqlClientUtils();

	final ObjectResponse objectResponse;

	/** Indicates what is being read by the {@link #readTokenizerForInputParameters(StringTokenizer) method */
	private enum InputParameterStep {
		NAME, VALUE
	};

	/**
	 * This class gives parsing capabilities for the QueryString for one object.<BR/>
	 * For instance, for the GraphQL query <I>queryType.boards("{id name publiclyAvailable topics(since:
	 * \"2018-12-20\"){id}}")</I>, it is created for the field named <I>boards</I>, then the
	 * {@link #readTokenizerForResponseDefinition(StringTokenizer)} is called for the whole String. <BR/>
	 * Then another {@link QueryField} is created, for the field named <I>topics</I>, and the <I>(since:
	 * \"2018-12-20\")</I> is parsed by the {@link #readTokenizerForInputParameters(StringTokenizer)}, then the
	 * <I>{id}</I> String is parsed by {@link #readTokenizerForResponseDefinition(StringTokenizer)} .
	 * 
	 * @author EtienneSF
	 */
	class QueryField {
		/** The class that contains this field */
		Class<?> owningClazz;
		/**
		 * The GraphQL class of the type, that is: the type of the field if it's not a List. And the type of the items
		 * of the list, if the field's type is a list
		 */
		Class<?> clazz;
		/** The name of this field */
		String name;
		/** The alias of this field */
		String alias = null;

		/** The list of input parameters for this QueryFields */
		List<InputParameter> inputParameters = new ArrayList<>();

		/**
		 * All subfields contained in this field. Empty if the field is a GraphQL Scalar. At least one if the field is a
		 * not a Scalar
		 */
		List<QueryField> fields = new ArrayList<>();

		/**
		 * The constructor, when created by the {@link Builder}: it must provide the owningClass
		 * 
		 * @param owningClazz
		 *            The {@link Class} that owns the field
		 * @param clazz
		 *            The {@link Class} of the field
		 * @param name
		 *            The name of the field
		 * @throws GraphQLRequestPreparationException
		 */
		QueryField(Class<?> owningClazz, Class<?> clazz, String name) throws GraphQLRequestPreparationException {
			this.owningClazz = owningClazz;
			this.clazz = clazz;
			this.name = name;
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
		 *            The {@link StringTokenizer}, where the next token is the first token <B><I>after</I></B> the '{'
		 *            have already been read. <BR/>
		 *            The {@link StringTokenizer} is read until the '}' associated with this already read '{'.<BR/>
		 *            For instance, when this method is called with the {@link StringTokenizer} where these characters
		 *            are still to read: <I>id date author{name email alias} title content}}</I>, the
		 *            {@link StringTokenizer} is read until and including the first '}' that follows content. Thus,
		 *            there is still a '}' to read.
		 * @throws GraphQLRequestPreparationException
		 */
		public void readTokenizerForResponseDefinition(StringTokenizer st) throws GraphQLRequestPreparationException {
			/** The last field we've read. */
			QueryField lastReadField = null;

			while (st.hasMoreTokens()) {
				String token = st.nextToken();
				switch (token) {
				case " ":
					// Nothing to do.
					break;
				case ":":
					// The previously read field name is actually an alias
					if (lastReadField == null) {
						throw new GraphQLRequestPreparationException(
								"The given query has a ':' character, not preceded by a proper alias name (before <"
										+ st.nextToken() + ">)");
					}
					lastReadField.alias = lastReadField.name;
					// The real field name is the next real token (we'll check latter that the field names are valid)
					lastReadField.name = " ";
					while (lastReadField.name.equals(" ")) {
						lastReadField.name = st.nextToken();
					}

					// We try to get the class of this field
					lastReadField.owningClazz = clazz;
					lastReadField.clazz = getFieldType(clazz, lastReadField.name, true);

					break;
				case "(":
					// We're starting the reading of field parameters
					if (lastReadField == null) {
						throw new GraphQLRequestPreparationException(
								"The given query has a parentesis '(' not preceded by a field name (error while reading field <"
										+ name + ">");
					} else {
						lastReadField.readTokenizerForInputParameters(st);
					}
					break;
				case "{":
					// The last field we've read is actually an object (a non Scalar GraphQL type), as it itself has
					// fields
					if (lastReadField == null) {
						throw new GraphQLRequestPreparationException(
								"The given query has two '{', one after another (error while reading field <" + name
										+ ">)");
					} else if (lastReadField.clazz == null) {
						throw new GraphQLRequestPreparationException(
								"Starting reading definition of field '" + lastReadField.name + "' of class '"
										+ owningClazz.getName() + "', but the owningClass is not set");
					} else if (lastReadField.fields.size() > 0) {
						throw new GraphQLRequestPreparationException(
								"The given query contains a '{' not preceded by a fieldname, after field <"
										+ lastReadField.name + "> while reading <" + this.name + ">");
					} else {
						// Ok, let's read the field for the subobject, for which we just read the name (and potentiel
						// alias :
						lastReadField.readTokenizerForResponseDefinition(st);
						// Let's clear the lastReadField, as we already have read its content.
						lastReadField = null;
					}
					break;
				case "}":
					// We're finished our current object : let's get out of this method.
					return;
				default:
					// It's a field. Scalar or not ? That is the question. We don't care yet. If the next token is a
					// '{', we'll read its content and fill its fields list.
					lastReadField = new QueryField(clazz, getFieldType(clazz, token, false), token);

					fields.add(lastReadField);
				}// switch
			} // while

			// Oups, we should not arrive here:
			throw new GraphQLRequestPreparationException("The field <" + name
					+ "> has a non finished list of fields (it lacks the finishing '}') while reading <" + this.name
					+ ">");
		}

		/**
		 * Reads the input parameters for a Field. It can be either a Field of a Query, Mutation or Subscription, or a
		 * Field of a standard GraphQL Type.
		 * 
		 * @param st
		 *            The StringTokenizer, where the opening parenthesis has been read. It will be read until and
		 *            including the next closing parenthesis.
		 * @throws GraphQLRequestPreparationException
		 *             If the request string is invalid
		 */
		void readTokenizerForInputParameters(StringTokenizer st) throws GraphQLRequestPreparationException {
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
					break;
				case ",":
					if (step != InputParameterStep.NAME) {
						throw new GraphQLRequestPreparationException("Misplacer comma for the field '" + name
								+ "' is not finished (no closing parenthesis)");
					}
					break;
				case ")":
					// We should be waiting for a name, and have already read at least one name
					if (parameterName == null) {
						throw new GraphQLRequestPreparationException("Misplaced closing parenthesis for the field '"
								+ name + "' (no parameter has been read)");
					} else if (step != InputParameterStep.NAME) {
						throw new GraphQLRequestPreparationException("Misplaced closing parenthesis for the field '"
								+ name + "' is not finished (no closing parenthesis)");
					}
					// We're finished, here.
					return;
				default:
					switch (step) {
					case NAME:
						parameterName = token;
						step = InputParameterStep.VALUE;
						break;
					case VALUE:
						// We've read the parameter value. Let's add this parameter.
						if (token.startsWith("?")) {
							inputParameters.add(new InputParameter(parameterName, token.substring(1), null, false,
									getCustomScalarGraphQLType(owningClazz, name, parameterName)));
						} else if (token.startsWith("&")) {
							inputParameters.add(new InputParameter(parameterName, token.substring(1), null, true,
									getCustomScalarGraphQLType(owningClazz, name, parameterName)));
						} else if (token.startsWith("\"") && token.endsWith("\"")) {
							// The inputParameter starts and ends by "
							// It's a regular String.
							String value = token.substring(1, token.length() - 1);
							inputParameters.add(new InputParameter(parameterName, null, value, true, null));
						} else if (token.startsWith("\"") || token.endsWith("\"")) {
							// Too bad, there is a " only at the end or only at the beginning
							throw new GraphQLRequestPreparationException(
									"Bad parameter value: parameter values should start and finish by \", or not having any \" at the beginning and end.. But it's not the case for the value <"
											+ token + "> of parameter <" + parameterName
											+ ">. Maybe you wanted to add a bind parameter instead (bind parameter must start with a ? or a &");
						} else {
							Object parameterValue = getParameterValue(owningClazz, name, parameterName, token);
							inputParameters.add(new InputParameter(parameterName, null, parameterValue, true, null));
						}
						step = InputParameterStep.NAME;
						break;
					}
				}
			}

			throw new GraphQLRequestPreparationException(
					"The list of parameters for the field '" + name + "' is not finished (no closing parenthesis)");
		}

		private Object getParameterValue(Class<?> owningClass, String fieldName, String parameterName,
				String parameterValue) throws GraphQLRequestPreparationException {
			Object ret = null;

			Field field;
			try {
				field = owningClass.getDeclaredField(graphqlUtils.getJavaName(fieldName));
			} catch (NoSuchFieldException | SecurityException e) {
				throw new GraphQLRequestPreparationException("Couldn't parse the value for the parameter '"
						+ parameterName + "' of the field '" + fieldName + "'", e);
			}

			GraphQLInputParameters graphQLInputParameters = field.getDeclaredAnnotation(GraphQLInputParameters.class);
			if (graphQLInputParameters == null) {
				throw new GraphQLRequestPreparationException("[Internal error] The field '" + fieldName
						+ "' is lacking the GraphQLInputParameters annotation");
			}

			String parameterType = null;
			for (int i = 0; i < graphQLInputParameters.names().length; i += 1) {
				if (graphQLInputParameters.names()[i].equals(parameterName)) {
					parameterType = graphQLInputParameters.types()[i];
				}
			}
			if (parameterType == null) {
				throw new GraphQLRequestPreparationException("[Internal error] Can't find the type for the parameter '"
						+ parameterName + "' of the field '" + fieldName + "'");
			}

			// Let's check if this type is a Custom Scalar
			GraphQLScalarType scalarType = CustomScalarRegistryImpl.customScalarRegistry
					.getGraphQLScalarType(parameterType);

			if (scalarType != null) {
				// This type is a Custom Scalar. Let's ask the CustomScalar implementation to translate this value.
				ret = scalarType.getCoercing().parseValue(parameterValue);
			} else {
				// This type is not a Custom Scalar, so it must be a standard Scalar. Let's manage it
				String parameterClassname = owningClass.getPackageName() + "."
						+ graphqlUtils.getJavaName(parameterType);
				Class<?> parameterClass;
				try {
					parameterClass = Class.forName(parameterClassname);
				} catch (ClassNotFoundException e) {
					throw new GraphQLRequestPreparationException("Couldn't find the class (" + parameterClassname
							+ ") of the parameter '" + parameterName + "' of the field '" + fieldName + "'", e);
				}

				if (parameterClass.isEnum()) {
					// This parameter is an enum. The parameterValue is one of its elements
					Method valueOf = graphqlUtils.getMethod("valueOf", parameterClass, String.class);
					ret = graphqlUtils.invokeMethod(valueOf, null, parameterValue);
				} else if (parameterClass.isAssignableFrom(Boolean.class)) {
					// This parameter is a boolean. Only true and false are valid boolean.
					if (!"true".equals(parameterValue) && !"false".equals(parameterValue)) {
						throw new GraphQLRequestPreparationException(
								"Only true and false are allowed values for booleans. But the parameter '"
										+ parameterName + "' of the field '" + fieldName + "' has the '"
										+ parameterValue + "'");
					}
					ret = "true".equals(parameterValue);
				} else if (parameterClass.isAssignableFrom(Integer.class)) {
					ret = Integer.parseInt(parameterValue);
				} else if (parameterClass.isAssignableFrom(Float.class)) {
					ret = Float.parseFloat(parameterValue);
				}
			} // else (scalarType != null)

			if (ret != null)
				return ret;
			else
				throw new GraphQLRequestPreparationException("Couldn't parse the value for the parameter '"
						+ parameterName + "' of the field '" + fieldName + "'. The value is '" + parameterValue + "'");
		}

		/**
		 * Retrieves the class of the fieldName field of the owningClass class.
		 * 
		 * @param owningClass
		 * @param fieldName
		 * @param returnIdMandatory
		 *            If true, a {@link GraphQLRequestPreparationException} is thrown if the field is not found.
		 * @return The class of the field. Or null of the field doesn't exist, and returnIdMandatory is false
		 * @throws GraphQLRequestPreparationException
		 */
		private Class<?> getFieldType(Class<?> owningClass, String fieldName, boolean returnIdMandatory)
				throws GraphQLRequestPreparationException {
			if (owningClass.isInterface()) {
				// We try to get the class of this getter of the field
				try {
					Method method = owningClass.getDeclaredMethod("get" + graphqlUtils.getPascalCase(fieldName));

					// We must manage the type erasure for list. So we use the GraphQL annotations to retrieve types.
					GraphQLNonScalar graphQLNonScalar = method.getAnnotation(GraphQLNonScalar.class);
					GraphQLScalar graphQLScalar = method.getAnnotation(GraphQLScalar.class);

					if (graphQLNonScalar != null)
						return graphQLNonScalar.javaClass();
					else if (graphQLScalar != null)
						return graphQLScalar.javaClass();
					else
						throw new GraphQLRequestPreparationException(
								"Error while looking for the getter for the field '" + fieldName
										+ "' in the interface '" + owningClass.getName()
										+ "': this method should have one of these annotations: GraphQLNonScalar or GraphQLScalar ");
				} catch (NoSuchMethodException e) {
					// Hum, the field doesn't exist.
					if (!returnIdMandatory)
						return null;
					else
						throw new GraphQLRequestPreparationException(
								"Error while looking for the getter for the field '" + fieldName + "' in the class '"
										+ owningClass.getName() + "'",
								e);
				} catch (SecurityException e) {
					throw new GraphQLRequestPreparationException("Error while looking for the getter for the field '"
							+ fieldName + "' in the class '" + owningClass.getName() + "'", e);
				}
			} else {
				// We try to get the class of this field
				try {
					Field field = owningClass.getDeclaredField(graphqlUtils.getJavaName(fieldName));

					// We must manage the type erasure for list. So we use the GraphQL annotations to retrieve types.
					GraphQLCustomScalar graphQLCustomScalar = field.getAnnotation(GraphQLCustomScalar.class);
					GraphQLNonScalar graphQLNonScalar = field.getAnnotation(GraphQLNonScalar.class);
					GraphQLScalar graphQLScalar = field.getAnnotation(GraphQLScalar.class);

					if (graphQLCustomScalar != null)
						return graphQLCustomScalar.javaClass();
					else if (graphQLNonScalar != null)
						return graphQLNonScalar.javaClass();
					else if (graphQLScalar != null)
						return graphQLScalar.javaClass();
					else
						throw new GraphQLRequestPreparationException("Error while looking for the the field '"
								+ fieldName + "' in the class '" + owningClass.getName()
								+ "': this field should have one of these annotations: GraphQLNonScalar or GraphQLScalar ");
				} catch (NoSuchFieldException e) {
					// Hum, the field doesn't exist.
					if (!returnIdMandatory)
						return null;
					else
						throw new GraphQLRequestPreparationException("Error while looking for the the field '"
								+ fieldName + "' in the class '" + owningClass.getName() + "'", e);
				} catch (SecurityException e) {
					throw new GraphQLRequestPreparationException("Error while looking for the the field '" + fieldName
							+ "' in the class '" + owningClass.getName() + "'", e);
				}
			}
		}

	}// class QueryField

	////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////// START OF THE CLASS CODE /////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a Builder, for a field without alias
	 * 
	 * @param owningClass
	 *            The class that contains this field
	 * @param fieldName
	 *            The field for which we must build an ObjectResponse
	 * @throws GraphQLRequestPreparationException
	 */
	public Builder(Class<?> owningClass, String fieldName) throws GraphQLRequestPreparationException {
		objectResponse = new ObjectResponse(owningClass, fieldName, null);
	}

	/**
	 * Creates a Builder
	 * 
	 * @param owningClass
	 *            The class that contains this field
	 * @param fieldName
	 *            The field for which we must build an ObjectResponse
	 * @param fieldAlias
	 *            Its optional alias (may be null)
	 * @throws GraphQLRequestPreparationException
	 */
	public Builder(Class<?> owningClass, String fieldName, String fieldAlias)
			throws GraphQLRequestPreparationException {
		objectResponse = new ObjectResponse(owningClass, fieldName, fieldAlias);
	}

	/**
	 * Adds a scalar field with no alias, to the {@link ObjectResponse} we are building
	 * 
	 * @param fieldName
	 * @return The current builder, to allow the standard builder construction chain
	 * @throws NullPointerException
	 *             If the fieldName is null
	 * @throws GraphQLRequestPreparationException
	 *             If the fieldName or the fieldAlias is not valid
	 */
	public Builder withField(String fieldName) throws GraphQLRequestPreparationException {
		return withField(fieldName, null);
	}

	/**
	 * Adds a scalar field with an alias, to the {@link ObjectResponse} we are building. This field has no input
	 * parameters. To add a field with Input parameters, please use
	 * 
	 * @param fieldName
	 * @param alias
	 * @return The current builder, to allow the standard builder construction chain
	 * @throws NullPointerException
	 *             If the fieldName is null
	 * @throws GraphQLRequestPreparationException
	 *             If the fieldName or the fieldAlias is not valid
	 */
	public Builder withField(String fieldName, String alias) throws GraphQLRequestPreparationException {
		return withField(fieldName, alias, null);
	}

	/**
	 * Adds a scalar field with an alias, to the {@link ObjectResponse} we are building. This field has no input
	 * parameters. To add a field with Input parameters, please use
	 * 
	 * @param fieldName
	 * @param alias
	 * @return The current builder, to allow the standard builder construction chain
	 * @throws NullPointerException
	 *             If the fieldName is null
	 * @throws GraphQLRequestPreparationException
	 *             If the fieldName or the fieldAlias is not valid
	 */
	public Builder withField(String fieldName, String alias, List<InputParameter> inputParameters)
			throws GraphQLRequestPreparationException {

		// We check that this field exist, and is a scaler
		graphqlClientUtils.checkFieldOfGraphQLType(fieldName, true, objectResponse.field.clazz);

		// Let's check that this field is not already in the list
		for (ObjectResponse.Field field : objectResponse.scalarFields) {
			if (field.name.equals(fieldName)) {
				throw new GraphQLRequestPreparationException("The field <" + fieldName
						+ "> is already in the field list for the objet <" + objectResponse.field.name + ">");
			}
		}

		ObjectResponse.Field field = new ObjectResponse.Field(fieldName, alias, objectResponse.field.clazz,
				graphqlClientUtils.checkFieldOfGraphQLType(fieldName, true, objectResponse.field.clazz),
				inputParameters);

		// This will check that the alias is null or a valid GraphQL identifier
		objectResponse.scalarFields.add(field);

		return this;
	}

	/**
	 * Add an {@link InputParameter} to the current Object Response definition.
	 * 
	 * @param inputParameter
	 * @return The current {@link Builder}
	 */
	@Deprecated
	public Builder withInputParameter(InputParameter inputParameter) {
		objectResponse.addInputParameter(inputParameter);
		return this;
	}

	/**
	 * Add an {@link InputParameter} to the current Object Response definition.
	 * 
	 * @param name
	 *            name of the field parameter, as defined in the GraphQL schema
	 * @param value
	 *            The value to be sent to the server. If a String, it will be surroundered by double quotes, to be JSON
	 *            compatible. Otherwise, the toString() method is called to write the result in the GraphQL query.
	 * @return The current {@link Builder}
	 */
	public Builder withInputParameterHardCoded(String name, Object value) {
		objectResponse.addInputParameter(new InputParameter(name, null, value, true, null));
		return this;
	}

	/**
	 * Add an {@link InputParameter} to the current Object Response definition.
	 * 
	 * @param name
	 *            name of the field parameter, as defined in the GraphQL schema
	 * @param bindParameterName
	 *            The name of the parameter, as it will be provided later for the request execution: it's up to the
	 *            client application to provide (or not) a value associated with this parameterName.
	 * @param mandatory
	 *            true if this parameter must be provided for request execution. If mandatory is true, and no value is
	 *            provided for request execution, a {@link GraphQLRequestExecutionException} exception will be thrown,
	 *            instead of sending the request to the GraphQL server. Of course, parameter that are mandatory in the
	 *            GraphQL schema should be declared as mandatory here. But, depending on your client use case, you may
	 *            declare other parameter to be mandatory.
	 * @return The current {@link Builder}
	 * @throws GraphQLRequestPreparationException
	 */
	public Builder withInputParameter(String name, String bindParameterName, boolean mandatory)
			throws GraphQLRequestPreparationException {
		GraphQLScalarType graphQLScalarType = getCustomScalarGraphQLType(objectResponse.getOwningClass(),
				objectResponse.getFieldName(), name);
		objectResponse
				.addInputParameter(new InputParameter(name, bindParameterName, null, mandatory, graphQLScalarType));
		return this;
	}

	/**
	 * Add a list of {@link InputParameter}s to the current Object Response definition.
	 * 
	 * @param inputParameters
	 * @return The current {@link Builder}
	 */
	public Builder withInputParameters(List<InputParameter> inputParameters) {
		objectResponse.addInputParameters(inputParameters);
		return this;
	}

	/**
	 * Adds a non scalar field (a sub-object), to the {@link ObjectResponse} we are building. The given objectResponse
	 * contains the field name and its optional alias.
	 * 
	 * @param subobjetResponseDef
	 *            The {@link ObjectResponse} for this sub-object
	 * @return The current builder, to allow the standard builder construction chain
	 * @throws NullPointerException
	 *             If the fieldName is null
	 * @throws GraphQLRequestPreparationException
	 *             If the subobjetResponseDef can't be added. For instance: the fieldName or the fieldAlias is not
	 *             valid, or if the field of this subobjetResponseDef doesn't exist in the current owningClass...
	 */
	public Builder withSubObject(ObjectResponse subobjetResponseDef) throws GraphQLRequestPreparationException {
		// The sub-object must be based ... on a subobject of the current Field.
		// That is: the owningClass for the subject must be our field class.
		if (subobjetResponseDef.field.owningClass != objectResponse.getFieldClass()) {
			throw new GraphQLRequestPreparationException(
					"Class mismatch when trying to add the Field '" + subobjetResponseDef.getFieldName()
							+ "' owned by the class '" + subobjetResponseDef.getOwningClass().getName()
							+ "' to the field '" + objectResponse.getFieldName() + "' of class '"
							+ objectResponse.getFieldClass().getName() + "' (the two classes should be identical)");
		}
		// Let's check that this sub-object is not already in the list
		for (ObjectResponse subObject : objectResponse.subObjects) {
			if (subObject.field.name.equals(subobjetResponseDef.getFieldName())) {
				throw new GraphQLRequestPreparationException("The field <" + subObject.field.name
						+ "> is already in the field list for the objet <" + objectResponse.field.name + ">");
			}
		}

		// Then, we register this objectResponse as a subObject
		this.objectResponse.subObjects.add(subobjetResponseDef);

		// Let's go on with our builder
		return this;
	}

	/**
	 * Returns the built {@link ObjectResponse}. If no field (either scalar or suboject) has been added, then all scalar
	 * fields are added.
	 * 
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public ObjectResponse build() throws GraphQLRequestPreparationException {
		// If no field (either scalar or sub-object) has been added, then all scalar fields are added.
		if (objectResponse.scalarFields.size() == 0 && objectResponse.subObjects.size() == 0) {
			addKnownScalarFields();
		}
		return objectResponse;
	}

	/**
	 * Builds a {@link ObjectResponse} from a part of a GraphQL query. This part define what's expected as a response
	 * for the field of the current {@link ObjectResponse} for this builder.
	 * 
	 * @param queryResponseDef
	 *            A part of a response, for instance (for the hero query of the Star Wars GraphQL schema): "{ id name
	 *            friends{name}}"<BR/>
	 *            No special character are allowed (linefeed...).<BR/>
	 *            This parameter can be a null or an empty string. In this case, all scalar fields are added.
	 * @param episode
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public Builder withQueryResponseDef(String queryResponseDef) throws GraphQLRequestPreparationException {

		if (queryResponseDef == null || queryResponseDef.trim().equals("")) {
			addKnownScalarFields();
		} else {
			// Ok, we have to parse a string which looks like that: "{ id name friends{name}}"
			// We tokenize the string, by using the space as a delimiter, and all other special GraphQL characters
			StringTokenizer st = new StringTokenizer(queryResponseDef, STRING_TOKENIZER_DELIMITER, true);

			// We expect a first "{"
			// But leading spaces are allowed. Let's skip them.
			String token = " ";
			while (token.equals(" ")) {
				token = st.nextToken();
			}
			if (!token.equals("{")) {
				throw new GraphQLRequestPreparationException("The queryResponseDef should start with '{'");
			}

			QueryField queryField = new QueryField(objectResponse.field.owningClass, objectResponse.field.clazz,
					objectResponse.field.name);
			try {
				queryField.readTokenizerForResponseDefinition(st);
			} catch (GraphQLRequestPreparationException e) {
				throw new GraphQLRequestPreparationException(
						e.getMessage() + " while reading the queryReponseDef: " + queryResponseDef, e);
			}

			// We should have only spaces left
			while (st.hasMoreTokens()) {
				token = st.nextToken();
				switch (token) {
				case " ":
					// Nothing to do.
					break;
				default:
					throw new GraphQLRequestPreparationException(
							"Unexpected token <" + token + "> at the end of the queryReponseDef: " + queryResponseDef);
				}// switch
			} // while

			// Ok, the queryResponseDef has been parsed, and the content is store in our queryField.
			// Let's build our ObjectResponse
			withQueryField(queryField);
		}

		return this;

	}

	/**
	 * Add all scalar fields of the current class into the current {@link ObjectResponse}. The scalar fields which have
	 * already been added to the query are not added, just in case.
	 * 
	 * @throws GraphQLRequestPreparationException
	 * 
	 */
	private void addKnownScalarFields() throws GraphQLRequestPreparationException {
		if (objectResponse.getFieldClass().isInterface()) {
			// For interfaces, we loop through all getters
			for (Method method : objectResponse.getFieldClass().getDeclaredMethods()) {
				if (method.getName().startsWith("get")) {
					GraphQLScalar annotation = method.getAnnotation(GraphQLScalar.class);
					if (annotation != null) {
						// Ok, we have a getter (like getName), annotated by GraphQLNonScalar
						withField(getCamelCase(method.getName().substring(3)));
					}
				}
			}
		} else {
			// For classes, we loop through all attributes
			for (java.lang.reflect.Field attribute : objectResponse.getFieldClass().getDeclaredFields()) {
				GraphQLScalar annotation = attribute.getAnnotation(GraphQLScalar.class);
				if (annotation != null) {
					// Ok, we have a getter (like getName), annotated by GraphQLNonScalar
					withField(getCamelCase(attribute.getName()));
				}
			}
		}
	}

	/**
	 * Convert the given name, to a camel case name. Currenly very simple : it puts the first character in lower case.
	 * 
	 * @return
	 */
	public static String getCamelCase(String name) {
		return name.substring(0, 1).toLowerCase() + name.substring(1);
	}

	/**
	 * Reads the fields contained in the given {@link QueryField}, and call the relevant withXxx method of this builder
	 * 
	 * @param queryField
	 * @throws GraphQLRequestPreparationException
	 */
	private Builder withQueryField(QueryField queryField) throws GraphQLRequestPreparationException {
		if (!queryField.name.equals(objectResponse.getFieldName())) {
			throw new GraphQLRequestPreparationException("[INTERNAL ERROR] the field name of the queryField is <"
					+ queryField.name + "> whereas the field name of the objetResponseDef is <"
					+ objectResponse.getFieldName() + ">");
		}

		for (QueryField field : queryField.fields) {
			if (field.fields.size() == 0) {
				// It's a Scalar
				withField(field.name, field.alias, field.inputParameters);
			} else {
				// It's a non Scalar field : we'll recurse down one level, by calling withQueryField again.
				Builder subobjectResponseDef = new Builder(objectResponse.field.clazz, field.name, field.alias)
						.withQueryField(field).withInputParameters(field.inputParameters);
				withSubObject(subobjectResponseDef.build());
			}
		}

		return this;
	}

	/**
	 * Retrieves the {@link GraphQLScalarType} from this input parameter, if this parameter is a Custom Scalar
	 * 
	 * @param owningClass
	 *            The class that contains this field
	 * @param fieldName
	 *            The field name
	 * @param parameterName
	 *            The parameter name, which must be the name for an input parameter for this field in the GraphQL schema
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	private GraphQLScalarType getCustomScalarGraphQLType(Class<?> owningClass, String fieldName, String parameterName)
			throws GraphQLRequestPreparationException {

		Field field;
		try {
			field = owningClass.getDeclaredField(graphqlUtils.getJavaName(fieldName));
		} catch (NoSuchFieldException | SecurityException e) {
			throw new GraphQLRequestPreparationException("Error while looking for the the field '" + fieldName
					+ "' in the class '" + owningClass.getName() + "'", e);
		}

		GraphQLInputParameters inputParams = field.getAnnotation(GraphQLInputParameters.class);
		if (inputParams == null)
			throw new GraphQLRequestPreparationException("The field '" + fieldName + "' of the class '"
					+ owningClass.getName() + "' has no input parameters. Error while looking for its '" + parameterName
					+ "' input parameter");

		for (int i = 0; i < inputParams.names().length; i += 1) {
			if (inputParams.names()[i].equals(parameterName)) {
				// We've found the expected parameter
				String typeName = inputParams.types()[i];
				return CustomScalarRegistryImpl.customScalarRegistry.getGraphQLScalarType(typeName);
			}
		}

		throw new GraphQLRequestPreparationException(
				"The parameter of name '" + parameterName + "' has not been found for the field '" + fieldName
						+ "' of the class '" + owningClass.getName() + "'");

	}
}
