##
#set( $D = '$' )
##
## Maven ignores the default value for springBeanSuffix, and replaces it by a null value. In this case, we replace the value by an empty String 
#if (!$configuration.springBeanSuffix) #set($springBeanSuffix="") #else #set($springBeanSuffix = ${configuration.springBeanSuffix}) #end
##
/** Generated by the default template from graphql-java-generator */
package ${configuration.springAutoConfigurationPackage};

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.graphql.client.GraphQlClient;
import org.springframework.graphql.client.HttpGraphQlClient;
import org.springframework.graphql.client.WebSocketGraphQlClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.Builder;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;

import com.graphql_java_generator.client.GraphqlClientUtils;
import com.graphql_java_generator.client.SpringContextBean;
import com.graphql_java_generator.util.GraphqlUtils;

/**
 * This Spring {@link AutoConfiguration} class defines the default Spring Beans for this GraphQL schema.
 * 
 * @author etienne-sf
 */
@AutoConfiguration
public class GraphQLMavenPluginAutoConfiguration${springBeanSuffix} {

	private static Logger logger = LoggerFactory.getLogger(GraphQLMavenPluginAutoConfiguration${springBeanSuffix}.class);

	// Creating this bean makes sure that its static field is set. This is mandatory for some part of the code that must
	// be kept, to allow compliance with existing projects.
	@Autowired
	SpringContextBean springContextBean;

	@Value(value = "${D}{graphql.endpoint${springBeanSuffix}.url}")
	private String graphqlEndpoint${springBeanSuffix}Url;

	@Autowired
	ApplicationContext applicationContext;
	
	final SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

	/**
	 * This beans defines the GraphQL endpoint for the current GraphQL schema, as a {@link String}. The <I>application.properties</I> 
	 * must define the GraphQL URL endpoint in the <I>graphql.endpoint${springBeanSuffix}.url</I> property.
	 * 
	 * 
	 * @return Returns the value of the <I>graphql.endpoint${springBeanSuffix}.url</I> application property.
	 * @see https://docs.spring.io/spring-boot/docs/2.3.3.RELEASE/reference/html/spring-boot-features.html#boot-features-external-config
	 */
	@Bean
	@ConditionalOnMissingBean(name = "graphqlEndpoint${springBeanSuffix}")
	String graphqlEndpoint${springBeanSuffix}() {
		return graphqlEndpoint${springBeanSuffix}Url;
	}

	/**
	 * The Spring reactive {@link WebClient} that will execute the HTTP requests for GraphQL queries and mutations.<BR/>
	 * This bean is only created if no such bean already exists
	 */
	@Bean
	@ConditionalOnMissingBean(name = "webClient${springBeanSuffix}")
	public WebClient webClient${springBeanSuffix}(String graphqlEndpoint${springBeanSuffix}) {
		logger.debug("Creating default webClient${springBeanSuffix} (from the GraphQLSpringAutoConfiguration${springBeanSuffix} class) for graphqlEndpoint${springBeanSuffix} [webSocketGraphQlClientAllGraphQLCases: context startup date={}}]",
				formater.format(new Date(applicationContext.getStartupDate())));
		return WebClient.builder()//
				.baseUrl(graphqlEndpoint${springBeanSuffix})//
				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.defaultUriVariables(Collections.singletonMap("url", graphqlEndpoint${springBeanSuffix}))
				.build();
	}

#if ($configuration.queryMutationExecutionProtocol == "http")
	@Bean
	@ConditionalOnMissingBean(name = "httpGraphQlClient${springBeanSuffix}")
	GraphQlClient httpGraphQlClient${springBeanSuffix}() {
		logger.debug("Creating default httpGraphQlClient${springBeanSuffix} (from the GraphQLSpringAutoConfiguration${springBeanSuffix} class) [webSocketGraphQlClientAllGraphQLCases: context startup date={}}]",
				formater.format(new Date(applicationContext.getStartupDate())));
		// The usual way to autowire other beans is to define them as parameters of the bean definition methods. But this doesn't
		// seem to work when several beans of the same type exist, and one is defined as "@Primary". 
		// So we retrieve "manually" the needed bean from its name:
		WebClient webClient = (WebClient) applicationContext.getBean("webClient${springBeanSuffix}");
		return HttpGraphQlClient.builder(webClient).build();
	}

#end
## If the protocol for queries/mutations is webSocket, or if there are subscription, then the web socket GraphQL client spring bean must be built
#if ($configuration.queryMutationExecutionProtocol == "webSocket" || ! $documentParser.subscriptionType) ## $documentParser.subscriptionType != null
	@Bean
	@ConditionalOnMissingBean(name = "webSocketGraphQlClient${springBeanSuffix}")
	GraphQlClient webSocketGraphQlClient${springBeanSuffix}() {
		logger.debug("Creating default webSocketGraphQlClient${springBeanSuffix} (from the GraphQLSpringAutoConfiguration${springBeanSuffix} class) [webSocketGraphQlClientAllGraphQLCases: context startup date={}}]",
				formater.format(new Date(applicationContext.getStartupDate())));
		WebSocketClient client = new ReactorNettyWebSocketClient();
		return WebSocketGraphQlClient.builder(graphqlEndpoint${springBeanSuffix}Url, client).build();
	}

#end
	@Bean
	@ConditionalOnMissingBean(name = "graphqlClientUtils")
	GraphqlClientUtils graphqlClientUtils() {
		return GraphqlClientUtils.graphqlClientUtils;
	}

	@Bean
	@ConditionalOnMissingBean(name = "graphqlUtils")
	GraphqlUtils graphqlUtils() {
		return GraphqlUtils.graphqlUtils;
	}
}
