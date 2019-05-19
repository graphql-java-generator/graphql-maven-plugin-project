package org.graphql.maven.plugin.samples.simple.client.graphql;

import org.graphql.maven.plugin.samples.simple.client.Queries;

import com.generated.graphql.Character;
import com.generated.graphql.Droid;
import com.generated.graphql.Episode;
import com.generated.graphql.Human;
import com.generated.graphql.QueryType;

import graphql.java.client.response.GraphQLExecutionException;
import graphql.java.client.response.GraphQLRequestPreparationException;

/**
 * This class implements the simplest way to call GraphQl queries, with the GraphQL Java Generator
 * 
 * @author EtienneSF
 */
public class DirectQueries implements Queries {

	QueryType queryType = new QueryType();

	@Override
	public Character heroFull() throws GraphQLExecutionException, GraphQLRequestPreparationException {
		return queryType.hero("", Episode.NEWHOPE);
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
		return queryType.human((String) null, "45");
	}

	@Override
	public Human humanPartial() throws GraphQLExecutionException, GraphQLRequestPreparationException {
		return queryType.human("{appearsIn homePlanet name}", "45");
	}

	@Override
	public Human humanFriendsFriendsFriends() throws GraphQLExecutionException, GraphQLRequestPreparationException {
		return queryType.human("{id appearsIn name friends {name friends {friends{id name appearsIn}}}}", "180");
	}

	@Override
	public Droid droidFull() throws GraphQLExecutionException, GraphQLRequestPreparationException {
		return queryType.droid("", "3");
	}

	@Override
	public Droid droidPartial() throws GraphQLExecutionException, GraphQLRequestPreparationException {
		return queryType.droid("{appearsIn primaryFunction name}", "3");
	}

	@Override
	public Droid droidFriendsFriendsFriends() throws GraphQLExecutionException, GraphQLRequestPreparationException {
		return queryType
				.droid("{id appearsIn name friends {name friends {friends{id name appearsIn}}} primaryFunction }", "2");
	}

	@Override
	public Droid droidDoesNotExist() throws GraphQLExecutionException, GraphQLRequestPreparationException {
		return queryType.droid("{id appearsIn friends {name friends {friends{id name appearsIn}}} primaryFunction }",
				"doesn't exist");
	}

}
