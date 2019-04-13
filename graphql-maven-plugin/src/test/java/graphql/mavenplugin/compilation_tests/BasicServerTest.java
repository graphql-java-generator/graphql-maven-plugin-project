package graphql.mavenplugin.compilation_tests;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import graphql.mavenplugin_notscannedbyspring.Basic_Server_SpringConfiguration;

@SpringJUnitConfig(classes = { Basic_Server_SpringConfiguration.class })
class BasicServerTest extends AbstractIntegrationTest {

	// Everything is in the AbstractIntegrationTest class.

	// The only aim of this class, is ti have its own Spring Configuration (Basic_Server_SpringConfiguration)

	@BeforeEach
	public void setUp() {
		graphqlTestHelper.checkSchemaStringProvider("basic.graphqls");
	}

}
