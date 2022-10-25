/**
 * 
 */
package org.allGraphQLCases.prefixsuffix.impl;

import graphql.language.StringValue;
import graphql.schema.*;

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

	/** Custom Scalar for String management. */
	public static GraphQLScalarType getElseScalar() {
		return GraphQLScalarType.newScalar().name("else").description("Custom Scalar for String management. ")
				.coercing(new Coercing<String, String>() {

					/**
					 * Called to convert a Java object result of a DataFetcher to a valid runtime value for the scalar
					 * type. <br/>
					 * Note : Throw {@link CoercingSerializeException} if there is fundamental problem
					 * during serialization, don't return null to indicate failure. <br/>
					 * Note : You should not allow {@link RuntimeException}s to come out of your serialize
					 * method, but rather catch them and fire them as {@link CoercingSerializeException}
					 * instead as per the method contract.
					 *
					 * @param dataFetcherResult
					 *            is never null
					 *
					 * @return a serialized value which may be null.
					 *
					 * @throws CoercingSerializeException
					 *             if value input can't be serialized
					 */
					@Override
					public String serialize(Object input) throws CoercingSerializeException {
						if (!(input instanceof String)) {
							throw new CoercingSerializeException(
									"Can't parse the '" + input.toString() + "' string to a String");
						} else {
							return (String) input;
						}
					}

					/**
					 * Called to resolve an input from a query variable into a Java object acceptable for the scalar
					 * type. <br/>
					 * Note : You should not allow {@link RuntimeException}s to come out of your parseValue
					 * method, but rather catch them and fire them as {@link CoercingParseValueException}
					 * instead as per the method contract.
					 *
					 * @param input
					 *            is never null
					 *
					 * @return a parsed value which is never null
					 *
					 * @throws CoercingParseValueException
					 *             if value input can't be parsed
					 */
					@Override
					public String parseValue(Object o) throws CoercingParseValueException {
						if (!(o instanceof String)) {
							throw new CoercingParseValueException(
									"Can't parse the '" + o.toString() + "' string to a String");
						} else {
							return (String) o;
						}
					}

					/**
					 * Called during query validation to convert a query input AST node into a Java object acceptable
					 * for the scalar type. The input object will be an instance of {@link graphql.language.Value}.
					 * <br/>
					 * Note : You should not allow {@link RuntimeException}s to come out of your parseLiteral
					 * method, but rather catch them and fire them as
					 * {@link CoercingParseLiteralException} instead as per the method contract.
					 *
					 * @param input
					 *            is never null
					 *
					 * @return a parsed value which is never null
					 *
					 * @throws CoercingParseLiteralException
					 *             if input literal can't be parsed
					 */
					@Override
					public String parseLiteral(Object o) throws CoercingParseLiteralException {
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
