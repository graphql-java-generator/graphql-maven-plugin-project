/**
 * 
 */
package com.graphql_java_generator.plugin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This class contains all one definitions for a Custom Scalar type
 * 
 * @author EtienneSF
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomScalarDefinition {

	/** The type name, as defined in the GraphQL schema, for instance "Date" */
	String graphQLTypeName;

	/** The full class name for the java type that contains the data for this type, once in the Java code */
	String javaType;

	/** The full class name for the CustomScalarConverter that will convert date from String, and to String */
	String customScalarConverter;

}
