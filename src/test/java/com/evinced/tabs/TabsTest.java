package com.evinced.tabs;

import com.evinced.EvincedWebDriver;
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

public class TabsTest {
	private final String pageThatOpensTabs = FileUtils.getRelativePath("tabs/index.html");

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
//		driver = new EvincedWebDriver(new ChromeDriver());
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
	public void validatingTabsUsingEvStart() {
		driver.get(pageThatOpensTabs);
		driver.evStart();

		// open a new tab
		driver.findElement(new By.ById("open-tab-link")).click();

		// important! the evStart and evStop scope are limited to a window handle.
		// this means that if you have more than one tab, you need to switch it's window and only then run `evStart` and `evStop`
		// The `evStop` must be called on the tab that `evStart` command was called on, before starting `evStart` on a new tab
		Report report = driver.evStop();
		List<Issue> issues = report.getIssues();
		assertEquals(1, issues.size());

		// change Selenium context to the new tab
		String tabWindowHandle = driver.getWindowHandles().toArray()[1].toString();
		driver.switchTo().window(tabWindowHandle);

		// get report on the new tab
		driver.evStart();
		report = driver.evStop();
		issues = report.getIssues();
		assertEquals(3, issues.size());
	}


	@Test
	public void validatingTabsUsingEvReport() {
		driver.get(pageThatOpensTabs);

		// get report for index page
		Report report = driver.evReport();
		List<Issue> issues = report.getIssues();
		assertEquals(1, issues.size());

		driver.findElement(new By.ById("open-tab-link")).click();

		String tabWindowHandle = driver.getWindowHandles().toArray()[1].toString();

		// move to tab
		driver.switchTo().window(tabWindowHandle);

		// get report on the new tab
		report = driver.evReport();
		issues = report.getIssues();
		assertEquals(3, issues.size());
	}
}