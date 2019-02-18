package graphql.mavenplugin;

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
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import graphql.mavenplugin.test.compiler.CompilationTestHelper;
import graphql.mavenplugin.test.helper.AllGraphQLCasesSpringConfiguration;
import graphql.mavenplugin.test.helper.GraphqlTestHelper;
import graphql.parser.Parser;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { AllGraphQLCasesSpringConfiguration.class })
@Disabled // This class should be ignored when executing the tests
class AbstractIntegrationTest {

	/**
	 * Contains the argument to send to ctx.getResource, to retrieve the graphqls schema definition file, from the
	 * classpath
	 */
	final protected String graphqlsResourceLocation;

	@Autowired
	protected ApplicationContext ctx;
	@Autowired
	protected CompilationTestHelper compilationTestHelper;
	@Autowired
	protected GraphqlTestHelper graphqlTestHelper;

	@Autowired
	protected Log log;
	@Autowired
	protected String basePackage;
	@Autowired
	protected String encoding;
	@Autowired
	protected File targetSourceFolder;
	@Autowired
	protected File targetClassFolder;

	protected DocumentParser documentParser;
	protected CodeGenerator codeGenerator;

	/**
	 * 
	 * @param graphqlsResourceLocation
	 *            The argument to send to ctx.getResource, to retrieve the graphqls schema definition file, from the
	 *            classpath
	 */
	public AbstractIntegrationTest(String graphqlsResourceLocation) {
		this.graphqlsResourceLocation = graphqlsResourceLocation;
	}

	@BeforeEach
	void setUp() throws Exception {
		documentParser = new DocumentParser();
		documentParser.basePackage = basePackage;
		documentParser.log = new SystemStreamLog();
		Resource resource = ctx.getResource(graphqlsResourceLocation);
		documentParser.documents = new ArrayList<>();
		documentParser.documents.add(new Parser().parseDocument(graphqlTestHelper.readSchema(resource)));
		documentParser.parseDocuments();

		codeGenerator = new CodeGenerator();
		codeGenerator.documentParser = documentParser;
		codeGenerator.basePackage = basePackage;
		codeGenerator.log = documentParser.log;
		codeGenerator.encoding = encoding;
		codeGenerator.targetSourceFolder = targetSourceFolder;
	}

	/**
	 * This test will be executed for each concrete subclass of this class
	 * 
	 * @throws MojoExecutionException
	 * @throws IOException
	 */
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

		compilationTestHelper.checkCompleteCompilationStatus(null);
	}

}
