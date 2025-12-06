/**
 * 
 */
package org.allGraphQLCases.server.impl;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import graphql.schema.DataFetchingEnvironment;

/**
 * @author etienne-sf
 *
 */
@Controller
//@Primary //useless, as there is not Autowired of CharacterController in the code, but for the consistency with
//OverriddenController
@SchemaMapping(typeName = "Character")
public class CharacterControllerOverridden {

	@SchemaMapping(field = "name")
	public Object name(DataFetchingEnvironment dataFetchingEnvironment,
			org.allGraphQLCases.server.SIP_Character_SIS origin, @Argument("uppercase") java.lang.Boolean uppercase) {
		return ((uppercase != null && origin.getName() != null && uppercase) ? origin.getName().toUpperCase()
				: origin.getName()) + " (overridden)";
	}

}
