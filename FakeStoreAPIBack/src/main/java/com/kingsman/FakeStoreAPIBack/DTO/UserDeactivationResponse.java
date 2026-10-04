package com.kingsman.FakeStoreAPIBack.DTO;

// Resultado de dar de baja un usuario: el usuario actualizado y cuántas compras se le cancelaron
public record UserDeactivationResponse(UserResponse user, int cancelledOrders) {
}
