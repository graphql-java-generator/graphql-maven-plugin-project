package com.graphql_java_generator.client.domain.forum;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.graphql_java_generator.customscalars.CustomScalarRegistryImpl;
import com.graphql_java_generator.client.response.AbstractCustomScalarDeserializer;
import com.graphql_java_generator.customscalars.GraphQLScalarTypeDate;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;

import graphql.schema.GraphQLScalarType;

import java.util.Date;

/**
 * This class is a standard Deserializer for Jackson. It uses the {@link GraphQLScalarType} that is implemented by the project for this scalar
 */
public class CustomScalarDeserializerDate  extends AbstractCustomScalarDeserializer<java.util.Date> {

	private static final long serialVersionUID = 1L;

	protected CustomScalarDeserializerDate() {
		super(java.util.Date.class,
				com.graphql_java_generator.customscalars.GraphQLScalarTypeDate.Date
				);
	}
}
