package com.graphql_java_generator.samples.simple.client.graphql;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

import com.generated.graphql.Character;
import com.generated.graphql.Droid;
import com.generated.graphql.Episode;
import com.generated.graphql.Human;
import com.generated.graphql.MutationType;
import com.generated.graphql.QueryType;
import com.graphql_java_generator.client.response.GraphQLRequestExecutionException;
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
	public Character heroFull() throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		return queryType.hero("", null);
	}

	@Override
	public Character heroPartial(Episode episode)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		return queryType.hero("{appearsIn name}", episode);
	}

	@Override
	public Character heroFriendsFriendsFriends(Episode episode)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		return queryType.hero("{id appearsIn friends {name friends {friends{id name appearsIn}}}}", episode);
	}

	@Override
	public Human humanFull(String id) throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		return queryType.human((String) null, id);
	}

	@Override
	public Human humanPartial(String id) throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		return queryType.human("{appearsIn homePlanet name}", id);
	}

	@Override
	public Human humanFriendsFriendsFriends(String id)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		return queryType.human("{id appearsIn name friends {name friends {friends{id name appearsIn}}}}", id);
	}

	@Override
	public Droid droidFull(String id) throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		return queryType.droid("", id);
	}

	@Override
	public Droid droidPartial(String id) throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		return queryType.droid("{appearsIn primaryFunction name}", id);
	}

	@Override
	public Droid droidFriendsFriendsFriends(String id)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		return queryType
				.droid("{id appearsIn name friends {name friends {friends{id name appearsIn}}} primaryFunction }", id);
	}

	@Override
	public Droid droidDoesNotExist() throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		return queryType.droid("{id appearsIn friends {name friends {friends{id name appearsIn}}} primaryFunction }",
				"00000000-0000-0000-0000-000000001111");
	}

	@Override
	public Human createHuman(String name, String homePlanet)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		return mutationType.createHuman("{id name appearsIn homePlanet friends {id name}}", name, homePlanet);
	}

	@Override
	public Character addFriend(String idCharacter, String idNewFriend)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		return mutationType.addFriend("{id name appearsIn friends {id name}}", idCharacter, idNewFriend);
	}

}
