/**
 * 
 */
package com.graphql_java_generator.client.response;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import graphql.language.FloatValue;
import graphql.language.IntValue;
import graphql.language.StringValue;
import graphql.language.Value;
import graphql.schema.GraphQLScalarType;

/**
 * Jackson Deserializer for Custom Scalar.
 * 
 * @author EtienneSF
 */
public class AbstractCustomScalarDeserializer<T> extends StdDeserializer<T> {

	private static final long serialVersionUID = 1L;

	/**
	 * The {@link GraphQLScalarType} instance that manages this Custom Scalar. It's used to deserialize the value read
	 * on the Jackson response from the server
	 */
	final GraphQLScalarType graphQLScalarType;

	protected AbstractCustomScalarDeserializer(Class<?> handledType, GraphQLScalarType graphQLScalarType) {
		super(handledType);
		this.graphQLScalarType = graphQLScalarType;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		Value<?> value;
		switch (p.currentToken()) {
		case VALUE_NUMBER_FLOAT:
			value = new FloatValue(p.getDecimalValue());
			break;
		case VALUE_NUMBER_INT:
			value = new IntValue(p.getBigIntegerValue());
			break;
		case VALUE_STRING:
			value = new StringValue(p.getText());
			break;
		default:
			throw new JsonParseException(p, "Non managed JSON token: " + p.currentToken());
		}

		return (T) graphQLScalarType.getCoercing().parseLiteral(value);
	}

}
