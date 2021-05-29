/**
 * 
 */
package com.graphql_java_generator.plugin;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

/**
 * This interface is a wrapper for the sonatype BuildContext interface, that allows to properly integrate Maven build
 * into the IDE. It's up to the Maven or the Gradle plugin to implement this interface. <BR/>
 * The Maven plugin will link it to the sonatype BuildContext interface, that is an intermediate between the Maven build
 * (including the IDE environment) and the file system, with the source and generated projects.<BR/>
 * The Gradle plugin will link it directly to the file system.
 * 
 * @author etienne-sf
 */
public interface PluginBuildContext {

	static Logger logger = LoggerFactory.getLogger(PluginBuildContext.class);

	/**
	 * Returns <code>true</code> if the file has changed since last build or is not under basedir.
	 */
	boolean hasDelta(File file);

	/**
	 * Returns <code>true</code> if at least one of these files has changed since last build or is not under basedir.
	 */
	default boolean hasDelta(List<Resource> resources) {
		File file = null;
		boolean hasDelta = false;

		for (Resource resource : resources) {
			try {
				file = resource.getFile();
			} catch (IOException e) {
				// According to the BuildContext doc, and as this resource is not under basedir, we return true
				if (logger.isDebugEnabled())
					logger.debug("IOException in PluginBuildContext.hasDelta(List<Resource>) for '"
							+ file.getAbsolutePath() + "'");
				return true;
			}
			hasDelta = hasDelta || hasDelta(file);
		}

		logger.info("PluginBuildContext.hasDelta(List<Resource>) returns '" + hasDelta + "'");
		return hasDelta;
	}

	/**
	 * Returns a new OutputStream that writes into the <code>file</code>.
	 * 
	 * Files changed using OutputStream returned by this method do not need to be explicitly refreshed using
	 * {@link #refresh(File)}.
	 *
	 * As an optional optimization, OutputStreams created by incremental build context will attempt to avoid writing to
	 * the file if file content has not changed.
	 */
	OutputStream newFileOutputStream(File file) throws IOException;

}
