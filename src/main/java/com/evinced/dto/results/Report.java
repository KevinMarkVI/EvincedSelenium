package com.evinced.dto.results;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class Report {
	List<Issue> issues;

	public Report(List<Issue> issues) {
		this.issues = issues;
	}

	public Report(HashMap<String, AnalysisResult> accessibilityReports) {
		this.issues = accessibilityReports.values().stream().map(AnalysisResult::getReport).flatMap(Collection::stream)
				.collect(Collectors.toList());
	}

	public List<Issue> getIssues() {
		return issues;
	}

	public void setIssues(List<Issue> issues) {
		this.issues = issues;
	}
}
