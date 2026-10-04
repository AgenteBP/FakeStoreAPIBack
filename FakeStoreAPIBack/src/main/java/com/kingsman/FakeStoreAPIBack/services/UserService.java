package com.kingsman.FakeStoreAPIBack.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kingsman.FakeStoreAPIBack.DTO.UserDeactivationResponse;
import com.kingsman.FakeStoreAPIBack.DTO.UserResponse;
import com.kingsman.FakeStoreAPIBack.DTO.UserUpdateRequest;
import com.kingsman.FakeStoreAPIBack.exception.ConflictException;
import com.kingsman.FakeStoreAPIBack.exception.NotFoundException;
import com.kingsman.FakeStoreAPIBack.models.User;
import com.kingsman.FakeStoreAPIBack.repositories.AddressRepository;
import com.kingsman.FakeStoreAPIBack.repositories.OrderRepository;
import com.kingsman.FakeStoreAPIBack.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

// El alta de usuarios está en AuthService.register (POST /api/auth/register)
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final OrderRepository orderRepository;
    private final CurrentUserService currentUserService;
    private final OrderService orderService;

    // Solo ADMIN (lo restringe SecurityConfig)
    @Transactional(readOnly = true)
    public List<UserResponse> getAll() {
        return userRepository.findAll().stream().map(UserResponse::fromEntity).toList();
    }

    // Un CLIENT solo puede ver su propio perfil
    @Transactional(readOnly = true)
    public UserResponse getById(Long id) {
        currentUserService.checkOwnerOrAdmin(id);
        return UserResponse.fromEntity(findUser(id));
    }

    // Actualiza nombre, email y teléfono. La contraseña y el rol no se cambian desde acá.
    // Un CLIENT solo puede editar su propio perfil. Si el email cambia, se verifica que no lo use otro usuario.
    @Transactional
    public UserResponse update(Long id, UserUpdateRequest request) {
        currentUserService.checkOwnerOrAdmin(id);
        RequestValidator.requireText(request.email(), "email");
        User user = findUser(id);

        boolean emailChanged = !user.getEmail().equals(request.email());
        if (emailChanged && userRepository.findByEmail(request.email()).isPresent()) {
            throw new ConflictException("Ya existe un usuario con el email " + request.email());
        }

        user.setUserName(request.userName());
        user.setEmail(request.email());
        user.setPhone(request.phone());
        return UserResponse.fromEntity(user);
    }

    // Solo ADMIN (lo restringe SecurityConfig).
    // Borra un usuario solo si no tiene direcciones ni compras: las compras son historial y se conservan.
    @Transactional
    public void delete(Long id) {
        findUser(id);
        if (addressRepository.existsByUser_IdUser(id) || orderRepository.countByUser(id) > 0) {
            throw new ConflictException("El usuario tiene direcciones o compras asociadas y no se puede borrar");
        }
        userRepository.deleteById(id);
    }

    // Baja lógica (solo ADMIN, lo restringe SecurityConfig). Pasos:
    //   1. Marca al usuario como inactivo: ya no puede iniciar sesión y su token deja de funcionar.
    //   2. Cancela sus compras PENDING y PAID, que devuelven el stock (ver OrderService).
    // Todo en una transacción: si falla la cancelación de una compra, el usuario no queda dado de baja.
    // El ADMIN no puede darse de baja a sí mismo, para no quedarse sin acceso al panel.
    @Transactional
    public UserDeactivationResponse deactivate(Long id) {
        if (currentUserService.get().getIdUser().equals(id)) {
            throw new ConflictException("No podés darte de baja a vos mismo");
        }
        User user = findUser(id);
        if (!user.isEnabled()) {
            throw new ConflictException("El usuario ya está dado de baja");
        }

        user.setActive(false);
        int cancelledOrders = orderService.cancelNotShippedOrdersOfUser(id);
        return new UserDeactivationResponse(UserResponse.fromEntity(user), cancelledOrders);
    }

    // Reactiva un usuario dado de baja (solo ADMIN). Las compras canceladas en la baja no se recuperan.
    @Transactional
    public UserResponse activate(Long id) {
        User user = findUser(id);
        if (user.isEnabled()) {
            throw new ConflictException("El usuario ya está activo");
        }
        user.setActive(true);
        return UserResponse.fromEntity(user);
    }

    private User findUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("No existe el usuario con id " + id));
    }
}
