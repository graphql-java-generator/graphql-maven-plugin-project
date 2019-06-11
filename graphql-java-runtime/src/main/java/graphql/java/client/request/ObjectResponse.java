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
 * The structure is recursive: a {@link ObjectResponse} itsled contains one or more {@link ObjectResponse}, to describe
 * the Sub-object(s) that should be returned.<BR/>
 * A {@link ObjectResponse} can not be created directly. You must use an {@link Builder} to create a
 * {@link ObjectResponse}. This {@link Builder} allows to easily add fields, which can be scalars or sub-objects. And it
 * validates for each the GraphQL schema is respected.<BR/>
 * There are two types of {@link ObjectResponse}:
 * <UL>
 * <LI><B>Query {@link ObjectResponse}</B>: a query ResponsDef is returned by the generated code. For instance, if your
 * schema contains a QueryType type, a QueryType object will be generated. For each query in the GraphQL schema, this
 * QueryType object contains two methods: a getter which returns the {@link Builder} for this query, and the method wich
 * actually do the call to the GraphQL server for this query.</LI>
 * <LI><B>Sub-object {@link ObjectResponse}</B>: such a {@link ObjectResponse} allow you to define what's expected for a
 * field that is actually an object. This field is a sub-object of the object owning this field. To link such a
 * {@link ObjectResponse}, you'll use the {@link Builder#withSubObject(String, ObjectResponse)} or the
 * {@link Builder#withSubObject(String, String, ObjectResponse)} method.</LI>
 * </UL>
 * <BR/>
 * <BR/>
 * <H2>How to use the {@link Builder}</H2><BR/>
 * You'll first get a {@link ObjectResponse} from the generated QueryType (or whatever name you have in your GraphQL
 * schema for the query object), and its method getXxxxResponseDef, where Xxxx is the name of the query into the
 * QueryType.<BR/>
 * On this {@link Builder}, you can call:
 * <UL>
 * <LI><B>One of the withField methods</B> to add a scalar field to the expected response</LI>
 * <LI><B>One of the withEntity methods</B> to add a field whose type is not scalar. That is, to add a field whose type
 * is an object defined in the GraphQL schema. The withEntity methods needs a {@link ObjectResponse}. To get this
 * {@link ObjectResponse}, you'll create a {@link Builder} with one of the newSubObjectResponseDefBuilder methods.</LI>
 * </UL>
 * 
 * @author EtienneSF
 */
public class ObjectResponse {

	/** Logger for this class */
	private static Logger logger = LogManager.getLogger();

	static GraphqlUtils graphqlUtils = new GraphqlUtils();

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
	 * Indicates the fieldName within the owning objet, for which this {@link ObjectResponse} lists the fields and
	 * sub-objects that should be returned by the GraphQL server
	 */
	String fieldName;
	/**
	 * Indicates the class of the field, for which fieldName within the {@link ObjectResponse} lists the fields and
	 * sub-objects that should be returned by the GraphQL server
	 */
	Class<?> fieldClass;
	/**
	 * Indicates the alias under which this GraphQl field should be returned by the GraphQL server
	 */
	String fieldAlias;

	/** The list of fields that the GraphQL server should return for this GraphQL object */
	List<Field> scalarFields = new ArrayList<>();

	/**
	 * The list of direct sub-ojects that the GraphQL server should return for this GraphQL object, in the form of the
	 * list of what response is expected for each. This is recursive, so of course, this sub-object may also have their
	 * own sub-objects
	 */
	List<ObjectResponse> subObjects = new ArrayList<>();

	/**
	 * A {@link ObjectResponse} can only be created through the {@link Builder} created for it. See the
	 * {@link #newResponseDefBuilder(String)} to create such a builder.
	 * 
	 * @param fieldClass
	 *            the class of the field, for which we'll define the
	 * @see #newResponseDefBuilder(String)
	 */
	ObjectResponse(Class<?> fieldClass) {
		this.fieldClass = fieldClass;
	}

	/**
	 * Contruct a new {@link Builder}. You can then call the withField or withSubObject methods to
	 * 
	 * @param clazz
	 *            The Class for which contains the field for which this {@link ObjectResponse} defines the expected
	 *            response from the GraphQL server
	 * @param fieldName
	 *            The name of the field for which this {@link ObjectResponse} defines the expected response from the
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
	 *            The Class for which contains the field for which this {@link ObjectResponse} defines the expected
	 *            response from the GraphQL server
	 * @param fieldName
	 *            The name of the field for which this {@link ObjectResponse} defines the expected response from the
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

		// The next line checks that the given name is not null, and is owned by the ObjectResponse class (that is:
		// the given clazz), and that the alias is a valid GraphQL identifier
		ret.objectResponse.setOwningClass(clazz);
		ret.objectResponse.setField(fieldName, fieldAlias);

		return ret;
	}

	/**
	 * Contruct a new {@link Builder}
	 * 
	 * @param name
	 * @return
	 */
	public static Builder newSubObjectBuilder(Class<?> clazz) {
		return new Builder(clazz);
	}

	/**
	 * Sets the clazz that contains the non scalar field, which response is defined by the {@link ObjectResponse}.
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
	 * Defines the field for this {@link ObjectResponse}, with a null alias. See
	 * {@link ObjectResponse#setField(String, String)} for more information
	 * 
	 * @param fieldName
	 *            The name of the field
	 * @throws NullPointerException
	 *             If fieldName is null
	 * @throws GraphQLRequestPreparationException
	 *             If the given fieldName is not a valid identifier, or if the field is not owned by the class of this
	 *             {@link ObjectResponse} or if this field is not a scalar ({@link ObjectResponse} can not be built for
	 *             scalars)
	 */
	void setField(String fieldName) throws GraphQLRequestPreparationException {
		setField(fieldName, null);
	}

	/**
	 * Set the field for this {@link ObjectResponse}, that is the field for which the instance describes what response
	 * is expected from the GraphQL Server. This method checks that the given GraphQL name is valid, and that the class
	 * of this {@link ObjectResponse} actually contains such a field. This field can be either a non scalar field of an
	 * object, a query of a QueryType, a mutation of a MutationType, a subscription of a SubscriptionType.
	 * 
	 * @param fieldName
	 *            The name of the field
	 * @param fieldAlias
	 *            The alias for this field. It can be null
	 * @throws NullPointerException
	 *             If fieldName is null
	 * @throws GraphQLRequestPreparationException
	 *             If the given fieldName is not a valid identifier, or if the field is not owned by the class of this
	 *             {@link ObjectResponse} or if this field is not a scalar ({@link ObjectResponse} can not be built for
	 *             scalars)
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

	public String getFieldAlias() {
		return fieldAlias;
	}

	public Class<?> getFieldClass() {
		return fieldClass;
	}

	public String getFieldName() {
		return fieldName;
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

		// We first loop through the field of the current ObjectResponse
		scalarFields.stream().forEach(f -> appendFieldName(sb, f.name, f.alias));

		// Then we loop though all sub-objects
		for (ObjectResponse o : subObjects) {
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
