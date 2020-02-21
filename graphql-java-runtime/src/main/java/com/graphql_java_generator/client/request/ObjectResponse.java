/**
 * 
 */
package com.graphql_java_generator.client.request;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;

import com.graphql_java_generator.client.GraphqlClientUtils;
import com.graphql_java_generator.client.QueryExecutor;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;

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

	static private GraphqlClientUtils graphqlClientUtils = new GraphqlClientUtils();

	/**
	 * Internal class represents a field of a GraphQL Object, that should appear in the response from the GraphQL
	 * server.
	 */
	static class Field {
		final String name;
		final String alias;
		final Class<?> owningClass;
		final Class<?> clazz;
		final List<InputParameter> inputParameters;

		Field(String name, String alias, Class<?> owningClass, Class<?> clazz, List<InputParameter> inputParameters)
				throws GraphQLRequestPreparationException {
			graphqlClientUtils.checkName(name);
			if (alias != null) {
				graphqlClientUtils.checkName(alias);
			}
			this.name = name;
			this.alias = alias;
			this.owningClass = owningClass;
			this.clazz = clazz;
			this.inputParameters = (inputParameters == null) ? new ArrayList<>() : inputParameters;
		}
	}

	Marker marker = QueryExecutor.GRAPHQL_MARKER;

	/**
	 * Indicates the field within the owning object, for which this {@link ObjectResponse} lists the fields and
	 * sub-objects that should be returned by the GraphQL server.
	 */
	final Field field;

	/** The list of fields that the GraphQL server should return for this GraphQL object */
	List<Field> scalarFields = new ArrayList<>();

	/**
	 * The list of direct sub-objects that the GraphQL server should return for this GraphQL object, in the form of the
	 * list of what response is expected for each. This is recursive, so of course, this sub-object may also have their
	 * own sub-objects
	 */
	List<ObjectResponse> subObjects = new ArrayList<>();

	/**
	 * A {@link ObjectResponse} can only be created through the {@link Builder} created for it. See the
	 * {@link #newResponseDefBuilder(String)} to create such a builder.
	 * 
	 * @param owningClass
	 *            The class that owns the field for which we create this ObjectResponse
	 * @param fieldName
	 *            The field in the owningClass that owns the field for which we create this ObjectResponse
	 * @throws GraphQLRequestPreparationException
	 *             If the given field name doesn't exist in this owningClass
	 * @see #newResponseDefBuilder(String)
	 */
	ObjectResponse(Class<?> owningClass, String fieldName) throws GraphQLRequestPreparationException {
		this(owningClass, fieldName, null);
	}

	/**
	 * A {@link ObjectResponse} can only be created through the {@link Builder} created for it. See the
	 * {@link #newResponseDefBuilder(String)} to create such a builder.
	 * 
	 * @param owningClass
	 *            The class that owns the field for which we create this ObjectResponse
	 * @param fieldName
	 *            The field in the owningClass that owns the field for which we create this ObjectResponse
	 * @param fieldAlias
	 *            The optional alias for this field (may be null)
	 * @throws GraphQLRequestPreparationException
	 *             If the given field name doesn't exist in this owningClass
	 * @see #newResponseDefBuilder(String)
	 */
	ObjectResponse(Class<?> owningClass, String fieldName, String fieldAlias)
			throws GraphQLRequestPreparationException {
		this.field = new Field(fieldName, fieldAlias, owningClass,
				graphqlClientUtils.checkFieldOfGraphQLType(fieldName, false, owningClass), null);
	}

	/**
	 * Retrieves the alias for the field, which response is defined by this instance
	 * 
	 * @return
	 */
	public String getFieldAlias() {
		return field.alias;
	}

	/**
	 * Retrieves the GraphQL type that owns the field, which response is defined by this instance
	 * 
	 * @return
	 */
	public Class<?> getOwningClass() {
		return field.owningClass;
	}

	/**
	 * Retrieves the class for the field, which response is defined by this instance
	 * 
	 * @return
	 */
	public Class<?> getFieldClass() {
		return field.clazz;
	}

	/**
	 * Retrieves the name for the field, which response is defined by this instance
	 * 
	 * @return
	 */
	public String getFieldName() {
		return field.name;
	}

	/**
	 * Retrieves the {@link InputParameter} for the field, which response is defined by this instance
	 * 
	 * @return
	 */
	public List<InputParameter> getInputParameters() {
		return field.inputParameters;
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
	 * @throws GraphQLRequestExecutionException
	 *             When there is an issue during execution, typically while managing the bind variables.
	 */
	public void appendResponseQuery(StringBuilder sb, Map<String, Object> parameters, boolean appendSpaceParam)
			throws GraphQLRequestExecutionException {

		//////////////////////////////////////////////////////////
		// We start with the field name
		appendFieldName(sb, appendSpaceParam, getFieldName(), getFieldAlias());

		//////////////////////////////////////////////////////////
		// Then the input parameters

		// Let's list the non null parameters ...
		List<String> params = new ArrayList<String>();
		for (InputParameter param : getInputParameters()) {
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
					sb.append(", ");
				writeComma = true;
				sb.append(param);
			} // for
			sb.append(")");
		}

		//////////////////////////////////////////////////////////
		// Then field list (if any)

		boolean appendSpaceLocal = false;
		if (scalarFields.size() > 0 || subObjects.size() > 0) {
			logger.debug("Appending ReponseDef content for field " + field.name + " of type " + field.clazz);
			sb.append("{");

			// We first loop through the field of the current ObjectResponse
			for (Field f : scalarFields) {
				appendFieldName(sb, appendSpaceLocal, f.name, f.alias);
				appendSpaceLocal = true;
			}

			// Then we loop though all sub-objects
			for (ObjectResponse o : subObjects) {
				o.appendResponseQuery(sb, parameters, appendSpaceLocal);
				appendSpaceLocal = true;
			} // for

			sb.append("}");
		}
	}

	/**
	 * Append one field (or object) name and optional alias to the given {@link StringBuilder}.
	 * 
	 * @param sb
	 * @param f
	 */
	void appendFieldName(StringBuilder sb, boolean appendSpace, String name, String alias) {

		if (appendSpace) {
			sb.append(" ");
		}

		// If we've an alias, let's write it
		if (alias != null) {
			sb.append(alias).append(":");
		}

		sb.append(name);
	}

	/**
	 * Add an {@link InputParameter} to this object.
	 * 
	 * @param inputParameter
	 */
	public void addInputParameter(InputParameter inputParameter) {
		field.inputParameters.add(inputParameter);
	}

	/**
	 * Add a list of {@link InputParameter}s to this object.
	 * 
	 * @param inputParameters
	 */
	public void addInputParameters(List<InputParameter> inputParameters) {
		field.inputParameters.addAll(inputParameters);
	}

}
