package com.graphql_java_generator.plugin.test.compiler;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import com.graphql_java_generator.plugin.conf.GraphQLConfiguration;

@Component
@PropertySource("test_compile.properties")
public class CompilationTestHelper {

	private static final Logger logger = LoggerFactory.getLogger(CompilationTestHelper.class);

	@Value("${java.version}")
	private String javaVersion;
	@Value("${java.release}")
	private String javaRelease;

	@Autowired
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
				classpath, pluginConfiguration.getSourceEncoding(), javaVersion, javaRelease);

		assertTrue(compilerPortFacadeImpl.compileAllSources(), "Full project compilation status");
	}

}
