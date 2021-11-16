package com.evinced.dto.init;

/**
 * Analysis Config part in init-configuration
 */
public class AnalysisConfig {
	private String analysisId;
	private String rootSelector;

	public String getAnalysisId() {
		return analysisId;
	}

	public void setAnalysisId(String analysisId) {
		this.analysisId = analysisId;
	}

	public String getRootSelector() {
		return rootSelector;
	}

	public void setRootSelector(String rootSelector) {
		this.rootSelector = rootSelector;
	}
}
