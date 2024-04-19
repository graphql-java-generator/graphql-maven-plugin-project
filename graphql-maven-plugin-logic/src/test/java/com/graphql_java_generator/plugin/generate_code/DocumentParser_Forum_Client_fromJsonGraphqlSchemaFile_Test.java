package com.graphql_java_generator.plugin.generate_code;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.graphql_java_generator.plugin.DocumentParser;
import com.graphql_java_generator.plugin.conf.GraphQLConfiguration;
import com.graphql_java_generator.plugin.test.helper.GraphQLConfigurationTestHelper;
import com.graphql_java_generator.plugin.test.helper.MavenTestHelper;

import graphql.mavenplugin_notscannedbyspring.Forum_Client_SpringConfiguration;

/**
 * 
 * @author etienne-sf
 */
@Execution(ExecutionMode.CONCURRENT)
class DocumentParser_Forum_Client_fromJsonGraphqlSchemaFile_Test extends AbstractDocumentParser_Forum_Client {

	@BeforeEach
	void setUp() throws Exception {
		// Let's build a dedicated Spring context, to override the plugin configuration
		this.ctx = new AnnotationConfigApplicationContext(Forum_Client_SpringConfiguration.class);
		this.documentParser = (GenerateCodeDocumentParser) this.ctx.getBean(DocumentParser.class);
		this.codeGenerator = this.ctx.getBean(GenerateCodeGenerator.class);
		MavenTestHelper mavenTestHelper = this.ctx.getBean(MavenTestHelper.class);
		this.configuration = (GraphQLConfigurationTestHelper) this.ctx.getBean(GraphQLConfiguration.class);

		this.configuration.jsonGraphqlSchemaFilename = "forum_GraphQLSchema.json";
		this.configuration.schemaFileFolder = mavenTestHelper.getFile("src/test/resources");

		this.documentParser.parseGraphQLSchemas();
	}

}
