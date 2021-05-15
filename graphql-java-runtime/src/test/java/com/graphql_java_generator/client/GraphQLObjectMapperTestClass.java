/**
 * 
 */
package com.graphql_java_generator.client;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.graphql_java_generator.annotation.GraphQLScalar;
import com.graphql_java_generator.client.domain.allGraphQLCases.CustomJacksonDeserializers;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;

/**
 * This class is used for json deserialization test of GraphQL alias values by {@link GraphQLObjectMapperTest}
 * 
 * @author etienne-sf
 */
public class GraphQLObjectMapperTestClass {

	Map<String, Object> aliasValues = new HashMap<>();

	public enum TestEnum {
		VALUE1, VALUE2, VALUE3
	}

	@JsonDeserialize(using = CustomJacksonDeserializers.Date.class)
	@GraphQLScalar(fieldName = "date", graphQLTypeSimpleName = "Date", javaClass = Date.class)
	public Date date;

	@GraphQLScalar(fieldName = "doubleField", graphQLTypeSimpleName = "Float", javaClass = Double.class)
	public Double doubleField;

	@GraphQLScalar(fieldName = "enumField", graphQLTypeSimpleName = "TestEnum", javaClass = TestEnum.class)
	public TestEnum enumField;

	@GraphQLScalar(fieldName = "intField", graphQLTypeSimpleName = "Int", javaClass = Integer.class)
	public Integer intField;

	public String theProperty;

	public String __typename;

	public void setAliasValue(String key, Object value) {
		aliasValues.put(key, value);
	}

	/**
	 * Retrieves the value for the given alias, as it has been received for this object in the GraphQL response. <BR/>
	 * This method <B>should not be used for Custom Scalars</B>, as the parser doesn't know if this alias is a custom
	 * scalar, and which custom scalar to use at deserialization time. In most case, a value will then be provided by
	 * this method with a basis json deserialization, but this value won't be the proper custom scalar value.
	 * 
	 * @param alias
	 * @return
	 * @throws GraphQLRequestExecutionException
	 *             If the value can not be parsed
	 */
	public Object getAliasValue(String alias) throws GraphQLRequestExecutionException {
		Object value = aliasValues.get(alias);
		if (value instanceof GraphQLRequestExecutionException)
			throw (GraphQLRequestExecutionException) value;
		else
			return value;
	}
}
