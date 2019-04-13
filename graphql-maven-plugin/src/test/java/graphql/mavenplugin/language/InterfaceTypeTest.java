package graphql.mavenplugin.language;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import graphql.mavenplugin.PluginMode;

class InterfaceTypeTest {

	String packageName = "a.package.name";
	String packageName2 = "a.package.name";

	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	void testGetConcreteClassSimpleName() {
		// Preparation
		ObjectType o = new ObjectType(packageName2, PluginMode.SERVER);
		o.setName("AClassName");

		InterfaceType i = new InterfaceType(packageName, PluginMode.SERVER);
		i.setDefaultImplementation(o);

		// Verification
		assertEquals("AClassName", i.getConcreteClassSimpleName(), "");
	}

}
