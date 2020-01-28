/**
 * 
 */
package com.graphql_java_generator.client.request;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.graphql_java_generator.GraphqlUtils;
import com.graphql_java_generator.annotation.GraphQLInputType;
import com.graphql_java_generator.client.QueryExecutorImpl;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;

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
 * @author EtienneSF
 */
public class InputParameter {

	/** A utility class, that's used here */
	private static GraphqlUtils graphqlUtils = new GraphqlUtils();

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

	final GraphQLScalarType graphQLScalarType;

	/**
	 * Creates and returns a new instance of {@link InputParameter}, which is bound to a bind variable. The value for
	 * this bind variable must be provided, when calling the request execution.
	 * 
	 * @param name
	 * @param bindParameterName
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
		return InputParameter.newBindParameter(name, bindParameterName, mandatory, null);
	}

	/**
	 * Creates and returns a new instance of {@link InputParameter}, which is bound to a bind variable. The value for
	 * this bind variable must be provided, when calling the request execution.
	 * 
	 * @param name
	 * @param bindParameterName
	 * @param mandatory
	 *            true if the parameter's value must be defined during request/mutation/subscription execution. <BR/>
	 *            If mandatory is true and the parameter's value is not provided, a
	 *            {@link GraphQLRequestExecutionException} exception is thrown at execution time<BR/>
	 *            If mandatory is false and the parameter's value is not provided, this input parameter is not sent to
	 *            the server
	 * @param graphQLScalarType
	 *            If this input parameter's type is a GraphQL Custom Scalar, it must be provided. Otherwise, it must be
	 *            null. <BR/>
	 *            graphQLScalarType contains the {@link GraphQLScalarType} that allows to convert the value to a String
	 *            that can be written in the GraphQL request, or convert from a String that is found in the GraphQL
	 *            response. If this type is not a GraphQL Custom Scalar, it must be null.
	 * @return
	 * @see QueryExecutorImpl#execute(String, ObjectResponse, List, Class)
	 */
	public static InputParameter newBindParameter(String name, String bindParameterName, boolean mandatory,
			GraphQLScalarType graphQLScalarType) {
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
	 * The constructor is private. Instances must be created with one of these helper methods:
	 * {@link #newBindParameter(String, String)} or {@link #newHardCodedParameter(String, Object)}
	 * 
	 * @param name
	 *            The parameter name, as defined in the GraphQL schema
	 * @param bindParameterName
	 *            The name of the bind parameter, as defined in the GraphQL response definition. If null, it's a hard
	 *            coded value. The value is then mandatory.
	 * @param value
	 *            The value to send, for this input parameter. If null, it's a bind parameter. The bindParameterName is
	 *            then mandatory.
	 * @param mandatory
	 *            true if the parameter's value must be defined during request/mutation/subscription execution. <BR/>
	 *            If mandatory is true and the parameter's value is not provided, a
	 *            {@link GraphQLRequestExecutionException} exception is thrown at execution time<BR/>
	 *            If mandatory is false and the parameter's value is not provided, this input parameter is not sent to
	 *            the server
	 * @param graphQLScalarType
	 *            If this input parameter's type is a GraphQL Custom Scalar, it must be provided. Otherwise, it must be
	 *            null. <BR/>
	 *            graphQLScalarType contains the {@link GraphQLScalarType} that allows to convert the value to a String
	 *            that can be written in the GraphQL request, or convert from a String that is found in the GraphQL
	 *            response. If this type is not a GraphQL Custom Scalar, it must be null.
	 */
	private InputParameter(String name, String bindParameterName, Object value, boolean mandatory,
			GraphQLScalarType graphQLScalarType) {
		this.name = name;
		this.bindParameterName = bindParameterName;
		this.value = value;
		this.mandatory = mandatory;
		this.graphQLScalarType = graphQLScalarType;
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
		if (this.value == null) {
			// It's a Bind Variable.

			// If the InputParameter is mandatory, which must have its value in the map of BindVariables.
			if (mandatory && (bindVariables == null || !bindVariables.keySet().contains(this.bindParameterName))) {
				throw new GraphQLRequestExecutionException("The Bind Parameter for '" + this.bindParameterName
						+ "' must be provided in the BindVariables map");
			}

			if (bindVariables == null || !bindVariables.keySet().contains(this.bindParameterName))
				return null;
			else
				return this.getValueForGraphqlQuery(bindVariables.get(this.bindParameterName));
		} else
			return this.getValueForGraphqlQuery(this.value);

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
	String getValueForGraphqlQuery(Object val) throws GraphQLRequestExecutionException {
		if (val == null) {
			return null;
		} else if (val instanceof java.util.List) {
			return getListValue((List<?>) val);
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
	private String getListValue(List<?> list) throws GraphQLRequestExecutionException {
		StringBuilder result = new StringBuilder("[");
		for (int index = 0; index < list.size(); index++) {
			Object obj = list.get(index);
			result.append(this.getValueForGraphqlQuery(obj));
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
				result.append(": ");
				result.append(getValueForGraphqlQuery(val));

				separator = ", ";
			}
		} // for

		return result.append("}").toString();
	}

}
