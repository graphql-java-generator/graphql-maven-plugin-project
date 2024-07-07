package com.graphql_java_generator.plugin.generate_code;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.graphql_java_generator.plugin.conf.PluginMode;
import com.graphql_java_generator.plugin.language.DataFetcher;
import com.graphql_java_generator.plugin.language.Field;
import com.graphql_java_generator.plugin.language.FieldTypeAST;
import com.graphql_java_generator.plugin.language.Type;
import com.graphql_java_generator.plugin.language.impl.EnumType;
import com.graphql_java_generator.plugin.language.impl.FieldImpl;
import com.graphql_java_generator.plugin.language.impl.InterfaceType;
import com.graphql_java_generator.plugin.language.impl.ObjectType;
import com.graphql_java_generator.plugin.language.impl.ScalarType;
import com.graphql_java_generator.plugin.test.helper.GraphQLConfigurationTestHelper;

/**
 * 
 * @author etienne-sf
 */

class DocumentParserTest {

	String packageName = "org.graphql.test.generate";

	private GenerateCodeDocumentParser documentParser;
	private GraphQLConfigurationTestHelper pluginConfiguration;

	@BeforeEach
	void setUp() throws Exception {
		this.pluginConfiguration = new GraphQLConfigurationTestHelper(this);
		this.pluginConfiguration.mode = PluginMode.client; // client as a default value for the tests, here
		this.pluginConfiguration.packageName = this.packageName;

		this.documentParser = new GenerateCodeDocumentParser(this.pluginConfiguration);
	}

	@Test
	void test_getType() {
		RuntimeException e = assertThrows(RuntimeException.class, () -> this.documentParser.getType("doesn't exist"));
		assertTrue(e.getMessage().contains("doesn't exist"));
	}

	@Test
	public void test_addTypeAnnotationForClientMode() {
		Type type;

		type = new ObjectType("TheName", this.pluginConfiguration, this.documentParser);
		this.documentParser.addTypeAnnotationForClientMode(type);
		assertEquals("@GraphQLObjectType(\"TheName\")", type.getAnnotation(), type.getClass().getName());

		type = new InterfaceType("TheName", this.pluginConfiguration, this.documentParser);
		this.documentParser.addTypeAnnotationForClientMode(type);
		assertEquals(
				"@JsonTypeInfo(use = Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = \"__typename\", visible = true)\n"
						+ "@JsonSubTypes({ })\n" //
						+ "@GraphQLInterfaceType(\"TheName\")",
				type.getAnnotation(), type.getClass().getName());
		// Ok, that won't compile, as there is no sub types. But the JUnit test is OK ;-)
	}

	@Test
	public void test_addTypeAnnotationForServerMode() {
		Type type;
		this.pluginConfiguration.mode = PluginMode.server;

		type = new ObjectType("TheName", this.pluginConfiguration, this.documentParser);
		this.documentParser.addTypeAnnotationForServerMode(type);
		assertEquals("@Entity\n@GraphQLObjectType(\"TheName\")", type.getAnnotation(), type.getClass().getName());

		type = new InterfaceType("TheName", this.pluginConfiguration, this.documentParser);
		this.documentParser.addTypeAnnotationForServerMode(type);
		assertEquals("@GraphQLInterfaceType(\"TheName\")", type.getAnnotation(), type.getClass().getName());

		type = new EnumType("TheName", this.pluginConfiguration, this.documentParser);
		this.documentParser.addTypeAnnotationForServerMode(type);
		assertEquals("", type.getAnnotation(), type.getClass().getName());
	}

	@Test
	public void test_initDataFetcherForOneObject() {
		// Preparation
		this.documentParser.setTypes(new HashMap<>());
		this.documentParser.getTypes().put("Object1",
				new ObjectType("Object1", this.pluginConfiguration, this.documentParser));
		this.documentParser.getTypes().put("GraphQLScalar", new ScalarType("GraphQLScalar", "packageName",
				"classSimpleName", this.pluginConfiguration, this.documentParser));
		this.documentParser.getTypes().put("Interface0",
				new InterfaceType("Interface0", this.pluginConfiguration, this.documentParser));
		this.documentParser.getTypes().put("Enum0",
				new EnumType("Enum0", this.pluginConfiguration, this.documentParser));
		this.documentParser.getTypes().put("Object2",
				new ObjectType("Object2", this.pluginConfiguration, this.documentParser));

		this.documentParser.setObjectTypes(new ArrayList<>());
		this.documentParser.getObjectTypes().add((ObjectType) this.documentParser.getType("Object1"));
		this.documentParser.getObjectTypes().add((ObjectType) this.documentParser.getType("Object2"));
		//
		this.documentParser.getScalarTypes().add((ScalarType) this.documentParser.getType("GraphQLScalar"));
		//
		this.documentParser.setInterfaceTypes(new ArrayList<>());
		this.documentParser.getInterfaceTypes().add((InterfaceType) this.documentParser.getType("Interface0"));
		//
		this.documentParser.setEnumTypes(new ArrayList<>());
		this.documentParser.getEnumTypes().add((EnumType) this.documentParser.getType("Enum0"));

		ObjectType type = new ObjectType("NameOfTheType", this.pluginConfiguration, this.documentParser);

		String[] fields = { "Object1", "GraphQLScalar", "Interface0", "Enum0", "Object2" };
		for (int i = 0; i < 5; i += 1) {

			// When the field is a list, there is two fieldTypeAST: the list, then the real type
			FieldTypeAST fieldTypeAST;
			if ((i % 2) == 0) {
				FieldTypeAST realType = new FieldTypeAST(this.documentParser.getType(fields[i]).getName());
				fieldTypeAST = FieldTypeAST.builder().listDepth(1).listItemFieldTypeAST(realType).build();
			} else {
				fieldTypeAST = FieldTypeAST.builder().listDepth(0)
						.graphQLTypeSimpleName(this.documentParser.getType(fields[i]).getName()).build();
			}
			FieldImpl f = FieldImpl.builder().documentParser(this.documentParser).name("field" + i).owningType(type)
					.fieldTypeAST(fieldTypeAST).build();
			type.getFields().add(f);

			// Let's create its argument list
			List<Field> args = new ArrayList<>();
			for (int j = 0; j < i; j += 1) {// means: the first field has
				// When the field is a list, there is two fieldTypeAST: the list, then the real type
				FieldTypeAST argTypeAST;
				if ((j % 2) == 0) {
					FieldTypeAST realType = new FieldTypeAST(this.documentParser.getType(fields[i]).getName());
					argTypeAST = FieldTypeAST.builder().listDepth(1).listItemFieldTypeAST(realType).build();
				} else {
					argTypeAST = FieldTypeAST.builder().listDepth(0)
							.graphQLTypeSimpleName(this.documentParser.getType(fields[i]).getName()).build();
				}
				FieldImpl arg = FieldImpl.builder().documentParser(this.documentParser).name("arg" + j)
						.fieldTypeAST(argTypeAST).build();
				args.add(arg);
			}
			f.setInputParameters(args);
		}

		///////////////////////////////////////////////////////////////////////////////
		////////////////////// TEST FOR QUERY TYPES
		///////////////////////////////////////////////////////////////////////////////
		this.documentParser.setQueryType(type);
		this.documentParser.setInterfaceTypes(new ArrayList<>());
		this.documentParser.setObjectTypes(new ArrayList<>());
		this.documentParser.dataFetchers = new ArrayList<>();
		this.documentParser.dataFetchersDelegates = new ArrayList<>();
		this.documentParser.getEnumTypes()
				.add(new EnumType("AnEnumType", this.pluginConfiguration, this.documentParser));
		this.documentParser.getScalarTypes()
				.add(new ScalarType("Float", "java.lang", "Float", this.pluginConfiguration, this.documentParser));
		this.documentParser.getScalarTypes().add((ScalarType) this.documentParser.getType("GraphQLScalar"));

		type.setRequestType("AQuery");

		// Go, go, go
		this.documentParser.initDataFetcherForOneObject(type);

		// Verification
		int i = 0;
		assertEquals(5, this.documentParser.dataFetchers.size(), "size");
		//
		// For query types, there must be a Data Fetcher for each field.
		checkDataFetcher(this.documentParser.dataFetchers.get(i), "field0", 1, type, null,
				type.getFields().get(i++).getInputParameters());
		checkDataFetcher(this.documentParser.dataFetchers.get(i), "field1", 0, type, null,
				type.getFields().get(i++).getInputParameters());
		checkDataFetcher(this.documentParser.dataFetchers.get(i), "field2", 1, type, null,
				type.getFields().get(i++).getInputParameters());
		checkDataFetcher(this.documentParser.dataFetchers.get(i), "field3", 0, type, null,
				type.getFields().get(i++).getInputParameters());
		checkDataFetcher(this.documentParser.dataFetchers.get(i), "field4", 1, type, null,
				type.getFields().get(i++).getInputParameters());
		//
		// There should be one DataFetchersDelegate, as we have only one type.
		assertEquals(1, this.documentParser.dataFetchersDelegates.size(), "nb DataFetchersDelegates");
		assertEquals(5, this.documentParser.dataFetchersDelegates.get(0).getDataFetchers().size(),
				"nb DataFetchers in the DataFetchersDelegate");

		/////////////////////////////////////////////////////////////////////////////// ::
		////////////////////// TEST FOR OBJECT TYPES
		/////////////////////////////////////////////////////////////////////////////// ::
		this.documentParser.setQueryType(type);
		this.documentParser.setInterfaceTypes(new ArrayList<>());
		this.documentParser.setObjectTypes(new ArrayList<>());
		this.documentParser.setEnumTypes(new ArrayList<>());
		this.documentParser.setScalarTypes(new ArrayList<>());
		this.documentParser.dataFetchers = new ArrayList<>();
		this.documentParser.dataFetchersDelegates = new ArrayList<>();
		//
		this.documentParser.getObjectTypes().add(type);
		this.documentParser.getEnumTypes()
				.add(new EnumType("AnEnumType", this.pluginConfiguration, this.documentParser));
		this.documentParser.getScalarTypes()
				.add(new ScalarType("Float", "java.lang", "Float", this.pluginConfiguration, this.documentParser));
		this.documentParser.getScalarTypes().add((ScalarType) this.documentParser.getType("GraphQLScalar"));
		this.documentParser.getEnumTypes().add((EnumType) this.documentParser.getType("Enum0"));
		this.documentParser.fillTypesMap();

		type.setRequestType(null);

		// Go, go, go
		this.documentParser.initDataFetcherForOneObject(type);

		// Verification
		i = 0;
		assertEquals(3, this.documentParser.dataFetchers.size(), "size");
		//
		// For non query types, there must be a Data Fetcher only for non GraphQLScalar and non Enum field.
		checkDataFetcher(this.documentParser.dataFetchers.get(i++), "field0", 1, type, type.getName(),
				type.getFields().get(0).getInputParameters());
		checkDataFetcher(this.documentParser.dataFetchers.get(i++), "field2", 1, type, type.getName(),
				type.getFields().get(2).getInputParameters());
		checkDataFetcher(this.documentParser.dataFetchers.get(i++), "field4", 1, type, type.getName(),
				type.getFields().get(4).getInputParameters());
		//
		// There should be one DataFetchersDelegate, as we have only one type.
		assertEquals(1, this.documentParser.dataFetchersDelegates.size(), "nb DataFetchersDelegates");
		assertEquals(3, this.documentParser.dataFetchersDelegates.get(0).getDataFetchers().size(),
				"nb DataFetchers in the DataFetchersDelegate");

		/////////////////////////////////////////////////////////////////////////////// ::
		////////////////////// TEST FOR INTERFACE TYPES
		/////////////////////////////////////////////////////////////////////////////// ::
		this.documentParser.setQueryType(null);
		this.documentParser.setInterfaceTypes(new ArrayList<>());
		this.documentParser.setObjectTypes(new ArrayList<>());
		this.documentParser.setEnumTypes(new ArrayList<>());
		this.documentParser.setScalarTypes(new ArrayList<>());
		this.documentParser.dataFetchers = new ArrayList<>();
		this.documentParser.dataFetchersDelegates = new ArrayList<>();
		//
		this.documentParser.getInterfaceTypes()
				.add(new InterfaceType("AnInterface", this.pluginConfiguration, this.documentParser));
		this.documentParser.getEnumTypes()
				.add(new EnumType("AnEnumType", this.pluginConfiguration, this.documentParser));
		this.documentParser.getScalarTypes()
				.add(new ScalarType("Float", "java.lang", "Float", this.pluginConfiguration, this.documentParser));

		type.setRequestType(null);

		// Go, go, go
		this.documentParser.initDataFetcherForOneObject(type);

		// Verification
		i = 0;
		assertEquals(3, this.documentParser.dataFetchers.size(), "size");
		//
		// For non query types, there must be a Data Fetcher only for non GraphQLScalar and non Enum field.
		checkDataFetcher(this.documentParser.dataFetchers.get(i++), "field0", 1, type, type.getName(),
				type.getFields().get(0).getInputParameters());
		checkDataFetcher(this.documentParser.dataFetchers.get(i++), "field2", 1, type, type.getName(),
				type.getFields().get(2).getInputParameters());
		checkDataFetcher(this.documentParser.dataFetchers.get(i++), "field4", 1, type, type.getName(),
				type.getFields().get(4).getInputParameters());
		//
		// There should be one DataFetchersDelegate, as we have only one type.
		assertEquals(1, this.documentParser.dataFetchersDelegates.size(), "nb DataFetchersDelegates");
		assertEquals(3, this.documentParser.dataFetchersDelegates.get(0).getDataFetchers().size(),
				"nb DataFetchers in the DataFetchersDelegate");
	}

	private void checkDataFetcher(DataFetcher dataFetcher, String name, int list, Type type, String graphQLOriginType,
			List<Field> inputParameters) {
		assertEquals(name, dataFetcher.getName(), "name");
		assertEquals(list, dataFetcher.getField().getFieldTypeAST().getListDepth(), "list");
		assertEquals(type, dataFetcher.getField().getOwningType(), "type");
		assertEquals(inputParameters, dataFetcher.getField().getInputParameters(), "arguments");
		if (graphQLOriginType == null)
			assertNull(dataFetcher.getGraphQLOriginType(), "graphQLOriginType");
		else
			assertEquals(graphQLOriginType, dataFetcher.getGraphQLOriginType().getClassSimpleName(),
					"graphQLOriginType");
	}

}
