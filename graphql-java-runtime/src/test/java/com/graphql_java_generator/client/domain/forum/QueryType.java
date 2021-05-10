/** Generated by the default template from graphql-java-generator */
package com.graphql_java_generator.client.domain.forum;

import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.ws.rs.client.Client;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.graphql_java_generator.GraphQLField;
import com.graphql_java_generator.annotation.GraphQLInputParameters;
import com.graphql_java_generator.annotation.GraphQLNonScalar;
import com.graphql_java_generator.annotation.GraphQLObjectType;
import com.graphql_java_generator.annotation.GraphQLQuery;
import com.graphql_java_generator.annotation.GraphQLScalar;
import com.graphql_java_generator.annotation.RequestType;
import com.graphql_java_generator.client.GraphQLObjectMapper;
import com.graphql_java_generator.client.request.ObjectResponse;
import com.graphql_java_generator.exception.GraphQLRequestExecutionException;
import com.graphql_java_generator.exception.GraphQLRequestPreparationException;

/**
 * This class contains the response for a full request. See the
 * <A HREF="https://graphql-maven-plugin-project.graphql-java-generator.com/exec_graphql_requests.html">plugin web
 * site</A> for more information on full and partial requests.<BR/>
 * It also allows access to the _extensions_ part of the response. Take a look at the
 * <A HRE="https://spec.graphql.org/June2018/#sec-Response">GraphQL spec</A> for more information on this.
 * 
 * @author generated by graphql-java-generator
 * @see <a href=
 *      "https://github.com/graphql-java-generator/graphql-java-generator">https://github.com/graphql-java-generator/graphql-java-generator</a>
 */
@GraphQLQuery(name = "QueryType", type = RequestType.query)
@GraphQLObjectType("QueryType")
public class QueryType extends QueryTypeExecutor implements com.graphql_java_generator.client.GraphQLRequestObject {

	private ObjectMapper mapper = null;
	private JsonNode extensions;
	private Map<String, JsonNode> extensionsAsMap = null;

	public QueryType() {
		// No action
	}

	@JsonProperty("boards")
	@JsonDeserialize(using = CustomJacksonDeserializers.ListBoard.class)
	@GraphQLNonScalar(fieldName = "boards", graphQLTypeSimpleName = "Board", javaClass = Board.class)
	List<Board> boards;

	@JsonProperty("nbBoards")
	@GraphQLScalar(fieldName = "nbBoards", graphQLTypeSimpleName = "Int", javaClass = Integer.class)
	Integer nbBoards;

	@JsonProperty("topics")
	@JsonDeserialize(using = CustomJacksonDeserializers.ListTopic.class)
	@GraphQLInputParameters(names = { "boardName" }, types = { "String" }, mandatories = { true }, listDepths = {
			0 }, itemsMandatory = { false })
	@GraphQLNonScalar(fieldName = "topics", graphQLTypeSimpleName = "Topic", javaClass = Topic.class)
	List<Topic> topics;

	@JsonProperty("findTopics")
	@JsonDeserialize(using = CustomJacksonDeserializers.ListTopic.class)
	@GraphQLInputParameters(names = { "boardName", "keyword" }, types = { "String", "String" }, mandatories = { true,
			false }, listDepths = { 0, 1 }, itemsMandatory = { false, true })
	@GraphQLNonScalar(fieldName = "findTopics", graphQLTypeSimpleName = "Topic", javaClass = Topic.class)
	List<Topic> findTopics;

	@JsonProperty("__schema")
	@GraphQLNonScalar(fieldName = "__schema", graphQLTypeSimpleName = "__Schema", javaClass = __Schema.class)
	__Schema __schema;

	@JsonProperty("__type")
	@GraphQLInputParameters(names = { "name" }, types = { "String" }, mandatories = { true }, listDepths = {
			0 }, itemsMandatory = { false })
	@GraphQLNonScalar(fieldName = "__type", graphQLTypeSimpleName = "__Type", javaClass = __Type.class)
	__Type __type;

	@JsonProperty("__typename")
	@GraphQLScalar(fieldName = "__typename", graphQLTypeSimpleName = "String", javaClass = String.class)
	String __typename;

	public void setBoards(List<Board> boards) {
		this.boards = boards;
	}

	public List<Board> getBoards() {
		return boards;
	}

	public void setNbBoards(Integer nbBoards) {
		this.nbBoards = nbBoards;
	}

	public Integer getNbBoards() {
		return nbBoards;
	}

	public void setTopics(List<Topic> topics) {
		this.topics = topics;
	}

	public List<Topic> getTopics() {
		return topics;
	}

	public void setFindTopics(List<Topic> findTopics) {
		this.findTopics = findTopics;
	}

	public List<Topic> getFindTopics() {
		return findTopics;
	}

	public void set__schema(__Schema __schema) {
		this.__schema = __schema;
	}

	public __Schema get__schema() {
		return __schema;
	}

	public void set__type(__Type __type) {
		this.__type = __type;
	}

	public __Type get__type() {
		return __type;
	}

	public void set__typename(String __typename) {
		this.__typename = __typename;
	}

	public String get__typename() {
		return __typename;
	}

	@Override
	public String toString() {
		return "QueryType {" + "boards: " + boards + ", " + "nbBoards: " + nbBoards + ", " + "topics: " + topics + ", "
				+ "findTopics: " + findTopics + ", " + "__schema: " + __schema + ", " + "__type: " + __type + ", "
				+ "__typename: " + __typename + "}";
	}

	/**
	 * Enum of field names
	 */
	public static enum Field implements GraphQLField {
		Boards("boards"), NbBoards("nbBoards"), Topics("topics"), FindTopics("findTopics"), __schema(
				"__schema"), __type("__type"), __typename("__typename");

		private String fieldName;

		Field(String fieldName) {
			this.fieldName = fieldName;
		}

		@Override
		public String getFieldName() {
			return fieldName;
		}

		@Override
		public Class<?> getGraphQLType() {
			return this.getClass().getDeclaringClass();
		}

	}

	public static Builder builder() {
		return new Builder();
	}

	/**
	 * Builder
	 */
	public static class Builder {
		private List<Board> boards;
		private Integer nbBoards;
		private List<Topic> topics;
		private List<Topic> findTopics;
		private __Schema __schema;
		private __Type __type;

		public Builder withBoards(List<Board> boards) {
			this.boards = boards;
			return this;
		}

		public Builder withNbBoards(Integer nbBoards) {
			this.nbBoards = nbBoards;
			return this;
		}

		public Builder withTopics(List<Topic> topics) {
			this.topics = topics;
			return this;
		}

		public Builder withFindTopics(List<Topic> findTopics) {
			this.findTopics = findTopics;
			return this;
		}

		public Builder with__schema(__Schema __schema) {
			this.__schema = __schema;
			return this;
		}

		public Builder with__type(__Type __type) {
			this.__type = __type;
			return this;
		}

		public QueryType build() {
			QueryType _object = new QueryType();
			_object.setBoards(boards);
			_object.setNbBoards(nbBoards);
			_object.setTopics(topics);
			_object.setFindTopics(findTopics);
			_object.set__schema(__schema);
			_object.set__type(__type);
			_object.set__typename("QueryType");
			return _object;
		}
	}

	/** {@inheritDoc} */
	public QueryType(String graphqlEndpoint) {
		super(graphqlEndpoint);
	}

	/** {@inheritDoc} */
	public QueryType(String graphqlEndpoint, SSLContext sslContext, HostnameVerifier hostnameVerifier) {
		super(graphqlEndpoint, sslContext, hostnameVerifier);
	}

	/** {@inheritDoc} */
	public QueryType(String graphqlEndpoint, Client client, GraphQLObjectMapper objectMapper) {
		super(graphqlEndpoint, client, objectMapper);
	}

	private ObjectMapper getMapper() {
		if (mapper == null) {
			mapper = new ObjectMapper();
		}
		return mapper;
	}

	public JsonNode getExtensions() {
		return extensions;
	}

	@Override
	public void setExtensions(JsonNode extensions) {
		this.extensions = extensions;
	}

	/**
	 * Returns the extensions as a map. The values can't be deserialized, as their type is unknown.
	 * 
	 * @return
	 */
	public Map<String, JsonNode> getExtensionsAsMap() {
		if (extensionsAsMap == null) {
			ObjectMapper mapper = new ObjectMapper();
			extensionsAsMap = mapper.convertValue(extensions, new TypeReference<Map<String, JsonNode>>() {
			});
		}
		return extensionsAsMap;
	}

	/**
	 * Parse the value for the given _key_, as found in the <I>extensions</I> field of the GraphQL server's response,
	 * into the given _t_ class.
	 * 
	 * @param <T>
	 * @param key
	 * @param t
	 * @return null if the key is not in the <I>extensions</I> map. Otherwise: the value for this _key_, as a _t_
	 *         instance
	 * @throws JsonProcessingException
	 *             When there is an error when converting the key's value into the _t_ class
	 */
	public <T> T getExtensionsField(String key, Class<T> t) throws JsonProcessingException {
		JsonNode node = getExtensionsAsMap().get(key);
		return (node == null) ? null : getMapper().treeToValue(node, t);
	}

	/**
	 * This method is deprecated: please use {@link QueryTypeExecutor} class instead of this class, to execute this
	 * method. It is maintained to keep existing code compatible with the generated code. It will be removed in 2.0
	 * version.
	 */
	@Override
	@Deprecated
	public QueryTypeResponse execWithBindValues(String queryResponseDef, Map<String, Object> parameters)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		return super.exec(queryResponseDef, parameters);
	}

	/**
	 * This method is deprecated: please use {@link QueryTypeExecutor} class instead of this class, to execute this
	 * method. It is maintained to keep existing code compatible with the generated code. It will be removed in 2.0
	 * version.
	 */
	@Override
	@Deprecated
	public QueryTypeResponse exec(String queryResponseDef, Object... paramsAndValues)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		return super.exec(queryResponseDef, paramsAndValues);
	}

	/**
	 * This method is deprecated: please use {@link QueryTypeExecutor} class instead of this class, to execute this
	 * method. It is maintained to keep existing code compatible with the generated code. It will be removed in 2.0
	 * version.
	 */
	@Override
	@Deprecated
	public QueryTypeResponse execWithBindValues(ObjectResponse objectResponse, Map<String, Object> parameters)
			throws GraphQLRequestExecutionException {
		return super.execWithBindValues(objectResponse, parameters);
	}

	/**
	 * This method is deprecated: please use {@link QueryTypeExecutor} class instead of this class, to execute this
	 * method. It is maintained to keep existing code compatible with the generated code. It will be removed in 2.0
	 * version.
	 */
	@Override
	@Deprecated
	public QueryTypeResponse exec(ObjectResponse objectResponse, Object... paramsAndValues)
			throws GraphQLRequestExecutionException {
		return super.exec(objectResponse, paramsAndValues);
	}

	/**
	 * This method is deprecated: please use {@link QueryTypeExecutor} class instead of this class, to execute this
	 * method. It is maintained to keep existing code compatible with the generated code. It will be removed in 2.0
	 * version.
	 */
	@Override
	@Deprecated
	public com.graphql_java_generator.client.request.Builder getResponseBuilder()
			throws GraphQLRequestPreparationException {
		return super.getResponseBuilder();
	}

	/**
	 * This method is deprecated: please use {@link QueryTypeExecutor} class instead of this class, to execute this
	 * method. It is maintained to keep existing code compatible with the generated code. It will be removed in 2.0
	 * version.
	 */
	@Override
	@Deprecated
	public GraphQLRequest getGraphQLRequest(String fullRequest) throws GraphQLRequestPreparationException {
		return super.getGraphQLRequest(fullRequest);
	}

	/**
	 * This method is deprecated: please use {@link QueryTypeExecutor} class instead of this class, to execute this
	 * method. It is maintained to keep existing code compatible with the generated code. It will be removed in 2.0
	 * version.
	 */
	@Override
	@Deprecated
	@GraphQLNonScalar(fieldName = "boards", graphQLTypeSimpleName = "Board", javaClass = Board.class)
	public List<com.graphql_java_generator.client.domain.forum.Board> boardsWithBindValues(String queryResponseDef,
			Map<String, Object> parameters)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		return super.boardsWithBindValues(queryResponseDef, parameters);
	}

	/**
	 * This method is deprecated: please use {@link QueryTypeExecutor} class instead of this class, to execute this
	 * method. It is maintained to keep existing code compatible with the generated code. It will be removed in 2.0
	 * version.
	 */
	@Override
	@Deprecated
	@GraphQLNonScalar(fieldName = "boards", graphQLTypeSimpleName = "Board", javaClass = Board.class)
	public List<com.graphql_java_generator.client.domain.forum.Board> boards(String queryResponseDef,
			Object... paramsAndValues) throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		return super.boards(queryResponseDef, paramsAndValues);
	}

	/**
	 * This method is deprecated: please use {@link QueryTypeExecutor} class instead of this class, to execute this
	 * method. It is maintained to keep existing code compatible with the generated code. It will be removed in 2.0
	 * version.
	 */
	@Override
	@Deprecated
	@GraphQLNonScalar(fieldName = "boards", graphQLTypeSimpleName = "Board", javaClass = Board.class)
	public List<com.graphql_java_generator.client.domain.forum.Board> boardsWithBindValues(
			ObjectResponse objectResponse, Map<String, Object> parameters) throws GraphQLRequestExecutionException {
		return super.boardsWithBindValues(objectResponse, parameters);
	}

	/**
	 * This method is deprecated: please use {@link QueryTypeExecutor} class instead of this class, to execute this
	 * method. It is maintained to keep existing code compatible with the generated code. It will be removed in 2.0
	 * version.
	 */
	@Override
	@Deprecated
	@GraphQLNonScalar(fieldName = "boards", graphQLTypeSimpleName = "Board", javaClass = Board.class)
	public List<com.graphql_java_generator.client.domain.forum.Board> boards(ObjectResponse objectResponse,
			Object... paramsAndValues) throws GraphQLRequestExecutionException {
		return super.boards(objectResponse, paramsAndValues);
	}

	/**
	 * This method is deprecated: please use {@link QueryTypeExecutor} class instead of this class, to execute this
	 * method. It is maintained to keep existing code compatible with the generated code. It will be removed in 2.0
	 * version.
	 */
	@Override
	@Deprecated
	public com.graphql_java_generator.client.request.Builder getBoardsResponseBuilder()
			throws GraphQLRequestPreparationException {
		return super.getBoardsResponseBuilder();
	}

	/**
	 * This method is deprecated: please use {@link QueryTypeExecutor} class instead of this class, to execute this
	 * method. It is maintained to keep existing code compatible with the generated code. It will be removed in 2.0
	 * version.
	 */
	@Override
	@Deprecated
	public GraphQLRequest getBoardsGraphQLRequest(String partialRequest) throws GraphQLRequestPreparationException {
		return super.getBoardsGraphQLRequest(partialRequest);
	}

	/**
	 * This method is deprecated: please use {@link QueryTypeExecutor} class instead of this class, to execute this
	 * method. It is maintained to keep existing code compatible with the generated code. It will be removed in 2.0
	 * version.
	 */
	@Override
	@Deprecated
	@GraphQLScalar(fieldName = "nbBoards", graphQLTypeSimpleName = "Int", javaClass = Integer.class)
	public java.lang.Integer nbBoardsWithBindValues(String queryResponseDef, Map<String, Object> parameters)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		return super.nbBoardsWithBindValues(queryResponseDef, parameters);
	}

	/**
	 * This method is deprecated: please use {@link QueryTypeExecutor} class instead of this class, to execute this
	 * method. It is maintained to keep existing code compatible with the generated code. It will be removed in 2.0
	 * version.
	 */
	@Override
	@Deprecated
	@GraphQLScalar(fieldName = "nbBoards", graphQLTypeSimpleName = "Int", javaClass = Integer.class)
	public java.lang.Integer nbBoards(String queryResponseDef, Object... paramsAndValues)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		return super.nbBoards(queryResponseDef, paramsAndValues);
	}

	/**
	 * This method is deprecated: please use {@link QueryTypeExecutor} class instead of this class, to execute this
	 * method. It is maintained to keep existing code compatible with the generated code. It will be removed in 2.0
	 * version.
	 */
	@Override
	@Deprecated
	@GraphQLScalar(fieldName = "nbBoards", graphQLTypeSimpleName = "Int", javaClass = Integer.class)
	public java.lang.Integer nbBoardsWithBindValues(ObjectResponse objectResponse, Map<String, Object> parameters)
			throws GraphQLRequestExecutionException {
		return super.nbBoardsWithBindValues(objectResponse, parameters);
	}

	/**
	 * This method is deprecated: please use {@link QueryTypeExecutor} class instead of this class, to execute this
	 * method. It is maintained to keep existing code compatible with the generated code. It will be removed in 2.0
	 * version.
	 */
	@Override
	@Deprecated
	@GraphQLScalar(fieldName = "nbBoards", graphQLTypeSimpleName = "Int", javaClass = Integer.class)
	public java.lang.Integer nbBoards(ObjectResponse objectResponse, Object... paramsAndValues)
			throws GraphQLRequestExecutionException {
		return super.nbBoards(objectResponse, paramsAndValues);
	}

	/**
	 * This method is deprecated: please use {@link QueryTypeExecutor} class instead of this class, to execute this
	 * method. It is maintained to keep existing code compatible with the generated code. It will be removed in 2.0
	 * version.
	 */
	@Override
	@Deprecated
	public com.graphql_java_generator.client.request.Builder getNbBoardsResponseBuilder()
			throws GraphQLRequestPreparationException {
		return super.getNbBoardsResponseBuilder();
	}

	/**
	 * This method is deprecated: please use {@link QueryTypeExecutor} class instead of this class, to execute this
	 * method. It is maintained to keep existing code compatible with the generated code. It will be removed in 2.0
	 * version.
	 */
	@Override
	@Deprecated
	public GraphQLRequest getNbBoardsGraphQLRequest(String partialRequest) throws GraphQLRequestPreparationException {
		return super.getNbBoardsGraphQLRequest(partialRequest);
	}

	/**
	 * This method is deprecated: please use {@link QueryTypeExecutor} class instead of this class, to execute this
	 * method. It is maintained to keep existing code compatible with the generated code. It will be removed in 2.0
	 * version.
	 */
	@Override
	@Deprecated
	@GraphQLNonScalar(fieldName = "topics", graphQLTypeSimpleName = "Topic", javaClass = Topic.class)
	public List<com.graphql_java_generator.client.domain.forum.Topic> topicsWithBindValues(String queryResponseDef,
			String boardName, Map<String, Object> parameters)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		return super.topicsWithBindValues(queryResponseDef, boardName, parameters);
	}

	/**
	 * This method is deprecated: please use {@link QueryTypeExecutor} class instead of this class, to execute this
	 * method. It is maintained to keep existing code compatible with the generated code. It will be removed in 2.0
	 * version.
	 */
	@Override
	@Deprecated
	@GraphQLNonScalar(fieldName = "topics", graphQLTypeSimpleName = "Topic", javaClass = Topic.class)
	public List<com.graphql_java_generator.client.domain.forum.Topic> topics(String queryResponseDef, String boardName,
			Object... paramsAndValues) throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		return super.topics(queryResponseDef, boardName, paramsAndValues);
	}

	/**
	 * This method is deprecated: please use {@link QueryTypeExecutor} class instead of this class, to execute this
	 * method. It is maintained to keep existing code compatible with the generated code. It will be removed in 2.0
	 * version.
	 */
	@Override
	@Deprecated
	@GraphQLNonScalar(fieldName = "topics", graphQLTypeSimpleName = "Topic", javaClass = Topic.class)
	public List<com.graphql_java_generator.client.domain.forum.Topic> topicsWithBindValues(
			ObjectResponse objectResponse, String boardName, Map<String, Object> parameters)
			throws GraphQLRequestExecutionException {
		return super.topicsWithBindValues(objectResponse, boardName, parameters);
	}

	/**
	 * This method is deprecated: please use {@link QueryTypeExecutor} class instead of this class, to execute this
	 * method. It is maintained to keep existing code compatible with the generated code. It will be removed in 2.0
	 * version.
	 */
	@Override
	@Deprecated
	@GraphQLNonScalar(fieldName = "topics", graphQLTypeSimpleName = "Topic", javaClass = Topic.class)
	public List<com.graphql_java_generator.client.domain.forum.Topic> topics(ObjectResponse objectResponse,
			String boardName, Object... paramsAndValues) throws GraphQLRequestExecutionException {
		return super.topics(objectResponse, boardName, paramsAndValues);
	}

	/**
	 * This method is deprecated: please use {@link QueryTypeExecutor} class instead of this class, to execute this
	 * method. It is maintained to keep existing code compatible with the generated code. It will be removed in 2.0
	 * version.
	 */
	@Override
	@Deprecated
	public com.graphql_java_generator.client.request.Builder getTopicsResponseBuilder()
			throws GraphQLRequestPreparationException {
		return super.getTopicsResponseBuilder();
	}

	/**
	 * This method is deprecated: please use {@link QueryTypeExecutor} class instead of this class, to execute this
	 * method. It is maintained to keep existing code compatible with the generated code. It will be removed in 2.0
	 * version.
	 */
	@Override
	@Deprecated
	public GraphQLRequest getTopicsGraphQLRequest(String partialRequest) throws GraphQLRequestPreparationException {
		return super.getTopicsGraphQLRequest(partialRequest);
	}

	/**
	 * This method is deprecated: please use {@link QueryTypeExecutor} class instead of this class, to execute this
	 * method. It is maintained to keep existing code compatible with the generated code. It will be removed in 2.0
	 * version.
	 */
	@Override
	@Deprecated
	@GraphQLNonScalar(fieldName = "findTopics", graphQLTypeSimpleName = "Topic", javaClass = Topic.class)
	public List<com.graphql_java_generator.client.domain.forum.Topic> findTopicsWithBindValues(String queryResponseDef,
			String boardName, List<String> keyword, Map<String, Object> parameters)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		return super.findTopicsWithBindValues(queryResponseDef, boardName, keyword, parameters);
	}

	/**
	 * This method is deprecated: please use {@link QueryTypeExecutor} class instead of this class, to execute this
	 * method. It is maintained to keep existing code compatible with the generated code. It will be removed in 2.0
	 * version.
	 */
	@Override
	@Deprecated
	@GraphQLNonScalar(fieldName = "findTopics", graphQLTypeSimpleName = "Topic", javaClass = Topic.class)
	public List<com.graphql_java_generator.client.domain.forum.Topic> findTopics(String queryResponseDef,
			String boardName, List<String> keyword, Object... paramsAndValues)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		return super.findTopics(queryResponseDef, boardName, keyword, paramsAndValues);
	}

	/**
	 * This method is deprecated: please use {@link QueryTypeExecutor} class instead of this class, to execute this
	 * method. It is maintained to keep existing code compatible with the generated code. It will be removed in 2.0
	 * version.
	 */
	@Override
	@Deprecated
	@GraphQLNonScalar(fieldName = "findTopics", graphQLTypeSimpleName = "Topic", javaClass = Topic.class)
	public List<com.graphql_java_generator.client.domain.forum.Topic> findTopicsWithBindValues(
			ObjectResponse objectResponse, String boardName, List<String> keyword, Map<String, Object> parameters)
			throws GraphQLRequestExecutionException {
		return super.findTopicsWithBindValues(objectResponse, boardName, keyword, parameters);
	}

	/**
	 * This method is deprecated: please use {@link QueryTypeExecutor} class instead of this class, to execute this
	 * method. It is maintained to keep existing code compatible with the generated code. It will be removed in 2.0
	 * version.
	 */
	@Override
	@Deprecated
	@GraphQLNonScalar(fieldName = "findTopics", graphQLTypeSimpleName = "Topic", javaClass = Topic.class)
	public List<com.graphql_java_generator.client.domain.forum.Topic> findTopics(ObjectResponse objectResponse,
			String boardName, List<String> keyword, Object... paramsAndValues) throws GraphQLRequestExecutionException {
		return super.findTopics(objectResponse, boardName, keyword, paramsAndValues);
	}

	/**
	 * This method is deprecated: please use {@link QueryTypeExecutor} class instead of this class, to execute this
	 * method. It is maintained to keep existing code compatible with the generated code. It will be removed in 2.0
	 * version.
	 */
	@Override
	@Deprecated
	public com.graphql_java_generator.client.request.Builder getFindTopicsResponseBuilder()
			throws GraphQLRequestPreparationException {
		return super.getFindTopicsResponseBuilder();
	}

	/**
	 * This method is deprecated: please use {@link QueryTypeExecutor} class instead of this class, to execute this
	 * method. It is maintained to keep existing code compatible with the generated code. It will be removed in 2.0
	 * version.
	 */
	@Override
	@Deprecated
	public GraphQLRequest getFindTopicsGraphQLRequest(String partialRequest) throws GraphQLRequestPreparationException {
		return super.getFindTopicsGraphQLRequest(partialRequest);
	}

	/**
	 * This method is deprecated: please use {@link QueryTypeExecutor} class instead of this class, to execute this
	 * method. It is maintained to keep existing code compatible with the generated code. It will be removed in 2.0
	 * version.
	 */
	@Override
	@Deprecated
	@GraphQLNonScalar(fieldName = "__schema", graphQLTypeSimpleName = "__Schema", javaClass = __Schema.class)
	public com.graphql_java_generator.client.domain.forum.__Schema __schemaWithBindValues(String queryResponseDef,
			Map<String, Object> parameters)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		return super.__schemaWithBindValues(queryResponseDef, parameters);
	}

	/**
	 * This method is deprecated: please use {@link QueryTypeExecutor} class instead of this class, to execute this
	 * method. It is maintained to keep existing code compatible with the generated code. It will be removed in 2.0
	 * version.
	 */
	@Override
	@Deprecated
	@GraphQLNonScalar(fieldName = "__schema", graphQLTypeSimpleName = "__Schema", javaClass = __Schema.class)
	public com.graphql_java_generator.client.domain.forum.__Schema __schema(String queryResponseDef,
			Object... paramsAndValues) throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		return super.__schema(queryResponseDef, paramsAndValues);
	}

	/**
	 * This method is deprecated: please use {@link QueryTypeExecutor} class instead of this class, to execute this
	 * method. It is maintained to keep existing code compatible with the generated code. It will be removed in 2.0
	 * version.
	 */
	@Override
	@Deprecated
	@GraphQLNonScalar(fieldName = "__schema", graphQLTypeSimpleName = "__Schema", javaClass = __Schema.class)
	public com.graphql_java_generator.client.domain.forum.__Schema __schemaWithBindValues(ObjectResponse objectResponse,
			Map<String, Object> parameters) throws GraphQLRequestExecutionException {
		return super.__schemaWithBindValues(objectResponse, parameters);
	}

	/**
	 * This method is deprecated: please use {@link QueryTypeExecutor} class instead of this class, to execute this
	 * method. It is maintained to keep existing code compatible with the generated code. It will be removed in 2.0
	 * version.
	 */
	@Override
	@Deprecated
	@GraphQLNonScalar(fieldName = "__schema", graphQLTypeSimpleName = "__Schema", javaClass = __Schema.class)
	public com.graphql_java_generator.client.domain.forum.__Schema __schema(ObjectResponse objectResponse,
			Object... paramsAndValues) throws GraphQLRequestExecutionException {
		return super.__schema(objectResponse, paramsAndValues);
	}

	/**
	 * This method is deprecated: please use {@link QueryTypeExecutor} class instead of this class, to execute this
	 * method. It is maintained to keep existing code compatible with the generated code. It will be removed in 2.0
	 * version.
	 */
	@Override
	@Deprecated
	public com.graphql_java_generator.client.request.Builder get__schemaResponseBuilder()
			throws GraphQLRequestPreparationException {
		return super.get__schemaResponseBuilder();
	}

	/**
	 * This method is deprecated: please use {@link QueryTypeExecutor} class instead of this class, to execute this
	 * method. It is maintained to keep existing code compatible with the generated code. It will be removed in 2.0
	 * version.
	 */
	@Override
	@Deprecated
	public GraphQLRequest get__schemaGraphQLRequest(String partialRequest) throws GraphQLRequestPreparationException {
		return super.get__schemaGraphQLRequest(partialRequest);
	}

	/**
	 * This method is deprecated: please use {@link QueryTypeExecutor} class instead of this class, to execute this
	 * method. It is maintained to keep existing code compatible with the generated code. It will be removed in 2.0
	 * version.
	 */
	@Override
	@Deprecated
	@GraphQLNonScalar(fieldName = "__type", graphQLTypeSimpleName = "__Type", javaClass = __Type.class)
	public com.graphql_java_generator.client.domain.forum.__Type __typeWithBindValues(String queryResponseDef,
			String name, Map<String, Object> parameters)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		return super.__typeWithBindValues(queryResponseDef, name, parameters);
	}

	/**
	 * This method is deprecated: please use {@link QueryTypeExecutor} class instead of this class, to execute this
	 * method. It is maintained to keep existing code compatible with the generated code. It will be removed in 2.0
	 * version.
	 */
	@Override
	@Deprecated
	@GraphQLNonScalar(fieldName = "__type", graphQLTypeSimpleName = "__Type", javaClass = __Type.class)
	public com.graphql_java_generator.client.domain.forum.__Type __type(String queryResponseDef, String name,
			Object... paramsAndValues) throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		return super.__type(queryResponseDef, name, paramsAndValues);
	}

	/**
	 * This method is deprecated: please use {@link QueryTypeExecutor} class instead of this class, to execute this
	 * method. It is maintained to keep existing code compatible with the generated code. It will be removed in 2.0
	 * version.
	 */
	@Override
	@Deprecated
	@GraphQLNonScalar(fieldName = "__type", graphQLTypeSimpleName = "__Type", javaClass = __Type.class)
	public com.graphql_java_generator.client.domain.forum.__Type __typeWithBindValues(ObjectResponse objectResponse,
			String name, Map<String, Object> parameters) throws GraphQLRequestExecutionException {
		return super.__typeWithBindValues(objectResponse, name, parameters);
	}

	/**
	 * This method is deprecated: please use {@link QueryTypeExecutor} class instead of this class, to execute this
	 * method. It is maintained to keep existing code compatible with the generated code. It will be removed in 2.0
	 * version.
	 */
	@Override
	@Deprecated
	@GraphQLNonScalar(fieldName = "__type", graphQLTypeSimpleName = "__Type", javaClass = __Type.class)
	public com.graphql_java_generator.client.domain.forum.__Type __type(ObjectResponse objectResponse, String name,
			Object... paramsAndValues) throws GraphQLRequestExecutionException {
		return super.__type(objectResponse, name, paramsAndValues);
	}

	/**
	 * This method is deprecated: please use {@link QueryTypeExecutor} class instead of this class, to execute this
	 * method. It is maintained to keep existing code compatible with the generated code. It will be removed in 2.0
	 * version.
	 */
	@Override
	@Deprecated
	public com.graphql_java_generator.client.request.Builder get__typeResponseBuilder()
			throws GraphQLRequestPreparationException {
		return super.get__typeResponseBuilder();
	}

	/**
	 * This method is deprecated: please use {@link QueryTypeExecutor} class instead of this class, to execute this
	 * method. It is maintained to keep existing code compatible with the generated code. It will be removed in 2.0
	 * version.
	 */
	@Override
	@Deprecated
	public GraphQLRequest get__typeGraphQLRequest(String partialRequest) throws GraphQLRequestPreparationException {
		return super.get__typeGraphQLRequest(partialRequest);
	}

	/**
	 * This method is deprecated: please use {@link QueryTypeExecutor} class instead of this class, to execute this
	 * method. It is maintained to keep existing code compatible with the generated code. It will be removed in 2.0
	 * version.
	 */
	@Override
	@Deprecated
	@GraphQLScalar(fieldName = "__typename", graphQLTypeSimpleName = "String", javaClass = String.class)
	public java.lang.String __typenameWithBindValues(String queryResponseDef, Map<String, Object> parameters)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		return super.__typenameWithBindValues(queryResponseDef, parameters);
	}

	/**
	 * This method is deprecated: please use {@link QueryTypeExecutor} class instead of this class, to execute this
	 * method. It is maintained to keep existing code compatible with the generated code. It will be removed in 2.0
	 * version.
	 */
	@Override
	@Deprecated
	@GraphQLScalar(fieldName = "__typename", graphQLTypeSimpleName = "String", javaClass = String.class)
	public java.lang.String __typename(String queryResponseDef, Object... paramsAndValues)
			throws GraphQLRequestExecutionException, GraphQLRequestPreparationException {
		return super.__typename(queryResponseDef, paramsAndValues);
	}

	/**
	 * This method is deprecated: please use {@link QueryTypeExecutor} class instead of this class, to execute this
	 * method. It is maintained to keep existing code compatible with the generated code. It will be removed in 2.0
	 * version.
	 */
	@Override
	@Deprecated
	@GraphQLScalar(fieldName = "__typename", graphQLTypeSimpleName = "String", javaClass = String.class)
	public java.lang.String __typenameWithBindValues(ObjectResponse objectResponse, Map<String, Object> parameters)
			throws GraphQLRequestExecutionException {
		return super.__typenameWithBindValues(objectResponse, parameters);
	}

	/**
	 * This method is deprecated: please use {@link QueryTypeExecutor} class instead of this class, to execute this
	 * method. It is maintained to keep existing code compatible with the generated code. It will be removed in 2.0
	 * version.
	 */
	@Override
	@Deprecated
	@GraphQLScalar(fieldName = "__typename", graphQLTypeSimpleName = "String", javaClass = String.class)
	public java.lang.String __typename(ObjectResponse objectResponse, Object... paramsAndValues)
			throws GraphQLRequestExecutionException {
		return super.__typename(objectResponse, paramsAndValues);
	}

	/**
	 * This method is deprecated: please use {@link QueryTypeExecutor} class instead of this class, to execute this
	 * method. It is maintained to keep existing code compatible with the generated code. It will be removed in 2.0
	 * version.
	 */
	@Override
	@Deprecated
	public com.graphql_java_generator.client.request.Builder get__typenameResponseBuilder()
			throws GraphQLRequestPreparationException {
		return super.get__typenameResponseBuilder();
	}

	/**
	 * This method is deprecated: please use {@link QueryTypeExecutor} class instead of this class, to execute this
	 * method. It is maintained to keep existing code compatible with the generated code. It will be removed in 2.0
	 * version.
	 */
	@Override
	@Deprecated
	public GraphQLRequest get__typenameGraphQLRequest(String partialRequest) throws GraphQLRequestPreparationException {
		return super.get__typenameGraphQLRequest(partialRequest);
	}

}
