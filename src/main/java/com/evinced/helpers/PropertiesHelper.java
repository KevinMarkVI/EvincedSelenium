package com.evinced.helpers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Properties;

public class PropertiesHelper {
	private static final Logger logger = LogManager.getLogger(PropertiesHelper.class);

	public static String getVersion() {
		final Properties properties = new Properties();
		try {
			properties.load(PropertiesHelper.class.getClassLoader().getResourceAsStream(".properties"));
			return properties.getProperty("version");
		} catch (IOException e) {
			logger.error("Error getting version from pom.xml", e);
			return "default-version";
		}
	}

}
