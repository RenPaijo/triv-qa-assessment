# Retained Evidence Index

Historical application-execution snapshots are from **2026-10-29**. They are not a new execution of the current code. The audited code was compiled and dry-run validated on **2026-10-30** without a device.

## Observed results — keep these runs separate

| Evidence | Actual recorded result | Interpretation |
|---|---|---|
| `final-non-otp-cucumber-report.html` / `.json` | **6/6 scenarios PASS**, including LOG-002 | Earlier full-suite run. It predates the current account/test-data blocker. The filename is historical and does not accurately describe its contents. |
| `screenshots/` and `../final-non-otp-cucumber-report.html` | LOG-001, LOG-003, REG-001, REG-003 PASS; REG-002 FAILED in this later batch | The later REG-002 attempt failed while locating `textinput_error`. Do not describe this batch as five PASS scenarios. |
| `reg-002-pass/` | REG-002 focused rerun PASS | `This field cannot be empty` was observed after the assertion/locator fix. |
| `login-activation-required/` | LOG-002 FAILED on the old dashboard assertion after reaching account activation | Evidence of a subsequent activation blocker, **not a PASS**. Current code detects this state and aborts it as blocked before asserting login success. |
| `login-blocked/` | LOG-002 account blocked | Evidence of the latest account-state blocker. |
| `login-pass-historical/Dashboard.png` | Earlier LOG-002 dashboard | Historical successful-login evidence only; not a new full-suite result. |

## Current blocker and commands

`LOG-002` is now intentionally skipped before Appium starts unless `loginValidDataReady=true` is supplied. The blocker is test-data availability: an active authorized account, usable phone number, and fresh OTP are required.

The command `mvn clean test` generates five executed scenarios and one skipped LOG-002 scenario. It must not be described as a historical six-PASS full-suite result without the run date/context. No new full-suite PASS is claimed after the audit.

Generated screenshots and page-source XML may contain account identifiers or other UI data. Review and sanitize them before public submission. Do not include real passwords, OTPs, PINs, or tokens.
