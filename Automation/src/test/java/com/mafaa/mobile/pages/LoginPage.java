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

public class LoginPage {

    private final AndroidDriver driver;
    private final WebDriverWait wait;

    @AndroidFindBy(uiAutomator = "new UiSelector().text(\"Login\")")
    private WebElement loginPage;

    @AndroidFindBy(id = "id.co.triv:id/in_email")
    private WebElement emailField;

    @AndroidFindBy(id = "id.co.triv:id/in_password")
    private WebElement passwordField;


    @AndroidFindBy(uiAutomator = "new UiSelector().text(\"Credentials Don't Match\")")
    private WebElement errorMessage;

    public LoginPage(AndroidDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        PageFactory.initElements(new AppiumFieldDecorator(driver), this);
    }

    public void login(String email, String password) {
        wait.until(ExpectedConditions.visibilityOf(emailField)).sendKeys(email);
        passwordField.sendKeys(password);
        WebElement formLoginButton = driver.findElement(AppiumBy.androidUIAutomator(
                "new UiSelector().className(\"android.widget.Button\").text(\"Sign In\")"));
        wait.until(ExpectedConditions.elementToBeClickable(formLoginButton)).click();
    }

    public boolean isLoginPageDisplayed() {
        return wait.until(ExpectedConditions.visibilityOf(loginPage)).isDisplayed();
    }

    public String getErrorMessage() {
        return wait.until(ExpectedConditions.visibilityOf(errorMessage)).getText();
    }
}
