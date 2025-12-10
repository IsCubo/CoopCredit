# 🔌 Ejemplos de Endpoints - CoopCredit API

## 📌 Base URL
```
http://localhost:8084
```

---

## 🔐 1. AUTENTICACIÓN

### 1.1 Registrar nuevo usuario (POST)
**Endpoint:** `POST /auth/register`

**Headers:**
```
Content-Type: application/json
```

**Body:**
```json
{
  "documento": "1017654311",
  "nombre": "Juan Pérez García",
  "email": "juan.perez@example.com",
  "password": "SecurePassword123!",
  "salario": 3500000,
  "fechaAfiliacion": "2024-06-15"
}
```

**Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMDE3NjU0MzExIiwiaWF0IjoxNzMzNzU1NDAwLCJleHAiOjE3MzM4NDE4MDB9.signature",
  "type": "Bearer",
  "expiresIn": 86400000,
  "usuario": {
    "id": 1,
    "documento": "1017654311",
    "nombre": "Juan Pérez García",
    "email": "juan.perez@example.com",
    "rol": "ROLE_AFILIADO"
  }
}
```

**Posibles errores:**
- `400`: Documento duplicado, validación fallida
- `500`: Error interno del servidor

---

### 1.2 Iniciar sesión (POST)
**Endpoint:** `POST /auth/login`

**Headers:**
```
Content-Type: application/json
```

**Body:**
```json
{
  "documento": "1017654311",
  "password": "SecurePassword123!"
}
```

**Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMDE3NjU0MzExIiwiaWF0IjoxNzMzNzU1NDAwLCJleHAiOjE3MzM4NDE4MDB9.signature",
  "type": "Bearer",
  "expiresIn": 86400000,
  "usuario": {
    "id": 1,
    "documento": "1017654311",
    "nombre": "Juan Pérez García",
    "email": "juan.perez@example.com",
    "rol": "ROLE_AFILIADO"
  }
}
```

**Posibles errores:**
- `401`: Credenciales inválidas
- `500`: Error interno del servidor

---

## 👥 2. AFILIADOS

### 2.1 Obtener todos los afiliados (GET)
**Endpoint:** `GET /api/v1/affiliates`

**Headers:**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Content-Type: application/json
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "documento": "1017654311",
    "nombre": "Juan Pérez García",
    "email": "juan.perez@example.com",
    "salario": 3500000,
    "fechaAfiliacion": "2024-06-15",
    "estado": "ACTIVO"
  },
  {
    "id": 2,
    "documento": "1017654312",
    "nombre": "María García López",
    "email": "maria.garcia@example.com",
    "salario": 2800000,
    "fechaAfiliacion": "2023-12-01",
    "estado": "ACTIVO"
  },
  {
    "id": 3,
    "documento": "1017654313",
    "nombre": "Carlos Rodríguez Martínez",
    "email": "carlos.rodriguez@example.com",
    "salario": 4200000,
    "fechaAfiliacion": "2023-03-10",
    "estado": "INACTIVO"
  }
]
```

**Requisitos:**
- Rol: `ROLE_ADMIN`
- Token JWT válido

**Posibles errores:**
- `401`: Token inválido o expirado
- `403`: Rol insuficiente
- `500`: Error interno del servidor

---

## 💳 3. SOLICITUDES DE CRÉDITO

### 3.1 Crear nueva solicitud de crédito (POST)
**Endpoint:** `POST /api/v1/applications`

**Headers:**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Content-Type: application/json
```

**Body:**
```json
{
  "affiliateId": 1,
  "requestedAmount": 5000000,
  "termMonths": 36
}
```

**Response (201 CREATED):**
```json
{
  "id": 1,
  "affiliateId": 1,
  "requestedAmount": 5000000,
  "termMonths": 36,
  "proposedRate": 8.5,
  "applicationDate": "2024-12-09T18:30:00Z",
  "status": "APROBADO",
  "evaluation": {
    "id": 1,
    "riskScore": 720,
    "riskLevel": "BAJO",
    "approvalReason": "Perfil crediticio favorable. Relación cuota/ingreso dentro de límites. Antigüedad suficiente.",
    "evaluationDate": "2024-12-09T18:30:05Z"
  }
}
```

**Requisitos:**
- Rol: `ROLE_AFILIADO` o `ROLE_ADMIN`
- Afiliado debe estar `ACTIVO`
- Antigüedad mínima: 6 meses
- Monto > 0
- Plazo: 12-60 meses

**Posibles errores:**

**400 - Validación fallida:**
```json
{
  "type": "https://example.com/errors/validation",
  "title": "Validación fallida",
  "status": 400,
  "detail": "El monto solicitado debe ser mayor a 0",
  "instance": "/api/v1/applications",
  "timestamp": "2024-12-09T18:30:00Z",
  "traceId": "abc123def456"
}
```

**401 - No autorizado:**
```json
{
  "type": "https://example.com/errors/unauthorized",
  "title": "No autorizado",
  "status": 401,
  "detail": "Token JWT inválido o expirado",
  "instance": "/api/v1/applications",
  "timestamp": "2024-12-09T18:30:00Z"
}
```

**403 - Acceso denegado:**
```json
{
  "type": "https://example.com/errors/forbidden",
  "title": "Acceso denegado",
  "status": 403,
  "detail": "Se requiere rol ROLE_AFILIADO o ROLE_ADMIN",
  "instance": "/api/v1/applications",
  "timestamp": "2024-12-09T18:30:00Z"
}
```

**404 - Afiliado no encontrado:**
```json
{
  "type": "https://example.com/errors/not-found",
  "title": "Recurso no encontrado",
  "status": 404,
  "detail": "Afiliado con ID 999 no existe",
  "instance": "/api/v1/applications",
  "timestamp": "2024-12-09T18:30:00Z"
}
```

---

## 🧪 Ejemplos con cURL

### Registrarse
```bash
curl -X POST http://localhost:8084/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "documento": "1017654311",
    "nombre": "Juan Pérez García",
    "email": "juan.perez@example.com",
    "password": "SecurePassword123!",
    "salario": 3500000,
    "fechaAfiliacion": "2024-06-15"
  }'
```

### Iniciar sesión
```bash
curl -X POST http://localhost:8084/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "documento": "1017654311",
    "password": "SecurePassword123!"
  }'
```

### Obtener afiliados (guardar token en variable)
```bash
TOKEN="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."

curl -X GET http://localhost:8084/api/v1/affiliates \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json"
```

### Crear solicitud de crédito
```bash
TOKEN="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."

curl -X POST http://localhost:8084/api/v1/applications \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "affiliateId": 1,
    "requestedAmount": 5000000,
    "termMonths": 36
  }'
```

---

## 📊 Endpoints de Observabilidad

### Health Check
```bash
curl http://localhost:8084/actuator/health
```

**Response:**
```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": {
        "database": "PostgreSQL",
        "validationQuery": "isValid()"
      }
    },
    "diskSpace": {
      "status": "UP"
    }
  }
}
```

### Información de la aplicación
```bash
curl http://localhost:8084/actuator/info
```

### Métricas
```bash
curl http://localhost:8084/actuator/metrics
```

### Prometheus
```bash
curl http://localhost:8084/actuator/prometheus
```

---

## 📝 Notas Importantes

1. **Token JWT**: Válido por 24 horas (86400000 ms)
2. **Documento único**: No puede haber dos usuarios con el mismo documento
3. **Antigüedad mínima**: 6 meses desde la fecha de afiliación
4. **Evaluación automática**: Al crear una solicitud, se evalúa automáticamente el riesgo
5. **Transaccionalidad**: Todo el proceso es transaccional
6. **Roles disponibles**:
   - `ROLE_AFILIADO`: Puede crear solicitudes
   - `ROLE_ANALISTA`: Puede ver solicitudes pendientes
   - `ROLE_ADMIN`: Acceso completo

---

## 🔗 Acceso a Swagger UI

```
http://localhost:8084/swagger-ui/index.html
```

Aquí puedes probar todos los endpoints de forma interactiva.
