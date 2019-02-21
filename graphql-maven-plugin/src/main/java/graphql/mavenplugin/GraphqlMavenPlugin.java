/**
 * 
 */
package graphql.mavenplugin;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.AbstractApplicationContext;

import com.oembedler.moon.graphql.boot.GraphQLJavaToolsAutoConfiguration;
import com.oembedler.moon.graphql.boot.SchemaStringProvider;

import graphql.language.Document;
import graphql.parser.Parser;

/**
 * @author EtienneSF
 */
@Mojo(name = "graphql")
@Configuration
@Import({ JacksonAutoConfiguration.class, GraphQLJavaToolsAutoConfiguration.class })
public class GraphqlMavenPlugin extends AbstractMojo {

	@Parameter(property = "graphql.basePackage", defaultValue = "com.generated.graphql")
	private String basePackage;

	@Parameter(property = "graphql.encoding", defaultValue = "UTF-8")
	private String encoding;

	@Parameter(property = "graphql.outputDirectory", defaultValue = "${project.build.directory}/generated-sources/graphql-client")
	private File targetSourceFolder;

	/** The Spring IoC container */
	AbstractApplicationContext ctx;

	public void execute() throws MojoExecutionException, MojoFailureException {
		try {

			getLog().info("Starting generation of java classes from graphqls files");

			// We'll use Spring IoC
			ctx = new AnnotationConfigApplicationContext(getClass());
			DocumentParser documentParser = ctx.getBean(DocumentParser.class);
			int nbClasses = documentParser.parseDocuments();
			ctx.close();

			getLog().info(nbClasses + "java classes have been generated from graphqls files");

		} catch (Exception e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Let's give access to all the maven parameters as spring bean

	@Bean
	String basePackage() {
		return basePackage;
	}

	@Bean
	String encoding() {
		return encoding;
	}

	@Bean
	Log log() {
		return getLog();
	}

	@Bean
	File targetSourceFolder() {
		return targetSourceFolder;
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
