package com.graphql_java_generator.customscalars;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
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
 * A proposed DateTime scalar, that stores dates in String, formatted as: 2019-07-03T20:47:55Z<BR/>
 * This Scalar is proposed to be used, for integration testing (checks that the plugin correctly manages Custom Scalars,
 * see samples) and with more documentation to help people to create their own Custom Scalar implementations.
 * 
 * @author etienne-sf
 */
public class GraphQLScalarTypeDateTime {

	public GraphQLScalarTypeDateTime() {
		// No action
	}

	/**
	 * Custom Scalar for DateTime management. It serializes datetimes in String, formatted as: yyyy-MM-dd'T'HH:mm:ss'Z'
	 */
	public static GraphQLScalarType DateTime = GraphQLScalarType.newScalar().name("DateTime").description(
			"Custom Scalar for DateTime management. It serializes datetimes in String, formatted as: yyyy-MM-dd'T'HH:mm:ss'Z'")
			.coercing(
					//
					// Note: String is the way the data is stored in GraphQL queries
					// Date is the type while in the java code, either in the client and in the server
					new Coercing<Date, String>() {

						/**
						 * The date pattern, used when exchanging date with this {@link GraphQLScalarType} from and to
						 * the GrahQL Server
						 */
						final static String DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'";
						SimpleDateFormat formater = new SimpleDateFormat(DATE_PATTERN, Locale.ENGLISH);
						{
							formater.setCalendar(new GregorianCalendar(Locale.ENGLISH));
						}

						@Override
						public String serialize(Object input, @NonNull GraphQLContext graphQLContext,
								@NonNull Locale locale) throws CoercingSerializeException {
							if (!(input instanceof Date)) {
								throw new CoercingSerializeException(
										"Can't parse the '" + input.toString() + "' string to a DateTime");
							} else {
								synchronized (formater) {
									return formater.format((Date) input);
								}
							}
						}

						@Override
						public Date parseValue(Object o, @NonNull GraphQLContext graphQLContext, @NonNull Locale locale)
								throws CoercingParseValueException {
							if (!(o instanceof String)) {
								throw new CoercingParseValueException(
										"Can't parse the '" + o.toString() + "' string to a DateTime");
							} else {
								try {
									synchronized (formater) {
										return formater.parse((String) o);
									}
								} catch (ParseException e) {
									throw new CoercingParseValueException(e.getMessage(), e);
								}
							}
						}

						@Override
						public Date parseLiteral(@NonNull Value<?> input, @NonNull CoercedVariables variables,
								@NonNull GraphQLContext graphQLContext, @NonNull Locale locale)
								throws CoercingParseLiteralException {
							// o is an AST, that is: an instance of a class that implements graphql.language.Value
							if (!(input instanceof StringValue)) {
								throw new CoercingParseValueException(
										"Can't parse the '" + input.toString() + "' string to a DateTime");
							} else {
								try {
									synchronized (formater) {
										return formater.parse(((StringValue) input).getValue());
									}
								} catch (ParseException e) {
									throw new CoercingParseValueException(e.getMessage(), e);
								}
							}
						}
					})
			.build();

}
