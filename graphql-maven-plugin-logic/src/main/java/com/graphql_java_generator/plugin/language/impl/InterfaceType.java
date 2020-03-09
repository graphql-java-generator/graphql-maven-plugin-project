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
 * This class represents an Interface, as defined in a GraphQL schema. In the generated classes, this needs to be an
 * interface, as an object type may implement several interfaces. One of the issue to face, when generating the POJOs,
 * is that we may have to instanciate an instance of this interface. For instance, in this GraphQL schema:
 * 
 * <PRE>
 interface Character {
    id: ID!
    name: String!
}

type Human implements Character {
    id: ID!
    name: String!
    friends: [Character]!
}

type Droid implements Character {
    id: ID!
    name: String!
    primaryFunction: String!
}
 * </PRE>
 * 
 * If you get a Human from a query, it will return its friends. But when returning it, you'll only get the its id and
 * name. The mapper has to instanciate a POJO, but this POJO can't be a Human, nor a Droid.<BR/>
 * To solve this, a GraphQL interface declaration let to two java files:
 * <UL>
 * <LI>The interface itself. In the above case, the interface is named Character, as defined in the GraphQL schema.</LI>
 * <LI>A concrete Java class, implementing the interface itself, and only it. In the above case, the interface would be
 * named CharacterImpl, a name derived from the interface named defined in the GraphQL schema. In the case where this
 * name is an already defined type in the GraphQL schema, the generator will search for non used alternative names
 * (CharacterImpl1, CharacterImpl2...)</LI>
 * </UL>
 * 
 * @author EtienneSF
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class InterfaceType extends ObjectType {

	/**
	 * Contains the list of all concrete types which implements this interface. That is: all the types defined in the
	 * GraphQL schema, and the concrete type created by the GraphQL maven plugin to implement this interface
	 */
	List<ObjectType> implementingTypes = new ArrayList<>();

	public InterfaceType(String name, String packageName, PluginMode mode) {
		super(name, packageName, mode, GraphQlType.INTERFACE);
	}

	/**
	 * @param packageName
	 *            the package name where it must be created
	 * @param mode
	 *            The current {@link PluginMode}
	 */
	public InterfaceType(String packageName, PluginMode mode) {
		super(null, packageName, mode, GraphQlType.INTERFACE);
	}

}
