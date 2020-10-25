##
## Velocity template for the Subscription type (client side). 
##
## The generated class contains:
## - If separateUtilityClasses is false: all the fields (and their getters/setters), as stated in the GraphQL schema 
## - All the utility classes that allow to prepare and execute the subscription
##
##
## This template has these inputs:
## packageUtilName 			The package where this class must be generated
## configuration		The plugin's configuration
## object					The subscription type, for which this executor is being generated
##
/** Generated by the default template from graphql-java-generator */
package ${packageUtilName};
#macro(inputParams)#foreach ($inputParameter in $field.inputParameters), ${inputParameter.javaType} ${inputParameter.javaName}#end#end
#macro(inputValues)#foreach ($inputParameter in $field.inputParameters), ${inputParameter.javaName}#end#end

import java.util.HashMap;
import java.util.Map;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.ws.rs.client.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.graphql_java_generator.annotation.GraphQLNonScalar;
import com.graphql_java_generator.annotation.GraphQLScalar;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;
import com.graphql_java_generator.client.request.InputParameter;
import com.graphql_java_generator.client.request.ObjectResponse;

#foreach($import in ${object.imports})
import $import;
#end

import com.graphql_java_generator.client.GraphQLConfiguration;
import com.graphql_java_generator.client.GraphqlClientUtils;
import com.graphql_java_generator.client.SubscriptionCallback;
import com.graphql_java_generator.client.SubscriptionClient;

/**
 * @author generated by graphql-java-generator
 * @see <a href="https://github.com/graphql-java-generator/graphql-java-generator">https://github.com/graphql-java-generator/graphql-java-generator</a>
 * @deprecated Please use the {@link ${object.classSimpleName}Executor} class instead.
 */
@Deprecated
${object.annotation}
public class ${object.classSimpleName} extends ${object.classSimpleName}Executor {

#if(!${configuration.separateUtilityClasses})
#parse ("templates/object_content.vm.java")
#end

	/** {@inheritDoc} */
	public ${object.classSimpleName}(String graphqlEndpoint) {
		super(graphqlEndpoint);
	}

	/** {@inheritDoc} */
	public ${object.classSimpleName}(String graphqlEndpoint, SSLContext sslContext, HostnameVerifier hostnameVerifier) {
		super(graphqlEndpoint, sslContext, hostnameVerifier);
	}

	/** {@inheritDoc} */
	public ${object.classSimpleName}(String graphqlEndpoint, Client client, ObjectMapper objectMapper) {
		super(graphqlEndpoint, client, objectMapper);
	}

}
