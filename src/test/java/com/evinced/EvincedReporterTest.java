package com.evinced;

import com.evinced.dto.results.Issue;
import com.evinced.dto.results.Report;
import com.evinced.utils.ChromeOptionsHelper;
import com.evinced.utils.FileUtils;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.*;
import org.junit.rules.TestName;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class EvincedReporterTest {
	private final String page1 = FileUtils.getRelativePath("site1.html");
	@Rule
	public TestName testName = new TestName();
	private EvincedWebDriver driver;

	@BeforeClass
	public static void setupClass() {
		WebDriverManager.chromedriver().setup();
	}

	/**
	 * Instantiate the WebDriver and navigate to the test site.
	 */
	@Before
	public void setUp() {
		ChromeOptions chromeOptions = ChromeOptionsHelper.getHeadlessOptionsConfiguration();
		driver = new EvincedWebDriver(new ChromeDriver(chromeOptions));
		driver.manage().window().maximize();
	}

	private void assertFileExistsAndDelete(Path file, String message) {
		assertTrue(message, Files.exists(file));
		try {
			// cleanup
			Files.deleteIfExists(file);
		} catch (Exception ignored) {
		}
	}

	/**
	 * Ensure we close the WebDriver after finishing
	 */
	@After
	public void tearDown() {
		driver.quit();
	}

	@Test
	public void htmlReportCreationAfterRecordingTest() {
		driver.get(page1);
		driver.evStart();
		// RUN VALIDATIONS
		Report report = driver.evStop();
		List<Issue> issues = report.getIssues();
		assertTrue("Issue should arrive from page " + page1, issues.stream().anyMatch(issue -> issue.getElements().stream().anyMatch(issueElement -> issueElement.getPageUrl().contains(page1))));
		Path htmlReport = EvincedReporter.writeEvResultsToFile("htmlReport" + Math.random(), report, EvincedReporter.FileFormat.HTML);
		Path jsonReport = EvincedReporter.writeEvResultsToFile("jsonReport" + Math.random(), report, EvincedReporter.FileFormat.JSON);

		assertFileExistsAndDelete(htmlReport, "HTML report should exist in file system");
		assertFileExistsAndDelete(jsonReport, "JSON report should exist in file system");
	}

	@Test
	public void htmlReportCreationAfterEvReportTest() {
		driver.get(page1);
		Report report = driver.evReport();
		List<Issue> issues = report.getIssues();
		assertTrue("Issue should arrive from page " + page1, issues.stream().anyMatch(issue -> issue.getElements().stream().anyMatch(issueElement -> issueElement.getPageUrl().contains(page1))));
		Path htmlReport = EvincedReporter.writeEvResultsToFile("htmlReport" + Math.random(), report, EvincedReporter.FileFormat.HTML);
		Path jsonReport = EvincedReporter.writeEvResultsToFile("jsonReport" + Math.random(), report, EvincedReporter.FileFormat.JSON);

		assertFileExistsAndDelete(htmlReport, "HTML report should exist in file system");
		assertFileExistsAndDelete(jsonReport, "JSON report should exist in file system");
	}
}
