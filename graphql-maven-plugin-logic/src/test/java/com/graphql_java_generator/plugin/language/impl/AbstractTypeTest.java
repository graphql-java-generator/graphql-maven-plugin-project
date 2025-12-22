package com.graphql_java_generator.plugin.language.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.text.DateFormat;
import java.util.Set;
import java.util.TreeSet;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.graphql_java_generator.plugin.test.helper.GraphQLConfigurationTestHelper;

class AbstractTypeTest {

	GraphQLConfigurationTestHelper pluginConfiguration = new GraphQLConfigurationTestHelper(this);

	/** A test class for {@link graphqlClientUtilsTest#test_addImports()} */
	public class AnInnerClass {
		int dummy;
	}

	@Test
	void testAddImportClassOfQ() {
		// Preparation
		AbstractType type = new ObjectType("DateFormat", pluginConfiguration, null);
		assertEquals(0, type.getImports().size());

		// Add of a class from the same package
		type.addImport(pluginConfiguration.getPackageName(), pluginConfiguration.getPackageName() + ".AClassName");
		assertEquals(0, type.getImports().size());

		// Add of a class of the same name (nothing happens: it would cause a name conflict in the generated code)
		type.addImport(pluginConfiguration.getPackageName(), DateFormat.class.getName());
		assertEquals(0, type.getImports().size());

		// Add of a class from another package
		type.addImport(pluginConfiguration.getPackageName(), java.util.Date.class.getName());
		assertEquals(1, type.getImports().size());
		assertTrue(type.getImports().contains("java.util.Date"));

		// Add of an inner class
		type.addImport(pluginConfiguration.getPackageName(), JsonSubTypes.Type.class.getName());
		assertEquals(2, type.getImports().size());
		assertTrue(type.getImports().contains("java.util.Date"));
		assertTrue(type.getImports().contains("com.fasterxml.jackson.annotation.JsonSubTypes.Type"));

		// Add of an inner class: when the MainClassname is given in the package name
		type.addImport(pluginConfiguration.getPackageName(),
				com.fasterxml.jackson.annotation.JsonTypeInfo.Id.class.getName());
		assertEquals(3, type.getImports().size());
		assertTrue(type.getImports().contains("java.util.Date"));
		assertTrue(type.getImports().contains("com.fasterxml.jackson.annotation.JsonSubTypes.Type"));
		assertTrue(type.getImports().contains("com.fasterxml.jackson.annotation.JsonTypeInfo.Id"));
	}

	@Test
	void testAddImportStringString() {
		// Preparation
		AbstractType type = new ObjectType("DateFormat", pluginConfiguration, null);
		assertEquals(0, type.getImports().size());

		// Add of a class from the same package
		type.addImport(pluginConfiguration.getPackageName(), pluginConfiguration.getPackageName() + ".JsonSubTypes");
		assertEquals(0, type.getImports().size());

		// Add of a class from the same name (nothing happens: it would cause a name conflict in the generated code)
		type.addImport(pluginConfiguration.getPackageName(), "java.text.DateFormat");
		assertEquals(0, type.getImports().size());

		// Add of a class from another package
		type.addImport(pluginConfiguration.getPackageName(), "java.sql.Date");
		assertEquals(1, type.getImports().size());
		assertTrue(type.getImports().contains("java.sql.Date"));

		// Add of an inner class: the simple classname is then: MainClassname$InnerClassname
		type.addImport(pluginConfiguration.getPackageName(), "com.fasterxml.jackson.annotation.JsonSubTypes.Type");
		assertEquals(2, type.getImports().size());
		assertTrue(type.getImports().contains("java.sql.Date"));
		assertTrue(type.getImports().contains("com.fasterxml.jackson.annotation.JsonSubTypes.Type"));

		// Add of an inner class: when the MainClassname is given in the package name
		type.addImport(pluginConfiguration.getPackageName(), "com.fasterxml.jackson.annotation.JsonTypeInfo.Id");
		assertEquals(3, type.getImports().size());
		assertTrue(type.getImports().contains("java.sql.Date"));
		assertTrue(type.getImports().contains("com.fasterxml.jackson.annotation.JsonSubTypes.Type"));
		assertTrue(type.getImports().contains("com.fasterxml.jackson.annotation.JsonTypeInfo.Id"));

		// Add of an inner class: when the MainClassname is given in the package name
		type.addImport(pluginConfiguration.getPackageName(), "com.fasterxml.jackson.annotation.JsonTypeInfo$Id2");
		assertEquals(4, type.getImports().size());
		assertTrue(type.getImports().contains("java.sql.Date"));
		assertTrue(type.getImports().contains("com.fasterxml.jackson.annotation.JsonSubTypes.Type"));
		assertTrue(type.getImports().contains("com.fasterxml.jackson.annotation.JsonTypeInfo.Id"));
		assertTrue(type.getImports().contains("com.fasterxml.jackson.annotation.JsonTypeInfo.Id2"));
	}

	@Test
	void testGetJavaNamePrefixAndSuffix() {

		ObjectType typeObject = new ObjectType("MyType", pluginConfiguration, null);
		pluginConfiguration.typePrefix = "TypePrefix";
		pluginConfiguration.typeSuffix = "TypeSuffix";
		assertEquals("TypePrefixMyTypeTypeSuffix", typeObject.getJavaName());

		ObjectType inputObject = new ObjectType("MyInput", pluginConfiguration, null);
		inputObject.setInputType(true);
		pluginConfiguration.inputPrefix = "InputPrefix";
		pluginConfiguration.inputSuffix = "InputSuffix";
		assertEquals("InputPrefixMyInputInputSuffix", inputObject.getJavaName());

		InterfaceType interfaceObject = new InterfaceType("MyInterface", pluginConfiguration, null);
		pluginConfiguration.interfacePrefix = "InterfacePrefix";
		pluginConfiguration.interfaceSuffix = "InterfaceSuffix";
		assertEquals("InterfacePrefixMyInterfaceInterfaceSuffix", interfaceObject.getJavaName());

		UnionType unionObject = new UnionType("MyUnion", pluginConfiguration, null);
		pluginConfiguration.unionPrefix = "UnionPrefix";
		pluginConfiguration.unionSuffix = "UnionSuffix";
		assertEquals("UnionPrefixMyUnionUnionSuffix", unionObject.getJavaName());

		EnumType enumObject = new EnumType("MyEnum", pluginConfiguration, null);
		pluginConfiguration.enumPrefix = "EnumPrefix";
		pluginConfiguration.enumSuffix = "EnumSuffix";
		assertEquals("EnumPrefixMyEnumEnumSuffix", enumObject.getJavaName());

	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	public void test_addImports() {
		// Preparation
		AbstractType type = new ObjectType("DateFormat", pluginConfiguration, null);
		Set<String> imports = new TreeSet<>();

		// Same package
		type.addImport(imports, getClass().getPackage().getName(), getClass().getName());
		assertEquals(0, imports.size(), "Same package: import not added");

		// primitive type
		type.addImport(imports, getClass().getPackage().getName(), "byte[]");
		assertEquals(0, imports.size(), "primitive type: import not added");

		// java.lang
		type.addImport(imports, getClass().getPackage().getName(), java.lang.String.class.getName());
		assertEquals(0, imports.size(), "java.lang: import not added");

		// java.util
		type.addImport(imports, getClass().getPackage().getName(), java.util.Date.class.getName());
		assertEquals(1, imports.size(), "java.util: import added");
		assertEquals("java.util.Date", imports.toArray(new String[0])[0]);

		imports = new TreeSet<>();
		type.addImport(imports, "another.target.package", AnInnerClass.class.getName());
		assertEquals(1, imports.size(), "import added");
		assertEquals("com.graphql_java_generator.plugin.language.impl.AbstractTypeTest.AnInnerClass",
				imports.toArray(new String[0])[0]);

		imports = new TreeSet<>();
		type.addImport(imports, "another.target.package", Type.class.getName());
		assertEquals(1, imports.size(), "import added");
		assertEquals("com.fasterxml.jackson.annotation.JsonSubTypes.Type", imports.toArray(new String[0])[0]);

		// Check of useJakartaEE9
		imports = new TreeSet<>();
		type.addImport(imports, "another.target.package", "jakarta.test");
		assertEquals(1, imports.size(), "import added");
		assertEquals("jakarta.test", imports.toArray(new String[0])[0]);
	}
}