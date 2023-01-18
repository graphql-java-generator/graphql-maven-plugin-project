/**
 * 
 */
package com.graphql_java_generator.annotation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.allGraphQLCases.client.CEP_Episode_CES;
import org.allGraphQLCases.client.CINP_AllFieldCasesInput_CINS;
import org.allGraphQLCases.client.CIP_AllFieldCasesInterface_CIS;
import org.allGraphQLCases.client.CTP_AllFieldCases_CTS;
import org.allGraphQLCases.client.CUP_AnyCharacter_CUS;
import org.allGraphQLCases.client.util.MyQueryTypeExecutorAllGraphQLCases;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * Integration test to check that the {@link GraphQLDirective}s annotations have been properly generated, and are
 * available at runtime. <br/>
 * This test needs that the code has been generated and added to the classpath. There would be hack to add folders in
 * the classpath at runtime, during the 'graphql-maven-plugin-logic' integration tests. But it's ugly, and it would
 * generate conflicts between the client and server generated code. It's the reason of this test begin here.<br/>
 * The same test exists in the graphql-maven-plugin-samples-allGraphQLCases-server module
 * 
 * @author etienne-sf
 */
public class GraphQLDirectiveTest {

	static class ExpectedDirective {
		final String name;
		List<String> parameterNames = new ArrayList<>();
		List<String> parameterTypes = new ArrayList<>();
		List<String> parameterValues = new ArrayList<>();

		ExpectedDirective(String name) {
			this.name = name;
		}

		ExpectedDirective withParamName(String paramName) {
			parameterNames.add(paramName);
			return this;
		}

		ExpectedDirective withParamType(String paramType) {
			parameterTypes.add(paramType);
			return this;
		}

		ExpectedDirective withParamValue(String paramValue) {
			parameterValues.add(paramValue);
			return this;
		}
	};

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Start of the test's code (specific to client mode)
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Test
	void testGraphQLDirective_onEveryExecutorMethodAndArguments() {
		//////////////////////////////////////////////////////////////////////////////////////////
		// Step 1 : the class annotations
		checkClassDirective(null, MyQueryTypeExecutorAllGraphQLCases.class);

		//////////////////////////////////////////////////////////////////////////////////////////
		// Step2 : the method annotations
		checkMethodDirective(//
				Arrays.asList(new ExpectedDirective("@testDirective")//
						.withParamName("value").withParamName("anotherValue")//
						.withParamType("String!").withParamType("String")//
						.withParamValue("on withOneOptionalParam").withParamValue("something else")), //
				MyQueryTypeExecutorAllGraphQLCases.class, //
				Arrays.asList("withOneOptionalParamWithBindValues", "withOneOptionalParam",
						"withOneOptionalParamWithBindValues"));

		//////////////////////////////////////////////////////////////////////////////////////////
		// Step3 : the parameter annotations
		checkMethodArgumentDirective(//
				Arrays.asList(new ExpectedDirective("@testDirective").withParamName("value").withParamType("String!")
						.withParamValue("test for issue 162")), //
				MyQueryTypeExecutorAllGraphQLCases.class, //
				Arrays.asList("withOneOptionalParamWithBindValues", "withOneOptionalParam",
						"withOneOptionalParamWithBindValues"), //
				"character");
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Start of the test's code (common to client and server mode)
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Disabled
	@Test
	void testGraphQLDirective_onCustomScalars() {
		fail("No GraphQLDirective on custom scalar yet");
	}

	@Test
	void testGraphQLDirective_onEnumObjetsAndValues() throws Exception {
		//////////////////////////////////////////////////////////////////////////////////////////
		// Step 1 : the enum annotations
		checkClassDirective(//
				Arrays.asList(//
						new ExpectedDirective("@testDirective")//
								.withParamName("value").withParamName("anotherValue").withParamName("anInt")
								.withParamName("aFloat").withParamName("aBoolean").withParamName("anID")
								.withParamName("anEnum").withParamName("aCustomScalarDate").withParamName("anArray")
								.withParamName("anObject")
								//
								.withParamType("String!").withParamType("String").withParamType("Int")
								.withParamType("Float").withParamType("Boolean").withParamType("ID")
								.withParamType("Episode").withParamType("Date").withParamType("[String!]")
								.withParamType("CharacterInput")
								//
								.withParamValue("on Enum").withParamValue("69").withParamValue("666")
								.withParamValue("666.666").withParamValue("true")
								.withParamValue("00000000-0000-0000-0000-000000000002").withParamValue("NEWHOPE")
								.withParamValue("2001-02-28").withParamValue("[\"str1\",\"str2\"]")
								.withParamValue("{name:\"specific name\",appearsIn:[NEWHOPE,EMPIRE],type:\"Human\"}"), //
						new ExpectedDirective("@testExtendKeyword").withParamName("msg").withParamType("String")
								.withParamValue("an Episode extension")), //
				CEP_Episode_CES.class);

		//////////////////////////////////////////////////////////////////////////////////////////
		// Step 2 : the value annotations
		checkEnumValueDirective(//
				Arrays.asList(//
						new ExpectedDirective("@anotherTestDirective")), //
				"EMPIRE", //
				CEP_Episode_CES.class);

		checkEnumValueDirective(//
				Arrays.asList(//
						new ExpectedDirective("@testDirective")//
								.withParamName("value").withParamName("anotherValue") //
								.withParamType("String!").withParamType("String") //
								.withParamValue("on Enum values").withParamValue("-1"),
						new ExpectedDirective("@anotherTestDirective")), //
				"DOES_NOT_EXIST", //
				CEP_Episode_CES.class);
	}

	@Test
	void testGraphQLDirective_onInputTypeAndFieldsAndGettersSetters() throws Exception {
		//////////////////////////////////////////////////////////////////////////////////////////
		// Step 1 : the class annotations
		checkClassDirective(//
				Arrays.asList(//
						new ExpectedDirective("@testDirective")//
								.withParamName("value")//
								.withParamType("String!") //
								.withParamValue("on Input Type"), //
						new ExpectedDirective("@testExtendKeyword")), //
				CINP_AllFieldCasesInput_CINS.class);

		//////////////////////////////////////////////////////////////////////////////////////////
		// Step 2 : the field getter and setter annotations
		checkMethodDirective(//
				Arrays.asList(//
						new ExpectedDirective("@testDirective")//
								.withParamName("value")//
								.withParamType("String!")//
								.withParamValue("on Input Field")), //
				CINP_AllFieldCasesInput_CINS.class, //
				Arrays.asList("getId", "setId"));

		//////////////////////////////////////////////////////////////////////////////////////////
		// Step 3 : the field (attribute) annotations
		checkAttributeDirective(//
				Arrays.asList(//
						new ExpectedDirective("@testDirective")//
								.withParamName("value")//
								.withParamType("String!")//
								.withParamValue("on Input Field")), //
				CINP_AllFieldCasesInput_CINS.class, //
				"id");
	}

	@Test
	void testGraphQLDirective_onInterfaceAndFieldsAndGettersSetters() {
		//////////////////////////////////////////////////////////////////////////////////////////
		// Step 1 : the class annotations
		checkClassDirective(//
				Arrays.asList(//
						new ExpectedDirective("@testDirective")//
								.withParamName("value") //
								.withParamType("String!") //
								.withParamValue("on Interface"), //
						new ExpectedDirective("@testExtendKeyword")), //
				CIP_AllFieldCasesInterface_CIS.class);

		//////////////////////////////////////////////////////////////////////////////////////////
		// Step 2 : the field getter annotations
		checkMethodDirective(//
				Arrays.asList(//
						new ExpectedDirective("@testDirective")//
								.withParamName("value")//
								.withParamType("String!")//
								.withParamValue("on interface field (name)")), //
				CIP_AllFieldCasesInterface_CIS.class, //
				Arrays.asList("getName", "setName"));
	}

	@Disabled
	@Test
	void testGraphQLDirective_onScalars() {
		fail("No GraphQLDirective on scalar yet");
	}

	@Disabled
	@Test
	void testGraphQLDirective_onSchema() {
		fail("No GraphQLDirective on schema yet");
	}

	@Test
	void testGraphQLDirective_onObjetTypeAndFieldsAndGettersSetters() throws Exception {

		//////////////////////////////////////////////////////////////////////////////////////////
		// Step 1 : the class annotations
		checkClassDirective(//
				Arrays.asList(//
						new ExpectedDirective("@testDirective")//
								.withParamName("value") //
								.withParamType("String!") //
								.withParamValue(
										"on Object\n With a line feed\\\n and a carriage return.\n It also contains 'strange' characters, to check the plugin behavior: \\'\"}])({[\\"), //
						new ExpectedDirective("@anotherTestDirective"),
						new ExpectedDirective("@testExtendKeyword").withParamName("msg").withParamType("String")
								.withParamValue("comes from type extension")), //
				CTP_AllFieldCases_CTS.class);

		//////////////////////////////////////////////////////////////////////////////////////////
		// Step 2 : the field getter and setter annotations
		checkMethodDirective(//
				Arrays.asList(//
						new ExpectedDirective("@testDirective")//
								.withParamName("value")//
								.withParamType("String!")//
								.withParamValue("on Field")), //
				CTP_AllFieldCases_CTS.class, //
				Arrays.asList("getId", "setId"));

		//////////////////////////////////////////////////////////////////////////////////////////
		// Step 3 : the field (attribute) annotations
		checkAttributeDirective(//
				Arrays.asList(//
						new ExpectedDirective("@testDirective")//
								.withParamName("value")//
								.withParamType("String!")//
								.withParamValue("on Field")), //
				CTP_AllFieldCases_CTS.class, //
				"id");
	}

	@Test
	void testGraphQLDirective_onUnions() {
		checkClassDirective(//
				Arrays.asList(//
						new ExpectedDirective("@testDirective")//
								.withParamName("value") //
								.withParamType("String!") //
								.withParamValue("on Union"), //
						new ExpectedDirective("@testExtendKeyword")), //
				CUP_AnyCharacter_CUS.class);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Internal methods
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Check that the given <i>expectedDirectives</i> exist on the given <i>cls</i>
	 * 
	 * @param expectedDirectives
	 *            may be null
	 * @param cls
	 */
	private void checkClassDirective(List<ExpectedDirective> expectedDirectives, Class<?> cls) {
		checkDirectiveAnnotationList(//
				expectedDirectives, //
				cls.getAnnotationsByType(GraphQLDirective.class), //
				"class " + cls.getName());
	}

	private void checkEnumValueDirective(List<ExpectedDirective> expectedDirectives, String valueName,
			Class<? extends Enum<?>> enumClass) throws Exception {
		checkDirectiveAnnotationList(//
				expectedDirectives, //
				enumClass.getField(valueName).getAnnotationsByType(GraphQLDirective.class), //
				"enum " + enumClass.getName());

	}

	/**
	 * Check that the given <i>expectedDirectives</i> exist on all the given methods of the given <i>cls</i>
	 * 
	 * @param expectedDirectives
	 * @param cls
	 * @param methodNames
	 *            The names of the method to check. If several methods have match this name, each of them will be
	 *            tested. If <i>cls/<i> have no method of any of these method names, then this check fails.
	 */
	private void checkMethodDirective(List<ExpectedDirective> expectedDirectives, Class<?> cls,
			List<String> methodNames) {
		for (String methodName : methodNames) {
			boolean found = false;
			for (Method method : cls.getMethods()) {
				if (method.getName().equals(methodName)) {
					checkDirectiveAnnotationList(expectedDirectives,
							method.getAnnotationsByType(GraphQLDirective.class),
							"method " + cls.getName() + "." + methodName);
					found = true; // We keep looping, as this class may contain several methods with this name
				}
			} // for(method)

			if (!found) {
				fail("The class " + cls.getName() + " has no method of name '" + methodName + "'");
			}
		} // for(methodName)
	}

	/**
	 * Check that the given <i>expectedDirectives</i> exist on the given <i>attributeName</i> of the given <i>cls</i>
	 * 
	 * @param expectedDirectives
	 * @param cls
	 * @param attributeName
	 *            The name of the attribute to check.
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 * @throws Exception
	 *             when the attribute doesn't exist, or is not acessible
	 */
	private void checkAttributeDirective(List<ExpectedDirective> expectedDirectives, Class<?> cls, String attributeName)
			throws Exception {
		Field field = cls.getDeclaredField(attributeName);

		checkDirectiveAnnotationList(//
				expectedDirectives, //
				field.getAnnotationsByType(GraphQLDirective.class), //
				"attribute " + cls.getName() + "." + attributeName);
	}

	/**
	 * Check that the given <i>expectedDirectives</i> exist on the <i>argumentName</i> argument of all the given methods
	 * of the given <i>cls</i>
	 * 
	 * @param expectedDirectives
	 * @param cls
	 * @param methodNames
	 *            The names of the method to check. If several methods have match this name, each of them will be
	 *            tested. If <i>cls/<i> have no method of any of these method names, then this check fails.
	 * @param parameterName
	 *            If any of these methods doesn't have any argument of this name, then this check fails.
	 */
	private void checkMethodArgumentDirective(List<ExpectedDirective> expectedDirectives, Class<?> cls,
			List<String> methodNames, String parameterName) {
		for (String methodName : methodNames) {
			boolean foundMethod = false;
			for (Method method : cls.getMethods()) {
				if (method.getName().equals(methodName)) {
					boolean foundParameter = false;
					int numParameter = -1;
					Annotation[][] parameterAnnotations = method.getParameterAnnotations();
					for (Parameter arg : method.getParameters()) {
						numParameter += 1;
						if (arg.getName().equals(parameterName)) {
							List<GraphQLDirective> directivesOnParameter = Stream.of(parameterAnnotations[numParameter])//
									.filter(a -> a.annotationType().getCanonicalName()
											.equals(GraphQLDirective.class.getCanonicalName()))//
									.map(a -> (GraphQLDirective) a)//
									.collect(Collectors.toList());
							checkDirectiveAnnotationList(//
									expectedDirectives, //
									directivesOnParameter, //
									"argument " + arg.getName() + " of  method " + cls.getName() + "."
											+ method.getName());
							foundParameter = true;
							break;
						}
					} // for(parameter)
					assertTrue(foundParameter, "The method " + cls.getName() + "." + method.getName()
							+ " must have a parameter of name '" + parameterName + "'");
					foundMethod = true;
				} // if(methodName)
			} // for(method)

			assertTrue(foundMethod, "The class " + cls.getName() + " must have a method of name '" + methodName + "'");
		} // for(methodName)
	}

	private void checkDirectiveAnnotationList(List<ExpectedDirective> expectedDirectives,
			GraphQLDirective[] annotations, String src) {
		checkDirectiveAnnotationList(expectedDirectives, Arrays.asList(annotations), src);
	}

	private void checkDirectiveAnnotationList(List<ExpectedDirective> expectedDirectives,
			List<GraphQLDirective> annotations, String src) {
		if (expectedDirectives == null)
			expectedDirectives = new ArrayList<>();

		assertEquals(expectedDirectives.size(), annotations.size(), "Nb of @GraphQLDirective for " + src);

		// Ok the number of annotations is correct. Let's loop and check the annotation list content
		boolean found;
		for (ExpectedDirective expectedDirective : expectedDirectives) {
			found = false;
			for (GraphQLDirective annotation : annotations) {
				if (annotation.name().equals(expectedDirective.name)) {
					found = true;
					assertlistOfStringIsEqual(expectedDirective.parameterNames,
							Arrays.asList(annotation.parameterNames()),
							"check of parameterNames of " + annotation.name() + " for " + src);
					assertlistOfStringIsEqual(expectedDirective.parameterTypes,
							Arrays.asList(annotation.parameterTypes()),
							"check of parameterTypes of " + annotation.name() + " for " + src);
					assertlistOfStringIsEqual(expectedDirective.parameterValues,
							Arrays.asList(annotation.parameterValues()),
							"check of parameterValues of " + annotation.name() + " for " + src);
				}
			} // for

			// We should have found the directive in the expected list
			assertTrue(found, "The expected " + expectedDirective.name
					+ " directive is missing in the @GraphQLDirective of the " + src);

		}
	}

	private void assertlistOfStringIsEqual(List<String> expected, List<String> actual, String src) {
		if (expected == null || expected.size() == 0) {
			assertTrue(actual == null || actual.size() == 0,
					"if one list is null, both should be null or empty (" + src + ")");
		} else {
			assertTrue(actual != null && actual.size() > 0,
					"if one list is not empty, both should be not empty (" + src + ")");
		}

		assertEquals(expected.size(), actual.size(), "Checking list size for " + src);

		// As both list are of the same size, if each item of expected exists in actual, then it's ok
		for (int i = 0; i < expected.size(); i += 1) {
			if (!actual.contains(expected.get(i))) {
				// This "if" could be done in one step, in a assertTrue statement. But doing this allows to put a break
				// point
				fail("The item '" + expected.get(i) + "' is expected, and should exist in the the actual list (" + src
						+ ")");
			}
		}
	}
}
