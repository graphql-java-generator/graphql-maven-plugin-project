package graphql.java.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import graphql.java.client.domain.Character;
import graphql.java.client.domain.Episode;
import graphql.java.client.request.InputParameter;
import graphql.java.client.request.ResponseDefinition;
import graphql.java.client.request.ResponseDefinitionImpl;

/**
 * Manual test for query execution. Not a JUnit test
 * 
 * @author EtienneSF
 */
public class ManualTest {

	public static void main(String[] args) throws GraphQLResponseParseException, IOException {
		QueryExecutor executor = new QueryExecutorImpl();

		// InputParameters
		List<InputParameter> parameters = new ArrayList<>();
		parameters.add(new InputParameter("episode", Episode.NEWHOPE));

		// ResponseDefinition
		ResponseDefinition responseDef = new ResponseDefinitionImpl(QueryExecutor.GRAPHQL_QUERY_MARKER);
		responseDef.addResponseField("id");
		responseDef.addResponseField("name");
		responseDef.addResponseField("appearsIn");
		ResponseDefinition friendsResponseDef = responseDef.addResponseEntity("friends");
		friendsResponseDef.addResponseField("name");

		Character character = executor.execute("hero", parameters, responseDef, Character.class);
		System.out.println(character);
	}

}
