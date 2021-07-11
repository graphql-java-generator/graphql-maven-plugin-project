package org.allGraphQLCases.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.allGraphQLCases.SpringTestConfig;
import org.allGraphQLCases.client.AllFieldCases;
import org.allGraphQLCases.client.AllFieldCasesInput;
import org.allGraphQLCases.client.Character;
import org.allGraphQLCases.client.CharacterInput;
import org.allGraphQLCases.client.Droid;
import org.allGraphQLCases.client.Episode;
import org.allGraphQLCases.client.FieldParameterInput;
import org.allGraphQLCases.client.Human;
import org.allGraphQLCases.client._extends;
import org.allGraphQLCases.client.util.MyQueryTypeExecutor;
import org.allGraphQLCases.demo.PartialQueries;
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
abstract class AbstractIT {

	@Autowired
	MyQueryTypeExecutor queryType;

	@Autowired
	protected ApplicationContext ctx;

	PartialQueries partialQueries;

	@BeforeEach
	void setup() {
		partialQueries = getQueries();
		assertNotNull(partialQueries);
	}

	/** Get the class that will execute the queries. This is a particular class, for each test */
	protected abstract PartialQueries getQueries();

	@Execution(ExecutionMode.CONCURRENT)
	@Test
	void test_withoutParameters() throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		List<Character> list = partialQueries.withoutParameters();

		assertNotNull(list);
		assertEquals(10, list.size());
		for (Character c : list) {
			checkCharacter(c, "withoutParameters", true, "Random String (", 0, 0);
		}
	}

	@Execution(ExecutionMode.CONCURRENT)
	@Test
	void test_withOneOptionalParam() throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {

		// Without parameter
		Character c = partialQueries.withOneOptionalParam(null);
		checkCharacter(c, "test_withOneOptionalParam(null)", false, "Random String (", 0, 0);

		// With a parameter
		CharacterInput input = new CharacterInput();
		input.setName("A name");
		input.setAppearsIn(new ArrayList<Episode>());
		input.setType("Human");

		// Go, go, go
		c = partialQueries.withOneOptionalParam(input);
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
				() -> partialQueries.withOneMandatoryParam(null));
		assertTrue(e.getMessage().contains("character"));
	}

	@Execution(ExecutionMode.CONCURRENT)
	@Test
	void test_withOneMandatoryParam_OK() throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		// With a non null parameter
		CharacterInput input = new CharacterInput();
		input.setName("A name");
		input.setAppearsIn(new ArrayList<Episode>());
		input.setType("Droid");

		// Go, go, go
		Character c = partialQueries.withOneMandatoryParam(input);

		// Verification
		assertEquals("Droid", c.getClass().getSimpleName());
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
		Character c = partialQueries.withEnum(null);
		assertEquals(Episode.NEWHOPE.name(), c.getName());// See server code for more info

		// With a non null parameter
		c = partialQueries.withEnum(Episode.JEDI);
		assertEquals(Episode.JEDI.name(), c.getName()); // See server code for more info
	}

	@Execution(ExecutionMode.CONCURRENT)
	@Test
	void test_withList() throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		// Preparation
		CharacterInput ci1 = new CharacterInput();
		ci1.setName("A name");
		ci1.setAppearsIn(new ArrayList<Episode>());
		ci1.setType("Droid");
		//
		CharacterInput ci2 = new CharacterInput();
		ci2.setName("Another name");
		ci2.setAppearsIn(new ArrayList<Episode>());
		ci2.setType("Human");
		//
		List<CharacterInput> list = new ArrayList<CharacterInput>();
		list.add(ci1);
		list.add(ci2);
		//
		String firstName = "A first name";

		// Go, go, go
		List<Character> ret = partialQueries.withList(firstName, list);

		// Verification
		assertEquals(2, ret.size());
		//
		int i = 0;
		assertNotNull(ret.get(i).getId());
		assertEquals(firstName, ret.get(i).getName());
		assertTrue(ret.get(i) instanceof Droid);
		//
		i += 1;
		assertNotNull(ret.get(i).getId());
		assertEquals("Another name", ret.get(i).getName());
		assertTrue(ret.get(i) instanceof Human);
	}

	@Execution(ExecutionMode.CONCURRENT)
	@Test
	void test_allFieldCases() throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {

		// Preparation
		AllFieldCasesInput allFieldCasesInput = null;
		Boolean uppercase = true;
		String textToAppendToTheForname = "textToAppendToTheForname";
		long nbItemsWithId = 3;
		@SuppressWarnings("deprecation")
		Date date = new Date(2000 - 1900, 12 - 1, 20);
		@SuppressWarnings("deprecation")
		Date date2 = new Date(2000 - 1900, 12 - 1, 21);
		List<Date> dates = new ArrayList<>();
		dates.add(date);
		dates.add(date2);
		Boolean uppercaseNameList = null;
		String textToAppendToTheFornameWithId = "textToAppendToTheFornameWithId";
		FieldParameterInput input = new FieldParameterInput();
		input.setUppercase(true);
		int nbItemsWithoutId = 6;
		FieldParameterInput inputList = null;
		String textToAppendToTheFornameWithoutId = "textToAppendToTheFornameWithoutId";

		// Go, go, go
		AllFieldCases allFieldCases = partialQueries.allFieldCases(allFieldCasesInput, uppercase,
				textToAppendToTheForname, nbItemsWithId, date, dates, uppercaseNameList, textToAppendToTheFornameWithId,
				input, nbItemsWithoutId, inputList, textToAppendToTheFornameWithoutId);

		// Verification
		assertNotNull(allFieldCases);
		assertNotNull(allFieldCases.getId());

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
				() -> partialQueries.error("This is an expected error"));
		assertTrue(e.getMessage().contains("This is an expected error"),
				"'" + e.getMessage() + "' should contain 'This is an expected error'");
	}

	@Execution(ExecutionMode.CONCURRENT)
	@Test
	void test_aBreak() throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		assertEquals(_extends.FLOAT, partialQueries.aBreak(_extends.FLOAT, null).getCase());
		assertEquals(_extends.DOUBLE, partialQueries.aBreak(_extends.DOUBLE, null).getCase());
	}

	private void checkCharacter(Character c, String testDecription, boolean idShouldBeNull, String nameStartsWith,
			int nbFriends, int nbAppearsIn) {

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
			for (Character friend : c.getFriends()) {
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
