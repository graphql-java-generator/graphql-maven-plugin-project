package com.graphql_java_generator.schema_personalization;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import com.generated.annotation.AllEnums1;
import com.generated.annotation.AllEnums2;
import com.generated.annotation.LocalAnnotation;
import com.generated.annotation.MyReplacementAnnotation;
import org.allGraphQLCases.client.CEP_extends_CES;
import org.allGraphQLCases.client.CINP_DroidInput_CINS;
import org.allGraphQLCases.client.CIP_Character_CIS;
import org.allGraphQLCases.client.CIP_Commented_CIS;
import org.allGraphQLCases.client.CIP_Node_CIS;
import org.allGraphQLCases.client.CIP_WithID_CIS;
import org.allGraphQLCases.client.CTP_Droid_CTS;
import org.allGraphQLCases.client.CTP_Human_CTS;
import org.allGraphQLCases.client.CUP_AnyCharacter_CUS;
import com.generated.interfaces.AllInputTypes1;
import com.generated.interfaces.AllInputTypes2;
import com.generated.interfaces.AllInterfaces1;
import com.generated.interfaces.AllInterfaces2;
import com.generated.interfaces.AllTypes1;
import com.generated.interfaces.AllTypes2;
import com.generated.interfaces.AllUnions1;
import com.generated.interfaces.AllUnions2;
import com.generated.interfaces.AnyCharacterInterface;
import com.generated.interfaces.CharacterInterface;
import com.generated.interfaces.DroidInputInterface;
import com.generated.interfaces.HumanInterface;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.Test;

import com.graphql_java_generator.annotation.GraphQLDirectives;
import com.graphql_java_generator.annotation.GraphQLEnumType;
import com.graphql_java_generator.annotation.GraphQLInterfaceType;

/**
 * Various tests to check that the <i>/src/main/graphql/schema_personalization.json</i> has been applied
 * 
 * @author etienne_sf
 */

public class CheckSchemaPersonalizationTest {

	@Test
	void checkHuman() throws ClassNotFoundException, NoSuchFieldException, SecurityException, NoSuchMethodException {
		Class<?> humanClass = this.getClass().getClassLoader().loadClass(CTP_Human_CTS.class.getName());
		//
		Class<?>[] interfaces = humanClass.getInterfaces();
		assertEquals(8, interfaces.length);
		assertTrue(ArrayUtils.contains(interfaces, CIP_Character_CIS.class));
		assertTrue(ArrayUtils.contains(interfaces, CIP_Commented_CIS.class));
		assertTrue(ArrayUtils.contains(interfaces, CIP_WithID_CIS.class));
		assertTrue(ArrayUtils.contains(interfaces, CUP_AnyCharacter_CUS.class));
		assertTrue(ArrayUtils.contains(interfaces, CIP_Node_CIS.class));
		assertTrue(ArrayUtils.contains(interfaces, AllTypes1.class));
		assertTrue(ArrayUtils.contains(interfaces, AllTypes2.class));
		assertTrue(ArrayUtils.contains(interfaces, HumanInterface.class));
		//
		// The age field must have been added
		Field age = humanClass.getDeclaredField("age");
		assertEquals(Integer.class, age.getType());
		LocalAnnotation localAnnotation = age.getAnnotation(LocalAnnotation.class);
		assertNotNull(localAnnotation, "human.age annotation");
		assertEquals("age", localAnnotation.value());
		// The age2 field must have been added
		Field age2 = humanClass.getDeclaredField("age2");
		assertEquals(List.class, age2.getType());
		localAnnotation = age2.getAnnotation(LocalAnnotation.class);
		assertNotNull(localAnnotation, "human.age2 annotation");
		assertEquals("age2", localAnnotation.value());
	}

	@Test
	void checkDroid() throws ClassNotFoundException, NoSuchFieldException, SecurityException {
		Class<?> droidClass = this.getClass().getClassLoader().loadClass(CTP_Droid_CTS.class.getName());
		//
		assertEquals(3, droidClass.getAnnotations().length);
		assertNotNull(droidClass.getAnnotation(MyReplacementAnnotation.class));
		//
		Class<?>[] interfaces = droidClass.getInterfaces();
		assertEquals(6, interfaces.length);
		assertTrue(ArrayUtils.contains(interfaces, CIP_Character_CIS.class));
		assertTrue(ArrayUtils.contains(interfaces, CIP_WithID_CIS.class));
		assertTrue(ArrayUtils.contains(interfaces, CUP_AnyCharacter_CUS.class));
		assertTrue(ArrayUtils.contains(interfaces, CIP_Node_CIS.class));
		assertTrue(ArrayUtils.contains(interfaces, AllTypes1.class));
		assertTrue(ArrayUtils.contains(interfaces, AllTypes2.class));
		//
		// The age field must have been added
		Field age = droidClass.getDeclaredField("age");
		assertEquals(Integer.class, age.getType());
		//
		Field id = droidClass.getDeclaredField("id");
		assertEquals(3, id.getAnnotations().length);
		LocalAnnotation localAnnotation = id.getAnnotation(LocalAnnotation.class);
		assertNotNull(localAnnotation, "droid.id annotation");
		assertEquals("local annotation", localAnnotation.value());
		//
		Field name = droidClass.getDeclaredField("name");
		assertEquals(4, name.getAnnotations().length);
		localAnnotation = name.getAnnotation(LocalAnnotation.class);
		assertNotNull(localAnnotation, "droid.name annotation");
		assertEquals("an annotation", localAnnotation.value());
		//
		assertNotNull(droidClass.getDeclaredField("age"));
	}

	@Test
	void checkDroidInput() throws ClassNotFoundException {
		Class<?> droidInputClass = this.getClass().getClassLoader().loadClass(CINP_DroidInput_CINS.class.getName());
		//
		Class<?>[] interfaces = droidInputClass.getInterfaces();
		assertEquals(3, interfaces.length);
		assertTrue(ArrayUtils.contains(interfaces, AllInputTypes1.class));
		assertTrue(ArrayUtils.contains(interfaces, AllInputTypes2.class));
		assertTrue(ArrayUtils.contains(interfaces, DroidInputInterface.class));
	}

	@Test
	void checkExtends() throws ClassNotFoundException {
		Class<?> extendsClass = this.getClass().getClassLoader().loadClass(CEP_extends_CES.class.getName());
		//
		assertEquals(4, extendsClass.getAnnotations().length);
		//
		GraphQLEnumType graphQLEnumType = extendsClass.getAnnotation(GraphQLEnumType.class);
		assertNotNull(graphQLEnumType, "extends annotation");
		assertEquals("extends", graphQLEnumType.value());
		//
		LocalAnnotation localAnnotation = extendsClass.getAnnotation(LocalAnnotation.class);
		assertNotNull(localAnnotation, "extends annotation");
		assertEquals("enum's annotation", localAnnotation.value());
		//
		AllEnums1 allEnums1 = extendsClass.getAnnotation(AllEnums1.class);
		assertNotNull(allEnums1, "extends annotation");
		assertEquals("enum1", allEnums1.value());
		//
		AllEnums2 allEnums2 = extendsClass.getAnnotation(AllEnums2.class);
		assertNotNull(allEnums2, "extends annotation");
		assertEquals("enum2", allEnums2.value());

	}

	@Test
	void checkAnyCharacter() throws ClassNotFoundException {
		Class<?> anyCharacterClass = this.getClass().getClassLoader().loadClass(CUP_AnyCharacter_CUS.class.getName());
		assertEquals(5, anyCharacterClass.getAnnotations().length);
		//
		LocalAnnotation localAnnotation = anyCharacterClass.getAnnotation(LocalAnnotation.class);
		assertNotNull(localAnnotation, "union annotation");
		assertEquals("union's annotation", localAnnotation.value());
		//
		GraphQLDirectives graphQLDirectives = anyCharacterClass.getAnnotation(GraphQLDirectives.class);
		assertEquals(2, graphQLDirectives.value().length);
		//
		Class<?>[] interfaces = anyCharacterClass.getInterfaces();
		assertEquals(3, interfaces.length);
		assertTrue(ArrayUtils.contains(interfaces, AllUnions1.class));
		assertTrue(ArrayUtils.contains(interfaces, AllUnions2.class));
		assertTrue(ArrayUtils.contains(interfaces, AnyCharacterInterface.class));
	}

	@Test
	void checkCharacter() throws NoSuchMethodException, SecurityException, ClassNotFoundException {
		Class<?> characterClass = this.getClass().getClassLoader().loadClass(CIP_Character_CIS.class.getName());
		//
		assertEquals(5, characterClass.getAnnotations().length);
		//
		GraphQLDirectives graphQLDirectives = characterClass.getAnnotation(GraphQLDirectives.class);
		assertEquals(2, graphQLDirectives.value().length);
		//
		assertNotNull(characterClass.getAnnotation(GraphQLInterfaceType.class));
		//
		LocalAnnotation localAnnotation = characterClass.getAnnotation(LocalAnnotation.class);
		assertNotNull(localAnnotation, "interface annotation");
		assertEquals("interface's annotation", localAnnotation.value());
		//
		Class<?>[] interfaces = characterClass.getInterfaces();
		assertEquals(4, interfaces.length);
		assertTrue(ArrayUtils.contains(interfaces, AllInterfaces1.class));
		assertTrue(ArrayUtils.contains(interfaces, AllInterfaces2.class));
		assertTrue(ArrayUtils.contains(interfaces, CIP_Node_CIS.class));
		assertTrue(ArrayUtils.contains(interfaces, CharacterInterface.class));
		//
		Method getAge = characterClass.getMethod("getAge", (Class<?>[]) null);
		assertEquals(1, getAge.getAnnotations().length);
		localAnnotation = getAge.getAnnotation(LocalAnnotation.class);
		assertNotNull(localAnnotation, "interface annotation");
		assertEquals("interface method's annotation", localAnnotation.value());
	}
}
