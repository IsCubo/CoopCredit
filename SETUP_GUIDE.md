# 🚀 CoopCredit Setup & Installation Guide

## Prerequisites

Before setting up CoopCredit, ensure you have the following installed:

### Required Software

- **Java Development Kit (JDK) 17+**
  ```bash
  java -version
  # Output: openjdk version "17.0.x" or higher
  ```

- **Apache Maven 3.8+**
  ```bash
  mvn -version
  # Output: Apache Maven 3.8.x or higher
  ```

- **Docker & Docker Compose**
  ```bash
  docker --version
  docker-compose --version
  ```

- **Git** (for cloning the repository)
  ```bash
  git --version
  ```

### Optional Software

- **PostgreSQL Client** (for direct database access)
  ```bash
  psql --version
  ```

- **cURL** (for testing API endpoints)
  ```bash
  curl --version
  ```

---

## Installation Steps

### Step 1: Clone the Repository

```bash
git clone https://github.com/your-org/coopcredit.git
cd coopcredit
```

### Step 2: Verify Project Structure

```bash
ls -la
# Expected output:
# drwxr-xr-x  src/
# drwxr-xr-x  target/
# -rw-r--r--  pom.xml
# -rw-r--r--  README.md
# -rw-r--r--  docker-compose-local.yml
# -rwxr-xr-x  start.sh
```

### Step 3: Configure Environment Variables

Create a `.env` file in the project root:

```bash
cat > .env << EOF
# Database Configuration
DB_URL_POSTGRES=jdbc:postgresql://localhost:5432/coop_credit_db
DB_USERNAME_POSTGRES=root
DB_PASSWORD_POSTGRES=admin123

# External Services
EXTERNAL_SERVICE_URL=http://localhost:8082/risk-evaluation

# JWT Configuration
SECRET_KEY=MTIzNDU2Nzg5MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTI
EXPIRATION_TOKEN=86400000

# Application
SPRING_PROFILES_ACTIVE=dev
EOF
```

### Step 4: Build the Project

```bash
# Clean and build
mvn clean install

# Or just compile without tests
mvn clean compile -DskipTests
```

### Step 5: Start PostgreSQL

#### Option A: Using Docker Compose (Recommended)

```bash
# Start PostgreSQL container
docker-compose -f docker-compose-local.yml up -d postgres

# Verify it's running
docker-compose -f docker-compose-local.yml ps

# Expected output:
# NAME                COMMAND                  STATE
# coopcredit_postgres docker-entrypoint.sh ... Up (healthy)
```

#### Option B: Using Local PostgreSQL

If you have PostgreSQL installed locally:

```bash
# Start PostgreSQL service
sudo systemctl start postgresql

# Create database
createdb -U postgres coop_credit_db

# Create user
psql -U postgres -c "CREATE USER root WITH PASSWORD 'admin123';"
psql -U postgres -c "ALTER ROLE root WITH SUPERUSER;"
```

### Step 6: Run Database Migrations

Flyway will automatically run migrations on application startup. To verify:

```bash
# Check migration status
docker exec coopcredit_postgres psql -U root -d coop_credit_db -c "\dt"

# Expected output:
# public | affiliate
# public | credit_application
# public | coop_user
# public | role
# public | risk_evaluation
# public | user_role
```

### Step 7: Start the Application

#### Option A: Using the Startup Script (Recommended)

```bash
# Make script executable
chmod +x start.sh

# Run the script
./start.sh
```

#### Option B: Using Maven

```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"
```

#### Option C: Using Java Directly

```bash
# Build JAR
mvn clean package -DskipTests

# Run JAR
java -jar target/CoopCredit-1.0.0.jar
```

### Step 8: Verify Application is Running

```bash
# Check health endpoint
curl http://localhost:8081/actuator/health

# Expected output:
# {"status":"UP","components":{"db":{"status":"UP"},...}}

# Access Swagger UI
open http://localhost:8081/swagger-ui/index.html
```

---

## Configuration

### Application Properties

Edit `src/main/resources/application.yml`:

```yaml
server:
  port: 8081

spring:
  application:
    name: CoopCredit
  
  datasource:
    url: jdbc:postgresql://localhost:5432/coop_credit_db
    username: root
    password: admin123
  
  jpa:
    hibernate:
      ddl-auto: validate
  
  flyway:
    enabled: true
    locations: classpath:db/migration

jwt:
  secret:
    key: ${SECRET_KEY:MTIzNDU2Nzg5MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTI}
  expiration:
    time: ${EXPIRATION_TOKEN:86400000}
```

### Development Profile

For development, use `application-dev.yml`:

```yaml
spring:
  jpa:
    show-sql: true
    properties:
      hibernate:
        format_sql: true

logging:
  level:
    com.riwi.coopcredit: DEBUG
    org.hibernate.SQL: DEBUG
```

---

## Docker Setup

### Build Docker Image

```bash
# Build the image
docker build -t coopcredit:latest .

# Verify image
docker images | grep coopcredit
```

### Run with Docker Compose

```bash
# Start all services
docker-compose up -d

# View logs
docker-compose logs -f coopcredit-app

# Stop services
docker-compose down

# Stop and remove volumes
docker-compose down -v
```

### Docker Compose Services

```yaml
services:
  postgres:
    image: postgres:16-alpine
    ports:
      - "5432:5432"
    environment:
      POSTGRES_USER: root
      POSTGRES_PASSWORD: admin123
      POSTGRES_DB: coop_credit_db
  
  coopcredit-app:
    build: .
    ports:
      - "8081:8081"
    depends_on:
      postgres:
        condition: service_healthy
```

---

## Troubleshooting

### Issue: Port Already in Use

**Problem**: `Address already in use: bind`

**Solution**:

```bash
# Find process using port 8081
lsof -i :8081

# Kill the process
kill -9 <PID>

# Or change the port in application.yml
server:
  port: 8082
```

### Issue: Database Connection Failed

**Problem**: `Connection refused`

**Solution**:

```bash
# Check if PostgreSQL is running
docker-compose ps

# Restart PostgreSQL
docker-compose restart postgres

# Check logs
docker-compose logs postgres
```

### Issue: Migration Failed

**Problem**: `Flyway migration error`

**Solution**:

```bash
# Check migration files
ls -la src/main/resources/db/migration/

# Verify database schema
docker exec coopcredit_postgres psql -U root -d coop_credit_db -c "\dt"

# Reset database (WARNING: Deletes all data)
docker-compose down -v
docker-compose up -d postgres
```

### Issue: Authentication Failed

**Problem**: `401 Unauthorized`

**Solution**:

```bash
# Verify default users exist
docker exec coopcredit_postgres psql -U root -d coop_credit_db -c "SELECT * FROM coop_user;"

# Check JWT configuration
curl http://localhost:8081/actuator/health
```

### Issue: Out of Memory

**Problem**: `java.lang.OutOfMemoryError`

**Solution**:

```bash
# Increase JVM memory
export JAVA_OPTS="-Xmx512m -Xms256m"
mvn spring-boot:run
```

---

## Development Workflow

### 1. Local Development

```bash
# Start PostgreSQL
docker-compose -f docker-compose-local.yml up -d postgres

# Run application
mvn spring-boot:run

# Make code changes
# Changes are automatically reloaded with Spring DevTools
```

### 2. Testing

```bash
# Run all tests
mvn test

# Run specific test
mvn test -Dtest=AuthControllerTest

# Run with coverage
mvn jacoco:report
```

### 3. Building

```bash
# Build JAR
mvn clean package

# Build with tests
mvn clean package

# Build without tests
mvn clean package -DskipTests
```

### 4. Deployment

```bash
# Build Docker image
docker build -t coopcredit:v1.0.0 .

# Push to registry
docker push your-registry/coopcredit:v1.0.0

# Deploy
docker run -d -p 8081:8081 coopcredit:v1.0.0
```

---

## IDE Setup

### IntelliJ IDEA

1. Open project: `File > Open > Select project folder`
2. Configure JDK: `File > Project Structure > Project > SDK > JDK 17+`
3. Enable annotation processing: `Settings > Build > Compiler > Annotation Processors > Enable`
4. Install Lombok plugin: `Settings > Plugins > Search "Lombok" > Install`

### Visual Studio Code

1. Install extensions:
   - Extension Pack for Java
   - Spring Boot Extension Pack
   - REST Client

2. Create `.vscode/launch.json`:

```json
{
  "version": "0.2.0",
  "configurations": [
    {
      "type": "java",
      "name": "Spring Boot App",
      "request": "launch",
      "mainClass": "com.riwi.coopcredit.CoopCreditApplication",
      "projectName": "CoopCredit",
      "cwd": "${workspaceFolder}",
      "console": "integratedTerminal"
    }
  ]
}
```

### Eclipse

1. Import project: `File > Import > Existing Maven Projects`
2. Configure JDK: `Project > Properties > Java Compiler > JDK Compliance > 17`
3. Install Lombok: `Help > Eclipse Marketplace > Search "Lombok" > Install`

---

## Performance Tuning

### Database Optimization

```yaml
spring:
  jpa:
    properties:
      hibernate:
        jdbc:
          batch_size: 20
        order_inserts: true
        order_updates: true
        default_batch_fetch_size: 50
```

### Connection Pool

```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 10
      minimum-idle: 5
      connection-timeout: 20000
      idle-timeout: 300000
      max-lifetime: 1200000
```

### Caching

```yaml
spring:
  cache:
    type: simple
    cache-names:
      - affiliates
      - applications
```

---

## Security Checklist

- [ ] Change default JWT secret key
- [ ] Change default database credentials
- [ ] Enable HTTPS in production
- [ ] Configure CORS properly
- [ ] Implement rate limiting
- [ ] Set up API key authentication
- [ ] Enable audit logging
- [ ] Configure firewall rules
- [ ] Use environment variables for secrets
- [ ] Implement input validation

---

## Next Steps

1. **Read Documentation**:
   - [README_EN.md](./README_EN.md) - Project overview
   - [API_DOCUMENTATION.md](./API_DOCUMENTATION.md) - API reference
   - [ARCHITECTURE.md](./ARCHITECTURE.md) - System design

2. **Explore Code**:
   - Check `src/main/java` for source code
   - Review `src/test/java` for test examples

3. **Test API**:
   - Visit http://localhost:8081/swagger-ui/index.html
   - Try example requests

4. **Contribute**:
   - Create feature branches
   - Write tests for new features
   - Submit pull requests

---

## Support

For issues or questions:

1. Check [Troubleshooting](#troubleshooting) section
2. Review logs: `docker-compose logs coopcredit-app`
3. Open an issue on GitHub
4. Contact the development team

---

## Additional Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
- [Docker Documentation](https://docs.docker.com/)
- [JWT Introduction](https://jwt.io/introduction)
- [Hexagonal Architecture](https://en.wikipedia.org/wiki/Hexagonal_architecture)
