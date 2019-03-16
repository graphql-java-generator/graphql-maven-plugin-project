package graphql.java.client;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GraphqlUtilsTest {

	GraphqlUtils graphqlUtils;

	@BeforeEach
	void setUp() throws Exception {
		graphqlUtils = new GraphqlUtils();
	}

	@Test
	void testCheckName() {
		// Some valid name: we call check, and no exception should be thrown
		graphqlUtils.checkName("avalidname");
		graphqlUtils.checkName("aValidName");
		graphqlUtils.checkName("_aValidName");
		graphqlUtils.checkName("ValidName");

		// Various types of checks KO
		assertThrows(IllegalArgumentException.class, () -> graphqlUtils.checkName("qdqd qdsq"));
		assertThrows(IllegalArgumentException.class, () -> graphqlUtils.checkName("qdqd.qdsq"));
		assertThrows(IllegalArgumentException.class, () -> graphqlUtils.checkName("qdqdqdsq."));
		assertThrows(IllegalArgumentException.class, () -> graphqlUtils.checkName(".qdqdqdsq"));
		assertThrows(IllegalArgumentException.class, () -> graphqlUtils.checkName("qdqdqdsq*"));
		assertThrows(IllegalArgumentException.class, () -> graphqlUtils.checkName("qdqdqdsèq"));
	}

}
