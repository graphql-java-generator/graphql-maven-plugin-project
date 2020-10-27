package com.graphql_java_generator.client.response;

import java.util.List;

/**
 * Jackson custom deserializer must have no-args cosntructors. So we need to have one concrete class for each kind
 * (=java type) of field to deserialize
 * 
 * @author etienne-sf
 */
public class CustomJacksonDeserializerListString extends AbstractCustomJacksonDeserializer<List<String>> {

	private static final long serialVersionUID = 1L;

	protected CustomJacksonDeserializerListString() {
		super(null, true, String.class, null);
	}

}
