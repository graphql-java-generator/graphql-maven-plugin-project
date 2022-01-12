package com.graphql_java_generator.plugin.language;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
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

	// @Test
	// void testGetFieldJavaTypeNamesFromImplementedInterface() throws IOException, NoSuchFieldException {
	// List<String> fields;
	// // Go, go, go
	// documentParser.parseDocuments();
	//
	// // Verification
	//
	// Type tfoo1 = documentParser.getType("TFoo1");
	// //
	// fields = new ArrayList<>(tfoo1.getField("id").getFieldJavaTypeNamesFromImplementedInterface());
	// assertEquals(1, fields.size());
	// assertEquals("java.lang.String", fields.get(0));
	// //
	// fields = new ArrayList<>(tfoo1.getField("bar").getFieldJavaTypeNamesFromImplementedInterface());
	// assertEquals(1, fields.size());
	// assertEquals("org.graphql.mavenplugin.junittest.allgraphqlcases_client_springconfiguration.IBar1",
	// fields.get(0));
	//
	// Type tfoo12 = documentParser.getType("TFoo12");
	// //
	// fields = new ArrayList<>(tfoo12.getField("id").getFieldJavaTypeNamesFromImplementedInterface());
	// assertEquals(1, fields.size());
	// assertEquals("java.lang.String", fields.get(0));
	// //
	// fields = new ArrayList<>(new TreeSet<>(tfoo12.getField("bar").getFieldJavaTypeNamesFromImplementedInterface()));
	// assertEquals(2, fields.size());
	// assertEquals("org.graphql.mavenplugin.junittest.allgraphqlcases_client_springconfiguration.IBar1",
	// fields.get(0));
	// assertEquals("org.graphql.mavenplugin.junittest.allgraphqlcases_client_springconfiguration.IBar2",
	// fields.get(1));
	//
	// Type tfoo3 = documentParser.getType("TFoo3");
	// //
	// fields = new ArrayList<>(tfoo3.getField("id").getFieldJavaTypeNamesFromImplementedInterface());
	// assertEquals(1, fields.size());
	// assertEquals("java.lang.String", fields.get(0));
	// //
	// fields = new ArrayList<>(new TreeSet<>(tfoo3.getField("bar").getFieldJavaTypeNamesFromImplementedInterface()));
	// assertEquals(3, fields.size());
	// assertEquals("org.graphql.mavenplugin.junittest.allgraphqlcases_client_springconfiguration.IBar1",
	// fields.get(0));
	// assertEquals("org.graphql.mavenplugin.junittest.allgraphqlcases_client_springconfiguration.IBar12",
	// fields.get(1));
	// assertEquals("org.graphql.mavenplugin.junittest.allgraphqlcases_client_springconfiguration.IBar2",
	// fields.get(2));
	// }
	//
	// /** Test of the generated code : the setter generated after issue #114 should work */
	// @Test
	// void test_Issue1114_checkGenerateCode() {
	// final String TBAR_ID = "TBar's id";
	//
	// // Case 1: TFoo1
	// //
	// TBar1 tbar1 = new TBar1();
	// tbar1.setId(TBAR_ID);
	// TFoo1 tfoo1 = new TFoo1();
	// //
	// tfoo1.setBar(tbar1); // set(TBar1)
	// assertEquals(TBAR_ID, tfoo1.getBar().getId());
	// //
	// tfoo1.setBar(null);
	// assertEquals(null, tfoo1.getBar().getId(), "The bar attribute has been cleared");
	// tfoo1.setBar((IBar1) tbar1);// set(IBar1)
	// assertEquals(TBAR_ID, tfoo1.getBar().getId());
	//
	// // Case 2: TFoo12
	// //
	// TBar12 tbar12 = new TBar12();
	// tbar12.setId(TBAR_ID);
	// TFoo12 tfoo12 = new TFoo12();
	// //
	// tfoo12.setBar(tbar12);
	// assertEquals(TBAR_ID, tfoo12.getBar().getId());
	// //
	// tfoo12.setBar(null);
	// assertEquals(null, tfoo12.getBar().getId(), "The bar attribute has been cleared");
	// tfoo12.setBar((IBar1) tbar12);
	// assertEquals(TBAR_ID, tfoo12.getBar().getId());
	// //
	// tfoo12.setBar(null);
	// assertEquals(null, tfoo12.getBar().getId(), "The bar attribute has been cleared");
	// tfoo12.setBar((IBar2) tbar12);
	// assertEquals(TBAR_ID, tfoo12.getBar().getId());
	//
	// // Case 3: TFoo3
	// //
	// TFoo3 tfoo3 = new TFoo3();
	// //
	// tfoo3.setBar(tbar12);
	// assertEquals(TBAR_ID, tfoo3.getBar().getId());
	// //
	// tfoo3.setBar(null);
	// assertEquals(null, tfoo3.getBar().getId(), "The bar attribute has been cleared");
	// tfoo3.setBar((IBar1) tbar12);
	// assertEquals(TBAR_ID, tfoo3.getBar().getId());
	// //
	// tfoo3.setBar(null);
	// assertEquals(null, tfoo3.getBar().getId(), "The bar attribute has been cleared");
	// tfoo3.setBar((IBar2) tbar12);
	// assertEquals(TBAR_ID, tfoo3.getBar().getId());
	//
	// // Case 4: ?? (with a list)
	// //
	//
	// }

}
