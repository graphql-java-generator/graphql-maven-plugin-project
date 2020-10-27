package org.allGraphQLCases.test;

import java.util.List;

public class CustomScalarDeserializerListHuman
		extends AbstractCustomScalarDeserializer<List<org.allGraphQLCases.client.Human>> {

	private static final long serialVersionUID = 1L;

	protected CustomScalarDeserializerListHuman() {
		super(null, true, org.allGraphQLCases.client.Human.class, null);
	}

}
