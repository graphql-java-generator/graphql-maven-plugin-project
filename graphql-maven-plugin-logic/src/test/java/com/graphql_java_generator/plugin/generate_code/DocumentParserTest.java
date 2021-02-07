package com.graphql_java_generator.plugin.generate_code;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
		pluginConfiguration = new GraphQLConfigurationTestHelper(this);
		pluginConfiguration.mode = PluginMode.client; // client as a default value for the tests, here
		pluginConfiguration.packageName = packageName;

		documentParser = new GenerateCodeDocumentParser();
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

		type = new ObjectType("TheName", pluginConfiguration);
		documentParser.addTypeAnnotationForClientMode(type);
		assertEquals("@GraphQLObjectType(\"TheName\")", type.getAnnotation(), type.getClass().getName());

		type = new InterfaceType("TheName", pluginConfiguration);
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

		type = new ObjectType("TheName", pluginConfiguration);
		documentParser.addTypeAnnotationForServerMode(type);
		assertEquals("@Entity\n@GraphQLObjectType(\"TheName\")", type.getAnnotation(), type.getClass().getName());

		type = new InterfaceType("TheName", pluginConfiguration);
		documentParser.addTypeAnnotationForServerMode(type);
		assertEquals("@GraphQLInterfaceType(\"TheName\")", type.getAnnotation(), type.getClass().getName());

		type = new EnumType("TheName", pluginConfiguration);
		documentParser.addTypeAnnotationForServerMode(type);
		assertEquals("", type.getAnnotation(), type.getClass().getName());
	}

	@Test
	public void test_initDataFetcherForOneObject() {
		// Preparation
		documentParser.setTypes(new HashMap<>());
		documentParser.getTypes().put("Object1", new ObjectType("Object1", pluginConfiguration));
		documentParser.getTypes().put("GraphQLScalar",
				new ScalarType("GraphQLScalar", "packageName", "classSimpleName", pluginConfiguration));
		documentParser.getTypes().put("Interface0", new InterfaceType("Interface0", pluginConfiguration));
		documentParser.getTypes().put("Enum0", new EnumType("Enum0", pluginConfiguration));
		documentParser.getTypes().put("Object2", new ObjectType("Object2", pluginConfiguration));

		documentParser.setObjectTypes(new ArrayList<>());
		documentParser.getObjectTypes().add((ObjectType) documentParser.getType("Object1"));
		documentParser.getObjectTypes().add((ObjectType) documentParser.getType("Object2"));
		//
		documentParser.getScalarTypes().add((ScalarType) documentParser.getType("GraphQLScalar"));
		//
		documentParser.setInterfaceTypes(new ArrayList<>());
		documentParser.getInterfaceTypes().add((InterfaceType) documentParser.getType("Interface0"));
		//
		documentParser.setEnumTypes(new ArrayList<>());
		documentParser.getEnumTypes().add((EnumType) documentParser.getType("Enum0"));

		ObjectType type = new ObjectType("NameOfTheType", pluginConfiguration);

		String[] fields = { "Object1", "GraphQLScalar", "Interface0", "Enum0", "Object2" };
		for (int i = 0; i < 5; i += 1) {

			// When the field is a list, there is two fieldTypeAST: the list, then the real type
			FieldTypeAST fieldTypeAST;
			if ((i % 2) == 0) {
				FieldTypeAST realType = new FieldTypeAST(documentParser.getType(fields[i]).getName());
				fieldTypeAST = FieldTypeAST.builder().list(true).listItemFieldTypeAST(realType).build();
			} else {
				fieldTypeAST = FieldTypeAST.builder().list(false)
						.graphQLTypeSimpleName(documentParser.getType(fields[i]).getName()).build();
			}
			FieldImpl f = FieldImpl.builder().documentParser(documentParser).name("field" + i).owningType(type)
					.fieldTypeAST(fieldTypeAST).build();
			type.getFields().add(f);

			// Let's create its argument list
			List<Field> args = new ArrayList<>();
			for (int j = 0; j < i; j += 1) {// means: the first field has
				// When the field is a list, there is two fieldTypeAST: the list, then the real type
				FieldTypeAST argTypeAST;
				if ((j % 2) == 0) {
					FieldTypeAST realType = new FieldTypeAST(documentParser.getType(fields[i]).getName());
					argTypeAST = FieldTypeAST.builder().list(true).listItemFieldTypeAST(realType).build();
				} else {
					argTypeAST = FieldTypeAST.builder().list(false)
							.graphQLTypeSimpleName(documentParser.getType(fields[i]).getName()).build();
				}
				FieldImpl arg = FieldImpl.builder().documentParser(documentParser).name("arg" + j)
						.fieldTypeAST(argTypeAST).build();
				args.add(arg);
			}
			f.setInputParameters(args);
		}

		///////////////////////////////////////////////////////////////////////////////
		////////////////////// TEST FOR QUERY TYPES
		///////////////////////////////////////////////////////////////////////////////
		documentParser.setQueryType(type);
		documentParser.setInterfaceTypes(new ArrayList<>());
		documentParser.setObjectTypes(new ArrayList<>());
		documentParser.dataFetchers = new ArrayList<>();
		documentParser.dataFetchersDelegates = new ArrayList<>();
		documentParser.getEnumTypes().add(new EnumType("AnEnumType", pluginConfiguration));
		documentParser.getScalarTypes().add(new ScalarType("Float", "java.lang", "Float", pluginConfiguration));
		documentParser.getScalarTypes().add((ScalarType) documentParser.getType("GraphQLScalar"));

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
		documentParser.setQueryType(type);
		documentParser.setInterfaceTypes(new ArrayList<>());
		documentParser.setObjectTypes(new ArrayList<>());
		documentParser.setEnumTypes(new ArrayList<>());
		documentParser.setScalarTypes(new ArrayList<>());
		documentParser.dataFetchers = new ArrayList<>();
		documentParser.dataFetchersDelegates = new ArrayList<>();
		//
		documentParser.getObjectTypes().add(type);
		documentParser.getEnumTypes().add(new EnumType("AnEnumType", pluginConfiguration));
		documentParser.getScalarTypes().add(new ScalarType("Float", "java.lang", "Float", pluginConfiguration));
		documentParser.getScalarTypes().add((ScalarType) documentParser.getType("GraphQLScalar"));
		documentParser.getEnumTypes().add((EnumType) documentParser.getType("Enum0"));
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
		documentParser.setQueryType(null);
		documentParser.setInterfaceTypes(new ArrayList<>());
		documentParser.setObjectTypes(new ArrayList<>());
		documentParser.setEnumTypes(new ArrayList<>());
		documentParser.setScalarTypes(new ArrayList<>());
		documentParser.dataFetchers = new ArrayList<>();
		documentParser.dataFetchersDelegates = new ArrayList<>();
		//
		documentParser.getInterfaceTypes().add(new InterfaceType("AnInterface", pluginConfiguration));
		documentParser.getEnumTypes().add(new EnumType("AnEnumType", pluginConfiguration));
		documentParser.getScalarTypes().add(new ScalarType("Float", "java.lang", "Float", pluginConfiguration));

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
		assertEquals(list, dataFetcher.getField().getFieldTypeAST().isList(), "list");
		assertEquals(type, dataFetcher.getField().getOwningType(), "type");
		assertEquals(inputParameters, dataFetcher.getField().getInputParameters(), "arguments");
		assertEquals(graphQLOriginType, dataFetcher.getGraphQLOriginType(), "graphQLOriginType");
	}

}
