package com.graphql_java_generator.samples.basic.client;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.graphql_java_generator.client.GraphqlClientUtils;
import com.graphql_java_generator.samples.customtemplates.client.graphql.forum.client.QueryExecutor;

@Configuration
@SpringBootApplication
@ComponentScan(basePackageClasses = { GraphqlClientUtils.class, QueryExecutor.class })
public class SpringConfiguration {

}
