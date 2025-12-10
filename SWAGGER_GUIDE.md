# 📚 Documentación Swagger - CoopCredit API

## Acceso a Swagger UI

Una vez que la aplicación esté en ejecución, accede a la documentación interactiva en:

```
http://localhost:8084/swagger-ui/index.html
```

O directamente a la especificación OpenAPI:

```
http://localhost:8084/v3/api-docs
```

---

## 🔐 Autenticación con JWT

Todos los endpoints protegidos requieren un token JWT en el header:

```
Authorization: Bearer <tu_token_jwt>
```

### Flujo de Autenticación:

1. **Registrarse**: `POST /auth/register`
2. **Iniciar sesión**: `POST /auth/login`
3. **Usar el token**: Incluir en el header `Authorization: Bearer <token>`

---

## 📋 Endpoints Disponibles

### 1. **Autenticación** (`/auth`)

#### Registrar nuevo usuario
```http
POST /auth/register
Content-Type: application/json

{
  "documento": "1017654311",
  "nombre": "Juan Pérez",
  "email": "juan@example.com",
  "password": "SecurePassword123",
  "salario": 3000000,
  "fechaAfiliacion": "2024-01-15"
}
```

**Respuesta (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "expiresIn": 86400000,
  "usuario": {
    "id": 1,
    "documento": "1017654311",
    "nombre": "Juan Pérez",
    "email": "juan@example.com",
    "rol": "ROLE_AFILIADO"
  }
}
```

---

#### Iniciar sesión
```http
POST /auth/login
Content-Type: application/json

{
  "documento": "1017654311",
  "password": "SecurePassword123"
}
```

**Respuesta (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "expiresIn": 86400000,
  "usuario": {
    "id": 1,
    "documento": "1017654311",
    "nombre": "Juan Pérez",
    "email": "juan@example.com",
    "rol": "ROLE_AFILIADO"
  }
}
```

---

### 2. **Afiliados** (`/api/v1/affiliates`)

#### Obtener todos los afiliados
```http
GET /api/v1/affiliates
Authorization: Bearer <token_jwt>
```

**Requisitos:**
- Rol requerido: `ROLE_ADMIN`
- Token JWT válido

**Respuesta (200 OK):**
```json
[
  {
    "id": 1,
    "documento": "1017654311",
    "nombre": "Juan Pérez",
    "email": "juan@example.com",
    "salario": 3000000,
    "fechaAfiliacion": "2024-01-15",
    "estado": "ACTIVO"
  },
  {
    "id": 2,
    "documento": "1017654312",
    "nombre": "María García",
    "email": "maria@example.com",
    "salario": 2500000,
    "fechaAfiliacion": "2023-08-20",
    "estado": "ACTIVO"
  }
]
```

---

### 3. **Solicitudes de Crédito** (`/api/v1/applications`)

#### Crear nueva solicitud de crédito
```http
POST /api/v1/applications
Authorization: Bearer <token_jwt>
Content-Type: application/json

{
  "affiliateId": 1,
  "requestedAmount": 5000000,
  "termMonths": 36
}
```

**Requisitos:**
- Rol requerido: `ROLE_AFILIADO` o `ROLE_ADMIN`
- Afiliado debe estar en estado `ACTIVO`
- Antigüedad mínima: 6 meses
- Monto solicitado > 0
- Plazo válido (12-60 meses)

**Respuesta (201 CREATED):**
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
    "approvalReason": "Perfil crediticio favorable",
    "evaluationDate": "2024-12-09T18:30:05Z"
  }
}
```

**Códigos de Error:**

- **400 Bad Request**: Validación fallida
  ```json
  {
    "type": "https://example.com/errors/validation",
    "title": "Validación fallida",
    "status": 400,
    "detail": "El monto solicitado debe ser mayor a 0",
    "instance": "/api/v1/applications",
    "timestamp": "2024-12-09T18:30:00Z"
  }
  ```

- **401 Unauthorized**: Token inválido o expirado
- **403 Forbidden**: Rol insuficiente
- **404 Not Found**: Afiliado no existe

---

## 🔑 Roles y Permisos

| Rol | Endpoints Accesibles |
|-----|----------------------|
| `ROLE_AFILIADO` | POST `/api/v1/applications` (crear solicitud) |
| `ROLE_ANALISTA` | GET `/api/v1/applications` (ver pendientes) |
| `ROLE_ADMIN` | Todos los endpoints |

---

## 🧪 Pruebas con cURL

### 1. Registrarse
```bash
curl -X POST http://localhost:8084/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "documento": "1017654311",
    "nombre": "Juan Pérez",
    "email": "juan@example.com",
    "password": "SecurePassword123",
    "salario": 3000000,
    "fechaAfiliacion": "2024-01-15"
  }'
```

### 2. Iniciar sesión
```bash
curl -X POST http://localhost:8084/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "documento": "1017654311",
    "password": "SecurePassword123"
  }'
```

### 3. Crear solicitud de crédito
```bash
curl -X POST http://localhost:8084/api/v1/applications \
  -H "Authorization: Bearer <token_jwt>" \
  -H "Content-Type: application/json" \
  -d '{
    "affiliateId": 1,
    "requestedAmount": 5000000,
    "termMonths": 36
  }'
```

### 4. Obtener afiliados (Admin)
```bash
curl -X GET http://localhost:8084/api/v1/affiliates \
  -H "Authorization: Bearer <token_jwt>"
```

---

## 📊 Métricas y Health Check

### Health Check
```
GET http://localhost:8084/actuator/health
```

### Métricas
```
GET http://localhost:8084/actuator/metrics
```

### Prometheus
```
GET http://localhost:8084/actuator/prometheus
```

---

## 🚀 Ejecutar la Aplicación

### Localmente
```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"
```

### Con Docker Compose
```bash
docker-compose up -d
```

---

## 📝 Notas Importantes

1. **Token JWT**: Válido por 24 horas (86400000 ms)
2. **Documento único**: No puede haber dos usuarios con el mismo documento
3. **Antigüedad mínima**: 6 meses desde la fecha de afiliación
4. **Evaluación automática**: Al crear una solicitud, se evalúa automáticamente el riesgo
5. **Transaccionalidad**: Todo el proceso es transaccional

---

## 🔗 Enlaces Útiles

- **Swagger UI**: http://localhost:8084/swagger-ui/index.html
- **OpenAPI JSON**: http://localhost:8084/v3/api-docs
- **Health**: http://localhost:8084/actuator/health
- **Métricas**: http://localhost:8084/actuator/metrics
