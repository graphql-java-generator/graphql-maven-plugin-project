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
		AbstractType type = new ObjectType("DateFormat", "com.fasterxml.jackson.annotation", pluginConfiguration);
		assertEquals(0, type.getImports().size());

		// Add of a class from the same package
		type.addImport(JsonSubTypes.class);
		assertEquals(0, type.getImports().size());

		// Add of a class from the same name (nothing happens: it would cause a name conflict in the generated code)
		type.addImport(DateFormat.class);
		assertEquals(0, type.getImports().size());

		// Add of a class from another package
		type.addImport(java.sql.Date.class);
		assertEquals(1, type.getImports().size());
		assertTrue(type.getImports().contains("java.sql.Date"));

		// Add of an inner class
		type.addImport(JsonSubTypes.Type.class);
		assertEquals(2, type.getImports().size());
		assertTrue(type.getImports().contains("java.sql.Date"));
		assertTrue(type.getImports().contains("com.fasterxml.jackson.annotation.JsonSubTypes.Type"));

		// Add of an inner class: when the MainClassname is given in the package name
		type.addImport(com.fasterxml.jackson.annotation.JsonTypeInfo.Id.class);
		assertEquals(3, type.getImports().size());
		assertTrue(type.getImports().contains("java.sql.Date"));
		assertTrue(type.getImports().contains("com.fasterxml.jackson.annotation.JsonSubTypes.Type"));
		assertTrue(type.getImports().contains("com.fasterxml.jackson.annotation.JsonTypeInfo.Id"));
	}

	@Test
	void testAddImportStringString() {
		// Preparation
		AbstractType type = new ObjectType("DateFormat", "com.fasterxml.jackson.annotation", pluginConfiguration);
		assertEquals(0, type.getImports().size());

		// Add of a class from the same package
		type.addImport("com.fasterxml.jackson.annotation", "JsonSubTypes");
		assertEquals(0, type.getImports().size());

		// Add of a class from the same name (nothing happens: it would cause a name conflict in the generated code)
		type.addImport("java.text", "DateFormat");
		assertEquals(0, type.getImports().size());

		// Add of a class from another package
		type.addImport("java.sql", "Date");
		assertEquals(1, type.getImports().size());
		assertTrue(type.getImports().contains("java.sql.Date"));

		// Add of an inner class: the simple classname is then: MainClassname$InnerClassname
		type.addImport("com.fasterxml.jackson.annotation.JsonSubTypes", "Type");
		assertEquals(2, type.getImports().size());
		assertTrue(type.getImports().contains("java.sql.Date"));
		assertTrue(type.getImports().contains("com.fasterxml.jackson.annotation.JsonSubTypes.Type"));

		// Add of an inner class: when the MainClassname is given in the package name
		type.addImport("com.fasterxml.jackson.annotation", "JsonTypeInfo.Id");
		assertEquals(3, type.getImports().size());
		assertTrue(type.getImports().contains("java.sql.Date"));
		assertTrue(type.getImports().contains("com.fasterxml.jackson.annotation.JsonSubTypes.Type"));
		assertTrue(type.getImports().contains("com.fasterxml.jackson.annotation.JsonTypeInfo.Id"));

		// Add of an inner class: when the MainClassname is given in the package name
		type.addImport("com.fasterxml.jackson.annotation", "JsonTypeInfo$Id2");
		assertEquals(4, type.getImports().size());
		assertTrue(type.getImports().contains("java.sql.Date"));
		assertTrue(type.getImports().contains("com.fasterxml.jackson.annotation.JsonSubTypes.Type"));
		assertTrue(type.getImports().contains("com.fasterxml.jackson.annotation.JsonTypeInfo.Id"));
		assertTrue(type.getImports().contains("com.fasterxml.jackson.annotation.JsonTypeInfo.Id2"));
	}

}
