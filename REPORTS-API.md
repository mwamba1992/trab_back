# TRAB Reports API Documentation

## Base Info

- **Base Path:** `/api/reports`
- **Method:** `POST` (all endpoints)
- **Content-Type:** `application/json`
- **Auth:** OAuth2 Bearer Token required

---

## Response Format

All endpoints return the same structure:

```json
{
  "status": true,
  "code": 200,
  "description": "Report generated successfully",
  "data": {
    "content": "<base64-encoded-string>",
    "contentType": "application/pdf",
    "fileName": "appeals-report-1706789012345.pdf"
  }
}
```

| Field | Type | Description |
|-------|------|-------------|
| `data.content` | string | Base64-encoded file content (PDF or Excel) |
| `data.contentType` | string | `application/pdf` or `application/vnd.openxmlformats-officedocument.spreadsheetml.sheet` |
| `data.fileName` | string | Suggested download filename |

### Frontend Download Example

```javascript
const response = await api.post('/api/reports/appeals', filter);
const { content, contentType, fileName } = response.data.data;

const byteCharacters = atob(content);
const byteNumbers = new Array(byteCharacters.length);
for (let i = 0; i < byteCharacters.length; i++) {
  byteNumbers[i] = byteCharacters.charCodeAt(i);
}
const byteArray = new Uint8Array(byteNumbers);
const blob = new Blob([byteArray], { type: contentType });

const link = document.createElement('a');
link.href = URL.createObjectURL(blob);
link.download = fileName;
link.click();
```

---

## Request Body (ReportFilterDto)

All endpoints accept the same request body. **All fields are optional.**

```json
{
  "dateFrom": "2024-01-01",
  "dateTo": "2024-12-31",
  "format": "pdf"
}
```

| Field | Type | Default | Description |
|-------|------|---------|-------------|
| `dateFrom` | string | `null` | Start date (`yyyy-MM-dd`) |
| `dateTo` | string | `null` | End date (`yyyy-MM-dd`) |
| `format` | string | `"pdf"` | Output format: `"pdf"` or `"excel"` |
| `taxType` | string | `null` | Filter by tax type name |
| `statusTrend` | string | `null` | Appeal status trend |
| `judgeId` | string | `null` | Filter by judge ID |
| `financialYear` | string | `null` | e.g. `"2023/2024"` |
| `progressStatus` | string | `null` | Case progress status |
| `isTribunal` | string | `null` | Tribunal filter |
| `minDaysOpen` | integer | `90` | Minimum overdue days (overdue-cases only) |
| `dateOfDecisionFrom` | string | `null` | Decision date start (`yyyy-MM-dd`) |
| `dateOfDecisionTo` | string | `null` | Decision date end (`yyyy-MM-dd`) |
| `region` | string | `null` | Region name (filters by appeal number) |
| `wonBy` | string | `null` | Case won by party |
| `chairPerson` | string | `null` | Chair person (Judge ID) |
| `hearingStage` | string | `null` | Hearing/proceeding stage |

> When `dateFrom` and `dateTo` are both omitted, reports return **all records**.

---

## Endpoints

### 1. Appeals Report
```
POST /api/reports/appeals
```
Appeal register with filing dates, tax types, amounts, and status.

**Relevant filters:** `dateFrom`, `dateTo`, `taxType`, `statusTrend`, `dateOfDecisionFrom`, `dateOfDecisionTo`, `region`, `wonBy`, `chairPerson`, `hearingStage`

**Example:**
```json
{
  "dateFrom": "2024-01-01",
  "dateTo": "2024-12-31",
  "taxType": "Income Tax",
  "statusTrend": "PENDING",
  "region": "Dar es Salaam",
  "format": "pdf"
}
```

---

### 2. Appeals by Region Report
```
POST /api/reports/appeals-by-region
```
Same as Appeals Report but **grouped by region** (e.g., Dar es Salaam, Arusha, Mwanza). Each region section has its own list and subtotals. Region is derived from appeal number prefix (e.g., `DSM.5/2024` = Dar es Salaam).

**Relevant filters:** `dateFrom`, `dateTo`, `taxType`, `statusTrend`, `dateOfDecisionFrom`, `dateOfDecisionTo`, `region`, `wonBy`, `chairPerson`, `hearingStage`

**Example:**
```json
{
  "dateFrom": "2024-01-01",
  "dateTo": "2024-12-31",
  "format": "pdf"
}
```

---

### 3. Judge Workload Report
```
POST /api/reports/judge-workload
```
Cases per judge with decided/pending breakdown.

**Relevant filters:** `dateFrom`, `dateTo`

**Example:**
```json
{ "dateFrom": "2024-01-01", "dateTo": "2024-12-31", "format": "pdf" }
```

---

### 4. Case Status Summary Report
```
POST /api/reports/case-status-summary
```
Aggregated case counts by status trend.

**Relevant filters:** `dateFrom`, `dateTo`

---

### 5. Tax Type Analysis Report
```
POST /api/reports/tax-type-analysis
```
Case distribution and amounts grouped by tax type.

**Relevant filters:** `dateFrom`, `dateTo`

---

### 6. Overdue Cases Report
```
POST /api/reports/overdue-cases
```
Cases exceeding the minimum open days threshold.

**Relevant filters:** `dateFrom`, `dateTo`, `minDaysOpen` (default: 90)

**Example:**
```json
{ "dateFrom": "2024-01-01", "dateTo": "2024-12-31", "minDaysOpen": 180, "format": "pdf" }
```

---

### 7. Payment Report
```
POST /api/reports/payments
```
Payment transactions grouped by application type with subtotals.

**Relevant filters:** `dateFrom`, `dateTo`

---

### 8. Outstanding Bills Report
```
POST /api/reports/outstanding-bills
```
Unpaid bills with aging analysis (0-30, 31-60, 61-90, 90+ days).

**Relevant filters:** `dateFrom`, `dateTo`

---

### 9. Revenue Summary Report
```
POST /api/reports/revenue-summary
```
Revenue collection rates by category (billed vs collected).

**Relevant filters:** `dateFrom`, `dateTo`

---

### 10. Bill Reconciliation Report
```
POST /api/reports/bill-reconciliation
```
Bill-to-payment matching with variance and status (Paid/Unpaid/Expired).

**Relevant filters:** `dateFrom`, `dateTo`

---

### 11. Notice Report
```
POST /api/reports/notices
```
Notices with payment status (Paid/Unpaid).

**Relevant filters:** `dateFrom`, `dateTo`

---

### 12. Summons Report
```
POST /api/reports/summons
```
Hearing summons with judge panel, venue, and linked cases.

**Relevant filters:** `dateFrom`, `dateTo`

---

### 13. Application Register Report
```
POST /api/reports/applications
```
Applications with applicant, respondent, tax type, and status.

**Relevant filters:** `dateFrom`, `dateTo`

---

### 14. Financial Year Comparison Report
```
POST /api/reports/financial-year-comparison
```
Year-over-year comparison of appeals and applications. **No date filter needed** — returns all financial years.

**Example:**
```json
{ "format": "pdf" }
```

---

### 15. Top Appellants Report
```
POST /api/reports/top-appellants
```
Most frequent appellants ranked by case count.

**Relevant filters:** `dateFrom`, `dateTo`

---

## Quick Reference

| # | Endpoint | Description | Layout |
|---|----------|-------------|--------|
| 1 | `/appeals` | Appeal register | Landscape |
| 2 | `/appeals-by-region` | Appeals grouped by region | Landscape |
| 3 | `/judge-workload` | Judge case distribution | Landscape |
| 4 | `/case-status-summary` | Status aggregation | Portrait |
| 5 | `/tax-type-analysis` | Tax type breakdown | Landscape |
| 6 | `/overdue-cases` | Overdue/aging cases | Landscape |
| 7 | `/payments` | Payment transactions | Landscape |
| 8 | `/outstanding-bills` | Unpaid bills + aging | Landscape |
| 9 | `/revenue-summary` | Collection rates | Portrait |
| 10 | `/bill-reconciliation` | Bill vs payment match | Landscape |
| 11 | `/notices` | Notice register | Landscape |
| 12 | `/summons` | Hearing summons | Landscape |
| 13 | `/applications` | Application register | Landscape |
| 14 | `/financial-year-comparison` | FY comparison | Portrait |
| 15 | `/top-appellants` | Top appellants ranked | Landscape |

## Error Response

```json
{
  "status": false,
  "code": 500,
  "description": "Error generating report: <error message>",
  "data": null
}
```
