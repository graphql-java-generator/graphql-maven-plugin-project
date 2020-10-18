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

import com.graphql_java_generator.plugin.GenerateCodeGenerator;

import graphql.ThreadSafe;

/**
 * The <I>graphql</I> goal generates the java code from one or more GraphQL schemas. It allows to work in Java with
 * graphQL, in a schema first approach.<BR/>
 * It has two main modes:
 * <UL>
 * <LI><B>client mode:</B> it generates a class for each query, mutation and subscription type. These classes contain
 * the methods to call the queries, mutations and subscriptions. That is: to execute a query against the GraphQL server,
 * you just have to call one of this method. It also generates the POJOs from the GraphQL schema. The <B>GraphQL
 * response is stored in these POJOs</B>, for an easy and standard use in Java.</LI>
 * <LI><B>server mode:</B> it generates the whole heart of the GraphQL server. The developer has only to develop request
 * to the data. That is the main method (in a jar project) or the main servler (in a war project), and all the Spring
 * wiring, based on graphql-java-spring, itself being build on top of graphql-java. It also generates the POJOs. An
 * option allows to annotate them with the standard JPA annotations, to make it easy to link with a database. This goal
 * generates the interfaces for the DataFetchersDelegate (often named providers) that the server needs to implement</LI>
 * </UL>
 * 
 * @author etienne-sf
 */
@Mojo(name = "generateClientCode", defaultPhase = LifecyclePhase.GENERATE_SOURCES, requiresProject = true)
@ThreadSafe
public class GenerateClientCodeMojo extends AbstractGenerateClientCodeMojo {

	int nbGeneratedClasses = 0;

	@Configuration
	@Import({ JacksonAutoConfiguration.class })
	@ComponentScan(basePackages = { "com.graphql_java_generator" }, excludeFilters = {
			@Filter(type = FilterType.REGEX, pattern = ".*\\.GenerateGraphQLSchema.*"),
			@Filter(type = FilterType.REGEX, pattern = ".*\\.GenerateServerCode.*"),
			@Filter(type = FilterType.REGEX, pattern = ".*\\.GraphQL.*") })
	public class SpringConfiguration {

	}

	protected GenerateClientCodeMojo() {
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
