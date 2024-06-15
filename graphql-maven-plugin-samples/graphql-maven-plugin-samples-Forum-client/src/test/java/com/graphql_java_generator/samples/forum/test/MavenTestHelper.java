package com.graphql_java_generator.samples.forum.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

@Component
public class MavenTestHelper {

	final static String MODULE_SAMPLE_NAME = "graphql-xxx-plugin-samples";
	final static String MODULE_NAME = "graphql-xxx-plugin-samples-Forum-client";
	final static String TARGET_RESOURCE_FOLDER = "/target/junittest_graphql/UNIT_TEST_NAME/generated-resources";
	final static String TARGET_SOURCE_FOLDER = "/target/junittest_graphql/UNIT_TEST_NAME/generated-src";
	final static String RUNTIME_BASE_PACKAGE_FOLDER = "com/graphql_java_generator";
	final static String TESTING_RUNTIME_SOURCE_FILE = "target/test-classes/graphql-java-runtime-sources.jar";

	/**
	 * Returns the root path for this module. The issue here, is that the current path is the root path for this module,
	 * if the build is done only for this module. But if the build is done for the whole project, the current path is
	 * one level above (that is: the root for the whole project)
	 * 
	 * @return
	 */
	public File getModulePathFile() {
		String path = new File(".").getAbsolutePath();
		String mavenOrGradle = path.contains("graphql-gradle-plugin") ? "gradle" : "maven";
		String moduleName = MODULE_NAME.replace("xxx", mavenOrGradle);
		String moduleSampleName = MODULE_SAMPLE_NAME.replace("xxx", mavenOrGradle);

		if (path.contains(moduleName)) {
			// The current path is the full one
			return new File(path);
		} else if (path.contains(moduleSampleName)) {
			// Only samples are being build
			return new File(path, moduleName);
		} else {
			// The current folder is the main project
			return new File(path, moduleName + "/" + moduleSampleName);
		}
	}

	/**
	 * Get the folder where the resources should be generated, calculated from the given test name. <BR/>
	 * For instance, for test 'Basic', the folder would be something like
	 * File("${project_folder}/target/junittest/basic/generated-resources")
	 * 
	 * @param unitTestName
	 * @return
	 */
	public File getTargetResourceFolder(String unitTestName) {
		return new File(getModulePathFile(), TARGET_RESOURCE_FOLDER.replace("UNIT_TEST_NAME", unitTestName));
	}

	/**
	 * Get the folder where the source should be generated, calculated from the given test name. <BR/>
	 * For instance, for test 'Basic', the folder would be something like
	 * File("${project_folder}/target/junittest/basic/generated-src")
	 * 
	 * @param unitTestName
	 * @return
	 */
	public File getTargetSourceFolder(String unitTestName) {
		return new File(getModulePathFile(), TARGET_SOURCE_FOLDER.replace("UNIT_TEST_NAME", unitTestName));
	}

	/**
	 * Gets the base folder for runtime classes calculated for the given test name
	 * 
	 * @param unitTestName
	 * @return
	 */
	public File getTargetRuntimeClassesBaseSourceFolder(String unitTestName) {
		return new File(getTargetSourceFolder(unitTestName), RUNTIME_BASE_PACKAGE_FOLDER);
	}

	/**
	 * Resolve the file for runtime sources to be used in IDE tests related to code generation
	 * 
	 * @return
	 */
	public File getTestRutimeSourcesJarFile() {
		return new File(TESTING_RUNTIME_SOURCE_FILE);
	}

	/**
	 * Remove a folder and all its content. This method checks that this folder is in a target subfolder. That is: it
	 * contains 'target' in it.
	 * 
	 * @param file
	 *            The folder to delete (including its content, subfolders...
	 * @return true if the file was removed. false if the file didn't exist
	 * @throws IllegalArgumentException
	 *             If it's not a target folder
	 */
	public boolean deleteDirectoryAndContentIfExists(File file) throws IllegalArgumentException {
		if (!file.getAbsolutePath().contains("target")) {
			throw new IllegalArgumentException(file.getAbsolutePath() + " is not in a target folder");
		}
		if (file.exists()) {
			if (!file.isDirectory()) {
				throw new IllegalArgumentException(file.getAbsolutePath() + " is not a folder");
			}
			File[] files = file.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					deleteDirectoryAndContentIfExists(files[i]);
				} else {
					files[i].delete();
				}
			} // for
			return file.delete();
		} // if

		return false;
	}

	/**
	 * Read a file, and returns its content as a string
	 * 
	 * @param relativePath
	 *            The relative path from the project's base dir (e.g.: /src/test/resources/test.txt or
	 *            src/test/resources/test.txt)
	 * @return The content of the file, which is expected to be a text file
	 */
	public String readFile(String relativePath) {
		String path = ((relativePath.startsWith("/") || (relativePath.startsWith("\\"))) ? "" : "/") + relativePath;
		return readFile(new File(getModulePathFile(), path));
	}

	/**
	 * Write a string into a file in the current maven module.
	 * 
	 * @param content
	 *            The string that will be written in the file
	 * @param relativePath
	 *            The relative path from the project's base dir (e.g.: /src/test/resources/test.txt or
	 *            src/test/resources/test.txt)
	 * @return The content of the file, which is expected to be a text file
	 */
	public void writeFile(String relativePath, String content) {
		String path = ((relativePath.startsWith("/") || (relativePath.startsWith("\\"))) ? "" : "/") + relativePath;
		File file = new File(getModulePathFile(), path);
		try (FileWriter writer = new FileWriter(file)) {
			writer.write(content);
		} catch (IOException e) {
			throw new IllegalStateException("Cannot write file " + file.getPath(), e);
		}
	}

	/**
	 * Read a file, and returns its content as a string
	 * 
	 * @param file
	 *            The file to read
	 * @return The content of the file, which is expected to be a text file
	 */
	public String readFile(File file) {
		StringWriter writer = new StringWriter();
		try (InputStream inputStream = new FileInputStream(file)) {
			IOUtils.copy(inputStream, writer, StandardCharsets.UTF_8);
		} catch (IOException e) {
			throw new IllegalStateException("Cannot read file " + file.getPath(), e);
		}
		return writer.toString();
	}
}
