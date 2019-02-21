package graphql.mavenplugin.language;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FieldTypeTest {

	@BeforeEach
	void setUp() throws Exception {
	}

	@Test
	void testGetjavaClassSimpleName() {
		// Preparation
		FieldType fieldType = new FieldType();
		fieldType.setJavaClassFullName("my.beautiful.package.MyClass");

		// Go, go, go
		String simpleName = fieldType.getJavaClassSimpleName();

		// Verification
		assertEquals("MyClass", simpleName);
	}

}
