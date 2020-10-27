package org.allGraphQLCases.test;

import java.util.List;

public class CustomScalarDeserializerListCharacter
		extends AbstractCustomScalarDeserializer<List<org.allGraphQLCases.client.Character>> {

	private static final long serialVersionUID = 1L;

	protected CustomScalarDeserializerListCharacter() {
		super(null, true, org.allGraphQLCases.client.Character.class, null);
	}

}
