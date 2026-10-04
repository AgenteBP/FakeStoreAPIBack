package com.kingsman.FakeStoreAPIBack.DTO;

// Cantidad de unidades que llegaron y se suman a lo disponible
public record RestockRequest(Integer quantity) {
}
