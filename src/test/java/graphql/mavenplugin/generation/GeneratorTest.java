package graphql.mavenplugin.generation;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import org.apache.commons.io.IOUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import graphql.language.Document;
import graphql.mavenplugin.SpringConfiguration;
import graphql.parser.Parser;

/**
 * 
 * @author EtienneSF
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { SpringConfiguration.class })
class GeneratorTest {

	@Autowired
	private ApplicationContext ctx;

	private Generator generator;
	private Parser parser;

	@BeforeEach
	void setUp() throws Exception {
		generator = new Generator();
		parser = new Parser();
	}

	@Test
	void testGenerateTargetFiles() throws MojoExecutionException {
		// Preparation
		Document basic = parser.parseDocument(readSchema(ctx.getResource("/helloworld.graphqls")));
		Document helloWorld = parser.parseDocument(readSchema(ctx.getResource("/helloworld.graphqls")));
		generator.documents = new ArrayList<Document>();
		generator.documents.add(basic);
		generator.documents.add(helloWorld);

		// Go, go, go
		int i = generator.generateTargetFiles();

		// Verification
		assertEquals(3, i, "3 classes expected");
	}

	@Test
	void testGenerateForOneDocument_basic() {
		// Preparation
		Resource resource = ctx.getResource("/basic.graphqls");
		Document doc = parser.parseDocument(readSchema(resource));

		// Go, go, go
		int i = generator.generateForOneDocument(doc);

		// Verification
		assertEquals(1, i, "One class is generated");
	}

	@Test
	void testGenerateForOneDocument_helloworld() {
		// Preparation
		Resource resource = ctx.getResource("/helloworld.graphqls");
		Document doc = parser.parseDocument(readSchema(resource));

		// Go, go, go
		int i = generator.generateForOneDocument(doc);

		// Verification
		assertEquals(2, i, "Two classes are generated");
	}

	private String readSchema(Resource resource) {
		StringWriter writer = new StringWriter();
		try (InputStream inputStream = resource.getInputStream()) {
			IOUtils.copy(inputStream, writer, StandardCharsets.UTF_8);
		} catch (IOException e) {
			throw new IllegalStateException("Cannot read graphql schema from resource " + resource, e);
		}
		return writer.toString();
	}

}
