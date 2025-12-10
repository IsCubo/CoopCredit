# 🧪 Guía de Pruebas - CoopCredit

## Descripción General

Este documento describe la estrategia de pruebas implementada en CoopCredit, incluyendo pruebas unitarias, de integración y mejores prácticas.

---

## 📋 Estructura de Pruebas

```
src/test/java/com/riwi/coopcredit/
├── application/
│   └── usecase/
│       ├── RegisterAffiliateUseCaseImplTest.java
│       └── CreateApplicationUseCaseImplTest.java
├── domain/
│   └── model/
│       ├── AffiliateTest.java
│       └── CreditApplicationTest.java
└── infrastructure/
    └── adapter/
        └── input/
            └── controller/
                └── AuthControllerTest.java
```

---

## 🧬 Pruebas Unitarias

### 1. RegisterAffiliateUseCaseImplTest

**Ubicación:** `src/test/java/com/riwi/coopcredit/application/usecase/RegisterAffiliateUseCaseImplTest.java`

**Propósito:** Validar la lógica de negocio del caso de uso de registro de afiliados.

**Casos de prueba:**

- ✅ `testRegisterAffiliateSuccess`: Registra un nuevo afiliado exitosamente
- ✅ `testRegisterAffiliateWithDuplicateDocument`: Lanza excepción cuando el documento ya existe
- ✅ `testRegisterAffiliateWithInvalidSalary`: Lanza excepción cuando el salario es inválido
- ✅ `testRegisterAffiliateWithZeroSalary`: Lanza excepción cuando el salario es cero
- ✅ `testRegisterAffiliateCallsRepositoryOnce`: Verifica que el repositorio se llama una sola vez

**Ejecución:**
```bash
mvn test -Dtest=RegisterAffiliateUseCaseImplTest
```

---

### 2. CreateApplicationUseCaseImplTest

**Ubicación:** `src/test/java/com/riwi/coopcredit/application/usecase/CreateApplicationUseCaseImplTest.java`

**Propósito:** Validar la lógica de evaluación de solicitudes de crédito.

**Casos de prueba:**

- ✅ `testCreateApplicationWithLowRiskApproved`: Crea solicitud con riesgo bajo (APROBADA)
- ✅ `testCreateApplicationWithMediumRiskApproved`: Crea solicitud con riesgo medio (APROBADA)
- ✅ `testCreateApplicationWithHighRiskRejected`: Crea solicitud con riesgo alto (RECHAZADA)
- ✅ `testCreateApplicationAffiliateNotFound`: Lanza excepción cuando el afiliado no existe
- ✅ `testCreateApplicationRiskServiceFails`: Maneja fallos del servicio de riesgo
- ✅ `testCreateApplicationSavesTwice`: Verifica que se guarda dos veces (inicial y actualizada)
- ✅ `testCreateApplicationConvertsBigDecimalToDouble`: Valida conversión de tipos
- ✅ `testCreateApplicationWithBoundaryRiskScore500`: Prueba límite de score 500
- ✅ `testCreateApplicationWithBoundaryRiskScore700`: Prueba límite de score 700

**Ejecución:**
```bash
mvn test -Dtest=CreateApplicationUseCaseImplTest
```

---

### 3. AffiliateTest

**Ubicación:** `src/test/java/com/riwi/coopcredit/domain/model/AffiliateTest.java`

**Propósito:** Validar el modelo de dominio Affiliate.

**Casos de prueba:**

- ✅ `testCreateValidAffiliateWithoutId`: Crea un Affiliate válido sin ID
- ✅ `testCreateValidAffiliateWithId`: Crea un Affiliate válido con ID
- ✅ `testCreateAffiliateWithNegativeSalary`: Lanza excepción con salario negativo
- ✅ `testCreateAffiliateWithZeroSalary`: Lanza excepción con salario cero
- ✅ `testCreateAffiliateWithInvalidSalaries`: Pruebas parametrizadas con salarios inválidos
- ✅ `testCreateAffiliateWithValidSalaries`: Pruebas parametrizadas con salarios válidos
- ✅ `testUpdateAnnualIncome`: Actualiza el salario correctamente
- ✅ `testUpdateDocument`: Actualiza el documento correctamente
- ✅ `testUpdateEmail`: Actualiza el email correctamente

**Ejecución:**
```bash
mvn test -Dtest=AffiliateTest
```

---

### 4. CreditApplicationTest

**Ubicación:** `src/test/java/com/riwi/coopcredit/domain/model/CreditApplicationTest.java`

**Propósito:** Validar el modelo de dominio CreditApplication.

**Casos de prueba:**

- ✅ `testCreateValidCreditApplication`: Crea una solicitud válida
- ✅ `testSetApplicationStatus`: Establece el estado correctamente
- ✅ `testSetRiskScore`: Establece el score de riesgo correctamente
- ✅ `testSetRiskLevel`: Establece el nivel de riesgo correctamente
- ✅ `testIsPendingTrue`: Verifica que está en estado PENDIENTE
- ✅ `testIsPendingFalse`: Verifica que NO está en estado PENDIENTE
- ✅ `testChangeStatusFromPendingToRejected`: Cambia estado de PENDIENTE a RECHAZADA
- ✅ `testMultipleStatusChanges`: Prueba múltiples cambios de estado
- ✅ `testApplicationDatePersistsAfterStatusChange`: Verifica que la fecha persiste

**Ejecución:**
```bash
mvn test -Dtest=CreditApplicationTest
```

---

## 🔗 Pruebas de Integración

### AuthControllerTest

**Ubicación:** `src/test/java/com/riwi/coopcredit/infrastructure/adapter/input/controller/AuthControllerTest.java`

**Propósito:** Validar los endpoints de autenticación.

**Casos de prueba:**

- ✅ `testRegisterUserSuccess`: Registra un nuevo usuario exitosamente
- ✅ `testRegisterUserWithInvalidData`: Retorna 400 con datos inválidos
- ✅ `testLoginUserSuccess`: Inicia sesión exitosamente
- ✅ `testLoginUserWithInvalidCredentials`: Retorna 401 con credenciales inválidas
- ✅ `testRegisterUserWithEmptyBody`: Retorna 400 con body vacío
- ✅ `testRegisterUserWithWrongContentType`: Retorna 400 con Content-Type incorrecto

**Ejecución:**
```bash
mvn test -Dtest=AuthControllerTest
```

---

## 🛠️ Herramientas y Dependencias

### JUnit 5 (Jupiter)
Framework de pruebas moderno para Java.

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
```

### Mockito
Framework para crear mocks y stubs.

```xml
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-core</artifactId>
    <scope>test</scope>
</dependency>
```

### Spring Boot Test
Utilidades para pruebas de Spring Boot.

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
```

### Testcontainers (Opcional)
Para pruebas con contenedores Docker.

```xml
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>postgresql</artifactId>
    <scope>test</scope>
</dependency>
```

---

## 🚀 Ejecutar Pruebas

### Ejecutar todas las pruebas
```bash
mvn test
```

### Ejecutar una clase de prueba específica
```bash
mvn test -Dtest=RegisterAffiliateUseCaseImplTest
```

### Ejecutar un método de prueba específico
```bash
mvn test -Dtest=RegisterAffiliateUseCaseImplTest#testRegisterAffiliateSuccess
```

### Ejecutar pruebas con cobertura
```bash
mvn test jacoco:report
```

### Ver reporte de cobertura
```bash
open target/site/jacoco/index.html
```

### Ejecutar pruebas en paralelo
```bash
mvn test -DparallelTestCount=4
```

---

## 📊 Patrones de Prueba

### 1. Arrange-Act-Assert (AAA)

Todas las pruebas siguen el patrón AAA:

```java
@Test
void testExample() {
    // Arrange: Preparar datos de prueba
    Affiliate affiliate = new Affiliate(...);
    
    // Act: Ejecutar la acción
    Affiliate result = registerAffiliateUseCase.register(affiliate);
    
    // Assert: Verificar resultados
    assertNotNull(result);
    assertEquals("1017654311", result.getDocument());
}
```

### 2. Mocking con Mockito

```java
@Mock
private AffiliateRepositoryPort affiliateRepositoryPort;

@InjectMocks
private RegisterAffiliateUseCaseImpl registerAffiliateUseCase;

@BeforeEach
void setUp() {
    // Configurar comportamiento del mock
    when(affiliateRepositoryPort.findByDocument("1017654311"))
        .thenReturn(Optional.empty());
}
```

### 3. Pruebas Parametrizadas

```java
@ParameterizedTest
@ValueSource(doubles = {0.01, 1.0, 1000.0, 3500000.0})
void testCreateAffiliateWithValidSalaries(double salary) {
    Affiliate affiliate = new Affiliate(..., salary);
    assertNotNull(affiliate);
}
```

### 4. Pruebas de Excepciones

```java
@Test
void testRegisterAffiliateWithDuplicateDocument() {
    when(affiliateRepositoryPort.findByDocument("1017654311"))
        .thenReturn(Optional.of(existingAffiliate));
    
    DomainException exception = assertThrows(DomainException.class, () -> {
        registerAffiliateUseCase.register(affiliate);
    });
    
    assertTrue(exception.getMessage().contains("ya se encuentra registrado"));
}
```

---

## ✅ Mejores Prácticas

1. **Nombres descriptivos**: Los nombres de los tests describen claramente qué se prueba
   ```java
   void testRegisterAffiliateWithDuplicateDocument() // ✅ Bueno
   void test1() // ❌ Malo
   ```

2. **Una asercción principal por test**: Cada test valida un comportamiento específico
   ```java
   // ✅ Bueno: Una asercción principal
   assertEquals(ApplicationStatus.APROBADA, result.getStatus());
   
   // ❌ Malo: Múltiples aserciones sin relación
   assertEquals(1L, result.getId());
   assertEquals("Juan", result.getName());
   assertEquals(3500000.0, result.getSalary());
   ```

3. **Usar @DisplayName**: Proporciona descripción legible en reportes
   ```java
   @DisplayName("Debe registrar un nuevo afiliado exitosamente")
   void testRegisterAffiliateSuccess() { ... }
   ```

4. **Verificar interacciones con Mockito**: Asegurar que los métodos se llaman correctamente
   ```java
   verify(affiliateRepositoryPort, times(1)).findByDocument("1017654311");
   verify(affiliateRepositoryPort, never()).save(any());
   ```

5. **Usar @BeforeEach para setup común**: Evitar duplicación de código
   ```java
   @BeforeEach
   void setUp() {
       affiliate = new Affiliate(...);
   }
   ```

---

## 📈 Cobertura de Código

### Objetivo de Cobertura
- **Líneas**: > 80%
- **Ramas**: > 75%
- **Métodos**: > 85%

### Generar Reporte de Cobertura
```bash
mvn clean test jacoco:report
```

### Ver Reporte
```bash
open target/site/jacoco/index.html
```

---

## 🔍 Debugging de Pruebas

### Ejecutar con salida detallada
```bash
mvn test -X
```

### Ejecutar una prueba específica con debug
```bash
mvn -Dmaven.surefire.debug test -Dtest=RegisterAffiliateUseCaseImplTest#testRegisterAffiliateSuccess
```

### Agregar logs en pruebas
```java
@Test
void testExample() {
    System.out.println("Iniciando prueba...");
    // ... código de prueba
    System.out.println("Prueba completada");
}
```

---

## 📝 Próximas Mejoras

- [ ] Agregar pruebas de integración con Testcontainers
- [ ] Pruebas de carga con JMH
- [ ] Pruebas de seguridad con Spring Security Test
- [ ] Pruebas E2E con Selenium/Playwright
- [ ] Aumentar cobertura a 90%

---

## 🔗 Referencias

- [JUnit 5 Documentation](https://junit.org/junit5/docs/current/user-guide/)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- [Spring Boot Testing](https://spring.io/guides/gs/testing-web/)
- [Testcontainers](https://www.testcontainers.org/)
