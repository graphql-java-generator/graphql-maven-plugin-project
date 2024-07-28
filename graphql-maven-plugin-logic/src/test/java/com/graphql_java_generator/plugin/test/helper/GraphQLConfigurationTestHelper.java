package com.graphql_java_generator.plugin.test.helper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.graphql_java_generator.plugin.conf.BatchMappingDataFetcherReturnType;
import com.graphql_java_generator.plugin.conf.CommonConfiguration;
import com.graphql_java_generator.plugin.conf.CustomScalarDefinition;
import com.graphql_java_generator.plugin.conf.GenerateServerCodeConfiguration;
import com.graphql_java_generator.plugin.conf.GraphQLConfiguration;
import com.graphql_java_generator.plugin.conf.Packaging;
import com.graphql_java_generator.plugin.conf.PluginMode;
import com.graphql_java_generator.plugin.conf.QueryMutationExecutionProtocol;

import lombok.Getter;

/**
 * 
 * @author etienne-sf
 */
@Getter
public class GraphQLConfigurationTestHelper implements GraphQLConfiguration {

	// All getters are generated thanks to Lombok, see the '@Getter' class annotation
	public Logger logger;

	public BatchMappingDataFetcherReturnType batchMappingDataFetcherReturnType = BatchMappingDataFetcherReturnType.FLUX_V;
	public boolean addRelayConnections = false;
	public boolean copyRuntimeSources = false;
	public List<CustomScalarDefinition> customScalars = new ArrayList<>();
	public boolean generateBatchLoaderEnvironment = true; // Server side
	public boolean generateBatchMappingDataFetchers = false; // Server side
	public boolean generateDataFetcherForEveryFieldsWithArguments = false;// Server side
	public boolean generateDataLoaderForLists = false;// Server side
	public boolean generateDeprecatedRequestResponse = false;// Client side
	private Boolean generateJacksonAnnotations = null; // (for POJO only)See below: isGenerateJacksonAnnotations()
														// either generateJacksonAnnotations if it is not null, or true
														// id client mode, or false if server mode. Because of this
														// rule, Velocity MUST call the isGenerateJacksonAnnotations()
														// method. So this attribute must be private
	public boolean generateJPAAnnotation = true;// Server side
	public boolean generateUtilityClasses = true;// Both sides
	public String ignoredSpringMappings = "";// Server side
	public String javaTypeForIDType = GenerateServerCodeConfiguration.DEFAULT_JAVA_TYPE_FOR_ID_TYPE;// Server side
	public String jsonGraphqlSchemaFilename = null;// Both sides
	public PluginMode mode = null;
	public String packageName = "org.my.test.package";
	private Integer maxTokens = Integer.parseInt(CommonConfiguration.DEFAULT_MAX_TOKENS);
	public Packaging packaging = null;
	public File projectDir;// Initialized in the constructor
	public QueryMutationExecutionProtocol queryMutationExecutionProtocol = QueryMutationExecutionProtocol.http;
	public String scanBasePackages = "null";
	public File schemaFileFolder = null;
	public String schemaFilePattern = null;
	public File schemaPersonalizationFile = null;
	public boolean separateUtilityClasses = false;// Both sides
	public boolean skipGenerationIfSchemaHasNotChanged = true;
	public String sourceEncoding = "UTF-8";
	public String springBeanSuffix = "MySchema";
	public File targetClassFolder = null;
	public File targetResourceFolder = null;
	public File targetSourceFolder = null;
	public Map<String, String> templates = new HashMap<String, String>();
	public boolean useJakartaEE9 = false;

	public String typePrefix = "";
	public String typeSuffix = "";
	public String unionPrefix = "";
	public String unionSuffix = "";
	public String enumPrefix = "";
	public String enumSuffix = "";
	public String interfacePrefix = "";
	public String interfaceSuffix = "";
	public String inputPrefix = "";
	public String inputSuffix = "";

	/**
	 * @param caller
	 *            Used to retrieve the appropriate Log4j logger
	 */
	public GraphQLConfigurationTestHelper(Object caller) {
		this.logger = LoggerFactory.getLogger(caller.getClass());

		try {
			this.projectDir = new File(".").getCanonicalPath().endsWith("graphql-maven-plugin-logic") ? //
					new File(".") : new File("./graphql-maven-plugin-logic");
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	@Override
	public boolean isGenerateJacksonAnnotations() {
		if (this.generateJacksonAnnotations != null)
			return this.generateJacksonAnnotations;
		else
			return this.mode.equals(PluginMode.client);
	}

	public void setGenerateJacksonAnnotations(boolean generateJacksonAnnotations) {
		this.generateJacksonAnnotations = generateJacksonAnnotations;
	}

}
