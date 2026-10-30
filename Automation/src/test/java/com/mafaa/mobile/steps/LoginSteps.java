package com.mafaa.mobile.steps;

import com.mafaa.mobile.drivers.DriverManager;
import com.mafaa.mobile.pages.HomePage;
import com.mafaa.mobile.pages.LoginPage;
import com.mafaa.mobile.pages.VerificationPage;
import com.mafaa.mobile.support.TestCredentials;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

public class LoginSteps {

    private final HomePage homePage = new HomePage(DriverManager.getDriver());
    private final LoginPage loginPage = new LoginPage(DriverManager.getDriver());

    @When("User opens the login page")
    public void userOpensLoginPage() {
        homePage.clickLogin();
    }

    @When("User logs in with email {string} and password {string}")
    public void userLogsIn(String email, String password) {
        loginPage.login(email, password);
    }

    @Then("The login page should be displayed")
    public void verifyLoginPage() {
        Assertions.assertTrue(loginPage.isLoginPageDisplayed(),
                "The Login page was not displayed.");
    }

    @When("User logs in with the configured valid credentials")
    public void userLogsInWithValidCredentials() {
        loginPage.login(TestCredentials.required("TRIV_EMAIL"),
                TestCredentials.required("TRIV_PASSWORD"));
    }

    @And("User completes login verification with the supplied OTP")
    public void userCompletesLoginVerification() {
        VerificationPage verificationPage = new VerificationPage(DriverManager.getDriver());
        String otp = TestCredentials.required("TRIV_OTP");
        verificationPage.enterVerificationCode(otp);
        verificationPage.ensureLoginCanContinue();
        if (verificationPage.isPinKeypadDisplayed()) {
            String pin = TestCredentials.required("TRIV_PIN");
            verificationPage.enterPin(pin);
        }
    }

    @Then("The user should be logged in successfully")
    public void verifySuccessfulLogin() {
        Assertions.assertTrue(homePage.isDashboardDisplayed(),
                "The dashboard or verified profile was not displayed after successful login.");
    }

    @Then("A login validation error should be displayed")
    public void verifyLoginError() {
        Assertions.assertFalse(loginPage.getErrorMessage().isBlank(),
                "The Login validation error was not displayed.");
    }
}
