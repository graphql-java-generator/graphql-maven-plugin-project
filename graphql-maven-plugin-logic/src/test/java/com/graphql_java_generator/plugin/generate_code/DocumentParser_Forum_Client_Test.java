package com.graphql_java_generator.plugin.generate_code;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.graphql_java_generator.plugin.test.helper.GraphQLConfigurationTestHelper;

import graphql.mavenplugin_notscannedbyspring.Forum_Client_SpringConfiguration;

/**
 * 
 * @author etienne-sf
 */
@Execution(ExecutionMode.CONCURRENT)
class DocumentParser_Forum_Client_Test extends AbstractDocumentParser_Forum_Client {

	@BeforeEach
	void setUp() throws Exception {
		this.ctx = new AnnotationConfigApplicationContext(Forum_Client_SpringConfiguration.class);
		this.documentParser = this.ctx.getBean(GenerateCodeDocumentParser.class);
		this.codeGenerator = this.ctx.getBean(GenerateCodeGenerator.class);
		this.configuration = this.ctx.getBean(GraphQLConfigurationTestHelper.class);

		this.documentParser.parseGraphQLSchemas();
	}

}
