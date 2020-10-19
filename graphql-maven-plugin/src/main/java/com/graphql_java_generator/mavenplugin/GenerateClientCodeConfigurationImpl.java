/**
 * 
 */
package com.graphql_java_generator.mavenplugin;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.graphql_java_generator.plugin.conf.CustomScalarDefinition;
import com.graphql_java_generator.plugin.conf.GenerateClientCodeConfiguration;
import com.graphql_java_generator.plugin.conf.Logger;
import com.graphql_java_generator.plugin.conf.PluginMode;

/**
 * @author etienne-sf
 *
 */
@Component
public class GenerateClientCodeConfigurationImpl implements GenerateClientCodeConfiguration {

	@Autowired
	private GenerateClientCodeMojo mojo;

	private MavenLogger log = null;

	@Override
	public List<CustomScalarDefinition> getCustomScalars() {
		return mojo.customScalars;
	}

	@Override
	public boolean isGenerateDeprecatedRequestResponse() {
		return mojo.generateDeprecatedRequestResponse;
	}

	@Override
	public Logger getLog() {
		if (log == null) {
			log = new MavenLogger(mojo);
		}
		return log;
	}

	@Override
	public PluginMode getMode() {
		return mojo.mode;
	}

	@Override
	public String getPackageName() {
		return mojo.packageName;
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
	public String getSourceEncoding() {
		return mojo.sourceEncoding;
	}

	public File getTargetFolder() {
		return new File(mojo.project.getBasedir(), "target");
	}

	@Override
	public File getTargetClassFolder() {
		return new File(getTargetFolder(), "classes");
	}

	@Override
	public File getTargetSourceFolder() {
		return new File(getTargetFolder(), mojo.targetSourceFolder);
	}

	@Override
	public boolean isCopyRuntimeSources() {
		return this.mojo.copyRuntimeSources;
	}

	@Override
	public boolean isSeparateUtilityClasses() {
		return this.mojo.separateUtilityClasses;
	}

	@Override
	public Map<String, String> getTemplates() {
		return this.mojo.templates;
	}

	@Override
	public boolean isAddRelayConnections() {
		return this.mojo.addRelayConnections;
	}

}
