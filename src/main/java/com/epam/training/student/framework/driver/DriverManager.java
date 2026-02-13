package com.epam.training.student.framework.driver;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;

import java.time.Duration;

public final class DriverManager {

    private DriverManager() {}

    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();

    public static WebDriver getDriver() {
        return DRIVER.get();
    }

    public static void startDriver(String browser) {
        WebDriver driver = switch (browser.toLowerCase()) {
            case "chrome" -> new ChromeDriver();
            case "edge" -> new EdgeDriver();
            default -> throw new IllegalArgumentException("Unsupported browser: " + browser);
        };

        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0)); // explicit waits only
        driver.manage().window().maximize();

        DRIVER.set(driver);
    }

    public static void quitDriver() {
        WebDriver driver = DRIVER.get();
        if (driver != null) {
            driver.quit();
            DRIVER.remove();
        }
    }
}
