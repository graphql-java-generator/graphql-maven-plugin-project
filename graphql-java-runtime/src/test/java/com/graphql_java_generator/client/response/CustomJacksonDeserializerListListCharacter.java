package com.graphql_java_generator.client.response;

import java.util.List;

public class CustomJacksonDeserializerListListCharacter extends
		AbstractCustomJacksonDeserializer<List<com.graphql_java_generator.client.domain.allGraphQLCases.Character>> {

	private static final long serialVersionUID = 1L;

	protected CustomJacksonDeserializerListListCharacter() {
		super(new CustomJacksonDeserializerListCharacter(), true,
				com.graphql_java_generator.client.domain.allGraphQLCases.Character.class, null);
	}

}
