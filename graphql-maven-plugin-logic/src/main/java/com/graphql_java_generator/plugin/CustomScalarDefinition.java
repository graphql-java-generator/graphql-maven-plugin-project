/**
 * 
 */
package com.graphql_java_generator.plugin;

import java.io.Serializable;

import graphql.schema.GraphQLScalarType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This class contains the definition for a Custom Scalar type
 * 
 * @author etienne-sf
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomScalarDefinition implements Serializable {

	private static final long serialVersionUID = 1L;

	/** The type name, as defined in the GraphQL schema, for instance "Date" */
	String graphQLTypeName;

	/** The full class name for the java type that contains the data for this type, once in the Java code */
	String javaType;

	/**
	 * The full class name for the {@link GraphQLScalarType} that will manage this Custom Scalar. For instance:
	 * <I>com.graphql_java_generator.customscalars.GraphQLScalarTypeDate</I>.<BR/>
	 * You must provide exactly one of: graphQLScalarTypeClass, graphQLScalarTypeStaticField and
	 * graphQLScalarTypeGetter.
	 */
	String graphQLScalarTypeClass;

	/**
	 * The full class name followed by the static field name that contains the {@link GraphQLScalarType} that will
	 * manage this Custom Scalar. For instance: <I>graphql.Scalars.GraphQLLong</I>.<BR/>
	 * You must provide exactly one of: graphQLScalarTypeClass, graphQLScalarTypeStaticField and
	 * graphQLScalarTypeGetter.
	 */
	String graphQLScalarTypeStaticField;

	/**
	 * The full class name followed by the static method name that returns the {@link GraphQLScalarType} that will
	 * manage this Custom Scalar. For instance: <I>org.mycompany.MyScalars.getGraphQLLong()</I>. This call may contain
	 * parameters. Provided that this a valid java command<BR/>
	 * You must provide exactly one of: graphQLScalarTypeClass, graphQLScalarTypeStaticField and
	 * graphQLScalarTypeGetter.
	 */
	String graphQLScalarTypeGetter;

}
