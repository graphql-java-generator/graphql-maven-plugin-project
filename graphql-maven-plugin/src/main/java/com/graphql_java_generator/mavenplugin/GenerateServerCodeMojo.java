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
import org.springframework.stereotype.Component;

import com.graphql_java_generator.plugin.DocumentParser;
import com.graphql_java_generator.plugin.language.DataFetchersDelegate;
import com.graphql_java_generator.util.GraphqlUtils;

import graphql.ThreadSafe;

/**
 * <P>
 * The <I>generateServerCode</I> Maven goal (and Gradle task) generates the java code for an almost ready to start
 * GraphQL server. The developer has only to develop request to the data.
 * </P>
 * <P>
 * The java code is generated from one or more GraphQL schemas. It allows to work in Java with graphQL, in a schema
 * first approach. These items are generated:
 * </P>
 * <UL>
 * <LI>the main method (in a jar project) or the main servlet (in a war project)</LI>
 * <LI>All the GraphQL wiring, based on graphql-java-spring, itself being build on top of graphql-java</LI>
 * <LI>All the POJOs, that contain the incoming request contents. The request response is written by the user code into
 * these POJO, and the plugin take care of mapping them into the server response.</LI>
 * <LI>An option allows to annotate the POJOs with the standard JPA annotations, to make it easy to link with a
 * database. Please note that a</LI>
 * <LI>All the interfaces for the {@link DataFetchersDelegate} (named providers in the graphql.org presentation) that
 * the server needs to implement</LI>
 * </UL>
 * <P>
 * The specific code that needs to be implemented is the access to the Data: your database, other APIs or web services,
 * or any kind of storage you may have. This is done by implementing the interfaces for the {@link DataFetchersDelegate}
 * into a Spring component, that is:
 * </P>
 * <UL>
 * <LI>Create a class for each generated {@link DataFetchersDelegate} interface</LI>
 * <LI>Make it implement the relevant {@link DataFetchersDelegate} interface</LI>
 * <LI>Mark it with the {@link Component} annotation</LI>
 * </UL>
 * <P>
 * And you're done! :)
 * </P>
 * <P>
 * You'll find more info in the tutorials: take a look at the
 * <A HREF="https://github.com/graphql-java-generator/GraphQL-Forum-Maven-Tutorial-server">Maven server tutorial</A> or
 * the <A HREF="https://github.com/graphql-java-generator/GraphQL-Forum-Gradle-Tutorial-server">Gradle server
 * tutorial</A>
 * </P>
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

	@Configuration
	@Import({ JacksonAutoConfiguration.class })
	@ComponentScan(basePackageClasses = { DocumentParser.class, GraphqlUtils.class }, //
			excludeFilters = { @Filter(type = FilterType.REGEX, pattern = ".*\\.GenerateClientCode.*"),
					@Filter(type = FilterType.REGEX, pattern = ".*\\.GenerateGraphQLSchema.*"),
					@Filter(type = FilterType.REGEX, pattern = ".*\\.GraphQL.*") })
	public class SpringConfiguration {

	}

	protected GenerateServerCodeMojo() {
		super(SpringConfiguration.class);
	}

}
