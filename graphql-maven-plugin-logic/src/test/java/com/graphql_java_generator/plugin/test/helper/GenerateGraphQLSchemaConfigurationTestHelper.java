package com.graphql_java_generator.plugin.test.helper;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.graphql_java_generator.plugin.conf.Logger;
import com.graphql_java_generator.plugin.conf.GenerateGraphQLSchemaConfiguration;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author etienne-sf
 */
@Getter
@Setter
public class GenerateGraphQLSchemaConfigurationTestHelper implements GenerateGraphQLSchemaConfiguration {

	// All getters are generated thanks to Lombok, see the '@Getter' class annotation
	public Logger log;

	public boolean addRelayConnections = false;
	public String packageName = "my.test.package";
	public File schemaFileFolder = null;
	public String schemaFilePattern = null;
	public String resourceEncoding = null;
	public File targetFolder = null;
	public String targetSchemaFileName = null;
	public Map<String, String> templates = new HashMap<String, String>();

	/**
	 * @param caller
	 *            Used to retrieve the appropriate Log4j logger
	 */
	public GenerateGraphQLSchemaConfigurationTestHelper(Object caller) {
		log = new Slf4jLogger(caller);
	}

}
