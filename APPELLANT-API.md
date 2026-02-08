# Appellant API

**Auth:** OAuth2 Bearer Token required on all endpoints

---

## Response Format

```json
{
  "status": true,
  "code": 6000,
  "description": "Success message",
  "data": { }
}
```

List endpoints return:

```json
{
  "status": true,
  "code": 6000,
  "totalElements": 42,
  "data": [ ],
  "description": null
}
```

| Code | Meaning |
|------|---------|
| `6000` | Success |
| `6004` | No record found |
| `6005` | Failure |

---

# CRUD Endpoints

**Base Path:** `/api/appellants`

## Appellant Object

```json
{
  "appellantId": 1,
  "firstName": "ABC TRADING LTD",
  "lastName": "",
  "natureOfBusiness": "TRADING",
  "phoneNumber": "0712345678",
  "email": "abc@example.com",
  "tinNumber": "123-456-789",
  "incomeTaxFileNumber": "NONE",
  "vatNumber": "NONE",
  "createdDate": "2024-06-15"
}
```

---

### 1. List All Appellants

```
GET /api/appellants
```

**Response:**
```json
{
  "status": true,
  "code": 6000,
  "totalElements": 150,
  "data": [
    {
      "appellantId": 1,
      "firstName": "ABC TRADING LTD",
      "tinNumber": "123-456-789",
      "email": "abc@example.com",
      "phoneNumber": "0712345678",
      "natureOfBusiness": "TRADING",
      "createdDate": "2024-06-15"
    }
  ],
  "description": null
}
```

---

### 2. Get Single Appellant

```
GET /api/appellants/{id}
```

**Response:**
```json
{
  "status": true,
  "code": 6000,
  "description": null,
  "data": {
    "appellantId": 1,
    "firstName": "ABC TRADING LTD",
    "lastName": "",
    "tinNumber": "123-456-789",
    "email": "abc@example.com",
    "phoneNumber": "0712345678",
    "natureOfBusiness": "TRADING",
    "incomeTaxFileNumber": "NONE",
    "vatNumber": "NONE",
    "createdDate": "2024-06-15"
  }
}
```

---

### 3. Create Appellant

```
POST /api/appellants
```

**Request Body (AppellantDto):**
```json
{
  "firstName": "XYZ COMPANY LTD",
  "lastName": "",
  "natureOfBusiness": "MANUFACTURING",
  "phoneNumber": "0755123456",
  "email": "xyz@company.co.tz",
  "tinNumber": "987-654-321",
  "incomeTaxFileNumber": "IT-001",
  "vatNumber": "VAT-001"
}
```

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `firstName` | string | Yes | Appellant name |
| `lastName` | string | No | Last name (usually empty for companies) |
| `natureOfBusiness` | string | No | Type of business |
| `phoneNumber` | string | No | Contact phone |
| `email` | string | No | Contact email |
| `tinNumber` | string | No | TIN number (defaults to "NONE") |
| `incomeTaxFileNumber` | string | No | Income tax file number (defaults to "NONE") |
| `vatNumber` | string | No | VAT number (defaults to "NONE") |

**Response:**
```json
{
  "status": true,
  "code": 6000,
  "description": "Appellant saved successfully",
  "data": { "appellantId": 5, "firstName": "XYZ COMPANY LTD", "..." : "..." }
}
```

---

### 4. Update Appellant

```
PUT /api/appellants/{id}
```

**Request Body:** Same as Create. Only provided fields are updated (null fields are ignored).

```json
{
  "phoneNumber": "0786999888",
  "email": "new-email@company.co.tz"
}
```

**Response:**
```json
{
  "status": true,
  "code": 6000,
  "description": "Appellant updated successfully",
  "data": { "appellantId": 5, "..." : "..." }
}
```

---

### 5. Delete Appellant

```
DELETE /api/appellants/{id}
```

**Response:**
```json
{
  "status": true,
  "code": 6000,
  "description": "Appellant deleted successfully",
  "data": null
}
```

---

## CRUD Quick Reference

| # | Method | Endpoint | Description |
|---|--------|----------|-------------|
| 1 | GET | `/api/appellants` | List all appellants |
| 2 | GET | `/api/appellants/{id}` | Get single appellant |
| 3 | POST | `/api/appellants` | Create appellant |
| 4 | PUT | `/api/appellants/{id}` | Update appellant |
| 5 | DELETE | `/api/appellants/{id}` | Delete appellant |

---

# Migration Endpoints

**Base Path:** `/api/admin/migration`

Migration runs in **two phases**. Each phase has a preview (dry run) and execute endpoint.

## Execution Order

```
Step 1: GET  /api/admin/migration/extract-appellants/preview   -> review counts
Step 2: POST /api/admin/migration/extract-appellants            -> populate Appellant table
Step 3: ** STOP -- verify Appellant table in DB **
Step 4: GET  /api/admin/migration/link-appeals/preview          -> review link counts
Step 5: POST /api/admin/migration/link-appeals                  -> set FK on all appeals
Step 6: ** VERIFY -- check reports, check appeal detail pages **
```

---

### 6. Phase 1 Preview -- Extract Appellants (Dry Run)

```
GET /api/admin/migration/extract-appellants/preview
```

Scans all appeals and shows how many unique appellants will be created. **No data is modified.**

**Response:**
```json
{
  "status": true,
  "code": 6000,
  "description": "Phase 1 preview completed",
  "data": {
    "totalAppealsScanned": 1200,
    "uniqueNamesFound": 340,
    "appellantsCreated": 320,
    "appellantsSkipped": 20,
    "duplicateVariants": [
      {
        "normalizedName": "ABC LTD",
        "originalVariants": ["ABC Ltd", "ABC LIMITED", "A.B.C Ltd"],
        "appealCount": 15
      }
    ]
  }
}
```

| Field | Type | Description |
|-------|------|-------------|
| `totalAppealsScanned` | int | Total appeals scanned |
| `uniqueNamesFound` | int | Unique appellant names found |
| `appellantsCreated` | int | New Appellant records that will be / were created |
| `appellantsSkipped` | int | Already existing Appellant records (skipped) |
| `duplicateVariants` | array | Names with multiple spelling variants |
| `duplicateVariants[].normalizedName` | string | Uppercase normalized name |
| `duplicateVariants[].originalVariants` | array | Original spellings found |
| `duplicateVariants[].appealCount` | int | Number of appeals with this name |

---

### 7. Phase 1 Execute -- Extract Appellants

```
POST /api/admin/migration/extract-appellants
```

Creates Appellant records from appeal data. Same response format as preview.

---

### 8. Phase 2 Preview -- Link Appeals (Dry Run)

```
GET /api/admin/migration/link-appeals/preview
```

Shows how many appeals will be linked to Appellant records. **No data is modified.**

**Response:**
```json
{
  "status": true,
  "code": 6000,
  "description": "Phase 2 preview completed",
  "data": {
    "totalAppealsProcessed": 1200,
    "appealsLinked": 1180,
    "appealsAlreadyLinked": 0,
    "appealsUnmatched": 20,
    "unmatchedAppealNos": ["DSM.5/2020", "ARU.12/2021"]
  }
}
```

| Field | Type | Description |
|-------|------|-------------|
| `totalAppealsProcessed` | int | Total appeals processed |
| `appealsLinked` | int | Appeals that will be / were linked to Appellant |
| `appealsAlreadyLinked` | int | Appeals that already had FK set |
| `appealsUnmatched` | int | Appeals with no matching Appellant found |
| `unmatchedAppealNos` | array | Appeal numbers that could not be matched |

---

### 9. Phase 2 Execute -- Link Appeals

```
POST /api/admin/migration/link-appeals
```

Sets `appealant_id` FK on each appeal. Same response format as preview.

---

## Migration Quick Reference

| # | Method | Endpoint | Description |
|---|--------|----------|-------------|
| 6 | GET | `/api/admin/migration/extract-appellants/preview` | Phase 1 dry run |
| 7 | POST | `/api/admin/migration/extract-appellants` | Phase 1 execute |
| 8 | GET | `/api/admin/migration/link-appeals/preview` | Phase 2 dry run |
| 9 | POST | `/api/admin/migration/link-appeals` | Phase 2 execute |
