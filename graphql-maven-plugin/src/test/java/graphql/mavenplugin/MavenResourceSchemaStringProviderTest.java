package graphql.mavenplugin;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.List;

import org.apache.maven.project.MavenProject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import graphql.mavenplugin.test.helper.GraphqlTestHelper;
import graphql.mavenplugin.test.helper.MavenTestHelper;
import graphql.mavenplugin_notscannedbyspring.MavenResourceSchemaStringProviderTest_Server_SpringConfiguration;

@SpringJUnitConfig(classes = { MavenResourceSchemaStringProviderTest_Server_SpringConfiguration.class })
class MavenResourceSchemaStringProviderTest {

	@Autowired
	private MavenResourceSchemaStringProvider mavenResourceSchemaStringProvider;

	MavenProject project;

	@Autowired
	protected GraphqlTestHelper graphqlTestHelper;
	@Autowired
	protected MavenTestHelper mavenTestHelper;

	@BeforeEach
	void setUp() throws Exception {
		graphqlTestHelper.checkSchemaStringProvider("MavenResourceSchemaStringProviderTest/*.graphqls");
	}

	@Test
	void testSchemaStrings() throws IOException {
		// Preparation

		// Go, go, go
		List<String> strings = mavenResourceSchemaStringProvider.schemaStrings();

		// Verification
		assertEquals(2, strings.size(), "Nb schemas found");
		assertEquals(
				mavenTestHelper.readFile("/src/test/resources/MavenResourceSchemaStringProviderTest/file0.graphqls"),
				strings.get(0), "First file content");
		assertEquals(
				mavenTestHelper.readFile("/src/test/resources/MavenResourceSchemaStringProviderTest/file1.graphqls"),
				strings.get(1), "First file content");
	}

}
