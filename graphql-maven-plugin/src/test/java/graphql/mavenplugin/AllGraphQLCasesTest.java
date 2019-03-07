package graphql.mavenplugin;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import graphql.mavenplugin_notscannedbyspring.AllGraphQLCasesSpringConfiguration;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { AllGraphQLCasesSpringConfiguration.class })
class AllGraphQLCasesTest extends AbstractIntegrationTest {

	// Everything is in the AbstractIntegrationTest class.

	// The only aim of this class, is ti have its own Spring Configuration (AllGraphQLCasesSpringConfiguration)
	@BeforeEach
	public void setUp() {
		graphqlTestHelper.checkSchemaStringProvider("allGraphQLCases.graphqls");
	}
}
