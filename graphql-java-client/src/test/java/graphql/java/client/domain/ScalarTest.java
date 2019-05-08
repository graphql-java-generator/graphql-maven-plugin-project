package graphql.java.client.domain;

import graphql.java.client.Scalar;

public class ScalarTest {

	@Scalar(graphqlType = Episode.class)
	Episode episode;

}
