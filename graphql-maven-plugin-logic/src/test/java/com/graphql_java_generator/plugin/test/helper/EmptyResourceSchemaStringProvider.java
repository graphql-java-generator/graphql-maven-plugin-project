/**
 * 
 */
package com.graphql_java_generator.plugin.test.helper;

import java.util.ArrayList;
import java.util.List;

import com.graphql_java_generator.plugin.ResourceSchemaStringProvider;

/**
 * @author etienne-sf
 *
 */
public class EmptyResourceSchemaStringProvider extends ResourceSchemaStringProvider {

	@Override
	public List<String> schemaStrings() {
		return new ArrayList<String>();
	}

}
