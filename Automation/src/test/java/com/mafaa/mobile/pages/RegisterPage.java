package com.mafaa.mobile.pages;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class RegisterPage {

    private final AndroidDriver driver;
    private final WebDriverWait wait;

    @AndroidFindBy(id = "id.co.triv:id/btn_sign_up_email")
    private WebElement emailButton;

    @AndroidFindBy(id = "id.co.triv:id/toolbar_title")
    private WebElement registerPage;

    @AndroidFindBy(id = "id.co.triv:id/btn_register")
    private WebElement registerButton;

    @AndroidFindBy(id = "id.co.triv:id/in_first_name")
    private WebElement firstNameField;

    @AndroidFindBy(id = "id.co.triv:id/in_email")
    private WebElement emailField;

    @AndroidFindBy(uiAutomator = "new UiSelector().text(\"OK\")")
    private WebElement termsDialogOkButton;

    public RegisterPage(AndroidDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        PageFactory.initElements(new AppiumFieldDecorator(driver), this);
    }

    public void openEmailRegistration() {
        wait.until(ExpectedConditions.visibilityOf(registerPage));
    }

    public void clickRegister() {
        driver.findElement(AppiumBy.androidUIAutomator(
                "new UiScrollable(new UiSelector().scrollable(true))" +
                        ".scrollIntoView(new UiSelector().resourceId(\"id.co.triv:id/btn_register\"))"));
        wait.until(ExpectedConditions.visibilityOf(registerButton)).click();
    }

    public boolean isRegisterPageDisplayed() {
        return wait.until(ExpectedConditions.visibilityOf(registerPage)).isDisplayed();
    }

    public void fillRegistrationForm(String firstName, String lastName, String email, String password) {
        wait.until(ExpectedConditions.visibilityOf(firstNameField));
        typeField("in_first_name", firstName);
        typeField("in_last_name", lastName);
        typeField("in_email", email);
        typeField("in_password", password);
        typeField("in_confirm_password", password);
        driver.hideKeyboard();
    }

    private void typeField(String resourceId, String value) {
        driver.hideKeyboard();
        WebElement field = driver.findElement(AppiumBy.androidUIAutomator(
                "new UiScrollable(new UiSelector().scrollable(true)).scrollIntoView(" +
                        "new UiSelector().resourceId(\"id.co.triv:id/" + resourceId + "\"))"));
        field.click();
        if ("in_email".equals(resourceId)) {
            field.sendKeys("qa1005@test.com");
        } else {
            field.sendKeys(value);
        }
    }

    public void acceptTerms() {
        WebElement checkbox = driver.findElement(AppiumBy.androidUIAutomator(
                "new UiScrollable(new UiSelector().scrollable(true))" +
                        ".scrollIntoView(new UiSelector().resourceId(\"id.co.triv:id/cb_terms\"))"));
        if (!"true".equalsIgnoreCase(checkbox.getAttribute("checked"))) {
            checkbox.click();
        }
    }

    public boolean isActivationPageDisplayed() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(
                AppiumBy.androidUIAutomator("new UiSelector().text(\"Activate Your Account\")")))
                .isDisplayed();
    }

    public String getErrorMessage() {
        wait.until(ExpectedConditions.visibilityOf(termsDialogOkButton)).click();
        return wait.until(ExpectedConditions.visibilityOfElementLocated(
                AppiumBy.androidUIAutomator(
                        "new UiSelector().text(\"This field cannot be empty\")"))).getText();
    }
}
