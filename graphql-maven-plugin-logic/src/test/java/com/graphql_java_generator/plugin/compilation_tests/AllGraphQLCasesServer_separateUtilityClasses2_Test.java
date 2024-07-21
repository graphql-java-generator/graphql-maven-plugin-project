package com.graphql_java_generator.plugin.compilation_tests;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;

import org.dataloader.BatchLoaderEnvironment;
import org.dataloader.DataLoader;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.graphql.execution.BatchLoaderRegistry;

import graphql.mavenplugin_notscannedbyspring.AllGraphQLCases_Server_SpringConfiguration_separateUtilityClasses2;
import graphql.schema.DataFetchingEnvironment;

class AllGraphQLCasesServer_separateUtilityClasses2_Test extends AbstractIntegrationTest {

	public AllGraphQLCasesServer_separateUtilityClasses2_Test() {
		super(AllGraphQLCases_Server_SpringConfiguration_separateUtilityClasses2.class);
	}

	@BeforeEach
	public void setUp() {
		this.graphqlTestHelper.checkSchemaStringProvider("allGraphQLCases*.graphqls");
	}

	@Override
	protected void doAdditionalChecks() throws Exception {
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
		// generateBatchLoaderEnvironment
		assertTrue(this.configuration.isGenerateBatchLoaderEnvironment());
		assertNotNull(allFieldCasesControllerClass.getConstructor(BatchLoaderRegistry.class),
				"There should be a constructor, with a BatchLoaderRegistry parameter");
		assertThrows(//
				NoSuchMethodException.class, //
				() -> dataFetchersDelegateAllFieldCases.getMethod(//
						"batchLoader", //
						List.class),
				"There should be no batchLoader(List) method");
		assertNotNull(//
				dataFetchersDelegateAllFieldCases.getMethod(//
						"batchLoader", //
						List.class, //
						BatchLoaderEnvironment.class),
				"There should be a batchLoader(List,BatchLoaderEnvironment) method");

		//////////////////////////////////////////////////////////////////////////////////////
		// generateDataFetcherForEveryFieldsWithArguments
		assertTrue(this.configuration.isGenerateDataFetcherForEveryFieldsWithArguments());
		assertNotNull(
				allFieldCasesControllerClass.getMethod("forname", DataFetchingEnvironment.class, allFieldCasesClass,
						Boolean.class, String.class),
				"There should be a forname(DataFetchingEnvironment,AllFieldCases,Boolean,String) method");
		assertNotNull(
				dataFetchersDelegateAllFieldCases.getMethod("forname", DataFetchingEnvironment.class,
						allFieldCasesClass, Boolean.class, String.class),
				"There should be a forname(DataFetchingEnvironment,AllFieldCasesnBoolean,String) method");

		//////////////////////////////////////////////////////////////////////////////////////
		// generateDataLoaderForLists
		assertTrue(this.configuration.isGenerateDataLoaderForLists());
		// comments field
		assertNotNull(//
				allFieldCasesControllerClass.getMethod(//
						"comments", //
						DataFetchingEnvironment.class, //
						allFieldCasesClass),
				"There should be a comments(DataFetchingEnvironment,AllFieldCases) method (without the DataLoader parameter)");
		assertThrows(//
				NoSuchMethodException.class, //
				() -> allFieldCasesControllerClass.getMethod(//
						"comments", //
						DataFetchingEnvironment.class, //
						DataLoader.class, //
						allFieldCasesClass),
				"There should be no comments(DataFetchingEnvironment,DataLoader,AllFieldCases) method (with the DataLoader parameter)");
		assertNotNull(//
				dataFetchersDelegateAllFieldCases.getMethod(//
						"comments", //
						DataFetchingEnvironment.class, //
						allFieldCasesClass),
				"There should be a comments(DataFetchingEnvironment,AllFieldCases) method (without the DataLoader parameter)");
		assertThrows(NoSuchMethodException.class, //
				() -> dataFetchersDelegateAllFieldCases.getMethod(//
						"comments", //
						DataFetchingEnvironment.class, //
						DataLoader.class, //
						allFieldCasesClass),
				"There should be no comments(DataFetchingEnvironment,DataLoader,AllFieldCases) method (with the DataLoader parameter)");
		//
		// friends field
		assertThrows(NoSuchMethodException.class, //
				() -> allFieldCasesControllerClass.getMethod(//
						"friends", //
						DataFetchingEnvironment.class, //
						allFieldCasesClass),
				"There should be no friends(DataFetchingEnvironment,AllFieldCases) method (without the DataLoader parameter)");
		assertNotNull(//
				allFieldCasesControllerClass.getMethod(//
						"friends", //
						DataFetchingEnvironment.class, //
						DataLoader.class, //
						allFieldCasesClass),
				"There should be a friends(DataFetchingEnvironment,DataLoader,AllFieldCases) method (with the DataLoader parameter)");
		assertThrows(NoSuchMethodException.class, //
				() -> dataFetchersDelegateAllFieldCases.getMethod(//
						"friends", //
						DataFetchingEnvironment.class, //
						allFieldCasesClass),
				"There should be no friends(DataFetchingEnvironment,AllFieldCases) method (without the DataLoader parameter)");
		assertNotNull(//
				dataFetchersDelegateAllFieldCases.getMethod(//
						"friends", //
						DataFetchingEnvironment.class, //
						DataLoader.class, //
						allFieldCasesClass),
				"There should be a friends(DataFetchingEnvironment,DataLoader,AllFieldCases) method (with the DataLoader parameter)");

		//////////////////////////////////////////////////////////////////////////////////////
		// generateJPAAnnotation
		assertTrue(this.configuration.isGenerateJPAAnnotation());
		assertNotNull(allFieldCasesClass.getAnnotation(Entity.class));
		Field id = allFieldCasesClass.getDeclaredField("id");
		assertNotNull(id.getAnnotation(Id.class));
		assertNotNull(id.getAnnotation(GeneratedValue.class));
		Field dates = allFieldCasesClass.getDeclaredField("dates");
		assertNotNull(dates.getAnnotation(Transient.class));
	}

}
