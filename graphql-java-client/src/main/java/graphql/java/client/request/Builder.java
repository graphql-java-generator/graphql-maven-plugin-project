package graphql.java.client.request;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import graphql.java.client.GraphqlUtils;
import graphql.java.client.response.GraphQLRequestPreparationException;

/**
 * This class is a Builder that'll help to build a {@link ObjectResponse}, which defines what should appear in the
 * response from the GraphQL server.
 * 
 * @author EtienneSF
 */
public class Builder {

	GraphqlUtils graphqlUtils = new GraphqlUtils();

	final ObjectResponse objectResponse;

	class QueryField {
		/** The name of this field */
		String name;
		/** The alias of this field */
		String alias;
		/**
		 * All subfields contained in this field. Empty if the field is a GraphQL Scalar. At least one if the field is a
		 * not a Scalar
		 */
		List<QueryField> fields = new ArrayList<>();

		QueryField(String name) {
			this.name = name;
		}

		public void readTokenizer(StringTokenizer st) throws GraphQLRequestPreparationException {
			// The last field we've read.
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
					throw new GraphQLRequestPreparationException(
							"The given query contains a '(' (parenthesis), field parameters are not managed yet");
				case "{":
					// The last field we've read is actually an object (a non Scalar GraphQL type), as it itself has
					// fields
					if (lastReadField == null) {
						throw new GraphQLRequestPreparationException(
								"The given query has two '{', one after another (error while reading field <" + name
										+ ">) while reading <" + this.name + ">");
					} else if (lastReadField.fields.size() > 0) {
						throw new GraphQLRequestPreparationException(
								"The given query contains a '{' not preceded by a fieldname, after field <"
										+ lastReadField.name + "> while reading <" + this.name + ">");
					} else {
						// Ok, let's read the field for the subobject, for which we just read the name (and potentiel
						// alias :
						lastReadField.readTokenizer(st);
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

			// Oups, we should not arrive here :
			throw new GraphQLRequestPreparationException("The field <" + name
					+ "> has a non finished list of fields (it lacks the finishing '}' while reading <" + this.name
					+ ">");
		}
	}

	Builder(Class<?> objectClass) {
		objectResponse = new ObjectResponse(objectClass);
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
		graphqlUtils.checkFieldOfGraphQLType(fieldName, true, objectResponse.fieldClass);

		// Let's check that this field is not already in the list
		for (ObjectResponse.Field field : objectResponse.scalarFields) {
			if (field.name.equals(fieldName)) {
				throw new GraphQLRequestPreparationException("The field <" + fieldName
						+ "> is already in the field list for the objet <" + objectResponse.fieldName + ">");
			}
		}

		// This will check that the alias is null or a valid GraphQL identifier
		objectResponse.scalarFields.add(new ObjectResponse.Field(fieldName, alias));
		return this;
	}

	/**
	 * Adds a non scalar field (a subobject) without alias, to the {@link ObjectResponse} we are building
	 * 
	 * @param fieldName
	 * @param alias
	 * @return The current builder, to allow the standard builder construction chain
	 * @throws NullPointerException
	 *             If the fieldName is null
	 * @throws GraphQLRequestPreparationException
	 *             If the fieldName or the fieldAlias is not valid
	 */
	public Builder withSubObject(String fieldName, ObjectResponse objectResponse)
			throws GraphQLRequestPreparationException {
		return withSubObject(fieldName, null, objectResponse);
	}

	/**
	 * Adds a scalar field (a subobject) with an alias, to the {@link ObjectResponse} we are building
	 * 
	 * @param fieldName
	 * @param alias
	 * @return The current builder, to allow the standard builder construction chain
	 * @throws NullPointerException
	 *             If the fieldName is null
	 * @throws GraphQLRequestPreparationException
	 *             If the fieldName or the fieldAlias is not valid
	 */
	public Builder withSubObject(String fieldName, String fieldAlias, ObjectResponse subobjetResponseDef)
			throws GraphQLRequestPreparationException {

		// The subobject is identified by the given fieldName in the fieldClass of the current objetResponseDef.
		// Let's check that the responseDefParam is of the good class.
		Class<?> subObjetClass = graphqlUtils.checkFieldOfGraphQLType(fieldName, false, subobjetResponseDef.fieldClass);
		if (!subObjetClass.equals(subobjetResponseDef.fieldClass)) {
			throw new GraphQLRequestPreparationException("Error creating subobject: the given field <" + fieldName
					+ "> is of type " + subObjetClass.getName() + ", but the given ObjetResponseDef is of type "
					+ subobjetResponseDef.fieldClass.getName());
		}

		// Let's check that this subobject is not already in the list
		for (ObjectResponse subObject : objectResponse.subObjects) {
			if (subObject.fieldName.equals(fieldName)) {
				throw new GraphQLRequestPreparationException("The field <" + subObject.fieldName
						+ "> is already in the field list for the objet <" + objectResponse.fieldName + ">");
			}
		}

		// Ok, s let's create the subobject
		subobjetResponseDef.setOwningClass(this.objectResponse.fieldClass);
		subobjetResponseDef.setField(fieldName, fieldAlias);

		// Then, we register this objectResponse as a subObject
		this.objectResponse.subObjects.add(subobjetResponseDef);

		// Let's go on with our builder
		return this;
	}

	public ObjectResponse build() {
		return objectResponse;
	}

	/**
	 * Builds a {@link ObjectResponse} from a part of a GraphQL query. This part define what's expected as a response
	 * for the field of the current {@link ObjectResponse} for this builder.
	 * 
	 * @param queryResponseDef
	 *            A part of a response, for instance (for the hero query of the Star Wars GraphQL schema): "{ id name
	 *            friends{name}}"<BR/>
	 *            No special character are allowed (linefeed...).
	 * @param episode
	 * @return
	 * @throws GraphQLRequestPreparationException
	 */
	public Builder withQueryResponseDef(String queryResponseDef) throws GraphQLRequestPreparationException {
		// Ok, we have to parse a string which looks like that: "{ id name friends{name}}"
		// We first replace each "{" by " { " and "}" by " } ". Then we tokenize the string, by using the space as a
		// delimiter
		StringTokenizer st = new StringTokenizer(queryResponseDef, " {}:()", true);
		// We expect a first "{"
		String token = " ";
		while (token.equals(" ")) {
			token = st.nextToken();
		}
		if (!token.equals("{")) {
			throw new GraphQLRequestPreparationException(
					"The queryResponseDef should start and finish with '{' and '}'");
		}

		QueryField queryField = new QueryField(objectResponse.fieldName);
		try {
			queryField.readTokenizer(st);
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

		return this;

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
				ObjectResponse subobjectResponseDef = ObjectResponse
						.newQueryResponseDefBuilder(objectResponse.fieldClass, field.name, field.alias)
						.withQueryField(field).build();
				withSubObject(field.name, field.alias, subobjectResponseDef);
			}
		}

		return this;
	}
}
