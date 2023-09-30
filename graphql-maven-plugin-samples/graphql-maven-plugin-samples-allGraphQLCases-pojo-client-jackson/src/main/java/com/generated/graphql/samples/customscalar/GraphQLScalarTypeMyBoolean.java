/**
 * 
 */
package com.generated.graphql.samples.customscalar;

import graphql.language.BooleanValue;
import graphql.language.StringValue;
import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;
import graphql.schema.GraphQLScalarType;

/**
 * A scalar to test the correction of the issues
 * <a href="https://github.com/graphql-java-generator/graphql-maven-plugin-project/issues/184">184</a> and
 * <a href="https://github.com/graphql-java-generator/graphql-maven-plugin-project/issues/198">198</a>, it would
 * generate an error.
 * 
 * @author etienne-sf
 */
public class GraphQLScalarTypeMyBoolean {

	/**
	 * Useless String scalar.<BR/>
	 * It's used both as a sample, to be completed by a developper, according to his/her needs, and for the use in some
	 * tests of the plugin logic (like in the Shopify sample, to handle (badly) various scalar as based on
	 * strings).<BR/>
	 * It's actually a bad management, as this custom scalars does nothing, but read basic strings. It's just for test.
	 */
	public static GraphQLScalarType MyBooleanScalarType = GraphQLScalarType.newScalar().name("MyBoolean") //$NON-NLS-1$
			.description("Useless Custom Scalar for MyBoolean management").coercing( //$NON-NLS-1$

					new Coercing<Boolean, Boolean>() {

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
						public Boolean serialize(Object input) throws CoercingSerializeException {
							if (!(input instanceof Boolean)) {
								throw new CoercingSerializeException(
										"Can't parse the '" + input.toString() + "' to a String"); //$NON-NLS-1$ //$NON-NLS-2$
							} else {
								return (Boolean) input;
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
						public Boolean parseValue(Object o) throws CoercingParseValueException {
							if (!(o instanceof String)) {
								throw new CoercingParseValueException(
										"Can't parse the '" + o.toString() + "' string to a Boolean"); //$NON-NLS-1$ //$NON-NLS-2$
							} else {
								return Boolean.valueOf(Boolean.parseBoolean((String) o));
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
						public Boolean parseLiteral(Object o) throws CoercingParseLiteralException {
							// o is an AST, that is: an instance of a class that implements graphql.language.Value
							if (o instanceof StringValue) {
								return Boolean.valueOf(((StringValue) o).getValue());
							} else if (o instanceof BooleanValue) {
								return Boolean.valueOf(((BooleanValue) o).isValue());
							} else {
								throw new CoercingParseValueException(
										"Can't parse the '" + o.toString() + "' to a Boolean"); //$NON-NLS-1$ //$NON-NLS-2$
							}
						}
					})
			.build();
}
