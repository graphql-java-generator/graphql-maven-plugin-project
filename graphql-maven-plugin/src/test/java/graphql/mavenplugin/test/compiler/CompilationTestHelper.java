package graphql.mavenplugin.test.compiler;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

import javax.annotation.Resource;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.springframework.stereotype.Component;

import graphql.mavenplugin.GraphqlMavenPlugin;
import graphql.mavenplugin.test.helper.AllGraphQLCasesSpringConfiguration;

@Component
public class CompilationTestHelper {

	/** The maven logging system */
	@Resource
	Log log;

	/** @See {@link GraphqlMavenPlugin#targetSourceFolder} */
	@Resource
	File targetSourceFolder;

	/** @see AllGraphQLCasesSpringConfiguration#targetClassFolder(File) */
	@Resource
	File targetClassFolder;

	/** @See {@link GraphqlMavenPlugin#encoding} */
	@Resource
	String encoding;

	/**
	 * Execute a full compilation, to check the whole project.
	 * 
	 * @throws MojoExecutionException
	 */
	public void checkCompleteCompilationStatus(String classpath) throws MojoExecutionException {
		log.info("Check full project compilation status with classpath: " + classpath);
		GeneratedSourceCompiler compilerPortFacadeImpl = GeneratedSourceCompilerFactory.getGeneratedSourceCompiler(log,
				null, targetSourceFolder, targetClassFolder, classpath, encoding);
		assertTrue(compilerPortFacadeImpl.compileAllSources(), "Full project compilation status");
	}

}
