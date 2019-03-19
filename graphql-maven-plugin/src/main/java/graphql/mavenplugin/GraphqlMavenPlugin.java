/**
 * 
 */
package graphql.mavenplugin;

import java.io.File;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

/**
 * @author EtienneSF
 */
@Mojo(name = "graphql", defaultPhase = LifecyclePhase.GENERATE_SOURCES, requiresProject = true)
public class GraphqlMavenPlugin extends AbstractMojo {

	final static String MODE_CLIENT = "client";
	final static String MODE_SERVER = "server";

	/** The basePackage in which the generated classes will be created */
	@Parameter(property = "graphql.mavenplugin.basePackage", defaultValue = "com.generated.graphql")
	String basePackage;

	/** The encoding for the generated source files */
	@Parameter(property = "graphql.mavenplugin.encoding", defaultValue = "UTF-8")
	String encoding;

	/**
	 * The generation mode: either client (to generate the code which can query a graphql server) or server (to generate
	 * a code for the server side)
	 */
	@Parameter(property = "graphql.mavenplugin.mode", defaultValue = "client")
	String mode;

	/** The description where the graphql schema file should be found, within the maven project structure */
	@Parameter(property = "graphql.mavenplugin.schemaFilePattern", defaultValue = "src/main/resources/*.graphqls")
	String schemaFilePattern;

	/** The folder where the generated classes will be generated */
	@Parameter(property = "graphql.mavenplugin.targetSourceFolder", defaultValue = "${project.build.directory}/generated-sources/graphql-maven-plugin")
	File targetSourceFolder;

	/** Not available to the user: the {@link MavenProject} in which the plugin executes */
	@Parameter(defaultValue = "${project}", readonly = true, required = true)
	MavenProject project;

	/** {@inheritDoc} */
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		try {

			if (!MODE_CLIENT.equals(mode) && !MODE_SERVER.equals(mode)) {
				throw new MojoExecutionException("mode must be one of these values: " + MODE_CLIENT + ", " + MODE_SERVER
						+ ". But it is: " + mode);
			}

			getLog().info("Starting generation of java classes from graphqls files");

			// We'll use Spring IoC
			SpringConfiguration.mojo = this;
			AbstractApplicationContext ctx = new AnnotationConfigApplicationContext(SpringConfiguration.class);

			DocumentParser documentParser = ctx.getBean(DocumentParser.class);
			int nbClassesToGenerate = documentParser.parseDocuments();

			CodeGenerator codeGenerator = ctx.getBean(CodeGenerator.class);
			int nbGeneratedClasses = codeGenerator.generateCode();

			ctx.close();

			project.addCompileSourceRoot(targetSourceFolder.getAbsolutePath());

			if (nbClassesToGenerate != nbGeneratedClasses) {
				getLog().warn(nbClassesToGenerate + " classes were parsed, but only " + nbGeneratedClasses
						+ " were generated");
			}
			getLog().info(nbGeneratedClasses + " java classes have been generated from graphqls files");

		} catch (Exception e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}

}
