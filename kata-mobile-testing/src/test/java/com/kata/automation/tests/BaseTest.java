package com.kata.automation.tests;

import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import com.kata.automation.utils.DriverManager;

import io.appium.java_client.android.AndroidDriver;

public class BaseTest {
    protected static AndroidDriver driver;

    @BeforeSuite
    public void globalSetUp() {
        driver = DriverManager.getDriver();
    }

    @AfterSuite
    public void globalTearDown() {
        DriverManager.quitDriver();
    }
}
