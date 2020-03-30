/**
 * 
 */
package graphql.mavenplugin_notscannedbyspring;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.graphql_java_generator.plugin.CustomScalarDefinition;
import com.graphql_java_generator.plugin.PluginConfiguration;
import com.graphql_java_generator.plugin.PluginMode;
import com.graphql_java_generator.plugin.ResourceSchemaStringProvider;
import com.graphql_java_generator.plugin.test.helper.MavenTestHelper;
import com.graphql_java_generator.plugin.test.helper.PluginConfigurationTestHelper;

import graphql.language.Document;
import graphql.parser.Parser;
import lombok.Getter;
import lombok.Setter;

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
	private final String schemaFilePattern;
	
	@Getter
	@Setter
	private String schemaFileSubFolder;
	
	private PluginMode mode;
	private String schemaPersonalizationFilename = PluginConfiguration.DEFAULT_SCHEMA_PERSONALIZATION_FILE;
	private List<CustomScalarDefinition> customScalars = null;

	@Resource
	MavenTestHelper mavenTestHelper;

	protected AbstractSpringConfiguration(String schemaFilePattern, PluginMode mode) {
		this.schemaFilePattern = schemaFilePattern;
		this.mode = mode;
	}

	protected AbstractSpringConfiguration(String schemaFilePattern, PluginMode mode,
			String schemaPersonalizationFilename, List<CustomScalarDefinition> customScalars) {
		this.schemaFilePattern = schemaFilePattern;
		this.mode = mode;
		this.schemaPersonalizationFilename = schemaPersonalizationFilename;
		this.customScalars = customScalars;
	}

	protected AbstractSpringConfiguration(String schemaFilePattern, PluginMode mode,
			List<CustomScalarDefinition> customScalars) {
		this.schemaFilePattern = schemaFilePattern;
		this.mode = mode;
		this.customScalars = customScalars;
	}

	@Bean
	PluginConfiguration pluginConfigurationTestHelper(MavenTestHelper mavenTestHelper) {
		PluginConfigurationTestHelper pluginConfigurationTestHelper = new PluginConfigurationTestHelper(this);
		pluginConfigurationTestHelper.schemaFileFolder = new File(mavenTestHelper.getModulePathFile(),
				"/src/test/resources");
		if (schemaFileSubFolder != null) {
			pluginConfigurationTestHelper.schemaFileFolder = new File(pluginConfigurationTestHelper.schemaFileFolder,
					schemaFileSubFolder);
		}

		String classname = this.getClass().getSimpleName();
		int firstDollar = classname.indexOf('$');
		pluginConfigurationTestHelper.packageName = BASE_PACKAGE + "."
				+ classname.substring(0, firstDollar).toLowerCase();

		pluginConfigurationTestHelper.customScalars = customScalars;
		pluginConfigurationTestHelper.mode = mode;
		pluginConfigurationTestHelper.schemaFilePattern = schemaFilePattern;
		pluginConfigurationTestHelper.schemaPersonalizationFile = (PluginConfiguration.DEFAULT_SCHEMA_PERSONALIZATION_FILE
				.equals(schemaPersonalizationFilename)) ? null
						: new File(mavenTestHelper.getModulePathFile(), schemaPersonalizationFilename);
		pluginConfigurationTestHelper.sourceEncoding = ENCODING;
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
