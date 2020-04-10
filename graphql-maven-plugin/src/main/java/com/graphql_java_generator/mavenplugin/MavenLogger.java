package com.graphql_java_generator.mavenplugin;

import org.apache.maven.plugin.logging.Log;

import com.graphql_java_generator.plugin.Logger;

/**
 * 
 * @author etienne-sf
 */
public class MavenLogger implements Logger {

	final private Log log;

	MavenLogger(GraphqlMavenPlugin mojo) {
		this.log = mojo.getLog();
	}

	@Override
	public boolean isDebugEnabled() {
		return log.isDebugEnabled();
	}

	@Override
	public void debug(String msg) {
		log.debug(msg);
	}

	@Override
	public void debug(String msg, Throwable t) {
		log.debug(msg, t);
	}

	@Override
	public boolean isInfoEnabled() {
		return log.isInfoEnabled();
	}

	@Override
	public void info(String msg) {
		log.info(msg);
	}

	@Override
	public void info(String msg, Throwable t) {
		log.info(msg, t);
	}

	@Override
	public boolean isWarnEnabled() {
		return log.isWarnEnabled();
	}

	@Override
	public void warn(String msg) {
		log.warn(msg);
	}

	@Override
	public void warn(String msg, Throwable t) {
		log.warn(msg, t);
	}

	@Override
	public boolean isErrorEnabled() {
		return log.isErrorEnabled();
	}

	@Override
	public void error(String msg) {
		log.error(msg);
	}

	@Override
	public void error(String msg, Throwable t) {
		log.error(msg, t);
	}
}
