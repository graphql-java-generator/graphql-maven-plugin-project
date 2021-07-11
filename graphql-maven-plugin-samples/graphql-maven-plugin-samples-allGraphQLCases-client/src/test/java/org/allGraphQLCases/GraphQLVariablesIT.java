package org.allGraphQLCases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.allGraphQLCases.client.AnotherMutationType;
import org.allGraphQLCases.client.CharacterInput;
import org.allGraphQLCases.client.Episode;
import org.allGraphQLCases.client.Human;
import org.allGraphQLCases.client.HumanInput;
import org.allGraphQLCases.client.MyQueryType;
import org.allGraphQLCases.client.util.AnotherMutationTypeExecutor;
import org.allGraphQLCases.client.util.GraphQLRequest;
import org.allGraphQLCases.client.util.MyQueryTypeExecutor;
import org.allGraphQLCases.client.util.TheSubscriptionTypeExecutor;
import org.allGraphQLCases.subscription.SubscriptionCallbackToADate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.graphql_java_generator.client.SubscriptionClient;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;

//Adding "webEnvironment = SpringBootTest.WebEnvironment.NONE" avoid this error:
//"No qualifying bean of type 'ReactiveClientRegistrationRepository' available"
//More details here: https://stackoverflow.com/questions/62558552/error-when-using-enablewebfluxsecurity-in-springboot
@SpringBootTest(classes = SpringTestConfig.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Execution(ExecutionMode.CONCURRENT)
class GraphQLVariablesIT {

	@Autowired
	MyQueryTypeExecutor myQuery;
	@Autowired
	AnotherMutationTypeExecutor mutationType;
	@Autowired
	TheSubscriptionTypeExecutor subscriptionExecutor;

	public static class ExtensionValue {
		public String name;
		public String forname;
	}

	@Execution(ExecutionMode.CONCURRENT)
	@Test
	void test_GraphQLVariable_directQuery()
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		// Preparation
		List<List<Double>> matrix = Arrays.asList(//
				null, //
				Arrays.asList(), //
				Arrays.asList(1.0), //
				Arrays.asList(4.0, 5.0, 6.0)//
		);

		// Go, go, go
		MyQueryType resp = myQuery.exec(
				"query queryWithAMatrix($matrixParam: [[Float]]!) {withListOfList(matrix:$matrixParam){matrix}}", //
				"matrixParam", matrix);

		// Verifications
		List<List<Double>> ret = resp.getWithListOfList().getMatrix();
		assertNotNull(ret);
		assertEquals(4, ret.size());
		int i = 0;
		//
		assertNull(ret.get(i++));
		//
		List<Double> item = ret.get(i++);
		assertNotNull(item);
		assertEquals(0, item.size());
		//
		item = ret.get(i++);
		assertNotNull(item);
		assertEquals(1, item.size());
		assertEquals(1, item.get(0));
		//
		item = ret.get(i++);
		assertNotNull(item);
		assertEquals(3, item.size());
		assertEquals(4, item.get(0));
		assertEquals(5, item.get(1));
		assertEquals(6, item.get(2));
	}

	@Execution(ExecutionMode.CONCURRENT)
	@Test
	void withDirectiveTwoParameters() throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		// Preparation
		GraphQLRequest directiveOnQuery = mutationType.getGraphQLRequest("query namedQuery($uppercase :\n" //
				+ "Boolean, \n\r"//
				+ " $Value :   String ! , $anotherValue:String) {directiveOnQuery (uppercase: $uppercase) @testDirective(value:$Value, anotherValue:$anotherValue)}");
		Map<String, Object> params = new HashMap<>();
		params.put("uppercase", true);
		params.put("anotherValue", "another value");
		params.put("Value", "a first value");

		// Go, go, go
		MyQueryType resp = directiveOnQuery.execQuery(params);

		// Verifications
		assertNotNull(resp);
		List<String> ret = resp.getDirectiveOnQuery();
		assertNotNull(ret);
		assertEquals(2, ret.size());
		//
		assertEquals("A FIRST VALUE", ret.get(0));
		assertEquals("ANOTHER VALUE", ret.get(1));
	}

	@Execution(ExecutionMode.CONCURRENT)
	@Test
	void mutation() throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		// Preparation
		GraphQLRequest mutationWithDirectiveRequest = mutationType.getGraphQLRequest("" + //
				"mutation creation($name  : String!, $friends : [ CharacterInput\r\n] , $appearsIn: [Episode]!,$uppercaseName:Boolean)\r\n"//
				+ "{createHuman (human: {name:$name, friends:$friends, appearsIn:$appearsIn}) "//
				+ "{id name(uppercase: $uppercaseName) appearsIn friends {id friends appearsIn name}}}"//
		);

		CharacterInput friendsParam = CharacterInput.builder().withName("a friend's name")
				.withAppearsIn(Arrays.asList(Episode.NEWHOPE)).withType("Human").build();

		// Go, go, go
		AnotherMutationType resp = mutationWithDirectiveRequest.execMutation(//
				"name", "a new name", //
				"appearsIn", Arrays.asList(Episode.JEDI, Episode.EMPIRE, Episode.NEWHOPE), //
				"uppercaseName", true, //
				"friends", friendsParam);

		// Verifications
		assertNotNull(resp);
		Human human = resp.getCreateHuman();
		assertNotNull(human);
		assertEquals("A NEW NAME", human.getName());
		assertEquals(3, human.getAppearsIn().size());
		assertEquals(Episode.JEDI, human.getAppearsIn().get(0));
		assertEquals(Episode.EMPIRE, human.getAppearsIn().get(1));
		assertEquals(Episode.NEWHOPE, human.getAppearsIn().get(2));
	}

	@Execution(ExecutionMode.CONCURRENT)
	@Test
	void test_mixBindParameterGraphQLVariable()
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		// Preparation
		GraphQLRequest mutation = mutationType.getGraphQLRequest("" + //
				"mutation creation($uppercaseName:Boolean)\r\n"//
				+ "{createHuman (human: &human) "//
				+ "{id name(uppercase: $uppercaseName) appearsIn friends {id friends appearsIn name}}}"//
		);

		CharacterInput friendParam = CharacterInput.builder().withName("a friend's name")
				.withAppearsIn(Arrays.asList(Episode.NEWHOPE)).withType("Human").build();
		//
		HumanInput input = new HumanInput();
		input.setName("a new name");
		input.setAppearsIn(Arrays.asList(Episode.JEDI, Episode.EMPIRE, Episode.NEWHOPE));
		input.setFriends(Arrays.asList(friendParam));

		// Go, go, go
		AnotherMutationType resp = mutation.execMutation(//
				"human", input, //
				"uppercaseName", true);

		// Verifications
		assertNotNull(resp);
		Human human = resp.getCreateHuman();
		assertNotNull(human);
		assertEquals("A NEW NAME", human.getName());
	}

	@Execution(ExecutionMode.CONCURRENT)
	@Test
	public void test_GraphQLVariables_subscribeToADate()
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException, InterruptedException {
		// Preparation
		SubscriptionCallbackToADate callback = new SubscriptionCallbackToADate(
				"test_GraphQLVariables_subscribeToAList");
		Date date = new GregorianCalendar(2021, 4 - 1, 15).getTime();

		// Go, go, go
		GraphQLRequest subscription = subscriptionExecutor.getGraphQLRequest(
				"subscription sub($aCustomScalarParam: Date!) {issue53(date: $aCustomScalarParam){}}");
		SubscriptionClient sub = subscription.execSubscription(callback, Date.class, "aCustomScalarParam", date);

		// Let's wait a max of 10 second, until we receive some notifications (my PC is really slow, especially when the
		// antivirus consumes 98% of my CPU!
		try {
			for (int i = 1; i < 100; i += 1) {
				if (callback.lastReceivedMessage != null)
					break;
				Thread.sleep(100); // Wait 0.1 second
			} // for
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
		}

		// Let's disconnect from the subscription
		sub.unsubscribe();

		// Verification
		assertNotNull(callback.lastReceivedMessage, "The subscription should have received a message");
	}
}
