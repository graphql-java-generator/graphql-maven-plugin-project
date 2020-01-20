package com.graphql_java_generator.client.domain.forum;

import java.io.IOException;
import java.util.Date;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.graphql_java_generator.AbstractCustomScalarConverter;

public class CustomScalarConverterDate extends AbstractCustomScalarConverter<Date> {

	public CustomScalarConverterDate() {
		super(Date.class, "Date", true);
	}

	@Override
	public Date convertFromString(String str) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String convertToString(Object o) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void serialize(Date value, JsonGenerator gen, SerializerProvider provider) throws IOException {
		// TODO Auto-generated method stub

	}

}
