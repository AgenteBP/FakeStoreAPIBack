package com.kingsman.FakeStoreAPIBack.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kingsman.FakeStoreAPIBack.DTO.PaymentRequest;
import com.kingsman.FakeStoreAPIBack.DTO.PaymentResponse;
import com.kingsman.FakeStoreAPIBack.services.PaymentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/orders/{orderId}/payments")
@CrossOrigin(origins = { "http://localhost:5173", "http://localhost:8080" })
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping
    public ResponseEntity<List<PaymentResponse>> getByOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(paymentService.getByOrder(orderId));
    }

    @PostMapping
    public ResponseEntity<PaymentResponse> create(@PathVariable Long orderId, @RequestBody PaymentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentService.create(orderId, request));
    }
}
