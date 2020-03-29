package com.graphql_java_generator.plugin.compilation_tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;

/**
 * Base integration test for Custom templates scenario
 * Offers methods to assert customized code validation
 * @author ggomez
 *
 */
public abstract class AbstractCustomTemplateIntegrationTest extends AbstractIntegrationTest {
	
	protected static final String CUSTOMIZED_CODE_FIRST_LINE = "/** This template is custom **/";
	
	/**
	 * Helper method that validates that given dir contains files generated with custom templates
	 * Custom template file are identified by it's first line. It's value is \/** This template is custom **\/
	 * @param generatedSourcesDir
	 */
	protected void assertCustomTemplateGeneration(File sourcesDirectory) {
		// Validate that every file generated has been generated with the templates in src/test/resources/templates_personalization
		Arrays.stream(sourcesDirectory.listFiles())
			.filter(generatedFile -> generatedFile.exists() && generatedFile.isFile())
			.forEach(generatedFile-> {
				try {
					assertEquals(CUSTOMIZED_CODE_FIRST_LINE, FileUtils.readLines(generatedFile, "UTF-8").get(0), 
							String.format("File %s is not generated with custom template", generatedFile));					
				} catch(Exception e) {
					throw new RuntimeException(e);
				}
			});
	}
		

}
