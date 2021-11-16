package com.evinced.helpers;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.config.Configurator;

public class LogHelper {
	public static void initializeLogger(Level logLevel) {
		Configurator.setLevel(LogManager.getRootLogger().getName(), logLevel);
	}
}