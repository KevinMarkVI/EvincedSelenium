package com.evinced.configuration;

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

public class EvincedDefaultConfigurationTest {
	private final String pageWithRootSelector = FileUtils.getRelativePath("site3-rootselector.html");

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
		EvincedConfiguration configuration = new EvincedConfiguration();
		configuration.setRootSelector(".subtree-selector");
		driver = new EvincedWebDriver(new ChromeDriver(chromeOptions), configuration);
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
	public void overrideDefaultConfigEvReportTest() {
		// run a single test with configuration (note the configuration on browser init in this file)
		driver.get(pageWithRootSelector);
		Report report = driver.evReport();
		List<Issue> issues = report.getIssues();
		assertEquals(1, issues.size());

		// run evreport again with no config, should use the default ones
		report = driver.evReport(new EvincedConfiguration());
		issues = report.getIssues();
		assertEquals(4, issues.size());

		// running again without passing config, should use the defaults passed on init
		report = driver.evReport();
		issues = report.getIssues();
		assertEquals(1, issues.size());
	}

	@Test
	public void overrideDefaultConfigEvStartTest() {
		// run a single test with configuration (note the configuration on browser init in this file)
		driver.get(pageWithRootSelector);
		driver.evStart();
		Report report = driver.evStop();
		List<Issue> issues = report.getIssues();
		assertEquals(1, issues.size());

		// run evreport again with no config, should use the default ones
		driver.evStart(new EvincedConfiguration());
		report = driver.evStop();
		issues = report.getIssues();
		assertEquals(4, issues.size());

		// running again without passing config, should use the defaults passed on init
		driver.evStart();
		report = driver.evStop();
		issues = report.getIssues();
		assertEquals(1, issues.size());
	}
}