/**
 * 
 */
package com.graphql_java_generator.plugin.language.impl;

import java.util.ArrayList;
import java.util.List;

import com.graphql_java_generator.plugin.GraphQLConfiguration;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * This class represents an Union, as defined in a GraphQL schema. In the generated classes, this needs to be an
 * interface, as an object type may be in several unions.
 * 
 * @author etienne-sf
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UnionType extends ObjectType {

	/**
	 * Contains the list of all types that are allowed in this union, in the GraphQL schema.
	 */
	List<ObjectType> memberTypes = new ArrayList<>();

	public UnionType(String name, String packageName, GraphQLConfiguration pluginConfiguration) {
		super(name, packageName, pluginConfiguration, GraphQlType.UNION);
	}

	/**
	 * @param packageName
	 *            the package name where it must be created
	 * @param pluginConfiguration
	 *            The current {@link GraphQLConfiguration}
	 */
	public UnionType(String packageName, GraphQLConfiguration pluginConfiguration) {
		super(null, packageName, pluginConfiguration, GraphQlType.UNION);
	}

	/**
	 * There is no concrete class for an union. So we return it's name (to be used in the @JsonDeserialize annotation)
	 */
	@Override
	public String getConcreteClassSimpleName() {
		return getName();
	}

}
