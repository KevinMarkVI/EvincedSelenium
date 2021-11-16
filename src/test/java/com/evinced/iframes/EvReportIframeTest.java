package com.evinced.iframes;

import com.evinced.EvincedWebDriver;
import com.evinced.dto.configuration.EvincedConfiguration;
import com.evinced.dto.results.Issue;
import com.evinced.dto.results.Report;
import com.evinced.utils.ChromeOptionsHelper;
import com.evinced.utils.FileUtils;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.*;
import org.junit.rules.TestName;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.List;

import static org.junit.Assert.assertEquals;

@Ignore
public class EvReportIframeTest {
	private final String iframeWithFourLevelsWithIssues = FileUtils.getRelativePath("iframes/with-issues/four-levels-nested-iframes.html");
	private final String iframeLevel1 = FileUtils.getRelativePath("iframes/with-issues/foo.html");
	private final String iframeLevel2 = FileUtils.getRelativePath("iframes/with-issues/bar.html");
	private final String iframeLevel3 = FileUtils.getRelativePath("iframes/with-issues/baz.html");

	private final String iframeWithFourLevelsAndNoIssues = FileUtils.getRelativePath("iframes/no-issues/four-levels-nested-iframes.html");
	private final String iframeLevel1NoIssues = FileUtils.getRelativePath("iframes/no-issues/foo.html");
	private final String iframeLevel2NoIssues = FileUtils.getRelativePath("iframes/no-issues/bar.html");
	private final String iframeLevel3NoIssues = FileUtils.getRelativePath("iframes/no-issues/baz.html");

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

	private void assertVEvReportValidationCount(String url, long expected) {
		driver.get(url);
		Report report = driver.evReport();
		List<Issue> issues = report.getIssues();
		assertEquals(expected, issues.size());
	}

	@Test
	public void evReportWithIframesEnabled() {
		// check each page separately
		// parent
		assertVEvReportValidationCount(iframeWithFourLevelsWithIssues, 2);

		// iframe level 1
		assertVEvReportValidationCount(iframeLevel1, 3);

		// iframe level 2
		assertVEvReportValidationCount(iframeLevel2, 2);

		// iframe level 3
		assertVEvReportValidationCount(iframeLevel3, 2);

		// run validation on parent with iframes config turned on
		driver.get(iframeWithFourLevelsWithIssues);
		EvincedConfiguration configuration = new EvincedConfiguration();
		configuration.setIncludeIframes(true);
		Report report = driver.evReport(configuration);
		List<Issue> issues = report.getIssues();

		assertEquals("Number of issues in all iframes should be the same as their sum", 9, issues.size());
	}

	@Test
	public void evReportWithIframesEnabledNoIssues() {
		// check each page separately
		// parent
		assertVEvReportValidationCount(iframeWithFourLevelsAndNoIssues, 0);

		// iframe level 1
		assertVEvReportValidationCount(iframeLevel1NoIssues, 0);

		// iframe level 2
		assertVEvReportValidationCount(iframeLevel2NoIssues, 0);

		// iframe level 3
		assertVEvReportValidationCount(iframeLevel3NoIssues, 0);

		// run validation on parent with iframes config turned on
		driver.get(iframeWithFourLevelsAndNoIssues);
		EvincedConfiguration configuration = new EvincedConfiguration();
		configuration.setIncludeIframes(true);
		Report report = driver.evReport(configuration);
		List<Issue> issues = report.getIssues();

		assertEquals("Number of issues in all iframes should be the same as their sum", 0, issues.size());
	}


}