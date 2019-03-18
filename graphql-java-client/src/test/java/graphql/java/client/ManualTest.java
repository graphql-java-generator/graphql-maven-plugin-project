package graphql.java.client;

import java.util.ArrayList;
import java.util.List;

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

	public static void main(String[] args) {
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

		executor.execute("hero", parameters, responseDef);

	}

}
