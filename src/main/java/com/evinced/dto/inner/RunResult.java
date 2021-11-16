package com.evinced.dto.inner;

import com.evinced.dto.results.AnalysisResult;

/**
 * inner structure of selenium execute async script
 */
public class RunResult {
	private String error;
	private AnalysisResult results;

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public AnalysisResult getResults() {
		return results;
	}

	public void setResults(AnalysisResult results) {
		this.results = results;
	}
}
