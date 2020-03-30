/**
 * 
 */
package com.graphql_java_generator.mavenplugin;

import java.io.File;
import java.util.List;

import com.graphql_java_generator.plugin.CustomScalarDefinition;
import com.graphql_java_generator.plugin.Logger;
import com.graphql_java_generator.plugin.Packaging;
import com.graphql_java_generator.plugin.PluginConfiguration;
import com.graphql_java_generator.plugin.PluginMode;

/**
 * @author EtienneSF
 *
 */
public class PluginConfigurationImpl implements PluginConfiguration {

	final private GraphqlMavenPlugin mojo;
	final private MavenLogger log;

	PluginConfigurationImpl(GraphqlMavenPlugin mojo) {
		this.mojo = mojo;
		log = new MavenLogger(mojo);

		// Let's check that the Packaging is a valid value
		try {
			Packaging.valueOf(mojo.project.getPackaging());
		} catch (Exception e) {
			throw new RuntimeException("The project packaging is <" + mojo.project.getPackaging()
					+ ">. This is not accepted by this plugin", e);
		}
	}

	@Override
	public List<CustomScalarDefinition> getCustomScalars() {
		return mojo.customScalars;
	}

	@Override
	public boolean getGenerateJPAAnnotation() {
		return mojo.generateJPAAnnotation;
	}

	@Override
	public Logger getLog() {
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
	public Packaging getPackaging() {
		return Packaging.valueOf(mojo.project.getPackaging());
	}

	@Override
	public File getSchemaFileFolder() {
		return new File(mojo.project.getBasedir(), mojo.schemaFileFolder);
	}

	@Override
	public String getSchemaFilePattern() {
		return mojo.schemaFilePattern;
	}

	@Override
	public File getSchemaPersonalizationFile() {
		return (PluginConfiguration.DEFAULT_SCHEMA_PERSONALIZATION_FILE.equals(mojo.schemaPersonalizationFile)) ? null
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
	public boolean isCopyGraphQLJavaSources() {
		return this.mojo.copyGraphQLJavaSources;
	}

}
