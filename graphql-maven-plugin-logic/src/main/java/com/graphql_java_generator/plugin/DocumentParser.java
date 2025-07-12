/**
 * 
 */
package com.graphql_java_generator.plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.graphql_java_generator.plugin.conf.CommonConfiguration;
import com.graphql_java_generator.plugin.conf.CustomScalarDefinition;
import com.graphql_java_generator.plugin.conf.GenerateCodeCommonConfiguration;
import com.graphql_java_generator.plugin.conf.GraphQLConfiguration;
import com.graphql_java_generator.plugin.generate_schema.GenerateGraphQLSchemaDocumentParser;
import com.graphql_java_generator.plugin.language.AppliedDirective;
import com.graphql_java_generator.plugin.language.Description;
import com.graphql_java_generator.plugin.language.Directive;
import com.graphql_java_generator.plugin.language.DirectiveLocation;
import com.graphql_java_generator.plugin.language.EnumValue;
import com.graphql_java_generator.plugin.language.Field;
import com.graphql_java_generator.plugin.language.FieldTypeAST;
import com.graphql_java_generator.plugin.language.Type;
import com.graphql_java_generator.plugin.language.impl.AppliedDirectiveImpl;
import com.graphql_java_generator.plugin.language.impl.CustomScalarType;
import com.graphql_java_generator.plugin.language.impl.DirectiveImpl;
import com.graphql_java_generator.plugin.language.impl.EnumType;
import com.graphql_java_generator.plugin.language.impl.EnumValueImpl;
import com.graphql_java_generator.plugin.language.impl.FieldImpl;
import com.graphql_java_generator.plugin.language.impl.InterfaceType;
import com.graphql_java_generator.plugin.language.impl.ObjectType;
import com.graphql_java_generator.plugin.language.impl.ScalarType;
import com.graphql_java_generator.plugin.language.impl.UnionType;
import com.graphql_java_generator.util.GraphqlUtils;

import graphql.introspection.IntrospectionResultToSchema;
import graphql.language.AbstractDescribedNode;
import graphql.language.AbstractNode;
import graphql.language.Argument;
import graphql.language.DirectiveDefinition;
import graphql.language.Document;
import graphql.language.EnumTypeDefinition;
import graphql.language.EnumTypeExtensionDefinition;
import graphql.language.EnumValueDefinition;
import graphql.language.FieldDefinition;
import graphql.language.InputObjectTypeDefinition;
import graphql.language.InputObjectTypeExtensionDefinition;
import graphql.language.InputValueDefinition;
import graphql.language.InterfaceTypeDefinition;
import graphql.language.InterfaceTypeExtensionDefinition;
import graphql.language.ListType;
import graphql.language.NonNullType;
import graphql.language.ObjectTypeDefinition;
import graphql.language.ObjectTypeExtensionDefinition;
import graphql.language.OperationTypeDefinition;
import graphql.language.ScalarTypeDefinition;
import graphql.language.ScalarTypeExtensionDefinition;
import graphql.language.SchemaDefinition;
import graphql.language.SchemaExtensionDefinition;
import graphql.language.StringValue;
import graphql.language.TypeDefinition;
import graphql.language.TypeName;
import graphql.language.UnionTypeDefinition;
import graphql.language.UnionTypeExtensionDefinition;
import graphql.parser.Parser;
import graphql.parser.ParserOptions;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import lombok.Getter;
import lombok.Setter;

/**
 * This class generates the Java classes, from the documents. These documents are read from the
 * graphql-spring-boot-starter code, in injected here thanks to spring's magic.<BR/>
 * There is no validity check: we trust the information in the Document, as it is read by the GraphQL {@link Parser}.
 * <BR/>
 * The graphQL-java library maps both FieldDefinition and InputValueDefinition in very similar structures, which are
 * actually trees. These structures are too hard too read in a Velocity template, and we need to parse down to a
 * properly structures way for that.
 * 
 * @author etienne-sf
 */
@Getter
public abstract class DocumentParser implements InitializingBean {

	private static final Logger logger = LoggerFactory.getLogger(DocumentParser.class);

	protected final String DEFAULT_QUERY_NAME = "Query";
	protected final String DEFAULT_MUTATION_NAME = "Mutation";
	protected final String DEFAULT_SUBSCRIPTION_NAME = "Subscription";

	/**
	 * This instance is responsible for providing all the configuration parameters from the project configuration
	 * (Maven, Gradle...)
	 */
	@Autowired
	protected CommonConfiguration configuration;

	/**
	 * A utility that adds Relay Connection capabilities to the read schema. It is called if the
	 * {@link CommonConfiguration#isAddRelayConnections()} is true
	 */
	@Autowired
	protected AddRelayConnections addRelayConnections;

	/** Various utilities, grouped in a dedicated class */
	@Autowired
	protected GraphqlUtils graphqlUtils;

	/////////////////////////////////////////////////////////////////////////////////////////////
	// Internal attributes for this class

	/**
	 * This Spring Bean is responsible for finding and loading all the GraphQL schema files, based on the project
	 * configuration.
	 */
	@Autowired
	protected ResourceSchemaStringProvider schemaStringProvider;

	/** The result of the GraphQL schema parsing */
	TypeDefinitionRegistry typeDefinitionRegistry;

	/** List of all the directives that have been read in the GraphQL schema */
	@Getter
	@Setter
	protected List<Directive> directives = new ArrayList<>();

	/** List of the directives that have declared for the <code>schema</code> in the GraphQL schema */
	@Getter
	@Setter
	protected List<AppliedDirective> schemaDirectives = new ArrayList<>();

	/**
	 * The name of the type that implements the query operation. The default name is "Query". It is overridden in the
	 * {@link #readSchemaDefinition()} method, if defined in the provided GraphQL schema.
	 */
	@Getter
	protected String queryTypeName = this.DEFAULT_QUERY_NAME;
	/** The Query root operation for this Document */
	@Getter
	@Setter
	protected ObjectType queryType = null;

	/**
	 * The name of the type that implements the mutation operation. The default name is "Mutation". It is overridden in
	 * the {@link #readSchemaDefinition()} method, if defined in the provided GraphQL schema.
	 */
	@Getter
	protected String mutationTypeName = this.DEFAULT_MUTATION_NAME;
	/**
	 * The Mutation root operation for this Document, if defined (that is: if this schema implements one or more
	 * mutations)
	 */
	@Getter
	@Setter
	protected ObjectType mutationType = null;

	/**
	 * The name of the type that implements the subscription operation. The default name is "Subscription". It is
	 * overridden in the {@link #readSchemaDefinition()} method, if defined in the provided GraphQL schema.
	 */
	@Getter
	protected String subscriptionTypeName = this.DEFAULT_SUBSCRIPTION_NAME;
	/**
	 * The Subscription root operation for this Document, if defined (that is: if this schema implements one or more
	 * subscriptions)
	 */
	@Getter
	@Setter
	protected ObjectType subscriptionType = null;

	/**
	 * All the {@link ObjectType} which have been read during the reading of the documents
	 */
	@Getter
	@Setter
	List<ObjectType> objectTypes = new ArrayList<>();

	/**
	 * All the {@link InterfaceTypeDefinition} which have been read during the reading of the documents
	 */
	@Getter
	@Setter
	protected List<InterfaceType> interfaceTypes = new ArrayList<>();

	/**
	 * All the {@link UnionTypeDefinition} which have been read during the reading of the documents
	 */
	@Getter
	@Setter
	protected List<UnionType> unionTypes = new ArrayList<>();

	/** All the {@link ObjectType} which have been read during the reading of the documents */
	@Getter
	@Setter
	protected List<EnumType> enumTypes = new ArrayList<>();

	/**
	 * maps for all scalers, when they are mandatory. The key is the type name. The value is the class to use in the
	 * java code
	 */
	@Setter
	protected List<ScalarType> scalarTypes = new ArrayList<>();

	/** All the {@link CustomScalarType} which have been read during the reading of the documents */
	protected List<CustomScalarType> customScalars = new ArrayList<>();

	/**
	 * All the {@link Type}s that have been parsed, added by the default scalars. So it contains the query, the mutation
	 * (if defined), the subscription (if defined), the types, the input types, all the scalars (including the default
	 * ones), the interfaces, the unions and the enums.<br/>
	 * The key is the type's name. The value is the {@link Type}.
	 */
	@Getter
	@Setter
	protected Map<String, com.graphql_java_generator.plugin.language.Type> types = new HashMap<>();

	@Override
	public void afterPropertiesSet() {

		logger.debug("Starting DocumentParser's PostConstruct intialization");

		//////////////////////////////////////////////////////////////////////////////////////////
		// Add of all GraphQL scalars: standard and customs depending on the use case
		initScalarTypes(UUID.class);

		//////////////////////////////////////////////////////////////////////////////////////////
		// Add of all GraphQL standard directives
		//
		// @skip
		DirectiveImpl skip = new DirectiveImpl();
		skip.setName("skip");
		skip.getArguments()
				.add(FieldImpl.builder().name("if")
						.fieldTypeAST(FieldTypeAST.builder().graphQLTypeSimpleName("Boolean").mandatory(true).build())//
						.build());
		skip.getDirectiveLocations().add(DirectiveLocation.FIELD);
		skip.getDirectiveLocations().add(DirectiveLocation.FRAGMENT_SPREAD);
		skip.getDirectiveLocations().add(DirectiveLocation.INLINE_FRAGMENT);
		skip.setStandard(true);
		this.directives.add(skip);
		//
		// @include
		DirectiveImpl include = new DirectiveImpl();
		include.setName("include");
		include.getArguments().add(FieldImpl//
				.builder()//
				.name("if")
				.fieldTypeAST(FieldTypeAST.builder().graphQLTypeSimpleName("Boolean").mandatory(true).build())//
				.build());
		include.getDirectiveLocations().add(DirectiveLocation.FIELD);
		include.getDirectiveLocations().add(DirectiveLocation.FRAGMENT_SPREAD);
		include.getDirectiveLocations().add(DirectiveLocation.INLINE_FRAGMENT);
		include.setStandard(true);
		this.directives.add(include);
		//
		// @defer
		DirectiveImpl defer = new DirectiveImpl();
		defer.setName("defer");
		defer.getArguments().add(FieldImpl//
				.builder()//
				.name("if")
				.fieldTypeAST(FieldTypeAST.builder().graphQLTypeSimpleName("Boolean").mandatory(true).build())//
				.build());
		defer.getDirectiveLocations().add(DirectiveLocation.FIELD);
		defer.setStandard(true);
		this.directives.add(defer);
		//
		// @deprecated
		DirectiveImpl deprecated = new DirectiveImpl();
		deprecated.setName("deprecated");
		deprecated.getArguments().add(FieldImpl//
				.builder()//
				.name("reason").fieldTypeAST(FieldTypeAST.builder().graphQLTypeSimpleName("String").build())
				.defaultValue(new StringValue("No longer supported"))//
				.build());
		deprecated.getDirectiveLocations().add(DirectiveLocation.FIELD_DEFINITION);
		deprecated.getDirectiveLocations().add(DirectiveLocation.ENUM_VALUE);
		deprecated.setStandard(true);
		this.directives.add(deprecated);

		//
		// @specifiedBy
		DirectiveImpl specifiedBy = new DirectiveImpl();
		specifiedBy.setName("specifiedBy");
		specifiedBy.getArguments().add(FieldImpl//
				.builder()//
				.name("url")//
				.fieldTypeAST(FieldTypeAST.builder().graphQLTypeSimpleName("String").build())//
				.build());
		specifiedBy.getDirectiveLocations().add(DirectiveLocation.FIELD_DEFINITION);
		specifiedBy.getDirectiveLocations().add(DirectiveLocation.ENUM_VALUE);
		specifiedBy.setStandard(true);
		this.directives.add(specifiedBy);

		logger.debug("Finished DocumentParser's PostConstruct intialization");

	}

	/**
	 * This method initializes the {@link #scalarTypes} list. This list depends on the use case
	 * 
	 * @param IDclass
	 * 
	 */
	protected void initScalarTypes(Class<?> IDclass) {
		this.scalarTypes.add(new ScalarType("Boolean", "java.lang", "Boolean", this.configuration, this));
		// GraphQL Float is a double precision number
		this.scalarTypes.add(new ScalarType("Float", "java.lang", "Double", this.configuration, this));
		// By default, we use the UUID type for the ID GraphQL type
		this.scalarTypes.add(new ScalarType("ID", IDclass.getPackage().getName(), IDclass.getSimpleName(),
				this.configuration, this));
		this.scalarTypes.add(new ScalarType("Int", "java.lang", "Integer", this.configuration, this));
		this.scalarTypes.add(new ScalarType("String", "java.lang", "String", this.configuration, this));
	}

	/**
	 * The main method of the class: it graphqlUtils.executes the generation of the GraphQL schemas, as defined in the
	 * project configuration
	 * 
	 * @return
	 * @throws IOException
	 *             When an error occurs, during the parsing of the GraphQL schemas
	 */
	public int parseGraphQLSchemas() throws IOException {
		logger.debug("Starting documents parsing");

		// Configuration of the GraphQL schema parser, from the project configuration
		ParserOptions newDefault = ParserOptions.newParserOptions().maxTokens(this.configuration.getMaxTokens())
				.build();
		ParserOptions.setDefaultParserOptions(newDefault);
		SchemaParser schemaParser = new SchemaParser();

		// Let's parse the provided GraphQL schema(s)
		if (this.configuration.getJsonGraphqlSchemaFilename() != null
				&& !"".equals(this.configuration.getJsonGraphqlSchemaFilename())) {
			// Let's load the GraphQL schema from a json file, that is the result of an introspection query
			Map<String, Object> map = loadJsonGraphqlSchemaFile();
			Document document = new IntrospectionResultToSchema().createSchemaDefinition(map);
			this.typeDefinitionRegistry = schemaParser.buildRegistry(document);

			// Let's load and merge the Introspection schema with the above one, as this is need for the plugin.
			try (InputStream is = this.schemaStringProvider.getIntrospectionSchema().getInputStream()) {
				this.typeDefinitionRegistry.merge(schemaParser.parse(is));
			}
		} else {
			// Let's load the GraphQL schema from regular GraphQL schema file(s)
			// Note: getConcatenatedSchemaStrings returns a GraphQL schema that contains the Introspection schema
			String concatenatedSchemas = this.schemaStringProvider.getConcatenatedSchemaStrings();
			this.typeDefinitionRegistry = schemaParser.parse(concatenatedSchemas);
		}

		// The Directives must be read first, as they may be found on almost any kind of definition in the GraphQL
		// schema
		this.typeDefinitionRegistry.getDirectiveDefinitions().values().stream()
				.forEach(def -> this.directives.add(readDirectiveDefinition(def)));

		// Then a look at the schema definition, to list the defined queries, mutations and subscriptions (should be
		// only one of each), but we're ready for more. (for instance if several schema files have been merged)
		logger.debug("Reading schema definition");
		readSchemaDefinition();

		// Scalar definitions are not returned by the typeDefinitionRegistry.types() method. So we need a specific loop
		for (ScalarTypeDefinition def : this.typeDefinitionRegistry.scalars().values()) {
			// The scalars() method returns all scalars, whether they are custom or not. But we need here to add only
			// custom scalars (mainly to be able to properly re-generate the schema afterwards)
			boolean isCustom = true;
			for (ScalarType s : this.scalarTypes) {
				if (s.getName().equals(def.getName())) {
					isCustom = false;
					break;
				}
			}
			if (isCustom) {
				this.customScalars.add(readCustomScalarType(def));
			}
		}

		logger.debug("Reading type definitions");
		for (TypeDefinition<?> def : this.typeDefinitionRegistry.types().values()) {
			// directive
			if ((Object) def instanceof DirectiveDefinition) {
				// This test is awful, but without the (Object) there is a compilation error. And I want to be sure that
				// these tests resist to a change in the graphql-java hierarchy change (like it already happened), so I
				// want to keep this test here

				// Directives have already been read
			} else
			// enum
			if (def instanceof EnumTypeDefinition) {
				this.enumTypes.add(readEnumType(//
						new EnumType(((EnumTypeDefinition) def).getName(), this.configuration, this),
						(EnumTypeDefinition) def));
			} else
			// input object
			if (def instanceof InputObjectTypeDefinition) {
				this.objectTypes.add(readInputType(//
						new ObjectType(((InputObjectTypeDefinition) def).getName(), this.configuration, this),
						(InputObjectTypeDefinition) def));
			} else
			// interface
			if (def instanceof InterfaceTypeDefinition) {
				this.interfaceTypes.add(readInterfaceType((InterfaceTypeDefinition) def));
			} else
			// object
			if (def instanceof ObjectTypeDefinition) {
				// Let's check what kind of ObjectDefinition we have
				ObjectType o = readObjectTypeDefinition((ObjectTypeDefinition) def);
				this.objectTypes.add(o);

				// Let's register this type as a request, if it is the case
				if (o.getName().equals(this.queryTypeName)) {
					this.queryType = o;
					o.setRequestType("query");
				} else if (o.getName().equals(this.mutationTypeName)) {
					o.setRequestType("mutation");
					this.mutationType = o;
				} else if (o.getName().equals(this.subscriptionTypeName)) {
					o.setRequestType("subscription");
					this.subscriptionType = o;
				}
			} else
			// schema
			if ((Object) def instanceof SchemaDefinition || (Object) def instanceof SchemaExtensionDefinition) {
				// This test is awful, but without the (Object) there is a compilation error. And I want to be sure that
				// these tests resist to a change in the graphql-java hierarchy change (like it already happened), so I
				// want to keep this test here

				// No action, we already parsed it
			} else
			// union
			if (def instanceof UnionTypeDefinition) {
				// Unions are read latter, once all GraphQL types have been parsed
			} else {
				logger.warn("Non managed node type: " + def.getClass().getName());
			}
		} // for

		// Once all Types have been properly read, we can read the union types
		logger.debug("Reading union definitions");
		this.typeDefinitionRegistry.types().values().stream()//
				.filter(n -> (n instanceof UnionTypeDefinition)) // We want the union definitions
				.filter(n -> !(n instanceof UnionTypeExtensionDefinition)) // We want their extensions (to avoid doubled
																			// definitions)
				.forEach(n -> this.unionTypes
						.add(readUnionType(new UnionType(((UnionTypeDefinition) n).getName(), this.configuration, this),
								(UnionTypeDefinition) n)));

		// Let's finalize some "details":

		// The types Map allows to retrieve easily a Type from its name
		logger.debug("Fill the type map");
		fillTypesMap();

		// Manage the ExtensionDefinitions that have been read from the GraphQL schema
		manageEnumExtensionDefinitions();
		manageInputExtensionDefinitions();
		manageInterfaceExtensionDefinitions();
		manageScalarExtensionDefinitions();
		manageTypeExtensionDefinitions();
		manageUnionExtensionDefinitions();

		// Init the list of the object implementing each interface. This is done last, when all objects has been read by
		// the plugin.
		logger.debug("Init list of interface implementations");
		initListOfInterfaceImplementations();

		// Add the Relay connection capabilities, if configured for it
		if (this.configuration.isAddRelayConnections()) {
			this.addRelayConnections.addRelayConnections();
		}

		// Identify if one or more input types depends on the JSON custom scalar, to properly generate the
		// spring-graphql controllers that expect such input types
		checkInputTypesForDependenciesToJsonOrObjectCustomScalar();

		// We're done
		int nbClasses = this.objectTypes.size() + this.enumTypes.size() + this.interfaceTypes.size();
		logger.debug("classes identified = " + nbClasses);
		return nbClasses;

	}

	/**
	 * Reads the json schema file, and return the json content as a map.
	 * 
	 * @return
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	private Map<String, Object> loadJsonGraphqlSchemaFile() throws IOException {
		// Let's read the json schema files
		File jsonFile = new File(this.configuration.getSchemaFileFolder(),
				this.configuration.getJsonGraphqlSchemaFilename());
		logger.debug("Reading GraphQL schema from this json file: {}", jsonFile);

		ObjectMapper objectMapper = new ObjectMapper();
		try (InputStream isFlowJson = new FileInputStream(jsonFile)) {
			return objectMapper.readValue(isFlowJson, Map.class);
		}
	}

	/**
	 * Fill the {@link #types} map, from all the types (object, interface, enum, scalars) that are valid for this
	 * schema. This allow to get the properties from their type, as only their type's name is known when parsing the
	 * schema.
	 */
	public void fillTypesMap() {
		// Directive are directly added to the types map.
		// TODO remove this method, and add each type in the types map as it is read

		this.scalarTypes.stream().forEach(s -> this.types.put(s.getName(), s));
		this.customScalars.stream().forEach(s -> this.types.put(s.getName(), s));
		this.objectTypes.stream().forEach(o -> this.types.put(o.getName(), o));
		this.interfaceTypes.stream().forEach(i -> this.types.put(i.getName(), i));
		this.unionTypes.stream().forEach(u -> this.types.put(u.getName(), u));
		this.enumTypes.stream().forEach(e -> this.types.put(e.getName(), e));
	}

	/**
	 * Reads a directive definition, and stores its informations into the {@link DirectiveImpl} for further processing
	 * 
	 * @param node
	 * @return
	 */
	public Directive readDirectiveDefinition(DirectiveDefinition node) {
		DirectiveImpl directive = new DirectiveImpl();

		directive.setName(node.getName());

		// Let's read all its input parameters
		directive.setArguments(node.getInputValueDefinitions().stream().map(this::readFieldTypeDefinition)
				.collect(Collectors.toList()));

		// Let's store its comments,
		directive.setComments(node.getComments());

		// its description,
		if (node.getDescription() != null) {
			directive.setDescription(getDescription(node.getDescription()));
		}

		// and all its locations
		for (graphql.language.DirectiveLocation dl : node.getDirectiveLocations()) {
			DirectiveLocation dirLoc = DirectiveLocation.valueOf(DirectiveLocation.class, dl.getName());
			directive.getDirectiveLocations().add(dirLoc);
		}

		// If it's repeatable, let's store this information
		if (node.isRepeatable()) {
			directive.setRepeatable(true);
		}

		return directive;
	}

	public Directive getDirectiveDefinition(String name) {
		for (Directive d : this.directives) {
			if (d.getName().equals(name)) {
				return d;
			}
		}
		// Oups, not found!
		throw new RuntimeException("The directive named '" + name + "' could not be found");

	}

	/**
	 * Reads a GraphQL directive that has been applied to an item of the GraphQL schema. The relevant directive
	 * definition should already have been read before (see {@link #readDirectiveDefinition(DirectiveDefinition)}).
	 * 
	 * @param directives
	 * @return
	 */
	List<AppliedDirective> readAppliedDirectives(List<graphql.language.Directive> directives) {
		List<AppliedDirective> ret = new ArrayList<>();

		if (directives != null) {
			for (graphql.language.Directive nodeDirective : directives) {
				AppliedDirectiveImpl d = new AppliedDirectiveImpl();
				d.setDirective(getDirectiveDefinition(nodeDirective.getName()));
				// Let's read its arguments
				if (nodeDirective.getArguments() != null) {
					for (Argument a : nodeDirective.getArguments()) {
						// We store the graphql.language.Value as we receive it. We may not have parsed the relevant
						// Object to check its field, and obviously, we can"t instanciate any object or enum yet, as we
						// dont't even generated any code.
						d.getArgumentValues().put(a.getName(), a.getValue());
					}
				}
				ret.add(d);
			} // for
		} // if

		return ret;
	}

	/**
	 * Reads the <code>schema</code> definition from the GraphQL schema
	 * 
	 * @param schemaDef
	 * @param queryObjectNames
	 * @param mutationObjectNames
	 * @param subscriptionObjectNames
	 * 
	 */
	void readSchemaDefinition() {
		// First step: read the schema definition, if it exists
		Optional<SchemaDefinition> optSchemaDef = this.typeDefinitionRegistry.schemaDefinition();
		if (optSchemaDef.isPresent()) {
			// The schema has been defined in the provided schema
			readOneSchemaDefinition(optSchemaDef.get());
		}

		// Second step, read the schema extensions, if any
		for (SchemaExtensionDefinition extDef : this.typeDefinitionRegistry.getSchemaExtensionDefinitions()) {
			readOneSchemaDefinition(extDef);
		} // for
	}

	private void readOneSchemaDefinition(SchemaDefinition def) {
		for (OperationTypeDefinition opDef : def.getOperationTypeDefinitions()) {
			TypeName type = opDef.getTypeName();
			switch (opDef.getName()) {
			case "query":
				this.queryTypeName = type.getName();
				break;
			case "mutation":
				this.mutationTypeName = type.getName();
				break;
			case "subscription":
				this.subscriptionTypeName = type.getName();
				break;
			default:
				throw new RuntimeException(
						"Unexpected OperationTypeDefinition while reading schema: " + opDef.getName());
			}// switch
		} // for

		this.schemaDirectives.addAll(readAppliedDirectives(def.getDirectives()));
	}

	/**
	 * Read an object type from its GraphQL definition
	 * 
	 * @param node
	 * @return
	 */
	public ObjectType readObjectTypeDefinition(ObjectTypeDefinition node) {
		ObjectType objectType = new ObjectType(node.getName(), this.configuration, this);
		return addObjectTypeDefinition(objectType, node);
	}

	void manageEnumExtensionDefinitions() {
		for (List<EnumTypeExtensionDefinition> extList : this.typeDefinitionRegistry.enumTypeExtensions().values()) {
			for (EnumTypeExtensionDefinition def : extList) {
				EnumType enumType = getType(def.getName(), EnumType.class, true);
				readEnumType(enumType, def);
			}
		}
	}

	void manageInputExtensionDefinitions() {
		for (List<InputObjectTypeExtensionDefinition> extList : this.typeDefinitionRegistry.inputObjectTypeExtensions()
				.values()) {
			for (InputObjectTypeExtensionDefinition def : extList) {
				ObjectType objectType = getType(def.getName(), ObjectType.class, true);
				readInputType(objectType, def);
			}
		}
	}

	void manageInterfaceExtensionDefinitions() {
		for (List<InterfaceTypeExtensionDefinition> extList : this.typeDefinitionRegistry.interfaceTypeExtensions()
				.values()) {
			for (InterfaceTypeExtensionDefinition def : extList) {
				InterfaceType interfaceType = getType(def.getName(), InterfaceType.class, true);
				interfaceType.getAppliedDirectives().addAll(readAppliedDirectives(def.getDirectives()));
				interfaceType.getFields().addAll(def.getFieldDefinitions().stream()
						.map(d -> readField(d, interfaceType)).collect(Collectors.toList()));
			}
		}
	}

	void manageScalarExtensionDefinitions() {
		for (List<ScalarTypeExtensionDefinition> extList : this.typeDefinitionRegistry.scalarTypeExtensions()
				.values()) {
			for (ScalarTypeExtensionDefinition node : extList) {
				ScalarType type = getType(node.getName(), ScalarType.class, true);
				type.getAppliedDirectives().addAll(readAppliedDirectives(node.getDirectives()));
			}
		}
	}

	/**
	 * Manages the type extensions found in the read {@link Document}s, and them to the relevant object(s)
	 */
	void manageTypeExtensionDefinitions() {
		for (List<ObjectTypeExtensionDefinition> extList : this.typeDefinitionRegistry.objectTypeExtensions()
				.values()) {
			for (ObjectTypeExtensionDefinition node : extList) {
				ObjectType objectType = getType(node.getName(), ObjectType.class, true);
				addObjectTypeDefinition(objectType, node);
			}
		}
	}

	void manageUnionExtensionDefinitions() {
		for (List<UnionTypeExtensionDefinition> extList : this.typeDefinitionRegistry.unionTypeExtensions().values()) {
			for (UnionTypeExtensionDefinition node : extList) {
				UnionType unionType = getType(node.getName(), UnionType.class, true);
				readUnionType(unionType, node);
			}
		}
	}

	/**
	 * This method read the given definition, and add it to the given object. It can be called for both the Type
	 * definition, and the TypeExtension definition
	 * 
	 * @param objectType
	 *            The Object Type Definition in which the node properties must be stored. It should be null when reading
	 *            a {@link ObjectTypeDefinition}, so that a new {@link ObjectType} is returned. And not null for
	 *            {@link ObjectTypeExtensionDefinition}, so that the read properties are added to the already existing
	 *            {@link ObjectType}.
	 * @param node
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	private ObjectType addObjectTypeDefinition(final ObjectType objectType, ObjectTypeDefinition node) {
		objectType.getAppliedDirectives().addAll(readAppliedDirectives(node.getDirectives()));

		// Let's read all its fields
		objectType.getFields().addAll(node.getFieldDefinitions().stream().map(def -> readField(def, objectType))
				.collect(Collectors.toList()));

		// Let's store its comments
		objectType.getComments()
				.addAll(node.getComments().stream().map(c -> c.getContent()).collect(Collectors.toList()));

		// and its description,
		if (node.getDescription() != null) {
			objectType.setDescription(getDescription(node.getDescription()));
		}

		// Let's read all the interfaces this object implements
		for (graphql.language.Type type : node.getImplements()) {
			if (type instanceof TypeName) {
				objectType.getImplementz().add(((TypeName) type).getName());
			} else if (type instanceof graphql.language.EnumValue) {
				objectType.getImplementz().add(((graphql.language.EnumValue) type).getName());
			} else {
				throw new RuntimeException("Non managed object type '" + type.getClass().getName()
						+ "' when listing implementations for the object '" + node.getName() + "'");
			}
		} // for

		return objectType;
	}

	/**
	 * Read an input type from its GraphQL definition. It can be called with either an {@link InputObjectTypeDefinition}
	 * or an {@link InputObjectTypeExtensionDefinition} node
	 * 
	 * @param objectType
	 *            The input type that is to be read, or extended
	 * @param node
	 * @return
	 */
	ObjectType readInputType(ObjectType objectType, InputObjectTypeDefinition node) {

		objectType.setInputType(true);

		objectType.getAppliedDirectives().addAll(readAppliedDirectives(node.getDirectives()));

		// Let's store its comments
		objectType.getComments()
				.addAll(node.getComments().stream().map(c -> c.getContent()).collect(Collectors.toList()));

		// and its description,
		if (node.getDescription() != null) {
			objectType.setDescription(getDescription(node.getDescription()));
		}

		// Let's read all its fields
		for (InputValueDefinition def : node.getInputValueDefinitions()) {
			FieldImpl field = readFieldTypeDefinition(def);
			field.setOwningType(objectType);

			objectType.getFields().add(field);
		}

		return objectType;
	}

	/**
	 * Read an interface type from its GraphQL definition.<BR/>
	 * This method doesn't add the interface annotations, as the client annotations needs to know all types that
	 * implement this interface. Interface annotations are added in the {@link #addAnnotations()} method.
	 * 
	 * @param node
	 * @return
	 * @see #initInterfaceAnnotations()
	 */
	@SuppressWarnings("rawtypes")
	InterfaceType readInterfaceType(InterfaceTypeDefinition node) {
		// Let's check if it's a real object, or part of a schema (query, subscription,
		// mutation) definition

		InterfaceType interfaceType = new InterfaceType(node.getName(), this.configuration, this);

		interfaceType.setAppliedDirectives(readAppliedDirectives(node.getDirectives()));

		// Let's store its comments
		interfaceType.setComments(node.getComments());

		// and its description,
		if (node.getDescription() != null) {
			interfaceType.setDescription(getDescription(node.getDescription()));
		}

		// Let's read all its fields
		interfaceType.setFields(node.getFieldDefinitions().stream().map(def -> readField(def, interfaceType))
				.collect(Collectors.toList()));

		// Let's read all the interfaces that this one implements
		for (graphql.language.Type type : node.getImplements()) {
			if (type instanceof TypeName) {
				interfaceType.getImplementz().add(((TypeName) type).getName());
			} else {
				throw new RuntimeException("Non managed object type '" + type.getClass().getName()
						+ "' when listing implementations for the object '" + node.getName() + "'");
			}
		} // for

		return interfaceType;
	}

	/**
	 * Read an union type from its GraphQL definition. This method can be called for both a {@link UnionTypeDefinition}
	 * or a {@link UnionTypeExtensionDefinition}
	 * 
	 * @param node
	 * @return
	 */
	UnionType readUnionType(UnionType unionType, UnionTypeDefinition node) {
		unionType.getAppliedDirectives().addAll(readAppliedDirectives(node.getDirectives()));

		// Let's store its comments
		unionType.getComments()
				.addAll(node.getComments().stream().map(c -> c.getContent()).collect(Collectors.toList()));

		// and its description,
		if (node.getDescription() != null) {
			unionType.setDescription(getDescription(node.getDescription()));
		}

		for (graphql.language.Type<?> memberType : node.getMemberTypes()) {
			String memberTypeName = (String) this.graphqlUtils.invokeMethod("getName", memberType);

			// We can not use getType yet, as the type list is not filled.
			ObjectType type = null;
			for (ObjectType ot : this.objectTypes) {
				if (ot.getName().equals(memberTypeName)) {
					type = ot;
					break;
				}
			}
			if (type == null) {
				throw new RuntimeException("Could not find the ObjectType named '" + memberTypeName + "'");
			}

			type.getMemberOfUnions().add(unionType);
			type.getImplementz().add(unionType.getName());
			unionType.getMemberTypes().add(type);
		}

		return unionType;
	}

	/**
	 * Reads a GraphQL Custom Scalar, from its definition. This method checks that the CustomScalar has already been
	 * defined, in the plugin configuration.
	 * 
	 * @param node
	 *            The {@link CustomScalarType} that represents this Custom Scalar
	 * @return
	 */
	CustomScalarType readCustomScalarType(ScalarTypeDefinition node) {
		String name = node.getName();

		CustomScalarDefinition customScalarDef = null;
		if (!(this instanceof GenerateGraphQLSchemaDocumentParser)) {
			// An implementation of this custom scalar must have been provided
			@SuppressWarnings("unchecked")
			List<CustomScalarDefinition> confCustomScalarDefs = (List<CustomScalarDefinition>) this.graphqlUtils
					.invokeGetter(this.configuration, "customScalars");
			if (confCustomScalarDefs != null) {
				for (CustomScalarDefinition csd : confCustomScalarDefs) {
					if (name.equals(csd.getGraphQLTypeName())) {
						customScalarDef = csd;
						break;
					}
				}
			}
			if (customScalarDef == null) {
				throw new RuntimeException(
						"The plugin configuration must provide an implementation for the Custom Scalar '" + name
								+ "' custom scalar");
			}
		}
		CustomScalarType customScalarType = new CustomScalarType(name, customScalarDef, this.configuration, this);

		customScalarType.getAppliedDirectives().addAll(readAppliedDirectives(node.getDirectives()));

		// Let's store its comments
		customScalarType.setComments(node.getComments());

		// and its description,
		if (node.getDescription() != null) {
			customScalarType.setDescription(getDescription(node.getDescription()));
		}

		return customScalarType;
	}

	/**
	 * Reads an enum definition. It can be called from either an {@link EnumTypeDefinition} or an
	 * {@link EnumTypeExtensionDefinition}
	 * 
	 * @param enumType
	 *            The enum for which the definition is read.
	 * @param node
	 * @return
	 */
	public EnumType readEnumType(EnumType enumType, EnumTypeDefinition node) {
		enumType.getAppliedDirectives().addAll(readAppliedDirectives(node.getDirectives()));

		// Let's store its comments
		enumType.getComments()
				.addAll(node.getComments().stream().map(c -> c.getContent()).collect(Collectors.toList()));

		// and its description,
		if (node.getDescription() != null) {
			enumType.setDescription(getDescription(node.getDescription()));
		}

		for (EnumValueDefinition enumValDef : node.getEnumValueDefinitions()) {
			EnumValue val = EnumValueImpl.builder().name(enumValDef.getName())
					.appliedDirectives(readAppliedDirectives(enumValDef.getDirectives())).build();
			enumType.getValues().add(val);
		} // for

		return enumType;
	}

	/**
	 * Reads one GraphQL {@link FieldDefinition}, and maps it into a {@link Field}.
	 * 
	 * @param node
	 * @param owningType
	 *            The type which contains this field
	 * @return
	 * @throws MojographqlUtils.executionException
	 */
	Field readField(FieldDefinition node, Type owningType) {

		FieldImpl field = readFieldTypeDefinition(node); // includes reading the directives
		field.setOwningType(owningType);

		// Let's read all its input parameters
		field.setInputParameters(node.getInputValueDefinitions().stream().map(this::readFieldTypeDefinition)
				.collect(Collectors.toList()));

		// Let's store its comments
		field.setComments(node.getComments());

		// and its description
		if (node.getDescription() != null) {
			field.setDescription(getDescription(node.getDescription()));
		}

		return field;
	}

	/**
	 * Reads a field, which can be either a GraphQL {@link FieldDefinition} or an {@link InputValueDefinition}, and maps
	 * it into a {@link Field}. The graphQL-java library maps both FieldDefinition and InputValueDefinition in very
	 * similar structures, which are actually trees. These structures are too hard too read in a Velocity template, and
	 * we need to parse down to a properly structures way for that.
	 * 
	 * @param fieldDef
	 * @param field
	 * @return
	 */
	@SuppressWarnings("unchecked")
	FieldImpl readFieldTypeDefinition(AbstractNode<?> fieldDef) {
		FieldImpl field = FieldImpl.builder().documentParser(this).build();

		field.setName((String) this.graphqlUtils.invokeMethod("getName", fieldDef));
		field.setAppliedDirectives(readAppliedDirectives(
				(List<graphql.language.Directive>) this.graphqlUtils.invokeMethod("getDirectives", fieldDef)));

		field.setFieldTypeAST(readFieldTypeAST(this.graphqlUtils.invokeMethod("getType", fieldDef)));

		// For InputValueDefinition, we may have a default value
		if (fieldDef instanceof InputValueDefinition) {
			field.setDefaultValue(((InputValueDefinition) fieldDef).getDefaultValue());
		}

		// Add description if exists
		try {
			if (((AbstractDescribedNode<InputValueDefinition>) fieldDef).getDescription() != null) {
				field.setDescription(
						getDescription(((AbstractDescribedNode<InputValueDefinition>) fieldDef).getDescription()));
			}
		} catch (ClassCastException ignored) {
			// Do nothing description remains null
		}

		return field;

	}

	FieldTypeAST readFieldTypeAST(Object fieldDef) {
		if (fieldDef instanceof TypeName) {
			TypeName typeName = (TypeName) fieldDef;
			FieldTypeAST ret = new FieldTypeAST(typeName.getName());
			ret.setListDepth(0);
			return ret;
		} else if (fieldDef instanceof ListType) {
			// This node contains a list. Let's recurse one.
			ListType node = (ListType) fieldDef;
			FieldTypeAST listItemTypeAST = readFieldTypeAST(node.getType());
			// We return a list of the read subnode.
			FieldTypeAST fieldTypeAST = new FieldTypeAST();
			fieldTypeAST.setListDepth(listItemTypeAST.getListDepth() + 1);
			fieldTypeAST.setListItemFieldTypeAST(listItemTypeAST);
			fieldTypeAST.setItemMandatory(node.getChildren().get(0) instanceof NonNullType);
			return fieldTypeAST;
		} else if (fieldDef instanceof NonNullType) {
			// Let's recurse in the AST for this mandatory type
			NonNullType subNode = (NonNullType) fieldDef;
			FieldTypeAST fieldTypeAST = readFieldTypeAST(subNode.getType());
			// The type is mandatory
			fieldTypeAST.setMandatory(true);
			return fieldTypeAST;
		} else {
			throw new RuntimeException("Non managed fieldDef: " + fieldDef.getClass().getName());
		}

	}

	/**
	 * A utility method, which maps an object type to the class full name of the Java class which will be generated for
	 * this object type. This utility method is based on the {@link GraphQLConfiguration#getPackageName()} plugin
	 * attribute, available in this class
	 * 
	 * @param name
	 */
	String getGeneratedFieldFullClassName(String name) {
		return ((GenerateCodeCommonConfiguration) this.configuration).getPackageName() + "." + name;
	}

	/**
	 * Returns the type for the given name
	 * 
	 * @param typeName
	 * @return
	 * @throws RuntimeException
	 *             if the type could not be found
	 * @See {@link #getType(String, boolean)}
	 */
	public Type getType(String typeName) {
		return getType(typeName, true);
	}

	/**
	 * Returns the type for the given name
	 * 
	 * @param typeName
	 * @param throwExceptionIfNotFound
	 *            If true, a {@link RuntimeException} is thrown when the type is not found. If false and the type is not
	 *            found, null is returned.
	 * @return The found type, or null if the type is not found and throwExceptionIfNotFound is false
	 * @throws RuntimeException
	 *             if <I>throwExceptionIfNotFound</I> is true and the type could not be found
	 */
	public Type getType(String typeName, boolean throwExceptionIfNotFound) {
		Type ret = this.types.get(typeName);
		if (throwExceptionIfNotFound && ret == null) {
			throw new RuntimeException("The type named '" + typeName + "' could not be found");
		}
		return ret;
	}

	/**
	 * Returns the type for the given name, where the found type is of an expected kind.
	 * 
	 * @param typeName
	 * @param classOfType
	 *            The expected class for this type (typically one of {@link ScalarType}, {@link ObjectType}...)
	 * @param throwExceptionIfNotFound
	 *            If true, a {@link RuntimeException} is thrown when the type is not found. If false and the type is not
	 *            found, null is returned.
	 * @return The found type, or null if the type is not found and throwExceptionIfNotFound is false
	 * @throws RuntimeException
	 *             if <I>throwExceptionIfNotFound</I> is true and the type could not be found
	 */
	public <T> T getType(String typeName, Class<T> classOfType, boolean throwExceptionIfNotFound) {
		Type ret = this.types.get(typeName);

		if (ret == null) {
			if (throwExceptionIfNotFound) {
				throw new RuntimeException("The type named '" + typeName + "' could not be found");
			} else {
				return null;
			}
		}

		if (classOfType.isInstance(ret)) {
			return classOfType.cast(ret);
		}

		throw new RuntimeException("The type named '" + typeName + "' should be an instance of "
				+ classOfType.getSimpleName() + " but is an instance of " + ret.getClass().getSimpleName());
	}

	/**
	 * For each interface, identify the list of object types which implements it. This is done last, when all objects
	 * has been read by the plugin. .
	 * 
	 * @see InterfaceType#getImplementingTypes()
	 */
	void initListOfInterfaceImplementations() {
		for (InterfaceType interfaceType : this.interfaceTypes) {
			Stream.concat(this.objectTypes.stream(), this.interfaceTypes.stream()).forEach((o) -> {
				if (o.getImplementz().contains(interfaceType.getName())) {
					// This object implements the current interface we're looping in.
					interfaceType.getImplementingTypes().add(o);
				}
			});
		} // for
	}

	/**
	 * Identify if one or more input types depends on the JSON or the Object custom scalar, to properly generate the
	 * spring-graphql controllers that expect such input types
	 */
	private void checkInputTypesForDependenciesToJsonOrObjectCustomScalar() {
		boolean mustIterateOnceMore = true;

		while (mustIterateOnceMore) {

			// by default it's the last iteration, unless we change the 'dependsOnJsonCustomScalar' status for a type
			// at least once.
			mustIterateOnceMore = false;

			for (ObjectType o : this.objectTypes) {
				if (!o.isDependsOnJsonOrObjectCustomScalar()) {
					for (Field f : o.getFields()) {
						if (false || //
								f.getType().getName().equals("JSON") //
								|| //
								f.getType().getName().equals("Object") //
								|| //
								(f.getType() instanceof ObjectType
										&& ((ObjectType) f.getType()).isDependsOnJsonOrObjectCustomScalar())) {
							o.setDependsOnJsonOrObjectCustomScalar(true);
							mustIterateOnceMore = true;
							continue;
						}
					}
				}
			} // for
		} // while
	}

	/**
	 * Returns the name of the package for utility classes.<BR/>
	 * In this class, it always return the result of {@link CommonConfiguration#getPackageName()}
	 * 
	 * @return
	 */
	protected String getUtilPackageName() {
		return ((GenerateCodeCommonConfiguration) this.configuration).getPackageName();
	}

	/**
	 * Returns an instance of {@link Description} based on the given String, or null of this string is null
	 * 
	 * @param description
	 * @return
	 */
	private Description getDescription(graphql.language.Description description) {
		return (description == null) ? null : new Description(description);
	}

}