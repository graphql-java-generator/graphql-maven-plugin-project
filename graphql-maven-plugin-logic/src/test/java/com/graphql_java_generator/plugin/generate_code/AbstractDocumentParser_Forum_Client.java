package com.graphql_java_generator.plugin.generate_code;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.context.support.AbstractApplicationContext;

import com.graphql_java_generator.plugin.language.Field;
import com.graphql_java_generator.plugin.language.Relation;
import com.graphql_java_generator.plugin.language.RelationType;
import com.graphql_java_generator.plugin.language.Type;
import com.graphql_java_generator.plugin.language.impl.ObjectType;
import com.graphql_java_generator.plugin.test.helper.GraphQLConfigurationTestHelper;

/**
 * 
 * @author etienne-sf
 */
@Execution(ExecutionMode.CONCURRENT)
abstract class AbstractDocumentParser_Forum_Client {

	AbstractApplicationContext ctx = null;
	GenerateCodeDocumentParser documentParser;
	GenerateCodeGenerator codeGenerator;
	GraphQLConfigurationTestHelper configuration;

	@AfterEach
	void cleanup() {
		if (this.ctx != null) {
			this.ctx.close();
		}
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_initRelations() {

		// Verification
		assertEquals(19, this.documentParser.relations.size(), "nb relations found");

		// The Relations are found in different orders, depending on the source
		// (graphqls or json file). So we manage sall the relations, in any order:
		for (Relation relation : this.documentParser.relations) {
			String field = relation.getObjectType().getName() + "." + relation.getField().getName();

			if (field.startsWith("__")) {
				// not check of the introspection schema
			} else {
				switch (field) {
				case "Board.topics":
					checkRelation(relation, "Board", "topics", "Topic", null, RelationType.OneToMany, true);
					break;
				case "Topic.author":
					checkRelation(relation, "Topic", "author", "Member", null, RelationType.ManyToOne, true);
					break;
				case "Topic.posts":
					checkRelation(relation, "Topic", "posts", "Post", null, RelationType.OneToMany, true);
					break;
				case "Post.author":
					checkRelation(relation, "Post", "author", "Member", null, RelationType.ManyToOne, true);
					break;
				default:
					fail("unexpected case: " + field);
				}
			}
		}
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

	/**
	 * Tests the annotation. We're in Client mode, thanks to the Spring
	 * Configuration used for this test
	 */
	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_addAnnotations_Topic_client() {
		// Preparation
		Type topic = this.documentParser.getObjectTypes().stream().filter(o -> o.getName().equals("Topic")).findFirst()
				.get();

		// Verification
		assertEquals("@GraphQLObjectType(\"Topic\")", topic.getAnnotation());
		int i = 0;
		checkFieldAnnotation(topic.getFields().get(i++), "id",
				"@JsonProperty(\"id\")\n\t@GraphQLScalar( fieldName = \"id\", graphQLTypeSimpleName = \"ID\", javaClass = java.lang.String.class, listDepth = 0)");
		checkFieldAnnotation(topic.getFields().get(i++), "date", ""//
				+ "@JsonProperty(\"date\")\n" //
				+ "\t@JsonDeserialize(using = CustomJacksonDeserializers.Date.class)\n" //
				+ "\t@GraphQLScalar( fieldName = \"date\", graphQLTypeSimpleName = \"Date\", javaClass = java.util.Date.class, listDepth = 0)");
		checkFieldAnnotation(topic.getFields().get(i++), "author", "@JsonProperty(\"author\")\n"//
				+ "\t@GraphQLNonScalar( fieldName = \"author\", graphQLTypeSimpleName = \"Member\", javaClass = org.junittest.forum_client_springconfiguration.Member.class, listDepth = 0)");
		checkFieldAnnotation(topic.getFields().get(i++), "publiclyAvailable", ""//
				+ "@JsonProperty(\"publiclyAvailable\")\n"//
				+ "\t@GraphQLScalar( fieldName = \"publiclyAvailable\", graphQLTypeSimpleName = \"Boolean\", javaClass = java.lang.Boolean.class, listDepth = 0)");
		checkFieldAnnotation(topic.getFields().get(i++), "nbPosts", "" //
				+ "@JsonProperty(\"nbPosts\")\n"//
				+ "\t@GraphQLScalar( fieldName = \"nbPosts\", graphQLTypeSimpleName = \"Int\", javaClass = java.lang.Integer.class, listDepth = 0)");
		checkFieldAnnotation(topic.getFields().get(i++), "title", ""//
				+ "@JsonProperty(\"title\")\n"//
				+ "\t@GraphQLScalar( fieldName = \"title\", graphQLTypeSimpleName = \"String\", javaClass = java.lang.String.class, listDepth = 0)");
		checkFieldAnnotation(topic.getFields().get(i++), "content", ""//
				+ "@JsonProperty(\"content\")\n"//
				+ "\t@GraphQLScalar( fieldName = \"content\", graphQLTypeSimpleName = \"String\", javaClass = java.lang.String.class, listDepth = 0)");
		checkFieldAnnotation(topic.getFields().get(i++), "posts", ""//
				+ "@JsonProperty(\"posts\")\n"//
				+ "\t@JsonDeserialize(using = CustomJacksonDeserializers.ListPost.class)\n"//
				+ "\t@GraphQLInputParameters(names = {\"memberId\", \"memberName\", \"since\"}, types = {\"ID\", \"String\", \"Date\"}, mandatories = {false, false, true}, listDepths = {0, 0, 0}, itemsMandatory = {false, false, false})\n"
				+ "\t@GraphQLNonScalar( fieldName = \"posts\", graphQLTypeSimpleName = \"Post\", javaClass = org.junittest.forum_client_springconfiguration.Post.class, listDepth = 1)");
	}

	/**
	 * Tests the annotation. We're in Client mode, thanks to the Spring
	 * Configuration used for this test
	 */
	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_addAnnotations_Mutation1_client() {
		// Preparation
		Type mutation = this.documentParser.getObjectTypes().stream().filter(o -> o.getName().equals("Mutation"))
				.findFirst().get();

		// Verification
		assertEquals(""//
				+ "@GraphQLQuery(name = \"Mutation\", type = RequestType.mutation)\n"
				+ "@GraphQLObjectType(\"Mutation\")", mutation.getAnnotation());
		int i = 0;
		checkFieldAnnotation(mutation.getFields().get(i++), "createBoard", ""//
				+ "@JsonProperty(\"createBoard\")\n"
				+ "	@GraphQLInputParameters(names = {\"name\", \"publiclyAvailable\"}, types = {\"String\", \"Boolean\"}, mandatories = {true, false}, listDepths = {0, 0}, itemsMandatory = {false, false})\n"
				+ "	@GraphQLNonScalar( fieldName = \"createBoard\", graphQLTypeSimpleName = \"Board\", javaClass = org.junittest.forum_client_springconfiguration.Board.class, listDepth = 0)");
	}

	/**
	 * Tests the annotation. We're in Client mode, thanks to the Spring
	 * Configuration used for this test
	 */
	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_addAnnotations_Mutation2_client() {
		// Preparation
		Type mutation = this.documentParser.getMutationType();

		// Verification
		assertEquals("" //
				+ "@GraphQLQuery(name = \"Mutation\", type = RequestType.mutation)\n"
				+ "@GraphQLObjectType(\"Mutation\")", mutation.getAnnotation());
		int i = 0;
		checkFieldAnnotation(mutation.getFields().get(i++), "createBoard", ""//
				+ "@JsonProperty(\"createBoard\")\n"
				+ "\t@GraphQLInputParameters(names = {\"name\", \"publiclyAvailable\"}, types = {\"String\", \"Boolean\"}, mandatories = {true, false}, listDepths = {0, 0}, itemsMandatory = {false, false})\n"
				+ "\t@GraphQLNonScalar( fieldName = \"createBoard\", graphQLTypeSimpleName = \"Board\", javaClass = org.junittest.forum_client_springconfiguration.Board.class, listDepth = 0)");
	}

	/** Tests the Data Fetchers that are listed during parsing */
	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_initDataFetchers() {
		assertEquals(0, this.documentParser.dataFetchers.size(), "no data fetcher in client mode");
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void test_checkIntrospectionQueries() {
		assertNotNull(this.documentParser.getQueryType());
		ObjectType query = this.documentParser.getQueryType();

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
		ObjectType post = (ObjectType) this.documentParser.getType("Post");
		assertNotNull(post);

		// The java class for scalar should not be here. It can lead to name collision,
		// for instance with java.util.Date
		// and java.sql.Date.
		// So java class for scalars should used only with full classname (not with
		// imports).
		assertFalse(post.getImports().contains("java.util.Date"), "expecting java.util.Date");

		assertFalse(post.getImports().contains("com.fasterxml.jackson.annotation.JsonProperty"),
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
