package com.epam.training.student.tests;

import com.epam.training.student.framework.driver.DriverManager;
import com.epam.training.student.framework.pages.InventoryPage;
import com.epam.training.student.framework.pages.LoginPage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.stream.Stream;
import org.junit.jupiter.params.provider.Arguments;

public class LoginTest {
    //login test
    private static final Logger log = LoggerFactory.getLogger(LoginTest.class);

    private static final String BASE_URL = "https://www.saucedemo.com/";

    // Locators (XPath requisito) - definidos acá para NO tocar tus Pages
    private static final By ERROR_MESSAGE = By.xpath("//h3[@data-test='error']");
    private static final By INVENTORY_CONTAINER = By.xpath("//div[@id='inventory_container']");

    // =========================
    // ✅ DEMO ONLY - SLEEP CONFIG
    // =========================
    // Cambiá estos valores si querés más/menos pausa en la demo
    private static final long DEMO_SLEEP_UC1_MS = 2000;
    private static final long DEMO_SLEEP_UC2_MS = 2000;
    private static final long DEMO_SLEEP_UC3_MS = 2000;
    // =========================

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
    void uc1_empty_credentials_username_required(String browser, String user, String pass) throws InterruptedException {
        DriverManager.startDriver(browser);
        WebDriver driver = DriverManager.getDriver();
        driver.get(BASE_URL);

        LoginPage login = new LoginPage(driver);

        login.typeUsername(user)
                .typePassword(pass)
                .clearUsername()
                .clearPassword()
                .clickLogin();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.visibilityOfElementLocated(ERROR_MESSAGE));

        // =========================
        // ✅ DEMO ONLY - UC1 SLEEP
        // (Mala práctica para automation real; solo para presentación)
        // =========================
        Thread.sleep(DEMO_SLEEP_UC1_MS);

        Assertions.assertTrue(login.getErrorText().contains("Username is required"));
        log.info("UC-1 PASS  | browser={} | error='{}'", browser, login.getErrorText());
    }

    @ParameterizedTest(name = "UC-2 password empty on {0}")
    @MethodSource("uc2Data")
    void uc2_password_required(String browser, String user, String pass) throws InterruptedException {
        DriverManager.startDriver(browser);
        WebDriver driver = DriverManager.getDriver();
        driver.get(BASE_URL);

        LoginPage login = new LoginPage(driver);

        login.typeUsername(user)
                .typePassword(pass)
                .clearPassword()
                .clickLogin();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        wait.until(ExpectedConditions.visibilityOfElementLocated(ERROR_MESSAGE));

        // =========================
        // ✅ DEMO ONLY - UC2 SLEEP
        // (Mala práctica para automation real; solo para presentación)
        // =========================
        Thread.sleep(DEMO_SLEEP_UC2_MS);

        Assertions.assertTrue(login.getErrorText().contains("Password is required"));
        log.info("UC-2 PASS  | browser={} | error='{}'", browser, login.getErrorText());
    }

    @ParameterizedTest(name = "UC-3 valid login on {0}")
    @MethodSource("uc3Data")
    void uc3_valid_login_title_is_swag_labs(String browser, String user, String pass) throws InterruptedException {
        DriverManager.startDriver(browser);
        WebDriver driver = DriverManager.getDriver();
        driver.get(BASE_URL);

        LoginPage login = new LoginPage(driver);

        login.clearUsername()
                .clearPassword()
                .typeUsername(user)
                .typePassword(pass)
                .clickLogin();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Esperamos navegación real al dashboard
        wait.until(ExpectedConditions.urlContains("inventory.html"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(INVENTORY_CONTAINER));

        // =========================
        // ✅ DEMO ONLY - UC3 SLEEP
        // (Mala práctica para automation real; solo para presentación)
        // =========================
        Thread.sleep(DEMO_SLEEP_UC3_MS);

        Assertions.assertEquals("Swag Labs", driver.getTitle());

        InventoryPage inventory = new InventoryPage(driver);
        Assertions.assertTrue(inventory.isLoaded());
        log.info("UC-3 PASS  | browser={} | url={} | title={}", browser, driver.getCurrentUrl(), driver.getTitle());
    }
}
