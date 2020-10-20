/**
 * 
 */
package com.graphql_java_generator.mavenplugin;

import java.io.File;
import java.io.IOException;
import java.time.Duration;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

import com.graphql_java_generator.plugin.GenerateCodeGenerator;
import com.graphql_java_generator.plugin.language.DataFetchersDelegate;

import graphql.ThreadSafe;

/**
 * The <I>generateServerCode</I> Maven goal (and Gradle task) generates the java code from one or more GraphQL schemas.
 * It allows to work in Java with graphQL, in a schema first approach.<BR/>
 * It generates the whole heart of the GraphQL server. The developer has only to develop request to the data. These
 * items are generated:
 * <UL>
 * <LI>the main method (in a jar project) or the main servler (in a war project)</LI>
 * <LI>All the GraphQL wiring, based on graphql-java-spring, itself being build on top of graphql-java</LI>
 * <LI>All the POJOs, that contain the incoming request contents. The request reponse is written by the user code into
 * these POJO, and the plugin take care of mapping them into the server response.</LI>
 * <LI>An option allows to annotate the POJOs with the standard JPA annotations, to make it easy to link with a
 * database. Please note that a</LI>
 * <LI>All the interfaces for the {@link DataFetchersDelegate} (named providers in the graphql.org presentation) that
 * the server needs to implement</LI>
 * </UL>
 * The specific code that needs to be implemented is the access to the Data: your database, other APIs or web services,
 * or any kind of storage you may have. This is done by implementing the interfaces for the {@link DataFetchersDelegate}
 * into a Spring component, that is:
 * <UL>
 * <LI>Create a class for each generated {@link DataFetchersDelegate} interface</LI>
 * <LI>Make it implement the relevant {@link DataFetchersDelegate} interface</LI>
 * <LI>Mark it with the {@link Component} annotation</LI>
 * </UL>
 * And you're done! :) <BR/>
 * <BR/>
 * You'll find more info in the tutorials: take a look at the
 * <A HREF="https://github.com/graphql-java-generator/GraphQL-Forum-Maven-Tutorial-server">Maven server tutorial</A> or
 * the <A HREF="https://github.com/graphql-java-generator/GraphQL-Forum-Gradle-Tutorial-server">Gradle server
 * tutorial</A>
 * 
 * @author etienne-sf
 */
@Mojo(name = "generateServerCode", defaultPhase = LifecyclePhase.GENERATE_SOURCES, requiresProject = true)
@ThreadSafe
// No need to add the @Component spring annotation: AbstractCommonMojo added this instance into the spring context, to
// use the instance which attributs has been set with the pom content
public class GenerateServerCodeMojo extends AbstractGenerateServerCodeMojo {

	// All the Mojo parameters are defined in the AbstractXxxx classes, that contains the contain the hierarchical
	// structure of the Maven goals.
	// See the explanation in the AbstractCommonMojo for more details.

	int nbGeneratedClasses = 0;

	@Configuration
	@Import({ JacksonAutoConfiguration.class })
	@ComponentScan(basePackages = { "com.graphql_java_generator" }, excludeFilters = {
			@Filter(type = FilterType.REGEX, pattern = ".*\\.GenerateClientCode.*"),
			@Filter(type = FilterType.REGEX, pattern = ".*\\.GenerateGraphQLSchema.*"),
			@Filter(type = FilterType.REGEX, pattern = ".*\\.GraphQL.*") })
	public class SpringConfiguration {

	}

	protected GenerateServerCodeMojo() {
		super(SpringConfiguration.class);
	}

	@Override
	protected void executeSpecificJob() throws IOException {
		GenerateCodeGenerator codeGenerator = ctx.getBean(GenerateCodeGenerator.class);
		nbGeneratedClasses = codeGenerator.generateCode();

		File targetDir = new File(project.getBasedir(), "target");
		project.addCompileSourceRoot(new File(targetDir, targetSourceFolder).getAbsolutePath());
	}

	@Override
	protected void logResult(Duration duration) {
		getLog().info(nbGeneratedClasses + " java classes have been generated from the schema(s) '" + schemaFilePattern
				+ "' in the package '" + packageName + "' in " + duration.getSeconds() + " seconds");
	}
}
