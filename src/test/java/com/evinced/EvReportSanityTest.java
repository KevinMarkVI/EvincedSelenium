package com.evinced;

import com.evinced.dto.configuration.EvincedConfiguration;
import com.evinced.dto.results.Issue;
import com.evinced.dto.results.Report;
import com.evinced.utils.ChromeOptionsHelper;
import com.evinced.utils.EvReportHelper;
import com.evinced.utils.FileUtils;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.*;
import org.junit.rules.TestName;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class EvReportSanityTest {
	private final String inaccessibleDemoPage = FileUtils.getRelativePath("example-inaccessible-page.html");
	private final String page1 = FileUtils.getRelativePath("site1.html");
	private final String railpassPage = "https://www.railpass.com";

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

	/**
	 * Ensure we close the WebDriver after finishing
	 */
	@After
	public void tearDown() {
		driver.quit();
	}

	/**
	 * Basic test
	 */

	@Test
	public void evReportSimpleTest() {
		driver.get(page1);
		Report report = driver.evReport();
		List<Issue> issues = report.getIssues();
		assertEquals(2, issues.size());
	}

	@Test
	public void evReportWithSeveralIssuesTest() {
		driver.get(inaccessibleDemoPage);
		Report report = driver.evReport();
		List<Issue> issues = report.getIssues();

		assertEquals(50, issues.size());

		// validate types
		assertEquals(45, EvReportHelper.getIssuesByType(issues, "AXE-COLOR-CONTRAST").size());
		assertEquals(1, EvReportHelper.getIssuesByType(issues, "AXE-FRAME-TITLE").size());
		assertEquals(1, EvReportHelper.getIssuesByType(issues, "AXE-HTML-HAS-LANG").size());
		assertEquals(1, EvReportHelper.getIssuesByType(issues, "AXE-IMAGE-ALT").size());
		assertEquals(1, EvReportHelper.getIssuesByType(issues, "AXE-LABEL").size());
		assertEquals(1, EvReportHelper.getIssuesByType(issues, "AXE-LINK-NAME").size());
	}

	@Test
	public void railpassRunOnceSanityTest() {
		driver.get(railpassPage);

		EvincedConfiguration configuration = new EvincedConfiguration();

		// RUN VALIDATIONS
		Report report = driver.evReport(configuration);
		List<Issue> issues = report.getIssues();

		System.out.println("number of results = " + issues.size());
		assertTrue("report has 24 or 27 issues", issues.size() == 24 || issues.size() == 27);
	}
}