/**
 * 
 */
package graphql.mavenplugin;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.maven.plugin.MojoExecutionException;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import graphql.language.Document;
import graphql.parser.Parser;

/**
 * @author EtienneSF
 */
@Configuration
@Import({ JacksonAutoConfiguration.class })
@ComponentScan
public class SpringConfiguration {

	/**
	 * This static field is a trick to let the Spring ApplicationContext access to this instance. If you find any better
	 * solution, let us know !
	 */
	static GraphqlMavenPlugin mojo = null;

	@Bean
	PluginConfiguration pluginConfiguration() {
		return new PluginConfigurationImpl(mojo);
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
	public List<Document> documents(ResourceSchemaStringProvider schemaStringProvider) throws MojoExecutionException {
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
