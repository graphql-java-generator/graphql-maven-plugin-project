package com.graphql_java_generator.plugin.compilation_tests;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

import org.apache.commons.lang3.exception.ContextedRuntimeException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import com.graphql_java_generator.plugin.DocumentParser;
import com.graphql_java_generator.plugin.Generator;
import com.graphql_java_generator.plugin.conf.GraphQLConfiguration;
import com.graphql_java_generator.plugin.generate_code.GenerateCodeDocumentParser;
import com.graphql_java_generator.plugin.test.compiler.CompilationTestHelper;
import com.graphql_java_generator.plugin.test.helper.GraphqlTestHelper;
import com.graphql_java_generator.plugin.test.helper.MavenTestHelper;

abstract class AbstractIntegrationTest {

	private static final Logger logger = LoggerFactory.getLogger(AbstractIntegrationTest.class);

	Class<?> springConfClass;

	protected AbstractApplicationContext ctx = null;
	protected DocumentParser generateCodeDocumentParser;
	protected Generator codeGenerator;
	protected CompilationTestHelper compilationTestHelper;
	protected GraphqlTestHelper graphqlTestHelper;
	protected MavenTestHelper mavenTestHelper;
	protected GraphQLConfiguration configuration;
	protected String moduleName;

	Method classLoaderAddUrlMethod = null;
	/** The {@link ClassLoader} that adds the target class folder to the current classpath */
	URLClassLoader urlClassLoader = null;

	protected enum FileType {
		POJO, UTIL, AUTOCONFIGURATION
	}

	public AbstractIntegrationTest(Class<?> springConfClass) {
		this.springConfClass = springConfClass;
	}

	@BeforeEach
	void loadApplicationContext() throws IOException {
		urlClassLoader = null;

		ctx = new AnnotationConfigApplicationContext(springConfClass);
		generateCodeDocumentParser = ctx.getBean(DocumentParser.class);
		codeGenerator = ctx.getBean(Generator.class);
		compilationTestHelper = ctx.getBean(CompilationTestHelper.class);
		graphqlTestHelper = ctx.getBean(GraphqlTestHelper.class);
		mavenTestHelper = ctx.getBean(MavenTestHelper.class);
		configuration = ctx.getBean(GraphQLConfiguration.class);
		moduleName = configuration.getPackageName();

		generateCodeDocumentParser.parseGraphQLSchemas();
	}

	@AfterEach
	void cleanUp() {
		if (ctx != null) {
			ctx.close();
		}
	}

	/**
	 * This test will be executed for each concrete subclass of this class
	 * 
	 * @throws MojoExecutionException
	 * @throws IOException
	 */
	@Test
	void testGenerateCode() throws Exception {

		logger.info("Starting {} test", this.getClass().getName());

		// Preparation
		mavenTestHelper.deleteDirectoryAndContentIfExists(configuration.getTargetSourceFolder());
		mavenTestHelper.deleteDirectoryAndContentIfExists(configuration.getTargetClassFolder());
		configuration.logConfiguration();

		// Go, go, go
		codeGenerator.generateCode();

		// Let's rename the template module-info file to a real one, to check the module's behavior
		// Renaming the module-info.java.template to module-info.java activate the module check in the plugin logic
		// maven module. This triggers various compilation error, once the unit test are executed.
		// This renaming is commented, until this is solved
		// File moduleInfo = new File(configuration.getTargetSourceFolder(),
		// GenerateCodeGenerator.MODULE_INFO_TEMPLATE_FILENAME);
		// moduleInfo.renameTo(new File(configuration.getTargetSourceFolder(), "module-info.java"));

		// Verifications
		compilationTestHelper.checkCompleteCompilationStatus(null);

		// Possible additional checks from the implementing class
		doAdditionalChecks();
	}

	protected void doAdditionalChecks() throws Exception {
		// The default implementation is to do nothing
	}

	/**
	 * Loads the given class from the generated files. This method is used to load the class, and apply java reflexion
	 * to check the generated code.
	 * 
	 * @param simpleClassname
	 *            The name of the generated class, without the package, for instance "QueryController"
	 * @param fileType
	 * @return
	 * @throws Exception
	 */
	protected Class<?> loadGeneratedClass(String simpleClassname, FileType fileType) throws Exception {
		String fullClassname;
		switch (fileType) {
		case POJO:
			fullClassname = configuration.getPackageName() + "." + simpleClassname;
			break;
		case AUTOCONFIGURATION:
			fullClassname = configuration.getPackageName() + "." + simpleClassname;
			break;
		case UTIL:
			if (configuration.isSeparateUtilityClasses()) {
				fullClassname = configuration.getPackageName() + "." + GenerateCodeDocumentParser.UTIL_PACKAGE_NAME
						+ "." + simpleClassname;
			} else {
				fullClassname = configuration.getPackageName() + "." + simpleClassname;
			}
			break;
		default:
			throw new ContextedRuntimeException("Unexpected case: " + fileType);
		}
		return getURLClassLoader().loadClass(fullClassname);
	}

	protected URLClassLoader getURLClassLoader() throws Exception {
		if (urlClassLoader == null) {
			URL[] urls = { configuration.getTargetClassFolder().toURI().toURL() };
			urlClassLoader = new URLClassLoader(urls, getClass().getClassLoader());
		}
		return urlClassLoader;
	}
}
