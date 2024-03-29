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
import com.graphql_java_generator.annotation.GraphQLObjectType;
import com.graphql_java_generator.annotation.GraphQLScalar;

/**
 *
 * @author generated by graphql-java-generator
 * @see <a href="https://github.com/graphql-java-generator/graphql-java-generator">https://github.com/graphql-java-generator/graphql-java-generator</a>
 */
@GraphQLObjectType("Builder")
@JsonInclude(Include.NON_NULL)
@SuppressWarnings("unused")
public class Builder 
{


	/**
	 * This map contains the deserialized values for the alias, as parsed from the json response from the GraphQL
	 * server. The key is the alias name, the value is the deserialiazed value (taking into account custom scalars,
	 * lists, ...)
	 */
	@com.graphql_java_generator.annotation.GraphQLIgnore
	Map<String, Object> aliasValues = new HashMap<>();

	public Builder(){
		// No action
	}

	/**
	 *  Generates a clash between the java classname, and the Builder class generated in each POJO
	 */
	@JsonProperty("someValue")
	@GraphQLScalar(fieldName = "someValue", graphQLTypeSimpleName = "String", javaClass = java.lang.String.class)
	java.lang.String someValue;


	@JsonProperty("__typename")
	@GraphQLScalar(fieldName = "__typename", graphQLTypeSimpleName = "String", javaClass = java.lang.String.class)
	java.lang.String __typename;



	/**
	 *  Generates a clash between the java classname, and the Builder class generated in each POJO
	 */
	public void setSomeValue(java.lang.String someValue) {
		this.someValue = someValue;
	}

	/**
	 *  Generates a clash between the java classname, and the Builder class generated in each POJO
	*/
	public java.lang.String getSomeValue() {
		return someValue;
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
        return "Builder {"
				+ "someValue: " + someValue
				+ ", "
				+ "__typename: " + __typename
        		+ "}";
    }

	public static _Builder builder() {
		return new _Builder();
	}

	/**
	 * The Builder that helps building instance of this POJO. You can get an instance of this class, by calling the
	 * {@link #builder()}
	 * <br/>As this GraphQL type's name is Builder, the inner Builder class is renamed to _Builder, to avoid name 
	 * collision during Java compilation.
	 */
	public static class _Builder {
		private java.lang.String someValue;

		public _Builder withSomeValue(java.lang.String someValue) {
			this.someValue = someValue;
			return this;
		}

		public Builder build() {
			Builder _object = new Builder();
			_object.setSomeValue(someValue);
			_object.set__typename("Builder");
			return _object;
		}
	}
}
