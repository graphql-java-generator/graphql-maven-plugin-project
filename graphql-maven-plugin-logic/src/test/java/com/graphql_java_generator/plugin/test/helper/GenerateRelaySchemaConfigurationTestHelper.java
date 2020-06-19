package com.graphql_java_generator.plugin.test.helper;

import java.io.File;

import org.springframework.stereotype.Component;

import com.graphql_java_generator.plugin.GenerateRelaySchemaConfiguration;
import com.graphql_java_generator.plugin.Logger;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author etienne-sf
 */
@Component
@Getter
@Setter
public class GenerateRelaySchemaConfigurationTestHelper implements GenerateRelaySchemaConfiguration {

	// All getters are generated thanks to Lombok, see the '@Getter' class annotation
	final Logger log;

	public File schemaFileFolder = null;
	public String schemaFilePattern = null;
	public String resourceEncoding = null;
	public File targetFolder = null;

	/**
	 * @param caller
	 *            Used to retrieve the appropriate Log4j logger
	 */
	public GenerateRelaySchemaConfigurationTestHelper(Object caller) {
		log = new Slf4jLogger(caller);
	}

}
