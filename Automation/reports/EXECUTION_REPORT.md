# Execution Report — TRIV Android Automation

Last application execution: 2026-10-29 (UTC+07)  
Code audit/compile: 2026-10-30 (UTC+07)  
Device: Android 14 / API 34, `emulator-5554`, `sdk_gphone64_x86_64` (recorded session capabilities)  
Framework: Java 17 / Maven / JUnit 5 / Cucumber / Appium UiAutomator2

These are historical results, not a new full-suite run after the audit. See `final-evidence/README.md` for the evidence index, including the earlier REG-002 failure and subsequent focused PASS.

## Latest observed results

| ID | Observed result | Basis |
|---|---|---|
| LOG-001 | PASS | Login page title displayed. |
| LOG-002 | **BLOCKED — test data unavailable** | Both available dummy phone numbers are already bound. The first account is blocked while retaining its number; the second requires email activation and phone-number binding again. Reusing the registered number is rejected as already used. PIN/dashboard cannot be retested. |
| LOG-003 | PASS | Invalid dummy credentials produced `Credentials Don't Match`. |
| REG-001 | PASS | Registration form displayed. |
| REG-002 | PASS in focused rerun | Empty registration produced `This field cannot be empty`. |
| REG-003 | PASS | Valid random registration reached `Activate Your Account`. |

This is **five PASS and one latest BLOCKED**, not an all-green full suite. REG-002 has a focused rerun after improving its assertion. Historical `LOG-002` PASS does not change the latest blocker.

## Evidence mapping

- `LOG-001`, `LOG-003`, `REG-001`, `REG-003`: `final-evidence/final-non-otp-cucumber-report.html` (embedded step evidence).
- `REG-002`: `final-evidence/reg-002-pass/`.
- Latest `LOG-002`: `final-evidence/login-activation-required/`.
- Earlier `LOG-002` dashboard PASS, historical only: `final-evidence/login-pass-historical/Dashboard.png`.
- Ordinary generated evidence from prior exploratory runs remains outside these curated folders and is excluded from Git.

## Reproducible commands

From `Automation`, with the emulator and Appium running:

```powershell
mvn clean test "-DdeviceName=emulator-5554" "-Dudid=emulator-5554" "-Dcucumber.filter.tags=not @LOG-002"
```

`LOG-002` must run separately with fresh local `TRIV_EMAIL`, `TRIV_PASSWORD`, `TRIV_OTP`, and `TRIV_PIN`:

```powershell
mvn clean test "-DdeviceName=emulator-5554" "-Dudid=emulator-5554" "-Dcucumber.filter.tags=@LOG-002" "-DloginValidDataReady=true"
```

`LOG-002` is skipped by default unless `-DloginValidDataReady=true` is supplied, preventing an OTP request without usable account data. Do not rerun without fresh OTP, completed activation, valid phone binding, and authorized PIN. No email/SMS is read and no authentication is bypassed.

## Code audit — 2026-10-30

- `mvn clean test -Dcucumber.execution.dry-run=true`: **BUILD SUCCESS**; 6 scenarios and 26 steps, no undefined bindings. No application test was executed.
- Removed the unused OTP file handoff; `TRIV_OTP` is now required.
- Added explicit account-activation detection and OTP/PIN validation.
- Improved device/server configuration, registration checkbox handling, and guaranteed driver cleanup.
- Final audit: forwarded short Maven credential aliases through Surefire, defaulted `noReset=true`, simplified step-evidence capture, translated remaining messages, and corrected evidence paths.
- Final emulator execution after these changes is pending; results above are the latest observed runs from 2026-10-29.

## Notes

- The historical PASS dashboard text `Register Success` is retained as an observed app-state locator, not a claim that registration just occurred.
- Referral prompts after login are optional and dismissed when present.
- Empty registration and valid random registration use observed application text for assertions.
- Test data remains in configuration rather than Gherkin/Java source; OTP and PIN values are not included in this report.
