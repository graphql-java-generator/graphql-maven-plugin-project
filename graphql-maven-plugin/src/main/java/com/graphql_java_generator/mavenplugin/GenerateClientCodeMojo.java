/**
 * 
 */
package com.graphql_java_generator.mavenplugin;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;

import com.graphql_java_generator.plugin.DocumentParser;
import com.graphql_java_generator.util.GraphqlUtils;

import graphql.ThreadSafe;

/**
 * The <I>generateClientCode</I> Maven goal (and Gradle task) generates the java code from one or more GraphQL schemas.
 * It allows to work in Java with graphQL, in a schema first approach.<BR/>
 * It generates a class for each query, mutation and subscription type. These classes contain the methods to call the
 * queries, mutations and subscriptions. That is: to execute a query against the GraphQL server, you just have to call
 * one of these methods. It also generates the POJOs from the GraphQL schema. The <B>GraphQL response is stored in these
 * POJOs</B>, for an easy and standard use in Java. <BR/>
 * <BR/>
 * You'll find more info in the tutorials: take a look at the
 * <A HREF="https://github.com/graphql-java-generator/GraphQL-Forum-Maven-Tutorial-client">Maven client tutorial</A> or
 * the <A HREF="https://github.com/graphql-java-generator/GraphQL-Forum-Gradle-Tutorial-client">Gradle client
 * tutorial</A>
 * 
 * @author etienne-sf
 */
@Mojo(name = "generateClientCode", defaultPhase = LifecyclePhase.GENERATE_SOURCES, requiresProject = true)
@ThreadSafe
// No need to add the @Component spring annotation: AbstractCommonMojo added this instance into the spring context, to
// use the instance which attributs has been set with the pom content
public class GenerateClientCodeMojo extends AbstractGenerateClientCodeMojo {

	// All the Mojo parameters are defined in the AbstractXxxx classes, that contains the contain the hierarchical
	// structure of the Maven goals.
	// See the explanation in the AbstractCommonMojo for more details.

	@Configuration
	@Import({ JacksonAutoConfiguration.class })
	@ComponentScan(basePackageClasses = { DocumentParser.class, GraphqlUtils.class }, excludeFilters = {
			@Filter(type = FilterType.REGEX, pattern = ".*\\.GenerateGraphQLSchema.*"),
			@Filter(type = FilterType.REGEX, pattern = ".*\\.GenerateServerCode.*"),
			@Filter(type = FilterType.REGEX, pattern = ".*\\.GraphQL.*") })
	public class SpringConfiguration {

	}

	protected GenerateClientCodeMojo() {
		super(SpringConfiguration.class);
	}

}
