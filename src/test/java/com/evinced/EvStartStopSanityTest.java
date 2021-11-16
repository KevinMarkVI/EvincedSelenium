package com.evinced;

import com.evinced.dto.results.Issue;
import com.evinced.dto.results.Report;
import com.evinced.exceptions.EvincedRuntimeException;
import com.evinced.utils.ChromeOptionsHelper;
import com.evinced.utils.FileUtils;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.*;
import org.junit.rules.TestName;
import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.List;

import static org.junit.Assert.*;

public class EvStartStopSanityTest {
	private final String page1 = FileUtils.getRelativePath("site1.html");
	private final String page2 = FileUtils.getRelativePath("site2.html");
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

	@Test
	public void recordingTestOnASinglePage() {
		driver.get(railpassPage);
		driver.evStart();

		// interacting with the page - opening a dropdown
		driver.findElement(By.className("input__travellers")).click();

		// RUN VALIDATIONS
		Report report = driver.evStop();
		List<Issue> issues = report.getIssues();

		assertTrue("Recording session should yield more results that runOnce session", issues.size() > 20);
	}

	@Test
	public void recordingTestWithMultipleSites() {
		driver.get(page1);
		driver.evStart();
		driver.get(page2);

		// RUN VALIDATIONS
		Report report = driver.evStop();
		List<Issue> issues = report.getIssues();

		assertTrue("Issue should arrive from page " + page1, issues.stream().anyMatch(issue -> issue.getElements().stream().anyMatch(issueElement -> issueElement.getPageUrl().contains(page1))));
		assertTrue("Issue should arrive from page " + page2, issues.stream().anyMatch(issue -> issue.getElements().stream().anyMatch(issueElement -> issueElement.getPageUrl().contains(page2))));
	}

	@Test
	public void startShouldRecordeValidationWhenNavigatingBetweenPages() {

		driver.get(page1);

		// RUN VALIDATIONS
		driver.evStart();
		driver.get("https://www.google.com");
		driver.get("https://www.simpleweb.org");
		Report report = driver.evStop();
		List<Issue> issues = report.getIssues();
		assertNotNull(issues);
	}

	@Test
	public void recordingWithMultipleStartStops() {
		driver.get(page1);
		driver.evStart();
		Report firstStopReport = driver.evStop();
		assertEquals(2, firstStopReport.getIssues().size());

		// adding an inaccessible image tag
		driver.executeScript("document.body.append(document.createElement('img'))");
		driver.evStart();
		Report secondStopReport = driver.evStop();
		assertEquals(3, secondStopReport.getIssues().size());

		// refreshing and checking the same page, this time without the extra image
		driver.get(page1);
		driver.evStart();
		Report thirdStopReport = driver.evStop();

		// we should get 2 elements, ignoring what happened after last evStop
		assertEquals(2, thirdStopReport.getIssues().size());
	}

	@Test
	public void startShouldFailWhenRunMoreThanOnceSimultaneously() {
		try {
			driver.evStart();
			driver.evStart();
		} catch (EvincedRuntimeException e) {
			assertEquals("Running another `evStart` command during a run is not supported.", e.getMessage());
			return;
		}
		fail("An exception should have been thrown");
	}

	@Test
	public void stopShouldFailWhenRunningBeforeEvStart() {
		driver.get(page1);

		// RUN VALIDATIONS
		driver.evStart();
		try {
			driver.evStart();
		} catch (EvincedRuntimeException e) {
			assertEquals("Running another `evStart` command during a run is not supported.", e.getMessage());
			return;
		}
		fail("An exception should have been thrown");
	}
}