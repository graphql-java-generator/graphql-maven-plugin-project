/**
 * 
 */
package graphql.java.client.request;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;

import graphql.java.client.GraphqlUtils;
import graphql.java.client.QueryExecutor;
import graphql.java.client.response.GraphQLRequestPreparationException;

/**
 * This class describes what response is expected from the GraphQL server. That is: the fields and sub-objects that the
 * response from the GraphQL server should contain, for one GraphQL type. <BR/>
 * The structure is recursive: a {@link ObjectResponseDef} itsled contains one or more {@link ObjectResponseDef}, to
 * describe the Sub-object(s) that should be returned.<BR/>
 * A {@link ObjectResponseDef} can not be created directly. You must use an {@link Builder} to create a
 * {@link ObjectResponseDef}. This {@link Builder} allows to easily add fields, which can be scalars or sub-objects. And
 * it validates for each the GraphQL schema is respected.<BR/>
 * There are two types of {@link ObjectResponseDef}:
 * <UL>
 * <LI><B>Query {@link ObjectResponseDef}</B>: a query ResponsDef is returned by the generated code. For instance, if
 * your schema contains a QueryType type, a QueryType object will be generated. For each query in the GraphQL schema,
 * this QueryType object contains two methods: a getter which returns the {@link Builder} for this query, and the method
 * wich actually do the call to the GraphQL server for this query.</LI>
 * <LI><B>Sub-object {@link ObjectResponseDef}</B>: such a {@link ObjectResponseDef} allow you to define what's expected
 * for a field that is actually an object. This field is a sub-object of the object owning this field. To link such a
 * {@link ObjectResponseDef}, you'll use the {@link Builder#withSubObject(String, ObjectResponseDef)} or the
 * {@link Builder#withSubObject(String, String, ObjectResponseDef)} method.</LI>
 * </UL>
 * <BR/>
 * <BR/>
 * <H2>How to use the {@link Builder}</H2><BR/>
 * You'll first get a {@link ObjectResponseDef} from the generated QueryType (or whatever name you have in your GraphQL
 * schema for the query object), and its method getXxxxResponseDef, where Xxxx is the name of the query into the
 * QueryType.<BR/>
 * On this {@link Builder}, you can call:
 * <UL>
 * <LI><B>One of the withField methods</B> to add a scalar field to the expected response</LI>
 * <LI><B>One of the withEntity methods</B> to add a field whose type is not scalar. That is, to add a field whose type
 * is an object defined in the GraphQL schema. The withEntity methods needs a {@link ObjectResponseDef}. To get this
 * {@link ObjectResponseDef}, you'll create a {@link Builder} with one of the newSubObjectResponseDefBuilder
 * methods.</LI>
 * </UL>
 * 
 * @author EtienneSF
 */
public class ObjectResponseDef {

	/** Logger for this class */
	private static Logger logger = LogManager.getLogger();

	static GraphqlUtils graphqlUtils = new GraphqlUtils();

	/** This class is a Builder that'll help to define what should appear in the response from the GraphQL server */
	public static class Builder {
		final ObjectResponseDef objectResponseDef;

		Builder(Class<?> objectClass) {
			objectResponseDef = new ObjectResponseDef(objectClass);
		}

		/**
		 * Adds a scalar field with no alias, to the {@link ObjectResponseDef} we are building
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
		 * Adds a scalar field with an alias, to the {@link ObjectResponseDef} we are building
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
			graphqlUtils.checkFieldOfGraphQLType(fieldName, true, objectResponseDef.fieldClass);

			// This will check that the alias is null or a valid GraphQL identifier
			objectResponseDef.fields.add(new Field(fieldName, alias));
			return this;
		}

		/**
		 * Adds a non scalar field (a subobject) without alias, to the {@link ObjectResponseDef} we are building
		 * 
		 * @param fieldName
		 * @param alias
		 * @return The current builder, to allow the standard builder construction chain
		 * @throws NullPointerException
		 *             If the fieldName is null
		 * @throws GraphQLRequestPreparationException
		 *             If the fieldName or the fieldAlias is not valid
		 */
		public Builder withSubObject(String fieldName, ObjectResponseDef objectResponseDef)
				throws GraphQLRequestPreparationException {
			return withSubObject(fieldName, null, objectResponseDef);
		}

		/**
		 * Adds a scalar field (a subobject) with an alias, to the {@link ObjectResponseDef} we are building
		 * 
		 * @param fieldName
		 * @param alias
		 * @return The current builder, to allow the standard builder construction chain
		 * @throws NullPointerException
		 *             If the fieldName is null
		 * @throws GraphQLRequestPreparationException
		 *             If the fieldName or the fieldAlias is not valid
		 */
		public Builder withSubObject(String fieldName, String fieldAlias, ObjectResponseDef subobjetResponseDef)
				throws GraphQLRequestPreparationException {
			// The subobject is identified by the given fieldName in the fieldClass of the current objetResponseDef.
			// Let's check that the responseDefParam is of the good class.
			Class<?> subObjetClass = graphqlUtils.checkFieldOfGraphQLType(fieldName, false,
					subobjetResponseDef.fieldClass);
			if (!subObjetClass.equals(subobjetResponseDef.fieldClass)) {
				throw new GraphQLRequestPreparationException("Error creating subobject: the given field <" + fieldName
						+ "> is of type " + subObjetClass.getName() + ", but the given ObjetResponseDef is of type "
						+ subobjetResponseDef.fieldClass.getName());
			}

			// Ok, s let's create the subobject
			subobjetResponseDef.setOwningClass(this.objectResponseDef.fieldClass);
			subobjetResponseDef.setField(fieldName, fieldAlias);

			// Then, we register this objectResponseDef as a subObject
			this.objectResponseDef.subObjects.add(subobjetResponseDef);

			// Let's go on with our builder
			return this;
		}

		public ObjectResponseDef build() {
			return objectResponseDef;
		}
	}

	/**
	 * Internal class represents an attribute of a GraphQL Object, that should appear in the response from the GraphQL
	 * server.
	 */
	static class Field {
		final String name;
		final String alias;

		Field(String name, String alias) throws GraphQLRequestPreparationException {
			graphqlUtils.checkName(name);
			if (alias != null) {
				graphqlUtils.checkName(alias);
			}
			this.name = name;
			this.alias = alias;
		}
	}

	Marker marker = QueryExecutor.GRAPHQL_MARKER;

	/**
	 * Indicates the GraphQL which contains the field, for which this object lists the field and sub-objects that should
	 * be returned by the GraphQL server
	 */
	Class<?> owningClass;

	/**
	 * Indicates the fieldName within the owning objet, for which this {@link ObjectResponseDef} lists the fields and
	 * sub-objects that should be returned by the GraphQL server
	 */
	String fieldName;
	/**
	 * Indicates the class of the field, for which fieldName within the {@link ObjectResponseDef} lists the fields and
	 * sub-objects that should be returned by the GraphQL server
	 */
	Class<?> fieldClass;
	/**
	 * Indicates the alias under which this GraphQl field should be returned by the GraphQL server
	 */
	String fieldAlias;

	/** The list of fields that the GraphQL server should return for this GraphQL object */
	List<Field> fields = new ArrayList<>();

	/**
	 * The list of direct sub-ojects that the GraphQL server should return for this GraphQL object, in the form of the
	 * list of what response is expected for each. This is recursive, so of course, this sub-object may also have their
	 * own sub-objects
	 */
	List<ObjectResponseDef> subObjects = new ArrayList<>();

	/**
	 * A {@link ObjectResponseDef} can only be created through the {@link Builder} created for it. See the
	 * {@link #newResponseDefBuilder(String)} to create such a builder.
	 * 
	 * @param fieldClass
	 *            the class of the field, for which we'll define the
	 * @see #newResponseDefBuilder(String)
	 */
	ObjectResponseDef(Class<?> fieldClass) {
		this.fieldClass = fieldClass;
	}

	/**
	 * Contruct a new {@link Builder}. You can then call the withField or withSubObject methods to
	 * 
	 * @param clazz
	 *            The Class for which contains the field for which this {@link ObjectResponseDef} defines the expected
	 *            response from the GraphQL server
	 * @param fieldName
	 *            The name of the field for which this {@link ObjectResponseDef} defines the expected response from the
	 *            GraphQL server. There will be no alias for this field in the request.
	 * @return
	 * @throws NullPointerException
	 *             If the fieldName is null
	 * @throws GraphQLRequestPreparationException
	 *             If the fieldName is not valid
	 */
	public static Builder newQueryResponseDefBuilder(Class<?> clazz, String fieldName)
			throws GraphQLRequestPreparationException {
		return newQueryResponseDefBuilder(clazz, fieldName, null);
	}

	/**
	 * Contruct a new {@link Builder}
	 * 
	 * @param clazz
	 *            The Class for which contains the field for which this {@link ObjectResponseDef} defines the expected
	 *            response from the GraphQL server
	 * @param fieldName
	 *            The name of the field for which this {@link ObjectResponseDef} defines the expected response from the
	 *            GraphQL server
	 * @param fieldAlias
	 *            The alias for this field. Typically necessay if you want to do two queries of the name within one
	 *            server call
	 * @return
	 * @throws NullPointerException
	 *             If the fieldName is null
	 * @throws GraphQLRequestPreparationException
	 *             If the fieldName or the fieldAlias is not valid
	 */
	public static Builder newQueryResponseDefBuilder(Class<?> clazz, String fieldName, String fieldAlias)
			throws GraphQLRequestPreparationException {
		Builder ret = new Builder(clazz);

		// The next line checks that the given name is not null, and is owned by the ObjectResponseDef class (that is:
		// the given clazz), and that the alias is a valid GraphQL identifier
		ret.objectResponseDef.setOwningClass(clazz);
		ret.objectResponseDef.setField(fieldName, fieldAlias);

		return ret;
	}

	/**
	 * Contruct a new {@link Builder}
	 * 
	 * @param name
	 * @return
	 */
	public static Builder newSubObjectResponseDefBuilder(Class<?> clazz) {
		return new Builder(clazz);
	}

	/**
	 * Sets the clazz that contains the non scalar field, which response is defined by the {@link ObjectResponseDef}.
	 * 
	 * @param clazz
	 *            may not be null
	 * @throw NullPointerException when clazz is null
	 */
	void setOwningClass(Class<?> clazz) {
		if (clazz == null) {
			throw new NullPointerException("The owningClass of an " + this.getClass().getName() + " may not be null");
		}
		this.owningClass = clazz;
	}

	/**
	 * Defines the field for this {@link ObjectResponseDef}, with a null alias. See
	 * {@link ObjectResponseDef#setField(String, String)} for more information
	 * 
	 * @param fieldName
	 *            The name of the field
	 * @throws NullPointerException
	 *             If fieldName is null
	 * @throws GraphQLRequestPreparationException
	 *             If the given fieldName is not a valid identifier, or if the field is not owned by the class of this
	 *             {@link ObjectResponseDef} or if this field is not a scalar ({@link ObjectResponseDef} can not be
	 *             built for scalars)
	 */
	void setField(String fieldName) throws GraphQLRequestPreparationException {
		setField(fieldName, null);
	}

	/**
	 * Set the field for this {@link ObjectResponseDef}, that is the field for which the instance describes what
	 * response is expected from the GraphQL Server. This method checks that the given GraphQL name is valid, and that
	 * the class of this {@link ObjectResponseDef} actually contains such a field. This field can be either a non scalar
	 * field of an object, a query of a QueryType, a mutation of a MutationType, a subscription of a SubscriptionType.
	 * 
	 * @param fieldName
	 *            The name of the field
	 * @param fieldAlias
	 *            The alias for this field. It can be null
	 * @throws NullPointerException
	 *             If fieldName is null
	 * @throws GraphQLRequestPreparationException
	 *             If the given fieldName is not a valid identifier, or if the field is not owned by the class of this
	 *             {@link ObjectResponseDef} or if this field is not a scalar ({@link ObjectResponseDef} can not be
	 *             built for scalars)
	 */
	void setField(String fieldName, String fieldAlias) throws GraphQLRequestPreparationException {
		// We check that this field exist, whether or not it is a scaler
		Class<?> clazz = graphqlUtils.checkFieldOfGraphQLType(fieldName, false, owningClass);
		if (fieldAlias != null) {
			graphqlUtils.checkName(fieldAlias);
		}

		this.fieldName = fieldName;
		this.fieldAlias = fieldAlias;
		this.fieldClass = clazz;
	}

	public String getFieldName() {
		return fieldName;
	}

	public String getFieldAlias() {
		return fieldAlias;
	}

	/**
	 * Retrieves the part of the query, which describes the fields that the GraphQL server should return.<BR/>
	 * For instance, for the query: <I>{hero(episode: NEWHOPE) {id name}}</I>, the response definition is <I>{id
	 * name}</I>
	 * 
	 * @param sb
	 *            The {@link StringBuilder} where the response must be appended
	 * 
	 * @return
	 */
	public void appendResponseQuery(StringBuilder sb) {
		logger.debug("Appending ReponseDef content for field " + fieldName + " of type " + fieldClass);
		sb.append("{");

		// We first loop through the field of the current ObjectResponseDef
		fields.stream().forEach(f -> appendFieldName(sb, f.name, f.alias));

		// Then we loop though all sub-objects
		for (ObjectResponseDef o : subObjects) {
			appendFieldName(sb, o.fieldName, o.fieldAlias);
			// Let's add all queried fields for this object
			o.appendResponseQuery(sb);
		} // for

		sb.append("}");
	}

	/**
	 * Append one field (or object) name and optional alias to the given {@link StringBuilder}.
	 * 
	 * @param sb
	 * @param f
	 */
	void appendFieldName(StringBuilder sb, String name, String alias) {
		sb.append(" ");

		// If we've an alias, let's write it
		if (alias != null) {
			sb.append(alias).append(": ");
		}

		sb.append(name);
	}

}
