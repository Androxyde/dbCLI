package org.androxyde;

import java.lang.instrument.Instrumentation;
import java.io.File;
import java.net.URLClassLoader;
import java.net.URL;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.jar.JarFile;
import java.lang.reflect.Method;

public class Agent {

	public final static String version = "dbCLI release 1.0 "+ System.getProperty("java.vm.vendor");
	private static Instrumentation inst = null;
	
	private static Set<File> jars = new LinkedHashSet<File>();

    // The JRE will call method before launching your main()
    public static void premain(final String a, final Instrumentation inst) {
    	Agent.inst = inst;
    }

    // The JRE will call method before launching your main()
    public static void agentmain(final String a, final Instrumentation inst) {
    	Agent.inst = inst;
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

}