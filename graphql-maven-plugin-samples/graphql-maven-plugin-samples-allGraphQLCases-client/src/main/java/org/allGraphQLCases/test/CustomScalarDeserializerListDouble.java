package org.allGraphQLCases.test;

import java.util.List;

/**
 * Jackson custom deserializer must have no-args cosntructors. So we need to have one concrete class for each kind
 * (=java type) of field to deserialize
 * 
 * @author etienne-sf
 */
public class CustomScalarDeserializerListDouble extends AbstractCustomScalarDeserializer<List<List<Double>>> {

	private static final long serialVersionUID = 1L;

	protected CustomScalarDeserializerListDouble() {
		super(new CustomScalarDeserializerDouble(), Double.class, graphql.Scalars.GraphQLFloat);
	}

}
