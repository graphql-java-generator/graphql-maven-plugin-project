package com.graphql_java_generator.client.domain.allGraphQLCases;

import com.graphql_java_generator.client.directive.Directive;
import com.graphql_java_generator.client.directive.DirectiveLocation;
import com.graphql_java_generator.client.directive.DirectiveRegistry;
import com.graphql_java_generator.client.directive.DirectiveRegistryImpl;
import com.graphql_java_generator.client.request.InputParameter;
import com.graphql_java_generator.customscalars.CustomScalarRegistryImpl;

public class DirectiveRegistryInitializer {
	
	/**
	 * Initialization of the {@link DirectiveRegistry} with all known custom scalars, that is with all custom scalars
	 * defined in the project pom
	 */
	public static DirectiveRegistry initDirectiveRegistry() {
		DirectiveRegistry directiveRegistry = new DirectiveRegistryImpl();
		Directive directive;
		InputParameter param;

		/////////////////////////////////////////////////////////////////////////////////////////////////////
		// Creating Directive skip
		/////////////////////////////////////////////////////////////////////////////////////////////////////
		directive = new Directive();
		directive.setName("skip");
		directive.setPackageName("org.allGraphQLCases.client");
		param = InputParameter.newHardCodedParameter("if", null, true,
					graphql.Scalars.GraphQLBoolean
				);
		directive.getArguments().add(param);
		directive.getDirectiveLocations().add(DirectiveLocation.FIELD);
		directive.getDirectiveLocations().add(DirectiveLocation.FRAGMENT_SPREAD);
		directive.getDirectiveLocations().add(DirectiveLocation.INLINE_FRAGMENT);
		directiveRegistry.registerDirective(directive);

		/////////////////////////////////////////////////////////////////////////////////////////////////////
		// Creating Directive include
		/////////////////////////////////////////////////////////////////////////////////////////////////////
		directive = new Directive();
		directive.setName("include");
		directive.setPackageName("org.allGraphQLCases.client");
		param = InputParameter.newHardCodedParameter("if", null, true,
					graphql.Scalars.GraphQLBoolean
				);
		directive.getArguments().add(param);
		directive.getDirectiveLocations().add(DirectiveLocation.FIELD);
		directive.getDirectiveLocations().add(DirectiveLocation.FRAGMENT_SPREAD);
		directive.getDirectiveLocations().add(DirectiveLocation.INLINE_FRAGMENT);
		directiveRegistry.registerDirective(directive);

		/////////////////////////////////////////////////////////////////////////////////////////////////////
		// Creating Directive defer
		/////////////////////////////////////////////////////////////////////////////////////////////////////
		directive = new Directive();
		directive.setName("defer");
		directive.setPackageName("org.allGraphQLCases.client");
		param = InputParameter.newHardCodedParameter("if", null, true,
					graphql.Scalars.GraphQLBoolean
				);
		directive.getArguments().add(param);
		directive.getDirectiveLocations().add(DirectiveLocation.FIELD);
		directiveRegistry.registerDirective(directive);

		/////////////////////////////////////////////////////////////////////////////////////////////////////
		// Creating Directive deprecated
		/////////////////////////////////////////////////////////////////////////////////////////////////////
		directive = new Directive();
		directive.setName("deprecated");
		directive.setPackageName("org.allGraphQLCases.client");
		param = InputParameter.newHardCodedParameter("reason", null, false,
					graphql.Scalars.GraphQLString
				);
		directive.getArguments().add(param);
		directive.getDirectiveLocations().add(DirectiveLocation.FIELD_DEFINITION);
		directive.getDirectiveLocations().add(DirectiveLocation.ENUM_VALUE);
		directiveRegistry.registerDirective(directive);

		/////////////////////////////////////////////////////////////////////////////////////////////////////
		// Creating Directive testDirective
		/////////////////////////////////////////////////////////////////////////////////////////////////////
		directive = new Directive();
		directive.setName("testDirective");
		directive.setPackageName("org.allGraphQLCases.client");
		param = InputParameter.newHardCodedParameter("value", null, true,
					graphql.Scalars.GraphQLString
				);
		directive.getArguments().add(param);
		param = InputParameter.newHardCodedParameter("anotherValue", null, false,
					graphql.Scalars.GraphQLString
				);
		directive.getArguments().add(param);
		param = InputParameter.newHardCodedParameter("anInt", null, false,
					graphql.Scalars.GraphQLInt
				);
		directive.getArguments().add(param);
		param = InputParameter.newHardCodedParameter("aFloat", null, false,
					graphql.Scalars.GraphQLFloat
				);
		directive.getArguments().add(param);
		param = InputParameter.newHardCodedParameter("aBoolean", null, false,
					graphql.Scalars.GraphQLBoolean
				);
		directive.getArguments().add(param);
		param = InputParameter.newHardCodedParameter("anID", null, false,
					graphql.Scalars.GraphQLID
				);
		directive.getArguments().add(param);
		param = InputParameter.newHardCodedParameter("aCustomScalarDate", null, false,
					CustomScalarRegistryImpl.customScalarRegistry.getGraphQLScalarType("Date")
				);
		directive.getArguments().add(param);
		directive.getDirectiveLocations().add(DirectiveLocation.QUERY);
		directive.getDirectiveLocations().add(DirectiveLocation.MUTATION);
		directive.getDirectiveLocations().add(DirectiveLocation.FIELD);
		directive.getDirectiveLocations().add(DirectiveLocation.FRAGMENT_DEFINITION);
		directive.getDirectiveLocations().add(DirectiveLocation.FRAGMENT_SPREAD);
		directive.getDirectiveLocations().add(DirectiveLocation.INLINE_FRAGMENT);
		directive.getDirectiveLocations().add(DirectiveLocation.SCHEMA);
		directive.getDirectiveLocations().add(DirectiveLocation.SCALAR);
		directive.getDirectiveLocations().add(DirectiveLocation.OBJECT);
		directive.getDirectiveLocations().add(DirectiveLocation.FIELD_DEFINITION);
		directive.getDirectiveLocations().add(DirectiveLocation.ARGUMENT_DEFINITION);
		directive.getDirectiveLocations().add(DirectiveLocation.INTERFACE);
		directive.getDirectiveLocations().add(DirectiveLocation.UNION);
		directive.getDirectiveLocations().add(DirectiveLocation.ENUM);
		directive.getDirectiveLocations().add(DirectiveLocation.ENUM_VALUE);
		directive.getDirectiveLocations().add(DirectiveLocation.INPUT_OBJECT);
		directive.getDirectiveLocations().add(DirectiveLocation.INPUT_FIELD_DEFINITION);
		directiveRegistry.registerDirective(directive);

		/////////////////////////////////////////////////////////////////////////////////////////////////////
		// Creating Directive anotherTestDirective
		/////////////////////////////////////////////////////////////////////////////////////////////////////
		directive = new Directive();
		directive.setName("anotherTestDirective");
		directive.setPackageName("org.allGraphQLCases.client");
		directive.getDirectiveLocations().add(DirectiveLocation.QUERY);
		directive.getDirectiveLocations().add(DirectiveLocation.MUTATION);
		directive.getDirectiveLocations().add(DirectiveLocation.FIELD);
		directive.getDirectiveLocations().add(DirectiveLocation.FRAGMENT_DEFINITION);
		directive.getDirectiveLocations().add(DirectiveLocation.FRAGMENT_SPREAD);
		directive.getDirectiveLocations().add(DirectiveLocation.INLINE_FRAGMENT);
		directive.getDirectiveLocations().add(DirectiveLocation.SCHEMA);
		directive.getDirectiveLocations().add(DirectiveLocation.SCALAR);
		directive.getDirectiveLocations().add(DirectiveLocation.OBJECT);
		directive.getDirectiveLocations().add(DirectiveLocation.FIELD_DEFINITION);
		directive.getDirectiveLocations().add(DirectiveLocation.ARGUMENT_DEFINITION);
		directive.getDirectiveLocations().add(DirectiveLocation.INTERFACE);
		directive.getDirectiveLocations().add(DirectiveLocation.UNION);
		directive.getDirectiveLocations().add(DirectiveLocation.ENUM);
		directive.getDirectiveLocations().add(DirectiveLocation.ENUM_VALUE);
		directive.getDirectiveLocations().add(DirectiveLocation.INPUT_OBJECT);
		directive.getDirectiveLocations().add(DirectiveLocation.INPUT_FIELD_DEFINITION);
		directiveRegistry.registerDirective(directive);


		DirectiveRegistryImpl.directiveRegistry = directiveRegistry;
		return directiveRegistry;
	}

}
