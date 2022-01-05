/**
 * 
 */
package com.graphql_java_generator.plugin.language.impl;

import java.util.ArrayList;
import java.util.List;

import com.graphql_java_generator.plugin.DocumentParser;
import com.graphql_java_generator.plugin.conf.CommonConfiguration;

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

	/**
	 * @param name
	 *            the name for this type
	 * @param configuration
	 *            The current plugin configuration, which is accessible through an interface that extends
	 *            {@link CommonConfiguration}
	 * @param documentParser
	 *            The {@link DocumentParser} that has parsed the schema, and so that contains the whole schema
	 *            definition
	 */
	public UnionType(String name, CommonConfiguration configuration, DocumentParser documentParser) {
		super(name, GraphQlType.UNION, configuration, documentParser);
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
