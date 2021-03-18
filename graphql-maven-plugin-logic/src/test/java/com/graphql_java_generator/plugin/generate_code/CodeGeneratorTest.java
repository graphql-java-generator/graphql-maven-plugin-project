package com.graphql_java_generator.plugin.generate_code;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;

import org.apache.commons.io.FileUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.mockito.ArgumentCaptor;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.graphql_java_generator.plugin.CodeTemplate;
import com.graphql_java_generator.plugin.conf.GraphQLConfiguration;
import com.graphql_java_generator.plugin.conf.PluginMode;
import com.graphql_java_generator.plugin.language.Type;
import com.graphql_java_generator.plugin.language.impl.ObjectType;
import com.graphql_java_generator.plugin.test.helper.GraphQLConfigurationTestHelper;
import com.graphql_java_generator.plugin.test.helper.MavenTestHelper;

import graphql.mavenplugin_notscannedbyspring.AllGraphQLCases_Server_SpringConfiguration;

@Execution(ExecutionMode.CONCURRENT)
class CodeGeneratorTest {
	AnnotationConfigApplicationContext context;
	GraphQLConfigurationTestHelper pluginConfiguration;
	MavenTestHelper mavenTestHelper;

	private File targetResourceFolder;
	private File targetSourceFolder;
	private File targetRuntimeClassesSourceFolder;
	private File testRuntimeSourcesFile;
	private GenerateCodeGenerator codeGenerator;

	@BeforeEach
	void setUp() throws Exception {
		context = new AnnotationConfigApplicationContext(AllGraphQLCases_Server_SpringConfiguration.class);
		pluginConfiguration = context.getBean(GraphQLConfigurationTestHelper.class);
		mavenTestHelper = context.getBean(MavenTestHelper.class);

		targetResourceFolder = mavenTestHelper.getTargetResourceFolder(this.getClass().getSimpleName());
		targetSourceFolder = mavenTestHelper.getTargetSourceFolder(this.getClass().getSimpleName());
		targetRuntimeClassesSourceFolder = mavenTestHelper
				.getTargetRuntimeClassesBaseSourceFolder(this.getClass().getSimpleName());
		testRuntimeSourcesFile = mavenTestHelper.getTestRutimeSourcesJarFile();

		if (targetResourceFolder.exists()) {
			FileUtils.forceDelete(targetResourceFolder);
			targetResourceFolder.mkdirs();
		}
		if (targetSourceFolder.exists()) {
			FileUtils.forceDelete(targetSourceFolder);
			targetSourceFolder.mkdirs();
		}

		if (testRuntimeSourcesFile.exists()) {
			testRuntimeSourcesFile.delete();
		}

		codeGenerator = context.getBean(GenerateCodeGenerator.class);
		codeGenerator.generateCodeDocumentParser = new GenerateCodeDocumentParser();
		codeGenerator.generateCodeDocumentParser.configuration = pluginConfiguration;
	}

	@AfterEach
	void close() {
		context.close();
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void testCodeGenerator() {
		assertNotNull(codeGenerator.velocityEngineFromClasspath, "Velocity engine must be initialized");
	}

	/**
	 * This test is important, as it checks the Velocity context which is sent to the templates. As it will be possible
	 * for users of the plugin, to define their own templates, the Velocity context is a kind of API, exposed to other's
	 * templates. It must remain stable. Field can be added. <B>But no field should be removed</B>.
	 */
	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_generateTargetFile_client() {
		// Let's mock the Velocity engine, to check how it is called
		codeGenerator.velocityEngineFromClasspath = mock(VelocityEngine.class);
		Template mockedTemplate = mock(Template.class);
		when(codeGenerator.velocityEngineFromClasspath.getTemplate(anyString(), anyString()))
				.thenReturn(mockedTemplate);

		codeGenerator.generateCodeDocumentParser = mock(GenerateCodeDocumentParser.class);
		pluginConfiguration.mode = PluginMode.client;

		ObjectType object1 = new ObjectType("O1", pluginConfiguration);
		// ObjectType object2 = new ObjectType("O2", configuration);
		List<Type> objects = new ArrayList<>();
		objects.add(object1);
		// objects.add(object2);

		String type = "my test type";
		String templateFilename = "folder/a_template_for_test.vm";

		// Go, go, go
		int i = codeGenerator.generateTargetFiles(objects, type, templateFilename, false);

		// Verification
		assertEquals(objects.size(), i, "Nb files generated");

		// Let's check the parameter for getTemplate
		ArgumentCaptor<String> argument1 = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<String> argument2 = ArgumentCaptor.forClass(String.class);
		verify(codeGenerator.velocityEngineFromClasspath, times(1)).getTemplate(argument1.capture(),
				argument2.capture());
		assertEquals(templateFilename, argument1.getValue(), "checks the parameter for getTemplate");
		assertEquals("UTF-8", argument2.getValue());

		///////////////////////////////////////////////////////////////////////////////////////////////////////////
		// Let's check the velocity context sent to the template ... THIS IS IMPORTANT! DO NOT BREAK IT!
		///////////////////////////////////////////////////////////////////////////////////////////////////////////
		ArgumentCaptor<Context> argumentContext = ArgumentCaptor.forClass(Context.class);
		verify(mockedTemplate, times(1)).merge(argumentContext.capture(), any(Writer.class));
		// We have the Context sent to the Template.merge(..) method. Let's check its content
		assertEquals(pluginConfiguration.getPackageName(),
				((GraphQLConfiguration) argumentContext.getValue().get("configuration")).getPackageName(),
				"Context: checks the package");
		assertEquals(object1, argumentContext.getValue().get("object"), "Context: checks the package");
		assertEquals(type, argumentContext.getValue().get("type"), "Context: checks the package");
		///////////////////////////////////////////////////////////////////////////////////////////////////////////
	}

	/**
	 * This test is important, as it checks the Velocity context which is sent to the templates. As it will be possible
	 * for users of the plugin, to define their own templates, the Velocity context is a kind of API, exposed to other's
	 * templates. It must remain stable. Field can be added. <B>But no field should be removed</B>.
	 */
	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_generateTargetFile_server() {
		// Let's mock the Velocity engine, to check how it is called
		codeGenerator.velocityEngineFromClasspath = mock(VelocityEngine.class);
		Template mockedTemplate = mock(Template.class);
		when(codeGenerator.velocityEngineFromClasspath.getTemplate(anyString(), anyString()))
				.thenReturn(mockedTemplate);

		pluginConfiguration.mode = PluginMode.server;

		ObjectType object1 = new ObjectType("O1", pluginConfiguration);
		List<Type> objects = new ArrayList<>();
		objects.add(object1);

		String type = "my test type";
		String templateFilename = "folder/a_template_for_test.vm";

		// Go, go, go
		int i = codeGenerator.generateTargetFiles(objects, type, templateFilename, false);

		// Verification
		assertEquals(objects.size(), i, "Nb files generated");

		// Let's check the parameter for getTemplate
		ArgumentCaptor<String> argument1 = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<String> argument2 = ArgumentCaptor.forClass(String.class);
		verify(codeGenerator.velocityEngineFromClasspath, times(1)).getTemplate(argument1.capture(),
				argument2.capture());
		assertEquals(templateFilename, argument1.getValue(), "checks the parameter for getTemplate");
		assertEquals("UTF-8", argument2.getValue());

		///////////////////////////////////////////////////////////////////////////////////////////////////////////
		// Let's check the velocity context sent to the template ... THIS IS IMPORTANT! DO NOT BREAK IT!
		///////////////////////////////////////////////////////////////////////////////////////////////////////////
		ArgumentCaptor<Context> argumentContext = ArgumentCaptor.forClass(Context.class);
		verify(mockedTemplate, times(1)).merge(argumentContext.capture(), any(Writer.class));
		// We have the Context sent to the Template.merge(..) method. Let's check its content
		assertEquals(pluginConfiguration.getPackageName(),
				((GraphQLConfiguration) argumentContext.getValue().get("configuration")).getPackageName(),
				"Context: checks the package");
		assertEquals(object1, argumentContext.getValue().get("object"), "Context: checks the package");
		assertEquals(type, argumentContext.getValue().get("type"), "Context: checks the package");
		///////////////////////////////////////////////////////////////////////////////////////////////////////////
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_generateOneFile_inClasspath() throws IOException {
		// Preparation
		VelocityContext velocityContext = new VelocityContext();
		File targetFile = new File(targetResourceFolder + "/testTemplate_inClasspath");

		// Go, go, go
		codeGenerator.generateOneFile(targetFile, "In test_generateOneFile_InClasspath", velocityContext,
				"testTemplate.vm");

		// If there is no error, then the template has been found. The test is Ok
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_generateOneFile_inFileSystem() throws IOException {
		// Preparation
		VelocityContext velocityContext = new VelocityContext();
		File targetFile = new File(targetResourceFolder + "/testTemplate_inFileSystem");

		// Go, go, go
		codeGenerator.generateOneFile(targetFile, "In test_generateOneFile_InClasspath", velocityContext,
				"/src/test/resources/testTemplate.vm");

		// If there is no error, then the template has been found. The test is Ok
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void testGetJavaFile() throws IOException {
		// Preparation
		String name = "MyClass";
		String packageName = "my.package";
		pluginConfiguration.packageName = packageName;
		pluginConfiguration.separateUtilityClasses = false;
		pluginConfiguration.targetResourceFolder = targetResourceFolder;
		pluginConfiguration.targetSourceFolder = targetSourceFolder;

		////////////////////////////////////////////////////////////////////////////////////////////////////
		// Go, go, go (separateUtilityClasses=false, isUtility=false)
		File file = codeGenerator.getJavaFile(name, false);
		// Verification
		String expectedEndOfPath = (targetSourceFolder.getCanonicalPath() + '/' + packageName + '/' + name)
				.replace('.', '/').replace('\\', '/') + ".java";
		assertEquals(expectedEndOfPath, file.getCanonicalPath().replace('\\', '/'), "The file path should end with "
				+ expectedEndOfPath + ", but is " + file.getCanonicalPath().replace('\\', '/'));

		////////////////////////////////////////////////////////////////////////////////////////////////////
		// Go, go, go (separateUtilityClasses=false, isUtility=true)
		file = codeGenerator.getJavaFile(name, true);
		// Verification
		expectedEndOfPath = (targetSourceFolder.getCanonicalPath() + '/' + packageName + '/' + name).replace('.', '/')
				.replace('\\', '/') + ".java";
		assertEquals(expectedEndOfPath, file.getCanonicalPath().replace('\\', '/'), "The file path should end with "
				+ expectedEndOfPath + ", but is " + file.getCanonicalPath().replace('\\', '/'));

		// separateUtilityClasses=true
		pluginConfiguration.separateUtilityClasses = true;

		////////////////////////////////////////////////////////////////////////////////////////////////////
		// Go, go, go (separateUtilityClasses=true, isUtility=false)
		file = codeGenerator.getJavaFile(name, false);
		// Verification
		expectedEndOfPath = (targetSourceFolder.getCanonicalPath() + '/' + packageName + '/' + name).replace('.', '/')
				.replace('\\', '/') + ".java";
		assertEquals(expectedEndOfPath, file.getCanonicalPath().replace('\\', '/'), "The file path should end with "
				+ expectedEndOfPath + ", but is " + file.getCanonicalPath().replace('\\', '/'));

		////////////////////////////////////////////////////////////////////////////////////////////////////
		// Go, go, go (separateUtilityClasses=true, isUtility=true)
		file = codeGenerator.getJavaFile(name, true);
		// Verification
		expectedEndOfPath = (targetSourceFolder.getCanonicalPath() + '/' + packageName + '/'
				+ GenerateCodeDocumentParser.UTIL_PACKAGE_NAME + '/' + name).replace('.', '/').replace('\\', '/')
				+ ".java";
		assertEquals(expectedEndOfPath, file.getCanonicalPath().replace('\\', '/'), "The file path should end with "
				+ expectedEndOfPath + ", but is " + file.getCanonicalPath().replace('\\', '/'));
	}

	/**
	 * Test to validate the code generation process copies runtime sources if
	 * {@link GraphQLConfiguration#isCopyRuntimeSources()} is set to true
	 *
	 * @throws IOException
	 */
	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void testGenerateCode_copyRuntimeSources() throws IOException {
		// Preparation
		pluginConfiguration.mode = PluginMode.client;
		pluginConfiguration.packageName = "test.generatecode.enabled";
		pluginConfiguration.copyRuntimeSources = true;
		pluginConfiguration.schemaFileFolder = new File("src/test/resources");
		pluginConfiguration.schemaFilePattern = "basic.graphqls";
		pluginConfiguration.targetResourceFolder = targetResourceFolder;
		pluginConfiguration.targetSourceFolder = targetSourceFolder;
		pluginConfiguration.targetClassFolder = targetSourceFolder;

		if (Objects.isNull(getClass().getResourceAsStream("/graphql-java-runtime-sources.jar"))) {
			createRuntimeSourcesJar();
		}

		// Go, go, go
		codeGenerator.generateCode();

		// Verification
		assertTrue(targetRuntimeClassesSourceFolder.exists());
		assertTrue(targetRuntimeClassesSourceFolder.isDirectory());
		assertTrue(targetResourceFolder.exists());
		assertTrue(targetResourceFolder.isDirectory());
		assertTrue(targetRuntimeClassesSourceFolder.exists());
		assertTrue(targetRuntimeClassesSourceFolder.isDirectory());
		//
		File metaInf = new File(targetResourceFolder, "META-INF");
		assertTrue(metaInf.exists());
		assertTrue(metaInf.isDirectory());
		String[] metaInfChildren = metaInf.list();
		assertEquals(1, metaInfChildren.length);
		assertTrue(metaInfChildren[0].equals("spring.factories"));
	}

	/**
	 * Test to validate the code generation process does not copy runtime sources if
	 * {@link GraphQLConfiguration#isCopyRuntimeSources()} is set to false
	 *
	 * @throws IOException
	 */
	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void testGenerateCode_skipCopyRuntimeSources() throws IOException {

		pluginConfiguration.mode = PluginMode.client;
		pluginConfiguration.packageName = "test.generatecode.enabled";
		pluginConfiguration.copyRuntimeSources = false;
		pluginConfiguration.schemaFileFolder = new File("src/test/resources");
		pluginConfiguration.schemaFilePattern = "basic.graphqls";
		pluginConfiguration.targetResourceFolder = targetResourceFolder;
		pluginConfiguration.targetSourceFolder = targetSourceFolder;
		pluginConfiguration.targetClassFolder = targetSourceFolder;

		codeGenerator.generateCode();
		assertFalse(targetRuntimeClassesSourceFolder.exists());
	}

	/**
	 * Test for validating default template resolution
	 */
	@Test
	@Execution(ExecutionMode.CONCURRENT)
	protected void testResolveTemplateDefault() {
		pluginConfiguration.templates.clear();
		assertEquals(CodeTemplate.WIRING.getDefaultValue(), this.codeGenerator.resolveTemplate(CodeTemplate.WIRING));
		;
	}

	/**
	 * Test for validating customized template resolution
	 */
	@Test
	@Execution(ExecutionMode.CONCURRENT)
	protected void testResolveTemplateCustom() {
		pluginConfiguration.templates.clear();
		pluginConfiguration.templates.put(CodeTemplate.WIRING.name(), "/my/custom/template");
		assertEquals("/my/custom/template", this.codeGenerator.resolveTemplate(CodeTemplate.WIRING));
		;
	}

	/**
	 * Creates a mock graphql-java-runtime-sources.jar Generates a jar with the package com.graphql_java_generator. and
	 * sample file
	 *
	 * @throws IOException
	 */
	protected void createRuntimeSourcesJar() throws IOException {
		File file = mavenTestHelper.getTestRutimeSourcesJarFile();
		if (file.exists()) {
			file.delete();
		}

		FileOutputStream fout = new FileOutputStream(file);
		JarOutputStream jarOut = new JarOutputStream(fout);
		jarOut.putNextEntry(new ZipEntry("com/")); // Folders must end with "/".
		jarOut.putNextEntry(new ZipEntry("com/graphql_java_generator/")); // Folders must end with "/".
		jarOut.putNextEntry(new ZipEntry("com/graphql_java_generator/SomeFile.java"));
		jarOut.write("Some text".getBytes());
		jarOut.closeEntry();
		jarOut.close();
		fout.close();
	}

}
