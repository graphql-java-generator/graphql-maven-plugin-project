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

	/** The packageName in which the generated classes will be created */
	@Parameter(property = "graphql.mavenplugin.packageName", defaultValue = "com.generated.graphql")
	String packageName;

	/** The encoding for the generated source files */
	@Parameter(property = "graphql.mavenplugin.encoding", defaultValue = "UTF-8")
	String encoding;

	/**
	 * The generation mode: either client or server. Choose client to generate the code which can query a graphql server
	 * or server to generate a code for the server side.
	 */
	@Parameter(property = "graphql.mavenplugin.mode", defaultValue = "client")
	PluginMode mode;

	/**
	 * The pattern to find the graphql schema file(s). The default value is "/*.graphqls" meaning that the maven plugin
	 * will search all graphqls files in the "src/main/resources" folder, and that the generated code will search for
	 * all graphqls file in the root of the classpath.<BR/>
	 * For instance, you can set in schemaFilePattern this value "myFolder/*.graphqls" to search for all schemas in the
	 * "myFolder" subfolder of src/main/resources (for the plugin execution). At runtime, the path used for search will
	 * then be classpath:/myFolder/*.graphqls".<BR/>
	 * You can also define one schema, by putting "mySchema.myOtherExtension" in the schemaFilePattern configuration
	 * parameter of the plugin.
	 */
	@Parameter(property = "graphql.mavenplugin.schemaFilePattern", defaultValue = "*.graphqls")
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

			getLog().info("Starting generation of java classes from graphqls files");

			// We'll use Spring IoC
			SpringConfiguration.mojo = this;
			AbstractApplicationContext ctx = new AnnotationConfigApplicationContext(SpringConfiguration.class);

			DocumentParser documentParser = ctx.getBean(DocumentParser.class);
			documentParser.parseDocuments();

			CodeGenerator codeGenerator = ctx.getBean(CodeGenerator.class);
			int nbGeneratedClasses = codeGenerator.generateCode();

			ctx.close();

			project.addCompileSourceRoot(targetSourceFolder.getAbsolutePath());

			getLog().info(nbGeneratedClasses + " java classes have been generated from graphqls files");

		} catch (Exception e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}

}
