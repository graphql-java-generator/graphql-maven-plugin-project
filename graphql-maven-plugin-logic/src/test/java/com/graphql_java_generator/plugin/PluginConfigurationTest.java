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

		pluginConfiguration.typePrefix = "typePrefix";
		assertEquals("typePrefix", pluginConfiguration.getTypePrefix());

		pluginConfiguration.typeSuffix = "typeSuffix";
		assertEquals("typeSuffix", pluginConfiguration.getTypeSuffix());

		pluginConfiguration.inputPrefix = "inputPrefix";
		assertEquals("inputPrefix", pluginConfiguration.getInputPrefix());

		pluginConfiguration.inputSuffix = "inputSuffix";
		assertEquals("inputSuffix", pluginConfiguration.getInputSuffix());

		pluginConfiguration.unionPrefix = "unionPrefix";
		assertEquals("unionPrefix", pluginConfiguration.getUnionPrefix());

		pluginConfiguration.unionSuffix = "unionSuffix";
		assertEquals("unionSuffix", pluginConfiguration.getUnionSuffix());

		pluginConfiguration.interfacePrefix = "interfacePrefix";
		assertEquals("interfacePrefix", pluginConfiguration.getInterfacePrefix());

		pluginConfiguration.interfaceSuffix = "interfaceSuffix";
		assertEquals("interfaceSuffix", pluginConfiguration.getInterfaceSuffix());

		pluginConfiguration.enumPrefix = "enumPrefix";
		assertEquals("enumPrefix", pluginConfiguration.getEnumPrefix());

		pluginConfiguration.enumSuffix = "enumSuffix";
		assertEquals("enumSuffix", pluginConfiguration.getEnumSuffix());
	}

}
