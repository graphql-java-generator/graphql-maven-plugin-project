package com.graphql_java_generator.client.domain.allGraphQLCases;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.graphql_java_generator.annotation.GraphQLNonScalar;
import com.graphql_java_generator.client.response.Error;

public class TheSubscriptionTypeRootResponse {

	@JsonProperty("subscription")
	@GraphQLNonScalar(fieldName = "TheSubscriptionType", graphQLTypeName = "TheSubscriptionType", javaClass = TheSubscriptionTypeResponse.class)
	TheSubscriptionTypeResponse subscription;

	@JsonProperty("errors")
	@JsonDeserialize(contentAs = Error.class)
	public List<Error> errors;

	public TheSubscriptionTypeResponse getSubscriptionPascalCase() {
		return subscription;
	}

	public void setSubscription(TheSubscriptionTypeResponse subscription) {
		this.subscription = subscription;
	}

	public List<Error> getErrors() {
		return errors;
	}

	public void setErrors(List<Error> errors) {
		this.errors = errors;
	}

}
