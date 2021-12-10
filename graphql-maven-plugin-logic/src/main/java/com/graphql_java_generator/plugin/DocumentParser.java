/**
 * 
 */
package com.graphql_java_generator.plugin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.graphql_java_generator.plugin.conf.CommonConfiguration;
import com.graphql_java_generator.plugin.conf.GenerateCodeCommonConfiguration;
import com.graphql_java_generator.plugin.conf.GraphQLConfiguration;
import com.graphql_java_generator.plugin.language.AppliedDirective;
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
import com.graphql_java_generator.plugin.language.impl.ScalarExtensionType;
import com.graphql_java_generator.plugin.language.impl.ScalarType;
import com.graphql_java_generator.plugin.language.impl.UnionType;
import com.graphql_java_generator.util.GraphqlUtils;

import graphql.language.AbstractNode;
import graphql.language.Argument;
import graphql.language.Definition;
import graphql.language.DirectiveDefinition;
import graphql.language.Document;
import graphql.language.EnumTypeDefinition;
import graphql.language.EnumValueDefinition;
import graphql.language.FieldDefinition;
import graphql.language.InputObjectTypeDefinition;
import graphql.language.InputValueDefinition;
import graphql.language.InterfaceTypeDefinition;
import graphql.language.ListType;
import graphql.language.NonNullType;
import graphql.language.ObjectTypeDefinition;
import graphql.language.ObjectTypeExtensionDefinition;
import graphql.language.OperationTypeDefinition;
import graphql.language.ScalarTypeDefinition;
import graphql.language.ScalarTypeExtensionDefinition;
import graphql.language.SchemaDefinition;
import graphql.language.StringValue;
import graphql.language.TypeName;
import graphql.language.UnionTypeDefinition;
import graphql.parser.Parser;
import graphql.parser.ParserOptions;
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
public abstract class DocumentParser {

	private static final Logger logger = LoggerFactory.getLogger(DocumentParser.class);

	protected final String DEFAULT_QUERY_NAME = "Query";
	protected final String DEFAULT_MUTATION_NAME = "Mutation";
	protected final String DEFAULT_SUBSCRIPTION_NAME = "Subscription";

	/**
	 * This instance is responsible for providing all the configuration parameter from the project (Maven, Gradle...)
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
	 * This Spring Bean is created by {@link SpringConfiguration}. The {@link ResourceSchemaStringProvider} adds the
	 * introspection schema into the documents list.<BR/>
	 * See also the {@link #addIntrospectionCapabilities()} that finalize the introspection capabilities for the
	 * generated code.
	 */
	@Autowired
	protected Documents documents;

	/** List of all the directives that have been read in the GraphQL schema */
	@Getter
	@Setter
	protected List<Directive> directives = new ArrayList<>();

	/** The Query root operation for this Document */
	@Getter
	@Setter
	protected ObjectType queryType = null;

	/**
	 * The Subscription root operation for this Document, if defined (that is: if this schema implements one or more
	 * subscriptions)
	 */
	@Getter
	@Setter
	protected ObjectType subscriptionType = null;

	/**
	 * The Mutation root operation for this Document, if defined (that is: if this schema implements one or more
	 * mutations)
	 */
	@Getter
	@Setter
	protected ObjectType mutationType = null;

	/**
	 * All the {@link ObjectType} which have been read during the reading of the documents
	 */
	@Getter
	@Setter
	List<ObjectType> objectTypes = new ArrayList<>();

	/**
	 * We store all the found object extensions (extend GraphQL keyword), to manage them once all object definitions
	 * have been read
	 */
	protected List<ObjectTypeExtensionDefinition> objectTypeExtensionDefinitions = new ArrayList<>();

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
	 * ones), the interfaces, the unions and the enums
	 */
	@Getter
	@Setter
	protected Map<String, com.graphql_java_generator.plugin.language.Type> types = new HashMap<>();

	@PostConstruct
	public void postConstruct() {

		logger.debug("Starting DocumentParser's PostConstrut intialization");

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
		directives.add(skip);
		//
		// @include
		DirectiveImpl include = new DirectiveImpl();
		include.setName("include");
		include.getArguments()
				.add(FieldImpl.builder().name("if")
						.fieldTypeAST(FieldTypeAST.builder().graphQLTypeSimpleName("Boolean").mandatory(true).build())//
						.build());
		include.getDirectiveLocations().add(DirectiveLocation.FIELD);
		include.getDirectiveLocations().add(DirectiveLocation.FRAGMENT_SPREAD);
		include.getDirectiveLocations().add(DirectiveLocation.INLINE_FRAGMENT);
		include.setStandard(true);
		directives.add(include);
		//
		// @defer
		DirectiveImpl defer = new DirectiveImpl();
		defer.setName("defer");
		defer.getArguments()
				.add(FieldImpl.builder().name("if")
						.fieldTypeAST(FieldTypeAST.builder().graphQLTypeSimpleName("Boolean").mandatory(true).build())//
						.build());
		defer.getDirectiveLocations().add(DirectiveLocation.FIELD);
		defer.setStandard(true);
		directives.add(defer);
		//
		// @deprecated
		DirectiveImpl deprecated = new DirectiveImpl();
		deprecated.setName("deprecated");
		deprecated.getArguments()
				.add(FieldImpl.builder().name("reason")
						.fieldTypeAST(FieldTypeAST.builder().graphQLTypeSimpleName("String").build())
						.defaultValue(new StringValue("No longer supported")).build());
		deprecated.getDirectiveLocations().add(DirectiveLocation.FIELD_DEFINITION);
		deprecated.getDirectiveLocations().add(DirectiveLocation.ENUM_VALUE);
		deprecated.setStandard(true);
		directives.add(deprecated);

		logger.debug("Finished DocumentParser's PostConstrut intialization");

	}

	/**
	 * This method initializes the {@link #scalarTypes} list. This list depends on the use case
	 * 
	 * @param IDclass
	 * 
	 */
	protected void initScalarTypes(Class<?> IDclass) {
		scalarTypes.add(new ScalarType("Boolean", "java.lang", "Boolean", configuration));
		// GraphQL Float is a double precision number
		scalarTypes.add(new ScalarType("Float", "java.lang", "Double", configuration));
		// By default, we use the UUID type for the ID GraphQL type
		scalarTypes.add(new ScalarType("ID", IDclass.getPackage().getName(), IDclass.getSimpleName(), configuration));
		scalarTypes.add(new ScalarType("Int", "java.lang", "Integer", configuration));
		scalarTypes.add(new ScalarType("String", "java.lang", "String", configuration));
	}

	/**
	 * The main method of the class: it graphqlUtils.executes the generation of the given documents
	 * 
	 * @param documents
	 *            The GraphQL definition schema, from which the code is to be generated
	 * @return
	 * @throws IOException
	 *             When an error occurs, during the parsing of the GraphQL schemas
	 */
	public int parseDocuments() throws IOException {
		logger.debug("Starting documents parsing");

		// Configuration of the GraphQL schema parser, from the project configuration
		if (configuration.getParserOptions() != null && configuration.getParserOptions().getMaxTokens() != null) {
			ParserOptions newDefault = ParserOptions.newParserOptions()
					.maxTokens(configuration.getParserOptions().getMaxTokens()).build();
			ParserOptions.setDefaultParserOptions(newDefault);
		}

		documents.getDocuments().stream().forEach(this::parseOneDocument);

		logger.debug("Documents have been parsed. Executing internal finalizations");

		// Let's finalize some "details":

		// Init the list of the object implementing each interface. This is done last, when all objects has been read by
		// the plugin.
		logger.debug("Init list of interface implementations");
		initListOfInterfaceImplementations();
		// The types Map allows to retrieve easily a Type from its name
		logger.debug("Fill type map");
		fillTypesMap();
		// Manage ObjectTypeExtensionDefinition: add the extension to the object they belong to
		manageObjectTypeExtensionDefinition();
		// Add the Relay connection capabilities, if configured for it
		if (configuration.isAddRelayConnections()) {
			addRelayConnections.addRelayConnections();
		}

		// We're done
		int nbClasses = objectTypes.size() + enumTypes.size() + interfaceTypes.size();
		logger.debug(documents.getDocuments().size() + " document(s) parsed (" + nbClasses + ")");
		return nbClasses;
	}

	/**
	 * Generates the target classes for the given GraphQL schema definition
	 * 
	 * @param document
	 */
	public void parseOneDocument(Document document) {
		// List of all the names of the query types. There should be only one. But we're ready for more (for instance if
		// several schema files have been merged)
		List<String> queryObjectNames = new ArrayList<>();
		// List of all the names of the mutation types. There should be only one. But we're ready for more (for instance
		// if several schema files have been merged)
		List<String> mutationObjectNames = new ArrayList<>();
		// List of all the names of the subscription types. There should be only one. But we're ready for more (for
		// instance if several schema files have been merged)
		List<String> subscriptionObjectNames = new ArrayList<>();

		// The Directives must be read first, as they may be found on almost any kind of definition in the GraphQL
		// schema
		document.getDefinitions().stream().filter(n -> (n instanceof DirectiveDefinition))
				.forEach(node -> directives.add(readDirectiveDefinition((DirectiveDefinition) node)));

		// Looks for a schema definitions, to list the defined queries, mutations and subscriptions (should be only one
		// of each), but we're ready for more. (for instance if several schema files have been merged)
		logger.debug("Looking for schema definition");
		for (Definition<?> node : document.getDefinitions()) {
			if (node instanceof SchemaDefinition) {
				readSchemaDefinition((SchemaDefinition) node, queryObjectNames, mutationObjectNames,
						subscriptionObjectNames);
			} // if
		} // for

		logger.debug("Reading node definitions");
		for (Definition<?> node : document.getDefinitions()) {
			// directive
			if (node instanceof DirectiveDefinition) {
				// Directives are read latter
			} else
			// enum
			if (node instanceof EnumTypeDefinition) {
				enumTypes.add(readEnumType((EnumTypeDefinition) node));
			} else
			// input object
			if (node instanceof InputObjectTypeDefinition) {
				objectTypes.add(readInputObjectType((InputObjectTypeDefinition) node));
			} else
			// interface
			if (node instanceof InterfaceTypeDefinition) {
				interfaceTypes.add(readInterfaceType((InterfaceTypeDefinition) node));
			} else
			// extend object
			if (node instanceof ObjectTypeExtensionDefinition) {
				// ObjectTypeExtensionDefinition is a subclass of ObjectTypeDefinition, so we need to check it first.
				//
				// No action here: we'll manage all the object extensions once all object definitions have been read
				objectTypeExtensionDefinitions.add((ObjectTypeExtensionDefinition) node);
			} else
			// object
			if (node instanceof ObjectTypeDefinition) {
				// Let's check what kind of ObjectDefinition we have
				String name = ((ObjectTypeDefinition) node).getName();
				if (queryObjectNames.contains(name) || DEFAULT_QUERY_NAME.equals(name)) {
					// We first read the object type, that'll go to the main package
					ObjectType o = readObjectTypeDefinition((ObjectTypeDefinition) node);
					o.setRequestType("query");
					objectTypes.add(o);
					// Then we read the query, that'll go in the util subpackage: its imports are different
					if (queryType != null) {
						throw new RuntimeException(
								"Error while reading the query '" + ((ObjectTypeDefinition) node).getName()
										+ "'. A Query root operation has already been read, with name'"
										+ queryType.getName() + "'");
					}
					queryType = o;
				} else if (mutationObjectNames.contains(name) || DEFAULT_MUTATION_NAME.equals(name)) {
					// We first read the object type, that'll go to the main package
					ObjectType o = readObjectTypeDefinition((ObjectTypeDefinition) node);
					o.setRequestType("mutation");
					objectTypes.add(o);
					// Then we read the mutation, that'll go in the util subpackage: its imports are different
					if (mutationType != null) {
						throw new RuntimeException(
								"Error while reading the mutation '" + ((ObjectTypeDefinition) node).getName()
										+ "'. A Mutation root operation has already been read, with name'"
										+ mutationType.getName() + "'");
					}
					mutationType = o;
				} else if (subscriptionObjectNames.contains(name) || DEFAULT_SUBSCRIPTION_NAME.equals(name)) {
					// We first read the object type, that'll go to the main package
					ObjectType o = readObjectTypeDefinition((ObjectTypeDefinition) node);
					o.setRequestType("subscription");
					objectTypes.add(o);
					// Then we read the subscription, that'll go in the util subpackage: its imports are different
					if (subscriptionType != null) {
						throw new RuntimeException(
								"Error while reading the subscription '" + ((ObjectTypeDefinition) node).getName()
										+ "'. A Subscription root operation has already been read, with name'"
										+ subscriptionType.getName() + "'");
					}
					subscriptionType = o;
				} else {
					objectTypes.add(readObjectTypeDefinition((ObjectTypeDefinition) node));
				}
			} else
			// scalar extension
			if (node instanceof ScalarTypeExtensionDefinition) {
				readScalarExtensionType((ScalarTypeExtensionDefinition) node);
			} else
			// scalar
			if (node instanceof ScalarTypeDefinition) {
				// Custom scalars implementation must be provided by the configuration. We just check that it's OK.
				readCustomScalarType((ScalarTypeDefinition) node);
			} else
			// schema
			if (node instanceof SchemaDefinition) {
				// No action, we already parsed it
			} else
			// union
			if (node instanceof UnionTypeDefinition) {
				// Unions are read latter, once all GraphQL types have been parsed
			} else {
				logger.warn("Non managed node type: " + node.getClass().getName());
			}
		} // for

		// Once all Types have been properly read, we can read the union types
		logger.debug("Reading union definitions");
		document.getDefinitions().stream().filter(n -> (n instanceof UnionTypeDefinition))
				.forEach(n -> unionTypes.add(readUnionType((UnionTypeDefinition) n)));
	}

	/**
	 * Fill the {@link #types} map, from all the types (object, interface, enum, scalars) that are valid for this
	 * schema. This allow to get the properties from their type, as only their type's name is known when parsing the
	 * schema.
	 */
	public void fillTypesMap() {
		// Directive are directly added to the types map.
		// TODO remove this method, and add each type in the types map as it is read

		scalarTypes.stream().forEach(s -> types.put(s.getName(), s));
		objectTypes.stream().forEach(o -> types.put(o.getName(), o));
		interfaceTypes.stream().forEach(i -> types.put(i.getName(), i));
		unionTypes.stream().forEach(u -> types.put(u.getName(), u));
		enumTypes.stream().forEach(e -> types.put(e.getName(), e));
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

		// Let's store its comments
		directive.setComments(node.getComments());

		// and all its locations
		for (graphql.language.DirectiveLocation dl : node.getDirectiveLocations()) {
			DirectiveLocation dirLoc = DirectiveLocation.valueOf(DirectiveLocation.class, dl.getName());
			directive.getDirectiveLocations().add(dirLoc);
		}

		return directive;
	}

	public Directive getDirectiveDefinition(String name) {
		for (Directive d : directives) {
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
	 * @param schemaDef
	 * @param queryObjectNames
	 * @param mutationObjectNames
	 * @param subscriptionObjectNames
	 * 
	 */
	public void readSchemaDefinition(SchemaDefinition schemaDef, List<String> queryObjectNames,
			List<String> mutationObjectNames, List<String> subscriptionObjectNames) {

		for (OperationTypeDefinition opDef : schemaDef.getOperationTypeDefinitions()) {
			TypeName type = opDef.getTypeName();
			switch (opDef.getName()) {
			case "query":
				queryObjectNames.add(type.getName());
				break;
			case "mutation":
				mutationObjectNames.add(type.getName());
				break;
			case "subscription":
				subscriptionObjectNames.add(type.getName());
				break;
			default:
				throw new RuntimeException(
						"Unexpected OperationTypeDefinition while reading schema: " + opDef.getName());
			}// switch
		} // for
	}

	/**
	 * Read an object type from its GraphQL definition
	 * 
	 * @param node
	 * @return
	 */
	public ObjectType readObjectTypeDefinition(ObjectTypeDefinition node) {
		ObjectType objectType = new ObjectType(node.getName(), configuration);
		return addObjectTypeDefinition(objectType, node);
	}

	/**
	 * Manages all the extensions found in the read {@link Document}s, and them to the relevant object(s)
	 */
	void manageObjectTypeExtensionDefinition() {
		for (ObjectTypeExtensionDefinition node : objectTypeExtensionDefinitions) {
			ObjectType objectType = (ObjectType) getType(node.getName());
			addObjectTypeDefinition(objectType, node);
		}
	}

	/**
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
		objectType.setAppliedDirectives(readAppliedDirectives(node.getDirectives()));

		// Let's read all its fields
		objectType.getFields().addAll(node.getFieldDefinitions().stream().map(def -> readField(def, objectType))
				.collect(Collectors.toList()));

		// Let's store its comments
		objectType.setComments(node.getComments());

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
	 * Read an input object type from its GraphQL definition
	 * 
	 * @param node
	 * @return
	 */
	ObjectType readInputObjectType(InputObjectTypeDefinition node) {

		ObjectType objectType = new ObjectType(node.getName(), configuration);
		objectType.setInputType(true);

		objectType.setAppliedDirectives(readAppliedDirectives(node.getDirectives()));

		// Let's store its comments
		objectType.setComments(node.getComments());

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

		InterfaceType interfaceType = new InterfaceType(node.getName(), configuration);

		interfaceType.setAppliedDirectives(readAppliedDirectives(node.getDirectives()));

		// Let's store its comments
		interfaceType.setComments(node.getComments());

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
	 * Read an union type from its GraphQL definition
	 * 
	 * @param node
	 * @return
	 */
	UnionType readUnionType(UnionTypeDefinition node) {
		UnionType unionType = new UnionType(node.getName(), configuration);
		unionType.setAppliedDirectives(readAppliedDirectives(node.getDirectives()));

		// Let's store its comments
		unionType.setComments(node.getComments());

		for (graphql.language.Type<?> memberType : node.getMemberTypes()) {
			String memberTypeName = (String) graphqlUtils.invokeMethod("getName", memberType);

			// We can not use getType yet, as the type list is not filled.
			ObjectType type = null;
			for (ObjectType ot : objectTypes) {
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
	ScalarExtensionType readScalarExtensionType(ScalarTypeExtensionDefinition node) {
		String name = node.getName();

		// The current node is an extension of a GraphQL scalar. We must find the entry in the scalar list, to replace
		// it by the scalar extension definition
		boolean found = false;
		ScalarType scalarType = null;
		for (ScalarType t : scalarTypes) {
			if (t.getName().equals(name)) {
				found = true;
				scalarType = t;
			}
		} // for
		if (!found) {
			throw new RuntimeException(
					"[Internal error] The '" + name + "' scalar definition was not properly initialized");
		}

		ScalarExtensionType scalarExtensionType = new ScalarExtensionType(name, scalarType.getPackageName(),
				scalarType.getClassSimpleName(), configuration);
		scalarExtensionType.setAppliedDirectives(readAppliedDirectives(node.getDirectives()));
		scalarExtensionType.setComments(node.getComments());

		// We replace the definition for the original GraphQL scalar by this one
		scalarTypes.remove(scalarType);
		scalarTypes.add(scalarExtensionType);

		return scalarExtensionType;
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

		CustomScalarType customScalarType = getCustomScalarType(name);
		customScalarType.setAppliedDirectives(readAppliedDirectives(node.getDirectives()));

		// Let's store its comments
		customScalarType.setComments(node.getComments());

		return customScalarType;
	}

	/**
	 * This method retrieves the definition for the given Custom Scalar.
	 * 
	 * @param name
	 * @return
	 */
	protected abstract CustomScalarType getCustomScalarType(String name);

	/**
	 * Reads an enum definition, and create the relevant {@link EnumType}
	 * 
	 * @param node
	 * @return
	 */
	public EnumType readEnumType(EnumTypeDefinition node) {
		EnumType enumType = new EnumType(node.getName(), configuration);

		enumType.setAppliedDirectives(readAppliedDirectives(node.getDirectives()));

		// Let's store its comments
		enumType.setComments(node.getComments());

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
	 * @param fieldDef
	 * @param owningType
	 *            The type which contains this field
	 * @return
	 * @throws MojographqlUtils.executionException
	 */
	Field readField(FieldDefinition fieldDef, Type owningType) {

		FieldImpl field = readFieldTypeDefinition(fieldDef);
		field.setOwningType(owningType);

		// Let's read all its input parameters
		field.setInputParameters(fieldDef.getInputValueDefinitions().stream().map(this::readFieldTypeDefinition)
				.collect(Collectors.toList()));

		// Let's store its comments
		field.setComments(fieldDef.getComments());

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

		field.setName((String) graphqlUtils.invokeMethod("getName", fieldDef));
		field.setAppliedDirectives(readAppliedDirectives(
				(List<graphql.language.Directive>) graphqlUtils.invokeMethod("getDirectives", fieldDef)));

		field.setFieldTypeAST(readFieldTypeAST(graphqlUtils.invokeMethod("getType", fieldDef)));

		// For InputValueDefinition, we may have a default value
		if (fieldDef instanceof InputValueDefinition) {
			field.setDefaultValue(((InputValueDefinition) fieldDef).getDefaultValue());
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
		return ((GenerateCodeCommonConfiguration) configuration).getPackageName() + "." + name;
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
		Type ret = types.get(typeName);
		if (throwExceptionIfNotFound && ret == null)
			throw new RuntimeException("The type named '" + typeName + "' could not be found");
		return ret;
	}

	/**
	 * For each interface, identify the list of object types which implements it. This is done last, when all objects
	 * has been read by the plugin. .
	 * 
	 * @see InterfaceType#getImplementingTypes()
	 */
	void initListOfInterfaceImplementations() {
		for (InterfaceType interfaceType : interfaceTypes) {
			Stream.concat(objectTypes.stream(), interfaceTypes.stream()).forEach((o) -> {
				if (o.getImplementz().contains(interfaceType.getName())) {
					// This object implements the current interface we're looping in.
					interfaceType.getImplementingTypes().add(o);
				}
			});
		} // for
	}

	/**
	 * Returns the name of the package for utility classes.<BR/>
	 * In this class, it always return the result of {@link CommonConfiguration#getPackageName()}
	 * 
	 * @return
	 */
	protected String getUtilPackageName() {
		return ((GenerateCodeCommonConfiguration) configuration).getPackageName();
	}

}