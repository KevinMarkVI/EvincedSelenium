package com.evinced.configuration;

import com.evinced.EvincedWebDriver;
import com.evinced.dto.configuration.AxeConfiguration;
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

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class AxeConfigurationTest {
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

	/**
	 * Basic test
	 */

	@Test
	public void passingConfigOnEvReportTest() {
		// running evReport without axe excludes
		driver.get(page1);
		Report report = driver.evReport();
		List<Issue> issues = report.getIssues();
		assertEquals("Page should have 2 issues", 2, issues.size());

		// running evReport with axe excludes
		EvincedConfiguration configuration = new EvincedConfiguration();
		AxeConfiguration axeConfig = new AxeConfiguration();
		axeConfig.setRules(Collections.singletonMap("html-has-lang", Collections.singletonMap("enabled", false)));
		configuration.setAxeConfig(axeConfig);
		Report reportWithAxeConfig = driver.evReport(configuration);
		List<Issue> issuesWithAxeConfig = reportWithAxeConfig.getIssues();
		assertEquals("Page should have 1 issue - Axe config should filter out the html-has-lang rule", 1, issuesWithAxeConfig.size());
		assertEquals("Report should include only the image tag rule ", issuesWithAxeConfig.get(0).getType().getName().toLowerCase(), "image-alt");
	}

	@Test
	public void passingConfigOnConstructorTest() {
		EvincedConfiguration configuration = new EvincedConfiguration();
		AxeConfiguration axeConfig = new AxeConfiguration();
		axeConfig.setRules(Collections.singletonMap("html-has-lang", Collections.singletonMap("enabled", false)));
		configuration.setAxeConfig(axeConfig);

		// passing axe config on constructor
		ChromeOptions chromeOptions = ChromeOptionsHelper.getHeadlessOptionsConfiguration();
		EvincedWebDriver driver2 = null;
		try {
			// a new driver is needed to avoid collision between tests
			driver2 = new EvincedWebDriver(new ChromeDriver(chromeOptions), configuration);

			driver2.get(page1);
			Report report = driver2.evReport();
			List<Issue> issues = report.getIssues();
			assertEquals("Page should have 1 issue - Axe config should filter out the html-has-lang rule", 1, issues.size());
			assertEquals("Report should include only the image tag rule ", issues.get(0).getType().getName().toLowerCase(), "image-alt");
		} finally {
			driver2.quit();
		}
	}

	@Test
	public void passingConfigOnEvStartTest() {
		// running evReport without axe excludes
		driver.get(page1);
		driver.evStart();
		Report report = driver.evStop();
		List<Issue> issues = report.getIssues();
		assertEquals("Page should have 2 issues", 2, issues.size());

		// running evReport with axe excludes
		EvincedConfiguration configuration = new EvincedConfiguration();
		AxeConfiguration axeConfig = new AxeConfiguration();
		axeConfig.setRules(Collections.singletonMap("html-has-lang", Collections.singletonMap("enabled", false)));
		configuration.setAxeConfig(axeConfig);
		driver.evStart(configuration);
		Report reportWithAxeConfig = driver.evStop();
		List<Issue> issuesWithAxeConfig = reportWithAxeConfig.getIssues();
		assertEquals("Page should have 1 issue - Axe config should filter out the html-has-lang rule", 1, issuesWithAxeConfig.size());
		assertEquals("Report should include only the image tag rule ", issuesWithAxeConfig.get(0).getType().getName().toLowerCase(), "image-alt");
	}

	@Test
	public void changingConfigOnEvReportMultipleTimesTest() {
		// running evReport without axe excludes
		driver.get(page1);
		Report report = driver.evReport();
		List<Issue> issues = report.getIssues();
		assertEquals("Page should have 2 issues", 2, issues.size());

		// running evReport with axe excludes
		EvincedConfiguration configuration = new EvincedConfiguration();
		AxeConfiguration axeConfig = new AxeConfiguration();
		axeConfig.setRules(Collections.singletonMap("html-has-lang", Collections.singletonMap("enabled", false)));
		configuration.setAxeConfig(axeConfig);
		Report reportWithAxeConfig = driver.evReport(configuration);
		List<Issue> issuesWithAxeConfig = reportWithAxeConfig.getIssues();
		assertEquals("Page should have 1 issue - Axe config should filter out the html-has-lang rule", 1, issuesWithAxeConfig.size());
		assertEquals("Report should include only the image tag rule ", issuesWithAxeConfig.get(0).getType().getName().toLowerCase(), "image-alt");

		axeConfig.setRules(Collections.singletonMap("image-alt", Collections.singletonMap("enabled", false)));
		configuration.setAxeConfig(axeConfig);
		Report reportWithImageAltDisabled = driver.evReport(configuration);
		List<Issue> issuesWithoutAltImage = reportWithImageAltDisabled.getIssues();
		assertEquals("Page should have 1 issue - Axe config should filter out the image-alt rule", 1, issuesWithoutAltImage.size());
		assertEquals("Report should include only the html-has-lang rule ", issuesWithoutAltImage.get(0).getType().getName().toLowerCase(), "html-has-lang");
	}


}