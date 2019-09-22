/**
 * 
 */
package com.graphql_java_generator.samples.server;

import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.opencsv.CSVReader;

/**
 * Thanks to Viral and his blog, here is a CSV data loader. In previous version, we used dbunits. But it doesn't seem to
 * be compatible with uuid column type.<BR/>
 * The code provided by Viral has been adaptated:
 * <UL>
 * <LI>Add of the path for the files, as a constructor argument</LI>
 * <LI>Table name and file name are the same (filename must be postfixed by ".csv"</LI>
 * <LI>This class expect the csv file to be provided on the given path of the classpath</LI>
 * <LI>Removed support for a specific separator</LI>
 * <LI>Add of log4j logging</LI>
 * </UL>
 * 
 * @author Viral Patel
 * @see <a href=
 *      "https://viralpatel.net/blogs/java-load-csv-file-to-database/">https://viralpatel.net/blogs/java-load-csv-file-to-database/</a>
 */
public class CSVLoader {
	private static final String SQL_INSERT = "INSERT INTO ${table}(${keys}) VALUES(${values})";
	private static final String TABLE_REGEX = "\\$\\{table\\}";
	private static final String KEYS_REGEX = "\\$\\{keys\\}";
	private static final String VALUES_REGEX = "\\$\\{values\\}";

	/** The logger for this class */
	Logger logger = LogManager.getLogger();

	final private Connection connection;

	final private String path;

	/**
	 * Public constructor to build CSVLoader object with Connection details. The connection is closed on success or
	 * failure.
	 * 
	 * @param connection
	 */
	public CSVLoader(Connection connection, String path) {
		this.connection = connection;
		this.path = path;
	}

	/**
	 * Parse CSV file using OpenCSV library and load in given database table.
	 * 
	 * @param tableName
	 *            Database table name to import data
	 * @param truncateBeforeLoad
	 *            Truncate the table before inserting new records.
	 * @throws Exception
	 */
	public void loadCSV(String tableName, boolean truncateBeforeLoad) throws Exception {
		String fullPath = null;
		CSVReader csvReader = null;
		PreparedStatement ps = null;
		int count = 0;

		logger.debug("Loading data into '{}'", tableName);

		try {
			fullPath = (path.startsWith("/") ? "" : "/") + path + (path.endsWith("/") ? "" : "/") + tableName + ".csv";
			logger.debug("Before loading data into '{}' from '{}'", tableName, fullPath);
			Resource csvURL = new ClassPathResource(fullPath);
			csvReader = new CSVReader(new InputStreamReader(csvURL.getInputStream()));

			String[] headerRow = csvReader.readNext();

			if (null == headerRow) {
				throw new FileNotFoundException(
						"No columns defined in given CSV file." + "Please check the CSV file format.");
			}

			String questionmarks = StringUtils.repeat("?,", headerRow.length);
			questionmarks = (String) questionmarks.subSequence(0, questionmarks.length() - 1);

			String query = SQL_INSERT.replaceFirst(TABLE_REGEX, tableName);
			query = query.replaceFirst(KEYS_REGEX, StringUtils.join(headerRow, ","));
			query = query.replaceFirst(VALUES_REGEX, questionmarks);

			logger.debug("Query: " + query);

			String[] nextLine;
			ps = null;

			connection.setAutoCommit(false);
			ps = connection.prepareStatement(query);

			if (truncateBeforeLoad) {
				// delete data from table before loading csv
				connection.createStatement().execute("DELETE FROM " + tableName);
			}

			final int batchSize = 1000;
			// Date date = null;
			while ((nextLine = csvReader.readNext()) != null) {

				if (null != nextLine) {
					int index = 1;
					for (String string : nextLine) {
						// date = DateUtil.convertToDate(string);
						// if (null != date) {
						// ps.setDate(index++, new java.sql.Date(date.getTime()));
						// } else {
						ps.setString(index++, string);
						// }
					}
					ps.addBatch();
				}
				if (++count % batchSize == 0) {
					ps.executeBatch();
				}
			}
			ps.executeBatch(); // insert remaining records
			connection.commit();
		} catch (Exception e) {
			connection.rollback();
			String msg = "Error occured while reading file '" + fullPath + "' (" + e.getMessage() + ")";
			logger.error(msg);
			throw new Exception(msg, e);

		} finally {
			if (ps != null)
				ps.close();
			if (csvReader != null)
				csvReader.close();
		}

		logger.info(count + " lines inserted in the " + tableName + " table");
	}

}
