package com.graphql_java_generator.client.domain.starwars.scalar;

import com.graphql_java_generator.annotation.GraphQLScalar;
import com.graphql_java_generator.client.domain.starwars.Episode;

public class ScalarTest {

	@GraphQLScalar(list = false, fieldName = "episode", graphQLTypeName = "Episode", javaClass = Episode.class)
	Episode episode;

}
