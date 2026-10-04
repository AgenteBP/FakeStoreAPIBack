package com.kingsman.FakeStoreAPIBack.config;

import java.sql.Date;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.kingsman.FakeStoreAPIBack.Enum.Role;
import com.kingsman.FakeStoreAPIBack.models.User;
import com.kingsman.FakeStoreAPIBack.repositories.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// Crea el primer usuario ADMIN al arrancar, si todavía no hay ninguno.
// Hace falta porque el registro público (POST /api/auth/register) siempre crea usuarios CLIENT.
// Email y contraseña salen de admin.email y admin.password en application.properties.
@Slf4j
@Component
@RequiredArgsConstructor
public class AdminSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.email}")
    private String adminEmail;

    @Value("${admin.password}")
    private String adminPassword;

    @Override
    public void run(String... args) {
        if (userRepository.existsByRole(Role.ADMIN)) {
            return;
        }
        if (userRepository.findByEmail(adminEmail).isPresent()) {
            log.warn("No se creó el ADMIN: ya existe un usuario con el email {}", adminEmail);
            return;
        }

        User admin = new User();
        admin.setUserName("Administrador");
        admin.setEmail(adminEmail);
        admin.setPassword(passwordEncoder.encode(adminPassword));
        admin.setRegistrationDate(Date.valueOf(LocalDate.now()));
        admin.setRole(Role.ADMIN);
        userRepository.save(admin);

        log.info("Usuario ADMIN creado: {}", adminEmail);
    }
}
