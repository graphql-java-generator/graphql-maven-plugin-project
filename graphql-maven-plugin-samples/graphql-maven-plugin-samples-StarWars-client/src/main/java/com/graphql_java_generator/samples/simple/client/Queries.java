package com.graphql_java_generator.samples.simple.client;

import com.generated.graphql.Character;
import com.generated.graphql.Droid;
import com.generated.graphql.Human;

import com.graphql_java_generator.client.response.GraphQLExecutionException;
import com.graphql_java_generator.client.response.GraphQLRequestPreparationException;

public interface Queries {

	// First part: queries

	Character heroFull() throws GraphQLExecutionException, GraphQLRequestPreparationException;

	Character heroPartial() throws GraphQLExecutionException, GraphQLRequestPreparationException;

	Character heroFriendsFriendsFriends() throws GraphQLExecutionException, GraphQLRequestPreparationException;

	Human humanFull() throws GraphQLExecutionException, GraphQLRequestPreparationException;

	Human humanPartial() throws GraphQLExecutionException, GraphQLRequestPreparationException;

	Human humanFriendsFriendsFriends() throws GraphQLExecutionException, GraphQLRequestPreparationException;

	Droid droidFull() throws GraphQLExecutionException, GraphQLRequestPreparationException;

	Droid droidPartial() throws GraphQLExecutionException, GraphQLRequestPreparationException;

	Droid droidFriendsFriendsFriends() throws GraphQLExecutionException, GraphQLRequestPreparationException;

	Droid droidDoesNotExist() throws GraphQLExecutionException, GraphQLRequestPreparationException;

	// Second part: mutations

	Human createHuman(String name, String homePlanet)
			throws GraphQLExecutionException, GraphQLRequestPreparationException;

	Character addFriend(String idCharacter, String idNewFriend)
			throws GraphQLExecutionException, GraphQLRequestPreparationException;
}