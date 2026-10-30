Feature: TRIV mobile login

  @LOG-001
  Scenario: User opens the login page
    Given User launches the TRIV application
    When User opens the login page
    Then The login page should be displayed

  @LOG-002
  Scenario: User logs in with valid credentials
    Given User launches the TRIV application
    And User opens the login page
    When User logs in with the configured valid credentials
    And User completes login verification with the supplied OTP
    Then The user should be logged in successfully

  @LOG-003
  Scenario: User logs in with invalid credentials
    Given User launches the TRIV application
    And User opens the login page
    When User logs in with email "qa.invalid@example.com" and password "Invalid123!"
    Then A login validation error should be displayed
