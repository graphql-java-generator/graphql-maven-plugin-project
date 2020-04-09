/**
 * 
 */
package com.graphql_java_generator.plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.persistence.Entity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.graphql_java_generator.GraphqlUtils;
import com.graphql_java_generator.annotation.GraphQLInputType;
import com.graphql_java_generator.annotation.GraphQLNonScalar;
import com.graphql_java_generator.annotation.GraphQLQuery;
import com.graphql_java_generator.annotation.GraphQLScalar;
import com.graphql_java_generator.annotation.RequestType;
import com.graphql_java_generator.plugin.language.AppliedDirective;
import com.graphql_java_generator.plugin.language.BatchLoader;
import com.graphql_java_generator.plugin.language.DataFetcher;
import com.graphql_java_generator.plugin.language.DataFetchersDelegate;
import com.graphql_java_generator.plugin.language.Directive;
import com.graphql_java_generator.plugin.language.DirectiveLocation;
import com.graphql_java_generator.plugin.language.EnumValue;
import com.graphql_java_generator.plugin.language.Field;
import com.graphql_java_generator.plugin.language.Relation;
import com.graphql_java_generator.plugin.language.RelationType;
import com.graphql_java_generator.plugin.language.Type;
import com.graphql_java_generator.plugin.language.Type.GraphQlType;
import com.graphql_java_generator.plugin.language.impl.AbstractType;
import com.graphql_java_generator.plugin.language.impl.AppliedDirectiveImpl;
import com.graphql_java_generator.plugin.language.impl.BatchLoaderImpl;
import com.graphql_java_generator.plugin.language.impl.CustomScalarType;
import com.graphql_java_generator.plugin.language.impl.DataFetcherImpl;
import com.graphql_java_generator.plugin.language.impl.DataFetchersDelegateImpl;
import com.graphql_java_generator.plugin.language.impl.DirectiveImpl;
import com.graphql_java_generator.plugin.language.impl.EnumType;
import com.graphql_java_generator.plugin.language.impl.EnumValueImpl;
import com.graphql_java_generator.plugin.language.impl.FieldImpl;
import com.graphql_java_generator.plugin.language.impl.InterfaceType;
import com.graphql_java_generator.plugin.language.impl.ObjectType;
import com.graphql_java_generator.plugin.language.impl.RelationImpl;
import com.graphql_java_generator.plugin.language.impl.ScalarType;
import com.graphql_java_generator.plugin.language.impl.UnionType;
import com.graphql_java_generator.plugin.schema_personalization.JsonSchemaPersonalization;

import graphql.language.AbstractNode;
import graphql.language.Argument;
import graphql.language.ArrayValue;
import graphql.language.BooleanValue;
import graphql.language.Definition;
import graphql.language.DirectiveDefinition;
import graphql.language.Document;
import graphql.language.EnumTypeDefinition;
import graphql.language.EnumValueDefinition;
import graphql.language.FieldDefinition;
import graphql.language.FloatValue;
import graphql.language.InputObjectTypeDefinition;
import graphql.language.InputValueDefinition;
import graphql.language.IntValue;
import graphql.language.InterfaceTypeDefinition;
import graphql.language.ListType;
import graphql.language.Node;
import graphql.language.NonNullType;
import graphql.language.NullValue;
import graphql.language.ObjectTypeDefinition;
import graphql.language.OperationTypeDefinition;
import graphql.language.ScalarTypeDefinition;
import graphql.language.SchemaDefinition;
import graphql.language.StringValue;
import graphql.language.TypeName;
import graphql.language.UnionTypeDefinition;
import graphql.language.Value;
import graphql.parser.Parser;
import lombok.Getter;

/**
 * This class generates the Java classes, from the documents. These documents are read from the
 * graphql-spring-boot-starter code, in injected here thanks to spring's magic.<BR/>
 * There is no validity check: we trust the information in the Document, as it is read by the GraphQL {@link Parser}.
 * <BR/>
 * The graphQL-java library maps both FieldDefinition and InputValueDefinition in very similar structures, which are
 * actually trees. These structures are too hard too read in a Velocity template, and we need to parse down to a
 * properly structures way for that.
 * 
 * @author EtienneSF
 */
@Component
@Getter
public class DocumentParser {

	private static final String INTROSPECTION_QUERY = "__IntrospectionQuery";

	final String DEFAULT_QUERY_NAME = "Query";
	final String DEFAULT_MUTATION_NAME = "Mutation";
	final String DEFAULT_SUBSCRIPTION_NAME = "Subscription";

	/**
	 * This instance is responsible for providing all the configuration parameter from the project (Maven, Gradle...)
	 */
	@Autowired
	PluginConfiguration pluginConfiguration;

	@Autowired
	GraphqlUtils graphqlUtils;

	/////////////////////////////////////////////////////////////////////////////////////////////
	// Internal attributes for this class

	/**
	 * This Spring Bean is created by {@link SpringConfiguration}. The {@link ResourceSchemaStringProvider} adds the
	 * introspection schema into the documents list.<BR/>
	 * See also the {@link #addIntrospectionCapabilities()} that finalize the introspection capabilities for the
	 * generated code.
	 */
	@Autowired
	List<Document> documents;

	/**
	 * The {@link JsonSchemaPersonalization} allows the user to update what the plugin would have generate, through a
	 * json configuration file
	 */
	@Autowired
	JsonSchemaPersonalization jsonSchemaPersonalization;

	/** List of all the directives that have been read in the GraphQL schema */
	@Getter
	List<Directive> directives = new ArrayList<>();

	/**
	 * All the Query Types for this Document. There may be several ones, if more than one GraphQLs files have been
	 * merged
	 */
	@Getter
	List<ObjectType> queryTypes = new ArrayList<>();

	/**
	 * All the Subscription Types for this Document. There may be several ones, if more than one GraphQLs files have
	 * been merged
	 */
	@Getter
	List<ObjectType> subscriptionTypes = new ArrayList<>();

	/**
	 * All the Mutation Types for this Document. There may be several ones, if more than one GraphQLs files have been
	 * merged
	 */
	@Getter
	List<ObjectType> mutationTypes = new ArrayList<>();

	/**
	 * All the {@link ObjectType} which have been read during the reading of the documents
	 */
	@Getter
	List<ObjectType> objectTypes = new ArrayList<>();

	/**
	 * All the {@link InterfaceTypeDefinition} which have been read during the reading of the documents
	 */
	@Getter
	List<InterfaceType> interfaceTypes = new ArrayList<>();

	/**
	 * All the {@link UnionTypeDefinition} which have been read during the reading of the documents
	 */
	@Getter
	List<UnionType> unionTypes = new ArrayList<>();

	/** All the {@link ObjectType} which have been read during the reading of the documents */
	@Getter
	List<EnumType> enumTypes = new ArrayList<>();

	/**
	 * maps for all scalers, when they are mandatory. The key is the type name. The value is the class to use in the
	 * java code
	 */
	List<ScalarType> scalarTypes = new ArrayList<>();

	/** All the {@link CustomScalarType} which have been read during the reading of the documents */
	List<CustomScalarType> customScalars = new ArrayList<>();

	/** All the {@link Type}s that have been parsed, added by the default scalars */
	Map<String, com.graphql_java_generator.plugin.language.Type> types = new HashMap<>();

	/** All {@link Relation}s that have been found in the GraphQL schema(s) */
	List<Relation> relations = new ArrayList<>();

	/**
	 * All {@link DataFetcher}s that need to be implemented for this/these schema/schemas
	 */
	List<DataFetcher> dataFetchers = new ArrayList<>();

	/**
	 * All {@link DataFetchersDelegate}s that need to be implemented for this/these schema/schemas
	 */
	List<DataFetchersDelegate> dataFetchersDelegates = new ArrayList<>();

	/**
	 * All {@link BatchLoader}s that need to be implemented for this/these schema/schemas
	 */
	List<BatchLoader> batchLoaders = new ArrayList<>();

	@PostConstruct
	public void postConstruct() {

		pluginConfiguration.getLog().debug("Starting DocumentParser's PostConstrut intialization");

		//////////////////////////////////////////////////////////////////////////////////////////
		// Add of all GraphQL standard scalars

		// In client mode, ID type is managed as a String
		if (pluginConfiguration.getMode().equals(PluginMode.server))
			scalarTypes.add(new ScalarType("ID", "java.util", "UUID", pluginConfiguration.getMode()));
		else
			scalarTypes.add(new ScalarType("ID", "java.lang", "String", pluginConfiguration.getMode()));

		scalarTypes.add(new ScalarType("String", "java.lang", "String", pluginConfiguration.getMode()));

		// It seems that both boolean&Boolean, int&Int, float&Float are accepted.
		scalarTypes.add(new ScalarType("boolean", "java.lang", "Boolean", pluginConfiguration.getMode()));
		scalarTypes.add(new ScalarType("Boolean", "java.lang", "Boolean", pluginConfiguration.getMode()));
		scalarTypes.add(new ScalarType("int", "java.lang", "Integer", pluginConfiguration.getMode()));
		scalarTypes.add(new ScalarType("Int", "java.lang", "Integer", pluginConfiguration.getMode()));
		scalarTypes.add(new ScalarType("Float", "java.lang", "Float", pluginConfiguration.getMode()));
		scalarTypes.add(new ScalarType("float", "java.lang", "Float", pluginConfiguration.getMode()));

		//////////////////////////////////////////////////////////////////////////////////////////
		// Add of all GraphQL custom scalar implementations must be provided by the plugin configuration
		pluginConfiguration.getLog().debug("Storing custom scalar's implementations [START]");
		if (pluginConfiguration.getCustomScalars() != null) {
			for (CustomScalarDefinition customScalarDef : pluginConfiguration.getCustomScalars()) {
				String className = customScalarDef.getJavaType();
				int lstPointPosition = className.lastIndexOf('.');
				String packageName = className.substring(0, lstPointPosition);
				String simpleClassName = className.substring(lstPointPosition + 1);

				CustomScalarType type = new CustomScalarType(customScalarDef.getGraphQLTypeName(), packageName,
						simpleClassName, customScalarDef.getGraphQLScalarTypeClass(),
						customScalarDef.getGraphQLScalarTypeStaticField(), customScalarDef.getGraphQLScalarTypeGetter(),
						pluginConfiguration.getMode());
				customScalars.add(type);
				types.put(type.getName(), type);
			}
		}
		pluginConfiguration.getLog().debug("Storing custom scalar's implementations [END]");

		//////////////////////////////////////////////////////////////////////////////////////////
		// Add of all GraphQL standard directives
		DirectiveImpl skip = new DirectiveImpl();
		skip.setName("skip");
		skip.getArguments().add(FieldImpl.builder().name("if").graphQLTypeName("Boolean").mandatory(true).build());
		skip.getDirectiveLocations().add(DirectiveLocation.FIELD);
		skip.getDirectiveLocations().add(DirectiveLocation.FRAGMENT_SPREAD);
		skip.getDirectiveLocations().add(DirectiveLocation.INLINE_FRAGMENT);
		directives.add(skip);
		//
		DirectiveImpl include = new DirectiveImpl();
		include.setName("include");
		include.getArguments().add(FieldImpl.builder().name("if").graphQLTypeName("Boolean").mandatory(true).build());
		include.getDirectiveLocations().add(DirectiveLocation.FIELD);
		include.getDirectiveLocations().add(DirectiveLocation.FRAGMENT_SPREAD);
		include.getDirectiveLocations().add(DirectiveLocation.INLINE_FRAGMENT);
		directives.add(include);
		//
		DirectiveImpl defer = new DirectiveImpl();
		defer.setName("defer");
		defer.getArguments().add(FieldImpl.builder().name("if").graphQLTypeName("Boolean").mandatory(true).build());
		defer.getDirectiveLocations().add(DirectiveLocation.FIELD);
		directives.add(defer);
		//
		DirectiveImpl deprecated = new DirectiveImpl();
		deprecated.setName("deprecated");
		// deprecated.getArguments().add(FieldImpl.builder().name("reason").graphQLTypeName("String")
		// .defaultValue("No longer supported").build());
		deprecated.getDirectiveLocations().add(DirectiveLocation.FIELD_DEFINITION);
		deprecated.getDirectiveLocations().add(DirectiveLocation.ENUM_VALUE);
		directives.add(deprecated);

		pluginConfiguration.getLog().debug("Finished DocumentParser's PostConstrut intialization");

	}

	/**
	 * The main method of the class: it graphqlUtils.executes the generation of the given documents
	 * 
	 * @param documents
	 *            The GraphQL definition schema, from which the code is to be generated
	 * @return
	 */
	public int parseDocuments() {
		pluginConfiguration.getLog().debug("Starting documents parsing");

		int nbClasses = documents.stream().mapToInt(this::parseOneDocument).sum();

		pluginConfiguration.getLog().debug("Documents have been parsed. Executing internal finalizations");

		// Let's finalize some "details":

		// Add introspection capabilities (the introspection schema has already been read, as it is added by
		// ResourceSchemaStringProvider in the documents list
		pluginConfiguration.getLog().debug("Adding introspection capabilities");
		addIntrospectionCapabilities();
		// Init the list of the object implementing each interface. This is done last, when all objects has been read by
		// the plugin.
		pluginConfiguration.getLog().debug("Init list of interface implementations");
		initListOfInterfaceImplementations();
		// The types Map allows to retrieve easily a Type from its name
		pluginConfiguration.getLog().debug("Fill type map");
		fillTypesMap();
		// Let's identify every relation between objects, interface or union in the model
		pluginConfiguration.getLog().debug("Init relations");
		initRelations();
		// Some annotations are needed for Jackson or JPA
		pluginConfiguration.getLog().debug("Add annotations");
		addAnnotations();
		// List all data fetchers
		pluginConfiguration.getLog().debug("Init data fetchers");
		initDataFetchers();
		// List all Batch Loaders
		pluginConfiguration.getLog().debug("Init batch loaders");
		initBatchLoaders();

		// Apply the user's schema personalization
		pluginConfiguration.getLog().debug("Apply schema personalization");
		jsonSchemaPersonalization.applySchemaPersonalization();

		return nbClasses;
	}

	/**
	 * Generates the target classes for the given GraphQL schema definition
	 * 
	 * @param document
	 */
	int parseOneDocument(Document document) {
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
		pluginConfiguration.getLog().debug("Looking for schema definition");
		for (Definition<?> node : document.getDefinitions()) {
			if (node instanceof SchemaDefinition) {
				readSchemaDefinition((SchemaDefinition) node, queryObjectNames, mutationObjectNames,
						subscriptionObjectNames);
			} // if
		} // for

		pluginConfiguration.getLog().debug("Reading node definitions");
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
			// object
			if (node instanceof ObjectTypeDefinition) {
				// Let's check what kind of ObjectDefinition we have
				String name = ((ObjectTypeDefinition) node).getName();
				if (queryObjectNames.contains(name) || DEFAULT_QUERY_NAME.equals(name)) {
					ObjectType query = readObjectType((ObjectTypeDefinition) node);
					query.setRequestType("query");
					queryTypes.add(query);
				} else if (mutationObjectNames.contains(name) || DEFAULT_MUTATION_NAME.equals(name)) {
					ObjectType mutation = readObjectType((ObjectTypeDefinition) node);
					mutation.setRequestType("mutation");
					mutationTypes.add(mutation);
				} else if (subscriptionObjectNames.contains(name) || DEFAULT_SUBSCRIPTION_NAME.equals(name)) {
					ObjectType subscription = readObjectType((ObjectTypeDefinition) node);
					subscription.setRequestType("subscription");
					subscriptionTypes.add(subscription);
				} else {
					objectTypes.add(readObjectType((ObjectTypeDefinition) node));
				}
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
				pluginConfiguration.getLog().warn("Non managed node type: " + node.getClass().getName());
			}
		} // for

		// Once all Types have been properly read, we can read the union types
		pluginConfiguration.getLog().debug("Reading union definitions");
		document.getDefinitions().stream().filter(n -> (n instanceof UnionTypeDefinition))
				.forEach(n -> unionTypes.add(readUnionType((UnionTypeDefinition) n)));

		// We're done
		return queryTypes.size() + subscriptionTypes.size() + mutationTypes.size() + objectTypes.size()
				+ enumTypes.size() + interfaceTypes.size();

	}

	/**
	 * Fill the {@link #types} map, from all the types (object, interface, enum, scalars) that are valid for this
	 * schema. This allow to get the properties from their type, as only their type's name is known when parsing the
	 * schema.
	 */
	void fillTypesMap() {
		// Directive are directly added to the types map.
		// TODO remove this method, and add each type in the types map as it is read
		queryTypes.stream().forEach(q -> types.put(q.getName(), q));
		mutationTypes.stream().forEach(m -> types.put(m.getName(), m));
		subscriptionTypes.stream().forEach(s -> types.put(s.getName(), s));
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
	Directive readDirectiveDefinition(DirectiveDefinition node) {
		DirectiveImpl directive = new DirectiveImpl();

		directive.setName(node.getName());

		// Let's read all its input parameters
		directive.setArguments(node.getInputValueDefinitions().stream().map(this::readFieldTypeDefinition)
				.collect(Collectors.toList()));

		// and all its locations
		for (graphql.language.DirectiveLocation dl : node.getDirectiveLocations()) {
			DirectiveLocation dirLoc = DirectiveLocation.valueOf(DirectiveLocation.class, dl.getName());
			directive.getDirectiveLocations().add(dirLoc);
		}

		return directive;
	}

	private Directive getDirectiveDefinition(String name) {
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
	void readSchemaDefinition(SchemaDefinition schemaDef, List<String> queryObjectNames,
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
	@SuppressWarnings("rawtypes")
	ObjectType readObjectType(ObjectTypeDefinition node) {
		// Let's check if it's a real object, or part of a schema (query, subscription,
		// mutation) definition

		ObjectType objectType = new ObjectType(node.getName(), pluginConfiguration.getPackageName(),
				pluginConfiguration.getMode());

		objectType.setAppliedDirectives(readAppliedDirectives(node.getDirectives()));

		// Let's read all its fields
		objectType.setFields(node.getFieldDefinitions().stream().map(def -> readField(def, objectType))
				.collect(Collectors.toList()));

		// Let's read all the other object types that this one implements
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

		ObjectType objectType = new ObjectType(node.getName(), pluginConfiguration.getPackageName(),
				pluginConfiguration.getMode());
		objectType.setInputType(true);

		objectType.setAppliedDirectives(readAppliedDirectives(node.getDirectives()));

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
	InterfaceType readInterfaceType(InterfaceTypeDefinition node) {
		// Let's check if it's a real object, or part of a schema (query, subscription,
		// mutation) definition

		InterfaceType interfaceType = new InterfaceType(node.getName(), pluginConfiguration.getPackageName(),
				pluginConfiguration.getMode());

		interfaceType.setAppliedDirectives(readAppliedDirectives(node.getDirectives()));

		// Let's read all its fields
		interfaceType.setFields(node.getFieldDefinitions().stream().map(def -> readField(def, interfaceType))
				.collect(Collectors.toList()));

		return interfaceType;
	}

	/**
	 * Read an union type from its GraphQL definition
	 * 
	 * @param node
	 * @return
	 */
	UnionType readUnionType(UnionTypeDefinition node) {
		// Let's check if it's a real object, or part of a schema (query, subscription,
		// mutation) definition

		UnionType unionType = new UnionType(node.getName(), pluginConfiguration.getPackageName(),
				pluginConfiguration.getMode());
		unionType.setAppliedDirectives(readAppliedDirectives(node.getDirectives()));

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
	CustomScalarType readCustomScalarType(ScalarTypeDefinition node) {
		String name = node.getName();

		for (CustomScalarType customScalarType : customScalars) {
			if (customScalarType.getName().equals(name)) {
				customScalarType.setAppliedDirectives(readAppliedDirectives(node.getDirectives()));
				return customScalarType;
			}
		}

		throw new RuntimeException(
				"The plugin configuration must provide an implementation for the Custom Scalar '" + name + "'.");
	}

	/**
	 * Reads an enum definition, and create the relevant {@link EnumType}
	 * 
	 * @param node
	 * @return
	 */
	EnumType readEnumType(EnumTypeDefinition node) {
		EnumType enumType = new EnumType(node.getName(), pluginConfiguration.getPackageName(),
				pluginConfiguration.getMode());

		enumType.setAppliedDirectives(readAppliedDirectives(node.getDirectives()));

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

		// Let's default value to false
		field.setMandatory(false);
		field.setList(false);
		field.setItemMandatory(false);

		TypeName typeName = null;
		if (graphqlUtils.invokeMethod("getType", fieldDef) instanceof TypeName) {
			typeName = (TypeName) graphqlUtils.invokeMethod("getType", fieldDef);
		} else if (graphqlUtils.invokeMethod("getType", fieldDef) instanceof NonNullType) {
			field.setMandatory(true);
			Node<?> node = ((NonNullType) graphqlUtils.invokeMethod("getType", fieldDef)).getType();
			if (node instanceof TypeName) {
				typeName = (TypeName) node;
			} else if (node instanceof ListType) {
				Node<?> subNode = ((ListType) node).getType();
				field.setList(true);
				if (subNode instanceof TypeName) {
					typeName = (TypeName) subNode;
				} else if (subNode instanceof NonNullType) {
					typeName = (TypeName) ((NonNullType) subNode).getType();
					field.setItemMandatory(true);
				} else {
					throw new RuntimeException("Case not found (subnode of a ListType). The node is of type "
							+ subNode.getClass().getName() + " (for field " + field.getName() + ")");
				}
			} else {
				throw new RuntimeException("Case not found (subnode of a NonNullType). The node is of type "
						+ node.getClass().getName() + " (for field " + field.getName() + ")");
			}
		} else if (graphqlUtils.invokeMethod("getType", fieldDef) instanceof ListType) {
			field.setList(true);
			Node<?> node = ((ListType) graphqlUtils.invokeMethod("getType", fieldDef)).getType();
			if (node instanceof TypeName) {
				typeName = (TypeName) node;
			} else if (node instanceof NonNullType) {
				typeName = (TypeName) ((NonNullType) node).getType();
				field.setItemMandatory(true);
			} else {
				throw new RuntimeException("Case not found (subnode of a ListType). The node is of type "
						+ node.getClass().getName() + " (for field " + field.getName() + ")");
			}
		}

		// We have the type. But we may not have parsed it yet. So we just write its
		// name. And will get the
		// com.graphql_java_generator.plugin.language.Type when generating the code.
		field.setGraphQLTypeName(typeName.getName());

		// For InputValueDefinition, we may have a default value
		if (fieldDef instanceof InputValueDefinition) {
			field.setDefaultValue(((InputValueDefinition) fieldDef).getDefaultValue());
		}

		return field;

	}

	/**
	 * A utility method, which maps an object type to the class full name of the Java class which will be generated for
	 * this object type. This utility method is based on the {@link PluginConfiguration#getPackageName()} plugin
	 * attribute, available in this class
	 * 
	 * @param name
	 */
	String getGeneratedFieldFullClassName(String name) {
		return pluginConfiguration.getPackageName() + "." + name;
	}

	/**
	 * Returns the type for the given name
	 * 
	 * @param typeName
	 * @return
	 * @throws RuntimeException
	 *             if the type could not be found
	 */
	public Type getType(String typeName) {
		Type ret = types.get(typeName);
		if (ret == null)
			throw new RuntimeException("The type named '" + typeName + "' could not be found");
		return ret;
	}

	/**
	 * Returns the {@link DataFetchersDelegate} that manages the given type.
	 * 
	 * @param type
	 *            The type, for which the DataFetchersDelegate is searched. It may not be null.
	 * @param createIfNotExists
	 *            if true: a new DataFetchersDelegate is created when there is no {@link DataFetchersDelegate} for this
	 *            type yet. If false: no DataFetchersDelegate creation.
	 * @return The relevant DataFetchersDelegate, or null of there is no DataFetchersDelegate for this type and
	 *         createIfNotExists is false
	 * @throws NullPointerException
	 *             If type is null
	 */
	public DataFetchersDelegate getDataFetchersDelegate(Type type, boolean createIfNotExists) {
		if (type == null) {
			throw new NullPointerException("type may not be null");
		}

		for (DataFetchersDelegate dfd : dataFetchersDelegates) {
			if (dfd.getType().equals(type)) {
				return dfd;
			}
		}

		// No DataFetchersDelegate for this type exists yet
		if (createIfNotExists) {
			DataFetchersDelegate dfd = new DataFetchersDelegateImpl(type);
			dataFetchersDelegates.add(dfd);
			return dfd;
		} else {
			return null;
		}
	}

	/**
	 * Reads all the GraphQl objects, interfaces, union... that have been read from the GraphQL schema, and list all the
	 * relations between Server objects (that is: all objects out of the Query/Mutation/Subscription types and the input
	 * types). The found relations are stored, to be reused during the code generation.<BR/>
	 * These relations are important for the server mode of the plugin, to generate the proper JPA annotations.
	 */
	void initRelations() {
		for (Type type : getObjectTypes()) {
			if (!type.isInputType()) {
				for (Field field : type.getFields()) {
					if (field.getType() instanceof ObjectType) {
						RelationType relType = field.isList() ? RelationType.OneToMany : RelationType.ManyToOne;
						RelationImpl relation = new RelationImpl(type, field, relType);
						//
						((FieldImpl) field).setRelation(relation);
						relations.add(relation);
					} // if (instanceof ObjectType)
				} // if (!type.isInputType())
			} // for (field)
		} // for (type)
	}

	/**
	 * Defines the annotation for each field of the read objects and interfaces. For the client mode, this is
	 * essentially the Jackson annotations, to allow deserialization of the server response, into the generated classes.
	 * For the server mode, this is essentially the JPA annotations, to define the interaction with the database,
	 * through Spring Data
	 */
	void addAnnotations() {
		// No annotation for types.
		// We go through each field of each type we generate, to define the relevant
		// annotation
		switch (pluginConfiguration.getMode()) {
		case client:
			// Type annotations
			interfaceTypes.stream().forEach(o -> addTypeAnnotationForClientMode(o));
			objectTypes.stream().forEach(o -> addTypeAnnotationForClientMode(o));
			unionTypes.stream().forEach(o -> addTypeAnnotationForClientMode(o));

			// Add the GraphQLQuery annotation
			queryTypes.parallelStream().forEach(f -> addQueryAnnotationForClientMode(f, RequestType.query));
			mutationTypes.stream().forEach(f -> addQueryAnnotationForClientMode(f, RequestType.mutation));
			subscriptionTypes.forEach(f -> addQueryAnnotationForClientMode(f, RequestType.subscription));

			// Field annotations
			objectTypes.stream().flatMap(o -> o.getFields().stream()).forEach(f -> addFieldAnnotationForClientMode(f));
			interfaceTypes.stream().flatMap(o -> o.getFields().stream())
					.forEach(f -> addFieldAnnotationForClientMode(f));
			queryTypes.parallelStream().flatMap(o -> o.getFields().stream())
					.forEach(f -> addFieldAnnotationForClientMode(f));
			mutationTypes.stream().flatMap(o -> o.getFields().stream())
					.forEach(f -> addFieldAnnotationForClientMode(f));
			subscriptionTypes.stream().flatMap(o -> o.getFields().stream())
					.forEach(f -> addFieldAnnotationForClientMode(f));

			break;
		case server:
			Stream.concat(objectTypes.stream(), interfaceTypes.stream())
					.forEach(o -> addTypeAnnotationForServerMode(o));
			Stream.concat(objectTypes.stream(), interfaceTypes.stream()).flatMap(o -> o.getFields().stream())
					.forEach(f -> addFieldAnnotationForServerMode(f));
			break;
		}

	}

	/**
	 * Add the {@link GraphQLQuery} annotation to the given query/mutation/subscription
	 * 
	 * @param f
	 *            The query/mutation/subscription, for the annotation must be added.
	 * @param type
	 *            The kind of request
	 */
	private void addQueryAnnotationForClientMode(ObjectType f, RequestType type) {
		f.addAnnotation("@GraphQLQuery(name = \"" + f.getName() + "\", type = RequestType." + type.name() + ")");
	}

	/**
	 * This method add the needed annotation(s) to the given type, when in client mode
	 * 
	 * @param o
	 */
	void addTypeAnnotationForClientMode(Type o) {
		// No specific annotation for objects and interfaces when in client mode.

		if (o instanceof InterfaceType || o instanceof UnionType) {
			o.addAnnotation(
					"@JsonTypeInfo(use = Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = \"__typename\", visible = true)");

			// jsonSubTypes annotation looks like this:
			// @JsonSubTypes({ @Type(value = Droid.class, name = "Droid"), @Type(value = Human.class, name = "Human") })
			StringBuffer jsonSubTypes = new StringBuffer();
			jsonSubTypes.append("@JsonSubTypes({");
			boolean addSeparator = false;

			List<ObjectType> types;
			if (o instanceof InterfaceType)
				types = ((InterfaceType) o).getImplementingTypes();
			else
				types = ((UnionType) o).getMemberTypes();

			for (ObjectType type : types) {
				// No separator for the first iteration
				if (addSeparator)
					jsonSubTypes.append(",");
				else
					addSeparator = true;
				jsonSubTypes.append(" @Type(value = ").append(type.getName()).append(".class, name = \"")
						.append(type.getName()).append("\")");
			}
			jsonSubTypes.append(" })");

			o.addAnnotation(jsonSubTypes.toString());
		}

		// Let's add the annotations, that are common to both the client and the server mode
		addTypeAnnotationForBothClientAndServerMode(o);
	}

	/**
	 * This method add the needed annotation(s) to the given type when in server mode. This typically add the
	 * JPA @{@link Entity} annotation.
	 * 
	 * @param o
	 */
	void addTypeAnnotationForServerMode(Type o) {

		if (!o.isInputType()) {
			if (pluginConfiguration.getGenerateJPAAnnotation() && o instanceof ObjectType
					&& !(o instanceof InterfaceType)) {
				((AbstractType) o).addAnnotation("@Entity");
			}
		}

		// Let's add the annotations, that are common to both the client and the server mode
		addTypeAnnotationForBothClientAndServerMode(o);
	}

	/**
	 * This method add the needed annotation(s) to the given type when in server mode. This typically add
	 * the @{@link GraphQLInputType} annotation.
	 * 
	 * @param o
	 */
	private void addTypeAnnotationForBothClientAndServerMode(Type o) {
		if (o instanceof InterfaceType) {
			o.addAnnotation("@GraphQLInterfaceType(\"" + o.getName() + "\")");
		} else if (o instanceof UnionType) {
			o.addAnnotation("@GraphQLUnionType(\"" + o.getName() + "\")");
		} else if (o instanceof ObjectType) {
			if (((ObjectType) o).isInputType()) {
				// input type
				o.addAnnotation("@GraphQLInputType(\"" + o.getName() + "\")");
			} else {
				// Standard object type
				o.addAnnotation("@GraphQLObjectType(\"" + o.getName() + "\")");
			}
		}
	}

	/**
	 * This method add the needed annotation(s) to the given field. It should be called when the maven plugin is in
	 * client mode. This typically add the Jackson annotation, to allow the desialization of the GraphQL server
	 * response.
	 * 
	 * @param field
	 */
	void addFieldAnnotationForClientMode(Field field) {
		// No json field annotation for interfaces or unions. The json annotation is directly on the interface or union
		// type.
		// if (!(field.getType() instanceof InterfaceType) && !(field.getType() instanceof UnionType)) {
		String contentAs = null;
		String using = null;
		if (field.isList()) {
			contentAs = field.getType().getConcreteClassSimpleName() + ".class";
		}
		if (field.getType().isCustomScalar()) {
			using = "CustomScalarDeserializer" + field.getType().getName() + ".class";
		}
		if (contentAs != null || using != null) {
			((FieldImpl) field).addAnnotation(buildJsonDeserializeAnnotation(contentAs, using));
		}
		// }

		addFieldAnnotationForBothClientAndServerMode(field);
	}

	/**
	 * This method add the needed annotation(s) to the given field. It should be called when the maven plugin is in
	 * server mode. This typically add the JPA @Id, @GeneratedValue, @Transient annotations.
	 * 
	 * @param field
	 */
	void addFieldAnnotationForServerMode(Field field) {
		if (pluginConfiguration.getGenerateJPAAnnotation() && !field.getOwningType().isInputType()) {
			if (field.isId()) {
				// We have found the identifier
				((FieldImpl) field).addAnnotation("@Id");
				((FieldImpl) field).addAnnotation("@GeneratedValue");
			} else if (field.getRelation() != null || field.isList()) {
				// We prevent JPA to manage the relations: we want the GraphQL Data Fetchers to do it, instead.
				((FieldImpl) field).addAnnotation("@Transient");
			}
		}

		addFieldAnnotationForBothClientAndServerMode(field);
	}

	/**
	 * This method add the annotation(s) that are common to the server and the client mode, to the given field. It
	 * typically adds the {@link GraphQLScalar} and {@link GraphQLNonScalar} annotations, to allow runtime management of
	 * the generated code.
	 * 
	 * @param field
	 */
	void addFieldAnnotationForBothClientAndServerMode(Field field) {
		if (field.getType() instanceof ScalarType || field.getType() instanceof EnumType) {
			((FieldImpl) field).addAnnotation("@GraphQLScalar(fieldName = \"" + field.getName()
					+ "\", graphQLTypeName = \"" + field.getGraphQLTypeName() + "\", javaClass = "
					+ field.getType().getClassSimpleName() + ".class)");
		} else {
			((FieldImpl) field).addAnnotation("@GraphQLNonScalar(fieldName = \"" + field.getName()
					+ "\", graphQLTypeName = \"" + field.getGraphQLTypeName() + "\", javaClass = "
					+ field.getType().getClassSimpleName() + ".class)");
		}
	}

	/**
	 * Identified all the GraphQL Data Fetchers needed from this/these schema/schemas
	 */
	void initDataFetchers() {
		if (pluginConfiguration.getMode().equals(PluginMode.server)) {
			queryTypes.stream().forEach(o -> initDataFetcherForOneObject(o, true));
			mutationTypes.stream().forEach(o -> initDataFetcherForOneObject(o, true));
			objectTypes.stream().forEach(o -> initDataFetcherForOneObject(o, false));
			interfaceTypes.stream().forEach(o -> initDataFetcherForOneObject(o, false));
		}
	}

	/**
	 * Identified all the GraphQL Data Fetchers needed for this type
	 *
	 * @param type
	 * @param isQueryOrMutationType
	 *            true if the given type is actually a query, false otherwise
	 */
	void initDataFetcherForOneObject(ObjectType type, boolean isQueryOrMutationType) {

		// No DataFetcher for :
		// 1) the "artificial" Object Type created to instanciate an Interface. This "artificial" Object
		// Type is for internal usage only, and to be used in Client mode to allow instanciation of the server response
		// interface object. It doesn't exist in the GraphQL Schema. Thus, it must have no DataFetchersDelegate.
		// 2) the input type
		if (type.getDefaultImplementationForInterface() == null && !type.isInputType()) {

			// Creation of the DataFetchersDelegate. It will be added to the list only if it contains at least one
			// DataFetcher.
			DataFetchersDelegate dataFetcherDelegate = new DataFetchersDelegateImpl(type);

			for (Field field : type.getFields()) {
				DataFetcherImpl dataFetcher = null;

				if (isQueryOrMutationType) {
					// For queries and field that are lists, we take the argument read in the schema
					// as is: all the needed
					// informations is already parsed.
					dataFetcher = new DataFetcherImpl(field, false);
				} else if (((type instanceof ObjectType || type instanceof InterfaceType) && //
						(field.isList() || field.getType() instanceof ObjectType
								|| field.getType() instanceof InterfaceType))) {
					// For Objects and Interfaces, we need to add a specific data fetcher. The objective there is to
					// manage
					// the relations with GraphQL, and not via JPA. The aim is to use the GraphQL data loader : very
					// important to limit the number of subqueries, when subobjects are queried.
					// In these case, we need to create a new field that add the object ID as a parameter of the Data
					// Fetcher
					FieldImpl newField = FieldImpl.builder().documentParser(this).name(field.getName())
							.list(field.isList()).owningType(field.getOwningType())
							.graphQLTypeName(field.getGraphQLTypeName()).build();

					// Let's add the id for the owning type of the field, then all its input parameters
					for (Field inputParameter : field.getInputParameters()) {
						List<Field> list = newField.getInputParameters();
						list.add(inputParameter);
					}

					// We'll use a Batch Loader if:
					// 1) It's a Data Fetcher from an object to another one (we're already in this case)
					// 2) That target object has an id (it can be either a list or a single object)
					// 3) The Relation toward the target object is OneToOne or ManyToOne. That is this field is not a
					// list
					boolean useBatchLoader = (field.getType().getIdentifier() != null) && (!field.isList());

					dataFetcher = new DataFetcherImpl(newField, useBatchLoader);
					dataFetcher.setSourceName(type.getName());
				}

				// If we found a DataFether, let's register it.
				if (dataFetcher != null) {
					dataFetcher.setDataFetcherDelegate(dataFetcherDelegate);
					dataFetcherDelegate.getDataFetchers().add(dataFetcher);
					dataFetchers.add(dataFetcher);
				}
			} // for

			// If at least one DataFetcher has been created, we register this
			// DataFetchersDelegate
			if (dataFetcherDelegate.getDataFetchers().size() > 0) {
				dataFetchersDelegates.add(dataFetcherDelegate);
			}
		}
	}

	/**
	 * Identify each BatchLoader to generate, and attach its {@link DataFetcher} to its {@link DataFetchersDelegate}.
	 * The whole stuff is stored into {@link #batchLoaders}
	 */
	private void initBatchLoaders() {
		if (pluginConfiguration.getMode().equals(PluginMode.server)) {
			// objectTypes contains both the objects defined in the schema, and the concrete objects created to map the
			// interfaces, along with Enums...

			// We fetch only the objects, here. The interfaces are managed just after
			pluginConfiguration.getLog().debug("Init batch loader for objects");
			objectTypes.stream().filter(o -> (o.getGraphQlType() == GraphQlType.OBJECT && !o.isInputType()))
					.forEach(o -> initOneBatchLoader(o));

			// Let's go through all interfaces.
			pluginConfiguration.getLog().debug("Init batch loader for objects");
			interfaceTypes.stream().forEach(i -> initOneBatchLoader(i));
		}
	}

	/**
	 * Analyzes one object, and decides if there should be a {@link BatchLoader} for it
	 * 
	 * @param type
	 *            the Type that may need a BatchLoader
	 */
	private void initOneBatchLoader(ObjectType type) {
		pluginConfiguration.getLog().debug("Init batch loader for " + type.getName());
		// No BatchLoader for the "artificial" Object Type created to instanciate an Interface. This "artificial" Object
		// Type is for internal usage only, and to be used in Client mode to allow instanciation of the server response
		// interface object. It doesn't exist in the GraphQL Schema. Thus, it must have no BatchLoader.
		if (type.getDefaultImplementationForInterface() == null) {
			Field id = type.getIdentifier();
			if (id != null) {
				batchLoaders.add(new BatchLoaderImpl(type, getDataFetchersDelegate(type, true)));
			}
		}
	}

	/**
	 * For each interface, identify the list of object types which implements it. This is done last, when all objects
	 * has been read by the plugin.
	 * 
	 * @see InterfaceType#getImplementingTypes()
	 */
	void initListOfInterfaceImplementations() {
		for (InterfaceType interfaceType : interfaceTypes) {
			for (ObjectType objectType : objectTypes) {
				if (objectType.getImplementz().contains(interfaceType.getName())) {
					// This object implements the current interface we're looping in.
					interfaceType.getImplementingTypes().add(objectType);
				}
			}
		}
	}

	/**
	 * Build an @JsonDeserialize annotation with one or more attributes
	 * 
	 * @param contentAs
	 *            contentAs class name
	 * @param using
	 *            using class name
	 * @return annotation string
	 */
	private String buildJsonDeserializeAnnotation(String contentAs, String using) {
		StringBuffer annotationBuf = new StringBuffer();
		annotationBuf.append("@JsonDeserialize(");
		boolean addComma = false;
		if (contentAs != null) {
			annotationBuf.append("contentAs = ").append(contentAs);
			addComma = true;
		}

		if (using != null) {
			if (addComma) {
				annotationBuf.append(", ");
			}
			annotationBuf.append("using = ").append(using);
		}
		annotationBuf.append(")");
		return annotationBuf.toString();
	}

	/**
	 * Add introspection capabilities: the __schema and __type query into a dedicated __IntrospectionQuery, and the
	 * __typename into each GraphQL object.<BR/>
	 * Note: the introspection schema has already been parsed, as it is added by {@link ResourceSchemaStringProvider} in
	 * the documents list
	 */
	void addIntrospectionCapabilities() {
		// No action in server mode: everything is handled by graphql-java
		if (pluginConfiguration.getMode().equals(PluginMode.client)) {

			/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// First step : add the introspection queries into the existing query. If no query exists, one is created.s
			/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			if (queryTypes.size() == 0) {
				// There was no query. We need to create one.
				ObjectType introspectionQuery = new ObjectType(pluginConfiguration.getPackageName(),
						pluginConfiguration.getPackageName(), pluginConfiguration.getMode());
				introspectionQuery.setName(INTROSPECTION_QUERY);
				introspectionQuery.setRequestType("query");
				queryTypes.add(introspectionQuery);
			}

			// We add the introspection capability into each query (but there should be only one)
			for (ObjectType query : queryTypes) {
				FieldImpl __schema = FieldImpl.builder().documentParser(this).name("__schema")
						.graphQLTypeName("__Schema").owningType(query).mandatory(true).build();
				//
				FieldImpl __type = FieldImpl.builder().documentParser(this).name("__type").graphQLTypeName("__Type")
						.owningType(query).mandatory(true).build();
				__type.getInputParameters().add(FieldImpl.builder().documentParser(this).name("name")
						.graphQLTypeName("String").mandatory(true).build());
				//
				query.getFields().add(__schema);
				query.getFields().add(__type);
			}

			/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// Second step: add the __datatype field into every GraphQL type (out of input types)
			// That is : in all regular object types and interfaces.
			/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			for (ObjectType type : objectTypes) {
				if (!type.isInputType()) {
					type.getFields().add(FieldImpl.builder().documentParser(this).name("__typename")
							.graphQLTypeName("String").owningType(type).mandatory(false).build());
				}
			}
			for (InterfaceType type : interfaceTypes) {
				type.getFields().add(FieldImpl.builder().documentParser(this).name("__typename")
						.graphQLTypeName("String").owningType(type).mandatory(false).build());
			}
		}
	}

	/**
	 * Get the internal value for a {@link Value} stored in the graphql-java AST.
	 * 
	 * @param value
	 *            The value for which we need to extract the real value
	 * @param graphqlTypeName
	 *            The type name for this value, as defined in the GraphQL schema. This is used when it's an object
	 *            value, to create an instance of the correct java class.
	 * @param action
	 *            The action that is executing, to generated an explicit error message. It can be for instance "Reading
	 *            directive directiveName".
	 * @return
	 */
	private Object getValue_IsItReallyUsed(Value<?> value, String graphqlTypeName, String action) {
		if (value instanceof StringValue) {
			return ((StringValue) value).getValue();
		} else if (value instanceof BooleanValue) {
			return ((BooleanValue) value).isValue();
		} else if (value instanceof IntValue) {
			return ((IntValue) value).getValue();
		} else if (value instanceof FloatValue) {
			return ((FloatValue) value).getValue();
		} else if (value instanceof graphql.language.EnumValue) {
			// For enums, we can't retrieve an instance of the enum value, as the enum class has not been created yet.
			// So we just return the label of the enum, as a String.
			return ((graphql.language.EnumValue) value).getName();
		} else if (value instanceof NullValue) {
			return null;
		} else if (value instanceof ArrayValue) {
			List<Value> list = ((ArrayValue) value).getValues();
			Object[] ret = new Object[list.size()];
			for (int i = 0; i < list.size(); i += 1) {
				ret[i] = getValue_IsItReallyUsed(list.get(i), graphqlTypeName, action + ": ArrayValue(" + i + ")");
			}
			return ret;
			// } else if (value instanceof ObjectValue) {
			// return null;
		} else {
			throw new RuntimeException(
					"Value of type " + value.getClass().getName() + " is not managed (" + action + ")");
		}
	}
}