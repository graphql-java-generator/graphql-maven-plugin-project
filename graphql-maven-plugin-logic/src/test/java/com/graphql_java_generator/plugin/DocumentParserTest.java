package com.graphql_java_generator.plugin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

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
import com.graphql_java_generator.plugin.test.helper.PluginConfigurationTestHelper;

/**
 * 
 * @author EtienneSF
 */

class DocumentParserTest {

	String packageName = "org.graphql.test.generate";

	private DocumentParser documentParser;
	private PluginConfigurationTestHelper pluginConfiguration;

	@BeforeEach
	void setUp() throws Exception {
		pluginConfiguration = new PluginConfigurationTestHelper(this);
		pluginConfiguration.mode = PluginMode.client; // client as a default value for the tests, here
		pluginConfiguration.packageName = packageName;

		documentParser = new DocumentParser();
		documentParser.pluginConfiguration = pluginConfiguration;

	}

	@Test
	void test_defineDefaultInterfaceImplementationClassName() {
		// Preparation
		ObjectType o = new ObjectType(packageName, PluginMode.server);
		o.setName("interfaceImpl");
		documentParser.interfaceTypes = new ArrayList<>();
		documentParser.objectTypes = new ArrayList<>();
		documentParser.objectTypes.add(o);

		InterfaceType i1 = new InterfaceType(packageName, PluginMode.server);
		i1.setName("interface");
		documentParser.interfaceTypes.add(i1);

		InterfaceType i2 = new InterfaceType(packageName, PluginMode.server);
		i2.setName("anotherInterface");
		documentParser.interfaceTypes.add(i2);

		// Some verifications, before starting
		assertEquals(1, documentParser.objectTypes.size(), "Nb objects (before)");
		assertEquals(2, documentParser.interfaceTypes.size(), "Nb interfaces (before)");
		assertNull(i1.getDefaultImplementation(), "No default implementation, after instance creation");
		assertNull(i2.getDefaultImplementation(), "No default implementation, after instance creation");

		// Go, go, go
		int count = documentParser.defineDefaultInterfaceImplementationClassName();

		// Verification
		assertEquals(2, count, "Nb interfaces created, according to defineDefaultInterfaceImplementationClassName()");
		assertEquals(3, documentParser.objectTypes.size(), "Nb objects (after)");
		assertEquals(2, documentParser.interfaceTypes.size(), "Nb interfaces (after)");

		// Checks for the first object, by two different pathes
		assertEquals("interfaceImpl1", i1.getDefaultImplementation().getName(),
				"Default implementation can not be interfaceImpl, as it is already used (path 1)");
		assertEquals("interfaceImpl1", documentParser.objectTypes.get(1).getName(),
				"Default implementation can not be interfaceImpl, as it is already used (path 2)");

		// Checks for the second object, by two different pathes
		assertEquals("anotherInterfaceImpl", i2.getDefaultImplementation().getName(),
				"Default implementation is the standard name, as there is no conflict (general case) (path 1)");
		assertEquals("anotherInterfaceImpl", documentParser.objectTypes.get(2).getName(),
				"Default implementation is the standard name, as there is no conflict (general case) (path 2)");
	}

	@Test
	public void test_addTypeAnnotationForClientMode() {
		Type type;

		type = new ObjectType("TheName", "the.package.name", PluginMode.client);
		documentParser.addTypeAnnotationForClientMode(type);
		assertEquals("", type.getAnnotation(), type.getClass().getName());

		type = new InterfaceType("TheName", "the.package.name", PluginMode.client);
		documentParser.addTypeAnnotationForClientMode(type);
		assertEquals("", type.getAnnotation(), type.getClass().getName());
	}

	@Test
	public void test_addTypeAnnotationForServerMode() {
		Type type;
		pluginConfiguration.mode = PluginMode.server;

		type = new ObjectType("TheName", "the.package.name", PluginMode.server);
		documentParser.addTypeAnnotationForServerMode(type);
		assertEquals("@Entity", type.getAnnotation(), type.getClass().getName());

		type = new InterfaceType("TheName", "the.package.name", PluginMode.server);
		documentParser.addTypeAnnotationForServerMode(type);
		assertEquals("", type.getAnnotation(), type.getClass().getName());

		type = new EnumType("TheName", "the.package.name", PluginMode.server);
		documentParser.addTypeAnnotationForServerMode(type);
		assertEquals("", type.getAnnotation(), type.getClass().getName());
	}

	@Test
	public void test_initDataFetcherForOneObject() {
		// Preparation
		documentParser.types = new HashMap<>();
		documentParser.types.put("Object1", new ObjectType("Object1", "package", PluginMode.server));
		documentParser.types.put("GraphQLScalar",
				new ScalarType("GraphQLScalar", "packageName", "classSimpleName", PluginMode.server));
		documentParser.types.put("Interface0", new InterfaceType("Interface0", "packageName", PluginMode.server));
		documentParser.types.put("Enum0", new EnumType("Enum0", "packageName", PluginMode.server));
		documentParser.types.put("Object2", new ObjectType("Object2", "package", PluginMode.server));

		documentParser.objectTypes = new ArrayList<>();
		documentParser.objectTypes.add((ObjectType) documentParser.getType("Object1"));
		documentParser.objectTypes.add((ObjectType) documentParser.getType("Object2"));
		//
		documentParser.scalars.add((ScalarType) documentParser.getType("GraphQLScalar"));
		//
		documentParser.interfaceTypes = new ArrayList<>();
		documentParser.interfaceTypes.add((InterfaceType) documentParser.getType("Interface0"));
		//
		documentParser.enumTypes = new ArrayList<>();
		documentParser.enumTypes.add((EnumType) documentParser.getType("Enum0"));

		ObjectType type = new ObjectType("Package name", PluginMode.client);
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
		documentParser.queryTypes = new ArrayList<>();
		documentParser.interfaceTypes = new ArrayList<>();
		documentParser.objectTypes = new ArrayList<>();
		documentParser.dataFetchers = new ArrayList<>();
		documentParser.dataFetchersDelegates = new ArrayList<>();
		documentParser.queryTypes.add(type);
		documentParser.enumTypes.add(new EnumType("AnEnumType", "packageName", PluginMode.server));
		documentParser.scalars.add(new ScalarType("Float", "java.lang", "Float", PluginMode.server));
		documentParser.scalars.add((ScalarType) documentParser.getType("GraphQLScalar"));

		// Go, go, go
		documentParser.initDataFetcherForOneObject(type, true);

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
		documentParser.queryTypes = new ArrayList<>();
		documentParser.interfaceTypes = new ArrayList<>();
		documentParser.objectTypes = new ArrayList<>();
		documentParser.enumTypes = new ArrayList<>();
		documentParser.scalars = new ArrayList<>();
		documentParser.dataFetchers = new ArrayList<>();
		documentParser.dataFetchersDelegates = new ArrayList<>();
		//
		documentParser.objectTypes.add(type);
		documentParser.enumTypes.add(new EnumType("AnEnumType", "packageName", PluginMode.server));
		documentParser.scalars.add(new ScalarType("Float", "java.lang", "Float", PluginMode.server));
		documentParser.scalars.add((ScalarType) documentParser.getType("GraphQLScalar"));
		documentParser.enumTypes.add((EnumType) documentParser.getType("Enum0"));
		documentParser.fillTypesMap();

		// Go, go, go
		documentParser.initDataFetcherForOneObject(type, false);

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
		documentParser.queryTypes = new ArrayList<>();
		documentParser.interfaceTypes = new ArrayList<>();
		documentParser.objectTypes = new ArrayList<>();
		documentParser.enumTypes = new ArrayList<>();
		documentParser.scalars = new ArrayList<>();
		documentParser.dataFetchers = new ArrayList<>();
		documentParser.dataFetchersDelegates = new ArrayList<>();
		//
		documentParser.interfaceTypes.add(new InterfaceType("AnInterface", "a.package", PluginMode.server));
		documentParser.enumTypes.add(new EnumType("AnEnumType", "packageName", PluginMode.server));
		documentParser.scalars.add(new ScalarType("Float", "java.lang", "Float", PluginMode.server));

		// Go, go, go
		documentParser.initDataFetcherForOneObject(type, false);

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

	private void checkDataFetcher(DataFetcher dataFetcher, String name, boolean list, Type type, String sourceName,
			List<Field> inputParameters) {
		assertEquals(name, dataFetcher.getName(), "name");
		assertEquals(list, dataFetcher.getField().isList(), "list");
		assertEquals(type, dataFetcher.getField().getOwningType(), "type");
		assertEquals(inputParameters, dataFetcher.getField().getInputParameters(), "arguments");
		assertEquals(sourceName, dataFetcher.getSourceName(), "sourceName");
	}

}
