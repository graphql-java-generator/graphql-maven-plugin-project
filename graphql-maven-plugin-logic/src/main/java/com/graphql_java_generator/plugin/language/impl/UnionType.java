/**
 * 
 */
package com.graphql_java_generator.plugin.language.impl;

import java.util.ArrayList;
import java.util.List;

import com.graphql_java_generator.plugin.PluginMode;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * This class represents an Union, as defined in a GraphQL schema. In the generated classes, this needs to be an
 * interface, as an object type may be in several unions.
 * 
 * @author EtienneSF
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UnionType extends ObjectType {

	/**
	 * Contains the list of all types that are allowed in this union, in the GraphQL schema.
	 */
	List<ObjectType> memberTypes = new ArrayList<>();

	public UnionType(String name, String packageName, PluginMode mode) {
		super(name, packageName, mode, GraphQlType.UNION);
	}

	/**
	 * @param packageName
	 *            the package name where it must be created
	 * @param mode
	 *            The current {@link PluginMode}
	 */
	public UnionType(String packageName, PluginMode mode) {
		super(null, packageName, mode, GraphQlType.UNION);
	}

	/**
	 * There is no concrete class for an union. So we return it's name (to be used in the @JsonDeserialize annotation)
	 */
	@Override
	public String getConcreteClassSimpleName() {
		return getName();
	}

}
