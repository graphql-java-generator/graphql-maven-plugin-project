package graphql.mavenplugin;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import graphql.mavenplugin.test.helper.AllGraphQLCasesSpringConfiguration;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { AllGraphQLCasesSpringConfiguration.class })
class AllGraphQLCasesTest extends AbstractIntegrationTest {

	public AllGraphQLCasesTest() {
		super("/allGraphQLCases.graphqls");
	}

}
