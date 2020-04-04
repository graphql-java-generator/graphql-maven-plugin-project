package com.graphql_java_generator.client.request;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.graphql_java_generator.client.GraphQLConfiguration;
import com.graphql_java_generator.client.domain.allGraphQLCases.AnotherMutationType;
import com.graphql_java_generator.client.domain.allGraphQLCases.Episode;
import com.graphql_java_generator.client.domain.allGraphQLCases.GraphQLRequest;
import com.graphql_java_generator.client.domain.allGraphQLCases.HumanInput;
import com.graphql_java_generator.client.domain.allGraphQLCases.MyQueryType;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;

class AbstractGraphQLRequestTest_fragment {

	HumanInput input;
	Map<String, Object> params = new HashMap<>();

	@BeforeEach
	void setup() {
		// Default configuration for GraphQLRequest
		GraphQLRequest.setStaticConfiguration(new GraphQLConfiguration("http://localhost"));

		// A useful init for some tests
		input = new HumanInput();
		input.setName("a new name");
		List<Episode> episodes = new ArrayList<>();
		episodes.add(Episode.JEDI);
		episodes.add(Episode.EMPIRE);
		episodes.add(Episode.NEWHOPE);
		input.setAppearsIn(episodes);

		params.put("humanInput", input);
		params.put("value", "the mutation value");
		params.put("anotherValue", "the other mutation value");
		params.put("uppercaseFalse", false);
		params.put("uppercaseTrue", true);
	}

	@Test
	void testBuild_ThreeGlobalFragments() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		// Go, go, go
		MyQueryType queryType = new MyQueryType("http://localhost");
		AbstractGraphQLRequest graphQLRequest = queryType.getGraphQLRequest(//
				""//
						+ "query{withoutParameters{appearsIn ...fragment1}} " //
						//
						+ "fragment fragment1 on Character {"//
						+ "   id appearsIn friends{id ...fragment3 ...fragment2 }"//
						+ "}"//
						//
						+ "fragment fragment2 on Character {"//
						+ "   id name(uppercase: &uppercaseTrue) "//
						+ "}"// //
						+ "fragment fragment3 on Character { appearsIn }"//
		);

		// Verification
		assertEquals(1, graphQLRequest.query.fields.size());
		QueryField withoutParameters = graphQLRequest.query.fields.get(0);
		assertEquals("withoutParameters", withoutParameters.name);
		assertEquals(1, withoutParameters.fields.size(), "no __typename as there is a fragment");
		//
		assertEquals("appearsIn", withoutParameters.fields.get(0).name);
		assertEquals("Episode", withoutParameters.fields.get(0).clazz.getSimpleName());
		assertEquals(0, withoutParameters.fields.get(0).inputParameters.size());
		// Nb fragments
		assertEquals(1, withoutParameters.fragments.size());
		assertEquals("fragment1", withoutParameters.fragments.get(0));

		assertEquals(3, graphQLRequest.fragments.size());
		//
		// Fragment 1
		//
		QueryField content1 = graphQLRequest.fragments.get(0).content;
		assertEquals("Character", content1.clazz.getSimpleName());
		assertEquals(4, content1.fields.size(), "with __typename");
		int i = 0;
		assertEquals("id", content1.fields.get(i++).getName());
		assertEquals("appearsIn", content1.fields.get(i++).name);
		assertEquals("friends", content1.fields.get(i++).name);
		assertEquals("__typename", content1.fields.get(i++).name);
		//
		QueryField friends = content1.fields.get(2);
		assertEquals("friends", friends.name);
		assertEquals(1, friends.fields.size(), "no __typename as there is a fragment");
		i = 0;
		assertEquals("id", friends.fields.get(i++).name);
		assertEquals(2, friends.fragments.size());
		assertEquals("fragment3", friends.fragments.get(0));
		assertEquals("fragment2", friends.fragments.get(1));
		//
		// Fragment 2
		//
		// + "fragment fragment2 on Character {"//
		// + " id name "//
		// + "}"// //
		//
		QueryField content2 = graphQLRequest.fragments.get(1).content;
		assertEquals("Character", content2.clazz.getSimpleName());
		assertEquals(3, content2.fields.size(), "with __typename");
		i = 0;
		assertEquals("id", content2.fields.get(i++).name);
		assertEquals("name", content2.fields.get(i++).name);
		assertEquals("__typename", content2.fields.get(i++).name);
		//
		// Fragment 3
		//
		// fragment fragment3 on Character { appearsIn }
		//
		QueryField content3 = graphQLRequest.fragments.get(2).content;
		assertEquals("Character", content3.clazz.getSimpleName());
		assertEquals(2, content3.fields.size(), "with __typename");
		i = 0;
		assertEquals("appearsIn", content3.fields.get(i++).name);
		assertEquals("__typename", content3.fields.get(i++).name);

		assertEquals("{\"query\":\""//
				+ "fragment fragment1 on Character{id appearsIn friends{id ...fragment3 ...fragment2} __typename}"
				+ "fragment fragment2 on Character{id name(uppercase:true) __typename}"//
				+ "fragment fragment3 on Character{appearsIn __typename}" //
				+ "query{withoutParameters{appearsIn ...fragment1}}"//
				+ "\",\"variables\":null,\"operationName\":null}", //
				graphQLRequest.buildRequest(params));
	}

	@Test
	void testBuild_TwoInlineFragments() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		int i = 0;

		// Go, go, go
		MyQueryType queryType = new MyQueryType("http://localhost");
		AbstractGraphQLRequest graphQLRequest = queryType.getGraphQLRequest(//
				""//
						+ "query{" //
						+ "  withoutParameters{"//
						+ "    appearsIn " //
						+ "    ...id " //
						+ "    ... on Character { ...id friends { ...id }} " //
						+ "    ... on Droid {  primaryFunction ... on Character {name(uppercase: ?uppercaseFalse) friends {name}}  } " //
						+ "    ... on Human {  homePlanet ... on Human { ... on Character  { name(uppercase: ?notDefinedBindVariable)}} } " //
						+ "  } "//
						+ "} " //
						+ "fragment id on Character {id} "//
		);

		// Verification
		assertEquals(1, graphQLRequest.query.fields.size());
		//
		QueryField withoutParameters = graphQLRequest.query.fields.get(0);
		assertEquals("withoutParameters", withoutParameters.name);
		assertEquals(1, withoutParameters.fields.size(), "no __typename as there is a fragment");
		//
		assertEquals("appearsIn", withoutParameters.fields.get(0).name);
		//
		// Nb fragments
		assertEquals(1, withoutParameters.fragments.size());
		assertEquals("id", withoutParameters.fragments.get(0));
		//
		// NB inline fragments
		assertEquals(3, withoutParameters.inlineFragments.size());
		//
		// inline fragment Character
		Fragment fragmentCharacter = withoutParameters.inlineFragments.get(0);
		assertEquals(null, fragmentCharacter.name);
		assertEquals("Character", fragmentCharacter.content.clazz.getSimpleName());
		assertEquals(1, fragmentCharacter.content.fields.size(), "no __typename as there is a fragment");
		i = 0;
		assertEquals("friends", fragmentCharacter.content.fields.get(i++).name);
		assertEquals(1, fragmentCharacter.content.fragments.size());
		assertEquals("id", fragmentCharacter.content.fragments.get(0));
		assertEquals(0, fragmentCharacter.content.inlineFragments.size());
		// fragmentCharacter.friends
		assertEquals(1, fragmentCharacter.content.fields.get(0).fragments.size());
		assertEquals("id", fragmentCharacter.content.fields.get(0).fragments.get(0));
		assertEquals(0, fragmentCharacter.content.fields.get(0).inlineFragments.size());
		//
		// inline fragment Droid
		Fragment fragmentDroid = withoutParameters.inlineFragments.get(1);
		assertEquals(null, fragmentDroid.name);
		assertEquals("Droid", fragmentDroid.content.clazz.getSimpleName());
		assertEquals(1, fragmentDroid.content.fields.size(), "no __typename as there is a fragment");
		i = 0;
		assertEquals("primaryFunction", fragmentDroid.content.fields.get(i++).name);
		assertEquals(0, fragmentDroid.content.fragments.size());
		assertEquals(1, fragmentDroid.content.inlineFragments.size());
		// fragmentDroid.inline
		assertEquals("Character", fragmentDroid.content.inlineFragments.get(0).content.clazz.getSimpleName());
		assertEquals("name", fragmentDroid.content.inlineFragments.get(0).content.fields.get(0).name);
		assertEquals("friends", fragmentDroid.content.inlineFragments.get(0).content.fields.get(1).name);
		assertEquals("__typename", fragmentDroid.content.inlineFragments.get(0).content.fields.get(2).name);
		assertEquals(1, fragmentDroid.content.inlineFragments.get(0).content.fields.get(0).inputParameters.size());
		InputParameter name = fragmentDroid.content.inlineFragments.get(0).content.fields.get(0).inputParameters.get(0);
		assertEquals("uppercase", name.name);
		assertEquals("Boolean", name.graphQLCustomScalarType.getName());
		assertEquals(null, name.value);
		assertEquals("uppercaseFalse", name.bindParameterName);
		//
		// inline fragment Human
		Fragment fragmentHuman = withoutParameters.inlineFragments.get(2);
		assertEquals(null, fragmentHuman.name);
		assertEquals("Human", fragmentHuman.content.clazz.getSimpleName());
		assertEquals(1, fragmentHuman.content.fields.size(), "no __typename as there is a fragment");
		i = 0;
		assertEquals("homePlanet", fragmentHuman.content.fields.get(i++).name);
		assertEquals(0, fragmentHuman.content.fragments.size());
		assertEquals(1, fragmentHuman.content.inlineFragments.size());
		// fragmentHuman.inline
		assertEquals("Human", fragmentHuman.content.inlineFragments.get(0).content.clazz.getSimpleName());
		assertEquals(0, fragmentHuman.content.inlineFragments.get(0).content.fields.size());
		assertEquals(0, fragmentHuman.content.inlineFragments.get(0).content.fragments.size());
		assertEquals(1, fragmentHuman.content.inlineFragments.get(0).content.inlineFragments.size());
		// No field or fragment for the inline fragment, but one new inline fragment
		Fragment inlineInlineHuman = fragmentHuman.content.inlineFragments.get(0).content.inlineFragments.get(0);
		assertEquals("name", inlineInlineHuman.content.fields.get(0).name);
		assertEquals(1, inlineInlineHuman.content.fields.get(0).inputParameters.size());
		assertEquals("uppercase", inlineInlineHuman.content.fields.get(0).inputParameters.get(0).name);
		assertEquals("Boolean",
				inlineInlineHuman.content.fields.get(0).inputParameters.get(0).graphQLCustomScalarType.getName());
		assertEquals(null, inlineInlineHuman.content.fields.get(0).inputParameters.get(0).value);
		assertEquals("notDefinedBindVariable",
				inlineInlineHuman.content.fields.get(0).inputParameters.get(0).bindParameterName);

		// + "query{" //
		// + " withoutParameters{"//
		// + " appearsIn " //
		// + " ...id " //
		// + " ... on Character { ...id friends { ...id }} " //
		// + " ... on Droid { primaryFunction ... on Character {name(uppercase: ?uppercaseFalse) friends {name}} } " //
		// + " ... on Human { homePlanet ... on Human {name(uppercase: ?notDefinedBindVariable)} } " //
		// + " } "//
		// + "} " //
		// + "fragment id on Character {id} "//

		assertEquals("{\"query\":\""//
				+ "fragment id on Character{id __typename}" //
				+ "query{" //
				+ "withoutParameters{appearsIn ...id" //
				+ " ... on Character{friends{...id} ...id}" //
				+ " ... on Droid{primaryFunction ... on Character{name(uppercase:false) friends{name __typename} __typename}}" //
				+ " ... on Human{homePlanet ... on Human{... on Character{name __typename}}}" //
				+ "}"//
				+ "}"//
				+ "\",\"variables\":null,\"operationName\":null}", //
				graphQLRequest.buildRequest(params));
	}

	@Test
	void testBuild_Full_createHuman_withBuilder()
			throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {
		// Preparation
		AnotherMutationType mutationType = new AnotherMutationType("http://localhost/graphql");

		// Go, go, go
		AbstractGraphQLRequest graphQLRequest = mutationType.getResponseBuilder().withQueryResponseDef(//
				"" //
						+ "mutation { createHuman (human: &humanInput) @testDirective(value:&value, anotherValue:?anotherValue)   "//
						+ "{...character}}"//
						+ " fragment character on Character {id name appearsIn friends {id name}}")
				.build();

		// Verification
		assertEquals("{\"query\":\"" //
				+ "fragment character on Character{id name appearsIn friends{id name __typename} __typename}"
				+ "mutation{createHuman(human:{name:\\\"a new name\\\",appearsIn:[JEDI,EMPIRE,NEWHOPE]}) @testDirective(value:\\\"the mutation value\\\",anotherValue:\\\"the other mutation value\\\")"//
				+ "{...character}}" //
				+ "\",\"variables\":null,\"operationName\":null}", //
				graphQLRequest.buildRequest(params));
	}

	@Test
	void testBuild_Full_createHuman() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		// Go, go, go
		GraphQLRequest graphQLRequest = new GraphQLRequest(//
				"   fragment character \n \r \t on \n \r \t  Character { id name appearsIn friends {id name} }"
						+ "mutation {createHuman (human: &humanInput) @testDirective(value:&value, anotherValue:?anotherValue)   "//
						+ "{    ...character       }}"//
		);

		// Verification
		assertEquals("{\"query\":\"" //
				+ "fragment character on Character{id name appearsIn friends{id name __typename} __typename}"
				+ "mutation{createHuman(human:{name:\\\"a new name\\\",appearsIn:[JEDI,EMPIRE,NEWHOPE]}) @testDirective(value:\\\"the mutation value\\\",anotherValue:\\\"the other mutation value\\\")"//
				+ "{...character}}" //
				+ "\",\"variables\":null,\"operationName\":null}", //
				graphQLRequest.buildRequest(params));
		;
	}

	@Test
	void testBuild_Partial_createHuman() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		// Preparation
		AnotherMutationType mutationType = new AnotherMutationType("http://localhost/graphql");
		params = new HashMap<>();
		params.put("anotherMutationTypeCreateHumanHuman", input);
		params.put("value", "the mutation value");
		params.put("anotherValue", "the other mutation value");

		// Go, go, go
		AbstractGraphQLRequest graphQLRequest = mutationType.getCreateHumanResponseBuilder().withQueryResponseDef(
				"{id name ... on Human {friends {id name} appearsIn @testDirective(value:&value,anotherValue:?anotherValue)}}}")
				.build();

		// Verification
		assertEquals("{\"query\":\"mutation" //
				+ "{createHuman(human:{name:\\\"a new name\\\",appearsIn:[JEDI,EMPIRE,NEWHOPE]})"//
				+ "{id name ... on Human{friends{id name __typename} appearsIn @testDirective(value:\\\"the mutation value\\\",anotherValue:\\\"the other mutation value\\\") __typename}}}" //
				+ "\",\"variables\":null,\"operationName\":null}", //
				graphQLRequest.buildRequest(params));
	}

	@Test
	void testBuild_Partial_allFieldCases() throws GraphQLRequestPreparationException, GraphQLRequestExecutionException {

		// Preparation
		MyQueryType queryType = new MyQueryType("http://localhost/graphql");

		List<Date> dates = new ArrayList<>();
		dates.add(new GregorianCalendar(2022, 5 - 1, 1).getTime());
		dates.add(new GregorianCalendar(2022, 5 - 1, 2).getTime());
		dates.add(new GregorianCalendar(2022, 5 - 1, 3).getTime());

		params = new HashMap<>();
		params.put("uppercase", true);
		params.put("textToAppendToTheForname", "append");
		params.put("date", new GregorianCalendar(2022, 5 - 1, 20).getTime());
		params.put("dates", dates);
		params.put("uppercaseNameList", true);
		params.put("textToAppendToTheFornameWithId", "append2");
		params.put("nbItemsWithoutId", 69);
		params.put("textToAppendToTheFornameWithoutId", "append3");

		// Go, go, go
		AbstractGraphQLRequest allFieldCasesRequest = queryType
				.getAllFieldCasesGraphQLRequest("{ ... on WithID { id } name " //
						+ " forname(uppercase: ?uppercase, textToAppendToTheForname: ?textToAppendToTheForname) "
						+ " age nbComments  comments booleans aliases planets friends {id}" //
						+ " oneWithIdSubType {id name} "//
						+ " listWithIdSubTypes(nbItems: ?nbItemsWithId, date: ?date, dates: &dates, uppercaseName: ?uppercaseNameList, textToAppendToTheForname: ?textToAppendToTheFornameWithId) {name id}"
						+ " oneWithoutIdSubType(input: ?input) {name}"//
						+ " listWithoutIdSubTypes(nbItems: ?nbItemsWithoutId, input: ?inputList, textToAppendToTheForname: ?textToAppendToTheFornameWithoutId) {name}" //
						+ "}");

		// Verification
		assertEquals("{\"query\":\"query{allFieldCases" //
				+ "{name" //
				+ " forname(uppercase:true,textToAppendToTheForname:\\\"append\\\")"
				+ " age nbComments comments booleans aliases planets friends{id __typename}" //
				+ " oneWithIdSubType{id name __typename}"//
				+ " listWithIdSubTypes(date:\\\"2022-05-20\\\",dates:[\\\"2022-05-01\\\",\\\"2022-05-02\\\",\\\"2022-05-03\\\"],"
				+ "uppercaseName:true,textToAppendToTheForname:\\\"append2\\\"){name id __typename}"
				+ " oneWithoutIdSubType{name __typename}"//
				+ " listWithoutIdSubTypes(nbItems:69,textToAppendToTheForname:\\\"append3\\\"){name __typename}" //
				+ " ... on WithID{id __typename}" //
				+ "}}" //
				+ "\",\"variables\":null,\"operationName\":null}", //
				allFieldCasesRequest.buildRequest(params));
	}

}
