package com.evinced.helpers;

import com.evinced.exceptions.EvincedRuntimeException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class FileReaderHelper {

	private static final Logger logger = LogManager.getLogger(FileReaderHelper.class);
	private static final String lineSeparator = System.getProperty("line.separator");

	/**
	 * @return Contents of evinced SDK script with a configured reporter.
	 */
	public static String readFileWithLineBreaksFromUrl(final URL script) {
		final StringBuilder sb = new StringBuilder();
		URLConnection connection;
		try {
			connection = script.openConnection();
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
				String line;
				while ((line = reader.readLine()) != null) {
					sb.append(line);
					// important - it's needed to add these line breaks, otherwise the bundle won't work
					sb.append(lineSeparator);
				}
				return sb.toString();
			}
		} catch (Exception e) {
			logger.error("Error reading Evinced Engines file", e);
			throw new EvincedRuntimeException("Error reading Evinced Engines\n" + e);
		}
	}
}
