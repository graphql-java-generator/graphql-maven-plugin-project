package graphql.java.client.domain.starwars;

import graphql.java.annotation.GraphQLScalar;

public class ScalarTest {

	@GraphQLScalar(graphqlType = Episode.class)
	Episode episode;

}
