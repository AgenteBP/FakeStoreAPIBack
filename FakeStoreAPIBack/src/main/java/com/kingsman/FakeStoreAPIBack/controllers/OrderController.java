package com.kingsman.FakeStoreAPIBack.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kingsman.FakeStoreAPIBack.DTO.OrderRequest;
import com.kingsman.FakeStoreAPIBack.DTO.OrderResponse;
import com.kingsman.FakeStoreAPIBack.DTO.OrderStatusRequest;
import com.kingsman.FakeStoreAPIBack.services.OrderService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = { "http://localhost:5173", "http://localhost:8080" })
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/orders")
    public ResponseEntity<List<OrderResponse>> getAll() {
        return ResponseEntity.ok(orderService.getAll());
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<OrderResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getById(id));
    }

    @GetMapping("/users/{userId}/orders")
    public ResponseEntity<List<OrderResponse>> getByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(orderService.getByUser(userId));
    }

    @PostMapping("/orders")
    public ResponseEntity<OrderResponse> create(@RequestBody OrderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.create(request));
    }

    // Pago simulado: el dueño de la compra la pasa de PENDING a PAID
    @PostMapping("/orders/{id}/pay")
    public ResponseEntity<OrderResponse> pay(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.pay(id));
    }

    // El dueño de la compra la cancela mientras está PENDING
    @PostMapping("/orders/{id}/cancel")
    public ResponseEntity<OrderResponse> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.cancel(id));
    }

    @PatchMapping("/orders/{id}/status")
    public ResponseEntity<OrderResponse> changeStatus(@PathVariable Long id, @RequestBody OrderStatusRequest request) {
        return ResponseEntity.ok(orderService.changeStatus(id, request));
    }
}
