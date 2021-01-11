package com.graphql_java_generator.plugin.test.helper;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.graphql_java_generator.plugin.conf.CustomScalarDefinition;
import com.graphql_java_generator.plugin.conf.GenerateServerCodeConfiguration;
import com.graphql_java_generator.plugin.conf.GraphQLConfiguration;
import com.graphql_java_generator.plugin.conf.Logger;
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
	public Logger pluginLogger;

	public boolean addRelayConnections = false;
	public boolean copyRuntimeSources = false; // This will speed build time up (less classes to compile, and allow
	// to load several generated source folders in the IDE.
	public List<CustomScalarDefinition> customScalars = new ArrayList<>();
	public boolean generateDeprecatedRequestResponse = true;
	public boolean generateJPAAnnotation = true;
	public String javaTypeForIDType = GenerateServerCodeConfiguration.DEFAULT_JAVA_TYPE_FOR_ID_TYPE;
	public PluginMode mode = null;
	public String packageName = "org.my.test.package";
	public Packaging packaging = null;
	public String scanBasePackages = "null";
	public File schemaFileFolder = null;
	public String schemaFilePattern = null;
	public File schemaPersonalizationFile = null;
	public boolean separateUtilityClasses = false;
	public String sourceEncoding = "UTF-8";
	public File targetClassFolder = null;
	public File targetResourceFolder = null;
	public File targetSourceFolder = null;
	public Map<String, String> templates = new HashMap<String, String>();

	/**
	 * @param caller
	 *            Used to retrieve the appropriate Log4j logger
	 */
	public GraphQLConfigurationTestHelper(Object caller) {
		pluginLogger = new Slf4jLogger(caller);
	}

}
