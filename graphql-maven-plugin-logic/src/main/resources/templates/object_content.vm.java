import java.util.HashMap;
import java.util.Map;

##
## When in client mode, we add the capability to receive unknown JSON attributes, which includes returned values for GraphQL aliases
##
#if(${configuration.mode}=="client")

	@com.graphql_java_generator.annotation.GraphQLIgnore
	Map<String, TreeNode> aliasTreeNodes = new HashMap<>();

	@com.graphql_java_generator.annotation.GraphQLIgnore
	Map<String, Object> aliasValues = new HashMap<>();
#end
##

	public ${targetFileName}(){
		// No action
	}

#foreach ($field in $object.fields)
#if ($field.comments.size() > 0)
	/**
#end	
#foreach ($comment in $field.comments)
	 * $comment
#end
#if ($field.comments.size() > 0)
	 */
#end
	${field.annotation}
	${field.javaType} ${field.javaName};


#end

#foreach ($field in $object.fields)
#if ($field.comments.size() > 0)
	/**
#end	
#foreach ($comment in $field.comments)
	 * $comment
#end
#if ($field.comments.size() > 0)
	 */
#end
	public void set${field.pascalCaseName}(${field.javaType} ${field.javaName}) {
		this.${field.javaName} = ${field.javaName};
	}

#if ($field.comments.size() > 0)
	/**
#end	
#foreach ($comment in $field.comments)
	 * $comment
#end
#if ($field.comments.size() > 0)
	 */
#end
	public ${field.javaType} get${field.pascalCaseName}() {
		return ${field.javaName};
	}

#end
##
## When in client mode, we add the capability to receive unknown JSON attributes, which includes returned values for GraphQL aliases
##
#if(${configuration.mode}=="client")

	/**
	 * The setter from an alias value. This method is called by the Jackson DeserializationProblemHandler that has been
	 * configured on the Jackson ObjectMapper, while deserialization, for each unknown GraphQL property.
	 * 
	 * @param key
	 *            The key read in the incoming JSON response
	 * @param value
	 *            The relevant value
	 */
	public void setAliasValue(String key, TreeNode value) {
		aliasTreeNodes.put(key, value);
	}
	
	/**
	 * Retrieves the value for the given alias, and map it into POJO(s), according to the given classes list
	 * 
	 * @param alias
	 *            The alias name, which value is to be returned
	 * @param clazz
	 *            The POJO class that maps the GraphQL type, whether or not it's in an array. For instance String.class
	 *            for String, [String], [[String]]...
	 * @return The parsed value. That is, according to the above sample: a String, a List<String> or a
	 *         List<List<String>>
	 * @throws GraphQLRequestExecutionException
	 *             When an error occurs during deserialization
	 */
	public Object getAliasValue(String alias, Class<?> clazz) throws GraphQLRequestExecutionException {
		Object ret = aliasValues.get(alias);
		if (ret != null) {
			// The result has already been computed. Let's return it.
			return ret;
		} else {
			// The result has either not been computed or is null (in which case, its parsing will be quick)
			ret = GraphqlUtils.graphqlUtils.getAliasValue(aliasTreeNodes.get(alias), clazz);
			// Let's store the result, so we won't have to compute it again
			aliasValues.put(alias, ret);
			return ret;
		}
	}

#end
    public String toString() {
        return "${object.javaName} {"
#foreach ($field in $object.fields)
				+ "${field.javaName}: " + ${field.javaName}
#if($foreach.hasNext)
				+ ", "
#end 
#end
        		+ "}";
    }

    /**
	 * Enum of field names
	 */
	 public static enum Field implements GraphQLField {
#foreach ($field in $object.fields)
		${field.pascalCaseName}("${field.name}")#if($foreach.hasNext),
#end
#end;

		private String fieldName;

		Field(String fieldName) {
			this.fieldName = fieldName;
		}

		public String getFieldName() {
			return fieldName;
		}

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
#foreach ($field in $object.fields)
#if(${field.javaName} != '__typename')
		private ${field.javaType} ${field.javaName};
#end
#end


#foreach ($field in $object.fields)
#if(${field.javaName} != '__typename')
		public Builder with${field.pascalCaseName}(${field.javaType} ${field.javaName}) {
			this.${field.javaName} = ${field.javaName};
			return this;
		}
#end
#end

		public ${targetFileName} build() {
			${targetFileName} _object = new ${targetFileName}();
#foreach ($field in $object.fields)
#if(${field.javaName} == '__typename')
			_object.set__typename("${object.javaName}");
#else
			_object.set${field.pascalCaseName}(${field.javaName});
#end
#end
			return _object;
		}
	}
