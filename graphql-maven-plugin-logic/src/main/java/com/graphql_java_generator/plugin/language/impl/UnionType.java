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

	public UnionType(String name, String packageName) {
		super(name, packageName, GraphQlType.UNION);
	}

	/**
	 * @param packageName
	 *            the package name where it must be created
	 * @param pluginConfiguration
	 *            The current {@link GraphQLConfiguration}
	 */
	public UnionType(String packageName) {
		super(null, packageName, GraphQlType.UNION);
	}

	/**
	 * There is no concrete class for an union. So we return it's name (to be used in the @JsonDeserialize
	 * annotation).<BR/>
	 * {@inheritDoc}
	 */
	@Override
	public String getConcreteClassSimpleName() {
		return getName();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("UnionType {name=" + getName() + ", members=[");
		boolean appendSep = false;
		for (ObjectType m : memberTypes) {
			if (appendSep)
				sb.append(",");
			else
				appendSep = true;
			sb.append(m.getName());
		}
		sb.append("]}");
		return sb.toString();
	}

}
