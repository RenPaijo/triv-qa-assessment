package com.mafaa.mobile.steps;

import com.mafaa.mobile.drivers.DriverManager;
import com.mafaa.mobile.pages.HomePage;
import com.mafaa.mobile.pages.RegisterPage;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

public class RegisterSteps {

    private final HomePage homePage = new HomePage(DriverManager.getDriver());
    private final RegisterPage registerPage = new RegisterPage(DriverManager.getDriver());

    @When("User opens the registration page")
    public void userOpensRegisterPage() {
        homePage.clickRegister();
        registerPage.openEmailRegistration();
    }

    @When("User submits the registration form without completing mandatory fields")
    public void userSubmitsEmptyForm() {
        registerPage.clickRegister();
    }

    @Then("The registration page should be displayed")
    public void verifyRegisterPage() {
        Assertions.assertTrue(registerPage.isRegisterPageDisplayed(),
                "The Register page was not displayed.");
    }

    @When("User submits a valid registration form with valid data")
    public void userSubmitsValidRegistration() {
        String email = System.getProperty("TRIV_REGISTER_EMAIL",
                "qa1005@test.com");
        registerPage.fillRegistrationForm("Test", "Automation", email, "Test12345!");
        registerPage.acceptTerms();
        registerPage.clickRegister();
    }

    @Then("The account activation page should be displayed")
    public void verifyActivationPage() {
        Assertions.assertTrue(registerPage.isActivationPageDisplayed(),
                "The Activate Your Account page was not displayed.");
    }

    @Then("Registration validation errors should be displayed")
    public void verifyRegisterError() {
        Assertions.assertFalse(registerPage.getErrorMessage().isBlank(),
                "The Register validation error was not displayed.");
    }
}
