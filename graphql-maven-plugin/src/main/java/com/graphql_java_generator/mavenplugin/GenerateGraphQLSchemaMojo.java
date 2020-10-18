/**
 * 
 */
package com.graphql_java_generator.mavenplugin;

import java.time.Duration;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

import com.graphql_java_generator.plugin.GenerateGraphQLSchema;

import graphql.ThreadSafe;

/**
 * The <I>generateGraphQLSchema</I> goal generates GraphQL schema, based on the source GraphQL schemas.<BR/>
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
public class GenerateGraphQLSchemaMojo extends AbstractGenerateGraphQLSchemaMojo {

	@Configuration
	@ComponentScan(basePackages = { "com.graphql_java_generator" }, excludeFilters = {
			@Filter(type = FilterType.REGEX, pattern = ".*\\.GraphQL.*"),
			@Filter(type = FilterType.REGEX, pattern = ".*\\.GenerateClientCode.*"),
			@Filter(type = FilterType.REGEX, pattern = ".*\\.GenerateServerCode.*") })
	public class SpringConfiguration {

	}

	GenerateGraphQLSchemaMojo() {
		super(SpringConfiguration.class);
	}

	@Override
	protected void executeSpecificJob() throws Exception {
		GenerateGraphQLSchema generateGraphQLSchema = ctx.getBean(GenerateGraphQLSchema.class);
		generateGraphQLSchema.generateGraphQLSchema();
	}

	@Override
	protected void logResult(Duration duration) {
		getLog().debug("Finished generation of the GraphQL schema");
	}

}
