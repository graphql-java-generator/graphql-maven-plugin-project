package com.graphql_java_generator.client.request;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import com.graphql_java_generator.annotation.GraphQLScalar;
import com.graphql_java_generator.client.GraphqlUtils;
import com.graphql_java_generator.client.response.GraphQLRequestPreparationException;

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
		/** The name of this field */
		String name;
		/** The alias of this field */
		String alias;

		/** The list of input parameters for this QueryFields */
		List<InputParameter> inputParameters = new ArrayList<>();

		/**
		 * All subfields contained in this field. Empty if the field is a GraphQL Scalar. At least one if the field is a
		 * not a Scalar
		 */
		List<QueryField> fields = new ArrayList<>();

		QueryField(String name) {
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
					lastReadField = new QueryField(token);
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
							inputParameters.add(InputParameter.newBindParameter(parameterName, token.substring(1)));
						} else {
							// The inputParameter should start and end by "
							if (!token.startsWith("\"") || !token.endsWith("\"")) {
								throw new GraphQLRequestPreparationException(
										"Bad parameter value: parameter values should start and finish by \". But it's not the case for the value <"
												+ token + "> of parameter <" + parameterName + ">");
							}
							String value = token.substring(1, token.length() - 1);
							inputParameters.add(InputParameter.newHardCodedParameter(parameterName, value));
						}
						step = InputParameterStep.NAME;
						break;
					}
				}
			}

			throw new GraphQLRequestPreparationException(
					"The list of parameters for the field '" + name + "' is not finished (no closing parenthesis)");
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
	 * Adds a scalar field with an alias, to the {@link ObjectResponse} we are building
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
		// We check that this field exist, and is a scaler
		graphqlUtils.checkFieldOfGraphQLType(fieldName, true, objectResponse.field.clazz);

		// Let's check that this field is not already in the list
		for (ObjectResponse.Field field : objectResponse.scalarFields) {
			if (field.name.equals(fieldName)) {
				throw new GraphQLRequestPreparationException("The field <" + fieldName
						+ "> is already in the field list for the objet <" + objectResponse.field.name + ">");
			}
		}

		// This will check that the alias is null or a valid GraphQL identifier
		objectResponse.scalarFields.add(new ObjectResponse.Field(fieldName, alias, objectResponse.field.clazz,
				graphqlUtils.checkFieldOfGraphQLType(fieldName, true, objectResponse.field.clazz)));
		return this;
	}

	/**
	 * Add an {@link InputParameter} to the current Object Response definition.
	 * 
	 * @param inputParameter
	 * @return The current {@link Builder}
	 */
	public Builder withInputParameter(InputParameter inputParameter) {
		objectResponse.addInputParameter(inputParameter);
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
			throw new GraphQLRequestPreparationException("Trying to add the Field '"
					+ subobjetResponseDef.getFieldName() + "' owned by the class '"
					+ subobjetResponseDef.getOwningClass().getName() + "' to the field '"
					+ objectResponse.getFieldName() + "' of class '" + objectResponse.getFieldClass().getName() + "'");
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

			QueryField queryField = new QueryField(objectResponse.field.name);
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
				withField(field.name, field.alias);
			} else {
				// It's a non Scalar field : we'll recurse down one level, by calling withQueryField again.
				Builder subobjectResponseDef = new Builder(objectResponse.field.clazz, field.name, field.alias)
						.withQueryField(field).withInputParameters(field.inputParameters);
				withSubObject(subobjectResponseDef.build());
			}
		}

		return this;
	}
}
