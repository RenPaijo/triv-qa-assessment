package com.mafaa.mobile.pages;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.opentest4j.TestAbortedException;

import java.time.Duration;
import java.util.Map;
import java.util.regex.Pattern;

public class VerificationPage {

    private static final By PIN_KEY = AppiumBy.id("id.co.triv:id/num1");
    private static final By OTP_INPUT = AppiumBy.id("id.co.triv:id/pin_box_container");
    private static final Pattern OTP_PATTERN = Pattern.compile("[A-Za-z0-9]{4,8}", Pattern.CASE_INSENSITIVE);

    private final AndroidDriver driver;
    private final WebDriverWait wait;

    public VerificationPage(AndroidDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(60));
    }

    public void enterVerificationCode(String code) {
        if (!OTP_PATTERN.matcher(code).matches()) {
            throw new IllegalArgumentException("TRIV_OTP harus berisi 4-8 karakter alfanumerik.");
        }
        wait.until(ExpectedConditions.elementToBeClickable(OTP_INPUT)).click();
        driver.executeScript("mobile: type", Map.of("text", code));
    }

    public void ensureLoginCanContinue() {
        By activationTitle = AppiumBy.androidUIAutomator(
                "new UiSelector().text(\"Activate Your Account\")");
        By optionalPrompt = AppiumBy.androidUIAutomator("new UiSelector().text(\"Maybe Later\")");
        By phoneNumberRequirement = AppiumBy.androidUIAutomator(
                "new UiSelector().textMatches(\"(?i).*phone.*(number|verification|required|already|used)"
                        + "|.*(nomor|verifikasi).*(hp|telepon|digunakan).*|.*(hp|telepon).*(wajib|digunakan).*\")");
        try {
            new WebDriverWait(driver, Duration.ofSeconds(90)).until(driver ->
                    !driver.findElements(PIN_KEY).isEmpty()
                            || !driver.findElements(optionalPrompt).isEmpty()
                            || !driver.findElements(activationTitle).isEmpty()
                            || !driver.findElements(phoneNumberRequirement).isEmpty());
        } catch (TimeoutException exception) {
            throw new IllegalStateException(
                    "After OTP, no recognized PIN, activation, phone-verification, or optional-prompt "
                            + "page appeared within 90 seconds.", exception);
        }
        if (!driver.findElements(activationTitle).isEmpty()
                || !driver.findElements(phoneNumberRequirement).isEmpty()) {
            throw new TestAbortedException(
                    "BLOCKED: email activation and/or phone-number verification is required. "
                            + "Both available dummy numbers are already bound; no usable new number is available.");
        }
        if (!driver.findElements(optionalPrompt).isEmpty()) {
            driver.findElement(optionalPrompt).click();
        } else if (!driver.findElements(PIN_KEY).isEmpty()) {
            return;
        }
    }

    public boolean isPinKeypadDisplayed() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(PIN_KEY)).isDisplayed();
        } catch (TimeoutException exception) {
            return false;
        }
    }

    public void enterPin(String pin) {
        if (!pin.matches("\\d{4,8}")) {
            throw new IllegalArgumentException("TRIV_PIN harus berisi 4-8 digit angka.");
        }
        wait.until(ExpectedConditions.elementToBeClickable(PIN_KEY));
        for (char digit : pin.toCharArray()) {
            wait.until(ExpectedConditions.elementToBeClickable(
                    AppiumBy.id("id.co.triv:id/num" + digit))).click();
        }
    }
}
