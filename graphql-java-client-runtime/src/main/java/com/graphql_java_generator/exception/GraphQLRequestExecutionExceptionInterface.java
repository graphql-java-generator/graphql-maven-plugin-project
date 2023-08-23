package com.graphql_java_generator.exception;

import java.util.List;

import org.springframework.graphql.ResponseError;

public interface GraphQLRequestExecutionExceptionInterface {

	List<ResponseError> getErrors();

}