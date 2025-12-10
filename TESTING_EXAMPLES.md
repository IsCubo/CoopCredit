# 📚 Ejemplos de Pruebas - CoopCredit

## Ejemplos Prácticos de Pruebas Unitarias

---

## 1. Pruebas con Mockito

### Ejemplo: Mock de Repositorio

```java
@ExtendWith(MockitoExtension.class)
class RegisterAffiliateUseCaseImplTest {

    @Mock
    private AffiliateRepositoryPort affiliateRepositoryPort;

    @InjectMocks
    private RegisterAffiliateUseCaseImpl registerAffiliateUseCase;

    @Test
    void testRegisterAffiliateSuccess() {
        // Arrange: Configurar el mock
        Affiliate affiliate = new Affiliate(
            "1017654311",
            "Juan",
            "Pérez",
            "juan@example.com",
            3500000.0
        );

        Affiliate savedAffiliate = new Affiliate(
            1L,
            "1017654311",
            "Juan",
            "Pérez",
            "juan@example.com",
            3500000.0,
            LocalDate.now(),
            null
        );

        // Configurar comportamiento del mock
        when(affiliateRepositoryPort.findByDocument("1017654311"))
            .thenReturn(Optional.empty());
        when(affiliateRepositoryPort.save(any(Affiliate.class)))
            .thenReturn(savedAffiliate);

        // Act: Ejecutar el caso de uso
        Affiliate result = registerAffiliateUseCase.register(affiliate);

        // Assert: Verificar resultados
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("1017654311", result.getDocument());

        // Verify: Verificar que se llamó al mock
        verify(affiliateRepositoryPort, times(1)).findByDocument("1017654311");
        verify(affiliateRepositoryPort, times(1)).save(affiliate);
    }
}
```

---

## 2. Pruebas Parametrizadas

### Ejemplo: Validar múltiples valores

```java
@ParameterizedTest
@ValueSource(doubles = {0.01, 1.0, 1000.0, 3500000.0, 999999999.0})
@DisplayName("Debe crear Affiliate con salarios válidos (positivos)")
void testCreateAffiliateWithValidSalaries(double salary) {
    // Act
    Affiliate affiliate = new Affiliate(
        "1017654311",
        "Juan",
        "Pérez",
        "juan@example.com",
        salary
    );

    // Assert
    assertNotNull(affiliate);
    assertEquals(salary, affiliate.getAnnualIncome());
}
```

### Ejemplo: Validar múltiples combinaciones

```java
@ParameterizedTest
@CsvSource({
    "1017654311, Juan, Pérez, 3500000.0",
    "1017654312, María, García, 2800000.0",
    "1017654313, Carlos, Rodríguez, 4200000.0"
})
@DisplayName("Debe crear múltiples Affiliates válidos")
void testCreateMultipleAffiliates(String doc, String first, String last, double salary) {
    // Act
    Affiliate affiliate = new Affiliate(doc, first, last, "email@example.com", salary);

    // Assert
    assertEquals(doc, affiliate.getDocument());
    assertEquals(first, affiliate.getFirstName());
    assertEquals(last, affiliate.getLastName());
    assertEquals(salary, affiliate.getAnnualIncome());
}
```

---

## 3. Pruebas de Excepciones

### Ejemplo: Validar excepciones

```java
@Test
@DisplayName("Debe lanzar excepción cuando el documento ya existe")
void testRegisterAffiliateWithDuplicateDocument() {
    // Arrange
    Affiliate affiliate = new Affiliate(
        "1017654311",
        "Juan",
        "Pérez",
        "juan@example.com",
        3500000.0
    );

    Affiliate existingAffiliate = new Affiliate(
        1L,
        "1017654311",
        "Existing",
        "User",
        "existing@example.com",
        2500000.0,
        LocalDate.now(),
        null
    );

    when(affiliateRepositoryPort.findByDocument("1017654311"))
        .thenReturn(Optional.of(existingAffiliate));

    // Act & Assert
    DomainException exception = assertThrows(DomainException.class, () -> {
        registerAffiliateUseCase.register(affiliate);
    });

    // Verificar el mensaje de error
    assertTrue(exception.getMessage().contains("ya se encuentra registrado"));

    // Verificar que no se llamó al save
    verify(affiliateRepositoryPort, never()).save(any());
}
```

---

## 4. Pruebas de Integración con MockMvc

### Ejemplo: Probar endpoint REST

```java
@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("Debe registrar un nuevo usuario exitosamente")
    void testRegisterUserSuccess() throws Exception {
        // Arrange
        RegisterRequest registerRequest = RegisterRequest.builder()
            .document("1017654311")
            .username("Juan Pérez")
            .email("juan@example.com")
            .password("SecurePassword123")
            .annualIncome(new BigDecimal("3500000.00"))
            .build();

        AuthResponse authResponse = AuthResponse.builder()
            .token("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
            .build();

        when(authenticationService.register(any(RegisterRequest.class)))
            .thenReturn(authResponse);

        // Act & Assert
        mockMvc.perform(post("/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(registerRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token", notNullValue()));
    }
}
```

---

## 5. Pruebas con Verificación de Comportamiento

### Ejemplo: Verificar llamadas a métodos

```java
@Test
@DisplayName("Debe guardar la solicitud dos veces")
void testCreateApplicationSavesTwice() {
    // Arrange
    Affiliate affiliate = new Affiliate(
        1L,
        "1017654311",
        "Juan",
        "Pérez",
        "juan@example.com",
        3500000.0,
        LocalDate.now().minusMonths(7),
        null
    );

    CreditApplication savedApp1 = new CreditApplication(
        new BigDecimal("5000000"),
        36,
        affiliate
    );
    savedApp1.setId(1L);

    CreditApplication savedApp2 = new CreditApplication(
        new BigDecimal("5000000"),
        36,
        affiliate
    );
    savedApp2.setId(1L);
    savedApp2.setStatus(ApplicationStatus.APROBADA);
    savedApp2.setRiskScore(750);
    savedApp2.setRiskLevel("BAJO RIESGO");

    when(affiliateRepositoryPort.findById(1L))
        .thenReturn(Optional.of(affiliate));
    when(applicationRepositoryPort.save(any(CreditApplication.class)))
        .thenReturn(savedApp1)
        .thenReturn(savedApp2);
    when(riskExternalPort.getRiskScore(anyString(), anyDouble(), anyInt()))
        .thenReturn(750);

    // Act
    CreditApplication result = createApplicationUseCase.create(
        1L,
        new BigDecimal("5000000"),
        36
    );

    // Assert
    assertNotNull(result);
    assertEquals(ApplicationStatus.APROBADA, result.getStatus());

    // Verify: Verificar que se llamó exactamente 2 veces
    verify(applicationRepositoryPort, times(2)).save(any(CreditApplication.class));
}
```

---

## 6. Pruebas de Casos Límite

### Ejemplo: Validar límites

```java
@Test
@DisplayName("Debe manejar score de riesgo en límite (500)")
void testCreateApplicationWithBoundaryRiskScore500() {
    // Arrange
    when(affiliateRepositoryPort.findById(1L))
        .thenReturn(Optional.of(affiliate));
    when(applicationRepositoryPort.save(any(CreditApplication.class)))
        .thenReturn(creditApplication);
    when(riskExternalPort.getRiskScore(anyString(), anyDouble(), anyInt()))
        .thenReturn(500); // Límite entre medio y alto

    // Act
    CreditApplication result = createApplicationUseCase.create(
        1L,
        new BigDecimal("5000000"),
        36
    );

    // Assert
    assertEquals(ApplicationStatus.APROBADA, result.getStatus());
    assertEquals("MEDIO RIESGO", result.getRiskLevel());
}

@Test
@DisplayName("Debe manejar score de riesgo en límite (700)")
void testCreateApplicationWithBoundaryRiskScore700() {
    // Arrange
    when(affiliateRepositoryPort.findById(1L))
        .thenReturn(Optional.of(affiliate));
    when(applicationRepositoryPort.save(any(CreditApplication.class)))
        .thenReturn(creditApplication);
    when(riskExternalPort.getRiskScore(anyString(), anyDouble(), anyInt()))
        .thenReturn(700); // Límite entre medio y bajo

    // Act
    CreditApplication result = createApplicationUseCase.create(
        1L,
        new BigDecimal("5000000"),
        36
    );

    // Assert
    assertEquals(ApplicationStatus.APROBADA, result.getStatus());
    assertEquals("BAJO RIESGO", result.getRiskLevel());
}
```

---

## 7. Pruebas de Conversión de Tipos

### Ejemplo: Validar conversión BigDecimal a Double

```java
@Test
@DisplayName("Debe convertir BigDecimal a Double correctamente")
void testCreateApplicationConvertsBigDecimalToDouble() {
    // Arrange
    BigDecimal amount = new BigDecimal("7500000.50");
    
    when(affiliateRepositoryPort.findById(1L))
        .thenReturn(Optional.of(affiliate));
    when(applicationRepositoryPort.save(any(CreditApplication.class)))
        .thenReturn(creditApplication);
    when(riskExternalPort.getRiskScore(anyString(), anyDouble(), anyInt()))
        .thenReturn(700);

    // Act
    createApplicationUseCase.create(1L, amount, 48);

    // Assert & Verify
    verify(riskExternalPort, times(1)).getRiskScore(
        "1017654311",
        7500000.50,  // Verificar que se convirtió correctamente
        48
    );
}
```

---

## 8. Pruebas de Métodos Booleanos

### Ejemplo: Validar métodos que retornan boolean

```java
@Test
@DisplayName("Debe verificar que una solicitud está en estado PENDIENTE")
void testIsPendingTrue() {
    // Arrange
    CreditApplication application = new CreditApplication(
        new BigDecimal("5000000"),
        36,
        affiliate
    );

    // Assert
    assertTrue(application.isPending());
}

@Test
@DisplayName("Debe verificar que una solicitud NO está en estado PENDIENTE")
void testIsPendingFalse() {
    // Arrange
    CreditApplication application = new CreditApplication(
        new BigDecimal("5000000"),
        36,
        affiliate
    );
    application.setStatus(ApplicationStatus.APROBADA);

    // Assert
    assertFalse(application.isPending());
}
```

---

## 9. Pruebas de Getters y Setters

### Ejemplo: Validar acceso a propiedades

```java
@Test
@DisplayName("Debe obtener y establecer el ID correctamente")
void testSetAndGetId() {
    // Arrange
    CreditApplication application = new CreditApplication();

    // Act
    application.setId(1L);

    // Assert
    assertEquals(1L, application.getId());
}

@Test
@DisplayName("Debe obtener y establecer el monto solicitado correctamente")
void testSetAndGetRequestedAmount() {
    // Arrange
    CreditApplication application = new CreditApplication();
    BigDecimal newAmount = new BigDecimal("7500000");

    // Act
    application.setRequestedAmount(newAmount);

    // Assert
    assertEquals(newAmount, application.getRequestedAmount());
}
```

---

## 10. Pruebas de Múltiples Cambios de Estado

### Ejemplo: Validar transiciones de estado

```java
@Test
@DisplayName("Debe manejar múltiples cambios de estado")
void testMultipleStatusChanges() {
    // Arrange
    CreditApplication application = new CreditApplication(
        new BigDecimal("5000000"),
        36,
        affiliate
    );
    assertEquals(ApplicationStatus.PENDIENTE, application.getStatus());

    // Act & Assert
    application.setStatus(ApplicationStatus.APROBADA);
    assertEquals(ApplicationStatus.APROBADA, application.getStatus());

    application.setStatus(ApplicationStatus.RECHAZADA);
    assertEquals(ApplicationStatus.RECHAZADA, application.getStatus());

    application.setStatus(ApplicationStatus.APROBADA);
    assertEquals(ApplicationStatus.APROBADA, application.getStatus());
}
```

---

## 🎯 Checklist para Escribir Buenas Pruebas

- [ ] El nombre del test describe claramente qué se prueba
- [ ] Sigue el patrón Arrange-Act-Assert
- [ ] Prueba un solo comportamiento
- [ ] No tiene dependencias de otras pruebas
- [ ] Es rápida de ejecutar
- [ ] Usa mocks apropiadamente
- [ ] Verifica tanto el resultado como el comportamiento
- [ ] Incluye casos límite y excepciones
- [ ] Tiene una asercción principal clara
- [ ] Usa @DisplayName para descripción legible

---

## 📊 Comandos Útiles

```bash
# Ejecutar todas las pruebas
mvn test

# Ejecutar una clase específica
mvn test -Dtest=RegisterAffiliateUseCaseImplTest

# Ejecutar un método específico
mvn test -Dtest=RegisterAffiliateUseCaseImplTest#testRegisterAffiliateSuccess

# Ejecutar con salida detallada
mvn test -X

# Generar reporte de cobertura
mvn clean test jacoco:report

# Ejecutar pruebas en paralelo
mvn test -DparallelTestCount=4

# Ejecutar solo pruebas unitarias
mvn test -Dgroups=unit

# Ejecutar solo pruebas de integración
mvn test -Dgroups=integration
```
