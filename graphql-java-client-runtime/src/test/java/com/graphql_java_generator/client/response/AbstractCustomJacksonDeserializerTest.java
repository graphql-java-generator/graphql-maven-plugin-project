package com.graphql_java_generator.client.response;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.graphql_java_generator.annotation.GraphQLNonScalar;
import com.graphql_java_generator.annotation.GraphQLScalar;
import com.graphql_java_generator.domain.client.allGraphQLCases.Character;
import com.graphql_java_generator.domain.client.allGraphQLCases.Droid;
import com.graphql_java_generator.domain.client.allGraphQLCases.Episode;
import com.graphql_java_generator.domain.client.allGraphQLCases.Human;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.annotation.JsonSerialize;

class AbstractCustomJacksonDeserializerTest {

	/** A class to test the Jackson serialization and deserialization with the custom deserialize */
	public static class Test {

		@JsonDeserialize(using = CustomJacksonDeserializers.ListListCharacter.class)
		@JsonProperty("characters")
		@GraphQLNonScalar(fieldName = "friends", graphQLTypeSimpleName = "Human", javaClass = Character.class)
		List<List<Character>> listOfListOfCharacters;

		@JsonSerialize(contentUsing = CustomJacksonSerializers.Date.class)
		@JsonDeserialize(using = CustomJacksonDeserializers.ListDate.class)
		@JsonProperty("dates")
		@GraphQLScalar(fieldName = "dates", graphQLTypeSimpleName = "Date", javaClass = Date.class)
		List<Date> dates;

		@JsonDeserialize(using = CustomJacksonDeserializers.ListString.class)
		@JsonProperty("comments")
		@GraphQLScalar(fieldName = "comments", graphQLTypeSimpleName = "String", javaClass = String.class)
		List<String> comments;

		@JsonDeserialize(using = CustomJacksonDeserializers.ListBoolean.class)
		@JsonProperty("booleans")
		@GraphQLScalar(fieldName = "booleans", graphQLTypeSimpleName = "Boolean", javaClass = Boolean.class)
		List<Boolean> booleans;

		@JsonDeserialize(using = CustomJacksonDeserializers.ListHuman.class)
		@JsonProperty("friends")
		@GraphQLNonScalar(fieldName = "friends", graphQLTypeSimpleName = "Human", javaClass = Human.class)
		List<Human> friends;

		@JsonDeserialize(using = CustomJacksonDeserializers.ListListDouble.class)
		@JsonProperty("matrix")
		@GraphQLScalar(fieldName = "matrix", graphQLTypeSimpleName = "Float", javaClass = Double.class)
		List<List<Double>> matrix;

		@JsonDeserialize(using = CustomJacksonDeserializers.ListID.class)
		@JsonProperty("ids")
		@GraphQLScalar(fieldName = "ids", graphQLTypeSimpleName = "ID", javaClass = String.class)
		List<String> ids;

		public List<Date> getDates() {
			return dates;
		}

		public void setDates(List<Date> dates) {
			this.dates = dates;
		}

		public List<String> getComments() {
			return comments;
		}

		public void setComments(List<String> comments) {
			this.comments = comments;
		}

		public List<Boolean> getBooleans() {
			return booleans;
		}

		public void setBooleans(List<Boolean> booleans) {
			this.booleans = booleans;
		}

		public List<Human> getFriends() {
			return friends;
		}

		public void setFriends(List<Human> friends) {
			this.friends = friends;
		}

		public List<List<Character>> getListOfListOfCharacters() {
			return listOfListOfCharacters;
		}

		public void setListOfListOfCharacters(List<List<Character>> listOfListOfCharacters) {
			this.listOfListOfCharacters = listOfListOfCharacters;
		}

		public List<List<Double>> getMatrix() {
			return matrix;
		}

		public void setMatrix(List<List<Double>> matrix) {
			this.matrix = matrix;
		}
	}

	@org.junit.jupiter.api.Test
	void test_deserialize() throws JacksonException {
		// Preparation
		Test test = new Test();
		//
		List<Date> dates = new ArrayList<>();
		dates.add(new GregorianCalendar(2020, 12 - 1, 21).getTime());
		dates.add(new GregorianCalendar(2020, 12 - 1, 22).getTime());
		dates.add(new GregorianCalendar(2020, 12 - 1, 23).getTime());
		test.dates = dates;
		//
		List<String> comments = new ArrayList<>();
		comments.add("comment 1");
		comments.add("comment 2");
		test.comments = comments;
		//
		List<Boolean> booleans = new ArrayList<>();
		booleans.add(true);
		booleans.add(false);
		test.booleans = booleans;
		//
		List<Human> friends = new ArrayList<>();
		List<Character> friendsOfFirstHuman = new ArrayList<>();
		List<Episode> appearsIn = new ArrayList<>();
		appearsIn.add(Episode.EMPIRE);
		appearsIn.add(Episode.JEDI);
		friendsOfFirstHuman.add(Human.builder().withId("id friend 1").withName("friend 1").withNbComments(-1)
				.withAppearsIn(appearsIn).build());
		friendsOfFirstHuman.add(Droid.builder().withId("id friend 2").withName("friend 2").build());
		friends.add(Human.builder().withId("id1").withName("human 1").withNbComments(1).withFriends(friendsOfFirstHuman)
				.build());
		friends.add(Human.builder().withId("id2").withName("human 2").withNbComments(2).build());
		test.friends = friends;
		//
		List<List<Character>> listOfListOfCharacters = new ArrayList<>();
		listOfListOfCharacters.add(null); // An interesting test
		listOfListOfCharacters.add(new ArrayList<>()); // An empty list
		listOfListOfCharacters.add(friendsOfFirstHuman); // A non empty list
		test.listOfListOfCharacters = listOfListOfCharacters;
		//
		List<List<Double>> matrix = new ArrayList<>();
		List<Double> sublist = new ArrayList<>();
		sublist.add(-61.203425);
		sublist.add(0.2343257);
		matrix.add(sublist);
		List<Double> sublist2 = new ArrayList<>();
		sublist2.add(-61.203425 * 2);
		sublist2.add(0.2343257 * 2);
		sublist2.add(234.567);
		matrix.add(sublist2);
		test.matrix = matrix;
		//
		test.ids = new ArrayList<>();
		test.ids.add("11111111-1111-1111-1111-111111111111");
		test.ids.add("22222222-2222-2222-2222-222222222222");
		test.ids.add("33333333-3333-3333-3333-333333333333");

		String json = new ObjectMapper().writeValueAsString(test);

		// Go, go, go
		Test verify = new ObjectMapper().readValue(json, Test.class);

		// Verification
		assertEquals(3, verify.dates.size());
		assertTrue(verify.dates.get(0) instanceof Date);
		assertEquals(new GregorianCalendar(2020, 12 - 1, 21).getTime(), verify.dates.get(0));
		assertTrue(verify.dates.get(1) instanceof Date);
		assertEquals(new GregorianCalendar(2020, 12 - 1, 22).getTime(), verify.dates.get(1));
		assertTrue(verify.dates.get(2) instanceof Date);
		assertEquals(new GregorianCalendar(2020, 12 - 1, 23).getTime(), verify.dates.get(2));
		//
		assertEquals(2, verify.comments.size());
		assertEquals("comment 1", verify.comments.get(0));
		assertEquals("comment 2", verify.comments.get(1));
		//
		assertEquals(2, verify.booleans.size());
		assertEquals(true, verify.booleans.get(0));
		assertEquals(false, verify.booleans.get(1));
		//
		assertEquals(2, verify.friends.size());
		assertEquals(2, verify.friends.get(0).getFriends().size(), "embedded friends list");
		assertEquals("id friend 1", verify.friends.get(0).getFriends().get(0).getId(), "embedded list (id)");
		assertEquals(2, verify.friends.get(0).getFriends().get(0).getAppearsIn().size(),
				"embedded episodes (appearsIn)");
		assertEquals(Episode.EMPIRE, verify.friends.get(0).getFriends().get(0).getAppearsIn().get(0),
				"embedded episodes (appearsIn(0))");
		//
		assertEquals(3, verify.listOfListOfCharacters.size());
		assertEquals(null, verify.listOfListOfCharacters.get(0));
		assertEquals(0, verify.listOfListOfCharacters.get(1).size());
		assertEquals(2, verify.listOfListOfCharacters.get(2).size());
		//
		assertEquals(2, verify.matrix.size());
		//
		assertEquals(2, verify.matrix.get(0).size());
		assertEquals(-61.203425, verify.matrix.get(0).get(0));
		assertEquals(0.2343257, verify.matrix.get(0).get(1));
		//
		assertEquals(3, verify.matrix.get(1).size());
		assertEquals(-61.203425 * 2, verify.matrix.get(1).get(0));
		assertEquals(0.2343257 * 2, verify.matrix.get(1).get(1));
		assertEquals(234.567, verify.matrix.get(1).get(2));
		//
		assertEquals(3, test.ids.size());
		assertEquals("11111111-1111-1111-1111-111111111111", test.ids.get(0));
		assertEquals("22222222-2222-2222-2222-222222222222", test.ids.get(1));
		assertEquals("33333333-3333-3333-3333-333333333333", test.ids.get(2));
	}

}
