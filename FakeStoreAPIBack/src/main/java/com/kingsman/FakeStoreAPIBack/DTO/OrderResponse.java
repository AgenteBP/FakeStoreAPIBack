package com.kingsman.FakeStoreAPIBack.DTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.kingsman.FakeStoreAPIBack.Enum.OrderStatus;

// customerName y customerEmail sirven a la vista del ADMIN para saber de quién es cada compra
public record OrderResponse(Long idOrder, Long idUser, String customerName, String customerEmail,
        Long idShippingAddress, LocalDateTime orderDate, OrderStatus status, BigDecimal total,
        List<OrderItemResponse> items) {
}
