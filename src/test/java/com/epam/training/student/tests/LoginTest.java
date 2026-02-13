package com.epam.training.student.tests;

import com.epam.training.student.framework.driver.DriverManager;
import com.epam.training.student.framework.pages.InventoryPage;
import com.epam.training.student.framework.pages.LoginPage;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
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
import java.util.stream.Stream;

public class LoginTest {

    private static final Logger log = LoggerFactory.getLogger(LoginTest.class);

    private static final String BASE_URL = "https://www.saucedemo.com/";

    private static final By ERROR_MESSAGE = By.xpath("//h3[@data-test='error']");
    private static final By INVENTORY_CONTAINER = By.xpath("//div[@id='inventory_container']");

    private static final long DEMO_SLEEP_MS = 5000;

    static Stream<UcScenario> scenarios() {
        return Stream.of(
                new UcScenario("UC-1", "any_user", "any_pass", "Username is required", true),
                new UcScenario("UC-2", "any_user", "any_pass", "Password is required", false),
                new UcScenario("UC-3", "standard_user", "secret_sauce", null, false)
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("scenarios")
    void run_uc_pair_in_parallel(UcScenario sc) throws InterruptedException {
        runPairInParallel(sc.name, (browser) -> executeScenario(browser, sc));
    }

    private void executeScenario(String browser, UcScenario sc) {
        DriverManager.startDriver(browser);
        try {
            WebDriver driver = DriverManager.getDriver();
            driver.get(BASE_URL);

            LoginPage login = new LoginPage(driver);

            if (sc.name.equals("UC-1")) {
                login.typeUsername(sc.username)
                        .typePassword(sc.password)
                        .clearUsername()
                        .clearPassword()
                        .clickLogin();

                new WebDriverWait(driver, Duration.ofSeconds(12))
                        .until(ExpectedConditions.visibilityOfElementLocated(ERROR_MESSAGE));

                demoSleep(DEMO_SLEEP_MS);

                assertThat(login.getErrorText(), containsString(sc.expectedError));
                log.info("{} PASS | browser={} | error='{}'", sc.name, browser, login.getErrorText());
                return;
            }

            if (sc.name.equals("UC-2")) {
                login.typeUsername(sc.username)
                        .typePassword(sc.password)
                        .clearPassword()
                        .clickLogin();

                new WebDriverWait(driver, Duration.ofSeconds(12))
                        .until(ExpectedConditions.visibilityOfElementLocated(ERROR_MESSAGE));

                demoSleep(DEMO_SLEEP_MS);

                assertThat(login.getErrorText(), containsString(sc.expectedError));
                log.info("{} PASS | browser={} | error='{}'", sc.name, browser, login.getErrorText());
                return;
            }

            if (sc.name.equals("UC-3")) {
                login.clearUsername()
                        .clearPassword()
                        .typeUsername(sc.username)
                        .typePassword(sc.password)
                        .clickLogin();

                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(18));
                wait.until(ExpectedConditions.urlContains("inventory.html"));
                wait.until(ExpectedConditions.visibilityOfElementLocated(INVENTORY_CONTAINER));

                demoSleep(DEMO_SLEEP_MS);

                assertThat(driver.getTitle(), is("Swag Labs"));
                InventoryPage inventory = new InventoryPage(driver);
                assertThat(inventory.isLoaded(), is(true));

                log.info("{} PASS | browser={} | url={} | title={}",
                        sc.name, browser, driver.getCurrentUrl(), driver.getTitle());
            }

        } finally {
            DriverManager.quitDriver();
        }
    }

    private void runPairInParallel(String ucName, BrowserTask task) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(2);

        Thread chrome = new Thread(() -> {
            try { task.run("chrome"); }
            finally { latch.countDown(); }
        }, ucName + "-CHROME");

        Thread edge = new Thread(() -> {
            try { task.run("edge"); }
            finally { latch.countDown(); }
        }, ucName + "-EDGE");

        chrome.start();
        edge.start();
        latch.await();
    }

    private static void demoSleep(long ms) {
        if (ms <= 0) return;
        try { Thread.sleep(ms); }
        catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }

    @FunctionalInterface
    private interface BrowserTask {
        void run(String browser);
    }

    static class UcScenario {
        final String name;
        final String username;
        final String password;
        final String expectedError;
        final boolean clearBoth;

        UcScenario(String name, String username, String password, String expectedError, boolean clearBoth) {
            this.name = name;
            this.username = username;
            this.password = password;
            this.expectedError = expectedError;
            this.clearBoth = clearBoth;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
