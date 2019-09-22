/**
 * 
 */
package graphql.mavenplugin_notscannedbyspring;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.graphql_java_generator.plugin.PluginConfiguration;
import com.graphql_java_generator.plugin.PluginMode;
import com.graphql_java_generator.plugin.ResourceSchemaStringProvider;
import com.graphql_java_generator.plugin.test.helper.MavenTestHelper;
import com.graphql_java_generator.plugin.test.helper.PluginConfigurationTestHelper;

import graphql.language.Document;
import graphql.parser.Parser;

/**
 * The Spring configuration used for JUnit tests. To use tit, just create a subclass, and provide the schemaFilePattern
 * to its constructor
 * 
 * @author EtienneSF
 */
@Configuration
@Import({ JacksonAutoConfiguration.class })
public abstract class AbstractSpringConfiguration {

	private final static String BASE_PACKAGE = "org.graphql.mavenplugin.junittest";
	private final static String ENCODING = "UTF-8";

	/** Logger pour cette classe */
	protected Logger logger = LogManager.getLogger();
	private final String schemaFilePattern;

	private PluginMode mode;
	private String schemaPersonalizationFilename = PluginConfiguration.DEFAULT_SCHEMA_PERSONALIZATION_FILE;

	@Resource
	MavenTestHelper mavenTestHelper;

	protected AbstractSpringConfiguration(String schemaFilePattern, PluginMode mode) {
		this.schemaFilePattern = schemaFilePattern;
		this.mode = mode;
	}

	protected AbstractSpringConfiguration(String schemaFilePattern, PluginMode mode,
			String schemaPersonalizationFilename) {
		this.schemaFilePattern = schemaFilePattern;
		this.mode = mode;
		this.schemaPersonalizationFilename = schemaPersonalizationFilename;
	}

	@Bean
	PluginConfiguration pluginConfigurationTestHelper(MavenTestHelper mavenTestHelper) {
		PluginConfigurationTestHelper pluginConfigurationTestHelper = new PluginConfigurationTestHelper(this);
		pluginConfigurationTestHelper.mainResourcesFolder = new File(mavenTestHelper.getModulePathFile(),
				"/src/test/resources");

		String classname = this.getClass().getSimpleName();
		int firstDollar = classname.indexOf('$');
		pluginConfigurationTestHelper.packageName = BASE_PACKAGE + "."
				+ classname.substring(0, firstDollar).toLowerCase();

		pluginConfigurationTestHelper.sourceEncoding = ENCODING;
		pluginConfigurationTestHelper.mode = mode;
		pluginConfigurationTestHelper.schemaFilePattern = schemaFilePattern;
		pluginConfigurationTestHelper.schemaPersonalizationFile = (PluginConfiguration.DEFAULT_SCHEMA_PERSONALIZATION_FILE
				.equals(schemaPersonalizationFilename)) ? null
						: new File(mavenTestHelper.getModulePathFile(), schemaPersonalizationFilename);
		pluginConfigurationTestHelper.targetSourceFolder = mavenTestHelper.getTargetSourceFolder(
				(classname.contains("$")) ? classname = classname.substring(0, classname.indexOf('$')) : classname);
		pluginConfigurationTestHelper.targetClassFolder = new File(
				pluginConfigurationTestHelper.targetSourceFolder.getParentFile(), "compilation_test");

		return pluginConfigurationTestHelper;
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
