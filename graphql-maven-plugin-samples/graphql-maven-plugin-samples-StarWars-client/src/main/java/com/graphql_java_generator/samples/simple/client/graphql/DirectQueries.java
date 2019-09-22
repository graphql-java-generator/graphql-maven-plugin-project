package com.graphql_java_generator.samples.simple.client.graphql;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

import com.generated.graphql.Character;
import com.generated.graphql.Droid;
import com.generated.graphql.Episode;
import com.generated.graphql.Human;
import com.generated.graphql.MutationType;
import com.generated.graphql.QueryType;
import com.graphql_java_generator.client.response.GraphQLExecutionException;
import com.graphql_java_generator.client.response.GraphQLRequestPreparationException;
import com.graphql_java_generator.samples.simple.client.Queries;

/**
 * This class implements the simplest way to call GraphQl queries, with the GraphQL Java Generator
 * 
 * @author EtienneSF
 */
public class DirectQueries implements Queries {

	final QueryType queryType;
	final MutationType mutationType;

	/**
	 * This constructor expects the URI of the GraphQL server. This constructor works only for http servers, not for
	 * https ones.<BR/>
	 * For example: https://my.server.com/graphql
	 * 
	 * @param graphqlEndpoint
	 *            the https URI for the GraphQL endpoint
	 * @param sslContext
	 * @param hostnameVerifier
	 */
	public DirectQueries(String graphqlEndpoint, SSLContext sslContext, HostnameVerifier hostnameVerifier) {
		queryType = new QueryType(graphqlEndpoint, sslContext, hostnameVerifier);
		mutationType = new MutationType(graphqlEndpoint, sslContext, hostnameVerifier);
	}

	@Override
	public Character heroFull() throws GraphQLExecutionException, GraphQLRequestPreparationException {
		return queryType.hero("", null);
	}

	@Override
	public Character heroPartial() throws GraphQLExecutionException, GraphQLRequestPreparationException {
		return queryType.hero("{appearsIn name}", Episode.NEWHOPE);
	}

	@Override
	public Character heroFriendsFriendsFriends() throws GraphQLExecutionException, GraphQLRequestPreparationException {
		return queryType.hero("{id appearsIn friends {name friends {friends{id name appearsIn}}}}", Episode.NEWHOPE);
	}

	@Override
	public Human humanFull() throws GraphQLExecutionException, GraphQLRequestPreparationException {
		return queryType.human((String) null, "00000000-0000-0000-0000-000000000045");
	}

	@Override
	public Human humanPartial() throws GraphQLExecutionException, GraphQLRequestPreparationException {
		return queryType.human("{appearsIn homePlanet name}", "00000000-0000-0000-0000-000000000045");
	}

	@Override
	public Human humanFriendsFriendsFriends() throws GraphQLExecutionException, GraphQLRequestPreparationException {
		return queryType.human("{id appearsIn name friends {name friends {friends{id name appearsIn}}}}",
				"00000000-0000-0000-0000-000000000180");
	}

	@Override
	public Droid droidFull() throws GraphQLExecutionException, GraphQLRequestPreparationException {
		return queryType.droid("", "00000000-0000-0000-0000-000000000003");
	}

	@Override
	public Droid droidPartial() throws GraphQLExecutionException, GraphQLRequestPreparationException {
		return queryType.droid("{appearsIn primaryFunction name}", "00000000-0000-0000-0000-000000000003");
	}

	@Override
	public Droid droidFriendsFriendsFriends() throws GraphQLExecutionException, GraphQLRequestPreparationException {
		return queryType.droid(
				"{id appearsIn name friends {name friends {friends{id name appearsIn}}} primaryFunction }",
				"00000000-0000-0000-0000-000000000002");
	}

	@Override
	public Droid droidDoesNotExist() throws GraphQLExecutionException, GraphQLRequestPreparationException {
		return queryType.droid("{id appearsIn friends {name friends {friends{id name appearsIn}}} primaryFunction }",
				"00000000-0000-0000-0000-000000001111");
	}

	@Override
	public Human createHuman(String name, String homePlanet)
			throws GraphQLExecutionException, GraphQLRequestPreparationException {
		return mutationType.createHuman("{id name appearsIn homePlanet friends {id name}}", name, homePlanet);
	}

	@Override
	public Character addFriend(String idCharacter, String idNewFriend)
			throws GraphQLExecutionException, GraphQLRequestPreparationException {
		return mutationType.addFriend("{id name appearsIn friends {id name}}", idCharacter, idNewFriend);
	}

}
