package com.graphql_java_generator.plugin.compilation_tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;

import graphql.mavenplugin_notscannedbyspring.Forum_Server_SpringConfiguration;

@Tag("forum")
class ForumServerTest extends AbstractIntegrationTest {

	public ForumServerTest() {
		super(Forum_Server_SpringConfiguration.class);
	}

	@BeforeEach
	public void setUp() {
		graphqlTestHelper.checkSchemaStringProvider("forum.graphqls");
	}

}
