package graphql.java.client;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface NonScalar {

	public Class<?> graphqlType();

}
