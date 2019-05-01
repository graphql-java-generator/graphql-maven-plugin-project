package graphql.java.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;

import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.GraphQLError;
import graphql.java.client.response.GraphQLExecutionException;
import graphql.schema.GraphQLSchema;
import graphql.schema.StaticDataFetcher;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;

public class ManualTest_GraphQLJava {

	public static void main(String[] args) throws GraphQLExecutionException, IOException {
		ManualTest_GraphQLJava test = new ManualTest_GraphQLJava();
		test.executeHero();
		// test.executeHuman();
	}

	GraphQL graphQL;

	public ManualTest_GraphQLJava() {
		graphQL = getGraphQL();
	}

	GraphQL getGraphQL() {
		String schema = readSchema(new ClassPathResource("/starWarsSchema.graphqls"));

		SchemaParser schemaParser = new SchemaParser();
		TypeDefinitionRegistry typeDefinitionRegistry = schemaParser.parse(schema);

		RuntimeWiring runtimeWiring = RuntimeWiring.newRuntimeWiring()
				.type("Query", builder -> builder.dataFetcher("hello", new StaticDataFetcher("world"))).build();

		SchemaGenerator schemaGenerator = new SchemaGenerator();
		GraphQLSchema graphQLSchema = schemaGenerator.makeExecutableSchema(typeDefinitionRegistry, runtimeWiring);

		return GraphQL.newGraphQL(graphQLSchema).build();
	}

	private String readSchema(org.springframework.core.io.Resource resource) {
		StringWriter writer = new StringWriter();
		try (InputStream inputStream = resource.getInputStream()) {
			IOUtils.copy(inputStream, writer, StandardCharsets.UTF_8);
		} catch (IOException e) {
			throw new IllegalStateException("Cannot read graphql schema from resource " + resource, e);
		}
		return writer.toString();
	}

	private void executeHero() {
		ExecutionInput executionInput = ExecutionInput.newExecutionInput().query("query { hero { name } }").build();

		ExecutionResult executionResult = graphQL.execute(executionInput);

		Object data = executionResult.getData();
		List<GraphQLError> errors = executionResult.getErrors();

		System.out.println(executionResult.getData().toString());
	}

}
