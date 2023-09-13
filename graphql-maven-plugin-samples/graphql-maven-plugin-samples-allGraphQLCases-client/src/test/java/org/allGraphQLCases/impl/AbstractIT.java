package org.allGraphQLCases.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.allGraphQLCases.SpringTestConfig;
import org.allGraphQLCases.client.CEP_Episode_CES;
import org.allGraphQLCases.client.CEP_extends_CES;
import org.allGraphQLCases.client.CINP_AllFieldCasesInput_CINS;
import org.allGraphQLCases.client.CINP_CharacterInput_CINS;
import org.allGraphQLCases.client.CINP_FieldParameterInput_CINS;
import org.allGraphQLCases.client.CIP_Character_CIS;
import org.allGraphQLCases.client.CTP_AllFieldCases_CTS;
import org.allGraphQLCases.client.CTP_Droid_CTS;
import org.allGraphQLCases.client.CTP_Human_CTS;
import org.allGraphQLCases.client.MyQueryTypeExecutorAllGraphQLCases;
import org.allGraphQLCases.demo.PartialQueries;
import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;

/**
 * As it is suffixed by "IT", this is an integration test. Thus, it allows us to start the GraphQL StatWars server, see
 * the pom.xml file for details.
 * 
 * @author etienne-sf
 */
// Adding "webEnvironment = SpringBootTest.WebEnvironment.NONE" avoid this error:
// "No qualifying bean of type 'ReactiveClientRegistrationRepository' available"
// More details here: https://stackoverflow.com/questions/62558552/error-when-using-enablewebfluxsecurity-in-springboot
@SpringBootTest(classes = SpringTestConfig.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Execution(ExecutionMode.CONCURRENT)
public abstract class AbstractIT {

	@Autowired
	MyQueryTypeExecutorAllGraphQLCases queryType;

	@Autowired
	protected ApplicationContext ctx;

	PartialQueries partialQueries;

	@BeforeEach
	void setup() {
		this.partialQueries = getQueries();
		assertNotNull(this.partialQueries);
	}

	/** Get the class that will execute the queries. This is a particular class, for each test */
	protected abstract PartialQueries getQueries();

	@Execution(ExecutionMode.CONCURRENT)
	@Test
	void test_withoutParameters() throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		List<CIP_Character_CIS> list = this.partialQueries.withoutParameters();

		assertNotNull(list);
		assertEquals(10, list.size());
		for (CIP_Character_CIS c : list) {
			checkCharacter(c, "withoutParameters", true, "Random String (", 0, 0);
		}
	}

	@Execution(ExecutionMode.CONCURRENT)
	@Test
	void test_withOneOptionalParam() throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {

		// Without parameter
		CIP_Character_CIS c = this.partialQueries.withOneOptionalParam(null);
		checkCharacter(c, "test_withOneOptionalParam(null)", false, "Random String (", 0, 0);

		// With a parameter
		CINP_CharacterInput_CINS input = new CINP_CharacterInput_CINS();
		input.setName("A name");
		input.setAppearsIn(new ArrayList<CEP_Episode_CES>());
		input.setType("Human");

		// Go, go, go
		c = this.partialQueries.withOneOptionalParam(input);
		// Verification
		assertNotNull(c.getId());
		assertEquals("A name", c.getName());

		// appearsIn and friends is generated on server side.
		assertNotNull(c.getAppearsIn());
		assertEquals(2, c.getAppearsIn().size()); // See DataFetchersDelegateHumanImpl.appearsIn
		assertNotNull(c.getFriends());
		assertEquals(6, c.getFriends().size());// See DataFetchersDelegateHumanImpl.friends
	}

	@Execution(ExecutionMode.CONCURRENT)
	@Test
	void test_withOneMandatoryParam_nullParameter()
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {

		GraphQLRequestExecutionException e = assertThrows(GraphQLRequestExecutionException.class,
				() -> this.partialQueries.withOneMandatoryParam(null));
		assertTrue(e.getMessage().contains("character"));
	}

	@Execution(ExecutionMode.CONCURRENT)
	@Test
	void test_withOneMandatoryParam_OK() throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		// With a non null parameter
		CINP_CharacterInput_CINS input = new CINP_CharacterInput_CINS();
		input.setName("A name");
		input.setAppearsIn(new ArrayList<CEP_Episode_CES>());
		input.setType("Droid");

		// Go, go, go
		CIP_Character_CIS c = this.partialQueries.withOneMandatoryParam(input);

		// Verification
		assertTrue(c instanceof CTP_Droid_CTS);
		assertNotNull(c.getId());
		assertEquals("A name", c.getName());

		// appearsIn and friends is generated on server side.
		assertNotNull(c.getAppearsIn());
		assertEquals(2, c.getAppearsIn().size()); // See DataFetchersDelegateDroidImpl.appearsIn
		assertNotNull(c.getFriends());
		assertEquals(5, c.getFriends().size());// See DataFetchersDelegateDroidImpl.friends
	}

	@Execution(ExecutionMode.CONCURRENT)
	@Test
	void test_withEnum() throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {

		// With null parameter: NEWHOPE is the default value
		CIP_Character_CIS c = this.partialQueries.withEnum(null);
		assertEquals(CEP_Episode_CES.NEWHOPE.name(), c.getName());// See server code for more info

		// With a non null parameter
		c = this.partialQueries.withEnum(CEP_Episode_CES.JEDI);
		assertEquals(CEP_Episode_CES.JEDI.name(), c.getName()); // See server code for more info
	}

	@Execution(ExecutionMode.CONCURRENT)
	@Test
	void test_withList() throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		// Preparation
		CINP_CharacterInput_CINS ci1 = new CINP_CharacterInput_CINS();
		ci1.setName("A name");
		ci1.setAppearsIn(new ArrayList<CEP_Episode_CES>());
		ci1.setType("Droid");
		//
		CINP_CharacterInput_CINS ci2 = new CINP_CharacterInput_CINS();
		ci2.setName("Another name");
		ci2.setAppearsIn(new ArrayList<CEP_Episode_CES>());
		ci2.setType("Human");
		//
		List<CINP_CharacterInput_CINS> list = new ArrayList<CINP_CharacterInput_CINS>();
		list.add(ci1);
		list.add(ci2);
		//
		String firstName = "A first name";

		// Go, go, go
		List<CIP_Character_CIS> ret = this.partialQueries.withList(firstName, list);

		// Verification
		assertEquals(2, ret.size());
		//
		int i = 0;
		assertNotNull(ret.get(i).getId());
		assertEquals(firstName, ret.get(i).getName());
		assertTrue(ret.get(i) instanceof CTP_Droid_CTS);
		//
		i += 1;
		assertNotNull(ret.get(i).getId());
		assertEquals("Another name", ret.get(i).getName());
		assertTrue(ret.get(i) instanceof CTP_Human_CTS);
	}

	@Execution(ExecutionMode.CONCURRENT)
	@Test
	void test_allFieldCases() throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		String uuid = UUID.randomUUID().toString();
		Date date1 = new Calendar.Builder().setDate(2022, 4 - 1, 1).build().getTime();
		Date date2 = new Calendar.Builder().setDate(2022, 4 - 1, 2).build().getTime();
		Date date3 = new Calendar.Builder().setDate(2022, 4 - 1, 3).build().getTime();
		Date date4 = new Calendar.Builder().setDate(2022, 4 - 1, 4).build().getTime();
		Date date5 = new Calendar.Builder().setDate(2022, 4 - 1, 5).build().getTime();
		Date date6 = new Calendar.Builder().setDate(2022, 4 - 1, 6).build().getTime();
		OffsetDateTime dateTime = OffsetDateTime.of(2022, 4 - 1, 6, 13, 58, 59, 0, ZoneOffset.UTC);

		// Preparation
		@SuppressWarnings("unchecked")
		CINP_AllFieldCasesInput_CINS allFieldCasesInput = CINP_AllFieldCasesInput_CINS.builder()//
				.withId(uuid)//
				.withName("a name")//
				.withAge((long) 666)//
				.withDateTime(dateTime)//
				.withAliases(new ArrayList<String>())//
				.withDate(date1)//
				.withDates((List<Date>) (Object) Arrays.asList(Arrays.array(date2, date3)))
				.withPlanets((List<String>) (Object) Arrays.asList(Arrays.array("planet1", "planet2")))
				.withMatrix(new ArrayList<List<Double>>())//
				.build();
		Boolean uppercase = true;
		String textToAppendToTheForname = "textToAppendToTheForname";
		long nbItemsWithId = 3;
		List<Date> dates = new ArrayList<>();
		dates.add(date4);
		dates.add(date5);
		Boolean uppercaseNameList = null;
		String textToAppendToTheFornameWithId = "textToAppendToTheFornameWithId";
		CINP_FieldParameterInput_CINS input = new CINP_FieldParameterInput_CINS();
		input.setUppercase(true);
		int nbItemsWithoutId = 6;
		CINP_FieldParameterInput_CINS inputList = null;
		String textToAppendToTheFornameWithoutId = "textToAppendToTheFornameWithoutId";

		// Go, go, go
		CTP_AllFieldCases_CTS allFieldCases = this.partialQueries.allFieldCases(allFieldCasesInput, uppercase,
				textToAppendToTheForname, nbItemsWithId, date6, dateTime, dates, uppercaseNameList,
				textToAppendToTheFornameWithId, input, nbItemsWithoutId, inputList, textToAppendToTheFornameWithoutId);

		// Verification
		assertNotNull(allFieldCases);
		assertEquals(uuid, allFieldCases.getId());
		assertEquals("a name", allFieldCases.getName());
		assertEquals(666, allFieldCases.getAge());
		//
		// The lists are generated by the server (and doesn't match the given list in the input parameter, by GraphQL
		// design)
		assertNotNull(allFieldCases.getAliases());
		assertEquals(10, allFieldCases.getAliases().size());
		//
		assertEquals(date1, allFieldCases.getDate());
		//
		// The lists are generated by the server (and doesn't match the given list in the input parameter, by GraphQL
		// design)
		assertNotNull(allFieldCases.getDates());
		assertEquals(2, allFieldCases.getDates().size());
		assertEquals(date2, allFieldCases.getDates().get(0));
		assertEquals(date3, allFieldCases.getDates().get(1));
		//
		// The lists are generated by the server (and doesn't match the given list in the input parameter, by GraphQL
		// design)
		assertNotNull(allFieldCases.getPlanets());
		assertEquals(10, allFieldCases.getPlanets().size());
		// assertEquals("planet1", allFieldCases.getPlanets().get(0));
		// assertEquals("planet2", allFieldCases.getPlanets().get(1));
		//
		assertEquals(dateTime, allFieldCases.getDateTime());

		// listWithIdSubTypes
		assertEquals(nbItemsWithId, allFieldCases.getListWithIdSubTypes().size());
		assertTrue(allFieldCases.getListWithIdSubTypes().get(0).getName().endsWith(textToAppendToTheFornameWithId));
		// listWithoutIdSubTypes
		assertEquals(nbItemsWithoutId, allFieldCases.getListWithoutIdSubTypes().size());
		assertTrue(
				allFieldCases.getListWithoutIdSubTypes().get(0).getName().endsWith(textToAppendToTheFornameWithoutId));
	}

	@Execution(ExecutionMode.CONCURRENT)
	@Test
	void test_error() {

		GraphQLRequestExecutionException e = assertThrows(GraphQLRequestExecutionException.class,
				() -> this.partialQueries.error("This is an expected error"));
		assertTrue(e.getMessage().contains("This is an expected error"),
				"'" + e.getMessage() + "' should contain 'This is an expected error'");
	}

	@Execution(ExecutionMode.CONCURRENT)
	@Test
	void test_aBreak() throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		assertEquals(CEP_extends_CES.FLOAT, this.partialQueries.aBreak(CEP_extends_CES.FLOAT, null).getCase());
		assertEquals(CEP_extends_CES.DOUBLE, this.partialQueries.aBreak(CEP_extends_CES.DOUBLE, null).getCase());
	}

	public static void checkCharacter(CIP_Character_CIS c, String testDecription, boolean idShouldBeNull,
			String nameStartsWith, int nbFriends, int nbAppearsIn) {

		if (idShouldBeNull)
			assertNull(c.getId(), testDecription + " (id)");
		else
			assertNotNull(c.getId(), testDecription + " (id)");

		assertTrue(c.getName().startsWith(nameStartsWith),
				testDecription + " (name starts with " + nameStartsWith + ")");

		// nbFriends is the number of friends... before any call to addFriend
		if (nbFriends == 0) {
			// c.getFriends() may be null
			if (c.getFriends() != null) {
				assertTrue(c.getFriends().size() >= 0, testDecription + " (friends)");
			}
		} else {
			assertTrue(c.getFriends().size() >= nbFriends, testDecription + " (friends)");
			for (CIP_Character_CIS friend : c.getFriends()) {
				// Expected fields: id and name
				assertNotNull(friend.getId());
				assertNotNull(friend.getName());
				assertNull(friend.getAppearsIn());
			}
		}

		// nbEpisodes is the expected number of episodes
		if (nbAppearsIn == 0) {
			// c.getAppearsIn() may be null
			if (c.getAppearsIn() != null) {
				assertTrue(c.getAppearsIn().size() >= 0, testDecription + " (getAppearsIn)");
			}
		} else {
			assertTrue(c.getAppearsIn().size() >= nbAppearsIn, testDecription + " (getAppearsIn)");
		}

	}
}
