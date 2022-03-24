package com.graphql_java_generator.plugin.test.helper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.graphql_java_generator.plugin.conf.CustomScalarDefinition;
import com.graphql_java_generator.plugin.conf.GenerateServerCodeConfiguration;
import com.graphql_java_generator.plugin.conf.GraphQLConfiguration;
import com.graphql_java_generator.plugin.conf.Packaging;
import com.graphql_java_generator.plugin.conf.PluginMode;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author etienne-sf
 */
@Getter
@Setter
public class GraphQLConfigurationTestHelper implements GraphQLConfiguration {

	// All getters are generated thanks to Lombok, see the '@Getter' class annotation
	public Logger logger;

	public boolean addRelayConnections = false;
	public boolean copyRuntimeSources = false; // This will speed build time up (less classes to compile, and allow
	// to load several generated source folders in the IDE.
	public List<CustomScalarDefinition> customScalars = new ArrayList<>();
	public boolean generateBatchLoaderEnvironment = false;
	public boolean generateDataLoaderForLists = false;
	public boolean generateDeprecatedRequestResponse = true;
	private Boolean generateJacksonAnnotations = null; // See below: isGenerateJacksonAnnotations() either
														// generateJacksonAnnotations if it is not null, or true id
														// client mode, or false if server mode. Because of this rule,
														// Velocity MUST call the isGenerateJacksonAnnotations() method.
														// So this attribute must be private
	public boolean generateJPAAnnotation = true;
	public boolean generateUtilityClasses = true;
	public String javaTypeForIDType = GenerateServerCodeConfiguration.DEFAULT_JAVA_TYPE_FOR_ID_TYPE;
	public PluginMode mode = null;
	public String packageName = "org.my.test.package";
	public Integer maxTokens;
	public Packaging packaging = null;
	public File projectDir;// Initialized in the constructor
	public String scanBasePackages = "null";
	public File schemaFileFolder = null;
	public String schemaFilePattern = null;
	public File schemaPersonalizationFile = null;
	public boolean separateUtilityClasses = false;
	// As the GraphQL schema won't change, and we always want to regenerate the sources, we won't skip it
	public boolean skipGenerationIfSchemaHasNotChanged = false;
	public String sourceEncoding = "UTF-8";
	public String springBeanSuffix = "MySchema";
	public File targetClassFolder = null;
	public File targetResourceFolder = null;
	public File targetSourceFolder = null;
	public Map<String, String> templates = new HashMap<String, String>();

	/**
	 * @param caller
	 *            Used to retrieve the appropriate Log4j logger
	 */
	public GraphQLConfigurationTestHelper(Object caller) {
		logger = LoggerFactory.getLogger(caller.getClass());

		maxTokens = 200000; // Necessary for github and shopify schemas.

		try {
			projectDir = new File(".").getCanonicalPath().endsWith("graphql-maven-plugin-logic") ? //
					new File(".") : new File("./graphql-maven-plugin-logic");
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	@Override
	public boolean isGenerateJacksonAnnotations() {
		if (generateJacksonAnnotations != null)
			return generateJacksonAnnotations;
		else
			return mode.equals(PluginMode.client);
	}

	public void setGenerateJacksonAnnotations(boolean generateJacksonAnnotations) {
		this.generateJacksonAnnotations = generateJacksonAnnotations;
	}

}
