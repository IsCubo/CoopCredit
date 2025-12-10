# 🏦 CoopCredit - Credit Application Management System

## 📋 Description

CoopCredit is a comprehensive credit application management system for cooperatives, built with **Hexagonal Architecture**, **Spring Boot 3.5**, **PostgreSQL**, and **JWT Security**. The system provides:

- ✅ Authentication and authorization with JWT
- ✅ Affiliate (cooperative member) management
- ✅ Credit applications with automatic risk evaluation
- ✅ Integration with external risk evaluation service
- ✅ Advanced validations and global error handling
- ✅ Observability with Actuator + Micrometer
- ✅ Interactive documentation with Swagger/OpenAPI
- ✅ Unit and integration tests
- ✅ Containerization with Docker
- ✅ Database migrations with Flyway

---

## 🚀 Quick Start

### Requirements
- Java 17+
- Maven 3.8+
- Docker & Docker Compose

### Option 1: Run with Script (Recommended)

```bash
# Make the script executable
chmod +x start.sh

# Run the script that starts PostgreSQL + Spring Boot
./start.sh
```

The script automatically:
1. Starts PostgreSQL in Docker
2. Waits for PostgreSQL to be ready
3. Compiles the application
4. Executes Spring Boot

### Option 2: Run with Docker Compose (PostgreSQL Only)

```bash
# Start PostgreSQL
docker-compose -f docker-compose-local.yml up -d postgres

# In another terminal, run Spring Boot
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"
```

### Option 3: Run Locally without Docker

```bash
# Ensure PostgreSQL is running on localhost:5432
# with user: root, password: admin123

# Run the application
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"
```

---

## 📚 Documentation

### 🔗 API Access Points

- **Swagger UI**: http://localhost:8081/swagger-ui/index.html
- **OpenAPI JSON**: http://localhost:8081/v3/api-docs
- **Health Check**: http://localhost:8081/actuator/health
- **Metrics**: http://localhost:8081/actuator/metrics

---

## 🔐 Authentication

### Authentication Flow

1. **Register**: `POST /auth/register`
   ```json
   {
     "document": "1017654311",
     "username": "Juan Pérez",
     "email": "juan@example.com",
     "password": "SecurePassword123",
     "annualIncome": 3500000.00
   }
   ```

2. **Login**: `POST /auth/login`
   ```json
   {
     "username": "juan@example.com",
     "password": "SecurePassword123"
   }
   ```

3. **Response**:
   ```json
   {
     "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqdWFuQGV4YW1wbGUuY29tIiwiaWF0IjoxNzY1MzI3MDQ5LCJleHAiOjE3NjU0MTM0NDl9..."
   }
   ```

### Using the Token

Include the token in the `Authorization` header for protected endpoints:

```bash
curl -H "Authorization: Bearer YOUR_TOKEN" http://localhost:8081/api/v1/applications
```

---

## 👥 Default Users

### Admin User
- **Email**: `admin@coopcredit.com`
- **Password**: `admin123`
- **Role**: `ROLE_ADMIN`

### Affiliate User
- **Email**: `afiliado@coopcredit.com`
- **Password**: `affiliate123`
- **Role**: `ROLE_AFILIADO`

---

## 🏗️ Architecture

### Hexagonal Architecture Layers

```
┌─────────────────────────────────────────────┐
│         Input Adapters (Controllers)        │
│         REST, WebSockets, etc.              │
└──────────────┬──────────────────────────────┘
               │
┌──────────────▼──────────────────────────────┐
│         Application Layer                   │
│         Use Cases, Business Logic           │
└──────────────┬──────────────────────────────┘
               │
┌──────────────▼──────────────────────────────┐
│         Domain Layer                        │
│         Pure Business Rules                 │
└──────────────┬──────────────────────────────┘
               │
┌──────────────▼──────────────────────────────┐
│         Output Adapters                     │
│         Persistence, External Services      │
└─────────────────────────────────────────────┘
```

### Key Components

- **Domain**: Pure business logic, no framework dependencies
- **Application**: Use cases implementing business workflows
- **Infrastructure**: Database access, external service integration
- **Ports & Adapters**: Interfaces for dependency injection

---

## 🗄️ Database Schema

### Tables

- **coop_user**: User authentication and security
- **role**: User roles (ADMIN, ANALYST, AFFILIATE)
- **user_role**: Many-to-many relationship between users and roles
- **affiliate**: Cooperative member profiles
- **credit_application**: Credit application requests
- **risk_evaluation**: Risk assessment results

### Migrations

Database migrations are managed by Flyway:

1. **V1__Initial_Schema.sql** - Table structure
2. **V2__relaciones.sql** - Foreign keys and indexes
3. **V3__insert_initial_data.sql** - Initial roles and users

---

## 🔧 Configuration

### Environment Variables

```bash
# Database
DB_URL_POSTGRES=jdbc:postgresql://localhost:5432/coop_credit_db
DB_USERNAME_POSTGRES=root
DB_PASSWORD_POSTGRES=admin123

# External Services
EXTERNAL_SERVICE_URL=http://localhost:8082/risk-evaluation

# JWT
SECRET_KEY=MTIzNDU2Nzg5MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTI
EXPIRATION_TOKEN=86400000
```

### Application Properties

See `src/main/resources/application.yml` for full configuration.

---

## 🧪 Testing

### Run All Tests

```bash
mvn test
```

### Run Specific Test Class

```bash
mvn test -Dtest=AuthControllerTest
```

### Test Coverage

```bash
mvn jacoco:report
```

---

## 📊 Monitoring & Observability

### Actuator Endpoints

- `/actuator/health` - Application health status
- `/actuator/metrics` - Application metrics
- `/actuator/info` - Application information
- `/actuator/loggers` - Logger configuration

### Metrics Available

- HTTP request count and duration
- Database connection pool metrics
- JVM memory and thread metrics
- Custom application metrics

---

## 🐳 Docker Deployment

### Build Docker Image

```bash
docker build -t coopcredit:latest .
```

### Run with Docker Compose

```bash
docker-compose up -d
```

### View Logs

```bash
docker-compose logs -f coopcredit-app
```

---

## 📝 API Endpoints

### Authentication

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/auth/register` | Register new user |
| POST | `/auth/login` | User login |

### Affiliates

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/affiliates` | List all affiliates |
| GET | `/api/v1/affiliates/{id}` | Get affiliate by ID |
| POST | `/api/v1/affiliates` | Create new affiliate |
| PUT | `/api/v1/affiliates/{id}` | Update affiliate |

### Credit Applications

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/applications` | List applications |
| GET | `/api/v1/applications/{id}` | Get application by ID |
| POST | `/api/v1/applications` | Create new application |
| PUT | `/api/v1/applications/{id}` | Update application |

---

## 🛠️ Development

### Project Structure

```
src/
├── main/
│   ├── java/com/riwi/coopcredit/
│   │   ├── domain/              # Pure business logic
│   │   ├── application/         # Use cases
│   │   ├── infrastructure/      # Adapters & configuration
│   │   └── CoopCreditApplication.java
│   └── resources/
│       ├── application.yml
│       ├── application-dev.yml
│       └── db/migration/        # Flyway migrations
└── test/
    └── java/com/riwi/coopcredit/
        └── (Test classes)
```

### Build

```bash
mvn clean install
```

### Run

```bash
mvn spring-boot:run
```

---

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

---

## 👥 Contributors

- Development Team

---

## 📞 Support

For issues or questions, please open an issue on the project repository.
