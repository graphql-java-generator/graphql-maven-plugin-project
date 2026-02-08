package com.graphql_java_generator.customscalars;

import java.util.Locale;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

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
 * Useless else scalar. This scalar is just a test that the plugin can manage a Scalar, whis name is a java
 * keyword.<BR/>
 * It's used both as a sample, to be completed by a developper, according to his/her needs, and for the use in some
 * tests of the plugin logic (like in the Shopify sample, to handle (badly) various scalar as based on strings).<BR/>
 * It's actually a bad management, as this custom scalars does nothing, but read basic strings. It's just for test.
 * 
 * @author etienne-sf
 */
public class GraphQLScalarTypeElse {

	public GraphQLScalarTypeElse() {
		// No action
	}

	/** Custom Scalar for String management. */
	public static GraphQLScalarType getElseScalar() {
		return GraphQLScalarType.newScalar().name("else").description("Custom Scalar for String management. ")
				.coercing(new Coercing<String, String>() {

					@Override
					public @Nullable String serialize(@NonNull Object input, @NonNull GraphQLContext graphQLContext,
							@NonNull Locale locale) throws CoercingSerializeException {
						if (!(input instanceof String)) {
							throw new CoercingSerializeException(
									"Can't parse the '" + input.toString() + "' string to a String");
						} else {
							return input.toString();
						}
					}

					@Override
					public String parseValue(@NonNull Object o, @NonNull GraphQLContext graphQLContext,
							@NonNull Locale locale) throws CoercingParseValueException {
						if (!(o instanceof String)) {
							throw new CoercingParseValueException(
									"Can't parse the '" + o.toString() + "' string to a String");
						} else {
							return (String) o;
						}
					}

					@Override
					public String parseLiteral(@NonNull Value<?> o, @NonNull CoercedVariables variables,
							@NonNull GraphQLContext graphQLContext, @NonNull Locale locale)
							throws CoercingParseLiteralException {
						// o is an AST, that is: an instance of a class that implements graphql.language.Value
						if (!(o instanceof StringValue)) {
							throw new CoercingParseValueException(
									"Can't parse the '" + o.toString() + "' string to a String");
						} else {
							return ((StringValue) o).getValue();
						}
					}
				}).build();
	}
}
