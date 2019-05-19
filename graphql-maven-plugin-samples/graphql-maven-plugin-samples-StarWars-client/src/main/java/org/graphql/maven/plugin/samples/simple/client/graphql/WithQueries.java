/**
 * 
 */
package org.graphql.maven.plugin.samples.simple.client.graphql;

import org.graphql.maven.plugin.samples.simple.client.Queries;

import com.generated.graphql.Character;
import com.generated.graphql.Droid;
import com.generated.graphql.Episode;
import com.generated.graphql.Human;
import com.generated.graphql.QueryType;

import graphql.java.client.request.ObjectResponse;
import graphql.java.client.response.GraphQLExecutionException;
import graphql.java.client.response.GraphQLRequestPreparationException;

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

	QueryType queryType = new QueryType();

	ObjectResponse heroFullResponse;
	ObjectResponse heroPartialResponse;
	ObjectResponse heroFriendsFriendsFriendsResponse;

	ObjectResponse humanFullResponse;
	ObjectResponse humanPartialResponse;
	ObjectResponse humanFriendsFriendsFriendsResponse;

	ObjectResponse droidFullResponse;
	ObjectResponse droidPartialResponse;
	ObjectResponse droidFriendsFriendsFriendsResponse;

	/**
	 * The constructors prepares the queries. That is: once the instance is created, you know that your queries are
	 * syntaxly correct
	 * 
	 * @throws GraphQLRequestPreparationException
	 */
	public WithQueries() throws GraphQLRequestPreparationException {
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
	}

	@Override
	public Character heroFull() throws GraphQLExecutionException {
		return queryType.hero(heroFullResponse, Episode.NEWHOPE);
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
		return queryType.human(humanFullResponse, "45");
	}

	@Override
	public Human humanPartial() throws GraphQLExecutionException {
		return queryType.human(humanPartialResponse, "45");
	}

	@Override
	public Human humanFriendsFriendsFriends() throws GraphQLExecutionException {
		return queryType.human(humanFriendsFriendsFriendsResponse, "180");
	}

	@Override
	public Droid droidFull() throws GraphQLExecutionException {
		return queryType.droid(droidFullResponse, "3");
	}

	@Override
	public Droid droidPartial() throws GraphQLExecutionException {
		return queryType.droid(droidPartialResponse, "3");
	}

	@Override
	public Droid droidFriendsFriendsFriends() throws GraphQLExecutionException {
		return queryType.droid(droidFriendsFriendsFriendsResponse, "2");
	}

	@Override
	public Droid droidDoesNotExist() throws GraphQLExecutionException {
		return queryType.droid(droidFriendsFriendsFriendsResponse, "doesn't exist");
	}

}