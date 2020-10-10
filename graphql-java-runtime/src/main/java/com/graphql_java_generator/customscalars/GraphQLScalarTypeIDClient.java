/**
 * 
 */
package com.graphql_java_generator.customscalars;

import graphql.language.StringValue;
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

						/**
						 * Called to convert a Java object result of a DataFetcher to a valid runtime value for the
						 * scalar type. <br/>
						 * Note : Throw {@link graphql.schema.CoercingSerializeException} if there is fundamental
						 * problem during serialisation, don't return null to indicate failure. <br/>
						 * Note : You should not allow {@link java.lang.RuntimeException}s to come out of your serialize
						 * method, but rather catch them and fire them as
						 * {@link graphql.schema.CoercingSerializeException} instead as per the method contract.
						 *
						 * @param dataFetcherResult
						 *            is never null
						 *
						 * @return a serialized value which may be null.
						 *
						 * @throws graphql.schema.CoercingSerializeException
						 *             if value input can't be serialized
						 */
						@Override
						public String serialize(Object input) throws CoercingSerializeException {
							if (!(input instanceof String)) {
								throw new CoercingSerializeException(
										"Can't parse the '" + input.toString() + "' ID to a String");
							} else {
								return (String) input;
							}
						}

						/**
						 * Called to resolve an input from a query variable into a Java object acceptable for the scalar
						 * type. <br/>
						 * Note : You should not allow {@link java.lang.RuntimeException}s to come out of your
						 * parseValue method, but rather catch them and fire them as
						 * {@link graphql.schema.CoercingParseValueException} instead as per the method contract.
						 *
						 * @param input
						 *            is never null
						 *
						 * @return a parsed value which is never null
						 *
						 * @throws graphql.schema.CoercingParseValueException
						 *             if value input can't be parsed
						 */
						@Override
						public String parseValue(Object o) throws CoercingParseValueException {
							if (!(o instanceof String)) {
								throw new CoercingParseValueException(
										"Can't parse the '" + o.toString() + "' string to an ID");
							} else {
								return (String) o;
							}
						}

						/**
						 * Called during query validation to convert a query input AST node into a Java object
						 * acceptable for the scalar type. The input object will be an instance of
						 * {@link graphql.language.Value}. <br/>
						 * Note : You should not allow {@link java.lang.RuntimeException}s to come out of your
						 * parseLiteral method, but rather catch them and fire them as
						 * {@link graphql.schema.CoercingParseLiteralException} instead as per the method contract.
						 *
						 * @param input
						 *            is never null
						 *
						 * @return a parsed value which is never null
						 *
						 * @throws graphql.schema.CoercingParseLiteralException
						 *             if input literal can't be parsed
						 */
						@Override
						public String parseLiteral(Object o) throws CoercingParseLiteralException {
							// o is an AST, that is: an instance of a class that implements graphql.language.Value
							if (!(o instanceof StringValue)) {
								throw new CoercingParseValueException(
										"Can't parse the '" + o.toString() + "' string to an ID");
							} else {
								return ((StringValue) o).getValue();
							}
						}
					})
			.build();
}
