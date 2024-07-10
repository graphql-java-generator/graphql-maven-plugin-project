/**
 * 
 */
package com.graphql_java_generator.plugin.generate_code;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.graphql_java_generator.annotation.GraphQLInputParameters;
import com.graphql_java_generator.annotation.GraphQLInputType;
import com.graphql_java_generator.annotation.GraphQLInterfaceType;
import com.graphql_java_generator.annotation.GraphQLNonScalar;
import com.graphql_java_generator.annotation.GraphQLObjectType;
import com.graphql_java_generator.annotation.GraphQLQuery;
import com.graphql_java_generator.annotation.GraphQLScalar;
import com.graphql_java_generator.annotation.GraphQLUnionType;
import com.graphql_java_generator.annotation.RequestType;
import com.graphql_java_generator.plugin.DocumentParser;
import com.graphql_java_generator.plugin.ResourceSchemaStringProvider;
import com.graphql_java_generator.plugin.conf.GenerateCodeCommonConfiguration;
import com.graphql_java_generator.plugin.conf.GenerateGraphQLSchemaConfiguration;
import com.graphql_java_generator.plugin.conf.GenerateServerCodeConfiguration;
import com.graphql_java_generator.plugin.conf.PluginMode;
import com.graphql_java_generator.plugin.language.BatchLoader;
import com.graphql_java_generator.plugin.language.CustomScalar;
import com.graphql_java_generator.plugin.language.DataFetcher;
import com.graphql_java_generator.plugin.language.DataFetchersDelegate;
import com.graphql_java_generator.plugin.language.Field;
import com.graphql_java_generator.plugin.language.FieldTypeAST;
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
import com.graphql_java_generator.plugin.schema_personalization.GenerateCodeJsonSchemaPersonalization;

import graphql.parser.Parser;
import lombok.Getter;

/**
 * This class parses the GraphQL shema file(s), and loads it in a structure that'll make it easy to send to Velocity
 * templates. There is no validity check: we trust the information in the Document, as it is read by the GraphQL
 * {@link Parser}. <BR/>
 * The graphQL-java library maps both FieldDefinition and InputValueDefinition in very similar structures, which are
 * actually trees. These structures are too hard too read in a Velocity template, and we need to parse down to a
 * properly structures way for that.<BR/>
 * This class should not be used directly. Please use the {@link GenerateCodePluginExecutor} instead.
 * 
 * @author etienne-sf
 */
@Component
@Getter
public class GenerateCodeDocumentParser extends DocumentParser {

	private static final Logger logger = LoggerFactory.getLogger(GenerateCodeDocumentParser.class);

	/**
	 * The name of the package for utility classes, when the <I>separateUtilClasses</I> plugin parameter is set to true.
	 * This is the name of subpackage within the package defined by the <I>packageName</I> plugin parameter. <BR/>
	 * This constant is useless when the <I>separateUtilClasses</I> plugin parameter is set to false, which is its
	 * default value.
	 */
	public static final String UTIL_PACKAGE_NAME = "util";

	static final String INTROSPECTION_QUERY = "__IntrospectionQuery";

	static final String IGNORED_SPRING_MAPPINGS_SEPARATOR = ", \t\r\n";

	/////////////////////////////////////////////////////////////////////////////////////////////
	// Internal attributes for this class

	/**
	 * The {@link GenerateCodeJsonSchemaPersonalization} allows the user to update what the plugin would have generate,
	 * through a json configuration file
	 */
	@Autowired
	GenerateCodeJsonSchemaPersonalization jsonSchemaPersonalization;

	/** All {@link Relation}s that have been found in the GraphQL schema(s) */
	List<Relation> relations = new ArrayList<>();

	/**
	 * All {@link DataFetcher}s that need to be implemented for this/these schema/schemas
	 */
	List<DataFetcher> dataFetchers = new ArrayList<>();

	/**
	 * All {@link DataFetchersDelegate}s that need to be implemented for this/these schema/schemas. <br/>
	 * Since 2.8, it is possible to ignore types and fields for this generation, thanks to the . These ignored types and
	 * fiels
	 */
	List<DataFetchersDelegate> dataFetchersDelegates = new ArrayList<>();

	/**
	 * All {@link BatchLoader}s that need to be implemented for this/these schema/schemas
	 */
	List<BatchLoader> batchLoaders = new ArrayList<>();

	/** The list of {@link CustomDeserializer} that contains the custom deserializers that must be generated. */
	private List<CustomDeserializer> customDeserializers = new ArrayList<>();

	/** The list of {@link CustomSerializer} that contains the custom serializers that must be generated. */
	private List<CustomSerializer> customSerializers = new ArrayList<>();

	/** The list of GraphQL types for which no DataFetcherDelegates and no Controller should be generated */
	Set<String> typeSpringMappingIgnored = null;

	/** The list of GraphQL type's fields for which no DataFetcherDelegates and no Controller should be generated */
	Map<String, Set<String>> fieldSpringMappingIgnored = null;

	/** The default constructor */
	public GenerateCodeDocumentParser() {
	}

	/** A constructor for tests, to allow overriding the configuration */
	GenerateCodeDocumentParser(GenerateCodeCommonConfiguration conf) {
		this.configuration = conf;
	}

	@Override
	public void afterPropertiesSet() {
		if (!(super.configuration instanceof GenerateCodeCommonConfiguration)) {
			throw new RuntimeException(
					"[Internal error] The plugin configuration must implement the GenerateCodeCommonConfiguration interface, but is '"
							+ super.configuration.getClass().getName() + "'");
		}
		super.afterPropertiesSet();
	}

	@Override
	public GenerateCodeCommonConfiguration getConfiguration() {
		return (GenerateCodeCommonConfiguration) this.configuration;
	}

	/**
	 * This method initializes the {@link #scalarTypes} list. This list depends on the use case
	 * 
	 */
	@Override
	protected void initScalarTypes(Class<?> notUsed) {
		// Let's load the standard Scalar types
		if (getConfiguration().getMode().equals(PluginMode.server)) {
			try {
				super.initScalarTypes(
						Class.forName(((GenerateServerCodeConfiguration) this.configuration).getJavaTypeForIDType()));
			} catch (ClassNotFoundException e) {
				throw new RuntimeException(e.getMessage(), e);
			}
		} else {
			// In client mode, ID type is managed as a String
			super.initScalarTypes(String.class);
		}
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
	@Override
	public int parseGraphQLSchemas() throws IOException {
		/////////////////////////////////////////////////////////////////////////////////////////////////////
		// Let's start by some controls on the configuration parameters
		if (getConfiguration().getMode().equals(PluginMode.server)) {
			if (getConfiguration().isAddRelayConnections() && // Let's have a test that works for windows (\) and unix
																// (/)
					getConfiguration().getSchemaFilePattern()
							.endsWith(GenerateGraphQLSchemaConfiguration.DEFAULT_TARGET_SCHEMA_FILE_NAME)) {
				// In server mode, the graphql-java needs to have access to the GraphQL schema.
				throw new IllegalArgumentException(
						"When the addRelayConnections is set to true, the GraphQL schema must be provided have another name than '"
								+ GenerateGraphQLSchemaConfiguration.DEFAULT_TARGET_SCHEMA_FILE_NAME
								+ "'. Please check the https://graphql-maven-plugin-project.graphql-java-generator.com/server_add_relay_connection.html page for more information");
			}
		}

		/////////////////////////////////////////////////////////////////////////////////////////////////////
		// Then we read the GraphQL documents
		super.parseGraphQLSchemas();

		/////////////////////////////////////////////////////////////////////////////////////////////////////
		// And to finish, we calculate and store the capabilities for code generation

		// Add introspection capabilities (the introspection schema has already been read, as it is added by
		// ResourceSchemaStringProvider in the documents list
		logger.debug("Adding introspection capabilities");
		addIntrospectionCapabilities();
		// Let's identify every relation between objects, interface or union in the model
		logger.debug("Init relations");
		initRelations();
		// Some annotations are needed for Jackson or JPA
		logger.debug("Add annotations");
		addAnnotations();
		// List of all Custom Deserializers
		initCustomDeserializers();
		// List of all Custom Serializers
		initCustomSerializers();
		// List all data fetchers
		logger.debug("Init data fetchers");
		initDataFetchers();
		// List all Batch Loaders
		logger.debug("Init batch loaders");
		initBatchLoaders();
		// Fill in the import list
		addImports();

		// Apply the user's schema personalization
		logger.debug("Apply schema personalization");
		this.jsonSchemaPersonalization.applySchemaPersonalization();

		// We're done
		int nbClasses = getObjectTypes().size() + getEnumTypes().size() + getInterfaceTypes().size();
		logger.debug("Nb classes identified = " + nbClasses);
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

		for (DataFetchersDelegate dfd : this.dataFetchersDelegates) {
			if (dfd.getType().equals(type)) {
				return dfd;
			}
		}

		// No DataFetchersDelegate for this type exists yet
		if (createIfNotExists) {
			DataFetchersDelegate dfd = new DataFetchersDelegateImpl(type);
			this.dataFetchersDelegates.add(dfd);
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
							RelationType relType = field.getFieldTypeAST().getListDepth() > 0 ? RelationType.OneToMany
									: RelationType.ManyToOne;
							RelationImpl relation = new RelationImpl(type, field, relType);
							//
							((FieldImpl) field).setRelation(relation);
							this.relations.add(relation);
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
		switch (getConfiguration().getMode()) {
		case client:
			// Type annotations
			this.graphqlUtils.concatStreams(Type.class, true, null, null, null, this.interfaceTypes, getObjectTypes(),
					this.unionTypes).forEach(o -> addTypeAnnotationForClientMode(o));

			// Field annotations
			this.graphqlUtils.concatStreams(Type.class, true, null, null, null, getObjectTypes(), this.interfaceTypes)
					.flatMap(o -> o.getFields().stream()).forEach(f -> addFieldAnnotationForClientMode((FieldImpl) f));

			break;
		case server:
			this.graphqlUtils
					.concatStreams(ObjectType.class, true, null, null, null, getObjectTypes(), this.interfaceTypes)
					.forEach(o -> addTypeAnnotationForServerMode(o));
			this.graphqlUtils
					.concatStreams(ObjectType.class, true, null, null, null, getObjectTypes(), this.interfaceTypes)
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
			if (getConfiguration().isGenerateJacksonAnnotations()) {
				type.addImport(getConfiguration().getPackageName(), JsonTypeInfo.class.getName());
				type.addImport(getConfiguration().getPackageName(), JsonTypeInfo.Id.class.getName());
				type.addAnnotation(
						"@JsonTypeInfo(use = Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = \"__typename\", visible = true)");

				// jsonSubTypes annotation looks like this:
				// @JsonSubTypes({ @Type(value = Droid.class, name = "Droid"), @Type(value = Human.class, name =
				// "Human") })
				StringBuilder jsonSubTypes = new StringBuilder();
				type.addImport(getConfiguration().getPackageName(), JsonSubTypes.class.getName());
				type.addImport(getConfiguration().getPackageName(), JsonSubTypes.Type.class.getName());
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
					jsonSubTypes.append(" @Type(value = ").append(t.getJavaName()).append(".class, name = \"")
							.append(t.getName()).append("\")");
				}
				jsonSubTypes.append(" })");

				type.addAnnotation(jsonSubTypes.toString());
			}
		}

		// Add the GraphQLQuery annotation fpr query/mutation/subscription and for objects that are a
		// query/mutation/subscription
		if (type instanceof ObjectType && ((ObjectType) type).getRequestType() != null) {
			type.addImport(getConfiguration().getPackageName(), GraphQLQuery.class.getName());
			type.addImport(getConfiguration().getPackageName(), RequestType.class.getName());
			type.addAnnotation("@GraphQLQuery(name = \"" + type.getName() + "\", type = RequestType."
					+ ((ObjectType) type).getRequestType() + ")");

		}

		// Let's add the annotations, that are common to both the client and the server mode
		addTypeAnnotationForBothClientAndServerMode(type);
	}

	/**
	 * This method add the needed annotation(s) to the given type when in server mode. This typically add the JPA
	 * &amp;Entity annotation.
	 * 
	 * @param o
	 */
	void addTypeAnnotationForServerMode(Type o) {

		// We generates the @Entity annotation when:
		// 1) It's asked in the plugin configuration
		// 2) The object is a regular object (not an input type)
		// 3) It's not an object that is a query/mutation/subscription
		if (this.configuration instanceof GenerateServerCodeConfiguration
				&& ((GenerateServerCodeConfiguration) this.configuration).isGenerateJPAAnnotation()
				&& o instanceof ObjectType && !(o instanceof InterfaceType) && !(o instanceof UnionType)
				&& !((ObjectType) o).isInputType() && ((ObjectType) o).getRequestType() == null) {
			o.addImport(getConfiguration().getPackageName(), "javax.persistence.Entity");
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
			o.addImport(getConfiguration().getPackageName(), GraphQLInterfaceType.class.getName());
			o.addAnnotation("@GraphQLInterfaceType(\"" + o.getName() + "\")");
		} else if (o instanceof UnionType) {
			o.addImport(getConfiguration().getPackageName(), GraphQLUnionType.class.getName());
			o.addAnnotation("@GraphQLUnionType(\"" + o.getName() + "\")");
		} else if (o instanceof ObjectType) {
			if (((ObjectType) o).isInputType()) {
				// input type
				o.addImport(getConfiguration().getPackageName(), GraphQLInputType.class.getName());
				o.addAnnotation("@GraphQLInputType(\"" + o.getName() + "\")");
			} else {
				// Standard object type
				o.addImport(getConfiguration().getPackageName(), GraphQLObjectType.class.getName());
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

		if (getConfiguration().isGenerateJacksonAnnotations()) {
			field.getOwningType().addImport(getConfiguration().getPackageName(), JsonProperty.class.getName());
			field.addAnnotation("@JsonProperty(\"" + field.getName() + "\")");
		}

		if (getConfiguration().isGenerateJacksonAnnotations()) {
			// No json deserialization for input type
			if (!field.getOwningType().isInputType()
					&& (field.getFieldTypeAST().getListDepth() > 0 || field.getType().isCustomScalar())) {
				// Custom Deserializer (for all lists and custom scalars)
				String classSimpleName = "CustomJacksonDeserializers."//
						+ CustomDeserializer.getCustomDeserializerClassSimpleName(
								field.getFieldTypeAST().getListDepth(),
								this.graphqlUtils.getJavaName(field.getType().getName()));
				field.getOwningType().addImport(getConfiguration().getPackageName(),
						getUtilPackageName() + ".CustomJacksonDeserializers");
				field.getOwningType().addImport(getConfiguration().getPackageName(), JsonDeserialize.class.getName());

				field.addAnnotation(buildJsonDeserializeAnnotation(null, classSimpleName + ".class"));
			}

			// json serialization is only for input types
			if (field.getOwningType().isInputType() && field.getType().isCustomScalar()) {
				// Custom Serializer (only for custom scalars)
				String classSimpleName = "CustomJacksonSerializers."//
						+ CustomSerializer.getCustomSerializerClassSimpleName(field.getFieldTypeAST().getListDepth(),
								this.graphqlUtils.getJavaName(field.getType().getName()));
				field.getOwningType().addImport(getConfiguration().getPackageName(),
						getUtilPackageName() + ".CustomJacksonSerializers");
				field.getOwningType().addImport(getConfiguration().getPackageName(), JsonSerialize.class.getName());
				field.getOwningType().addImport(getConfiguration().getPackageName(),
						field.getType().getClassFullName());

				field.addAnnotation(buildJsonSerializeAnnotation(classSimpleName + ".class"));
			}
		}

		if (field.getInputParameters().size() > 0) {
			// Let's add the @GraphQLInputParameters annotation
			field.getOwningType().addImport(getConfiguration().getPackageName(),
					GraphQLInputParameters.class.getName());
			StringBuilder names = new StringBuilder();
			StringBuilder types = new StringBuilder();
			StringBuilder mandatories = new StringBuilder();
			StringBuilder listDepths = new StringBuilder();
			StringBuilder itemsMandatory = new StringBuilder();
			String separator = "";
			for (Field param : field.getInputParameters()) {
				names.append(separator).append('"').append(param.getName()).append('"');
				types.append(separator).append('"').append(param.getGraphQLTypeSimpleName()).append('"');
				mandatories.append(separator).append(param.getFieldTypeAST().isMandatory());
				listDepths.append(separator).append(param.getFieldTypeAST().getListDepth());
				itemsMandatory.append(separator).append(param.getFieldTypeAST().isItemMandatory());
				separator = ", ";
			}
			field.addAnnotation("@GraphQLInputParameters(names = {" + names + "}, types = {" + types
					+ "}, mandatories = {" + mandatories + "}, listDepths = {" + listDepths + "}, itemsMandatory = {"
					+ itemsMandatory + "})");
		}

		addFieldAnnotationForBothClientAndServerMode(field);
	}

	/**
	 * This method add the needed annotation(s) to the given field. It should be called when the maven plugin is in
	 * server mode. This typically add the JPA @Id, @GeneratedValue, @Transient annotations.
	 * 
	 * @param field
	 */
	void addFieldAnnotationForServerMode(Field field) {
		if (this.configuration instanceof GenerateServerCodeConfiguration
				&& ((GenerateServerCodeConfiguration) this.configuration).isGenerateJPAAnnotation()
				&& !field.getOwningType().isInputType()) {
			if (field.isId()) {
				// We have found the identifier
				field.getOwningType().addImport(getConfiguration().getPackageName(), "javax.persistence.Id");
				((FieldImpl) field).addAnnotation("@Id");
				field.getOwningType().addImport(getConfiguration().getPackageName(),
						"javax.persistence.GeneratedValue");
				((FieldImpl) field).addAnnotation("@GeneratedValue");
			} else if (field.getRelation() != null || field.getFieldTypeAST().getListDepth() > 0) {
				// We prevent JPA to manage the relations: we want the GraphQL Data Fetchers to do it, instead.
				field.getOwningType().addImport(getConfiguration().getPackageName(), "javax.persistence.Transient");
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
		if (field.getFieldTypeAST().getListDepth() > 0) {
			field.getOwningType().addImport(getConfiguration().getPackageName(), List.class.getName());
		}

		if (field.getType() instanceof ScalarType || field.getType() instanceof EnumType) {
			field.getOwningType().addImport(getConfiguration().getPackageName(), GraphQLScalar.class.getName());
			((FieldImpl) field).addAnnotation(""//
					+ "@GraphQLScalar("//
					+ " fieldName = \"" + field.getName() + "\","//
					+ " graphQLTypeSimpleName = \"" + field.getGraphQLTypeSimpleName() + "\","//
					+ " javaClass = " + field.getType().getClassFullName() + ".class,"//
					+ " listDepth = " + field.getFieldTypeAST().getListDepth()//
					+ ")");
		} else {
			field.getOwningType().addImport(getConfiguration().getPackageName(), GraphQLNonScalar.class.getName());
			((FieldImpl) field).addAnnotation(""//
					+ "@GraphQLNonScalar("//
					+ " fieldName = \"" + field.getName() + "\","//
					+ " graphQLTypeSimpleName = \"" + field.getGraphQLTypeSimpleName() + "\","//
					+ " javaClass = " + field.getType().getClassFullName() + ".class,"//
					+ " listDepth = " + field.getFieldTypeAST().getListDepth() //
					+ ")");
		}
	}

	/**
	 * Identified all the GraphQL Data Fetchers needed from this/these schema/schemas
	 */
	void initDataFetchers() {
		if (getConfiguration().getMode().equals(PluginMode.server)) {
			getObjectTypes().stream().forEach(o -> initDataFetcherForOneObject(o));
			this.interfaceTypes.stream().forEach(o -> initDataFetcherForOneObject(o));
			this.unionTypes.stream().forEach(o -> initDataFetcherForOneObject(o));
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
		// No DataFetcher generation if the type is in the list of ignored type Spring Mappings
		if (//
		((type instanceof ObjectType || type instanceof InterfaceType) && !type.isInputType())
				&& !isTypeSpringMappingIgnored(type)) {

			// Creation of the DataFetchersDelegate. It will be added to the list only if it contains at least one
			// DataFetcher.
			DataFetchersDelegate dataFetcherDelegate = new DataFetchersDelegateImpl(type);

			for (Field field : type.getFields()) {
				// No DataFetcher generation if the field is in the list of ignored type Spring Mappings
				if (!isFieldSpringMappingIgnored(field)) {
					if (type.getRequestType() != null) {
						// For query/mutation/subscription, we take the argument read in the schema as is: all the
						// needed
						// informations is already parsed.
						// There is no source for requests, as they are the root of the hierarchy
						this.dataFetchers.add(new DataFetcherImpl(field, dataFetcherDelegate, true, false, null));
					} else if (false
							// A data fetcher is needed to:
							// 1) manage lists
							|| field.getFieldTypeAST().getListDepth() > 0 //
							// 2) Manage fields that return a type or an interface
							|| field.getType() instanceof ObjectType || field.getType() instanceof InterfaceType
							// 3) Manage fields that has argument(s) ... only if
							// generateDataFetcherForEveryFieldsWithArguments is
							// true
							|| (((GenerateServerCodeConfiguration) this.configuration)
									.isGenerateDataFetcherForEveryFieldsWithArguments()
									&& field.getInputParameters().size() > 0)//
					) {
						// For Objects and Interfaces, we need to add a specific data fetcher. The objective there is to
						// manage the relations with GraphQL. The aim is to use the GraphQL data loader :
						// very important to limit the number of subqueries, when subobjects are queried. In these case,
						// we
						// need to create a new field that add the object ID as a parameter of the Data Fetcher

						// What's the need to duplicate the field instance ???
						FieldImpl newField = (FieldImpl) field;
						// FieldImpl newField = FieldImpl.builder().documentParser(this).name(field.getName())
						// .fieldTypeAST(FieldTypeAST.builder().list(field.getFieldTypeAST().getListDepth() > 0)
						// .graphQLTypeSimpleName(field.getgraphQLTypeSimpleName()).build())
						// .owningType(field.getOwningType()).build();
						//
						// // Let's add the id for the owning type of the field, then all its input parameters
						// for (Field inputParameter : field.getInputParameters()) {
						// List<Field> list = newField.getInputParameters();
						// list.add(inputParameter);
						// }

						// We'll add a data fetcher with a data loader, to use a Batch Loader, if:
						// 1) It's a Data Fetcher from an object to another one (we're already in this case)
						// 2) That target object has an id (it can be either a list or a single object)
						// 3) The Relation toward the target object is OneToOne or ManyToOne. That is this field is not
						// a
						// list
						// graphql-java will then determines at runtime if a dataloader is needed in the running case,
						// or
						// not
						boolean withDataLoader = field.getType().getIdentifier() != null;
						if (field.getFieldTypeAST().getListDepth() > 0) {
							// In versions before 1.18.3, there was be no DataLoader for fields that are lists
							// This behavior is controlled by the generateDataLoaderForLists plugin parameter and the
							// generateDataLoaderForLists directive (that can associated directly to the GraphQL field)
							withDataLoader = ((GenerateServerCodeConfiguration) this.configuration)
									.isGenerateDataLoaderForLists()
									|| null != field.getAppliedDirectives().stream()//
											.filter(directive -> directive.getDirective().getName()
													.equals("generateDataLoaderForLists"))
											.findAny()//
											.orElse(null);
						}

						DataFetcher df = new DataFetcherImpl(newField, dataFetcherDelegate, true, withDataLoader, type);
						this.dataFetchers.add(df);
						newField.setDataFetcher(df);
					}
				}
			} // for

			// If at least one DataFetcher has been created, we register this DataFetchersDelegate
			if (dataFetcherDelegate.getDataFetchers().size() > 0) {
				this.dataFetchersDelegates.add(dataFetcherDelegate);
			}
		}
	}

	/**
	 * Identify each BatchLoader to generate, and attach its {@link DataFetcher} to its {@link DataFetchersDelegate}.
	 * The whole stuff is stored into {@link #batchLoaders}
	 */
	private void initBatchLoaders() {
		if (getConfiguration().getMode().equals(PluginMode.server)) {
			// objectTypes contains both the objects defined in the schema, and the concrete objects created to map the
			// interfaces, along with Enums...

			// We fetch only the objects, here. The interfaces are managed just after
			logger.debug("Init batch loader for objects");
			getObjectTypes().stream().filter(o -> (o.getGraphQlType() == GraphQlType.OBJECT && !o.isInputType()))
					.forEach(o -> initOneBatchLoader(o));

			// Let's go through all interfaces.
			logger.debug("Init batch loader for objects");
			this.interfaceTypes.stream().forEach(i -> initOneBatchLoader(i));
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
		if (type.getRequestType() == null && !isTypeSpringMappingIgnored(type)) {

			logger.debug("Init batch loader for " + type.getName());

			Field id = type.getIdentifier();
			if (id != null) {
				this.batchLoaders.add(new BatchLoaderImpl(type, getDataFetchersDelegate(type, true)));
			}

		} // if
	}

	/**
	 * Build an @{@link JsonDeserialize} annotation with one or more attributes
	 * 
	 * @param contentAs
	 *            contentAs class name
	 * @param using
	 *            using class name
	 * @return annotation string
	 */
	private String buildJsonDeserializeAnnotation(String contentAs, String using) {
		StringBuilder annotationBuf = new StringBuilder();
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
	 * Build an @{@link JsonSerialize} annotation with one or more attributes
	 * 
	 * 
	 * @param using
	 *            using class name
	 * @return annotation string
	 */
	private String buildJsonSerializeAnnotation(String using) {
		StringBuilder annotationBuf = new StringBuilder();
		annotationBuf.append("@JsonSerialize(");
		boolean addComma = false;

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
		if (getConfiguration().getMode().equals(PluginMode.client)) {

			logger.debug("Adding introspection capability");

			/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// First step : add the introspection queries into the existing query. If no query exists, one is created.s
			/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			if (this.queryType == null) {
				logger.debug("The source schema contains no query: creating an empty query type");

				// There was no query. We need to create one. It will contain only the Introspection Query
				this.queryType = new ObjectType(this.DEFAULT_QUERY_NAME, this.configuration, this);
				this.queryType.setName(INTROSPECTION_QUERY);
				this.queryType.setRequestType("query");

				// Let's first add the regular object that'll receive the server response (in the default package)
				getObjectTypes().add(this.queryType);
				this.types.put(this.queryType.getName(), this.queryType);
			}

			// We also need to add the relevant fields into the regular object that matches the query.
			Type objectQuery = getType(this.queryType.getName());
			objectQuery.getFields().add(get__SchemaField(objectQuery));
			objectQuery.getFields().add(get__TypeField(objectQuery));

			/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// Second step: add the __datatype field into every GraphQL type (out of input types)
			// That is : in all regular object types and interfaces.
			/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			logger.debug("Adding __typename to each object");
			for (ObjectType type : getObjectTypes()) {
				if (!type.isInputType()) {
					type.getFields().add(FieldImpl.builder().documentParser(this).name("__typename")
							.fieldTypeAST(
									FieldTypeAST.builder().graphQLTypeSimpleName("String").mandatory(false).build())
							.owningType(type).build());
				}
			}
			logger.debug("Adding __typename to each interface");
			for (InterfaceType type : this.interfaceTypes) {
				type.getFields().add(FieldImpl.builder().documentParser(this).name("__typename")
						.fieldTypeAST(FieldTypeAST.builder().graphQLTypeSimpleName("String").mandatory(false).build())
						.owningType(type).build());
			}
		}
	}

	/**
	 * @param o
	 * @return
	 */
	private FieldImpl get__TypeField(Type o) {
		FieldImpl __type = FieldImpl.builder().documentParser(this).name("__type")
				.fieldTypeAST(FieldTypeAST.builder().graphQLTypeSimpleName("__Type").mandatory(true).build())//
				.owningType(o).build();
		__type.getInputParameters()
				.add(FieldImpl.builder().documentParser(this).name("name")
						.fieldTypeAST(FieldTypeAST.builder().graphQLTypeSimpleName("String").mandatory(true).build())//
						.owningType(o).build());
		return __type;
	}

	/**
	 * @param o
	 * @return
	 */
	private FieldImpl get__SchemaField(Type o) {
		FieldImpl __schema = FieldImpl.builder().documentParser(this).name("__schema")
				.fieldTypeAST(FieldTypeAST.builder().graphQLTypeSimpleName("__Schema").mandatory(true).build())//
				.owningType(o).build();
		return __schema;
	}

	/**
	 * 
	 * Default implementation in the {@link GenerateServerCodeConfiguration} interface, the return the list of mappings
	 * for GraphQL types to ignore, based on the raw value of the <code>ignoredSpringMappings</code> plugin parameter.
	 * That is: there will be no Controller class generated for them by the plugin
	 * 
	 * @return The list of GraphQL type names, extracted from the <code>ignoredSpringMappings</code> plugin parameter.
	 *         The ignored mappings on Fields are ignored.
	 * @see #getIgnoredSpringFieldMappings()
	 */
	boolean isTypeSpringMappingIgnored(ObjectType type) {
		return getTypeSpringMappingIgnored().contains(type.getName());
	}

	/**
	 * 
	 * Default implementation in the {@link GenerateServerCodeConfiguration} interface, the return the list of mappings
	 * for GraphQL fields to ignore, based on the raw value of the <code>ignoredSpringMappings</code> plugin parameter.
	 * That is: there will be no mapping generated for them by the plugin in the Controller for this type.
	 * 
	 * @param field
	 *            The field that we want to know it its GraphQL mapping is ignored or not
	 * 
	 * @return The map that contains the mappings of GraphQL fields that must be ignored by the plugin, that is: there
	 *         will be no mapping generated for them in the Spring GraphQL Controller that is generated by the plugin
	 *         for this type. <br/>
	 *         The key of the map is the GraphQL type's name<br/>
	 *         The value of the map is the GraphQL field's name
	 * 
	 */
	boolean isFieldSpringMappingIgnored(Field field) {
		Set<String> set = getFieldSpringMappingIgnored().get(field.getOwningType().getName());
		return (set == null) ? false : set.contains(field.getName());
	}

	/**
	 * Returns {@link #typeSpringMappingIgnored}, and initialize it if it wasn't already initialized.
	 * 
	 * @return
	 */
	Set<String> getTypeSpringMappingIgnored() { // Let's initialize typeSpringMappingIgnored, if it has not been yet
		if (this.typeSpringMappingIgnored == null) {
			this.typeSpringMappingIgnored = new HashSet<>();
			if (((GenerateServerCodeConfiguration) this.configuration).getIgnoredSpringMappings() != null) {
				StringTokenizer st = new StringTokenizer(
						((GenerateServerCodeConfiguration) this.configuration).getIgnoredSpringMappings(), ", \t\r\n");
				String s;

				while (st.hasMoreElements()) {
					s = st.nextToken();

					// If the ignoredSpringMappings contains a star, then all types that may have a controller must be
					// ignored
					if (s.equals("*")) {
						Stream.concat(getObjectTypes().stream(), getInterfaceTypes().stream())
								.forEach(t -> this.typeSpringMappingIgnored.add(t.getName()));
					}
					// Here, we ignore field mapping
					else if (!s.contains(".")) {
						// s must be a valid GraphQL type
						getType(s);// Would through an exception if s is not a valid typename
						this.typeSpringMappingIgnored.add(s);
					}
				} // while
			} // if (((GenerateServerCodeConfiguration) this.configuration).getIgnoredSpringMappings() != null)
		} // if (this.typeSpringMappingIgnored == null)

		return this.typeSpringMappingIgnored;
	}

	/**
	 * Returns {@link #fieldSpringMappingIgnored}, and initialize it if it wasn't already initialized.
	 * 
	 * @return
	 */
	Map<String, Set<String>> getFieldSpringMappingIgnored() {
		Pattern pattern = Pattern.compile("^(.*)\\.(.*)$");

		// Let's initialize fieldSpringMappingIgnored, if it has not been already
		if (this.fieldSpringMappingIgnored == null) {
			this.fieldSpringMappingIgnored = new HashMap<>();
			Type type;

			if (((GenerateServerCodeConfiguration) this.configuration).getIgnoredSpringMappings() != null) {
				StringTokenizer st = new StringTokenizer(
						((GenerateServerCodeConfiguration) this.configuration).getIgnoredSpringMappings(),
						IGNORED_SPRING_MAPPINGS_SEPARATOR);
				String s;

				while (st.hasMoreElements()) {
					s = st.nextToken();
					// Here, we take into account only field name (so there must be a point to separate the type and the
					// field name)
					Matcher matcher = pattern.matcher(s);
					if (!matcher.matches()) {
						// This string is not a field definition, as it doesn't contain any point
					} else {
						// We've found a string that is something like TYPE.FIELD. Let's check that
						try {
							type = getType(matcher.group(1));// Would through an exception if s is not a valid type name
						} catch (RuntimeException e) {
							throw new RuntimeException("Bad value in the 'ignoredSpringMappings' plugin parameter: "
									+ matcher.group(1) + " is not a valid GraphQL type name", e);
						}
						try {
							type.getField(matcher.group(2));
						} catch (NoSuchFieldException e) {
							throw new RuntimeException(
									"Bad value in the 'ignoredSpringMappings' plugin parameter: " + matcher.group(2)
											+ " is not a valid field name for the GraphQL type " + matcher.group(1),
									e);
						}

						// Ok, we've found a valid couple (typename,fieldname). Let's add this fieldname to the list of
						// ignored fieldnames for this typename
						Set<String> ignoredFieldNames = this.fieldSpringMappingIgnored.get(matcher.group(1));
						if (ignoredFieldNames == null) {
							// No field has been registered yet for this type. Let's register an empty list
							ignoredFieldNames = new HashSet<>();
							this.fieldSpringMappingIgnored.put(matcher.group(1), ignoredFieldNames);
						}
						// Let's add this fieldname to the list of ignored field for this type
						ignoredFieldNames.add(matcher.group(2));
					}
				} // while
			} // if (((GenerateServerCodeConfiguration) this.configuration).getIgnoredSpringMappings() != null)
		} // if (this.fieldSpringMappingIgnored == null)

		return this.fieldSpringMappingIgnored;
	}

	/**
	 * Adds the necessary java import, so that the generated classes compile. <BR/>
	 * The import for the annotation have already been added.
	 */
	private void addImports() {
		this.types.values().parallelStream().forEach(type -> addImportsForOneType(type));
	}

	/**
	 * Add all imports that are needed for this type
	 * 
	 * @param type
	 */
	private void addImportsForOneType(Type type) {
		if (type != null) {

			// Let's loop through all the fields
			for (Field f : type.getFields()) {
				if (f.getFieldTypeAST().getListDepth() > 0) {
					type.addImportForUtilityClasses(getUtilPackageName(), List.class.getName());
				}

				for (Field param : f.getInputParameters()) {
					if (param.getFieldTypeAST().getListDepth() > 0) {
						type.addImportForUtilityClasses(getUtilPackageName(), List.class.getName());
					}
				} // for(inputParameters)
			} // for(Fields)

			// Let's add some common imports
			type.addImportForUtilityClasses(getUtilPackageName(), GraphQLInputParameters.class.getName());

			// Some imports that are only for utility classes
			type.addImportForUtilityClasses(getUtilPackageName(), RequestType.class.getName());

			switch (getConfiguration().getMode()) {
			case client:
				if (getConfiguration().isGenerateJacksonAnnotations()) {
					type.addImportForUtilityClasses(getUtilPackageName(), JsonDeserialize.class.getName());
					type.addImportForUtilityClasses(getUtilPackageName(), JsonProperty.class.getName());
				}
				break;
			case server:
				break;
			default:
				throw new RuntimeException("unexpected plugin mode: " + getConfiguration().getMode().name());
			}
		}
	}

	/**
	 * This method reads all the object and interface types, to identify all the {@link CustomDeserializer} that must be
	 * defined.
	 */
	private void initCustomDeserializers() {
		Map<Type, Integer> maxListLevelPerType = new HashMap<>();
		Stream.concat(getObjectTypes().stream(), this.interfaceTypes.stream())
				// We deserialize data from the response. So there is no custom deserializer for fields that belong to
				// input types. Let's exclude the input types
				.filter((o) -> !o.isInputType())
				// Let's read all their fields
				.flatMap((o) -> o.getFields().stream())
				// Let's store, for each type, the maximum level of list we've found
				.forEach((f) -> {
					// listLevel: 0 for non array GraphQL types, 1 for arrays like [Int], 2 for nested arrays like
					// [[Int]]...
					int listLevel = f.getFieldTypeAST().getListDepth();

					Integer alreadyDefinedListLevel = maxListLevelPerType.get(f.getType());
					if (alreadyDefinedListLevel == null || alreadyDefinedListLevel < listLevel) {
						// The current type is a deeper nested array
						maxListLevelPerType.put(f.getType(), listLevel);
					}
				});

		// We now know the maximum listLevel for each type. We can now define all the necessary custom deserializers
		this.customDeserializers = new ArrayList<>();
		for (Type t : maxListLevelPerType.keySet()) {
			// First step: if this type is a custom scalar, we define its custom deserializer
			CustomDeserializer customScalarDeserializer = null;
			if (t.isCustomScalar()) {
				customScalarDeserializer = new CustomDeserializer(t, t.getName(), t.getClassFullName(),
						((CustomScalar) t).getCustomScalarDefinition(), 0, null);
				this.customDeserializers.add(customScalarDeserializer);
			}

			// Then: we manage all the list levels for the embedded arrays of this type, as found in the GraphQL schema.
			// So we loop from 1 (standard array) to the deepest level of embedded array found this type, as found in
			// the GraphQL schema.
			// found in this model for fields of this type
			CustomDeserializer lowerListLevelCustomDeserializer = customScalarDeserializer;
			for (int i = 1; i <= maxListLevelPerType.get(t); i += 1) {
				CustomDeserializer currentListLevelCustomDeserializer = new CustomDeserializer(t, t.getName(),
						t.getClassFullName(), null, i, lowerListLevelCustomDeserializer);
				this.customDeserializers.add(currentListLevelCustomDeserializer);
				lowerListLevelCustomDeserializer = currentListLevelCustomDeserializer;
			}
		}
	}

	/** This method reads all the input types, to identify all the {@link CustomSerializer} that must be defined. */
	private void initCustomSerializers() {
		Map<Type, Integer> maxListLevelPerType = new HashMap<>();
		getObjectTypes().stream()
				// We serialize data for the request. So only input types are concerned
				.filter((o) -> o.isInputType())
				// Let's read all their fields
				.flatMap((o) -> o.getFields().stream())
				// Only custom scalar fields need a custom serialize
				.filter((f) -> f.getType() instanceof CustomScalarType)
				// Let's store, for each type, the maximum level of list we've found
				.forEach((f) -> {
					// listLevel: 0 for non array GraphQL types, 1 for arrays like [Int], 2 for nested arrays like
					// [[Int]]...
					int listLevel = f.getFieldTypeAST().getListDepth();

					Integer alreadyDefinedListLevel = maxListLevelPerType.get(f.getType());
					if (alreadyDefinedListLevel == null || alreadyDefinedListLevel < listLevel) {
						// The current type is a deeper nested array
						maxListLevelPerType.put(f.getType(), listLevel);
					}
				});

		// We now know the maximum listLevel for each type. We can now define all the necessary custom serializers
		this.customSerializers = new ArrayList<>();
		for (Type t : maxListLevelPerType.keySet()) {
			// We manage all the list levels for the embedded arrays of this type, as found in the GraphQL schema.
			for (int i = 0; i <= maxListLevelPerType.get(t); i += 1) {
				this.customSerializers.add(new CustomSerializer(t.getName(), t.getClassFullName(),
						((CustomScalar) t).getCustomScalarDefinition(), i));
			}
		}
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
	protected String getUtilPackageName() {
		if (getConfiguration().isSeparateUtilityClasses()) {
			return getConfiguration().getPackageName() + "." + UTIL_PACKAGE_NAME;
		} else {
			return getConfiguration().getPackageName();
		}
	}

}