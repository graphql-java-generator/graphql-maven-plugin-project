/**
 * 
 */
package org.graphql.maven.plugin.samples.server.jpa;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dbunit.IDatabaseTester;
import org.dbunit.JdbcDatabaseTester;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.csv.CsvDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

/**
 * Various tools, specific to this project
 * 
 * @author EtienneSF
 */
@Component
public class DatabaseTools {

	Logger logger = LogManager.getLogger();

	final String FILE_PREFIX = "GraphQLServer";

	String url = "jdbc:h2:mem:testdb";
	String driverClassName = "org.h2.Driver";
	String username = "sa";
	String password = "";

	/**
	 * • Initialisation de la base de données : vérification qu’il n’y a pas de build concurrent, vidage de la base,
	 * recréation des tables et fonctions, remplissages des tables...
	 * 
	 * @throws SQLException
	 * 
	 * @throws Exception
	 */
	public void initDatabase() {
		try {
			IDatabaseTester databaseTester = new JdbcDatabaseTester(driverClassName, url, username, password);
			IDatabaseConnection iConn = databaseTester.getConnection();

			// Important: auto-commit is necessary to keep the dbsetup change in the database
			boolean autoCommitBefore = databaseTester.getConnection().getConnection().getAutoCommit();
			iConn.getConnection().setAutoCommit(true);
			iConn.getConfig().setProperty(DatabaseConfig.FEATURE_ALLOW_EMPTY_FIELDS, Boolean.TRUE);

			// Let's load our data
			// URL url = new ClassPathResource("starwars_data").getURL();
			// databaseTester.setDataSet(new CsvURLDataSet(url));
			Path dir = Files.createTempDirectory(FILE_PREFIX);
			createTempFileForCSV(dir, "droid.csv");
			createTempFileForCSV(dir, "human.csv");
			createTempFileForCSV(dir, "episode.csv");
			createTempFileForCSV(dir, "human_friends.csv");
			createTempFileForCSV(dir, "media.csv");
			createTempFileForCSV(dir, "table-ordering.txt");
			// IDataSet dataSets[] = { new CsvDataSet(createTempFileForCSV(dir, "data/droid.csv", "droid")),
			// new CsvDataSet(createTempFileForCSV(dir, "data/human.csv", "human")), };
			// databaseTester.setDataSet(new CsvDataSet(dir.toFile()));

			// will call default setUpOperation
			// databaseTester.onSetup();
			DatabaseOperation.CLEAN_INSERT.execute(iConn, new CsvDataSet(dir.toFile()));

			// and we restore the autoCommit status before leaving
			databaseTester.getConnection().getConnection().setAutoCommit(autoCommitBefore);
		} catch (Exception e) {
			// An error occured. We logged, but don't block the start of the server : very often, the issue is a gap
			// with the database model. And we need the in-memory database to be started, to check that.
			Throwable e2 = e;
			while (e2 != null) {
				logger.error(e2.getMessage());
				e2 = e2.getCause();
			}
		}
	}

	/**
	 * Writes to a temporary file, the content of the file located to the given path on the classpath
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
