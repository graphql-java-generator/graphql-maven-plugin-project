package org.graphql.maven.plugin.samples.Forum.client;

import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.coxautodev.graphql.tools.SchemaParser;
import com.coxautodev.graphql.tools.SchemaParserBuilder;
import com.oembedler.moon.graphql.boot.GraphQLJavaToolsAutoConfiguration;
import com.oembedler.moon.graphql.boot.SchemaStringProvider;

import graphql.GraphQL;
import graphql.schema.GraphQLSchema;

@Configuration
public class SpringConfiguration {

	/**
	 * Builds a {@link GraphqlSchema} from the graphqls schema files found on the classpath
	 * 
	 * @param schemaStringProvider
	 * @return
	 * @throws IOException
	 * @See {@link GraphQLJavaToolsAutoConfiguration}
	 */
	@Bean
	GraphQLSchema graphQLSchema(SchemaStringProvider schemaStringProvider) throws IOException {
		SchemaParserBuilder schemaParserBuild = SchemaParser.newParser();
		for (String schema : schemaStringProvider.schemaStrings()) {
			schemaParserBuild.schemaString(schema);
		} // for
		return schemaParserBuild.build().makeExecutableSchema();
	}

	@Bean
	GraphQL graphQL(GraphQLSchema graphQLSchema) {
		return GraphQL.newGraphQL(graphQLSchema).build();
	}

}
