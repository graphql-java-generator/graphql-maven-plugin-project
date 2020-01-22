package com.graphql_java_generator.client.domain.forum;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.graphql_java_generator.AbstractCustomScalarConverter;
import com.graphql_java_generator.CustomScalarConverter;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;

@Component
public class CustomScalarConverterDate extends AbstractCustomScalarConverter<Date> {

	private static final long serialVersionUID = 1L;

	/**
	 * The date pattern, used when exchanging date with this {@link CustomScalarConverter} from and to the GrahQL Server
	 */
	final static String DATE_PATTERN = "yyyy-MM-dd";
	SimpleDateFormat formater = new SimpleDateFormat(DATE_PATTERN);

	public CustomScalarConverterDate() {
		super(Date.class, "Date", true);
	}

	@Override
	public Date convertFromString(String str) throws GraphQLRequestExecutionException {
		try {
			return formater.parse(str);
		} catch (ParseException e) {
			throw new GraphQLRequestExecutionException("Can't parse the '" + str + "' string to a Date", e);
		}
	}

	@Override
	public String convertToString(Object o) throws GraphQLRequestExecutionException {
		if (!(o instanceof Date)) {
			throw new GraphQLRequestExecutionException("Can't parse the '" + o.toString() + "' string to a Date");
		} else {
			return formater.format((Date) o);
		}
	}

	@Override
	public void serialize(Date value, JsonGenerator gen, SerializerProvider provider) throws IOException {
		// TODO Auto-generated method stub

	}

}
