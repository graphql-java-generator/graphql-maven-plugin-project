/**
 * 
 */
package com.graphql_java_generator.samples.server;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.DriverManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

/**
 * Various tools, specific to this project
 * 
 * @author EtienneSF
 */
@Component
public class DatabaseInitializer {

	/** The logger for this class */
	Logger logger = LogManager.getLogger();

	final String FILE_PREFIX = "StarWars-server-data";

	String url = "jdbc:h2:mem:testdb";
	String driverClassName = "org.h2.Driver";
	String username = "sa";
	String password = "";

	/**
	 * This method waits for the application to be ready. Then it fills the database
	 * with sample data
	 * 
	 * @throws Exception
	 */
	@EventListener(ApplicationReadyEvent.class)
	public void initDatabase() throws Exception {
		Connection connection = null;
		try {
			Class.forName("org.h2.Driver");
			connection = DriverManager.getConnection("jdbc:h2:mem:testdb", "sa", "");
			connection.setAutoCommit(true);

			// Let's load our data
			CSVLoader loader = new CSVLoader(connection, "data");
			loader.loadCSV("droid", false);
			loader.loadCSV("human", false);
			loader.loadCSV("episode", false);
			loader.loadCSV("droid_appears_in", false);
			loader.loadCSV("human_appears_in", false);
			loader.loadCSV("character_friends", false);

		} catch (Exception e) {
			// An error occured. We logged, but don't block the start of the server : very
			// often, the issue is a gap
			// with the database model. And we need the in-memory database to be started, to
			// check that.
			Throwable e2 = e;
			while (e2 != null) {
				logger.error(e2.getMessage());
				e2 = e2.getCause();
			}
		} finally {
			if (connection != null) {
				connection.close();
			}
		}
	}

	/**
	 * Writes to a temporary file, the content of the file located to the given path
	 * on the classpath
	 * 
	 * @param path
	 * @param prefix
	 * @return
	 * @throws IOException
	 */
	File createTempFileForCSV(Path folder, String filename) throws IOException {
		File targetFile = new File(folder.toFile(), filename);
		InputStream inStream = new ClassPathResource("data/" + filename).getInputStream();
		Files.copy(inStream, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
		return targetFile;
	}
}
