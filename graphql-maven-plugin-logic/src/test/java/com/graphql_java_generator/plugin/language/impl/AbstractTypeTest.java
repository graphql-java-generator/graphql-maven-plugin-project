package com.graphql_java_generator.plugin.language.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.text.DateFormat;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.graphql_java_generator.plugin.GraphQLConfiguration;
import com.graphql_java_generator.plugin.test.helper.GraphQLConfigurationTestHelper;

class AbstractTypeTest {

	GraphQLConfiguration pluginConfiguration = new GraphQLConfigurationTestHelper(this);

	@Test
	void testAddImportClassOfQ() {
		// Preparation
		AbstractType type = new ObjectType("DateFormat", pluginConfiguration);
		assertEquals(0, type.getImports().size());

		// Add of a class from the same package
		type.addImport(pluginConfiguration.getPackageName(), pluginConfiguration.getPackageName() + ".AClassName");
		assertEquals(0, type.getImports().size());

		// Add of a class of the same name (nothing happens: it would cause a name conflict in the generated code)
		type.addImport(pluginConfiguration.getPackageName(), DateFormat.class.getName());
		assertEquals(0, type.getImports().size());

		// Add of a class from another package
		type.addImport(pluginConfiguration.getPackageName(), java.sql.Date.class.getName());
		assertEquals(1, type.getImports().size());
		assertTrue(type.getImports().contains("java.sql.Date"));

		// Add of an inner class
		type.addImport(pluginConfiguration.getPackageName(), JsonSubTypes.Type.class.getName());
		assertEquals(2, type.getImports().size());
		assertTrue(type.getImports().contains("java.sql.Date"));
		assertTrue(type.getImports().contains("com.fasterxml.jackson.annotation.JsonSubTypes.Type"));

		// Add of an inner class: when the MainClassname is given in the package name
		type.addImport(pluginConfiguration.getPackageName(),
				com.fasterxml.jackson.annotation.JsonTypeInfo.Id.class.getName());
		assertEquals(3, type.getImports().size());
		assertTrue(type.getImports().contains("java.sql.Date"));
		assertTrue(type.getImports().contains("com.fasterxml.jackson.annotation.JsonSubTypes.Type"));
		assertTrue(type.getImports().contains("com.fasterxml.jackson.annotation.JsonTypeInfo.Id"));
	}

	@Test
	void testAddImportStringString() {
		// Preparation
		AbstractType type = new ObjectType("DateFormat", pluginConfiguration);
		assertEquals(0, type.getImports().size());

		// Add of a class from the same package
		type.addImport(pluginConfiguration.getPackageName(), pluginConfiguration.getPackageName() + ".JsonSubTypes");
		assertEquals(0, type.getImports().size());

		// Add of a class from the same name (nothing happens: it would cause a name conflict in the generated code)
		type.addImport(pluginConfiguration.getPackageName(), "java.text.DateFormat");
		assertEquals(0, type.getImports().size());

		// Add of a class from another package
		type.addImport(pluginConfiguration.getPackageName(), "java.sql.Date");
		assertEquals(1, type.getImports().size());
		assertTrue(type.getImports().contains("java.sql.Date"));

		// Add of an inner class: the simple classname is then: MainClassname$InnerClassname
		type.addImport(pluginConfiguration.getPackageName(), "com.fasterxml.jackson.annotation.JsonSubTypes.Type");
		assertEquals(2, type.getImports().size());
		assertTrue(type.getImports().contains("java.sql.Date"));
		assertTrue(type.getImports().contains("com.fasterxml.jackson.annotation.JsonSubTypes.Type"));

		// Add of an inner class: when the MainClassname is given in the package name
		type.addImport(pluginConfiguration.getPackageName(), "com.fasterxml.jackson.annotation.JsonTypeInfo.Id");
		assertEquals(3, type.getImports().size());
		assertTrue(type.getImports().contains("java.sql.Date"));
		assertTrue(type.getImports().contains("com.fasterxml.jackson.annotation.JsonSubTypes.Type"));
		assertTrue(type.getImports().contains("com.fasterxml.jackson.annotation.JsonTypeInfo.Id"));

		// Add of an inner class: when the MainClassname is given in the package name
		type.addImport(pluginConfiguration.getPackageName(), "com.fasterxml.jackson.annotation.JsonTypeInfo$Id2");
		assertEquals(4, type.getImports().size());
		assertTrue(type.getImports().contains("java.sql.Date"));
		assertTrue(type.getImports().contains("com.fasterxml.jackson.annotation.JsonSubTypes.Type"));
		assertTrue(type.getImports().contains("com.fasterxml.jackson.annotation.JsonTypeInfo.Id"));
		assertTrue(type.getImports().contains("com.fasterxml.jackson.annotation.JsonTypeInfo.Id2"));
	}

}
