package com.kingsman.FakeStoreAPIBack.services;

import com.kingsman.FakeStoreAPIBack.exception.BadRequestException;

// Validaciones de campos obligatorios que usan todos los services.
// Si un campo falta, se lanza BadRequestException y el cliente recibe un 400.
public final class RequestValidator {

    private RequestValidator() {
    }

    public static void requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new BadRequestException("El campo '" + fieldName + "' es obligatorio");
        }
    }

    public static void requireNotNull(Object value, String fieldName) {
        if (value == null) {
            throw new BadRequestException("El campo '" + fieldName + "' es obligatorio");
        }
    }
}
