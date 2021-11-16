package com.evinced;

import com.evinced.dto.results.Issue;
import com.evinced.dto.results.IssueElement;
import com.evinced.dto.results.Report;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.escape.Escaper;
import com.google.common.html.HtmlEscapers;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class EvincedReporter {
	private static final String EVINCED_REPORTS_PATH = "evinced-report";
	private static final URL TABLE_TEMPLATE_URL = EvincedReporter.class.getResource("/templates/html-report-template.html");
	private static final String TABLE_CONTENT_PLACEHOLDER = "TABLE_CONTENT_PLACEHOLDER";
	private static final String TABLE_ROW_TEMPLATE = "<tr><td>%s</td><td class='url'>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td><td><a href='%s' target='_blank' rel='noopener noreferrer'>Open in a new tab</a></td></tr>\n";
	private static final Escaper htmlEscaper = HtmlEscapers.htmlEscaper();
	private static final ObjectMapper objectMapper = new ObjectMapper();
	private static final Logger logger = LogManager.getLogger(EvincedReporter.class);

	/**
	 * Writes the accessibility report to a file
	 *
	 * @param filename Name of the results file to create, for example: "results.html" or "results.json"
	 * @param report   Accessibility scan results
	 * @param format   HTML or JSON
	 * @return Path to generated report file, null if the report generation failed
	 */
	public static Path writeEvResultsToFile(String filename, final Report report, FileFormat format) {
		if (format == FileFormat.HTML) {
			return writeHtmlResults(filename, report);
		} else {
			return writeResults(filename, report);
		}
	}

	private static Path writeToFile(String filename, String contents) {
		try {
			File directory = new File(EVINCED_REPORTS_PATH);
			if (!directory.exists()) {
				boolean isDirectoryCreated = directory.mkdir();
				if (!isDirectoryCreated) {
					logger.error("Error creating the reports directory");
					return null;
				}
			}
			Path generatedReportPath = Paths.get(EVINCED_REPORTS_PATH, filename);
			Files.write(generatedReportPath, contents.getBytes(StandardCharsets.UTF_8));
			logger.debug("Report created at " + generatedReportPath);
			return generatedReportPath;
		} catch (Exception e) {
			logger.error("Error writing the report to file system", e);
			return null;
		}
	}

	/**
	 * Writes a raw object out to a JSON file with the specified name.
	 *
	 * @param name   Desired filename.
	 * @param report Report to print to file.
	 */
	private static Path writeResults(final String name, final Report report) {
		logger.debug("Generating a JSON report");
		List<Issue> issues = report.getIssues();
		try {
			return writeToFile(name + ".json", objectMapper.writeValueAsString(issues));
		} catch (JsonProcessingException e) {
			logger.error("Error writing JSON report to file system", e);
			return null;
		}
	}

	/**
	 * Writes a raw object out to a JSON file with the specified name.
	 *
	 * @param name   Desired filename
	 * @param report Report to print to file.
	 */
	private static Path writeHtmlResults(final String name, final Report report) {
		logger.debug("Generating an HTML report");
		List<Issue> issues = report.getIssues();
		StringBuilder tableString = new StringBuilder();
		for (int i = 0; i < issues.size(); i++) {
			Issue issue = issues.get(i);
			int index = i + 1; // start the count from 1, not 0
			IssueElement firstElement = issue.getElements().get(0);
			String url = firstElement.getPageUrl();
			String type = issue.getType().getName();
			String selector = firstElement.getSelector();
			String severity = issue.getSeverity().getName();
			String elementTemplateSignature = firstElement.getComponentId();
			String summary = issue.getSummary();
			String knowledgeBaseLink = issue.getKnowledgeBaseLink();

			tableString.append(String.format(TABLE_ROW_TEMPLATE, index, url, type, selector, severity, elementTemplateSignature, htmlEscaper.escape(summary), knowledgeBaseLink));
		}
		try {
			String tableTemplate = IOUtils.toString(TABLE_TEMPLATE_URL, StandardCharsets.UTF_8);
			String reportWithTableContent = tableTemplate.replace(TABLE_CONTENT_PLACEHOLDER, tableString.toString());
			return writeToFile(name + ".html", reportWithTableContent);
		} catch (Exception e) {
			logger.error("Error reading the HTML report template file", e);
			return null;
		}
	}

	public enum FileFormat {
		JSON, HTML
	}

}
