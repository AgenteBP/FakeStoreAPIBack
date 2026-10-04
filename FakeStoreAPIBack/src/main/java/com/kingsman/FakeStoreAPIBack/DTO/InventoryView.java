package com.kingsman.FakeStoreAPIBack.DTO;

// Proyección de InventoryRepository.findAllStock (stock de cada producto para la vista del ADMIN).
// Se devuelve tal cual como JSON: Spring serializa los getters.
public interface InventoryView {
    Long getIdProduct();

    String getProductName();

    String getSku();

    Boolean getActive();

    Integer getAvailableQuantity();

    Integer getReservedQuantity();
}
