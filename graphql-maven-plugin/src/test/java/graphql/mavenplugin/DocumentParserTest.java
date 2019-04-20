package graphql.mavenplugin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.logging.SystemStreamLog;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import graphql.mavenplugin.language.DataFetcher;
import graphql.mavenplugin.language.Field;
import graphql.mavenplugin.language.Type;
import graphql.mavenplugin.language.impl.EnumType;
import graphql.mavenplugin.language.impl.FieldImpl;
import graphql.mavenplugin.language.impl.InterfaceType;
import graphql.mavenplugin.language.impl.ObjectType;
import graphql.mavenplugin.language.impl.ScalarType;

/**
 * 
 * @author EtienneSF
 */

class DocumentParserTest {

	String basePackage = "org.graphql.test.generate";

	private DocumentParser documentParser;

	@BeforeEach
	void setUp() throws Exception {
		documentParser = new DocumentParser();
		documentParser.basePackage = basePackage;
		documentParser.log = new SystemStreamLog();
	}

	@Test
	void test_defineDefaultInterfaceImplementationClassName() {
		// Preparation
		ObjectType o = new ObjectType(basePackage, PluginMode.server);
		o.setName("interfaceImpl");
		documentParser.interfaceTypes = new ArrayList<>();
		documentParser.objectTypes = new ArrayList<>();
		documentParser.objectTypes.add(o);

		InterfaceType i1 = new InterfaceType(basePackage, PluginMode.server);
		i1.setName("interface");
		documentParser.interfaceTypes.add(i1);

		InterfaceType i2 = new InterfaceType(basePackage, PluginMode.server);
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
		documentParser.mode = PluginMode.server;

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
		List<Type> fieldTypes = new ArrayList<>();
		fieldTypes.add(new ObjectType("Object1", "package", PluginMode.server));
		fieldTypes.add(new ScalarType("Scalar", "packageName", "classSimpleName", PluginMode.server));
		fieldTypes.add(new InterfaceType("Interface0", "packageName", PluginMode.server));
		fieldTypes.add(new EnumType("Enum0", "packageName", PluginMode.server));
		fieldTypes.add(new ObjectType("Object2", "package", PluginMode.server));

		ObjectType type = new ObjectType("Package name", PluginMode.client);
		type.setName("NameOfTheType");

		for (int i = 0; i < 5; i += 1) {
			FieldImpl f = mock(FieldImpl.class);
			type.getFields().add(f);

			when(f.getName()).thenReturn("field" + i);
			when(f.getPascalCaseName()).thenReturn("Field" + i);
			when(f.isList()).thenReturn((i % 2) == 0);
			when(f.getType()).thenReturn(fieldTypes.get(i));
			when(f.getOwningType()).thenReturn(type);

			// Let's create its argument list
			List<Field> args = new ArrayList<>();
			for (int j = 0; j < i; j += 1) {// means: the first field has
				FieldImpl arg = mock(FieldImpl.class);
				when(arg.getName()).thenReturn("arg" + j);
				when(arg.getPascalCaseName()).thenReturn("Field" + j);
				when(arg.isList()).thenReturn((j % 2) == 0);
				when(arg.getType()).thenReturn(fieldTypes.get(j));
				args.add(arg);
			}
			when(f.getInputParameters()).thenReturn(args);
		}

		///////////////////////////////////////////////////////////////////////////////
		////////////////////// TEST FOR QUERY TYPES
		///////////////////////////////////////////////////////////////////////////////
		documentParser.queryTypes = new ArrayList<>();
		documentParser.interfaceTypes = new ArrayList<>();
		documentParser.objectTypes = new ArrayList<>();
		documentParser.dataFetchers = new ArrayList<>();
		documentParser.queryTypes.add(type);
		documentParser.enumTypes.add(new EnumType("AnEnumType", "packageName", PluginMode.server));
		documentParser.scalars.add(new ScalarType("Float", "java.lang", "Float", PluginMode.server));

		// Go, go, go
		documentParser.initDataFetcherForOneObject(type, true);

		// Verification
		int i = 0;
		assertEquals(5, documentParser.dataFetchers.size(), "size");
		// For query types, there must be a Data Fetcher for each field.
		checkDataFetcher(documentParser.dataFetchers.get(i), "Field0", true, type,
				type.getFields().get(i++).getInputParameters());
		checkDataFetcher(documentParser.dataFetchers.get(i), "Field1", false, type,
				type.getFields().get(i++).getInputParameters());
		checkDataFetcher(documentParser.dataFetchers.get(i), "Field2", true, type,
				type.getFields().get(i++).getInputParameters());
		checkDataFetcher(documentParser.dataFetchers.get(i), "Field3", false, type,
				type.getFields().get(i++).getInputParameters());
		checkDataFetcher(documentParser.dataFetchers.get(i), "Field4", true, type,
				type.getFields().get(i++).getInputParameters());

		/////////////////////////////////////////////////////////////////////////////// ::
		////////////////////// TEST FOR OBJECT TYPES
		/////////////////////////////////////////////////////////////////////////////// ::
		documentParser.queryTypes = new ArrayList<>();
		documentParser.interfaceTypes = new ArrayList<>();
		documentParser.objectTypes = new ArrayList<>();
		documentParser.enumTypes = new ArrayList<>();
		documentParser.scalars = new ArrayList<>();
		documentParser.dataFetchers = new ArrayList<>();
		//
		documentParser.objectTypes.add(type);
		documentParser.enumTypes.add(new EnumType("AnEnumType", "packageName", PluginMode.server));
		documentParser.scalars.add(new ScalarType("Float", "java.lang", "Float", PluginMode.server));

		// Go, go, go
		documentParser.initDataFetcherForOneObject(type, false);

		// Verification
		i = 0;
		assertEquals(3, documentParser.dataFetchers.size(), "size");
		// For non query types, there must be a Data Fetcher only for non Scalar and non Enum field.
		checkDataFetcher(documentParser.dataFetchers.get(i++), "Field0", true, type,
				type.getFields().get(0).getInputParameters());
		checkDataFetcher(documentParser.dataFetchers.get(i++), "Field2", true, type,
				type.getFields().get(2).getInputParameters());
		checkDataFetcher(documentParser.dataFetchers.get(i++), "Field4", true, type,
				type.getFields().get(4).getInputParameters());

		/////////////////////////////////////////////////////////////////////////////// ::
		////////////////////// TEST FOR INTERFACE TYPES
		/////////////////////////////////////////////////////////////////////////////// ::
		documentParser.queryTypes = new ArrayList<>();
		documentParser.interfaceTypes = new ArrayList<>();
		documentParser.objectTypes = new ArrayList<>();
		documentParser.enumTypes = new ArrayList<>();
		documentParser.scalars = new ArrayList<>();
		documentParser.dataFetchers = new ArrayList<>();
		//
		documentParser.interfaceTypes.add(new InterfaceType("AnInterface", "a.package", PluginMode.server));
		documentParser.enumTypes.add(new EnumType("AnEnumType", "packageName", PluginMode.server));
		documentParser.scalars.add(new ScalarType("Float", "java.lang", "Float", PluginMode.server));

		// Go, go, go
		documentParser.initDataFetcherForOneObject(type, false);

		// Verification
		i = 0;
		assertEquals(3, documentParser.dataFetchers.size(), "size");
		// For non query types, there must be a Data Fetcher only for non Scalar and non Enum field.
		checkDataFetcher(documentParser.dataFetchers.get(i++), "Field0", true, type,
				type.getFields().get(0).getInputParameters());
		checkDataFetcher(documentParser.dataFetchers.get(i++), "Field2", true, type,
				type.getFields().get(2).getInputParameters());
		checkDataFetcher(documentParser.dataFetchers.get(i++), "Field4", true, type,
				type.getFields().get(4).getInputParameters());
	}

	private void checkDataFetcher(DataFetcher dataFetcher, String name, boolean list, Type type,
			List<Field> inputParameters) {
		assertEquals("NameOfTheType" + name, dataFetcher.getName(), "name");
		assertEquals(list, dataFetcher.getField().isList(), "list");
		assertEquals(type, dataFetcher.getField().getOwningType(), "type");
		assertEquals(inputParameters, dataFetcher.getField().getInputParameters(), "arguments");
	}

}
