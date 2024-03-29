/** Generated by the default template from graphql-java-generator */
package com.graphql_java_generator.domain.client.forum;

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
import com.graphql_java_generator.annotation.GraphQLNonScalar;
import com.graphql_java_generator.annotation.GraphQLObjectType;
import com.graphql_java_generator.annotation.GraphQLScalar;
import java.util.List;

/**
 *
 * @author generated by graphql-java-generator
 * @see <a href="https://github.com/graphql-java-generator/graphql-java-generator">https://github.com/graphql-java-generator/graphql-java-generator</a>
 */
@GraphQLObjectType("__Field")
@JsonInclude(Include.NON_NULL)
@SuppressWarnings("unused")
public class __Field 
{


	/**
	 * This map contains the deserialized values for the alias, as parsed from the json response from the GraphQL
	 * server. The key is the alias name, the value is the deserialiazed value (taking into account custom scalars,
	 * lists, ...)
	 */
	@com.graphql_java_generator.annotation.GraphQLIgnore
	Map<String, Object> aliasValues = new HashMap<>();

	public __Field(){
		// No action
	}

	@JsonProperty("name")
	@GraphQLScalar(fieldName = "name", graphQLTypeSimpleName = "String", javaClass = java.lang.String.class)
	java.lang.String name;


	@JsonProperty("description")
	@GraphQLScalar(fieldName = "description", graphQLTypeSimpleName = "String", javaClass = java.lang.String.class)
	java.lang.String description;


	@JsonProperty("args")
	@JsonDeserialize(using = CustomJacksonDeserializers.List__InputValue.class)
	@GraphQLNonScalar(fieldName = "args", graphQLTypeSimpleName = "__InputValue", javaClass = __InputValue.class)
	List<__InputValue> args;


	@JsonProperty("type")
	@GraphQLNonScalar(fieldName = "type", graphQLTypeSimpleName = "__Type", javaClass = __Type.class)
	__Type type;


	@JsonProperty("isDeprecated")
	@GraphQLScalar(fieldName = "isDeprecated", graphQLTypeSimpleName = "Boolean", javaClass = java.lang.Boolean.class)
	java.lang.Boolean isDeprecated;


	@JsonProperty("deprecationReason")
	@GraphQLScalar(fieldName = "deprecationReason", graphQLTypeSimpleName = "String", javaClass = java.lang.String.class)
	java.lang.String deprecationReason;


	@JsonProperty("__typename")
	@GraphQLScalar(fieldName = "__typename", graphQLTypeSimpleName = "String", javaClass = java.lang.String.class)
	java.lang.String __typename;



	public void setName(java.lang.String name) {
		this.name = name;
	}

	public java.lang.String getName() {
		return name;
	}
		

	public void setDescription(java.lang.String description) {
		this.description = description;
	}

	public java.lang.String getDescription() {
		return description;
	}
		

	public void setArgs(List<__InputValue> args) {
		this.args = args;
	}

	public List<__InputValue> getArgs() {
		return args;
	}
		

	public void setType(__Type type) {
		this.type = type;
	}

	public __Type getType() {
		return type;
	}
		

	public void setIsDeprecated(java.lang.Boolean isDeprecated) {
		this.isDeprecated = isDeprecated;
	}

	public java.lang.Boolean getIsDeprecated() {
		return isDeprecated;
	}
		

	public void setDeprecationReason(java.lang.String deprecationReason) {
		this.deprecationReason = deprecationReason;
	}

	public java.lang.String getDeprecationReason() {
		return deprecationReason;
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
        return "__Field {"
				+ "name: " + name
				+ ", "
				+ "description: " + description
				+ ", "
				+ "args: " + args
				+ ", "
				+ "type: " + type
				+ ", "
				+ "isDeprecated: " + isDeprecated
				+ ", "
				+ "deprecationReason: " + deprecationReason
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
		private java.lang.String name;
		private java.lang.String description;
		private List<__InputValue> args;
		private __Type type;
		private java.lang.Boolean isDeprecated;
		private java.lang.String deprecationReason;

		public Builder withName(java.lang.String name) {
			this.name = name;
			return this;
		}
		public Builder withDescription(java.lang.String description) {
			this.description = description;
			return this;
		}
		public Builder withArgs(List<__InputValue> args) {
			this.args = args;
			return this;
		}
		public Builder withType(__Type type) {
			this.type = type;
			return this;
		}
		public Builder withIsDeprecated(java.lang.Boolean isDeprecated) {
			this.isDeprecated = isDeprecated;
			return this;
		}
		public Builder withDeprecationReason(java.lang.String deprecationReason) {
			this.deprecationReason = deprecationReason;
			return this;
		}

		public __Field build() {
			__Field _object = new __Field();
			_object.setName(name);
			_object.setDescription(description);
			_object.setArgs(args);
			_object.setType(type);
			_object.setIsDeprecated(isDeprecated);
			_object.setDeprecationReason(deprecationReason);
			_object.set__typename("__Field");
			return _object;
		}
	}
}
