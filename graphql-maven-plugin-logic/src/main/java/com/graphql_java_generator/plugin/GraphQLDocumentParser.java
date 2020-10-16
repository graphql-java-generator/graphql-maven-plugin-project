/**
 * 
 */
package com.graphql_java_generator.plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.graphql_java_generator.GraphQLField;
import com.graphql_java_generator.annotation.GraphQLInputParameters;
import com.graphql_java_generator.annotation.GraphQLInputType;
import com.graphql_java_generator.annotation.GraphQLInterfaceType;
import com.graphql_java_generator.annotation.GraphQLNonScalar;
import com.graphql_java_generator.annotation.GraphQLObjectType;
import com.graphql_java_generator.annotation.GraphQLQuery;
import com.graphql_java_generator.annotation.GraphQLScalar;
import com.graphql_java_generator.annotation.GraphQLUnionType;
import com.graphql_java_generator.annotation.RequestType;
import com.graphql_java_generator.plugin.conf.CustomScalarDefinition;
import com.graphql_java_generator.plugin.conf.GraphQLConfiguration;
import com.graphql_java_generator.plugin.conf.PluginMode;
import com.graphql_java_generator.plugin.language.BatchLoader;
import com.graphql_java_generator.plugin.language.DataFetcher;
import com.graphql_java_generator.plugin.language.DataFetchersDelegate;
import com.graphql_java_generator.plugin.language.Field;
import com.graphql_java_generator.plugin.language.Relation;
import com.graphql_java_generator.plugin.language.RelationType;
import com.graphql_java_generator.plugin.language.Type;
import com.graphql_java_generator.plugin.language.Type.GraphQlType;
import com.graphql_java_generator.plugin.language.impl.AbstractType;
import com.graphql_java_generator.plugin.language.impl.BatchLoaderImpl;
import com.graphql_java_generator.plugin.language.impl.CustomScalarType;
import com.graphql_java_generator.plugin.language.impl.DataFetcherImpl;
import com.graphql_java_generator.plugin.language.impl.DataFetchersDelegateImpl;
import com.graphql_java_generator.plugin.language.impl.EnumType;
import com.graphql_java_generator.plugin.language.impl.FieldImpl;
import com.graphql_java_generator.plugin.language.impl.InterfaceType;
import com.graphql_java_generator.plugin.language.impl.ObjectType;
import com.graphql_java_generator.plugin.language.impl.RelationImpl;
import com.graphql_java_generator.plugin.language.impl.ScalarType;
import com.graphql_java_generator.plugin.language.impl.UnionType;
import com.graphql_java_generator.plugin.schema_personalization.GraphQLJsonSchemaPersonalization;

import graphql.parser.Parser;
import lombok.Getter;

/**
 * This class parses the GraphQL shema file(s), and loads it in a structure that'll make it easy to send to Velocity
 * templates. There is no validity check: we trust the information in the Document, as it is read by the GraphQL
 * {@link Parser}. <BR/>
 * The graphQL-java library maps both FieldDefinition and InputValueDefinition in very similar structures, which are
 * actually trees. These structures are too hard too read in a Velocity template, and we need to parse down to a
 * properly structures way for that.
 * 
 * @author etienne-sf
 */
@Component
@Getter
public class GraphQLDocumentParser extends DocumentParser {

	/**
	 * The name of the package for utility classes, when the <I>separateUtilClasses</I> plugin parameter is set to true.
	 * This is the name of subpackage within the package defined by the <I>packageName</I> plugin parameter. <BR/>
	 * This constant is useless when the <I>separateUtilClasses</I> plugin parameter is set to false, which is its
	 * default value.
	 */
	public static final String UTIL_PACKAGE_NAME = "util";

	private static final String INTROSPECTION_QUERY = "__IntrospectionQuery";

	/**
	 * This instance is responsible for providing all the configuration parameter from the project (Maven, Gradle...)
	 */
	@Autowired
	GraphQLConfiguration configuration;

	/////////////////////////////////////////////////////////////////////////////////////////////
	// Internal attributes for this class

	/**
	 * The {@link GraphQLJsonSchemaPersonalization} allows the user to update what the plugin would have generate,
	 * through a json configuration file
	 */
	@Autowired
	GraphQLJsonSchemaPersonalization jsonSchemaPersonalization;

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

	/**
	 * This method initializes the {@link #scalarTypes} list. This list depends on the use case
	 * 
	 */
	@Override
	protected void initScalarTypes(Class<?> notUsed) {

		// Let's load the standard Scalar types
		if (configuration.getMode().equals(PluginMode.server)) {
			super.initScalarTypes(UUID.class);
		} else {
			// In client mode, ID type is managed as a String
			super.initScalarTypes(String.class);
		}

		//////////////////////////////////////////////////////////////////////////////////////////
		// Add of all GraphQL custom scalar implementations must be provided by the plugin configuration
		configuration.getLog().debug("Storing custom scalar's implementations [START]");
		if (configuration.getCustomScalars() != null) {
			for (CustomScalarDefinition customScalarDef : configuration.getCustomScalars()) {
				CustomScalarType type = new CustomScalarType(customScalarDef, configuration);
				customScalars.add(type);
				types.put(type.getName(), type);
			}
		}
		configuration.getLog().debug("Storing custom scalar's implementations [END]");
	}

	/**
	 * The main method of the class: it graphqlUtils.executes the generation of the given documents
	 * 
	 * @param documents
	 *            The GraphQL definition schema, from which the code is to be generated
	 * @return
	 */
	@Override
	public int parseDocuments() {
		/////////////////////////////////////////////////////////////////////////////////////////////////////
		// Let's start by reading the GraphQL documents
		super.parseDocuments();

		/////////////////////////////////////////////////////////////////////////////////////////////////////
		// Then, we add the capabilities for code generation

		// Add introspection capabilities (the introspection schema has already been read, as it is added by
		// ResourceSchemaStringProvider in the documents list
		configuration.getLog().debug("Adding introspection capabilities");
		addIntrospectionCapabilities();
		// Let's identify every relation between objects, interface or union in the model
		configuration.getLog().debug("Init relations");
		initRelations();
		// Some annotations are needed for Jackson or JPA
		configuration.getLog().debug("Add annotations");
		addAnnotations();
		// List all data fetchers
		configuration.getLog().debug("Init data fetchers");
		initDataFetchers();
		// List all Batch Loaders
		configuration.getLog().debug("Init batch loaders");
		initBatchLoaders();
		// Fill in the import list
		addImports();

		// Apply the user's schema personalization
		configuration.getLog().debug("Apply schema personalization");
		jsonSchemaPersonalization.applySchemaPersonalization();

		// We're done
		int nbClasses = objectTypes.size() + enumTypes.size() + interfaceTypes.size();
		configuration.getLog().debug(documents.size() + " document(s) parsed (" + nbClasses + ")");
		return nbClasses;
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
		for (ObjectType type : getObjectTypes()) {
			// We initiate the relations only for regular objects (not query/mutation/subscription)
			if (type.getRequestType() == null) {
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
			} // if (type.getRequestType()== null)
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
		switch (configuration.getMode()) {
		case client:
			// Type annotations
			graphqlUtils.concatStreams(Type.class, true, null, null, null, interfaceTypes, objectTypes, unionTypes)
					.forEach(o -> addTypeAnnotationForClientMode(o));

			// Field annotations
			graphqlUtils.concatStreams(Type.class, true, null, null, null, objectTypes, interfaceTypes)
					.flatMap(o -> o.getFields().stream()).forEach(f -> addFieldAnnotationForClientMode((FieldImpl) f));

			break;
		case server:
			graphqlUtils.concatStreams(ObjectType.class, true, null, null, null, objectTypes, interfaceTypes)
					.forEach(o -> addTypeAnnotationForServerMode(o));
			graphqlUtils.concatStreams(ObjectType.class, true, null, null, null, objectTypes, interfaceTypes)
					.flatMap(o -> o.getFields().stream()).forEach(f -> addFieldAnnotationForServerMode(f));
			break;
		}

	}

	/**
	 * This method add the needed annotation(s) to the given type, when in client mode
	 * 
	 * @param type
	 */
	void addTypeAnnotationForClientMode(Type type) {
		// No specific annotation for objects and interfaces when in client mode.

		if (type instanceof InterfaceType || type instanceof UnionType) {
			type.addImport(configuration.getPackageName(), JsonTypeInfo.class.getName());
			type.addImport(configuration.getPackageName(), JsonTypeInfo.Id.class.getName());
			type.addAnnotation(
					"@JsonTypeInfo(use = Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = \"__typename\", visible = true)");

			// jsonSubTypes annotation looks like this:
			// @JsonSubTypes({ @Type(value = Droid.class, name = "Droid"), @Type(value = Human.class, name = "Human") })
			StringBuffer jsonSubTypes = new StringBuffer();
			type.addImport(configuration.getPackageName(), JsonSubTypes.class.getName());
			type.addImport(configuration.getPackageName(), JsonSubTypes.Type.class.getName());
			jsonSubTypes.append("@JsonSubTypes({");
			boolean addSeparator = false;

			List<ObjectType> types;
			if (type instanceof InterfaceType)
				types = ((InterfaceType) type).getImplementingTypes();
			else
				types = ((UnionType) type).getMemberTypes();

			for (ObjectType t : types) {
				// No separator for the first iteration
				if (addSeparator)
					jsonSubTypes.append(",");
				else
					addSeparator = true;
				jsonSubTypes.append(" @Type(value = ").append(t.getName()).append(".class, name = \"")
						.append(t.getName()).append("\")");
			}
			jsonSubTypes.append(" })");

			type.addAnnotation(jsonSubTypes.toString());
		}

		// Add the GraphQLQuery annotation fpr query/mutation/subscription and for objects that are a
		// query/mutation/subscription
		if (type instanceof ObjectType && ((ObjectType) type).getRequestType() != null) {
			type.addImport(configuration.getPackageName(), GraphQLQuery.class.getName());
			type.addImport(configuration.getPackageName(), RequestType.class.getName());
			type.addAnnotation("@GraphQLQuery(name = \"" + type.getName() + "\", type = RequestType."
					+ ((ObjectType) type).getRequestType() + ")");

		}

		// Let's add the annotations, that are common to both the client and the server mode
		addTypeAnnotationForBothClientAndServerMode(type);
	}

	/**
	 * This method add the needed annotation(s) to the given type when in server mode. This typically add the
	 * JPA @{@link Entity} annotation.
	 * 
	 * @param o
	 */
	void addTypeAnnotationForServerMode(Type o) {

		// We generates the @Entity annotation when:
		// 1) It's asked in the plugin configuration
		// 2) The object is a regular object (not an input type)
		// 3) It's not an object that is a query/mutation/subscription
		if (configuration.isGenerateJPAAnnotation() && o instanceof ObjectType && !(o instanceof InterfaceType)
				&& !(o instanceof UnionType) && !((ObjectType) o).isInputType()
				&& ((ObjectType) o).getRequestType() == null) {
			o.addImport(configuration.getPackageName(), Entity.class.getName());
			((AbstractType) o).addAnnotation("@Entity");
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
			o.addImport(configuration.getPackageName(), GraphQLInterfaceType.class.getName());
			o.addAnnotation("@GraphQLInterfaceType(\"" + o.getName() + "\")");
		} else if (o instanceof UnionType) {
			o.addImport(configuration.getPackageName(), GraphQLUnionType.class.getName());
			o.addAnnotation("@GraphQLUnionType(\"" + o.getName() + "\")");
		} else if (o instanceof ObjectType) {
			if (((ObjectType) o).isInputType()) {
				// input type
				o.addImport(configuration.getPackageName(), GraphQLInputType.class.getName());
				o.addAnnotation("@GraphQLInputType(\"" + o.getName() + "\")");
			} else {
				// Standard object type
				o.addImport(configuration.getPackageName(), GraphQLObjectType.class.getName());
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
	void addFieldAnnotationForClientMode(FieldImpl field) {
		// No json field annotation for interfaces or unions. The json annotation is directly on the interface or union
		// type.
		String contentAs = null;
		String using = null;
		if (field.isList()) {
			field.getOwningType().addImport(configuration.getPackageName(), List.class.getName());
			contentAs = field.getType().getClassSimpleName() + ".class";
		}
		if (field.getType().isCustomScalar()) {
			String classname = "CustomScalarDeserializer" + field.getType().getName();
			field.getOwningType().addImport(configuration.getPackageName(), getUtilPackageName() + "." + classname);
			using = classname + ".class";
		}
		if (contentAs != null || using != null) {
			field.getOwningType().addImport(configuration.getPackageName(), JsonDeserialize.class.getName());
			field.addAnnotation(buildJsonDeserializeAnnotation(contentAs, using));
		}

		if (field.getInputParameters().size() > 0) {
			// Let's add the @GraphQLInputParameters annotation
			field.getOwningType().addImport(configuration.getPackageName(), GraphQLInputParameters.class.getName());
			StringBuilder names = new StringBuilder();
			StringBuilder types = new StringBuilder();
			String separator = "";
			for (Field param : field.getInputParameters()) {
				names.append(separator).append('"').append(param.getName()).append('"');
				types.append(separator).append('"').append(param.getGraphQLTypeName()).append('"');
				separator = ", ";
			}
			field.addAnnotation("@GraphQLInputParameters(names = {" + names + "}, types = {" + types + "})");
		}

		field.getOwningType().addImport(configuration.getPackageName(), JsonProperty.class.getName());
		field.addAnnotation("@JsonProperty(\"" + field.getName() + "\")");

		addFieldAnnotationForBothClientAndServerMode(field);
	}

	/**
	 * This method add the needed annotation(s) to the given field. It should be called when the maven plugin is in
	 * server mode. This typically add the JPA @Id, @GeneratedValue, @Transient annotations.
	 * 
	 * @param field
	 */
	void addFieldAnnotationForServerMode(Field field) {
		if (configuration.isGenerateJPAAnnotation() && !field.getOwningType().isInputType()) {
			if (field.isId()) {
				// We have found the identifier
				field.getOwningType().addImport(configuration.getPackageName(), Id.class.getName());
				((FieldImpl) field).addAnnotation("@Id");
				field.getOwningType().addImport(configuration.getPackageName(), GeneratedValue.class.getName());
				((FieldImpl) field).addAnnotation("@GeneratedValue");
			} else if (field.getRelation() != null || field.isList()) {
				// We prevent JPA to manage the relations: we want the GraphQL Data Fetchers to do it, instead.
				field.getOwningType().addImport(configuration.getPackageName(), Transient.class.getName());
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
			field.getOwningType().addImport(configuration.getPackageName(), GraphQLScalar.class.getName());
			((FieldImpl) field)
					.addAnnotation("@GraphQLScalar(fieldName = \"" + field.getName() + "\", graphQLTypeName = \""
							+ field.getGraphQLTypeName() + "\", list = " + ((field.isList()) ? "true" : "false")
							+ ", javaClass = " + field.getType().getClassSimpleName() + ".class)");
		} else {
			field.getOwningType().addImport(configuration.getPackageName(), GraphQLNonScalar.class.getName());
			((FieldImpl) field)
					.addAnnotation("@GraphQLNonScalar(fieldName = \"" + field.getName() + "\", graphQLTypeName = \""
							+ field.getGraphQLTypeName() + "\", list = " + ((field.isList()) ? "true" : "false")
							+ ", javaClass = " + field.getType().getClassSimpleName() + ".class)");
		}
	}

	/**
	 * Identified all the GraphQL Data Fetchers needed from this/these schema/schemas
	 */
	void initDataFetchers() {
		if (configuration.getMode().equals(PluginMode.server)) {
			objectTypes.stream().forEach(o -> initDataFetcherForOneObject(o));
			interfaceTypes.stream().forEach(o -> initDataFetcherForOneObject(o));
			unionTypes.stream().forEach(o -> initDataFetcherForOneObject(o));
		}
	}

	/**
	 * Identified all the GraphQL Data Fetchers needed for this type
	 *
	 * @param type
	 * @param isQueryOrMutationType
	 *            true if the given type is actually a query, false otherwise
	 */
	void initDataFetcherForOneObject(ObjectType type) {

		// No DataFetcher for input types
		if (!type.isInputType()) {

			// Creation of the DataFetchersDelegate. It will be added to the list only if it contains at least one
			// DataFetcher.
			DataFetchersDelegate dataFetcherDelegate = new DataFetchersDelegateImpl(type);

			for (Field field : type.getFields()) {

				if (type.getRequestType() != null) {
					// For query/mutation/subscription, we take the argument read in the schema as is: all the needed
					// informations is already parsed.
					// There is no source for requests, as they are the root of the hierarchy
					dataFetchers.add(new DataFetcherImpl(field, dataFetcherDelegate, true, false, null));
				} else if (((type instanceof ObjectType || type instanceof InterfaceType) && //
						(field.isList() || field.getType() instanceof ObjectType
								|| field.getType() instanceof InterfaceType))) {
					// For Objects and Interfaces, we need to add a specific data fetcher. The objective there is to
					// manage the relations with GraphQL. The aim is to use the GraphQL data loader :
					// very important to limit the number of subqueries, when subobjects are queried. In these case, we
					// need to create a new field that add the object ID as a parameter of the Data Fetcher
					FieldImpl newField = FieldImpl.builder().documentParser(this).name(field.getName())
							.list(field.isList()).owningType(field.getOwningType())
							.graphQLTypeName(field.getGraphQLTypeName()).build();

					// Let's add the id for the owning type of the field, then all its input parameters
					for (Field inputParameter : field.getInputParameters()) {
						List<Field> list = newField.getInputParameters();
						list.add(inputParameter);
					}

					// We'll add a data fetcher with a data loader, to use a Batch Loader, if:
					// 1) It's a Data Fetcher from an object to another one (we're already in this case)
					// 2) That target object has an id (it can be either a list or a single object)
					// 3) The Relation toward the target object is OneToOne or ManyToOne. That is this field is not a
					// list
					// graphql-java will then determines at runtime if a dataloader is needed in the running case, or
					// not
					boolean withDataLoader = field.getType().getIdentifier() != null && !field.isList();

					if (withDataLoader) {
						// We always have the 'standard' data fetcher in the DataFetcherDelegate. But only the one with
						// the data loader is declared in the GraphQLProvider and the GraphQLDataFetchers classes.
						dataFetchers
								.add(new DataFetcherImpl(newField, dataFetcherDelegate, false, false, type.getName()));
						// Then the datafetcher with the data loader
						dataFetchers
								.add(new DataFetcherImpl(newField, dataFetcherDelegate, true, true, type.getName()));
					} else {
						// We always have the 'standard' data fetcher
						dataFetchers
								.add(new DataFetcherImpl(newField, dataFetcherDelegate, true, false, type.getName()));
					}
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
		if (configuration.getMode().equals(PluginMode.server)) {
			// objectTypes contains both the objects defined in the schema, and the concrete objects created to map the
			// interfaces, along with Enums...

			// We fetch only the objects, here. The interfaces are managed just after
			configuration.getLog().debug("Init batch loader for objects");
			objectTypes.stream().filter(o -> (o.getGraphQlType() == GraphQlType.OBJECT && !o.isInputType()))
					.forEach(o -> initOneBatchLoader(o));

			// Let's go through all interfaces.
			configuration.getLog().debug("Init batch loader for objects");
			interfaceTypes.stream().forEach(i -> initOneBatchLoader(i));
		}
	}

	/**
	 * Analyzes one object, and decides if there should be a {@link BatchLoader} for it. No action if this type is a
	 * type that represents a query/mutation/subscription. There are {@link BatchLoader}s only for regular objects.
	 * 
	 * @param type
	 *            the Type that may need a BatchLoader
	 */
	private void initOneBatchLoader(ObjectType type) {
		// There is no Batch Loader for query/mutation/subscription
		if (type.getRequestType() == null) {

			configuration.getLog().debug("Init batch loader for " + type.getName());

			Field id = type.getIdentifier();
			if (id != null) {
				batchLoaders.add(new BatchLoaderImpl(type, getDataFetchersDelegate(type, true)));
			}

		} // if
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
		if (configuration.getMode().equals(PluginMode.client)) {

			/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// First step : add the introspection queries into the existing query. If no query exists, one is created.s
			/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			if (queryType == null) {
				// There was no query. We need to create one. It will contain only the Introspection Query
				queryType = new ObjectType(DEFAULT_QUERY_NAME, configuration);
				queryType.setName(INTROSPECTION_QUERY);
				queryType.setRequestType("query");

				// Let's first add the regular object that'll receive the server response (in the default package)
				objectTypes.add(queryType);
				types.put(queryType.getName(), queryType);
			}

			// We also need to add the relevant fields into the regular object that matches the query.
			// But they must be a separate instance, otherwise their annotation is added twice.
			Type objectQuery = getType(queryType.getName());
			objectQuery.getFields().add(get__SchemaField(objectQuery));
			objectQuery.getFields().add(get__TypeField(objectQuery));

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
	 * @param o
	 * @return
	 */
	private FieldImpl get__TypeField(Type o) {
		FieldImpl __type = FieldImpl.builder().documentParser(this).name("__type").graphQLTypeName("__Type")
				.owningType(o).mandatory(true).build();
		__type.getInputParameters().add(FieldImpl.builder().documentParser(this).name("name").graphQLTypeName("String")
				.mandatory(true).build());
		return __type;
	}

	/**
	 * @param o
	 * @return
	 */
	private FieldImpl get__SchemaField(Type o) {
		FieldImpl __schema = FieldImpl.builder().documentParser(this).name("__schema").graphQLTypeName("__Schema")
				.owningType(o).mandatory(true).build();
		return __schema;
	}

	/**
	 * Adds the necessary java import, so that the generated classes compile. <BR/>
	 * The import for the annotation have already been added.
	 */
	private void addImports() {
		types.values().parallelStream().forEach(type -> addImportsForOneType(type));
	}

	/**
	 * Add all import that are needed for this type
	 * 
	 * @param type
	 */
	private void addImportsForOneType(Type type) {
		if (type != null) {

			if (type.getName().equals("Post")) {
				// Just for a debut breakpoint
				int i = 0;
			}

			// First, the import for the object itself. It should do something only for separate utility classes.
			addAnImportForOneType(type, configuration.getPackageName() + "." + type.getClassSimpleName());

			// Let's loop through all the fields
			for (Field f : type.getFields()) {
				if (f.isList()) {
					addAnImportForOneType(type, List.class);
				}
				addAnImportForOneType(type, f.getType());

				for (Field param : f.getInputParameters()) {
					if (param.isList()) {
						addAnImportForOneType(type, List.class);
					}
					addAnImportForOneType(type, param.getType());
				} // for(inputParameters)
			} // for(Fields)

			// Let's add some common imports
			addAnImportForOneType(type, GraphQLField.class);
			addAnImportForOneType(type, GraphQLInputParameters.class);

			// Some imports that are only for utility classes
			type.addImportForUtilityClasses(getUtilPackageName(), RequestType.class.getName());

			switch (configuration.getMode()) {
			case client:
				addAnImportForOneType(type, JsonProperty.class);
				break;
			case server:
				break;
			default:
				throw new RuntimeException("unexpected plugin mode: " + configuration.getMode().name());
			}
		}
	}

	private void addAnImportForOneType(Type type, Class<?> clazzToImport) {
		addAnImportForOneType(type, clazzToImport.getName());
	}

	private void addAnImportForOneType(Type type, Type typeToImport) {
		if (typeToImport instanceof ScalarType) {
			addAnImportForOneType(type,
					((ScalarType) typeToImport).getPackageName() + "." + typeToImport.getClassSimpleName());
		} else {
			addAnImportForOneType(type, configuration.getPackageName() + "." + typeToImport.getClassSimpleName());
		}
	}

	private void addAnImportForOneType(Type type, String classname) {
		final String targetPackage = configuration.getPackageName();
		final String utilityPackage = getUtilPackageName();

		type.addImport(targetPackage, classname);
		type.addImportForUtilityClasses(utilityPackage, classname);
	}

	/**
	 * Returns the name of the package for utility classes, when the <I>separateUtilClasses</I> plugin parameter is set
	 * to true. This is the name of subpackage within the package defined by the <I>packageName</I> plugin parameter.
	 * <BR/>
	 * This constant is useless when the <I>separateUtilClasses</I> plugin parameter is set to false, which is its
	 * default value.
	 * 
	 * @return
	 */
	@Override
	String getUtilPackageName() {
		if (configuration.isSeparateUtilityClasses()) {
			return configuration.getPackageName() + "." + UTIL_PACKAGE_NAME;
		} else {
			return configuration.getPackageName();
		}
	}

	@Override
	CustomScalarType getCustomScalarType(String name) {
		for (CustomScalarType customScalarType : customScalars) {
			if (customScalarType.getName().equals(name)) {
				return customScalarType;
			}
		}

		throw new RuntimeException(
				"The plugin configuration must provide an implementation for the Custom Scalar '" + name + "'.");
	}

}