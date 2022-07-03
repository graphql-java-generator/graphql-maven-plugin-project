package com.graphql_java_generator.plugin;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import com.graphql_java_generator.plugin.test.helper.GraphQLConfigurationTestHelper;

@Execution(ExecutionMode.CONCURRENT)
class PluginConfigurationTest {

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void testGetQuotedScanBasePackages() {
		GraphQLConfigurationTestHelper pluginConfiguration = new GraphQLConfigurationTestHelper(this);

		pluginConfiguration.scanBasePackages = null;
		assertEquals("", pluginConfiguration.getQuotedScanBasePackages());

		pluginConfiguration.scanBasePackages = "";
		assertEquals("", pluginConfiguration.getQuotedScanBasePackages());

		pluginConfiguration.scanBasePackages = "null";
		assertEquals("", pluginConfiguration.getQuotedScanBasePackages());

		pluginConfiguration.scanBasePackages = " a. b . c";
		assertEquals(",\"a.b.c\"", pluginConfiguration.getQuotedScanBasePackages());

		pluginConfiguration.scanBasePackages = "a.b.c";
		assertEquals(",\"a.b.c\"", pluginConfiguration.getQuotedScanBasePackages());

		pluginConfiguration.scanBasePackages = "  a.b.c    ,    d.e.f  ";
		assertEquals(",\"a.b.c\",\"d.e.f\"", pluginConfiguration.getQuotedScanBasePackages());
	}

}
