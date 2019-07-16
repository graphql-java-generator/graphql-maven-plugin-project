/**
 * 
 */
package graphql.mavenplugin;

import java.io.File;

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
	public File getResourcesFolder() {
		return new File(mojo.project.getBasedir(), "/src/main/resources");
	}

	@Override
	public String getSchemaFilePattern() {
		return mojo.schemaFilePattern;
	}

	@Override
	public File getSchemaPersonalizationFile() {
		return mojo.schemaPersonalizationFile;
	}

	@Override
	public String getSourceEncoding() {
		return mojo.sourceEncoding;
	}

	@Override
	public File getTargetClassFolder() {
		return new File(mojo.project.getBasedir(), "target/classes");
	}

	@Override
	public File getTargetSourceFolder() {
		return mojo.targetSourceFolder;
	}

}
