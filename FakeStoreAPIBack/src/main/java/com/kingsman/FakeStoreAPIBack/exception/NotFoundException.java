package com.kingsman.FakeStoreAPIBack.exception;

// Se responde con 404
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
