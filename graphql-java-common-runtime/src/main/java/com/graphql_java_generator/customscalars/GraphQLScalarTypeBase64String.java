/**
 * 
 */
package com.graphql_java_generator.customscalars;

import java.util.Base64;

import graphql.language.StringValue;
import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;
import graphql.schema.GraphQLScalarType;

/**
 * @author etienne-sf
 */
public class GraphQLScalarTypeBase64String {

	public static final GraphQLScalarType GraphQLBase64String = GraphQLScalarType.newScalar()//
			.name("Base64String")//
			.description("Base64-encoded binary")//
			.coercing(//
						// Note: String is the way the data is stored in GraphQL queries
						// byte[] is the type while in the java code, either in the client and in the server
					new Coercing<byte[], String>() {
						@Override
						public String serialize(Object input) {
							if (input instanceof byte[]) {
								return Base64.getEncoder().encodeToString((byte[]) input);
							} else if (input instanceof String) {
								return (String) input;
							} else {
								throw new CoercingSerializeException("Can't parse the '" + input.toString()
										+ "' Date to a String (it should be a " + byte.class.getName() + "[] but is a "
										+ input.getClass().getName() + ")");
							}
						}

						@Override
						public byte[] parseValue(Object input) {
							if (input instanceof String) {
								try {
									return Base64.getDecoder().decode((String) input);
								} catch (IllegalArgumentException e) {
									throw new CoercingParseValueException(
											"Input string \"" + input + "\" is not a valid Base64 value", e);
								}
							} else if (input instanceof byte[]) {
								return (byte[]) input;
							} else {
								throw new CoercingParseValueException(
										"The input object should be either a String or a byte array, but is a "
												+ input.getClass().getName());
							}
						}

						@Override
						public byte[] parseLiteral(Object input) {
							if (input instanceof StringValue) {
								try {
									return Base64.getDecoder().decode(((StringValue) input).getValue());
								} catch (IllegalArgumentException e) {
									throw new CoercingParseLiteralException(
											"Input string \"" + input + "\" is not a valid Base64 value", e);
								}
							} else {
								throw new CoercingParseValueException(
										"Can't parse the '" + input.toString() + "' string to an Base64 string");
							}
						}
					})
			.build();
}
