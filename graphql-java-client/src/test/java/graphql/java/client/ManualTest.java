package graphql.java.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import graphql.java.client.domain.CharacterImpl;
import graphql.java.client.domain.Episode;
import graphql.java.client.domain.Human;
import graphql.java.client.domain.QueryType;
import graphql.java.client.request.InputParameter;
import graphql.java.client.request.ResponseDef;
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
	QueryType queryType = new QueryType();

	public static void main(String[] args) throws GraphQLExecutionException, IOException {
		ManualTest test = new ManualTest();
		test.executeHeroNew();
		test.executeHuman();
	}

	public void executeHeroNew() throws GraphQLExecutionException, IOException {
		System.out.println("-------------------------------------------------------------------------------------");
		System.out.println("------------------    executeHero()    ----------------------------------------------");

		// InputParameters
		List<InputParameter> parameters = new ArrayList<>();
		parameters.add(new InputParameter("episode", Episode.NEWHOPE));

		// ResponseDef
		ResponseDef responseDef = ResponseDef.newResponseDeBuilder("Character").build();
		responseDef.addResponseField("id");
		responseDef.addResponseField("name");
		responseDef.addResponseField("appearsIn");
		ResponseDef friendsResponseDef = responseDef.addSubObjectResponseDef("Character", "friends");
		friendsResponseDef.addResponseField("name");

		CharacterImpl character = executor.execute("hero", parameters, responseDef, CharacterImpl.class);
		System.out.println(character);
	}

	public void executeHuman() throws GraphQLExecutionException, IOException {
		System.out.println("-------------------------------------------------------------------------------------");
		System.out.println("------------------    executeHumanOld()    ---------------------------------------------");

		// InputParameters
		List<InputParameter> parameters = new ArrayList<>();
		parameters.add(new InputParameter("id", "qd"));

		// ResponseDef
		ResponseDef responseDef = ResponseDef.newResponseDeBuilder("Human").build();
		responseDef.addResponseField("id");
		responseDef.addResponseField("name");

		responseDef.addResponseField("appearsIn");

		ResponseDef friendsResponseDef = responseDef.addSubObjectResponseDef("Character", "friends");
		friendsResponseDef.addResponseField("name");

		Human human = executor.execute("human", parameters, responseDef, Human.class);
		System.out.println(human);
	}

}
