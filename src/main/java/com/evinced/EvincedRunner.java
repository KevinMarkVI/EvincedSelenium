package com.evinced;

import com.evinced.dto.configuration.EvincedConfiguration;
import com.evinced.dto.init.AnalysisPersistedData;
import com.evinced.dto.inner.RunResult;
import com.evinced.dto.results.AnalysisResult;
import com.evinced.exceptions.EvincedRuntimeException;
import com.evinced.helpers.EvincedInjectionHelper;
import com.evinced.helpers.EvincedScriptsHelper;
import com.evinced.helpers.IframeHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

public class EvincedRunner {

	private static final Logger logger = LogManager.getLogger(EvincedRunner.class);
	private final ObjectMapper objectMapper = new ObjectMapper();
	private final WebDriver driver;
	private final EvincedRunningState runningState;

	public EvincedRunner(WebDriver driver, EvincedRunningState runningState) {
		this.driver = driver;
		this.runningState = runningState;
	}


	/**
	 * Recursively injects Evinced to the top level document with the option to skip iframes.
	 *
	 * @param driver         WebDriver instance to inject into.
	 * @param includeIframes True if Evinced should not be injected into iframes
	 */
	private void injectSdk(final WebDriver driver, Boolean includeIframes) {
		String enginesScriptToInject = EvincedScriptsHelper.getBundleToInject();
		if (includeIframes) {
			IframeHelper.inject(driver, enginesScriptToInject);
			return;
		}

		if (EvincedInjectionHelper.isEvincedInjected(driver)) {
			return;
		}

		// inject script to main frame
		JavascriptExecutor js = (JavascriptExecutor) driver;
		driver.switchTo().defaultContent();
		js.executeScript(enginesScriptToInject);
	}

	private AnalysisResult parseResults(Object response) {
		RunResult evincedResult = objectMapper.convertValue(response, RunResult.class);
		String error = evincedResult.getError();
		if (!Strings.isNullOrEmpty(error)) {
			// If the error is non null, raise a runtime error.
			throw new EvincedRuntimeException(error);
		}
		// If there was no error, there must have been results.
		return evincedResult.getResults();
	}

	/**
	 * Run Evinced on the page
	 *
	 * @return An Evinced results document
	 */
	public AnalysisResult runAnalysisOnce(EvincedConfiguration configuration) {
		// inject Evinced bundle to page
		injectSdk(this.driver, configuration.shouldIncludeIframes());

		// run init
		executeEnginesInitScript(configuration, null);
		// run analysis once command
		String snippet = EvincedScriptsHelper.getEvincedRunAnalysisOnceSnippet();
		return executeReportGeneratingScript(snippet);
	}

	private void executeEnginesInitScript(EvincedConfiguration configuration, AnalysisPersistedData persistedData) {
		String snippet = EvincedScriptsHelper.getEvincedInitScript(configuration, persistedData);
		((JavascriptExecutor) this.driver).executeScript(snippet);
	}

	/**
	 * Start Evinced recording on the page
	 */
	public void startAnalysis(EvincedConfiguration configuration, String url) {
		// inject Evinced bundle to page
		injectSdk(this.driver, configuration.shouldIncludeIframes());

		// if there's a state from the a previous run, format and send it to the `start` command
		AnalysisPersistedData persistedState = runningState.getUrlPersistedData(url);
		// run init
		executeEnginesInitScript(configuration, persistedState);
		// start scan
		String snippet = EvincedScriptsHelper.getEvincedStartAnalysisSnippet();
		long startTime = System.nanoTime();

		((JavascriptExecutor) this.driver).executeAsyncScript(snippet);

		long endTime = System.nanoTime();
		long durationMilis = (endTime - startTime) / 1000000;
		logger.trace(String.format("Duration for Evinced execution: %d MS. Initiated by: %s", durationMilis, "startAnalysis"));

	}

	/**
	 * Stops recording and returns the Evinced results since previous 'start'
	 *
	 * @return An Evinced results document
	 */
	public AnalysisResult stopAnalysis() {
		String snippet = EvincedScriptsHelper.getEvincedStopAnalysisSnippet();
		long startTime = System.nanoTime();

		AnalysisResult analysisResult = executeReportGeneratingScript(snippet);

		long endTime = System.nanoTime();
		long durationMilis = (endTime - startTime) / 1000000;
		logger.trace(String.format("Duration for Evinced execution: %d MS. Initiated by: %s", durationMilis, "stopAnalysis"));
		return analysisResult;

	}

	private AnalysisResult executeReportGeneratingScript(String snippet) {
		Object response = ((JavascriptExecutor) this.driver).executeAsyncScript(snippet);
		return parseResults(response);
	}

	public WebDriver getDriver() {
		return driver;
	}

	/**
	 * verifies SDK is injected, stores current report and continues to scan
	 */
	public void handleDomOrPageChangeWhileRecording(EvincedConfiguration configuration) {
		String url = this.getDriver().getCurrentUrl();
		if (GeneralConfiguration.getUrlsToIgnore().contains(url)) {
			return;
		}
		// in case Evinced is already on the page - collect results from it
		AnalysisResult result = null;
		if (EvincedInjectionHelper.isEvincedInjected(driver)) {
			result = stopAnalysis();
		}

		startAnalysis(configuration, url);
		runningState.addAccessibilityReportsToResults(url, result);
	}
}
