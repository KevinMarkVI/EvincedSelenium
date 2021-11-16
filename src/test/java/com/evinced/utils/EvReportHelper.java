package com.evinced.utils;

import com.evinced.dto.results.Issue;

import java.util.List;
import java.util.stream.Collectors;

public class EvReportHelper {
	public static List<Issue> getIssuesByType(List<Issue> issues, String type) {
		return issues.stream().filter(r -> r.getType().getId().equals(type)).collect(Collectors.toList());
	}
}
