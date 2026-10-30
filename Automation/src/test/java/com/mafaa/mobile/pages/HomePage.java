package com.mafaa.mobile.pages;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class HomePage {

    private final AndroidDriver driver;
    private final WebDriverWait wait;

    @AndroidFindBy(id = "id.co.triv:id/label_dark")
    private WebElement darkThemeButton;

    @AndroidFindBy(id = "id.co.triv:id/btn_next")
    private WebElement nextButton;

    @AndroidFindBy(id = "id.co.triv:id/nav_wallet")
    private WebElement profileButton;

    @AndroidFindBy(id = "id.co.triv:id/btn_login")
    private WebElement walletLoginButton;

    @AndroidFindBy(uiAutomator = "new UiSelector().text(\"Login Now\")")
    private WebElement signInButton;

    @AndroidFindBy(id = "id.co.triv:id/btn_sign_up_email")
    private WebElement signUpButton;

    public HomePage(AndroidDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        PageFactory.initElements(new AppiumFieldDecorator(driver), this);
    }

    public boolean isDashboardDisplayed() {
        clickIfPresent("Maybe Later");
        clickIfPresent("Okay, Got it");
        return wait.until(ExpectedConditions.visibilityOfElementLocated(
                AppiumBy.androidUIAutomator(
                        "new UiSelector().text(\"Register Success\")"))).isDisplayed();
    }

    private void clickIfPresent(String text) {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(5)).until(
                    ExpectedConditions.elementToBeClickable(AppiumBy.androidUIAutomator(
                            "new UiSelector().text(\"" + text + "\")"))).click();
        } catch (TimeoutException ignored) {
            // Optional post-login dialogs vary between application sessions.
        }
    }

    public void openProfile() {
        completeThemeOnboardingIfPresent();
        wait.until(ExpectedConditions.visibilityOf(profileButton)).click();
        wait.until(ExpectedConditions.visibilityOf(walletLoginButton)).click();
    }

    private void completeThemeOnboardingIfPresent() {
        try {
            WebDriverWait onboardingWait = new WebDriverWait(driver, Duration.ofSeconds(5));
            onboardingWait.until(ExpectedConditions.elementToBeClickable(nextButton));
            for (int attempt = 0; attempt < 3; attempt++) {
                try {
                    Thread.sleep(2000);
                    onboardingWait.until(ExpectedConditions.elementToBeClickable(nextButton)).click();
                    wait.until(ExpectedConditions.visibilityOf(profileButton));
                    return;
                } catch (org.openqa.selenium.TimeoutException | InterruptedException ignored) {
                    if (ignored instanceof InterruptedException) {
                        Thread.currentThread().interrupt();
                        throw new IllegalStateException("Onboarding terinterupsi.", ignored);
                    }
                    // Retry Next if the theme animation consumed the click.
                }
            }
        } catch (org.openqa.selenium.TimeoutException ignored) {
            // Onboarding only appears after application data is reset.
        }
    }

    public void clickLogin() {
        openProfile();
        wait.until(ExpectedConditions.visibilityOf(signInButton)).click();
    }

    public void clickRegister() {
        openProfile();
        wait.until(ExpectedConditions.visibilityOf(signUpButton)).click();
    }
}
