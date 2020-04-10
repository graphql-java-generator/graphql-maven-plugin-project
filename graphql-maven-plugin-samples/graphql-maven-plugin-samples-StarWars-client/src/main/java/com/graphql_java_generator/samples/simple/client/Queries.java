package com.graphql_java_generator.samples.simple.client;

import com.generated.graphql.Character;
import com.generated.graphql.Droid;
import com.generated.graphql.Episode;
import com.generated.graphql.Human;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;
import com.graphql_java_generator.samples.simple.client.graphql.PartialDirectRequests;
import com.graphql_java_generator.samples.simple.client.graphql.PartialPreparedRequests;

/**
 * These are samples of queries that can be used with GraphQL against the Star Wars GraphQL schema. There are
 * implemented in three ways in these classes: {@link PartialDirectRequests}, {@link WithBuilder}, {@link PartialPreparedRequests}.<BR/>
 * You can see use of these queries in the JUnit tests.
 * 
 * @author etienne-sf
 *
 */
public interface Queries {

	// First part: queries

	Character heroFull() throws GraphQLRequestExecutionException, GraphQLRequestPreparationException;

	Character heroPartial(Episode episode) throws GraphQLRequestExecutionException, GraphQLRequestPreparationException;

	Character heroFriendsFriendsFriends(Episode episode)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException;

	Human humanFull(String id) throws GraphQLRequestExecutionException, GraphQLRequestPreparationException;

	Human humanPartial(String id) throws GraphQLRequestExecutionException, GraphQLRequestPreparationException;

	Human humanFriendsFriendsFriends(String id)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException;

	Droid droidFull(String id) throws GraphQLRequestExecutionException, GraphQLRequestPreparationException;

	Droid droidPartial(String id) throws GraphQLRequestExecutionException, GraphQLRequestPreparationException;

	Droid droidFriendsFriendsFriends(String id)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException;

	Droid droidDoesNotExist() throws GraphQLRequestExecutionException, GraphQLRequestPreparationException;

	// Second part: mutations

	Human createHuman(String name, String homePlanet)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException;

	Character addFriend(String idCharacter, String idNewFriend)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException;
}