package com.graphql_java_generator.plugin.test.helper;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.graphql_java_generator.plugin.conf.CommonConfiguration;
import com.graphql_java_generator.plugin.conf.GenerateGraphQLSchemaConfiguration;

/**
 *
 * @author etienne-sf
 */
public class GenerateGraphQLSchemaConfigurationTestHelper implements GenerateGraphQLSchemaConfiguration {

	// All getters are generated thanks to Lombok, see the '@Getter' class annotation
	public Logger logger;

	public boolean addRelayConnections = false;
	public String jsonGraphqlSchemaFilename = null;
	public String packageName = "my.test.package";
	private Integer maxTokens = Integer.parseInt(CommonConfiguration.DEFAULT_MAX_TOKENS);
	public File projectBuildDir = new File("./target/junittest_merge");
	public File projectDir = new File("./graphql-maven-plugin-logic");
	public File projectMainSourceFolder = new File(projectDir, "src/main/java");

	public File schemaFileFolder = null;
	public String schemaFilePattern = null;
	public boolean skipGenerationIfSchemaHasNotChanged = true;
	public String resourceEncoding = "UTF-8";
	public File targetFolder = null;
	public String targetSchemaFileName = null;
	public String targetSchemaSubFolder = CommonConfiguration.DEFAULT_TARGET_SCHEMA_SUBFOLDER;
	public Map<String, String> templates = new HashMap<String, String>();

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
	public GenerateGraphQLSchemaConfigurationTestHelper(Object caller) {
		logger = LoggerFactory.getLogger(caller.getClass());
	}

	public Logger getLogger() {
		return logger;
	}

	@Override
	public boolean isAddRelayConnections() {
		return addRelayConnections;
	}

	@Override
	public String getJsonGraphqlSchemaFilename() {
		return jsonGraphqlSchemaFilename;
	}

	public String getPackageName() {
		return packageName;
	}

	@Override
	public Integer getMaxTokens() {
		return maxTokens;
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
	public File getSchemaFileFolder() {
		return schemaFileFolder;
	}

	@Override
	public String getSchemaFilePattern() {
		return schemaFilePattern;
	}

	@Override
	public boolean isSkipGenerationIfSchemaHasNotChanged() {
		return skipGenerationIfSchemaHasNotChanged;
	}

	@Override
	public String getResourceEncoding() {
		return resourceEncoding;
	}

	@Override
	public File getTargetFolder() {
		return targetFolder;
	}

	@Override
	public String getTargetSchemaFileName() {
		return targetSchemaFileName;
	}

	@Override
	public String getTargetSchemaSubFolder() {
		return targetSchemaSubFolder;
	}

	@Override
	public Map<String, String> getTemplates() {
		return templates;
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
