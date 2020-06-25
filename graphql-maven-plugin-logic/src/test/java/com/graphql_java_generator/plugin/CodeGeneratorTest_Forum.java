package com.graphql_java_generator.plugin;

import javax.annotation.Resource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import com.graphql_java_generator.plugin.GraphQLCodeGenerator;
import com.graphql_java_generator.plugin.GraphQLDocumentParser;
import com.graphql_java_generator.plugin.test.helper.MavenTestHelper;

import graphql.mavenplugin_notscannedbyspring.Forum_Server_SpringConfiguration;

@ExtendWith(SpringExtension.class)
@SpringJUnitConfig(classes = { Forum_Server_SpringConfiguration.class })
class CodeGeneratorTest_Forum {

	@Resource
	MavenTestHelper mavenTestHelper;

	@javax.annotation.Resource
	protected GraphQLDocumentParser documentParser;
	@javax.annotation.Resource
	protected GraphQLCodeGenerator codeGenerator;

	@BeforeEach
	void setUp() throws Exception {
		documentParser.parseDocuments();
	}

}
