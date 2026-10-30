# Pages and Step Definitions — Code Walkthrough

## 0. Hooks and configuration supporting these classes

- `Hooks.setUp()` skips dry runs and the intentionally blocked `@LOG-002`, otherwise creates one Android session. `DriverManager` defaults to Android/UiAutomator2, emulator `emulator-5554`, app `id.co.triv`, `noReset=true`, and a 15-minute new-command timeout.
- `Hooks.captureStepEvidence()` saves a numbered PNG screenshot and XML page source after every step under `reports/screenshots/<scenario-id-name>/`. Evidence failures are logged rather than hiding the actual test result. `Hooks.tearDown()` closes the session.
- `TestCredentials.required()` reads a Java system property first and an environment variable second. The Maven aliases are `loginEmail`, `loginPassword`, `otp`, `pin`, and `registerEmail`. `TestRunner` selects `features`, binds `steps` and `hooks`, and writes HTML/JSON Cucumber reports.

## 1. Purpose and execution flow

This document explains the classes in `src/test/java/com/mafaa/mobile/pages/` and `src/test/java/com/mafaa/mobile/steps/`. It describes the responsibility of each field, locator, parameter, and method so that a reviewer can follow the automation without reading the implementation line by line.

The automation separates **Gherkin intent**, **application interaction**, and **test verification**:

```text
Cucumber feature (.feature)
        |
        v
Step Definition (steps/)
        |
        |-- supplies test data and calls Page Object methods
        |-- asserts the expected application result
        v
Page Object (pages/)
        |
        |-- locates Android elements
        |-- waits for UI state
        |-- clicks, types, reads text, or reports visibility
        v
AndroidDriver -> Appium UiAutomator2 -> TRIV application
```

### Page Objects versus Step Definitions

- A **Page Object** represents one application screen or closely related group of screens. It owns locators and low-level UI interactions. For example, `RegisterPage` knows how to scroll to a field, fill it, accept terms, and read the registration validation message.
- A **Step Definition** connects an English Gherkin sentence to Java. It coordinates Page Objects, supplies data, and performs assertions. For example, `RegisterSteps.verifyRegisterPage()` asserts that `registerPage.isRegisterPageDisplayed()` returns `true`.
- Page Objects generally do not decide whether a scenario passes. Step Definitions use JUnit assertions for that decision.
- `DriverManager.getDriver()` provides the Android session created by the test hook. It is passed to each Page Object; no Page Object creates its own Appium session.

### Common conventions

- `private final AndroidDriver driver` is the active Appium session used for direct element operations, scrolling, keyboard handling, and mobile commands.
- `private final WebDriverWait wait` provides condition-based waiting. Most pages use a 20-second timeout; `VerificationPage` uses 60 seconds for its general wait and 90 seconds for post-OTP navigation.
- `WebElement` fields annotated with `@AndroidFindBy` are Appium Page Factory locators. `PageFactory.initElements(new AppiumFieldDecorator(driver), this)` binds them to the current Android driver.
- `AppiumBy.androidUIAutomator(...)` locates elements through Android UiAutomator selectors. It is used for exact text, regular-expression text, and scrolling to resource IDs.
- `visibilityOf(...)` waits until an element is visible; `elementToBeClickable(...)` waits until it is visible and enabled for interaction.
- IDs such as `id.co.triv:id/in_email` are application resource IDs. Text locators such as `Sign In` depend on the displayed application language/version. Resource IDs are generally less presentation-dependent, but both must be updated if the application UI changes.

---

## 2. Page Objects

### `HomePage.java`

**Responsibility:** handle the dashboard/main page, optional referral prompt, Profile navigation, and entry points for Sign In and Sign Up.

#### Fields and locators

| Field | Purpose |
|---|---|
| `driver` | Runs direct Appium operations: UiScrollable navigation, prompt lookup, and prompt dismissal. |
| `wait` | Waits up to 20 seconds for dashboard, profile, or login/register entry elements. |
| `menuProfile` | Locates `id.co.triv:id/menu_profile`, the Profile tab used to reach account actions. |
| `signInButton` | Locates `id.co.triv:id/btn_sign_in`, the Sign In button in Profile. |
| `signUpButton` | Locates `id.co.triv:id/btn_sign_up`, the Sign Up button in Profile. |
| `dashboardMarker` | Locates `id.co.triv:id/balance_header`. Its visibility indicates that the main dashboard is present. |
| `profileTitle` | Locates text `Profil Saya`, used as a fallback when the main dashboard marker is not visible. |

#### Methods

| Method | Purpose and behavior |
|---|---|
| `HomePage(AndroidDriver driver)` | Stores the supplied session, creates the 20-second wait, and initializes Appium Page Factory fields. |
| `isDashboardDisplayed()` | Waits for either `balance_header` or `Profil Saya` to be visible. Returns `true` for either state and `false` on timeout. This supports the normal logged-in dashboard and the observed verified profile fallback. |
| `clickLogin()` | Calls `openProfile()`, then waits for and clicks Sign In. |
| `clickRegister()` | Calls `openProfile()`, then waits for and clicks Sign Up. |
| `dismissReferralPromptIfPresent()` | Checks immediately for exact text `Maybe Later`. If found, clicks it. An empty result means no prompt is present; this optional prompt is not treated as an error. |
| `openProfile()` | Dismisses the optional referral prompt. If `dashboardMarker` is already visible, it avoids unnecessary scrolling. Otherwise, it scrolls up to four screens in either direction looking for the dashboard marker. It then scrolls `menuProfile` into view and clicks it. |

`openProfile()` is private because it is navigation infrastructure, not an action intended to be called independently by Gherkin steps. The four-screen scroll bound prevents an unbounded search when the expected UI is absent.

### `LoginPage.java`

**Responsibility:** interact with the Login form and expose the Login title or invalid-credential error for assertions.

#### Fields and locators

| Field | Purpose |
|---|---|
| `driver` | Finds the form-specific Sign In button. This avoids relying on the Sign In button previously used in Profile. |
| `wait` | Waits up to 20 seconds for the email field, Login title, submit button, or error message. |
| `loginPage` | Locates exact text `Login`, used as the page-visible marker. |
| `emailField` | Locates `id.co.triv:id/in_email`, where the account email is entered. |
| `passwordField` | Locates `id.co.triv:id/in_password`, where the account password is entered. |
| `errorMessage` | Locates exact text `Credentials Don't Match`, used for the invalid-credential scenario. |

#### Methods

| Method | Purpose and behavior |
|---|---|
| `LoginPage(AndroidDriver driver)` | Stores the driver, creates the wait, and initializes the Page Factory fields. |
| `login(String email, String password)` | Waits for the email field and enters `email`; enters `password`; locates an Android button with exact text `Sign In`; waits until it is clickable and clicks it. The parameters are supplied by a Step Definition, not stored in the Page Object. |
| `isLoginPageDisplayed()` | Returns whether the Login title becomes visible. Used by `LOG-001`. |
| `getErrorMessage()` | Waits for the invalid-credential message and returns its text. Used by `LOG-003`. |

### `RegisterPage.java`

**Responsibility:** select email registration, fill and submit the form, accept terms, and expose registration or activation results.

#### Fields and locators

| Field | Purpose |
|---|---|
| `driver` | Finds and scrolls to form controls, clears/types into fields, and hides the keyboard. |
| `wait` | Waits up to 20 seconds for registration controls and result messages. |
| `emailButton` | Locates `id.co.triv:id/btn_sign_up_email`; selects registration by email. |
| `registerPage` | Locates `id.co.triv:id/toolbar_title`; serves as the registration-page marker. |
| `registerButton` | Locates `id.co.triv:id/btn_register`; submits the registration form. |
| `firstNameField` | Locates `id.co.triv:id/in_first_name`; used to wait until the form is ready before the helper fills all fields. |
| `emailField` | Locates `id.co.triv:id/in_email`; identifies the registration email input. The current helper uses resource IDs directly for form entry. |
| `termsDialogOkButton` | Locates exact text `OK`; dismisses the terms dialog shown when terms have not been accepted. |

#### Methods

| Method | Purpose and behavior |
|---|---|
| `RegisterPage(AndroidDriver driver)` | Stores the session, creates the wait, and initializes Page Factory elements. |
| `openEmailRegistration()` | Waits for and clicks the email-registration button. |
| `clickRegister()` | Scrolls `btn_register` into view, waits until visible, and clicks it. Used for both empty-form validation and valid-form submission. |
| `isRegisterPageDisplayed()` | Returns whether the registration toolbar title is visible. Used by `REG-001`. |
| `fillRegistrationForm(String firstName, String lastName, String email, String password)` | Waits for the first-name field, then fills first name, last name, email, password, and password confirmation. The same `password` value is used for both password inputs. Finally hides the keyboard. |
| `typeField(String resourceId, String value)` | Private helper: hides the keyboard, scrolls the requested `id.co.triv:id/<resourceId>` into view, clears existing text, and enters `value`. It reduces duplicated scrolling/filling logic. |
| `acceptTerms()` | Scrolls to `id.co.triv:id/cb_terms`. Reads its `checked` attribute and clicks only when it is not already `true`, preventing an already accepted checkbox from being toggled off. |
| `isActivationPageDisplayed()` | Waits for exact text `Activate Your Account` and returns its visibility. This is the expected result for `REG-003`; it does not claim that email activation itself has completed. |
| `getErrorMessage()` | Clicks the terms dialog's `OK`, then waits for and returns `This field cannot be empty`. Used by `REG-002` to verify required-field validation after dismissing the intermediate dialog. |

### `VerificationPage.java`

**Responsibility:** handle OTP entry, detect whether login can continue, dismiss an optional prompt, and enter the authorized PIN. This page participates only in `LOG-002`, which is currently blocked by account/phone-number test-data limitations.

#### Constants and fields

| Field | Purpose |
|---|---|
| `PIN_KEY` | Locates `id.co.triv:id/num1`, the PIN keypad's number-one key. It is used as the marker that the PIN screen is ready. |
| `OTP_INPUT` | Locates `id.co.triv:id/pin_box_container`, the OTP input container. |
| `OTP_PATTERN` | Accepts 4–8 alphanumeric characters. It validates supplied OTP format before any input is attempted; it does not validate whether the OTP is correct or current. |
| `driver` | Checks post-OTP page elements, enters OTP through `mobile: type`, clicks optional prompts, and clicks PIN keys. |
| `wait` | Waits up to 60 seconds for OTP/PIN controls. |

#### Methods and local variables

| Method or variable | Purpose and behavior |
|---|---|
| `VerificationPage(AndroidDriver driver)` | Stores the Android session and creates the 60-second wait. This class does not use Page Factory fields; its locators are `By` objects or local UiAutomator selectors. |
| `enterVerificationCode(String code)` | Validates `code` against `OTP_PATTERN`, clicks the OTP input, and types the value using Appium's `mobile: type` command. |
| `ensureLoginCanContinue()` | Waits up to 90 seconds for a known next state after OTP. Aborts as blocked if email activation or phone-number verification is required. Dismisses `Maybe Later` if present; returns immediately when the PIN keypad is already present. |
| `activationTitle` | Local locator for `Activate Your Account`, a blocked login state. |
| `optionalPrompt` | Local locator for `Maybe Later`, a dismissible intermediate prompt. |
| `phoneNumberRequirement` | Local case-insensitive text-pattern locator for English and Indonesian phone-number requirements/errors. It recognizes required, verification, already-used, and related phone-binding messages without attempting to bypass them. |
| `isPinKeypadDisplayed()` | Returns `true` if the PIN keypad appears within the general wait; returns `false` on timeout. The caller uses this to decide whether PIN input is applicable. |
| `enterPin(String pin)` | Requires 4–8 numeric digits, waits for the keypad, then clicks `num<digit>` once for each character in `pin`. |

`ensureLoginCanContinue()` throws `TestAbortedException` for activation/phone-binding blockers. This intentionally stops the scenario rather than waiting for a dashboard that cannot be reached or reporting the condition as PASS. A 90-second timeout with no recognized state is an execution error, not a successful login.

---

## 3. Step Definitions

### `CommonSteps.java`

**Responsibility:** define the Gherkin step shared by Login and Register scenarios.

| Member | Purpose |
|---|---|
| `@Given("User launches the TRIV application")` | Binds the common Given sentence to `userLaunchesTheTrivApplication()`. |
| `userLaunchesTheTrivApplication()` | Intentionally has no UI action. The `Hooks` setup has already initialized `AndroidDriver`, and Appium launches the configured application when the session is created. Keeping the explicit Gherkin step documents the initial state without launching the application a second time. |

### `LoginSteps.java`

**Responsibility:** implement Login navigation, credential submission, verification, and Login assertions.

#### Fields

| Field | Purpose |
|---|---|
| `homePage` | Navigates from the main application to Login and verifies the final dashboard state. It receives the active driver from `DriverManager`. |
| `loginPage` | Enters credentials and exposes Login title/error results. It shares the same Android session. |

`VerificationPage` is created only inside the verification step because it is needed only for the valid-login flow, not for opening Login or testing invalid credentials.

#### Methods and parameters

| Method / Gherkin binding | Purpose and behavior |
|---|---|
| `userOpensLoginPage()` — `When User opens the login page` | Calls `homePage.clickLogin()`. Used by `LOG-001`, `LOG-002`, and `LOG-003`. |
| `userLogsIn(String email, String password)` — `When User logs in with email {string} and password {string}` | Receives the two Gherkin string arguments and passes them to `loginPage.login(...)`. Used by `LOG-003` with invalid dummy data. |
| `verifyLoginPage()` — `Then The login page should be displayed` | Asserts that `loginPage.isLoginPageDisplayed()` is true. A false result fails the scenario with a missing-page message. |
| `userLogsInWithValidCredentials()` — `When User logs in with the configured valid credentials` | Reads required `TRIV_EMAIL` and `TRIV_PASSWORD` through `TestCredentials` and submits them. Values are supplied by environment variables or Maven properties, not embedded in this class. |
| `userCompletesLoginVerification()` — `And User completes login verification with the supplied OTP` | Creates `VerificationPage`, reads required `TRIV_OTP`, enters it, and calls `ensureLoginCanContinue()`. If the PIN keypad is displayed, reads required `TRIV_PIN` and enters it. Local variable `otp` is the one-run verification code; `pin` is read only if its page is actually reached. |
| `verifySuccessfulLogin()` — `Then The user should be logged in successfully` | Asserts that `homePage.isDashboardDisplayed()` is true. This is not reached when `LOG-002` is skipped or aborted by a blocker. |
| `verifyLoginError()` — `Then A login validation error should be displayed` | Retrieves the Login error text and asserts that it is not blank. Used by `LOG-003`. |

`TestCredentials.required(name)` supplies configuration values and fails clearly if a required value is absent. The values are not logged by these step methods.

### `RegisterSteps.java`

**Responsibility:** implement Register navigation, form submission, generated test data, and Register assertions.

#### Fields

| Field | Purpose |
|---|---|
| `homePage` | Opens Profile and selects Sign Up. |
| `registerPage` | Selects email registration, fills/submits the form, accepts terms, and exposes validation/activation outcomes. |

#### Methods and variables

| Method / Gherkin binding | Purpose and behavior |
|---|---|
| `userOpensRegisterPage()` — `When User opens the registration page` | Calls `homePage.clickRegister()` followed by `registerPage.openEmailRegistration()`. Used by all Register scenarios. |
| `userSubmitsEmptyForm()` — `When User submits the registration form without completing mandatory fields` | Clicks Register without filling mandatory fields. Used by `REG-002`. |
| `verifyRegisterPage()` — `Then The registration page should be displayed` | Asserts that the registration toolbar title is visible. Used by `REG-001`. |
| `userSubmitsValidRegistration()` — `When User submits a valid registration form with random data` | Builds an email, fills the form with dummy first name `Test`, last name `Automation`, and password `Test12345!`, accepts terms, and submits. Used by `REG-003`. |
| Local `email` in `userSubmitsValidRegistration()` | Uses the `TRIV_REGISTER_EMAIL` environment variable or `registerEmail` Maven property if provided. Otherwise, creates `qa.<currentTimeMillis>@example.com`. The timestamp reduces duplicate-email failures across runs; it does not create a real mailbox or promise email activation. |
| `verifyActivationPage()` — `Then The account activation page should be displayed` | Asserts that `Activate Your Account` is visible. This verifies successful registration submission up to the assignment's activation prerequisite, not actual inbox activation. |
| `verifyRegisterError()` — `Then Registration validation errors should be displayed` | Retrieves the required-field validation message and asserts that it is not blank. Used by `REG-002`. |

---

## 4. Scenario-to-method map

| Scenario | Main flow |
|---|---|
| `REG-001` | `CommonSteps` launch state -> `RegisterSteps.userOpensRegisterPage()` -> `HomePage.clickRegister()` -> `RegisterPage.openEmailRegistration()` -> `verifyRegisterPage()` assertion. |
| `REG-002` | Open Register -> `userSubmitsEmptyForm()` -> `RegisterPage.clickRegister()` -> `verifyRegisterError()` -> `RegisterPage.getErrorMessage()` assertion. |
| `REG-003` | Open Register -> `userSubmitsValidRegistration()` -> `fillRegistrationForm()` -> `acceptTerms()` -> `clickRegister()` -> `verifyActivationPage()` assertion. |
| `LOG-001` | `CommonSteps` launch state -> `LoginSteps.userOpensLoginPage()` -> `HomePage.clickLogin()` -> `verifyLoginPage()` assertion. |
| `LOG-002` | Currently skipped by default because authorized usable account/phone data is unavailable. When explicitly enabled: configured credentials -> `LoginPage.login()` -> OTP entry -> next-state detection -> conditional PIN entry -> dashboard assertion. Activation/phone-binding requirements abort as blocked. |
| `LOG-003` | Open Login -> Gherkin-provided invalid credentials -> `LoginPage.login()` -> `verifyLoginError()` assertion. |

## 5. Maintenance notes

- If a locator changes, update the Page Object that owns it rather than adding application selectors to Step Definitions.
- If expected behavior changes, update both the relevant Page Object result method and the Step Definition assertion. A method returning `true` only establishes that its locator was visible; the assertion is what makes that condition a test requirement.
- Do not reduce waits to arbitrary sleeps. Prefer a specific visible/clickable condition or a known next-state condition.
- The default full-suite run skips `LOG-002` before Appium starts. The blocker and its recovery conditions are documented in `KNOWN_ISSUES.md`; it is not counted as a passed valid-login execution.
- OTP and PIN values are secrets and one-run/session-sensitive data. This document describes their flow but intentionally contains no real values.
