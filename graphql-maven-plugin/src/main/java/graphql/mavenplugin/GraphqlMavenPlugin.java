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

	@Parameter(property = "graphql.basePackage", defaultValue = "com.generated.graphql")
	String basePackage;

	@Parameter(property = "graphql.encoding", defaultValue = "UTF-8")
	String encoding;

	@Parameter(property = "graphql.schemaFilePattern", defaultValue = "src/main/resources/*.graphqls")
	String schemaFilePattern;

	@Parameter(property = "graphql.targetSourceFolder", defaultValue = "${project.build.directory}/generated-sources/graphql-client")
	File targetSourceFolder;

	@Parameter(defaultValue = "${project}", readonly = true, required = true)
	MavenProject project;

	public void execute() throws MojoExecutionException, MojoFailureException {
		try {

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
			getLog().info(nbGeneratedClasses + "java classes have been generated from graphqls files");

		} catch (Exception e) {
			throw new MojoExecutionException(e.getMessage(), e);
		}
	}

}
