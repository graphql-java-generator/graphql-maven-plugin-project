package com.graphql_java_generator.mavenplugin;

import com.graphql_java_generator.plugin.conf.GeneratePojoConfiguration;

public class AbstractGeneratePojoMojo extends AbstractGraphQLMojo implements GeneratePojoConfiguration {

	protected AbstractGeneratePojoMojo(Class<?> springConfigurationClass) {
		super(springConfigurationClass);
	}

}
