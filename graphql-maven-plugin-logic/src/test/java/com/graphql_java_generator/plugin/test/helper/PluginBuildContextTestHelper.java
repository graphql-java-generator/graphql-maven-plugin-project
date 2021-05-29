package com.graphql_java_generator.plugin.test.helper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.graphql_java_generator.plugin.PluginBuildContext;

public class PluginBuildContextTestHelper implements PluginBuildContext {

	@Override
	public boolean hasDelta(File file) {
		return true;
	}

	@Override
	public OutputStream newFileOutputStream(File file) throws IOException {
		return new FileOutputStream(file);
	}

}
