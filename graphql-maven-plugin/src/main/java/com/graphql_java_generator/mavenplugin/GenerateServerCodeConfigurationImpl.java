/**
 * 
 */
package com.graphql_java_generator.mavenplugin;

import java.io.File;
import java.util.List;
import java.util.Map;

import com.graphql_java_generator.plugin.conf.CustomScalarDefinition;
import com.graphql_java_generator.plugin.conf.GenerateServerCodeConfiguration;
import com.graphql_java_generator.plugin.conf.GraphQLConfiguration;
import com.graphql_java_generator.plugin.conf.Logger;
import com.graphql_java_generator.plugin.conf.Packaging;

/**
 * @author etienne-sf
 *
 */
public class GenerateServerCodeConfigurationImpl implements GenerateServerCodeConfiguration {

	final private GenerateServerCodeMojo mojo;
	final private MavenLogger log;

	GenerateServerCodeConfigurationImpl(GenerateServerCodeMojo mojo2) {
		this.mojo = mojo2;
		log = new MavenLogger(mojo2);

		// Let's check that the Packaging is a valid value
		try {
			Packaging.valueOf(mojo2.project.getPackaging());
		} catch (Exception e) {
			throw new RuntimeException("The project packaging is <" + mojo2.project.getPackaging()
					+ ">. This is not accepted by this plugin", e);
		}
	}

	@Override
	public List<CustomScalarDefinition> getCustomScalars() {
		return mojo.customScalars;
	}

	@Override
	public Logger getLog() {
		return log;
	}

	@Override
	public String getPackageName() {
		return mojo.packageName;
	}

	@Override
	public Packaging getPackaging() {
		return Packaging.valueOf(mojo.project.getPackaging());
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
	public File getSchemaPersonalizationFile() {
		return (GraphQLConfiguration.DEFAULT_SCHEMA_PERSONALIZATION_FILE.equals(mojo.schemaPersonalizationFile)) ? null
				: new File(mojo.project.getBasedir(), mojo.schemaPersonalizationFile);
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
	public boolean isGenerateDeprecatedRequestResponse() {
		return mojo.generateDeprecatedRequestResponse;
	}

	@Override
	public boolean isGenerateJPAAnnotation() {
		return mojo.generateJPAAnnotation;
	}

	@Override
	public String getScanBasePackages() {
		return mojo.scanBasePackages;
	}

	@Override
	public Map<String, String> getTemplates() {
		return this.mojo.templates;
	}

	@Override
	public boolean isSeparateUtilityClasses() {
		return this.mojo.separateUtilityClasses;
	}

	@Override
	public boolean isAddRelayConnections() {
		return this.mojo.addRelayConnections;
	}

}
