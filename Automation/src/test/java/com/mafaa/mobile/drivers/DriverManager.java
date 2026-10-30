package com.mafaa.mobile.drivers;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;

import java.net.URI;
import java.time.Duration;

public class DriverManager {

    private static AndroidDriver driver;

    public static AndroidDriver getDriver() {
        return driver;
    }

    public static void initializeDriver() {
        UiAutomator2Options options = new UiAutomator2Options()
                .setPlatformName("Android")
                .setAutomationName("UiAutomator2")
                .setDeviceName(System.getProperty("deviceName", "emulator-5554"))
                .setUdid(System.getProperty("udid", "emulator-5554"))
                .setAppPackage(System.getProperty("appPackage", "id.co.triv"))
                .setAppActivity(System.getProperty("appActivity", "id.co.triv.main.splash.SplashActivity"))
                .setAutoGrantPermissions(true)
                .amend("appium:forceAppLaunch", true)
                .setNoReset(Boolean.parseBoolean(System.getProperty("noReset", "true")))
                .setNewCommandTimeout(Duration.ofMinutes(15));

        try {
            driver = new AndroidDriver(
                    URI.create(System.getProperty("appiumUrl", "http://127.0.0.1:4723/")).toURL(), options);
        } catch (Exception exception) {
            throw new RuntimeException("Failed to create the Android driver. "
                    + "Check Appium, the Android device, and configured capabilities.", exception);
        }
    }

    public static void quitDriver() {
        if (driver != null) {
            try {
                driver.quit();
            } finally {
                driver = null;
            }
        }
    }
}
