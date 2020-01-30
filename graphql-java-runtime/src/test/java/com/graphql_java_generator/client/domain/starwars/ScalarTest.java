package com.graphql_java_generator.client.domain.starwars;

import com.graphql_java_generator.annotation.GraphQLScalar;

public class ScalarTest {

	@GraphQLScalar(graphQLTypeName = "Episode", javaClass = Episode.class)
	Episode episode;

}
