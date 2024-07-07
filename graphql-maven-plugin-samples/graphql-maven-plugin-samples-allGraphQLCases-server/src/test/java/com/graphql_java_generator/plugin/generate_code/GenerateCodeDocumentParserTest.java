/**
 * 
 */
package com.graphql_java_generator.plugin.generate_code;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * @author etienne-sf
 */
public class GenerateCodeDocumentParserTest {

	@Test
	void test_IgnoredSpringMappings_Bar140() {
		assertThrows(ClassNotFoundException.class, () -> this.getClass().getClassLoader()
				.loadClass("org.allGraphQLCases.server.DataFetchersDelegateBar140"));
		assertThrows(ClassNotFoundException.class,
				() -> this.getClass().getClassLoader().loadClass("org.allGraphQLCases.server.Bar140Controller"));
	}

	@Test
	void test_IgnoredSpringMappings_IBar140() {
		assertThrows(ClassNotFoundException.class, () -> this.getClass().getClassLoader()
				.loadClass("org.allGraphQLCases.server.DataFetchersDelegateIBar140"));
		assertThrows(ClassNotFoundException.class,
				() -> this.getClass().getClassLoader().loadClass("org.allGraphQLCases.server.IBar140Controller"));
	}

	@Test
	void test_IgnoredSpringMappings_Character_name() throws ClassNotFoundException {
		final Class<?> clzz = this.getClass().getClassLoader()
				.loadClass("org.allGraphQLCases.server.DataFetchersDelegateCharacter");
		assertThrows(NoSuchMethodException.class, () -> clzz.getMethod("name"));

		final Class<?> classController = this.getClass().getClassLoader()
				.loadClass("org.allGraphQLCases.server.CharacterController");
		assertThrows(NoSuchMethodException.class, () -> classController.getMethod("name"));
	}

	@Test
	void test_IgnoredSpringMappings_MyQueryType_checkOverriddenController() throws ClassNotFoundException {
		final Class<?> classDataFetchersDelegate = this.getClass().getClassLoader()
				.loadClass("org.allGraphQLCases.server.DataFetchersDelegateMyQueryType");
		assertThrows(NoSuchMethodException.class,
				() -> classDataFetchersDelegate.getMethod("checkOverriddenController"));

		final Class<?> classController = this.getClass().getClassLoader()
				.loadClass("org.allGraphQLCases.server.MyQueryTypeController");
		assertThrows(NoSuchMethodException.class, () -> classController.getMethod("checkOverriddenController"));
	}

}
