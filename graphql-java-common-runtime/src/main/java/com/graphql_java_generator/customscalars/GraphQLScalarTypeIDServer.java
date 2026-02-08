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
 * ID are managed as UUID, on server side. This class takes care of ID attributes by serializing to and deserializing
 * from UUID. It used internally in the generated code, in the various method that need this serialization or
 * deserialization.
 * 
 * @author etienne-sf
 */
public class GraphQLScalarTypeIDServer {

	public GraphQLScalarTypeIDServer() {
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
					// UUID is the type while in the java code, when in the server
					new Coercing<java.util.UUID, String>() {

						@Override
						public String serialize(Object input, @NonNull GraphQLContext graphQLContext,
								@NonNull Locale locale) throws CoercingSerializeException {
							if (!(input instanceof java.util.UUID)) {
								throw new CoercingSerializeException("Can't parse the '" + input.toString()
										+ "' UUID to a String (it should be a UUID but is a "
										+ input.getClass().getName() + ")");
							} else {
								return ((java.util.UUID) input).toString();
							}
						}

						@Override
						public java.util.UUID parseValue(Object o, @NonNull GraphQLContext graphQLContext,
								@NonNull Locale locale) throws CoercingParseValueException {
							if (!(o instanceof String)) {
								throw new CoercingParseValueException("Can't parse the '" + o.toString()
										+ "' string to a UUID (it should be a String but is a " + o.getClass().getName()
										+ ")");
							}
							return java.util.UUID.fromString((String) o);
						}

						@Override
						public java.util.UUID parseLiteral(@NonNull Value<?> input, @NonNull CoercedVariables variables,
								@NonNull GraphQLContext graphQLContext, @NonNull Locale locale)
								throws CoercingParseLiteralException {
							// o is an AST, that is: an instance of a class that implements graphql.language.Value
							if (!(input instanceof StringValue)) {
								throw new CoercingParseValueException("Can't parse the '" + input.toString()
										+ "' string value to a UUID (it should be a StringValue but is a "
										+ input.getClass().getName() + ")");
							}
							return java.util.UUID.fromString(input.toString());
						}
					})
			.build();

}
