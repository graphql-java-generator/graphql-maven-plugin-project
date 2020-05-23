package com.graphql_java_generator.plugin;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.graphql_java_generator.plugin.test.helper.PluginConfigurationTestHelper;

class PluginConfigurationTest {

	@Test
	void testGetQuotedScanBasePackages() {
		PluginConfigurationTestHelper pluginConfiguration = new PluginConfigurationTestHelper(this);

		pluginConfiguration.setScanBasePackages(null);
		assertEquals("", pluginConfiguration.getQuotedScanBasePackages());

		pluginConfiguration.setScanBasePackages("");
		assertEquals("", pluginConfiguration.getQuotedScanBasePackages());

		pluginConfiguration.setScanBasePackages("null");
		assertEquals("", pluginConfiguration.getQuotedScanBasePackages());

		pluginConfiguration.setScanBasePackages(" a. b . c");
		assertEquals(",\"a.b.c\"", pluginConfiguration.getQuotedScanBasePackages());

		pluginConfiguration.setScanBasePackages("a.b.c");
		assertEquals(",\"a.b.c\"", pluginConfiguration.getQuotedScanBasePackages());

		pluginConfiguration.setScanBasePackages("  a.b.c    ,    d.e.f  ");
		assertEquals(",\"a.b.c\",\"d.e.f\"", pluginConfiguration.getQuotedScanBasePackages());
	}

}
