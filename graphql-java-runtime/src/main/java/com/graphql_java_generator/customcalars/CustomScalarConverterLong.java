package com.graphql_java_generator.customcalars;

import java.text.SimpleDateFormat;

import org.springframework.stereotype.Component;

import com.graphql_java_generator.AbstractCustomScalarConverter;
import com.graphql_java_generator.CustomScalarConverter;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;

@Deprecated
@Component
public class CustomScalarConverterLong extends AbstractCustomScalarConverter<Long> {

	/**
	 * The date pattern, used when exchanging date with this {@link CustomScalarConverter} from and to the GrahQL Server
	 */
	final static String DATE_PATTERN = "yyyy-MM-dd";
	SimpleDateFormat formater = new SimpleDateFormat(DATE_PATTERN);

	public CustomScalarConverterLong() {
		super("Long", true);
	}

	@Override
	public Long convertFromString(String str) throws GraphQLRequestExecutionException {
		try {
			return Long.parseLong(str);
		} catch (NumberFormatException e) {
			throw new GraphQLRequestExecutionException("Can't parse the '" + str + "' string to a Long", e);
		}
	}

	@Override
	public String convertToString(Object o) throws GraphQLRequestExecutionException {
		if (o == null) {
			return null;
		} else if (!(o instanceof Long)) {
			throw new GraphQLRequestExecutionException(
					"Expected an instance of Long, but was a " + o.getClass().getName() + "'");
		} else {
			return ((Long) o).toString();
		}
	}

}
