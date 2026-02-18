package com.graphql_java_generator.customscalars;

import java.util.Base64;
import java.util.Locale;

import org.jspecify.annotations.NonNull;

import graphql.GraphQLContext;
import graphql.execution.CoercedVariables;
import graphql.language.StringValue;
import graphql.language.Value;
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
						public String serialize(@NonNull Object input, @NonNull GraphQLContext graphQLContext,
								@NonNull Locale locale) throws CoercingSerializeException {
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
						public byte[] parseValue(@NonNull Object o, @NonNull GraphQLContext graphQLContext,
								@NonNull Locale locale) throws CoercingParseValueException {
							if (o instanceof String) {
								try {
									return Base64.getDecoder().decode((String) o);
								} catch (IllegalArgumentException e) {
									throw new CoercingParseValueException(
											"Input string \"" + o + "\" is not a valid Base64 value", e);
								}
							} else if (o instanceof byte[]) {
								return (byte[]) o;
							} else {
								throw new CoercingParseValueException(
										"The input object should be either a String or a byte array, but is a "
												+ o.getClass().getName());
							}
						}

						@Override
						public byte[] parseLiteral(@NonNull Value<?> o, @NonNull CoercedVariables variables,
								@NonNull GraphQLContext graphQLContext, @NonNull Locale locale)
								throws CoercingParseLiteralException {
							if (o instanceof StringValue) {
								try {
									return Base64.getDecoder().decode(((StringValue) o).getValue());
								} catch (IllegalArgumentException e) {
									throw new CoercingParseLiteralException(
											"Input string \"" + o + "\" is not a valid Base64 value", e);
								}
							} else {
								throw new CoercingParseValueException(
										"Can't parse the '" + o.toString() + "' string to an Base64 string");
							}
						}
					})
			.build();
}
