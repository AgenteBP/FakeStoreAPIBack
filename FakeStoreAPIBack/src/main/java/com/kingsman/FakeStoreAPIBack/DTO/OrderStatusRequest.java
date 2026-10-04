package com.kingsman.FakeStoreAPIBack.DTO;

import com.kingsman.FakeStoreAPIBack.Enum.OrderStatus;

public record OrderStatusRequest(OrderStatus status) {
}
