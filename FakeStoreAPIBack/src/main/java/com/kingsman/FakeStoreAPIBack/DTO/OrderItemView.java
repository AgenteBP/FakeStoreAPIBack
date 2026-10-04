package com.kingsman.FakeStoreAPIBack.DTO;

import java.math.BigDecimal;

// Proyección para la consulta nativa de OrderItemRepository (ítem + nombre del producto)
public interface OrderItemView {
    Long getIdOrderItem();

    Long getIdProduct();

    String getProductName();

    Integer getQuantity();

    BigDecimal getUnitPrice();
}
