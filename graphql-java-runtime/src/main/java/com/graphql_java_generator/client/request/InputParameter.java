/**
 * 
 */
package com.graphql_java_generator.client.request;

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
		if (value == null) {
			return null;
		} else {
			switch (value.getClass().getName()) {
			case "java.lang.String":
				return getStringValue((String) value);
			default:
				return value.toString();
			}
		}
	}

	/**
	 * @return
	 */
	private static String getStringValue(String str) {
		return "\\\"" + str.replace("\"", "\\\"") + "\\\"";
	}

}
