# Known Issues and Follow-up

## APP-002 — No usable dummy account/phone number for valid login

- **Issue:** Both available dummy phone numbers are already bound to accounts. The first account is blocked but still retains its phone number. The second account asks for email activation and phone-number binding again; reusing its registered number is rejected as already used.
- **Affected Scenario:** `LOG-002`.
- **Root Cause / Indication:** Test-data limitation: account block, email activation, and phone-number uniqueness. No authorized unused phone number remains. This is not diagnosed as an automation defect.
- **Troubleshooting:** Used both available dummy accounts. The first reached `User blocked!`; the second reached `Activate Your Account` after credentials/OTP and could not bind an already-used phone number. No further accounts were created and no third-party phone numbers were attempted.
- **Current Status:** **BLOCKED — no usable authorized dummy account/phone number.** The previous dashboard run is retained as historical evidence only, not claimed as the current result.
- **Evidence:** `Automation/reports/final-evidence/login-activation-required/` and `Automation/reports/final-evidence/login-blocked/`.
- **Next Step:** Obtain an authorized unblocked account whose activation and phone binding are valid, or ask the account owner/support to unblock/reset phone-number binding. Rerun `@LOG-002` with fresh OTP and authorized PIN after usable test data is available.

## APP-001 — Previous valid-login account was blocked

- **Issue:** On 2026-10-29 at approximately 21:43 +07, the previous account displayed `User blocked!` after credentials were submitted.
- **Affected Scenario:** `LOG-002`.
- **Root Cause / Indication:** Account-level block; the UI does not reveal why it occurred.
- **Troubleshooting:** Stopped before OTP; no OTP was submitted. Avoided repeated blocked-account attempts and moved to the replacement dummy account, whose current blocker is documented in APP-002.
- **Current Status:** **Unresolved for the previous account; historical only.** Latest status is APP-002.
- **Evidence:** `Automation/reports/final-evidence/login-blocked/Step-4.png` and `Step-5.png`.
- **Next Step:** Account owner/support must unblock the authorized account, or testing should continue with another authorized activated account.

## AUT-001 — Generated reports overwrite by default

- **Issue:** Repeating a scenario overwrites the default Cucumber report and per-step screenshots.
- **Affected Scenario:** All scenarios.
- **Root Cause / Indication:** Report paths are fixed in the current runner/hooks.
- **Troubleshooting:** Selected PASS evidence was copied to case-specific folders before later runs; focused Cucumber reports use distinct names.
- **Current Status:** **Documented automation limitation; not an application defect.**
- **Evidence:** `README.md` and `Automation/reports/EXECUTION_REPORT.md`.
- **Next Step:** Copy required artifacts before every rerun. For CI, add timestamp/run-ID output directories.

## AUT-002 — Test-data persistence

- **Issue:** Reusing a registered email causes `Email already registered`.
- **Affected Scenario:** `REG-003`.
- **Root Cause / Indication:** Registration account data persists server-side even when execution stops at activation.
- **Troubleshooting:** A fresh random email and valid password are generated per run. `@example.com` addresses intentionally do not claim mailbox access or activation.
- **Current Status:** **Documented automation limitation; not an application defect.**
- **Evidence:** `Automation/reports/final-evidence/final-non-otp-cucumber-report.html`; REG-003's final step shows the activation-page result.
- **Next Step:** For fully repeatable activated-account testing, provide a controlled staging mailbox and supported test-data cleanup/API.

## Scope note — Email OTP and PIN

`REG-003` verifies successful submission through the activation page, matching the supplied expected result. It does not read an inbox, activate an account, or bypass OTP. `LOG-002` uses a tester-supplied OTP and authorized PIN only to test login. No account or credential security is bypassed.
