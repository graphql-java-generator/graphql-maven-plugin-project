package com.graphql_java_generator.customscalars;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
 * A proposed Date scalar, that stores dates in String, formatted as: YYYY-MM-DD<BR/>
 * This Scalar is proposed to be used, for integration testing (checks that the plugin correctly manages Custom Scalars,
 * see samples) and with more documentation to help people to create their own Custom Scalar implementations.
 * 
 * @author etienne-sf
 */
public class GraphQLScalarTypeDate {

	public GraphQLScalarTypeDate() {
		// No action
	}

	/** Logger for this class */
	private static Logger logger = LoggerFactory.getLogger(GraphQLScalarTypeDate.class);

	/** Custom Scalar for Date management. It serializes dates in String, formatted as: YYYY-MM-DD */
	public static GraphQLScalarType Date = GraphQLScalarType.newScalar().name("Date")
			.description("Custom Scalar for Date management. It serializes dates in String, formatted as: YYYY-MM-DD.")
			.coercing( //
						// Note: String is the way the data is stored in GraphQL queries
						// Date is the type while in the java code, either in the client and in the server
					new Coercing<Date, String>() {

						/**
						 * The date pattern, used when exchanging date with this {@link GraphQLScalarType} from and to
						 * the GrahQL Server
						 */
						final static String DATE_PATTERN = "yyyy-MM-dd";
						final SimpleDateFormat formater = new SimpleDateFormat(DATE_PATTERN, Locale.ENGLISH);
						{
							formater.setCalendar(new GregorianCalendar(Locale.ENGLISH));
						}

						@Override
						public String serialize(Object input, @NonNull GraphQLContext graphQLContext,
								@NonNull Locale locale) throws CoercingSerializeException {
							if (input instanceof Date) {
								synchronized (formater) {
									return formater.format((Date) input);
								}
							} else {
								throw new CoercingSerializeException("Can't parse the '" + input.toString()
										+ "' Date to a String (it should be a " + Date.class.getName() + " but is a "
										+ input.getClass().getName() + ")");
							}
						}

						@Override
						public Date parseValue(Object o, @NonNull GraphQLContext graphQLContext, @NonNull Locale locale)
								throws CoercingSerializeException {
							if (!(o instanceof String)) {
								throw new CoercingParseValueException("Can't parse the '" + o.toString()
										+ "' string to a Date (it should be a String but is a " + o.getClass().getName()
										+ ")");
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
							String val = null;
							// o is an AST, that is: an instance of a class that implements graphql.language.Value
							if (!(input instanceof StringValue)) {
								throw new CoercingParseValueException("Can't parse the '" + input.toString()
										+ "' string value to a Date (it should be a StringValue but is a "
										+ input.getClass().getName() + ")");
							} else {
								try {
									val = ((StringValue) input).getValue();
									logger.trace("Parsing date from this literal: '{}'", val);
									synchronized (formater) {
										return formater.parse(val);
									}
								} catch (ParseException e) {
									throw new CoercingParseValueException(
											e.getMessage() + " when trying to parse '" + val + "'", e);
								}
							}
						}
					})
			.build();

}
