package com.kingsman.FakeStoreAPIBack.DTO;

// Respuesta de login y registro: el token para mandar en el header Authorization y los datos del usuario
public record AuthResponse(String token, long expiresInMinutes, UserResponse user) {
}
