package graphql.mavenplugin.language;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InterfaceTypeTest {

	String packageName = "a.package.name";
	String packageName2 = "a.package.name";

	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	void testGetConcreteClassSimpleName() {
		// Preparation
		ObjectType o = new ObjectType(packageName2);
		o.setName("AClassName");

		InterfaceType i = new InterfaceType(packageName);
		i.setDefaultImplementation(o);

		// Verification
		assertEquals("AClassName", i.getConcreteClassSimpleName(), "");
	}

}
