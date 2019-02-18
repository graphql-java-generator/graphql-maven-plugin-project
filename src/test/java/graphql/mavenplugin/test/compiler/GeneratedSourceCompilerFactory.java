/**
 * 
 */
package graphql.mavenplugin.test.compiler;

import java.io.File;
import java.nio.charset.Charset;

import org.apache.maven.plugin.logging.Log;

/**
 * 
 * 
 * @author EtienneSF
 */
public class GeneratedSourceCompilerFactory {

	/**
	 * The method to get a new instance of {@link GeneratedSourceCompiler}
	 * 
	 * @param log
	 *            The logger, where to write all output messages
	 * @param className
	 *            The clas that is being generated
	 * @param javaSrcFolder
	 *            The folder, where to store java files.
	 * @param classTargetFolder
	 *            The folder, where to store class files.
	 * @param classpath
	 *            The classpath to be added to the compiler classpath, to allow compilation of the received generated
	 *            sources
	 * @param charset
	 *            The name of the {@link Charset} (like UTF-8...), to use to store the java source file.
	 * @return
	 */
	public static GeneratedSourceCompiler getGeneratedSourceCompiler(Log log, String className, File javaSrcFolder,
			File classTargetFolder, String classpath, String encoding) {
		return new GeneratedSourceCompilerImpl(log, className, javaSrcFolder, classTargetFolder, classpath,
				Charset.forName(encoding));
	}

}
