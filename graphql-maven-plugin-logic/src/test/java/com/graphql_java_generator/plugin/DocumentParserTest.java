package com.graphql_java_generator.plugin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.graphql_java_generator.plugin.language.DataFetcher;
import com.graphql_java_generator.plugin.language.Field;
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

	private GraphQLDocumentParser documentParser;
	private GraphQLConfigurationTestHelper pluginConfiguration;

	@BeforeEach
	void setUp() throws Exception {
		pluginConfiguration = new GraphQLConfigurationTestHelper(this);
		pluginConfiguration.mode = PluginMode.client; // client as a default value for the tests, here
		pluginConfiguration.packageName = packageName;

		documentParser = new GraphQLDocumentParser();
		documentParser.configuration = pluginConfiguration;

	}

	@Test
	void test_getType() {
		RuntimeException e = assertThrows(RuntimeException.class, () -> documentParser.getType("doesn't exist"));
		assertTrue(e.getMessage().contains("doesn't exist"));
	}

	@Test
	public void test_addTypeAnnotationForClientMode() {
		Type type;

		type = new ObjectType("TheName", "the.package.name", pluginConfiguration);
		documentParser.addTypeAnnotationForClientMode(type);
		assertEquals("@GraphQLObjectType(\"TheName\")", type.getAnnotation(), type.getClass().getName());

		type = new InterfaceType("TheName", "the.package.name", pluginConfiguration);
		documentParser.addTypeAnnotationForClientMode(type);
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
		pluginConfiguration.mode = PluginMode.server;

		type = new ObjectType("TheName", "the.package.name", pluginConfiguration);
		documentParser.addTypeAnnotationForServerMode(type);
		assertEquals("@Entity\n@GraphQLObjectType(\"TheName\")", type.getAnnotation(), type.getClass().getName());

		type = new InterfaceType("TheName", "the.package.name", pluginConfiguration);
		documentParser.addTypeAnnotationForServerMode(type);
		assertEquals("@GraphQLInterfaceType(\"TheName\")", type.getAnnotation(), type.getClass().getName());

		type = new EnumType("TheName", "the.package.name", pluginConfiguration);
		documentParser.addTypeAnnotationForServerMode(type);
		assertEquals("", type.getAnnotation(), type.getClass().getName());
	}

	@Test
	public void test_initDataFetcherForOneObject() {
		// Preparation
		documentParser.types = new HashMap<>();
		documentParser.types.put("Object1", new ObjectType("Object1", "package", pluginConfiguration));
		documentParser.types.put("GraphQLScalar",
				new ScalarType("GraphQLScalar", "packageName", "classSimpleName", pluginConfiguration));
		documentParser.types.put("Interface0", new InterfaceType("Interface0", "packageName", pluginConfiguration));
		documentParser.types.put("Enum0", new EnumType("Enum0", "packageName", pluginConfiguration));
		documentParser.types.put("Object2", new ObjectType("Object2", "package", pluginConfiguration));

		documentParser.objectTypes = new ArrayList<>();
		documentParser.objectTypes.add((ObjectType) documentParser.getType("Object1"));
		documentParser.objectTypes.add((ObjectType) documentParser.getType("Object2"));
		//
		documentParser.scalarTypes.add((ScalarType) documentParser.getType("GraphQLScalar"));
		//
		documentParser.interfaceTypes = new ArrayList<>();
		documentParser.interfaceTypes.add((InterfaceType) documentParser.getType("Interface0"));
		//
		documentParser.enumTypes = new ArrayList<>();
		documentParser.enumTypes.add((EnumType) documentParser.getType("Enum0"));

		ObjectType type = new ObjectType("Package name", pluginConfiguration);
		type.setName("NameOfTheType");

		String[] fields = { "Object1", "GraphQLScalar", "Interface0", "Enum0", "Object2" };
		for (int i = 0; i < 5; i += 1) {
			FieldImpl f = FieldImpl.builder().documentParser(documentParser).build(); // Necessary to manage the
																						// FieldImpl.getType() method
			type.getFields().add(f);

			f.setName("field" + i);
			f.setList((i % 2) == 0);
			f.setGraphQLTypeName(documentParser.getType(fields[i]).getName());
			f.setOwningType(type);

			// Let's create its argument list
			List<Field> args = new ArrayList<>();
			for (int j = 0; j < i; j += 1) {// means: the first field has
				FieldImpl arg = FieldImpl.builder().documentParser(documentParser).build();
				arg.setName("arg" + j);
				arg.setList((j % 2) == 0);
				args.add(arg);
			}
			f.setInputParameters(args);
		}

		///////////////////////////////////////////////////////////////////////////////
		////////////////////// TEST FOR QUERY TYPES
		///////////////////////////////////////////////////////////////////////////////
		documentParser.queryType = type;
		documentParser.interfaceTypes = new ArrayList<>();
		documentParser.objectTypes = new ArrayList<>();
		documentParser.dataFetchers = new ArrayList<>();
		documentParser.dataFetchersDelegates = new ArrayList<>();
		documentParser.enumTypes.add(new EnumType("AnEnumType", "packageName", pluginConfiguration));
		documentParser.scalarTypes.add(new ScalarType("Float", "java.lang", "Float", pluginConfiguration));
		documentParser.scalarTypes.add((ScalarType) documentParser.getType("GraphQLScalar"));

		type.setRequestType("AQuery");

		// Go, go, go
		documentParser.initDataFetcherForOneObject(type);

		// Verification
		int i = 0;
		assertEquals(5, documentParser.dataFetchers.size(), "size");
		//
		// For query types, there must be a Data Fetcher for each field.
		checkDataFetcher(documentParser.dataFetchers.get(i), "field0", true, type, null,
				type.getFields().get(i++).getInputParameters());
		checkDataFetcher(documentParser.dataFetchers.get(i), "field1", false, type, null,
				type.getFields().get(i++).getInputParameters());
		checkDataFetcher(documentParser.dataFetchers.get(i), "field2", true, type, null,
				type.getFields().get(i++).getInputParameters());
		checkDataFetcher(documentParser.dataFetchers.get(i), "field3", false, type, null,
				type.getFields().get(i++).getInputParameters());
		checkDataFetcher(documentParser.dataFetchers.get(i), "field4", true, type, null,
				type.getFields().get(i++).getInputParameters());
		//
		// There should be one DataFetchersDelegate, as we have only one type.
		assertEquals(1, documentParser.dataFetchersDelegates.size(), "nb DataFetchersDelegates");
		assertEquals(5, documentParser.dataFetchersDelegates.get(0).getDataFetchers().size(),
				"nb DataFetchers in the DataFetchersDelegate");

		/////////////////////////////////////////////////////////////////////////////// ::
		////////////////////// TEST FOR OBJECT TYPES
		/////////////////////////////////////////////////////////////////////////////// ::
		documentParser.queryType = type;
		documentParser.interfaceTypes = new ArrayList<>();
		documentParser.objectTypes = new ArrayList<>();
		documentParser.enumTypes = new ArrayList<>();
		documentParser.scalarTypes = new ArrayList<>();
		documentParser.dataFetchers = new ArrayList<>();
		documentParser.dataFetchersDelegates = new ArrayList<>();
		//
		documentParser.objectTypes.add(type);
		documentParser.enumTypes.add(new EnumType("AnEnumType", "packageName", pluginConfiguration));
		documentParser.scalarTypes.add(new ScalarType("Float", "java.lang", "Float", pluginConfiguration));
		documentParser.scalarTypes.add((ScalarType) documentParser.getType("GraphQLScalar"));
		documentParser.enumTypes.add((EnumType) documentParser.getType("Enum0"));
		documentParser.fillTypesMap();

		type.setRequestType(null);

		// Go, go, go
		documentParser.initDataFetcherForOneObject(type);

		// Verification
		i = 0;
		assertEquals(3, documentParser.dataFetchers.size(), "size");
		//
		// For non query types, there must be a Data Fetcher only for non GraphQLScalar and non Enum field.
		checkDataFetcher(documentParser.dataFetchers.get(i++), "field0", true, type, type.getName(),
				type.getFields().get(0).getInputParameters());
		checkDataFetcher(documentParser.dataFetchers.get(i++), "field2", true, type, type.getName(),
				type.getFields().get(2).getInputParameters());
		checkDataFetcher(documentParser.dataFetchers.get(i++), "field4", true, type, type.getName(),
				type.getFields().get(4).getInputParameters());
		//
		// There should be one DataFetchersDelegate, as we have only one type.
		assertEquals(1, documentParser.dataFetchersDelegates.size(), "nb DataFetchersDelegates");
		assertEquals(3, documentParser.dataFetchersDelegates.get(0).getDataFetchers().size(),
				"nb DataFetchers in the DataFetchersDelegate");

		/////////////////////////////////////////////////////////////////////////////// ::
		////////////////////// TEST FOR INTERFACE TYPES
		/////////////////////////////////////////////////////////////////////////////// ::
		documentParser.queryType = null;
		documentParser.interfaceTypes = new ArrayList<>();
		documentParser.objectTypes = new ArrayList<>();
		documentParser.enumTypes = new ArrayList<>();
		documentParser.scalarTypes = new ArrayList<>();
		documentParser.dataFetchers = new ArrayList<>();
		documentParser.dataFetchersDelegates = new ArrayList<>();
		//
		documentParser.interfaceTypes.add(new InterfaceType("AnInterface", "a.package", pluginConfiguration));
		documentParser.enumTypes.add(new EnumType("AnEnumType", "packageName", pluginConfiguration));
		documentParser.scalarTypes.add(new ScalarType("Float", "java.lang", "Float", pluginConfiguration));

		type.setRequestType(null);

		// Go, go, go
		documentParser.initDataFetcherForOneObject(type);

		// Verification
		i = 0;
		assertEquals(3, documentParser.dataFetchers.size(), "size");
		//
		// For non query types, there must be a Data Fetcher only for non GraphQLScalar and non Enum field.
		checkDataFetcher(documentParser.dataFetchers.get(i++), "field0", true, type, type.getName(),
				type.getFields().get(0).getInputParameters());
		checkDataFetcher(documentParser.dataFetchers.get(i++), "field2", true, type, type.getName(),
				type.getFields().get(2).getInputParameters());
		checkDataFetcher(documentParser.dataFetchers.get(i++), "field4", true, type, type.getName(),
				type.getFields().get(4).getInputParameters());
		//
		// There should be one DataFetchersDelegate, as we have only one type.
		assertEquals(1, documentParser.dataFetchersDelegates.size(), "nb DataFetchersDelegates");
		assertEquals(3, documentParser.dataFetchersDelegates.get(0).getDataFetchers().size(),
				"nb DataFetchers in the DataFetchersDelegate");
	}

	private void checkDataFetcher(DataFetcher dataFetcher, String name, boolean list, Type type,
			String graphQLOriginType, List<Field> inputParameters) {
		assertEquals(name, dataFetcher.getName(), "name");
		assertEquals(list, dataFetcher.getField().isList(), "list");
		assertEquals(type, dataFetcher.getField().getOwningType(), "type");
		assertEquals(inputParameters, dataFetcher.getField().getInputParameters(), "arguments");
		assertEquals(graphQLOriginType, dataFetcher.getGraphQLOriginType(), "graphQLOriginType");
	}

}
