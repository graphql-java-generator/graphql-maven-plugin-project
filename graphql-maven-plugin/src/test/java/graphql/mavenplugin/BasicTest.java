package graphql.mavenplugin;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import graphql.mavenplugin_notscannedbyspring.BasicSpringConfiguration;

@SpringJUnitConfig(classes = { BasicSpringConfiguration.class })
class BasicTest extends AbstractIntegrationTest {

	// Everything is in the AbstractIntegrationTest class.

	// The only aim of this class, is ti have its own Spring Configuration (BasicSpringConfiguration)

	@BeforeEach
	public void setUp() {
		graphqlTestHelper.checkSchemaStringProvider("basic.graphqls");
	}

}
