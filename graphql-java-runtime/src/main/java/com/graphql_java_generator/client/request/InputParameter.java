/**
 * 
 */
package com.graphql_java_generator.client.request;

import java.util.List;

/**
 * Contains a parameter, to be sent to a query (mutation...).
 * 
 * @author EtienneSF
 */
public class InputParameter {

	/** The parameter name, as defined in the GraphQL schema */
	final String name;

	/** The value to send, for this input parameter */
	final Object value;

	/**
	 * @param name
	 *            The parameter name, as defined in the GraphQL schema
	 * @param value
	 *            The value to send, for this input parameter
	 */
	public InputParameter(String name, Object value) {
		this.name = name;
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
	 * @return
	 */
	public String getValueForGraphqlQuery() {
		return this.getValueForGraphqlQuery(this.value);
	}

	/**
	 * This method is used both by {@link #getValueForGraphqlQuery()} and {@link #getListValue(List)} to extract a value
	 * as a string.
	 * 
	 * @param val
	 * @return
	 */
	private String getValueForGraphqlQuery(Object val) {
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
