Feature: TRIV mobile registration

  @REG-001
  Scenario: User opens the registration page
    Given User launches the TRIV application
    When User opens the registration page
    Then The registration page should be displayed

  @REG-002
  Scenario: User submits an empty registration form
    Given User launches the TRIV application
    And User opens the registration page
    When User submits the registration form without completing mandatory fields
    Then Registration validation errors should be displayed

  @REG-003
  Scenario: User registers with valid data and reaches account activation
    Given User launches the TRIV application
    And User opens the registration page
    When User submits a valid registration form with random data
    Then The account activation page should be displayed
