/**
 * 
 */
package graphql.mavenplugin;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.oembedler.moon.graphql.boot.GraphQLJavaToolsAutoConfiguration;
import com.oembedler.moon.graphql.boot.SchemaStringProvider;

import graphql.language.Document;
import graphql.parser.Parser;

/**
 * @author EtienneSF
 */
@Configuration
@Import({ JacksonAutoConfiguration.class, GraphQLJavaToolsAutoConfiguration.class })
@ComponentScan
public class SpringConfiguration {

	/**
	 * This static field is a trick to let the Spring ApplicationContext access to this instance. If you find any better
	 * solution, let us know !
	 */
	static GraphqlMavenPlugin mojo = null;

	/**
	 * This bean returns the resource folder for the current project. It is override when running tests, by the test
	 * one, that returns "/src/test/resources".
	 * 
	 * @return "/src/main/resources"
	 */
	@Bean
	@ConditionalOnMissingBean
	public String resourcesFolder() {
		return "/src/main/resources";
	}

	@Bean
	String packageName() {
		return mojo.packageName;
	}

	@Bean
	String encoding() {
		return mojo.encoding;
	}

	@Bean
	Log log() {
		return mojo.getLog();
	}

	@Bean
	PluginMode mode() {
		return mojo.mode;
	}

	@Bean
	MavenProject project() {
		return mojo.project;
	}

	@Bean
	String schemaFilePattern() {
		return mojo.schemaFilePattern;
	}

	@Bean
	File schemaPersonalizationFile() {
		return mojo.schemaPersonalizationFile;
	}

	@Bean
	File targetSourceFolder() {
		return mojo.targetSourceFolder;
	}

	/**
	 * Loads the schema from the graphqls files. This method uses the {@link GraphQLJavaToolsAutoConfiguration} from the
	 * 
	 * project, to load the schema from the graphqls files
	 * 
	 * @throws MojoExecutionException
	 *             When an error occurs while reading or parsing the graphql definition files
	 */
	@Bean
	public List<Document> documents(SchemaStringProvider schemaStringProvider) throws MojoExecutionException {
		try {
			Parser parser = new Parser();
			return schemaStringProvider.schemaStrings().stream().map(parser::parseDocument)
					.collect(Collectors.toList());
		} catch (IOException e) {
			throw new MojoExecutionException("Error while reading graphql schema definition files: " + e.getMessage(),
					e);
		}

	}
}
