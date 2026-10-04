package com.kingsman.FakeStoreAPIBack.exception;

// Se responde con 403: el usuario está logueado pero no puede acceder a ese dato (ej. la compra de otro usuario)
public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String message) {
        super(message);
    }
}
