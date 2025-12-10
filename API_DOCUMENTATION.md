# 📚 CoopCredit API Documentation

## Overview

CoopCredit API provides RESTful endpoints for managing credit applications, affiliates, and user authentication. All endpoints return JSON responses and use JWT for authentication.

---

## Base URL

```
http://localhost:8081/api/v1
```

---

## Authentication

### JWT Token

All protected endpoints require a Bearer token in the Authorization header:

```bash
Authorization: Bearer <your_jwt_token>
```

### Token Expiration

Tokens expire after 24 hours (86400000 milliseconds).

---

## Response Format

### Success Response (2xx)

```json
{
  "data": {
    "id": 1,
    "name": "John Doe"
  },
  "message": "Operation successful",
  "timestamp": "2025-12-09T19:37:29Z"
}
```

### Error Response (4xx, 5xx)

```json
{
  "type": "https://example.com/errors/validation-error",
  "title": "Validation Error",
  "status": 400,
  "detail": "The email is already registered",
  "instance": "/auth/register",
  "timestamp": "2025-12-09T19:37:29Z",
  "traceId": "abc123def456"
}
```

---

## Endpoints

### Authentication Endpoints

#### Register New User

**Request**

```http
POST /auth/register
Content-Type: application/json

{
  "document": "1017654311",
  "username": "Juan Pérez",
  "email": "juan@example.com",
  "password": "SecurePassword123",
  "annualIncome": 3500000.00
}
```

**Response (200 OK)**

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqdWFuQGV4YW1wbGUuY29tIiwiaWF0IjoxNzY1MzI3MDQ5LCJleHAiOjE3NjU0MTM0NDl9..."
}
```

**Errors**

- `400 Bad Request` - Validation error (invalid email, duplicate document, etc.)
- `500 Internal Server Error` - Server error

---

#### User Login

**Request**

```http
POST /auth/login
Content-Type: application/json

{
  "username": "juan@example.com",
  "password": "SecurePassword123"
}
```

**Response (200 OK)**

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqdWFuQGV4YW1wbGUuY29tIiwiaWF0IjoxNzY1MzI3MDQ5LCJleHAiOjE3NjU0MTM0NDl9..."
}
```

**Errors**

- `401 Unauthorized` - Invalid credentials
- `400 Bad Request` - Missing required fields

---

### Affiliate Endpoints

#### List All Affiliates

**Request**

```http
GET /affiliates
Authorization: Bearer <token>
```

**Response (200 OK)**

```json
[
  {
    "id": 1,
    "document": "1017654311",
    "firstName": "Juan",
    "lastName": "Pérez",
    "email": "juan@example.com",
    "annualIncome": 3500000.00,
    "registrationDate": "2025-12-09"
  }
]
```

**Permissions**

- `ROLE_ADMIN` - Full access
- `ROLE_ANALYST` - Read-only access

---

#### Get Affiliate by ID

**Request**

```http
GET /affiliates/{id}
Authorization: Bearer <token>
```

**Response (200 OK)**

```json
{
  "id": 1,
  "document": "1017654311",
  "firstName": "Juan",
  "lastName": "Pérez",
  "email": "juan@example.com",
  "annualIncome": 3500000.00,
  "registrationDate": "2025-12-09"
}
```

**Errors**

- `404 Not Found` - Affiliate not found
- `401 Unauthorized` - Invalid or missing token

---

#### Create New Affiliate

**Request**

```http
POST /affiliates
Authorization: Bearer <token>
Content-Type: application/json

{
  "document": "1234567890",
  "firstName": "Maria",
  "lastName": "García",
  "email": "maria@example.com",
  "annualIncome": 2500000.00
}
```

**Response (201 Created)**

```json
{
  "id": 2,
  "document": "1234567890",
  "firstName": "Maria",
  "lastName": "García",
  "email": "maria@example.com",
  "annualIncome": 2500000.00,
  "registrationDate": "2025-12-09"
}
```

**Permissions**

- `ROLE_ADMIN` - Required

---

### Credit Application Endpoints

#### List Credit Applications

**Request**

```http
GET /applications
Authorization: Bearer <token>
```

**Query Parameters**

- `status` - Filter by status (PENDIENTE, APROBADA, RECHAZADA)
- `affiliateId` - Filter by affiliate ID
- `page` - Page number (default: 0)
- `size` - Page size (default: 20)

**Response (200 OK)**

```json
[
  {
    "id": 1,
    "affiliateId": 1,
    "requestedAmount": 5000000.00,
    "termMonths": 36,
    "applicationDate": "2025-12-09T19:37:29Z",
    "status": "PENDIENTE",
    "riskScore": 650,
    "riskLevel": "MEDIUM"
  }
]
```

---

#### Get Application by ID

**Request**

```http
GET /applications/{id}
Authorization: Bearer <token>
```

**Response (200 OK)**

```json
{
  "id": 1,
  "affiliateId": 1,
  "requestedAmount": 5000000.00,
  "termMonths": 36,
  "applicationDate": "2025-12-09T19:37:29Z",
  "status": "PENDIENTE",
  "riskScore": 650,
  "riskLevel": "MEDIUM"
}
```

---

#### Create Credit Application

**Request**

```http
POST /applications
Authorization: Bearer <token>
Content-Type: application/json

{
  "affiliateId": 1,
  "requestedAmount": 5000000.00,
  "termMonths": 36
}
```

**Response (201 Created)**

```json
{
  "id": 1,
  "affiliateId": 1,
  "requestedAmount": 5000000.00,
  "termMonths": 36,
  "applicationDate": "2025-12-09T19:37:29Z",
  "status": "PENDIENTE",
  "riskScore": 650,
  "riskLevel": "MEDIUM"
}
```

**Permissions**

- `ROLE_AFILIADO` - Can create own applications
- `ROLE_ADMIN` - Can create for any affiliate

---

#### Update Application Status

**Request**

```http
PUT /applications/{id}
Authorization: Bearer <token>
Content-Type: application/json

{
  "status": "APROBADA"
}
```

**Response (200 OK)**

```json
{
  "id": 1,
  "affiliateId": 1,
  "requestedAmount": 5000000.00,
  "termMonths": 36,
  "applicationDate": "2025-12-09T19:37:29Z",
  "status": "APROBADA",
  "riskScore": 650,
  "riskLevel": "MEDIUM"
}
```

**Permissions**

- `ROLE_ANALYST` - Can update status
- `ROLE_ADMIN` - Full access

---

## HTTP Status Codes

| Code | Meaning |
|------|---------|
| 200 | OK - Request successful |
| 201 | Created - Resource created |
| 204 | No Content - Request successful, no content |
| 400 | Bad Request - Invalid request data |
| 401 | Unauthorized - Missing or invalid token |
| 403 | Forbidden - Insufficient permissions |
| 404 | Not Found - Resource not found |
| 409 | Conflict - Resource already exists |
| 500 | Internal Server Error - Server error |

---

## Error Codes

| Code | Description |
|------|-------------|
| VALIDATION_ERROR | Input validation failed |
| DUPLICATE_DOCUMENT | Document already registered |
| DUPLICATE_EMAIL | Email already registered |
| AFFILIATE_NOT_FOUND | Affiliate not found |
| APPLICATION_NOT_FOUND | Application not found |
| UNAUTHORIZED | Invalid credentials |
| FORBIDDEN | Insufficient permissions |
| INVALID_TOKEN | Token is invalid or expired |

---

## Rate Limiting

Currently, there is no rate limiting implemented. This may be added in future versions.

---

## Pagination

Endpoints that return lists support pagination:

```http
GET /applications?page=0&size=20
```

Response includes:

```json
{
  "content": [...],
  "totalElements": 100,
  "totalPages": 5,
  "currentPage": 0,
  "pageSize": 20
}
```

---

## Filtering & Sorting

### Filtering

```http
GET /applications?status=PENDIENTE&affiliateId=1
```

### Sorting

```http
GET /applications?sort=applicationDate,desc
```

---

## CORS

CORS is enabled for all origins. The following headers are allowed:

- `Content-Type`
- `Authorization`
- `Accept`

---

## Versioning

The API uses URL versioning: `/api/v1/`

Future versions will be available at `/api/v2/`, etc.

---

## Changelog

### Version 1.0.0 (2025-12-09)

- Initial release
- Authentication endpoints
- Affiliate management
- Credit application management
- Risk evaluation integration
