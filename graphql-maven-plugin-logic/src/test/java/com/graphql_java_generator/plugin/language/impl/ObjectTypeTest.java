/**
 * 
 */
package com.graphql_java_generator.plugin.language.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.graphql_java_generator.plugin.conf.PluginMode;
import com.graphql_java_generator.plugin.language.FieldTypeAST;
import com.graphql_java_generator.plugin.test.helper.GraphQLConfigurationTestHelper;

/**
 * @author etienne-sf
 *
 */
class ObjectTypeTest {

	String name;
	String packageName;
	PluginMode mode;

	ObjectType objectType;
	GraphQLConfigurationTestHelper pluginConfiguration = new GraphQLConfigurationTestHelper(this);

	@BeforeEach
	void setUp() {
		name = "A name";
		packageName = "a.package.name";
		mode = PluginMode.server;
		objectType = new ObjectType(name, pluginConfiguration);

		FieldImpl f = FieldImpl.builder().documentParser(null).name("field1")
				.fieldTypeAST(FieldTypeAST.builder().graphQLTypeSimpleName("ID").build()).build();
		objectType.getFields().add(f);

		f = FieldImpl.builder().documentParser(null).name("field2")
				.fieldTypeAST(FieldTypeAST.builder().graphQLTypeSimpleName("String").build()).build();
		objectType.getFields().add(f);

	}

	@Test
	void test_getIdentifier_NoId() {
		((FieldImpl) objectType.getFields().get(0)).setId(false);
		assertNull(objectType.getIdentifier(),
				"With no field being an identifier, we should receive null when calling getIdentifier.");
	}

	@Test
	void test_getIdentifier_OneId() {
		assertNotNull(objectType.getIdentifier());
		assertEquals("field1", objectType.getIdentifier().getName(),
				"With only one field being an identifier, we receive this field when calling getIdentifier.");
	}

	@Test
	void test_getIdentifier_TwoId() {
		FieldImpl f = FieldImpl.builder().documentParser(null).name("field3")
				.fieldTypeAST(FieldTypeAST.builder().graphQLTypeSimpleName("ID").build()).build();
		objectType.getFields().add(f);

		assertNull(objectType.getIdentifier(),
				"With only two fields being an identifier, we receive null when calling getIdentifier (multiple identifier is currently not managed).");
	}

}
