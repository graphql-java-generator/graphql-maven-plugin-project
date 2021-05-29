package com.graphql_java_generator.mavenplugin;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonatype.plexus.build.incremental.BuildContext;
import org.springframework.stereotype.Component;

import com.graphql_java_generator.plugin.PluginBuildContext;

@Component
public class PluginBuildContextImpl implements PluginBuildContext {

	static Logger logger = LoggerFactory.getLogger(PluginBuildContextImpl.class);

	/** The {@link BuildContext} allows to link the file system with the IDE */
	BuildContext buildContext = null;

	/** @see BuildContext#hasDelta(File) */
	@Override
	public boolean hasDelta(File file) {
		boolean ret = buildContext.hasDelta(file);

		if (ret)
			logger.info("BuildContext found no change for file '" + file.getAbsolutePath() + "'");
		else
			logger.info("BuildContext found changes for file '" + file.getAbsolutePath() + "'");

		return ret;
	}

	/** @see BuildContext#newFileOutputStream(File) */
	@Override
	public OutputStream newFileOutputStream(File file) throws IOException {
		return buildContext.newFileOutputStream(file);
	}

	public BuildContext getBuildContext() {
		return buildContext;
	}

	public void setBuildContext(BuildContext buildContext) {
		if (buildContext == null) {
			throw new NullPointerException("buildContext may not be null");
		}
		this.buildContext = buildContext;
	}

}
