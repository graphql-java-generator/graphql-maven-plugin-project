/**
 * 
 */
package graphql.mavenplugin.test.compiler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import graphql.mavenplugin.Logger;

/**
 * This class allows to compile a given source code.
 * 
 * @author EtienneSF
 */
public class GeneratedSourceCompilerImpl implements GeneratedSourceCompiler {

	/** The logger, where to output the error, warning... of the compilation */
	Logger log;

	/** The root folder, where java source file must be written, and compiled from. */
	File javaSrcFolder;

	/** The root folder, where class file must be written */
	File classTargetFolder;

	/** The name of the class being compiled */
	String className;

	/** The classpath to be added to the compiler classpath, to allow compilation of the received generated sources */
	String classpath;

	/** The charset that will be use to generate the java file */
	Charset javaFileCharset;

	/**
	 * @param log
	 * @param className
	 *            The full classname, that is: with the package (e.g.: java.io.File)
	 * @param javaSrcFolder
	 * @param classTargetFolder
	 * @param classpath
	 *            If null, the current classpath is used.
	 * @param javaFileCharset
	 *            The {@link Charset} (like UTF-8...) to use to store the java source file.
	 */
	public GeneratedSourceCompilerImpl(Logger log, String className, File javaSrcFolder, File classTargetFolder,
			String classpath, Charset javaFileCharset) {
		this.log = log;
		this.className = className;
		this.javaSrcFolder = javaSrcFolder;
		this.classTargetFolder = classTargetFolder;
		if (classpath != null) {
			this.classpath = classpath;
		} else {
			StringBuffer buffer = new StringBuffer();
			log.debug("Using current class loader context classpath for compilation");
			for (URL url : ((URLClassLoader) (Thread.currentThread().getContextClassLoader())).getURLs()) {
				buffer.append(new File(url.getPath()));
				buffer.append(System.getProperty("path.separator"));
			}
			this.classpath = buffer.toString();
		}

		this.javaFileCharset = javaFileCharset;
	}

	/** {@inheritDoc} */
	@Override
	public boolean compileAllSources() {
		// First step: read the java files
		List<File> javaFiles = new ArrayList<>();
		fillJavaFileList(javaSrcFolder, javaFiles);
		// Then, compile them
		return compileJavaFiles(javaFiles);
	}

	/**
	 * Read all java files in this folder, and its subfolder, and add them to the given list.
	 * 
	 * @param folder
	 * @param javaFiles
	 */
	void fillJavaFileList(File folder, List<File> javaFiles) {
		for (File f : folder.listFiles()) {
			if (f.isDirectory()) {
				// A folder: we dig in.
				fillJavaFileList(f, javaFiles);
			} else if (f.getName().endsWith(".java")) {
				// A java file, we add it to the list.
				javaFiles.add(f);
			}
			// Other 'stuff' than folders and java files are ignored.
		} // for
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see {@link #GeneratedSourceCompilerImpl(Logger, String, File, File, String, Charset)}
	 */
	@Override
	public boolean compileOneSource() {
		// First step: store the java file
		List<File> javaFiles = new ArrayList<>();
		javaFiles.add(getJavaFile());

		return compileJavaFiles(javaFiles);
	}

	/**
	 * @param javaFiles
	 * @return
	 */
	boolean compileJavaFiles(List<File> javaFiles) {
		// Let's manage the classpath
		List<String> optionList = new ArrayList<String>();
		if (classpath != null) {
			optionList.add("-classpath");
			// Let's add the classpath
			optionList.add(classpath);
			log.debug("Compilation will use this classpath: " + classpath);
		} /*
			 * else { // set compiler's classpath to be same as the runtime's optionList.add("-classpath");
			 * optionList.add(System.getProperty(classpath)); }
			 */
		// Let's manage the target directory
		if (classTargetFolder != null) {
			optionList.add("-d");
			// Let's add the classpath
			optionList.add(classTargetFolder.getAbsolutePath());
			log.debug("[pre-compilation step] Class file will be written in " + classTargetFolder.getAbsolutePath());
			// Let's insure that the necessary folders are created.
			classTargetFolder.mkdirs();
		}

		// Let's do the compilation
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
		Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromFiles(javaFiles);
		// boolean success = compiler.getTask(sw, fileManager, diagnosticListener, optionList, null,
		// compilationUnits).call();
		boolean success = compiler.getTask(null, fileManager, new DiagnosticListenerImpl(log, className), optionList,
				null, compilationUnits).call();
		try {
			fileManager.close();
		} catch (IOException e) {
			throw new RuntimeException("Error after generating the java source file", e);
		}

		return success;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.wsutils.servergeneration.portimpl.compiler.GeneratedSourceCompiler#compile(java.lang.String)
	 */
	@Override
	public boolean compileGivenSource(String javaSource) {
		storeJavaSource(javaSource);
		return compileOneSource();
	}

	/**
	 * Return the path for the java file to compile.
	 * 
	 * @return
	 */
	private File getJavaFile() {
		String getRelativePath = className.replace(".", "/") + ".java";
		return new File(javaSrcFolder, getRelativePath);
	}

	/**
	 * @param javaSource
	 * @return
	 */
	private File storeJavaSource(String javaSource) {
		File javaFile = getJavaFile();
		createFolderHierarchy(javaFile);

		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(javaFile);
			fos.write(javaSource.getBytes(javaFileCharset));
		} catch (IOException e) {
			throw new RuntimeException("Could generate the java source file", e);
		} finally {
			try {
				if (fos != null) {
					fos.close();
				}
			} catch (IOException e) {
				log.error("Could not close the OutputStream to the java source file", e);
			}
		}

		return javaFile;
	}

	/**
	 * @return
	 */
	File getClassFile() {
		String getRelativePath = className.replace(".", "/") + ".class";
		File f = new File(classTargetFolder, getRelativePath);
		/*
		 * try { f.createNewFile(); } catch (IOException e) { throw new MojoExecutionException("Could not create the " +
		 * f.getAbsolutePath(), e); }
		 */
		return f;
	}

	/**
	 * Create the subfolder structure, for the given regular {@link File] f.
	 * 
	 * @param f
	 *            The file whose parent are to be created.
	 */
	void createFolderHierarchy(File f) {
		if (!f.getParentFile().exists()) {
			f.getParentFile().mkdirs();
		}
	}

}
