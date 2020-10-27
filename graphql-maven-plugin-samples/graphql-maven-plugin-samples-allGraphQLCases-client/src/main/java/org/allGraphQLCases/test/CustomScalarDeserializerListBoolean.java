package org.allGraphQLCases.test;

import java.util.List;

/**
 * Jackson custom deserializer must have no-args cosntructors. So we need to have one concrete class for each kind
 * (=java type) of field to deserialize
 * 
 * @author etienne-sf
 */
public class CustomScalarDeserializerListBoolean extends AbstractCustomScalarDeserializer<List<Boolean>> {

	private static final long serialVersionUID = 1L;

	protected CustomScalarDeserializerListBoolean() {
		super(null, true, Boolean.class, null);
	}

}
