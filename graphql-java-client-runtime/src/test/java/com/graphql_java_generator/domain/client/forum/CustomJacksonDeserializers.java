/** Generated by the default template from graphql-java-generator */
package com.graphql_java_generator.domain.client.forum;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.graphql_java_generator.client.response.AbstractCustomJacksonDeserializer;
import com.graphql_java_generator.customscalars.GraphQLScalarTypeDate;

import graphql.schema.GraphQLScalarType;

/**
 * This class is a standard Deserializer for Jackson. It uses the {@link GraphQLScalarType} that is implemented by the project for this scalar
 */
@SuppressWarnings("unused")
public class CustomJacksonDeserializers {
	
	public static class ListTopic extends AbstractCustomJacksonDeserializer<List<Topic>> {
		private static final long serialVersionUID = 1L;
		public ListTopic() {
			super(
				null,
true,
				Topic.class,
				null
			);
		}
	}

	public static class List__EnumValue extends AbstractCustomJacksonDeserializer<List<__EnumValue>> {
		private static final long serialVersionUID = 1L;
		public List__EnumValue() {
			super(
				null,
true,
				__EnumValue.class,
				null
			);
		}
	}

	public static class List__Directive extends AbstractCustomJacksonDeserializer<List<__Directive>> {
		private static final long serialVersionUID = 1L;
		public List__Directive() {
			super(
				null,
true,
				__Directive.class,
				null
			);
		}
	}

	public static class List__DirectiveLocation extends AbstractCustomJacksonDeserializer<List<__DirectiveLocation>> {
		private static final long serialVersionUID = 1L;
		public List__DirectiveLocation() {
			super(
				null,
true,
				__DirectiveLocation.class,
				null
			);
		}
	}

	public static class List__Type extends AbstractCustomJacksonDeserializer<List<__Type>> {
		private static final long serialVersionUID = 1L;
		public List__Type() {
			super(
				null,
true,
				__Type.class,
				null
			);
		}
	}

	public static class List__InputValue extends AbstractCustomJacksonDeserializer<List<__InputValue>> {
		private static final long serialVersionUID = 1L;
		public List__InputValue() {
			super(
				null,
true,
				__InputValue.class,
				null
			);
		}
	}

	public static class ListBoard extends AbstractCustomJacksonDeserializer<List<Board>> {
		private static final long serialVersionUID = 1L;
		public ListBoard() {
			super(
				null,
true,
				Board.class,
				null
			);
		}
	}

	public static class ListPost extends AbstractCustomJacksonDeserializer<List<Post>> {
		private static final long serialVersionUID = 1L;
		public ListPost() {
			super(
				null,
true,
				Post.class,
				null
			);
		}
	}

	public static class Date extends AbstractCustomJacksonDeserializer<java.util.Date> {
		private static final long serialVersionUID = 1L;
		public Date() {
			super(
				null,
 false,
				java.util.Date.class,
  				com.graphql_java_generator.customscalars.GraphQLScalarTypeDate.Date
			);
		}
	}

	public static class List__Field extends AbstractCustomJacksonDeserializer<List<__Field>> {
		private static final long serialVersionUID = 1L;
		public List__Field() {
			super(
				null,
true,
				__Field.class,
				null
			);
		}
	}

}
