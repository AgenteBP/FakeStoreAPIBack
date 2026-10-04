package com.kingsman.FakeStoreAPIBack.DTO;

import java.util.List;

// No lleva el id del usuario: la compra siempre es del usuario logueado (se toma del token)
public record OrderRequest(Long idShippingAddress, List<OrderItemRequest> items) {
}
