package org.allGraphQLCases.test;

import java.util.List;

/**
 * Jackson custom deserializer must have no-args cosntructors. So we need to have one concrete class for each kind
 * (=java type) of field to deserialize
 * 
 * @author etienne-sf
 */
public class CustomScalarDeserializerListString extends AbstractCustomScalarDeserializer<List<String>> {

	private static final long serialVersionUID = 1L;

	protected CustomScalarDeserializerListString() {
		super(null, true, String.class, null);
	}

}
