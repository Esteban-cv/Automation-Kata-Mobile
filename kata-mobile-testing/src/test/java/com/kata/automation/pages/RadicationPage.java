package com.kata.automation.pages;

import org.openqa.selenium.By;

import io.appium.java_client.android.AndroidDriver;

public class RadicationPage extends BasePage {

    public RadicationPage(AndroidDriver driver) {
        super(driver); // Envía el driver a BasePage
    }

    // LOCALIZADORES DE LA PAGINA DE RADICACION

    private static final By RADICATE_BUTTON = By.xpath(
        "//android.widget.TextView[@text=\"Radicación\"]");
    private static final By DEPARTMENT_DROPDOWN = By.xpath(
        "//android.widget.TextView[@text=\"Departamento de nacimiento del solicitante\"]/following-sibling::android.view.View[@content-desc=\"Menú desplegable\"]");
    private static final By CITY_DROPDOWN = By.xpath(
        "//android.widget.TextView[@text=\"Ciudad de nacimiento del solicitante\"]/following-sibling::android.view.View[@content-desc=\"Menú desplegable\"]");
    private static final By EXPEDITION_DATE = By.xpath(
        "//android.widget.TextView[@text=\"Fecha de expedición de identificación del solicitante\"]/following-sibling::android.widget.EditText");
    private static final By EXPEDITION_DEPARTMENT_CLIENT_DOCUMENT = By.xpath(
        "//android.widget.TextView[@text=\"Departamento de expedición de identificación del solicitante\"]/following-sibling::android.view.View[@content-desc=\"Menú desplegable\"]");
    
    
        public void radicationProcess(String departmentBirth, String cityBirth, int expDay, int expMounth, int expYear) {
        // Click en el boton de radicacion
        click(RADICATE_BUTTON);

        scrollAndClickReliable("Departamento de nacimiento del solicitante", DEPARTMENT_DROPDOWN, 0.251);
        click(byVisibleText(departmentBirth));

        scrollAndClickReliable("Ciudad de nacimiento del solicitante", CITY_DROPDOWN, 0.10);
        click(byVisibleText(cityBirth));

        click(EXPEDITION_DATE);
        selectDateInCalendar(expDay, expMounth, expYear);

        scrollToText("Ciudad de expedición de identificación del solicitante", 0.10);

        clickComposeDropdown(EXPEDITION_DEPARTMENT_CLIENT_DOCUMENT);
        click(byVisibleText(departmentBirth));
    }

}
