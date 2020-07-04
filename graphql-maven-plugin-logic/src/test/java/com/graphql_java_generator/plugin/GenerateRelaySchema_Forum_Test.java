package com.graphql_java_generator.plugin;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.graphql_java_generator.GraphqlUtils;
import com.graphql_java_generator.plugin.language.Type;
import com.graphql_java_generator.plugin.language.impl.FieldImpl;
import com.graphql_java_generator.plugin.test.helper.DeepComparator;
import com.graphql_java_generator.plugin.test.helper.DeepComparator.ComparisonRule;
import com.graphql_java_generator.plugin.test.helper.DeepComparator.DiffenceType;
import com.graphql_java_generator.plugin.test.helper.DeepComparator.Difference;
import com.graphql_java_generator.plugin.test.helper.GenerateRelaySchemaConfigurationTestHelper;
import com.graphql_java_generator.plugin.test.helper.MavenTestHelper;

import generate_relay_schema.mavenplugin_notscannedbyspring.Forum_Client_SpringConfiguration;
import generate_relay_schema.mavenplugin_notscannedbyspring.GeneratedForum_Client_SpringConfiguration;
import graphql.language.Document;
import lombok.EqualsAndHashCode;

//import com.cedarsoftware.util.DeepEquals;

/**
 * 
 * @author etienne-sf
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { Forum_Client_SpringConfiguration.class })
class GenerateRelaySchema_Forum_Test {

	/** The logger for this instance */
	protected transient Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	DocumentParser documentParser;

	@Autowired
	GenerateRelaySchema generateRelaySchema;

	@Autowired
	GenerateRelaySchemaConfigurationTestHelper configuration;

	@Autowired
	GraphqlUtils graphqlUtils;

	MavenTestHelper mavenTestHelper = new MavenTestHelper();
	Document generatedDocument;

	DeepComparator deepComparator;

	@EqualsAndHashCode // This will generate the equals method, used later in the unit tests. Note: this won't deep
						// compare the collections.
	@Deprecated
	class FieldProperties {
		String name;
		boolean mandatory = false;
		boolean list = false;
		boolean itemMandatory = false;
		List<FieldProperties> props = new ArrayList<>();
	}

	@BeforeEach
	void setUp() {
		deepComparator = new DeepComparator();

		//////////////////////////////////////////////////////////////////////////////////////////////////////////////
		/////////////// Compared classes //////////////////////////////////////////////////////////////////////////
		deepComparator.addComparedClass(com.graphql_java_generator.plugin.GenerateRelaySchemaDocumentParser.class);
		// language
		deepComparator.addComparedClass(com.graphql_java_generator.plugin.language.impl.AppliedDirectiveImpl.class);
		deepComparator.addComparedClass(com.graphql_java_generator.plugin.language.impl.BatchLoaderImpl.class);
		deepComparator.addComparedClass(com.graphql_java_generator.plugin.language.impl.CustomScalarType.class);
		deepComparator.addComparedClass(com.graphql_java_generator.plugin.language.impl.DataFetcherImpl.class);
		deepComparator.addComparedClass(com.graphql_java_generator.plugin.language.impl.DataFetchersDelegateImpl.class);
		deepComparator.addComparedClass(com.graphql_java_generator.plugin.language.impl.DirectiveImpl.class);
		deepComparator.addComparedClass(com.graphql_java_generator.plugin.language.impl.EnumType.class);
		deepComparator.addComparedClass(com.graphql_java_generator.plugin.language.impl.EnumValueImpl.class);
		deepComparator.addComparedClass(com.graphql_java_generator.plugin.language.impl.FieldImpl.class);
		deepComparator.addComparedClass(com.graphql_java_generator.plugin.language.impl.InterfaceType.class);
		deepComparator.addComparedClass(com.graphql_java_generator.plugin.language.impl.ObjectType.class);
		deepComparator.addComparedClass(com.graphql_java_generator.plugin.language.impl.RelationImpl.class);
		deepComparator.addComparedClass(com.graphql_java_generator.plugin.language.impl.ScalarType.class);
		deepComparator.addComparedClass(com.graphql_java_generator.plugin.language.impl.UnionType.class);

		//////////////////////////////////////////////////////////////////////////////////////////////////////////////
		/////////////// Ignored classes //////////////////////////////////////////////////////////////////////////
		deepComparator.addIgnoredClass(Document.class);
		deepComparator.addIgnoredClass(GenerateRelaySchemaConfigurationTestHelper.class);
		deepComparator.addIgnoredClass(GraphqlUtils.class);

		//////////////////////////////////////////////////////////////////////////////////////////////////////////////
		/////////////// Ignored fields //////////////////////////////////////////////////////////////////////////
		deepComparator.addIgnoredFields(com.graphql_java_generator.plugin.GenerateRelaySchemaDocumentParser.class,
				"configuration");
		deepComparator.addIgnoredFields(com.graphql_java_generator.plugin.GenerateRelaySchemaDocumentParser.class,
				"graphqlUtils");
		deepComparator.addIgnoredFields(com.graphql_java_generator.plugin.GenerateRelaySchemaDocumentParser.class,
				"documents");
		deepComparator.addIgnoredFields(com.graphql_java_generator.plugin.GenerateRelaySchemaDocumentParser.class,
				"objectTypeExtensionDefinitions");

		// To break the cycle where comparing the type of a FieldImpl, we define a specific comparison rule:
		deepComparator.addSpecificComparisonRules(FieldImpl.class, "owningType", new ComparisonRule() {
			@Override
			public List<Difference> compare(Object o1, Object o2) {
				Type type1 = (Type) o1;
				Type type2 = (Type) o2;
				if (!type1.getName().equals(type2.getName())) {
					List<Difference> differences = new ArrayList<>();
					differences.add(new DeepComparator.Difference("/name", DiffenceType.VALUE, type1.getName(),
							type2.getName(), null));
					return differences;
				}
				return null;
			}
		});
	}

	/**
	 * This is not a unit test. It's more a full integration test, that checks that the GraphQL generated from the forum
	 * schema is complete.<BR/>
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
	 * @throws IOException
	 */
	@Test
	@DirtiesContext
	void testGenerateRelaySchema() throws IOException {

		// Go, go, go
		generateRelaySchema.generateRelaySchema();

		// Let's load the content of the generated schema in a new DocumentParser
		AbstractApplicationContext ctx = new AnnotationConfigApplicationContext(
				GeneratedForum_Client_SpringConfiguration.class);
		// Let's log the current configuration (this will do something only when in debug mode)
		ctx.getBean(GenerateRelaySchemaConfiguration.class).logConfiguration();
		//
		GenerateRelaySchemaDocumentParser generatedDocumentParser = ctx
				.getBean(GenerateRelaySchemaDocumentParser.class);
		generatedDocumentParser.parseDocuments();
		//
		ctx.close();

		// Let's check the two DocumentParser instances, to check they are the same
		List<Difference> differences = deepComparator.compare(documentParser, generatedDocumentParser);
		if (differences.size() > 0) {
			String firstLine = differences.size()
					+ " differences found between the two parsers (details in the log file: target/JUnit-tests.log4j.log)";
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

		fail("	Un ajout dans le README de java-util (DeepEquals) vers la javadoc serait bien");

		fail("missing the source and target DocumentParser comparison");
	}

}
