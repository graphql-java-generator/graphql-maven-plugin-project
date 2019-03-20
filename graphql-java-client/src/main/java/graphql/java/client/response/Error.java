package graphql.java.client.response;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;

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
	public void logError(Logger logger, Marker marker) {
		String queryPathStr = queryPath.stream().collect(Collectors.joining(","));
		logger.error(marker, "[{}] {}: {}, path: {} ({})", validationErrorType, errorType, message, queryPathStr,
				description);
	}

}
