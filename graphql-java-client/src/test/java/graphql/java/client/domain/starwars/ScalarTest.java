package graphql.java.client.domain;

import graphql.java.client.annotation.GraphQLScalar;

public class ScalarTest {

	@GraphQLScalar(graphqlType = Episode.class)
	Episode episode;

}
