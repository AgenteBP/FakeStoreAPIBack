package com.kingsman.FakeStoreAPIBack.exception;

// Se responde con 409: la solicitud es válida pero choca con el estado actual de los datos
public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}
