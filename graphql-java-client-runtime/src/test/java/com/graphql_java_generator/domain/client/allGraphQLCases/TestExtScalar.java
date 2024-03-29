/** Generated by the default template from graphql-java-generator */
package com.graphql_java_generator.domain.client.allGraphQLCases;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.graphql_java_generator.annotation.GraphQLObjectType;
import com.graphql_java_generator.annotation.GraphQLScalar;

/**
 * #############################################################################################################
 * ##############   TEST CASE FOR ISSUES #######################################################################
 * #############################################################################################################
 * ############################
 *  test for issue #35
 * 
 *  But removed from this GraphQL schema as enum values in Directives are not supported by graphql-java v14.0 on server side
 *  It works Ok on client side
 * 
 * directive @relation(
 *     name: String
 *     direction: direction
 * ) on FIELD_DEFINITION
 * 
 * enum direction {
 *     IN
 *     OUT
 * }
 * ############################
 * ############################
 *  test for issue #36
 *
 * @author generated by graphql-java-generator
 * @see <a href="https://github.com/graphql-java-generator/graphql-java-generator">https://github.com/graphql-java-generator/graphql-java-generator</a>
 */
@GraphQLObjectType("TestExtScalar")
@JsonInclude(Include.NON_NULL)
@SuppressWarnings("unused")
public class TestExtScalar 
{


	/**
	 * This map contains the deserialized values for the alias, as parsed from the json response from the GraphQL
	 * server. The key is the alias name, the value is the deserialiazed value (taking into account custom scalars,
	 * lists, ...)
	 */
	@com.graphql_java_generator.annotation.GraphQLIgnore
	Map<String, Object> aliasValues = new HashMap<>();

	public TestExtScalar(){
		// No action
	}

	@JsonProperty("bad")
	@JsonDeserialize(using = CustomJacksonDeserializers.NonNegativeInt.class)
	@GraphQLScalar(fieldName = "bad", graphQLTypeSimpleName = "NonNegativeInt", javaClass = java.lang.Integer.class)
	java.lang.Integer bad;


	@JsonProperty("__typename")
	@GraphQLScalar(fieldName = "__typename", graphQLTypeSimpleName = "String", javaClass = java.lang.String.class)
	java.lang.String __typename;



	public void setBad(java.lang.Integer bad) {
		this.bad = bad;
	}

	public java.lang.Integer getBad() {
		return bad;
	}
		

	public void set__typename(java.lang.String __typename) {
		this.__typename = __typename;
	}

	public java.lang.String get__typename() {
		return __typename;
	}
		

 
	/**
	 * This method is called during the json deserialization process, by the {@link GraphQLObjectMapper}, each time an
	 * alias value is read from the json.
	 * 
	 * @param aliasName
	 * @param aliasDeserializedValue
	 */
	public void setAliasValue(String aliasName, Object aliasDeserializedValue) {
		aliasValues.put(aliasName, aliasDeserializedValue);
	}

	/**
	 * Retrieves the value for the given alias, as it has been received for this object in the GraphQL response. <BR/>
	 * This method <B>should not be used for Custom Scalars</B>, as the parser doesn't know if this alias is a custom
	 * scalar, and which custom scalar to use at deserialization time. In most case, a value will then be provided by
	 * this method with a basis json deserialization, but this value won't be the proper custom scalar value.
	 * 
	 * @param alias
	 * @return
	 */
	public Object getAliasValue(String alias) {
		return aliasValues.get(alias);
	}

    public String toString() {
        return "TestExtScalar {"
				+ "bad: " + bad
				+ ", "
				+ "__typename: " + __typename
        		+ "}";
    }

	public static Builder builder() {
		return new Builder();
	}

	/**
	 * The Builder that helps building instance of this POJO. You can get an instance of this class, by calling the
	 * {@link #builder()}
	 */
	public static class Builder {
		private java.lang.Integer bad;

		public Builder withBad(java.lang.Integer bad) {
			this.bad = bad;
			return this;
		}

		public TestExtScalar build() {
			TestExtScalar _object = new TestExtScalar();
			_object.setBad(bad);
			_object.set__typename("TestExtScalar");
			return _object;
		}
	}
}
