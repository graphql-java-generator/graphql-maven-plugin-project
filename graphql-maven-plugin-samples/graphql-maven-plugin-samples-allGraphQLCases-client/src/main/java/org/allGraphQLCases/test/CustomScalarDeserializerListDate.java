package org.allGraphQLCases.test;

import java.util.Date;
import java.util.List;

import org.allGraphQLCases.client.util.CustomScalarDeserializerDate;

/**
 * Jackson custom deserializer must have no-args cosntructors. So we need to have one concrete class for each kind
 * (=java type) of field to deserialize
 * 
 * @author etienne-sf
 */
public class CustomScalarDeserializerListDate extends AbstractCustomScalarDeserializer<List<Date>> {

	private static final long serialVersionUID = 1L;

	protected CustomScalarDeserializerListDate() {
		super(new CustomScalarDeserializerDate(), true, Date.class, null);
	}

}
