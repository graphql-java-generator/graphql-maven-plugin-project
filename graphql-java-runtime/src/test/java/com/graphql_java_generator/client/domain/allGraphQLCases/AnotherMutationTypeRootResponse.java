package com.graphql_java_generator.client.domain.allGraphQLCases;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.graphql_java_generator.annotation.GraphQLNonScalar;
import com.graphql_java_generator.client.response.Error;

public class AnotherMutationTypeRootResponse {

	@JsonProperty("mutation")
	@GraphQLNonScalar(fieldName = "AnotherMutationType", graphQLTypeName = "AnotherMutationType", javaClass = AnotherMutationTypeResponse.class)
	AnotherMutationTypeResponse mutation;

	@JsonProperty("errors")
	@JsonDeserialize(contentAs = Error.class)
	public List<Error> errors;

	public AnotherMutationTypeResponse getMutationPascalCase() {
		return mutation;
	}

	public void setMutation(AnotherMutationTypeResponse mutation) {
		this.mutation = mutation;
	}

	public List<Error> getErrors() {
		return errors;
	}

	public void setErrors(List<Error> errors) {
		this.errors = errors;
	}

}
