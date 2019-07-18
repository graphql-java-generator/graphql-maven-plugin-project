/**
 * 
 */
package com.graphql_java_generator.plugin.test.helper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Simulate a
 * 
 * @author Etienne
 */
public class Log4jLogger implements com.graphql_java_generator.plugin.Logger {

	/** The logger, to which all log calls will be delegated */
	Logger logger = null;

	/**
	 * 
	 * @param caller
	 *            Used to retrieve the appropriate Log4j logger
	 */
	public Log4jLogger(Object caller) {
		logger = LogManager.getLogger(caller.getClass());
	}

	@Override
	public void debug(String arg0) {
		logger.debug(arg0);
	}

	@Override
	public void debug(String arg0, Throwable arg1) {
		logger.debug(arg0);
	}

	@Override
	public void error(String arg0) {
		logger.error(arg0);
	}

	@Override
	public void error(String arg0, Throwable arg1) {
		logger.error(arg0, arg1);
	}

	@Override
	public void info(String arg0) {
		logger.info(arg0);
	}

	@Override
	public void info(String arg0, Throwable arg1) {
		logger.info(arg0);
	}

	@Override
	public boolean isDebugEnabled() {
		return logger.isDebugEnabled();
	}

	@Override
	public boolean isErrorEnabled() {
		return logger.isErrorEnabled();
	}

	@Override
	public boolean isInfoEnabled() {
		return logger.isInfoEnabled();
	}

	@Override
	public boolean isWarnEnabled() {
		return logger.isWarnEnabled();
	}

	@Override
	public void warn(String arg0) {
		logger.warn(arg0);
	}

	@Override
	public void warn(String arg0, Throwable arg1) {
		logger.warn(arg0, arg1);
	}
}
