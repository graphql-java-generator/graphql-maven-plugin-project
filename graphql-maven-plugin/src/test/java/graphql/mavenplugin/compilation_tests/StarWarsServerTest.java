package graphql.mavenplugin.compilation_tests;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import graphql.mavenplugin_notscannedbyspring.StarWars_Server_SpringConfiguration;

@SpringJUnitConfig(classes = { StarWars_Server_SpringConfiguration.class })
class StarWarsServerTest extends AbstractIntegrationTest {

	// Everything is in the AbstractIntegrationTest class.

	// The only aim of this class, is to have its own Spring Configuration (Forum_Server_SpringConfiguration)

	@BeforeEach
	public void setUp() {
		graphqlTestHelper.checkSchemaStringProvider("starWarsSchema.graphqls");
	}

}
