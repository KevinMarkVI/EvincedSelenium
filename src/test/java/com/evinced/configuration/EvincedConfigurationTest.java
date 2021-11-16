package com.evinced.configuration;

import com.evinced.EvincedWebDriver;
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

public class EvincedConfigurationTest {
	private final String pageWithRootSelector = FileUtils.getRelativePath("site3-rootselector.html");
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

	/**
	 * Ensure we close the WebDriver after finishing
	 */
	@After
	public void tearDown() {
		driver.quit();
	}

	@Test
	public void rootSelectorConfigurationEvStartTest() {
		driver.get(pageWithRootSelector);

		// use global default config
		driver.evStart();
		Report report = driver.evStop();
		List<Issue> issues = report.getIssues();
		assertEquals(4, issues.size());

		// run a single test with configuration
		EvincedConfiguration configuration = new EvincedConfiguration();
		configuration.setRootSelector(".subtree-selector");
		driver.evStart(configuration);
		report = driver.evStop();
		issues = report.getIssues();
		assertEquals(1, issues.size());

		// run evreport again with no config, should use the default ones
		driver.evStart();
		report = driver.evStop();
		issues = report.getIssues();
		assertEquals(4, issues.size());
	}


	@Test
	public void rootSelectorConfigurationEvReportTest() {
		driver.get(pageWithRootSelector);

		// run evReport on entire page
		Report report = driver.evReport();
		List<Issue> issues = report.getIssues();
		assertEquals(4, issues.size());

		// run evReport on a selector
		EvincedConfiguration configuration = new EvincedConfiguration();
		configuration.setRootSelector(".subtree-selector");
		Report reportForRootSelector = driver.evReport(configuration);
		List<Issue> issuesForRootSelector = reportForRootSelector.getIssues();
		assertEquals(1, issuesForRootSelector.size());
		assertEquals(1, EvReportHelper.getIssuesByType(issuesForRootSelector, "AXE-IMAGE-ALT").size());
	}

	@Test
	public void rootSelectorConstructurConfigurationEvReportTest() {
		EvincedConfiguration configuration = new EvincedConfiguration();
		configuration.setRootSelector(".subtree-selector");
		// passing axe config on constructor
		ChromeOptions chromeOptions = ChromeOptionsHelper.getHeadlessOptionsConfiguration();
		EvincedWebDriver driver2 = null;
		try {
			// a new driver is needed to avoid collision between tests
			driver2 = new EvincedWebDriver(new ChromeDriver(chromeOptions), configuration);
			driver2.get(pageWithRootSelector);

			// run evReport on a selector
			Report reportForRootSelector = driver2.evReport();
			List<Issue> issuesForRootSelector = reportForRootSelector.getIssues();
			assertEquals(1, issuesForRootSelector.size());
			assertEquals(1, EvReportHelper.getIssuesByType(issuesForRootSelector, "AXE-IMAGE-ALT").size());
		} finally {
			driver2.quit();
		}
	}

	@Test
	public void evReportWithEmptyConfigurationObjectTest() {
		driver.get(page1);
		Report report = driver.evReport(new EvincedConfiguration());
		List<Issue> issues = report.getIssues();
		assertEquals(2, issues.size());
	}
}