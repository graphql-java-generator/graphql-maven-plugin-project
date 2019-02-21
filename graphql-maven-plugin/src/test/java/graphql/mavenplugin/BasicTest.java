package graphql.mavenplugin;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import graphql.mavenplugin.test.helper.BasicSpringConfiguration;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { BasicSpringConfiguration.class })
class BasicTest extends AbstractIntegrationTest {

	public BasicTest() {
		super("/basic.graphqls");
	}

}
