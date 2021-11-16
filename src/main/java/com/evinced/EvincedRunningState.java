package com.evinced;

import com.evinced.dto.init.AnalysisPersistedData;
import com.evinced.dto.results.AnalysisResult;

import java.util.HashMap;

public class EvincedRunningState {
	private HashMap<String, AnalysisResult> accessibilityReports = new HashMap<>();
	private boolean isRunning;

	public void addAccessibilityReportsToResults(String url, AnalysisResult result) {
		if (result != null) {
			// overriding previous result for that URL.
			accessibilityReports.put(url, result);
		}
	}

	public HashMap<String, AnalysisResult> getAccessibilityReports() {
		return accessibilityReports;
	}

	public void setAccessibilityReports(HashMap<String, AnalysisResult> reports) {
		this.accessibilityReports = reports;
	}

	public boolean isRunning() {
		return isRunning;
	}

	public void setRunning(boolean running) {
		isRunning = running;
	}

	public AnalysisPersistedData getUrlPersistedData(String url) {
		AnalysisResult previousReportForUrl = getAccessibilityReports().get(url);
		if (previousReportForUrl == null) {
			return null;
		}
		return new AnalysisPersistedData(previousReportForUrl);
	}
}
