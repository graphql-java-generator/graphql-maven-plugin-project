/**
 * 
 */
package com.graphql_java_generator.plugin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.graphql_java_generator.plugin.language.impl.InterfaceType;

/**
 * This method add the relay capabilities into the GraphQL schema, as it has been read by {@link DocumentParser}. The
 * relay capabilities are specified in <A HREF="https://relay.dev/graphql/connections.htm">this doc</A>.<BR/>
 * This class will add the items described below in the currently read schema data. It is the possible to generate a
 * Java Really compatible code (by using the <I>graphql</I> goal/task), or to generate the Relay compatible GraphQL
 * schema (by using the <I>mergeSchema</I> task/goal). <BR/>
 * The items added to the in-memory read schema are:
 * <UL>
 * <LI>The <I>Node</I> interface in the GraphQL schema (if not already defined). If this interface is already defined in
 * the given schema, but is not compliant, then an error is thrown.</LI>
 * <LI>The <I>@RelayConnexion</I> directive definition in the GraphQL schema (if not already defined). If this is
 * already defined in the given schema, but is not compliant with the relay specification, then an error is thrown.</LI>
 * <LI>The <I>PageInfo</I> type in the GraphQL schema (if not already defined). If this type is already defined in the
 * given schema, but is not compliant with the relay specification, then an error is thrown.</LI>
 * <LI>All the Edge and Connection types in the GraphQL schema, for each type that is marked by the
 * <I>@RelayConnexion</I> directive.</LI>
 * </UL>
 * 
 * @author etienne-sf
 */
@Component
public class AddRelayConnections {

	/**
	 * The {@link DocumentParser} contains the GraphQL schema data, as it has been read from the given GraphQL schema
	 * file(s).
	 */
	@Autowired
	private DocumentParser documentParser;

	public void addRelayConnections() {

		addNodeInterface();

	}

	private void addNodeInterface() {
		final String NODE = "Node";
		boolean found = false;
		for (InterfaceType d : documentParser.getInterfaceTypes()) {
			if (NODE.equals(d.getName())) {
				// The interface should exist only once, so we may not already have found it.
				// if (found) {
				// fail("There are two interfaces with '" + NODE + "' as a name");
				// }
				// // We've found it.
				// found = true;
				// // Let's check its properties
				// assertEquals(0, d.getImplementz().size(), "No implements");
				// assertEquals(0, d.getMemberOfUnions().size(), "No unions");
				// assertEquals(0, d.getFields().size(), "One field");
				// assertEquals("id", d.getFields().get(0).getName(), "field is id");
				// assertEquals("ID", d.getFields().get(0).getGraphQLTypeName(), "field'stype is ID");
				// assertEquals(true, d.getFields().get(0).isId(), "field is an ID");
				// assertEquals(false, d.getFields().get(0).isItemMandatory(), "field is not a list");
				// assertEquals(false, d.getFields().get(0).isList(), "field is not a list");
				// assertEquals(true, d.getFields().get(0).isMandatory(), "field is mandatory");
				// assertEquals(null, d.getRequestType(), "not a query/mutation/subscription");
				// assertEquals(false, d.isInputType(), "Not an input type");
			}
		}

		// if (!found) {
		// fail("The interface " + NODE + " has not been found");
		// }

		throw new RuntimeException("Not yet implemented");
	}

}
