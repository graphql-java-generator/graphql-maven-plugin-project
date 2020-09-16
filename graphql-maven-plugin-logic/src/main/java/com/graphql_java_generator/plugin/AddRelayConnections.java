/**
 * 
 */
package com.graphql_java_generator.plugin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.graphql_java_generator.plugin.language.impl.FieldImpl;
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

	@Autowired
	CommonConfiguration configuration;

	public void addRelayConnections() {

		addNodeInterface();

	}

	private void addNodeInterface() {
		final String NODE = "Node";
		boolean found = false;
		for (InterfaceType i : documentParser.getInterfaceTypes()) {
			if (NODE.equals(i.getName())) {
				// We've found it.
				found = true;

				// Let's check its properties
				if (i.getMemberOfUnions().size() != 0) {
					throw new RuntimeException("The " + NODE
							+ " interface already exists, but is not compliant with the Relay specification (member of unions)");
				}
				if (i.getFields().size() != 1) {
					throw new RuntimeException("The " + NODE
							+ " interface already exists, but is not compliant with the Relay specification (it should contain exactly one field)");
				}
				if ("id".equals(i.getFields().get(0).getName())) {
					throw new RuntimeException("The " + NODE
							+ " interface already exists, but is not compliant with the Relay specification (it should contain only the 'id' field)");
				}
				if ("ID".equals(i.getFields().get(0).getGraphQLTypeName())) {
					throw new RuntimeException("The " + NODE
							+ " interface already exists, but is not compliant with the Relay specification (it should contain only the 'id' field, of the 'ID' type)");
				}
				if (!i.getFields().get(0).isId()) {
					throw new RuntimeException("The " + NODE
							+ " interface already exists, but is not compliant with the Relay specification (it should contain only the 'id' field, that is an identified)");
				}
				if (i.getFields().get(0).isList()) {
					throw new RuntimeException("The " + NODE
							+ " interface already exists, but is not compliant with the Relay specification (it should contain only the 'id' field, that is not a list)");
				}
				if (!i.getFields().get(0).isMandatory()) {
					throw new RuntimeException("The " + NODE
							+ " interface already exists, but is not compliant with the Relay specification (it should contain only the 'id' field, that is mandatory)");
				}
				if (i.getRequestType() != null) {
					throw new RuntimeException("The " + NODE
							+ " interface already exists, but is not compliant with the Relay specification (it should not be a query/mutation/subscription)");
				}
				if (i.isInputType()) {
					throw new RuntimeException("The " + NODE
							+ " interface already exists, but is not compliant with the Relay specification (it should not be an input type)");
				}

				// Ok, this interface is compliant. We're done.
				break;
			}
		}

		if (!found) {
			// The standard case: the interface doesn't exist in the given schema(s). Let's define it.
			InterfaceType i = new InterfaceType(NODE, configuration.getPackageName());
			// Adding the id field toe the Node interface
			FieldImpl f = FieldImpl.builder().name("id").graphQLTypeName("ID").id(true).mandatory(true).owningType(i)
					.build();
			i.getFields().add(f);

			documentParser.getInterfaceTypes().add(i);
			documentParser.getTypes().put(NODE, i);
		}

	}

}
