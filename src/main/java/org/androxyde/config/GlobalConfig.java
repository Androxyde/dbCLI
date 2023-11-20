package org.androxyde.config;

import java.io.File;
import java.net.URL;
import java.util.Optional;
import java.util.Properties;

public class GlobalConfig {

	static Properties properties = new Properties();
	
	public static void load() {
		try {
			String clidir = new File(GlobalConfig.class.getClassLoader().getResource("org/manu/config/GlobalConfig.class").getFile().split("!")[0]).getParent();
			properties.load(new URL(clidir+File.separator+"conf"+File.separator+"config.properties").openStream());
		} catch (Exception e) {
		}
	}

	public static String getProperty(String property) {
		
		if (properties.size()==0) load();
		return Optional.ofNullable(properties.getProperty(property)).orElse("");
		
	}

	public static String toJson() {
		return "";//JSON.prettyPrint(properties);
	}

}