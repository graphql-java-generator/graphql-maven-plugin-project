package com.graphql_java_generator.plugin.test.helper;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.graphql_java_generator.plugin.conf.CommonConfiguration;
import com.graphql_java_generator.plugin.conf.GenerateGraphQLSchemaConfiguration;

import lombok.Getter;

/**
 *
 * @author etienne-sf
 */
@Getter
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

}
