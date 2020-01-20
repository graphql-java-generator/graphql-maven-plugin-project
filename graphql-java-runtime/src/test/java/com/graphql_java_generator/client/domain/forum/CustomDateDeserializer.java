package com.graphql_java_generator.client.domain.forum;

import java.io.IOException;
import java.util.Date;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.graphql_java_generator.CustomScalarRegistryImpl;

public class CustomDateDeserializer extends StdDeserializer<Date> {

	private static final long serialVersionUID = 1L;
	CustomScalarConverterDate customScalarConverterDate = null;

	protected CustomDateDeserializer() {
		super(Date.class);
	}

	@Override
	public Date deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		return getCustomScalarConverterDate().convertFromString(p.getText());
	}

	private CustomScalarConverterDate getCustomScalarConverterDate() {
		if (customScalarConverterDate == null) {
			customScalarConverterDate = (CustomScalarConverterDate) CustomScalarRegistryImpl.customScalarRegistry
					.getCustomScalarConverter("Date");
		}
		return customScalarConverterDate;
	}

}
