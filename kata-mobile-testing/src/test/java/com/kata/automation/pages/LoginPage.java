package com.kata.automation.pages;

import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;

public class LoginPage extends BasePage {

    // 1. Localizadores
    private static final By CONTINUE_FIRST_BUTTON = By
            .id("com.kata.mobile:id/fragmentCurrentSessionFloatingActionButton");
    private static final By PASSWORD_FIELD = By
            .id("com.kata.mobile:id/fragmentLoginCredentialsPasswordTextInputEditText");
    private static final By SUBMIT_lOGIN = By.id("com.kata.mobile:id/fragmentCredentialsLoginButton");
    private static final By CONTINUE_LAST_BUTTON = By.id("com.kata.mobile:id/activityDownloadContinueButton");
    private static final By AUTO_SAVE_ALERT = By.id("android:id/autofill_save");
    private static final By CANCEL_AUTO_SAVE = By.id("android:id/autofill_save_no");

    // Constructor: Inicializa el driver y un wait explícito
    public LoginPage(AndroidDriver driver) {
        super(driver);
    }

    // Método de alto nivel que consolida todo el flujo de inicio de sesión
    public void doLogin(String password) {
        click(CONTINUE_FIRST_BUTTON);
        type(PASSWORD_FIELD, password);
        takeEvidence("Analista_data", "Credenciales inicio de sesion");
        click(SUBMIT_lOGIN);
        try {
            waitForVisibility(AUTO_SAVE_ALERT);
            System.out.println("Auto save detectado");
            click(CANCEL_AUTO_SAVE);
            System.out.println("Cerrando Auto save");
        } catch (Exception e) {
            System.out.println("No se encontro el autosave");
        }
        click(CONTINUE_LAST_BUTTON);

    }
}