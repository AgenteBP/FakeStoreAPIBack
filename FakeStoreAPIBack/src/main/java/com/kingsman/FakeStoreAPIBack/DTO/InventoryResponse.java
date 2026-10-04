package com.kingsman.FakeStoreAPIBack.DTO;

import com.kingsman.FakeStoreAPIBack.models.Inventory;

public record InventoryResponse(Long idProduct, Integer availableQuantity, Integer reservedQuantity) {

    public static InventoryResponse fromEntity(Inventory inventory) {
        return new InventoryResponse(inventory.getProduct().getIdProduct(), inventory.getAvailableQuantity(),
                inventory.getReservedQuantity());
    }
}
