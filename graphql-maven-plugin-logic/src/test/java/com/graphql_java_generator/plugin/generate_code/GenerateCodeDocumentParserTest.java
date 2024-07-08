package com.graphql_java_generator.plugin.generate_code;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import com.graphql_java_generator.plugin.conf.CommonConfiguration;
import com.graphql_java_generator.plugin.language.DataFetchersDelegate;
import com.graphql_java_generator.plugin.language.Field;
import com.graphql_java_generator.plugin.language.impl.ObjectType;
import com.graphql_java_generator.plugin.test.helper.GraphQLConfigurationTestHelper;

import graphql.mavenplugin_notscannedbyspring.AllGraphQLCases_Server_SpringConfiguration;

public class GenerateCodeDocumentParserTest {

	final String IGNORED_SPRING_MAPPINGS_CONF = " Character,Droid.friends\tAllFieldCases\rHuman.friends\nDroid.appearsIn,Character";

	AbstractApplicationContext ctx;
	GenerateCodeDocumentParser generateCodeDocumentParser;
	GraphQLConfigurationTestHelper graphQLConfigurationTestHelper;
	ObjectType objetTypeAllFieldCases;
	ObjectType objetTypeCharacter;
	ObjectType objetTypeDroid;
	ObjectType objetTypeHuman;
	Field fieldDroidAppearsIn;
	Field fieldDroidFriends;
	Field fieldHumanFriends;

	/**
	 * This method loads the context, based on the given ignoredSpringMappings value
	 * 
	 * @param ignoredSpringMappings
	 *            The value for this parameter to use when parsing the schema
	 * @throws NoSuchFieldException
	 * @throws IOException
	 */
	void setupOneTest(String ignoredSpringMappings) throws NoSuchFieldException, IOException {
		this.ctx = new AnnotationConfigApplicationContext(AllGraphQLCases_Server_SpringConfiguration.class);
		this.generateCodeDocumentParser = this.ctx.getBean(GenerateCodeDocumentParser.class);
		this.graphQLConfigurationTestHelper = (GraphQLConfigurationTestHelper) this.ctx
				.getBean(CommonConfiguration.class);
		//
		this.graphQLConfigurationTestHelper.generateDataFetcherForEveryFieldsWithArguments = true;
		this.graphQLConfigurationTestHelper.ignoredSpringMappings = ignoredSpringMappings;
		//
		this.generateCodeDocumentParser.afterPropertiesSet();
		this.generateCodeDocumentParser.parseGraphQLSchemas();

		// mock definitions
		this.objetTypeAllFieldCases = (ObjectType) this.generateCodeDocumentParser.getType("AllFieldCases");
		this.objetTypeCharacter = (ObjectType) this.generateCodeDocumentParser.getType("Character");
		this.objetTypeDroid = (ObjectType) this.generateCodeDocumentParser.getType("Droid");
		this.objetTypeHuman = (ObjectType) this.generateCodeDocumentParser.getType("Human");
		this.fieldDroidAppearsIn = this.objetTypeDroid.getField("appearsIn");
		this.fieldDroidFriends = this.objetTypeDroid.getField("friends");
		this.fieldHumanFriends = this.objetTypeHuman.getField("friends");
	}

	@AfterEach
	void close() {
		this.ctx.close();
	}

	/**
	 * Tests of these methods: {@link GenerateCodeDocumentParser#getTypeSpringMappingIgnored()},
	 * {@link GenerateCodeDocumentParser#getFieldSpringMappingIgnored()},
	 * {@link GenerateCodeDocumentParser#isTypeSpringMappingIgnored(com.graphql_java_generator.plugin.language.impl.ObjectType)}
	 * and
	 * {@link GenerateCodeDocumentParser#isFieldSpringMappingIgnored(com.graphql_java_generator.plugin.language.impl.ObjectType, com.graphql_java_generator.plugin.language.Field)}
	 * 
	 * @throws IOException
	 * @throws NoSuchFieldException
	 */
	@Test
	void test_xxxSpringMappingIgnored_methods() throws NoSuchFieldException, IOException {
		RuntimeException e;
		Set<String> set;

		// Let's parse the schema
		setupOneTest(this.IGNORED_SPRING_MAPPINGS_CONF);

		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// The correct configuration has been set by the setup()
		// Let's check that typeSpringMappingIgnored and fieldSpringMappingIgnored attributs, and the datafetchers that
		// have been identified (and ignored)
		assertNotNull(this.generateCodeDocumentParser.getTypeSpringMappingIgnored());
		assertEquals(2, this.generateCodeDocumentParser.getTypeSpringMappingIgnored().size());
		assertTrue(this.generateCodeDocumentParser.getTypeSpringMappingIgnored().contains("Character"),
				"contains Character");
		assertTrue(this.generateCodeDocumentParser.getTypeSpringMappingIgnored().contains("AllFieldCases"),
				"contains AllFieldCases");
		//
		assertNotNull(this.generateCodeDocumentParser.getFieldSpringMappingIgnored());
		assertEquals(2, this.generateCodeDocumentParser.getFieldSpringMappingIgnored().size());
		//
		set = this.generateCodeDocumentParser.getFieldSpringMappingIgnored().get("Droid");
		assertNotNull(set);
		assertEquals(2, set.size());
		assertTrue(set.contains("appearsIn"));
		assertTrue(set.contains("friends"));
		//
		set = this.generateCodeDocumentParser.getFieldSpringMappingIgnored().get("Human");
		assertNotNull(set);
		assertEquals(1, set.size());
		assertTrue(set.contains("friends"));
		//
		assertTrue(this.generateCodeDocumentParser.isTypeSpringMappingIgnored(this.objetTypeAllFieldCases));
		assertTrue(this.generateCodeDocumentParser.isTypeSpringMappingIgnored(this.objetTypeCharacter));
		assertTrue(this.generateCodeDocumentParser.isFieldSpringMappingIgnored(this.fieldDroidAppearsIn));
		assertTrue(this.generateCodeDocumentParser.isFieldSpringMappingIgnored(this.fieldDroidFriends));
		assertTrue(this.generateCodeDocumentParser.isFieldSpringMappingIgnored(this.fieldHumanFriends));
		//
		// The usual DataFetchersDelegate should exist. Let's test some:
		assertEquals(5, this.generateCodeDocumentParser.dataFetchersDelegates.stream()//
				.filter(dfd -> dfd.getType().getName().equals("MyQueryType") //
						|| dfd.getType().getName().equals("AnotherMutationType") //
						|| dfd.getType().getName().equals("TheSubscriptionType") //
						|| dfd.getType().getName().equals("AllFieldCasesInterfaceType") //
						|| dfd.getType().getName().equals("TFoo1") //
				)//
				.count());
		//
		// The ignored once must not exist
		assertEquals(0, this.generateCodeDocumentParser.dataFetchersDelegates.stream()//
				.filter(dfd -> dfd.getType().getName().equals("Character") //
						|| dfd.getType().getName().equals("AllFieldCases") //
				)//
				.count());
		//
		// The ignored fields must not exist (while their DataFetchersDelegate still exists)
		// Droid
		DataFetchersDelegate dfdDroid = this.generateCodeDocumentParser.dataFetchersDelegates.stream()//
				.filter(dfd -> dfd.getType().getName().equals("Droid")).collect(Collectors.toList()).get(0);
		assertEquals(1,
				dfdDroid.getDataFetchers().stream().filter(df -> df.getField().getName().equals("name")).count(),
				"The data fetcher for the 'Droid.name' field should still exist");
		assertEquals(0,
				dfdDroid.getDataFetchers().stream().filter(
						df -> df.getField().getName().equals("friends") || df.getField().getName().equals("appearsIn"))
						.count(),
				"The ignored mapping should generate no data fetcher");
		// Human
		DataFetchersDelegate dfdHuman = this.generateCodeDocumentParser.dataFetchersDelegates.stream()//
				.filter(dfd -> dfd.getType().getName().equals("Human")).collect(Collectors.toList()).get(0);
		assertEquals(2,
				dfdHuman.getDataFetchers().stream().filter(
						df -> df.getField().getName().equals("name") || df.getField().getName().equals("appearsIn"))
						.count(),
				"The data fetcher for the 'Droid.name' field should still exist");
		assertEquals(0,
				dfdHuman.getDataFetchers().stream().filter(df -> df.getField().getName().equals("friends")).count(),
				"The ignored mapping should generate no data fetcher");

		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// The correct configuration has been set by the setup()
		// Let's check that typeSpringMappingIgnored and fieldSpringMappingIgnored attributs, and the datafetchers that
		// have been identified (and ignored)
		initConfIgnoredSpringMappings(this.IGNORED_SPRING_MAPPINGS_CONF + this.IGNORED_SPRING_MAPPINGS_CONF, false);
		//
		assertNotNull(this.generateCodeDocumentParser.getTypeSpringMappingIgnored());
		assertEquals(2, this.generateCodeDocumentParser.getTypeSpringMappingIgnored().size());
		assertTrue(this.generateCodeDocumentParser.getTypeSpringMappingIgnored().contains("Character"),
				"contains Character");
		assertTrue(this.generateCodeDocumentParser.getTypeSpringMappingIgnored().contains("AllFieldCases"),
				"contains AllFieldCases");
		//
		assertNotNull(this.generateCodeDocumentParser.getFieldSpringMappingIgnored());
		assertEquals(2, this.generateCodeDocumentParser.getFieldSpringMappingIgnored().size());
		//
		set = this.generateCodeDocumentParser.getFieldSpringMappingIgnored().get("Droid");
		assertNotNull(set);
		assertEquals(2, set.size());
		assertTrue(set.contains("friends"));
		assertTrue(set.contains("appearsIn"));
		//
		set = this.generateCodeDocumentParser.getFieldSpringMappingIgnored().get("Human");
		assertNotNull(set);
		assertEquals(1, set.size());
		assertTrue(set.contains("friends"));
		//
		assertTrue(this.generateCodeDocumentParser.isTypeSpringMappingIgnored(this.objetTypeAllFieldCases));
		assertTrue(this.generateCodeDocumentParser.isTypeSpringMappingIgnored(this.objetTypeCharacter));
		assertTrue(this.generateCodeDocumentParser.isFieldSpringMappingIgnored(this.fieldDroidAppearsIn));
		assertTrue(this.generateCodeDocumentParser.isFieldSpringMappingIgnored(this.fieldDroidFriends));
		assertTrue(this.generateCodeDocumentParser.isFieldSpringMappingIgnored(this.fieldHumanFriends));

		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// null
		initConfIgnoredSpringMappings(null, false);
		//
		assertNotNull(this.generateCodeDocumentParser.getTypeSpringMappingIgnored());
		assertEquals(0, this.generateCodeDocumentParser.getTypeSpringMappingIgnored().size());
		//
		assertNotNull(this.generateCodeDocumentParser.getFieldSpringMappingIgnored());
		assertEquals(0, this.generateCodeDocumentParser.getFieldSpringMappingIgnored().size());
		//
		assertFalse(this.generateCodeDocumentParser.isTypeSpringMappingIgnored(this.objetTypeDroid));
		assertFalse(this.generateCodeDocumentParser.isTypeSpringMappingIgnored(this.objetTypeHuman));
		assertFalse(this.generateCodeDocumentParser.isFieldSpringMappingIgnored(this.fieldDroidAppearsIn));
		assertFalse(this.generateCodeDocumentParser.isFieldSpringMappingIgnored(this.fieldDroidFriends));
		assertFalse(this.generateCodeDocumentParser.isFieldSpringMappingIgnored(this.fieldHumanFriends));

		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// Empty strings
		initConfIgnoredSpringMappings("", false);
		//
		assertNotNull(this.generateCodeDocumentParser.getTypeSpringMappingIgnored());
		assertEquals(0, this.generateCodeDocumentParser.getTypeSpringMappingIgnored().size());
		//
		assertNotNull(this.generateCodeDocumentParser.getFieldSpringMappingIgnored());
		assertEquals(0, this.generateCodeDocumentParser.getFieldSpringMappingIgnored().size());
		//
		assertFalse(this.generateCodeDocumentParser.isTypeSpringMappingIgnored(this.objetTypeDroid));
		assertFalse(this.generateCodeDocumentParser.isTypeSpringMappingIgnored(this.objetTypeHuman));
		assertFalse(this.generateCodeDocumentParser.isFieldSpringMappingIgnored(this.fieldDroidAppearsIn));
		assertFalse(this.generateCodeDocumentParser.isFieldSpringMappingIgnored(this.fieldDroidFriends));
		assertFalse(this.generateCodeDocumentParser.isFieldSpringMappingIgnored(this.fieldHumanFriends));

		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// Only separators
		initConfIgnoredSpringMappings("", true);
		//
		assertNotNull(this.generateCodeDocumentParser.getTypeSpringMappingIgnored());
		assertEquals(0, this.generateCodeDocumentParser.getTypeSpringMappingIgnored().size());
		//
		assertNotNull(this.generateCodeDocumentParser.getFieldSpringMappingIgnored());
		assertEquals(0, this.generateCodeDocumentParser.getFieldSpringMappingIgnored().size());
		//
		assertFalse(this.generateCodeDocumentParser.isTypeSpringMappingIgnored(this.objetTypeDroid));
		assertFalse(this.generateCodeDocumentParser.isTypeSpringMappingIgnored(this.objetTypeHuman));
		assertFalse(this.generateCodeDocumentParser.isFieldSpringMappingIgnored(this.fieldDroidAppearsIn));
		assertFalse(this.generateCodeDocumentParser.isFieldSpringMappingIgnored(this.fieldDroidFriends));
		assertFalse(this.generateCodeDocumentParser.isFieldSpringMappingIgnored(this.fieldHumanFriends));

		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// Bad type (no separators)
		initConfIgnoredSpringMappings("ThisTypeDoesNotExist", false);
		//
		e = assertThrows(RuntimeException.class, () -> this.generateCodeDocumentParser.getTypeSpringMappingIgnored());
		assertTrue(e.getMessage().contains("ThisTypeDoesNotExist"));
		//
		assertNotNull(this.generateCodeDocumentParser.getFieldSpringMappingIgnored(),
				"types should be ignored in field list");
		assertEquals(0, this.generateCodeDocumentParser.getFieldSpringMappingIgnored().size(),
				"types should be ignored in field list");
		//
		assertFalse(this.generateCodeDocumentParser.isTypeSpringMappingIgnored(this.objetTypeDroid));
		assertFalse(this.generateCodeDocumentParser.isTypeSpringMappingIgnored(this.objetTypeHuman));
		assertFalse(this.generateCodeDocumentParser.isFieldSpringMappingIgnored(this.fieldDroidAppearsIn));
		assertFalse(this.generateCodeDocumentParser.isFieldSpringMappingIgnored(this.fieldDroidFriends));
		assertFalse(this.generateCodeDocumentParser.isFieldSpringMappingIgnored(this.fieldHumanFriends));

		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// Bad type (with separators)
		initConfIgnoredSpringMappings("ThisTypeDoesNotExist", true);
		//
		e = assertThrows(RuntimeException.class, () -> this.generateCodeDocumentParser.getTypeSpringMappingIgnored());
		assertTrue(e.getMessage().contains("ThisTypeDoesNotExist"));
		//
		assertNotNull(this.generateCodeDocumentParser.getFieldSpringMappingIgnored(),
				"types should be ignored in field list");
		assertEquals(0, this.generateCodeDocumentParser.getFieldSpringMappingIgnored().size(),
				"types should be ignored in field list");
		//
		assertFalse(this.generateCodeDocumentParser.isTypeSpringMappingIgnored(this.objetTypeDroid));
		assertFalse(this.generateCodeDocumentParser.isTypeSpringMappingIgnored(this.objetTypeHuman));
		assertFalse(this.generateCodeDocumentParser.isFieldSpringMappingIgnored(this.fieldDroidAppearsIn));
		assertFalse(this.generateCodeDocumentParser.isFieldSpringMappingIgnored(this.fieldDroidFriends));
		assertFalse(this.generateCodeDocumentParser.isFieldSpringMappingIgnored(this.fieldHumanFriends));

		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// Bad type in field declaration
		initConfIgnoredSpringMappings("ThisTypeDoesNotExist.field", true);
		//
		assertNotNull(this.generateCodeDocumentParser.getTypeSpringMappingIgnored(),
				"bad types in field declaration should be ignored when reading ignored types");
		assertEquals(0, this.generateCodeDocumentParser.getTypeSpringMappingIgnored().size(),
				"bad types in field declaration should be ignored when reading ignored types");
		//
		e = assertThrows(RuntimeException.class, () -> this.generateCodeDocumentParser.getFieldSpringMappingIgnored());
		assertTrue(e.getMessage().contains("ThisTypeDoesNotExist"));
		//
		assertFalse(this.generateCodeDocumentParser.isTypeSpringMappingIgnored(this.objetTypeDroid));
		assertFalse(this.generateCodeDocumentParser.isTypeSpringMappingIgnored(this.objetTypeHuman));
		assertFalse(this.generateCodeDocumentParser.isFieldSpringMappingIgnored(this.fieldDroidAppearsIn));
		assertFalse(this.generateCodeDocumentParser.isFieldSpringMappingIgnored(this.fieldDroidFriends));
		assertFalse(this.generateCodeDocumentParser.isFieldSpringMappingIgnored(this.fieldHumanFriends));

		//////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// Bad field name
		initConfIgnoredSpringMappings("Human.ThisFieldDoesNotExist", true);
		//
		assertNotNull(this.generateCodeDocumentParser.getTypeSpringMappingIgnored(),
				"bad field name in field declaration should be ignored when reading ignored types");
		assertEquals(0, this.generateCodeDocumentParser.getTypeSpringMappingIgnored().size(),
				"bad field name in field declaration should be ignored when reading ignored types");
		//
		e = assertThrows(RuntimeException.class, () -> this.generateCodeDocumentParser.getFieldSpringMappingIgnored());
		assertTrue(e.getMessage().contains("ThisFieldDoesNotExist"), "The exception message was: " + e.getMessage());
		//
		assertFalse(this.generateCodeDocumentParser.isTypeSpringMappingIgnored(this.objetTypeDroid));
		assertFalse(this.generateCodeDocumentParser.isTypeSpringMappingIgnored(this.objetTypeHuman));
		assertFalse(this.generateCodeDocumentParser.isFieldSpringMappingIgnored(this.fieldDroidAppearsIn));
		assertFalse(this.generateCodeDocumentParser.isFieldSpringMappingIgnored(this.fieldDroidFriends));
		assertFalse(this.generateCodeDocumentParser.isFieldSpringMappingIgnored(this.fieldHumanFriends));
	}

	@Test
	void testAllMappingsIgnored() throws NoSuchFieldException, IOException {
		// Let's parse the schema
		setupOneTest("*");

		// No controller and no DataFetchersDelegate
		assertEquals(0, this.generateCodeDocumentParser.getDataFetchersDelegates().size(),
				"There should be no DataFetchersDelegate (which leads to no controllers");
	}

	/**
	 * 
	 */
	private void initConfIgnoredSpringMappings(String val, boolean addIgnoredDelimiters) {
		if (val == null) {
			this.graphQLConfigurationTestHelper.ignoredSpringMappings = null;
		} else {
			this.graphQLConfigurationTestHelper.ignoredSpringMappings = ""//
					+ ((addIgnoredDelimiters) ? GenerateCodeDocumentParser.IGNORED_SPRING_MAPPINGS_SEPARATOR : "")//
					+ val//
					+ ((addIgnoredDelimiters) ? GenerateCodeDocumentParser.IGNORED_SPRING_MAPPINGS_SEPARATOR : "")//
			;
		}
		this.generateCodeDocumentParser.typeSpringMappingIgnored = null;
		this.generateCodeDocumentParser.fieldSpringMappingIgnored = null;
	}

}
