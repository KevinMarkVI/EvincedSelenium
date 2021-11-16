package com.evinced.utils;

import org.openqa.selenium.chrome.ChromeOptions;

public class ChromeOptionsHelper {
	public static ChromeOptions getHeadlessOptionsConfiguration() {
		ChromeOptions chromeOptions = new ChromeOptions();
		chromeOptions.addArguments("--headless");
		chromeOptions.addArguments("--disable-gpu");
		chromeOptions.addArguments("--window-size=1920,1080");
		return chromeOptions;
	}
}
