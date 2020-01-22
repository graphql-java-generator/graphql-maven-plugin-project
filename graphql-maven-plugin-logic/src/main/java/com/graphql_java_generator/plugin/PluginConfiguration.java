/**
 * 
 */
package com.graphql_java_generator.plugin;

import java.io.File;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * This interface contains all the configuration parameters for the plugin, as an interface.<BR/>
 * All these methods are directly the property names, to map against a Spring {@link Configuration} that defines the
 * {@link Bean}s. These beans can then be reused in Spring Component, thank to Spring IoC and its dependency injection
 * capability.
 * 
 * @author EtienneSF
 */
public interface PluginConfiguration {

	public final static String DEFAULT_PACKAGE_NAME = "com.generated.graphql";
	public final static String DEFAULT_SOURCE_ENCODING = "UTF-8";
	public final static String DEFAULT_MODE = "client";// Must be a string, for maven plugin declaration
	public final static String DEFAULT_SCHEMA_FILE_PATTERN = "*.graphqls";
	public final static String DEFAULT_SCHEMA_PERSONALIZATION_FILE = "null"; // Can't by null, must be a valid String.
																				// Dummy Java issue... :(
	public final static String DEFAULT_TARGET_SOURCE_FOLDER = "/generated-sources/graphql-maven-plugin";

	/**
	 * Indicates whether the plugin should generate the JPA annotations, for generated objects, when in server mode.
	 * Default value is false
	 */
	public boolean getGenerateJPAAnnotation();

	/**
	 * The logging system to use. It's implemented against the JDK one, to avoid useless dependencies. For instance you
	 * can use log4j2, by adding the 'Log4j JDK Logging Adapter' (JUL)
	 */
	public Logger getLog();

	/**
	 * The generation mode: either client or server. Choose client to generate the code which can query a graphql server
	 * or server to generate a code for the server side.<BR/>
	 * This parameter is mandatory.
	 */
	public PluginMode getMode();

	/** The packageName in which the generated classes will be created */
	public String getPackageName();

	/**
	 * The packaging is the kind of artefact generated by the project. Typically: jar (for a standard Java application)
	 * or war (for a webapp)
	 */
	public Packaging getPackaging();

	/**
	 * The main resources folder, typically '/src/main/resources' of the current project. That's where the GraphQL
	 * schema(s) are expected to be: in this folder, or one of these subfolders
	 */
	public File getMainResourcesFolder();

	/**
	 * The pattern to find the graphql schema file(s). The default value is "/*.graphqls" meaning that the maven plugin
	 * will search all graphqls files in the "src/main/resources" folder. The current version can read only one
	 * file.<BR/>
	 * In the future, it will search for all graphqls file in the root of the classpath.<BR/>
	 * In the future, it will be possible to set in schemaFilePattern values like "myFolder/*.graphqls" to search for
	 * all schemas in the "myFolder" subfolder of src/main/resources (for the plugin execution). At runtime, the path
	 * used for search will then be classpath:/myFolder/*.graphqls".<BR/>
	 * It will also be possible to define one schema, by putting "mySchema.myOtherExtension" in the schemaFilePattern
	 * configuration parameter of the plugin.
	 */
	public String getSchemaFilePattern();

	/**
	 * Gets the Schema personalization file, which allows to override the default code generation behavior. See the
	 * plugin doc for more details.
	 * 
	 * @return
	 */
	public File getSchemaPersonalizationFile();

	/** The encoding for the generated source files */
	public String getSourceEncoding();

	/**
	 * The folder where the generated classes will be compiled, that is: where the class file are stored after
	 * compilation
	 */
	public File getTargetClassFolder();

	/** The folder where the generated classes will be generated */
	public File getTargetSourceFolder();

	/** Logs all the configuration parameters, in the debug level */
	default public void logConfiguration() {
		if (getLog().isDebugEnabled()) {
			getLog().debug("The graphql-java-generator Plugin Configuration is:");
			getLog().debug("  Mode: " + getMode());
			getLog().debug("  PackageName: " + getPackageName());
			getLog().debug("  Packaging: " + getPackaging());
			getLog().debug("  MainResourcesFolder: " + getMainResourcesFolder());
			getLog().debug("  SchemaFilePattern: " + getSchemaFilePattern());
			getLog().debug("  SchemaPersonalizationFile: " + getSchemaPersonalizationFile());
			getLog().debug("  SourceEncoding: " + getMode());
			getLog().debug("  TargetClassFolder: " + getTargetClassFolder());
			getLog().debug("  TargetSourceFolder: " + getTargetSourceFolder());
		}
	}
}
