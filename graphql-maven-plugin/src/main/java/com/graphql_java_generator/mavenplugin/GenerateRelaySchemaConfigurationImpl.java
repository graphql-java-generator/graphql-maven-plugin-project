/**
 * 
 */
package com.graphql_java_generator.mavenplugin;

import java.io.File;
import java.util.Map;

import com.graphql_java_generator.plugin.GenerateRelaySchemaConfiguration;
import com.graphql_java_generator.plugin.Logger;

/**
 * @author etienne-sf
 *
 */
// @Component
public class GenerateRelaySchemaConfigurationImpl implements GenerateRelaySchemaConfiguration {

	final private GenerateRelaySchemaMojo mojo;
	final private MavenLogger log;

	GenerateRelaySchemaConfigurationImpl(GenerateRelaySchemaMojo mojo) {
		this.mojo = mojo;
		log = new MavenLogger(mojo);
	}

	@Override
	public Logger getLog() {
		return log;
	}

	@Override
	public File getSchemaFileFolder() {
		return mojo.schemaFileFolder;
	}

	@Override
	public String getSchemaFileName() {
		return mojo.schemaFileName;
	}

	@Override
	public String getSchemaFilePattern() {
		return mojo.schemaFilePattern;
	}

	@Override
	public String getResourceEncoding() {
		return mojo.resourceEncoding;
	}

	@Override
	public File getTargetFolder() {
		return mojo.targetFolder;
	}

	@Override
	public Map<String, String> getTemplates() {
		return this.mojo.templates;
	}
}
