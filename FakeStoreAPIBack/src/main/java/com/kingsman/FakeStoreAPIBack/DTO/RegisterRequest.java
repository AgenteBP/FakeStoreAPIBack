package com.kingsman.FakeStoreAPIBack.DTO;

public record RegisterRequest(String userName, String email, String password, Integer phone) {
}
