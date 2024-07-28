package com.graphql_java_generator.plugin.compilation_tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.dataloader.BatchLoaderEnvironment;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.graphql.data.method.annotation.BatchMapping;

import graphql.GraphQLContext;
import graphql.mavenplugin_notscannedbyspring.AllGraphQLCases_Server_SpringConfiguration_util_batchMapping;

class AllGraphQLCasesServer_util_batchMapping_Test extends AbstractIntegrationTest {

	public AllGraphQLCasesServer_util_batchMapping_Test() {
		super(AllGraphQLCases_Server_SpringConfiguration_util_batchMapping.class);
	}

	@BeforeEach
	public void setUp() {
		this.graphqlTestHelper.checkSchemaStringProvider("allGraphQLCases*.graphqls");
	}

	@Override
	protected void doAdditionalChecks() throws Exception {
		Method method;

		Class<?> queryControllerClass = loadGeneratedClass("MyQueryTypeController", FileType.UTIL);
		assertNotNull(queryControllerClass);
		//
		Class<?> mutationControllerClass = loadGeneratedClass("AnotherMutationTypeController", FileType.UTIL);
		assertNotNull(mutationControllerClass);
		//
		Class<?> subscriptionControllerClass = loadGeneratedClass("TheSubscriptionTypeController", FileType.UTIL);
		assertNotNull(subscriptionControllerClass);
		//
		Class<?> allFieldCasesClass = loadGeneratedClass("AllFieldCases", FileType.POJO);
		assertNotNull(allFieldCasesClass);
		//
		Class<?> allFieldCasesControllerClass = loadGeneratedClass("AllFieldCasesController", FileType.UTIL);
		assertNotNull(allFieldCasesControllerClass);
		//
		Class<?> dataFetchersDelegateAllFieldCases = loadGeneratedClass("DataFetchersDelegateAllFieldCases",
				FileType.UTIL);
		assertNotNull(dataFetchersDelegateAllFieldCases);

		assertTrue(this.configuration.isSeparateUtilityClasses());
		assertTrue(allFieldCasesControllerClass.getPackage().getName().endsWith(".util"));
		assertTrue(dataFetchersDelegateAllFieldCases.getPackage().getName().endsWith(".util"));

		//////////////////////////////////////////////////////////////////////////////////////
		// generateBatchMappingDataFetchers
		assertTrue(this.configuration.isGenerateBatchMappingDataFetchers(),
				"generateBatchMappingDataFetchers should be true in this test");
		//
		assertEquals(0, allFieldCasesControllerClass.getConstructors().length,
				"There should be no constructor in the Controller");
		assertEquals(0,
				Arrays.stream(dataFetchersDelegateAllFieldCases.getMethods())
						.filter(m -> m.getName().equals("batchLoader")).count(),
				"There should be no batchLoader(List) method");

		// query, mutation and subscription should have no method with @BatchMapping annotation
		assertEquals(0,
				Arrays.stream(queryControllerClass.getDeclaredMethods())
						.filter(m -> m.getAnnotation(BatchMapping.class) != null).count(),
				"query, mutation and subscription should have no method with @BatchMapping annotation");
		assertEquals(0,
				Arrays.stream(mutationControllerClass.getDeclaredMethods())
						.filter(m -> m.getAnnotation(BatchMapping.class) != null).count(),
				"query, mutation and subscription should have no method with @BatchMapping annotation");
		assertEquals(0,
				Arrays.stream(subscriptionControllerClass.getDeclaredMethods())
						.filter(m -> m.getAnnotation(BatchMapping.class) != null).count(),
				"query, mutation and subscription should have no method with @BatchMapping annotation");
		//
		// All methods in the controllers and DataFetcherDelegates should have the @BatchMapping annotation
		assertEquals(0,
				Arrays.stream(allFieldCasesControllerClass.getDeclaredMethods())
						.filter(m -> m.getAnnotation(BatchMapping.class) == null).count(),
				"type's controllers have all their methods with @BatchMapping annotation");

		//
		method = allFieldCasesControllerClass.getMethod("oneWithoutFieldParameter", BatchLoaderEnvironment.class,
				GraphQLContext.class, List.class);
		assertNotNull(method,
				"There should be a oneWithoutFieldParameter(BatchLoaderEnvironment,GraphQLContext,List) method");
		assertNotNull(method.getAnnotation(BatchMapping.class));

		fail("Ok for now. But there should be more things to test");
	}

}
