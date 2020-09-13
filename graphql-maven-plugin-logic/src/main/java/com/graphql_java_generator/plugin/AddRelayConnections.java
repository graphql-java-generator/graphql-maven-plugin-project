/**
 * 
 */
package com.graphql_java_generator.plugin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
 * <LI>The <I>Node</I> interface in the GraphQL schema (if not already defined). If this interface is already defined in
 * the given schema, but is not compliant with the relay specification, then an error is thrown.</LI>
 * <LI>The <I>PageInfo</I> type in the GraphQL schema (if not already defined). If this type is already defined in the
 * given schema, but is not compliant with the relay specification, then an error is thrown.</LI>
 * <LI>All the Edge and Connection type in the GraphQL schema, for each type that is marked by the
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

	}

}
