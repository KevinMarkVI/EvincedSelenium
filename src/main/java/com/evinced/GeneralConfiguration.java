package com.evinced;

import java.util.Collections;
import java.util.List;

public class GeneralConfiguration {

	public static List<String> getUrlsToIgnore() {
		// data:, is the opening URL when opening a new ChromeDriver in selenium, and it has a11y issues
		return Collections.singletonList("data:,");
	}
}
