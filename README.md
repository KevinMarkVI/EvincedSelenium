# Evinced SDK for Selenium Java

A Selenium SDK to find accessibility issues.

## Requirements

- Selenium version 3.141.59 or higher.
- Java version 1.8 or higher.
- Only ChromeDriver is supported at the moment.

## Setup

```bash
  <dependency>
        <groupId>com.evinced</groupId>
        <artifactId>java-selenium</artifactId>
        <version>1.0.1</version>
    </dependency>
```

Include it inside a test file, when using Selenium WebDriver

## Usage examples
### Run Analysis Once
```java
    import com.evinced.AccessibilityReport;
    import com.evinced.EvincedWebDriver;
    // import all test classes here - Selenium, etc.           

    // define your driver - currently only Chrome is supported
    WebDriver chromeDriver = new ChromeDriver();

    // pass the driver to the EvincedWebDriver constructor
    EvincedWebDriver driver = new EvincedWebDriver(chromeDriver);

    // continue the test as you planned, all of the WebDriver functionality
    driver.get("https://www.google.com");

    // you can run the Evinced tool to get a snapshot of the current screen accessibility status
    AccessibilityReport report = driver.evReport();

```

### Recording an entire session using `evStart` and `evStop`

```java
    import com.evinced.AccessibilityReport;
    import com.evinced.EvincedWebDriver;
    // import all test classes here - Selenium, etc.           
    
    // define your driver - currently only Chrome is supported
    WebDriver chromeDriver = new ChromeDriver();
    
    // pass the driver to the EvincedWebDriver constructor
    EvincedWebDriver driver = new EvincedWebDriver(chromeDriver);
    
    // continue the test as you planned, all of the WebDriver functionality
    driver.get("https://www.google.com");
    
    // you can run the Evinced tool to get a snapshot of the current screen accessibility status
    AccessibilityReport report = driver.runAnalysisOnce();
    
    // or you can start a recording session
    // start recording
    driver.evStart();
    
    // do things on the page - click, type, open modals, etc.
            
    // Stops the recording and return a detailed report with all validations the occured since last `evStart` command
    AccessibilityReport report = driver.evStop();
```
### Run Analysis Once on a single root selector

```java
    import com.evinced.AccessibilityReport;
    import com.evinced.EvincedWebDriver;
    // import all test classes here - Selenium, etc.           

    // define your driver - currently only Chrome is supported
    WebDriver chromeDriver = new ChromeDriver();

    // pass the driver to the EvincedWebDriver constructor
    EvincedWebDriver driver = new EvincedWebDriver(chromeDriver);
    
    EvincedConfiguration configuration = new EvincedConfiguration();
    // define the selector that the accessebility analysis will run on.
    // Evinced will check that selector and its children, and avoid its parent(s) and siblings.
    configuration.setRootSelector('#some .selector');
    
    driver.get("https://demo.com.evinced.com/");

    AnalysisResult result = driver.evReport(configuration);
    assertEquals(result.getIssues().length,6)
```

### Setting the log level
####Mainly to better investigate internal failures
Set the log level on the EvincedWebDriver object.
Logs will be printed to the console.

```java
    import org.apache.logging.log4j.Level;
    ...
    // initialize EvincedWebDriver
    WebDriver chromeDriver = new ChromeDriver();
    EvincedWebDriver driver = new EvincedWebDriver(chromeDriver);
   
   // set log level
    driver.setLogLevel(Level.ERROR); // now errors will be printed to console
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
The evStart and evStop scopes are limited to a single window handle.
this means that if you have more than one tab, you need to switch it's window and only then run `evStart` and `evStop`
The `evStop` must be called on the tab that `evStart` command was called on, before starting `evStart` on a new tab

Example
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
	

### Exporting results as a JSON file
```java
    import com.evinced.AccessibilityReport;
    import com.evinced.EvincedWebDriver;
    // import all test classes here - Selenium, etc.           

    // define your driver - currently only Chrome is supported
    WebDriver chromeDriver = new ChromeDriver();

    // pass the driver to the EvincedWebDriver constructor
    EvincedWebDriver driver = new EvincedWebDriver(chromeDriver);

    // continue the test as you planned, all of the WebDriver functionality
    driver.get("https://www.google.com");

    // you can run the Evinced tool to get a snapshot of the current screen accessibility status
    AnalysisResult result = driver.evReport();
    
    // Export report as a JSON file
    driver.writeEvResultsToFile("file-name", result, EvincedReporter.FileFormat.JSON);

```
#### JSON file example

### Exporting results as an HTML file
```java
    import com.evinced.AccessibilityReport;
    import com.evinced.EvincedWebDriver;
    // import all test classes here - Selenium, etc.           

    // define your driver - currently only Chrome is supported
    WebDriver chromeDriver = new ChromeDriver();

    // pass the driver to the EvincedWebDriver constructor
    EvincedWebDriver driver = new EvincedWebDriver(chromeDriver);

    // continue the test as you planned, all of the WebDriver functionality
    driver.get("https://www.google.com");

    // you can run the Evinced tool to get a snapshot of the current screen accessibility status
	AnalysisResult result = driver.runAnalysisOnce();
    
    // Export report as a JSON file
    driver.writeEvResultsToFile("file-name", result, EvincedReporter.FileFormat.HTML);

```
### HTML report example
<img src="https://user-images.githubusercontent.com/60566974/123771814-085e5b80-d8d4-11eb-8bfc-feae2628a097.png" width="600" />

# Developer notes:
## Pre-requisites
- Install mvn https://maven.apache.org/download.cgi
- Install Java XXX

## todo: write something about Selenium versions

## Build project
- Check that the project compiles - `mvn compile`
- Run Tests - `mvn test`
- Create a package (JAR) - `mvn package`
- Create a package + skip tests - `mvn package -DskipTests`
- Bump version - `mvn build-helper:parse-version versions:set -DnewVersion=\${parsedVersion.majorVersion}.\${parsedVersion.minorVersion}.\${parsedVersion.nextIncrementalVersion} versions:commit`


*comments:*
Based on: https://github.com/nadvolod/selenium-java
