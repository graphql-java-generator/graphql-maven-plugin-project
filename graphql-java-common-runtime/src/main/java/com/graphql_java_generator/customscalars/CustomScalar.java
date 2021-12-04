/**
 * 
 */
package com.graphql_java_generator.customscalars;

import graphql.schema.GraphQLScalarType;

/**
 * This class represents a custom scalar, with all the information to allow proper execution of the generated code
 * 
 * @author etienne-sf
 */
public class CustomScalar {

	/**
	 * The java type that will contain values for this custom scalar. This is needed to properly create the data from
	 * the value read in a string, especially when reading a GraphQL request, when in client mode
	 */
	private Class<?> valueClazz;

	/** The {@link GraphQLScalarType} type, that will manage serialization and deserialization */
	private GraphQLScalarType graphQLScalarType;

	public CustomScalar(GraphQLScalarType graphQLScalarType, Class<?> valueClazz) {
		super();
		this.valueClazz = valueClazz;
		this.graphQLScalarType = graphQLScalarType;
	}

	public String getGraphQLTypeName() {
		return graphQLScalarType.getName();
	}

	public Class<?> getValueClazz() {
		return valueClazz;
	}

	public void setValueClazz(Class<?> valueClazz) {
		this.valueClazz = valueClazz;
	}

	public GraphQLScalarType getGraphQLScalarType() {
		return graphQLScalarType;
	}

	public void setGraphQLScalarType(GraphQLScalarType graphQLScalarType) {
		this.graphQLScalarType = graphQLScalarType;
	}

}
