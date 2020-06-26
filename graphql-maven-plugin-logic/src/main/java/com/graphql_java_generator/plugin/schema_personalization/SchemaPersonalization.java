/**
 * 
 */
package com.graphql_java_generator.plugin.schema_personalization;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * This class contains the data in java form that the user has put in his/her schema configuration file.<BR/>
 * It is not public, as only {@link GraphQLJsonSchemaPersonalization} may use it.
 * 
 * @See {@link GraphQLJsonSchemaPersonalization}
 * @author etienne-sf
 */
@Data
class SchemaPersonalization {

	List<EntityPersonalization> entityPersonalizations = new ArrayList<>();

}
