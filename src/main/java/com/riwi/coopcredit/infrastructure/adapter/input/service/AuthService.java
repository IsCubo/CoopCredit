package com.riwi.coopcredit.infrastructure.adapter.input.service;


import com.riwi.coopcredit.infrastructure.adapter.input.auth.dto.AuthResponse;
import com.riwi.coopcredit.infrastructure.adapter.input.auth.dto.LoginRequest;
import com.riwi.coopcredit.infrastructure.adapter.input.auth.dto.RegisterRequest;
import com.riwi.coopcredit.infrastructure.adapter.output.persistence.entity.UserEntity;
import com.riwi.coopcredit.infrastructure.adapter.output.persistence.repository.UserRepository;
import com.riwi.coopcredit.infrastructure.adapter.input.auth.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        UserEntity user = userRepository.findByUsername(request.getUsername()).orElseThrow();
        String token = jwtService.generateToken(user);
        return AuthResponse.builder().token(token).build();
    }

    public AuthResponse register(RegisterRequest request) {
        UserEntity user = UserEntity.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .isEnabled(true)
                .createdAt(LocalDateTime.now())
                .build();

        userRepository.save(user);

        String token = jwtService.generateToken(user);

        return AuthResponse.builder()
                .token(token)
                .build();
    }
}
