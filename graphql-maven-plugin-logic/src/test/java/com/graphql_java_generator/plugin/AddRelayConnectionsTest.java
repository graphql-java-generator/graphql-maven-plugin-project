package com.graphql_java_generator.plugin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import com.graphql_java_generator.plugin.language.Directive;
import com.graphql_java_generator.plugin.language.DirectiveLocation;
import com.graphql_java_generator.plugin.language.impl.InterfaceType;
import com.graphql_java_generator.plugin.test.helper.MergeSchemaConfigurationTestHelper;

import merge.mavenplugin_notscannedbyspring.AllGraphQLCases_Client_SpringConfiguration;

@Disabled
@Execution(ExecutionMode.CONCURRENT)
class AddRelayConnectionsTest {

	@BeforeEach
	void setup() {

	}

	/**
	 * Test of the addRelayConnections capabilities, on a schema that doesn't contain any
	 */
	@Test
	void testAddRelayConnections_schemaWithoutRelay() {
		// Preparation
		AbstractApplicationContext ctx = new AnnotationConfigApplicationContext(
				AllGraphQLCases_Client_SpringConfiguration.class);
		((MergeSchemaConfigurationTestHelper) ctx.getBean(MergeSchemaConfiguration.class)).addRelayConnections = true;
		ctx.getBean(MergeSchemaConfiguration.class).logConfiguration();
		MergeDocumentParser documentParser = ctx.getBean(MergeDocumentParser.class);

		// Go, go, go
		documentParser.parseDocuments();

		// Verification
		checkRelayConnectionDirective(documentParser);
		checkNodeInterface(documentParser);
		checkPageInfoType(documentParser);
		checkRelayConnexionDirective(documentParser);

		// Let's free the used resources
		ctx.close();
	}

	@Test
	void testAddRelayConnections_schemaAlreadyRelayCompliant() {
		fail("Not yet implemented");
	}

	@Test
	void testAddRelayConnections_schemaWithWrongRelayConnectionDirective() {
		fail("Not yet implemented");
	}

	@Test
	void testAddRelayConnections_schemaWithWrongNode() {
		fail("Not yet implemented");
	}

	@Test
	void testAddRelayConnections_schemaWithWrongPageInfo() {
		fail("Not yet implemented");
	}

	@Test
	void testAddRelayConnections_schemaWithWrongEdge() {
		fail("Not yet implemented");
	}

	@Test
	void testAddRelayConnections_schemaWithWrongConnection() {
		fail("Not yet implemented");
	}

	private void checkRelayConnectionDirective(MergeDocumentParser documentParser) {
		final String RELAY_CONNECTION = "RelayConnection";
		boolean found = false;
		for (Directive d : documentParser.getDirectives()) {
			if (RELAY_CONNECTION.equals(d.getName())) {
				// The directive should exist only once, so we may not already have found it.
				if (found) {
					fail("There are two directives with '" + RELAY_CONNECTION + "' as a name");
				}
				// We've found it.
				found = true;
				// Let's check its properties
				assertEquals(0, d.getArguments().size(), "No arguments");
				assertEquals(1, d.getDirectiveLocations().size(), "Directive has one location");
				assertEquals(DirectiveLocation.FIELD_DEFINITION, d.getDirectiveLocations().get(0),
						"Directive locations may only be: FIELD_DEFINITION");
			}
		}

		if (!found) {
			fail("The directive " + RELAY_CONNECTION + " has not been found");
		}
	}

	private void checkNodeInterface(MergeDocumentParser documentParser) {
		final String NODE = "Node";
		boolean found = false;
		for (InterfaceType d : documentParser.getInterfaceTypes()) {
			if (NODE.equals(d.getName())) {
				// The interface should exist only once, so we may not already have found it.
				if (found) {
					fail("There are two interfaces with '" + NODE + "' as a name");
				}
				// We've found it.
				found = true;
				// Let's check its properties
				assertEquals(0, d.getImplementz().size(), "No implements");
				assertEquals(0, d.getMemberOfUnions().size(), "No unions");
				assertEquals(0, d.getFields().size(), "One field");
				assertEquals("id", d.getFields().get(0).getName(), "field is id");
				assertEquals("ID", d.getFields().get(0).getGraphQLTypeName(), "field'stype is ID");
				assertEquals(true, d.getFields().get(0).isId(), "field is an ID");
				assertEquals(false, d.getFields().get(0).isItemMandatory(), "field is not a list");
				assertEquals(false, d.getFields().get(0).isList(), "field is not a list");
				assertEquals(true, d.getFields().get(0).isMandatory(), "field is mandatory");
				assertEquals(null, d.getRequestType(), "not a query/mutation/subscription");
				assertEquals(false, d.isInputType(), "Not an input type");
			}
		}

		if (!found) {
			fail("The interface " + NODE + " has not been found");
		}
	}

	private void checkPageInfoType(MergeDocumentParser documentParser) {
		fail("Not yet implemented");
	}

	private void checkRelayConnexionDirective(MergeDocumentParser documentParser) {
		fail("Not yet implemented");
	}

}
