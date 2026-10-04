package com.kingsman.FakeStoreAPIBack.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kingsman.FakeStoreAPIBack.DTO.InventoryRequest;
import com.kingsman.FakeStoreAPIBack.DTO.InventoryResponse;
import com.kingsman.FakeStoreAPIBack.DTO.InventoryView;
import com.kingsman.FakeStoreAPIBack.DTO.RestockRequest;
import com.kingsman.FakeStoreAPIBack.services.InventoryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = { "http://localhost:5173", "http://localhost:8080" })
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/inventory")
    public ResponseEntity<List<InventoryView>> getAllStock() {
        return ResponseEntity.ok(inventoryService.getAllStock());
    }

    @GetMapping("/products/{productId}/inventory")
    public ResponseEntity<InventoryResponse> getByProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(inventoryService.getByProduct(productId));
    }

    @PutMapping("/products/{productId}/inventory")
    public ResponseEntity<InventoryResponse> updateAvailable(@PathVariable Long productId,
            @RequestBody InventoryRequest request) {
        return ResponseEntity.ok(inventoryService.updateAvailable(productId, request));
    }

    @PostMapping("/products/{productId}/inventory/restock")
    public ResponseEntity<InventoryResponse> restock(@PathVariable Long productId,
            @RequestBody RestockRequest request) {
        return ResponseEntity.ok(inventoryService.restock(productId, request));
    }
}
