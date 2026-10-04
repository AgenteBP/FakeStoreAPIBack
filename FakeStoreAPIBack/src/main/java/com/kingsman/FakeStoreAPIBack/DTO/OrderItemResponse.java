package com.kingsman.FakeStoreAPIBack.DTO;

import java.math.BigDecimal;

public record OrderItemResponse(Long idOrderItem, Long idProduct, String productName, Integer quantity,
        BigDecimal unitPrice, BigDecimal subtotal) {

    public static OrderItemResponse fromView(OrderItemView view) {
        BigDecimal subtotal = view.getUnitPrice().multiply(BigDecimal.valueOf(view.getQuantity()));
        return new OrderItemResponse(view.getIdOrderItem(), view.getIdProduct(), view.getProductName(),
                view.getQuantity(), view.getUnitPrice(), subtotal);
    }
}
