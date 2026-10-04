package com.kingsman.FakeStoreAPIBack.DTO;

import java.time.LocalDate;

import com.kingsman.FakeStoreAPIBack.Enum.Role;
import com.kingsman.FakeStoreAPIBack.models.User;

// No incluye la contraseña: nunca debe salir de la API
public record UserResponse(Long idUser, String userName, String email, Integer phone, LocalDate registrationDate,
        Role role, boolean active) {

    public static UserResponse fromEntity(User user) {
        LocalDate registrationDate = user.getRegistrationDate() == null ? null : user.getRegistrationDate().toLocalDate();
        return new UserResponse(user.getIdUser(), user.getUserName(), user.getEmail(), user.getPhone(),
                registrationDate, user.getRole(), user.isEnabled());
    }
}
