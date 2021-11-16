package com.evinced;

import com.deque.html.axecore.results.Results;
import com.deque.html.axecore.selenium.AxeReporter;
import com.evinced.dto.results.Issue;
import com.evinced.dto.results.Report;
import com.evinced.utils.ChromeOptionsHelper;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.json.JSONObject;
import org.junit.*;
import org.junit.rules.TestName;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import com.deque.html.axecore.selenium.AxeBuilder;
import org.openqa.selenium.WebDriver;



import java.util.List;

public class DemoTest {

    public static final String demoPage = "https://demo.evinced.com/";

    @Rule
    public TestName testName = new TestName();

    private EvincedWebDriver driver;
    private WebDriver webDriver;

    @BeforeClass
    public static void setupClass() {
        WebDriverManager.chromedriver().setup();
    }

    @Before
    public void setUp() {

        driver = new EvincedWebDriver(new ChromeDriver());
        driver.manage().window().maximize();

        // Start the Evinced Engine
        driver.evStart();
    }

    @After
    public void tearDown() {
        // Stop the Evinced Engine
        Report report = driver.evStop();

        // Optional assertion for gating purposes
        //List<Issue> issues = report.getIssues();
        //Assert.assertEquals(8, issues.size());

        // Output the Accessibility results in JSON or HTML
        EvincedReporter.writeEvResultsToFile(testName.getMethodName(), report, EvincedReporter.FileFormat.HTML);
        EvincedReporter.writeEvResultsToFile(testName.getMethodName(), report, EvincedReporter.FileFormat.JSON);

        driver.quit();
    }

    @Test
    public void someApplicationExistingTest() {
        driver.get(demoPage);
        // interacting with the page - opening all dropdowns
        WebElement firstDropdown = driver.findElement(By.cssSelector("div.filter-container > div:nth-child(1) > div > div.dropdown.line"));
        firstDropdown.click();
        System.out.println("Clicked on first dropdown");

        WebElement secondDropdown = driver.findElement(By.cssSelector("div.filter-container > div:nth-child(2) > div > div.dropdown.line"));
        secondDropdown.click();
        System.out.println("Clicked on second dropdown");

        driver.findElement(By.cssSelector(".react-date-picker")).click();
        System.out.println("Clicked on third dropdown");
    }

//    @Test
//    public void testAxeOne(){
//        driver.get(demoPage);
//
//        Results result = new AxeBuilder().analyze(driver);
//        List<com.deque.html.axecore.results.Rule> violations = result.getViolations();
//        System.out.println("VIOLATIONS FOUND: " + violations.size());
//        if (violations.size() > 0)
//        {
//            AxeReporter.writeResultsToJsonFile("/Users/kevinberg/desktop/Axe", result);
//        }
//        // More test code...
//    }
}















// Run with: mvn test -Dtest=DemoTest
