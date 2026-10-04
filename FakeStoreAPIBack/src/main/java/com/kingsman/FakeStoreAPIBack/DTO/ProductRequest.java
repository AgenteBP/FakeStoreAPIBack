package com.kingsman.FakeStoreAPIBack.DTO;

import java.math.BigDecimal;

// active solo se usa al actualizar (sirve para reactivar un producto); al crear siempre queda activo
public record ProductRequest(String name, String description, BigDecimal price, String sku, String imageUrl,
        Long idCategory, Boolean active) {
}
