package com.epam.training.student.tests;

import com.epam.training.student.framework.driver.DriverManager;
import com.epam.training.student.framework.pages.InventoryPage;
import com.epam.training.student.framework.pages.LoginPage;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;

@TestMethodOrder(OrderAnnotation.class) // ✅ UC1 -> UC2 -> UC3 en orden
public class LoginTest {

    private static final Logger log = LoggerFactory.getLogger(LoginTest.class);

    private static final String BASE_URL = "https://www.saucedemo.com/";

    // XPath (requisito)
    private static final By ERROR_MESSAGE = By.xpath("//h3[@data-test='error']");
    private static final By INVENTORY_CONTAINER = By.xpath("//div[@id='inventory_container']");

    // =========================
    // ✅ DEMO ONLY (si lo pones en 0, NO duerme)
    // =========================
    private static final long DEMO_SLEEP_UC1_MS = 5000;
    private static final long DEMO_SLEEP_UC2_MS = 5000;
    private static final long DEMO_SLEEP_UC3_MS = 5000;
    // =========================

    // =========================
    // ✅ UC-1 (Chrome + Edge en paralelo)
    // =========================
    @Test
    @Order(1)
    void UC1_pair_chrome_and_edge() throws InterruptedException {
        runPairInParallel("UC-1", (browser) -> {
            DriverManager.startDriver(browser);
            try {
                WebDriver driver = DriverManager.getDriver();
                driver.get(BASE_URL);

                LoginPage login = new LoginPage(driver);

                login.typeUsername("any_user")
                        .typePassword("any_pass")
                        .clearUsername()
                        .clearPassword()
                        .clickLogin();

                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(12));
                wait.until(ExpectedConditions.visibilityOfElementLocated(ERROR_MESSAGE));

                // ✅ demo only (no rompe si lo pones en 0)
                demoSleep(DEMO_SLEEP_UC1_MS);

                assertThat(login.getErrorText(), containsString("Username is required"));
                log.info("UC-1 PASS | browser={} | error='{}'", browser, login.getErrorText());
            } finally {
                DriverManager.quitDriver(); // ✅ se cierra cada browser en su thread
            }
        });
    }

    // =========================
    // ✅ UC-2 (Chrome + Edge en paralelo)
    // =========================
    @Test
    @Order(2)
    void UC2_pair_chrome_and_edge() throws InterruptedException {
        runPairInParallel("UC-2", (browser) -> {
            DriverManager.startDriver(browser);
            try {
                WebDriver driver = DriverManager.getDriver();
                driver.get(BASE_URL);

                LoginPage login = new LoginPage(driver);

                login.typeUsername("any_user")
                        .typePassword("any_pass")
                        .clearPassword()
                        .clickLogin();

                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(12));
                wait.until(ExpectedConditions.visibilityOfElementLocated(ERROR_MESSAGE));

                // ✅ demo only (no rompe si lo pones en 0)
                demoSleep(DEMO_SLEEP_UC2_MS);

                assertThat(login.getErrorText(), containsString("Password is required"));
                log.info("UC-2 PASS | browser={} | error='{}'", browser, login.getErrorText());
            } finally {
                DriverManager.quitDriver();
            }
        });
    }

    // =========================
    // ✅ UC-3 (Chrome + Edge en paralelo)
    // =========================
    @Test
    @Order(3)
    void UC3_pair_chrome_and_edge() throws InterruptedException {
        runPairInParallel("UC-3", (browser) -> {
            DriverManager.startDriver(browser);
            try {
                WebDriver driver = DriverManager.getDriver();
                driver.get(BASE_URL);

                LoginPage login = new LoginPage(driver);

                login.clearUsername()
                        .clearPassword()
                        .typeUsername("standard_user")
                        .typePassword("secret_sauce")
                        .clickLogin();

                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(18));
                wait.until(ExpectedConditions.urlContains("inventory.html"));
                wait.until(ExpectedConditions.visibilityOfElementLocated(INVENTORY_CONTAINER));

                // ✅ demo only (no rompe si lo pones en 0)
                demoSleep(DEMO_SLEEP_UC3_MS);

                assertThat(driver.getTitle(), is("Swag Labs"));

                InventoryPage inventory = new InventoryPage(driver);
                assertThat(inventory.isLoaded(), is(true));

                log.info("UC-3 PASS | browser={} | url={} | title={}",
                        browser, driver.getCurrentUrl(), driver.getTitle());
            } finally {
                DriverManager.quitDriver();
            }
        });
    }

    // ======================================================
    // ✅ Helper: corre Chrome + Edge al mismo tiempo (pareja)
    // y espera a que terminen ambos para seguir al siguiente UC
    // ======================================================
    private void runPairInParallel(String ucName, BrowserTask task) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(2);

        Thread chrome = new Thread(() -> {
            try {
                task.run("chrome");
            } finally {
                latch.countDown();
            }
        }, ucName + "-CHROME");

        Thread edge = new Thread(() -> {
            try {
                task.run("edge");
            } finally {
                latch.countDown();
            }
        }, ucName + "-EDGE");

        chrome.start();
        edge.start();

        // ✅ NO sigue al próximo UC hasta que terminen los 2 browsers
        latch.await();
    }

    // ======================================================
    // ✅ DEMO ONLY: sleep "seguro"
    // - Si ms=0 no hace nada
    // - No rompe compilación dentro de lambdas
    // ======================================================
    private static void demoSleep(long ms) {
        if (ms <= 0) return;
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @FunctionalInterface
    private interface BrowserTask {
        void run(String browser);
    }
}
