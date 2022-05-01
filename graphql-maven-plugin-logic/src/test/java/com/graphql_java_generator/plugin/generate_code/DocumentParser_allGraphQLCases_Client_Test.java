package com.graphql_java_generator.plugin.generate_code;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import com.graphql_java_generator.plugin.Documents;
import com.graphql_java_generator.plugin.conf.GraphQLConfiguration;
import com.graphql_java_generator.plugin.language.impl.EnumType;
import com.graphql_java_generator.plugin.language.impl.InterfaceType;
import com.graphql_java_generator.plugin.language.impl.ObjectType;
import com.graphql_java_generator.plugin.language.impl.ScalarType;
import com.graphql_java_generator.plugin.language.impl.UnionType;

import graphql.mavenplugin_notscannedbyspring.AllGraphQLCases_Client_SpringConfiguration;

/**
 * 
 * @author etienne-sf
 */
@Execution(ExecutionMode.CONCURRENT)
class DocumentParser_allGraphQLCases_Client_Test {

	AbstractApplicationContext ctx = null;
	GenerateCodeDocumentParser generateCodeDocumentParser;
	GraphQLConfiguration pluginConfiguration;
	Documents documents;

	@BeforeEach
	void loadApplicationContext() throws IOException {
		ctx = new AnnotationConfigApplicationContext(AllGraphQLCases_Client_SpringConfiguration.class);
		generateCodeDocumentParser = ctx.getBean(GenerateCodeDocumentParser.class);
		pluginConfiguration = ctx.getBean(GraphQLConfiguration.class);
		documents = ctx.getBean(Documents.class);
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_parseOneDocument_allGraphQLCases() throws IOException {
		// Go, go, go
		generateCodeDocumentParser.parseDocuments();

		// Verification

		// Check of the CustomJacksonDeserializers
		// Let's check that there are two CustomJacksonDeserializers for Float, with depth of 1 and 2
		boolean foundDepth1 = false;
		boolean foundDepth2 = false;
		for (CustomDeserializer cd : generateCodeDocumentParser.getCustomDeserializers()) {
			if (cd.getGraphQLTypeName().equals("Float")) {
				if (cd.getListDepth() == 1) {
					foundDepth1 = true;
				} else if (cd.getListDepth() == 2) {
					foundDepth2 = true;
				} else {
					fail("Unexpected depth for Float type: " + cd.getListDepth());
				}
			}
		} // for

		assertTrue(foundDepth1, "CustomDeserializer of depth 1 found for Float");
		assertTrue(foundDepth2, "CustomDeserializer of depth 2 found for Float");
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_GraphQLExtensions() throws IOException {
		// Go, go, go
		generateCodeDocumentParser.parseDocuments();

		// Verification

		// enum
		EnumType e = generateCodeDocumentParser.getType("Unit", EnumType.class, true);
		assertEquals(1, e.getAppliedDirectives().stream()
				.filter(d -> d.getDirective().getName().equals("testExtendKeyword")).count(),
				"The @testExtendKeyword directive has been added to the enum");
		assertEquals(1, e.getValues().stream().filter(v -> v.getName().equals("YEAR")).count(),
				"The YEAR value still exists in the enum");
		assertEquals(1, e.getValues().stream().filter(v -> v.getName().equals("MONTH")).count(),
				"The MONTH value still exists in the enum");
		assertEquals(1, e.getValues().stream().filter(v -> v.getName().equals("DAY")).count(),
				"The DAY value still exists in the enum");
		assertEquals(1, e.getValues().stream().filter(v -> v.getName().equals("HOUR")).count(),
				"The HOUR value has been added to the enum");
		assertEquals(1, e.getValues().stream().filter(v -> v.getName().equals("MINUTE")).count(),
				"The MINUTE value has been added to the enum");
		assertEquals(1, e.getValues().stream().filter(v -> v.getName().equals("SECOND")).count(),
				"The SECOND value has been added to the enum");

		// input
		ObjectType input = generateCodeDocumentParser.getType("AllFieldCasesInput", ObjectType.class, true);
		assertTrue(input.isInputType(), "Our input is actually an input");
		assertEquals(1,
				input.getAppliedDirectives().stream()
						.filter(d -> d.getDirective().getName().equals("testExtendKeyword")).count(),
				"The @testExtendKeyword directive has been added to the input type");
		assertEquals(1, input.getFields().stream().filter(f -> f.getName().equals("extendedField")).count(),
				"The extendedField field has been added to the input type");

		// interface
		InterfaceType i = generateCodeDocumentParser.getType("AllFieldCasesInterface", InterfaceType.class, true);
		// Interface extension may not add implemented interface to an existing interface (as of juin 2018 GraphQL
		// specs)
		// assertTrue(i.getImplementz().contains("interfaceToTestExtendKeyword"),
		// "The interfaceToTestExtendKeyword interface has been added to the interface");
		assertEquals(1, i.getAppliedDirectives().stream()
				.filter(d -> d.getDirective().getName().equals("testExtendKeyword")).count(),
				"The @testExtendKeyword directive has been added to the interface");
		assertEquals(1, i.getFields().stream().filter(f -> f.getName().equals("extendedField")).count(),
				"The extendedField field has been added to the interface");

		// scalar
		ScalarType s = generateCodeDocumentParser.getType("Long", ScalarType.class, true);
		assertEquals(1, s.getAppliedDirectives().stream()
				.filter(d -> d.getDirective().getName().equals("testExtendKeyword")).count(),
				"The @testExtendKeyword directive has been added to the scalar");

		// schema
		assertEquals(1,
				generateCodeDocumentParser.getSchemaDirectives().stream()
						.filter(d -> d.getDirective().getName().equals("testExtendKeyword")).count(),
				"The @testExtendKeyword directive has been added to the schema");
		assertNotNull(generateCodeDocumentParser.getSubscriptionType(),
				"The subscription has been added to the schema (not null)");
		assertEquals("subscription", generateCodeDocumentParser.getSubscriptionType().getName(),
				"The subscription has been added to the schema (name)");

		// type
		ObjectType t = generateCodeDocumentParser.getType("AllFieldCasesInterface", ObjectType.class, true);
		assertFalse(t.isInputType(), "Our type is not an input");
		assertTrue(t.getImplementz().contains("interfaceToTestExtendKeyword"),
				"The interfaceToTestExtendKeyword interface has been added to the interface");
		assertEquals(1, t.getAppliedDirectives().stream()
				.filter(d -> d.getDirective().getName().equals("testExtendKeyword")).count(),
				"The @testExtendKeyword directive has been added to the interface");
		assertEquals(1, t.getFields().stream().filter(f -> f.getName().equals("extendedField")).count(),
				"The extendedField field has been added to the interface");

		// union
		UnionType u = generateCodeDocumentParser.getType("AllFieldCasesInterface", UnionType.class, true);
		assertEquals(1, u.getAppliedDirectives().stream()
				.filter(d -> d.getDirective().getName().equals("testExtendKeyword")).count(),
				"The @testExtendKeyword directive has been added to the union");
		assertEquals(1, u.getMemberOfUnions().stream().filter(u0 -> u0.getName().equals("Pet")).count(),
				"The Pet type has been added to the union");

		fail("not yet implemented");
	}
}
