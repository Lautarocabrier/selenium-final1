package com.epam.training.student.framework.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class InventoryPage {

    private final WebDriver driver;

    // XPath locators
    private final By inventoryContainer = By.xpath("//div[@id='inventory_container']");
    private final By headerTitle        = By.xpath("//span[@class='title']");

    public InventoryPage(WebDriver driver) {
        this.driver = driver;
    }

    public WebElement inventoryContainerElement() {
        return driver.findElement(inventoryContainer);
    }

    public boolean isLoaded() {
        return inventoryContainerElement().isDisplayed();
    }

    public String getHeaderTitle() {
        return driver.findElement(headerTitle).getText();
    }
}
