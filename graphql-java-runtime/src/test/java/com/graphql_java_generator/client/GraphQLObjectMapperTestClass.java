/**
 * 
 */
package com.graphql_java_generator.client;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.TreeNode;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;

import graphql.schema.GraphQLScalarType;

/**
 * This class is used for json deserialization test of GraphQL alias values by {@link GraphQLObjectMapperTest}
 * 
 * @author etienne-sf
 */
public class GraphQLObjectMapperTestClass {

	Map<String, Object> aliasParsedValues = new HashMap<>();
	Map<String, TreeNode> aliasTreeNodeValues = new HashMap<>();

	public enum TestEnum {
		VALUE1, VALUE2, VALUE3
	}

	public String theProperty;

	public String __typename;

	public void setAliasParsedValue(String key, Object value) {
		aliasParsedValues.put(key, value);
	}

	public void setAliasTreeNodeValue(String key, TreeNode value) {
		aliasTreeNodeValues.put(key, value);
	}

	public String get__typename() {
		return __typename;
	}

	public void set__typename(String __typename) {
		this.__typename = __typename;
	}

	public String getTheProperty() {
		return theProperty;
	}

	public void setTheProperty(String theProperty) {
		this.theProperty = theProperty;
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
	public Object getValue(String alias) throws GraphQLRequestExecutionException {
		Object value = aliasParsedValues.get(alias);
		if (value instanceof GraphQLRequestExecutionException)
			throw (GraphQLRequestExecutionException) value;
		else
			return value;
	}

	/**
	 * Retrieves the custom scalar value for the given alias, based on the provided custom scalar class.
	 * 
	 * @param property
	 * @param customScalar
	 * @return This method returns an instance of customScalar for a <I>CustomScalar</I> GraphQL type, a
	 *         List<customScalar> for a <I>[CustomScalar]</I> GraphQL type, a List<List<customScalar>> for a
	 *         <I>[[CustomScalar]]</I> GraphQL type...
	 */
	public Object getCustomScalarValue(String property, GraphQLScalarType customScalar) {
		return customScalar.getCoercing().parseValue(aliasParsedValues.get(property));
	}

	/**
	 * Retrieves the enum value for the given alias, based on the provided enum class.
	 * 
	 * @param <T>
	 * @param property
	 * @param enumClass
	 * @return This method returns an instance of enumClass for a <I>Enum</I> GraphQL type, a List<enumClass> for a
	 *         <I>[Enum]</I> GraphQL type, a List<List<enumClass>> for a <I>[[Enum]]</I> GraphQL type...
	 */
	public <T extends Enum<T>> Object getEnumValue(String property, Class<T> enumClass) {
		return Enum.valueOf(enumClass, (String) aliasParsedValues.get(property));
	}

}
