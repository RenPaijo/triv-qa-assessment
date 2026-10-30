# Test Cases — Withdraw Rupiah (TRIV QA Assessment)

Dokumen ini menjelaskan seluruh test case Part A beserta asumsi dan data uji. Isi disalin verbatim dari `Test_Cases.xlsx` (sumber utama). Seluruh test case berstatus design-only: Actual Result = N/A, Status = Not Executed.

Komposisi: 20 test case — 10 Negative, 6 Boundary, 4 Positive. Mencakup 8 kategori wajib: Positive, Negative, Boundary, Balance Validation, Bank Validation, OTP Verification, Transaction Validation, Duplicate Prevention.

## WD-001 — Valid Rp50.000 withdrawal to registered BCA account ends Completed with correct deduction

- **Feature:** Withdraw Rupiah - End to End
- **Test Type:** Positive | **Priority:** High
- **Preconditions:** Logged in as QA001 | Initial Balance Rp200.000 | Bank BCA 1234567890 a.n Budi QA (verified) | Fee Rp2.500 | OTP valid 123456 (60s)
- **Test Data:** Nominal=50000 | OTP=123456 (valid, <60s)
- **Test Steps:**
  1. Open Withdraw page
  2. Select registered BCA account
  3. Input 50000
  4. Tap Lanjut
  5. Verify confirmation page
  6. Request OTP and input 123456 within 60s
  7. Confirm
  8. Check status, balance and history
- **Expected Result:** Confirmation: Rp50.000, BCA ****7890 a.n Budi QA, fee Rp2.500, total Rp52.500 | Completed | Balance Rp147.500 | One matching history entry
- **Actual Result:** N/A | **Status:** Not Executed

## WD-002 — Boundary: minimum amount Rp10.000 is accepted

- **Feature:** Withdraw Rupiah - Amount
- **Test Type:** Boundary | **Priority:** High
- **Preconditions:** Logged in as QA001 | Initial Balance Rp200.000 | Bank BCA 1234567890 a.n Budi QA (verified) | Fee Rp2.500 | OTP valid 123456 (60s)
- **Test Data:** Nominal=10000 (min, inclusive) | OTP=123456
- **Test Steps:**
  1. Open Withdraw page
  2. Select BCA account
  3. Input 10000
  4. Lanjut
  5. Verify confirmation (Total Rp12.500)
  6. Request OTP and input 123456 within 60s
  7. Confirm
- **Expected Result:** Completed | Total Rp12.500 | Balance Rp187.500 | One history entry
- **Actual Result:** N/A | **Status:** Not Executed

## WD-003 — Boundary: maximum amount Rp100.000 is accepted

- **Feature:** Withdraw Rupiah - Amount
- **Test Type:** Boundary | **Priority:** High
- **Preconditions:** Logged in as QA001 | Initial Balance Rp200.000 | Bank BCA 1234567890 a.n Budi QA (verified) | Fee Rp2.500 | OTP valid 123456 (60s)
- **Test Data:** Nominal=100000 (max, inclusive) | OTP=123456
- **Test Steps:**
  1. Open Withdraw page
  2. Select BCA account
  3. Input 100000
  4. Lanjut
  5. Verify confirmation (Total Rp102.500)
  6. Request OTP and input 123456 within 60s
  7. Confirm
- **Expected Result:** Completed | Total Rp102.500 | Balance Rp97.500 | One history entry
- **Actual Result:** N/A | **Status:** Not Executed

## WD-004 — Boundary: amount below minimum (Rp9.999) is rejected

- **Feature:** Withdraw Rupiah - Amount
- **Test Type:** Boundary | **Priority:** High
- **Preconditions:** Logged in as QA001 | Initial Balance Rp200.000 | Bank BCA 1234567890 a.n Budi QA (verified) | Fee Rp2.500 | OTP valid 123456 (60s)
- **Test Data:** Nominal=9999
- **Test Steps:**
  1. Open Withdraw page
  2. Select BCA account
  3. Input 9999 (min-1)
  4. Tap Lanjut
- **Expected Result:** Rejected with a clear minimum-withdrawal validation message | No OTP request | Balance remains Rp200.000
- **Actual Result:** N/A | **Status:** Not Executed

## WD-005 — Boundary: amount above maximum (Rp100.001) is rejected

- **Feature:** Withdraw Rupiah - Amount
- **Test Type:** Boundary | **Priority:** High
- **Preconditions:** Logged in as QA001 | Initial Balance Rp200.000 | Bank BCA 1234567890 a.n Budi QA (verified) | Fee Rp2.500 | OTP valid 123456 (60s)
- **Test Data:** Nominal=100001
- **Test Steps:**
  1. Open Withdraw page
  2. Select BCA account
  3. Input 100001 (max+1)
  4. Tap Lanjut
- **Expected Result:** Rejected with a clear maximum-per-transaction validation message | No OTP request | Balance unchanged
- **Actual Result:** N/A | **Status:** Not Executed

## WD-006 — Empty nominal is rejected with validation message

- **Feature:** Withdraw Rupiah - Amount
- **Test Type:** Negative | **Priority:** High
- **Preconditions:** Logged in as QA001 | Initial Balance Rp200.000 | Bank BCA 1234567890 a.n Budi QA (verified) | Fee Rp2.500 | OTP valid 123456 (60s)
- **Test Data:** Nominal=(empty)
- **Test Steps:**
  1. Open Withdraw page
  2. Select BCA account
  3. Leave nominal empty
  4. Tap Lanjut
- **Expected Result:** Rejected with a clear required-nominal validation message | Cannot proceed to confirmation or OTP | Balance unchanged
- **Actual Result:** N/A | **Status:** Not Executed

## WD-007 — Non-positive-integer nominal is rejected (0, negative, decimal, text)

- **Feature:** Withdraw Rupiah - Amount
- **Test Type:** Negative | **Priority:** High
- **Preconditions:** Logged in as QA001 | Initial Balance Rp200.000 | Bank BCA 1234567890 a.n Budi QA (verified) | Fee Rp2.500 | OTP valid 123456 (60s)
- **Test Data:** Nominal in {0, -5000, 50.5, abc}
- **Test Steps:**
  1. Open Withdraw page
  2. Select BCA account
  3. Try each value: 0 | -5000 | 50.5 | abc
  4. Tap Lanjut each time
- **Expected Result:** Each value rejected with a clear positive-integer validation message | Cannot proceed | Balance unchanged | Decimals rejected, not rounded
- **Actual Result:** N/A | **Status:** Not Executed

## WD-008 — Insufficient balance (amount + fee > balance) is rejected

- **Feature:** Withdraw Rupiah - Balance
- **Test Type:** Negative | **Priority:** High
- **Preconditions:** Logged in as QA001 | Initial Balance Rp100.000 (scenario-specific override) | Bank BCA 1234567890 a.n Budi QA (verified) | Fee Rp2.500 | OTP valid 123456 (60s)
- **Test Data:** Nominal=100000 (valid maximum) | Total needed Rp102.500 | Available balance Rp100.000
- **Test Steps:**
  1. Open Withdraw page
  2. Select BCA account
  3. Input 100000 (needs Rp102.500 > Rp100.000)
  4. Lanjut
- **Expected Result:** Rejected with a clear insufficient-balance validation message | Balance remains Rp100.000 | No transaction (A-11)
- **Actual Result:** N/A | **Status:** Not Executed

## WD-009 — Boundary: balance exactly equals amount + fee is accepted (remaining Rp0)

- **Feature:** Withdraw Rupiah - Balance
- **Test Type:** Boundary | **Priority:** High
- **Preconditions:** Logged in as QA001 | Initial Balance Rp102.500 (scenario-specific override) | Bank BCA 1234567890 a.n Budi QA (verified) | Fee Rp2.500 | OTP valid 123456 (60s)
- **Test Data:** Nominal=100000 (valid maximum) | OTP=123456 | Initial balance Rp102.500
- **Test Steps:**
  1. Open Withdraw page
  2. Select BCA account
  3. Input 100000 (100.000+2.500=102.500)
  4. Lanjut
  5. Request OTP and input 123456 within 60s
  6. Confirm and check balance
- **Expected Result:** Completed | Total Rp102.500 | Balance Rp0, never negative | One matching history entry
- **Actual Result:** N/A | **Status:** Not Executed

## WD-010 — Cannot proceed without selecting a destination bank account

- **Feature:** Withdraw Rupiah - Bank
- **Test Type:** Negative | **Priority:** High
- **Preconditions:** Logged in as QA001 | Initial Balance Rp200.000 | Bank BCA 1234567890 a.n Budi QA (verified) | Fee Rp2.500 | OTP valid 123456 (60s) | No account selected (see Q-08)
- **Test Data:** Nominal=50000 | Bank=(none selected)
- **Test Steps:**
  1. Open Withdraw page
  2. Do NOT select bank account
  3. Input 50000
  4. Tap Lanjut
- **Expected Result:** Rejected with a clear destination-account-required message | Cannot proceed | Balance unchanged
- **Actual Result:** N/A | **Status:** Not Executed

## WD-011 — Unavailable or unverified destination account cannot be used

- **Feature:** Withdraw Rupiah - Bank
- **Test Type:** Negative | **Priority:** High
- **Preconditions:** Logged in as QA001 | Initial Balance Rp200.000 | Bank BCA 1234567890 a.n Budi QA (verified) | Fee Rp2.500 | OTP valid 123456 (60s) | Account list contains an unverified/inactive/deleted account (see Q-07)
- **Test Data:** Nominal=50000 | Bank=unverified/inactive/deleted account
- **Test Steps:**
  Execute separate runs for unverified, inactive, and deleted accounts. For each run:
  1. Open Withdraw page
  2. Attempt to select the account in that state
  3. Input 50000
  4. Tap Lanjut
  5. Record the result for that account state
- **Expected Result:** Each unavailable account is hidden or blocked with a clear message | No transaction | Balance unchanged (Q-07)
- **Actual Result:** N/A | **Status:** Not Executed

## WD-012 — Confirmation page shows masked account, fee and correct total deduction

- **Feature:** Withdraw Rupiah - Confirmation
- **Test Type:** Positive | **Priority:** Medium
- **Preconditions:** Logged in as QA001 | Initial Balance Rp200.000 | Bank BCA 1234567890 a.n Budi QA (verified) | Fee Rp2.500 | OTP valid 123456 (60s)
- **Test Data:** Nominal=50000
- **Test Steps:**
  1. Open Withdraw page
  2. Select BCA 1234567890 Budi QA
  3. Input 50000
  4. Tap Lanjut
  5. Inspect confirmation page
- **Expected Result:** Confirmation matches nominal, BCA ****7890 a.n Budi QA, fee Rp2.500, and total Rp52.500
- **Actual Result:** N/A | **Status:** Not Executed

## WD-013 — Boundary: valid 6-digit OTP submitted at second 59 is accepted

- **Feature:** Withdraw Rupiah - OTP
- **Test Type:** Boundary | **Priority:** High
- **Preconditions:** Logged in as QA001 | Initial Balance Rp200.000 | Bank BCA 1234567890 a.n Budi QA (verified) | Fee Rp2.500 | OTP valid 123456 (60s)
- **Test Data:** Nominal=50000 | OTP=123456 at second 59
- **Test Steps:**
  1. Complete amount+confirm steps for 50000
  2. Request OTP and note issuance time
  3. At ~59s input 123456
  4. Submit
- **Expected Result:** Accepted at second 59 | Completed | Balance Rp147.500 | One history entry
- **Actual Result:** N/A | **Status:** Not Executed

## WD-014 — Incorrect or malformed OTP is rejected (wrong code, wrong length, non-digit)

- **Feature:** Withdraw Rupiah - OTP
- **Test Type:** Negative | **Priority:** High
- **Preconditions:** Logged in as QA001 | Initial Balance Rp200.000 | Bank BCA 1234567890 a.n Budi QA (verified) | Fee Rp2.500 | OTP valid 123456 (60s)
- **Test Data:** OTP in {654321, 12345, 1234567, abc123, empty}
- **Test Steps:**
  Execute each OTP value as a separate run. For each run:
  1. Complete amount+confirm for 50000
  2. Request a fresh OTP
  3. Input one value: 654321 (wrong) | 12345 (5 digits) | 1234567 (7 digits) | abc123 (non-digit) | empty
  4. Submit
  5. Record the result for that OTP value
- **Expected Result:** Each value rejected with a clear invalid-OTP validation message | No transaction | Balance remains Rp200.000
- **Actual Result:** N/A | **Status:** Not Executed

## WD-015 — Expired OTP (older than 60 seconds) is rejected

- **Feature:** Withdraw Rupiah - OTP
- **Test Type:** Negative | **Priority:** High
- **Preconditions:** Logged in as QA001 | Initial Balance Rp200.000 | Bank BCA 1234567890 a.n Budi QA (verified) | Fee Rp2.500 | OTP valid 123456 (60s)
- **Test Data:** Nominal=50000 | OTP=123456 issued >60s ago
- **Test Steps:**
  1. Complete amount+confirm for 50000
  2. Request OTP, wait >60s (e.g. 65-70s)
  3. Input the now-expired code 123456
  4. Submit
- **Expected Result:** After 60s rejected with a clear expired-OTP validation message | No balance change | Request a new OTP to retry
- **Actual Result:** N/A | **Status:** Not Executed

## WD-016 — Reused OTP (already used) is rejected on the next transaction

- **Feature:** Withdraw Rupiah - OTP
- **Test Type:** Negative | **Priority:** High
- **Preconditions:** Logged in as QA001 | Initial Balance Rp200.000 | Bank BCA 1234567890 a.n Budi QA (verified) | Fee Rp2.500 | OTP valid 123456 (60s) | First Rp50.000 tx already Completed with OTP 123456
- **Test Data:** Tx1: first OTP issuance/token=123456 (consumed) | Tx2 Nominal=20000, replayed first OTP issuance/token=123456 (no new OTP requested)
- **Test Steps:**
  1. Complete first 50000 withdrawal with the first issued OTP 123456 (Completed)
  2. Start second withdrawal 20000
  3. Do NOT request a new OTP
  4. Replay the first OTP issuance/token with code 123456
  5. Submit
- **Expected Result:** Replay rejected with a clear used-or-invalid-OTP validation message | Second transaction not processed | Only Tx1 deducted Rp52.500; balance Rp147.500 | One history entry (A-10)
- **Actual Result:** N/A | **Status:** Not Executed

## WD-017 — Pending transaction is shown and moves to a final status without resubmission

- **Feature:** Withdraw Rupiah - Status
- **Test Type:** Positive | **Priority:** Medium
- **Preconditions:** Logged in as QA001 | Initial Balance Rp200.000 | Bank BCA 1234567890 a.n Budi QA (verified) | Fee Rp2.500 | OTP valid 123456 (60s) | Backend delay/sandbox to force Pending
- **Test Data:** Nominal=50000 | OTP=123456 | Forced Pending via delay/mock
- **Test Steps:**
  1. Withdraw 50000 with valid OTP 123456
  2. Observe status Pending (do NOT resubmit)
  3. Wait for processing to finish
  4. Check final status, balance, history
- **Expected Result:** Pending appears first, then Completed | One Rp52.500 deduction | One history entry updated to Completed
- **Actual Result:** N/A | **Status:** Not Executed

## WD-018 — Failed transaction causes no balance loss (hold refunded) and history shows Failed

- **Feature:** Withdraw Rupiah - Status
- **Test Type:** Negative | **Priority:** High
- **Preconditions:** Logged in as QA001 | Initial Balance Rp200.000 | Bank BCA 1234567890 a.n Budi QA (verified) | Fee Rp2.500 | OTP valid 123456 (60s) | Backend forced-fail available (bank timeout simulator)
- **Test Data:** Nominal=50000 | OTP=123456 valid | Failure injection=bank timeout
- **Test Steps:**
  1. Withdraw 50000 with valid OTP 123456
  2. Trigger processing failure (bank reject/timeout)
  3. Check status, balance, history
- **Expected Result:** Failed with clear message | No balance debit, or any debit fully refunded | One Failed history entry
- **Actual Result:** N/A | **Status:** Not Executed

## WD-019 — Duplicate submit during Pending debits only once (back-button / double-tap guard)

- **Feature:** Withdraw Rupiah - Duplicate
- **Test Type:** Negative | **Priority:** High
- **Preconditions:** Logged in as QA001 | Initial Balance Rp200.000 | Bank BCA 1234567890 a.n Budi QA (verified) | Fee Rp2.500 | OTP valid 123456 (60s) | Tx in Pending state (slow processing)
- **Test Data:** Nominal=50000 | Actions: Back+resubmit, double Confirm during Pending
- **Test Steps:**
  Execute two independent runs with a fresh transaction for each:
  Run A (Back + retry):
  1. Withdraw 50000, submit OTP 123456
  2. While Pending, press Back and retry the same transaction
  3. Wait until final status
  4. Check balance and history count
  Run B (double Confirm):
  1. Withdraw 50000, submit OTP 123456
  2. Rapidly double-tap Confirm while Pending
  3. Wait until final status
  4. Check balance and history count
- **Expected Result:** Each run: one Rp52.500 deduction, balance Rp147.500 | One history entry | Resubmit blocked with a clear transaction-in-progress message
- **Actual Result:** N/A | **Status:** Not Executed

## WD-020 — Transaction history shows accurate nominal, destination, status and details

- **Feature:** Withdraw Rupiah - History
- **Test Type:** Positive | **Priority:** Medium
- **Preconditions:** Logged in as QA001 | Test setup has created at least one Completed and one Failed transaction
- **Test Data:** History with >=1 Completed + >=1 Failed
- **Test Steps:**
  1. Open transaction history
  2. Inspect each entry (nominal, masked destination, status)
  3. Open detail of one Completed and one Failed tx
  4. Cross-check against actual amounts and statuses
- **Expected Result:** History shows correct nominal, masked destination, and status | Details match each transaction | Status changes update the existing entry, never duplicate it
- **Actual Result:** N/A | **Status:** Not Executed

---

## Asumsi dan Pertanyaan Klarifikasi

Disalin verbatim dari sheet Assumptions di file yang sama.

| ID | Type | Area | Assumption / Clarification |
|----|------|------|----------------------------|
| A-01 | Assumption | Amount (A.1, A.2) | Min Rp10.000 and max Rp100.000 are inclusive; no daily/monthly limit is defined, so back-to-back transactions are assumed allowed while balance suffices. |
| A-02 | Assumption | Amount (A.5) | Rupiah has no fractional part; decimals are rejected, not rounded. Thousand separators (50.000) are parsed as 50000. If the field uses a numeric keyboard, the text sub-case (abc in WD-007) cannot be typed and is skipped with a note (or attempted via paste). |
| A-03 | Assumption | Balance (A.4) | Balance exactly equal to amount + fee is accepted (inclusive comparison (balance >= amount + fee)), leaving Rp0. |
| A-04 | Assumption | Bank (B.3) | Masked account format is unspecified; assumed **** + last 4 digits, full number never exposed on UI or history. |
| A-05 | Assumption | Validation (A.6) | Exact validation message wording is unspecified; any clear relevant message is accepted. |
| A-06 | Assumption | OTP (C.1) | Dummy OTP 123456 is accepted in the test environment as the valid code. |
| A-07 | Assumption | Status (E.2, E.5) | Pending and Failed states require simulation (delay/mock/bank-timeout hook); happy-path runs yield Completed. |
| A-08 | Assumption | Failed (E.5) | On Failed, neither nominal nor admin fee is charged; any hold is released automatically. |
| A-09 | Assumption | History (E.7) | One transaction has exactly one history entry; status changes update the entry instead of creating duplicates. |
| A-10 | Assumption | OTP reuse (C.5) | The dummy OTP value 123456 is static, so 'reuse' cannot mean 'same digits typed again'. Assumed: each OTP request issues a single-use token (distinguished by issuance/session via test hook); WD-016 replays the consumed token issuance, which must be rejected even though the digits are identical. |
| A-11 | Assumption | Balance (A.4, A.6) | Insufficient-balance validation is assumed to happen up front (before OTP is issued). If the app validates after OTP instead, WD-008 steps must be adjusted to complete OTP first. |
| Q-01 | Question | OTP (C.3) | Is OTP still valid at exactly second 60, or already expired? If resend is requested, is the old code invalidated? |
| Q-02 | Question | OTP (C.4) | Is there a max wrong-OTP attempt limit (lockout / temporary block)? |
| Q-03 | Question | Amount (A.1, A.2) | Is there a daily/monthly cumulative limit besides the per-transaction max? |
| Q-04 | Question | Bank (B.1, B.4) | What defines an invalid/unavailable account (unverified, inactive, deleted, bank-side reject)? |
| Q-05 | Question | Status (E.3, E.5) | While Pending, are funds held or deducted upfront? When is a Failed hold released (sync/async)? |
| Q-06 | Question | Duplicate (E.3, E.6) | What is the duplicate-prevention mechanism (idempotency key, disabled button)? How long is the Pending window? |
| Q-07 | Question | Bank (B.1, B.4) | Only one verified dummy account is provided. Please provide an unverified and an inactive/deleted test account (or a supported way to simulate those states) so WD-011 is executable. |
| Q-08 | Question | Bank (B.2) | If the user has only one registered account, is it auto-selected? If yes, WD-010 needs an account-less test user or a deselect step. |

## Test Data (Dummy)

| Field | Value | Usage / Notes |
|-------|-------|---------------|
| User ID | QA001 | Dummy test user. |
| Username | dummy_user | Dummy test user. |
| Initial Balance | Rp200.000 | Default balance for standard scenarios. |
| Bank Name | BCA | Registered bank. |
| Account Number | 1234567890 | Confirmation must show masked form, e.g. ****7890. |
| Account Holder | Budi QA | Expected holder on confirmation. |
| Valid OTP | 123456 | Valid code per issuance; each issuance is single-use (see A-10). |
| OTP Expiration | 60 seconds | Measured from issuance; second-59 valid, >60 expired. |
| Admin Fee | Rp2.500 | Charged on every transaction, including edge checks. |
| Sample Withdrawal | Rp50.000 | Main-flow sample. |
| Sample Total Deduction | Rp52.500 | 50000 + 2500. |
| Sample Remaining | Rp147.500 | 200000 - 52500 (matches PDF example). |
| Minimum | Rp10.000 | Inclusive lower bound. |
| Maximum | Rp100.000 | Inclusive upper bound per transaction. |
| Below minimum | Rp9.999 | Boundary negative. |
| Above maximum | Rp100.001 | Boundary negative. |
| Insufficient case | Withdraw Rp100.000 @ Rp100.000 | Needs Rp102.500, must reject for insufficient balance. |
| Exact-balance case | Withdraw Rp100.000 @ Rp102.500 | Needs Rp102.500, must succeed and leave Rp0. |
