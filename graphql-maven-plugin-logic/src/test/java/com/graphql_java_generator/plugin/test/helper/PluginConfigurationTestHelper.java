package com.graphql_java_generator.plugin.test.helper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.graphql_java_generator.plugin.CustomScalarDefinition;
import com.graphql_java_generator.plugin.Logger;
import com.graphql_java_generator.plugin.Packaging;
import com.graphql_java_generator.plugin.PluginConfiguration;
import com.graphql_java_generator.plugin.PluginMode;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author EtienneSF
 */
@Getter
@Setter
public class PluginConfigurationTestHelper implements PluginConfiguration {

	// All getters are generated thanks to Lombok, see the '@Getter' class annotation
	final Logger log;

	public List<CustomScalarDefinition> customScalars = new ArrayList<>();
	public PluginMode mode = null;
	public String packageName = null;
	public Packaging packaging = null;
	public File schemaFileFolder = null;
	public String schemaFilePattern = null;
	public File schemaPersonalizationFile = null;
	public String sourceEncoding = null;
	public File targetClassFolder = null;
	public File targetSourceFolder = null;
	public boolean copyGraphQLJavaSources = true;

	/**
	 * @param caller
	 *            Used to retrieve the appropriate Log4j logger
	 */
	public PluginConfigurationTestHelper(Object caller) {
		log = new Slf4jLogger(caller);
	}

	@Override
	public boolean getGenerateJPAAnnotation() {
		return true;
	}

	@Override
	public boolean isCopyGraphQLJavaSources() {
		return copyGraphQLJavaSources;
	}

}
