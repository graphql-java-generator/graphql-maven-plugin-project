package com.graphql_java_generator.domain.client.starwars.scalar;

import com.graphql_java_generator.annotation.GraphQLScalar;
import com.graphql_java_generator.domain.client.allGraphQLCases.Episode;

public class ScalarTest {

	@GraphQLScalar(fieldName = "episode", graphQLTypeSimpleName = "Episode", javaClass = Episode.class)
	Episode episode;

}
