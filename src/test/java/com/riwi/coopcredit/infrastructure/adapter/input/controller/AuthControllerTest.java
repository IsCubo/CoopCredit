package com.riwi.coopcredit.infrastructure.adapter.input.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.riwi.coopcredit.infrastructure.adapter.input.auth.AuthenticationService;
import com.riwi.coopcredit.infrastructure.adapter.input.auth.dto.AuthResponse;
import com.riwi.coopcredit.infrastructure.adapter.input.auth.dto.LoginRequest;
import com.riwi.coopcredit.infrastructure.adapter.input.auth.dto.RegisterRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Pruebas de integración para AuthController")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthenticationService authenticationService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private AuthResponse authResponse;

    @BeforeEach
    void setUp() {
        registerRequest = RegisterRequest.builder()
                .document("1017654311")
                .username("Juan Pérez")
                .email("juan@example.com")
                .password("SecurePassword123")
                .annualIncome(new BigDecimal("3500000.00"))
                .build();

        loginRequest = LoginRequest.builder()
                .username("1017654311")
                .password("SecurePassword123")
                .build();

        authResponse = AuthResponse.builder()
                .token("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
                .build();
    }

    @Test
    @DisplayName("Debe registrar un nuevo usuario exitosamente")
    void testRegisterUserSuccess() throws Exception {
        // Arrange
        when(authenticationService.register(any(RegisterRequest.class)))
                .thenReturn(authResponse);

        // Act & Assert
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", notNullValue()))
                .andExpect(jsonPath("$.type").value("Bearer"))
                .andExpect(jsonPath("$.expiresIn").value(86400000L));
    }

    @Test
    @DisplayName("Debe retornar 400 cuando el registro tiene datos inválidos")
    void testRegisterUserWithInvalidData() throws Exception {
        // Arrange
        RegisterRequest invalidRequest = RegisterRequest.builder()
                .document("") // Documento vacío
                .username("Juan")
                .email("invalid-email")
                .password("123") // Contraseña muy corta
                .annualIncome(new BigDecimal("-1000.0")) // Salario negativo
                .build();

        // Act & Assert
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Debe iniciar sesión exitosamente")
    void testLoginUserSuccess() throws Exception {
        // Arrange
        when(authenticationService.login(any(LoginRequest.class)))
                .thenReturn(authResponse);

        // Act & Assert
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", notNullValue()))
                .andExpect(jsonPath("$.type").value("Bearer"));
    }

    @Test
    @DisplayName("Debe retornar 401 cuando las credenciales son inválidas")
    void testLoginUserWithInvalidCredentials() throws Exception {
        // Arrange
        LoginRequest invalidLogin = LoginRequest.builder()
                .username("1017654311")
                .password("WrongPassword")
                .build();

        when(authenticationService.login(any(LoginRequest.class)))
                .thenThrow(new IllegalArgumentException("Credenciales inválidas"));

        // Act & Assert
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidLogin)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Debe retornar 400 cuando el body está vacío")
    void testRegisterUserWithEmptyBody() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Debe retornar 400 cuando el Content-Type es incorrecto")
    void testRegisterUserWithWrongContentType() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.TEXT_PLAIN)
                .content("invalid data"))
                .andExpect(status().isBadRequest());
    }
}
