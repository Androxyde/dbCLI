package org.androxyde;

import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URLClassLoader;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.jar.JarFile;
import java.lang.reflect.Method;

public class Agent {

	public final static String version = "dbCLI Agent release 1.0 "+ System.getProperty("java.vm.vendor");
	private static Instrumentation inst = null;

	private static String rootFolder=null;

	private static Set<File> jars = new LinkedHashSet<File>();

	public static void premain(final String a, final Instrumentation inst) {

		Agent.agentmain(a,inst);

	}

    // The JRE will call method before launching your main()
    public static void agentmain(final String a, final Instrumentation inst) {

		Agent.inst = inst;

		rootFolder = Agent.urlToFile(Agent.getLocation(Agent.class)).getParentFile().getAbsolutePath();

		System.out.println(rootFolder);
		System.out.println(System.getenv("APP_HOME"));

		try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(rootFolder))) {
			for (Path path : stream) {
				if (!Files.isDirectory(path) && path.toString().endsWith(".jar")) {
					Agent.addToClassPath(path.toFile());
				}
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

	}

    public static boolean contains(File f) {
    	return jars.contains(f);
    }

    public static boolean addToClassPath(File f) {

        ClassLoader cl = ClassLoader.getSystemClassLoader();

        if (! jars.contains(f) ) {

	        jars.add(f);
	
	        try {
	
	        	// If Java 9 or higher use Instrumentation
	            if (!(cl instanceof URLClassLoader)) {
	                inst.appendToSystemClassLoaderSearch(new JarFile(f));
	                return true;
	            }
	
	            // If Java 8 or below fallback to old method
	            Method m = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
	            m.setAccessible(true);
	            m.invoke(cl, (Object)f.toURI().toURL());
	        } catch (Throwable e) {
	                e.printStackTrace();
	                return false;
	        }
	        return false;

        } else {

        	return true;

        }

    }

	public static URL getLocation(final Class<?> c) {
		if (c == null) return null; // could not load the class

		// try the easy way first
		try {
			final URL codeSourceLocation =
					c.getProtectionDomain().getCodeSource().getLocation();
			if (codeSourceLocation != null) return codeSourceLocation;
		}
		catch (final SecurityException e) {
			// NB: Cannot access protection domain.
		}
		catch (final NullPointerException e) {
			// NB: Protection domain or code source is null.
		}

		// NB: The easy way failed, so we try the hard way. We ask for the class
		// itself as a resource, then strip the class's path from the URL string,
		// leaving the base path.

		// get the class's raw resource path
		final URL classResource = c.getResource(c.getSimpleName() + ".class");
		if (classResource == null) return null; // cannot find class resource

		final String url = classResource.toString();
		final String suffix = c.getCanonicalName().replace('.', '/') + ".class";
		if (!url.endsWith(suffix)) return null; // weird URL

		// strip the class's path from the URL string
		final String base = url.substring(0, url.length() - suffix.length());

		String path = base;

		// remove the "jar:" prefix and "!/" suffix, if present
		if (path.startsWith("jar:")) path = path.substring(4, path.length() - 2);

		try {
			return new URL(path);
		}
		catch (final MalformedURLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static File urlToFile(final URL url) {
		return url == null ? null : urlToFile(url.toString());
	}

	public static File urlToFile(final String url) {
		String path = url;
		if (path.startsWith("jar:")) {
			// remove "jar:" prefix and "!/" suffix
			final int index = path.indexOf("!/");
			path = path.substring(4, index);
		}
		try {
			return new File(new URL(path).toURI());
		}
		catch (final MalformedURLException e) {
			// NB: URL is not completely well-formed.
		}
		catch (final URISyntaxException e) {
			// NB: URL is not completely well-formed.
		}
		if (path.startsWith("file:")) {
			// pass through the URL as-is, minus "file:" prefix
			path = path.substring(5);
			return new File(path);
		}
		throw new IllegalArgumentException("Invalid URL: " + url);
	}

}