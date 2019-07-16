/**
 * 
 */
package graphql.mavenplugin.test.helper;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import graphql.mavenplugin.ResourceSchemaStringProvider;

/**
 * @author EtienneSF
 */
@Component
public class GraphqlTestHelper {

	@Autowired
	ResourceSchemaStringProvider schemaStringProvider;

	public String readSchema(Resource resource) {
		StringWriter writer = new StringWriter();
		try (InputStream inputStream = resource.getInputStream()) {
			IOUtils.copy(inputStream, writer, StandardCharsets.UTF_8);
		} catch (IOException e) {
			throw new IllegalStateException("Cannot read graphql schema from resource " + resource, e);
		}
		return writer.toString();
	}

	/**
	 * This method checks that the {@link SchemaStringProvider} bean:
	 * <UL>
	 * <LI>Is an instance of our {@link SchemaStringProvider}, that is: {@link MavenResourceSchemaStringProvider}</LI>
	 * <LI>Its file pattern end with the given pattern. e.g. : endsWith the filename for the graphql of this unit test
	 * like 'helloworld.graphqls'</LI>
	 * </UL>
	 * 
	 * @param patternToCheck
	 *            Typically, the file name that contains the graphql schema for this test.
	 */
	public void checkSchemaStringProvider(String patternToCheck) {
		String foundPattern = schemaStringProvider.getSchemaFilePattern();
		assertTrue(foundPattern.endsWith(patternToCheck), "schemaStringProvider pattern should end with '"
				+ patternToCheck + "', but it is '" + foundPattern + "'");
	}

}
