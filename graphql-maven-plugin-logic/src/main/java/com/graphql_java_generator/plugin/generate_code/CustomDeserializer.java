package com.graphql_java_generator.plugin.generate_code;

import java.util.List;

import com.graphql_java_generator.plugin.conf.CustomScalarDefinition;
import com.graphql_java_generator.util.GraphqlUtils;

import graphql.schema.GraphQLScalarType;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * This class contains the data that allows to generate a custom Jackson deserializer. These deserializer are used to
 * deserialize the incoming response. There are two types of custom deserializer:
 * <UL>
 * <LI>The GraphQL Custom Scalars deserialiser. It takes an input JSON token, and creates the Java instance that match
 * this custom scalar</LI>
 * <LI>The list Custom deserializer. It can deserialize a JSON list. When dealing with dealing with one level of
 * embedded {@link List} (java) or array (GraphQL), it's useless. But GraphQL allows any deep of nested arrays. To
 * manage <I>[[[Human]]]</I>, for instance, it's not possible to deserialize it based on Jackson annotation. So the
 * custom List Deserializer takes care of GraphQL arrays, with any level of nested array.</LI>
 * </UL>
 * 
 * @author etienne-sf
 */
@Data
@AllArgsConstructor
public class CustomDeserializer {

	/** The name of the GraphQL type is used to name the java class for the deserialize */
	private String graphQLTypeName;

	/**
	 * The Java type (for instance java.lang.String), that represent the GraphQL type.<BR/>
	 * For instance, for a GraphQLType <I>[[String]]</I>, the <I>javaClassFullName</I> would contain
	 * <I>List<List<java.lang.String>></I>
	 */
	private String itemJavaClassFullName;

	/**
	 * The {@link CustomScalarDefinition} that contains the information to declare the {@link GraphQLScalarType}. This
	 * should be null for list deserializer. It's mandatory for custom scalars deserializer.
	 */
	private CustomScalarDefinition customScalarDefinition;

	/**
	 * Indicates at which level of nested array this custom deserializer is. To deserialize a value (for custom scalar),
	 * the <I>listLevel</I> is 0. To deserialize a <I>[[Character]]</I>, the <I>listLevel</I> is 2.
	 */
	private int listLevel;

	/**
	 * The {@link CustomDeserializer} to which the JSON parsing should be delegated, for each item of the JSON
	 * list.<BR/>
	 * Null for custom scalars deserializer. Mandatory for list deserializer.
	 */
	private CustomDeserializer itemCustomDeserializer;

	/**
	 * Returns the simple name for the deserializer class.
	 * 
	 * @return The simple name looks like this: <I>CustomJacksonDeserializerListListDate</I>, where:
	 *         <UL>
	 *         <LI>ListList shows that this custom scalar is a list deserializer. It reads items that are at level 2 of
	 *         nested GraphQL arrays (= Java list).</LI>
	 *         <LI>Date is the simple name for the {@link #javaType} of this custom deserializer</LI>
	 *         </UL>
	 */
	public String getClassSimpleName() {
		return getCustomDeserializerClassSimpleName(listLevel, GraphqlUtils.graphqlUtils.getJavaName(graphQLTypeName));
	}

	/**
	 * Returns The Java type (for instance java.lang.String), that represent the GraphQL type.<BR/>
	 * For instance, for a GraphQLType <I>[[String]]</I>, the <I>javaClassFullName</I> would contain
	 * <I>List<List<java.lang.String>></I>
	 * 
	 * @return
	 */
	public String getJavaClassFullName() {
		String ret = itemJavaClassFullName;
		for (int i = 1; i <= listLevel; i += 1) {
			ret = "List<" + ret + ">";
		}
		return ret;
	}

	/**
	 * Standard utility to calculate a Custom Deserialize name. Used in this class, and to define the Jackson annotation
	 * on the field
	 * 
	 * @param listLevel
	 *            Indicates at which level of nested array this custom deserializer is. To deserialize a value (for
	 *            custom scalar), the <I>listLevel</I> is 0. To deserialize a <I>[[Character]]</I>, the <I>listLevel</I>
	 *            is 2.
	 * @param itemClassSimpleName
	 *            The class simple name of the item of the list. For instance, for a field that is
	 *            <I>List&lt;List&lt;Date&gt;&gt;</I>, the <I>itemClassSimpleName</I> would be <I>Date</I>
	 * @return
	 */
	public static String getCustomDeserializerClassSimpleName(int listLevel, String graphQLTypeName) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < listLevel; i += 1) {
			sb.append("List");
		}
		sb.append(graphQLTypeName);
		return sb.toString();

	}
}
