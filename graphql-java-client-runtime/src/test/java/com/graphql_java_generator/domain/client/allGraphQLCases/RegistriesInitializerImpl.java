package com.graphql_java_generator.domain.client.allGraphQLCases;

import com.graphql_java_generator.client.CustomScalarRegistry;
import com.graphql_java_generator.client.CustomScalarRegistryImpl;
import com.graphql_java_generator.client.RegistriesInitializer;
import com.graphql_java_generator.client.directive.Directive;
import com.graphql_java_generator.client.directive.DirectiveLocation;
import com.graphql_java_generator.client.directive.DirectiveRegistry;
import com.graphql_java_generator.client.directive.DirectiveRegistryImpl;
import com.graphql_java_generator.client.request.InputParameter;
import com.graphql_java_generator.customscalars.GraphQLScalarTypeIDClient;

import graphql.scalars.ExtendedScalars;

public class RegistriesInitializerImpl implements RegistriesInitializer {
	public final static RegistriesInitializer registriesInitializer = new RegistriesInitializerImpl();

	private RegistriesInitializerImpl() {
		initCustomScalarRegistry();
		initDirectiveRegistry();
		GraphQLTypeMappingImpl.initGraphQLTypeMappingRegistry();
	}

	/**
	 * Initialization of the {@link DirectiveRegistry} with all directives defined in the current schema
	 */
	private void initDirectiveRegistry() {
		DirectiveRegistryImpl.registerDirectiveRegistry(getSchema(), (directiveRegistry) -> {
			Directive directive;

			/////////////////////////////////////////////////////////////////////////////////////
			// Creating Directive skip
			/////////////////////////////////////////////////////////////////////////////////////
			directive = new Directive(getSchema());
			directive.setName("skip");
			directive.setPackageName("com.graphql_java_generator.domain.client.allGraphQLCases");
			directive.getArguments()
					.add(InputParameter.newHardCodedParameter(getSchema(), "if", null, "Boolean", true, 0, false));
			directive.getDirectiveLocations().add(DirectiveLocation.FIELD);
			directive.getDirectiveLocations().add(DirectiveLocation.FRAGMENT_SPREAD);
			directive.getDirectiveLocations().add(DirectiveLocation.INLINE_FRAGMENT);
			directiveRegistry.registerDirective(directive);

			/////////////////////////////////////////////////////////////////////////////////////
			// Creating Directive include
			/////////////////////////////////////////////////////////////////////////////////////
			directive = new Directive(getSchema());
			directive.setName("include");
			directive.setPackageName("com.graphql_java_generator.domain.client.allGraphQLCases");
			directive.getArguments()
					.add(InputParameter.newHardCodedParameter(getSchema(), "if", null, "Boolean", true, 0, false));
			directive.getDirectiveLocations().add(DirectiveLocation.FIELD);
			directive.getDirectiveLocations().add(DirectiveLocation.FRAGMENT_SPREAD);
			directive.getDirectiveLocations().add(DirectiveLocation.INLINE_FRAGMENT);
			directiveRegistry.registerDirective(directive);

			/////////////////////////////////////////////////////////////////////////////////////
			// Creating Directive defer
			/////////////////////////////////////////////////////////////////////////////////////
			directive = new Directive(getSchema());
			directive.setName("defer");
			directive.setPackageName("com.graphql_java_generator.domain.client.allGraphQLCases");
			directive.getArguments()
					.add(InputParameter.newHardCodedParameter(getSchema(), "if", null, "Boolean", true, 0, false));
			directive.getDirectiveLocations().add(DirectiveLocation.FIELD);
			directiveRegistry.registerDirective(directive);

			/////////////////////////////////////////////////////////////////////////////////////
			// Creating Directive deprecated
			/////////////////////////////////////////////////////////////////////////////////////
			directive = new Directive(getSchema());
			directive.setName("deprecated");
			directive.setPackageName("com.graphql_java_generator.domain.client.allGraphQLCases");
			directive.getArguments()
					.add(InputParameter.newHardCodedParameter(getSchema(), "reason", null, "String", false, 0, false));
			directive.getDirectiveLocations().add(DirectiveLocation.FIELD_DEFINITION);
			directive.getDirectiveLocations().add(DirectiveLocation.ENUM_VALUE);
			directiveRegistry.registerDirective(directive);

			/////////////////////////////////////////////////////////////////////////////////////
			// Creating Directive IDScalarDirective
			/////////////////////////////////////////////////////////////////////////////////////
			directive = new Directive(getSchema());
			directive.setName("IDScalarDirective");
			directive.setPackageName("com.graphql_java_generator.domain.client.allGraphQLCases");
			directive.getDirectiveLocations().add(DirectiveLocation.SCALAR);
			directiveRegistry.registerDirective(directive);

			/////////////////////////////////////////////////////////////////////////////////////
			// Creating Directive RelayConnection
			/////////////////////////////////////////////////////////////////////////////////////
			directive = new Directive(getSchema());
			directive.setName("RelayConnection");
			directive.setPackageName("com.graphql_java_generator.domain.client.allGraphQLCases");
			directive.getDirectiveLocations().add(DirectiveLocation.FIELD_DEFINITION);
			directiveRegistry.registerDirective(directive);

			/////////////////////////////////////////////////////////////////////////////////////
			// Creating Directive generateDataLoaderForLists
			/////////////////////////////////////////////////////////////////////////////////////
			directive = new Directive(getSchema());
			directive.setName("generateDataLoaderForLists");
			directive.setPackageName("com.graphql_java_generator.domain.client.allGraphQLCases");
			directive.getDirectiveLocations().add(DirectiveLocation.FIELD_DEFINITION);
			directiveRegistry.registerDirective(directive);

			/////////////////////////////////////////////////////////////////////////////////////
			// Creating Directive testExtendKeyword
			/////////////////////////////////////////////////////////////////////////////////////
			directive = new Directive(getSchema());
			directive.setName("testExtendKeyword");
			directive.setPackageName("com.graphql_java_generator.domain.client.allGraphQLCases");
			directive.getDirectiveLocations().add(DirectiveLocation.QUERY);
			directive.getDirectiveLocations().add(DirectiveLocation.MUTATION);
			directive.getDirectiveLocations().add(DirectiveLocation.SUBSCRIPTION);
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

			/////////////////////////////////////////////////////////////////////////////////////
			// Creating Directive testDirective
			/////////////////////////////////////////////////////////////////////////////////////
			directive = new Directive(getSchema());
			directive.setName("testDirective");
			directive.setPackageName("com.graphql_java_generator.domain.client.allGraphQLCases");
			directive.getArguments()
					.add(InputParameter.newHardCodedParameter(getSchema(), "value", null, "String", true, 0, false));
			directive.getArguments().add(
					InputParameter.newHardCodedParameter(getSchema(), "anotherValue", null, "String", false, 0, false));
			directive.getArguments()
					.add(InputParameter.newHardCodedParameter(getSchema(), "anArray", null, "String", false, 1, true));
			directive.getArguments().add(InputParameter.newHardCodedParameter(getSchema(), "anObject", null,
					"CharacterInput", false, 0, false));
			directive.getArguments()
					.add(InputParameter.newHardCodedParameter(getSchema(), "anInt", null, "Int", false, 0, false));
			directive.getArguments()
					.add(InputParameter.newHardCodedParameter(getSchema(), "aFloat", null, "Float", false, 0, false));
			directive.getArguments().add(
					InputParameter.newHardCodedParameter(getSchema(), "aBoolean", null, "Boolean", false, 0, false));
			directive.getArguments()
					.add(InputParameter.newHardCodedParameter(getSchema(), "anID", null, "ID", false, 0, false));
			directive.getArguments().add(InputParameter.newHardCodedParameter(getSchema(), "aCustomScalarDate", null,
					"Date", false, 0, false));
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

			/////////////////////////////////////////////////////////////////////////////////////
			// Creating Directive anotherTestDirective
			/////////////////////////////////////////////////////////////////////////////////////
			directive = new Directive(getSchema());
			directive.setName("anotherTestDirective");
			directive.setPackageName("com.graphql_java_generator.domain.client.allGraphQLCases");
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
		});
	}

	/**
	 * Initialization of the {@link CustomScalarRegistry} with all known custom scalars, that is with all custom scalars
	 * defined in the project pom
	 */
	private void initCustomScalarRegistry() {
		CustomScalarRegistryImpl.registerCustomScalarRegistry(getSchema(), (customScalarRegistry) -> {
			// Registering the ID parser, for client mode
			customScalarRegistry.registerGraphQLScalarType("ID", GraphQLScalarTypeIDClient.ID, String.class);

			customScalarRegistry.registerGraphQLScalarType("Date",
					com.graphql_java_generator.customscalars.GraphQLScalarTypeDate.Date, java.util.Date.class);
			customScalarRegistry.registerGraphQLScalarType("Long", ExtendedScalars.GraphQLLong, java.lang.Long.class);
			customScalarRegistry.registerGraphQLScalarType("Base64String",
					com.graphql_java_generator.customscalars.GraphQLScalarTypeBase64String.GraphQLBase64String,
					byte[].class);
			customScalarRegistry.registerGraphQLScalarType("Object", graphql.scalars.ExtendedScalars.Object,
					java.lang.Object.class);
			customScalarRegistry.registerGraphQLScalarType("JSON", graphql.scalars.ExtendedScalars.Json,
					tools.jackson.databind.node.ObjectNode.class);
			customScalarRegistry.registerGraphQLScalarType("String",
					com.graphql_java_generator.customscalars.GraphQLScalarTypeString.String, java.lang.String.class);
			customScalarRegistry.registerGraphQLScalarType("NonNegativeInt",
					graphql.scalars.ExtendedScalars.NonNegativeInt, java.lang.Integer.class);
		});
	}

	@Override
	public String getPackageName() {
		return "com.graphql_java_generator.domain.client.allGraphQLCases";
	}

	@Override
	public String getSchema() {
		return "AllGraphQLCases";
	}

	@Override
	public Class<?> getQueryRootResponseClass() {
		return MyQueryTypeRootResponse.class;
	}

	@Override
	public Class<?> getMutationRootResponseClass() {
		return AnotherMutationTypeRootResponse.class;
	}

	@Override
	public Class<?> getSubscriptionRootResponseClass() {
		return TheSubscriptionTypeRootResponse.class;
	}

	// @Override
	// protected Class<? extends GraphQLRequestObject> getSubscriptionClass() {
	// return Subscription.class;
	// }
}
