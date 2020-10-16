/**
 * 
 */
package com.graphql_java_generator.plugin.language;

import com.graphql_java_generator.plugin.conf.CustomScalarDefinition;

/**
 * All types found in the GraphQL schema(s), and discovered during the GraphQL parsing, are instance of
 * {@link CustomScalar}.
 * 
 * @author etienne-sf
 */
public interface CustomScalar extends Type {

	// public enum KindOfValue {
	// /** An array of values */
	// // ARRAY,
	// // BOOLEAN
	// // ENUM,
	// /** Any decimal number that can be stored into a {@link BigDecimal} */
	// FLOAT,
	// /** Any integer value that can be stored in a {@link BigInteger} type */
	// INT,
	// /** A scalar serialized a an object */
	// OBJECT,
	// /** A value that is propagated surrounded by double quotes */
	// STRING
	// };
	//
	// /** The kind of values that is written in the GraphQL request and response */
	// KindOfValue getKindOfValue();

	/** retrieves the definition for this custom scalar */
	public CustomScalarDefinition getCustomScalarDefinition();

}
