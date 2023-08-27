package com.graphql_java_generator.client;

/**
 * This interface marks a class as being the non reactive executor for a GraphQL mutation. This is allows Spring to
 * inject the executor for the mutation type (if any) into an autowired attribute of a Spring Bean.
 * 
 * @author etienne-sf
 */
public interface GraphQLMutationExecutor {

}
