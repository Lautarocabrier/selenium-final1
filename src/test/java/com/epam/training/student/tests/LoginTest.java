package com.epam.training.student.tests;

import com.epam.training.student.framework.driver.DriverManager;
import com.epam.training.student.framework.pages.LoginPage;
import com.epam.training.student.framework.pages.InventoryPage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.stream.Stream;
import org.junit.jupiter.params.provider.Arguments;

public class LoginTest {

    private static final String BASE_URL = "https://www.saucedemo.com/";

    // Data Provider: browsers
    static Stream<String> browsers() {
        return Stream.of("chrome", "edge");
    }

    // UC-1 data provider
    static Stream<Arguments> uc1Data() {
        return browsers().map(b -> Arguments.of(b, "any_user", "any_pass"));
    }

    // UC-2 data provider
    static Stream<Arguments> uc2Data() {
        return browsers().map(b -> Arguments.of(b, "any_user", "any_pass"));
    }

    // UC-3 data provider
    static Stream<Arguments> uc3Data() {
        return browsers().map(b -> Arguments.of(b, "standard_user", "secret_sauce"));
    }

    @AfterEach
    void tearDown() {
        DriverManager.quitDriver();
    }

    @ParameterizedTest(name = "UC-1 empty credentials on {0}")
    @MethodSource("uc1Data")
    void uc1_empty_credentials_username_required(String browser, String user, String pass) {
        DriverManager.startDriver(browser);
        WebDriver driver = DriverManager.getDriver();
        driver.get(BASE_URL);

        LoginPage login = new LoginPage(driver);

        // Type any credentials -> clear -> login
        login.typeUsername(user)
                .typePassword(pass)
                .clearUsername()
                .clearPassword()
                .clickLogin();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.visibilityOf(login.errorElement()));

        // SauceDemo returns: "Epic sadface: Username is required"
        Assertions.assertTrue(login.getErrorText().contains("Username is required"));
    }

    @ParameterizedTest(name = "UC-2 password empty on {0}")
    @MethodSource("uc2Data")
    void uc2_password_required(String browser, String user, String pass) {
        DriverManager.startDriver(browser);
        WebDriver driver = DriverManager.getDriver();
        driver.get(BASE_URL);

        LoginPage login = new LoginPage(driver);

        // Type username & password -> clear password -> login
        login.typeUsername(user)
                .typePassword(pass)
                .clearPassword()
                .clickLogin();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.visibilityOf(login.errorElement()));

        Assertions.assertTrue(login.getErrorText().contains("Password is required"));
    }

    @ParameterizedTest(name = "UC-3 valid login on {0}")
    @MethodSource("uc3Data")
    void uc3_valid_login_title_is_swag_labs(String browser, String user, String pass) {
        DriverManager.startDriver(browser);
        WebDriver driver = DriverManager.getDriver();
        driver.get(BASE_URL);

        LoginPage login = new LoginPage(driver);

        login.typeUsername(user)
                .typePassword(pass)
                .clickLogin();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(8));
        wait.until(ExpectedConditions.titleIs("Swag Labs"));

        // extra: inventory loaded (optional but good)
        InventoryPage inventory = new InventoryPage(driver);
        wait.until(ExpectedConditions.visibilityOf(inventory.inventoryContainerElement()));

        Assertions.assertEquals("Swag Labs", driver.getTitle());
    }
}
