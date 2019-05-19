package org.graphql.maven.plugin.samples.simple.client;

import com.generated.graphql.Character;
import com.generated.graphql.Droid;
import com.generated.graphql.Human;

import graphql.java.client.response.GraphQLExecutionException;
import graphql.java.client.response.GraphQLRequestPreparationException;

public interface Queries {

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

}