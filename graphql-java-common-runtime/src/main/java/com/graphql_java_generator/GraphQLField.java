package com.graphql_java_generator;

/**
 * Common interface implemented by all GraphQL POJO field name enumerations.
 */
public interface GraphQLField {

    /**
     * Returns the field name of the field
     * @return
     */
    String getFieldName();

    /**
     * Returns the GraphQL class which declares the field
     * @return
     */
    Class<?> getGraphQLType();

}
