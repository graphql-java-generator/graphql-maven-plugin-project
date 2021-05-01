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
 * <P>
 * <B>This goal is <U>deprecated</U></B>. The <I>graphql</I> goal generates the java code from one or more GraphQL
 * schemas. It allows to work in Java with graphQL, in a schema first approach.
 * </P>
 * It will be maintained in the future 2.x versions. The <I>generateClientCode</I> and <I>generateServerCode</I> should
 * be used instead.<BR/>
 * The <I>graphql</I> goal has two main modes:
 * <UL>
 * <LI><B>client mode:</B> it does the same jobs as the <I>generateClientCode</I> goal. It generates a class for each
 * query, mutation and subscription type. These classes contain the methods to call the queries, mutations and
 * subscriptions. That is: to execute a query against the GraphQL server, you just have to call one of this method. It
 * also generates the POJOs from the GraphQL schema. The <B>GraphQL response is stored in these POJOs</B>, for an easy
 * and standard use in Java.</LI>
 * <LI><B>server mode:</B> it does the same jobs as the <I>generateServerCode</I> goal. It generates the whole heart of
 * the GraphQL server. The developer has only to develop request to the data. That is the main method (in a jar project)
 * or the main server (in a war project), and all the Spring wiring, based on graphql-java-spring, itself being build on
 * top of graphql-java. It also generates the POJOs. An option allows to annotate them with the standard JPA
 * annotations, to make it easy to link with a database. This goal generates the interfaces for the DataFetchersDelegate
 * (often named providers) that the server needs to implement</LI>
 * </UL>
 * 
 * @author etienne-sf
 */
@Mojo(name = "graphql", defaultPhase = LifecyclePhase.GENERATE_SOURCES, requiresProject = true)
@ThreadSafe
// No need to add the @Component spring annotation: AbstractCommonMojo added this instance into the spring context, to
// use the instance which attributes has been set with the pom content
public class GraphQLMojo extends AbstractGraphQLMojo {

	// All the Mojo parameters are defined in the AbstractXxxx classes, that contains the contain the hierarchical
	// structure of the Maven goals.
	// See the explanation in the AbstractCommonMojo for more details.

	@Configuration
	@Import({ JacksonAutoConfiguration.class })
	@ComponentScan(basePackageClasses = { DocumentParser.class, GraphqlUtils.class }, excludeFilters = {
			@Filter(type = FilterType.REGEX, pattern = ".*\\.GenerateClientCode.*"),
			@Filter(type = FilterType.REGEX, pattern = ".*\\.GenerateGraphQLSchema.*"),
			@Filter(type = FilterType.REGEX, pattern = ".*\\.GenerateServerCode.*") })
	public class SpringConfiguration {

	}

	public GraphQLMojo() {
		super(SpringConfiguration.class);
	}

}
