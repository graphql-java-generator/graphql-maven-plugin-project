/**
 * 
 */
package graphql.mavenplugin.test.compiler;

import org.apache.maven.plugin.MojoExecutionException;

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
    public boolean compileAllSources() throws MojoExecutionException;

    /**
     * Compiles the java file.
     * 
     * @param javaSource
     * @return true if the compilation was Ok (without errors)
     * @throws MojoExecutionException
     */
    public boolean compileOneSource() throws MojoExecutionException;

    /**
     * This stores the given source code into the java file, then compiles it. The source file comes from the instance
     * parameters.
     * 
     * @param javaSource
     * @return true if the compilation was Ok (without errors)
     * @throws MojoExecutionException
     */
    public boolean compileGivenSource(String javaSource) throws MojoExecutionException;

}
