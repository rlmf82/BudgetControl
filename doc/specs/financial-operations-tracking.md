# Financial Operations Tracking — Business Rules

Source story:
> As a user, I want to save my financial operations like payments and incomes so that I can control my budget and generate a monthly and yearly report.

---

## Rule 1: Must record every financial operation with a type, a decimal amount, a description, and a category chosen from a predefined, editable list; the date is the only optional field and defaults to the current date when omitted.

- Example: The one where the user records a payment of 45.90, described as "groceries", categorized as "Food", dated 05/08/2026.
- Example: The one where the user records an income of 2500.00, described as "August salary", categorized as "Salary", dated 01/08/2026.
- Counter-example: The one where the user records a payment without specifying a date, and the system saves it using today's date automatically — valid, not a bug.
- Questions: None.

## Rule 2: Must classify every operation as either "payment" or "income" only; refunds are recorded as income, and any transfer — whether to a third party or between the user's own accounts — is recorded as a payment, since the system does not distinguish between the two.

- Example: The one where the user receives a refund of 30 for a returned item and records it as an income.
- Example: The one where the user transfers 500 to pay their landlord and records it as a payment.
- Counter-example: The one where the user moves money between their own savings and checking accounts and records it as a payment too, exactly like a transfer to a third party, since the app treats both cases as equal.
- Questions: None.

## Rule 3: Should reject an operation whose amount is zero or negative; amounts are recorded in Euro, with no maximum amount and no multi-currency support.

- Example: The one where the user tries to save a payment of -20 and the system rejects it.
- Example: The one where the user tries to save an income of 0 and the system rejects it.
- Counter-example: The one where the user saves a very large income, e.g., €1,000,000, and it is accepted since there is no upper limit.
- Questions: None.

## Rule 4: Must assign every operation to the month and year of its date, using the Portugal calendar day (regardless of the WET/WEST offset in effect) as the reference, and must reject operations dated in the future.

- Example: The one where a payment dated 31/08/2026 is included in August 2026's monthly report and in the 2026 yearly report.
- Example: The one where the user tries to save an income dated tomorrow and the system rejects it, since future-dated operations aren't allowed.
- Counter-example: The one where the user records an operation dated in the past (e.g., a January bill entered in August) and it is correctly placed into January's report rather than the current month's.
- Questions: None.

## Rule 5: Should persist every saved operation indefinitely, and should allow the user to edit or delete a saved operation; only reports generated after the edit reflect the change, and no audit trail of edits/deletions is kept.

- Example: The one where the user edits the amount of a payment from 100 to 80, and a monthly report generated afterward shows the updated balance.
- Example: The one where the user deletes an income entry, and a report generated afterward no longer includes it in the monthly or yearly totals.
- Counter-example: The one where the user had already generated and downloaded August's report before editing an operation dated in August — that earlier report is not updated retroactively; only a newly generated report reflects the edit.
- Questions: None.

## Rule 6: Should generate a report for a full calendar month (first day to last day) or for a custom date range within a single year, summarizing total income, total payments, and balance broken down by category, with the option to list the individual operations behind each category; a custom range that crosses a year boundary is rejected.

- Example: The one where the user requests the report for August 2026 and sees totals grouped by category (e.g., Food: 300, Rent: 800, Salary: 2500) plus an overall balance.
- Example: The one where the user requests a custom range from 10/08/2026 to 20/08/2026 and receives income/payment/balance totals, broken down by category, for just that window.
- Counter-example: The one where a category has no operations in the selected period, and it is simply omitted (or shown as zero) rather than causing an error.
- Questions: None.

## Rule 7: Should generate a yearly report summarizing total income, total payments, and balance across the year, including a month-by-month breakdown with category totals (and operation-level drill-down) for each month; for the current, in-progress year, only the months elapsed so far are shown and future months are omitted.

- Example: The one where the user requests the 2025 report and sees a category breakdown, with drill-down to individual operations, for each of the 12 months plus the annual totals.
- Example: The one where the user requests the 2026 report in August, and it shows a breakdown only for January through August, with September–December omitted entirely rather than shown as zero.
- Counter-example: The one where the user requests a yearly report for a year with no recorded operations at all, and all monthly breakdowns and totals show zero instead of an error.
- Questions: None.
