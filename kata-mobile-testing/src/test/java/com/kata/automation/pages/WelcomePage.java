package com.kata.automation.pages;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.appium.java_client.android.AndroidDriver;

public class WelcomePage {
    private AndroidDriver driver;
    private WebDriverWait wait;


    private By allowLocation = By.id("com.android.permissioncontroller:id/permission_allow_foreground_only_button");
    private By allowCalendarAndNotification = By.id("com.android.permissioncontroller:id/permission_allow_button");
    private By organizationName = By.id("com.kata.mobile:id/fragmentTenantTextInputEditText");
    private By continueButton = By.id("com.kata.mobile:id/fragmentTenantContinueButton");

    public WelcomePage(AndroidDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void allowPermissions() {
        driver.findElement(allowLocation).click();
        driver.findElement(allowCalendarAndNotification).click();
        driver.findElement(allowCalendarAndNotification).click();
    }

    public void writeOrganization(String organization) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(organizationName)).sendKeys(organization);
    }

    public void continueClick() {
        driver.findElement(continueButton).click();
    }

    public void completeWelcomePage(String organization) {
        allowPermissions();
        writeOrganization(organization);
        continueClick();
    }

}
