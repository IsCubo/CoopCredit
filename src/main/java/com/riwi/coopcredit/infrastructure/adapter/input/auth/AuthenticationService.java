package com.riwi.coopcredit.infrastructure.adapter.input.auth;

import com.riwi.coopcredit.domain.exception.DomainException;
import com.riwi.coopcredit.infrastructure.adapter.input.auth.dto.AuthResponse;
import com.riwi.coopcredit.infrastructure.adapter.input.auth.dto.LoginRequest;
import com.riwi.coopcredit.infrastructure.adapter.input.auth.dto.RegisterRequest;
import com.riwi.coopcredit.infrastructure.adapter.output.persistence.entity.AffiliateEntity;
import com.riwi.coopcredit.infrastructure.adapter.output.persistence.entity.RoleEntity;
import com.riwi.coopcredit.infrastructure.adapter.output.persistence.entity.UserEntity;
import com.riwi.coopcredit.infrastructure.adapter.output.persistence.repository.AffiliateJpaRepository;
import com.riwi.coopcredit.infrastructure.adapter.output.persistence.repository.RoleRepository;
import com.riwi.coopcredit.infrastructure.adapter.output.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final AffiliateJpaRepository affiliateRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // 1. Validación de unicidad de email/documento (CRÍTICO)
        if (userRepository.findByUsername(request.getEmail()).isPresent() || affiliateRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new DomainException("El email ya está registrado.");
        }
        if (affiliateRepository.findByDocument(request.getDocument()).isPresent()) {
            throw new DomainException("El documento ya está registrado.");
        }

        // 2. Obtener el Rol de Afiliado por defecto
        RoleEntity affiliateRole = roleRepository.findByName("ROLE_AFILIADO")
                .orElseThrow(() -> new DomainException("Rol ROLE_AFILIADO no encontrado."));

        // 3. Crear el UserEntity (seguridad)
        UserEntity user = UserEntity.builder()
                .username(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(Set.of(affiliateRole))
                .isEnabled(true)
                .createdAt(LocalDateTime.now())
                .build();
        userRepository.save(user);

        // 4. Crear el AffiliateEntity (perfil) y enlazar al UserEntity
        // Dividir el nombre en firstName y lastName
        String[] nameParts = request.getUsername().trim().split("\\s+", 2);
        String firstName = nameParts[0];
        String lastName = nameParts.length > 1 ? nameParts[1] : "";
        
        AffiliateEntity affiliate = AffiliateEntity.builder()
                .document(request.getDocument())
                .firstName(firstName)
                .lastName(lastName)
                .email(request.getEmail())
                .annualIncome(request.getAnnualIncome())
                .registrationDate(LocalDate.now())
                .user(user) // Enlace 1-1
                .build();
        affiliateRepository.save(affiliate);

        // 5. Generar Token
        String jwtToken = jwtService.generateToken(user);
        return new AuthResponse(jwtToken);
    }

    public AuthResponse login(LoginRequest request) {
        // 1. Autenticar usando AuthenticationManager (lanza excepción si credenciales son incorrectas)
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        // 2. Si la autenticación es exitosa, buscar UserDetails y generar token
        UserDetails user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new DomainException("Error interno de seguridad: UserDetails no encontrado."));

        String jwtToken = jwtService.generateToken(user);
        return new AuthResponse(jwtToken);
    }
}