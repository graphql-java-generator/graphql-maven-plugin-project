package com.graphql_java_generator.client.request;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import com.graphql_java_generator.GraphqlUtils;
import com.graphql_java_generator.annotation.GraphQLScalar;
import com.graphql_java_generator.client.GraphqlClientUtils;
import com.graphql_java_generator.client.QueryExecutorImpl;
import com.graphql_java_generator.client.directive.Directive;
import com.graphql_java_generator.client.directive.DirectiveRegistry;
import com.graphql_java_generator.client.directive.DirectiveRegistryImpl;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;

import graphql.schema.GraphQLScalarType;

/**
 * This class is a Builder that'll help to build a {@link ObjectResponseOld}, which defines what should appear in the
 * response from the GraphQL server.
 * 
 * @author EtienneSF
 */
public class BuilderOld {

	/**
	 * The list of character that can separate tokens, in the GraphQL query string. These token are read by the
	 * {@link StringTokenizer}.
	 */
	public static final String STRING_TOKENIZER_DELIMITER = " {},:()\n\r\t@";

	GraphqlUtils graphqlUtils = GraphqlUtils.graphqlUtils;
	GraphqlClientUtils graphqlClientUtils = GraphqlClientUtils.graphqlClientUtils;
	DirectiveRegistry directiveRegistry = DirectiveRegistryImpl.directiveRegistry;

	final ObjectResponseOld objectResponse;
	List<Fragment> fragments;

	////////////////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////// START OF THE CLASS CODE /////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates a Builder, for a field without alias
	 * 
	 * @param owningClass
	 *            The class that contains this field
	 * @param fieldName
	 *            The field for which we must build an ObjectResponseOld
	 * @throws GraphQLRequestPreparationException
	 */
	public BuilderOld(Class<?> owningClass, String fieldName) throws GraphQLRequestPreparationException {
		this(owningClass, fieldName, null, false);
	}

	/**
	 * Creates a Builder
	 * 
	 * @param owningClass
	 *            The class that contains this field
	 * @param fieldName
	 *            The field for which we must build an ObjectResponseOld
	 * @param fieldAlias
	 *            Its optional alias (may be null)
	 * @throws GraphQLRequestPreparationException
	 */
	@Deprecated
	BuilderOld(Class<?> owningClass, String fieldName, String fieldAlias) throws GraphQLRequestPreparationException {
		this(owningClass, fieldName, fieldAlias, false);
	}

	/**
	 * Creates a Builder, for a field without alias
	 * 
	 * @param owningClass
	 *            The class that contains this field
	 * @param fieldName
	 *            The field for which we must build an ObjectResponseOld
	 * @param queryLevel
	 *            true if this {@link ObjectResponseOld} contains the response definition from the query level. This is
	 *            used in the {@link QueryExecutorImpl#execute(String, ObjectResponseOld, Map, Class)} method, to
	 *            properly build the request.
	 * @throws GraphQLRequestPreparationException
	 */
	public BuilderOld(Class<?> owningClass, String fieldName, boolean queryLevel)
			throws GraphQLRequestPreparationException {
		this(owningClass, fieldName, null, queryLevel);
	}

	/**
	 * Creates a Builder
	 * 
	 * @param owningClass
	 *            The class that contains this field
	 * @param fieldName
	 *            The field for which we must build an ObjectResponseOld
	 * @param fieldAlias
	 *            Its optional alias (may be null)
	 * @param queryLevel
	 *            true if this {@link ObjectResponseOld} contains the response definition from the query level. This is
	 *            used in the {@link QueryExecutorImpl#execute(String, ObjectResponseOld, Map, Class)} method, to
	 *            properly build the request.
	 * @throws GraphQLRequestPreparationException
	 */
	@Deprecated
	BuilderOld(Class<?> owningClass, String fieldName, String fieldAlias, boolean queryLevel)
			throws GraphQLRequestPreparationException {
		objectResponse = new ObjectResponseOld(owningClass, fieldName, fieldAlias);
		objectResponse.setQueryLevel(queryLevel);
	}

	/**
	 * Adds a scalar field with no alias, to the {@link ObjectResponseOld} we are building
	 * 
	 * @param fieldName
	 * @return The current builder, to allow the standard builder construction chain
	 * @throws NullPointerException
	 *             If the fieldName is null
	 * @throws GraphQLRequestPreparationException
	 *             If the fieldName or the fieldAlias is not valid
	 */
	@Deprecated
	BuilderOld withField(String fieldName) throws GraphQLRequestPreparationException {
		return withField(fieldName, null);
	}

	/**
	 * Adds a scalar field with an alias, to the {@link ObjectResponseOld} we are building. This field has no input
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
	@Deprecated
	BuilderOld withField(String fieldName, String alias) throws GraphQLRequestPreparationException {
		return withField(fieldName, alias, null, null);
	}

	/**
	 * Adds a scalar field with an alias, to the {@link ObjectResponseOld} we are building. This field has no input
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
	@Deprecated
	BuilderOld withField(String fieldName, String alias, List<InputParameter> inputParameters,
			List<Directive> directives) throws GraphQLRequestPreparationException {

		// We check that this field exist, and is a scaler
		graphqlClientUtils.checkFieldOfGraphQLType(fieldName, true, objectResponse.field.clazz);

		// Let's check that this field is not already in the list
		for (ObjectResponseOld.Field field : objectResponse.scalarFields) {
			if (field.name.equals(fieldName)) {
				throw new GraphQLRequestPreparationException("The field <" + fieldName
						+ "> is already in the field list for the objet <" + objectResponse.field.name + ">");
			}
		}

		ObjectResponseOld.Field field = new ObjectResponseOld.Field(fieldName, alias, objectResponse.field.clazz,
				graphqlClientUtils.checkFieldOfGraphQLType(fieldName, true, objectResponse.field.clazz),
				inputParameters, directives);

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
	public BuilderOld withInputParameter(InputParameter inputParameter) {
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
	@Deprecated
	BuilderOld withInputParameterHardCoded(String name, Object value) {
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
	@Deprecated
	BuilderOld withInputParameter(String name, String bindParameterName, boolean mandatory)
			throws GraphQLRequestPreparationException {
		GraphQLScalarType graphQLScalarType = graphqlClientUtils.getCustomScalarGraphQLType(null,
				objectResponse.getOwningClass(), objectResponse.getFieldName(), name);
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
	@Deprecated
	BuilderOld withInputParameters(List<InputParameter> inputParameters) {
		objectResponse.addInputParameters(inputParameters);
		return this;
	}

	/**
	 * Add a list of {@link Directive}s to the current Object Response definition.
	 * 
	 * @param directives
	 * @return The current {@link Builder}
	 */
	@Deprecated
	BuilderOld withDirectives(List<Directive> directives) {
		objectResponse.addDirectives(directives);
		return this;
	}

	@Deprecated
	BuilderOld withFragment(Fragment fragment) {
		fragments.add(fragment);
		return this;
	}

	/**
	 * Adds a non scalar field (a sub-object), to the {@link ObjectResponseOld} we are building. The given
	 * objectResponse contains the field name and its optional alias.
	 * 
	 * @param subobjetResponseDef
	 *            The {@link ObjectResponseOld} for this sub-object
	 * @return The current builder, to allow the standard builder construction chain
	 * @throws NullPointerException
	 *             If the fieldName is null
	 * @throws GraphQLRequestPreparationException
	 *             If the subobjetResponseDef can't be added. For instance: the fieldName or the fieldAlias is not
	 *             valid, or if the field of this subobjetResponseDef doesn't exist in the current owningClass...
	 */
	@Deprecated
	BuilderOld withSubObject(ObjectResponseOld subobjetResponseDef) throws GraphQLRequestPreparationException {
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
		for (ObjectResponseOld subObject : objectResponse.subObjects) {
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
	 * Returns the built {@link ObjectResponseOld}. If no field (either scalar or suboject) has been added, then all
	 * scalar fields are added.
	 * 
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public ObjectResponseOld build() throws GraphQLRequestPreparationException {
		// If no field (either scalar or sub-object) has been added, then all scalar fields are added.
		if (objectResponse.scalarFields.size() == 0 && objectResponse.subObjects.size() == 0) {
			addKnownScalarFields();
		}
		// We add the __typename field for every type that is queried, if __typename was not already queried.
		// This allows to manage returned GraphQL interfaces and unions instances, to be instanciated in the proper java
		// class.
		addTypenameFields(objectResponse);

		return objectResponse;
	}

	/**
	 * Builds a {@link ObjectResponseOld} from a part of a GraphQL query. This part define what's expected as a response
	 * for the field of the current {@link ObjectResponseOld} for this builder.
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
	public BuilderOld withQueryResponseDef(String queryResponseDef) throws GraphQLRequestPreparationException {

		if (queryResponseDef == null || queryResponseDef.trim().equals("")) {
			addKnownScalarFields();
		} else {
			// Ok, we have to parse a string which looks like that: "{ id name friends{name}}"
			// We tokenize the string, by using the space as a delimiter, and all other special GraphQL characters
			StringTokenizer st = new StringTokenizer(queryResponseDef, STRING_TOKENIZER_DELIMITER, true);
			QueryField queryField = null;

			// We scan the input string. It may contain fragment definition and query/mutation/subscription
			while (st.hasMoreTokens()) {
				String token = st.nextToken();

				switch (token) {
				case " ":
				case "\n":
				case "\r":
				case "\t":
					break;
				case "{":
					// We've found the start of the query/mutation/subscription
					queryField = new QueryField(objectResponse.field.owningClass, objectResponse.field.name);
					// try {
					// queryField.readTokenizerForResponseDefinition(st);
					// } catch (GraphQLRequestPreparationException e) {
					// throw new GraphQLRequestPreparationException(
					// e.getMessage() + " while reading the queryReponseDef: " + queryResponseDef, e);
					// }
					break;
				case "query":
				case "mutation":
				case "subscription":
					// No action
					break;
				case "fragment":
					// withFragment(new Fragment(st, null));
					break;
				default:
					throw new GraphQLRequestPreparationException(
							"Unknown token '" + token + " while reading the queryReponseDef: " + queryResponseDef);
				}
			}

			if (queryField == null) {
				throw new GraphQLRequestPreparationException("No response definition found");
			}

			// Ok, the queryResponseDef has been parsed, and the content is store in our queryField.
			// Let's build our ObjectResponseOld
			withQueryField(queryField);
		}

		return this;

	}

	/**
	 * Add all scalar fields of the current class into the current {@link ObjectResponseOld}. The scalar fields which
	 * have already been added to the query are not added, just in case.
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
	private BuilderOld withQueryField(QueryField queryField) throws GraphQLRequestPreparationException {
		if (!queryField.name.equals(objectResponse.getFieldName())) {
			throw new GraphQLRequestPreparationException("[INTERNAL ERROR] the field name of the queryField is <"
					+ queryField.name + "> whereas the field name of the objetResponseDef is <"
					+ objectResponse.getFieldName() + ">");
		}

		for (QueryField field : queryField.fields) {
			if (field.fields.size() == 0) {
				// It's a Scalar
				withField(field.name, field.alias, field.inputParameters, field.directives);
			} else {
				// It's a non Scalar field : we'll recurse down one level, by calling withQueryField again.
				BuilderOld subobjectResponseDef = new BuilderOld(objectResponse.field.clazz, field.name, field.alias)
						.withQueryField(field).withInputParameters(field.inputParameters)
						.withDirectives(field.directives);
				withSubObject(subobjectResponseDef.build());
			}
		}

		return this;
	}

	/**
	 * Adds the _typename into the scalar fields list (if it doesn't already exist) for this ObjectResponseOld, and fo
	 * the same recursively for all its sub-objects responses.
	 * 
	 * @param objectResponse
	 * @throws GraphQLRequestPreparationException
	 */
	private void addTypenameFields(ObjectResponseOld objectResponse) throws GraphQLRequestPreparationException {

		// We add the __typename for all levels, but the query/mutation/subscription one
		if (!objectResponse.isQueryLevel()) {
			// Let's look for an existing __typename field
			ObjectResponseOld.Field __typename = null;
			for (ObjectResponseOld.Field f : objectResponse.scalarFields) {
				if (f.name.equals("__typename")) {
					__typename = f;
					break;
				}
			}
			// If __typename was not found, we add it
			if (__typename == null) {
				__typename = new ObjectResponseOld.Field("__typename", null, objectResponse.getFieldClass(),
						String.class, null, null);
				objectResponse.scalarFields.add(__typename);
			}
		}

		// Then we recurse into every sub-object
		for (ObjectResponseOld or : objectResponse.subObjects) {
			// For subobjects, we always add the __typename field
			addTypenameFields(or);
		}
	}

}
