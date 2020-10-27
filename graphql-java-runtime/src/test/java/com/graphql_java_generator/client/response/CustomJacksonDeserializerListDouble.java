package com.graphql_java_generator.client.response;

import java.util.List;

/**
 * Jackson custom deserializer must have no-args cosntructors. So we need to have one concrete class for each kind
 * (=java type) of field to deserialize
 * 
 * @author etienne-sf
 */
public class CustomJacksonDeserializerListDouble extends AbstractCustomJacksonDeserializer<List<List<Double>>> {

	private static final long serialVersionUID = 1L;

	protected CustomJacksonDeserializerListDouble() {
		super(null, true, Double.class, null);
	}

}
