/**
 * 
 */
package com.graphql_java_generator.plugin.language.impl;

import com.graphql_java_generator.plugin.conf.CommonConfiguration;
import com.graphql_java_generator.plugin.conf.GraphQLConfiguration;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author etienne-sf
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ScalarExtensionType extends ScalarType {

	/**
	 * 
	 * @param name
	 *            The name of the GraphQL type
	 * @param packageName
	 *            The package of the java class that will hold this value
	 * @param classSimpleName
	 *            The simple name (without the package) of the java class that will hold this value
	 * @param configuration
	 *            The current {@link GraphQLConfiguration}
	 */
	public ScalarExtensionType(String name, String packageName, String classSimpleName,
			CommonConfiguration configuration) {
		super(name, packageName, classSimpleName, configuration);
	}

}
