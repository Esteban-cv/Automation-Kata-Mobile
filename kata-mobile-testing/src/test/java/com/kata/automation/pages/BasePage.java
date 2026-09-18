package com.kata.automation.pages;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Locale;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Pause;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;

public class BasePage {
    protected AndroidDriver driver;
    protected WebDriverWait wait;

    private static final Locale LOCALE_ES = new Locale("es", "ES");
    private static final DateTimeFormatter DAY_ACCESSIBILITY_FORMAT = DateTimeFormatter.ofPattern("dd MMMM yyyy", LOCALE_ES);

    public BasePage(AndroidDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(45));
    }

    protected void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    protected void waitForVisibility(By locator) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected void scrollDownPixels(int pixels) {
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence swipe = new Sequence(finger, 1);
        int startX = 540;
        int startY = 1500;
        int endY = startY - pixels;

        swipe.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), startX, startY));
        swipe.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        swipe.addAction(new Pause(finger, Duration.ofMillis(100)));
        swipe.addAction(finger.createPointerMove(Duration.ofMillis(250), PointerInput.Origin.viewport(), startX, endY));
        swipe.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        driver.perform(Arrays.asList(swipe));
    }

    protected void scrollUpPixels(int pixels) {
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence swipe = new Sequence(finger, 1);
        int startX = 500;
        int startY = 800;
        int endY = startY + pixels;

        swipe.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), startX, startY));
        swipe.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        swipe.addAction(new Pause(finger, Duration.ofMillis(100)));
        swipe.addAction(finger.createPointerMove(Duration.ofMillis(250), PointerInput.Origin.viewport(), startX, endY));
        swipe.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        driver.perform(Arrays.asList(swipe));
    }

    protected void scrollDownPercent(double percentAmount) {
        Dimension size = driver.manage().window().getSize();
        int startX = size.width / 2;
        int startY = (int) (size.height * 0.50);
        int endY = startY - (int) (size.height * percentAmount);
        endY = Math.max(10, Math.min(size.height - 10, endY));

        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence swipe = new Sequence(finger, 1);

        swipe.addAction(finger.createPointerMove(Duration.ZERO, PointerInput.Origin.viewport(), startX, startY));
        swipe.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        swipe.addAction(new Pause(finger, Duration.ofMillis(150)));
        swipe.addAction(finger.createPointerMove(Duration.ofMillis(350), PointerInput.Origin.viewport(), startX, endY));
        swipe.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));

        driver.perform(Arrays.asList(swipe));
    }

    protected void scrollToTextAndClick(String text) {
        By locator = By.xpath("//android.widget.TextView[contains(@text, '" + text + "')]");
        int maxScrolls = 5;
        driver.manage().timeouts().implicitlyWait(Duration.ofMillis(500));

        for (int i = 0; i < maxScrolls; i++) {
            try {
                WebElement element = driver.findElement(locator);
                if (element.isDisplayed()) {
                    driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(30));
                    return;
                }
            } catch (Exception e) {
                scrollDownPercent(0.35);
            }
        }
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(30));
        throw new RuntimeException("No se encontró el texto '" + text + "' tras " + maxScrolls + " scrolls.");
    }

    protected void scrollToText(String text) {
        scrollToText(text, 0.20);
    }

    protected void scrollToText(String text, double jumpPercent) {
        By locator = By.xpath("//android.widget.TextView[contains(@text, '" + text + "')]");
        driver.manage().timeouts().implicitlyWait(Duration.ofMillis(500));

        for (int i = 0; i < 8; i++) {
            try {
                if (driver.findElement(locator).isDisplayed()) {
                    scrollDownPercent(0.12);
                    driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(30));
                    return;
                }
            } catch (Exception e) {
                scrollDownPercent(jumpPercent);
            }
        }
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(30));
        throw new RuntimeException("No se encontró el texto '" + text + "' tras 8 scrolls.");
    }

    protected By byVisibleText(String text) {
        return By.xpath("//android.widget.TextView[@text=\"" + text + "\"]");
    }

    protected void click(By locator) {
        clickSafely(locator);
    }

    protected void clickComposeDropdown(By locator) {
        int maxAttempts = 3;
        Exception lastError = null;

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
                element.click();
                return;
            } catch (Exception e) {
                lastError = e;
                sleep(400); 
            }
        }
        throw new RuntimeException("No se pudo abrir el dropdown " + locator + " tras " + maxAttempts + " intentos.", lastError);
    }

    protected void type(By locator, String text) {
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
        element.clear();
        element.sendKeys(text);
    }

    protected void selectDateInCalendar(int day, int month, int year) {
        LocalDate targetDate = LocalDate.of(year, month, day);
        click(By.id("android:id/date_picker_header_year"));

        int currentYear = LocalDate.now().getYear();
        By yearLocator = AppiumBy.androidUIAutomator("new UiSelector().text(\"" + year + "\")");

        boolean yearFound = false;
        int maxScrolls = 20;

        driver.manage().timeouts().implicitlyWait(Duration.ofMillis(500));

        for (int i = 0; i < maxScrolls; i++) {
            try {
                WebElement yearElement = driver.findElement(yearLocator);
                if (yearElement.isDisplayed()) {
                    yearElement.click();
                    yearFound = true;
                    break;
                }
            } catch (Exception e) {
                if (year < currentYear) {
                    scrollUpPixels(450);
                } else {
                    scrollDownPixels(450);
                }
            }
        }

        if (!yearFound) {
            throw new IllegalStateException("No se encontró el año tras " + maxScrolls + " intentos.");
        }

        navigateToMonth(YearMonth.of(year, month));
        String dayAccessibilityId = targetDate.format(DAY_ACCESSIBILITY_FORMAT);
        click(AppiumBy.accessibilityId(dayAccessibilityId));
        click(By.xpath("//android.widget.FrameLayout[@resource-id=\"android:id/content\"]/android.view.ViewGroup/android.view.View/android.view.View/android.view.View/android.view.View[2]/android.widget.Button"));
    }

    private void navigateToMonth(YearMonth targetYearMonth) {
        int maxAttempts = 100;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", new Locale("es", "ES"));
        By monthIndicator = By.xpath("(//android.view.View[@resource-id=\"android:id/month_view\"]/android.view.View[@content-desc])[15]");

        for (int i = 0; i < maxAttempts; i++) {
            String dayContentDesc = wait.until(ExpectedConditions.presenceOfElementLocated(monthIndicator)).getAttribute("content-desc");
            String cleanDateText = dayContentDesc.toLowerCase(new Locale("es", "ES")).trim();
            LocalDate currentDate = LocalDate.parse(cleanDateText, formatter);
            YearMonth currentYearMonth = YearMonth.from(currentDate);

            if (targetYearMonth.equals(currentYearMonth)) {
                return;
            }

            if (targetYearMonth.isBefore(currentYearMonth)) {
                click(AppiumBy.accessibilityId("Mes anterior"));
            } else {
                click(AppiumBy.accessibilityId("Mes siguiente"));
            }
            sleep(300);
        }
        throw new IllegalStateException("No se pudo llegar al mes/año objetivo: " + targetYearMonth);
    }

    protected void answerQuestion(String questionText, String decision) {
        String optionText = "SI".equalsIgnoreCase(decision) ? "Si" : "No";
        By dynamicLocator = By.xpath("//android.widget.TextView[normalize-space(@text)=\"" + questionText + "\"]/..//android.widget.TextView[normalize-space(@text)=\"" + optionText + "\"]");
        scrollToText(questionText, 0.25); 
        clickSafely(dynamicLocator);
        sleep(200);
    }

    protected void clickSafely(By locator) {
        int maxAttempts = 3;
        Exception lastError = null;

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
                element.click();
                return; 
            } catch (StaleElementReferenceException | ElementClickInterceptedException e) {
                lastError = e;
                sleep(300); 
            }
        }
        throw new RuntimeException("No se pudo hacer click en " + locator + " tras " + maxAttempts + " intentos.", lastError);
    }

    protected void takeEvidence(String orderIdentifier, String stepName) {
        try {
            String folderPath = "evidencias/orden_" + orderIdentifier;
            Files.createDirectories(Paths.get(folderPath));
            File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            File destFile = new File(folderPath + "/" + stepName + "_" + System.currentTimeMillis() + ".png");
            Files.copy(srcFile.toPath(), destFile.toPath());
            System.out.println("Evidencia guardada: " + destFile.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Error al guardar la evidencia: " + e.getMessage());
        }
    }

    protected boolean isFullyInViewport(WebElement element) {
        org.openqa.selenium.Rectangle rect = element.getRect();
        org.openqa.selenium.Dimension screen = driver.manage().window().getSize();
        return rect.getY() >= 0 && (rect.getY() + rect.getHeight()) <= screen.getHeight();
    }

    protected void scrollAndClickReliable(String anchorText, By targetLocator, double jumpPercent) {
        int maxAttempts = 3;
        Exception lastError = null;

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                scrollToText(anchorText, jumpPercent);
                WebElement target = wait.until(ExpectedConditions.visibilityOfElementLocated(targetLocator));

                if (!isFullyInViewport(target)) {
                    scrollDownPercent(-0.08); 
                    target = wait.until(ExpectedConditions.visibilityOfElementLocated(targetLocator));
                }

                target.click();
                return; 
            } catch (Exception e) {
                lastError = e;
                sleep(400);
            }
        }
        throw new RuntimeException("No se pudo hacer click en " + targetLocator + " tras " + maxAttempts + " intentos.", lastError);
    }
}