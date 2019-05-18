package org.graphql.maven.plugin.samples.simple.client;

import com.generated.graphql.Character;
import com.generated.graphql.Droid;
import com.generated.graphql.Human;

import graphql.java.client.response.GraphQLExecutionException;
import graphql.java.client.response.GraphQLRequestPreparationException;

public interface Queries {

	Character heroSimple() throws GraphQLExecutionException, GraphQLRequestPreparationException;

	Character heroFriendsFriendsFriends() throws GraphQLExecutionException, GraphQLRequestPreparationException;

	Human humanSimple() throws GraphQLExecutionException, GraphQLRequestPreparationException;

	Human humanFriendsFriendsFriends() throws GraphQLExecutionException, GraphQLRequestPreparationException;

	Droid droidSimple() throws GraphQLExecutionException, GraphQLRequestPreparationException;

	Droid droidFriendsFriendsFriends() throws GraphQLExecutionException, GraphQLRequestPreparationException;

	Droid droidDoesNotExist() throws GraphQLExecutionException, GraphQLRequestPreparationException;

}