/**
 * 
 */
package com.graphql_java_generator.mavenplugin;

import java.io.File;
import java.util.Map;

import com.graphql_java_generator.plugin.conf.Logger;
import com.graphql_java_generator.plugin.conf.GenerateGraphQLSchemaConfiguration;

/**
 * @author etienne-sf
 *
 */
public class GenerateGraphQLSchemaConfigurationImpl implements GenerateGraphQLSchemaConfiguration {

	final private GenerateGraphQLSchemaMojo mojo;
	final private MavenLogger log;

	GenerateGraphQLSchemaConfigurationImpl(GenerateGraphQLSchemaMojo mojo) {
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
	public String getTargetSchemaFileName() {
		return mojo.targetSchemaFileName;
	}

	@Override
	public Map<String, String> getTemplates() {
		return this.mojo.templates;
	}

	@Override
	public String getPackageName() {
		// Not used
		return null;
	}

	@Override
	public boolean isAddRelayConnections() {
		return this.mojo.addRelayConnections;
	}
}
