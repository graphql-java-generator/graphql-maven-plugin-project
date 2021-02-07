/**
 * 
 */
package com.graphql_java_generator.plugin;

import java.io.IOException;

/**
 * This class is reponsible for the generation of the sources and/or resources, depending on the Goal/Task.
 * 
 * @author etienne-sf
 */
public interface Generator {

	/**
	 * Execution of the code generation. The generation is done on the data build by the
	 * {@link #generateCodeDocumentParser}
	 * 
	 * @Return The number of generated classes
	 * @throws MojoExecutionException
	 * @throws IOException
	 */
	public int generateCode() throws IOException;
}
