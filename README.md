# QA Technical Assessment — TRIV

## Project description

Repository for the TRIV QA Engineer Technical Assessment, containing **Withdraw Rupiah** manual test cases and Android automation for **Login** and **Register**. The automation uses Appium, Cucumber/Gherkin, Maven, and Page Object Model. It supports Android emulators and physical devices and runs through Maven commands.

**Submission Status: Partially Completed (with documented issues).** Five automation scenarios passed in the recorded executions before the latest refactor; these are not results from a new full-suite run. `LOG-002` is currently **BLOCKED** because no active dummy account with a usable phone number is available. Technical details are documented in [`KNOWN_ISSUES.md`](KNOWN_ISSUES.md).

## Deliverables

- **Test Case:** `Test Case/Test_Cases.xlsx` and `Test Case/Test_Cases.md` — 20 Withdraw Rupiah test cases: 4 Positive, 10 Negative, and 6 Boundary. All are design-only and **Not Executed**.
- **Automation:** Java 17, Appium UiAutomator2, Cucumber, JUnit 5, Maven, and Page Object Model.
- **Known issues:** `KNOWN_ISSUES.md`.
- **Code walkthrough:** `Automation/CODE_WALKTHROUGH.md` — explains every Page Object and Step Definition field, locator, parameter, and method.

## Tools and dependencies

| Component | Version |
|---|---|
| Java | Release 17; currently compiled with JDK 21.0.8 |
| Maven | 3.9.16 |
| Appium Java Client | 9.2.3 |
| Appium server | 3.7.0 (recorded execution) |
| Automation driver | UiAutomator2 |
| Cucumber | 7.20.1 |
| JUnit | 5.10.3 |
| Selenium API / BOM | 4.19.1 |
| Platform | Android 14 (API 34), emulator `emulator-5554` |

**Recorded execution environment:** Android 14 / API 34, Google `sdk_gphone64_x86_64`, `1080x2400`, from Appium session capabilities in the retained Cucumber report. Before a new execution, confirm the device has not changed with `adb -s emulator-5554 shell getprop ro.build.version.release` and `adb -s emulator-5554 shell getprop ro.build.version.sdk`.

Exact dependency configuration is in `Automation/pom.xml`.

## Part C: Test coverage and execution status

Environment: `emulator-5554`, Android, package `id.co.triv`, Java release 17, Appium 2. Last application execution was **2026-10-29**. The table below reports the latest observed application results, **not a new full-suite execution after the 2026-10-30 code audit**. A rerun may produce a different result; historical evidence is not claimed as a latest PASS.

| ID | Scenario | Latest observed result | Evidence / note |
|---|---|---|---|
| LOG-001 | Open the Login page | **PASS** | `Automation/reports/final-evidence/final-non-otp-cucumber-report.html` |
| LOG-002 | Valid login with OTP and PIN | **BLOCKED — test data unavailable** | Both available dummy phone numbers are already bound. The blocked first account still retains its number; the second account requires email activation and another phone-number binding. Reusing either number is rejected as already used. PIN/dashboard cannot be retested until authorized, usable account data is available. Previous activation evidence: `reports/final-evidence/login-activation-required/`; historical dashboard evidence: `reports/final-evidence/login-pass-historical/Dashboard.png`. |
| LOG-003 | Login with invalid credentials | **PASS** | Actual validation: `Credentials Don't Match`. |
| REG-001 | Open the Registration page | **PASS** | Registration form displayed. |
| REG-002 | Submit an empty Registration form | **PASS in focused rerun; earlier attempt FAILED** | The focused rerun confirmed `This field cannot be empty`. The initial attempt failed with `NoSuchElementException` while locating `textinput_error`; both results are retained. Latest evidence: `reports/final-evidence/reg-002-pass/`. |
| REG-003 | Register with valid data | **PASS** | Reached **Activate Your Account**. No email activation or OTP bypass is performed. Uses `qa1005@test.com`; replace this address in `RegisterPage` and `RegisterSteps` before rerunning because it is now registered. |

**Latest status: five PASS (including a focused REG-002 rerun), one BLOCKED due to unavailable usable dummy account/phone number.** This is a documented **Partially Completed** execution, not an all-green suite. Exact commands and limitations are in `Automation/reports/EXECUTION_REPORT.md`.

## Project structure

```text
triv-qa-assessment/
├── Test Case/
│   ├── Test_Cases.xlsx           # 20 Withdraw Rupiah test cases (source workbook)
│   └── Test_Cases.md             # Markdown version of the test cases
├── Automation/
│   ├── pom.xml
│   ├── CODE_WALKTHROUGH.md       # Page Object and Step Definition documentation
│   ├── src/test/
│   │   ├── java/com/mafaa/mobile/
│   │   │   ├── drivers/          # Appium session and capabilities
│   │   │   ├── hooks/            # setup/teardown and evidence capture
│   │   │   ├── pages/            # locators and UI actions (POM)
│   │   │   ├── runners/          # Cucumber/JUnit runner
│   │   │   ├── steps/            # Gherkin steps and assertions
│   │   │   └── support/          # secure test-data access
│   │   └── resources/features/   # Gherkin scenarios
│   └── reports/
│       ├── final-evidence/       # curated latest evidence per case
│       ├── historical-evidence/  # superseded runs, not latest results
│       └── EXECUTION_REPORT.md
├── README.md                     # project overview and execution instructions
├── KNOWN_ISSUES.md               # documented blockers and limitations
└── requirements.txt              # text extraction of the supplied assessment brief
```

## Prerequisites

1. JDK 17 and Maven.
2. Android emulator/device with the supplied TRIV APK installed.
3. `adb devices` shows `emulator-5554` (or configure another serial).
4. Appium and the UiAutomator2 driver; the recorded execution used Appium 3.7.0 and UiAutomator2 driver 8.7.0.

```powershell
appium --address 127.0.0.1 --port 4723
```

Defaults: Appium URL `http://127.0.0.1:4723/`, device/UDID `emulator-5554`, app package `id.co.triv`. Override with `appiumUrl`, `deviceName`, `udid`, or `appPackage` Maven properties when needed. App data is preserved by default (`noReset=true`); use `-DnoReset=false` only when a clean application state is intended.

## Run the five non-OTP scenarios

Run from `Automation`:

```powershell
mvn clean test "-DdeviceName=emulator-5554" "-Dudid=emulator-5554" "-Dcucumber.filter.tags=not @LOG-002"
```

## Run the full suite

Five scenarios execute; `LOG-002` is skipped before Appium starts and remains documented as **BLOCKED**:

```powershell
mvn clean test "-DdeviceName=emulator-5554" "-Dudid=emulator-5554"
```

## Run valid login (`@LOG-002`) separately — only after test data is available

No password, PIN, OTP, or OAuth credential is hardcoded in Gherkin or Java. Required local values:

- `TRIV_EMAIL` (`-DloginEmail=...`), `TRIV_PASSWORD` (`-DloginPassword=...`): authorized test-account credentials.
- `TRIV_OTP` (`-Dotp=...`): current 4–8 character alphanumeric OTP; mandatory and fresh for every attempt.
- `TRIV_PIN` (`-Dpin=...`): current account PIN (4–8 numeric digits).

These short Maven properties are forwarded to the test JVM. Keep environment-variable alternatives configured in the shell or IDE, not in a committed file.

Example PowerShell environment setup (replace placeholders locally):

```powershell
$env:TRIV_EMAIL = 'your-dummy@example.com'
$env:TRIV_PASSWORD = 'your-password'
$env:TRIV_OTP = 'fresh-code'
$env:TRIV_PIN = '1234'
mvn clean test "-DdeviceName=emulator-5554" "-Dudid=emulator-5554" "-Dcucumber.filter.tags=@LOG-002" "-DloginValidDataReady=true"
Remove-Item Env:TRIV_EMAIL,Env:TRIV_PASSWORD,Env:TRIV_OTP,Env:TRIV_PIN
```

**Current blocker:** both available dummy phone numbers are already bound. `@LOG-002` is skipped by default before Appium starts. Only add `-DloginValidDataReady=true` after an authorized active account with usable phone binding is available. Without this property, the blocker is explicit rather than creating a fresh OTP request.

The test does not read email/SMS, activate accounts, or bypass authentication. Optional referral prompts are dismissed. Activation or phone-number requirements abort the scenario with a blocked reason instead of a generic dashboard timeout. Do not rerun without a new OTP.

## Evidence and repeatability

- Cucumber HTML and JSON reports are generated by the JUnit Platform runner.
- `reports/screenshots/` contains a numbered screenshot after every Gherkin step.
- Every step saves a PNG screenshot and XML page source in `reports/screenshots/<scenario-id-name>/`. These files can contain UI identifiers and must be reviewed before sharing.
- `Automation/reports/EXECUTION_REPORT.md` contains the scenario execution report and result basis.
- `Automation/reports/final-evidence/README.md` indexes historical PASS, failure, and blocker evidence; it distinguishes these from a new run.
- `final-evidence/` contains selected latest PASS evidence and the `LOG-002` activation/phone-binding blocker. These are retained snapshots, not a continuously refreshed live dashboard.
- Historical `LOG-002` PASS evidence is explicitly separated from the latest blocker.
- Each scenario starts a fresh Appium session and closes it in `@After`.
- Ordinary generated reports overwrite on rerun; copy required evidence before running again.
- Part A is test design only; no withdrawal execution is claimed. The minimum-balance design follows `balance >= amount + fee` and does not add an amount-minimum assertion to `WD-003`.
- Validate compilation and Gherkin/step bindings without a device:
  ```powershell
  mvn clean test "-Dcucumber.execution.dry-run=true"
  ```
  Latest dry-run: **6 scenarios and 26 steps, BUILD SUCCESS**. Dry-run validates structure only; it is not evidence of application execution.

Review evidence before sharing: screenshots may contain account identifiers. Never publish passwords, PINs, OTPs, OAuth tokens, or local credential files.
