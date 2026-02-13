# SauceDemo Login Tests (UC-1 / UC-2 / UC-3)

This project contains automated UI tests for the login page of SauceDemo:
https://www.saucedemo.com/

The goal is to validate three login scenarios (UC-1, UC-2, UC-3) using:
- Selenium WebDriver
- JUnit 5
- Maven
- XPath locators
- Hamcrest assertions
- Parallel execution on Chrome and Edge

## Tech Stack
- Java
- Maven
- JUnit 5
- Selenium WebDriver
- WebDriverManager
- Hamcrest
- Log4j2 (logging via SLF4J bridge)

## Test Scenarios

UC-1: Empty credentials
1. Type any credentials into Username and Password
2. Clear both inputs
3. Click Login
4. Verify error message contains: "Username is required"

UC-2: Username provided, password empty
1. Type any username
2. Type any password
3. Clear the Password input
4. Click Login
5. Verify error message contains: "Password is required"

UC-3: Valid login
1. Use a valid username from the accepted list (example: standard_user)
2. Use password: secret_sauce
3. Click Login
4. Verify page title is "Swag Labs" and inventory page is loaded

## Parallel Execution Behavior

For each UC, the test runs the same scenario in parallel on:
- Chrome
- Edge

Execution order is:
1) UC-1 runs (Chrome + Edge at the same time)
2) UC-2 runs (Chrome + Edge at the same time)
3) UC-3 runs (Chrome + Edge at the same time)

## How to Run

### Prerequisites
- Java installed (JDK)
- Maven installed (or use the IDE Maven integration)

### Run from terminal
From the project root (where pom.xml is located):

mvn test

### Run from IntelliJ
Open the project and run the LoginTest class.

## Notes
- The project uses explicit waits (WebDriverWait) for stability.
- You may see Selenium CDP warnings for some browser versions; tests should still run normally.
