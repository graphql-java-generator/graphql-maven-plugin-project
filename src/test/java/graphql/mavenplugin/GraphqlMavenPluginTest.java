package graphql.mavenplugin;

import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author EtienneSF
 */
class GraphqlMavenPluginTest {

	GraphqlMavenPlugin graphqlMavenPlugin;

	@BeforeEach
	void setUp() throws Exception {
		graphqlMavenPlugin = new GraphqlMavenPlugin();
	}

	@Test
	void testGenerateTargetFiles() {
		fail("Not yet implemented");
	}

}
