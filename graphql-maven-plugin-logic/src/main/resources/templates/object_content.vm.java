##
## When in client mode, we add the capability to receive unknown JSON attributes, which includes returned values for GraphQL aliases
##
#if(${configuration.mode}=="client")

	/**
	 * This map contains the deserialized values for the alias, as parsed from the json response from the GraphQL
	 * server. The key is the alias name, the value is the deserialiazed value (taking into account custom scalars,
	 * lists, ...)
	 */
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
	 * This method is called during the json deserialization process, by the {@link GraphQLObjectMapper}, each time an
	 * alias value is read from the json.
	 * 
	 * @param aliasName
	 * @param aliasDeserializedValue
	 */
	public void setAliasValue(String aliasName, Object aliasDeserializedValue) {
		aliasValues.put(aliasName, aliasDeserializedValue);
	}

	/**
	 * Retrieves the value for the given alias, as it has been received for this object in the GraphQL response. <BR/>
	 * This method <B>should not be used for Custom Scalars</B>, as the parser doesn't know if this alias is a custom
	 * scalar, and which custom scalar to use at deserialization time. In most case, a value will then be provided by
	 * this method with a basis json deserialization, but this value won't be the proper custom scalar value.
	 * 
	 * @param alias
	 * @return
	 */
	public Object getAliasValue(String alias) {
		return aliasValues.get(alias);
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
