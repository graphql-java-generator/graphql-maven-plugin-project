package com.graphql_java_generator.plugin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.graphql_java_generator.plugin.language.Field;
import com.graphql_java_generator.plugin.language.Relation;
import com.graphql_java_generator.plugin.language.RelationType;
import com.graphql_java_generator.plugin.language.Type;
import com.graphql_java_generator.plugin.language.impl.ObjectType;

import graphql.language.Document;
import graphql.mavenplugin_notscannedbyspring.Forum_Client_SpringConfiguration;

/**
 * 
 * @author etienne-sf
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { Forum_Client_SpringConfiguration.class })
class DocumentParser_Forum_Client_Test {

	@Autowired
	DocumentParser documentParser;

	@SuppressWarnings("unused")
	private Document doc;

	@BeforeEach
	void setUp() throws Exception {
		documentParser.parseDocuments();
	}

	@Test
	@DirtiesContext
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
	@DirtiesContext
	void test_addAnnotations_Topic_client() {
		// Preparation
		Type topic = documentParser.objectTypes.stream().filter(o -> o.getName().equals("Topic")).findFirst().get();

		// Verification
		assertEquals("@GraphQLObjectType(\"Topic\")", topic.getAnnotation());
		int i = 0;
		checkFieldAnnotation(topic.getFields().get(i++), "id",
				"@JsonProperty(\"id\")\n\t@GraphQLScalar(fieldName = \"id\", graphQLTypeName = \"ID\", javaClass = String.class)");
		checkFieldAnnotation(topic.getFields().get(i++), "date", ""//
				+ "@JsonDeserialize(using = CustomScalarDeserializerDate.class)\n" //
				+ "\t@JsonProperty(\"date\")\n" //
				+ "	@GraphQLScalar(fieldName = \"date\", graphQLTypeName = \"Date\", javaClass = Date.class)");
		checkFieldAnnotation(topic.getFields().get(i++), "author", "@JsonProperty(\"author\")\n"//
				+ "\t@GraphQLNonScalar(fieldName = \"author\", graphQLTypeName = \"Member\", javaClass = Member.class)");
		checkFieldAnnotation(topic.getFields().get(i++), "publiclyAvailable", ""//
				+ "@JsonProperty(\"publiclyAvailable\")\n"//
				+ "\t@GraphQLScalar(fieldName = \"publiclyAvailable\", graphQLTypeName = \"Boolean\", javaClass = Boolean.class)");
		checkFieldAnnotation(topic.getFields().get(i++), "nbPosts", "" //
				+ "@JsonProperty(\"nbPosts\")\n"//
				+ "\t@GraphQLScalar(fieldName = \"nbPosts\", graphQLTypeName = \"Int\", javaClass = Integer.class)");
		checkFieldAnnotation(topic.getFields().get(i++), "title", ""//
				+ "@JsonProperty(\"title\")\n"//
				+ "\t@GraphQLScalar(fieldName = \"title\", graphQLTypeName = \"String\", javaClass = String.class)");
		checkFieldAnnotation(topic.getFields().get(i++), "content", ""//
				+ "@JsonProperty(\"content\")\n"//
				+ "\t@GraphQLScalar(fieldName = \"content\", graphQLTypeName = \"String\", javaClass = String.class)");
		checkFieldAnnotation(topic.getFields().get(i++), "posts", ""//
				+ "@JsonDeserialize(contentAs = Post.class)\n"//
				+ "\t@GraphQLInputParameters(names = {\"memberId\", \"memberName\", \"since\"}, types = {\"ID\", \"String\", \"Date\"})\n"
				+ "\t@JsonProperty(\"posts\")\n"//
				+ "\t@GraphQLNonScalar(fieldName = \"posts\", graphQLTypeName = \"Post\", javaClass = Post.class)");
	}

	/** Tests the annotation. We're in Client mode, thanks to the Spring Configuration used for this test */
	@Test
	@DirtiesContext
	void test_addAnnotations_Mutation1_client() {
		// Preparation
		Type mutation = documentParser.objectTypes.stream().filter(o -> o.getName().equals("MutationType")).findFirst()
				.get();

		// Verification
		assertEquals(""//
				+ "@GraphQLQuery(name = \"MutationType\", type = RequestType.mutation)\n"
				+ "@GraphQLObjectType(\"MutationType\")", mutation.getAnnotation());
		int i = 0;
		checkFieldAnnotation(mutation.getFields().get(i++), "createBoard",
				"@GraphQLInputParameters(names = {\"name\", \"publiclyAvailable\"}, types = {\"String\", \"Boolean\"})\n"
						+ "	@JsonProperty(\"createBoard\")\n"
						+ "	@GraphQLNonScalar(fieldName = \"createBoard\", graphQLTypeName = \"Board\", javaClass = Board.class)");
	}

	/** Tests the annotation. We're in Client mode, thanks to the Spring Configuration used for this test */
	@Test
	@DirtiesContext
	void test_addAnnotations_Mutation2_client() {
		// Preparation
		Type mutation = documentParser.mutationTypes.get(0);

		// Verification
		assertEquals("" //
				+ "@GraphQLQuery(name = \"MutationType\", type = RequestType.mutation)\n"
				+ "@GraphQLObjectType(\"MutationType\")", mutation.getAnnotation());
		int i = 0;
		checkFieldAnnotation(mutation.getFields().get(i++), "createBoard",
				"@GraphQLInputParameters(names = {\"name\", \"publiclyAvailable\"}, types = {\"String\", \"Boolean\"})\n"
						+ "	@JsonProperty(\"createBoard\")\n"
						+ "	@GraphQLNonScalar(fieldName = \"createBoard\", graphQLTypeName = \"Board\", javaClass = Board.class)");
	}

	private void checkFieldAnnotation(Field field, String name, String annotation) {
		String msg = "Check annotation for field " + field.getName() + " (client mode)";
		assertEquals(name, field.getName(), msg + " [name]");
		assertEquals(annotation, field.getAnnotation(), msg + " [annotation]");
	}

	/** Tests the Data Fetchers that are listed during parsing */
	@Test
	@DirtiesContext
	void test_initDataFetchers() {
		assertEquals(0, documentParser.dataFetchers.size(), "no data fetcher in client mode");
	}

	@Test
	@DirtiesContext
	void test_checkIntrospectionQueries() {
		assertEquals(1, documentParser.queryTypes.size());
		ObjectType query = documentParser.queryTypes.get(0);

		// Verification
		assertEquals("QueryType", query.getName());
		assertEquals(6, query.getFields().size(), "4 + the 2 introspection queries added");

		int j = 0; // The first query is 0, see ++j below
		// boards: [Board]
		checkField(query, j, "boards", true, false, false, "Board",
				documentParser.pluginConfiguration.getPackageName() + ".Board");
		assertEquals(0, query.getFields().get(j).getInputParameters().size());
		j += 1;
		// nbBoards: Int
		checkField(query, j, "nbBoards", false, false, false, "Int", Integer.class.getName());
		assertEquals(0, query.getFields().get(j).getInputParameters().size());
		j += 1;
		// topics(boardName: String!): [Topic]!
		checkField(query, j, "topics", true, true, false, "Topic",
				documentParser.pluginConfiguration.getPackageName() + ".Topic");
		assertEquals(1, query.getFields().get(j).getInputParameters().size());
		checkInputParameter(query, j, 0, "boardName", false, true, null, "String", String.class.getName(), null);
		j += 1;
		// findTopics(boardName: String!, keyword: [String!]): [Topic]
		checkField(query, j, "findTopics", true, false, false, "Topic",
				documentParser.pluginConfiguration.getPackageName() + ".Topic");
		assertEquals(2, query.getFields().get(j).getInputParameters().size());
		checkInputParameter(query, j, 0, "boardName", false, true, null, "String", String.class.getName(), null);
		checkInputParameter(query, j, 1, "keyword", true, false, true, "String", String.class.getName(), null);
		j += 1;
		// __schema: __Schema!
		checkField(query, j, "__schema", false, true, false, "__Schema",
				documentParser.pluginConfiguration.getPackageName() + ".__Schema");
		assertEquals(0, query.getFields().get(j).getInputParameters().size());
		j += 1;
		// __type(name: String!): __Type
		checkField(query, j, "__type", false, true, false, "__Type",
				documentParser.pluginConfiguration.getPackageName() + ".__Type");
		assertEquals(1, query.getFields().get(j).getInputParameters().size());
		checkInputParameter(query, j, 0, "name", false, true, null, "String", String.class.getName(), null);
		j += 1;
	}

	private void checkField(ObjectType type, int j, String name, boolean list, boolean mandatory, Boolean itemMandatory,
			String typeName, String classname) {
		Field field = type.getFields().get(j);
		String fieldDescForJUnitMessage = "Field n°" + j + " (" + name + ")";

		assertEquals(name, field.getName(), "field name is " + name + " (for " + fieldDescForJUnitMessage + ")");
		assertEquals(list, field.isList(), "field list is " + list + " (for " + fieldDescForJUnitMessage + ")");
		assertEquals(mandatory, field.isMandatory(),
				"field mandatory is " + mandatory + " (for " + fieldDescForJUnitMessage + ")");
		if (itemMandatory != null) {
			assertEquals(itemMandatory, field.isItemMandatory(),
					"field itemMandatory is " + itemMandatory + " (for " + fieldDescForJUnitMessage + ")");
		}

		Type fieldType = field.getType();
		assertEquals(typeName, fieldType.getName(),
				"type name is " + typeName + " (for " + fieldDescForJUnitMessage + ")");
		assertEquals(classname, fieldType.getClassFullName(),
				"Class for field type is " + classname + " (for " + fieldDescForJUnitMessage + ")");
	}

	private void checkNbInputParameter(ObjectType type, int j, int nbInputParameters) {
		assertEquals(nbInputParameters, type.getFields().get(j).getInputParameters().size(),
				"field " + type.getFields().get(j).getName() + " should have " + nbInputParameters + " parameter");
	}

	private void checkInputParameter(ObjectType type, int j, int numParam, String name, boolean list, boolean mandatory,
			Boolean itemMandatory, String typeName, String classname, String defaultValue) {
		Field inputValue = type.getFields().get(j).getInputParameters().get(numParam);

		String intputParamDescForJUnitMessage = "Field n°" + j + " / input param n°" + numParam;

		assertEquals(name, inputValue.getName(),
				type.getName() + " - name is " + name + " (for " + intputParamDescForJUnitMessage + ")");
		assertEquals(list, inputValue.isList(),
				type.getName() + " - list is " + list + " (for " + intputParamDescForJUnitMessage + ")");
		assertEquals(mandatory, inputValue.isMandatory(),
				type.getName() + " - mandatory is " + mandatory + " (for " + intputParamDescForJUnitMessage + ")");
		if (itemMandatory != null) {
			assertEquals(itemMandatory, inputValue.isItemMandatory(), type.getName() + " - itemMandatory is "
					+ itemMandatory + " (for " + intputParamDescForJUnitMessage + ")");
		}

		Type fieldType = inputValue.getType();
		assertEquals(typeName, fieldType.getName(),
				"name is " + typeName + " (for " + intputParamDescForJUnitMessage + ")");
		assertEquals(classname, fieldType.getClassFullName(),
				"Class type is " + classname + " (for " + intputParamDescForJUnitMessage + ")");

		assertEquals(defaultValue, inputValue.getDefaultValue(),
				"Default Value is <" + defaultValue + "> (for " + intputParamDescForJUnitMessage + ")");
	}
}
