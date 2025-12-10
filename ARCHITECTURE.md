# 🏗️ CoopCredit Architecture

## Overview

CoopCredit follows a **Hexagonal Architecture** (also known as Ports and Adapters pattern) to ensure clean separation of concerns, testability, and maintainability.

---

## Architecture Layers

### 1. Domain Layer (Core)

**Location**: `src/main/java/com/riwi/coopcredit/domain/`

The domain layer contains pure business logic with **no framework dependencies**.

**Components**:

- **Models**: Core business entities
  - `Affiliate` - Cooperative member
  - `CreditApplication` - Credit request
  - `ApplicationStatus` - Enum for application states

- **Ports**: Interfaces defining contracts
  - Input Ports (Use Cases): `CreateApplicationUseCase`, `RegisterAffiliateUseCase`
  - Output Ports: `AffiliateRepositoryPort`, `RiskExternalPort`

- **Exceptions**: Domain-specific exceptions
  - `DomainException` - Base exception for business logic errors

**Key Principle**: The domain layer is completely independent of any framework or technology.

---

### 2. Application Layer (Use Cases)

**Location**: `src/main/java/com/riwi/coopcredit/application/usecase/`

The application layer implements business workflows using domain models and ports.

**Components**:

- **Use Cases**: Orchestrate business logic
  - `CreateApplicationUseCaseImpl` - Creates and evaluates credit applications
  - `RegisterAffiliateUseCaseImpl` - Registers new affiliates
  - `EvaluateApplicationUseCaseImpl` - Evaluates application risk

**Responsibilities**:

- Coordinate between domain and infrastructure
- Implement business workflows
- Handle transactions
- Validate business rules

**Example**:

```java
@Service
@Transactional
public class CreateApplicationUseCaseImpl implements CreateApplicationUseCase {
    
    private final AffiliateRepositoryPort affiliateRepository;
    private final CreditApplicationRepositoryPort applicationRepository;
    private final RiskExternalPort riskService;
    
    @Override
    public CreditApplication create(Long affiliateId, BigDecimal amount, Integer term) {
        // 1. Fetch affiliate
        Affiliate affiliate = affiliateRepository.findById(affiliateId)
            .orElseThrow(() -> new DomainException("Affiliate not found"));
        
        // 2. Create application
        CreditApplication app = new CreditApplication(amount, term, affiliate);
        
        // 3. Evaluate risk
        Integer riskScore = riskService.getRiskScore(
            affiliate.getDocument(),
            amount.doubleValue(),
            term
        );
        
        // 4. Determine status based on risk
        if (riskScore >= 700) {
            app.setStatus(ApplicationStatus.APROBADA);
        } else if (riskScore >= 500) {
            app.setStatus(ApplicationStatus.APROBADA);
        } else {
            app.setStatus(ApplicationStatus.RECHAZADA);
        }
        
        // 5. Persist
        return applicationRepository.save(app);
    }
}
```

---

### 3. Infrastructure Layer (Adapters)

**Location**: `src/main/java/com/riwi/coopcredit/infrastructure/`

The infrastructure layer implements ports and handles external concerns.

#### 3.1 Input Adapters (Controllers)

**Location**: `infrastructure/adapter/input/controller/`

REST controllers that expose API endpoints.

**Components**:

- `AuthController` - Authentication endpoints
- `AffiliateController` - Affiliate management
- `CreditApplicationController` - Application management

**Example**:

```java
@RestController
@RequestMapping("/auth")
public class AuthController {
    
    private final AuthenticationService authService;
    
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.ok(response);
    }
}
```

#### 3.2 Output Adapters (Persistence)

**Location**: `infrastructure/adapter/output/persistence/`

Implements repository ports for data persistence.

**Components**:

- **Repositories**: Spring Data JPA interfaces
  - `AffiliateJpaRepository`
  - `CreditApplicationJpaRepository`
  - `UserRepository`

- **Persistence Adapters**: Implement domain ports
  - `AffiliatePersistenceAdapter` implements `AffiliateRepositoryPort`
  - `CreditApplicationPersistenceAdapter` implements `CreditApplicationRepositoryPort`

- **Entities**: JPA entity classes
  - `AffiliateEntity`
  - `CreditApplicationEntity`
  - `UserEntity`

- **Mappers**: MapStruct mappers for entity-to-domain conversion
  - `AffiliateMapper`
  - `CreditApplicationMapper`

**Example**:

```java
@Service
@RequiredArgsConstructor
public class AffiliatePersistenceAdapter implements AffiliateRepositoryPort {
    
    private final AffiliateJpaRepository repository;
    private final AffiliateMapper mapper;
    
    @Override
    public Affiliate save(Affiliate affiliate) {
        AffiliateEntity entity = mapper.toEntity(affiliate);
        AffiliateEntity saved = repository.save(entity);
        return mapper.toDomain(saved);
    }
    
    @Override
    public Optional<Affiliate> findById(Long id) {
        return repository.findById(id)
            .map(mapper::toDomain);
    }
}
```

#### 3.3 External Service Adapters

**Location**: `infrastructure/adapter/output/external/`

Integrates with external services.

**Components**:

- `RiskCentralAdapter` - Calls external risk evaluation service
- `RestTemplateConfig` - HTTP client configuration

**Example**:

```java
@Service
public class RiskCentralAdapter implements RiskExternalPort {
    
    private final RestTemplate restTemplate;
    
    @Override
    public Integer getRiskScore(String document, Double amount, Integer term) {
        RiskRequest request = new RiskRequest(document, amount, term);
        RiskResponse response = restTemplate.postForObject(
            riskServiceUrl,
            request,
            RiskResponse.class
        );
        return response.getScore();
    }
}
```

#### 3.4 Configuration

**Location**: `infrastructure/config/`

Spring configuration classes.

**Components**:

- `SecurityConfig` - Spring Security configuration
  - JWT authentication
  - CORS configuration
  - Authorization rules

- `ApplicationConfig` - Application beans
  - Password encoder
  - Authentication manager
  - User details service

- `MapStructConfig` - MapStruct configuration

---

## Dependency Flow

```
┌─────────────────────────────────────────┐
│      REST Controllers (Input)           │
│  (AuthController, AffiliateController)  │
└──────────────┬──────────────────────────┘
               │ depends on
┌──────────────▼──────────────────────────┐
│      Use Cases (Application)            │
│  (CreateApplicationUseCase, etc.)       │
└──────────────┬──────────────────────────┘
               │ depends on
┌──────────────▼──────────────────────────┐
│      Domain Models & Ports              │
│  (Affiliate, CreditApplication, etc.)   │
└──────────────┬──────────────────────────┘
               │ depends on
┌──────────────▼──────────────────────────┐
│      Output Adapters (Persistence)      │
│  (Repositories, External Services)      │
└─────────────────────────────────────────┘
```

**Key Principle**: Dependencies flow inward toward the domain. The domain never depends on outer layers.

---

## Data Flow Example: Create Credit Application

```
1. HTTP Request
   POST /api/v1/applications
   {
     "affiliateId": 1,
     "requestedAmount": 5000000,
     "termMonths": 36
   }
   │
   ▼
2. Controller (Input Adapter)
   CreditApplicationController.create()
   │
   ▼
3. Use Case (Application Layer)
   CreateApplicationUseCaseImpl.create()
   │
   ├─ Fetch Affiliate (Domain Port)
   │  └─ AffiliatePersistenceAdapter.findById()
   │     └─ AffiliateJpaRepository.findById()
   │        └─ Database Query
   │
   ├─ Create Application (Domain Model)
   │  └─ new CreditApplication(amount, term, affiliate)
   │
   ├─ Evaluate Risk (External Port)
   │  └─ RiskCentralAdapter.getRiskScore()
   │     └─ HTTP Call to Risk Service
   │
   ├─ Determine Status (Business Logic)
   │  └─ if (riskScore >= 700) APROBADA else RECHAZADA
   │
   └─ Save Application (Domain Port)
      └─ CreditApplicationPersistenceAdapter.save()
         └─ CreditApplicationJpaRepository.save()
            └─ Database Insert
   │
   ▼
4. Response (Output Adapter)
   {
     "id": 1,
     "affiliateId": 1,
     "requestedAmount": 5000000,
     "termMonths": 36,
     "status": "APROBADA",
     "riskScore": 750
   }
```

---

## Technology Stack

### Core Framework
- **Spring Boot 3.5** - Application framework
- **Spring Security** - Authentication & authorization
- **Spring Data JPA** - Data persistence

### Database
- **PostgreSQL 16** - Primary database
- **Flyway** - Database migrations
- **Hibernate** - ORM

### API & Documentation
- **Spring Web** - REST API
- **Springdoc OpenAPI** - Swagger/OpenAPI documentation

### Utilities
- **MapStruct** - Entity-to-domain mapping
- **Lombok** - Boilerplate reduction
- **JWT (jjwt)** - JSON Web Tokens

### Testing
- **JUnit 5** - Unit testing
- **Mockito** - Mocking
- **Spring Boot Test** - Integration testing

### Monitoring
- **Spring Boot Actuator** - Health checks & metrics
- **Micrometer** - Metrics collection

---

## Design Patterns

### 1. Hexagonal Architecture
Separates business logic from technical concerns through ports and adapters.

### 2. Repository Pattern
Abstracts data access through repository interfaces.

### 3. Mapper Pattern
Converts between domain models and persistence entities using MapStruct.

### 4. Dependency Injection
Uses Spring's DI container for loose coupling.

### 5. Use Case Pattern
Implements business workflows as separate use case classes.

### 6. Strategy Pattern
Different risk evaluation strategies can be plugged in through the `RiskExternalPort`.

---

## Security Architecture

### Authentication Flow

```
1. User Registration
   POST /auth/register
   └─ Create User & Affiliate
   └─ Return JWT Token

2. User Login
   POST /auth/login
   └─ Validate Credentials
   └─ Generate JWT Token

3. Protected Request
   GET /api/v1/applications
   Header: Authorization: Bearer <token>
   └─ JwtAuthenticationFilter validates token
   └─ SecurityContext stores authentication
   └─ Request proceeds if authorized
```

### Authorization Levels

- **ROLE_ADMIN**: Full system access
- **ROLE_ANALYST**: Can view and approve applications
- **ROLE_AFILIADO**: Can view own applications

---

## Database Schema

### Entity Relationships

```
┌──────────────┐
│   coop_user  │
├──────────────┤
│ id (PK)      │
│ username     │
│ password     │
│ is_enabled   │
└──────────────┘
      │
      │ 1:N
      ▼
┌──────────────┐
│  user_role   │
├──────────────┤
│ user_id (FK) │
│ role_id (FK) │
└──────────────┘
      │
      │ N:1
      ▼
┌──────────────┐
│    role      │
├──────────────┤
│ id (PK)      │
│ name         │
└──────────────┘

┌──────────────┐
│  affiliate   │
├──────────────┤
│ id (PK)      │
│ document     │
│ email        │
│ user_id (FK) │
└──────────────┘
      │
      │ 1:N
      ▼
┌─────────────────────┐
│ credit_application  │
├─────────────────────┤
│ id (PK)             │
│ affiliate_id (FK)   │
│ requested_amount    │
│ status              │
│ risk_score          │
└─────────────────────┘
      │
      │ 1:1
      ▼
┌─────────────────────┐
│  risk_evaluation    │
├─────────────────────┤
│ id (PK)             │
│ application_id (FK) │
│ score               │
│ risk_level          │
└─────────────────────┘
```

---

## Error Handling

### Global Exception Handler

The `GlobalExceptionHandler` provides centralized error handling:

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ProblemDetail> handleDomainException(DomainException ex) {
        return ResponseEntity.badRequest()
            .body(ProblemDetail.forStatus(400)
                .withTitle("Business Logic Error")
                .withDetail(ex.getMessage()));
    }
}
```

### Error Response Format (RFC 7807)

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

## Testing Strategy

### Unit Tests
- Test domain models and business logic
- Mock external dependencies
- Use Mockito for mocking

### Integration Tests
- Test use cases with real repositories
- Use `@SpringBootTest` for Spring context
- Use `MockMvc` for testing controllers

### Test Structure

```
src/test/java/com/riwi/coopcredit/
├── domain/
│   └── model/
│       └── CreditApplicationTest.java
├── application/
│   └── usecase/
│       └── CreateApplicationUseCaseImplTest.java
└── infrastructure/
    └── adapter/
        ├── input/
        │   └── controller/
        │       └── AuthControllerTest.java
        └── output/
            └── persistence/
                └── AffiliatePersistenceAdapterTest.java
```

---

## Deployment Architecture

### Local Development

```
Developer Machine
├─ Spring Boot App (port 8081)
└─ PostgreSQL (port 5432)
```

### Docker Deployment

```
Docker Host
├─ coopcredit-app container
│  └─ Spring Boot App (port 8081)
├─ postgres container
│  └─ PostgreSQL (port 5432)
└─ Shared Network
```

### Production Considerations

- Use environment-specific configurations
- Implement proper logging and monitoring
- Set up database backups
- Use secrets management for sensitive data
- Implement API rate limiting
- Set up CI/CD pipeline

---

## Future Enhancements

1. **Microservices**: Split into separate services
2. **Event-Driven Architecture**: Use message queues
3. **Caching**: Implement Redis for performance
4. **GraphQL**: Add GraphQL API alongside REST
5. **API Versioning**: Support multiple API versions
6. **Advanced Monitoring**: Implement distributed tracing
7. **Load Balancing**: Horizontal scaling support
