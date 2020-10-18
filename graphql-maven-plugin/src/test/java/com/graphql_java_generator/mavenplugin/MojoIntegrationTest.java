/**
 * 
 */
package com.graphql_java_generator.mavenplugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.it.Verifier;
import org.apache.maven.it.util.ResourceExtractor;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

/**
 * This unit test builds the pom that is in the src/test/resources/integration-test folder
 * 
 * @author etienne-sf
 */
@Disabled // Not ready yet. Needs more time to understand how to use the maven-verifier plugin
@Execution(ExecutionMode.CONCURRENT)
public class MojoIntegrationTest {

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void integrationTest() throws Exception {

		// Check in your dummy Maven project in /src/test/resources/...
		// The testdir is computed from the location of this file.
		File testDir = ResourceExtractor.simpleExtractResources(getClass(), "/integration-test");

		// We must first make sure that any artifact created by this test has been removed from the local repository.
		// Failing to do this could cause unstable test results. Fortunately, the verifier makes it easy to do this.
		Verifier verifier = new Verifier(testDir.getAbsolutePath());
		verifier.deleteArtifact("org.apache.maven.its.itsample", "parent", "1.0", "pom");

		/*
		 * The Command Line Options (CLI) are passed to the verifier as a list. This is handy for things like redefining
		 * the local repository if needed. In this case, we use the -N flag so that Maven won't recurse. We are only
		 * installing the parent pom to the local repo here.
		 */
		List<String> cliOptions = new ArrayList<>();
		cliOptions.add("-N");
		verifier.executeGoal("install");

		/*
		 * This is the simplest way to check a build succeeded. It is also the simplest way to create an IT test: make
		 * the build pass when the test should pass, and make the build fail when the test should fail. There are other
		 * methods supported by the verifier. They can be seen here:
		 * http://maven.apache.org/shared/maven-verifier/apidocs/index.html
		 */
		verifier.verifyErrorFreeLog();

		/*
		 * Reset the streams before executing the verifier again.
		 */
		verifier.resetStreams();

		/*
		 * The verifier also supports beanshell scripts for verification of more complex scenarios. There are plenty of
		 * examples in the core-it tests here: https://svn.apache.org/repos/asf/maven/core-integration-testing/trunk
		 */ }

}
