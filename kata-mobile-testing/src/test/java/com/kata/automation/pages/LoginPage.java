package com.kata.automation.pages;

import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;


public class LoginPage {
    private AndroidDriver driver;
    private WebDriverWait wait;

    // 1. Localizadores
    private By continueFirstButton = By.id("com.kata.mobile:id/fragmentCurrentSessionFloatingActionButton");
    private By userNameField = By.id("com.kata.mobile:id/fragmentCredentialsUserTextInputEditText"); 
    private By passwordField = By.id("com.kata.mobile:id/fragmentLoginCredentialsPasswordTextInputEditText"); 
    private By submitLogin = By.id("com.kata.mobile:id/fragmentCredentialsLoginButton"); 
    private By okAlertButton = By.id("android:id/button1");
    
    

    // Constructor: Inicializa el driver y un wait explícito
    public LoginPage(AndroidDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }

    public void firstContinueClickButton() {
        driver.findElement(continueFirstButton).click();
        System.out.println("Verificando si aparece el Autofill de contraseñas...");
        try {
            // Usamos el ID exacto que muestra tu Inspector para el contenedor principal
            By autofillDialog = By.id("android:id/autofill_dialog_picker");
            
            // Espera corta de 3 segundos
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(3));
            shortWait.until(ExpectedConditions.visibilityOfElementLocated(autofillDialog));
            
            System.out.println("Autofill detectado. Cerrando diálogo...");
            
            // Esta es la línea de código clave que simula la tecla "Atrás" del celular
            driver.navigate().back(); 
            
        } catch (TimeoutException e) {
            System.out.println("No apareció el Autofill. Continuando con el login...");
        }
    }

    public void writeUserName(String userName) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(userNameField)).sendKeys(userName);
    }

    public void writePassword(String password) {
        driver.findElement(passwordField).sendKeys(password);
    }

    public void clickSubmit() {
        driver.findElement(submitLogin).click();
    }

    public void clickOkIfAppears() {
        try {
            WebDriverWait alertWait = new WebDriverWait(driver, Duration.ofSeconds(5));
            alertWait.until(ExpectedConditions.elementToBeClickable(okAlertButton)).click();
            
            // Clicl a OK despues de alerta
            wait.until(ExpectedConditions.elementToBeClickable(submitLogin)).click();
        } catch (TimeoutException e) {
            System.out.println("Algo salio mal con la alerta despues de ingresar credenciales...");
        }
    }

    public void syncronizeEventsCalendar() {
        By calendarCard = By.xpath("//android.view.ViewGroup/android.view.View/android.view.View/android.view.View[1]");
        WebDriverWait longWait = new WebDriverWait(driver, Duration.ofSeconds(90));

        longWait.until(ExpectedConditions.visibilityOfElementLocated(calendarCard));

        By firstRadioButton = By.xpath("//android.view.ViewGroup/android.view.View/android.view.View/android.view.View[1]/android.view.View[1]/android.widget.RadioButton");

        wait.until(ExpectedConditions.elementToBeClickable(firstRadioButton)).click();

        By okButtonForCalendar = By.xpath("//android.widget.Button");
        wait.until(ExpectedConditions.elementToBeClickable(okButtonForCalendar)).click();
    }

    public void clickContinueAfterCalendar() {
        By continueButton = By.id("com.kata.mobile:id/activityDownloadContinueButton");
        wait.until(ExpectedConditions.elementToBeClickable(continueButton)).click();

        By ordersMenu = By.xpath("//android.widget.TextView[@text=\"Órdenes\"]");
        WebDriverWait longWait = new WebDriverWait(driver, Duration.ofSeconds(20));

        longWait.until(ExpectedConditions.visibilityOfElementLocated(ordersMenu));
    }

    // Método de alto nivel que consolida todo el flujo de inicio de sesión
    public void doLogin(String password) {
    // Si la app quedó con sesión previa (cache viva), aparece este botón
    // if (!driver.findElements(continueFirstButton).isEmpty()) {
    //     firstContinueClickButton(); // ya maneja el autofill dialog también
    // } else {
    //     // Solo si es login "limpio" (primera vez) se escribe el usuario
    //     writeUserName(userName);
    // }
        writePassword(password);
    }

    public void afterLogin() {
        clickSubmit();
        clickContinueAfterCalendar();
    }
}