package graphql.mavenplugin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.apache.maven.plugin.logging.SystemStreamLog;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import graphql.mavenplugin.language.InterfaceType;
import graphql.mavenplugin.language.ObjectType;

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
		documentParser.defineDefaultInterfaceImplementationClassName();

		// Verification
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
}
