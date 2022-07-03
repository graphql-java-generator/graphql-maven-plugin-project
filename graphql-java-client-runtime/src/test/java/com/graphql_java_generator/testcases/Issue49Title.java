package com.graphql_java_generator.testcases;

public enum Issue49Title {
	DR("DR"), MR("MR"), MS("MS"), MRS("MRS");

	// The graphQlValue is needed on server side, to map the enum value to the value defined in the GraphQL schema. They
	// are different
	// when the value in the GraphQL schema is a java reserved keyword.
	private final String graphQlValue;

	public String graphQlValue() {
		return graphQlValue;
	}

	static public Issue49Title fromGraphQlValue(String graphQlValue) {
		for (Issue49Title e : Issue49Title.values()) {
			if (e.graphQlValue().equals(graphQlValue)) {
				return e;
			}
		}
		throw new IllegalArgumentException("No Episode exists with '" + graphQlValue + "' as a GraphQL value");
	}

	Issue49Title(String graphQlValue) {
		this.graphQlValue = graphQlValue;
	}
}
