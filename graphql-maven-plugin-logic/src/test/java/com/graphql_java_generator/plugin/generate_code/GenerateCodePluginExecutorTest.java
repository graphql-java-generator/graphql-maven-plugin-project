package com.graphql_java_generator.plugin.generate_code;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.graphql_java_generator.plugin.test.helper.GraphQLConfigurationTestHelper;

class GenerateCodePluginExecutorTest {

	GraphQLConfigurationTestHelper configuration;
	GenerateCodePluginExecutor pluginExecutor;

	@BeforeEach
	void setup() {
		configuration = new GraphQLConfigurationTestHelper(this);

		pluginExecutor = new GenerateCodePluginExecutor();
		pluginExecutor.configuration = configuration;
	}

	@Test
	void test_checkConfiguration() throws IOException {
		Map<String, String> templates = new HashMap<>();
		RuntimeException e;

		// A bad template name
		templates.put("a bad name", "don't care");
		configuration.templates = templates;
		e = assertThrows(RuntimeException.class, () -> pluginExecutor.checkConfiguration());
		assertTrue(e.getMessage().contains("'a bad name'"), e.getMessage());

		// A bad template file
		templates.clear();
		templates.put("OBJECT", "a bad file name");
		configuration.templates = templates;
		e = assertThrows(RuntimeException.class, () -> pluginExecutor.checkConfiguration());
		assertTrue(e.getMessage().contains("'a bad file name'"), e.getMessage());

		// A good local file
		templates.clear();
		templates.put("OBJECT", "src/main/resources/templates/client_CustomScalarRegistryInitializer.vm.java");
		configuration.templates = templates;
		pluginExecutor.checkConfiguration(); // This should not raise any exception

		// A good file in the classpath
		templates.clear();
		templates.put("OBJECT", "allGraphQLCases.graphqls");
		configuration.templates = templates;
		pluginExecutor.checkConfiguration(); // This should not raise any exception
	}

}
