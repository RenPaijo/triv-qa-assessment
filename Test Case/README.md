# Withdraw Rupiah — Test Cases

## Deliverable

The complete test suite is available in **[Test_Cases.xlsx](Test_Cases.xlsx)**.

- Application under test: **TRIV**
- Feature: **Withdraw Rupiah**
- Platform: **Android mobile application**
- Total test cases: **50**
- Execution status: **Not Executed**

## Workbook Contents

The workbook contains the following worksheets:

| Worksheet | Description |
|---|---|
| `Test_Cases` | Detailed test steps, test data, expected results, priority, test type, and status |
| `Summary` | Scope, assumptions, coverage, and execution instructions |
| `Coverage_Map` | Requirements, risk areas, and corresponding test case IDs |
| `Test_Data` | Reusable test-data categories and values |
| `Execution_Report` | Result template for recording actual test execution |

## Test Coverage

- **Positive:** valid bank and e-wallet withdrawals, supported boundary amounts, account selection, confirmation, and successful transaction processing.
- **Negative:** invalid amounts, unsupported accounts, invalid or incomplete input, cancellation, and rejection handling.
- **Boundary:** minimum and maximum withdrawal limits and insufficient-balance boundaries.
- **Validation:** required fields, amount format, account details, fee display, total deduction, and confirmation information.
- **Security:** PIN, OTP, authentication, session, and sensitive-data handling.
- **Network:** timeout, interruption, retry, and prevention of duplicate withdrawals.
- **Compatibility:** supported Android versions, screen sizes, and application states.

## Execution Notes

1. Open `Test_Cases.xlsx` in Microsoft Excel, LibreOffice Calc, or another compatible spreadsheet application.
2. Prepare a test account with sufficient balance and valid bank/e-wallet destination accounts.
3. Use a controlled test environment for OTP, PIN, failure, and network scenarios.
4. Execute each test case and record its actual result, status, evidence, and defect reference where applicable.
5. Do not use production funds or real personal banking credentials without authorization.

The original workbook status remains **Not Executed**; these test cases must not be represented as having passed without actual execution evidence.
