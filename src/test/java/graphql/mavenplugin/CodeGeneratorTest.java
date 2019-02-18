package graphql.mavenplugin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.io.IOException;

import javax.annotation.Resource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import graphql.mavenplugin.test.helper.AllGraphQLCasesSpringConfiguration;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { AllGraphQLCasesSpringConfiguration.class })
class CodeGeneratorTest {

	@Resource
	String basePackage;
	@Resource
	File targetSourceFolder;

	private CodeGenerator codeGenerator;

	@BeforeEach
	void setUp() throws Exception {
		codeGenerator = new CodeGenerator();
	}

	@Test
	void testCodeGenerator() {
		assertNotNull(codeGenerator.velocityEngine, "Velocity engine must be initialized");
	}

	@Test
	void testGetJavaFile() throws IOException {
		// Preparation
		String name = "MyClass";
		codeGenerator.basePackage = basePackage;
		codeGenerator.targetSourceFolder = targetSourceFolder;

		// Go, go, go
		File file = codeGenerator.getJavaFile(name);

		// Verification
		String expectedEndOfPath = (targetSourceFolder.getCanonicalPath() + '/' + basePackage + '/' + name)
				.replace('.', '/').replace('\\', '/') + ".java";
		assertEquals(expectedEndOfPath, file.getCanonicalPath().replace('\\', '/'), "The file path should end with "
				+ expectedEndOfPath + ", but is " + file.getCanonicalPath().replace('\\', '/'));
	}

}
