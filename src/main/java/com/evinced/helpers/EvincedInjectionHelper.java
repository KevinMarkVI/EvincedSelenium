package com.evinced.helpers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

public class EvincedInjectionHelper {

	private static final ObjectMapper objectMapper = new ObjectMapper();
	private static final Logger logger = LogManager.getLogger(EvincedInjectionHelper.class);

	public static boolean isEvincedInjected(WebDriver driver) {
		String snippet = EvincedScriptsHelper.getIsEvincedInjectedScript();
		Object response = ((JavascriptExecutor) driver).executeScript(snippet);
		try {
			return objectMapper.convertValue(response, Boolean.class);
		} catch (Exception e) {
			logger.error("Error communicating with web driver regarding the injection of Evinced code", e);
			// some parsing error
			return false;
		}
	}
}
