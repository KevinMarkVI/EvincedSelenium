 
# Evinced Selenium SDK
Java Selenium API To find accessibility issues.

# Getting started
## Prerequisites
- Selenium version 3.141.59 or higher.
- Java version 1.8 or higher.
- Only ChromeDriver is supported at the moment.

## Setup
### Add dependency to your project
Make sure to replace `{latest-sdk-version}` with the latest version available.
To list all versions visit our JFrog repository here [link to JFROG repo](link to JFROG repo)

Working with Maven:
Add the following to the `<dependecies>` section
```xml
<dependency>
    <groupId>com.evinced</groupId>
    <artifactId>java-selenium</artifactId>
    <version>{latest-sdk-version}</version>
    <scope>test</scope>
</dependency>
```

Working with Gradle:
```groovy
    testCompile "com.evinced:selenium-sdk:{latest-sdk-version}"
```

## Your first test
This is a minimal working example, with line by line explanations.
In this example we are using JUnit.
```java
	@Test
	public void evReportSimpleTest() {
	    // Initilize EvincedWebDriver which wraps a ChromeDriver instance
		EvincedWebDriver driver = new EvincedWebDriver(new ChromeDriver());
		
		// Navigate to google
		driver.get("https://www.google.com");
		
		// Get the accessibility report
		Report report = driver.evReport();

		// Assert that there are no more than 10 accessibility violations
		assertTrue(report.getIssues().size() < 10);
	}
```

## Evinced entities
###`Report`
Accessibility test result object, holds a list of `Issue`s
### `Issue`
Each issue represents an accessibility violation.
An issue consists of:
* `id`, `signature` - unique identifiers of the issue.
* `summary` - a short human-readable description of the issue.
* `description` - a detailed human-readable description of the issue.
* `type` - violation's type. For example - `image-alt`.
* `severity` - violation's severity. For example - `critical`, `serious`, `minor`.
* `additionalInformation` - More information about the violation.
* `elements` - a list of DOM elements that are a part of the violation, including a dom snippet and CSS selector.
* `tags` - a list of WCAG rules this validation is related to.
* `knowledgeBaseLink` - a link to Evinced Knowledge Base with more information regarding the violation.

# API
## `EvincedWebDriver`
### `constructor`
#### Default constructor
`EvincedWebDriver` constructor expects an instance of `ChromeWebDriver`
It is possible to pass an instance of a class that inherits or extends ChromeWebDriver.
```java
ChromeDriver chromeDriver = new ChromeDriver();
EvincedWebDriver driver = new EvincedWebDriver(chromeDriver);
```

### Additional configuration
An optional second parameter is an `EvincedConfiguration`.
When configurations are passed via the constructor, they are set as a default for all future actions.

If other configurations are passed to a specific function, such as `evReport` they override the constructor configurations for that specific operation.

See the `EvincedConfiguration` options section for more details.
```java
ChromeDriver chromeDriver = new ChromeDriver();
EvincedConfiguration configuration = new EvincedConfiguration();
EvincedWebDriver driver = new EvincedWebDriver(chromeDriver, configuration);
```

### `evReport`
Scans the current page and returns a list of accessibility issues.
(not supported during `evStart`)
returns a Report object containing accessibility issues

```java
Report report = evincedWebDriver.evReport();
```
When EvincedConfiguration object is provided to the `evReport` method it will be used for this run only,
and not be saved for future runs.
```java
Report report = evincedWebDriver.evReport(configuration);
```

### `evStart`
Watches for DOM mutations and page navigation, recording all accessibility
issues until `evStop()` is called
```java
evincedWebDriver.evStart();
```
When EvincedConfiguration object is provided to the `evStart` method it will be used for this run only,
and not be saved for future runs.
```java
Report report = evStart.evReport(configuration);
```

### `evStop`
Returns a Report object containing accessibility issues recorded issues since the last call to `evStart`.
```java
Report report = evincedWebDriver.evStop();
```

### `setLogLevel`
Sets the log level for Evinced code, defaults to no logging.
```java
evincedWebDriver.setLogLevel(Level.ERROR); // or any other level such as Level.DEBUG
```

## EvincedReporter
### `writeEvResultsToFile`
Writes the accessibility report to a file.
Returns the `Path` to the created file.

Two formats are available - HTML and JSON.

```java
    Report report = evincedWebDriver.evStop();
     -- or --
    Report report = evincedWebDriver.evReport();

    // create a JSON file named jsonReport.json
    EvincedReporter.writeEvResultsToFile("jsonReport", report, FileFormat.JSON);

    // create an HTML file named htmlReport.html
    EvincedReporter.writeEvResultsToFile("htmlReport", report, FileFormat.HTML);

```

#### HTML report example
<img src="https://user-images.githubusercontent.com/60566974/123771814-085e5b80-d8d4-11eb-8bfc-feae2628a097.png" width="600" />

# Configuration
Use the `EvincedConfiguration` object to pass configurations to the accessibility engines.
When passing the configuration to the constructor it will be the default for future `evStart` and `evReport` commands.
```java
ChromeDriver chromeDriver = new ChromeDriver();
EvincedConfiguration configuration = new EvincedConfiguration();
EvincedWebDriver driver = new EvincedWebDriver(chromeDriver, configuration);
```

When passing the configuration to the `evStart` or `evReport` commands it will temporarily override the constructor configurations (replacing it, not merging).

## EvincedConfiguration options
### `rootSelector`
Defaults to null. Choose a single CSS selector to run the analysis,
For example - run analysis on the element that hold only the menu bar.
When no configuration is passed, it will scan the entire page.

```java
EvincedConfiguration configuration = new EvincedConfiguration();
configuration.setRootSelector(".some-selector"); // css selector
EvincedWebDriver driver = new EvincedWebDriver(chromeDriver, configuration);
```

### `axeConfig`
Pass configuration to Axe (some of the validations Evinced runs are based on Axe, and uses the same configuration).
For Axe config options: https://github.com/dequelabs/axe-core/blob/develop/doc/API.md#api-name-axeconfigure.

```java
EvincedConfiguration configuration = new EvincedConfiguration();
AxeConfiguration axeConfig = new AxeConfiguration();

// Axe's syntax to make `html-has-lang` validation disabled
axeConfig.setRules(Collections.singletonMap("html-has-lang", Collections.singletonMap("enabled", false)));
configuration.setAxeConfig(axeConfig);

// passing axe config on constructor
EvincedWebDriver driver = new EvincedWebDriver(chromeDriver, configuration);

-- or --
// passing axe config via evStart
EvincedWebDriver driver = new EvincedWebDriver(chromeDriver);
driver.evStart(configuration);
```

### `includeIframes`
When set to true, makes the accessibility tests run the analysis on iframes that exist inside the page.
Defaults to `false`.

```java
EvincedConfiguration configuration = new EvincedConfiguration();
configuration.setIncludeIframes(true);
driver.evStart(configuration);

// the report will include issues that exist on a page or in iframes inside it
Report report = driver.evStop();
```

# Tutorials
### Testing accessibility in a specific state of the application
In this example, we are going to test the accessibility of a page in a specific state.

This test navigates to a page, opens a dropdown, and only then runs the accessibility engines and finds issues.

```java
import com.evinced.EvincedReporter;
import com.evinced.EvincedWebDriver;
import com.evinced.dto.results.Issue;
import com.evinced.dto.results.Report;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.List;

public class AccessibilityTests {

	@Test
	public void evReportWithAnOpenDropdownTest() {
		EvincedWebDriver driver = new EvincedWebDriver(new ChromeDriver());
		driver.get("https://demo.evinced.com/");

		// interacting with the page - opening all dropdown
		WebElement firstDropdown = driver.findElement(By.cssSelector("div.filter-container > div:nth-child(1) > div > div.dropdown.line"));
		firstDropdown.click();

		Report report = driver.evReport();
		List<Issue> issues = report.getIssues();
		Assert.assertEquals(8, issues.size());

		// write the report to HTML format to a file named: "test-results.html"
		EvincedReporter.writeEvResultsToFile("test-results", report, EvincedReporter.FileFormat.HTML);
	}
}

```

### Testing accessibility in a continuous mode
In this example we show how to test a page in multiple dynamic states, such as dropdowns that are opened and closed during the test.

And how do we decide when to stop the issues recording and get all the issues.

```java
import com.evinced.EvincedReporter;
import com.evinced.EvincedWebDriver;
import com.evinced.dto.results.Issue;
import com.evinced.dto.results.Report;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.List;

public class AccessibilityTests {
	
	@Test
	public void demoWithRecording() {
		EvincedWebDriver driver = new EvincedWebDriver(new ChromeDriver());
		driver.evStart();

		driver.get("https://demo.evinced.com/");

		// opening first dropdown
		WebElement firstDropdown = driver.findElement(By.cssSelector("div.filter-container > div:nth-child(1) > div > div.dropdown.line"));
		firstDropdown.click();

		// opening second dropdown
		WebElement secondDropdown = driver.findElement(By.cssSelector("div.filter-container > div:nth-child(2) > div > div.dropdown.line"));
		secondDropdown.click();

		// opening third dropdown
		driver.findElement(By.cssSelector(".react-date-picker")).click();

		// get accessibility issues that occurred since last evStart
		Report report = driver.evStop();
		List<Issue> issues = report.getIssues();
		Assert.assertEquals(11, issues.size());

	}
}

```

### Running on several tabs
Evinced supports performing accessibility tests that involves several tabs, however, it requires some more orchestration.
#### Running `evReport` on several tabs
The `evReport` command works as is on several tabs. Each time on a different tab.
You'll need to change the current window handle to be the selected tab, and only then run `evReport`.

Example
In this example, `page1.html` has a link that opens in a new tab.

```java
// navigate to page1
driver.get("page1.html");

// get report for page1
Report report = driver.evReport();
List<Issue> issues = report.getIssues();
// run some assertions
assertEquals(1, issues.size());

// perform an action that opens a new tab
driver.findElement(new By.ById("open-tab-link")).click();

// switch to new tab
String tabWindowHandle = driver.getWindowHandles().toArray()[1].toString();
driver.switchTo().window(tabWindowHandle);

// get report for the new tab,
// the issues for page1 won't appear here, only ones found on the new tab
report = driver.evReport();
issues = report.getIssues();
assertEquals(3, issues.size());
```

#### Running `evStart` and `evStop` on several tabs
If your test opens more than one tab, you should read this part.

The evStart and evStop scopes are limited to a single window handle.
this means that if you have more than one tab, you need to switch it's window and only then run `evStart` and `evStop`
The `evStop` must be called on the tab that `evStart` command was called on, before starting `evStart` on a new tab

##### _Example_
In this example, `page1.html` has a link that opens in a new tab.

```java
    // navigate to page1
    driver.get("page1.html");
    
    driver.evStart();
    
    // perform actions on page1, clicks, etc...
    
    // open a new tab
    driver.findElement(new By.ById("open-tab-link")).click();
    
    // the evStart and evStop scope are limited to a window handle.
    // this means that if you have more than one tab, you need to switch it's window and only then run `evStart` and `evStop`
    // The `evStop` must be called on the tab that `evStart` command was called on, before starting `evStart` on a new tab
    Report report = driver.evStop();
    List<Issue> issues = report.getIssues();
    assertEquals(1, issues.size());
    
    // perform an action that opens a new tab
    driver.findElement(new By.ById("open-tab-link")).click();
    
    // move to tab
    String tabWindowHandle = driver.getWindowHandles().toArray()[1].toString();
    driver.switchTo().window(tabWindowHandle);
    
    // get report for the new tab,
    // the issues for page1 won't appear here, only ones found on the new tab
    driver.evStart();
    report = driver.evStop();
    issues = report.getIssues();
    assertEquals(3, issues.size());
```

### Add accessibility testing to existing tests
It is possible to configure `EvincedWebDriver` to be called also on `@before` and `@after` methods.
In the example below, `evStart` starts recording before the test starts, and `evStop` is called after each test.

```java

import com.evinced.dto.results.Report;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.*;
import org.junit.rules.TestName;
import org.openqa.selenium.chrome.ChromeDriver;

public class BeforeAfterTest {

	@Rule
	public TestName testName = new TestName();

	private EvincedWebDriver driver;

	/**
	 * Instantiate the WebDriver
	 */
	@BeforeClass
	public void setupClass() {
		WebDriverManager.chromedriver().setup();
		driver = new EvincedWebDriver(new ChromeDriver());
		driver.manage().window().maximize();
	}

	/**
	 * Start recording before each test
	 */
	@Before
	public void setUp() {
		driver.evStart();
	}

	/**
	 * Ensure we close the WebDriver after finishing
	 */
	@After
	public void after() {
		// write an HTML report to file
		Report report = driver.evStop();
		EvincedReporter.writeEvResultsToFile(testName.getMethodName(), report, EvincedReporter.FileFormat.HTML);
	}

	@Test
	public void visitDemoPage() {
		driver.get("https://demo.evinced.com");
	}

	@Test
	public void visitGoogle() {
		driver.get("https://www.google.com");
	}
}
```
