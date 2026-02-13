package com.epam.training.student.framework.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class LoginPage {

    private final WebDriver driver;

    // XPath locators (requisito)
    private final By usernameInput = By.xpath("//input[@id='user-name']");
    private final By passwordInput = By.xpath("//input[@id='password']");
    private final By loginButton   = By.xpath("//input[@id='login-button']");
    private final By errorMessage  = By.xpath("//h3[@data-test='error']");

    public LoginPage(WebDriver driver) {
        this.driver = driver;
    }

    // Actions
    public LoginPage typeUsername(String username) {
        driver.findElement(usernameInput).sendKeys(username);
        return this;
    }

    public LoginPage typePassword(String password) {
        driver.findElement(passwordInput).sendKeys(password);
        return this;
    }

    public LoginPage clearUsername() {
        WebElement u = driver.findElement(usernameInput);
        u.click();
        u.sendKeys(Keys.chord(Keys.CONTROL, "a"));
        u.sendKeys(Keys.DELETE);
        return this;
    }

    public LoginPage clearPassword() {
        WebElement p = driver.findElement(passwordInput);
        p.click();
        p.sendKeys(Keys.chord(Keys.CONTROL, "a"));
        p.sendKeys(Keys.DELETE);
        return this;
    }

    public LoginPage clickLogin() {
        driver.findElement(loginButton).click();
        return this;
    }

    // Elements for explicit waits in tests
    public WebElement errorElement() {
        return driver.findElement(errorMessage);
    }

    public String getErrorText() {
        return driver.findElement(errorMessage).getText();
    }
}
