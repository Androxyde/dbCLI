package org.androxyde;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import io.micronaut.configuration.picocli.MicronautFactory;
import io.micronaut.context.ApplicationContext;
import io.micronaut.context.env.PropertySource;
import org.androxyde.cli.MyCommandListRenderer;
import org.androxyde.cli.MyExceptionHandler;
import org.androxyde.cli.dbCLI;
import org.androxyde.config.GlobalConfig;
import org.androxyde.utils.Json;
import org.androxyde.utils.OS;
import org.buildobjects.process.ProcBuilder;
import picocli.CommandLine;

import static io.micronaut.context.env.PropertySource.mapOf;
import static picocli.CommandLine.Model.UsageMessageSpec.SECTION_KEY_COMMAND_LIST;

public class Main {

	static Set<File> folders = new LinkedHashSet<File>();

	static class JarFilter implements FilenameFilter {
		
		String filter;
		
		public JarFilter(String filter) {
			this.filter=filter+".*.jar$";
		}

		public boolean accept(File dir, String name) {
			return name.toLowerCase().matches(filter);
		}

	}

	public static void loadBootJars() {
        try {
        	for (File folder:folders) {
        		for (File jar:folder.listFiles(new JarFilter("^.*(slf4j|log4j|logback|picocli).*jar$"))) {
        			if (!Agent.contains(jar)) Agent.addToClassPath(jar);
        		}
        	}
		} catch (Exception e) {
			e.printStackTrace();;
		}
		
	}

	public static void loadJars() {
        try {
        	for (File folder:folders) {
        		for (File jar:folder.listFiles(new JarFilter("^((?!jdbc|ucp|server).)*jar$"))) {
        			if (!Agent.contains(jar)) Agent.addToClassPath(jar);
        		}
        	}
		} catch (Exception e) {
			e.printStackTrace();;
		}
		
	}

	public static void loadSqlcl() {
		
		for (String stringFile:GlobalConfig.getProperty("sqlcl_libs").split(";")) {
			if (new File(stringFile).isDirectory()) {
				folders.add(new File(stringFile));
			}
			if (new File(stringFile+File.separator+"extras").isDirectory()) {
				folders.add(new File(stringFile+File.separator+"extras"));
			}
		}

		try {
        	for (File folder:folders) {
        		for (File jar:folder.listFiles(new JarFilter("^.*(dbtools|orai18n-mapping|orai18n-utility|xmlparser.*sans).*jar$"))) {
        			if (!Agent.contains(jar)) {
        				Agent.addToClassPath(jar);
        			}
        		}
        	}
		} catch (Exception e) {
			e.printStackTrace();;
		}
		
	}

	public static void loadJdbc() {
		
		List<File> folders = new ArrayList<File>();
		
		String oracleHome = System.getenv("ORACLE_HOME");
		
		if (oracleHome!=null) {
			if (new File(oracleHome+File.separator+"/jdbc/lib").isDirectory()) {
				folders.add(new File(oracleHome+File.separator+"/jdbc/lib"));
			}
			if (new File(oracleHome+File.separator+"/ucp/lib").isDirectory()) {
				folders.add(new File(oracleHome+File.separator+"/ucp/lib"));
			}
		}

		try {
        	for (File folder:folders) {

        		// search best jdbc driver
        			if (Agent.getJavaVersion()>10)
                		for (File jar:folder.listFiles(new JarFilter("^.*(jdbc11\\.).*jar$"))) {
                			if (!Agent.contains(jar)) {
                				Agent.addToClassPath(jar);
                			}
                		}
        			else {
            			if (Agent.getJavaVersion()>9)
                    		for (File jar:folder.listFiles(new JarFilter("^.*(jdbc10\\.).*jar$"))) {
                    			if (!Agent.contains(jar)) {
                    				Agent.addToClassPath(jar);
                    			}
                    		}
            			else {
            				for (File jar:folder.listFiles(new JarFilter("^.*(jdbc8\\.).*jar$"))) {
                    			if (!Agent.contains(jar)) {
                    				Agent.addToClassPath(jar);
                    			}
                    		}
            			}
        			}

        			if (Agent.getJavaVersion()>10)
                		for (File jar:folder.listFiles(new JarFilter("^.*(ucp11\\.).*jar$"))) {
                			if (!Agent.contains(jar)) {
                				Agent.addToClassPath(jar);
                			}
                		}
        			else {
        				for (File jar:folder.listFiles(new JarFilter("^.*(ucp\\.).*jar$"))) {
                			if (!Agent.contains(jar)) {
                				Agent.addToClassPath(jar);
                			}
                		}
        			}

        	}
		} catch (Exception e) {
			e.printStackTrace();;
		}
		
	}

	public static void main(String[] args) {

		loadBootJars();

		String env="prod";

		for (int i=0; i<args.length; i++) {
			if (args[i].equals("--env"))
				env=args[i+1];
		}

		try (ApplicationContext context = ApplicationContext.builder(
				dbCLI.class, "cli").start()) {
			context.getEnvironment().addPropertySource(
					PropertySource.of(
							"cli",
							mapOf(
									"api.url", "myurl"
							))
			).refresh();

			CommandLine cmd = new CommandLine(dbCLI.class, new MicronautFactory(context))
					.setCaseInsensitiveEnumValuesAllowed(true)
					.setUsageHelpAutoWidth(true)
					.setExecutionExceptionHandler(new MyExceptionHandler());

			cmd.getHelpSectionMap().put(SECTION_KEY_COMMAND_LIST,new MyCommandListRenderer());

			int result = cmd.execute(args);

			System.exit(result);

		}
	}
}