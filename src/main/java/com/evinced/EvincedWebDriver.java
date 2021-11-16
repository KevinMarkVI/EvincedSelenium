package com.evinced;

import com.evinced.dto.configuration.EvincedConfiguration;
import com.evinced.dto.results.AnalysisResult;
import com.evinced.dto.results.Report;
import com.evinced.exceptions.EvincedRuntimeException;
import com.evinced.helpers.LogHelper;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.events.EventFiringWebDriver;

import java.util.HashMap;

public class EvincedWebDriver extends EventFiringWebDriver {

	private static final Logger logger = LogManager.getLogger(EvincedWebDriver.class);

	EvincedRunningState runningState = new EvincedRunningState();
	EvincedConfiguration globalConfiguration = new EvincedConfiguration();
	EvincedWebListener eventListeners;
	EvincedRunner evincedRunner;

	/**
	 * Creates a WebDriver instance using Evinced's wrapper
	 * Enabling Evinced accessibility validation methods
	 *
	 * @param driver WebDriver instance to use
	 */
	public EvincedWebDriver(final WebDriver driver) {
		super(driver);
		this.evincedRunner = new EvincedRunner(driver, runningState);
		setLogLevel(Level.OFF);
	}

	/**
	 * Creates a WebDriver instance using Evinced's wrapper
	 * Enabling Evinced accessibility validation methods
	 *
	 * @param driver        WebDriver instance to use
	 * @param configuration Evinced accessibility configuration
	 */
	public EvincedWebDriver(final WebDriver driver, EvincedConfiguration configuration) {
		this(driver);
		this.globalConfiguration = configuration;
	}

	private void lockIsInProgress() {
		if (runningState.isRunning()) {
			throw new EvincedRuntimeException("Running another `evStart` command during a run is not supported.");
		}
		runningState.setRunning(true);
	}

	private void releaseIsInProgress() {
		runningState.setRunning(false);
	}

	/**
	 * Watches for DOM mutations and page navigation, recording all accessibility
	 * issues until `evStop()` is called
	 */
	public void evStart() {
		evStart(globalConfiguration);
	}

	/**
	 * Watches for DOM mutations and page navigation, recording all accessibility
	 * issues until `evStop()` is called
	 *
	 * @param configuration Evinced accessibility configuration
	 */
	public void evStart(EvincedConfiguration configuration) {
		logger.debug(String.format("evStart called with configuration %s", configuration));
		lockIsInProgress();

		// create and register listeners
		eventListeners = new EvincedWebListener(evincedRunner, configuration);
		register(eventListeners);

		String url = this.getWrappedDriver().getCurrentUrl();
		evincedRunner.startAnalysis(configuration, url);
	}

	/**
	 * Returns all recorded issues since the last call to `evStart`.
	 *
	 * @return A Report object containing accessibility issues
	 */
	public Report evStop() {
		logger.debug("evStop called");
		if (!runningState.isRunning()) {
			throw new EvincedRuntimeException("evStop was called before evStart");
		}
		unregister(eventListeners);
		releaseIsInProgress();

		HashMap<String, AnalysisResult> accessibilityReports = runningState.getAccessibilityReports();
		AnalysisResult report = evincedRunner.stopAnalysis();
		// we put the result in the hashmap to override previous results from the same URL
		accessibilityReports.put(getWrappedDriver().getCurrentUrl(), report);

		// empty previous results from running state
		this.runningState.setAccessibilityReports(new HashMap<>());
		return new Report(accessibilityReports);
	}

	/**
	 * Scans the current page and returns a list of accessibility issues.
	 * (not supported during `evStart`)
	 *
	 * @return A Report object containing accessibility issues
	 **/
	public Report evReport() {
		return evReport(globalConfiguration);
	}

	/**
	 * Scans the current page and returns a list of accessibility issues.
	 * (not supported during `evStart`)
	 *
	 * @param configuration Evinced accessibility configuration
	 * @return A Report object containing accessibility issues found on the current DOM snippet
	 **/
	public Report evReport(EvincedConfiguration configuration) {
		logger.debug(String.format("evReport called with configuration %s", configuration));
		lockIsInProgress();
		AnalysisResult report = evincedRunner.runAnalysisOnce(configuration);
		releaseIsInProgress();
		return new Report(report.getReport());
	}

	/**
	 * Set log level for Evinced related actions
	 *
	 * @param logLevel - log level to use
	 */
	public void setLogLevel(Level logLevel) {
		LogHelper.initializeLogger(logLevel);
		logger.debug("Log level is set to: " + logLevel);
	}

}
