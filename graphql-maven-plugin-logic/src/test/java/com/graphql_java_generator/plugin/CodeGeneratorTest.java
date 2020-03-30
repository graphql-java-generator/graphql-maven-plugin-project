package com.graphql_java_generator.plugin;

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

import javax.annotation.Resource;

import org.apache.commons.io.FileUtils;
import org.apache.velocity.Template;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.context.ApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.graphql_java_generator.plugin.language.Type;
import com.graphql_java_generator.plugin.language.impl.ObjectType;
import com.graphql_java_generator.plugin.test.helper.MavenTestHelper;
import com.graphql_java_generator.plugin.test.helper.PluginConfigurationTestHelper;

import graphql.mavenplugin_notscannedbyspring.AllGraphQLCases_Server_SpringConfiguration;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { AllGraphQLCases_Server_SpringConfiguration.class })
class CodeGeneratorTest {

	@Resource
	ApplicationContext context;
	@Resource
	PluginConfigurationTestHelper pluginConfiguration;
	@Resource
	MavenTestHelper mavenTestHelper;

	private File targetSourceFolder;
	private File targetRuntimeClassesSourceFolder;
	private File testRuntimeSourcesFile;
	private CodeGenerator codeGenerator;

	@BeforeEach
	void setUp() throws Exception {
		targetSourceFolder = mavenTestHelper.getTargetSourceFolder(this.getClass().getSimpleName());
		targetRuntimeClassesSourceFolder = mavenTestHelper
				.getTargetRuntimeClassesBaseSourceFolder(this.getClass().getSimpleName());
		testRuntimeSourcesFile = mavenTestHelper.getTestRutimeSourcesJarFile();

		if (targetSourceFolder.exists()) {
			FileUtils.forceDelete(targetSourceFolder);
			targetSourceFolder.mkdirs();
		}

		if (testRuntimeSourcesFile.exists()) {
			testRuntimeSourcesFile.delete();
		}

		codeGenerator = context.getBean(CodeGenerator.class);
		codeGenerator.documentParser = new DocumentParser();
	}

	@Test
	@DirtiesContext
	void testCodeGenerator() {
		assertNotNull(codeGenerator.velocityEngine, "Velocity engine must be initialized");
	}

	/**
	 * This test is important, as it checks the Velocity context which is sent to the templates. As it will be possible
	 * for users of the plugin, to define their own templates, the Velocity context is a kind of API, exposed to other's
	 * templates. It must remain stable. Field can be added. <B>But no field should be removed</B>.
	 */
	@Test
	@DirtiesContext
	void test_generateTargetFile_client() {
		// Let's mock the Velocity engine, to check how it is called
		codeGenerator.velocityEngine = mock(VelocityEngine.class);
		Template mockedTemplate = mock(Template.class);
		when(codeGenerator.velocityEngine.getTemplate(anyString(), anyString())).thenReturn(mockedTemplate);

		codeGenerator.documentParser = mock(DocumentParser.class);
		pluginConfiguration.mode = PluginMode.client;

		ObjectType object1 = new ObjectType(pluginConfiguration.getPackageName(), PluginMode.client);
		ObjectType object2 = new ObjectType(pluginConfiguration.getPackageName(), PluginMode.client);
		List<Type> objects = new ArrayList<>();
		objects.add(object1);
		objects.add(object2);

		String type = "my test type";
		String templateFilename = "folder/a_template_for_test.vm";

		// Go, go, go
		int i = codeGenerator.generateTargetFiles(objects, type, templateFilename);

		// Verification
		assertEquals(objects.size(), i, "Nb files generated");

		// Let's check the parameter for getTemplate
		ArgumentCaptor<String> argument1 = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<String> argument2 = ArgumentCaptor.forClass(String.class);
		verify(codeGenerator.velocityEngine, times(2)).getTemplate(argument1.capture(), argument2.capture());
		assertEquals(templateFilename, argument1.getValue(), "checks the parameter for getTemplate");
		assertEquals("UTF-8", argument2.getValue());

		///////////////////////////////////////////////////////////////////////////////////////////////////////////
		// Let's check the velocity context sent to the template ... THIS IS IMPORTANT! DO NOT BREAK IT!
		///////////////////////////////////////////////////////////////////////////////////////////////////////////
		ArgumentCaptor<Context> argumentContext = ArgumentCaptor.forClass(Context.class);
		verify(mockedTemplate, times(2)).merge(argumentContext.capture(), any(Writer.class));
		// We have the Context sent to the Template.merge(..) method. Let's check its content
		assertEquals(pluginConfiguration.getPackageName(),
				((PluginConfiguration) argumentContext.getValue().get("pluginConfiguration")).getPackageName(),
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
	@DirtiesContext
	void test_generateTargetFile_server() {
		// Let's mock the Velocity engine, to check how it is called
		codeGenerator.velocityEngine = mock(VelocityEngine.class);
		Template mockedTemplate = mock(Template.class);
		when(codeGenerator.velocityEngine.getTemplate(anyString(), anyString())).thenReturn(mockedTemplate);

		pluginConfiguration.mode = PluginMode.server;

		ObjectType object1 = new ObjectType(pluginConfiguration.getPackageName(), PluginMode.server);
		ObjectType object2 = new ObjectType(pluginConfiguration.getPackageName(), PluginMode.server);
		List<Type> objects = new ArrayList<>();
		objects.add(object1);
		objects.add(object2);

		String type = "my test type";
		String templateFilename = "folder/a_template_for_test.vm";

		// Go, go, go
		int i = codeGenerator.generateTargetFiles(objects, type, templateFilename);

		// Verification
		assertEquals(objects.size(), i, "Nb files generated");

		// Let's check the parameter for getTemplate
		ArgumentCaptor<String> argument1 = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<String> argument2 = ArgumentCaptor.forClass(String.class);
		verify(codeGenerator.velocityEngine, times(2)).getTemplate(argument1.capture(), argument2.capture());
		assertEquals(templateFilename, argument1.getValue(), "checks the parameter for getTemplate");
		assertEquals("UTF-8", argument2.getValue());

		///////////////////////////////////////////////////////////////////////////////////////////////////////////
		// Let's check the velocity context sent to the template ... THIS IS IMPORTANT! DO NOT BREAK IT!
		///////////////////////////////////////////////////////////////////////////////////////////////////////////
		ArgumentCaptor<Context> argumentContext = ArgumentCaptor.forClass(Context.class);
		verify(mockedTemplate, times(2)).merge(argumentContext.capture(), any(Writer.class));
		// We have the Context sent to the Template.merge(..) method. Let's check its content
		assertEquals(pluginConfiguration.getPackageName(),
				((PluginConfiguration) argumentContext.getValue().get("pluginConfiguration")).getPackageName(),
				"Context: checks the package");
		assertEquals(object1, argumentContext.getValue().get("object"), "Context: checks the package");
		assertEquals(type, argumentContext.getValue().get("type"), "Context: checks the package");
		///////////////////////////////////////////////////////////////////////////////////////////////////////////
	}

	@Test
	@DirtiesContext
	void testGetJavaFile() throws IOException {
		// Preparation
		String name = "MyClass";
		String packageName = "my.package";
		pluginConfiguration.packageName = packageName;
		pluginConfiguration.targetSourceFolder = targetSourceFolder;

		// Go, go, go
		File file = codeGenerator.getJavaFile(name);

		// Verification
		String expectedEndOfPath = (targetSourceFolder.getCanonicalPath() + '/' + packageName + '/' + name)
				.replace('.', '/').replace('\\', '/') + ".java";
		assertEquals(expectedEndOfPath, file.getCanonicalPath().replace('\\', '/'), "The file path should end with "
				+ expectedEndOfPath + ", but is " + file.getCanonicalPath().replace('\\', '/'));
	}

	/**
	 * Test to validate the code generation process copies runtime sources if
	 * {@link PluginConfiguration#isCopyGraphQLJavaSources()} is set to true
	 * 
	 * @throws IOException
	 */
	@Test
	@DirtiesContext
	void testGenerateCode_copySources() throws IOException {

		pluginConfiguration.mode = PluginMode.client;
		pluginConfiguration.packageName = "test.generatecode.enabled";
		pluginConfiguration.copyGraphQLJavaSources = true;
		pluginConfiguration.schemaFileFolder = new File("src/test/resources");
		pluginConfiguration.schemaFilePattern = "basic.graphqls";
		pluginConfiguration.targetSourceFolder = targetSourceFolder;
		pluginConfiguration.targetClassFolder = targetSourceFolder;

		if (Objects.isNull(getClass().getResourceAsStream("/graphql-java-runtime-sources.jar"))) {
			createRuntimeSourcesJar();
		}

		codeGenerator.generateCode();
		assertTrue(targetRuntimeClassesSourceFolder.exists());
		assertTrue(targetRuntimeClassesSourceFolder.isDirectory());
	}

	/**
	 * Test to validate the code generation process does not copy runtime sources if
	 * {@link PluginConfiguration#isCopyGraphQLJavaSources()} is set to false
	 * 
	 * @throws IOException
	 */
	@Test
	@DirtiesContext
	void testGenerateCode_skipCopySources() throws IOException {

		pluginConfiguration.mode = PluginMode.client;
		pluginConfiguration.packageName = "test.generatecode.enabled";
		pluginConfiguration.copyGraphQLJavaSources = false;
		pluginConfiguration.schemaFileFolder = new File("src/test/resources");
		pluginConfiguration.schemaFilePattern = "basic.graphqls";
		pluginConfiguration.targetSourceFolder = targetSourceFolder;
		pluginConfiguration.targetClassFolder = targetSourceFolder;

		codeGenerator.generateCode();
		assertFalse(targetRuntimeClassesSourceFolder.exists());
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
