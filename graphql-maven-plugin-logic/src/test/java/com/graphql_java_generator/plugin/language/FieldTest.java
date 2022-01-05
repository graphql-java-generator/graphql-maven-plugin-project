package com.graphql_java_generator.plugin.language;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import com.graphql_java_generator.plugin.Documents;
import com.graphql_java_generator.plugin.conf.GraphQLConfiguration;
import com.graphql_java_generator.plugin.generate_code.GenerateCodeDocumentParser;

import graphql.mavenplugin_notscannedbyspring.AllGraphQLCases_Client_SpringConfiguration;

class FieldTest {

	AbstractApplicationContext ctx = null;
	GenerateCodeDocumentParser documentParser;
	GraphQLConfiguration pluginConfiguration;
	Documents documents;

	@BeforeEach
	void loadApplicationContext() throws IOException {
		ctx = new AnnotationConfigApplicationContext(AllGraphQLCases_Client_SpringConfiguration.class);
		documentParser = ctx.getBean(GenerateCodeDocumentParser.class);
		pluginConfiguration = ctx.getBean(GraphQLConfiguration.class);
		documents = ctx.getBean(Documents.class);
	}

	@Test
	void testGetFieldJavaTypeNamesFromImplementedInterface() throws IOException, NoSuchFieldException {
		List<String> fields;
		// Go, go, go
		documentParser.parseDocuments();

		// Verification

		Type tfoo1 = documentParser.getType("TFoo1");
		//
		fields = new ArrayList<>(tfoo1.getField("id").getFieldJavaTypeNamesFromImplementedInterface());
		assertEquals(1, fields.size());
		assertEquals("java.lang.String", fields.get(0));
		//
		fields = new ArrayList<>(tfoo1.getField("bar").getFieldJavaTypeNamesFromImplementedInterface());
		assertEquals(1, fields.size());
		assertEquals("org.graphql.mavenplugin.junittest.allgraphqlcases_client_springconfiguration.IBar1",
				fields.get(0));

		Type tfoo12 = documentParser.getType("TFoo12");
		//
		fields = new ArrayList<>(tfoo12.getField("id").getFieldJavaTypeNamesFromImplementedInterface());
		assertEquals(1, fields.size());
		assertEquals("java.lang.String", fields.get(0));
		//
		fields = new ArrayList<>(new TreeSet<>(tfoo12.getField("bar").getFieldJavaTypeNamesFromImplementedInterface()));
		assertEquals(2, fields.size());
		assertEquals("org.graphql.mavenplugin.junittest.allgraphqlcases_client_springconfiguration.IBar1",
				fields.get(0));
		assertEquals("org.graphql.mavenplugin.junittest.allgraphqlcases_client_springconfiguration.IBar2",
				fields.get(1));

		Type tfoo3 = documentParser.getType("TFoo3");
		//
		fields = new ArrayList<>(tfoo3.getField("id").getFieldJavaTypeNamesFromImplementedInterface());
		assertEquals(1, fields.size());
		assertEquals("java.lang.String", fields.get(0));
		//
		fields = new ArrayList<>(new TreeSet<>(tfoo3.getField("bar").getFieldJavaTypeNamesFromImplementedInterface()));
		assertEquals(3, fields.size());
		assertEquals("org.graphql.mavenplugin.junittest.allgraphqlcases_client_springconfiguration.IBar1",
				fields.get(0));
		assertEquals("org.graphql.mavenplugin.junittest.allgraphqlcases_client_springconfiguration.IBar12",
				fields.get(1));
		assertEquals("org.graphql.mavenplugin.junittest.allgraphqlcases_client_springconfiguration.IBar2",
				fields.get(2));
	}

}
