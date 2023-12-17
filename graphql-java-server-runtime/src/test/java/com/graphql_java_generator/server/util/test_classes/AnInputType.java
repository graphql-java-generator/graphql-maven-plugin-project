package com.graphql_java_generator.server.util.test_classes;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.graphql_java_generator.annotation.GraphQLInputType;
import com.graphql_java_generator.annotation.GraphQLScalar;

/**
 * A class to test the {@link com.graphql_java_generator.server.util.GraphqlServerUtils#classNameExtractor(Class)}
 * method
 * 
 * @author etienne-sf
 */
@GraphQLInputType("AnInputType")
public class AnInputType {

	@JsonProperty("enumField")
	@GraphQLScalar(fieldName = "enumField", graphQLTypeSimpleName = "AnEnumType", javaClass = AnEnumType.class)
	public AnEnumType enumField;

	public AnEnumType getEnumField() {
		return this.enumField;
	}

	public void setEnumField(AnEnumType enumField) {
		this.enumField = enumField;
	}

}
