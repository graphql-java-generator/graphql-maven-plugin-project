/**
 * 
 */
package com.graphql_java_generator.mavenplugin;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

import com.graphql_java_generator.plugin.DocumentParser;
import com.graphql_java_generator.util.GraphqlUtils;

import graphql.ThreadSafe;

/**
 * <P>
 * The <I>generateGraphQLSchema</I> goal generates GraphQL schema, based on the source GraphQL schemas, and possibly
 * containing additional stuff, like the Relay connection objects.
 * </P>
 * It can be used to:
 * <UL>
 * <LI>Generate several GraphQL schema files into one file, for instance with additional schema files that would use the
 * <I>extend</I> GraphQL keyword</LI>
 * <LI>Reformat the schema file</LI>
 * <LI>Generate the GraphQL schema with the Relay Connection stuff (Node interface, XxxEdge and XxxConnection types),
 * thanks to the <I>addRelayConnections</I> plugin parameter.
 * </UL>
 * <BR/>
 * This goal is, by default, attached to the Initialize maven phase, to be sure that the GraphQL schema are generated
 * before the code generation would need it, if relevant.
 * 
 * @author etienne-sf
 */
@Mojo(name = "generateGraphQLSchema", defaultPhase = LifecyclePhase.INITIALIZE)
@ThreadSafe
// No need to add the @Component spring annotation: AbstractCommonMojo added this instance into the spring context, to
// use the instance which attributs has been set with the pom content
public class GenerateGraphQLSchemaMojo extends AbstractGenerateGraphQLSchemaMojo {

	// All the Mojo parameters are defined in the AbstractXxxx classes, that contains the contain the hierarchical
	// structure of the Maven goals.
	// See the explanation in the AbstractCommonMojo for more details.

	@Configuration
	@ComponentScan(basePackageClasses = { PluginBuildContextImpl.class, DocumentParser.class, GraphqlUtils.class }, //
			excludeFilters = { @Filter(type = FilterType.REGEX, pattern = ".*\\.GraphQL.*"),
					@Filter(type = FilterType.REGEX, pattern = ".*\\.GenerateClientCode.*"),
					@Filter(type = FilterType.REGEX, pattern = ".*\\.GenerateCode.*"),
					@Filter(type = FilterType.REGEX, pattern = ".*\\.GenerateServerCode.*") })
	public class SpringConfiguration {

	}

	GenerateGraphQLSchemaMojo() {
		super(SpringConfiguration.class);
	}

}
