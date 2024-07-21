package com.graphql_java_generator.plugin.compilation_tests;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
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

import graphql.mavenplugin_notscannedbyspring.AllGraphQLCases_Server_SpringConfiguration_separateUtilityClasses;
import graphql.schema.DataFetchingEnvironment;

class AllGraphQLCasesServer_separateUtilityClasses_Test extends AbstractIntegrationTest {

	public AllGraphQLCasesServer_separateUtilityClasses_Test() {
		super(AllGraphQLCases_Server_SpringConfiguration_separateUtilityClasses.class);
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
		assertFalse(this.configuration.isGenerateBatchLoaderEnvironment());
		assertNotNull(allFieldCasesControllerClass.getConstructor(BatchLoaderRegistry.class),
				"There should be a constructor, with a BatchLoaderRegistry parameter");
		assertNotNull(//
				dataFetchersDelegateAllFieldCases.getMethod(//
						"batchLoader", //
						List.class),
				"There should be a batchLoader(List) method");
		assertThrows(//
				NoSuchMethodException.class, //
				() -> dataFetchersDelegateAllFieldCases.getMethod(//
						"batchLoader", //
						List.class, //
						BatchLoaderEnvironment.class),
				"There should be no batchLoader(List,BatchLoaderEnvironment) method");

		//////////////////////////////////////////////////////////////////////////////////////
		// generateDataFetcherForEveryFieldsWithArguments
		assertFalse(this.configuration.isGenerateDataFetcherForEveryFieldsWithArguments());
		assertThrows(//
				NoSuchMethodException.class, //
				() -> allFieldCasesControllerClass.getMethod("forname", DataFetchingEnvironment.class,
						allFieldCasesClass, Boolean.class, String.class),
				"There should be no forname(DataFetchingEnvironment,AllFieldCases,Boolean,String) method");
		assertThrows(//
				NoSuchMethodException.class, //
				() -> dataFetchersDelegateAllFieldCases.getMethod("forname", DataFetchingEnvironment.class,
						allFieldCasesClass, Boolean.class, String.class),
				"There should be no forname(DataFetchingEnvironment,AllFieldCasesnBoolean,String) method");

		//////////////////////////////////////////////////////////////////////////////////////
		// generateDataLoaderForLists
		assertFalse(this.configuration.isGenerateDataLoaderForLists());
		assertNotNull(//
				allFieldCasesControllerClass.getMethod(//
						"friends", //
						DataFetchingEnvironment.class, //
						allFieldCasesClass),
				"There should be a friends(DataFetchingEnvironment,AllFieldCases) method (without the DataLoader parameter)");
		assertThrows(NoSuchMethodException.class, //
				() -> allFieldCasesControllerClass.getMethod(//
						"friends", //
						DataFetchingEnvironment.class, //
						DataLoader.class, //
						allFieldCasesClass),
				"There should be no friends(DataFetchingEnvironment,DataLoader,AllFieldCases) method (with the DataLoader parameter)");
		assertNotNull(//
				dataFetchersDelegateAllFieldCases.getMethod(//
						"friends", //
						DataFetchingEnvironment.class, //
						allFieldCasesClass),
				"There should be a friends(DataFetchingEnvironment,AllFieldCases) method (without the DataLoader parameter)");
		assertThrows(NoSuchMethodException.class, //
				() -> dataFetchersDelegateAllFieldCases.getMethod(//
						"friends", //
						DataFetchingEnvironment.class, //
						DataLoader.class, //
						allFieldCasesClass),
				"There should be no friends(DataFetchingEnvironment,DataLoader,AllFieldCases) method (with the DataLoader parameter)");

		//////////////////////////////////////////////////////////////////////////////////////
		// generateJPAAnnotation
		assertFalse(this.configuration.isGenerateJPAAnnotation());
		assertNull(allFieldCasesClass.getAnnotation(Entity.class));
		Field id = allFieldCasesClass.getDeclaredField("id");
		assertNull(id.getAnnotation(Id.class));
		assertNull(id.getAnnotation(GeneratedValue.class));
		Field dates = allFieldCasesClass.getDeclaredField("dates");
		assertNull(dates.getAnnotation(Transient.class));
	}

}
