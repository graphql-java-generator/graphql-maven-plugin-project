package graphql.mavenplugin.test.helper;

import java.io.File;

import org.springframework.stereotype.Component;

@Component
public class MavenTestHelper {

	final static String MODULE_NAME = "graphql-maven-plugin";

	/**
	 * Returns the root path for this module. The issue here, is that the current path is the root path for this module,
	 * if the build is done only for this module. But if the build is done for the whole project, the current path is
	 * one level above (that is: the root for the whole project)
	 * 
	 * @return
	 */
	public static File getModulePathFile() {
		String path = new File(".").getAbsolutePath();
		File f = null;
		if (path.contains(MODULE_NAME)) {
			f = new File(path);
		} else {
			f = new File(path, MODULE_NAME);
		}
		return f;
	}

	/**
	 * Remove a folder and all its content. This method checks that this folder is in a target subfolder. That is: it
	 * contains 'target' in it.
	 * 
	 * @param file
	 *            The folder to delete (including its content, subfolders...
	 * @return true if the file was removed. false if the file didn't exist
	 * @throws IllegalArgumentException
	 *             If it's not a target folder
	 */
	public boolean deleteDirectoryAndContentIfExists(File file) throws IllegalArgumentException {
		if (!file.getAbsolutePath().contains("target")) {
			throw new IllegalArgumentException(file.getAbsolutePath() + " is not in a target folder");
		}
		if (file.exists()) {
			if (!file.isDirectory()) {
				throw new IllegalArgumentException(file.getAbsolutePath() + " is not a folder");
			}
			File[] files = file.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					deleteDirectoryAndContentIfExists(files[i]);
				} else {
					files[i].delete();
				}
			} // for
			return file.delete();
		} // if

		return false;
	}
}
