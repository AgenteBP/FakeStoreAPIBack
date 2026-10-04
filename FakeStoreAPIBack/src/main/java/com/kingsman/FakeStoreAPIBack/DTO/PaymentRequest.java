package com.kingsman.FakeStoreAPIBack.DTO;

public record PaymentRequest(String type, String transactionStatus, String externalReference) {
}
