package com.graphql_java_generator.plugin.test.compiler;

import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.graphql_java_generator.plugin.conf.GraphQLConfiguration;

@Component
public class CompilationTestHelper {

	private static final Logger logger = LoggerFactory.getLogger(CompilationTestHelper.class);

	@Resource
	GraphQLConfiguration pluginConfiguration;

	/**
	 * Execute a full compilation, to check the whole project.
	 * 
	 * @throws MojoExecutionException
	 */
	public void checkCompleteCompilationStatus(String classpath) {
		logger.info("Check full project compilation status with classpath: " + classpath);
		GeneratedSourceCompiler compilerPortFacadeImpl = GeneratedSourceCompilerFactory.getGeneratedSourceCompiler(
				logger, null, pluginConfiguration.getTargetSourceFolder(), pluginConfiguration.getTargetClassFolder(),
				classpath, pluginConfiguration.getSourceEncoding());
		assertTrue(compilerPortFacadeImpl.compileAllSources(), "Full project compilation status");
	}

}
