/**
 * 
 */
package graphql.mavenplugin_notscannedbyspring;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.graphql_java_generator.plugin.Documents;
import com.graphql_java_generator.plugin.ResourceSchemaStringProvider;
import com.graphql_java_generator.plugin.conf.GraphQLConfiguration;
import com.graphql_java_generator.plugin.test.helper.GraphQLConfigurationTestHelper;
import com.graphql_java_generator.plugin.test.helper.MavenTestHelper;

import graphql.language.Document;
import graphql.parser.Parser;
import lombok.Getter;
import lombok.Setter;

/**
 * The Spring configuration used for JUnit tests. To use tit, just create a subclass, and provide the schemaFilePattern
 * to its constructor
 * 
 * @author etienne-sf
 */
@Configuration
@Import({ JacksonAutoConfiguration.class })
public abstract class AbstractSpringConfiguration {

	private static String BASE_PACKAGE = "org.graphql.mavenplugin.junittest";

	@Getter
	@Setter
	private String schemaFileSubFolder;

	@Resource
	MavenTestHelper mavenTestHelper;

	public class DocumentsTestHelperImpl implements Documents {
		List<Document> documents = new ArrayList<>();

		@Override
		public List<Document> getDocuments() throws IOException {
			return documents;
		}

		public void setDocuments(List<Document> documents) {
			this.documents = documents;
		}
	}

	@Bean
	GraphQLConfiguration graphQLConfigurationTestHelper(MavenTestHelper mavenTestHelper) {
		GraphQLConfigurationTestHelper configuration = new GraphQLConfigurationTestHelper(this);
		configuration.schemaFileFolder = new File(mavenTestHelper.getModulePathFile(), "/src/test/resources");
		if (schemaFileSubFolder != null) {
			configuration.schemaFileFolder = new File(configuration.schemaFileFolder, schemaFileSubFolder);
		}

		String classname = this.getClass().getSimpleName();
		int firstDollar = classname.indexOf('$');
		configuration.packageName = BASE_PACKAGE + "." + classname.substring(0, firstDollar).toLowerCase();

		configuration.targetSourceFolder = mavenTestHelper.getTargetSourceFolder(
				(classname.contains("$")) ? classname = classname.substring(0, classname.indexOf('$')) : classname);
		configuration.targetClassFolder = new File(configuration.targetSourceFolder.getParentFile(),
				"compilation_test");

		addSpecificConfigurationParameterValue(configuration);

		return configuration;
	}

	protected abstract void addSpecificConfigurationParameterValue(GraphQLConfigurationTestHelper configuration);

	/**
	 * Loads the schema from the graphqls files. This method uses the {@link GraphQLJavaToolsAutoConfiguration} from the
	 * 
	 * project, to load the schema from the graphqls files
	 * 
	 * @throws IOException
	 */
	@Bean
	public Documents documents(ResourceSchemaStringProvider schemaStringProvider) throws IOException {
		Parser parser = new Parser();
		List<Document> documents = schemaStringProvider.schemaStrings().stream().map(parser::parseDocument)
				.collect(Collectors.toList());
		DocumentsTestHelperImpl ret = new DocumentsTestHelperImpl();
		ret.setDocuments(documents);
		return ret;
	}
}
