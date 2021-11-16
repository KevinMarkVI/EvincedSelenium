
# Evinced Selenium SDK - Geb/Spoke integration guide
Java Selenium API To find accessibility issues.
This guide will help you integrate your Geb testing project with Evinced's Selenium SDK.

# Getting started
## Prerequisites
- Selenium version 3.141.59 or higher.
- Java version 1.8 or higher.
- Only ChromeDriver is supported at the moment.

## Setup
### Add `selenium-sdk` as a dependency to `build.gradle`
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
    // add this to the dependencies object
    testCompile "com.evinced:selenium-sdk:{latest-sdk-version}"
```

#### _Example_ for `build.gradle` file with Evinced sdk dependency
```groovy
plugins {
    id "idea"
    id "groovy"
    id "com.github.erdi.webdriver-binaries" version "2.2"
    id "com.github.erdi.idea-base" version "2.2"
}

ext {
    // The drivers we want to use
    drivers = ["firefox", "chrome", "chromeHeadless"]

    ext {
        gebVersion = '4.1'
        seleniumVersion = '3.14.0'
        chromeDriverVersion = '79.0.3945.36'
        geckoDriverVersion = '0.26.0'
    }
}

repositories {
    mavenCentral()
    // maven local used to import the library from local machine's MVN repo
    mavenLocal()
}

dependencies {
    // If using Spock, need to depend on geb-spock
    testCompile "org.gebish:geb-spock:$gebVersion"
    testCompile("org.spockframework:spock-core:1.3-groovy-2.5") {
        exclude group: "org.codehaus.groovy"
    }

    // If using JUnit, need to depend on geb-junit (3 or 4)
    testCompile "org.gebish:geb-junit4:$gebVersion"
    // make sure to take the correct selenium-sdk version    
    testCompile "com.evinced:selenium-sdk:0.0.7"
    // Drivers
    testCompile "org.seleniumhq.selenium:selenium-chrome-driver:$seleniumVersion"
    testCompile "org.seleniumhq.selenium:selenium-firefox-driver:$seleniumVersion"
}

webdriverBinaries {
    chromedriver {
        version = chromeDriverVersion
        fallbackTo32Bit = true
    }
    geckodriver geckoDriverVersion
}

drivers.each { driver ->
    task "${driver}Test"(type: Test) {
        group JavaBasePlugin.VERIFICATION_GROUP

        outputs.upToDateWhen { false }  // Always run tests

        systemProperty "geb.build.reportsDir", reporting.file("geb/$name")
        systemProperty "geb.env", driver
    }
}

test {
    dependsOn drivers.collect { tasks["${it}Test"] }
    enabled = false
}

tasks.withType(Test) {
    maxHeapSize = "1g"
    jvmArgs '-XX:MaxMetaspaceSize=128m'
    testLogging {
        exceptionFormat = 'full'
    }
}

tasks.withType(GroovyCompile) {
    groovyOptions.forkOptions.memoryMaximumSize = '256m'
}

```
### Update your Geb Configuration file to use `EvincedWebDriver`
In order to use the `selenium-sdk` you must wrap the `ChromeDriver` instance with an instance of `EvincedWebDriver`.

This can be done whenever you initialize the WebDriver object.

In case you are using a Geb Configuration file, you can do it like this:

```groovy
/*
	This is the Geb configuration file.
	See: http://www.gebish.org/manual/current/#configuration
*/

import com.evinced.EvincedWebDriver
import org.openqa.selenium.chrome.ChromeDriver


environments {
	
	chrome {
            System.setProperty("webdriver.chrome.driver", "/Users/asafshochetavida/workspace/geb-example-gradle/src/test/resources/chromedriver-91")

            // Wrap ChromeDriver with EvincedWebDriver to enable Evinced SDK
            driver = { new EvincedWebDriver(new ChromeDriver()) }
	}
}

```

#### Configuration using a `RemoteWebDriver`
In case you are using a `RemoveWebDriver`, using an external service or something like `selnoid`,
You can wrap the remote driver with `EvincedWebDriver` and everything will work as planned.

Geb configuration file example with `RemoteWebDriver`:
```groovy
/*
	This is the Geb configuration file.
	See: http://www.gebish.org/manual/current/#configuration
*/

import com.evinced.EvincedWebDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.remote.RemoteWebDriver

environments {
	chrome {
		driver = { new EvincedWebDriver(new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"), new ChromeOptions())) }
	}
}

```

## Your first test
This is a minimal working example using JUnit.

```groovy
import com.evinced.EvincedReporter
import com.evinced.dto.results.Report
import demosite.DemoSitePage
import geb.junit4.GebReportingTest
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4)
class DemoSiteTest extends GebReportingTest {

    @Test
    void simpleAccessibilityTest() {
        go "https://demo.evinced.com/"
        Report report = driver.evReport()
        assert report.issues.size() > 0
        EvincedReporter.writeEvResultsToFile("demoSiteReport", report, EvincedReporter.FileFormat.HTML)
    }
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
The following demos assume the existance of these files:

_DemoSitePage.groovy_
```groovy
import geb.Page

class DemoSitePage extends Page {

    static url = "https://demo.evinced.com/"

    static at = { title == "Home | Evinced, Demos site" }

    static content = {
        dropdowns { module(DemoSiteModule) }
    }
}

```

_DemoSiteModule.groovy_
```groovy
import geb.Module

class DemoSiteModule extends Module {
    static content = {
        firstDropdown { $("div.filter-container > div:nth-child(1) > div > div.dropdown.line") }
        secondDropdown { $("div.filter-container > div:nth-child(2) > div > div.dropdown.line") }
        thirdDropdown { $(".react-date-picker") }
    }

    void openFirstDropdown() {
        firstDropdown.click()
    }

    void openSecondDropdown() {
        secondDropdown.click()
    }

    void openThirdDropdown() {
        thirdDropdown.click()
    }
}
```

## Geb Spock integration

### Testing accessibility in a specific state of the application
In this example, we are going to test the accessibility of a page in a specific state.

This test navigates to a page, opens a dropdown, and only then runs the accessibility engines and finds issues.

```groovy
import com.evinced.dto.results.Report
import demosite.DemoSitePage
import geb.spock.GebSpec

class EvReportTest extends GebSpec {

    def "can get to the current Book of Geb"() {
        when: "Entering a demo site"
        to DemoSitePage

        and: "Opening all dropdowns"
        dropdowns.openFirstDropdown()

        then: "It should show some a11y issues"
        Report report = driver.evReport()
        report.issues.size() > 0
    }
}
```

### Testing accessibility in a continuous mode
In this example we show how to test a page in multiple dynamic states, such as dropdowns that are opened and closed during the test.

And how do we decide when to stop the issues recording and get all the issues.

```groovy
import com.evinced.dto.results.Report
import demosite.DemoSitePage
import geb.spock.GebSpec

class RecordingTest extends GebSpec {

    def "can get to the current Book of Geb"() {
        when: "Entering a demo site"
        to DemoSitePage
        driver.evStart() // start recording accessibility issues

        and: "Opening all dropdowns"
        dropdowns.openFirstDropdown()
        dropdowns.openSecondDropdown()
        dropdowns.openThirdDropdown()

        then: "It should show some a11y issues"
        Report report = driver.evStop() // stop recording accessibility issues
        report.issues.size() > 0
    }
}
```

### Add accessibility testing to existing tests
It is possible to configure `EvincedWebDriver` to be called also on `setup` and `cleanup` methods.
In the example below, `evStart` starts recording before the test starts, and `evStop` is called after each test.

```groovy
import com.evinced.EvincedReporter
import com.evinced.dto.results.Report
import demosite.DemoSitePage
import geb.spock.GebSpec

class DemoSiteSpockBeforeAfterSpec extends GebSpec {

    def setup() { // run before every feature method
        driver.evStart()
    }

    def cleanup() {  // run after every feature method
        Report r = driver.evStop()
        System.out.println("number of results "+r.issues.size())
        EvincedReporter.writeEvResultsToFile("foo", r, EvincedReporter.FileFormat.HTML)
    }

    def "can get to the current Book of Geb"() {
        when: "Entering a demo site"
        to DemoSitePage

        and: "Opening all dropdowns"
        dropdowns.openFirstDropdown()
        dropdowns.openSecondDropdown()
        dropdowns.openThirdDropdown()

        then: "End Test"
        assert true
    }

    def "go to google" () {
        when: "Entering a Google"
        go "https://www.google.com"

        then: "End Test"
        assert true
    }
}
```
