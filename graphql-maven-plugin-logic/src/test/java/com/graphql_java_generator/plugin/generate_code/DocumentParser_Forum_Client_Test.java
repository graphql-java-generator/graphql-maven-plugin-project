package com.graphql_java_generator.plugin.generate_code;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import com.graphql_java_generator.plugin.language.Field;
import com.graphql_java_generator.plugin.language.Relation;
import com.graphql_java_generator.plugin.language.RelationType;
import com.graphql_java_generator.plugin.language.Type;
import com.graphql_java_generator.plugin.language.impl.ObjectType;
import com.graphql_java_generator.plugin.test.helper.GraphQLConfigurationTestHelper;

import graphql.mavenplugin_notscannedbyspring.Forum_Client_SpringConfiguration;

/**
 * 
 * @author etienne-sf
 */
@Execution(ExecutionMode.CONCURRENT)
class DocumentParser_Forum_Client_Test {

	AbstractApplicationContext ctx = null;
	GenerateCodeDocumentParser documentParser;
	GenerateCodeGenerator codeGenerator;
	GraphQLConfigurationTestHelper configuration;

	@BeforeEach
	void setUp() throws Exception {
		ctx = new AnnotationConfigApplicationContext(Forum_Client_SpringConfiguration.class);
		documentParser = ctx.getBean(GenerateCodeDocumentParser.class);
		codeGenerator = ctx.getBean(GenerateCodeGenerator.class);
		configuration = ctx.getBean(GraphQLConfigurationTestHelper.class);

		documentParser.parseDocuments();
	}

	@AfterEach
	void cleanup() {
		if (ctx != null) {
			ctx.close();
		}
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_initRelations() {

		// Verification
		assertEquals(19, documentParser.relations.size(), "nb relations found");

		// The Relation are found in the order of their declaratioon in the GraphQL schema definition.
		// Let's check that
		int i = 0;
		checkRelation(documentParser.relations.get(i++), "Board", "topics", "Topic", null, RelationType.OneToMany,
				true);
		checkRelation(documentParser.relations.get(i++), "Topic", "author", "Member", null, RelationType.ManyToOne,
				true);
		checkRelation(documentParser.relations.get(i++), "Topic", "posts", "Post", null, RelationType.OneToMany, true);
		checkRelation(documentParser.relations.get(i++), "Post", "author", "Member", null, RelationType.ManyToOne,
				true);
	}

	private void checkRelation(Relation relation, String objectSource, String fieldSource, String typeTarget,
			String targetField, RelationType manytoone, boolean b) {
		String msg = "Relation: " + objectSource + "." + fieldSource + "->" + typeTarget + " (" + manytoone + ")";
		assertNotNull(relation, msg + " [relation not null");
		assertEquals(objectSource, relation.getObjectType().getName(), msg + " [objectSource]");
		assertEquals(fieldSource, relation.getField().getName(), msg + " [fieldSource]");
		assertEquals(typeTarget, relation.getField().getType().getName(), msg + " [typeTarget]");
		if (targetField == null) {
			assertNull(relation.getMappedyBy(), msg + " [mapped by]");
		} else {
			assertEquals(targetField, relation.getMappedyBy().getName(), msg + " [mapped by]");
		}
	}

	/** Tests the annotation. We're in Client mode, thanks to the Spring Configuration used for this test */
	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_addAnnotations_Topic_client() {
		// Preparation
		Type topic = documentParser.getObjectTypes().stream().filter(o -> o.getName().equals("Topic")).findFirst()
				.get();

		// Verification
		assertEquals("@GraphQLObjectType(\"Topic\")", topic.getAnnotation());
		int i = 0;
		checkFieldAnnotation(topic.getFields().get(i++), "id",
				"@JsonProperty(\"id\")\n\t@GraphQLScalar(fieldName = \"id\", graphQLTypeSimpleName = \"ID\", javaClass = java.lang.String.class)");
		checkFieldAnnotation(topic.getFields().get(i++), "date", ""//
				+ "@JsonProperty(\"date\")\n" //
				+ "\t@JsonDeserialize(using = CustomJacksonDeserializers.Date.class)\n" //
				+ "\t@GraphQLScalar(fieldName = \"date\", graphQLTypeSimpleName = \"Date\", javaClass = java.util.Date.class)");
		checkFieldAnnotation(topic.getFields().get(i++), "author", "@JsonProperty(\"author\")\n"//
				+ "\t@GraphQLNonScalar(fieldName = \"author\", graphQLTypeSimpleName = \"Member\", javaClass = org.graphql.mavenplugin.junittest.forum_client_springconfiguration.Member.class)");
		checkFieldAnnotation(topic.getFields().get(i++), "publiclyAvailable", ""//
				+ "@JsonProperty(\"publiclyAvailable\")\n"//
				+ "\t@GraphQLScalar(fieldName = \"publiclyAvailable\", graphQLTypeSimpleName = \"Boolean\", javaClass = java.lang.Boolean.class)");
		checkFieldAnnotation(topic.getFields().get(i++), "nbPosts", "" //
				+ "@JsonProperty(\"nbPosts\")\n"//
				+ "\t@GraphQLScalar(fieldName = \"nbPosts\", graphQLTypeSimpleName = \"Int\", javaClass = java.lang.Integer.class)");
		checkFieldAnnotation(topic.getFields().get(i++), "title", ""//
				+ "@JsonProperty(\"title\")\n"//
				+ "\t@GraphQLScalar(fieldName = \"title\", graphQLTypeSimpleName = \"String\", javaClass = java.lang.String.class)");
		checkFieldAnnotation(topic.getFields().get(i++), "content", ""//
				+ "@JsonProperty(\"content\")\n"//
				+ "\t@GraphQLScalar(fieldName = \"content\", graphQLTypeSimpleName = \"String\", javaClass = java.lang.String.class)");
		checkFieldAnnotation(topic.getFields().get(i++), "posts", ""//
				+ "@JsonProperty(\"posts\")\n"//
				+ "\t@JsonDeserialize(using = CustomJacksonDeserializers.ListPost.class)\n"//
				+ "\t@GraphQLInputParameters(names = {\"memberId\", \"memberName\", \"since\"}, types = {\"ID\", \"String\", \"Date\"}, mandatories = {false, false, true}, listDepths = {0, 0, 0}, itemsMandatory = {false, false, false})\n"
				+ "\t@GraphQLNonScalar(fieldName = \"posts\", graphQLTypeSimpleName = \"Post\", javaClass = org.graphql.mavenplugin.junittest.forum_client_springconfiguration.Post.class)");
	}

	/** Tests the annotation. We're in Client mode, thanks to the Spring Configuration used for this test */
	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_addAnnotations_Mutation1_client() {
		// Preparation
		Type mutation = documentParser.getObjectTypes().stream().filter(o -> o.getName().equals("Mutation")).findFirst()
				.get();

		// Verification
		assertEquals(""//
				+ "@GraphQLQuery(name = \"Mutation\", type = RequestType.mutation)\n"
				+ "@GraphQLObjectType(\"Mutation\")", mutation.getAnnotation());
		int i = 0;
		checkFieldAnnotation(mutation.getFields().get(i++), "createBoard", ""//
				+ "@JsonProperty(\"createBoard\")\n"
				+ "	@GraphQLInputParameters(names = {\"name\", \"publiclyAvailable\"}, types = {\"String\", \"Boolean\"}, mandatories = {true, false}, listDepths = {0, 0}, itemsMandatory = {false, false})\n"
				+ "	@GraphQLNonScalar(fieldName = \"createBoard\", graphQLTypeSimpleName = \"Board\", javaClass = org.graphql.mavenplugin.junittest.forum_client_springconfiguration.Board.class)");
	}

	/** Tests the annotation. We're in Client mode, thanks to the Spring Configuration used for this test */
	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_addAnnotations_Mutation2_client() {
		// Preparation
		Type mutation = documentParser.getMutationType();

		// Verification
		assertEquals("" //
				+ "@GraphQLQuery(name = \"Mutation\", type = RequestType.mutation)\n"
				+ "@GraphQLObjectType(\"Mutation\")", mutation.getAnnotation());
		int i = 0;
		checkFieldAnnotation(mutation.getFields().get(i++), "createBoard", ""//
				+ "@JsonProperty(\"createBoard\")\n"
				+ "\t@GraphQLInputParameters(names = {\"name\", \"publiclyAvailable\"}, types = {\"String\", \"Boolean\"}, mandatories = {true, false}, listDepths = {0, 0}, itemsMandatory = {false, false})\n"
				+ "\t@GraphQLNonScalar(fieldName = \"createBoard\", graphQLTypeSimpleName = \"Board\", javaClass = org.graphql.mavenplugin.junittest.forum_client_springconfiguration.Board.class)");
	}

	/** Tests the Data Fetchers that are listed during parsing */
	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_initDataFetchers() {
		assertEquals(0, documentParser.dataFetchers.size(), "no data fetcher in client mode");
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_checkIntrospectionQueries() {
		assertNotNull(documentParser.getQueryType());
		ObjectType query = documentParser.getQueryType();

		// Verification
		assertEquals("Query", query.getName());
		assertEquals(7, query.getFields().size(), "4 + the 2 introspection queries added + __typename");

		int j = 0; // The first query is 0, see ++j below
		// boards: [Board]
		checkField(query, j, "boards", 1, false, false, "Board", "Board");
		assertEquals(0, query.getFields().get(j).getInputParameters().size());
		j += 1;
		// nbBoards: Int
		checkField(query, j, "nbBoards", 0, false, false, "Int", Integer.class.getSimpleName());
		assertEquals(0, query.getFields().get(j).getInputParameters().size());
		j += 1;
		// topics(boardName: String!): [Topic]!
		checkField(query, j, "topics", 1, true, false, "Topic", "Topic");
		assertEquals(1, query.getFields().get(j).getInputParameters().size());
		checkInputParameter(query, j, 0, "boardName", 0, true, null, "String", String.class.getSimpleName(), null);
		j += 1;
		// findTopics(boardName: String!, keyword: [String!]): [Topic]
		checkField(query, j, "findTopics", 1, false, false, "Topic", "Topic");
		assertEquals(2, query.getFields().get(j).getInputParameters().size());
		checkInputParameter(query, j, 0, "boardName", 0, true, null, "String", String.class.getSimpleName(), null);
		checkInputParameter(query, j, 1, "keyword", 1, false, true, "String", String.class.getSimpleName(), null);
		j += 1;
		// __schema: __Schema!
		checkField(query, j, "__schema", 0, true, false, "__Schema", "__Schema");
		assertEquals(0, query.getFields().get(j).getInputParameters().size());
		j += 1;
		// __type(name: String!): __Type
		checkField(query, j, "__type", 0, true, false, "__Type", "__Type");
		assertEquals(1, query.getFields().get(j).getInputParameters().size());
		checkInputParameter(query, j, 0, "name", 0, true, null, "String", String.class.getSimpleName(), null);
		j += 1;
		// __typename: String!
		checkField(query, j, "__typename", 0, false, false, "String", "String");
		assertEquals(0, query.getFields().get(j).getInputParameters().size());
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_addImport() {
		ObjectType post = (ObjectType) documentParser.getType("Post");
		assertNotNull(post);

		// The java class for scalar should not be here. It can lead to name collision, for instance with java.util.Date
		// and java.sql.Date.
		// So java class for scalars should used only with full classname (not with imports).
		assertFalse(post.getImports().contains("java.util.Date"), "expecting java.util.Date");

		assertTrue(post.getImports().contains("com.fasterxml.jackson.annotation.JsonProperty"),
				"expecting com.fasterxml.jackson.annotation.JsonProperty");
		assertTrue(post.getImports().contains("com.fasterxml.jackson.databind.annotation.JsonDeserialize"),
				"expecting com.fasterxml.jackson.databind.annotation.JsonDeserialize");
		assertTrue(post.getImports().contains("com.graphql_java_generator.annotation.GraphQLNonScalar"),
				"expecting com.graphql_java_generator.annotation.GraphQLNonScalar");
		assertTrue(post.getImports().contains("com.graphql_java_generator.annotation.GraphQLObjectType"),
				"expecting com.graphql_java_generator.annotation.GraphQLObjectType");
		assertTrue(post.getImports().contains("com.graphql_java_generator.annotation.GraphQLScalar"),
				"expecting com.graphql_java_generator.annotation.GraphQLScalar");
	}

	/**
	 * Checks that the spring Autoconfiguration and META-INF/spring.factories are properly copied with the runtime
	 * sources
	 * 
	 * @throws IOException
	 */
	@Test
	void check_GraphQLJavaRuntimeProperties() throws IOException {
		// Preparation
		// We need to activate the copy of the runtime sources
		configuration.setCopyRuntimeSources(true);
		// Let's copy these file in a dedicated folder
		File parentFile = new File(configuration.targetSourceFolder.getParentFile().getParentFile(),
				"DocumentParser_Forum_Client_Test");
		File targetParentFolder = new File(parentFile, "autoconfiguration_test");
		File targetSourceFolder = new File(targetParentFolder, "java");
		configuration.setTargetSourceFolder(targetSourceFolder);
		File targetResourceFolder = new File(targetParentFolder, "resources");
		configuration.setTargetResourceFolder(targetResourceFolder);

		// Go, go, go
		codeGenerator.copyRuntimeSources();

		// Verification
		File javaRuntimeFile = new File(targetResourceFolder, "graphql-java-runtime.properties");
		assertTrue(javaRuntimeFile.exists(), "graphql-java-runtime should exist");
		assertTrue(javaRuntimeFile.isFile(), "graphql-java-runtime should be a file");
	}

	private void checkField(ObjectType type, int j, String name, int list, boolean mandatory, Boolean itemMandatory,
			String typeName, String classSimpleName) {
		Field field = type.getFields().get(j);
		String fieldDescForJUnitMessage = "Field n°" + j + " (" + name + ")";

		assertEquals(name, field.getName(), "field name is " + name + " (for " + fieldDescForJUnitMessage + ")");
		assertEquals(list, field.getFieldTypeAST().getListDepth(),
				"field list is " + list + " (for " + fieldDescForJUnitMessage + ")");
		assertEquals(mandatory, field.getFieldTypeAST().isMandatory(),
				"field mandatory is " + mandatory + " (for " + fieldDescForJUnitMessage + ")");
		if (list > 0 && itemMandatory != null) {
			assertEquals(itemMandatory, field.getFieldTypeAST().getListItemFieldTypeAST().isMandatory(),
					"field itemMandatory is " + itemMandatory + " (for " + fieldDescForJUnitMessage + ")");
		}

		Type fieldType = field.getType();
		assertEquals(typeName, fieldType.getName(),
				"type name is " + typeName + " (for " + fieldDescForJUnitMessage + ")");
		assertEquals(classSimpleName, fieldType.getClassSimpleName(),
				"Class for field type is " + classSimpleName + " (for " + fieldDescForJUnitMessage + ")");
	}

	private void checkInputParameter(ObjectType type, int j, int numParam, String name, int list, boolean mandatory,
			Boolean itemMandatory, String typeName, String classSimpleName, String defaultValue) {
		Field inputValue = type.getFields().get(j).getInputParameters().get(numParam);

		String intputParamDescForJUnitMessage = "Field n°" + j + " / input param n°" + numParam;

		assertEquals(name, inputValue.getName(),
				type.getName() + " - name is " + name + " (for " + intputParamDescForJUnitMessage + ")");
		assertEquals(list, inputValue.getFieldTypeAST().getListDepth(),
				type.getName() + " - list is " + list + " (for " + intputParamDescForJUnitMessage + ")");
		assertEquals(mandatory, inputValue.getFieldTypeAST().isMandatory(),
				type.getName() + " - mandatory is " + mandatory + " (for " + intputParamDescForJUnitMessage + ")");
		if (itemMandatory != null) {
			assertEquals(itemMandatory, inputValue.getFieldTypeAST().getListItemFieldTypeAST().isMandatory(),
					type.getName() + " - itemMandatory is " + itemMandatory + " (for " + intputParamDescForJUnitMessage
							+ ")");
		}

		Type fieldType = inputValue.getType();
		assertEquals(typeName, fieldType.getName(),
				"name is " + typeName + " (for " + intputParamDescForJUnitMessage + ")");
		assertEquals(classSimpleName, fieldType.getClassSimpleName(),
				"Class type is " + classSimpleName + " (for " + intputParamDescForJUnitMessage + ")");

		assertEquals(defaultValue, inputValue.getDefaultValue(),
				"Default Value is <" + defaultValue + "> (for " + intputParamDescForJUnitMessage + ")");
	}

	private void checkFieldAnnotation(Field field, String name, String annotation) {
		String msg = "Check annotation for field " + field.getName() + " (client mode)";
		assertEquals(name, field.getName(), msg + " [name]");
		assertEquals(annotation, field.getAnnotation(), msg + " [annotation]");
	}
}
