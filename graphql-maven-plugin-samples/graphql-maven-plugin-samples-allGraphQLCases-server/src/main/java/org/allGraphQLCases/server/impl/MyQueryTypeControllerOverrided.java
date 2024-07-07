package org.allGraphQLCases.server.impl;

import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import graphql.schema.DataFetchingEnvironment;

/**
 * This class is used to check that the overriding of a Spring Controller actually works at runtime, so it must be
 * checked on client side. To do this, one of the returned strings is changed.
 */
@Controller
@SchemaMapping(typeName = "MyQueryType")
public class MyQueryTypeControllerOverrided {

	@SchemaMapping(field = "checkOverriddenController")
	public String checkOverriddenController(DataFetchingEnvironment dataFetchingEnvironment) {
		return "Welcome from the overridden controller";
	}

}
