package com.graphql_java_generator.customscalars;

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
 * ID are managed as String, on client side. This class takes care of ID attributes by doing ... nothing! As value as
 * serialized and deserialized as String, to hide what it may mean.
 * 
 * @author etienne-sf
 */
public class GraphQLScalarTypeIDClient {

	public GraphQLScalarTypeIDClient() {
		// No action
	}

	/**
	 * UUID are managed as String, on client side. This class takes care of UUID attributes by doing ... nothing! As
	 * value as serialized and deserialized as String, to hide what it may mean.
	 */
	public static GraphQLScalarType ID = GraphQLScalarType.newScalar().name("ID")
			.description("ID custom scalar, for client side").coercing(
					//
					// Note: String is the way the data is stored in GraphQL queries
					// Date is the type while in the java code, either in the client and in the server
					new Coercing<String, String>() {

						@Override
						public String serialize(Object input, @NonNull GraphQLContext graphQLContext,
								@NonNull Locale locale) throws CoercingSerializeException {
							if (!(input instanceof String)) {
								throw new CoercingSerializeException(
										"Can't parse the '" + input.toString() + "' ID to a String");
							} else {
								return (String) input;
							}
						}

						@Override
						public String parseValue(Object o, @NonNull GraphQLContext graphQLContext,
								@NonNull Locale locale) throws CoercingParseValueException {
							if (!(o instanceof String)) {
								throw new CoercingParseValueException(
										"Can't parse the '" + o.toString() + "' string to an ID");
							} else {
								return (String) o;
							}
						}

						@Override
						public String parseLiteral(@NonNull Value<?> input, @NonNull CoercedVariables variables,
								@NonNull GraphQLContext graphQLContext, @NonNull Locale locale)
								throws CoercingParseLiteralException {
							// o is an AST, that is: an instance of a class that implements graphql.language.Value
							if (!(input instanceof StringValue)) {
								throw new CoercingParseValueException(
										"Can't parse the '" + input.toString() + "' string to an ID");
							} else {
								return ((StringValue) input).getValue();
							}
						}
					})
			.build();
}
