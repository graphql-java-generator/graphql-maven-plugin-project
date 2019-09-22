/**
 * 
 */
package com.graphql_java_generator.samples.simple.client.graphql;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

import com.generated.graphql.Character;
import com.generated.graphql.Droid;
import com.generated.graphql.Episode;
import com.generated.graphql.Human;
import com.generated.graphql.MutationType;
import com.generated.graphql.QueryType;
import com.graphql_java_generator.client.request.ObjectResponse;
import com.graphql_java_generator.client.response.GraphQLExecutionException;
import com.graphql_java_generator.client.response.GraphQLRequestPreparationException;
import com.graphql_java_generator.samples.simple.client.Queries;

/**
 * This class implements the away to call GraphQl queries, where all queries are prepared before execution.<BR/>
 * The advantages are:
 * <UL>
 * <LI>Performance: this avoid to build an {@link ObjectResponse} for each response. This {@link ObjectResponse} is
 * useful, to help control at runtime if a field has been queried or not. It allows to throw an exception when your code
 * tries to use a field that was not queried</LI>
 * <LI>Security: as all request have been prepared at startup, this make sure at startup that your queries are
 * valid.</LI>
 * </UL>
 * 
 * @author EtienneSF
 */
public class WithQueries implements Queries {

	final QueryType queryType;
	final MutationType mutationType;

	ObjectResponse heroFullResponse;
	ObjectResponse heroPartialResponse;
	ObjectResponse heroFriendsFriendsFriendsResponse;

	ObjectResponse humanFullResponse;
	ObjectResponse humanPartialResponse;
	ObjectResponse humanFriendsFriendsFriendsResponse;

	ObjectResponse droidFullResponse;
	ObjectResponse droidPartialResponse;
	ObjectResponse droidFriendsFriendsFriendsResponse;

	ObjectResponse createHuman;
	ObjectResponse addFriend;

	/**
	 * This constructor expects the URI of the GraphQL server. This constructor works only for http servers, not for
	 * https ones.<BR/>
	 * For example: https://my.server.com/graphql
	 * 
	 * @param graphqlEndpoint
	 *            the https URI for the GraphQL endpoint
	 * @param sslContext
	 * @param hostnameVerifier
	 * @throws GraphQLRequestPreparationException
	 */
	public WithQueries(String graphqlEndpoint, SSLContext sslContext, HostnameVerifier hostnameVerifier)
			throws GraphQLRequestPreparationException {
		queryType = new QueryType(graphqlEndpoint, sslContext, hostnameVerifier);
		mutationType = new MutationType(graphqlEndpoint, sslContext, hostnameVerifier);

		// The easiest way: don't precise which fields you want, and all known scalar fields are queried
		heroFullResponse = queryType.getHeroResponseBuilder().build();

		// Of course, you can precise the fields you want
		heroPartialResponse = queryType.getHeroResponseBuilder().withQueryResponseDef("{appearsIn name}").build();
		heroFriendsFriendsFriendsResponse = queryType.getHeroResponseBuilder()
				.withQueryResponseDef("{id appearsIn friends {name friends {friends{id name appearsIn}}}}").build();

		// The easiest way: don't precise which fields you want, and all known scalar fields are queried
		humanFullResponse = queryType.getHumanResponseBuilder().build();

		// Of course, you can precise the fields you want
		humanPartialResponse = queryType.getHumanResponseBuilder().withQueryResponseDef("{appearsIn homePlanet name}")
				.build();
		humanFriendsFriendsFriendsResponse = queryType.getHumanResponseBuilder()
				.withQueryResponseDef("{id appearsIn name friends {name friends {friends{id name appearsIn}}}}")
				.build();

		// The easiest way: don't precise which fields you want, and all known scalar fields are queried
		droidFullResponse = queryType.getDroidResponseBuilder().build();

		// Of course, you can precise the fields you want
		droidPartialResponse = queryType.getDroidResponseBuilder()
				.withQueryResponseDef("{appearsIn primaryFunction name}").build();
		droidFriendsFriendsFriendsResponse = queryType.getDroidResponseBuilder()
				.withQueryResponseDef(
						"{id appearsIn name friends {name friends {friends{id name appearsIn}}} primaryFunction }")
				.build();

		// A demo of a wrong query. The preparation below should fail.
		// In real code, this would throw an exception to the caller
		// But here, we want the code to go on. So we just check to confirm that the exception occurs, and actually hide
		// it.
		try {
			queryType.getDroidResponseBuilder().withQueryResponseDef(
					"{id appearsIn NON_EXISTING_FIELD friends {name friends {friends{id name appearsIn}}} primaryFunction }")
					.build();

			// Oups, if we got there, the expected exception was not thrown. Which means that the sample failed.
			// Let's throw an exception to block the execution (as the Maven build executes this sample, this will hang
			// the build)
			throw new RuntimeException("The query with the NON_EXISTING_FIELD should have thrown a "
					+ GraphQLRequestPreparationException.class.getName()
					+ " exception, but no exception was thrown (in " + this.getClass().getName() + ")");

		} catch (GraphQLRequestPreparationException e) {
			// This what's expected. So, no further action ... as we're in a sample !
		}

		// Mutations
		createHuman = mutationType.getCreateHumanResponseBuilder()
				.withQueryResponseDef("{id name homePlanet appearsIn friends {id name}}").build();
		addFriend = mutationType.getAddFriendResponseBuilder()
				.withQueryResponseDef("{id name appearsIn friends {id name}}").build();
	}

	@Override
	public Character heroFull() throws GraphQLExecutionException {
		return queryType.hero(heroFullResponse, null);
	}

	@Override
	public Character heroPartial() throws GraphQLExecutionException {
		return queryType.hero(heroPartialResponse, Episode.NEWHOPE);
	}

	@Override
	public Character heroFriendsFriendsFriends() throws GraphQLExecutionException {
		return queryType.hero(heroFriendsFriendsFriendsResponse, Episode.NEWHOPE);
	}

	@Override
	public Human humanFull() throws GraphQLExecutionException {
		return queryType.human(humanFullResponse, "00000000-0000-0000-0000-000000000045");
	}

	@Override
	public Human humanPartial() throws GraphQLExecutionException {
		return queryType.human(humanPartialResponse, "00000000-0000-0000-0000-000000000045");
	}

	@Override
	public Human humanFriendsFriendsFriends() throws GraphQLExecutionException {
		return queryType.human(humanFriendsFriendsFriendsResponse, "00000000-0000-0000-0000-000000000180");
	}

	@Override
	public Droid droidFull() throws GraphQLExecutionException {
		return queryType.droid(droidFullResponse, "00000000-0000-0000-0000-000000000003");
	}

	@Override
	public Droid droidPartial() throws GraphQLExecutionException {
		return queryType.droid(droidPartialResponse, "00000000-0000-0000-0000-000000000003");
	}

	@Override
	public Droid droidFriendsFriendsFriends() throws GraphQLExecutionException {
		return queryType.droid(droidFriendsFriendsFriendsResponse, "00000000-0000-0000-0000-000000000002");
	}

	@Override
	public Droid droidDoesNotExist() throws GraphQLExecutionException {
		return queryType.droid(droidFriendsFriendsFriendsResponse, "00000000-0000-0000-0000-000000001111");
	}

	@Override
	public Human createHuman(String name, String homePlanet)
			throws GraphQLExecutionException, GraphQLRequestPreparationException {
		return mutationType.createHuman(createHuman, name, homePlanet);
	}

	@Override
	public Character addFriend(String idCharacter, String idNewFriend)
			throws GraphQLExecutionException, GraphQLRequestPreparationException {
		return mutationType.addFriend(addFriend, idCharacter, idNewFriend);
	}
}