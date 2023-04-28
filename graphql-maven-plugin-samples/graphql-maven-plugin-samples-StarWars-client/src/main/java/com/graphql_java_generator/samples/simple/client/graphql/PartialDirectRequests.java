package com.graphql_java_generator.samples.simple.client.graphql;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.generated.graphql.Character;
import com.generated.graphql.Droid;
import com.generated.graphql.Episode;
import com.generated.graphql.Human;
import com.generated.graphql.util.MutationTypeExecutor;
import com.generated.graphql.util.QueryTypeExecutor;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;
import com.graphql_java_generator.samples.simple.client.Queries;

/**
 * This class implements the simplest way to call GraphQl queries, with the GraphQL Java Generator
 * 
 * @author etienne-sf
 */
@Component
public class PartialDirectRequests implements Queries {

	@Autowired
	QueryTypeExecutor queryTypeExecutor;

	@Autowired
	MutationTypeExecutor mutationTypeExecutor;

	@Override
	public Character heroFull() throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		return queryTypeExecutor.hero("", null);
	}

	@Override
	public Character heroPartial(Episode episode)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		return queryTypeExecutor.hero("{appearsIn name}", episode);
	}

	@Override
	public Character heroFriendsFriendsFriends(Episode episode)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		return queryTypeExecutor.hero("{id appearsIn friends {name friends {friends{id name appearsIn}}}}", episode);
	}

	@Override
	public Human humanFull(String id) throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		return queryTypeExecutor.human((String) null, id);
	}

	@Override
	public Human humanPartial(String id) throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		return queryTypeExecutor.human("{appearsIn homePlanet name}", id);
	}

	@Override
	public Human humanFriendsFriendsFriends(String id)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		return queryTypeExecutor.human("{id appearsIn name friends {name friends {friends{id name appearsIn}}}}", id);
	}

	@Override
	public Droid droidFull(String id) throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		return queryTypeExecutor.droid("", id);
	}

	@Override
	public Droid droidPartial(String id) throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		return queryTypeExecutor.droid("{appearsIn primaryFunction name}", id);
	}

	@Override
	public Droid droidFriendsFriendsFriends(String id)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		return queryTypeExecutor
				.droid("{id appearsIn name friends {name friends {friends{id name appearsIn}}} primaryFunction }", id);
	}

	@Override
	public Droid droidDoesNotExist() throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		return queryTypeExecutor.droid(
				"{id appearsIn friends {name friends {friends{id name appearsIn}}} primaryFunction }",
				"00000000-0000-0000-0000-000000001111");
	}

	@Override
	public Human createHuman(String name, String homePlanet)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		return mutationTypeExecutor.createHuman("{id name appearsIn homePlanet friends {id name}}", name, homePlanet);
	}

	@Override
	public Character addFriend(String idCharacter, String idNewFriend)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		return mutationTypeExecutor.addFriend("{id name appearsIn friends {id name}}", idCharacter, idNewFriend);
	}

}
