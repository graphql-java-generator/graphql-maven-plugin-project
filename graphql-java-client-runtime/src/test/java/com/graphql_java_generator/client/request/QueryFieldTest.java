package com.graphql_java_generator.client.request;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import com.graphql_java_generator.domain.client.allGraphQLCases.AllFieldCases;
import com.graphql_java_generator.domain.client.allGraphQLCases.AnotherMutationTypeRootResponse;
import com.graphql_java_generator.domain.client.allGraphQLCases.MyQueryTypeResponse;
import com.graphql_java_generator.domain.client.allGraphQLCases.MyQueryTypeRootResponse;
import com.graphql_java_generator.domain.client.allGraphQLCases.TheSubscriptionTypeRootResponse;
import com.graphql_java_generator.domain.client.allGraphQLCases.WithID;
import com.graphql_java_generator.domain.client.allGraphQLCases._break;
import com.graphql_java_generator.domain.client.forum.Board;
import com.graphql_java_generator.domain.client.forum.Post;
import com.graphql_java_generator.domain.client.starwars.MutationTypeRootResponse;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;

@Execution(ExecutionMode.CONCURRENT)
class QueryFieldTest {

	@Test
	void tests_addAlias() throws GraphQLRequestPreparationException {
		// Preparation
		QueryField queryField = new QueryField(AllFieldCases.class);
		Map<Class<?>, Map<String, Field>> aliasFields = new HashMap<>();
		GraphQLRequestPreparationException e;

		// Go, go, go: no alias
		queryField.readTokenizerForResponseDefinition(new QueryTokenizer("id}"), aliasFields);
		assertEquals(0, aliasFields.size());

		// Go, go, go: a valid alias for a field that already exists
		queryField.readTokenizerForResponseDefinition(new QueryTokenizer("idAlias : id }"), aliasFields);
		assertEquals(1, aliasFields.size());
		Class<?> clazz = aliasFields.keySet().iterator().next();
		assertEquals(AllFieldCases.class, clazz, "The alias is set for this class");
		assertEquals(1, aliasFields.get(clazz).size());
		String aliasName = aliasFields.get(clazz).keySet().iterator().next();
		assertEquals("idAlias", aliasName);
		assertEquals("id", aliasFields.get(clazz).get(aliasName).getName());

		// Go, go, go: another valid alias
		queryField.readTokenizerForResponseDefinition(new QueryTokenizer("matrixAlias: matrix}"), aliasFields);
		assertEquals(1, aliasFields.size());
		clazz = aliasFields.keySet().iterator().next();
		assertEquals(AllFieldCases.class, clazz, "The alias is set for this class");
		assertEquals(2, aliasFields.get(clazz).size());
		aliasName = aliasFields.get(clazz).keySet().iterator().next();
		assertEquals("matrixAlias", aliasName);
		assertEquals("matrix", aliasFields.get(clazz).get(aliasName).getName());

		// Some invalid aliases (check for error messages)
		e = assertThrows(GraphQLRequestPreparationException.class, () -> queryField.readTokenizerForResponseDefinition(
				new QueryTokenizer("anotherAlias: thisFieldDoesNotExist}"), aliasFields));
		assertTrue(e.getMessage().contains("'thisFieldDoesNotExist'"));

		e = assertThrows(GraphQLRequestPreparationException.class, () -> queryField
				.readTokenizerForResponseDefinition(new QueryTokenizer("invalid%alias: matrix}"), aliasFields));
		assertTrue(e.getMessage().contains("'invalid%alias'"));

		e = assertThrows(GraphQLRequestPreparationException.class, () -> queryField
				.readTokenizerForResponseDefinition(new QueryTokenizer("matrixAlias: dates}"), aliasFields));
		assertTrue(e.getMessage().contains("'matrixAlias'"),
				"an already defined alias for the same class, but for another field "
						+ " (the received error message is: " + e.getMessage() + ")");

		e = assertThrows(GraphQLRequestPreparationException.class, () -> queryField
				.readTokenizerForResponseDefinition(new QueryTokenizer("matrixAlias: name}"), aliasFields));
		assertTrue(e.getMessage().contains("'matrixAlias'"),
				"an already defined alias for the same class, but for another field"
						+ " (the received error message is: " + e.getMessage() + ")");
		assertTrue(e.getMessage().contains("'name'"),
				"an already defined alias for the same class, but for another field"
						+ " (the received error message is: " + e.getMessage() + ")");
		assertTrue(e.getMessage().contains("'matrix'"),
				"an already defined alias for the same class, but for another field"
						+ " (the received error message is: " + e.getMessage() + ")");

		///////////////////////////////////////////////
		// Some checks for aliases on an interface implemented by this class

		// If the alias is the same for the same field, nothing should happen
		QueryField withIDQueryField = new QueryField(WithID.class);
		withIDQueryField.readTokenizerForResponseDefinition(new QueryTokenizer("idAlias : id }"), aliasFields);
		assertEquals(4, aliasFields.size());
		clazz = AllFieldCases.class;
		assertEquals(2, aliasFields.get(clazz).size());
		Iterator<String> it = aliasFields.get(clazz).keySet().iterator();
		String matrixAliasName = it.next();
		assertEquals("matrixAlias", matrixAliasName);
		assertEquals("matrix", aliasFields.get(clazz).get(matrixAliasName).getName());
		String idAliasName = it.next();
		assertEquals("idAlias", idAliasName);
		assertEquals("id", aliasFields.get(clazz).get(idAliasName).getName());

		// If the alias is the same but for another field, then an error should be thrown
		e = assertThrows(GraphQLRequestPreparationException.class, () -> withIDQueryField
				.readTokenizerForResponseDefinition(new QueryTokenizer("matrixAlias : id }"), aliasFields));
		assertTrue(e.getMessage().contains("'matrixAlias'"),
				"an already defined alias for the same class, but for another field");
		assertTrue(e.getMessage().contains("'id'"),
				"an already defined alias for the same class, but for another field");
		assertTrue(e.getMessage().contains("'matrix'"),
				"an already defined alias for the same class, but for another field");
	}

	@Test
	void testIsScalar() throws GraphQLRequestPreparationException {
		assertFalse(new QueryField(MyQueryTypeRootResponse.class, "query").isScalar());
		assertFalse(new QueryField(MutationTypeRootResponse.class, "mutation").isScalar());

		assertFalse(new QueryField(MyQueryTypeResponse.class, "aBreak").isScalar());
		assertFalse(new QueryField(Post.class, "author").isScalar());

		// Interface
		assertFalse(new QueryField(MyQueryTypeResponse.class, "withEnum").isScalar());
		// Union
		assertFalse(new QueryField(MyQueryTypeResponse.class, "withEnum").isScalar());

		// Boolean
		assertTrue(new QueryField(Board.class, "publiclyAvailable").isScalar());
		// custom scalar
		assertTrue(new QueryField(Post.class, "date").isScalar());
		// enum
		assertTrue(new QueryField(_break.class, "case").isScalar());
		// ID
		assertTrue(new QueryField(Post.class, "id").isScalar());
		// String
		assertTrue(new QueryField(Post.class, "title").isScalar());
	}

	@Test
	void testIsQueryLevel() throws GraphQLRequestPreparationException {
		assertTrue(new QueryField(MyQueryTypeRootResponse.class, "query").isQueryLevel());
		assertTrue(new QueryField(AnotherMutationTypeRootResponse.class, "mutation").isQueryLevel());
		assertTrue(new QueryField(TheSubscriptionTypeRootResponse.class, "subscription").isQueryLevel());

		assertFalse(new QueryField(MyQueryTypeResponse.class, "aBreak").isQueryLevel());
		assertFalse(new QueryField(_break.class, "case").isQueryLevel());
	}

}
