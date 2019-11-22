package com.graphql_java_generator.samples.simple.client;

import com.generated.graphql.Character;
import com.generated.graphql.Droid;
import com.generated.graphql.Episode;
import com.generated.graphql.Human;
import com.graphql_java_generator.client.response.GraphQLExecutionException;
import com.graphql_java_generator.client.response.GraphQLRequestPreparationException;
import com.graphql_java_generator.samples.simple.client.graphql.DirectQueries;
import com.graphql_java_generator.samples.simple.client.graphql.WithBuilder;
import com.graphql_java_generator.samples.simple.client.graphql.WithQueries;

/**
 * These are samples of queries that can be used with GraphQL against the Star Wars GraphQL schema. There are
 * implemented in three ways in these classes: {@link DirectQueries}, {@link WithBuilder}, {@link WithQueries}.<BR/>
 * You can see use of these queries in the JUnit tests.
 * 
 * @author EtienneSF
 *
 */
public interface Queries {

	// First part: queries

	Character heroFull() throws GraphQLExecutionException, GraphQLRequestPreparationException;

	Character heroPartial(Episode episode) throws GraphQLExecutionException, GraphQLRequestPreparationException;

	Character heroFriendsFriendsFriends(Episode episode)
			throws GraphQLExecutionException, GraphQLRequestPreparationException;

	Human humanFull(String id) throws GraphQLExecutionException, GraphQLRequestPreparationException;

	Human humanPartial(String id) throws GraphQLExecutionException, GraphQLRequestPreparationException;

	Human humanFriendsFriendsFriends(String id) throws GraphQLExecutionException, GraphQLRequestPreparationException;

	Droid droidFull(String id) throws GraphQLExecutionException, GraphQLRequestPreparationException;

	Droid droidPartial(String id) throws GraphQLExecutionException, GraphQLRequestPreparationException;

	Droid droidFriendsFriendsFriends(String id) throws GraphQLExecutionException, GraphQLRequestPreparationException;

	Droid droidDoesNotExist() throws GraphQLExecutionException, GraphQLRequestPreparationException;

	// Second part: mutations

	Human createHuman(String name, String homePlanet)
			throws GraphQLExecutionException, GraphQLRequestPreparationException;

	Character addFriend(String idCharacter, String idNewFriend)
			throws GraphQLExecutionException, GraphQLRequestPreparationException;
}