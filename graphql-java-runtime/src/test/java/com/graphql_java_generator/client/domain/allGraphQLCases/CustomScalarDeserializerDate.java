package com.graphql_java_generator.client.domain.allGraphQLCases;

import com.graphql_java_generator.client.response.AbstractCustomScalarDeserializer;

import graphql.schema.GraphQLScalarType;

/**
 * This class is a standard Deserializer for Jackson. It uses the {@link GraphQLScalarType} that is implemented by the
 * project for this scalar
 */
public class CustomScalarDeserializerDate extends AbstractCustomScalarDeserializer<java.util.Date> {

	private static final long serialVersionUID = 1L;

	protected CustomScalarDeserializerDate() {
		super(java.util.Date.class, com.graphql_java_generator.customscalars.GraphQLScalarTypeDate.Date);
	}
}
