/**
 * 
 */
package generate_relay_schema.mavenplugin_notscannedbyspring;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

import com.graphql_java_generator.plugin.ResourceSchemaStringProvider;
import com.graphql_java_generator.plugin.test.helper.GenerateRelaySchemaConfigurationTestHelper;
import com.graphql_java_generator.plugin.test.helper.MavenTestHelper;

import graphql.language.Document;
import graphql.mavenplugin_notscannedbyspring.Forum_Client_SpringConfiguration;
import graphql.parser.Parser;

/**
 * The Spring configuration used for JUnit tests. To use tit, just create a subclass, and provide the schemaFilePattern
 * to its constructor
 * 
 * @author etienne-sf
 */
public abstract class AbstractSpringConfiguration {

	public final static String ROOT_UNIT_TEST_FOLDER = "target/junittest_generate-relay-schema/";
	public final static String ENCODING = "UTF-8";

	private final String schemaFilePattern;

	@Resource
	MavenTestHelper mavenTestHelper;
	@Autowired
	GenerateRelaySchemaConfigurationTestHelper configuration;

	protected AbstractSpringConfiguration(String schemaFilePattern) {
		this.schemaFilePattern = schemaFilePattern;
	}

	@Bean
	GenerateRelaySchemaConfigurationTestHelper graphQLConfigurationTestHelper() {
		GenerateRelaySchemaConfigurationTestHelper configuration = new GenerateRelaySchemaConfigurationTestHelper(this);
		configuration.schemaFileFolder = new File(mavenTestHelper.getModulePathFile(), "src/test/resources");
		configuration.schemaFileName = schemaFilePattern;
		configuration.schemaFilePattern = schemaFilePattern;
		configuration.resourceEncoding = ENCODING;
		configuration.targetFolder = new File(mavenTestHelper.getModulePathFile(),
				ROOT_UNIT_TEST_FOLDER + Forum_Client_SpringConfiguration.class.getSimpleName());
		return configuration;
	}

	/**
	 * Loads the schema from the graphqls files. This method uses the {@link GraphQLJavaToolsAutoConfiguration} from the
	 * 
	 * project, to load the schema from the graphqls files
	 * 
	 * @throws IOException
	 */
	@Bean
	public List<Document> documents(ResourceSchemaStringProvider schemaStringProvider) throws IOException {
		Parser parser = new Parser();
		return schemaStringProvider.schemaStrings().stream().map(parser::parseDocument).collect(Collectors.toList());
	}
}
