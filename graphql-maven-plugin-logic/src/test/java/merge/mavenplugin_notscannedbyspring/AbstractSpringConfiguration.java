/**
 * 
 */
package merge.mavenplugin_notscannedbyspring;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

import com.graphql_java_generator.plugin.Documents;
import com.graphql_java_generator.plugin.ResourceSchemaStringProvider;
import com.graphql_java_generator.plugin.test.helper.GenerateGraphQLSchemaConfigurationTestHelper;
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

	public final static String ROOT_UNIT_TEST_FOLDER = "target/junittest_merge/";

	@Resource
	MavenTestHelper mavenTestHelper;
	@Autowired
	GenerateGraphQLSchemaConfigurationTestHelper configuration;

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

	protected File getRootUnitTestFolder() {
		return new File(mavenTestHelper.getModulePathFile(), ROOT_UNIT_TEST_FOLDER);
	}

	@Bean
	GenerateGraphQLSchemaConfigurationTestHelper graphQLConfigurationTestHelper() {
		GenerateGraphQLSchemaConfigurationTestHelper configuration = new GenerateGraphQLSchemaConfigurationTestHelper(
				this);

		// Let's update what"s specific to the current test case
		addSpecificConfigurationParameterValue(configuration);

		return configuration;
	}

	/**
	 * Allows the concrete subclasses to supersede the default configuration values to values specific to the current
	 * test case
	 */
	protected abstract void addSpecificConfigurationParameterValue(
			GenerateGraphQLSchemaConfigurationTestHelper configuration);

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
