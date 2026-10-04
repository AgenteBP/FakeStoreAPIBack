package com.kingsman.FakeStoreAPIBack.models;

import java.sql.Date;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.kingsman.FakeStoreAPIBack.Enum.Role;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

// Implementa UserDetails para que Spring Security pueda autenticarlo directamente
@Entity
@Table(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_user")
    private Long idUser;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "email", unique = true, nullable = false)
    private String email;

    // Hash BCrypt, nunca la contraseña en texto plano
    @ToString.Exclude
    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "phone")
    private Integer phone;

    @Column(name = "registration_date")
    private Date registrationDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role;

    // Baja lógica: false = dado de baja por un ADMIN (no puede iniciar sesión).
    // columnDefinition con DEFAULT TRUE para que, al crear la columna, los usuarios existentes queden activos.
    @Column(name = "active", nullable = false, columnDefinition = "BOOLEAN NOT NULL DEFAULT TRUE")
    private Boolean active = true;

    // Spring Security revisa esto en el login: un usuario dado de baja no puede entrar
    @Override
    public boolean isEnabled() {
        return !Boolean.FALSE.equals(active);
    }

    // Spring Security usa el prefijo ROLE_ para que funcione hasRole("ADMIN") en SecurityConfig.
    // Un usuario viejo sin rol se trata como CLIENT.
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Role effectiveRole = role == null ? Role.CLIENT : role;
        return List.of(new SimpleGrantedAuthority("ROLE_" + effectiveRole.name()));
    }

    // Para Spring Security el "username" es el email: es con lo que el usuario inicia sesión
    @Override
    public String getUsername() {
        return email;
    }

    // Escrito a mano: Lombok no lo genera porque ya existe getUsername() y compara los nombres
    // sin distinguir mayúsculas. Es el nombre visible del usuario, no el que usa el login.
    public String getUserName() {
        return userName;
    }
}
