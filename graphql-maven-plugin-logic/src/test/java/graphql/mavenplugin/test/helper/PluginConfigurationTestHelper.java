package graphql.mavenplugin.test.helper;

import java.io.File;

import graphql.mavenplugin.Logger;
import graphql.mavenplugin.Packaging;
import graphql.mavenplugin.PluginConfiguration;
import graphql.mavenplugin.PluginMode;
import lombok.Getter;

/**
 * 
 * @author EtienneSF
 */
@Getter
public class PluginConfigurationTestHelper implements PluginConfiguration {

	// All getters are generated thanks to Lombok, see the '@Getter' class annotation
	final Logger log;
	public PluginMode mode = null;
	public String packageName = null;
	public Packaging packaging = null;
	public File resourcesFolder = null;
	public String schemaFilePattern = null;
	public File schemaPersonalizationFile = null;
	public String sourceEncoding = null;
	public File targetClassFolder = null;
	public File targetSourceFolder = null;

	/**
	 * @param caller
	 *            Used to retrieve the appropriate Log4j logger
	 */
	public PluginConfigurationTestHelper(Object caller) {
		log = new Log4jLogger(caller);
	}
}
