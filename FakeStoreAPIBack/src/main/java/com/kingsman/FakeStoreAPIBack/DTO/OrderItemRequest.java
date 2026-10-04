package com.kingsman.FakeStoreAPIBack.DTO;

// No lleva precio: el precio lo toma el servidor del producto, para que el cliente no pueda elegirlo
public record OrderItemRequest(Long idProduct, Integer quantity) {
}
