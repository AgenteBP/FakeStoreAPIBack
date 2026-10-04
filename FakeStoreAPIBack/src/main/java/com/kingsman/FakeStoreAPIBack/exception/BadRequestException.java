package com.kingsman.FakeStoreAPIBack.exception;

// Se responde con 400: faltan datos o vienen con un valor inválido
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
