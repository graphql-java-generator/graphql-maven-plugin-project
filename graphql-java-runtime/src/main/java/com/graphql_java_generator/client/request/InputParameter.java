/**
 * 
 */
package com.graphql_java_generator.client.request;

import java.util.List;
import java.util.Map;

import com.graphql_java_generator.client.QueryExecutorImpl;
import com.graphql_java_generator.client.response.GraphQLRequestExecutionException;

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

	/** The parameter name, as defined in the GraphQL schema */
	final String name;

	/**
	 * The bind parameter, as defined in the GraphQL query. <BR/>
	 * For instance <I>sinceParam</I> in <I>posts(since: :sinceParam) {date}</I>
	 */
	final String bindParameterName;

	/** The value to send, for this input parameter */
	final Object value;

	/**
	 * Creates and returns a new instance of {@link InputParameter}, which is bound to a bind variable. The value for
	 * this bind variable must be provided, when calling the request execution.
	 * 
	 * @param name
	 * @param bindParameterName
	 * @return
	 * @see QueryExecutorImpl#execute(String, ObjectResponse, List, Class)
	 */
	public static InputParameter newBindParameter(String name, String bindParameterName) {
		return new InputParameter(name, bindParameterName, null);
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
		return new InputParameter(name, null, value);
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
	 */
	private InputParameter(String name, String bindParameterName, Object value) {
		this.name = name;
		this.bindParameterName = bindParameterName;
		this.value = value;
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
			// It's a Bind Variable. Let's get its value, which should be given in the map of BindVariables.
			if (bindVariables == null) {
				throw new NullPointerException(
						"Internal error: the bindVariables Map is mandatory, as this parameter is a Bind Parameter");
			} else if (!bindVariables.keySet().contains(this.bindParameterName)) {
				throw new GraphQLRequestExecutionException("The Bind Parameter for '" + this.bindParameterName
						+ "' must be provided in the BindVariables map");
			}
			return this.getValueForGraphqlQuery(bindVariables.get(this.bindParameterName));
		} else {
			return this.getValueForGraphqlQuery(this.value);
		}

	}

	/**
	 * This method is used both by {@link #getValueForGraphqlQuery()} and {@link #getListValue(List)} to extract a value
	 * as a string.
	 * 
	 * @param val
	 * @return
	 */
	public String getValueForGraphqlQuery(Object val) {
		if (val == null) {
			return null;
		} else if (val instanceof String) {
			return getStringValue((String) val);
		} else if (val instanceof java.util.List) {
			return getListValue((List<?>) val);
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
	 * @param lst
	 * @return
	 */
	private String getListValue(List<?> lst) {
		if (lst == null) {
			return null;
		} else {
			StringBuilder result = new StringBuilder("[");
			for (int index = 0; index < lst.size(); index++) {
				Object obj = lst.get(index);
				result.append(this.getValueForGraphqlQuery(obj));
				if (index < lst.size() - 1) {
					result.append(",");
				}
			}
			return result.append("]").toString();
		}
	}

}
