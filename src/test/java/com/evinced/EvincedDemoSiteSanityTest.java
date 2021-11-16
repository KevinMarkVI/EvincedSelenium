package com.evinced;

import com.evinced.dto.results.Issue;
import com.evinced.dto.results.Report;
import com.evinced.utils.ChromeOptionsHelper;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.*;
import org.junit.rules.TestName;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.List;

public class EvincedDemoSiteSanityTest {

	public static final String evincedDemoPage = "https://demo.evinced.com/";

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
		// write report to files
		// To think - do we want it to be in the driver or as a utility?
		driver.quit();
	}

	@Test
	public void demoWithRecording() {
		driver.evStart();

		driver.get(evincedDemoPage);

		// interacting with the page - opening all dropdown
		WebElement firstDropdown = driver.findElement(By.cssSelector("div.filter-container > div:nth-child(1) > div > div.dropdown.line"));
		firstDropdown.click();
		System.out.println("Clicked on first dropdown");

		WebElement secondDropdown = driver.findElement(By.cssSelector("div.filter-container > div:nth-child(2) > div > div.dropdown.line"));
		secondDropdown.click();
		System.out.println("Clicked on second dropdown");

		driver.findElement(By.cssSelector(".react-date-picker")).click();
		System.out.println("Clicked on third dropdown");

		Report report = driver.evStop();
		List<Issue> issues = report.getIssues();
		Assert.assertEquals(11, issues.size());
		EvincedReporter.writeEvResultsToFile(testName.getMethodName(), report, EvincedReporter.FileFormat.HTML);
		EvincedReporter.writeEvResultsToFile(testName.getMethodName(), report, EvincedReporter.FileFormat.JSON);
	}

	@Test
	public void evReportWithAnOpenDropdownTest() {

		driver.get(evincedDemoPage);

		// interacting with the page - opening all dropdown
		WebElement firstDropdown = driver.findElement(By.cssSelector("div.filter-container > div:nth-child(1) > div > div.dropdown.line"));
		firstDropdown.click();

		Report report = driver.evReport();
		List<Issue> issues = report.getIssues();
		Assert.assertTrue(issues.size() < 9);

		// write the report to HTML format to a file named: "test-results.html"
		EvincedReporter.writeEvResultsToFile("test-results", report, EvincedReporter.FileFormat.HTML);
	}
}