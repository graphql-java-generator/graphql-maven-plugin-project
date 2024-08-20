package com.graphql_java_generator.plugin.compilation_tests;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

import org.apache.commons.lang3.exception.ContextedRuntimeException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

	Class<?> springConfClass;

	protected AbstractApplicationContext ctx = null;
	protected DocumentParser generateCodeDocumentParser;
	protected Generator codeGenerator;
	protected CompilationTestHelper compilationTestHelper;
	protected GraphqlTestHelper graphqlTestHelper;
	protected MavenTestHelper mavenTestHelper;
	protected GraphQLConfiguration configuration;

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
		this.urlClassLoader = null;

		this.ctx = new AnnotationConfigApplicationContext(this.springConfClass);
		this.generateCodeDocumentParser = this.ctx.getBean(DocumentParser.class);
		this.codeGenerator = this.ctx.getBean(Generator.class);
		this.compilationTestHelper = this.ctx.getBean(CompilationTestHelper.class);
		this.graphqlTestHelper = this.ctx.getBean(GraphqlTestHelper.class);
		this.mavenTestHelper = this.ctx.getBean(MavenTestHelper.class);
		this.configuration = this.ctx.getBean(GraphQLConfiguration.class);

		this.generateCodeDocumentParser.parseGraphQLSchemas();
	}

	@AfterEach
	void cleanUp() {
		if (this.ctx != null) {
			this.ctx.close();
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
		// Preparation
		this.mavenTestHelper.deleteDirectoryAndContentIfExists(this.configuration.getTargetSourceFolder());
		this.mavenTestHelper.deleteDirectoryAndContentIfExists(this.configuration.getTargetClassFolder());
		this.configuration.logConfiguration();

		// Go, go, go
		this.codeGenerator.generateCode();

		// Verifications
		this.compilationTestHelper.checkCompleteCompilationStatus(null);

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
			fullClassname = this.configuration.getPackageName() + "." + simpleClassname;
			break;
		case AUTOCONFIGURATION:
			fullClassname = this.configuration.getPackageName() + "." + simpleClassname;
			break;
		case UTIL:
			if (this.configuration.isSeparateUtilityClasses()) {
				fullClassname = this.configuration.getPackageName() + "." + GenerateCodeDocumentParser.UTIL_PACKAGE_NAME
						+ "." + simpleClassname;
			} else {
				fullClassname = this.configuration.getPackageName() + "." + simpleClassname;
			}
			break;
		default:
			throw new ContextedRuntimeException("Unexpected case: " + fileType);
		}
		return getURLClassLoader().loadClass(fullClassname);
	}

	protected URLClassLoader getURLClassLoader() throws Exception {
		if (this.urlClassLoader == null) {
			URL[] urls = { this.configuration.getTargetClassFolder().toURI().toURL() };
			this.urlClassLoader = new URLClassLoader(urls, getClass().getClassLoader());
		}
		return this.urlClassLoader;
	}
}
