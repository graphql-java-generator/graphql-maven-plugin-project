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

/**
 * 
 * @author etienne-sf
 */
public class GraphQLConfigurationTestHelper implements GraphQLConfiguration {

	// All getters are generated thanks to Lombok, see the '@Getter' class annotation
	public Logger logger;

	public BatchMappingDataFetcherReturnType batchMappingDataFetcherReturnType = BatchMappingDataFetcherReturnType.FLUX;
	public boolean addRelayConnections = false;
	public boolean copyRuntimeSources = false;
	public List<CustomScalarDefinition> customScalars = new ArrayList<>();
	public boolean generateBatchLoaderEnvironment = true; // Server side
	public boolean generateBatchMappingDataFetchers = false; // Server side
	public boolean generateDataFetcherForEveryFieldsWithArguments = false;// Server side
	public boolean generateDataLoaderForLists = false;// Server side
	public boolean generateDeprecatedRequestResponse = false;// Client side
	public Boolean generateJacksonAnnotations = null; // (for POJO only)See below: isGenerateJacksonAnnotations()
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
	public File projectBuildDir;// Initialized in the test constructors
	public File projectDir;// Initialized in the test constructor
	public File projectMainSourceFolder;// Initialized in the test constructor
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
	public String targetSchemaSubFolder = CommonConfiguration.DEFAULT_TARGET_SCHEMA_SUBFOLDER;
	public File targetSourceFolder = null;
	public Map<String, String> templates = new HashMap<String, String>();
	public boolean useJakartaEE9 = true;

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
		logger = LoggerFactory.getLogger(caller.getClass());

		try {
			projectDir = new File(".").getCanonicalPath().endsWith("graphql-maven-plugin-logic") ? //
					new File(".") : new File("./graphql-maven-plugin-logic");
			projectMainSourceFolder = new File(projectDir, "src/main/java");
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	@Override
	public boolean isGenerateJacksonAnnotations() {
		if (generateJacksonAnnotations != null) {
			return generateJacksonAnnotations;
		} else {
			return mode.equals(PluginMode.client);
		}
	}

	public void setGenerateJacksonAnnotations(boolean generateJacksonAnnotations) {
		this.generateJacksonAnnotations = generateJacksonAnnotations;
	}

	public Logger getLogger() {
		return logger;
	}

	@Override
	public BatchMappingDataFetcherReturnType getBatchMappingDataFetcherReturnType() {
		return batchMappingDataFetcherReturnType;
	}

	@Override
	public boolean isAddRelayConnections() {
		return addRelayConnections;
	}

	@Override
	public boolean isCopyRuntimeSources() {
		return copyRuntimeSources;
	}

	@Override
	public List<CustomScalarDefinition> getCustomScalars() {
		return customScalars;
	}

	@Override
	public boolean isGenerateBatchLoaderEnvironment() {
		return generateBatchLoaderEnvironment;
	}

	@Override
	public boolean isGenerateBatchMappingDataFetchers() {
		return generateBatchMappingDataFetchers;
	}

	@Override
	public boolean isGenerateDataFetcherForEveryFieldsWithArguments() {
		return generateDataFetcherForEveryFieldsWithArguments;
	}

	@Override
	public boolean isGenerateDataLoaderForLists() {
		return generateDataLoaderForLists;
	}

	@Override
	public boolean isGenerateDeprecatedRequestResponse() {
		return generateDeprecatedRequestResponse;
	}

	public Boolean getGenerateJacksonAnnotations() {
		return generateJacksonAnnotations;
	}

	@Override
	public boolean isGenerateJPAAnnotation() {
		return generateJPAAnnotation;
	}

	@Override
	public boolean isGenerateUtilityClasses() {
		return generateUtilityClasses;
	}

	@Override
	public String getIgnoredSpringMappings() {
		return ignoredSpringMappings;
	}

	@Override
	public String getJavaTypeForIDType() {
		return javaTypeForIDType;
	}

	@Override
	public String getJsonGraphqlSchemaFilename() {
		return jsonGraphqlSchemaFilename;
	}

	@Override
	public PluginMode getMode() {
		return mode;
	}

	@Override
	public String getPackageName() {
		return packageName;
	}

	@Override
	public Integer getMaxTokens() {
		return maxTokens;
	}

	@Override
	public Packaging getPackaging() {
		return packaging;
	}

	@Override
	public File getProjectBuildDir() {
		return projectBuildDir;
	}

	@Override
	public File getProjectDir() {
		return projectDir;
	}

	@Override
	public File getProjectMainSourceFolder() {
		return projectMainSourceFolder;
	}

	@Override
	public QueryMutationExecutionProtocol getQueryMutationExecutionProtocol() {
		return queryMutationExecutionProtocol;
	}

	@Override
	public String getScanBasePackages() {
		return scanBasePackages;
	}

	@Override
	public File getSchemaFileFolder() {
		return schemaFileFolder;
	}

	@Override
	public String getSchemaFilePattern() {
		return schemaFilePattern;
	}

	@Override
	public File getSchemaPersonalizationFile() {
		return schemaPersonalizationFile;
	}

	@Override
	public boolean isSeparateUtilityClasses() {
		return separateUtilityClasses;
	}

	@Override
	public boolean isSkipGenerationIfSchemaHasNotChanged() {
		return skipGenerationIfSchemaHasNotChanged;
	}

	@Override
	public String getSourceEncoding() {
		return sourceEncoding;
	}

	@Override
	public String getSpringBeanSuffix() {
		return springBeanSuffix;
	}

	@Override
	public File getTargetClassFolder() {
		return targetClassFolder;
	}

	@Override
	public File getTargetResourceFolder() {
		return targetResourceFolder;
	}

	@Override
	public String getTargetSchemaSubFolder() {
		return targetSchemaSubFolder;
	}

	@Override
	public File getTargetSourceFolder() {
		return targetSourceFolder;
	}

	@Override
	public Map<String, String> getTemplates() {
		return templates;
	}

	@Override
	public boolean isUseJakartaEE9() {
		return useJakartaEE9;
	}

	@Override
	public String getTypePrefix() {
		return typePrefix;
	}

	@Override
	public String getTypeSuffix() {
		return typeSuffix;
	}

	@Override
	public String getUnionPrefix() {
		return unionPrefix;
	}

	@Override
	public String getUnionSuffix() {
		return unionSuffix;
	}

	@Override
	public String getEnumPrefix() {
		return enumPrefix;
	}

	@Override
	public String getEnumSuffix() {
		return enumSuffix;
	}

	@Override
	public String getInterfacePrefix() {
		return interfacePrefix;
	}

	@Override
	public String getInterfaceSuffix() {
		return interfaceSuffix;
	}

	@Override
	public String getInputPrefix() {
		return inputPrefix;
	}

	@Override
	public String getInputSuffix() {
		return inputSuffix;
	}

}
