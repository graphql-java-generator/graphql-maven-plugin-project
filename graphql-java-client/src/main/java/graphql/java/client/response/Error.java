package graphql.java.client.response;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * 
 * The error POJO, mapped from the GraphQL server response, when an error occurs
 * 
 * @author EtienneSF
 */
public class Error {

	public String message;

	@JsonDeserialize(contentAs = Location.class)
	public List<Location> locations;

	public String description;

	public String validationErrorType;

	@JsonDeserialize(contentAs = String.class)
	public List<String> queryPath;

	public String errorType;

	public String path;

	@JsonDeserialize(contentAs = Extension.class)
	public List<Extension> extensions;

	/**
	 * Logs this error to the given {@link Logger}
	 * 
	 * @param logger
	 */
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();

		if (validationErrorType != null) {
			sb.append("[{").append(validationErrorType).append("}]");
		}
		if (errorType != null) {
			sb.append("{").append(errorType).append("}");
		}

		sb.append("{").append(message).append("}");

		if (queryPath != null) {
			sb.append(" path: {").append(queryPath.stream().collect(Collectors.joining(","))).append("}");
		}
		if (locations != null) {
			sb.append(" - locations: {")
					.append(locations.stream().map(Object::toString).collect(Collectors.joining(","))).append("}");
		}
		if (description != null) {
			sb.append(" - ({").append(description).append("})");
		}

		return sb.toString();
	}

}
