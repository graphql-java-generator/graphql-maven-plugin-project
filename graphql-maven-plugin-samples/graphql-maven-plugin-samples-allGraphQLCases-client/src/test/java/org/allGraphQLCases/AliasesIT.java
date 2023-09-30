/**
 * 
 */
package org.allGraphQLCases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;

import org.allGraphQLCases.client.CEP_Episode_CES;
import org.allGraphQLCases.client.CINP_AllFieldCasesInput_CINS;
import org.allGraphQLCases.client.CINP_FieldParameterInput_CINS;
import org.allGraphQLCases.client.CIP_Character_CIS;
import org.allGraphQLCases.client.CTP_AllFieldCasesWithoutIdSubtype_CTS;
import org.allGraphQLCases.client.CTP_AllFieldCases_CTS;
import org.allGraphQLCases.client.CTP_AnotherMutationType_CTS;
import org.allGraphQLCases.client.CTP_Human_CTS;
import org.allGraphQLCases.client.CTP_MyQueryType_CTS;
import org.allGraphQLCases.client.util.AnotherMutationTypeExecutorAllGraphQLCases;
import org.allGraphQLCases.client.util.GraphQLRequestAllGraphQLCases;
import org.allGraphQLCases.client.util.MyQueryTypeExecutorAllGraphQLCases;
import org.allGraphQLCases.client.util.TheSubscriptionTypeExecutorAllGraphQLCases;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.graphql_java_generator.client.SubscriptionCallback;
import com.graphql_java_generator.client.SubscriptionClient;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;

/**
 * @author etienne-sf
 */
// Adding "webEnvironment = SpringBootTest.WebEnvironment.NONE" avoid this error:
// "No qualifying bean of type 'ReactiveClientRegistrationRepository' available"
// More details here: https://stackoverflow.com/questions/62558552/error-when-using-enablewebfluxsecurity-in-springboot
@SpringBootTest(classes = SpringTestConfig.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Execution(ExecutionMode.CONCURRENT)
public class AliasesIT {

	/** The logger for this class */
	static protected Logger logger = LoggerFactory.getLogger(AliasesIT.class);

	@Autowired
	MyQueryTypeExecutorAllGraphQLCases queryType;
	@Autowired
	AnotherMutationTypeExecutorAllGraphQLCases mutationType;
	@Autowired
	TheSubscriptionTypeExecutorAllGraphQLCases subscriptionExecutor;

	SubscriptionClient sub = null;

	/**
	 * Test of list that contain list, when sending request and receiving response
	 * 
	 * @throws GraphQLRequestPreparationException
	 * @throws GraphQLRequestExecutionException
	 */
	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_ListOfList() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		// Preparation
		GraphQLRequestAllGraphQLCases GraphQLRequestAllGraphQLCases = this.queryType
				.getWithListOfListGraphQLRequest("{matrix2 : matrix}");
		//
		List<List<Double>> matrixSrc = new ArrayList<>();
		for (int i = 0; i <= 2; i += 1) {
			List<Double> sublist = new ArrayList<>();
			for (int j = 0; j <= 3; j += 1) {
				sublist.add((double) (i + j));
			}
			matrixSrc.add(sublist);
		} // for
		Predicate<List<List<Double>>> checkMatrix = new Predicate<List<List<Double>>>() {
			@Override
			public boolean test(List<List<Double>> matrixVerif) {
				for (int i = 0; i <= 2; i += 1) {
					List<Double> sublist = matrixVerif.get(i);
					assertEquals(4, sublist.size());
					for (int j = 0; j <= 3; j += 1) {
						assertEquals(i + j, sublist.get(j));
					}
				}
				return true;
			}
		};

		// Go, go, go
		CTP_AllFieldCases_CTS allFieldCases = this.queryType.withListOfList(GraphQLRequestAllGraphQLCases, matrixSrc);

		// Verification

		assertNotNull(allFieldCases);
		assertNull(allFieldCases.getMatrix(), "No matrix field in the response");
		@SuppressWarnings("unchecked")
		List<List<Double>> matrixVerif = (List<List<Double>>) allFieldCases.getAliasValue("matrix2");
		assertNotNull(matrixVerif);
		assertTrue(checkMatrix.test(matrixVerif), "Check of the returned matrix content");
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_Issue65_ListID() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		// Preparation
		List<CINP_FieldParameterInput_CINS> inputs = new ArrayList<>();
		inputs.add(CINP_FieldParameterInput_CINS.builder().withUppercase(true).build());
		inputs.add(CINP_FieldParameterInput_CINS.builder().withUppercase(false).build());
		//
		GraphQLRequestAllGraphQLCases GraphQLRequestAllGraphQLCases = this.queryType
				.getAllFieldCasesGraphQLRequest("{alias65:issue65(inputs: &inputs) issue65(inputs: &inputs)}");

		// Go, go, go
		CTP_AllFieldCases_CTS ret = this.queryType.allFieldCases(GraphQLRequestAllGraphQLCases, null, "inputs", inputs);

		// Verification
		List<CTP_AllFieldCasesWithoutIdSubtype_CTS> issue65 = ret.getIssue65();
		assertEquals(inputs.size(), issue65.size());
		assertEquals(issue65.get(0).getName().toUpperCase(), issue65.get(0).getName(),
				"The first name should be in uppercase");
		assertNotEquals(issue65.get(1).getName().toUpperCase(), issue65.get(1).getName(),
				"The second name should NOT be in uppercase");

		@SuppressWarnings("unchecked")
		List<CTP_AllFieldCasesWithoutIdSubtype_CTS> alias65 = (List<CTP_AllFieldCasesWithoutIdSubtype_CTS>) ret
				.getAliasValue("alias65");
		assertEquals(inputs.size(), alias65.size());
		assertEquals(alias65.get(0).getName().toUpperCase(), alias65.get(0).getName(),
				"The first name should be in uppercase");
		assertNotEquals(alias65.get(1).getName().toUpperCase(), alias65.get(1).getName(),
				"The second name should NOT be in uppercase");
	}

	@Test
	void testIssue200AliasOnBooleanCustomScalar()
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {

		// Go, go, go
		CTP_MyQueryType_CTS ret = this.queryType
				.exec("query{issue200(param:false) anAliasForTrue:issue200(param:true) anAliasForNull:issue200}");

		// Verification
		assertFalse(ret.getIssue200());
		//
		Object anAliasForTrue = ret.getAliasValue("anAliasForTrue");
		assertTrue(anAliasForTrue instanceof Boolean, anAliasForTrue.getClass().getName());
		assertTrue((Boolean) anAliasForTrue);
		//
		Object anAliasForNull = ret.getAliasValue("anAliasForNull");
		assertNull(anAliasForNull);
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_FullQuery() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		// Preparation
		GraphQLRequestAllGraphQLCases multipleQueriesRequest = this.queryType.getGraphQLRequest("{"//
				+ " directiveOnQuery (uppercase: false) @testDirective(value:&value, anotherValue:?anotherValue)"//
				+ " withOneOptionalParam {aliasId:id id aliasName:name name aliasAppearsIn:appearsIn appearsIn aliasFriends:friends {id name} friends {aliasId:id id aliasName:name name aliasFriends:friends {id name} friends {aliasId:id id aliasName:name name}}}"//
				+ " queryAlias:withOneOptionalParam  {aliasId:id id aliasName:name name aliasAppearsIn2:appearsIn appearsIn aliasFriends:friends {id name} friends {aliasId:id id aliasName:name name aliasFriends:friends {id name} friends {aliasId2:id id aliasName2:name name}}}"//
				+ " withoutParameters {appearsIn @skip(if: &skipAppearsIn) name @skip(if: &skipName) }"//
				+ "}");

		// Go, go, go
		CTP_MyQueryType_CTS resp = multipleQueriesRequest.execQuery( //
				"value", "An expected returned string", //
				"skipAppearsIn", true, //
				"skipName", false);

		// Verification
		assertNotNull(resp.getWithOneOptionalParam());
		assertNotNull(resp.getAliasValue("queryAlias"));
		assertNotNull(resp.getWithoutParameters());
		//
		// withOneOptionalParam
		CIP_Character_CIS withOneOptionalParam = resp.getWithOneOptionalParam();
		//
		assertNotNull(withOneOptionalParam.getAliasValue("aliasId"));
		assertTrue(withOneOptionalParam.getAliasValue("aliasId") instanceof String);
		//
		assertNotNull(withOneOptionalParam.getAliasValue("aliasName"));
		assertTrue(withOneOptionalParam.getAliasValue("aliasName") instanceof String);
		//
		assertNotNull(withOneOptionalParam.getName());
		//
		assertNotNull(withOneOptionalParam.getAliasValue("aliasAppearsIn"));
		assertNull(withOneOptionalParam.getAliasValue("aliasAppearsIn2"));
		assertTrue(withOneOptionalParam.getAliasValue("aliasAppearsIn") instanceof List);
		assertTrue(((List<?>) withOneOptionalParam.getAliasValue("aliasAppearsIn")).size() > 0);
		assertTrue(((List<?>) withOneOptionalParam.getAliasValue("aliasAppearsIn")).get(0) instanceof CEP_Episode_CES);
		//
		assertNotNull(withOneOptionalParam.getAppearsIn());
		//
		assertNotNull(withOneOptionalParam.getAliasValue("aliasFriends"));
		assertTrue(withOneOptionalParam.getAliasValue("aliasFriends") instanceof List);
		assertTrue(((List<?>) withOneOptionalParam.getAliasValue("aliasFriends")).size() > 0);
		assertTrue(((List<?>) withOneOptionalParam.getAliasValue("aliasFriends")).get(0) instanceof CIP_Character_CIS);
		CIP_Character_CIS ch = (CIP_Character_CIS) ((List<?>) withOneOptionalParam.getAliasValue("aliasFriends"))
				.get(0);
		assertNotNull(ch.getId());
		assertNotNull(ch.getName());
		//
		assertNotNull(withOneOptionalParam.getFriends());
		assertTrue(withOneOptionalParam.getFriends().size() > 0);
		CIP_Character_CIS charLevel1 = withOneOptionalParam.getFriends().get(0);
		assertNotNull(charLevel1.getAliasValue("aliasId"));
		assertNull(charLevel1.getAliasValue("aliasId2"));
		assertNotNull(charLevel1.getId());
		assertEquals(charLevel1.getAliasValue("aliasId"), charLevel1.getId());
		assertNotNull(charLevel1.getAliasValue("aliasName"));
		assertNotNull(charLevel1.getName());
		assertEquals(charLevel1.getAliasValue("aliasName"), charLevel1.getName());
		assertNotNull(charLevel1.getAliasValue("aliasFriends"));
		assertNotNull(charLevel1.getFriends());
		assertTrue(charLevel1.getFriends().size() > 0);
		CIP_Character_CIS charLevel2 = charLevel1.getFriends().get(0);
		assertNotNull(charLevel2.getAliasValue("aliasId"));
		assertNull(charLevel2.getAliasValue("aliasId2"));
		assertNotNull(charLevel2.getId());
		assertEquals(charLevel2.getAliasValue("aliasId"), charLevel2.getId());
		assertNotNull(charLevel2.getAliasValue("aliasName"));
		assertNull(charLevel2.getAliasValue("aliasName2"));
		assertNotNull(charLevel2.getName());
		assertEquals(charLevel2.getAliasValue("aliasName"), charLevel2.getName());
		assertNull(charLevel2.getAliasValue("aliasFriends"));
		//
		// Let's check the content of queryAlias (everything is the same out of the deepest level of friends)
		CIP_Character_CIS queryAlias = (CIP_Character_CIS) resp.getAliasValue("queryAlias");
		//
		assertNotNull(queryAlias.getAliasValue("aliasId"));
		assertTrue(queryAlias.getAliasValue("aliasId") instanceof String);
		//
		assertNotNull(queryAlias.getAliasValue("aliasName"));
		assertTrue(queryAlias.getAliasValue("aliasName") instanceof String);
		//
		assertNotNull(queryAlias.getName());
		//
		assertNull(queryAlias.getAliasValue("aliasAppearsIn"));
		assertNotNull(queryAlias.getAliasValue("aliasAppearsIn2"));
		assertTrue(queryAlias.getAliasValue("aliasAppearsIn2") instanceof List);
		assertTrue(((List<?>) queryAlias.getAliasValue("aliasAppearsIn2")).size() > 0);
		assertTrue(((List<?>) queryAlias.getAliasValue("aliasAppearsIn2")).get(0) instanceof CEP_Episode_CES);
		//
		assertNotNull(queryAlias.getAppearsIn());
		//
		assertNotNull(queryAlias.getAliasValue("aliasFriends"));
		assertTrue(queryAlias.getAliasValue("aliasFriends") instanceof List);
		assertTrue(((List<?>) queryAlias.getAliasValue("aliasFriends")).size() > 0);
		assertTrue(((List<?>) queryAlias.getAliasValue("aliasFriends")).get(0) instanceof CIP_Character_CIS);
		ch = (CIP_Character_CIS) ((List<?>) queryAlias.getAliasValue("aliasFriends")).get(0);
		assertNotNull(ch.getId());
		assertNotNull(ch.getName());
		//
		assertNotNull(queryAlias.getFriends());
		assertTrue(queryAlias.getFriends().size() > 0);
		//
		charLevel1 = queryAlias.getFriends().get(0);
		assertNotNull(charLevel1.getAliasValue("aliasId"));
		assertNull(charLevel1.getAliasValue("aliasId2"));
		assertNotNull(charLevel1.getId());
		assertEquals(charLevel1.getAliasValue("aliasId"), charLevel1.getId());
		assertNotNull(charLevel1.getAliasValue("aliasName"));
		assertNotNull(charLevel1.getName());
		assertEquals(charLevel1.getAliasValue("aliasName"), charLevel1.getName());
		assertNotNull(charLevel1.getAliasValue("aliasFriends"));
		assertNotNull(charLevel1.getFriends());
		assertTrue(charLevel1.getFriends().size() > 0);
		//
		charLevel2 = charLevel1.getFriends().get(0);
		assertNull(charLevel2.getAliasValue("aliasId"));
		assertNotNull(charLevel2.getAliasValue("aliasId2"));
		assertNotNull(charLevel2.getId());
		assertEquals(charLevel2.getAliasValue("aliasId2"), charLevel2.getId());
		assertNull(charLevel2.getAliasValue("aliasName"));
		assertNotNull(charLevel2.getAliasValue("aliasName2"));
		assertNotNull(charLevel2.getName());
		assertEquals(charLevel2.getAliasValue("aliasName2"), charLevel2.getName());
		assertNull(charLevel2.getAliasValue("aliasFriends"));
	}

	@SuppressWarnings("unchecked")
	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_createHuman() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		// Preparation
		Date date1 = new Calendar.Builder().setDate(2021, 5 - 1, 15).build().getTime();
		Date date2 = new Calendar.Builder().setDate(2021, 5 - 1, 16).build().getTime();
		CINP_AllFieldCasesInput_CINS inputType = CINP_AllFieldCasesInput_CINS.builder()
				.withId(UUID.randomUUID().toString()).withName("the name").withAge((long) 666).withDate(date1)
				.withDates(Arrays.asList(date1, date2)).withComments(Arrays.asList("Comment 1", "Comment 2"))
				.withBooleans(Arrays.asList(false, true, false)).withAliases(new ArrayList<String>())
				.withPlanets(Arrays.asList("a planet"))
				.withMatrix(Arrays.asList(Arrays.asList(1.0, 2.0), Arrays.asList(3.0))).build();
		GraphQLRequestAllGraphQLCases createHuman = this.mutationType.getGraphQLRequest(""//
				+ "mutation aMutationAlias($inputType:AllFieldCasesInput!) {"//
				+ " mutationAlias : createAllFieldCases(input:$inputType) {"//
				+ "  aliasId:id id aliasName:name name aliasAge:age age "
				+ "  aliasDate:date date aliasDates:dates dates aliasNbComments:nbComments nbComments aliasComments:comments comments "
				+ "  aliasBooleans:booleans booleans aliasMatrix:matrix matrix "//
				+ "}}");

		// Go, go, go
		CTP_AnotherMutationType_CTS resp = createHuman.execMutation("inputType", inputType);

		// Verification
		assertNotNull(resp.getAliasValue("mutationAlias"));
		assertTrue(resp.getAliasValue("mutationAlias") instanceof CTP_AllFieldCases_CTS);
		CTP_AllFieldCases_CTS verif = (CTP_AllFieldCases_CTS) resp.getAliasValue("mutationAlias");
		//
		assertEquals(inputType.getId(), verif.getId());
		assertEquals(inputType.getId(), verif.getAliasValue("aliasId"));
		//
		assertEquals(inputType.getName(), verif.getName());
		assertEquals(inputType.getName(), verif.getAliasValue("aliasName"));
		//
		assertEquals(inputType.getAge(), verif.getAge());
		assertEquals(inputType.getAge(), verif.getAliasValue("aliasAge"));
		//
		assertEquals(inputType.getDate(), verif.getDate());
		assertEquals(inputType.getDate(), verif.getAliasValue("aliasDate"));
		//
		assertEquals(2, verif.getDates().size());
		assertEquals(date1, verif.getDates().get(0));
		assertEquals(date2, verif.getDates().get(1));
		assertEquals(2, verif.getDates().size());
		assertEquals(date1, ((List<Date>) verif.getAliasValue("aliasDates")).get(0));
		assertEquals(date2, ((List<Date>) verif.getAliasValue("aliasDates")).get(1));
		//
		assertEquals(inputType.getNbComments(), verif.getNbComments());
		assertEquals(inputType.getNbComments(), verif.getAliasValue("aliasNbComments"));
		//
		assertEquals(2, verif.getComments().size());
		assertEquals("Comment 1", verif.getComments().get(0));
		assertEquals("Comment 2", verif.getComments().get(1));
		assertEquals(2, verif.getComments().size());
		assertEquals("Comment 1", ((List<String>) verif.getAliasValue("aliasComments")).get(0));
		assertEquals("Comment 2", ((List<String>) verif.getAliasValue("aliasComments")).get(1));
		//
		assertEquals(3, verif.getBooleans().size());
		assertEquals(false, verif.getBooleans().get(0));
		assertEquals(true, verif.getBooleans().get(1));
		assertEquals(false, verif.getBooleans().get(2));
		assertEquals(3, verif.getBooleans().size());
		assertEquals(false, ((List<String>) verif.getAliasValue("aliasBooleans")).get(0));
		assertEquals(true, ((List<String>) verif.getAliasValue("aliasBooleans")).get(1));
		assertEquals(false, ((List<String>) verif.getAliasValue("aliasBooleans")).get(2));
		//
		assertEquals(2, verif.getMatrix().size());
		assertEquals(2, verif.getMatrix().get(0).size());
		assertEquals(1.0, verif.getMatrix().get(0).get(0));
		assertEquals(2.0, verif.getMatrix().get(0).get(1));
		assertEquals(1, verif.getMatrix().get(1).size());
		assertEquals(3.0, verif.getMatrix().get(1).get(0));
		assertEquals(2, ((List<List<Double>>) verif.getAliasValue("aliasMatrix")).size());
		assertEquals(2, ((List<List<Double>>) verif.getAliasValue("aliasMatrix")).get(0).size());
		assertEquals(1.0, ((List<List<Double>>) verif.getAliasValue("aliasMatrix")).get(0).get(0));
		assertEquals(2.0, ((List<List<Double>>) verif.getAliasValue("aliasMatrix")).get(0).get(1));
		assertEquals(1, ((List<List<Double>>) verif.getAliasValue("aliasMatrix")).get(1).size());
		assertEquals(3.0, ((List<List<Double>>) verif.getAliasValue("aliasMatrix")).get(1).get(0));
	}

	public static class HumanSubscriptionCallback implements SubscriptionCallback<CTP_Human_CTS> {
		@SuppressWarnings("hiding")
		private static Logger logger = LoggerFactory.getLogger(HumanSubscriptionCallback.class);

		public boolean connected = false;
		public CTP_Human_CTS lastReceivedMessage;
		public Throwable lastError;

		@Override
		public void onConnect() {
			this.connected = true;
			logger.debug("Subscription connected");
		}

		@Override
		public void onMessage(CTP_Human_CTS t) {
			this.lastReceivedMessage = t;
			logger.debug("Message received: {}", t);
		}

		@Override
		public void onClose(int statusCode, String reason) {
			logger.debug("Subscription closed");
		}

		@Override
		public void onError(Throwable error) {
			this.lastError = error;
			logger.error("Subscription on error: {}", error);
		}
	}

	@Execution(ExecutionMode.CONCURRENT)
	@Test
	public void test_subscription()
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException, InterruptedException {
		// Preparation
		HumanSubscriptionCallback callback = new HumanSubscriptionCallback();

		// Go, go, go
		this.sub = this.subscriptionExecutor.subscribeNewHumanForEpisode(
				"{aliasId:id id aliasName:name name aliasHomePlanet:homePlanet homePlanet}", //
				callback, CEP_Episode_CES.JEDI);

		// Let's wait a max of 10 seconds, until we receive some notifications
		waitForEvent(10, () -> {
			return callback.lastReceivedMessage != null || callback.lastError != null;
		}, "Waiting for the subscription to receive the notification");

		// Let's disconnect from the subscription
		this.sub.unsubscribe();

		// Verification
		if (callback.lastError != null) {
			fail("The subscription raised this error: " + callback.lastError);
		}
		assertNotNull(callback.lastReceivedMessage);
		assertTrue(callback.lastReceivedMessage instanceof CTP_Human_CTS);
		CTP_Human_CTS verif = callback.lastReceivedMessage;
		assertEquals(verif.getId(), verif.getAliasValue("aliasId"));
		assertEquals(verif.getName(), verif.getAliasValue("aliasName"));
		assertEquals(verif.getHomePlanet(), verif.getAliasValue("aliasHomePlanet"));
	}

	/**
	 * Wait for as long as the given delay, but will return as soon as the test is ok. If the given delay expires, then
	 * this method fails
	 * 
	 * @param nbSeconds
	 * @param test
	 * @param expectedEvent
	 */
	public static void waitForEvent(int nbSeconds, BooleanSupplier test, String expectedEvent) {
		logger.debug("Starting to wait for '{}'", expectedEvent);
		int increment = 20;
		for (int i = 0; i < nbSeconds * 1000 / increment; i += 1) {
			if (test.getAsBoolean()) {
				// The condition is met. Let's return to the caller.
				logger.debug("Finished waiting for '{}' (the condition is met)", expectedEvent);
				return;
			}
			try {
				Thread.sleep(increment);
			} catch (InterruptedException e) {
				logger.trace("got interrupted");
			}
		}

		// Too bad...
		String msg = "The delay has expired, when waiting for '" + expectedEvent + "'";
		logger.error(msg);
		fail(msg);
	}
}