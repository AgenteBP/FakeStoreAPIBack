package com.kingsman.FakeStoreAPIBack.services;

import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.kingsman.FakeStoreAPIBack.Enum.Role;
import com.kingsman.FakeStoreAPIBack.exception.ForbiddenException;
import com.kingsman.FakeStoreAPIBack.models.User;

@Service
public class CurrentUserService {

    // Devuelve el usuario logueado. Lo deja el JwtAuthenticationFilter como "principal" del SecurityContext
    // cuando el request trae un token válido; si no hay, se responde 401.
    public User get() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof User user)) {
            throw new InsufficientAuthenticationException("Tenés que iniciar sesión");
        }
        return user;
    }

    // Un CLIENT solo puede acceder a sus propios datos; un ADMIN puede acceder a los de cualquiera.
    // ownerId es el id del usuario dueño del dato (la compra, la dirección, etc.).
    public void checkOwnerOrAdmin(Long ownerId) {
        User current = get();
        boolean isAdmin = current.getRole() == Role.ADMIN;
        if (!isAdmin && !current.getIdUser().equals(ownerId)) {
            throw new ForbiddenException("No tenés permiso para acceder a datos de otro usuario");
        }
    }
}
