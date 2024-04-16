package org.androxyde.config;

import org.androxyde.Agent;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;
import java.util.Optional;
import java.util.Properties;

public class GlobalConfig {

	private static Properties properties = new Properties();
	
	public static void load() {
		try {
			properties.load(new FileReader(Agent.getRootFolder()+File.separator+"conf"+File.separator+"config.properties"));
		} catch (Exception e) {
		}
		try {
			properties.setProperty("pf_env",Files.readString(new File("/etc/dbCLI/pf_env").toPath()));
		} catch (Exception e) {}
	}

	public static String getProperty(String property) {
		
		return Optional.ofNullable(properties.getProperty(property)).orElse("");
		
	}

	public static void setProperty(String key, String value) {

		properties.setProperty(key,value);

	}

}