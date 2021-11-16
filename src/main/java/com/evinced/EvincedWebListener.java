package com.evinced;

import com.evinced.dto.configuration.EvincedConfiguration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.events.WebDriverEventListener;

public class EvincedWebListener implements WebDriverEventListener {
	private static final Logger logger = LogManager.getLogger(EvincedWebListener.class);
	private final EvincedRunner evincedRunner;
	private final EvincedConfiguration configuration;

	public EvincedWebListener(EvincedRunner evincedRunner, EvincedConfiguration configuration) {
		this.evincedRunner = evincedRunner;
		this.configuration = configuration;
	}

	private void handleUserAction(String callerMethodName) {
		long startTime = System.nanoTime();
		this.evincedRunner.handleDomOrPageChangeWhileRecording(configuration);
		long endTime = System.nanoTime();
		long durationMilis = (endTime - startTime) / 1000000;
		logger.trace(String.format("Duration for Evinced execution: %d MS. Initiated by: %s", durationMilis, callerMethodName));
	}

	@Override
	public void beforeAlertAccept(WebDriver driver) {
		handleUserAction("beforeAlertAccept");
	}

	@Override
	public void afterAlertAccept(WebDriver driver) {
		handleUserAction("afterAlertAccept");
	}

	@Override
	public void afterAlertDismiss(WebDriver driver) {
		handleUserAction("afterAlertDismiss");
	}

	@Override
	public void beforeAlertDismiss(WebDriver driver) {
		handleUserAction("beforeAlertDismiss");
	}

	@Override
	public void beforeNavigateTo(String url, WebDriver driver) {
		handleUserAction("beforeNavigateTo");
	}

	@Override
	public void afterNavigateTo(String url, WebDriver driver) {
		handleUserAction("afterNavigateTo");
	}

	@Override
	public void beforeNavigateBack(WebDriver driver) {
		handleUserAction("beforeNavigateBack");
	}

	@Override
	public void afterNavigateBack(WebDriver driver) {
		handleUserAction("afterNavigateBack");
	}

	@Override
	public void beforeNavigateForward(WebDriver driver) {
		handleUserAction("beforeNavigateForward");
	}

	@Override
	public void afterNavigateForward(WebDriver driver) {
		handleUserAction("afterNavigateForward");
	}

	@Override
	public void beforeNavigateRefresh(WebDriver driver) {
		handleUserAction("beforeNavigateRefresh");
	}

	@Override
	public void afterNavigateRefresh(WebDriver driver) {
		handleUserAction("afterNavigateRefresh");
	}

	@Override
	public void beforeFindBy(By by, WebElement element, WebDriver driver) {
	}

	@Override
	public void afterFindBy(By by, WebElement element, WebDriver driver) {
	}

	@Override
	public void beforeClickOn(WebElement element, WebDriver driver) {
		handleUserAction("beforeClickOn");
	}

	@Override
	public void afterClickOn(WebElement element, WebDriver driver) {
		handleUserAction("afterClickOn");
	}

	@Override
	public void beforeChangeValueOf(WebElement element, WebDriver driver, CharSequence[] keysToSend) {
	}

	@Override
	public void afterChangeValueOf(WebElement element, WebDriver driver, CharSequence[] keysToSend) {
	}

	@Override
	public void beforeScript(String script, WebDriver driver) {
	}

	@Override
	public void afterScript(String script, WebDriver driver) {
	}

	@Override
	public void beforeSwitchToWindow(String windowName, WebDriver driver) {
		handleUserAction("beforeSwitchToWindow");
	}

	@Override
	public void afterSwitchToWindow(String windowName, WebDriver driver) {
		handleUserAction("afterSwitchToWindow");
	}

	@Override
	public void onException(Throwable throwable, WebDriver driver) {
	}

	@Override
	public <X> void beforeGetScreenshotAs(OutputType<X> target) {
	}

	@Override
	public <X> void afterGetScreenshotAs(OutputType<X> target, X screenshot) {
	}

	@Override
	public void beforeGetText(WebElement element, WebDriver driver) {
	}

	@Override
	public void afterGetText(WebElement element, WebDriver driver, String text) {
	}
}
