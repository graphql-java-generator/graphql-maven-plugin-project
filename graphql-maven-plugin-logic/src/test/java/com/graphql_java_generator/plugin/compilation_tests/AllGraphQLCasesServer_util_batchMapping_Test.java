package com.graphql_java_generator.plugin.compilation_tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.dataloader.BatchLoaderEnvironment;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.graphql.data.method.annotation.BatchMapping;

import graphql.GraphQLContext;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

abstract class AllGraphQLCasesServer_util_batchMapping_Test extends AbstractIntegrationTest {

	public AllGraphQLCasesServer_util_batchMapping_Test(Class<?> clz) {
		super(clz);
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
		Class<?> batchMappingTypeClass = loadGeneratedClass("BatchMappingTypeController", FileType.UTIL);
		assertNotNull(batchMappingTypeClass);
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
		//
		// The tests below are deactivated for now: we still need the Controller's constructor, to declare the
		// DataLoader, in case one field of this type has field parameters.
		//
		// assertEquals(0, batchMappingTypeClass.getConstructors().length,
		// "There should be no constructor in the BatchMappingTypeClass Controller");
		// method = batchMappingTypeClass.getMethod("friends", BatchLoaderEnvironment.class, GraphQLContext.class,
		// List.class);
		// assertNotNull(method);
		// //
		// assertEquals(1, allFieldCasesControllerClass.getConstructors().length,
		// "There should be one constructor in the AllFieldCases Controller");
		// constructor = allFieldCasesControllerClass.getConstructor(BatchLoaderRegistry.class);
		// assertNotNull(constructor);
		// //
		// assertEquals(0,
		// Arrays.stream(dataFetchersDelegateAllFieldCases.getMethods())
		// .filter(m -> m.getName().equals("batchLoader")).count(),
		// "There should be no batchLoader(List) method");

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
		// All fields without argument in the controllers and DataFetcherDelegates should have the @BatchMapping
		// annotation.
		assertEquals(8, Arrays.stream(allFieldCasesControllerClass.getDeclaredMethods())
				.filter(m -> m.getAnnotation(BatchMapping.class) != null).count());
		//
		method = allFieldCasesControllerClass.getMethod("oneWithoutFieldParameter", BatchLoaderEnvironment.class,
				GraphQLContext.class, List.class);
		assertNotNull(method,
				"There should be a oneWithoutFieldParameter(BatchLoaderEnvironment,GraphQLContext,List) method");
		assertNotNull(method.getAnnotation(BatchMapping.class));
		switch (this.configuration.getBatchMappingDataFetcherReturnType()) {
		case COLLECTION:
			assertEquals(Collection.class, method.getReturnType());
			break;
		case FLUX:
			assertEquals(Flux.class, method.getReturnType());
			break;
		case MAP:
			assertEquals(Map.class, method.getReturnType());
			break;
		case MONO_MAP:
			assertEquals(Mono.class, method.getReturnType());
			break;
		default:
			fail("Unexpected value for batchMappingDataFetcherReturnType: "
					+ this.configuration.getBatchMappingDataFetcherReturnType());
		}

		//
		method = allFieldCasesControllerClass.getMethod("dates", BatchLoaderEnvironment.class, GraphQLContext.class,
				List.class);
		assertNotNull(method, "There should be a dates(BatchLoaderEnvironment,GraphQLContext,List) method");
		assertNotNull(method.getAnnotation(BatchMapping.class));
	}

}
