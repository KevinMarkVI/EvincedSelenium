package com.evinced.helpers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;

import java.util.List;
import java.util.UUID;

/**
 * Holds the Web driver injection extension methods.
 */

public class IframeHelper {

	public static final String WINDOW_EVINCED_IFRAME_MARK = "window.evincedIframeMark";
	private static final Logger logger = LogManager.getLogger(IframeHelper.class);

	private static String addEvincedUniqueMarkToCurrentIframe(final WebDriver driver) {
		String id = UUID.randomUUID().toString();
		((JavascriptExecutor) driver).executeScript(String.format("%s = '%s'", WINDOW_EVINCED_IFRAME_MARK, id));
		return id;
	}

	private static String getEvincedUniqueMarkToCurrentIframe(final WebDriver driver) {
		return (String) ((JavascriptExecutor) driver).executeScript(String.format("return %s", WINDOW_EVINCED_IFRAME_MARK));
	}

	private static void removeEvincedMarkFromFrame(final WebDriver driver) {
		((JavascriptExecutor) driver).executeScript(String.format("delete %s", WINDOW_EVINCED_IFRAME_MARK));
	}

	/**
	 * Injects Axe script into frames.
	 * If a frame (not top-level) errors when injecting due to not being displayed, the error is ignored.
	 *
	 * @param driver WebDriver instance to inject into
	 * @param script The script to inject
	 */
	public static void inject(final WebDriver driver,
							  final String script) {
		JavascriptExecutor js = (JavascriptExecutor) driver;

		String contextIdBeforeInjection = addEvincedUniqueMarkToCurrentIframe(driver);
		driver.switchTo().defaultContent();
		js.executeScript(script);
		try {
			injectIntoFrames(driver, script);
		} catch (Exception e) {
			// Ignore all errors except those caused by the injected javascript itself
			if (e instanceof JavascriptException) {
				logger.error("Error injecting Evinced SDK to a frame/iframe", e);
				throw e;
			}
			logger.warn("Error injecting Evinced SDK to a frame/iframe, might be not related to the injection", e);
		}

		try {
			driver.switchTo().defaultContent();
			boolean didSwitchContextBackSucceed = switchToIframeWithEvincedMark(driver, contextIdBeforeInjection);
			if (!didSwitchContextBackSucceed) {
				logger.error("Error going back to the context before injection, failed without an exception");
				driver.switchTo().defaultContent();
			}
		} catch (Exception e) {
			logger.error("Error going back to the context before injection", e);
			driver.switchTo().defaultContent();
		}
	}

	/**
	 * Recursively find frames and inject a script into them.
	 * If a frame errors when injecting due to not being displayed, the error is ignored.
	 *
	 * @param driver An initialized WebDriver
	 * @param script Script to inject
	 */
	private static void injectIntoFrames(final WebDriver driver,
										 final String script) {
		List<WebElement> frames = driver.findElements(By.tagName("iframe"));

		// stopping the recursion, climbing back to parent
		if (frames.size() == 0) {
			driver.switchTo().parentFrame();
			return;
		}

		// iterating each child iframe and injecting to it
		for (WebElement frame : frames) {
			driver.switchTo().frame(frame);

			// no need to re-inject script to iframes that already has it
			if (!EvincedInjectionHelper.isEvincedInjected(driver)) {
				logger.debug("Evinced SDK is injected to a frame");
				JavascriptExecutor js = (JavascriptExecutor) driver;
				js.executeScript(script);
			}

			injectIntoFrames(driver, script);
		}
	}

	/**
	 * Recursively find the frame marked by a specific id and switching context to it.
	 * In case it wasn't found, switch to default context.
	 *
	 * @param driver        An initialized WebDriver
	 * @param evincedMarkId Iframe mark to look for
	 * @return true if the correct iframe was found
	 */
	private static boolean switchToIframeWithEvincedMark(final WebDriver driver, String evincedMarkId) {
		// if the current frame has the evincedId on it, stop the recursion
		String evincedId = getEvincedUniqueMarkToCurrentIframe(driver);
		if (evincedMarkId.equals(evincedId)) {
			removeEvincedMarkFromFrame(driver);
			// found the marked frame
			return true;
		}

		List<WebElement> frames = driver.findElements(By.tagName("iframe"));
		// stopping the recursion, climbing back to parent
		if (frames.size() == 0) {
			driver.switchTo().parentFrame();
			return false;
		}

		// iterating each child iframe to check if it or its children are the iframe we are looking for
		for (WebElement frame : frames) {
			driver.switchTo().frame(frame);
			if (switchToIframeWithEvincedMark(driver, evincedMarkId)) {
				// iframe was found somewhere in this subtree
				return true;
			}
		}

		// iframe wasn't found in this subtree
		return false;
	}
}
