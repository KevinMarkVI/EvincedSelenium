package com.evinced.dto.results;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AnalysisResult {
	private String analysisId;
	private List<Component> components;
	private List<Object> validations;
	private List<Issue> report;

	public String getAnalysisId() {
		return analysisId;
	}

	public void setAnalysisId(String analysisId) {
		this.analysisId = analysisId;
	}

	public List<Component> getComponents() {
		return components;
	}

	public void setComponents(List<Component> components) {
		this.components = components;
	}

	public List<Object> getValidations() {
		return validations;
	}

	public void setValidations(List<Object> validations) {
		this.validations = validations;
	}

	public List<Issue> getReport() {
		return report;
	}

	public void setReport(List<Issue> report) {
		this.report = report;
	}

}
