package com.kingsman.FakeStoreAPIBack.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kingsman.FakeStoreAPIBack.DTO.InventoryRequest;
import com.kingsman.FakeStoreAPIBack.DTO.InventoryResponse;
import com.kingsman.FakeStoreAPIBack.DTO.InventoryView;
import com.kingsman.FakeStoreAPIBack.DTO.RestockRequest;
import com.kingsman.FakeStoreAPIBack.exception.BadRequestException;
import com.kingsman.FakeStoreAPIBack.exception.NotFoundException;
import com.kingsman.FakeStoreAPIBack.models.Inventory;
import com.kingsman.FakeStoreAPIBack.models.Product;
import com.kingsman.FakeStoreAPIBack.repositories.InventoryRepository;
import com.kingsman.FakeStoreAPIBack.repositories.ProductRepository;

import lombok.RequiredArgsConstructor;

// Todo lo de este service es solo para ADMIN (lo restringe SecurityConfig)
@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;

    // Stock de todos los productos, de menor a mayor disponible
    @Transactional(readOnly = true)
    public List<InventoryView> getAllStock() {
        return inventoryRepository.findAllStock();
    }

    @Transactional(readOnly = true)
    public InventoryResponse getByProduct(Long productId) {
        Inventory inventory = inventoryRepository.findByProduct_IdProduct(productId)
                .orElseThrow(() -> new NotFoundException("No existe inventario para el producto con id " + productId));
        return InventoryResponse.fromEntity(inventory);
    }

    // Fija la cantidad disponible de un producto (corrección de stock).
    // No toca la cantidad reservada: esa la manejan las compras.
    // Si el producto no tiene fila de inventario todavía, se crea.
    @Transactional
    public InventoryResponse updateAvailable(Long productId, InventoryRequest request) {
        RequestValidator.requireNotNull(request.availableQuantity(), "availableQuantity");
        if (request.availableQuantity() < 0) {
            throw new BadRequestException("La cantidad disponible no puede ser negativa");
        }

        Inventory inventory = findOrCreateInventory(productId);
        inventory.setAvailableQuantity(request.availableQuantity());
        return InventoryResponse.fromEntity(inventoryRepository.save(inventory));
    }

    // Reposición: suma las unidades que llegaron a lo disponible.
    // Se suma con un UPDATE en SQL (no leyendo y guardando) para no pisar una compra que
    // reserve stock del mismo producto al mismo tiempo.
    @Transactional
    public InventoryResponse restock(Long productId, RestockRequest request) {
        RequestValidator.requireNotNull(request.quantity(), "quantity");
        if (request.quantity() <= 0) {
            throw new BadRequestException("La cantidad a reponer debe ser mayor a 0");
        }

        int updatedRows = inventoryRepository.restock(productId, request.quantity());
        if (updatedRows == 0) {
            // El producto todavía no tenía fila de inventario: se crea con esas unidades
            Inventory inventory = findOrCreateInventory(productId);
            inventory.setAvailableQuantity(request.quantity());
            inventoryRepository.save(inventory);
        }
        return getByProduct(productId);
    }

    private Inventory findOrCreateInventory(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("No existe el producto con id " + productId));

        return inventoryRepository.findByProduct_IdProduct(productId).orElseGet(() -> {
            Inventory newInventory = new Inventory();
            newInventory.setProduct(product);
            newInventory.setReservedQuantity(0);
            return newInventory;
        });
    }
}
