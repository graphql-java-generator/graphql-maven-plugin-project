package graphql.mavenplugin;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import graphql.mavenplugin.test.compiler.CompilationTestHelper;
import graphql.mavenplugin.test.helper.GraphqlTestHelper;
import graphql.mavenplugin.test.helper.SpringTestConfiguration;
import graphql.parser.Parser;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { SpringTestConfiguration.class })
class CodeGeneratorTest {

	@Autowired
	private ApplicationContext ctx;
	@Autowired
	private CompilationTestHelper compilationTestHelper;
	@Autowired
	private GraphqlTestHelper graphqlTestHelper;

	@Autowired
	Log log;
	@Autowired
	String basePackage;
	@Autowired
	File targetSourceFolder;
	@Autowired
	File targetClassFolder;

	private DocumentParser documentParser;
	private CodeGenerator codeGenerator;

	@BeforeEach
	void setUp() throws Exception {
		documentParser = new DocumentParser();
		documentParser.basePackage = basePackage;
		documentParser.log = new SystemStreamLog();
		Resource resource = ctx.getResource("/allGraphQLCases.graphqls");
		documentParser.documents = new ArrayList<>();
		documentParser.documents.add(new Parser().parseDocument(graphqlTestHelper.readSchema(resource)));
		documentParser.parseDocuments();

		codeGenerator = new CodeGenerator();
		codeGenerator.documentParser = documentParser;
		codeGenerator.basePackage = basePackage;
		codeGenerator.log = documentParser.log;
		codeGenerator.encoding = SpringTestConfiguration.ENCODING;
		codeGenerator.targetSourceFolder = targetSourceFolder;
	}

	@Test
	void testCodeGenerator() {
		assertNotNull(codeGenerator.velocityEngine, "Velocity engine must be initialized");
	}

	@Test
	void testGenerateCode() throws MojoExecutionException, IOException {
		// Preparation
		codeGenerator = spy(codeGenerator);

		// Go, go, go
		codeGenerator.generateCode();

		// Verification
		verify(codeGenerator, times(1)).generateEnumTypes();
		verify(codeGenerator, times(1)).generateObjectTypes();
		verify(codeGenerator, times(1)).generateQueryTypes();

		String classpath = codeGenerator.targetSourceFolder.getCanonicalPath();
		compilationTestHelper.checkCompleteCompilationStatus(classpath);
	}

	@Test
	void testGetJavaFile() throws IOException {
		// Preparation
		String name = "MyClass";

		// Go, go, go
		File file = codeGenerator.getJavaFile(name);

		// Verification
		String expectedEndOfPath = (SpringTestConfiguration.TARGET_SOURCE_FOLDER + '/'
				+ SpringTestConfiguration.BASE_PACKAGE + '/' + name).replace('.', '/') + ".java";
		assertTrue(file.getCanonicalPath().replace('\\', '/').endsWith(expectedEndOfPath),
				"The file path should end with " + expectedEndOfPath + ", but is "
						+ file.getCanonicalPath().replace('\\', '/'));
	}

}
