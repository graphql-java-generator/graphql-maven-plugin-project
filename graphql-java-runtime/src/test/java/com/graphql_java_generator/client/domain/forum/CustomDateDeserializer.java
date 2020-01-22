package com.graphql_java_generator.client.domain.forum;

import java.io.IOException;
import java.util.Date;

import org.springframework.boot.json.JsonParseException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.graphql_java_generator.CustomScalarRegistryImpl;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;

public class CustomDateDeserializer extends StdDeserializer<Date> {

	private static final long serialVersionUID = 1L;
	CustomScalarConverterDate customScalarConverterDate = null;

	protected CustomDateDeserializer() {
		super(Date.class);
	}

	@Override
	public Date deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		try {
			return getCustomScalarConverterDate(p).convertFromString(p.getText());
		} catch (GraphQLRequestExecutionException e) {
			throw new JsonParseException(e);
		}
	}

	private CustomScalarConverterDate getCustomScalarConverterDate(JsonParser p)
			throws com.fasterxml.jackson.core.JsonParseException {
		if (customScalarConverterDate == null) {
			customScalarConverterDate = (CustomScalarConverterDate) CustomScalarRegistryImpl.customScalarRegistry
					.getCustomScalarConverter("Date");
			if (customScalarConverterDate == null) {
				throw new com.fasterxml.jackson.core.JsonParseException(p,
						"No converter has been registered for the type 'Date'");
			}
		}
		return customScalarConverterDate;
	}

}
