/**
 * 
 */
package graphql.mavenplugin.test.compiler;

/**
 * @author EtienneSF
 */
public interface GeneratedSourceCompiler {

	/**
	 * Compile all sources stored in the root source folder for the instance.
	 * 
	 * @return true if the compilation was Ok (without errors)
	 * @throws MojoExecutionException
	 */
	public boolean compileAllSources();

	/**
	 * Compiles the java file.
	 * 
	 * @param javaSource
	 * @return true if the compilation was Ok (without errors)
	 * @throws MojoExecutionException
	 */
	public boolean compileOneSource();

	/**
	 * This stores the given source code into the java file, then compiles it. The source file comes from the instance
	 * parameters.
	 * 
	 * @param javaSource
	 * @return true if the compilation was Ok (without errors)
	 * @throws MojoExecutionException
	 */
	public boolean compileGivenSource(String javaSource);

}
