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
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.List;

import static org.junit.Assert.assertEquals;

@Ignore
public class DynamicallyAddedIframeTest {
	private final String dynamicIframesGenerationPage = FileUtils.getRelativePath("iframes/dynamic-iframes/index.html");

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
	public void dynamicallyAddedIframeEvStartTest() {
		driver.get(dynamicIframesGenerationPage);

		// check initial accessibility state
		Report report = driver.evReport();
		List<Issue> issues = report.getIssues();
		assertEquals(1, issues.size());

		EvincedConfiguration configuration = new EvincedConfiguration();
		configuration.setIncludeIframes(true);
		driver.evStart(configuration);

		// clicking on an element that creates an iframe
		driver.findElement(new By.ById("add-btn")).click();

		// expect evStop to collect issues from the child iframe as well
		report = driver.evStop();
		issues = report.getIssues();
		assertEquals(6, issues.size());

	}
}