/**
 * 
 */
package com.graphql_java_generator.samples.simple.client.graphql;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.generated.graphql.Character;
import com.generated.graphql.Droid;
import com.generated.graphql.Episode;
import com.generated.graphql.GraphQLRequest;
import com.generated.graphql.Human;
import com.generated.graphql.MutationTypeExecutor;
import com.generated.graphql.QueryTypeExecutor;
import com.graphql_java_generator.client.request.ObjectResponse;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;
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
 * @author etienne-sf
 */
@Component
public class PartialPreparedRequests implements Queries {

	@Autowired
	QueryTypeExecutor queryTypeExecutor;
	@Autowired
	MutationTypeExecutor mutationTypeExecutor;

	GraphQLRequest heroFullRequest;
	GraphQLRequest heroPartialRequest;
	GraphQLRequest heroFriendsFriendsFriendsRequest;

	GraphQLRequest humanFullRequest;
	GraphQLRequest humanPartialRequest;
	GraphQLRequest humanFriendsFriendsFriendsRequest;

	GraphQLRequest droidFullRequest;
	GraphQLRequest droidPartialRequest;
	GraphQLRequest droidFriendsFriendsFriendsRequest;

	GraphQLRequest createHumanRequest;
	GraphQLRequest addFriendRequest;

	/**
	 * This method is executed, once all dependencies have been loaded. It prepares all the GraphQL requests
	 */
	@PostConstruct
	public void init() throws GraphQLRequestPreparationException {

		// The easiest way: don't precise which fields you want, and all known scalar fields are queried
		heroFullRequest = queryTypeExecutor.getHeroGraphQLRequest(null);

		// Of course, you can precise the fields you want
		heroPartialRequest = queryTypeExecutor.getHeroGraphQLRequest("{appearsIn name}");
		heroFriendsFriendsFriendsRequest = queryTypeExecutor
				.getHeroGraphQLRequest("{id appearsIn friends {name friends {friends{id name appearsIn}}}}");

		// The easiest way: don't precise which fields you want, and all known scalar fields are queried
		humanFullRequest = queryTypeExecutor.getHumanGraphQLRequest(null);

		// Of course, you can precise the fields you want
		humanPartialRequest = queryTypeExecutor.getHumanGraphQLRequest("{appearsIn homePlanet name}");
		humanFriendsFriendsFriendsRequest = queryTypeExecutor
				.getHumanGraphQLRequest("{id appearsIn name friends {name friends {friends{id name appearsIn}}}}");

		// The easiest way: don't precise which fields you want, and all known scalar fields are queried
		droidFullRequest = queryTypeExecutor.getDroidGraphQLRequest(null);

		// Of course, you can precise the fields you want
		droidPartialRequest = queryTypeExecutor.getDroidGraphQLRequest("{appearsIn primaryFunction name}");
		droidFriendsFriendsFriendsRequest = queryTypeExecutor.getDroidGraphQLRequest(
				"{id appearsIn name friends {name friends {friends{id name appearsIn}}} primaryFunction }");

		// A demo of a wrong query. The preparation below should fail.
		// In real code, this would throw an exception to the caller
		// But here, we want the code to go on. So we just check to confirm that the exception occurs, and actually hide
		// it.
		try {
			queryTypeExecutor.getDroidGraphQLRequest(
					"{id appearsIn NON_EXISTING_FIELD friends {name friends {friends{id name appearsIn}}} primaryFunction }");

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
		createHumanRequest = mutationTypeExecutor
				.getCreateHumanGraphQLRequest("{id name homePlanet appearsIn friends {id name}}");
		addFriendRequest = mutationTypeExecutor.getAddFriendGraphQLRequest("{id name appearsIn friends {id name}}");
	}

	@Override
	public Character heroFull() throws GraphQLRequestExecutionException {
		return queryTypeExecutor.hero(heroFullRequest, null);
	}

	@Override
	public Character heroPartial(Episode episode) throws GraphQLRequestExecutionException {
		return queryTypeExecutor.hero(heroPartialRequest, episode);
	}

	@Override
	public Character heroFriendsFriendsFriends(Episode episode) throws GraphQLRequestExecutionException {
		return queryTypeExecutor.hero(heroFriendsFriendsFriendsRequest, episode);
	}

	@Override
	public Human humanFull(String id) throws GraphQLRequestExecutionException {
		return queryTypeExecutor.human(humanFullRequest, id);
	}

	@Override
	public Human humanPartial(String id) throws GraphQLRequestExecutionException {
		return queryTypeExecutor.human(humanPartialRequest, id);
	}

	@Override
	public Human humanFriendsFriendsFriends(String id) throws GraphQLRequestExecutionException {
		return queryTypeExecutor.human(humanFriendsFriendsFriendsRequest, id);
	}

	@Override
	public Droid droidFull(String id) throws GraphQLRequestExecutionException {
		return queryTypeExecutor.droid(droidFullRequest, id);
	}

	@Override
	public Droid droidPartial(String id) throws GraphQLRequestExecutionException {
		return queryTypeExecutor.droid(droidPartialRequest, id);
	}

	@Override
	public Droid droidFriendsFriendsFriends(String id) throws GraphQLRequestExecutionException {
		return queryTypeExecutor.droid(droidFriendsFriendsFriendsRequest, id);
	}

	@Override
	public Droid droidDoesNotExist() throws GraphQLRequestExecutionException {
		return queryTypeExecutor.droid(droidFriendsFriendsFriendsRequest, "00000000-0000-0000-0000-000000001111");
	}

	@Override
	public Human createHuman(String name, String homePlanet)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		return mutationTypeExecutor.createHuman(createHumanRequest, name, homePlanet);
	}

	@Override
	public Character addFriend(String idCharacter, String idNewFriend)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		return mutationTypeExecutor.addFriend(addFriendRequest, idCharacter, idNewFriend);
	}
}