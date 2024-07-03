/**
 * 
 */
package org.allGraphQLCases.server.impl;

import org.springframework.context.annotation.Primary;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.graphql.execution.BatchLoaderRegistry;
import org.springframework.stereotype.Controller;

import graphql.schema.DataFetchingEnvironment;

/**
 * @author gauthiereti
 *
 */
@Controller
@Primary
@SchemaMapping(typeName = "Character")
public class CharacterControllerOverridden extends org.allGraphQLCases.server.CharacterController {

	public CharacterControllerOverridden(BatchLoaderRegistry registry) {
		super(registry);
	}

	@Override
	@SchemaMapping(field = "name")
	public Object name(DataFetchingEnvironment dataFetchingEnvironment,
			org.allGraphQLCases.server.SIP_Character_SIS origin, @Argument("uppercase") java.lang.Boolean uppercase) {
		return super.name(dataFetchingEnvironment, origin, uppercase) + " (overridden)";
	}

}
