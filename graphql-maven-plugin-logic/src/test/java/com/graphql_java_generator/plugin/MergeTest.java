package com.graphql_java_generator.plugin;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import com.graphql_java_generator.GraphqlUtils;
import com.graphql_java_generator.plugin.conf.MergeSchemaConfiguration;
import com.graphql_java_generator.plugin.language.Type;
import com.graphql_java_generator.plugin.language.impl.FieldImpl;
import com.graphql_java_generator.plugin.language.impl.ObjectType;
import com.graphql_java_generator.plugin.language.impl.UnionType;
import com.graphql_java_generator.plugin.test.helper.DeepComparator;
import com.graphql_java_generator.plugin.test.helper.DeepComparator.ComparisonRule;
import com.graphql_java_generator.plugin.test.helper.DeepComparator.Difference;
import com.graphql_java_generator.plugin.test.helper.DeepComparator.DifferenceType;
import com.graphql_java_generator.plugin.test.helper.MavenTestHelper;
import com.graphql_java_generator.plugin.test.helper.MergeSchemaConfigurationTestHelper;

import graphql.language.Document;
import merge.mavenplugin_notscannedbyspring.AllGraphQLCases_Client_SpringConfiguration;
import merge.mavenplugin_notscannedbyspring.AllGraphQLCases_Client_SpringConfiguration_addRelayConnections;
import merge.mavenplugin_notscannedbyspring.Forum_Client_SpringConfiguration;
import merge.mavenplugin_notscannedbyspring.GeneratedAllGraphQLCases_Client_SpringConfiguration;
import merge.mavenplugin_notscannedbyspring.GeneratedAllGraphQLCases_Client_SpringConfiguration_addRelayConnections;
import merge.mavenplugin_notscannedbyspring.GeneratedForum_Client_SpringConfiguration;

/**
 * 
 * @author etienne-sf
 */
@Execution(ExecutionMode.CONCURRENT)
class MergeTest {

	/** The logger for this instance */
	protected transient Logger logger = LoggerFactory.getLogger(this.getClass());

	MavenTestHelper mavenTestHelper = new MavenTestHelper();
	Document generatedDocument;

	DeepComparator deepComparator;

	@BeforeEach
	void setUp() {
		deepComparator = new DeepComparator();

		//////////////////////////////////////////////////////////////////////////////////////////////////////////////
		/////////////// Ignored classes //////////////////////////////////////////////////////////////////////////
		deepComparator.addIgnoredClass(AddRelayConnections.class);
		deepComparator.addIgnoredClass(Document.class);
		deepComparator.addIgnoredClass(MergeSchemaConfigurationTestHelper.class);
		deepComparator.addIgnoredClass(GraphqlUtils.class);

		//////////////////////////////////////////////////////////////////////////////////////////////////////////////
		/////////////// Id fields //////////////////////////////////////////////////////////////////////////
		deepComparator.addIdField(ObjectType.class, "name");

		//////////////////////////////////////////////////////////////////////////////////////////////////////////////
		/////////////// Ignored fields //////////////////////////////////////////////////////////////////////////
		deepComparator.addIgnoredFields(FieldImpl.class, "documentParser");
		deepComparator.addIgnoredFields(com.graphql_java_generator.plugin.MergeDocumentParser.class, "configuration");
		deepComparator.addIgnoredFields(com.graphql_java_generator.plugin.MergeDocumentParser.class, "graphqlUtils");
		deepComparator.addIgnoredFields(com.graphql_java_generator.plugin.MergeDocumentParser.class, "documents");
		deepComparator.addIgnoredFields(com.graphql_java_generator.plugin.MergeDocumentParser.class,
				"objectTypeExtensionDefinitions");

		//////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// To break the cycle where comparing the type of a FieldImpl, we define some specific comparison rules:
		deepComparator.addSpecificComparisonRules(FieldImpl.class, "owningType", new ComparisonRule() {
			@Override
			public List<Difference> compare(Object o1, Object o2, int nbMaxDifferences) {
				Type type1 = (Type) o1;
				Type type2 = (Type) o2;
				if (!type1.getName().equals(type2.getName())) {
					List<Difference> differences = new ArrayList<>();
					differences.add(new DeepComparator.Difference("/name", DifferenceType.VALUE, type1.getName(),
							type2.getName(), null));
					return differences;
				}
				return null;
			}
		});
		deepComparator.addSpecificComparisonRules(UnionType.class, "memberTypes", new ComparisonRule() {
			@SuppressWarnings("unchecked")
			@Override
			public List<Difference> compare(Object o1, Object o2, int nbMaxDifferences) {
				List<ObjectType> members1 = (List<ObjectType>) o1;
				List<ObjectType> members2 = (List<ObjectType>) o2;
				List<String> lst1 = new ArrayList<>(members1.size());
				List<String> lst2 = new ArrayList<>(members2.size());
				for (ObjectType m : members1) {
					lst1.add(m.getName());
				}
				for (ObjectType m : members2) {
					lst2.add(m.getName());
				}
				return deepComparator.differences(lst1, lst2, nbMaxDifferences);
			}
		});
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void testGenerateRelaySchema_allGraphQLCases() throws IOException {
		executeGenerateRelaySchemaTest(AllGraphQLCases_Client_SpringConfiguration.class,
				GeneratedAllGraphQLCases_Client_SpringConfiguration.class,
				"mergeSchema for allGraphQLCases.graphqls (addRelayConnections=false)");

	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void testGenerateRelaySchema_allGraphQLCases_addRelayConnections() throws IOException {
		executeGenerateRelaySchemaTest(AllGraphQLCases_Client_SpringConfiguration_addRelayConnections.class,
				GeneratedAllGraphQLCases_Client_SpringConfiguration_addRelayConnections.class,
				"mergeSchema for allGraphQLCases.graphqls (addRelayConnections=true)");
	}

	@Test
	@Execution(ExecutionMode.CONCURRENT)
	void testGenerateRelaySchema_forum() throws IOException {
		executeGenerateRelaySchemaTest(Forum_Client_SpringConfiguration.class,
				GeneratedForum_Client_SpringConfiguration.class, "mergeSchema for forum.graphqls");
	}

	/**
	 * Executes one test for the generate-relay-schema goal/task. This is not a unit test. It's more a full integration
	 * test, that checks that the GraphQL generated from the forum schema is complete.<BR/>
	 * It's too complex to compare the GraphQL AST, especially to manage things like the <I>extends</I> keyword. So the
	 * principle is:
	 * <UL>
	 * <LI>Load the source GraphQL schemas in a source {@link DocumentParser}</LI>
	 * <LI>Generate the schema</LI>
	 * <LI>Load the generated schema in a target {@link DocumentParser}</LI>
	 * <LI>Deep compare the source and the target {@link DocumentParser} with a generic tool</LI>
	 * </UL>
	 * Doing this insure that the code is stable even if the {@link DocumentParser} implementation changes. And as this
	 * implementation is heavily tested, we an consider that it is complete, out of some known and documented
	 * limitations
	 * 
	 * @param sourceSpringConfClass
	 * @param generatedSpringConfClass
	 * @param test
	 */
	private void executeGenerateRelaySchemaTest(Class<?> sourceSpringConfClass, Class<?> generatedSpringConfClass,
			String test) {
		// Go, go, go
		AbstractApplicationContext ctx = new AnnotationConfigApplicationContext(sourceSpringConfClass);
		Merge sourceRelaySchema = ctx.getBean(Merge.class);
		MergeDocumentParser sourceDocumentParser = sourceRelaySchema.documentParser;
		sourceDocumentParser.parseDocuments();
		sourceRelaySchema.generateGraphQLSchema();
		// Let's log the current configuration (this will do something only when in debug mode)
		ctx.getBean(MergeSchemaConfiguration.class).logConfiguration();
		ctx.close();

		if (sourceDocumentParser.getConfiguration().isAddRelayConnections()) {
			// The addRelayConnections parameters generates changes in the generated schema. Let's update the source
			// schema, to take into account these changes

		}

		// Let's load the content of the generated schema in a new DocumentParser
		ctx = new AnnotationConfigApplicationContext(generatedSpringConfClass);
		// Let's log the current configuration (this will do something only when in debug mode)
		ctx.getBean(MergeSchemaConfiguration.class).logConfiguration();
		//
		MergeDocumentParser generatedDocumentParser = ctx.getBean(MergeDocumentParser.class);
		Merge generatedRelaySchema = ctx.getBean(Merge.class);
		generatedDocumentParser.parseDocuments();
		generatedRelaySchema.generateGraphQLSchema();
		//
		ctx.close();

		// Let's check the two DocumentParser instances, to check they are the same
		List<Difference> differences = deepComparator.differences(sourceDocumentParser, generatedDocumentParser,
				Integer.MAX_VALUE);
		if (differences.size() > 0) {
			String firstLine = test + ": " + differences.size()
					+ " differences found between the two parsers (details in the log file: target/JUnit-tests.log)";
			logger.info(firstLine);

			for (Difference d : differences) {
				logger.info("   " + d.path + " [diff: " + d.type + "] ");
				logger.info("        val1: " + d.value1);
				logger.info("        val2: " + d.value2);
				if (d.info != null)
					logger.info("        info: " + d.info);
			}

			fail(firstLine);
		}
	}

}
