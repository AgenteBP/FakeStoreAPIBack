package com.kingsman.FakeStoreAPIBack.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kingsman.FakeStoreAPIBack.DTO.AddressRequest;
import com.kingsman.FakeStoreAPIBack.DTO.AddressResponse;
import com.kingsman.FakeStoreAPIBack.services.AddressService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = { "http://localhost:5173", "http://localhost:8080" })
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @GetMapping("/users/{userId}/addresses")
    public ResponseEntity<List<AddressResponse>> getByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(addressService.getByUser(userId));
    }

    @PostMapping("/users/{userId}/addresses")
    public ResponseEntity<AddressResponse> create(@PathVariable Long userId, @RequestBody AddressRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(addressService.create(userId, request));
    }

    @GetMapping("/addresses/{id}")
    public ResponseEntity<AddressResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(addressService.getById(id));
    }

    @PutMapping("/addresses/{id}")
    public ResponseEntity<AddressResponse> update(@PathVariable Long id, @RequestBody AddressRequest request) {
        return ResponseEntity.ok(addressService.update(id, request));
    }

    @DeleteMapping("/addresses/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        addressService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
