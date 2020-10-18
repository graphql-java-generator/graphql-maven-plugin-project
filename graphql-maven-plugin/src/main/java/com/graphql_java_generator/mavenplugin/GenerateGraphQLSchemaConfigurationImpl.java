/**
 * 
 */
package com.graphql_java_generator.mavenplugin;

import java.io.File;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.graphql_java_generator.plugin.conf.GenerateGraphQLSchemaConfiguration;
import com.graphql_java_generator.plugin.conf.Logger;

/**
 * @author etienne-sf
 *
 */
@Component
public class GenerateGraphQLSchemaConfigurationImpl implements GenerateGraphQLSchemaConfiguration {

	@Autowired
	private GenerateGraphQLSchemaMojo mojo;

	private MavenLogger log = null;

	GenerateGraphQLSchemaConfigurationImpl(GenerateGraphQLSchemaMojo mojo) {
		this.mojo = mojo;
		log = new MavenLogger(mojo);
	}

	@Override
	public Logger getLog() {
		if (log == null) {
			log = new MavenLogger(mojo);
		}
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
