/**
 * 
 */
package com.graphql_java_generator.plugin.conf;

/**
 * This enum lists the protocol that may be configured to execute queries and mutations. This is based on the protocols
 * supported by the <a href="https://spring.io/projects/spring-graphql">spring-graphql</a> project
 * 
 * @author etienne-sf
 */
public enum QueryMutationExecutionProtocol {

	http, webSocket;

}
