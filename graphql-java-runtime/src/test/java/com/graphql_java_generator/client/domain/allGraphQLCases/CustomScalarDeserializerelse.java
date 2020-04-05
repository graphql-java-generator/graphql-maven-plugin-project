package com.graphql_java_generator.client.domain.allGraphQLCases;

import com.graphql_java_generator.client.response.AbstractCustomScalarDeserializer;
import com.graphql_java_generator.customscalars.GraphQLScalarTypeElse;

import graphql.schema.GraphQLScalarType;

/**
 * This class is a standard Deserializer for Jackson. It uses the {@link GraphQLScalarType} that is implemented by the
 * project for this scalar
 */
public class CustomScalarDeserializerelse extends AbstractCustomScalarDeserializer<java.lang.String> {

	private static final long serialVersionUID = 1L;

	protected CustomScalarDeserializerelse() {
		super(java.lang.String.class, GraphQLScalarTypeElse.getElseScalar());
	}
}
