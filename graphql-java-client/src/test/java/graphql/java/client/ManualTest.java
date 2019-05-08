package graphql.java.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import graphql.java.client.domain.Character;
import graphql.java.client.domain.CharacterImpl;
import graphql.java.client.domain.Episode;
import graphql.java.client.domain.Human;
import graphql.java.client.domain.QueryType;
import graphql.java.client.request.InputParameter;
import graphql.java.client.request.ObjectResponseDef;
import graphql.java.client.response.GraphQLExecutionException;
import graphql.java.client.response.GraphQLRequestPreparationException;

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

	public static void main(String[] args)
			throws GraphQLExecutionException, IOException, GraphQLRequestPreparationException {
		ManualTest test = new ManualTest();
		test.executeHero();
		test.executeHuman();
	}

	public void executeHero() throws GraphQLExecutionException, IOException, GraphQLRequestPreparationException {
		System.out.println("-------------------------------------------------------------------------------------");
		System.out.println("------------------    executeHero()    ----------------------------------------------");

		// InputParameters
		List<InputParameter> parameters = new ArrayList<>();
		parameters.add(new InputParameter("episode", Episode.NEWHOPE));

		// ObjectResponseDef
		ObjectResponseDef objectResponseDef = queryType.getHeroResponseDefBuilder().withField("id").withField("name")
				.withField("appearsIn").withSubObject("friends",
						ObjectResponseDef.newSubObjectResponseDefBuilder(Character.class).withField("name").build())
				.build();

		CharacterImpl character = executor.execute(objectResponseDef, parameters, CharacterImpl.class);
		System.out.println(character);
	}

	public void executeHuman() throws GraphQLExecutionException, IOException, GraphQLRequestPreparationException {
		System.out.println("-------------------------------------------------------------------------------------");
		System.out.println("------------------    executeHuman()    ---------------------------------------------");

		// InputParameters
		List<InputParameter> parameters = new ArrayList<>();
		parameters.add(new InputParameter("id", "180"));

		// ObjectResponseDef
		ObjectResponseDef objectResponseDef = queryType.getHumanResponseDefBuilder()//
				.withField("id").withField("name").withField("appearsIn")//
				.withSubObject("friends",
						ObjectResponseDef.newSubObjectResponseDefBuilder(Character.class).withField("name").build())
				.build();

		Human human = executor.execute(objectResponseDef, parameters, Human.class);
		System.out.println(human);
	}

}
