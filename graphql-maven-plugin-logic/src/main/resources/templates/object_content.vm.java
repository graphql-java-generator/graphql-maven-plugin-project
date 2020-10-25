
	public ${targetFileName}(){
		// No action
	}

#foreach ($field in $object.fields)
	${field.annotation}
	${field.javaType} ${field.javaName};


#end

#foreach ($field in $object.fields)
	public void set${field.pascalCaseName}(${field.javaType} ${field.javaName}) {
		this.${field.javaName} = ${field.javaName};
	}

	public ${field.javaType} get${field.pascalCaseName}() {
		return ${field.javaName};
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
