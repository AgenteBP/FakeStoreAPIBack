package com.kingsman.FakeStoreAPIBack.services;

import java.sql.Date;
import java.time.LocalDate;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kingsman.FakeStoreAPIBack.DTO.AuthResponse;
import com.kingsman.FakeStoreAPIBack.DTO.LoginRequest;
import com.kingsman.FakeStoreAPIBack.DTO.RegisterRequest;
import com.kingsman.FakeStoreAPIBack.DTO.UserResponse;
import com.kingsman.FakeStoreAPIBack.Enum.Role;
import com.kingsman.FakeStoreAPIBack.Security.Jwt.JwtService;
import com.kingsman.FakeStoreAPIBack.exception.ConflictException;
import com.kingsman.FakeStoreAPIBack.models.User;
import com.kingsman.FakeStoreAPIBack.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final CurrentUserService currentUserService;

    // Verifica email y contraseña con Spring Security, que compara la contraseña contra el hash BCrypt.
    // Si no coinciden, authenticate() lanza BadCredentialsException y el GlobalExceptionHandler responde 401.
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        RequestValidator.requireText(request.email(), "email");
        RequestValidator.requireText(request.password(), "password");

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));

        User user = userRepository.findByEmail(request.email()).orElseThrow();
        return buildResponse(user);
    }

    // Registra un usuario nuevo, siempre con rol CLIENT, y lo deja logueado devolviendo su token.
    // La contraseña se guarda hasheada con BCrypt: ni siquiera desde la base se puede leer la original.
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        RequestValidator.requireText(request.userName(), "userName");
        RequestValidator.requireText(request.email(), "email");
        RequestValidator.requireText(request.password(), "password");

        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new ConflictException("Ya existe un usuario con el email " + request.email());
        }

        User user = new User();
        user.setUserName(request.userName());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setPhone(request.phone());
        user.setRegistrationDate(Date.valueOf(LocalDate.now()));
        user.setRole(Role.CLIENT);
        userRepository.save(user);

        return buildResponse(user);
    }

    // Datos del usuario dueño del token. Sirve al front para recuperar la sesión al recargar la página.
    public UserResponse me() {
        return UserResponse.fromEntity(currentUserService.get());
    }

    private AuthResponse buildResponse(User user) {
        return new AuthResponse(jwtService.getToken(user), jwtService.getExpirationMinutes(),
                UserResponse.fromEntity(user));
    }
}
