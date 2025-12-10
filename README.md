# 🏦 CoopCredit - Sistema de Gestión de Solicitudes de Crédito

## 📋 Descripción

CoopCredit es un sistema integral de gestión de solicitudes de crédito para cooperativas, construido con **Arquitectura Hexagonal**, **Spring Boot 3.5**, **PostgreSQL** y **Seguridad JWT**. El sistema proporciona:

- ✅ Autenticación y autorización con JWT
- ✅ Gestión de afiliados (cooperativistas)
- ✅ Solicitudes de crédito con evaluación automática de riesgo
- ✅ Integración con servicio externo de evaluación de riesgo
- ✅ Validaciones avanzadas y manejo global de errores
- ✅ Observabilidad con Actuator + Micrometer
- ✅ Documentación interactiva con Swagger/OpenAPI
- ✅ Pruebas unitarias e integración
- ✅ Containerización con Docker

---

## 🚀 Inicio Rápido

### Requisitos
- Java 17+
- Maven 3.8+
- Docker & Docker Compose

### Opción 1: Ejecutar con Script (Recomendado)

```bash
# Hacer el script ejecutable
chmod +x start.sh

# Ejecutar el script que levanta PostgreSQL + Spring Boot
./start.sh
```

El script automáticamente:
1. Levanta PostgreSQL en Docker
2. Espera a que PostgreSQL esté listo
3. Compila la aplicación
4. Ejecuta Spring Boot

### Opción 2: Ejecutar con Docker Compose (Solo PostgreSQL)

```bash
# Levantar PostgreSQL
docker-compose -f docker-compose-local.yml up -d postgres

# En otra terminal, ejecutar Spring Boot
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"
```

### Opción 3: Ejecutar localmente sin Docker

```bash
# Asegúrate de que PostgreSQL esté corriendo en localhost:5432
# con usuario: root, contraseña: admin123

# Ejecutar la aplicación
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"
```

---

## 📚 Documentación

### 📖 Guía Completa de Swagger
Ver [SWAGGER_GUIDE.md](./SWAGGER_GUIDE.md) para:
- Acceso a Swagger UI
- Flujo de autenticación
- Descripción de todos los endpoints
- Ejemplos de respuestas

### 🔌 Ejemplos de Endpoints
Ver [ENDPOINTS_EXAMPLES.md](./ENDPOINTS_EXAMPLES.md) para:
- Ejemplos con cURL
- Ejemplos con JSON
- Códigos de error
- Casos de uso completos

---

## 🔐 Autenticación

### Flujo de Autenticación

1. **Registrarse**: `POST /auth/register`
   ```json
   {
     "documento": "1017654311",
     "nombre": "Juan Pérez",
     "email": "juan@example.com",
     "password": "SecurePassword123",
     "salario": 3000000,
     "fechaAfiliacion": "2024-01-15"
   }
   ```

2. **Iniciar sesión**: `POST /auth/login`
   ```json
   {
     "documento": "1017654311",
     "password": "SecurePassword123"
   }
   ```

3. **Usar token**: Incluir en header
   ```
   Authorization: Bearer <token_jwt>
   ```

---

## 📋 Endpoints Principales

### Autenticación
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/auth/register` | Registrar nuevo usuario |
| POST | `/auth/login` | Iniciar sesión |

### Afiliados
| Método | Endpoint | Descripción | Rol |
|--------|----------|-------------|-----|
| GET | `/api/v1/affiliates` | Obtener todos los afiliados | ADMIN |

### Solicitudes de Crédito
| Método | Endpoint | Descripción | Rol |
|--------|----------|-------------|-----|
| POST | `/api/v1/applications` | Crear solicitud de crédito | AFILIADO, ADMIN |

---

## 🔑 Roles y Permisos

| Rol | Permisos |
|-----|----------|
| `ROLE_AFILIADO` | Crear solicitudes de crédito |
| `ROLE_ANALISTA` | Ver solicitudes pendientes |
| `ROLE_ADMIN` | Acceso completo a todos los endpoints |

---

## 🏗️ Arquitectura

### Arquitectura Hexagonal

```
┌─────────────────────────────────────────┐
│         CAPA DE PRESENTACIÓN            │
│  (Controllers, DTOs, Mappers)           │
└────────────────┬────────────────────────┘
                 │
┌────────────────▼────────────────────────┐
│      PUERTOS DE ENTRADA (Use Cases)     │
│  (CreateApplicationUseCase, etc.)       │
└────────────────┬────────────────────────┘
                 │
┌────────────────▼────────────────────────┐
│         DOMINIO PURO                    │
│  (Modelos, Lógica de Negocio)           │
└────────────────┬────────────────────────┘
                 │
┌────────────────▼────────────────────────┐
│      PUERTOS DE SALIDA (Interfaces)     │
│  (RepositoryPort, RiskServicePort)      │
└────────────────┬────────────────────────┘
                 │
┌────────────────▼────────────────────────┐
│         ADAPTADORES                     │
│  (JPA Repositories, REST Clients)       │
└─────────────────────────────────────────┘
```

### Estructura de Carpetas

```
src/main/java/com/riwi/coopcredit/
├── domain/
│   ├── model/              # Entidades del dominio
│   ├── port/
│   │   ├── in/             # Puertos de entrada (Use Cases)
│   │   └── out/            # Puertos de salida (Interfaces)
│   └── service/            # Servicios de dominio
├── infrastructure/
│   ├── adapter/
│   │   ├── input/          # Adaptadores de entrada (Controllers)
│   │   └── output/         # Adaptadores de salida (JPA, REST)
│   ├── config/             # Configuración (Security, OpenAPI, etc.)
│   └── exception/          # Manejo de excepciones
└── application/            # Casos de uso implementados
```

---

## 🗄️ Base de Datos

### Migraciones Flyway

Las migraciones se ejecutan automáticamente al iniciar la aplicación:

- `V1__schema.sql`: Crear tablas
- `V2__relaciones.sql`: Crear relaciones
- `V3__datos_iniciales.sql`: Datos de prueba (opcional)

### Diagrama ER

```
┌──────────────────┐
│     USUARIO      │
├──────────────────┤
│ id (PK)          │
│ documento (UQ)   │
│ nombre           │
│ email            │
│ password         │
│ rol              │
└────────┬─────────┘
         │
         │ 1:N
         │
┌────────▼──────────────────┐
│  SOLICITUD_CREDITO        │
├───────────────────────────┤
│ id (PK)                   │
│ usuario_id (FK)           │
│ monto_solicitado          │
│ plazo_meses               │
│ tasa_propuesta            │
│ fecha_solicitud           │
│ estado                    │
└────────┬──────────────────┘
         │
         │ 1:1
         │
┌────────▼──────────────────┐
│  EVALUACION_RIESGO        │
├───────────────────────────┤
│ id (PK)                   │
│ solicitud_id (FK)         │
│ score_riesgo              │
│ nivel_riesgo              │
│ motivo_aprobacion         │
│ fecha_evaluacion          │
└───────────────────────────┘
```

---

## 📊 Observabilidad

### Health Check
```bash
curl http://localhost:8084/actuator/health
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

## 🧪 Pruebas

### Ejecutar pruebas unitarias
```bash
mvn test
```

### Ejecutar pruebas de integración
```bash
mvn verify
```

### Con cobertura
```bash
mvn test jacoco:report
```

---

## 🐳 Docker

### Build de la imagen
```bash
docker build -t coopcredit:latest .
```

### Ejecutar contenedor
```bash
docker run -p 8084:8080 \
  -e DB_URL_POSTGRES=jdbc:postgresql://postgres:5432/coop_credit_db \
  -e DB_USERNAME_POSTGRES=root \
  -e DB_PASSWORD_POSTGRES=admin123 \
  coopcredit:latest
```

### Docker Compose
```bash
docker-compose up -d
docker-compose down
docker-compose logs -f coopcredit-app
```

---

## 🔗 Enlaces Útiles

- **Swagger UI**: http://localhost:8081/swagger-ui/index.html
- **OpenAPI JSON**: http://localhost:8081/v3/api-docs
- **Health**: http://localhost:8081/actuator/health
- **Métricas**: http://localhost:8081/actuator/metrics
- **Prometheus**: http://localhost:8081/actuator/prometheus

---

## 📝 Configuración

### Variables de Entorno

```bash
# Base de datos
DB_URL_POSTGRES=jdbc:postgresql://localhost:5432/coop_credit_db
DB_USERNAME_POSTGRES=root
DB_PASSWORD_POSTGRES=admin123

# Servicio externo
EXTERNAL_SERVICE_URL=http://localhost:8082/risk-evaluation

# JWT
SECRET_KEY=MTIzNDU2Nzg5MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTI
EXPIRATION_TOKEN=86400000
```

---

## 🛠️ Tecnologías

- **Framework**: Spring Boot 3.5.7
- **Java**: 17
- **Base de datos**: PostgreSQL 16
- **ORM**: JPA + Hibernate
- **Seguridad**: Spring Security + JWT
- **Validación**: Bean Validation
- **Mapeo**: MapStruct
- **Documentación**: SpringDoc OpenAPI (Swagger)
- **Observabilidad**: Actuator + Micrometer + Prometheus
- **Testing**: JUnit 5 + Mockito + Testcontainers
- **Build**: Maven
- **Containerización**: Docker

---

## 📄 Licencia

Apache License 2.0

---

## 👥 Autor

CoopCredit Team

---

## 📞 Soporte

Para reportar bugs o sugerencias, contacta a: support@coopcredit.com