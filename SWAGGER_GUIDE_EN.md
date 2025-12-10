# 📚 Swagger/OpenAPI Documentation Guide - CoopCredit API

## Accessing Swagger UI

Once the application is running, access the interactive API documentation at:

```
http://localhost:8081/swagger-ui/index.html
```

Or directly access the OpenAPI specification:

```
http://localhost:8081/v3/api-docs
```

---

## 🔐 JWT Authentication

All protected endpoints require a JWT Bearer token in the Authorization header:

```
Authorization: Bearer <your_jwt_token>
```

### Authentication Flow:

1. **Register**: `POST /auth/register` - Create a new user account
2. **Login**: `POST /auth/login` - Obtain JWT token
3. **Use Token**: Include in `Authorization: Bearer <token>` header for protected endpoints

---

## 📋 Available Endpoints

### 1. Authentication Endpoints (`/auth`)

#### Register New User

**Endpoint**: `POST /auth/register`

**Request Body**:
```json
{
  "document": "1017654311",
  "username": "Juan Pérez",
  "email": "juan@example.com",
  "password": "SecurePassword123",
  "annualIncome": 3500000.00
}
```

**Response (200 OK)**:
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqdWFuQGV4YW1wbGUuY29tIiwiaWF0IjoxNzY1MzI3MDQ5LCJleHAiOjE3NjU0MTM0NDl9..."
}
```

**Error Responses**:
- `400 Bad Request` - Validation error (invalid email, duplicate document, etc.)
- `409 Conflict` - Email or document already registered

---

#### User Login

**Endpoint**: `POST /auth/login`

**Request Body**:
```json
{
  "username": "juan@example.com",
  "password": "SecurePassword123"
}
```

**Response (200 OK)**:
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqdWFuQGV4YW1wbGUuY29tIiwiaWF0IjoxNzY1MzI3MDQ5LCJleHAiOjE3NjU0MTM0NDl9..."
}
```

**Error Responses**:
- `401 Unauthorized` - Invalid credentials
- `400 Bad Request` - Missing required fields

---

### 2. Affiliate Endpoints (`/api/v1/affiliates`)

#### List All Affiliates

**Endpoint**: `GET /api/v1/affiliates`

**Headers**:
```
Authorization: Bearer <token>
```

**Response (200 OK)**:
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

**Permissions**:
- `ROLE_ADMIN` - Full access
- `ROLE_ANALYST` - Read-only access

---

#### Get Affiliate by ID

**Endpoint**: `GET /api/v1/affiliates/{id}`

**Headers**:
```
Authorization: Bearer <token>
```

**Response (200 OK)**:
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

**Error Responses**:
- `404 Not Found` - Affiliate not found
- `401 Unauthorized` - Invalid or missing token

---

### 3. Credit Application Endpoints (`/api/v1/applications`)

#### List Credit Applications

**Endpoint**: `GET /api/v1/applications`

**Headers**:
```
Authorization: Bearer <token>
```

**Query Parameters**:
- `status` - Filter by status (PENDIENTE, APROBADA, RECHAZADA)
- `affiliateId` - Filter by affiliate ID
- `page` - Page number (default: 0)
- `size` - Page size (default: 20)

**Response (200 OK)**:
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

**Endpoint**: `GET /api/v1/applications/{id}`

**Headers**:
```
Authorization: Bearer <token>
```

**Response (200 OK)**:
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

**Endpoint**: `POST /api/v1/applications`

**Headers**:
```
Authorization: Bearer <token>
Content-Type: application/json
```

**Request Body**:
```json
{
  "affiliateId": 1,
  "requestedAmount": 5000000.00,
  "termMonths": 36
}
```

**Response (201 Created)**:
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

**Permissions**:
- `ROLE_AFILIADO` - Can create own applications
- `ROLE_ADMIN` - Can create for any affiliate

---

## 🧪 Testing Endpoints in Swagger UI

### Step 1: Register a User

1. Click on the **POST /auth/register** endpoint
2. Click **"Try it out"**
3. Fill in the request body with user details
4. Click **"Execute"**
5. Copy the token from the response

### Step 2: Authorize with Token

1. Click the **"Authorize"** button (top right)
2. Paste your token in the format: `Bearer <your_token>`
3. Click **"Authorize"**
4. Click **"Close"**

### Step 3: Test Protected Endpoints

1. Select any protected endpoint (e.g., `GET /api/v1/applications`)
2. Click **"Try it out"**
3. Click **"Execute"**
4. View the response

---

## 📊 Response Codes

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

## 🔒 Security Schemes

### BearerAuth (JWT)

All endpoints (except `/auth/register` and `/auth/login`) require JWT Bearer authentication.

**Format**:
```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyIiwiaWF0IjoxNjM2NzI5NzAwLCJleHAiOjE2MzY4MTYxMDB9.signature
```

**Token Expiration**: 24 hours (86400000 milliseconds)

---

## 🎯 Common Use Cases

### Use Case 1: Register and Login

```bash
# 1. Register
curl -X POST http://localhost:8081/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "document": "1017654311",
    "username": "Juan Pérez",
    "email": "juan@example.com",
    "password": "SecurePassword123",
    "annualIncome": 3500000.00
  }'

# Response:
# {"token": "eyJhbGciOiJIUzI1NiJ9..."}

# 2. Login
curl -X POST http://localhost:8081/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "juan@example.com",
    "password": "SecurePassword123"
  }'

# Response:
# {"token": "eyJhbGciOiJIUzI1NiJ9..."}
```

### Use Case 2: Create Credit Application

```bash
# Use the token from login
TOKEN="eyJhbGciOiJIUzI1NiJ9..."

curl -X POST http://localhost:8081/api/v1/applications \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "affiliateId": 1,
    "requestedAmount": 5000000.00,
    "termMonths": 36
  }'
```

### Use Case 3: List Applications

```bash
TOKEN="eyJhbGciOiJIUzI1NiJ9..."

curl -X GET "http://localhost:8081/api/v1/applications?status=PENDIENTE" \
  -H "Authorization: Bearer $TOKEN"
```

---

## 🐛 Troubleshooting

### Issue: 401 Unauthorized

**Problem**: Getting 401 error on protected endpoints

**Solution**:
1. Verify token is valid and not expired
2. Check token format: `Authorization: Bearer <token>`
3. Ensure token is from `/auth/login` endpoint
4. Re-login to get a new token

### Issue: 403 Forbidden

**Problem**: Getting 403 error even with valid token

**Solution**:
1. Check user role permissions
2. Ensure user has required role for the endpoint
3. Contact admin to update user permissions

### Issue: 400 Bad Request

**Problem**: Getting 400 error on request

**Solution**:
1. Verify all required fields are included
2. Check data types match specification
3. Validate email format
4. Ensure document is unique (for registration)

---

## 📖 Additional Resources

- **Full API Documentation**: [API_DOCUMENTATION.md](./API_DOCUMENTATION.md)
- **Architecture Guide**: [ARCHITECTURE.md](./ARCHITECTURE.md)
- **Setup Guide**: [SETUP_GUIDE.md](./SETUP_GUIDE.md)
- **OpenAPI Specification**: http://localhost:8081/v3/api-docs

---

## 🔗 Useful Links

- **Swagger UI**: http://localhost:8081/swagger-ui/index.html
- **OpenAPI JSON**: http://localhost:8081/v3/api-docs
- **Health Check**: http://localhost:8081/actuator/health
- **Metrics**: http://localhost:8081/actuator/metrics

---

## 💡 Tips

1. **Save Tokens**: Keep tokens for testing multiple endpoints
2. **Use Swagger UI**: Easier than manual cURL commands
3. **Check Logs**: Server logs show detailed error information
4. **Test Locally**: Always test locally before production
5. **Update Credentials**: Change default passwords in production

---

## Support

For issues or questions about the API:

1. Check the [Troubleshooting](#troubleshooting) section
2. Review server logs
3. Check [API_DOCUMENTATION.md](./API_DOCUMENTATION.md)
4. Contact the development team
