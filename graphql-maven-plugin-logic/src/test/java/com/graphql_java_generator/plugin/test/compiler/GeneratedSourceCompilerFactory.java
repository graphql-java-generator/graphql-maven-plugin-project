/**
 * 
 */
package com.graphql_java_generator.plugin.test.compiler;

import java.io.File;
import java.nio.charset.Charset;

import org.slf4j.Logger;

/**
 * 
 * 
 * @author etienne-sf
 */
public class GeneratedSourceCompilerFactory {

	/**
	 * The method to get a new instance of {@link GeneratedSourceCompiler}
	 * 
	 * @param log
	 *            The logger, where to write all output messages
	 * @param className
	 *            The class that is being generated
	 * @param javaSrcFolder
	 *            The folder, where to store java files.
	 * @param classTargetFolder
	 *            The folder, where to store class files.
	 * @param classpath
	 *            The classpath to be added to the compiler classpath, to allow compilation of the received generated
	 *            sources. If null, the current classpath is used (taken from the "java.class.path" system property)
	 * @param javaRelease
	 * @param javaVersion
	 * @param charset
	 *            The name of the {@link Charset} (like UTF-8...), to use to store the java source file.
	 * @return
	 */
	public static GeneratedSourceCompiler getGeneratedSourceCompiler(Logger log, String className, File javaSrcFolder,
			File classTargetFolder, String classpath, String encoding, String javaVersion, String javaRelease) {
		classpath = (classpath == null) ? System.getProperty("java.class.path") : classpath;
		return new GeneratedSourceCompilerImpl(log, className, javaSrcFolder, classTargetFolder, classpath,
				Charset.forName(encoding), javaVersion, javaRelease);
	}

}
