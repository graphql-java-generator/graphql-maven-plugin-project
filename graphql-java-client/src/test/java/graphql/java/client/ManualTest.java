package graphql.java.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import graphql.java.client.domain.CharacterImpl;
import graphql.java.client.domain.Episode;
import graphql.java.client.domain.Human;
import graphql.java.client.request.InputParameter;
import graphql.java.client.request.ResponseDefinition;
import graphql.java.client.request.ResponseDefinition;
import graphql.java.client.response.GraphQLExecutionException;

/**
 * Manual test for query execution. Not a JUnit test. The automation for this test is done in the
 * graphql-maven-plugin-samples-StarWars-server module. This class is done for manual testing of the client, before
 * checking all around with the maven build of all modules.
 * 
 * @author EtienneSF
 */
public class ManualTest {

	QueryExecutor executor = new QueryExecutorImpl();

	public static void main(String[] args) throws GraphQLExecutionException, IOException {
		ManualTest test = new ManualTest();
		test.executeHero();
		test.executeHuman();
	}

	public void executeHeroOld() throws GraphQLExecutionException, IOException {
		System.out.println("-------------------------------------------------------------------------------------");
		System.out.println("------------------    executeHero()    ----------------------------------------------");

		// InputParameters
		List<InputParameter> parameters = new ArrayList<>();
		parameters.add(new InputParameter("episode", Episode.NEWHOPE));

		// ResponseDefinition
		ResponseDefinition responseDef = ResponseDefinition.newEntityBuilder().withField("id").withField("name")
				.withField("appearsIn")
				.withEntity(ResponseDefinition.newEntityBuilder("friends").withField("name").build()).build();

		CharacterImpl character = executor.execute("hero", parameters, responseDef, CharacterImpl.class);
		System.out.println(character);
	}

	public void executeHero() throws GraphQLExecutionException, IOException {
		System.out.println("-------------------------------------------------------------------------------------");
		System.out.println("------------------    executeHero()    ----------------------------------------------");

		// InputParameters
		List<InputParameter> parameters = new ArrayList<>();
		parameters.add(new InputParameter("episode", Episode.NEWHOPE));

		// ResponseDefinition
		ResponseDefinition responseDef = new ResponseDefinition(QueryExecutor.GRAPHQL_QUERY_MARKER);
		responseDef.addResponseField("id");
		responseDef.addResponseField("name");
		responseDef.addResponseField("appearsIn");
		ResponseDefinition friendsResponseDef = responseDef.addResponseEntity("friends");
		friendsResponseDef.addResponseField("name");

		CharacterImpl character = executor.execute("hero", parameters, responseDef, CharacterImpl.class);
		System.out.println(character);
	}

	public void executeHuman() throws GraphQLExecutionException, IOException {
		System.out.println("-------------------------------------------------------------------------------------");
		System.out.println("------------------    executeHuman()    ---------------------------------------------");

		// InputParameters
		List<InputParameter> parameters = new ArrayList<>();
		parameters.add(new InputParameter("id", "qd"));

		// ResponseDefinition
		ResponseDefinition responseDef = new ResponseDefinition(QueryExecutor.GRAPHQL_QUERY_MARKER);
		responseDef.addResponseField("id");
		responseDef.addResponseField("name");

		ResponseDefinition bestFriendResponseDef = responseDef.addResponseEntity("bestFriend");
		bestFriendResponseDef.addResponseField("id");
		bestFriendResponseDef.addResponseField("name");

		responseDef.addResponseField("appearsIn");

		ResponseDefinition friendsResponseDef = responseDef.addResponseEntity("friends");
		friendsResponseDef.addResponseField("name");

		Human human = executor.execute("human", parameters, responseDef, Human.class);
		System.out.println(human);
	}

}
