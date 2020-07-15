/**
 * 
 */
package merge.mavenplugin_notscannedbyspring;

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

	private String schemaFileFolder = "src/test/resources";
	private String schemaFilePattern;
	private String schemaFileName;
	private String targetFolder;

	@Resource
	MavenTestHelper mavenTestHelper;
	@Autowired
	GenerateRelaySchemaConfigurationTestHelper configuration;

	protected AbstractSpringConfiguration(String schemaFileFolder, String schemaFilePattern, String schemaFileName,
			String targetFolder) {
		this.schemaFileFolder = schemaFileFolder;
		this.schemaFilePattern = schemaFilePattern;
		this.schemaFileName = schemaFileName;
		this.targetFolder = targetFolder;
	}

	@Bean
	GenerateRelaySchemaConfigurationTestHelper graphQLConfigurationTestHelper() {
		GenerateRelaySchemaConfigurationTestHelper configuration = new GenerateRelaySchemaConfigurationTestHelper(this);
		configuration.schemaFileFolder = new File(mavenTestHelper.getModulePathFile(), schemaFileFolder);
		configuration.schemaFilePattern = schemaFilePattern;
		configuration.schemaFileName = schemaFileName;
		configuration.resourceEncoding = ENCODING;
		File rootTargetFolder = new File(mavenTestHelper.getModulePathFile(), ROOT_UNIT_TEST_FOLDER);
		configuration.targetFolder = new File(rootTargetFolder, targetFolder);
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
